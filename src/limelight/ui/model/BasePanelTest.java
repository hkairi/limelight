//- Copyright 2008 8th Light, Inc. All Rights Reserved.
//- Limelight and all included source files are distributed under terms of the GNU LGPL.

package limelight.ui.model;

import junit.framework.TestCase;
import limelight.ui.MockPanel;
import limelight.ui.Panel;
import limelight.ui.model.updates.LayoutAndPaintUpdate;
import limelight.ui.api.MockProp;
import limelight.LimelightError;
import limelight.styles.Style;
import limelight.util.Box;

import javax.swing.*;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;

public class BasePanelTest extends TestCase
{
  private TestableBasePanel panel;
  private MockPanel parent;
  private MockPanel child;
  private MockPanel grandChild;
  private MockPanel sibling;
  private MockProp prop;
  private PropPanel propPanel;
  private MouseEvent mouseEvent;
  private RootPanel root;

  class TestableBasePanel extends BasePanel
  {
    public Box getChildConsumableArea()
    {
      return null;
    }

    public Box getBoxInsidePadding()
    {
      return null;
    }

    public Style getStyle()
    {
      return null;
    }
  }

  public void setUp() throws Exception
  {
    RootPanel root = new RootPanel(new MockFrame());
    panel = new TestableBasePanel();
    root.setPanel(panel);
  }

  public void testPanelHasDefaultSize() throws Exception
  {
    assertEquals(50, panel.getHeight());
    assertEquals(50, panel.getWidth());
  }

  public void testLocationDefaults() throws Exception
  {
    assertEquals(0, panel.getX());
    assertEquals(0, panel.getY());
  }

  public void testCanSetSize() throws Exception
  {
    panel.setSize(100, 200);
    assertEquals(100, panel.getWidth());
    assertEquals(200, panel.getHeight());

    panel.setSize(300, 200);
    assertEquals(300, panel.getWidth());

    panel.setSize(300, 400);
    assertEquals(400, panel.getHeight());
  }

  public void testBothDimensionsAreZeroWhenOneIsZero() throws Exception
  {
    panel.setSize(100, 100);
    assertEquals(100, panel.getWidth());
    assertEquals(100, panel.getHeight());

    panel.setSize(0, 100);
    assertEquals(0, panel.getWidth());
    assertEquals(0, panel.getHeight());

    panel.setSize(100, 0);
    assertEquals(0, panel.getWidth());
    assertEquals(0, panel.getHeight());
  }

  public void testCanSetLocation() throws Exception
  {
    panel.setLocation(123, 456);
    assertEquals(123, panel.getX());
    assertEquals(456, panel.getY());
  }

  public void testContainsRealativePoint() throws Exception
  {
    panel.setLocation(100, 200);
    panel.setSize(300, 400);

    assertFalse(panel.containsRelativePoint(new Point(0, 0)));
    assertFalse(panel.containsRelativePoint(new Point(1000, 1000)));
    assertFalse(panel.containsRelativePoint(new Point(99, 400)));
    assertFalse(panel.containsRelativePoint(new Point(400, 400)));
    assertFalse(panel.containsRelativePoint(new Point(200, 199)));
    assertFalse(panel.containsRelativePoint(new Point(200, 600)));

    assertTrue(panel.containsRelativePoint(new Point(200, 400)));
    assertTrue(panel.containsRelativePoint(new Point(100, 400)));
    assertTrue(panel.containsRelativePoint(new Point(399, 400)));
    assertTrue(panel.containsRelativePoint(new Point(200, 200)));
    assertTrue(panel.containsRelativePoint(new Point(200, 599)));
  }

  public void testIsAncestor() throws Exception
  {
    createFamilyTree();

    assertTrue(child.isAncestor(parent));
    assertTrue(sibling.isAncestor(parent));
    assertTrue(grandChild.isAncestor(parent));
    assertTrue(grandChild.isAncestor(child));

    assertFalse(child.isAncestor(sibling));
    assertFalse(child.isAncestor(grandChild));
  }

  private void createFamilyTree()
  {
    root = new RootPanel(new MockFrame());
    parent = new MockPanel();
    root.setPanel(parent);
    child = new MockPanel();
    parent.add(child);
    grandChild = new MockPanel();
    child.add(grandChild);
    sibling = new MockPanel();
    parent.add(sibling);
  }

  public void testGetCommonAncestor() throws Exception
  {
    createFamilyTree();

    assertSame(parent, sibling.getClosestCommonAncestor(child));
    assertSame(parent, child.getClosestCommonAncestor(sibling));
    assertSame(parent, child.getClosestCommonAncestor(grandChild));
    assertSame(parent, grandChild.getClosestCommonAncestor(child));
    assertSame(parent, sibling.getClosestCommonAncestor(grandChild));
    assertSame(parent, grandChild.getClosestCommonAncestor(sibling));
    assertSame(child, grandChild.getClosestCommonAncestor(grandChild));
  }

