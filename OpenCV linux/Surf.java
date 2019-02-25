import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;

public class Surf {
	
	private Boolean debug;
	float nndrRatio;
	private int margeRechercheCrop;
	private int epsilon;
	
	private Mat imageDepartCroppe;
	private Mat imageArriveeCroppe;
	private Cadre cadreDepart;
	private Cadre cadreArrivee;
	private MatOfKeyPoint departKeyPoints;
	private FeatureDetector featureDetector;
	private MatOfKeyPoint departDescriptors;
	private DescriptorExtractor descriptorExtractor;
	private MatOfKeyPoint arriveeKeyPoints;
	private MatOfKeyPoint arriveeDescriptors;
	private List<MatOfDMatch> matches;
	private DescriptorMatcher descriptorMatcher;
	private LinkedList<DMatch> goodMatchesListBeforeTri;
	
	private ArrayList<Integer> vectorOfMove;
	

	
	public Surf(Boolean debug, int margeRechercheCrop, float nndrRatio, int epsilon){
		this.debug = debug;
		this.margeRechercheCrop = margeRechercheCrop;
		this.nndrRatio = nndrRatio;
		this.epsilon = epsilon;
	}
	
	public ArrayList<Integer> surf(Mat imageDepartNonCroppe, Mat imageArriveeNonCroppe, int x, int y, int w, int h){
		
	      cadreDepart = new Cadre(imageDepartNonCroppe,x,y,w,h);
	      imageDepartCroppe = cadreDepart.getImageCroppe();										// "Croppage" de l'image originale-L'appel � la classe cadre permet de controler les coordonn�es du cadre et donc d'�viter un out of range
	        
	      cadreArrivee=new Cadre(imageArriveeNonCroppe,x - margeRechercheCrop ,y - margeRechercheCrop ,w + 2*margeRechercheCrop,h + 2*margeRechercheCrop);
	      imageArriveeCroppe = cadreArrivee.getImageCroppe();
	
	     
	      //Creation des descripteurs sur l'image de depart
	      departKeyPoints = new MatOfKeyPoint();
	      featureDetector = FeatureDetector.create(FeatureDetector.SURF);
	      
	      if (debug){System.out.println("Detecting key points...");}
	      
	      featureDetector.detect(imageDepartCroppe, departKeyPoints);		//Detection des points cl�s qui sont mis dans departKeyPoints
	      departDescriptors = new MatOfKeyPoint();
	      descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SURF);
	      
	      if (debug){System.out.println("Computing descriptors...");}
	      
	      descriptorExtractor.compute(imageDepartCroppe, departKeyPoints, departDescriptors);//Associe un point cl� et un descripteur
	     
	      if (debug){System.out.println("Drawing key points on depart image...");}
	      
	      
	      // Match depart image with the arrivee image
	      arriveeKeyPoints = new MatOfKeyPoint();
	      arriveeDescriptors = new MatOfKeyPoint();
	      
	      if (debug){System.out.println("Detecting key points in background image...");}
	      
	      featureDetector.detect(imageArriveeCroppe, arriveeKeyPoints);
	      
	      if (debug){System.out.println("Computing descriptors in background image...");}
	      
	      descriptorExtractor.compute(imageArriveeCroppe, arriveeKeyPoints, arriveeDescriptors);
	      matches = new LinkedList<MatOfDMatch>();
	      descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
	      
	      if (debug){System.out.println("Matching depart and arrivee images...");}
	      
	      descriptorMatcher.knnMatch(departDescriptors, arriveeDescriptors, matches,2);
	      
	      if (debug){
	    	  System.out.println("Calculating good match list...");
	      }
	      
	      goodMatchesListBeforeTri = new LinkedList<DMatch>();	      
	      
	      
	      for (int i = 0; i < matches.size(); i++) {
	          MatOfDMatch matofDMatch = matches.get(i);
	          DMatch[] dmatcharray = matofDMatch.toArray();
	          DMatch m1 = dmatcharray[0];
	          DMatch m2 = dmatcharray[1];
	          if (m1.distance <= m2.distance * nndrRatio) {
	              goodMatchesListBeforeTri.addLast(m1);			//Ajout de tous les bons matching
	          }
	      }
	      if (goodMatchesListBeforeTri.size() >= 1) {
	    	  vectorOfMove = filtreVector();
	      }
	      return vectorOfMove;
	}
	
	
	public ArrayList<Integer> filtreVector(){
		
		double vx=0,vy=0,comptVNonFiltre=0,vxfinal=0,vyfinal=0,comptVFiltre=0;      

		if (debug){System.out.println("depart Found!!!");}
	          
	    List<KeyPoint> objKeypointlist = departKeyPoints.toList();
	    List<KeyPoint> scnKeypointlist = arriveeKeyPoints.toList();
	          
	    //Estimation du vecteur d�placement moyen
	   for (int i = 0; i < goodMatchesListBeforeTri.size(); i++) {
	    	Point ptdepart=objKeypointlist.get(goodMatchesListBeforeTri.get(i).queryIdx).pt;
	        Point ptarrivee=scnKeypointlist.get(goodMatchesListBeforeTri.get(i).trainIdx).pt;
	        vx+=(cadreArrivee.getXCoin() + ptarrivee.x - (cadreDepart.getXCoin() + ptdepart.x));
	        vy+=(cadreArrivee.getYCoin()+ ptarrivee.y - (cadreDepart.getYCoin() + ptdepart.y));
	        //ajout méthode médiane ici 
	        comptVNonFiltre++;
	        }
	    vx = vx/comptVNonFiltre;
	    vy = vy/comptVNonFiltre;
	    
	    
	      //Tri et estimation du vecteur mouvement en retirant les matching aberrants
	     for (int i = 0; i < goodMatchesListBeforeTri.size(); i++) {
	    	 Point ptdepart=objKeypointlist.get(goodMatchesListBeforeTri.get(i).queryIdx).pt;
	    	 Point ptarrivee=scnKeypointlist.get(goodMatchesListBeforeTri.get(i).trainIdx).pt;
          
	    	 vx+=(cadreArrivee.getXCoin() + ptarrivee.x - (cadreDepart.getXCoin() + ptdepart.x));
		     vy+=(cadreArrivee.getYCoin()+ ptarrivee.y - (cadreDepart.getYCoin() + ptdepart.y));
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
		
		return vectorOfMove;
	}
}
