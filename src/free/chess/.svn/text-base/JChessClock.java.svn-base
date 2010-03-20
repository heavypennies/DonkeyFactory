/**
 * The chess framework library.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2002 Alexander Maryanovsky.
 * All rights reserved.
 *
 * The chess framework library is free software; you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * The chess framework library is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the chess framework library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package free.chess;

import java.awt.*;
import free.util.TextUtilities;
import free.util.GraphicsUtilities;


/**
 * A Component for displaying a simple, text based chess clock. It also has the
 * property of taking up as much space as it can, regardless of the set font size.
 */

public class JChessClock extends AbstractChessClock{



  /**
   * Is this JChessClock showing second tenths?
   */

  private boolean isShowingTenths = false;




  /**
   * The active background color.
   */

  private Color activeBG = Color.blue;




  /**
   * The active foreground color.
   */

  private Color activeFG = Color.white;




  /**
   * The inactive background color. Defaults to the paren't background.
   */

  private Color inactiveBG = null;




  /**
   * The inactive foreground color.
   */

  private Color inactiveFG = Color.black;




  /**
   * The size of the JChessClock the last time it was painted.
   */

  private final Dimension lastSize = new Dimension();




  /**
   * The Font we used the last time this JChessClock was painted.
   */

  private Font lastFont = null;



  /**
   * The FontMetrics of the font we used the last time this JChessClock was painted.
   */

  private FontMetrics lastFontMetrics = null;




  /**
   * Creates a new JChessClock with the given initial amount of time 
   * (in milliseconds) on it.
   */

  public JChessClock(int time){
    super(time);
    setFont(new Font("Monospaced", Font.BOLD, 50));
  }




  /**
   * Returns true if this JChessClock is showing tenths of a second, false
   * otherwise.
   */

  public boolean isShowingTenths(){
    return isShowingTenths;
  }




  /**
   * Sets whether this JChessClock should be displaying tenths of a second.
   */

  public void setShowingTenths(boolean isShowingTenths){
    this.isShowingTenths = isShowingTenths;
  }




  /**
   * Sets the background color of this JChessClock when it's active.
   */

  public void setActiveBackground(Color color){
    activeBG = color;
  }



  /**
   * Returns the background color of this JChessClock when it's active.
   */

  public Color getActiveBackground(){
    return activeBG;
  }



  /**
   * Sets the foreground color of this JChessClock when it's active.
   */

  public void setActiveForeground(Color color){
    activeFG = color;
  }




  /**
   * Returns the foreground color of this JChessClock when it's active.
   */

  public Color getActiveForeground(){
    return activeFG;
  }




  /**
   * Sets the background color of this JChessClock when it's inactive.
   */

  public void setInactiveBackground(Color color){
    inactiveBG = color;
  }



  /**
   * Returns the background color of this JChessClock when it's inactive.
   */

  public Color getInactiveBackground(){
    return inactiveBG == null ? getParent().getBackground() : inactiveBG;
  }



  /**
   * Sets the foreground color of this JChessClock when it's inactive.
   */

  public void setInactiveForeground(Color color){
    inactiveFG = color;
  }




  /**
   * Returns the foreground color of this JChessClock when it's inactive.
   */

  public Color getInactiveForeground(){
    return inactiveFG;
  }




  /**
   * Sets whether the owner of this JChessClock is also the owner of the current
   * turn.
   */

  public void setActive(boolean isActive){
    super.setActive(isActive);
    repaint();
  }




  /**
   * Returns the string that should be drawn for the given time, in milliseconds.
   */

  protected String createTimeString(int time){
    boolean isNegative = time<0;

    time = Math.abs(time);
    int hours = time/(1000*60*60);
    time -= hours*1000*60*60;
    int minutes = time/(1000*60);
    time -= minutes*1000*60;
    int seconds = time/1000;
    time -= seconds*1000;
    int tenths = time/100;
    time -= tenths*100;

    String signString = isNegative ? "-" : "";
    String hoursString = String.valueOf(hours);
    String minutesString = TextUtilities.padStart(String.valueOf(minutes), '0', 2);
    String secondsString = TextUtilities.padStart(String.valueOf(seconds), '0', 2);
    String tenthsString = isShowingTenths() ? ("."+tenths) : "";

    if (hours!=0)
      return signString+hoursString+":"+minutesString+":"+secondsString+tenthsString;
    else
      return signString+minutesString+":"+secondsString+tenthsString;
  }




  /**
   * Determines and returns the largest font of the same kind as the given Font
   * with which the given text can be drawn so that it still fits into the given
   * width and height.
   */

  protected Font findLargestFittingFont(String text, int width, int height, Graphics g, Font font){
    int maxSize = GraphicsUtilities.getMaxFittingFontSize(g, font, text, new Dimension(width, height));
    return new Font(font.getName(), font.getStyle(), maxSize);
  }
  



  /**
   * Overrides JComponent.paintComponent(Graphics) to paint this JChessClock.
   */

  public void paintComponent(Graphics g){
    String text = createTimeString(getTime());
    int width = getWidth();
    int height = getHeight();
    if ((width!=lastSize.width)||(height!=lastSize.height)){
      lastSize.width = getWidth();
      lastSize.height = getHeight();

      lastFont = findLargestFittingFont(text, lastSize.width, lastSize.height, g, getFont());
      lastFontMetrics = g.getFontMetrics(lastFont);
    }

    g.setColor(isActive() ? getActiveBackground() : getInactiveBackground());
    g.fillRect(0, 0, width, height);

    g.setColor(isActive() ? getActiveForeground() : getInactiveForeground());
    g.setFont(lastFont);
    int x = 0;
    int y = lastFontMetrics.getMaxAscent();
    g.drawString(text,x,y);
  }




  /**
   * Returns the preferred size of this JChessClock.
   */

  public Dimension getPreferredSize(){
    String text = createTimeString(getTime());
    Font font = getFont();
    Font prefFont = new Font(font.getName(),font.getStyle(),50);
    FontMetrics fm = GraphicsUtilities.getFontMetrics(prefFont);
    int fontWidth = fm.stringWidth(text);
    int fontHeight = fm.getAscent() + fm.getDescent();
    return new Dimension(fontWidth, fontHeight);
  }




  /**
   * Returns the minimum size of this JChessClock.
   */

  public Dimension getMinimumSize(){
    String text = createTimeString(getTime());
    Font font = getFont();
    Font prefFont = new Font(font.getName(),font.getStyle(),12);
    FontMetrics fm = GraphicsUtilities.getFontMetrics(prefFont);
    int fontWidth = fm.stringWidth(text);
    int fontHeight = fm.getAscent() + fm.getDescent();
    return new Dimension(fontWidth, fontHeight);
  }

}