  public void testGetClosestCommonAncestorExceptionCase() throws Exception
  {
    createFamilyTree();

    try
    {
      parent.getClosestCommonAncestor(new MockPanel());
      fail("An exception is expected");
    }
    catch(LimelightError e)
    {
    }
  }

  public void testGetAbsoluteLocation() throws Exception
  {
    createFamilyTree();

    parent.setLocation(1, 10);
    child.setLocation(2, 20);
    grandChild.setLocation(5, 50);

    assertEquals(new Point(1, 10), parent.getAbsoluteLocation());
    assertEquals(new Point(3, 30), child.getAbsoluteLocation());
    assertEquals(new Point(8, 80), grandChild.getAbsoluteLocation());
  }

  public void testContainsAbsolutePoint() throws Exception
  {
    createFamilyTree();

    parent.setLocation(1, 10);
    child.setLocation(2, 20);
    grandChild.setLocation(5, 50);
    grandChild.setSize(10, 10);

    assertFalse(grandChild.containsAbsolutePoint(new Point(0, 0)));
    assertFalse(grandChild.containsAbsolutePoint(new Point(100, 100)));
    assertFalse(grandChild.containsAbsolutePoint(new Point(7, 85)));
    assertFalse(grandChild.containsAbsolutePoint(new Point(18, 85)));
    assertFalse(grandChild.containsAbsolutePoint(new Point(15, 79)));
    assertFalse(grandChild.containsAbsolutePoint(new Point(15, 90)));

    assertTrue(grandChild.containsAbsolutePoint(new Point(8, 80)));
    assertTrue(grandChild.containsAbsolutePoint(new Point(17, 89)));
    assertTrue(grandChild.containsAbsolutePoint(new Point(15, 85)));
  }
  
  public void testGetRoot() throws Exception
  {
    createFamilyTree();

    assertSame(root, parent.getRoot());
    assertSame(root, sibling.getRoot());
    assertSame(root, child.getRoot());
    assertSame(root, grandChild.getRoot());
  }

  //PARENT PANEL STUFF

  public void testCanAddPanels() throws Exception
  {
    Panel panel1 = new MockPanel();
    Panel panel2 = new MockPanel();

    panel.add(panel1);
    panel.add(panel2);

    assertEquals(panel1, panel.getChildren().get(0));
    assertEquals(panel2, panel.getChildren().get(1));
  }

  public void testGetOwnerOfPoint() throws Exception
  {
    Panel panel1 = new MockPanel();
    Panel panel2 = new MockPanel();

    panel1.setLocation(0, 0);
    panel1.setSize(100, 100);
    panel2.setLocation(100, 100);
    panel2.setSize(100, 100);

    panel.add(panel1);
    panel.add(panel2);

    assertSame(panel1, panel.getOwnerOfPoint(new Point(0, 0)));
    assertSame(panel2, panel.getOwnerOfPoint(new Point(100, 100)));
    assertSame(panel1, panel.getOwnerOfPoint(new Point(50, 50)));
    assertSame(panel2, panel.getOwnerOfPoint(new Point(150, 150)));
    assertSame(panel, panel.getOwnerOfPoint(new Point(150, 50)));
    assertSame(panel, panel.getOwnerOfPoint(new Point(50, 150)));
  }

  public void testGetOwnerOfPointWithNestedPanels() throws Exception
  {
    MockPanel panel1 = new MockPanel();
    Panel panel2 = new MockPanel();

    panel1.setLocation(50, 50);
    panel1.setSize(100, 100);
    panel2.setLocation(0, 0);
    panel2.setSize(10, 10);

    panel.add(panel1);
    panel1.add(panel2);

    assertSame(panel2, panel.getOwnerOfPoint(new Point(55, 55)));
  }

  public void testGetOwnerOfPointWithAFloater() throws Exception
  {
    MockPanel child1 = new MockPanel();
    child1.setLocation(0, 0);
    child1.setSize(100, 100);
    MockPanel floater = new MockPanel();
    floater.floater = true;
    floater.setLocation(25, 25);
    floater.setSize(50, 50);

    panel.add(child1);
    panel.add(floater);

    assertSame(child1, panel.getOwnerOfPoint(new Point(0, 0)));
    assertSame(floater, panel.getOwnerOfPoint(new Point(50, 50)));
  }

