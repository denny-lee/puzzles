package net.study;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

public class MsgServer {
	
	private boolean inited = false;
	private MessageBox msgBox;

	private int port = 9991;  
    private ServerSocketChannel serverSocketChannel;  
    private Charset charset = Charset.forName("UTF-8");  
    private Selector selector = null;
    
	public MsgServer(MessageBox msgBox) {
		this.msgBox = msgBox;
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
                        sc.register(selector, SelectionKey.OP_READ);
                    }  
                    if (key.isReadable()) { 
                    	SocketChannel client = (SocketChannel) key.channel();
                    	String msg = msgBox.get();
                    	if(null != msg && !"".equals(msg)) {
                    		ByteBuffer buf = ByteBuffer.wrap(msg.getBytes("UTF-8"));
                            client.write(buf);
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
                } catch (InterruptedException e) {
					e.printStackTrace();
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
