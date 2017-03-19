package net.study;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class FileClient {

	public static void main(String[] args) throws IOException {
//	    String fileName = "D:/basic.xml";
	    String fileName = "D:/log4j.properties";
	    String hostIp = "30.35.52.19";
	    int port = 9991;
	    new FileClient().sendFile(fileName, hostIp, port);
	}
	
	public void sendFile(String fileName, String hostIp, int port) throws IOException {
		int bufferSize = 1024 << 18;
	    int n = 0;
	    int read = 0;

	    SocketChannel socket = SocketChannel.open();
	    socket.configureBlocking(false);
	    socket.connect(new InetSocketAddress(hostIp, port));
	    Selector selector = Selector.open();
	    socket.register(selector, SelectionKey.OP_CONNECT);
	    String filePath = (fileName);

	    FileOutputStream out = new FileOutputStream(filePath);
	    FileChannel file = out.getChannel();
	    ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

	    selector.select();
	    Iterator ite = selector.selectedKeys().iterator();
	    while(ite.hasNext()) {
	    	selector.select();
	    	SelectionKey key = (SelectionKey) ite.next();
	    	ite.remove();
	    	SocketChannel sc = (SocketChannel) key.channel();
	    	if(key.isConnectable()) {
	    		if(sc.finishConnect()) {
	    			System.out.println("conn!!");
	    			while ((read = socket.read(buffer)) > 0) {
		    			System.out.println(read);
		    	        buffer.flip();

		    	        file.write(buffer);

		    	        n = n + read;

		    	        buffer.clear();
		    	    }
	    		} else {
	    			System.out.println("error!");
	    		}
	    	}
	    	if(key.isWritable()) {
	    		
	    	}
	    }
	    socket.close();

	    file.close();

	    out.close();
	}
}
