package ca.ubc.cs.nop;

import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class NopObject {
  int dropOffset_;
  boolean alive_;
  Rect rect_;
  int tickCounter_;
  int frame_;
  public NopObject(int dropOffset, boolean alive, int seed) {
    dropOffset_ = dropOffset;
    alive_ = alive;

    rect_ = new Rect();
    setSize(0, 0, 0, 0);

    frame_ = 0;
    tickCounter_ = seed;
  }

  public void setAlive(boolean alive) {
    alive_ = alive;
  }

  public void draw(Canvas canvas, Paint paint,
                   Bitmap alive0, Bitmap alive1,
                   Bitmap dead0, Bitmap dead1) {
    if (alive_) {
      if (frame_ == 0) {
        canvas.drawBitmap(alive0, null, rect_, paint);
      } else {
        canvas.drawBitmap(alive1, null, rect_, paint);
      }
    } else {
      if (frame_ == 0) {
        canvas.drawBitmap(dead0, null, rect_, paint);
      } else {
        canvas.drawBitmap(dead1, null, rect_, paint);
      }
    }

    tickCounter_++;
    if (frame_ == 0) {
      if (tickCounter_ > 40) {
        frame_ = 1;
        tickCounter_ = 0;
      }
    } else {
      if (tickCounter_ > 5) {
        frame_ = 0;
        tickCounter_ = 0;
      }
    }
  }

  public void setSize(int x, int y, int width, int height) {
    rect_.left = x- width / 2;
    rect_.right = x + width / 2;
    rect_.top = y - height / 2;
    rect_.bottom = y + height / 2;
  }
}