  public void testGetOwnerOfPointWithOverlappingFloaters() throws Exception
  {
    MockPanel child1 = new MockPanel();
    child1.setLocation(0, 0);
    child1.setSize(100, 100);
    MockPanel floater1 = new MockPanel();
    floater1.floater = true;
    floater1.setLocation(10, 10);
    floater1.setSize(50, 50);
    MockPanel floater2 = new MockPanel();
    floater2.floater = true;
    floater2.setLocation(40, 40);
    floater2.setSize(50, 50);

    panel.add(child1);
    panel.add(floater1);
    panel.add(floater2);

    assertSame(child1, panel.getOwnerOfPoint(new Point(0, 0)));
    assertSame(floater2, panel.getOwnerOfPoint(new Point(50, 50)));
    assertSame(floater1, panel.getOwnerOfPoint(new Point(20, 20)));
    assertSame(floater2, panel.getOwnerOfPoint(new Point(80, 80)));
  }

  public void testSterilization() throws Exception
  {
    panel.sterilize();

    try
    {
      panel.add(new MockPanel());
      fail("Should have thrown an exception");
    }
    catch(SterilePanelException e)
    {
      assertEquals("The panel for prop named 'Propless Panel' has been sterilized. Child components may not be added.", e.getMessage());
    }

    assertEquals(0, panel.getChildren().size());
    assertTrue(panel.isSterilized());
  }

  public void testRemovePanel() throws Exception
  {
    MockPanel panel1 = new MockPanel();
    MockPanel panel2 = new MockPanel();
    panel.add(panel1);
    panel.add(panel2);

    panel.remove(panel1);

    assertEquals(1, panel.getChildren().size());
    assertSame(panel2, panel.getChildren().get(0));
  }
  
  public void testRemoveAll() throws Exception
  {
    MockPanel panel1 = new MockPanel();
    MockPanel panel2 = new MockPanel();
    panel.add(panel1);
    panel.add(panel2);

    panel.removeAll();

    assertEquals(false, panel.hasChildren());
    assertEquals(0, panel.getChildren().size());
  }

  public void testRactanglesAreCached() throws Exception
  {
    Box rectangle = panel.getBoundingBox();

    assertSame(rectangle, panel.getBoundingBox());

    panel.setSize(123, 456);

    assertNotSame(rectangle, panel.getBoundingBox());
  }
  
  public void testAbsoluteLocationGetsChanged() throws Exception
  {
    Point location = panel.getAbsoluteLocation();

    panel.setLocation(123, 456);

    assertNotSame(location, panel.getAbsoluteLocation());
    assertEquals(123, panel.getAbsoluteLocation().x);
    assertEquals(456, panel.getAbsoluteLocation().y);
  }
  
  public void testAbsoluteBoundsChangesWhenLocationChanges() throws Exception
  {
    Box bounds = panel.getAbsoluteBounds();

    panel.setLocation(123, 456);

    assertNotSame(bounds, panel.getAbsoluteBounds());
    assertEquals(123, panel.getAbsoluteBounds().x);
    assertEquals(456, panel.getAbsoluteBounds().y);
  }
  
  public void testAbsoluteBoundsChangesWhenSizeChanges() throws Exception
  {
    Box bounds = panel.getAbsoluteBounds();

    assertSame(bounds, panel.getAbsoluteBounds());

    panel.setSize(123, 456);

    assertNotSame(bounds, panel.getBoundingBox());
    assertEquals(123, panel.getAbsoluteBounds().width);
    assertEquals(456, panel.getAbsoluteBounds().height);
  }

  void addPropPanel()
  {
    prop = new MockProp();
    propPanel = new PropPanel(prop);
    propPanel.add(panel);
    mouseEvent = new MouseEvent(new JPanel(), 1, 2, 3, 4, 5, 6, false);
  }
  
  public void testMousePressed() throws Exception
  {
    addPropPanel();
    panel.mousePressed(mouseEvent);

    assertNotNull(prop.pressedMouse);
  }
         
  public void testMouseReleased() throws Exception
  {
    addPropPanel();
    panel.mouseReleased(mouseEvent);

    assertNotNull(prop.releasedMouse);
  }
         
  public void testMouseClicked() throws Exception
  {
    addPropPanel();
    panel.mouseClicked(mouseEvent);

    assertNotNull(prop.clickedMouse);
  }
         
  public void testMouseDragged() throws Exception
  {
    addPropPanel();
    panel.mouseDragged(mouseEvent);

    assertNotNull(prop.draggedMouse);
  }
         
  public void testMouseMoved() throws Exception
  {
    addPropPanel();
    panel.mouseMoved(mouseEvent);

    assertNotNull(prop.movedMouse);
  }

  public void testCanBeBuffered() throws Exception
  {
    assertEquals(true, panel.canBeBuffered());
  }

