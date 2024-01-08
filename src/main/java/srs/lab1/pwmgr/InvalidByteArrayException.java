package srs.lab1.pwmgr;

/**
 * Iznimka koja označava da niz bajtova nije ispravan u širem smislu.
 * 
 * @author tomislav
 *
 */
public class InvalidByteArrayException extends RuntimeException {
	
	public InvalidByteArrayException() {}
	
	public InvalidByteArrayException(String msg) {
		super(msg);
	}

}
