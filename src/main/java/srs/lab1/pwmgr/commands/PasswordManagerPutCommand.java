package srs.lab1.pwmgr.commands;

import java.nio.file.Files;
import java.nio.file.Path;

import srs.lab1.crypto.FileCrypto;
import srs.lab1.pwmgr.InvalidByteArrayException;
import srs.lab1.pwmgr.PasswordStorage;
import srs.lab1.pwmgr.StringStorage;

public class PasswordManagerPutCommand extends AbstractPasswordManagerCommand {
	
	{
		cmdName = "put";
	}
	
	@Override
	public String execute(String[] args) {
		if (args.length != 3)
			return "there has to be exactly 3 arguments: master password, website and password.";
		
		String masterPassword = args[0];
		String site = args[1];
		String pw = args[2];
		
		Path p = STORAGE_FILE_PATH;
		if (!Files.exists(p))
			return "password manager is not initialized.";
		
		byte[] plain = null;
		try {
			plain = FileCrypto.decryptFromFile(masterPassword, p);
		} catch (InvalidByteArrayException ex) {
			return "unable to retrieve the storage.\n"
					+ "Either master password was wrong or encrypted files have been modified.";
		}
		
		PasswordStorage storage = StringStorage.fromBytes(plain);
		storage.put(site, pw);
		FileCrypto.encryptToFile(storage.convertToBytes(), masterPassword, p);
		
		
		
		return "Stored password for " + site + ".";
	}

}
