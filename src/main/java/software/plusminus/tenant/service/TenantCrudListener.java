package software.plusminus.tenant.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import software.plusminus.context.Context;
import software.plusminus.crud.CrudAction;
import software.plusminus.crud.listener.CrudListener;
import software.plusminus.tenant.annotation.Tenant;
import software.plusminus.tenant.exception.TenantException;
import software.plusminus.util.FieldUtils;

import java.lang.reflect.Field;
import java.util.Optional;

@AllArgsConstructor
@Component
public class TenantCrudListener implements CrudListener<Object> {

    private Context<String> tenantContext;

    @Override
    public void onAction(Object object, CrudAction action) {
        Optional<Field> field = FieldUtils.findFirstWithAnnotation(object.getClass(), Tenant.class);
        if (!field.isPresent()) {
            return;
        }
        String objectTenant = FieldUtils.read(object, String.class, field.get());
        String contextTenant = tenantContext.get();
        if (objectTenant == null && contextTenant != null && action == CrudAction.CREATE) {
            FieldUtils.write(object, contextTenant, field.get());
            return;
        }
        checkAccess(objectTenant, contextTenant);
    }

    private void checkAccess(String objectTenant, String contextTenant) {
        if (objectTenant == null) {
            objectTenant = "";
        }
        if (contextTenant == null) {
            contextTenant = "";
        }
        if (!ObjectUtils.nullSafeEquals(objectTenant, contextTenant)) {
            throw new TenantException("Cannot peform action on object with tenant " + objectTenant
                    + " as the current tenant is " + contextTenant);
        }
    }
}
