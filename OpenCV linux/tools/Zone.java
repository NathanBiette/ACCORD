package tools;

import exceptions.OutofFrameException;
import interfaces.InstrumentInterface;

/**
 * Outil Zone
 * Définit une zone formée par une frame (caméra 4K), un centre (viseur pour la caméra mobile)
 * éventuellement un canal DMX correspondant au projecteur éclairant la zone et un 
 * instrument qui joue dans cette zone
 * @author thibaud
 *
 */
public class Zone {

	private final int id;
	private static int idCounter = 0;
	private Frame frame;
	private Point center;
	private int dmxChannel = 0;
	private InstrumentInterface instrument = null;
	
	public Zone(Frame frame, Point center) {
		this.frame = frame;
		this.center = center;
		Zone.idCounter++;
		this.id = Zone.idCounter;
	} 

	public void setDmxChannel(int dmxChannel)
	{
		this.dmxChannel = dmxChannel;
	}
	
	public void setInstrument(InstrumentInterface instrument)
	{
		this.instrument = instrument;
	}
	
	public void setFrame(Frame frame) 
	{
		this.frame = frame;
	}
	
	public void setCenter(Point center)
	{
		this.center = center;
	}
	
	public void setCenterAuto() 
	{
		if (frame != null)
			try {
				center = Point.barycentre(frame.getPointA(), 
						new Point(frame.getPointA().getX() + frame.getWidth(),
								frame.getPointA().getY() + frame.getHeight()));
			} catch (OutofFrameException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public int getDmxChannel() {
		return dmxChannel;
	}
	public Point getCenter() {
		return center;
	}
	public Frame getFrame() {
		return frame;
	}
	public InstrumentInterface getInstrument()
	{
		return instrument;
	}
	public int getId() {
		return id;
	}

}
