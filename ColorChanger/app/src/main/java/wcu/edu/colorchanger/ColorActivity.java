package wcu.edu.colorchanger;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Starts Application by setting up a connection to a server on localhost port 3000.
 * Application waits for a color to be sent over connection and changes background to
 * that color.
 *
 * (Emulator) Needs to be uncommented for emulator to work.
 *
 * Referenced for http connection and parsing
 * http://www.informit.com/articles/article.aspx?p=26316&seqNum=5
 *
 * @version 2015-04-27
 * @author Johnathon Malott
 */
public class ColorActivity extends Activity {
    /**Local Address that application is calling from.*/
    //(Emulator)
    //final String host = "10.0.2.2";                //Change server address here
    /**Port that application is connecting to.*/
    final int port = 3000;
    /**Default color for background.*/
    static String color = "white";

    /**
     * Set background screen for application and spawn thread to make
     * a connection to server and wait for background color to display.
     *
     * @param savedInstanceState Most recently supplied data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layout = R.layout.activity_1_color;
        setContentView(layout);
        new SetUpConnection().execute();
    }

    /**
     * Find what color was passed back and set background to that color.
     *
     * @param colorSent Color that was sent back from server.
     */
    public void setImage(String colorSent){
        int color;

        switch(colorSent){
            case "yellow":
                color = Color.YELLOW;
                break;
            case "red":
                color = Color.RED;
                break;
            case "blue":
                color = Color.BLUE;
                break;
            case "green":
                color = Color.GREEN;
                break;
            default:
                color = Color.WHITE;
                Toast.makeText(this, colorSent + " not a valid route.", Toast.LENGTH_LONG).show();
        }
        (findViewById(R.id.background)).setBackgroundColor(color);
    }

    /**
     * Background thread that start connection and wait for color to be passed back.
     */
    class SetUpConnection extends AsyncTask<String, String, String> {

        /**
         * Connects to server and get color for background.
         * */
        @Override
        protected String doInBackground(String... args) {
            //Reads data from web page
            String server;
            try{
                //(Emulator)Set up socket to connect to server.
                //InetAddress address = InetAddress.getByName(host);      //Uncomment for manual server
                //Socket socket = new Socket(address, port);              //address to work
                Socket socket = new Socket("localhost", port);

                //Set up input and output streams.
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter (new OutputStreamWriter(socket.getOutputStream()));

                //Let server know that application is trying to connect,
                out.write("Android\n");
                out.flush();

                //Store color that was passed back.
                while((color = in.readLine()) != null && !color.equals("quit")){
                    publishProgress(color);
                }
                Log.e("Socket ","Closing");
                socket.close();
            }catch(IOException e){Log.e("Error: ", e.getMessage());}
            return null;
        }


        /**
         * Changes the background color of application when color is
         * passed over connection.
         *
         * @param values Color to change background to.
         */
        @Override
        protected void onProgressUpdate(String... values) {
            setImage(values[0]);
        }
    }
}
