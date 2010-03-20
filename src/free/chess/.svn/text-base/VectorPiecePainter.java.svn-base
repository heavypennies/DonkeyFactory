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
import free.chess.ColoredPiecePainter;
import free.chess.Piece;
import free.chess.ChessPiece;
                                                

/**            
 * An abstract class offering a convenient partial implementation for vector
 * based piece sets.
 */

public abstract class VectorPiecePainter implements ColoredPiecePainter{



  /**
   * The color of the white pieces.
   */

  private Color whiteColor;



  /**
   * The color of the black pieces.
   */

  private Color blackColor;



  /**
   * The color of the outline of the white pieces.
   */

  private Color whiteOutline;



  /**
   * The color of the outline of the black pieces.
   */

  private Color blackOutline;



  /**
   * The size of the cached pieces.
   */

  private Dimension cachedPieceSize = new Dimension(-1, -1);




  /**
   * The cached piece polygons.
   */

  private Polygon kingPolygon, queenPolygon, rookPolygon, bishopPolygon, knightPolygon, pawnPolygon;




  /**
   * Creates a new VectorPiecePainter. White pieces will be drawn with white
   * black pieces with black.
   */

  public VectorPiecePainter(){
    this(Color.white,Color.black);
  }




  /**
   * Creates a new VectorPiecePainter which will draw white and black pieces
   * using the given colors. The outline of the pieces will have an RGB value
   * reverse to the RGB values of the given colors (if the color for the white
   * pieces is for example R=255 G=128 B=0 then the outline of the white
   * pieces will be R=0 G=127 B=255).
   *
   * @param whiteColor The color for the white pieces.
   * @param blackColor The color for the black pieces.
   */

  public VectorPiecePainter(Color whiteColor, Color blackColor){
    this(whiteColor, blackColor, getReversed(whiteColor), getReversed(blackColor));
  }






  /**
   * Creates a new VectorPiecePainter which will produce
   * white and black pieces with the given colors and the given
   * outline colors.
   *
   * @param whiteColor The color for the white pieces.
   * @param blackColor The color for the black pieces.
   * @param whiteOutline The color for the outline of white pieces.
   * @param blackOutline The color for the outline of black pieces.
   */

  public VectorPiecePainter(Color whiteColor, Color blackColor,
                            Color whiteOutline, Color blackOutline){
    this.whiteColor = whiteColor;
    this.blackColor = blackColor;
    this.whiteOutline = whiteOutline;
    this.blackOutline = blackOutline;
  }




  
  /**
   * Creates and returns a Polygon of a king when fit into the given dimensions.
   * This polygon will be used to draw king pieces.
   */

  protected abstract Polygon createKingPolygon(int width, int height);




  /**
   * Creates and returns a Polygon of a queen when fit into the given dimensions.
   * This polygon will be used to draw queen pieces.
   */

  protected abstract Polygon createQueenPolygon(int width, int height);




  /**
   * Creates and returns a Polygon of a rook when fit into the given dimensions.
   * This polygon will be used to draw rook pieces.
   */

  protected abstract Polygon createRookPolygon(int width, int height);




  /**
   * Creates and returns a Polygon of a bishop when fit into the given dimensions.
   * This polygon will be used to draw bishop pieces.
   */

  protected abstract Polygon createBishopPolygon(int width, int height);




  /**
   * Creates and returns a Polygon of a knight when fit into the given dimensions.
   * This polygon will be used to draw knight pieces.
   */

  protected abstract Polygon createKnightPolygon(int width, int height);




  /**
   * Creates and returns a Polygon of a pawn when fit into the given dimensions.
   * This polygon will be used to draw pawn pieces.
   */

  protected abstract Polygon createPawnPolygon(int width, int height);



  /**
   * Returns the color with the opposite RGB values from the given color.
   *
   * @param color The color to reverse.
   */

  private static Color getReversed(Color color){
    return new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
  }




  /**
   * Retrieves the color with which white pieces are drawn.
   *
   * @return The color for the white pieces.
   */

  public Color getWhiteColor(){
    return whiteColor;
  }




  /**
   * Sets the color with which white pieces are drawn.
   */

  public void setWhiteColor(Color color){
    whiteColor = color;
  }





  /**
   * Retrieves the color with which black pieces are drawn.
   *
   * @return The color for the black pieces.
   */

  public Color getBlackColor(){
    return blackColor;
  }




  /**
   * Sets the color with which black pieces are drawn.
   */

  public void setBlackColor(Color color){
    blackColor = color;
  }




  /**
   * Retrieves the color with which the outline of white pieces is drawn.
   *
   * @return The color for the outline of white pieces.
   */

  public Color getWhiteOutline(){
    return whiteOutline;
  }




  /**
   * Sets the color with which the outline of white pieces is drawn.
   */

  public void setWhiteOutline(Color color){
    whiteOutline = color;
  }





  /**
   * Retrieves the color with which the outline of black pieces is drawn.
   *
   * @return The color for the outline of black pieces.
   */

  public Color getBlackOutline(){
    return blackOutline;
  }





  /**
   * Sets the color with which the outline of black pieces is drawn.
   */

  public void setBlackOutline(Color color){
    blackOutline = color;
  }




  /**
   * Clears the caching of piece polygons.
   */

  private void clearPieceCache(){
    kingPolygon = null;
    queenPolygon = null;
    rookPolygon = null;
    bishopPolygon = null;
    knightPolygon = null;
    pawnPolygon = null;
  }






  /**
   * Draws the given piece at the given coordinates with the given size on
   * the given Graphics.
   */

