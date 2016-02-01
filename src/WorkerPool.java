
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
 
public class WorkerPool {
 
    private RejectedExecutionHandlerImpl rejectionHandler = null;
    private ThreadFactory threadFactory = null;
    private ThreadPoolExecutor poolExecutor = null;
    
	public WorkerPool() {
		//Get the ThreadFactory implementation to use
		threadFactory = Executors.defaultThreadFactory();
		//RejectedExecutionHandler implementation
		 rejectionHandler = new RejectedExecutionHandlerImpl();
		//creating the ThreadPoolExecutor
		 poolExecutor = new ThreadPoolExecutor(4, 4, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1), threadFactory, rejectionHandler);
	}
	
	public ThreadPoolExecutor getPoolExecutor () {
		return poolExecutor;
	}
	
	public void execute(Runnable task) {
		poolExecutor.execute(task);
	}
	
	public void shutdown() {
		poolExecutor.shutdown();
	}
	
	
//    public static void main(String args[]) throws InterruptedException{
//        //RejectedExecutionHandler implementation
//        RejectedExecutionHandlerImpl rejectionHandler = new RejectedExecutionHandlerImpl();
//        //Get the ThreadFactory implementation to use
//        ThreadFactory threadFactory = Executors.defaultThreadFactory();
//        //creating the ThreadPoolExecutor
//        ThreadPoolExecutor executorPool = new ThreadPoolExecutor(4, 4, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1), threadFactory, rejectionHandler);
//        //start the monitoring thread
//        MyMonitorThread monitor = new MyMonitorThread(executorPool, 3);
//        Thread monitorThread = new Thread(monitor);
//        monitorThread.start();
//        //submit work to the thread pool
//        for(int i=0; i<4; i++){
//            executorPool.execute(new WorkerThread("cmd"+i));
//        }
//         
//        Thread.sleep(30000);
//        //shut down the pool
//        executorPool.shutdown();
//        //shut down the monitor thread
//        Thread.sleep(5000);
//        monitor.shutdown();
//         
//    }
    
    
}