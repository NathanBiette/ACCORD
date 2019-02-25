import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class Application extends JFrame  {
	
	private TrackingMonitor monitor;
	private JLabel label;
	private JButton button;
	
	/**
	 * constructeur de la vue pour arret manuel
	 * @throws IOException
	 */
	public Application(TrackingMonitor monitor) throws IOException{
		super("Pour fermer, cliquer");

		this.monitor = monitor;
		
		setLayout(new FlowLayout());
		label = new JLabel("Not Running");
		add(label);
		button = new JButton("Stop");
		add(button);
		event f = new event();
		button.addActionListener(f);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(180, 180);
		this.setVisible(true);
		this.setResizable(false);
		this.setTitle("Fen�tre d'arr�t");

		}
		
		public class event implements ActionListener {
			 @Override
			    public void actionPerformed(ActionEvent f) {
			        System.out.println("Arr�t en cours");
			        monitor.stopTracking();//Am�ne la fin de l'execution
			           
			 	}
		}		
}

