public class Stream {
	private StreamFrame frame;
	private StreamFfmpeg stream;
	
	
	public Stream(){
		this.stream = new StreamFfmpeg();
		StreamFrame streamF = new StreamFrame(this);
	}
	
	public Stream(String commandLineFile){
		this.stream = new StreamFfmpeg(commandLineFile);
		StreamFrame streamF = new StreamFrame(this);
	}
	
	public void run(){
		stream.run();
	}
	
	public void close(){
		stream.closeStream();
		 }
}
