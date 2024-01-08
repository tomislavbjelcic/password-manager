package srs.lab1.pwmgr;

import java.util.Random;

/**
 * Pomoćni razred sa korisnim metodama.
 * 
 * @author tomislav
 *
 */
public class Util {
	
	
	private Util() {}
	
	/**
	 * Konkatenira predana polja bajtova {@code arrays} i vraća takvo veliko polje pozivatelju.
	 * 
	 * @param arrays polja bajtova koja je potrebno spojiti.
	 * @return spojeno polje bajtova.
	 */
	public static byte[] concatByteArrays(byte[]... arrays) {
		int len = 0;
		for (byte[] a : arrays)
			len += a.length;
		
		byte[] result = new byte[len];
		int off = 0;
		for (byte[] a : arrays) {
			System.arraycopy(a, 0, result, off, a.length);
			off += a.length;
		}
		return result;
	}
	
	/**
	 * Metoda stvara polje veličine {@code size} i popunjava ga slučajnim bajtovima 
	 * koristeći generator slučajnih brojeva {@code rng}.
	 * 
	 * @param size
	 * @param rng
	 * @return
	 */
	public static byte[] getRandomBytes(int size, Random rng) {
		byte[] out = new byte[size];
		rng.nextBytes(out);
		return out;
	}
}
