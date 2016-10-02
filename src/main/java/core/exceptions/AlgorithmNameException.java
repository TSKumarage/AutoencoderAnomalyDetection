package core.exceptions;

/**
 * Created by wso2123 on 9/7/16.
 */
public class AlgorithmNameException extends IllegalStateException {
    private static final long serialVersionUID = 7178085223069282980L;

    public AlgorithmNameException(String message) {
        super(message);
    }
}