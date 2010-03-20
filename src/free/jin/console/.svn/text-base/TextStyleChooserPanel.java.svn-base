/**
 * Jin - a chess client for internet chess servers.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2002 Alexander Maryanovsky.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package free.jin.console;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import free.util.swing.ColorChooserButton;
import free.util.swing.FontSelectorPanel;
import bsh.Interpreter;
import bsh.EvalError;


/**
 * A common panel allowing to choose the font, foreground and optionally, 
 * background color of text.
 */

public class TextStyleChooserPanel extends JPanel{


  /**
   * The FontSelectorPanel we're using to select the font.
   */

  private final FontSelectorPanel fontSelector;




  /**
   * The ColorChooserButton used to select the foreground color.
   */

  private final ColorChooserButton foregroundChooser;




  /**
   * The ColorChooserButton used to select the background color.
   */

  private final ColorChooserButton backgroundChooser;





  /**
   * The sole ChangeEvent we need.
   */

  private final ChangeEvent changeEvent = new ChangeEvent(this);




  /**
   * Creates a new TextStyleChooserPanel with the given initial font,
   * foreground and background colors. The <code>allowBackgroundSelection</code>
   * argument specifies whether the user should be allowed to change the
   * background.
   */

  public TextStyleChooserPanel(Font initialFont, Color initForegroundColor, Color initBackgroundColor,
      final boolean initAntialiasingValue, boolean allowBackgroundSelection, boolean allowAntialiasingSelection){
    FontSelectorPanel.BooleanFontOption [] fontOptions = new FontSelectorPanel.BooleanFontOption[]{
      FontSelectorPanel.createBoldFontOption(),
      FontSelectorPanel.createItalicFontOption(),
      new FontSelectorPanel.BooleanFontOption("Antialias", 'A', initAntialiasingValue)
    };
    fontSelector = allowAntialiasingSelection ? new FontSelectorPanel(fontOptions) : new FontSelectorPanel();
    foregroundChooser = new ColorChooserButton("Foreground", initForegroundColor);
    foregroundChooser.setMnemonic('F');
    if (allowBackgroundSelection){
      backgroundChooser = new ColorChooserButton("Background", initBackgroundColor);
      backgroundChooser.setMnemonic('k');
    }
    else
      backgroundChooser = null;

    fontSelector.setPreviewPanel(new TextStylePreviewPanel(fontSelector, initAntialiasingValue));

    fontSelector.setSelectedFont(initialFont);
    fontSelector.getPreviewPanel().setOpaque(true);
    fontSelector.getPreviewPanel().setBackground(initBackgroundColor);
    fontSelector.getPreviewPanel().setForeground(initForegroundColor);

    createUI();

    ChangeListener colorChangeListener = new ChangeListener(){
      public void stateChanged(ChangeEvent evt){
        fontSelector.getPreviewPanel().setForeground(foregroundChooser.getColor());
        if (backgroundChooser != null)
          fontSelector.getPreviewPanel().setBackground(backgroundChooser.getColor());

        fireStateChanged();
      }
    };

    foregroundChooser.addChangeListener(colorChangeListener);
    if (backgroundChooser != null)
      backgroundChooser.addChangeListener(colorChangeListener);

    fontSelector.addChangeListener(new ChangeListener(){
      public void stateChanged(ChangeEvent evt){
        fireStateChanged();
      }
    });
  }




  /**
   * Creates the UI.
   */

  private void createUI(){
    setLayout(new BorderLayout(5,5));

    LayoutManager colorChooserPanelLayout;
    if (backgroundChooser == null)
      colorChooserPanelLayout = new FlowLayout();
    else
      colorChooserPanelLayout = new GridLayout(1, 2, 5, 5);

    JPanel colorChooserPanel = new JPanel(colorChooserPanelLayout);
    colorChooserPanel.add(foregroundChooser);
    
    if (backgroundChooser != null)
      colorChooserPanel.add(backgroundChooser);

    add(fontSelector, BorderLayout.CENTER);
    add(colorChooserPanel, BorderLayout.SOUTH);
  }





  /**
   * Adds a ChangeListener to the list of listeners receiving notifications when
   * one of the text properties changes.
   */

