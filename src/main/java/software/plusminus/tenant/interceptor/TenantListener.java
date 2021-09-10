package software.plusminus.tenant.interceptor;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import software.plusminus.tenant.annotation.Tenant;
import software.plusminus.tenant.exception.TenantException;
import software.plusminus.tenant.model.TenantAction;
import software.plusminus.tenant.service.TenantService;
import software.plusminus.util.FieldUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;

@SuppressFBWarnings("SE_NO_SERIALVERSIONID")
@Component
public class TenantListener implements PostLoadEventListener,
        PreInsertEventListener, PreUpdateEventListener, PreDeleteEventListener {

    private static final Object[] EMPTY_STATE = new Object[0];
    
    @Autowired
    private transient EntityManagerFactory entityManagerFactory;
    @Autowired
    private transient TenantService tenantService;

    @SuppressWarnings("PMD.CloseResource")
    @PostConstruct
    private void init() {
        SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
        registry.getEventListenerGroup(EventType.POST_LOAD).appendListener(this);
        registry.getEventListenerGroup(EventType.PRE_INSERT).appendListener(this);
        registry.getEventListenerGroup(EventType.PRE_UPDATE).appendListener(this);
        registry.getEventListenerGroup(EventType.PRE_DELETE).appendListener(this);
    }

    @Override
    public void onPostLoad(PostLoadEvent event) {
        process(TenantAction.READ, event.getEntity(), event.getPersister(), EMPTY_STATE);
    }

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        process(TenantAction.CREATE, event.getEntity(), event.getPersister(), event.getState());
        return false;
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        process(TenantAction.UPDATE, event.getEntity(), event.getPersister(), event.getState());
        return false;
    }

    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        process(TenantAction.DELETE, event.getEntity(), event.getPersister(), EMPTY_STATE);
        return false;
    }
    
    private void process(TenantAction action, Object entity, EntityPersister entityPersister, Object[] state) {
        Optional<Field> field = FieldUtils.findFirstWithAnnotation(entity.getClass(), Tenant.class);
        if (!field.isPresent()) {
            return;
        }
        String tenant = FieldUtils.read(entity, String.class, field.get());
        if (tenant == null) {
            tenant = onNullTenant(action, entity, field.get(), entityPersister, state);
        }
        checkAccess(action, tenant);
    }
    
    @Nullable
    private String onNullTenant(TenantAction action, Object entity, Field field,
                                EntityPersister entityPersister, Object[] state) {
        
        String tenant = provideTenantToPopulateInEntity(action);
        if (tenant != null) {
            FieldUtils.write(entity, tenant, field);
            String[] propertyNames = entityPersister.getEntityMetamodel().getPropertyNames();
            setValue(state, propertyNames, field.getName(), tenant);
        }
        return tenant;
    }

    private void setValue(Object[] currentState, String[] propertyNames, String propertyToSet, Object value) {
        int index = Arrays.asList(propertyNames).indexOf(propertyToSet);
        if (index >= 0) {
            currentState[index] = value;
        } else {
            throw new TenantException("Can't find '" + propertyToSet + "' property");
        }
    }

    private void checkAccess(TenantAction action, @org.springframework.lang.Nullable String tenant) {
        boolean hasAccess = ObjectUtils.nullSafeEquals(tenantService.currentTenant(), tenant);
        if (!hasAccess) {
            throw new TenantException("User has no " + action + " access for tenant " + tenant);
        }
    }

    private String provideTenantToPopulateInEntity(TenantAction action) {
        if (action == TenantAction.READ || action == TenantAction.DELETE) {
            return null;
        }
        return tenantService.currentTenant();
    }
}
