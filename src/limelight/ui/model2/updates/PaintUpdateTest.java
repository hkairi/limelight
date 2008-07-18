package limelight.ui.model2.updates;

import junit.framework.TestCase;
import limelight.ui.model2.RootPanel;
import limelight.ui.model2.MockFrame;
import limelight.ui.model2.Update;
import limelight.ui.MockPanel;
import limelight.ui.Panel;
import limelight.Context;
import limelight.caching.SimpleCache;

import java.awt.image.BufferedImage;

public class PaintUpdateTest extends TestCase
{
  private Update update;
  private RootPanel root;
  private MockPanel panel;

  public void setUp() throws Exception
  {
    Context.instance().bufferedImageCache = new SimpleCache<Panel, BufferedImage>();
    update = new LayoutAndPaintUpdate(5);
    root = new RootPanel(new MockFrame());
    panel = new MockPanel();
    root.setPanel(panel);
  }
  
  public void testPerformUpdate() throws Exception
  {
    update.performUpdate(panel);

    assertEquals(true, panel.wasPainted);
  }
}