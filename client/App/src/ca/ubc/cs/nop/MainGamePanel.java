
package ca.ubc.cs.nop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView {
final static public String tag = "Tracer";

private GameThread gameThread;  // For our thread needed to do logical processing without holding up the UI thread
private SurfaceHolder holder; // For our CallBacks.. (One of the areas I don't understand!)

public MainGamePanel(Context context) {
    super(context);
    Log.d(tag, "Inside MainGamePanel");
    gameThread = new GameThread(this); //Create the GameThread instance for our logical processing
    holder = getHolder();


    holder.addCallback(new SurfaceHolder.Callback() {


// Since we are using the SurfaceView, we need to use, at very least, the surfaceDestroyed and surfaceCreated methods.
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            boolean retry = true;
            Log.d(tag, "Inside SurfaceHolder Callback - surfaceDestroyed");
            gameThread.setRunning(false); // Stop the Thread from running because the surface was destroyed.  Can't play a game with no surface!!  

            while (retry) { 
                try {
                    Log.d(tag, "Inside SurfaceHolder Callback - surfaceDestroyed - while statement");
                    gameThread.join();
                    retry = false; //Loop until game thread is done, making sure the thread is taken care of.
                } catch (InterruptedException e) {
                    //  In case of catastrophic failure catch error!!!
                }
            }

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // let there be Surface!
            Log.d(tag, "Inside SurfaceHolder Callback - surfaceCreated");
            gameThread.setRunning(true); // Now we start the thread
            gameThread.start(); // and begin our game's logical processing

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format,
                int width, int height) {
            // The code to resize the screen ratio when it flips from landscape to portrait and vice versa

        }
    });
}

@Override
protected void onDraw(Canvas canvas) {
//This is where we draw stuff..  since this is just a skeleton demo, we only draw the color Dark Grey so we can visibly see that we actually accomplished something with the surfaceview drawing
    Log.d("MainActivitiy", "Inside onDraw"); 
    canvas.drawColor(Color.RED); // You can change the Color to whatever color you want, for this demo I just used Color.DKGRAY 

    }

}