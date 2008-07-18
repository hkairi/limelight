package limelight.ui.model2.updates;

import limelight.ui.Panel;
import limelight.ui.model2.PaintJob;
import limelight.ui.model2.RootPanel;
import limelight.ui.model2.Update;

public class PaintUpdate extends Update
{
  protected PaintUpdate(int severity)
  {
    super(severity);
  }

  public void performUpdate(Panel panel)
  {
    paintPanel(panel);
  }

  protected void paintPanel(Panel panel)
  {
    PaintJob job = new PaintJob(panel.getAbsoluteBounds());
    //TODO Why are we painting the root panel here?  So wastful! Maybe. Transparency?
    RootPanel rootPanel = (RootPanel) panel.getRoot();
    job.paint(rootPanel.getPanel()); //TODO - cast should not be neccessary here.
    job.applyTo(rootPanel.getGraphics());
  }
}