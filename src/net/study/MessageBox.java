package net.study;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class MessageBox {

	private BlockingQueue<String> msgBox = new LinkedBlockingDeque<String>(1);
	
	public void put(String str) throws InterruptedException {
		msgBox.put(str);
	}
	
	public String get() throws InterruptedException {
		return msgBox.take();
	}
	
}
