package software.plusminus.tenant.exception;

public class TenantException extends RuntimeException {

    public TenantException(Throwable cause) {
        super(cause);
    }

    public TenantException(String message) {
        super(message);
    }
}
