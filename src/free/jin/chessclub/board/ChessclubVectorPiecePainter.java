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

package free.jin.chessclub.board;

import java.awt.*;
import free.chess.VectorPiecePainter;


/**
 * An implementation of PiecePainter which draws blitzin-like vector pieces.
 * Note that the vector pieces belong exlusively to chessclub.com - you may use
 * them only as part of Jin and only with the chessclub.com server. You may
 * *not* use them for any other purpose, commercial or otherwise.
 */

public class ChessclubVectorPiecePainter extends VectorPiecePainter{



  /**
   * Creates a new ChessclubVectorPiecePainter. White pieces will have the
   * white color and black pieces the black color.
   */

  public ChessclubVectorPiecePainter(){
    super();
  }




  /**
   * Creates a new ChessclubVectorPiecePainter which will produce
   * White and black pieces with the given colors. The outline of the
   * pieces will have the opposite RGB values from the RGB values of
   * the given colors (if the color for the white pieces is for example
   * R=255 G=128 B=0 then the outline of the white pieces will be
   * R=0 G=127 B=255).
   *
   * @param whiteColor The color for the white pieces.
   * @param blackColor The color for the black pieces.
   */

  public ChessclubVectorPiecePainter(Color whiteColor, Color blackColor){
    super(whiteColor, blackColor);
  }






  /**
   * Creates a new ChessclubVectorPiecePainter which will produce
   * white and black pieces with the given colors and the given
   * outline colors.
   *
   * @param whiteColor The color for the white pieces.
   * @param blackColor The color for the black pieces.
   * @param whiteOutline The color for the outline of white pieces.
   * @param blackOutline The color for the outline of black pieces.
   */

  public ChessclubVectorPiecePainter(Color whiteColor, Color blackColor,
                                  Color whiteOutline, Color blackOutline){
    super(whiteColor, blackColor, whiteOutline, blackOutline);
  }




  /**
   * Returns 36x36.
   */

  public Dimension getPreferredPieceSize(){
    return new Dimension(36,36);
  }




  /**
   * Creates and returns a Polygon for drawing a king.
   */

  protected Polygon createKingPolygon(int width, int height){
    Polygon kingPolygon = new Polygon();
    kingPolygon.addPoint(12*width/37,34*height/37);
    kingPolygon.addPoint(25*width/37,34*height/37);
    kingPolygon.addPoint(27*width/37,33*height/37);
    kingPolygon.addPoint(29*width/37,23*height/37);
    kingPolygon.addPoint(32*width/37,22*height/37);
    kingPolygon.addPoint(34*width/37,20*height/37);
    kingPolygon.addPoint(35*width/37,17*height/37);
    kingPolygon.addPoint(35*width/37,16*height/37);
    kingPolygon.addPoint(34*width/37,13*height/37);
    kingPolygon.addPoint(32*width/37,11*height/37);
    kingPolygon.addPoint(28*width/37,10*height/37);
    kingPolygon.addPoint(24*width/37,12*height/37);
    kingPolygon.addPoint(23*width/37,13*height/37);
    kingPolygon.addPoint(22*width/37,13*height/37);
    kingPolygon.addPoint(22*width/37,11*height/37);
    kingPolygon.addPoint((int)(19.4*width/37),8*height/37);
    kingPolygon.addPoint((int)(19.4*width/37),(int)(6.4*height/37));
    kingPolygon.addPoint(21*width/37,(int)(6.4*height/37));
    kingPolygon.addPoint(21*width/37,(int)(4.6*height/37));
    kingPolygon.addPoint((int)(19.4*width/37),(int)(4.6*height/37));
    kingPolygon.addPoint((int)(19.4*width/37),3*height/37);
    kingPolygon.addPoint((int)(17.6*width/37),3*height/37);
    kingPolygon.addPoint((int)(17.6*width/37),(int)(4.6*height/37));
    kingPolygon.addPoint(16*width/37,(int)(4.6*height/37));
    kingPolygon.addPoint(16*width/37,(int)(6.4*height/37));
    kingPolygon.addPoint((int)(17.6*width/37),(int)(6.4*height/37));
    kingPolygon.addPoint((int)(17.6*width/37),8*height/37);
    kingPolygon.addPoint(15*width/37,11*height/37);
    kingPolygon.addPoint(15*width/37,13*height/37);
    kingPolygon.addPoint(14*width/37,13*height/37);
    kingPolygon.addPoint(13*width/37,12*height/37);
    kingPolygon.addPoint(9*width/37,10*height/37);
    kingPolygon.addPoint(5*width/37,11*height/37);
    kingPolygon.addPoint(3*width/37,13*height/37);
    kingPolygon.addPoint(2*width/37,16*height/37);
    kingPolygon.addPoint(2*width/37,17*height/37);
    kingPolygon.addPoint(3*width/37,20*height/37);
    kingPolygon.addPoint(5*width/37,22*height/37);
    kingPolygon.addPoint(8*width/37,23*height/37);
    kingPolygon.addPoint(10*width/37,33*height/37);

    return kingPolygon;
  }




