/**
 * The utillib library.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2002 Alexander Maryanovsky.
 * All rights reserved.
 *
 * The utillib library is free software; you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * The utillib library is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with utillib library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package free.util.swing;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;


/**
 * A color chooser component which displays a button and the currently selected
 * color. When the button is pressed, it opens a JColorChooser and allows the
 * user to select a different color.
 */

public class ColorChooserButton extends JComponent implements ActionListener{



  /**
   * The button we're using.
   */

  private final JButton button;




  /**
   * The icon's size.
   */

  private static final Dimension iconSize = new Dimension(30, 20);




  /**
   * The current color.
   */

  private Color color;




  /**
   * The sole ChangeEvent we need.
   */

  private final ChangeEvent changeEvent = new ChangeEvent(this);





  /**
   * Creates a new ColorChooserButton with no text and the given initial color.
   */

  public ColorChooserButton(Color initialColor){
    this(null, initialColor);
  }





  /**
   * Creates a new ColorChooserButton with the given text and initial color.
   */

  public ColorChooserButton(String text, Color initialColor){
    button = new JButton(text, new SolidColorRectangleIcon(iconSize, initialColor));

    setLayout(new BorderLayout());
    add(button, BorderLayout.CENTER);

    button.setHorizontalTextPosition(JButton.LEADING);
    button.setDefaultCapable(false);

    color = initialColor;

    button.addActionListener(this);
  }




  /**
   * Sets the mnemonic.
   */

  public void setMnemonic(char mnemonic){
    button.setMnemonic(mnemonic);
  } 




  /**
   * Sets the mnemonic.
   */

  public void setMnemonic(int mnemonic){
    button.setMnemonic(mnemonic);
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
   * Returns the currently selected color.
   */

  public Color getColor(){
    return color;
  }




  /**
   * Sets the current color.
   */

  public void setColor(Color color){
    this.color = color;
    button.setIcon(new SolidColorRectangleIcon(iconSize, color));
    fireStateChanged();
  }




  /**
   * Shows the color chooser.
   */

  public void actionPerformed(ActionEvent evt){
    Color newColor = JColorChooser.showDialog(SwingUtilities.windowForComponent(this),
      "Choose a color", color);
    if (newColor != null)
      setColor(newColor);
  }

}
