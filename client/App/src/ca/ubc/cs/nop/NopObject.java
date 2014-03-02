package ca.ubc.cs.nop;

public class NopObject {
  float x_;
  float y_;
  float width_;
  float height_;
  float dropOffset_;
  boolean alive_;
  public NopObject(
      float x, float y, float width, float height, float dropOffset, boolean alive) {
    x_ = x;
    y_ = y;
    width_ = width;
    height_ = height;
    dropOffset_ = dropOffset;
    alive_ = alive;
  }

  public void setAlive(boolean alive) {
    alive_ = alive;
  }

  public void draw() {
  }
}