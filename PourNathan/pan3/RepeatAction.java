import java.io.BufferedReader;
import javax.swing.JFrame;
import org.opencv.highgui.Highgui;        
import org.opencv.highgui.VideoCapture;

import tools.Frame;

//import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.opencv.core.Mat;

public class RepeatAction extends Thread{
	private final boolean debug=true;
	
	private final int sleepTime = 50; 			//temps d'attente en milliseconde
	private final int epsilon = 50;
	private final int margeRechercheCrop = 50;
	
	private boolean repetition=true;
	private ArrayList<Integer> vectorMouvement;	//Vecteur mouvement, initialis�e � 0 0
	//private int compteurDoc;
	ArrayList<Integer> vect;
	private int x, y, w, h; //position, lageur hauteur cadre de recherche initial
	
	private Mat imageArriveeNonCroppe=new Mat();
	private Mat imageDepartNonCroppe;
	
	
	private String adresseDossier;
	private String format;
	
	private JFrame jFrame;
	
	
	//private File fichierImageDepart;
	//private File fichierImageArrivee;
	
	private Surf surf;
	private VideoCapture camera;
	
	private final float nndrRatio = (float) 0.7; 		//Param�tre changeable: plus il est �lev� plus il y a de points d'int�rets mais plus il y a de matching en th�orie moins pr�cis
	 
	
	public RepeatAction(String folderPath, Frame cadreCrop){
		super("tracking");
		//Initialisation des fichiers
		adresseDossier=folderPath;
		this.format=".png";
		this.camera = new VideoCapture(1);
		
		//Autres initialisations
		vectorMouvement = new ArrayList<Integer>(2);				//Initialisation de vectorMouvement
		vectorMouvement.add(new Integer(0));
		vectorMouvement.add(new Integer(0));
	    
	    
		camera.read(imageArriveeNonCroppe);
	    
	    //File depart=new File(adresseDossier+Integer.toString(0)+format);
	    
	    jFrame=new JFrame();
	    jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(imageArriveeNonCroppe.cols(), imageArriveeNonCroppe.rows() + 30);
        jFrame.setLocation(500, 100);
	    
	    surf = new Surf(debug, margeRechercheCrop, nndrRatio, epsilon); 						//le 50 est la marge de recherche de zone de crop
	    
	   //on initialise la position du crop en rçupérant les coordonnées de la frame de départ
	    x = cadreCrop.getPointA().getX();
	    y = cadreCrop.getPointA().getY();
	    w = cadreCrop.getWidth();
	    h = cadreCrop.getHeight();
	    
		}
	
	public void run(){
		
		while (repetition){
			vectorMouvement = Surf(vectorMouvement);
			//try {
			//	this.sleep(sleepTime);
			//} catch (InterruptedException e) {
			//	// TODO Auto-generated catch block
			//	e.printStackTrace();
			//}
		}
	}
	
	public final ArrayList<Integer> Surf(ArrayList<Integer> vectIfNotWorking){ 
	  	
		//Initialization 
		
		if (debug){
			long debut=System.currentTimeMillis();					//Sert a mesurer le temps d'execution
			System.out.println(System.currentTimeMillis()-debut); 	//renvoie le temps ecoule depuis le debut
			System.out.println("Started....");
			System.out.println("Loading images...");
			}
		
		vect = new ArrayList<Integer>();							//x positif=mouvement vers la droite; y positif=mouvement vers le bas , vect contient le nouveau vecteur mouvement
		//Production des Mat pour analyse
		
		imageDepartNonCroppe = imageArriveeNonCroppe.clone();
		
		camera.read(imageArriveeNonCroppe);
		System.out.println(imageArriveeNonCroppe.cols());
		//A décommenter pour ffmpeg
		//String adresseImageArrivee =adresseDossier+Integer.toString(compteurDoc+1)+format;				//Adresse o� est stock�e l'image la plus vielle
	    //Mat imageArriveeNonCroppe = Highgui.imread(adresseImageArrivee, Highgui.CV_LOAD_IMAGE_COLOR);		//Chargement de la nouvelle image depuis le fichier contenant les images de ffmpeg
	    	    
	    vect=surf.surf(imageDepartNonCroppe, imageArriveeNonCroppe, x, y, w, h);
	    
	    surf.printImages(imageArriveeNonCroppe, w, h ,jFrame);
	    
	    x=x+vect.get(0);
	    
	    y=y+vect.get(1);
	    
	      
	      
	      
	     if (debug){System.out.println("Ended....");
	     System.out.println(vect.get(0));
	     System.out.println(vect.get(1));
	     }
	    	 //compteurDoc+=1;
	     
	    	/* Highgui.imwrite("/home/nathan/imagesOpenCV/images.jpg", retour);//Dessine l'image d'arriv�e ainsi que les 2 rectangles
        // Highgui.imwrite("C://Users//dimitri//Pictures//RectangleDepart.jpg", imageDepartNonCroppe);//Dessine l'image de d�part ainsi que les 2 rectangles
	      //fichierImageArrivee.delete();//On supprime la vielle image du dossier o� elle �tait, desormais inutile
         //fichierImageDepart.renameTo(fichierImageArrivee);//on la remplace par l'image r�cente, devenue vielle
         
	  	
		/*fichierImageDepart.delete();
		//fichierImageArrivee.renameTo(fichierImageDepart);
		fichierImageDepart = fichierImageArrivee ;
		fichierImageArrivee=new File(adresseImageArrivee);
		if (!fichierImageArrivee.isFile()){	
			System.out.println("En attente");
		    return vectIfNotWorking;
		    }											//Si la nouvelle image n'a pas encore �t� extraite, le programme reste sur un statut quo 
		File fichierImageArrivee = new File(adresseImageArrivee);*/
	      
	      return vect;

	
	
	
	
	
	//public void endRepetition(){
		//repetition=false;
		//camera.release();
	}

public final void stopTracking()
{
	repetition=false;
	camera.release();
}
 	
}
