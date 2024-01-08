package srs.lab1.pwmgr;

/**
 * Predstavlja model spremnika parova (stranica, lozinka).
 * 
 * @author tomislav
 *
 */
public interface PasswordStorage {
	
	/**
	 * Dohvaća lozinku sa stranice {@code site} ako postoji zapis sa tom stranicom. 
	 * Ako zapis ne postoji, metoda vraća {@code null}.
	 * 
	 * @param site stranica
	 * @return lozinka sa stranice {@code site} ako postoji, inače {@code null}.
	 */
	String get(String site);
	
	/**
	 * Stvara novi zapis (stranica, lozinka) ako ne postoji, ili staru lozinku mijenja novom.
	 * 
	 * @param site stranica
	 * @param password lozinka
	 * @return prethodna lozinka, a ako nije postojala onda {@code null}.
	 */
	String put(String site, String password);
	
	
	/**
	 * Stvara i vraća binarnu reprezentaciju spremnika, svojevrsna serijalizacija.
	 * 
	 * @return binarna reprezentacija spremnika.
	 */
	byte[] convertToBytes();
	
}
