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

package free.chess.art;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.Hashtable;
import free.chess.ImagePiecePainter;
import free.chess.ChessPiece;
import free.util.IOUtilities;
import free.util.ImageUtilities;


/**
 * An extension of ImagePiecePainter which loads the images by using the
 * Class.getResource() method, allowing you to package piece sets
 * along with your application's jar file.
 */

public class ResourceImagePiecePainter extends ImagePiecePainter{



  /**
   * Creates a new <code>ResourceImagePiecePainter</code> with the given
   * preferred piece image size. The given Hashtable maps Integer objects
   * specifying the size of the piece images to Hashtables which in turn map 
   * Piece objects to piece Images.
   */

  private ResourceImagePiecePainter(Dimension prefSize, Hashtable pieces){
    super(prefSize, pieces);
  }



  /**
   * <P>Creates a new ResourceImagePiecePainter with images loaded by the given
   * class' class loader from the given relative path (relative to the package
   * name of that class) and of the given extension. The
   * ResourceImagePiecePainter will try to read a file named "sizes.txt" from
   * the relative path. That file should contain two lines. The first line
   * must contain a single integer specifying the preferred size. The 2nd line
   * should contain a list of sizes of all the available piece images, separated
   * by spaces. It will then look for directories with names corresponding to 
   * the sizes and in those directories for resources named
   * "<color char><piece char>.<extension>" where <color char> is either
   * 'w' or 'b' (for black or white), <piece char> is one of 'k', 'q', 'r', 'b'
   * 'n' or 'p'. The images for all twelve pieces must be present there and they
   * must have the correct size.
   * <P>For example,
   * <code>new ResourceImagePiecePainter(com.mycompany.Myclass.class, "marble", "gif")</code>
   * will create a ResourceImagePiecePainter which will load "sizes.txt" from
   * com/mycompany/marble/sizes.txt and will then proceed loading images from
   * (for example) com/mycompany/marble/32/wb.gif, com/mycompany/marble/32/br.gif,
   * com/mycompany/marble/64/wq.gif etc.
   */

  public static ResourceImagePiecePainter getInstance(Class c, String relPath,
      String extension) throws IOException{

    Toolkit toolkit = Toolkit.getDefaultToolkit();
    InputStream sizesIn = c.getResourceAsStream(relPath+"/sizes.txt");
    StringTokenizer lines = new StringTokenizer(IOUtilities.loadText(sizesIn), "\n\r");
    int sizeInt = Integer.parseInt(lines.nextToken());
    Dimension prefSize = new Dimension(sizeInt, sizeInt);
    StringTokenizer sizes = new StringTokenizer(lines.nextToken(), " ");
    Hashtable pieceImages = new Hashtable(sizes.countTokens()*5/4);
    Image [] images = new Image[12*sizes.countTokens()];
    int imagesCount = 0;
    while (sizes.hasMoreTokens()){
      Hashtable pieces = new Hashtable(15);
      Integer size = new Integer(sizes.nextToken());

      String dirName = relPath+"/"+size+"/";

      pieces.put(ChessPiece.WHITE_KING, images[imagesCount++] =
        toolkit.getImage(c.getResource(dirName + "wk.gif")));
      pieces.put(ChessPiece.BLACK_KING, images[imagesCount++] = 
        toolkit.getImage(c.getResource(dirName + "bk.gif")));
      pieces.put(ChessPiece.WHITE_QUEEN, images[imagesCount++] =
        toolkit.getImage(c.getResource(dirName + "wq.gif")));
      pieces.put(ChessPiece.BLACK_QUEEN, images[imagesCount++] = 
        toolkit.getImage(c.getResource(dirName + "bq.gif")));
      pieces.put(ChessPiece.WHITE_ROOK, images[imagesCount++] =
        toolkit.getImage(c.getResource(dirName + "wr.gif")));
      pieces.put(ChessPiece.BLACK_ROOK, images[imagesCount++] =
        toolkit.getImage(c.getResource(dirName + "br.gif")));
      pieces.put(ChessPiece.WHITE_BISHOP, images[imagesCount++] = 
        toolkit.getImage(c.getResource(dirName + "wb.gif")));
      pieces.put(ChessPiece.BLACK_BISHOP, images[imagesCount++] = 
        toolkit.getImage(c.getResource(dirName + "bb.gif")));
      pieces.put(ChessPiece.WHITE_KNIGHT, images[imagesCount++] = 
        toolkit.getImage(c.getResource(dirName + "wn.gif")));
      pieces.put(ChessPiece.BLACK_KNIGHT, images[imagesCount++] =
        toolkit.getImage(c.getResource(dirName + "bn.gif")));
      pieces.put(ChessPiece.WHITE_PAWN, images[imagesCount++] =
        toolkit.getImage(c.getResource(dirName + "wp.gif")));
      pieces.put(ChessPiece.BLACK_PAWN, images[imagesCount++] =
        toolkit.getImage(c.getResource(dirName + "bp.gif")));

      pieceImages.put(size, pieces);
    }

    try{
      int [] results = ImageUtilities.preload(images, null);
    } catch (InterruptedException e){}

    return new ResourceImagePiecePainter(prefSize, pieceImages);
  }


}
