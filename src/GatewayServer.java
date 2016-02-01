import java.io.*;  
import java.nio.channels.*;
import java.net.InetSocketAddress;
//import java.net.ServerSocket;  
//import java.net.Socket;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
import java.util.logging.Level;  
import java.util.logging.Logger;

//import org.xml.sax.HandlerBase;

public class GatewayServer {
    private final static Logger logger = Logger.getLogger(GatewayServer.class.getName());  
    private boolean run=true;

//    public class CtrlC implements Runnable {  
//        private boolean bExit = false;  
////        private ServerSocket server;
//        private class ExitHandler extends Thread {  
//            public ExitHandler() {  
//                super("Exit Handler");  
//            }  
//            public void run() {  
//                System.out.println("Set exit");  
//                bExit = true;  
//            }  
//        }  
//        public CtrlC() {  
//            Runtime.getRuntime().addShutdownHook(new ExitHandler());  
//        }  
//        public void run(){  
//            while (!bExit) {  
//                // Do some thing  
//                try {
//                    Thread.sleep(1);
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
////            if(this.server != null){
////                try {
////                    System.out.println("this.server.close()");
////                    this.server.close();
////                } catch (IOException e) {
////                    // TODO Auto-generated catch block
////                    e.printStackTrace();
////                }
////            }
//            System.out.println("Exit OK");  
//        }  
//
////        public void setServer(ServerSocket server){
////            this.server = server;
////        }
//    } 
    
    public void shutdown(){
        this.run=false;
    }
    
    
    private int port = 10009;
    private ServerSocketChannel serverSocketChannel = null;
    private WorkerPool workerPool;
    //private static final int POOL_MULTIPLE = 4;
    
