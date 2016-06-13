package bgu.dcr.az.api.exp;

/**
 * 
 * @author bennyl
 */
public class InternalErrorException extends RuntimeException{

    /**
     * 
     */
    public InternalErrorException() {
		super();
	}

    /**
     * 
     * @param message
     * @param cause
     */
    public InternalErrorException(String message, Throwable cause) {
		super(message, cause);
	}

        /**
         * 
         * @param message
         */
        public InternalErrorException(String message) {
		super(message);
	}

        /**
         * 
         * @param cause
         */
        public InternalErrorException(Throwable cause) {
		super(cause);
	}
	
}
