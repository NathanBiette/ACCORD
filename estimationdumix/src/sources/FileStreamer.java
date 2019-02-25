package sources;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class FileStreamer {
	
	
	/** nom du fichier de la piste global */
	private String global;
	
	/** Liste des noms de fichiers des pistes impliquees */
	private String[] fichiers;
	
	/** frequence d'echantillonnage des pistes*/
	private float freqSample;
	
	/** resultats */
	private int[][] results;
	
	/** taille des echantillons sur lesquels on fait la tf (en nombre de frame) */
	private int sampleSize;
	
	/** decalage en frame entre chaque prelevement */
	private int delta;
	
	/** longueur de l'audio en frames */
	private long size;
	
	
	public FileStreamer(String[] fichiers, String global, int sampleSize, int delta) {
		this.fichiers = fichiers;
		this.global = global;
		this.sampleSize = sampleSize;
		this.delta = delta;
	}
	
	
	public int[][] getResults() {
		return results;
	}
	
	
	private AudioInputStream[] initialisation() throws UnsupportedAudioFileException, IOException {
		AudioInputStream[] streams = new AudioInputStream[fichiers.length];
		for (int i=0; i<fichiers.length; i++) {
			try {
				File file = new File(fichiers[i]);
				streams[i] = AudioSystem.getAudioInputStream(file);
			}
			catch (IOException e) {
				throw new IOException();
				//TODO deal with it
			}
			catch (UnsupportedAudioFileException e) {
				throw new UnsupportedAudioFileException();
				//TODO deal with it
			}
			
		}
		freqSample = streams[0].getFormat().getFrameRate();
		size = streams[0].getFrameLength();
		results = new int[streams.length][(int) (size /(delta - sampleSize))];
		return streams;
	}
 	
	
	public void upload() throws UnsupportedAudioFileException, IOException {
		try {
			AudioInputStream[] streams = initialisation();
			/* Pour l'instant on part du principe que toutes les pistes ont le même format 
			 * on pourra ajouter un test qui le vérifie ou perfectionner ça plus tard
			 */
			boolean test = true;
			int n = streams.length;
			int nbOctets = streams[0].getFormat().getSampleSizeInBits() / 8;
			int OctetsParFrame = streams[0].getFormat().getFrameSize();
			int OctetsLus = 0;
			byte[][] buffer = new byte[n][sampleSize * nbOctets];
			byte[] tmp = new byte[nbOctets];
			for (int k = 0; k<(int) (size/(delta - sampleSize)); k++) {
				for (int i = 0; i<n ; i++) {
					OctetsLus = streams[i].read(buffer[i], delta, nbOctets);
					if (OctetsLus != nbOctets) break;
					for (int j = 1; j < sampleSize; j++) {
						OctetsLus = streams[i].read(tmp, OctetsParFrame - nbOctets, nbOctets);
						if (OctetsLus != nbOctets) break;
						for (int s=0; s<nbOctets ; s++) {
							buffer[i][j * nbOctets + s] = tmp[s];
						}
					}
					if (OctetsLus != nbOctets) break;
				}
				//TODO traitement des donnees 
				if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
					
				}
			}
			
		}
		catch (IOException e) {
			//TODO deal with it
		}
		catch (UnsupportedAudioFileException e) {
			//TODO deal with it
		}
		
	}
	
}
