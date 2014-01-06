/* $Id$ */

package chess.engine.model;

import chess.engine.search.ABSearch;
import chess.engine.search.Searcher;
import chess.engine.search.SimpleEvaluator;

/**
 * @author Joshua Levine <jlevine@theladders.com>
 * @version $Revision$ $Name$ $Date$
 */
public final class Move
{
  public static final int CAPTURE_SCORE = (ABSearch.INFINITY - 40000);
  public static final int PROMOTE_SCORE = (ABSearch.INFINITY - 55000);
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
    this.score = CAPTURE_SCORE + (Piece.TYPE_VALUES[taken.type] - Piece.TYPE_VALUES[moved.type]);
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
    this.score = PROMOTE_SCORE + Piece.TYPE_VALUES[promoteTo];
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
    this.score = PROMOTE_SCORE + Piece.TYPE_VALUES[promoteTo];
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


    return new StringBuffer(pieceString)
         .append(taken != null ? "x" : "")
         .append(toSquare.toString().toLowerCase())
         .append(promoteTo != -1 ? "=" + Piece.toString(promoteTo, moved.color) : "")
         .append(check ? "+" : "").toString()
         /*+ " (" + score + ")"*/;
  }

  public String toFICSString()
  {
    if(moved == null)
    {
      return "null move";
    }

    return new StringBuilder(fromSquare.toString().toLowerCase())
         .append("-")
         .append(toSquare.toString().toLowerCase())
         .append((promoteTo != -1 ? "=" + Piece.toString(promoteTo, moved.color) : "")).toString();
  }

  public static String toString(Move[] moves)
  {
    return Move.toString(moves, 0);
  }
  public static String toString(Move[] moves, int startIndex)
  {
    StringBuilder buffer = new StringBuilder();
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
          buffer.append((count++)).append(". ");
        }
      }
      else
      {
        // first move
        if(move.moved.color == 1)
        {
          buffer.append((count++)).append(". ");
        }
        else
        {
          buffer.append((count++)).append(". ... ");
        }
      }


      buffer.append(move);
    }
    return buffer.toString();
  }

  public boolean matches(Move move)
  {
    return moved == move.moved &&
           fromSquare == move.fromSquare &&
           toSquare == move.toSquare &&
           taken == move.taken &&
           promoteTo == move.promoteTo;
  }

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
  public static int TAKEN_SQUARE_SHIFT = SHIFT_32 + TO_SHIFT;
  public static int MOVED_SHIFT = SHIFT_32 + TAKEN_SQUARE_SHIFT;
  public static int TAKEN_SHIFT = SHIFT_32 + MOVED_SHIFT;
  public static int PROMOTE_TO_SHIFT = SHIFT_128 + TAKEN_SQUARE_SHIFT;
  public static int SCORE_SHIFT = SHIFT_1024 + PROMOTE_TO_SHIFT;

  public static int FROM_MASK = TO_SHIFT - 1;
  public static int TO_MASK = TO_SHIFT - 1;
  public static int TAKEN_SQUARE_MASK = TAKEN_SQUARE_SHIFT - 1;
  public static int MOVED_MASK = MOVED_SHIFT - 1;
  public static int TAKEN_MASK = TAKEN_SHIFT - 1;
  public static int PROMOTE_TO_MASK = PROMOTE_TO_SHIFT - 1;
  public static int SCORE_MASK = SCORE_SHIFT - 1;

  public static long create(Square fromSquare, Square toSquare, Piece moved)
  {
    return fromSquare.index128 |
           (toSquare.index128 << TO_SHIFT) |
           (moved.index << MOVED_SHIFT) |
           (SimpleEvaluator.PIECE_VALUE_TABLES[moved.color][moved.type][toSquare.index64] - SimpleEvaluator.PIECE_VALUE_TABLES[moved.color][moved.type][fromSquare.index64]) << SCORE_SHIFT;
  }

  public static long create(Square fromSquare, Square toSquare, Piece moved, int promoteTo)
  {
    return fromSquare.index128 |
           (toSquare.index128 << TO_SHIFT) |
           (moved.index << MOVED_SHIFT) |
           (promoteTo << PROMOTE_TO_SHIFT) |
           (PROMOTE_SCORE + (Piece.TYPE_VALUES[promoteTo])) << SCORE_SHIFT;
  }

  public static long create(Square fromSquare, Square toSquare, Square takenSquare, Piece moved, Piece taken)
  {
    return fromSquare.index128 |
           (toSquare.index128 << TO_SHIFT) |
           (takenSquare.index128 << TAKEN_SQUARE_SHIFT) |
           (moved.index << MOVED_SHIFT) |
           (taken.index << TAKEN_SHIFT) |
           (CAPTURE_SCORE + (Piece.TYPE_VALUES[taken.type] - Piece.TYPE_VALUES[moved.type])) << SCORE_SHIFT;
  }

  public static long create(Square fromSquare, Square toSquare, Square takenSquare, Piece moved, Piece taken, int promoteTo)
  {
    return fromSquare.index128 |
           (toSquare.index128 << TO_SHIFT) |
           (takenSquare.index128 << TAKEN_SQUARE_SHIFT) |
           (moved.index << MOVED_SHIFT) |
           (taken.index << TAKEN_SHIFT) |
           (promoteTo << PROMOTE_TO_SHIFT) |
           (PROMOTE_SCORE + (Piece.TYPE_VALUES[promoteTo])) << SCORE_SHIFT;
  }

*/
}
