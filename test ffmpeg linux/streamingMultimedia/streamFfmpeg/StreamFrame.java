package streamingMultimedia.streamFfmpeg;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;



public class StreamFrame extends JFrame {
	private Stream stream;
	private JLabel label;
	private JButton button;
	
	public StreamFrame(Stream stream){
		super("Stream FFMPEG");
		this.stream = stream;
		setLayout(new FlowLayout());
		label = new JLabel("Streaming ...");
		add(label);
		button = new JButton("STOP");
		add(button);
		event f = new event();
		button.addActionListener(f);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(180, 80);
		this.setVisible(true);
		this.setResizable(false);
		this.setTitle("Stream FFMPEG");
	}
	
	public class event implements ActionListener {
		 @Override
		    public void actionPerformed(ActionEvent f) {
		        System.out.println("CLOSING STREAM");
		        button.setVisible(false);
		        stream.close();
		        label.setText("Streaming stopped");	
		 	}
		 }
}
