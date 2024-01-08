package srs.lab1.pwmgr.commands;

import java.nio.file.Path;
import java.nio.file.Paths;

import srs.lab1.pwmgr.PasswordManagerCommand;

public abstract class AbstractPasswordManagerCommand implements PasswordManagerCommand {
	
	protected String cmdName;
	protected static final Path STORAGE_FILE_PATH = Paths.get("./encryptedData/data.bin");
	
	
	@Override
	public String getCommandName() {
		return cmdName;
	}
	
}
