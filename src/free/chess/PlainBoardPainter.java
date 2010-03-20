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

import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Component;


/**
 * An implementation of BoardPainter which fills the SQUARES with colors.
 */

public class PlainBoardPainter implements ColoredBoardPainter{


  /**
   * The color of the light SQUARES.
   */

  private Color lightColor;



  /**
   * The color of the dark SQUARES.
   */

  private Color darkColor;



  /**
   * Creates a new PlainBoardPainter which paints SQUARES with the given light
   * square color and dark square color.
   */

  public PlainBoardPainter(Color lightColor, Color darkColor){
    this.lightColor = lightColor;
    this.darkColor = darkColor;
  }




  /**
   * Returns the color with which light SQUARES are drawn.
   */

  public Color getLightColor(){
    return lightColor;
  }




  /**
   * Sets the color of the light SQUARES.
   */

  public void setLightColor(Color color){
    this.lightColor = color;
  }




  /**
   * Returns the color with which dark SQUARES are drawn.
   */

  public Color getDarkColor(){
    return darkColor;
  }




  /**
   * Sets the color of the dark SQUARES.
   */

  public void setDarkColor(Color color){
    this.darkColor = color;
  }




  /**
   * Returns a 0x0 Dimension since we don't care at which size to draw the board.
   */

  public Dimension getPreferredBoardSize(){
    return new Dimension(0,0);
  }



  /**
   * Paints the board at the given location on the given Graphics scaled to
   * the given size.
   */

  public void paintBoard(Graphics g, Component component, int x, int y, int width, int height){
    int squareWidth = width/8;
    int squareHeight = height/8;
    for (int i=0;i<8;i++)
      for (int j=0;j<8;j++){
        if ((i+j)%2==0)
          g.setColor(lightColor);
        else
          g.setColor(darkColor);

        g.fillRect(x+i*squareWidth, y+j*squareHeight, squareWidth, squareHeight);
      }
  }


}
