
import java.io.*;
import java.net.Socket;
import java.nio.channels.*;
import java.util.logging.Level;
import java.util.logging.Logger; 

public class WorkerThread implements Runnable {
	 private final static Logger logger = Logger.getLogger(GatewayServer.class.getName()); 
    private SocketChannel socketChannel;
    private Socket socket;
     
    public WorkerThread(SocketChannel socketChannel){
        this.socketChannel = socketChannel;
        socket = socketChannel.socket();
    }
 
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " Start. From IPAddress = " + socket.getInetAddress());
        processCommand();
        System.out.println(Thread.currentThread().getName() + " From IPAddress = " + socket.getInetAddress() + " End.");
    }
 
    private void processCommand() {  
//    	InputStream is = null;  
//        OutputStream os = null;  
        try { 
        	//System.out.println("Connection " + connectionIndex + " Established");
//            is = socket.getInputStream();  
 //           os = socket.getOutputStream(); 
            
			BufferedReader br = getReader(socket);
			PrintWriter pw = getWriter(socket);
			
			BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
			
//            while(true)
            {
//            	int receivedStreamLength = is.available();
//            	if(receivedStreamLength <= 0){
//            		Thread.sleep(10);
//            		continue;
//            	}
//            	byte[] receivedMsg = new byte[receivedStreamLength];
//            	is.read(receivedMsg);
            	
    			String msg = null;
    			byte[] receivedMsg = new byte[1024];
    			int receivedMsgLength = 0;
//    			while ((msg = br.readLine()) != null) {
//    				System.out.println("Receive Message ---" + msg + "---From IPAddress :" + socket.getInetAddress()); 
//    				pw.println("echo" + msg);
//    				if (msg.equals("bye")) {
//						break;
//					}
//    			}
    			while ((receivedMsgLength = bis.read(receivedMsg, 0, 1024)) != -1) {
    				String remess = new String(receivedMsg, 0, receivedMsgLength);  
				System.out.println("Receive Message Length = " + receivedMsgLength + "---" + remess + "---From IPAddress :" + socket.getInetAddress()); 
				if (remess.equals("bye\r\n")) {
					break;
				}
			}
//                System.out.println("Receive Message From IPAddress :" + socket.getInetAddress());  
//                int recivedMsgFlag = receivedMsg[0] & 0xFF;
//                if(recivedMsgFlag == 0xFF){
//                	break;
//                }
//                os.write(receivedMsg);  
//                os.flush();
            }  
        } catch (IOException ex) {  
            logger.log(Level.SEVERE, null, ex);  
        } finally {  
//            try {  
//                is.close();  
//            } catch(Exception ex) {}  
//            try {  
//                os.close();  
//            } catch(Exception ex) {}  
            try {
            	if(socketChannel != null){
                	System.out.println("Connection From IPAddress :" + socket.getInetAddress() + " Close");
                    socketChannel.close(); 
            	} 
            } catch(Exception ex) {}  
        }   
    }
    
  private PrintWriter getWriter(Socket socket) throws IOException{
	OutputStream socketOut = socket.getOutputStream();
	return new PrintWriter(socketOut, true);
}

private BufferedReader getReader(Socket socket)throws IOException{
	InputStream socketIn = socket.getInputStream();
	return new BufferedReader(new InputStreamReader(socketIn));
}


//    @Override
//    public String toString(){
//        return this.command;
//    }
}