  /**
   * Paints an image of a king of the given size with the given color and
   * outline color using the given Graphics.
   */

  protected void drawKingImage(Graphics g, int width, int height, Color pieceColor, Color outlineColor){
    super.drawKingImage(g, width, height, pieceColor, outlineColor);

    g.setColor(outlineColor);
    g.drawLine(10*width/37,31*height/37,18*width/37,30*height/37);
    g.drawLine(18*width/37,30*height/37,19*width/37,30*height/37);
    g.drawLine(19*width/37,30*height/37,27*width/37,31*height/37);
    g.drawLine(9*width/37,28*height/37,16*width/37,27*height/37);
    g.drawLine(16*width/37,27*height/37,21*width/37,27*height/37);
    g.drawLine(21*width/37,27*height/37,28*width/37,28*height/37);
    g.drawLine(20*width/37,22*height/37,28*width/37,22*height/37);
    g.drawLine(28*width/37,22*height/37,30*width/37,21*height/37);
    g.drawLine(30*width/37,21*height/37,31*width/37,20*height/37);
    g.drawLine(31*width/37,20*height/37,32*width/37,17*height/37);
    g.drawLine(32*width/37,17*height/37,32*width/37,16*height/37);
    g.drawLine(32*width/37,16*height/37,31*width/37,13*height/37);
    g.drawLine(31*width/37,13*height/37,30*width/37,12*height/37);
    g.drawLine(30*width/37,12*height/37,27*width/37,12*height/37);
    g.drawLine(27*width/37,12*height/37,20*width/37,19*height/37);
    g.drawLine(20*width/37,19*height/37,20*width/37,22*height/37);
    g.drawLine(17*width/37,22*height/37,9*width/37,22*height/37);
    g.drawLine(9*width/37,22*height/37,7*width/37,21*height/37);
    g.drawLine(7*width/37,21*height/37,6*width/37,20*height/37);
    g.drawLine(6*width/37,20*height/37,5*width/37,17*height/37);
    g.drawLine(5*width/37,17*height/37,5*width/37,16*height/37);
    g.drawLine(5*width/37,16*height/37,6*width/37,13*height/37);
    g.drawLine(6*width/37,13*height/37,7*width/37,12*height/37);
    g.drawLine(7*width/37,12*height/37,10*width/37,12*height/37);
    g.drawLine(10*width/37,12*height/37,17*width/37,19*height/37);
    g.drawLine(17*width/37,19*height/37,17*width/37,22*height/37);
    g.drawLine(19*width/37,11*height/37,20*width/37,14*height/37);
    g.drawLine(20*width/37,14*height/37,19*width/37,15*height/37);
    g.drawLine(19*width/37,15*height/37,18*width/37,15*height/37);
    g.drawLine(18*width/37,15*height/37,17*width/37,14*height/37);
    g.drawLine(17*width/37,14*height/37,18*width/37,11*height/37);
    g.drawLine(18*width/37,11*height/37,19*width/37,11*height/37);
  }





  /**
   * Creates and returns a Polygon for drawing a queen.
   */

