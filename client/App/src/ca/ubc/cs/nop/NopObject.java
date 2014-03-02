package ca.ubc.cs.nop;

import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class NopObject {
  int x_;
  int y_;
  int width_;
  int height_;
  int dropOffset_;
  boolean alive_;
  Bitmap img_;
  Rect rect_;
  public NopObject(
      int x, int y, int width, int height, int dropOffset, boolean alive,
      Bitmap img) {
    x_ = x;
    y_ = y;
    width_ = width;
    height_ = height;
    dropOffset_ = dropOffset;
    alive_ = alive;
    img_ = img;

    rect_ = new Rect();
    rect_.left = x - width / 2;
    rect_.right = x + width / 2;
    rect_.top = y - height / 2;
    rect_.bottom = y + height / 2;
  }

  public void setAlive(boolean alive) {
    alive_ = alive;
  }

  public void draw(Canvas canvas, Paint paint) {
    // rect_.left = x - canvas.getWidth() / 2;
    // rect_.right = x - width / 2;
    // rect_.top = y - height / 2;
    // rect_.bottom = y + height / 2;

    canvas.drawBitmap(img_, null, rect_, paint);
  }
}