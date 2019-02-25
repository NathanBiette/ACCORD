import java.io.BufferedReader;
import java.awt.Color;
import java.awt.Graphics;

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



public class RepeatAction2{
	 Timer t;
	 Application fenetre;
	 private boolean repetition=true;
	 private boolean debug=true;
	 private ArrayList<Integer> ArrayList;//Vecteur mouvement, initialis�e � 0 0
	 private int ix, iy;//Coin haut gauche de la zone de recherche
	 private int compteurDoc;
	 private String adresseCoordonnees;
	 private String adresseDossier;
	 private String format;
	 private static final long serialVersionUID = 1L;
	 private BufferedImage imageCamera;
	 private int cpt=0;
	 VideoCapture camera = new VideoCapture(0);
	 private Mat frame =new Mat();
	 private int init=0;
	 
	public RepeatAction2() throws IOException {
		
		//Initialisation des fichiers
		adresseCoordonnees="/home/nathan/workspace/EstimationMouvement/src/test.txt";// adresse du fichier contenant les infos sur le crop
		adresseDossier="/home/nathan/imagesOpenCV";
		format=".png";
		
		//Sert � aller chercherix et iy � partir du fichier txt
		InputStream ips=new FileInputStream(adresseCoordonnees); 
		InputStreamReader ipsr=new InputStreamReader(ips);
		BufferedReader br=new BufferedReader(ipsr);
		String chaine=br.readLine();
		String[] xx=chaine.split(" ");
		int x=Integer.parseInt(xx[0]);
		int y=Integer.parseInt(xx[1]);
		ix=x-50;//Coin haut gauche de la zone � analyser � l'arriv�e-coordonn�es selon x
		iy=y-50;////Coin haut gauche de la zone � analyser � l'arriv�e-coordonn�es selon y
		compteurDoc=0;//Actuellement fichier de nom C:\\Users\\dimitri\\Sortie\\out[compteurDoc].png. compteurDoc est incr�ment� � chaque appel � Surf
		br.close();
		
		//Autres initialisations
		t = new Timer();//Mise � 0 du timer
		ArrayList=new ArrayList<Integer>(2);//Initialisation de ArrayList
	    ArrayList.add( new Integer(0));
		ArrayList.add( new Integer(0));
		
		//Chargement de la library
		 //File lib = null;
	     //String os = System.getProperty("os.name");
	     //String bitness = System.getProperty("sun.arch.data.model");
	     //Renvoie sur des fichiers diff�rents selon l'OS, va chercher opencv
	     //if (os.toUpperCase().contains("WINDOWS")) {
	       //  if (bitness.endsWith("64")) {
	         //	lib = new File("C:\\users\\dimitri\\Downloads\\oencv-2.4.11\\opencv\\build\\java\\x64\\"+System.mapLibraryName("opencv_java2411"));
	         //} else {
	          //   lib = new File("libs//x86//" + System.mapLibraryName("opencv_java2411"));
	         //}
	     // }
	      //System.load(lib.getAbsolutePath());
	      //System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
	    
	        
	        Mat frame = new Mat();
	        camera.read(frame);
	        File depart=new File(adresseDossier+Integer.toString(0)+format);
	        Highgui.imwrite(adresseDossier+Integer.toString(0)+format, frame);
	        
	      t.schedule(new MonAction(), 0, 1*100);//R�p�tition de MonAction toutes les 2 secondes
		    }

