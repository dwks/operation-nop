
package ca.ubc.cs.nop;

import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView {
final static public String tag = "MainActivity";

private GameThread gameThread;  // For our thread needed to do logical processing without holding up the UI thread
private SurfaceHolder holder; // For our CallBacks.. (One of the areas I don't understand!)

boolean startCalled;
public MainGamePanel(Context context) {
    super(context);
    Log.d(tag, "Inside MainGamePanel");
    gameThread = new GameThread(this); //Create the GameThread instance for our logical processing
    holder = getHolder();
    startCalled = false;

    holder.addCallback(new SurfaceHolder.Callback() {


// Since we are using the SurfaceView, we need to use, at very least, the surfaceDestroyed and surfaceCreated methods.
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        //     boolean retry = true;
        //     Log.d(tag, "Inside SurfaceHolder Callback - surfaceDestroyed");
/*            if (startCalled) {
                startCalled = false;
                gameThread.setRunning(false); // Stop the Thread from running because the surface was destroyed.  Can't play a game with no surface!!  
            }
*/
        //     while (retry) { 
        //         try {
        //             Log.d(tag, "Inside SurfaceHolder Callback - surfaceDestroyed - while statement");
        //             gameThread.join();
        //             retry = false; //Loop until game thread is done, making sure the thread is taken care of.
        //         } catch (InterruptedException e) {
        //             //  In case of catastrophic failure catch error!!!
        //         }
        //     }

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // let there be Surface!
            Log.d(tag, "Inside SurfaceHolder Callback - surfaceCreated");

            if (!startCalled) {
                startCalled = true;
                gameThread.setRunning(true); // Now we start the thread
                gameThread.start(); // and begin our game's logical processing
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format,
                int width, int height) {
            // The code to resize the screen ratio when it flips from landscape to portrait and vice versa

        }
    });
}

//Define bitmaps for drawing:
//Bitmap branchImg = BitmapFactory.decodeResource(getResources(), R.drawable.branch_full);

@Override
protected void onDraw(Canvas canvas) {
<<<<<<< HEAD
//This is where we draw stuff..  since this is just a skeleton demo, we only draw the color Dark Grey so we can visibly see that we actually accomplished something with the surfaceview drawing
        if (canvas == null) {
            // gameThread.setRunning(false); // Stop the Thread from running because the surface was destroyed.  Can't play a game with no surface!!  
            return;
        }
=======
    if (canvas == null) {
        gameThread.setRunning(false); // Stop the Thread from running because the surface was destroyed.  Can't play a game with no surface!!  
        return;
    }
>>>>>>> 0c07de13819c89215673a99ce3590bcfe103fab5
    Log.d("MainActivitiy", "Inside onDraw"); 

    // This is where all the bird animation stuff is drawn...

    canvas.drawColor(0xb8c7cf); // background colour

//    branchImg.setHeight(50);
//    branchImg.setWidth(50);
//    canvas.drawBitmap(branchImg, 5, 5, null);

}

  public void stopThread() {
    boolean retry = true;
    Log.d(tag, "Stopping thread");
    if (startCalled) {
      startCalled = false;
      gameThread.setRunning(false);  

      while (retry) { 
        try {
          Log.d(tag, "Inside stopThread while statement");
          gameThread.join();
          retry = false; //Loop until game thread is done, making sure the thread is taken care of.
        } catch (InterruptedException e) {
          //  In case of catastrophic failure catch error!!!
        }
      }
    }
  }
}