  protected Polygon createQueenPolygon(int width, int height){
    Polygon queenPolygon = new Polygon();

    queenPolygon.addPoint(8*width/37,34*height/37);
    queenPolygon.addPoint(29*width/37,34*height/37);
    queenPolygon.addPoint(29*width/37,27*height/37);
    queenPolygon.addPoint(32*width/37,13*height/37);
    queenPolygon.addPoint(33*width/37,11*height/37);
    queenPolygon.addPoint(35*width/37,9*height/37);
    queenPolygon.addPoint(35*width/37,7*height/37);
    queenPolygon.addPoint(34*width/37,6*height/37);
    queenPolygon.addPoint(31*width/37,6*height/37);
    queenPolygon.addPoint(30*width/37,7*height/37);
    queenPolygon.addPoint(30*width/37,10*height/37);
    queenPolygon.addPoint(31*width/37,11*height/37);
    queenPolygon.addPoint(26*width/37,19*height/37);
    queenPolygon.addPoint(24*width/37,8*height/37);
    queenPolygon.addPoint(26*width/37,6*height/37);
    queenPolygon.addPoint(26*width/37,4*height/37);
    queenPolygon.addPoint(25*width/37,3*height/37);
    queenPolygon.addPoint(22*width/37,3*height/37);
    queenPolygon.addPoint(21*width/37,4*height/37);
    queenPolygon.addPoint(21*width/37,7*height/37);
    queenPolygon.addPoint(22*width/37,8*height/37);
    queenPolygon.addPoint(19*width/37,17*height/37);
    queenPolygon.addPoint(18*width/37,17*height/37);
    queenPolygon.addPoint(15*width/37,8*height/37);
    queenPolygon.addPoint(16*width/37,7*height/37);
    queenPolygon.addPoint(16*width/37,4*height/37);
    queenPolygon.addPoint(15*width/37,3*height/37);
    queenPolygon.addPoint(12*width/37,3*height/37);
    queenPolygon.addPoint(11*width/37,4*height/37);
    queenPolygon.addPoint(11*width/37,6*height/37);
    queenPolygon.addPoint(13*width/37,8*height/37);
    queenPolygon.addPoint(11*width/37,19*height/37);
    queenPolygon.addPoint(6*width/37,11*height/37);
    queenPolygon.addPoint(7*width/37,10*height/37);
    queenPolygon.addPoint(7*width/37,7*height/37);
    queenPolygon.addPoint(6*width/37,6*height/37);
    queenPolygon.addPoint(3*width/37,6*height/37);
    queenPolygon.addPoint(2*width/37,7*height/37);
    queenPolygon.addPoint(2*width/37,9*height/37);
    queenPolygon.addPoint(4*width/37,11*height/37);
    queenPolygon.addPoint(5*width/37,13*height/37);
    queenPolygon.addPoint(8*width/37,27*height/37);

    return queenPolygon;
  }





  /**
   * Paints an image of a queen of the given size with the given color and
   * outline color using the given Graphics.
   */

  protected void drawQueenImage(Graphics g, int width, int height, Color pieceColor, Color outlineColor){
    super.drawQueenImage(g, width, height, pieceColor, outlineColor);

    g.setColor(outlineColor);
    g.drawLine(29*width/37,31*height/37,24*width/37,30*height/37);
    g.drawLine(24*width/37,30*height/37,13*width/37,30*height/37);
    g.drawLine(13*width/37,30*height/37,8*width/37,31*height/37);
    g.drawLine(8*width/37,26*height/37,13*width/37,25*height/37);
    g.drawLine(13*width/37,25*height/37,24*width/37,25*height/37);
    g.drawLine(24*width/37,25*height/37,29*width/37,26*height/37);
  }





  /**
   * Creates and returns a Polygon for drawing a rook.
   */

  protected Polygon createRookPolygon(int width, int height){
    Polygon rookPolygon = new Polygon();

    rookPolygon.addPoint(5*width/37,34*height/37);
    rookPolygon.addPoint(32*width/37,34*height/37);
    rookPolygon.addPoint(32*width/37,32*height/37);
    rookPolygon.addPoint(26*width/37,27*height/37);
    rookPolygon.addPoint(26*width/37,12*height/37);
    rookPolygon.addPoint(30*width/37,8*height/37);
    rookPolygon.addPoint(30*width/37,3*height/37);
    rookPolygon.addPoint(25*width/37,3*height/37);
    rookPolygon.addPoint(25*width/37,6*height/37);
    rookPolygon.addPoint(21*width/37,6*height/37);
    rookPolygon.addPoint(21*width/37,3*height/37);
    rookPolygon.addPoint(16*width/37,3*height/37);
    rookPolygon.addPoint(16*width/37,6*height/37);
    rookPolygon.addPoint(12*width/37,6*height/37);
    rookPolygon.addPoint(12*width/37,3*height/37);
    rookPolygon.addPoint(7*width/37,3*height/37);
    rookPolygon.addPoint(7*width/37,8*height/37);
    rookPolygon.addPoint(11*width/37,12*height/37);
    rookPolygon.addPoint(11*width/37,27*height/37);
    rookPolygon.addPoint(5*width/37,32*height/37);

    return rookPolygon;
  }

  
  /**
   * Paints an image of a rook of the given size with the given color and
   * outline color using the given Graphics.
   */

