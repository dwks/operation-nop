
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
  Paint paintBranchImg;
  Bitmap branchImg;
  Bitmap[] birdImg;
  NopObject branch_;
  NopObject[] birds_;
                                                  
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

    paintBranchImg = new Paint();
    branchImg = BitmapFactory.decodeResource(
        getResources(), R.drawable.branch_full);
    birdImg = new Bitmap[4];
    birdImg[0] = BitmapFactory.decodeResource(
        getResources(), R.drawable.bird_happy1_full);    
    birdImg[1] = BitmapFactory.decodeResource(
        getResources(), R.drawable.bird_happy2_full);    
    birdImg[2] = BitmapFactory.decodeResource(
        getResources(), R.drawable.bird_sad1_full);    
    birdImg[3] = BitmapFactory.decodeResource(
        getResources(), R.drawable.bird_sad2_full);

    branch_ = new NopObject(0, 1, true, 0);
    birds_ = new NopObject[5];
    birds_[0] = new NopObject(0, 50, true, 0);
    birds_[1] = new NopObject(1, 50, true, 34);
    birds_[2] = new NopObject(2, 50, true, 23);
    birds_[3] = new NopObject(3, 50, true, 10);
    birds_[4] = new NopObject(4, 50, false, 20);
}

@Override
protected void onDraw(Canvas canvas) {
//This is where we draw stuff..  since this is just a skeleton demo, we only draw the color Dark Grey so we can visibly see that we actually accomplished something with the surfaceview drawing
    if (canvas == null) {
      Log.d("MainActivitiy", "canvas is null"); 
        //gameThread.setRunning(false); // Stop the Thread from running because the surface was destroyed.  Can't play a game with no surface!!  
        return;
    }

    Log.d("MainActivitiy", "Inside onDraw"); 

    // This is where all the bird animation stuff is drawn...

    canvas.drawColor(0xb8c7cf); // background colour

    //sizing
    int birdPadding = 6;
    int branchOffset = 25;

    int branchWidth = canvas.getWidth();
    int branchHeight = branchWidth/4;
    int branchPosX = branchWidth/2;
    int branchPosY = branchOffset + branchHeight/2;
    int leftPosX = branchPosX - branchWidth/2;
    int topPosY = branchPosY - branchHeight/2;
    int birdWidth = (branchWidth-birdPadding*4)/5;
    int birdHeight = birdWidth;

    branch_.setSize(branchPosX, branchPosY, branchWidth, branchHeight);
    branch_.draw(canvas, paintBranchImg, branchImg, branchImg, branchImg, branchImg);

    birds_[0].setSize(leftPosX+birdWidth/2, topPosY, birdWidth, birdHeight);
    birds_[0].draw(canvas, paintBranchImg, birdImg[0], birdImg[1], birdImg[2], birdImg[3]);

    birds_[1].setSize(leftPosX+(birdWidth)+birdWidth/2+birdPadding, topPosY, birdWidth, birdHeight);
    birds_[1].draw(canvas, paintBranchImg, birdImg[0], birdImg[1], birdImg[2], birdImg[3]);

    birds_[2].setSize(leftPosX+(birdWidth)*2+birdWidth/2+birdPadding*2, topPosY, birdWidth, birdHeight);
    birds_[2].draw(canvas, paintBranchImg, birdImg[0], birdImg[1], birdImg[2], birdImg[3]);

    birds_[3].setSize(leftPosX+(birdWidth)*3+birdWidth/2+birdPadding*3, topPosY, birdWidth, birdHeight);
    birds_[3].draw(canvas, paintBranchImg, birdImg[0], birdImg[1], birdImg[2], birdImg[3]);

    birds_[4].setSize(leftPosX+(birdWidth)*4+birdWidth/2+birdPadding*4, topPosY, birdWidth, birdHeight);
    birds_[4].draw(canvas, paintBranchImg, birdImg[0], birdImg[1], birdImg[2], birdImg[3]);
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