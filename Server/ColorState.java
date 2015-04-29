/**
 * Handles the color that will be sent back to the android
 * application.
 * 
 * @author Johnathon Malott
 * @version 4/27/2015
 */
public class ColorState {
	/**Color sending to application*/
    private String color;
    /**False to shutdown server*/
    private boolean serverStatus = true;
    
    /**
     * Stores color from route.
     * 
     * @param color Color from route.
     */
    public ColorState(String color){
    	this.color = color;
    }
    
    /**
     * Get color to pass back to application.
     * 
     * @return Color to pass back to application,
     */
    public String getColor(){
    	return color;
    }
    
    /**
     * Set color from route to one to be passed back
     * to application.
     * 
     * @param color Color from route.
     */
    public void setColor(String color){
    	this.color = color;
    }
    
    /**
     * Get connection status of application client.
     * 
     * @return Connection status of application client;
     */
    public boolean getServerStatus(){
    	return serverStatus;
    }
    
    /**
     * Set application client connection status.
     * 
     * @param status If client is connected.
     */
    public void setServerStatus(boolean status){
    	this.serverStatus = status;
    }
}