    public GatewayServer() throws IOException{
	// TODO Auto-generated constructor stub
    	//Create a thread pool
    	//executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_MULTIPLE);
    	workerPool = new WorkerPool();
    	
    	serverSocketChannel = ServerSocketChannel.open();
    	serverSocketChannel.socket().setReuseAddress(true);
    	serverSocketChannel.socket().bind(new InetSocketAddress(port));
    	System.out.println("Gateway Server Start...");
    }   
    
    
    public void service() throws InterruptedException{
    	System.out.println("service Run...");
    	
    	//start the monitoring thread
        ThreadPoolMonitorThread threadPoolmonitor = new ThreadPoolMonitorThread(workerPool.getPoolExecutor(), 10);
        Thread threadPoolmonitorThread = new Thread(threadPoolmonitor);
        System.out.println("Start Thread Pool Monitor Thread...");
        threadPoolmonitorThread.start();
        
        ProgramMonitorThread programMointor = new ProgramMonitorThread(this);
        Thread programMointorThread = new Thread(programMointor);
        System.out.println("Start Program Monitor Thread...");
        programMointorThread.start();
        
    	while (run) {
    		SocketChannel socketChannel = null;
    		try {
    			//block here until accept a new connection
				socketChannel = serverSocketChannel.accept();
				//put the new socket into the thread poll
		        workerPool.execute(new WorkerThread(socketChannel));
		        
			} catch (IOException e) {
				// TODO: handle exception
				logger.log(Level.SEVERE, null, e);
				e.printStackTrace();
			}
			
		}
    	
    	try {
    		serverSocketChannel.close();
		} catch (IOException e) {
			// TODO: handle exception
			logger.log(Level.SEVERE, null, e);
			e.printStackTrace();
		}
    	
    	workerPool.shutdown();
    	Thread.sleep(5000);
    	threadPoolmonitor.shutdown();
    	Thread.sleep(5000);
    	programMointor.shutdown();
    	
    	
    }
    
    
    
    
    public static void main(String[] args) throws IOException, InterruptedException{  
//        GatewayServer gatewayServer = new GatewayServer();
//        GatewayServer.CtrlC ctrlC = gatewayServer.new CtrlC();  
//        Thread ctrlcThread = new Thread(ctrlC);  
//        ctrlcThread.setName("Ctrl C Thread");  
//        ctrlcThread.start();  
//        ctrlcThread.join();
    	
//    	int connectionIndex = 0;
//    	
//    	ServerSocket server = null;
//    	try{
//            server = new ServerSocket(10005);  
//            while (true) {  
//                Socket socket = server.accept();  
//                invoke(socket, connectionIndex++);  
//            }
//    	}catch (IOException ex) {  
//            logger.log(Level.SEVERE, null, ex);  
//        } finally {  
//        	 try {  
//        		 server.close(); 
//        		 System.out.println("Exit OK"); 
//             } catch(Exception ex) {
//            	 ex.printStackTrace();
//             }
//        }
    	
    	
    	new GatewayServer().service();
    	System.out.println("GatewayServer Exit.....");
    }  

//    private static void invoke(final Socket socket,  final int connectionIndex) throws IOException {  
//        new Thread(new Runnable() {  
//            public void run() {  
//            	InputStream is = null;  
//                OutputStream os = null;  
//                try { 
//                	System.out.println("Connection " + connectionIndex + " Established");
//                    is = socket.getInputStream();  
//                    os = socket.getOutputStream(); 
//                    while(true){
//                    	int receivedStreamLength = is.available();
//                    	if(receivedStreamLength <= 0){
//                    		continue;
//                    	}
//                    	byte[] receivedMsg = new byte[receivedStreamLength];
//                    	is.read(receivedMsg);
//                        System.out.println("Connection " + connectionIndex + " recdivedMsg(" +  receivedStreamLength + "):" + receivedMsg);  
//                        int recivedMsgFlag = receivedMsg[0] & 0xFF;
//                        if(recivedMsgFlag == 0xFF){
//                        	break;
//                        }
//                        os.write(receivedMsg);  
//                        os.flush();
//                    }  
//                } catch (IOException ex) {  
//                    logger.log(Level.SEVERE, null, ex);  
//                } finally {  
//                    try {  
//                        is.close();  
//                    } catch(Exception ex) {}  
//                    try {  
//                        os.close();  
//                    } catch(Exception ex) {}  
//                    try {
//                    	System.out.println("Connection " + connectionIndex + " Close");
//                        socket.close();  
//                    } catch(Exception ex) {}  
//                }  
//            }  
//        }).start();  
//    }
    
//    //every task has one Handler method to handler its own task
//    class Handler implements Runnable{
//    	private SocketChannel socketChannel;
//    	public Handler(SocketChannel socketChannel){
//    		this.socketChannel = socketChannel;
//    	}
//    	//in thread pool executorService.execute will execute the run method
//    	public void run() {
//    		handle(socketChannel);
//		}
//    }
//    
//    
//    //invoke within the task handler Runnable Interface's run method
//    public void handle(SocketChannel socketChannel){
//    	try {
//			Socket socket = socketChannel.socket();
//			System.out.println("Get Connection From Client: " + socket.getInetAddress() + ":" + socket.getPort());
//			
//			BufferedReader br = getReader(socket);
//			PrintWriter pw = getWriter(socket);
//			
//			String msg = null;
//			while ((msg = br.readLine()) != null) {
//				
//			}
//			
//			System.out.println("Exit Thread : ");
//			
//			
// 		} catch (IOException e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}finally {
//			try {
//				if (socketChannel != null) {
//					socketChannel.close();
//				}
//			} catch (IOException e) {
//				// TODO: handle exception
//				e.printStackTrace();
//			}
//		}
//    }
//    
//    private PrintWriter getWriter(Socket socket) throws IOException{
//    	OutputStream socketOut = socket.getOutputStream();
//    	return new PrintWriter(socketOut, true);
//    }
//    
//    private BufferedReader getReader(Socket socket)throws IOException{
//    	InputStream socketIn = socket.getInputStream();
//    	return new BufferedReader(new InputStreamReader(socketIn));
//    }

}
























