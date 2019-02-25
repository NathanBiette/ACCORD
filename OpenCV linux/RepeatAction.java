import java.io.BufferedReader;
import java.awt.Color;
import java.awt.Graphics;
import interfaces.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;        
import org.opencv.highgui.VideoCapture;        
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;



public class RepeatAction{
	private final boolean debug=true;
	
	private final int sleepTime = 50; 			//temps d'attente en milliseconde
	private final int epsilon = 50;
	private final int margeRechercheCrop = 50;
	
	private boolean repetition=true;
	
	private ArrayList<Integer> vectorMouvement;	//Vecteur mouvement, initialis�e � 0 0
	private int compteurDoc;
	ArrayList<Integer> vect;
	private int x, y;
	private int w = 720;
	private int h = 480;
	private LinkedList<DMatch> goodMatchesListBeforeTri;
	private Mat imageArriveeNonCroppe;
	private Mat imageDepartNonCroppe;
	
	
	private String adresseCoordonnees;
	private String adresseDossier;
	private String format;
	
	
	
	//private File fichierImageDepart;
	//private File fichierImageArrivee;
	
	private Surf surf;
	private VideoCapture camera = new VideoCapture(0);
	private Mat frame =new Mat();
	
	private final float nndrRatio = (float) 0.7; 		//Param�tre changeable: plus il est �lev� plus il y a de points d'int�rets mais plus il y a de matching en th�orie moins pr�cis
	 
	
	public RepeatAction() throws IOException {
		
		//Initialisation des fichiers
		adresseCoordonnees="/home/nathan/workspace/EstimationMouvement/src/crop.txt";// adresse du fichier contenant les infos sur le crop
		adresseDossier="/home/nathan/imagesOpenCV";
		format=".png";
		
		//Autres initialisations
		vectorMouvement = new ArrayList<Integer>(2);				//Initialisation de vectorMouvement
		vectorMouvement.add( new Integer(0));
		vectorMouvement.add( new Integer(0));
	    
	    camera.read(imageArriveeNonCroppe);
	    
	    File depart=new File(adresseDossier+Integer.toString(0)+format);
	    Highgui.imwrite(adresseDossier+Integer.toString(0)+format, frame);
	    
	    surf = new Surf(debug, margeRechercheCrop, nndrRatio, epsilon); 						//le 50 est la marge de recherche de zone de crop
	    
	    tools.Point crop = cropInit(adresseCoordonnees);			//on initialise la position du crop en lisant le fichier crop.txt
	    x = crop.getX();
	    y = crop.getY();
	    
		}
	
	public void run(){
		
		while (repetition){
			vectorMouvement = Surf(vectorMouvement);
			Thread.sleep(sleepTime);
		}
	}
	
