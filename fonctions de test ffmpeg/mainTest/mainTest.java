package mainTest;

import streamingMultimedia.*;

public class mainTest {

	public static void main(String[] args){
		String pathTostreamInit = "./src/streamingMultimedia/streamInit";
		StreamFfmpeg stream = new StreamFfmpeg(pathTostreamInit + "/mainStream");
		editorControlThreadTest control = new editorControlThreadTest(pathTostreamInit, stream);
		stream.start();
		control.start();
	}
}
