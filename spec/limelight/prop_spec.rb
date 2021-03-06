#- Copyright � 2008-2009 8th Light, Inc. All Rights Reserved.
#- Limelight and all included source files are distributed under terms of the GNU LGPL.

require File.expand_path(File.dirname(__FILE__) + "/../spec_helper")
require 'limelight/prop'
require 'limelight/dsl/styles_builder'
require 'limelight/scene'
require 'limelight/production'

describe Limelight::Prop do

  before(:each) do
    @casting_director = mock("casting_director", :fill_cast => nil)
    @scene = Limelight::Scene.new(:casting_director => @casting_director)
    @prop = Limelight::Prop.new(:id => "root", :name => "root_class")
    @scene.illuminate
    @scene << @prop
  end

  it "should set the childs parent before adding the child to the parent" do
    # This is important because a child must be illuminated before a parent knows about it
    child = Limelight::Prop.new(:id => "child", :name => "child_class")

    child.should_receive(:set_parent) do
      @prop.children.size.should == 0
    end
    @prop.add(child)

    @prop.children.size.should == 1
  end

  module TestPlayer
    class << self
      attr_reader :extended_prop

      def extended(prop)
        @extended_prop = (prop)
      end
    end

    def test_method
    end

    attr_reader :was_casted
    def casted
      @was_casted = true
    end
  end

  it "should extend added controllers and invoke the extended hook" do
    @prop.include_player(TestPlayer)

    TestPlayer.extended_prop.should == @prop
    @prop.respond_to?(:test_method).should == true
  end

  it "should call the player's casted method" do
    @prop.include_player(TestPlayer)

    @prop.was_casted.should == true
  end

  it "should have an id" do
    @prop.illuminate
    @prop.id.should == "root"
  end
  
  it "not start out being illuminated" do
    prop = Limelight::Prop.new(nil)
    
    prop.should_not be_illuminated
  end
  

  def build_prop_tree
    @child1 = Limelight::Prop.new(:id => "child1", :name => "child_class")
    @child2 = Limelight::Prop.new(:id => "child2", :name => "child_class")
    @grand_child1 = Limelight::Prop.new(:id => "grand_child1", :name => "grand_child_class")
    @grand_child2 = Limelight::Prop.new(:id => "grand_child2", :name => "grand_child_class")
    @grand_child3 = Limelight::Prop.new(:id => "grand_child3", :name => "grand_child_class")
    @grand_child4 = Limelight::Prop.new(:id => "grand_child4", :name => "grand_child_class")

    @prop << @child1 << @child2
    @child1 << @grand_child1 << @grand_child2
    @child2 << @grand_child3 << @grand_child4
  end

  it "should find children by id" do
    build_prop_tree
    @scene.find("blah").should == nil
    @scene.find("root").should be(@prop)
    @scene.find("child1").should be(@child1)
    @scene.find("child2").should be(@child2)
    @scene.find("grand_child1").should be(@grand_child1)
    @scene.find("grand_child2").should be(@grand_child2)
    @scene.find("grand_child3").should be(@grand_child3)
    @scene.find("grand_child4").should be(@grand_child4)
  end

  it "should find children by name" do
    build_prop_tree
    @prop.find_by_name("root_class").should == [@prop]
    @prop.find_by_name("child_class").should == [@child1, @child2]
    @prop.find_by_name("grand_child_class").should == [@grand_child1, @grand_child2, @grand_child3, @grand_child4]
  end

  it "should get and set text" do
    @prop.text = "blah"
    @prop.text.should == "blah"
    @prop.text = 123
    @prop.text.should == "123"
    @prop.text = nil
    @prop.text.should == ""
  end

  it "should have controllers" do
    prop = Limelight::Prop.new(:players => "abc, xyz")
    @scene << prop
    prop.players.should == "abc, xyz"
  end

  it "should get populated through constructor" do
    prop = Limelight::Prop.new(:name => "my_name", :id => "123", :players => "a, b, c")
    @scene << prop

    prop.name.should == "my_name"
    prop.id.should == "123"
  end

  it "should populate styles through constructor" do
    prop = Limelight::Prop.new(:width => "100", :text_color => "white", :background_image => "apple.jpg")
    @scene << prop

    prop.style.width.should == "100"
    prop.style.text_color.should == "#ffffffff"
    prop.style.background_image.should == "apple.jpg"
  end

  it "should define event through constructor using a string" do
    prop = Limelight::Prop.new(:on_mouse_entered => "return event")
    @scene << prop

    value = prop.mouse_entered("my event")

    value.should == "my event"
  end

  it "should pass scene on to children" do
    child = Limelight::Prop.new(:name => "child")

    @prop.parent.should == @scene
    @prop.scene.should == @scene

    @prop << child
    child.parent.should == @prop
    child.scene.should == @scene
  end

  it "should set styles upon adding to parent" do
    styles = Limelight::build_styles { child { width 123 } }
    scene = Limelight::Scene.new(:casting_director => @casting_director, :styles_hash => styles)
    prop = Limelight::Prop.new(:name => "child")
    scene.illuminate

    scene << prop

    prop.style.width.should == "123"
  end

  it "should set styles upon adding to parent" do
    prop = Limelight::Prop.new(:name => "child")

    @casting_director.should_receive(:fill_cast).with(prop)

    @scene << prop
  end

  it "should use populate data included by players" do
    prop = Limelight::Prop.new(:name => "child", :foo => "bar")
    @casting_director.should_receive(:fill_cast).with(prop) do
      prop.instance_eval "def foo=(value); @foo = value; end; def foo; return @foo; end;"
    end

    @scene << prop

    prop.foo.should == "bar"
  end

  it "should set styles upon illuminating, and convert them to strings" do
    prop = Limelight::Prop.new(:width => "100", :height => 200, :horizontal_alignment => :center)

    @scene << prop

    prop.style.width.should == "100"
    prop.style.height.should == "200"
    prop.style.horizontal_alignment.should == "center"
  end

  it "should add additional styles listed in options" do
    style1 = Limelight::Styles::RichStyle.new()
    style2 = Limelight::Styles::RichStyle.new()
    style3 = Limelight::Styles::RichStyle.new()
    @scene.styles.merge!("one" => style1, "two" => style2, "three" => style3)

    prop = Limelight::Prop.new(:name => "one", :styles => "two three")
    @scene << prop

    prop.style.should have_extension(style1)
    prop.style.should have_extension(style2)
    prop.style.should have_extension(style3)
  end

  it "should add additional styles and their hover part" do
    style1 = Limelight::Styles::RichStyle.new()
    style2 = Limelight::Styles::RichStyle.new()
    style3 = Limelight::Styles::RichStyle.new()
    style4 = Limelight::Styles::RichStyle.new()
    @scene.styles.merge!("one" => style1, "one.hover" => style2, "two" => style3, "two.hover" => style4)

    prop = Limelight::Prop.new(:name => "one", :styles => "two three")
    @scene << prop

    prop.style.should have_extension(style1)
    prop.style.should have_extension(style3)
    prop.hover_style.should == style2
    prop.hover_style.should have_extension(style4)
  end

  it "should be able to remove children" do
    child1 = Limelight::Prop.new()
    child2 = Limelight::Prop.new()
    child3 = Limelight::Prop.new()
    @prop << child1 << child2 << child3

    @prop.remove(child2)

    @prop.children.length.should == 2
    @prop.children.include?(child2).should == false
    @prop.panel.children.length.should == 2
    @prop.panel.children.include?(child2.panel).should == false
  end

  it "should make dimensions accessible" do
    @prop.panel.should_receive(:get_bounding_box).and_return("whole area")
    @prop.panel.should_receive(:get_box_inside_borders).and_return("area inside borders")

    @prop.area.should == "whole area"
    @prop.bordered_area.should == "area inside borders"
  end

  it "should give you a pen" do
    graphics = mock("graphics", :setColor => nil, :setStroke => nil, :setRenderingHint => nil)
    @prop.panel.should_receive(:getGraphics).and_return(graphics)

    pen = @prop.pen

    pen.context.should be(graphics)
  end

  it "should set after paint action" do
    block = Proc.new { |pen| }

    @prop.after_painting &block

    action = @prop.panel.after_paint_action
    action.should_not == nil
    action.class.should == Limelight::PaintAction
    action.block.should == block
  end

  it "should clear after paint action" do
    @prop.after_painting { |pen| puts "blah" }

    @prop.after_painting nil

    @prop.panel.after_paint_action.should == nil
  end

  it "should build children" do
    @prop.scene.production = Limelight::Production.new("some/path")
    @prop.build do
      one
      two do
        three
      end
    end

    @prop.children.length.should == 2
    @prop.children[0].name.should == "one"
    @prop.children[1].name.should == "two"
    @prop.children[1].children.length.should == 1
    @prop.children[1].children[0].name.should == "three"
  end

  it "should build children with options" do
    @prop.scene.production = Limelight::Production.new("some/path")
    @prop.build(:one_val => "hello") do
      one :text => @one_val
      two do
        three
      end

    end

    @prop.children.length.should == 2
    @prop.children[0].name.should == "one"
    @prop.children[0].text.should == "hello"
    @prop.children[1].name.should == "two"
    @prop.children[1].children.length.should == 1
    @prop.children[1].children[0].name.should == "three"
  end

  it "should play sound" do
    production = Limelight::Production.new("/blah")
    @scene.production = production
    production.root.should_receive(:path_to).with("some.au").and_return("/full/path/to/some.au");
    @prop.panel.should_receive(:play_sound).with("/full/path/to/some.au");

    @prop.play_sound("some.au")
  end

  it "should remember when it has been illuminated" do
    prop = Limelight::Prop.new
    prop.illuminated?.should == false

    @scene << prop

    prop.illuminated?.should == true;
  end

  #TODO remove_all should not remove scrollbars

  describe "events" do

    it "should not accept mouse_clicked events to start with" do
      @prop.accepts_mouse_clicked().should == false
      @prop.instance_eval("def mouse_clicked(e); puts 'hi'; end;")
      @prop.accepts_mouse_clicked().should == true
    end

    it "should not accept mouse_pressed events to start with" do
      @prop.accepts_mouse_pressed().should == false
      @prop.instance_eval("def mouse_pressed(e); puts 'hi'; end;")
      @prop.accepts_mouse_pressed().should == true
    end

    it "should not accept mouse_released events to start with" do
      @prop.accepts_mouse_released().should == false
      @prop.instance_eval("def mouse_released(e); puts 'hi'; end;")
      @prop.accepts_mouse_released().should == true
    end

  end

  describe "id" do

    it "should index its id when illuminated" do
      @scene.find("root").should == @prop
    end

    it "should unindex ids when removing children" do
      child = Limelight::Prop.new(:id => "child")
      @prop << child
      @scene.find("child").should == child

      @prop.remove(child)

      @scene.find("child").should == nil
    end

    it "should unindex ids when removing all children" do
      child1 = Limelight::Prop.new(:id => "child1")
      child2 = Limelight::Prop.new(:id => "child2")
      @prop << child1 << child2

      @prop.remove_all

      @scene.find("child1").should == nil
      @scene.find("child2").should == nil
    end

    it "should unindex grandchildren on remove all" do
      grandchild = Limelight::Prop.new(:id => "grandchild")
      child = Limelight::Prop.new(:id => "child")
      child << grandchild
      @prop << child

      @prop.remove_all

      @scene.find("child").should == nil
      @scene.find("grandchild").should == nil
    end
    
    it "should index it's id when being re-added to the prop tree" do
      child = Limelight::Prop.new(:id => "child")
      @prop << child
      @prop.remove(child)
      @prop << child

      @scene.find("child").should == child
    end
    
    it "should traverse and index children when being re-added to the prop tree" do
      grandchild = Limelight::Prop.new(:id => "grandchild")
      child = Limelight::Prop.new(:id => "child")
      child << grandchild
      @prop << child
      @prop.remove(child)
      @prop << child
      
      @scene.find("grandchild").should == grandchild
    end
    

  end

  describe "launch" do

    it "should launch a url" do
      os = mock("Limelight OS")
      instance = mock("Limelight OS instance", :os => os)
      Java::limelight.Context.stub!(:instance).and_return(instance)

      os.should_receive(:launch).with("http://www.google.com")

      @prop.launch("http://www.google.com")
    end

  end

end
