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
import java.awt.image.*;
import java.util.Hashtable;
import java.util.Enumeration;
import free.util.ImageUtilities;


/**
 * An implementation of PiecePainter which paints images.
 */

public class ImagePiecePainter implements PiecePainter{


  /**
   * The ImageFilter we use to create shaded images.
   */

  private static final ImageFilter shadingFilter = new ShadingFilter();



  /**
   * An array whose indices specify the size of the images and whose values
   * are Hashtables mapping Pieces to Images.
   */

  private final Hashtable [] pieceImages;



  /**
   * Same as pieceImages only for shaded images.
   */

  private final Hashtable [] shadedPieceImages;




  /**
   * The preferred size of the pieces.
   */

  private final Dimension prefPieceSize;




  /**
   * Creates a new ImagePiecePainter with the given preferred piece image size.
   * The given Hashtable should map Integer objects specifying the size of the
   * piece images to Hashtables which in turn map Piece objects to piece Images.
   */

  public ImagePiecePainter(Dimension prefPieceSize, Hashtable pieceImages){

    // Find the largest size
    int maxSize = 0;
    Enumeration sizes = pieceImages.keys();
    while (sizes.hasMoreElements()){
      int size = ((Integer)sizes.nextElement()).intValue();
      if (size <= 0)
        throw new IllegalArgumentException("Image sizes must be positive");

      if (size > maxSize)
        maxSize = size;
    }

    if (maxSize == 0)
      throw new IllegalArgumentException("No sizes in the hashtable");

    this.pieceImages = new Hashtable[maxSize + 1];
    this.shadedPieceImages = new Hashtable[maxSize + 1];

    // Fill the array
    sizes = pieceImages.keys();
    while (sizes.hasMoreElements()){
      Integer size = (Integer)sizes.nextElement();
      int sizeInt = size.intValue();

      Hashtable images = (Hashtable)pieceImages.get(size);
      int imagesCount = images.size();
      this.pieceImages[sizeInt] = new Hashtable(imagesCount);
      this.shadedPieceImages[sizeInt] = new Hashtable(imagesCount);

      Enumeration pieces = images.keys();
      while (pieces.hasMoreElements()){
        Object key = pieces.nextElement();
        Image image = (Image)images.get(key);
        Image shadedImage = shadeImage(image);

        this.pieceImages[sizeInt].put(key, image);
        this.shadedPieceImages[sizeInt].put(key, shadedImage);
      }
    }

    this.prefPieceSize = new Dimension(prefPieceSize.width, prefPieceSize.height);
  }




  /**
   * Returns a shaded version of the specified image.
   */

  protected Image shadeImage(Image image){
    Image shadedImage = Toolkit.getDefaultToolkit().createImage(
      new FilteredImageSource(image.getSource(), shadingFilter));
    return shadedImage;
  }




  /**
   * Returns the Image by which the given Piece is represented at the given
   * size.
   */

  protected Image getPieceImage(int size, Piece piece, boolean shaded){
    Hashtable [] images = shaded ? shadedPieceImages : pieceImages;

    if (size <= 0)
      throw new IllegalArgumentException("Image size must be positive");

    if (size >= images.length)
      return (Image)(images[images.length-1].get(piece));

    if (images[size] != null)
      return (Image)(images[size].get(piece));

    for (int i = size; i > 0; i--)
      if (images[i] != null)
        return (Image)(images[i].get(piece));

    for (int i = size+1; i < images.length; i++)
      if (images[i] != null)
        return (Image)(images[i].get(piece));

    throw new Error("This can't happen");
  }




  /**
   * Returns the preferred size of the pieces.
   */

  public Dimension getPreferredPieceSize(){
    return new Dimension(prefPieceSize.width, prefPieceSize.height);
  }





  /**
   * Paints the image of the given piece at the given coordinates on the given
   * Graphics object scaled to the given size.
   */

  public void paintPiece(Piece piece, Graphics g, Component component, Rectangle rect,
      boolean shaded){

    int x = rect.x;
    int y = rect.y;
    int width = rect.width;
    int height = rect.height;

    int size = width > height ? height : width;
    Image pieceImage = getPieceImage(size, piece, shaded);
    if (shaded)
      try{
        ImageUtilities.preload(pieceImage);
      } catch (InterruptedException e){}
    int pieceWidth = pieceImage.getWidth(null);
    int pieceHeight = pieceImage.getHeight(null);
    g.drawImage(pieceImage, x + (width - pieceWidth)/2, y + (height - pieceHeight)/2, component);
  }




  /**
   * The <code>ImageFilter</code> we use to create shaded piece images.
   */

  private static class ShadingFilter extends RGBImageFilter{

    public int filterRGB(int x, int y, int rgb){
      int alpha = (rgb >> 24) & 0xff;
      int r = (rgb >> 16) & 0xff;
      int g = (rgb >> 8) & 0xff;
      int b = rgb & 0xff;

      r = (r + 128*2)/3;
      g = (g + 128*2)/3;
      b = (b + 128*2)/3;

      return (alpha << 24) | (r << 16) | (g << 8) | b;
    }

  }


}
