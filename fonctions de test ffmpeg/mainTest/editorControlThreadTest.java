package mainTest;
import streamingMultimedia.*;
import exceptions.OutofFrameException;
import tools.Point;

public class editorControlThreadTest extends Thread{
	private String path;
	private StreamFfmpeg stream;
	
	public editorControlThreadTest(String path, StreamFfmpeg stream){
		super("testThreadControl");
		this.path = path;
		this.stream = stream;
	}
	
	public void run(){
		System.out.println("inside test controler thread");
		EditorControl control = new EditorControl(path + "/crop.txt", path + "/SelectStream.txt");
		try {
			this.sleep(10000);
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
		for(int i = 0; i< 100; i++){
			try {
				//p = new Point((int) (Math.sin(i)*720),(int) (Math.sin(i)*480));
				p = new Point(4*i,4* i);
			} catch (OutofFrameException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				control.moveCrop(p);
				System.out.println("crop moving");
				try {
					this.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		stream.close();
	}
}
