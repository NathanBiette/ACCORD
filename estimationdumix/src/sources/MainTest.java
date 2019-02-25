package sources;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.sun.prism.Graphics;

import affichage.Courbes;

public class MainTest {
	/* pour la deuxième étape de l'estimation du mix, faire un moyenne glissante ou comparer aux maxima */
	
	private final static boolean debug = true;
	
	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, InterruptedException {
		
		
		String[] fichiers = {"Data/essai02/instrument01.wav", "Data/essai02/instrument02.wav", "Data/essai02/instrument03.wav", "Data/essai02/instrument04.wav"};
		RealTimeFileReader fileReader = new RealTimeFileReader(fichiers, "Data/essai01/global.wav", 512, 100000, 50, 10);
		long start = System.currentTimeMillis();
		double[][] res = fileReader.readFiles();
		long stop = System.currentTimeMillis();
		if (debug) System.out.println(stop-start);
		
		
	}
	
	

}