  protected void drawRookImage(Graphics g, int width, int height, Color pieceColor, Color outlineColor){
    super.drawRookImage(g, width, height, pieceColor, outlineColor);

    g.setColor(outlineColor);
    g.drawLine(26*width/37,27*height/37,11*width/37,27*height/37);
    g.drawLine(26*width/37,12*height/37,11*width/37,12*height/37);
  }





  /**
   * Creates and returns a Polygon for drawing a bishop.
   */

  protected Polygon createBishopPolygon(int width, int height){
    Polygon bishopPolygon = new Polygon();

    bishopPolygon.addPoint(3*width/37,34*height/37);
    bishopPolygon.addPoint(34*width/37,34*height/37);
    bishopPolygon.addPoint(33*width/37,31*height/37);
    bishopPolygon.addPoint(24*width/37,31*height/37);
    bishopPolygon.addPoint(26*width/37,18*height/37);
    bishopPolygon.addPoint(26*width/37,15*height/37);
    bishopPolygon.addPoint(25*width/37,12*height/37);
    bishopPolygon.addPoint(20*width/37,7*height/37);
    bishopPolygon.addPoint(21*width/37,5*height/37);
    bishopPolygon.addPoint(20*width/37,3*height/37);
    bishopPolygon.addPoint(17*width/37,3*height/37);
    bishopPolygon.addPoint(16*width/37,5*height/37);
    bishopPolygon.addPoint(17*width/37,7*height/37);
    bishopPolygon.addPoint(12*width/37,12*height/37);
    bishopPolygon.addPoint(11*width/37,15*height/37);
    bishopPolygon.addPoint(11*width/37,18*height/37);
    bishopPolygon.addPoint(13*width/37,31*height/37);
    bishopPolygon.addPoint(4*width/37,31*height/37);

    return bishopPolygon;
  }




  /**
   * Paints an image of a bishop of the given size with the given color and
   * outline color using the given Graphics.
   */

  protected void drawBishopImage(Graphics g, int width, int height, Color pieceColor, Color outlineColor){
    super.drawBishopImage(g, width, height, pieceColor, outlineColor);

    g.setColor(outlineColor);
    g.drawLine(24*width/37,31*height/37,22*width/37,29*height/37);
    g.drawLine(22*width/37,29*height/37,15*width/37,29*height/37);
    g.drawLine(15*width/37,29*height/37,13*width/37,31*height/37);
    g.drawLine(25*width/37,25*height/37,12*width/37,25*height/37);
    g.drawLine(18*width/37,19*height/37,19*width/37,19*height/37);
    g.drawLine(19*width/37,19*height/37,19*width/37,16*height/37);
    g.drawLine(19*width/37,16*height/37,21*width/37,16*height/37);
    g.drawLine(21*width/37,16*height/37,21*width/37,15*height/37);
    g.drawLine(21*width/37,15*height/37,19*width/37,15*height/37);
    g.drawLine(19*width/37,15*height/37,19*width/37,13*height/37);
    g.drawLine(19*width/37,13*height/37,18*width/37,13*height/37);
    g.drawLine(18*width/37,13*height/37,18*width/37,15*height/37);
    g.drawLine(18*width/37,15*height/37,16*width/37,15*height/37);
    g.drawLine(16*width/37,15*height/37,16*width/37,16*height/37);
    g.drawLine(16*width/37,16*height/37,18*width/37,16*height/37);
    g.drawLine(18*width/37,16*height/37,18*width/37,19*height/37);
  }






  /**
   * Creates and returns a Polygon for drawing a knight.
   */

