package net.study;

import java.io.File;
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
//	    String fileName = "D:/aaa.zip";
	    String fileName = "/home/wittli/mybaks.zip";
//	    String hostIp = "30.35.52.19";
	    String hostIp = "192.168.0.101";
	    int port = 9991;
	    new FileClient().sendFile(fileName, hostIp, port);
	}
	
	public void sendFile(String fileName, String hostIp, int port) throws IOException {
		int bufferSize = 1024 << 4;
	    int read = 0;

	    Selector selector = Selector.open();
	    SocketChannel socket = SocketChannel.open();
	    socket.configureBlocking(false);
	    if(socket.connect(new InetSocketAddress(hostIp, port))) {
	    	socket.register(selector, SelectionKey.OP_READ);
	    } else {
	    	socket.register(selector, SelectionKey.OP_CONNECT);
	    }

	    File f = new File(fileName);
    	if(!f.exists()) {
    		f.createNewFile();
    	}
    	FileOutputStream out = new FileOutputStream(fileName);
	    FileChannel file = out.getChannel();
	    
	    while(selector.select() > 0) {
	    	Iterator ite = selector.selectedKeys().iterator();
		    while(ite.hasNext()) {
		    	
		    	SelectionKey key = (SelectionKey) ite.next();
		    	ite.remove();
		    	SocketChannel sc = (SocketChannel) key.channel();
		    	if(key.isConnectable()) {
		    		if(sc.finishConnect()) {
		    			System.out.println("conn!!");
		    			sc.write(ByteBuffer.wrap("hi".getBytes("UTF-8")));
		    			key.interestOps(SelectionKey.OP_READ);
		    		} else {
		    			System.out.println("error!");
		    		}
		    	}
		    	if(key.isReadable()) {
		    		System.out.println("client reading.");
		    		ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		    		read = socket.read(buffer);
	    			System.out.println(read);
	    	        buffer.flip();
	    	        if(read > 0) {
	    	        	file.write(buffer);
	    	        	buffer.clear();
	    	        } else {
	    	        	System.out.println("read < 0");
	    	        	file.close();
	    	    	    out.close();
	    	        	socket.close();
	    	        }
	    	        sc.write(ByteBuffer.wrap("h".getBytes()));
//	    	        sc.close();
//	    	        key.interestOps(SelectionKey.OP_READ);
		    	}
		    }
	    }
    	socket.close();
	}
}
