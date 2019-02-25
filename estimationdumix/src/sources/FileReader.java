package sources;

import java.io.File;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;

import nmf.NMFTransformer;
import traitementdesdonnees.Signal;
import wavefile.WavFile;

public class FileReader {
	
	private String[] fichiers;
	private String global;
	private final int nbFrame;
	private final int delta;
	
	
	public FileReader(String[] fichiers, String global, int nbFrame, int delta) {
		this.fichiers = fichiers;
		this.global = global;
		this.nbFrame = nbFrame;
		this.delta = delta;
	}
	
	
	public double[][] readFiles()
	   {
	      try
	      {
	    	 int n = fichiers.length;
	    	 
	    	  
	    	 WavFile[] wavFiles = new WavFile[n];
	    	 WavFile wavGlobal = WavFile.openWavFile(new File(global));
	    	 int longueur = (int) (wavGlobal.getNumFrames()/(nbFrame + delta));
	    	 double[][] res = new double[longueur + 10][n];
	    	 for (int i=0; i<n; i++) {
	    		 wavFiles[i] = WavFile.openWavFile(new File(fichiers[i]));
	    	 }
	    	 
	    	 int framesRead;
	    	 int globalFramesRead;
	    	 double[][] buffer = new double[n][];
	    	 double[] globalBuffer;
	    	 int k = 0;
	    	 
	    	 do
	         {
	    		 int numChannels = wavGlobal.getNumChannels();
	    		 
	    		 globalBuffer = new double[nbFrame * numChannels];
	    		 
	    		 double[] trash = new double[delta * numChannels];
	    		 
	    		 globalFramesRead = wavGlobal.readFrames(trash, delta);
	    		 globalFramesRead = wavGlobal.readFrames(globalBuffer, nbFrame);
	    		 
		         for (int i = 0 ; i<n ; i++) {
		        	 
			         numChannels = wavFiles[i].getNumChannels();
	
			         buffer[i] = new double[nbFrame * numChannels];
	
			         
			        
		         
		            // Read frames into buffer
		            wavFiles[i].readFrames(trash, delta);
			        framesRead = wavFiles[i].readFrames(buffer[i], nbFrame);
		         

		         }
		         
		         // do the NMF
		         double[] globalTF = Signal.transform(globalBuffer, true, true);
		         double[][] instruTF = new double[n][];
		         for (int i = 0 ; i < n ; i++) {
		        	 instruTF[i] = Signal.transform(buffer[i], true, true);
		         }
		         

		         NMFTransformer nmf = new NMFTransformer(2, 0.00001, n);
		         Array2DRowRealMatrix W = (Array2DRowRealMatrix) (new Array2DRowRealMatrix(instruTF)).transpose();
		         res[k] = nmf.NMF(globalTF, W);
		         k ++;
		         
		         
	         }
	         while (globalFramesRead != 0);
	         
	    	// Close the wavFile
	    	 wavGlobal.close();
	    	 for (int i = 0 ; i < n ; i++) {
	    		 wavFiles[i].close();
	    	 }
	    	 
	         return res; 
	         
	      }
	      catch (Exception e)
	      {
	         System.err.println(e);
	         return null;
	      }
	   }
	}