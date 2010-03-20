/* $Id$ */

package chess.engine.model;

import chess.engine.search.Searcher;
import chess.engine.search.SimpleEvaluator;

/**
 * @author Joshua Levine <jlevine@theladders.com>
 * @version $Revision$ $Name$ $Date$
 */
public class Move
{
  public boolean check;

  public Square fromSquare;
  public Square toSquare;
  public Square takenSquare;

  public Piece moved;
  public Piece taken;
  public int promoteTo = -1;

  public Piece castledRook;
  public Square castleFromSquare;
  public Square castleToSquare;

  public Square enPassentSquare;

  public int score;

  public Move()
  {
  }

  public static Move[] createMoves(int howMany)
  {
    Move[] moves = new Move[howMany];
    for(int i = 0;i < howMany;i++)
    {
      moves[i] = new Move();
    }

    return moves;
  }

  public void reset()
  {
    this.fromSquare = null;
    this.toSquare = null;
    this.takenSquare = null;
    this.moved = null;
    this.taken = null;
    this.promoteTo = -1;
    this.castledRook = null;
    this.castleFromSquare = null;
    this.castleToSquare = null;
    this.enPassentSquare = null;
    this.score = 0;
    this.check = false;
  }

  public void reset(Move move)
  {
    this.fromSquare = move.fromSquare;
    this.toSquare = move.toSquare;
    this.takenSquare = move.takenSquare;
    this.moved = move.moved;
    this.taken = move.taken;
    this.promoteTo = move.promoteTo;
    this.castledRook = move.castledRook;
    this.castleFromSquare = move.castleFromSquare;
    this.castleToSquare = move.castleToSquare;
    this.enPassentSquare = move.enPassentSquare;
    this.score = move.score;
    this.check = move.check;
  }


  public void reset(Square fromSquare, Square toSquare, Piece moved)
  {
    this.fromSquare = fromSquare;
    this.toSquare = toSquare;
    this.takenSquare = null;
    this.moved = moved;
    this.taken = null;
    this.promoteTo = -1;
    this.castledRook = null;
    this.castleFromSquare = null;
    this.castleToSquare = null;
    this.enPassentSquare = null;
    this.score = SimpleEvaluator.PIECE_VALUE_TABLES[moved.color][moved.type][toSquare.index64] - SimpleEvaluator.PIECE_VALUE_TABLES[moved.color][moved.type][fromSquare.index64];
    this.check = false;
  }

  public void reset(Square fromSquare, Square toSquare, Piece moved, Square enPassentSquare)
  {
    this.fromSquare = fromSquare;
    this.toSquare = toSquare;
    this.takenSquare = null;
    this.moved = moved;
    this.taken = null;
    this.promoteTo = -1;
    this.castledRook = null;
    this.castleFromSquare = null;
    this.castleToSquare = null;
    this.enPassentSquare = enPassentSquare;
    this.score = SimpleEvaluator.PIECE_VALUE_TABLES[moved.color][moved.type][toSquare.index64] - SimpleEvaluator.PIECE_VALUE_TABLES[moved.color][moved.type][fromSquare.index64];
    this.check = false;
  }

  public void reset(Square fromSquare, Square toSquare, Square takenSquare, Piece moved, Piece taken)
  {
/*
    if(fromSquare == null || toSquare == null)
    {
      int x = 1;
    }
*/

    this.fromSquare = fromSquare;
    this.toSquare = toSquare;
    this.takenSquare = takenSquare;
    this.moved = moved;
    this.taken = taken;
    this.promoteTo = -1;
    this.castledRook = null;
    this.castleFromSquare = null;
    this.castleToSquare = null;
    this.enPassentSquare = null;
    this.score = 40000 + (Piece.TYPE_VALUES[taken.type] - Piece.TYPE_VALUES[moved.type]);
    this.check = false;
  }

  public void reset(Square fromSquare, Square toSquare, Piece moved, Piece castledRook, Square castleFromSquare, Square castleToSquare)
  {
    this.fromSquare = fromSquare;
    this.toSquare = toSquare;
    this.takenSquare = null;
    this.moved = moved;
    this.taken = null;
    this.promoteTo = -1;
    this.castledRook = castledRook;
    this.castleFromSquare = castleFromSquare;
    this.castleToSquare = castleToSquare;
    this.enPassentSquare = null;
    this.score = 500;
    this.check = false;
  }

