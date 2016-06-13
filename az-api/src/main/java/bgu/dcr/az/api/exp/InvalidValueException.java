package bgu.dcr.az.api.exp;

/**
 * 
 * @author bennyl
 */
public class InvalidValueException extends RuntimeException {

    /**
     * 
     */
    public InvalidValueException() {
		super();
	}

    /**
     * 
     * @param arg0
     * @param arg1
     */
    public InvalidValueException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

    /**
     * 
     * @param arg0
     */
    public InvalidValueException(String arg0) {
		super(arg0);
	}

    /**
     * 
     * @param arg0
     */
    public InvalidValueException(Throwable arg0) {
		super(arg0);
	}

}