  public void testClearingCacheIsRecursive() throws Exception
  {
    panel.setLocation(20, 21);
    Box parentBounds = panel.getAbsoluteBounds();
    MockPanel child = new MockPanel();
    panel.add(child);
    child.setLocation(10, 11);
    Box childBounds = child.getAbsoluteBounds();

    panel.setLocation(30, 31);

    assertNotSame(parentBounds, panel.getAbsoluteBounds());
    assertNotSame(childBounds, child.getAbsoluteBounds());
  }

  public void testIterator() throws Exception
  {
    Iterator<Panel> iterator = panel.iterator();

    assertEquals(PanelIterator.class, iterator.getClass());
  }
  public void testNeededUpdate() throws Exception
  {
    panel.setParent(new RootPanel(new MockFrame()));
    Update update = newUpdate();

    panel.setNeededUpdate(update);
    assertSame(update, panel.getNeededUpdate());

  }

  private Update newUpdate()
  {
    return new Update(1) {
      public void performUpdate(Panel panel)
      {
      }
    };
  }

  public void testGetAndClearNeededUpdate() throws Exception
  {
    panel.setParent(new RootPanel(new MockFrame()));
    Update update = newUpdate();

    panel.setNeededUpdate(update);
    Update gottenUpdate = panel.getAndClearNeededUpdate();

    assertNull(panel.getNeededUpdate());
    assertSame(update, gottenUpdate);
  }

  public void testAddingPanelsRequiresUpdate() throws Exception
  {
    Panel child = new MockPanel();

    panel.add(child);

    assertEquals(true, panel.needsUpdating());
    assertEquals(LayoutAndPaintUpdate.class, panel.getNeededUpdate().getClass());
  }

  public void testRemoveRequiresUpdate() throws Exception
  {
    Panel child = new MockPanel();
    panel.add(child);
    panel.resetNeededUpdate();

    panel.remove(child);

    assertEquals(true, panel.needsUpdating());
    assertEquals(LayoutAndPaintUpdate.class, panel.getNeededUpdate().getClass());
  }

  public void testRemoveoesntRequireUpdateIfNoChildWasRemoved() throws Exception
  {
    Panel child = new MockPanel();
    panel.add(child);
    panel.resetNeededUpdate();

    panel.remove(new MockPanel());

    assertEquals(false, panel.needsUpdating());
  }

  public void testRemoveAllRequiresUpdate() throws Exception
  {
    Panel child = new MockPanel();
    panel.add(child);
    panel.resetNeededUpdate();

    panel.removeAll();

    assertEquals(true, panel.needsUpdating());
    assertEquals(LayoutAndPaintUpdate.class, panel.getNeededUpdate().getClass());
  }

  public void testRemoveAllDoesntRequireUpdateIfNoChildWasRemoved() throws Exception
  {
    panel.removeAll();

    assertEquals(false, panel.needsUpdating());
  }

  public void testAddingChildrenAtIndex() throws Exception
  {
    Panel childA = new MockPanel();
    Panel childB = new MockPanel();
    Panel childC = new MockPanel();

    panel.add(0, childA);
    panel.add(0, childB);
    panel.add(1, childC);

    assertSame(childA, panel.getChildren().get(2));
    assertSame(childB, panel.getChildren().get(0));
    assertSame(childC, panel.getChildren().get(1));
    assertSame(panel, childA.getParent());
    assertSame(panel, childB.getParent());
    assertSame(panel, childC.getParent());
  }
  
  public void testAddingChildAtIndexWhenSteralizedThrowsException() throws Exception
  {
    panel.sterilize();
    try
    {
      panel.add(0, panel);
      fail("should have thrown exception");
    }
    catch(Error e)
    {
      //should get exception
    }
  }

  public void testAddingPanelsAtIndexRequiresUpdate() throws Exception
  {
    Panel childA = new MockPanel();
    Panel childB = new MockPanel();

    panel.add(childA);
    panel.add(0, childB);

    assertEquals(true, panel.needsUpdating());
    assertEquals(LayoutAndPaintUpdate.class, panel.getNeededUpdate().getClass());
  }

  public void testGetChildrenReturnsACopiedList() throws Exception
  {
    Panel child = new MockPanel();
    panel.add(child);

    List<Panel> children = panel.getChildren();
    panel.remove(child);

    List<Panel> children2 = panel.getChildren();

    assertNotSame(children, children2);
    assertEquals(1, children.size());
    assertEquals(0, children2.size());
  }

  public void testGetChildrenProvidesReadonlyList() throws Exception
  {
    Panel child = new MockPanel();
    panel.add(child);

    List<Panel> children = panel.getChildren();

    try
    {
      children.add(new MockPanel());
      fail("Should have thrown exception");
    }
    catch(UnsupportedOperationException e)
    {
    }
  }
}