  public void reset(Square fromSquare, Square toSquare, Square takenSquare, Piece moved, Piece taken, Piece castledRook, Square castleFromSquare, Square castleToSquare, Square enPassentSquare)
  {
    this.fromSquare = fromSquare;
    this.toSquare = toSquare;
    this.takenSquare = takenSquare;
    this.moved = moved;
    this.taken = taken;
    this.promoteTo = -1;
    this.castledRook = castledRook;
    this.castleFromSquare = castleFromSquare;
    this.castleToSquare = castleToSquare;
    this.enPassentSquare = enPassentSquare;
    this.score = 500;
    this.check = false;
  }

  public void reset(Square fromSquare, Square toSquare, Piece moved, int promoteTo)
  {
    this.fromSquare = fromSquare;
    this.toSquare = toSquare;
    this.takenSquare = null;
    this.moved = moved;
    this.taken = null;
    this.promoteTo = promoteTo;
    this.castledRook = null;
    this.castleFromSquare = null;
    this.castleToSquare = null;
    this.enPassentSquare = null;
    this.score = 50000 + Piece.TYPE_VALUES[promoteTo];
    this.check = false;
  }

  public void reset(Square fromSquare, Square toSquare, Piece moved, Square takenSquare, Piece taken, int promoteTo)
  {
    this.fromSquare = fromSquare;
    this.toSquare = toSquare;
    this.takenSquare = takenSquare;
    this.moved = moved;
    this.taken = taken;
    this.promoteTo = promoteTo;
    this.castledRook = null;
    this.castleFromSquare = null;
    this.castleToSquare = null;
    this.enPassentSquare = null;
    this.score = 55000 + Piece.TYPE_VALUES[promoteTo];
    this.check = false;
  }

  public String toString()
  {
    if(moved == null)
    {
      return "null";
    }

    if(castledRook != null)
    {
      if(toSquare.file > Constants.FILE_E)
      {
        return moved.color == 1 ? "O-O" : "o-o";
      }
      else {
        return moved.color == 1 ? "O-O-O" : "o-o-o";
      }
    }

    String pieceString = moved.toString().toUpperCase();
    if(moved.type == Piece.PAWN)
    {
      if(taken != null){
        pieceString = Constants.FILE_STRINGS[fromSquare.file];
      }
      else {
        pieceString = "";
      }
    }


    return pieceString
         + (taken != null ? "x" : "")
         + toSquare.toString().toLowerCase()
         + (promoteTo != -1 ? "=" + Piece.toString(promoteTo, moved.color) : ""
         + (check ? "+" : "")
         /*+ " (" + score + ")"*/);
  }

  public String toFICSString()
  {
    if(moved == null)
    {
      return "null move";
    }

    return fromSquare.toString().toLowerCase()
         + "-"
         + toSquare.toString().toLowerCase()
         + (promoteTo != -1 ? "=" + Piece.toString(promoteTo, moved.color) : "");
  }

  public static void nextMove(Move[] moves, int moveIndex)
  {
    int bestIndex = -1;
    int best = -Searcher.INFINITY;

    for(int index = moveIndex;moves[index].moved != null;index++)
    {
      if(moves[index].score > best)
      {

        bestIndex = index;
        best = moves[index].score;
      }
    }
    if(bestIndex > -1)
    {
      Move temp = moves[bestIndex];
      moves[bestIndex] = moves[moveIndex];
      moves[moveIndex] = temp;
    }
  }

  public static String toString(Move[] moves)
  {
    return Move.toString(moves, 0);
  }
  public static String toString(Move[] moves, int startIndex)
  {
    StringBuffer buffer = new StringBuffer();
    int count = 1;
    for(Move move : moves)
    {
      if (count < startIndex + 1)
      {
        count++;
        continue;
      }
      if(move.moved == null)
      {
        if(Math.abs(move.score) > Searcher.MATE - 300 && Math.abs(move.score) < Searcher.MATE)
        {
          buffer.append(" #");
        }
        break;
      }
      if(buffer.length() > 0)
      {
        buffer.append(" ");
        if(move.moved.color == 1)
        {
          buffer.append((count++) + ". ");
        }
      }
      else
      {
        // first move
        if(move.moved.color == 1)
        {
          buffer.append((count++) + ". ");
        }
        else
        {
          buffer.append((count++) + ". ... ");
        }
      }


      buffer.append(move);
    }
    return buffer.toString();
  }

  public boolean matches(Move move)
  {
    return move != null &&
           moved == move.moved &&
           fromSquare == move.fromSquare &&
           toSquare == move.toSquare &&
           taken == move.taken &&
           promoteTo == move.promoteTo;
  }

