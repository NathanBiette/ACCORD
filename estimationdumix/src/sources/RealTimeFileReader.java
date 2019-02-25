package sources;

import java.io.File;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;

import nmf.NMFTransformer;
import traitementdesdonnees.Signal;
import wavefile.WavFile;

public class RealTimeFileReader {
	private String[] fichiers;
	private String global;
	private final int nbFrame;
	private final int delta;
	private final long supportedDelta;
	
	/** nombre de valeur sur lesquelles on effectue la moyenne */
	private final int intervalleMoyenne;
	
	/** seuil pour la cohérence de l'estimation du mix */
	private final double seuilSup = 5;
	private final double seuilInf = 0;
	
	private boolean debug = true;
	
	
	public RealTimeFileReader(String[] fichiers, String global, int nbFrame, int delta, long supportedDelta, int intervalleMoyenne) {
		this.fichiers = fichiers;
		this.global = global;
		this.nbFrame = nbFrame;
		this.delta = delta;
		this.supportedDelta = supportedDelta;
		this.intervalleMoyenne = intervalleMoyenne;
	}
	
	
	public double[][] readFiles()
	   {
	      long start = System.currentTimeMillis();
		  try
	      {
	    	 int n = fichiers.length;
	    	 
	    	  
	    	 WavFile[] wavFiles = new WavFile[n];
	    	 WavFile wavGlobal = WavFile.openWavFile(new File(global));
	    	 long sampleRate = wavGlobal.getSampleRate();
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
	    	 
	    	 long beginning = System.currentTimeMillis();
	    	 if (debug) System.out.println(beginning - start);
	    	 long currentDelta = 0;
	    	 start = 0;
	    	 do
	         {
	    		 int numChannels = wavGlobal.getNumChannels();
	    		 
	    		 globalBuffer = new double[nbFrame * numChannels];
	    		 
	    		 double[] trash = new double[delta * numChannels];
	    		 
	    		 globalFramesRead = wavGlobal.readFrames(trash, delta);
	    		 
	    		 for (int i = 0 ; i<n ; i++) {
		        	 
			         numChannels = wavFiles[i].getNumChannels();
	
			         buffer[i] = new double[nbFrame * numChannels];
	
			         
			        
		         
		            // Read frames into buffer
		            wavFiles[i].readFrames(trash, delta);
		         }
	    		 
	    		 long currentTime = System.currentTimeMillis();
	    		 currentDelta += ((long) (nbFrame + delta)*1000)/sampleRate;
	    		 
	    		 while(currentTime - beginning < currentDelta) currentTime = System.currentTimeMillis();
	    		 
	    		 globalFramesRead = wavGlobal.readFrames(globalBuffer, nbFrame);
	    		 
		         for (int i = 0 ; i<n ; i++) {
		        	 
			         numChannels = wavFiles[i].getNumChannels();
	
			         buffer[i] = new double[nbFrame * numChannels];
			         
			        framesRead = wavFiles[i].readFrames(buffer[i], nbFrame);
		         

		         }
		         
		         if (currentTime - beginning > currentDelta + supportedDelta) {
		        	 if (debug) System.out.println("donnée sautée");
		        	 continue;
		         }
		         start = System.currentTimeMillis();
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
		         
		         int idInstru = EDMMoyenneGlissante(res, k);
		         if (idInstru != -1) System.out.println(idInstru);
		         
	         }
	         while (globalFramesRead != 0);
	         
	    	// Close the wavFile
	    	 wavGlobal.close();
	    	 for (int i = 0 ; i < n ; i++) {
	    		 wavFiles[i].close();
	    	 }
	    	 
	    	 long stop = System.currentTimeMillis();
	    	 
	    	 if (debug) System.out.println(stop - start);
	    	 
	         return res; 
	         
	      }
	      catch (Exception e)
	      {
	         System.err.println(e);
	         return null;
	      }
	   }

	private int EDMMoyenneGlissante(double[][] res,int k) {
		int n = res[0].length;
		double[] estimations = new double[n];
		
		for (int j = 0 ; j < n ; j++) {
			for (int i = k-1 ; i >= 0 && i >= k - intervalleMoyenne ; i--) {
				estimations[j] += res[i][j];
			}
			estimations[j] = estimations[j]/(Math.min(k, intervalleMoyenne));
			//TODO eventuellement ponderer la difference par l'ecart-type
			estimations[j] = res[k][j] - estimations[j];
			if (estimations[j] > seuilSup) estimations[j] = 0;
		}
		
		double max = estimations[0];
		int indice = 0;
		for (int j = 0 ; j < n ; j++) {
			if (max < estimations[j]) {
				max = estimations[j];
				indice = j;
			}
		}
		
		if (max > seuilInf) return indice;
		return -1;
	}
}
