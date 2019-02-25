package teest;

import java.io.BufferedReader;
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
import java.util.Vector;
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
	 Timer t;
	 Application fenetre;
	 private boolean repetition=true;
	 private boolean debug=true;
	 private Vector<Integer> vector;//Vecteur mouvement, initialisée à 0 0
	 private int ix, iy;//Coin haut gauche de la zone de recherche
	 private int compteurDoc;
	 private String adresseCoordonnees;
	 private String adresseDossier;
	 private String format;
	 
	public RepeatAction() throws IOException {
		
		//Initialisation des fichiers
		adresseCoordonnees="C:\\Users\\dimitri\\Desktop\\Dossier PACT\\test.txt";// adresse du fichier contenant les infos sur le crop
		adresseDossier="C:\\Users\\dimitri\\ZZZZZZZ\\new";
		format=".png";
		
		//Timer et vecteur
		t = new Timer();//Mise à 0 du timer
		vector=new Vector<Integer>(2);//Initialisation de vector
	    vector.add( new Integer(0));
		vector.add( new Integer(0));
		
		//Sert à aller chercherix et iy à partir du fichier txt
		InputStream ips=new FileInputStream(adresseCoordonnees); //Flux de données issus du fichier d'adresse "adresseCoordonnees"
		InputStreamReader ipsr=new InputStreamReader(ips);//Lecture
		BufferedReader br=new BufferedReader(ipsr);//Lecture
		String chaine=br.readLine();
		String[] xx=chaine.split(" ");//Creation d'un tableau, un élément du tableau est un élément du fichier séparépar des " " 
		int x=Integer.parseInt(xx[0]);
		int y=Integer.parseInt(xx[1]);
		ix=x-50;//Coin haut gauche de la zone à analyser à l'arrivée-coordonnées selon x
		iy=y-50;////Coin haut gauche de la zone à analyser à l'arrivée-coordonnées selon y
		compteurDoc=0;//Actuellement fichier de nom C:\\Users\\dimitri\\Sortie\\out[compteurDoc].png. compteurDoc est incrémenté à chaque appel à Surf
		br.close();
		
		
		
		//Chargement de la library
		 File lib = null;
	     String os = System.getProperty("os.name");
	     String bitness = System.getProperty("sun.arch.data.model");
	     //Renvoie sur des fichiers différents selon l'OS, va chercher opencv
	     if (os.toUpperCase().contains("WINDOWS")) {
	         if (bitness.endsWith("64")) {
	         	lib = new File("C:\\users\\dimitri\\Downloads\\oencv-2.4.11\\opencv\\build\\java\\x64\\"+System.mapLibraryName("opencv_java2411"));
	         } else {
	             lib = new File("libs//x86//" + System.mapLibraryName("opencv_java2411"));
	         }
	      }
	      System.load(lib.getAbsolutePath());
	    
	      t.schedule(new MonAction(), 0, 1*2000);//Répétition de MonAction toutes les 2000 millisecondes
		    }

	class MonAction extends TimerTask {
		
		
	    public void run() {
	    	
	      if (repetition) {
	        try {
				vector=Surf(vector);//On actualise le vecteur mouvement, le cadre, l'image à analyser
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	      } else {
	        System.out.println("Terminé!");
	        t.cancel();
	        System.exit(0);
	        }
	      }
	    }
	
	public void endRepetition(){
		repetition=false;
	}
	
	public final Vector<Integer> Surf(Vector<Integer> vectIfNotWorking) throws IOException{ 
	  	
	  	//Initialisation 
		 long debut=System.currentTimeMillis();//Sert à mesurer le temps d'execution
	  	 Vector<Integer> vect=new Vector<Integer>();//x positif=mouvement vers la droite; y positif=mouvement vers le bas
	  	 Vector<Integer> vectBeforeTri=new Vector<Integer>();
	  	 double vx=0,vy=0,comptVNonFiltre=0,vxfinal=0,vyfinal=0,comptVFiltre=0;
	  	 String adresseImageDepart =adresseDossier+Integer.toString(compteurDoc)+format;//Adresse où est stockée l'image la plus récente (si elle existe)
	     String adresseImageArrivee =adresseDossier+Integer.toString(compteurDoc+1)+format;//Adresse où est stockée l'image la plus vielle
	     File fichierImageDepart=new File(adresseImageDepart);
	     if (!fichierImageDepart.isFile())
	      {	System.out.println("En attente");
	    	  return vectIfNotWorking;}//Si la nouvelle image n'a pas encore été extraite, le programme reste sur un statut quo 
	          //File fichierImageArrivee=new File(adresseImageArrivee);
     
	     // Pour lire le fichier texte, afin de determiner x et y
	     InputStream ips=new FileInputStream(adresseCoordonnees);
	     InputStreamReader ipsr=new InputStreamReader(ips);
	     BufferedReader br=new BufferedReader(ipsr);
	     String chaine=br.readLine();
	     String[] xx=chaine.split(" ");//Création à partir de la ligne lue d'un String[]- les différents éléments étant séparés par " " dans le doc
		int x=Integer.parseInt(xx[0]);
		int y=Integer.parseInt(xx[1]);
		int w=Integer.parseInt(xx[2]);// Cette ligne sera inutile une fois que le 1080p sera bon
		int h=Integer.parseInt(xx[3]);// idem
		br.close(); 
		
		if (debug){
			  System.out.println(System.currentTimeMillis()-debut); //renvoie le temps écoulé depuis le début
			  
		  //Production des Matrices pour analyse
	      System.out.println("Started....");
	      System.out.println("Loading images...");}
	      Mat imageDepartNonCroppe = Highgui.imread(adresseImageDepart, Highgui.CV_LOAD_IMAGE_COLOR);//"Chargement de la matrice depuis le fichier
	      Cadre CadreDepart=new Cadre(imageDepartNonCroppe,x,y,w,h);
	      Mat imageDepartCroppe=CadreDepart.getImageCroppe();// "Croppage" de l'image originale-L'appel à la classe cadre permet de controler les coordonnées du cadre et donc d'éviter un out of range
	      Mat imageArriveeNonCroppe = Highgui.imread(adresseImageArrivee, Highgui.CV_LOAD_IMAGE_COLOR);
	      Cadre CadreArrivee=new Cadre(imageArriveeNonCroppe,ix,iy,w+100,h+100);
	      Mat imageArrivee=CadreArrivee.getImageCroppe();
	
	      //Création des descripteurs sur l'image de départ
	      MatOfKeyPoint departKeyPoints = new MatOfKeyPoint();
	      FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SURF);
	      if (debug){
	      System.out.println("Detecting key points...");}
	      featureDetector.detect(imageDepartCroppe, departKeyPoints);//Detection des points clés qui sont mis dans departKeyPoints
	      MatOfKeyPoint departDescriptors = new MatOfKeyPoint();
	      DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SURF);
	      if (debug){
	    	  System.out.println("Computing descriptors...");
	      }
	      descriptorExtractor.compute(imageDepartCroppe, departKeyPoints, departDescriptors);//Associe un point clé et un descripteur
	     
	      // Create the matrix for output image.
	      Mat outputImage = new Mat(imageDepartCroppe.rows(), imageDepartCroppe.cols(), Highgui.CV_LOAD_IMAGE_COLOR);
	      Scalar newKeypointColor = new Scalar(7, 42, 117);
	      if (debug){
	    	  System.out.println("Drawing key points on depart image...");
	      }
	      Features2d.drawKeypoints(imageDepartCroppe, departKeyPoints, outputImage, newKeypointColor, 0);//Dessine les points clés sur "outputImage"
	      
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
	      float nndrRatio = (float) 0.7; //Paramètre changeable: plus il est élevé plus il y a de points d'intêrets mais plus il y a de matching en théorie moins précis
	      
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
	          
	          //Estimation du vecteur déplacement moyen
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
	           //Si un matching est détecté alors on rentre les nouvelles coordonnées du mouvement repéré
	      else {
	    	  vect=vectIfNotWorking;
	    	  if (debug){
	    		  System.out.println("comptVNonFiltre nul");
	    	  }
	    	  return vect;//Sinon on garde le vecteur mouvement précédent
	      }
	      //Tri et estimation du vecteur mouvement en retirant les matching aberrants
	       for (int i = 0; i < goodMatchesListBeforeTri.size(); i++) {
             Point ptdepart=objKeypointlist.get(goodMatchesListBeforeTri.get(i).queryIdx).pt;
             Point ptarrivee=scnKeypointlist.get(goodMatchesListBeforeTri.get(i).trainIdx).pt;
            
           	int lx= (int)(ix+ptarrivee.x-(x+ptdepart.x));
           	int ly= (int) (iy+ptarrivee.y-(y+ptdepart.y));
           	if (Math.abs(lx-vectBeforeTri.get(0))<100 && Math.abs(ly-vectBeforeTri.get(1))<100 ){
           		vxfinal+=(ix+ptarrivee.x-(x+ptdepart.x));
           		vyfinal+=(iy+ptarrivee.y-(y+ptdepart.y));
           		comptVFiltre++;
           		goodMatchesList.add(goodMatchesListBeforeTri.get(i));//Ici, on ne garde que les matching renvoyant des vecteurs non aberrants (demeurant assez proche du cadre moyen -x€[xmoyen-e,xmoyen+e] et de lmême pour y           	 
         }
	      }
	          
	      if (comptVFiltre!=0)
         {
         vect.add(new Integer((int) (vxfinal/comptVFiltre)));
         vect.add(new Integer((int) (vyfinal/comptVFiltre)));}
          //Si au moins un bon matching filitré est détecté alors on rentre les nouvelles coordonnées du mouvement repéré
     else {
   	  if (debug){
   		  System.out.println("comptVFiltre nul");
   	  }
   	  vect=vectIfNotWorking;
   	  return vect;//Sinon on garde le vecteur mouvement précédent
     }
          //Fin: tracé des images, actualisation  	
	      MatOfDMatch goodMatches = new MatOfDMatch();
	          goodMatches.fromList(goodMatchesList);
	          Features2d.drawMatches(imageDepartCroppe, departKeyPoints, imageArrivee, arriveeKeyPoints, goodMatches, matchoutput, matchestColor, newKeypointColor, new MatOfByte(), 2);
	          Highgui.imwrite("C://Users//dimitri//Desktop//matchoutput.jpg", matchoutput);//Renvoie les matching en image
	          
	      
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
         Core.rectangle(retour, new Point(CadreDepart.getXCoin()+vect.get(0),CadreDepart.getYCoin()+vect.get(1)), new Point(CadreDepart.getXCoin()+w+vect.get(0),CadreDepart.getYCoin()+h+vect.get(1)), new Scalar(255,0,255),0);//Nouvelle emplacement de la zone de départ dans l'image d'arrivée, en violet   
         Core.rectangle(retour, new Point(CadreDepart.getXCoin(),CadreDepart.getYCoin()), new Point(CadreDepart.getXCoin()+w,CadreDepart.getYCoin()+h), new Scalar(0,255,0),0);//La position originelle de l'objet "ciblé", en vert
         Core.rectangle(imageDepartNonCroppe, new Point(CadreDepart.getXCoin()+vect.get(0),CadreDepart.getYCoin()+vect.get(1)), new Point(CadreDepart.getXCoin()+w+vect.get(0),CadreDepart.getYCoin()+h+vect.get(1)), new Scalar(255,0,255),0);//Emplacement de la zone d'arivée dans la zone de départ, en violet 
         Core.rectangle(imageDepartNonCroppe, new Point(CadreDepart.getXCoin(),CadreDepart.getYCoin()), new Point(CadreDepart.getXCoin()+w,CadreDepart.getYCoin()+h), new Scalar(0,255,0),0);//La nouvelle position de l'objet "ciblé", en vert
         
         Highgui.imwrite("C://Users//dimitri//Pictures//RectangleArrivee.jpg", retour);
         Highgui.imwrite("C://Users//dimitri//Pictures//RectangleDepart.jpg", imageDepartNonCroppe);
	      //fichierImageArrivee.delete();//On supprime la vielle image du dossier où elle était, desormais inutile
         //fichierImageDepart.renameTo(fichierImageArrivee);//on la remplace par l'image récente, devenue vielle
	      }
         PrintWriter pw=new PrintWriter(new FileWriter(adresseCoordonnees));
         pw.print(Integer.toString((int)(x+(vxfinal/comptVFiltre)))+" "+Integer.toString((int)(y+(vyfinal/comptVFiltre)))+" "+Integer.toString(w)+" "+Integer.toString(h));
         pw.close();
     }
           else
           {vect=vectIfNotWorking;}
	 
	


	      return vect;



// TODO Auto-generated method stub

}
	  }