	public final ArrayList<Integer> Surf(ArrayList<Integer> vectIfNotWorking) throws IOException{ 
	  	
		//Initialization 
		
		if (debug){
			long debut=System.currentTimeMillis();					//Sert a mesurer le temps d'execution
			System.out.println(System.currentTimeMillis()-debut); 	//renvoie le temps ecoule depuis le debut
			System.out.println("Started....");
			System.out.println("Loading images...");
			}
		
		vect = new ArrayList<Integer>();							//x positif=mouvement vers la droite; y positif=mouvement vers le bas , vect contient le nouveau vecteur mouvement
		//Production des Mat pour analyse
		
		imageDepartNonCroppe = imageArriveeNonCroppe;
		camera.read(imageArriveeNonCroppe);
		
		//A décommenter pour ffmpeg
		//String adresseImageArrivee =adresseDossier+Integer.toString(compteurDoc+1)+format;				//Adresse o� est stock�e l'image la plus vielle
	    //Mat imageArriveeNonCroppe = Highgui.imread(adresseImageArrivee, Highgui.CV_LOAD_IMAGE_COLOR);		//Chargement de la nouvelle image depuis le fichier contenant les images de ffmpeg
	    	    
	    goodMatchesListBeforeTri = surf.surf(imageDepartNonCroppe, imageArriveeNonCroppe, x, y, w, h);
	      
	      
	      if (goodMatchesListBeforeTri.size() >= 1) {
	          
	    	  if (debug){System.out.println("depart Found!!!");}
	          
	    	  List<KeyPoint> objKeypointlist = departKeyPoints.toList();
	          List<KeyPoint> scnKeypointlist = arriveeKeyPoints.toList();
	          
	          //Estimation du vecteur d�placement moyen
	          for (int i = 0; i < goodMatchesListBeforeTri.size(); i++) {
	              Point ptdepart=objKeypointlist.get(goodMatchesListBeforeTri.get(i).queryIdx).pt;
	              Point ptarrivee=scnKeypointlist.get(goodMatchesListBeforeTri.get(i).trainIdx).pt;
	              //if (((ptdepart.x+x-ptarrivee.x) >0.1 || (-ptdepart.x-x+ptarrivee.x) >0.1 ) && ((ptdepart.y+y-ptarrivee.y) >0.1 || (-ptdepart.y-y+ptarrivee.y) >0.1 )){  
	            	vx+=(ix+ptarrivee.x-(x+ptdepart.x));
	            	vy+=(iy+ptarrivee.y-(y+ptdepart.y));
	            	comptVNonFiltre++;
	            	//}   
	          }
	          if (comptVNonFiltre!=0)
	          {
	          vectBeforeTri.add(new Integer((int) (vx/comptVNonFiltre)));
	          vectBeforeTri.add(new Integer((int) (vy/comptVNonFiltre)));}
	           //Si un matching est d�tect� alors on rentre les nouvelles coordonn�es du mouvement rep�r�
	      else {
	    	  vect=vectIfNotWorking;
	    	  if (debug){
	    		  System.out.println("comptVNonFiltre nul");
	    	  }
	    	  return vect;//Sinon on garde le vecteur mouvement pr�c�dent
	      }
	      //Tri et estimation du vecteur mouvement en retirant les matching aberrants
	       for (int i = 0; i < goodMatchesListBeforeTri.size(); i++) {
             Point ptdepart=objKeypointlist.get(goodMatchesListBeforeTri.get(i).queryIdx).pt;
             Point ptarrivee=scnKeypointlist.get(goodMatchesListBeforeTri.get(i).trainIdx).pt;
            
           	int lx= (int)(ix+ptarrivee.x-(x+ptdepart.x));//Mediane plut�t que moyenne
           	int ly= (int) (iy+ptarrivee.y-(y+ptdepart.y));
           	if (Math.abs(lx-vectBeforeTri.get(0))<epsilon && Math.abs(ly-vectBeforeTri.get(1))<epsilon ){
           		vxfinal+=(ix+ptarrivee.x-(x+ptdepart.x));
           		vyfinal+=(iy+ptarrivee.y-(y+ptdepart.y));
           		comptVFiltre++;
           		goodMatchesList.add(goodMatchesListBeforeTri.get(i));//Ici, on ne garde que les matching renvoyant des vecteurs non aberrants (demeurant assez proche du cadre moyen -x�[xmoyen-e,xmoyen+e] et de lm�me pour y           	 
         }
	      }
	          
	      if (comptVFiltre!=0)
         {
         vect.add(new Integer((int) (vxfinal/comptVFiltre)));
         vect.add(new Integer((int) (vyfinal/comptVFiltre)));}
          //Si au moins un bon matching filtr� est d�tect� alors on rentre les nouvelles coordonn�es du mouvement rep�r�
     else {
   	  if (debug){
   		  System.out.println("comptVFiltre nul");
   	  }
   	  vect=vectIfNotWorking;
   	  return vect;//Sinon on garde le vecteur mouvement pr�c�dent
     }
          //Fin: trac� des images, actualisation  	
	      MatOfDMatch goodMatches = new MatOfDMatch();
	          goodMatches.fromList(goodMatchesList);
	          Features2d.drawMatches(imageDepartCroppe, departKeyPoints, imageArrivee, arriveeKeyPoints, goodMatches, matchoutput, matchestColor, newKeypointColor, new MatOfByte(), 2);//Dessine le matching
	          //Highgui.imwrite("C://Users//dimitri//Desktop//matchoutput.jpg", matchoutput);//Renvoie les matching en image
	          
	      
	     if (debug){
	    	 System.out.println("Ended....");
	     }
	      ix=x-50;
		  iy=y-50;
		  compteurDoc+=1;
	      if (debug){
	    	  System.out.println(System.currentTimeMillis()-debut);
	      System.out.println(vect);
	     Mat retour=Highgui.imread(adresseImageArrivee, Highgui.CV_LOAD_IMAGE_COLOR);
         Core.rectangle(retour, new Point(CadreDepart.getXCoin()+vect.get(0),CadreDepart.getYCoin()+vect.get(1)), new Point(CadreDepart.getXCoin()+w+vect.get(0),CadreDepart.getYCoin()+h+vect.get(1)), new Scalar(255,0,255),0);//Nouvelle emplacement de la zone de d�part dans l'image d'arriv�e, en violet   
         Core.rectangle(retour, new Point(CadreDepart.getXCoin(),CadreDepart.getYCoin()), new Point(CadreDepart.getXCoin()+w,CadreDepart.getYCoin()+h), new Scalar(0,255,0),0);//La position originelle de l'objet "cibl�", en vert
         Core.rectangle(imageDepartNonCroppe, new Point(CadreDepart.getXCoin()+vect.get(0),CadreDepart.getYCoin()+vect.get(1)), new Point(CadreDepart.getXCoin()+w+vect.get(0),CadreDepart.getYCoin()+h+vect.get(1)), new Scalar(255,0,255),0);//Emplacement de la zone d'ariv�e dans la zone de d�part, en violet 
         Core.rectangle(imageDepartNonCroppe, new Point(CadreDepart.getXCoin(),CadreDepart.getYCoin()), new Point(CadreDepart.getXCoin()+w,CadreDepart.getYCoin()+h), new Scalar(0,255,0),0);//La nouvelle position de l'objet "cibl�", en vert
         
         Highgui.imwrite("/home/nathan/imagesOpenCV/images.jpg", retour);//Dessine l'image d'arriv�e ainsi que les 2 rectangles
        // Highgui.imwrite("C://Users//dimitri//Pictures//RectangleDepart.jpg", imageDepartNonCroppe);//Dessine l'image de d�part ainsi que les 2 rectangles
	      //fichierImageArrivee.delete();//On supprime la vielle image du dossier o� elle �tait, desormais inutile
         //fichierImageDepart.renameTo(fichierImageArrivee);//on la remplace par l'image r�cente, devenue vielle
         BufferedImage retourBuffered=MatToBufferedImage(retour);
         window(retourBuffered, "Original Image", 0, 0);
	      }
	      
         PrintWriter pw=new PrintWriter(new FileWriter(adresseCoordonnees));
         pw.print(Integer.toString((int)(x+(vxfinal/comptVFiltre)))+" "+Integer.toString((int)(y+(vyfinal/comptVFiltre)))+" "+Integer.toString(w)+" "+Integer.toString(h));//Actualisation de la position du filtre
         pw.close();
     }
           else
           {vect=vectIfNotWorking;}
	 
	      
	  	
		fichierImageDepart.delete();
		//fichierImageArrivee.renameTo(fichierImageDepart);
		fichierImageDepart = fichierImageArrivee ;
		fichierImageArrivee=new File(adresseImageArrivee);
		if (!fichierImageArrivee.isFile()){	
			System.out.println("En attente");
		    return vectIfNotWorking;
		    }											//Si la nouvelle image n'a pas encore �t� extraite, le programme reste sur un statut quo 
		File fichierImageArrivee = new File(adresseImageArrivee);
	      
	      return vect;
}
	
	
	
	
	class MonAction extends TimerTask {
		
		
	    public void run() {
	    	
	      if (repetition) {
	        try {
	        	vectorMouvement=Surf(vectorMouvement);//On actualise le vecteur mouvement, le cadre, l'image � analyser
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	      } else {
	        System.out.println("Termin�!");
	        t.cancel();
	        System.exit(0);
	        }
	      }
	    }
	
