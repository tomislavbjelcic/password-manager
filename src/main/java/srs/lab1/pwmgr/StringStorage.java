package srs.lab1.pwmgr;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Implementacija sučelja {@link PasswordStorage} koja primarno koristi velike Stringove 
 * kako bi zapisala sve parove (stranica, lozinka).<br>
 * Zapisi su međusobno odvojeni znakom novog reda, a svaki zapis je u obliku:<br>
 * stranica,lozinka<br>
 * primjerice "www.fer.unizg.hr,mojaFerWebSifra"<br>
 * Ukoliko stranica ili lozinka imaju zareze, koristi se escape mehanizam znakom backslasha "\" 
 * (isto vrijedi i za zapisivanje samih backslasha).
 * Primjerice, stranicu "nes\to,cud,no" sa lozinkom "lo,zi\,nk\a" bismo čuvali kao<br>
 * "nes\\to\,cud\,no,lo\,zi\\\,nk\\a"
 * 
 * @author tomislav
 *
 */
public class StringStorage implements PasswordStorage {
	
	/**
	 * Separator u zapisu između stranice i lozinke.
	 */
	private static final char SEP = ',';
	/**
	 * Escape znak.
	 */
	private static final char ESCAPE = '\\';
	/**
	 * Veličina slučajnog prefiksa u bajtovima.
	 */
	private static final int PREFIX_BYTE_SIZE = 16;
	/**
	 * Generator slučajnih brojeva (ne treba biti kriptografski).
	 */
	private static final Random NORMAL_RNG = new Random();
	
	/**
	 * Spremnik u obliku velikog promjenjivog Stringa.
	 */
	private StringBuilder storage;
	/**
	 * Sekundarni spremnik u obliku mape (preslikavanja) stranica -> lozinka.<br>
	 * Koristi se tek onda kad se učita cijeli spremnik i treba se dohvatiti određeni zapis.
	 */
	private Map<String, String> pwMap = new HashMap<>();
	
	/**
	 * Stvara novi prazni spremnik.
	 */
	public StringStorage() {
		this("");
	}
	
	/**
	 * Stvara novi spremnik iz podataka {@code data}. Ispravnost formata se ne provjerava.
	 * 
	 * @param data ulazni spremnik podataka u obliku Stringa.
	 */
	public StringStorage(String data) {
		storage = new StringBuilder(data);
		data.lines().map(StringStorage::splitUserPasswordPair)
					.forEach(a -> pwMap.put(removeEscapes(a[0]), removeEscapes(a[1])));
		
	}
	
	/**
	 * Stvara novi spremnik iz binarne reprezentacije spremnika {@code bytes}.<br>
	 * Izaziva iznimku {@link InvalidByteArrayException} ako je binarna reprezentacija prekratka, 
	 * jer postoji donja granica na veličinu (da nikad ne bude prazno).
	 * 
	 * @param bytes binarna reprezentacija spremnika.
	 * @return novi spremnik čija je binarna reprezentacija {@code bytes}.
	 * @throws InvalidByteArrayException ako je binarna reprezentacija prekratka.
	 */
	public static StringStorage fromBytes(byte[] bytes) {
		if (bytes.length < PREFIX_BYTE_SIZE)
			throw new InvalidByteArrayException("Too few bytes!");
		int off = PREFIX_BYTE_SIZE;
		int len = bytes.length - PREFIX_BYTE_SIZE;
		String data = new String(bytes, off, len, StandardCharsets.UTF_8);
		return new StringStorage(data);
	}
	
	@Override
	public String get(String site) {
		return pwMap.get(site);
	}

	@Override
	public String put(String site, String password) {
		String retval = pwMap.put(site, password);
		String pairStr = insertEscapes(site) + SEP + insertEscapes(password) + '\n';
		storage.append(pairStr);
		return retval;
	}

	@Override
	public byte[] convertToBytes() {
		byte[] prefix = Util.getRandomBytes(PREFIX_BYTE_SIZE, NORMAL_RNG);
		byte[] data = storage.toString().getBytes(StandardCharsets.UTF_8);
		return Util.concatByteArrays(prefix, data);
	}
	
	/**
	 * Pomoćna metoda koja briše escape znakove iz ispravnih escape sekvenci 
	 * u ulaznom Stringu {@code str}.<br>
	 * Ako postoji neispravna escape sekvenca, metoda izaziva {@link IllegalArgumentException}.
	 * 
	 * 
	 * @param str
	 * @return String sa izbrisanim escape sekvencama.
	 * @throws IllegalArgumentException
	 */
	private static String removeEscapes(String str) {
		StringBuilder sb = new StringBuilder(str);
		for (int i=0; i<sb.length(); i++) {
			char c = sb.charAt(i);
			if (c == ESCAPE) {
				int nextIdx = i+1;
				if (nextIdx >= sb.length())
					throw new IllegalArgumentException("Invalid escape sequence: " + str);
				char nextc = sb.charAt(nextIdx);
				if (nextc != ESCAPE && nextc != SEP)
					throw new IllegalArgumentException("Invalid escape sequence: " + str);
				sb.deleteCharAt(i);
				continue;
			}
		}
		return sb.toString();
	}
	
	/**
	 * Pomoćna metoda koja umeće escape znakove ispred znakova za koje je to potrebno.
	 * 
	 * @param str
	 * @return
	 */
	private static String insertEscapes(String str) {
		StringBuilder output = new StringBuilder();
		for (int i=0, len=str.length(); i<len; i++) {
			char c = str.charAt(i);
			if (c == ESCAPE || c == SEP)
				output.append(ESCAPE);
			output.append(c);
		}
		return output.toString();
	}
	
	/**
	 * Metoda zapis razdvaja ulazni zapis {@code line} na polje od 2 elementa čiji su članovi 
	 * stranica i lozinka (sa uključenim escape sekvencama).
	 * 
	 * @param line
	 * @return
	 */
	private static String[] splitUserPasswordPair(String line) {
		boolean escapeActive = false;
		int sepIdx = -1;
		for (int i=0, len=line.length(); i<len; i++) {
			char c = line.charAt(i);
			if (c == ESCAPE) {
				escapeActive = !escapeActive;
				continue;
			}
			if (c == SEP && !escapeActive) {
				sepIdx = i;
				break;
			}
			escapeActive = false;
		}
		
		String[] splitted = new String[] {
				line.substring(0, sepIdx),
				line.substring(sepIdx+1)
		};
		return splitted;
	}
	
	@Override
	public String toString() {
		return storage.toString();
	}
	
}
