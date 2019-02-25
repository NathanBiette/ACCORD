package affichage;

import java.applet.Applet;
import java.awt.Graphics;

/** Classe pour afficher les courbes obtenues par 
 * la NMF (on verifie ainsi que le programme fonctionne)
 */
@SuppressWarnings("serial")
public class Courbes extends Applet {
	
	private double[][] matrice;
	private int scale;
	
	public Courbes(double[][] matrice, int scale) {
		super();
		
		this.matrice = matrice;
		this.scale = scale;
	}
	
	public void paint(Graphics g) {
		int n = matrice.length;
		int m = matrice[0].length;
		
		for (int i = 0; i<n-1 ; i++) {
			for (int j = 0 ; j < m ; j++) {
				int y1 = (int) (matrice[i][j] * scale);
				int y2 = (int) (matrice[i+1][j] * scale);
				g.drawLine(i, y1, i+1, y2);
			}
		}
	}
}
