import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server waits for a connection from the android mobile application and
 * a route from a URL both with address localhost and port 3000.
 * 
 * When both connections are made and depending on the route taken a color 
 * will be sent back to the application.
 * 
 * Routes; red,yellow,blue,green.
 * 
 * @author Johnathon Malott
 * @version 4/27/2015
 */
public class Server {
	public static void main(String a[]){
		/**Port to listen on*/
		final int PORT = 3000;
		//Time delays
		final int TEN_MIN = 300000;
		final int ONE_SEC = 1000;
        /**Store default color as white*/
	    ColorState colorState = new ColorState("white");
	    /**Socket that server is listening on*/
	    ServerSocket socket = null;
		    
	    try{
	      socket = new ServerSocket(PORT);
	      //Server will shutdown after 10 minutes  
	      socket.setSoTimeout(TEN_MIN);
          System.out.println("have opened port " + PORT + " locally." + InetAddress.getLocalHost().getHostAddress());

          //Keep running till all threads are done
		  do{
			  //Accept socket connection and pass to thread to listen from another connection
			  if(Thread.activeCount() < 3){
		         Socket sock = socket.accept();
		         System.out.println("client has made socket connection");
                 (new Thread (new OneConnection(sock,colorState))).start();
                 System.out.println("Threads: " + Thread.activeCount());
			  } else {
				  //Delay so that Route URL can finish task
				  Thread.sleep(ONE_SEC);
			  }
		      //pool.execute(new OneConnection(sock, color));
		  }while(colorState.getServerStatus());
	    }catch(Exception e){e.printStackTrace();}
	    finally{
	      try{
	    	  if((socket != null ) && !socket.isClosed())
	    		  System.out.println("Server Closing.");
		          socket.close();
	      }catch(IOException e){System.out.println("Error Closing Server Socket.");}
		}
	}
}