package teest;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class Application extends JFrame  {

		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		private JLabel label;
		private JButton button;
		private RepeatAction action;
		public Application() throws IOException{
			super("Pour fermer, cliquer");

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
			this.setTitle("Fenêtre d'arrêt");
			action=new RepeatAction();

		}
		
		public class event implements ActionListener {
			 @Override
			    public void actionPerformed(ActionEvent f) {
			        System.out.println("Arrêt en cours");
			        action.endRepetition();//Amène la fin de l'execution
			           
			 	}
		}		
}

