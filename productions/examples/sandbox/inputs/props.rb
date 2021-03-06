#- Copyright � 2008-2009 8th Light, Inc. All Rights Reserved.
#- Limelight and all included source files are distributed under terms of the GNU LGPL.

__ :name => "sandbox"
__install "header.rb"
arena do
  table do
    cell { text_box_input :players => "text_box" }
  	cell(:border_width => "1") { input_log :id => "text_box_log" }
  	cell(:border_width => "1") { input_results :id => "text_box_results" }
    # end row
    cell { text_area_input :players => "text_area" }
  	cell { input_log :id => "text_area_log" }
  	cell(:border_width => "1") { input_results :id => "text_area_results" }
    # end row
    cell { check_box_input :players => "check_box" }
  	cell { input_log :id => "check_box_log" }
  	cell(:border_width => "1") { input_results :id => "check_box_results" }
#    # end row
    cell do
      radio_button_input :players => "radio_button", :id => "radio_1", :group => "radios"
      radio_label :text => "One"
      radio_button_input :players => "radio_button", :id => "radio_2", :group => "radios"
      radio_label :text => "Two"
      radio_button_input :players => "radio_button", :id => "radio_3", :group => "radios"
      radio_label :text => "Three"
    end
  	cell { input_log :id => "radio_button_log" }
  	cell(:border_width => "1") { input_results :id => "radio_button_results" }
    # end row
    cell { combo_box_input :players => "combo_box", :choices => "%w{Red Orange Yellow Green Blue Indigo Violet}" }
  	cell { input_log :id => "combo_box_log" }
  	cell(:border_width => "1") { input_results :id => "combo_box_results" }
    # end row
    cell { button_input :players => "button", :text => "A Button" }
  	cell { input_log :id => "button_log" }
  	cell(:border_width => "1") { input_results :id => "button_results" }
    #  end row
  end
end