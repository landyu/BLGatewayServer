import java.io.*;  

public class ProgramMonitorThread implements Runnable
{
    private boolean run=true;
    private GatewayServer gatewayServer = null;
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

   public ProgramMonitorThread(GatewayServer gatewayServer) {
		// TODO Auto-generated constructor stub
	   this.gatewayServer = gatewayServer;
	}
   
   
    public void shutdown(){
        this.run=false;
    }
 
    @Override
    public void run()
    {
        while(run){
        	String msg;
			try {
				msg = reader.readLine();
	        	if (msg.equals("exit")) {
	        		gatewayServer.shutdown();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
        }
             
    }
}