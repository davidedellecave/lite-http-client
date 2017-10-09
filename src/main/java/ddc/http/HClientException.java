package ddc.http;

public class HClientException extends Exception {
	private static final long serialVersionUID = 1L;
	
    public HClientException(String message) {
        super(message);
    }
    
    public HClientException(Throwable cause) {
        super(cause);
    }
    
    public HClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
