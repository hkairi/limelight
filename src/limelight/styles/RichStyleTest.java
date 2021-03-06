//- Copyright � 2008-2009 8th Light, Inc. All Rights Reserved.
//- Limelight and all included source files are distributed under terms of the GNU LGPL.

package limelight.styles;

import junit.framework.TestCase;

public class RichStyleTest extends TestCase
{
  private RichStyle style;
  private RichStyle style2;
  private RichStyle style3;
  private RichStyle style4;
  private RichStyle newStyle;
  private RichStyle anotherNewStyle;
  private MockStyleObserver observer;

  public void setUp() throws Exception
  {
    style = new RichStyle();
    style2 = new RichStyle();
    style3 = new RichStyle();
    style4 = new RichStyle();
    newStyle = new RichStyle();
    anotherNewStyle = new RichStyle();

    observer = new MockStyleObserver();
    style.addObserver(observer);
  }

  public void testHasDefaultsFirst() throws Exception
  {
    for(StyleDescriptor descriptor : Style.STYLE_LIST)
      assertEquals(descriptor.defaultValue, style.getCompiled(descriptor));
  }
  
  public void testCanSetNewValues() throws Exception
  {
    style.setBackgroundColor("blue");
    assertEquals("#0000ffff", style.getBackgroundColor());
    assertEquals("#0000ffff", style.get(Style.BACKGROUND_COLOR));
  }

  public void testInsetNext() throws Exception
  {
    style.addExtension(style2);
    assertEquals(true, style.hasExtension(style2));
    assertEquals(true, style2.hasObserver(style));

    style.addExtension(style3);
    assertEquals(true, style.hasExtension(style3));
    assertEquals(true, style3.hasObserver(style));
  }

  public void testRemoveExtension() throws Exception
  {
    style.addExtension(style2);
    style2.addExtension(style3);

    style.removeExtension(style2);

    assertEquals(true, style2.hasExtension(style3));
    assertEquals(false, style.hasExtension(style2));
  }

  public void testClearExtensions() throws Exception
  {
	  style.addExtension(style2);
	  style.addExtension(style3);
	
	  style.clearExtensions();
	
	  assertFalse(style.hasExtension(style2));
	  assertFalse(style.hasExtension(style3));
  }

  public void testClearExtensionsRemovesObserver() throws Exception
  {
    style.addExtension(style2);

    style.clearExtensions();

    assertFalse(style2.hasObserver(style));    
  }

  public void testClearExtensionsWithNoExtensions() throws Exception
  {
    try
    {
      style.clearExtensions();
    }
    catch(Exception e)
    {
      fail("clearExtensions() should not throw an exception - but does");
    }

  }

  public void testWithNothingStacked() throws Exception
  {
    style.setWidth("100");
    assertEquals("100", style.getWidth());
    assertEquals(true, observer.changed());
    assertEquals(true, observer.changed(Style.WIDTH));
  }

  public void testChangesFromBelow() throws Exception
  {
    style.addExtension(style2);
    style.addExtension(style3);

    assertFalse(observer.changed());

    style.setWidth("123");
    assertTrue(observer.changed());

    observer.flushChanges();
    assertFalse(observer.changed());

    style2.setWidth("123");
    assertFalse(observer.changed());

    observer.flushChanges();
    assertFalse(observer.changed());

    style3.setWidth("123");
    assertFalse(observer.changed());

    style3.setHeight("321");
    assertTrue(observer.changed());

    observer.flushChanges();
    assertFalse(observer.changed());
  }

  public void testSpecificChanges() throws Exception
  {
    style.addExtension(style2);
    style.addExtension(style3);

    assertFalse(observer.changed(Style.WIDTH));
    assertFalse(observer.changed(Style.HEIGHT));

    style3.setWidth("123");
    assertTrue(observer.changed(Style.WIDTH));
    assertFalse(observer.changed(Style.HEIGHT));
    observer.flushChanges();
    assertFalse(observer.changed(Style.WIDTH));
    assertFalse(observer.changed(Style.HEIGHT));

    style2.setWidth("123");
    assertTrue(observer.changed(Style.WIDTH));
    assertFalse(observer.changed(Style.HEIGHT));
    observer.flushChanges();
    assertFalse(observer.changed(Style.WIDTH));
    assertFalse(observer.changed(Style.HEIGHT));
  }

  public void testAddingToBottomAffectsChanges() throws Exception
  {
    style.setWidth("100");
    observer.flushChanges();

    newStyle.setWidth("200");

    style.addExtension(newStyle);
    assertFalse(observer.changed());

    anotherNewStyle.setHeight("100");
    style.addExtension(anotherNewStyle);
    assertTrue(observer.changed());
    assertTrue(observer.changed(Style.HEIGHT));
  }

  public void testRemoveFromBottomAffectsChanges() throws Exception
  {
    newStyle.setWidth("100");
    anotherNewStyle.setWidth("200");

    style.addExtension(newStyle);
    style.addExtension(anotherNewStyle);
    observer.flushChanges();

    style.removeExtension(anotherNewStyle);
    assertFalse(observer.changed());

    style.removeExtension(newStyle);
    assertTrue(observer.changed());
    assertTrue(observer.changed(Style.WIDTH));
  }

  public void testCanHaveLotsOfExtenders() throws Exception
  {
    style.setWidth("100");

    style2.addExtension(style);
    style3.addExtension(style);
    style4.addExtension(style);

    assertEquals(true, style2.hasExtension(style));
    assertEquals(true, style3.hasExtension(style));
    assertEquals(true, style4.hasExtension(style));
    assertEquals(true, style.hasObserver(style2));
    assertEquals(true, style.hasObserver(style3));
    assertEquals(true, style.hasObserver(style4));
    assertEquals("100", style2.getWidth());
    assertEquals("100", style3.getWidth());
    assertEquals("100", style4.getWidth());
  }

}
