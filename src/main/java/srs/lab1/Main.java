package srs.lab1;

import java.util.Arrays;
import java.util.Map;

import srs.lab1.pwmgr.PasswordManagerCommand;
import srs.lab1.pwmgr.commands.PasswordManagerGetCommand;
import srs.lab1.pwmgr.commands.PasswordManagerInitCommand;
import srs.lab1.pwmgr.commands.PasswordManagerPutCommand;

/**
 * Razred koji sadrži main metodu i definira sve podržane naredbe password managera.
 * 
 * @author tomislav
 *
 */
public class Main {
	
	/**
	 * Podržane naredbe password managera kao mapa (preslikavanje) iz imena 
	 * u objekt {@link PasswordManagerCommand}.
	 */
	private static final Map<String, PasswordManagerCommand> SUPPORTED_COMMANDS = Map.of(
			"init", new PasswordManagerInitCommand(),
			"put", new PasswordManagerPutCommand(),
			"get", new PasswordManagerGetCommand()
	);
	
	/**
	 * Ispisuje sve podržane naredbe.
	 */
	private static void printSupportedCommands() {
		SUPPORTED_COMMANDS.keySet().forEach(System.out::println);
	}
	
	/**
	 * Glavni program koji koristi argumente iz komandne linije {@code args} preko kojih 
	 * se zadaju naredba password managera i zatim argumenti tih naredbi.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args.length == 0) {
			System.out.println("Missing command. Supported: ");
			printSupportedCommands();
			return;
		}
		
		String command = args[0];
		PasswordManagerCommand cmd = SUPPORTED_COMMANDS.get(command);
		if (cmd == null) {
			System.out.println("Unsupported command \"" + command + "\". Supported: ");
			printSupportedCommands();
			return;
		}
		
		String[] argsNoCmdName = Arrays.copyOfRange(args, 1, args.length);
		
		String resultMsg = cmd.execute(argsNoCmdName);
		System.out.println("Command " + cmd.getCommandName() + ": " + resultMsg);
	}
	
}
