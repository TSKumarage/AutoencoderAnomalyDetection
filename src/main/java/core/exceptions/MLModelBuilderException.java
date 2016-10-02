package core.exceptions;

/**
 * Created by wso2123 on 8/31/16.
 */
public class MLModelBuilderException extends Exception {
    private static final long serialVersionUID = -8346783977372279074L;

    public MLModelBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public MLModelBuilderException(String message) {
        super(message);
    }

    public MLModelBuilderException(Throwable cause) {
        super(cause);
    }
}