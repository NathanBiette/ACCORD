package tools;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cette classe est utilisée pour capturer les logs des différents modules
 * @author thibaud
 *
 */
public class Log {

	public final static Logger logger = Logger.getLogger("pactLog");

	/**
	 * Fonction qui initialise le logger
	 */
	public void initLogger()
	{
		logger.setLevel(Level.ALL); //pour envoyer les messages de tous les niveaux
		//log.setUseParentHandlers(false); // pour supprimer la console par défaut
		addConsoleHandler();
		addFileHandler();
	}
	
	/**
	 * Fonction qui écrit les log sur la console
	 */
	private void addConsoleHandler()
	{
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(Level.INFO);
		logger.addHandler(consoleHandler);
	}
	
	/**
	 * Fonction qui écrit les logs dans un fichier
	 */
	private void addFileHandler()
	{
		try 
		{
			FileHandler fileHandler = new FileHandler("log/history.log", 1000, 0);
			fileHandler.setLevel(Level.ALL);
			logger.addHandler(fileHandler);
		} 
		catch (SecurityException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

	}
}