	class MonAction extends TimerTask {
		
		
	    public void run() {
	    	
	      if (repetition) {
	        try {
				ArrayList=Surf(ArrayList);//On actualise le vecteur mouvement, le cadre, l'image � analyser
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
	
	public final ArrayList<Integer> Surf(ArrayList<Integer> vectIfNotWorking) throws IOException{ 
	  	//Initialisation 
		 long debut=System.currentTimeMillis();//Sert � mesurer le temps d'execution
	  	 ArrayList<Integer> vect=new ArrayList<Integer>();//x positif=mouvement vers la droite; y positif=mouvement vers le bas
	  	 ArrayList<Integer> vectBeforeTri=new ArrayList<Integer>();
	  	 double vx=0,vy=0,comptVNonFiltre=0,vxfinal=0,vyfinal=0,comptVFiltre=0;
	  	 String adresseImageDepart =adresseDossier+Integer.toString(compteurDoc)+format;//Adresse o� est stock�e l'image la plus r�cente (si elle existe)
	     String adresseImageArrivee =adresseDossier+Integer.toString(compteurDoc+1)+format;//Adresse o� est stock�e l'image la plus vielle
	     File fichierImageDepart=new File(adresseImageDepart);
	     if (!fichierImageDepart.isFile())
	      {	System.out.println("En attente");
	    	  return vectIfNotWorking;}//Si la nouvelle image n'a pas encore �t� extraite, le programme reste sur un statut quo 
	          File fichierImageArrivee=new File(adresseImageArrivee);
     
	     // Pour lire le fichier texte, afin de determiner x et y
	     InputStream ips=new FileInputStream(adresseCoordonnees);
	     InputStreamReader ipsr=new InputStreamReader(ips);
	     BufferedReader br=new BufferedReader(ipsr);
	     String chaine=br.readLine();
	     String[] xx=chaine.split(" ");//Cr�ation � partir de la ligne lue d'un String[]- les diff�rents �l�ments �tant s�par�s par " " dans le doc
		int x=Integer.parseInt(xx[0]);
		int y=Integer.parseInt(xx[1]);
		int w=Integer.parseInt(xx[2]);// Cette ligne sera inutile une fois que le 1080p sera bon
		int h=Integer.parseInt(xx[3]);// idem
		
		br.close(); 
		
		if (debug){
			  System.out.println(System.currentTimeMillis()-debut); //renvoie le temps �coul� depuis le d�but
			  
		  //Production des Mat pour analyse
	      System.out.println("Started....");
	      System.out.println("Loading images...");}
	      Mat imageDepartNonCroppe = Highgui.imread(adresseImageDepart, Highgui.CV_LOAD_IMAGE_COLOR);//"Chargement de la matrice depuis le fichier
	      Cadre CadreDepart=new Cadre(imageDepartNonCroppe,x,y,w,h);
	      Mat imageDepartCroppe=CadreDepart.getImageCroppe();// "Croppage" de l'image originale-L'appel � la classe cadre permet de controler les coordonn�es du cadre et donc d'�viter un out of range
	      camera.read(frame); 
	      Mat imageArriveeNonCroppe =frame;
	      Highgui.imwrite(adresseImageArrivee, frame);
	        
	      Cadre CadreArrivee=new Cadre(imageArriveeNonCroppe,ix,iy,w+100,h+100);
	      Mat imageArrivee=CadreArrivee.getImageCroppe();
	
	      //Cr�ation des descripteurs sur l'image de d�part
	      MatOfKeyPoint departKeyPoints = new MatOfKeyPoint();
	      FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SURF);
	      if (debug){
	      System.out.println("Detecting key points...");}
	      featureDetector.detect(imageDepartCroppe, departKeyPoints);//Detection des points cl�s qui sont mis dans departKeyPoints
	      MatOfKeyPoint departDescriptors = new MatOfKeyPoint();
	      DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SURF);
	      if (debug){
	    	  System.out.println("Computing descriptors...");
	      }
	      descriptorExtractor.compute(imageDepartCroppe, departKeyPoints, departDescriptors);//Associe un point cl� et un descripteur
	     
	      // Create the matrix for output image.
	      Mat outputImage = new Mat(imageDepartCroppe.rows(), imageDepartCroppe.cols(), Highgui.CV_LOAD_IMAGE_COLOR);
	      Scalar newKeypointColor = new Scalar(7, 42, 117);
	      if (debug){
	    	  System.out.println("Drawing key points on depart image...");
	      }
	      Features2d.drawKeypoints(imageDepartCroppe, departKeyPoints, outputImage, newKeypointColor, 0);
	      
	      // Match depart image with the arrivee image
	      MatOfKeyPoint arriveeKeyPoints = new MatOfKeyPoint();
	      MatOfKeyPoint arriveeDescriptors = new MatOfKeyPoint();
	      if (debug){
	    	  System.out.println("Detecting key points in background image...");
	      }
	      featureDetector.detect(imageArrivee, arriveeKeyPoints);
	      if (debug){
	    	  System.out.println("Computing descriptors in background image...");
	      }
	      descriptorExtractor.compute(imageArrivee, arriveeKeyPoints, arriveeDescriptors);
	      Mat matchoutput = new Mat(imageArrivee.rows(), imageArrivee.cols(), Highgui.CV_LOAD_IMAGE_COLOR);
	      Scalar matchestColor = new Scalar(0, 255, 0);
	      List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();
	      DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
	      
	      if (debug){
	    	  System.out.println("Matching depart and arrivee images...");
	      }
	      descriptorMatcher.knnMatch(departDescriptors, arriveeDescriptors, matches,2);
	      
	      if (debug){
	    	  System.out.println("Calculating good match list...");
	      }
	      LinkedList<DMatch> goodMatchesListBeforeTri = new LinkedList<DMatch>();
	      LinkedList<DMatch> goodMatchesList = new LinkedList<DMatch>();
	      float nndrRatio = (float) 0.7; //Param�tre changeable: plus il est �lev� plus il y a de points d'int�rets mais plus il y a de matching en th�orie moins pr�cis
	      
	      for (int i = 0; i < matches.size(); i++) {
	          MatOfDMatch matofDMatch = matches.get(i);
	          DMatch[] dmatcharray = matofDMatch.toArray();
	          DMatch m1 = dmatcharray[0];
	          DMatch m2 = dmatcharray[1];
	          if (m1.distance <= m2.distance * nndrRatio) {
	              goodMatchesListBeforeTri.addLast(m1);//Ajout de tous les bons matching
	          }
	      }
	      if (goodMatchesListBeforeTri.size() >= 1) {
	          if (debug){
	        	  System.out.println("depart Found!!!");
	          }
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
           	if (Math.abs(lx-vectBeforeTri.get(0))<50 && Math.abs(ly-vectBeforeTri.get(1))<50 ){
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
	 
	      if (init==0)
	      {
	    	  init=1;
	      }
	      return vect;
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



