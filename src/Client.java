
import java.net.Socket;
import java.nio.channels.*;
import java.util.Queue;

public class Client {

	public Socket socket;
	public SocketChannel socketChannel;
	public byte CID;
	public byte sendSC;
	public byte receiveSC;
	public Queue<TunnellingFrame> sendMsgQueue;
	public Queue<TunnellingFrame> receiveMsgQueue;
	
}

