package interfaces;

public interface ServerInterface extends Runnable {

	/**
	 * Cette fonction initialise le module serveur
	 * @param port
	 * @param ihm
	 */
	void initServer(int port, IhmInterface ihm);
	
}
