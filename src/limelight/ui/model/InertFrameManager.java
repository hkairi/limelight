package limelight.ui.model;

import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;

public class InertFrameManager implements FrameManager
{
  public void watch(Frame frame)
  {
  }

  public Frame getActiveFrame()
  {
    return null;
  }

  public boolean isWatching(Frame frame)
  {
    return false;
  }

  public int getFrameCount()
  {
    return 0;
  }
}