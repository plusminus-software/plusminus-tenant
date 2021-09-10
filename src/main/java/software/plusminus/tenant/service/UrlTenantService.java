package software.plusminus.tenant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import software.plusminus.tenant.exception.TenantException;

import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;

@Order(2)
@Component
@ConditionalOnProperty(name = "tenant.useHostAsTenant")
public class UrlTenantService implements TenantService {

    @Autowired
    private HttpServletRequest request;
    
    @Nullable
    @Override
    public String currentTenant() {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return null;
        }
        URL url;
        try {
            url = new URL(request.getRequestURL().toString());
        } catch (MalformedURLException e) {
            throw new TenantException(e);
        }
        return url.getHost();
    }
}
