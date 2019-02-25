package sources;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Port;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class MicStreamer implements KeyListener {
	
	private static boolean stopped = false;
	
	public static void micStream() throws IOException, InterruptedException, UnsupportedAudioFileException {
		TargetDataLine line;
		if (AudioSystem.isLineSupported(Port.Info.MICROPHONE)) {
			try {
				//line = (TargetDataLine) AudioSystem.getLine(Port.Info.MICROPHONE);
				AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, false);
				//line.open(format);
				
				DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
				TargetDataLine ligne = (TargetDataLine) AudioSystem.getLine(info);
				ligne.open(format);
				
				ByteArrayOutputStream out  = new ByteArrayOutputStream();
				int numBytesRead;
				byte[] data = new byte[ligne.getBufferSize() / 5];

				// Begin audio capture.
				ligne.start();
				
				int k = 0;

				// Here, stopped is a global boolean set by another thread.
				while (!stopped) {
				   // Read the next chunk of data from the TargetDataLine.
				   numBytesRead =  ligne.read(data, 0, data.length);
				   // Save this chunk of data.
				   out.write(data, 0, numBytesRead);
				   k ++;
				   if (k >= 100) break;
				}     
				
				ligne.stop();
				
				byte[] buffer = out.toByteArray();
				
				out.close();
				
				ByteArrayInputStream in = new ByteArrayInputStream(buffer);
				
				AudioInputStream writingStream = new AudioInputStream(in, format, buffer.length/2);
				
				AudioSystem.write(writingStream, AudioFileFormat.Type.WAVE, new File("enregistrement.wav"));
				
				System.out.println("ok");
				System.out.println(out.size());
			}
			catch (LineUnavailableException e) {
				System.out.println("Le micro n'est pas disponible");
				return;
			}    
		}
		else {
			System.out.println("micro pas trouvé");
		}
		
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == 'Q') stopped = true;
		System.out.println("key typed");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	

}