  public void addChangeListener(ChangeListener listener){
    listenerList.add(ChangeListener.class, listener);
  }




  /**
   * Removes the given Changelistener from the list of listeners receiving
   * notifications when one of the text properties changes.
   */

  public void removeChangeListener(ChangeListener listener){
    listenerList.remove(ChangeListener.class, listener);
  }




  /**
   * Fires a ChangeEvent to all interested listeners.
   */

  protected void fireStateChanged(){
    Object [] listeners = listenerList.getListenerList();
    for (int i = 0; i < listeners.length; i += 2){
      if (listeners[i] == ChangeListener.class){
        ChangeListener listener = (ChangeListener)listeners[i+1];
        listener.stateChanged(changeEvent);
      }
    }
  }




  /**
   * Sets the currently selected foreground color.
   */

  public void setSelectedForeground(Color color){
    foregroundChooser.setColor(color);
  }




  /**
   * Returns the currently selected foreground color.
   */

  public Color getSelectedForeground(){
    return foregroundChooser.getColor();
  }




  /**
   * Sets the currently selected font.
   */

  public void setSelectedFont(Font font){
    fontSelector.setSelectedFont(font);
  }




  /**
   * Returns the currently selected font.
   */

  public Font getSelectedFont(){
    return fontSelector.getSelectedFont();
  }




  /**
   * Returns the background color.
   */

  public void setSelectedBackground(Color color){
    fontSelector.getPreviewPanel().setBackground(color);
    if (backgroundChooser != null)
      backgroundChooser.setColor(color);
    else{
      // In this case the event isn't fired automatically because the 
      // backgroundChooser isn't there to fire one to use.
      fireStateChanged();
    }
  }




  /**
   * Returns the selected background color.
   */

  public Color getSelectedBackground(){
    if (backgroundChooser == null)
      return fontSelector.getPreviewPanel().getBackground();
    else
      return backgroundChooser.getColor();
  }




  /**
   * Returns <code>true</code> if antialiasing selection is enabled,
   * <code>false</code> otherwise.
   */

  public boolean isAntialiasingSelectionEnabled(){
    return fontSelector.getFontOption("Antialias") != null;
  }




  /**
   * Returns the current value for text antialiasing.
   *
   * @throws IllegalArgumentException if text antialiasing selection is not
   * enabled.
   */

  public boolean isAntialias(){
    return fontSelector.getFontOptionValue("Antialias");
  }




  /**
   * Sets whether text should be antialiased.
   */

  public void setAntialias(boolean antialias){
    FontSelectorPanel.BooleanFontOption antialiasOption = fontSelector.getFontOption("Antialias");
    if (antialiasOption != null)
      antialiasOption.setValue(antialias);
   ((TextStylePreviewPanel)fontSelector.getPreviewPanel()).setAntialias(antialias);
  }




  /**
   * The text preview panel we use.
   */

  private class TextStylePreviewPanel extends FontSelectorPanel.DefaultPreviewPanel{


    /**
     * The initial antialiasing value.
     */

    private boolean antialias;



    /**
     * Becomes false if we find out we're not running under a Java2D capable JVM.
     */
    
    private boolean antialiasingSupported = true;



    /**
     * Creates a new <code>TextStylePreviewPanel</code> with the specified
     * user <code>FontSelectorPanel</code> and the initial antialiasing value.
     */

    public TextStylePreviewPanel(FontSelectorPanel fontSelector, boolean antialias){
      super(fontSelector);
      this.antialias = antialias;
    }


    

    /**
     * Enables/Disables antialiasing on the specified <code>Graphics</code> object.
     */

    public void paintComponent(Graphics g){
      if (antialiasingSupported){
        try{
          Interpreter bsh = new Interpreter();
          bsh.set("g", g);
          bsh.eval("g2 = (Graphics)g");
          String textAntialiasValue = "RenderingHints.VALUE_TEXT_ANTIALIAS_" + (antialias ? "ON" : "OFF");
          bsh.eval("g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, "+textAntialiasValue+")");
        } catch (EvalError e){
            antialiasingSupported = false;
          }
      }

      super.paintComponent(g);
    }



    /**
     * Sets the current value for antialiasing.
     */

    public void setAntialias(boolean antialias){
      this.antialias = antialias;
    }

  }
  

}
