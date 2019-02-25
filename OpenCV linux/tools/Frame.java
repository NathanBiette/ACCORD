package tools;

import exceptions.OutofFrameException;

/** Outil de cadrage pour la caméra 4K
 * défini par deux points
 * @author felixgaschi
 *
 */
public class Frame {
	
	private Point A;
	private int height;
	private int width;
	
	
	public Frame(int x1, int y1, int width, int height) throws OutofFrameException {
		this.A = new Point(x1, y1);
		this.height = height;
		this.width = width;
	}

	public Point getPointA() {
		return A;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
}
