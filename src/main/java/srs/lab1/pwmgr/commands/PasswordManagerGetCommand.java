package srs.lab1.pwmgr.commands;

import java.nio.file.Files;
import java.nio.file.Path;

import srs.lab1.crypto.FileCrypto;
import srs.lab1.pwmgr.InvalidByteArrayException;
import srs.lab1.pwmgr.PasswordStorage;
import srs.lab1.pwmgr.StringStorage;

public class PasswordManagerGetCommand extends AbstractPasswordManagerCommand {
	
	{
		cmdName = "get";
	}
	
	@Override
	public String execute(String[] args) {
		if (args.length != 2)
			return "there has to be exactly 2 arguments: master password and website.";
		
		String masterPassword = args[0];
		String site = args[1];
		
		Path p = STORAGE_FILE_PATH;
		if (!Files.exists(p))
			return "password manager is not initialized.";
		
		byte[] plain = null;
		try {
			plain = FileCrypto.decryptFromFile(masterPassword, p);
		} catch (InvalidByteArrayException ex) {
			return "unable to retrieve password for " + site + ".\n"
					+ "Either master password was wrong or encrypted files have been modified.";
		}
		
		PasswordStorage storage = StringStorage.fromBytes(plain);
		String pw = storage.get(site);
		String ret = pw==null ? "No record for " + site + "." :
			"Password for " + site + " is: " + pw;
		
		return ret;
	}
	
	
	
}
