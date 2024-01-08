package srs.lab1.pwmgr.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import srs.lab1.crypto.FileCrypto;
import srs.lab1.pwmgr.PasswordStorage;
import srs.lab1.pwmgr.StringStorage;

public class PasswordManagerInitCommand extends AbstractPasswordManagerCommand {
	
	{
		cmdName = "init";
	}
	
	
	
	@Override
	public String execute(String[] args) {
		if (args.length != 1)
			return "there has to be exactly 1 argument: master password.";
		
		String masterPassword = args[0];
		Path p = STORAGE_FILE_PATH;
		if (Files.exists(p))
			return "already initialized.";
		
		Path parent = p.getParent();
		if (!Files.exists(parent)) {
			try {
				Files.createDirectories(parent);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		PasswordStorage storage = new StringStorage();
		byte[] plainBytes = storage.convertToBytes();
		FileCrypto.encryptToFile(plainBytes, masterPassword, p);
		
		return "password manager initialized.";
	}
	
	
	
}
