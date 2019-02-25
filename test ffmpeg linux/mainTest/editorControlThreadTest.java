package mainTest;

import exceptions.OutofFrameException;
import streamingMultimedia.editorControl.EditorControl;
import tools.Point;

public class editorControlThreadTest extends Thread{
	private String path;
	
	public editorControlThreadTest(String path){
		super("testThreadControl");
		this.path = path;
	}
	
	public void run(){
		System.out.println("inside test controler thread");
		EditorControl control = new EditorControl(path + "/crop.txt", path + "/SelectStream.txt");
		try {
			this.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0; i< 3; i++){
			System.out.println("switching cam");
			control.selectCamera(i);
			try {
				this.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		control.selectCamera(1);
		Point p = null;
		for(int i = 0; i< 400; i++){
			try {
				//p = new Point((int) (Math.sin(i)*720),(int) (Math.sin(i)*480));
				p = new Point(i, i);
			} catch (OutofFrameException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				control.moveCrop(p);
		}
	}
}
