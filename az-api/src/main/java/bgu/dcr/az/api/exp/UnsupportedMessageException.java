package bgu.dcr.az.api.exp;

/**
 * 
 * @author bennyl
 */
public class UnsupportedMessageException extends RuntimeException{

    /**
     * 
     */
    public UnsupportedMessageException() {
		super();
	}

        /**
         * 
         * @param message
         * @param cause
         */
        public UnsupportedMessageException(String message, Throwable cause) {
		super(message, cause);
	}

        /**
         * 
         * @param message
         */
        public UnsupportedMessageException(String message) {
		super(message);
	}

        /**
         * 
         * @param cause
         */
        public UnsupportedMessageException(Throwable cause) {
		super(cause);
	}
	
}
