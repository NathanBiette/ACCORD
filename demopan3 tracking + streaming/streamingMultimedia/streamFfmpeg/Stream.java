package streamingMultimedia.streamFfmpeg;

public class Stream extends Thread{
	private StreamFrame frame;
	private StreamFfmpeg stream;
	
	
	public Stream(){
		super("stream ffmpeg");
		this.stream = new StreamFfmpeg();
		StreamFrame streamF = new StreamFrame(this);
	}
	
	public Stream(String commandLineFile){
		super("stream ffmpeg");
		this.stream = new StreamFfmpeg(commandLineFile);
		StreamFrame streamF = new StreamFrame(this);
	}
	
	public void run(){
		stream.start();
	}
	
	public void close(){
		stream.closeStream();
		 }
}
