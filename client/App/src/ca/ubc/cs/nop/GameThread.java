package ca.ubc.cs.nop;

import android.graphics.Canvas;
import android.util.Log;



public class GameThread extends Thread{
final static public String tag = "MainActivity";


private MainGamePanel view; 
private boolean running = false;

static final long FPS = 30; // To help limit the FPS when we draw, otherwise we would kill the CPU and increase the Battery Consumption.

public GameThread(MainGamePanel view){
    Log.d(tag, "inside GameThread");
    this.view = view;
}

public void setRunning(boolean run){
    Log.d(tag, "inside GameThread - setRunning");
    running = run; // For starting / stoping our game thread
}


@Override
public void run() {
    long ticksPS = 1000 / FPS; // Limit the frames per second
    long startTime; 
    long sleepTime;
    Log.d(tag, "inside GameThread - run");

    while(running){ // Our Main Game Loop is right here
        Canvas c = null; // build our canvas to draw on
        Log.d(tag, "inside GameThread - run - while loop");
        startTime = System.currentTimeMillis(); //get the current time in milliseconds - this is for helping us limit the FPS
        try{
            c = view.getHolder().lockCanvas(); //Before we can draw, we always have to lock the canvas, otherwise goblins will invade your app and destroy everything!
            synchronized (view.getHolder()){ // we have to synchronize this because we need to make sure that the method runs when at the proper time.
                view.onDraw(c); // this is where we pass our drawing information.  The canvas gets passed to the onDraw method in our MainGamePanel class.
            }
        }finally{
            if(c != null) {
                view.getHolder().unlockCanvasAndPost(c); // Once we are done drawing, we unlock our canvas and post.  which means we drew on the canvas, and now the devices screen will display our drawing.
            }
        }
        sleepTime = ticksPS-(System.currentTimeMillis() - startTime); // this is where we calculace how long we need this thread to sleep (again with the FPS) we want it limited to 30 FPS as defined in our FPS variable.
        try {
            if (sleepTime > 0){
                   sleep(sleepTime); // night night, sleep to limit the fps and save our batteries!
            }
            else{
                   sleep(10); // Incase something goes crazy, we still want to sleep the thread to save the battery.
            }
        }catch(Exception e){

        }

    }

}
}
