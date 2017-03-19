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
    
	public FileServer(MessageBox msgBox) {
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
                        System.out.println("here comes one client: " + sc.getLocalAddress());
                        sc.configureBlocking(false);  
                        sc.register(selector, SelectionKey.OP_WRITE);
                    }  
                    if (key.isWritable()) { 
                    	SocketChannel client = (SocketChannel) key.channel();
                    	ByteBuffer buf = ByteBuffer.allocate(1024 << 18);
                    	FileChannel fc = FileChannel.open(new File(FileUtils.ENC_NAME).toPath(), EnumSet.of(StandardOpenOption.READ));
                    	fc.read(buf);
                    	buf.flip();
                    	client.write(buf);
                    	buf.clear();
                    	fc.close();
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
        serverSocketChannel = ServerSocketChannel.open();  
        serverSocketChannel.socket().setReuseAddress(true);  
        serverSocketChannel.socket().bind(new InetSocketAddress(port));  
        selector = Selector.open();  
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
}
