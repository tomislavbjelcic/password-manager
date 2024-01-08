package srs.lab1.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import srs.lab1.pwmgr.InvalidByteArrayException;
import srs.lab1.pwmgr.Util;

/**
 * Razred koji sadrži metode za enkriptiranje niza bajtova čistih podataka 
 * u datoteke (isto niz bajtova) u formatu:<br>
 * IV + SALT + kriptat (zajedno sa oznakom)<br>
 * koristeći algoritam AES u GCM načinu rada sa 256 bitnim ključevima.<br>
 * 
 * @author tomislav
 *
 */
public class FileCrypto {
	
	/**
	 * Algoritam kriptiranja.
	 */
	public static final String ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding";
	/**
	 * Veličina inicijalizacijskog vektora (IV) u bajtovima.
	 */
	public static final int IV_BYTE_LENGTH = 16;
	/**
	 * Veličina salta u bajtovima.
	 */
	public static final int SALT_BYTE_LENGTH = 16;
	/**
	 * Veličina GCM oznake u bitovima.
	 */
	public static final int TAG_BIT_LENGTH = 128;
	
	/**
	 * Kriptografski generator slučajnih brojeva.
	 */
	private static final SecureRandom SECURE_RNG = new SecureRandom();
	
	/**
	 * Stvara i priprema {@link Cipher} objekt za enkriptiranje ili dekriptiranje 
	 * (argument {@code mode}) koristeći simetrični ključ {@code key} i inicijalizacijski 
	 * vektor {@code iv}.
	 * 
	 * @param mode argument koji označava treba li Cipher objekt enkriptirati ili dekriptirati.
	 * @param key tajni ključ
	 * @param iv inicijalizacijski vektor
	 * @return Cipher objekt
	 */
	private static Cipher getAESGCMCipherObject(int mode, SecretKey key, byte[] iv) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		AlgorithmParameterSpec gcmSpec = new GCMParameterSpec(TAG_BIT_LENGTH, iv);
		try {
			cipher.init(mode, key, gcmSpec);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return cipher;
	}
	
	/**
	 * Metoda iz lozinke {@code masterPassword} najprije stvara tajni simetrični AES256 ključ 
	 * (pri tome slučajno generira salt i inicijalizacijski vektor), zatim enkriptira bajtove 
	 * čistih podataka {@code plain} te ih zapisuje u datoteku putanje {@code file} u formatu:<br>
	 * IV + SALT + kriptat zajedno sa oznakom (jer se radi o autentificiranoj šifri).
	 * 
	 * @param plain polje bajtova čistih podataka.
	 * @param masterPassword lozinka iz koje se derivira tajni ključ.
	 * @param file putanja do odredišne datoteke gdje će se spremiti sadržaj.
	 */
	public static void encryptToFile(byte[] plain, String masterPassword, Path file) {
		
		// koristeći kriptografski generator slučajnih brojeva
		// generiraj slučajno IV i salt sa prikladnim veličinama
		byte[] iv = Util.getRandomBytes(IV_BYTE_LENGTH, SECURE_RNG);
		byte[] salt = Util.getRandomBytes(SALT_BYTE_LENGTH, SECURE_RNG);
		char[] passwordArr = masterPassword.toCharArray();
		
		// stvori AES256 tajni ključ sa generiranim saltom i master lozinkom
		SecretKey aesKey = KeyUtils.generateAESKeyFromPassword(passwordArr, salt);
		
		// stvori i podesi Cipher objekt sa stvorenim ključem i generiranim IV
		Cipher cipher = getAESGCMCipherObject(Cipher.ENCRYPT_MODE, aesKey, iv);
		
		byte[] cipherBytes = null;
		try {
			// enkripcija u jednom koraku
			cipherBytes = cipher.doFinal(plain);
		} catch (IllegalBlockSizeException | BadPaddingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		try (OutputStream os = Files.newOutputStream(file)) {
			// zapiši u binarnu datoteku u formatu
			// iv+salt+kriptat sa oznakom
			
			
			os.write(iv);
			os.write(salt);
			os.write(cipherBytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Metoda iz datoteke sa putanjom {@code file} čita sadržaj (prema dogovorenom formatu) 
	 * inicijalizacijskog vektora, salta i kriptata sa oznakom. Zatim koristeći predani 
	 * {@code masterPassword} i pročitanog salta stvara tajni AES256 ključ. Sa stvorenim ključem 
	 * i pročitanim inicijalizacijskim vektorom pokušava dekriptirati i verificirati 
	 * kriptat.<br> Ukoliko to bude uspješno, metoda vraća polje bajtova čistih podataka koji 
	 * su originalno bili enkriptirani.
	 * Ako datoteka nije u ispravnom formatu (primjerice premala je da bi se uopće mogli pročitati 
	 * IV i salt), predana lozinka {@code masterPassword} ne odgovara onoj kojom je enkriptirana ili 
	 * je datoteka u međuvremenu bila izmijenjena, oznaka se neće uspjeti verificirati i dekriptiranje 
	 * neće uspjeti. U tom slučaju metoda izaziva {@link InvalidByteArrayException}.
	 * 
	 * @param masterPassword lozinka s kojom je datoteka enkriptirana.
	 * @param file putanja datoteke sa kriptiranim sadržajem.
	 * @return polje bajtova čistih podataka.
	 * @throws InvalidByteArrayException ako dekriptiranje na bilo koji način ne uspije.
	 */
	public static byte[] decryptFromFile(String masterPassword, Path file) {
		
		byte[] iv = null;
		byte[] salt = null;
		byte[] cipherBytes = null;
		
		try (InputStream is = Files.newInputStream(file)) {
			
			// iz binarne datoteke pokušaj pročitati IV i salt
			// oni bi trebali biti u izvornom obliku
			iv = is.readNBytes(IV_BYTE_LENGTH);
			if (iv.length < IV_BYTE_LENGTH)
				throw new InvalidByteArrayException("File size too short!");
			salt = is.readNBytes(SALT_BYTE_LENGTH);
			if (salt.length < SALT_BYTE_LENGTH)
				throw new InvalidByteArrayException("File size too short!");
			
			// pročitaj ostatak, dakle kriptat sa oznakom
			cipherBytes = is.readAllBytes();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		char[] passwordArr = masterPassword.toCharArray();
		
		SecretKey aesKey = KeyUtils.generateAESKeyFromPassword(passwordArr, salt);
		
		Cipher cipher = getAESGCMCipherObject(Cipher.DECRYPT_MODE, aesKey, iv);
		
		byte[] plain = null;
		
		try {
			// pokušaj dekriptirati kriptat sa oznakom
			plain = cipher.doFinal(cipherBytes);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			if (!(e instanceof AEADBadTagException))
				e.printStackTrace();
			//AEADBadTagException označava da verifikacija oznake nije uspjela
			else
				throw new InvalidByteArrayException("AEADBadTagException.");
		}
		
		return plain;
	}
	
}
