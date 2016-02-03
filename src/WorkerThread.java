
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
//import java.util.Arrays;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger; 

public class WorkerThread implements Runnable {
	 private final static Logger logger = Logger.getLogger(GatewayServer.class.getName()); 
	 private enum FrameGotStatusEnum { NoAction, ToGetHeader, ToGetWholeFrame};
	 private SocketChannel socketChannel;
	 private Socket socket;
	 public Queue<byte[]> tunnellingFrameQueue;
    
//    private Client client = new Client();
     
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
        try { 

				BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());

    			byte[] receivedMsg = new byte[1024];
    			ByteBuffer tunnellingFrames = ByteBuffer.allocate(1024 * 2);
    			ByteBuffer tunnellingFramesOrig = ByteBuffer.allocate(1024 * 2);
    			int receivedMsgLength = 0;
//    			int offsetToWrite = 0;
    			int frameHeaderLength = 0;
    			int frameTotalLength = 0;
    			FrameGotStatusEnum frameGotStatus = FrameGotStatusEnum.NoAction;
    			
    			tunnellingFrames.clear();
    			tunnellingFramesOrig.clear();
    			while (true) {
    				
    				if ((receivedMsgLength = bis.read(receivedMsg, 0, 1024)) == -1) {
						
    					//read from socket error break the while
    					break;
					}
//    				if (offsetToWrite < 1024) {
//    					System.arraycopy(receivedMsg,0,tunnellingFrames,offsetToWrite,receivedMsgLength);
//        				offsetToWrite += receivedMsgLength;
//					}
    				
    				tunnellingFramesOrig.put(receivedMsg, 0, receivedMsgLength);
    				
    				tunnellingFrames = tunnellingFramesOrig.duplicate();
    				
    				//set limit = position then position = 0  
					tunnellingFrames.flip();
    				
    				boolean frameGotStatusFlag = true;
    				do{
    					switch (frameGotStatus) {
							case NoAction:{
								if(tunnellingFrames.hasRemaining()){
									frameHeaderLength = tunnellingFrames.get(tunnellingFrames.position());
									if (frameHeaderLength == 0x06) {
										frameGotStatus = FrameGotStatusEnum.ToGetHeader;
										break;
									}
									else{
										//header is wrong remove it and continue
										tunnellingFrames.get();
										break;
									}
								}
								else{
									//no data in the frame buff exit while
									frameGotStatusFlag = false;
									break;
								}
							}
							case ToGetHeader:{
								if (tunnellingFrames.remaining() >= frameHeaderLength ) {
									//tunnellingFrames.position() +   omit the illegal header byte
									int totalLenghtH = tunnellingFrames.get(tunnellingFrames.position() + frameHeaderLength - 2);
									int totalLenghtL = tunnellingFrames.get(tunnellingFrames.position() + frameHeaderLength - 1);
									frameTotalLength = totalLenghtH << 8 | totalLenghtL;
									frameGotStatus = FrameGotStatusEnum.ToGetWholeFrame;
									break;
								}
								else{
									//do not get enough data
									frameGotStatusFlag = false;
									break;
								}
							}
							case ToGetWholeFrame:{
								if (tunnellingFrames.remaining() >= frameTotalLength ) {
									//check frame whether it is correct if not correct remove the first byte and do the switch again
									byte[] frame = new byte[frameTotalLength];
									
									tunnellingFrames.get(frame, 0, frameTotalLength);
									this.processTunnellingFrame(frame);
									frameGotStatus = FrameGotStatusEnum.NoAction;
									frameHeaderLength = 0;
					    			frameTotalLength = 0;
									frameGotStatusFlag = tunnellingFrames.hasRemaining();
									break;
								}
								else{
									//do not get enough data
									frameGotStatusFlag = false;
									break;
								}
							}
							default:
								break;
    					}
    				}while(frameGotStatusFlag);
    				
    				
    				//get part frame remove the processed byte put the remaining byte to the begin
    				tunnellingFramesOrig.clear();
    				if (tunnellingFrames.hasRemaining()) {
    					tunnellingFramesOrig.put(tunnellingFrames);
					}
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

    
    private void processTunnellingFrame(byte[] frame){
    	int headerLength = frame[0];
    	byte[] header = new byte[headerLength];
    	System.arraycopy(frame, 0, header, 0, headerLength);
    	int totalLength = header[headerLength - 2] << 8 |  header[headerLength - 1];
    	
    	int structLength = frame[headerLength];
    	byte[] struct = new byte[structLength];
    	System.arraycopy(frame, headerLength, struct, 0, structLength);
    	
    	int requestDataLength = totalLength - headerLength - structLength;
    	byte[] requestData = new byte[requestDataLength];
    	System.arraycopy(frame, headerLength + structLength, requestData, 0, requestDataLength); 
    	
    	System.out.println("================= " + totalLength + "================= ");
    	System.out.println("Header = " + javax.xml.bind.DatatypeConverter.printHexBinary(header));
    	System.out.println("Struct = " + javax.xml.bind.DatatypeConverter.printHexBinary(struct));
    	System.out.println("Data = " + javax.xml.bind.DatatypeConverter.printHexBinary(requestData));
    	
    	
    }

//    @Override
//    public String toString(){
//        return this.command;
//    }
}
