package interfaces;

/**
 * Interface d'un instrument
 * Définit les propriétés d'un instrument à détecter
 * @author thibaud
 *
 */
public interface InstrumentInterface 
{
	/**
	 * Fonction utilisée pour assigner une voie du mixeur à un instrument
	 * @param mixerChannel
	 */
	public void setChannel(int mixerChannel);
}
