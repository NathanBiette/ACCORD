package mainTest;

import streamingMultimedia.streamFfmpeg.Stream;

import java.util.concurrent.TimeUnit;

import streamingMultimedia.editorControl.*;

public class mainTest {

	public static void main(String[] args){
		// TODO Auto-generated method stub
		String pathTostreamInit = "/home/nathan/pact54/Computer/src/streamingMultimedia/streamInit";
		Stream stream = new Stream(pathTostreamInit + "/mainStream");
		editorControlThreadTest control = new editorControlThreadTest(pathTostreamInit);
		stream.start();
		control.start();
	}

}