	public void endRepetition(){
		repetition=false;
		camera.release();
	}
	
	
public tools.Point cropInit(String adresseCoordonnees){
	BufferedReader br;
	tools.Point p;
	try{
		InputStream ips=new FileInputStream(adresseCoordonnees);
		InputStreamReader ipsr=new InputStreamReader(ips);
	    br=new BufferedReader(ipsr);
	    String chaine=br.readLine();
	    String[] xx=chaine.split(" ");//Cr�ation � partir de la ligne lue d'un String[]- les diff�rents �l�ments �tant s�par�s par " " dans le doc
		int x=Integer.parseInt(xx[0]);
		int y=Integer.parseInt(xx[1]);
		p = new tools.Point(x, y);
	}catch(Exception e){
		System.out.println(e);
	}finally{
		try{
			br.close();
		}catch(Exception e){
			System.out.println(e);
		}
	}
		return p;
	}
    

	
	 
	
}
	
	      


// TODO Auto-generated method stub


	
	  public BufferedImage MatToBufferedImage(Mat frame) {
	        //Mat() to BufferedImage
	        int type = 0;
	        if (frame.channels() == 1) {
	            type = BufferedImage.TYPE_BYTE_GRAY;
	        } else if (frame.channels() == 3) {
	            type = BufferedImage.TYPE_3BYTE_BGR;
	        }
	        BufferedImage image = new BufferedImage(frame.width(), frame.height(), type);
	        WritableRaster raster = image.getRaster();
	        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
	        byte[] data = dataBuffer.getData();
	        frame.get(0, 0, data);


	        return image;
	    }
	  public void window(BufferedImage img, String text, int x, int y) {
	        JFrame frame0 = new JFrame();
	        frame0.getContentPane().add(new JPanelOpenCV(img));
	        frame0.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame0.setTitle(text);
	        frame0.setSize(img.getWidth(), img.getHeight() + 30);
	        frame0.setLocation(x, y);
	        frame0.setVisible(true);
	    }
	  }



