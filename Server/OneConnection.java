import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Sets up input and output stream for connection and check to see if the application
 * connected or route from URL. 
 *  -If application it will wait for route before proceeding. 
 *  -If route, it will change based on route and wake application connection to send 
 *   color through. 
 * 
 * @author Johnathon Malott
 * @version 4/27/2015
 */
public class OneConnection implements Runnable{
	/**Start position for searching*/
	final int START = 0;
	/**End of keyword background in route*/
	final int END_BG = 16;
	/**End of keyword android from application*/
	final int END_APP = 7;
	/**Start of keyword background in route*/
	final int START_BG = 5;
	/**Socket that is holding connection.*/
	Socket socket;
	/**Input stream to be read from.*/
	BufferedReader in = null;
	/**Output stream to write to.*/
	BufferedWriter out = null;
	/**Color that is from route*/
	String color = "white";
	/**Holds data about the color to send to application*/
	ColorState colorState;

	/**
	 * Sets up connection with input and output stream.
	 * 
	 * @param socket Connection that was made.
	 * @param colorState Holds state of color to be pass to application.
	 * @throws IOException
	 */
	OneConnection(Socket socket, ColorState colorState) throws Exception{
	  this.socket = socket;
      this.colorState = colorState;
	  in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	  out = new BufferedWriter (new OutputStreamWriter (socket.getOutputStream()));
	}
	  
	/**
	 * Starts thread and finds if connection was made by application or Route URL.
	 */
	public void run(){
		//Message sent through input Stream.
		String input =null;
		
		try{
		      input = in.readLine();
		      
		      if(input == null) input="";
		      
		      //If Route URL
		      if(input.indexOf("GET") > -1){
		          startRoute(input);
		      } else if((input.length() >= END_APP) && input.substring(START,END_APP).equals("Android")){
		    	  while(colorState.getServerStatus()){
		            startApp();
		            //send color to application
		            out.write( colorState.getColor() + "\n");
		  	        out.flush();
		    	  }
		      }
		}catch(Exception e){ e.printStackTrace();}
		finally{
			try{
			   closeConnection();
			}catch(Exception e){System.out.println("Error closing input/ouput Stream");}
		}
	}
	
	/**
	 * If connection is made by route, find color after background and set that color so 
	 * it can be passed to application. Once set wake up application to receive color.
	 * 
	 * @param input Input that URL route sent.
	 * @throws Exception
	 */
	private void startRoute(String input) throws Exception{
	   //send acknowledgement that URL route was received. 
   	   out.write("HTTP-1.0 200 OK \n");
   	   out.flush();
   	     
   	   //Check that route was background/color
   	   if(input.length() > END_BG && input.substring(START_BG, END_BG).equals("background/")){
   	      //Get the color of the route sent 
   	      input = input.substring(END_BG);
   	      //Find end of color
   	      int i = input.indexOf(" ");
   	      System.out.println("Color: " + input.substring(START, i));
   	      color = input.substring(START, i);
   	      
   	      //Shutdown server from route URL
   	      if(color.equals("quit")) colorState.setServerStatus(false);;
   	          
   	      //Change the state of color to route that was passed and wake up application
   	      //connection.
   	      synchronized(colorState){
   	        System.out.println("Site before COLOR: " + colorState.getColor());
   	        colorState.setColor(color);
   	        System.out.println("Site after COLOR: " + colorState.getColor());
   	        colorState.notify();
   	      }
   	   }
	}
	
	/**
	 * If connection is made by application wait for Route URL to store color to
	 * send back to application.
	 * 
	 * @exception Exception
	 */
	private void startApp() throws Exception{
		synchronized(colorState){
 		   System.out.println("Android waiting COLOR: " + colorState.getColor());
 		   colorState.wait();
 		   System.out.println("Android running COLOR: " + colorState.getColor());
        }
	}
	
	/**
	 * Close all connections made by socket.
	 * 
	 * @throws Exception
	 */
	private void closeConnection() throws Exception{
	    in.close();
	    out.close();
	    socket.close();
        System.out.println("Connection Done!");
	}
}
