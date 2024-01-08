package srs.lab1.pwmgr;

/**
 * Sučelje koje modelira naredbu password managera.
 * 
 * @author tomislav
 *
 */
public interface PasswordManagerCommand {
	
	/**
	 * Izvršava naredbu password managera sa argumentima naredbe {@code args}.
	 * 
	 * @param args argumenti naredbe.
	 * @return povratna poruka koja označava ishod izvođenja naredbe.
	 */
	String execute(String[] args);
	
	
	/**
	 * Vraća ime naredbe.
	 * 
	 * @return ime naredbe.
	 */
	String getCommandName();
	
}