  // castling squares are weeeaaak...
  // 1 + 6 + 6 + 7 + 7 + 7 + + 7 + 7 + 7 + 6 + 4 + 6(score) 
  // capture, movedIndex, takenIndex, from, to, takenSquare, enPassentSquare, castleFromSqaure, castleToSquare, castledRook, promoteTo


/*
  public static int SHIFT_1024 = 11;
  public static int SHIFT_128 = 8;
  public static int SHIFT_64 = 7;
  public static int SHIFT_32 = 6;
  public static int SHIFT_16 = 5;
  public static int SHIFT_8 = 4;
  public static int SHIFT_4 = 3;
  public static int SHIFT_2 = 2;
  public static int SHIFT_1 = 1;

  public static int TO_SHIFT = SHIFT_128;
  public static int SQUARE_SHIFT = SHIFT_128 + TO_SHIFT;
  public static int MOVED_SHIFT = SHIFT_128 + SQUARE_SHIFT;
  public static int EP_SQUARE_SHIFT = SHIFT_32 + MOVED_SHIFT;
  public static int PROMOTE_TYPE_SHIFT = SHIFT_128 + EP_SQUARE_SHIFT;
  public static int CASTLED_ROOK_SHIFT = SHIFT_8 + PROMOTE_TYPE_SHIFT;
  public static int SCORE_SHIFT = SHIFT_1024 + PROMOTE_TYPE_SHIFT;

  public static long FROM_MASK = 128 - 1;
  public static long TO_MASK = (128 - 1) << TO_SHIFT;
  public static long SQUARE_MASK = (64 - 1) << SQUARE_SHIFT;
  public static long MOVED_MASK = (32 - 1) << MOVED_SHIFT;
  public static long EP_SQUARE_MASK = (64 - 1) << EP_SQUARE_SHIFT;
  public static long PROMOTE_TYPE_MASK = (8 - 1) << PROMOTE_TYPE_SHIFT;
  public static long CASTLED_ROOK_MASK = (32 - 1) << CASTLED_ROOK_SHIFT;
  public static long SCORE_MASK = (1024 - 1) << SCORE_SHIFT;


  public long create(Square fromSquare, Square toSquare, Piece moved)
  {
    return fromSquare.index128 |
           (toSquare.index128 << TO_SHIFT) |
           (moved.index128 << MOVED_SHIFT);
  }

  public long create(Square fromSquare, Square toSquare, Piece moved, Square enPassentSquare)
  {
    return fromSquare.index128 |
           (toSquare.index128 << TO_SHIFT) |
           (moved.index128 << MOVED_SHIFT) |
           (enPassentSquare.index128 << EP_SQUARE_SHIFT);
  }

  public long create(Square fromSquare, Square toSquare, Square takenSquare, Piece moved, Piece taken)
  {
    return fromSquare.index128 |
           (toSquare.index128 << TO_SHIFT) |
           (moved.index128 << MOVED_SHIFT) |
           (takenSquare.index128 << ) |

    return create(fromSquare, toSquare,  takenSquare, moved, taken, null, null, null, null);
  }

  public long create(Square fromSquare, Square toSquare, Piece moved, Piece castledRook, Square castleFromSquare, Square castleToSquare)
  {
    return create(fromSquare, toSquare, null, moved, null, castledRook, castleFromSquare, castleToSquare, null);
  }

  public long create(Square fromSquare, Square toSquare, Square takenSquare, Piece moved, Piece taken, Piece castledRook, Square castleFromSquare, Square castleToSquare, Square enPassentSquare)
  {
    this.fromSquare = fromSquare;
    this.toSquare = toSquare;
    this.takenSquare = takenSquare;
    this.moved = moved;
    this.taken = taken;
    this.castledRook = castledRook;
    this.castleFromSquare = castleFromSquare;
    this.castleToSquare = castleToSquare;
    this.enPassentSquare = enPassentSquare;

    if(taken != null) score += taken.color == 1 ? taken.value : -taken.value;
    if(promoteTo != -1) score += moved.color == 1 ? promoteTo * 100 : promoteTo * -100;
    if(castledRook != null) score += 200;
  }

  public long create(Square fromSquare, Square toSquare, Piece moved, int promoteTo)
  {
    this.fromSquare = fromSquare;
    this.toSquare = toSquare;
    this.moved = moved;
    this.promoteTo = promoteTo;
  }

  public long create(Square fromSquare, Square toSquare, Piece moved, Piece taken, int promoteTo)
  {
    this.fromSquare = fromSquare;
    this.toSquare = toSquare;
    this.takenSquare = toSquare;
    this.moved = moved;
    this.taken = taken;
    this.promoteTo = promoteTo;
  }

*/

}
