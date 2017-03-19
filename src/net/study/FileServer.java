package net.study;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.Iterator;

import encryp.FileUtils;

public class FileServer {
	
	private boolean inited = false;

	private int port = 9991;  
    private ServerSocketChannel serverSocketChannel;  
    private Charset charset = Charset.forName("UTF-8");  
    private Selector selector = null;
    
	public FileServer() {
		try {
			init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start() throws IOException {
		if(!inited) {
			init();
		}
		service();
	}
	
	public void service() throws IOException {
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		FileChannel fc = FileChannel.open(new File(FileUtils.ENC_NAME).toPath(), EnumSet.of(StandardOpenOption.READ));
		ByteBuffer bb = ByteBuffer.allocate(1024 << 4);
		while (selector.select() > 0) {  
			Iterator iterator = selector.selectedKeys().iterator();  
			while (iterator.hasNext()) {  
                SelectionKey key = null;
                try { 
                	key = (SelectionKey) iterator.next();  
                    iterator.remove();  
                    if (key.isAcceptable()) {  
                        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();  
                        SocketChannel sc = ssc.accept(); 
                        System.out.println("here comes one client: " + sc.getRemoteAddress());
                        sc.configureBlocking(false);  
                        sc.register(selector, SelectionKey.OP_READ);
                        key.interestOps(SelectionKey.OP_ACCEPT);
                    }  
                    if (key.isReadable()) { 
                    	SocketChannel sc = (SocketChannel) key.channel();
                    	ByteBuffer echo = ByteBuffer.allocate(3);
                    	int read = sc.read(echo);
                    	if(read > 0) {
                    		echo.clear();
                    		int remain = fc.read(bb);
                    		if(remain > 0) {
                    			bb.flip();
                        		int w = sc.write(bb);
                            	System.out.println(w);
                    		} else {
                    			fc.close();
                    		}
                        	bb.clear();
                        	key.interestOps(SelectionKey.OP_READ);
                    	}
                    }  
                } catch (IOException e) {  
                    e.printStackTrace();  
                    try {  
                        if (key != null) {  
                            key.cancel();  
                            key.channel().close();  
                        }  
                    } catch (ClosedChannelException cex) {  
                        e.printStackTrace();  
                    }  
                }
			}
		}
	}
	
	public void init() throws IOException {
		selector = Selector.open();  
        serverSocketChannel = ServerSocketChannel.open();  
        serverSocketChannel.socket().bind(new InetSocketAddress(port));  
        inited = true;
        System.out.println("服务器启动");  
	}
	public void destroy() throws IOException {
		serverSocketChannel.close();
		serverSocketChannel = null;
		selector.close();
		selector = null;
		System.out.println("服务废了");
		inited = false;
	}
	/* 编码过程 */  
    public ByteBuffer encode(String str) {  
        return charset.encode(str);  
    }  
  
    /* 解码过程 */  
    public String decode(ByteBuffer bb) {  
        return charset.decode(bb).toString();  
    } 
    
    public static void main(String[] args) {
    	try {
			new FileServer().start();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