  public void paintPiece(Piece piece, Graphics g, Component component, Rectangle rect,
      boolean shaded){

    Color pieceColor = (piece.isWhite() ? getWhiteColor() : getBlackColor());
    Color outlineColor = (piece.isWhite() ? getWhiteOutline() : getBlackOutline());

    if (shaded){
      pieceColor = shade(pieceColor);
      outlineColor = shade(outlineColor);
    }

    int x = rect.x;
    int y = rect.y;
    int width = rect.width;
    int height = rect.height;

    g.translate(x,y);

    if (!(piece instanceof ChessPiece))
      return;

    ChessPiece cPiece = (ChessPiece)piece;

    if (cPiece.isKing())
      drawKingImage(g, width, height, pieceColor, outlineColor);
    else if (cPiece.isQueen())
      drawQueenImage(g, width, height, pieceColor, outlineColor);
    else if (cPiece.isRook())
      drawRookImage(g, width, height, pieceColor, outlineColor);
    else if (cPiece.isBishop())
      drawBishopImage(g, width, height, pieceColor, outlineColor);
    else if (cPiece.isKnight())
      drawKnightImage(g, width, height, pieceColor, outlineColor);
    else if (cPiece.isPawn())
      drawPawnImage(g, width, height, pieceColor, outlineColor);

    g.translate(-x, -y);
  }




  /**
   * Returns a shaded version of the specified Color.
   */

  private Color shade(Color color){
    int r = (color.getRed() + 128*2)/3;
    int g = (color.getGreen() + 128*2)/3;
    int b = (color.getBlue() + 128*2)/3;

    return new Color(r, g, b);
  }



  /**
   * Paints an image of a king of the given size with the given color and
   * outline color using the given Graphics.
   */

  protected void drawKingImage(Graphics g, int width, int height, Color pieceColor, Color outlineColor){
    if ((cachedPieceSize.width!=width)||(cachedPieceSize.height!=height)||(kingPolygon==null)){
      if (kingPolygon!=null){
        cachedPieceSize.width = width;
        cachedPieceSize.height = height;
        clearPieceCache();
      }

      kingPolygon = createKingPolygon(width, height);
    }

    g.setColor(pieceColor);
    g.fillPolygon(kingPolygon);

    g.setColor(outlineColor);
    g.drawPolygon(kingPolygon);
  }






  /**
   * Paints an image of a queen of the given size with the given color and
   * outline color using the given Graphics.
   */

  protected void drawQueenImage(Graphics g, int width, int height, Color pieceColor, Color outlineColor){
    if ((cachedPieceSize.width!=width)||(cachedPieceSize.height!=height)||(queenPolygon==null)){
      if (queenPolygon!=null){
        cachedPieceSize.width = width;
        cachedPieceSize.height = height;
        clearPieceCache();
      }

      queenPolygon = createQueenPolygon(width, height);
    }

    g.setColor(pieceColor);
    g.fillPolygon(queenPolygon);

    g.setColor(outlineColor);
    g.drawPolygon(queenPolygon);
  }







  /**
   * Paints an image of a rook of the given size with the given color and
   * outline color using the given Graphics.
   */

  protected void drawRookImage(Graphics g, int width, int height, Color pieceColor, Color outlineColor){
    if ((cachedPieceSize.width!=width)||(cachedPieceSize.height!=height)||(rookPolygon==null)){
      if (rookPolygon!=null){
        cachedPieceSize.width = width;
        cachedPieceSize.height = height;
        clearPieceCache();
      }

      rookPolygon = createRookPolygon(width, height);
    }

    g.setColor(pieceColor);
    g.fillPolygon(rookPolygon);

    g.setColor(outlineColor);
    g.drawPolygon(rookPolygon);
  }






  /**
   * Paints an image of a bishop of the given size with the given color and
   * outline color using the given Graphics.
   */

  protected void drawBishopImage(Graphics g, int width, int height, Color pieceColor, Color outlineColor){
    if ((cachedPieceSize.width!=width)||(cachedPieceSize.height!=height)||(bishopPolygon==null)){
      if (bishopPolygon!=null){
        cachedPieceSize.width = width;
        cachedPieceSize.height = height;
        clearPieceCache();
      }

      bishopPolygon = createBishopPolygon(width, height);
    }

    g.setColor(pieceColor);
    g.fillPolygon(bishopPolygon);

    g.setColor(outlineColor);
    g.drawPolygon(bishopPolygon);
  }






  /**
   * Paints an image of a knight of the given size with the given color and
   * outline color using the given Graphics.
   */

  protected void drawKnightImage(Graphics g, int width, int height, Color pieceColor, Color outlineColor){
    if ((cachedPieceSize.width!=width)||(cachedPieceSize.height!=height)||(knightPolygon==null)){
      if (knightPolygon!=null){
        cachedPieceSize.width = width;
        cachedPieceSize.height = height;
        clearPieceCache();
      }

      knightPolygon = createKnightPolygon(width, height);
    }

    g.setColor(pieceColor);
    g.fillPolygon(knightPolygon);

    g.setColor(outlineColor);
    g.drawPolygon(knightPolygon);
  }




  /**
   * Paints an image of a pawn of the given size with the given color and
   * outline color using the given Graphics.
   */

  protected void drawPawnImage(Graphics g, int width, int height, Color pieceColor, Color outlineColor){
    if ((cachedPieceSize.width!=width)||(cachedPieceSize.height!=height)||(pawnPolygon==null)){
      if (pawnPolygon!=null){
        cachedPieceSize.width = width;
        cachedPieceSize.height = height;
        clearPieceCache();
      }

      pawnPolygon = createPawnPolygon(width, height);
    }

    g.setColor(pieceColor);
    g.fillPolygon(pawnPolygon);

    g.setColor(outlineColor);
    g.drawPolygon(pawnPolygon);
  }    





}