  protected Polygon createKnightPolygon(int width, int height){
    Polygon knightPolygon = new Polygon();

    knightPolygon.addPoint(10*width/37,34*height/37);
    knightPolygon.addPoint(34*width/37,34*height/37);
    knightPolygon.addPoint(34*width/37,26*height/37);
    knightPolygon.addPoint(31*width/37,14*height/37);
    knightPolygon.addPoint(26*width/37,9*height/37);
    knightPolygon.addPoint(21*width/37,7*height/37);
    knightPolygon.addPoint(18*width/37,3*height/37);
    knightPolygon.addPoint(17*width/37,3*height/37);
    knightPolygon.addPoint(18*width/37,7*height/37);
    knightPolygon.addPoint(14*width/37,3*height/37);
    knightPolygon.addPoint(13*width/37,3*height/37);
    knightPolygon.addPoint(14*width/37,7*height/37);
    knightPolygon.addPoint(12*width/37,6*height/37);
    knightPolygon.addPoint(9*width/37,9*height/37);
    knightPolygon.addPoint(2*width/37,23*height/37);
    knightPolygon.addPoint(6*width/37,27*height/37);
    knightPolygon.addPoint(10*width/37,23*height/37);
    knightPolygon.addPoint((int)(7.4*width/37),27*height/37);
    knightPolygon.addPoint(8*width/37,27*height/37);
    knightPolygon.addPoint(17*width/37,20*height/37);
    knightPolygon.addPoint(18*width/37,22*height/37);
    knightPolygon.addPoint(10*width/37,31*height/37);

    return knightPolygon;
  }


  


  /**
   * Paints an image of a knight of the given size with the given color and
   * outline color using the given Graphics.
   */

  protected void drawKnightImage(Graphics g, int width, int height, Color pieceColor, Color outlineColor){
    super.drawKnightImage(g, width, height, pieceColor, outlineColor);

    g.setColor(outlineColor);
    g.drawLine(12*width/37,11*height/37,11*width/37,11*height/37);
    g.drawLine(11*width/37,11*height/37,10*width/37,12*height/37);
    g.drawLine(10*width/37,12*height/37,9*width/37,14*height/37);
    g.drawLine(9*width/37,14*height/37,11*width/37,13*height/37);
    g.drawLine(11*width/37,13*height/37,12*width/37,12*height/37);
    g.drawLine(12*width/37,12*height/37,12*width/37,11*height/37);
    g.drawLine(21*width/37,10*height/37,27*width/37,15*height/37);
    g.drawLine(27*width/37,15*height/37,30*width/37,32*height/37);
    g.drawLine(30*width/37,32*height/37,32*width/37,32*height/37);
    g.drawLine(32*width/37,32*height/37,28*width/37,15*height/37);
    g.drawLine(28*width/37,15*height/37,22*width/37,10*height/37);
    g.drawLine(22*width/37,10*height/37,21*width/37,10*height/37);
    g.drawLine(5*width/37,20*height/37,4*width/37,23*height/37);
    g.drawLine(4*width/37,23*height/37,5*width/37,24*height/37);
    g.drawLine(5*width/37,24*height/37,6*width/37,21*height/37);
    g.drawLine(6*width/37,21*height/37,5*width/37,20*height/37);
  }





  /**
   * Creates and returns a Polygon for drawing a pawn.
   */

  protected Polygon createPawnPolygon(int width, int height){
    Polygon pawnPolygon = new Polygon();

    pawnPolygon.addPoint(6*width/37,31*height/37);
    pawnPolygon.addPoint(31*width/37,31*height/37);
    pawnPolygon.addPoint(31*width/37,27*height/37);
    pawnPolygon.addPoint(28*width/37,23*height/37);
    pawnPolygon.addPoint(22*width/37,19*height/37);
    pawnPolygon.addPoint(25*width/37,17*height/37);
    pawnPolygon.addPoint(25*width/37,13*height/37);
    pawnPolygon.addPoint(23*width/37,11*height/37);
    pawnPolygon.addPoint(21*width/37,11*height/37);
    pawnPolygon.addPoint(23*width/37,9*height/37);
    pawnPolygon.addPoint(23*width/37,6*height/37);
    pawnPolygon.addPoint(21*width/37,4*height/37);
    pawnPolygon.addPoint(16*width/37,4*height/37);
    pawnPolygon.addPoint(14*width/37,6*height/37);
    pawnPolygon.addPoint(14*width/37,9*height/37);
    pawnPolygon.addPoint(16*width/37,11*height/37);
    pawnPolygon.addPoint(14*width/37,11*height/37);
    pawnPolygon.addPoint(12*width/37,13*height/37);
    pawnPolygon.addPoint(12*width/37,17*height/37);
    pawnPolygon.addPoint(15*width/37,19*height/37);
    pawnPolygon.addPoint(9*width/37,23*height/37);
    pawnPolygon.addPoint(6*width/37,27*height/37);

    return pawnPolygon;
  }

}
