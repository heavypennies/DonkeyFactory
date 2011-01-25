/* $Id$ */

package chess.engine.model;

import chess.engine.utils.MoveGeneration;
import chess.engine.search.PositionHashtable;
import chess.engine.search.BoardEvaluator;

import java.util.*;
import java.io.*;

/**
 * TODO implement clone
 *
 *
 * @author Joshua Levine <jlevine@theladders.com>
 * @version $Revision$ $Name$ $Date$
 */
public class Board
{
  public Piece whiteKing;
  public Piece blackKing;

  // moveIndex is incremented and decremented with each call to make or unmake respectively.
  public int moveIndex;

  // true means its white's turn
  public int turn = 1;

  // the hash of this board
  public long hash1;
  public long pawnHash;

  // The stats contain information on castling,
  // as well as information kept incrementally for use in the evaluation
  public Stats stats = new Stats();

  public MoveStack moveStack = new MoveStack();

  public static final Square[] SQUARES = Square.values();
  public final BoardSquare[] boardSquares = new BoardSquare[128];
  public Piece[] pieces = new Piece[32];
  public int[] attacks = new int[64];  

  // indexed thusly:
  public static int ALL_PIECES = 6;
  public static int ALL_PIECES_R_45 = 7;
  public static int ALL_PIECES_L_45 = 8;
  public static int ALL_PIECES_R_90 = 9;

  /** <code>   0     1       0      1      2      3     4      5     6<br>
   * [black,white] [pawn, knight, bishop, rook, queen, king, all</code>
   */
  public long[][] pieceBoards = new long[2][8];

  public long allPieces = 0;
  public long allPiecesRL90 = 0;
  public long allPiecesRL45 = 0;
  public long allPiecesRR45 = 0;

  public int materialScore = 0;
  public int positionScore = 0;

  // keeps board hashes for draw by rep
  public long[] repetitionTable = new long[500];



  public long[] bishopAttacks = new long[64];
  public long[] rookAttacks = new long[64];

  private static Random random = new Random();


  private static long[][] pieceHash = new long[16][64];
  static
  {
    File zobristData = new File("hashKeys.dat");

    if(true || !zobristData.exists())
    {
      Random random = new Random(124353460892475679L);
      for(int pieceType = 0;pieceType < 12;pieceType++)
      {
        for(int squareIndex = 0;squareIndex < 64;squareIndex++)
        {
          pieceHash[pieceType][squareIndex] = random.nextLong();
        }
      }

      outer: while(true)
      {
        for(int pieceType = 0;pieceType < 12;pieceType++)
        {
          for(int squareIndex = 0;squareIndex < 64;squareIndex++)
          {
            for(int pieceType2 = 0;pieceType2 < 12;pieceType2++)
            {
              for(int squareIndex2 = 0;squareIndex2 < 64;squareIndex2++)
              {
                if((pieceType != pieceType2 || squareIndex != squareIndex2) &&
                  (((pieceHash[pieceType][squareIndex] & PositionHashtable.HASH_MASK) == (PositionHashtable.HASH_MASK)) ||
                   (pieceHash[pieceType][squareIndex] & PositionHashtable.HASH_MASK) == 0)
                   )
                {
                  pieceHash[pieceType][squareIndex] = random.nextLong();
                  System.out.println("Bad zobrist...");
                  continue outer;
                }
              }
            }
          }
        }
        writeZobristData(zobristData);
        break;
      }
    }

    try
    {
      System.out.println("Reading hash key data: ");
      ObjectInputStream zobristDataInputStream = new ObjectInputStream(new FileInputStream(zobristData));
      pieceHash = (long[][])zobristDataInputStream.readObject();


      outer: while(true)
      {
        for(int pieceType = 0;pieceType < 12;pieceType++)
        {
          for(int squareIndex = 0;squareIndex < 64;squareIndex++)
          {
            for(int pieceType2 = 0;pieceType2 < 12;pieceType2++)
            {
              for(int squareIndex2 = 0;squareIndex2 < 64;squareIndex2++)
              {
                if((pieceType != pieceType2 || squareIndex != squareIndex2) &&
                  (((pieceHash[pieceType][squareIndex] & PositionHashtable.HASH_MASK) == (PositionHashtable.HASH_MASK)) ||
                   (pieceHash[pieceType][squareIndex] & PositionHashtable.HASH_MASK) == 0)
                   )
                {
                  pieceHash[pieceType][squareIndex] = random.nextLong();
                  System.out.println("Bad zobrist...");
                  continue outer;
                }
              }
            }
          }
        }
        break;
      }
    }
    catch (IOException e)
    {
      System.err.println("Unable to read hash key data: ");
      e.printStackTrace();
      System.exit(0);
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("Unable to read hash key data: ");
      e.printStackTrace();
      System.exit(0);
    }
    
  }

  private static void writeZobristData(File zobristData)
  {
    try
        {
          System.err.println("Writing hash key data");
      ObjectOutputStream zobristDataOutputStream = new ObjectOutputStream(new FileOutputStream(zobristData));
      zobristDataOutputStream.writeObject(pieceHash);
    }
    catch (IOException e)
    {
      System.err.println("Unable to write hash key data");
      e.printStackTrace();
      System.exit(0);
    }
  }

  public Board()
  {
    hash1 = 0;
    pawnHash = 0;
    for(Square square : Square.values())
    {
      boardSquares[square.index128] = new BoardSquare(square);
    }

    for(int t = 0;t < 32;t++)
    {
      pieces[t] = new Piece(t, this, 0, 0, Square.A1);
      removePieceFromSquare(pieces[t], Square.A1);
    }
    stats = new Stats();
  }

  public Board(String epd)
  {
    this();

    // r3rnk1/4qpp1/p5np/4pQ2/Pb2N3/1B5P/1P3PP1/R1BR2K1 w

    setEPDPosition(epd);
  }

  public void setEPDPosition(String epd) {

    for(int color = 0;color < 2;color++)
    {
      for(int pieceType = 0;pieceType < 7; pieceType++)
      {
        pieceBoards[color][pieceType] = 0;
      }
    }

    int epdIndex = 0;
    int pieceIndex = 0;
    rank: for(int rank = 7;rank >= 0; rank--)
    {
      for(int file = 0; file < 8;file++)
      {
        char ch = epd.charAt(epdIndex++);
        if(ch == '/')
        {
          ch = epd.charAt(epdIndex++);
        }
        if(epdIndex > 64)
        {
          throw new RuntimeException("unable to parse epd: " + epd);
        }

        switch(ch)
        {
          case 'p' :
          {
            new Piece(pieceIndex++, this, 0, Piece.PAWN, SQUARES[(rank * 8) + file]);
            break;
          }
          case 'n' :
          {
            new Piece(pieceIndex++, this, 0, Piece.KNIGHT, SQUARES[(rank * 8) + file]);
            break;
          }
          case 'b' :
          {
            new Piece(pieceIndex++, this, 0, Piece.BISHOP, SQUARES[(rank * 8) + file]);
            break;
          }
          case 'r' :
          {
            new Piece(pieceIndex++, this, 0, Piece.ROOK, SQUARES[(rank * 8) + file]);
            break;
          }
          case 'q' :
          {
            new Piece(pieceIndex++, this, 0, Piece.QUEEN, SQUARES[(rank * 8) + file]);
            break;
          }
          case 'k' :
          {
            new Piece(pieceIndex++, this, 0, Piece.KING, SQUARES[(rank * 8) + file]);
            break;
          }

          case 'P' :
          {
            new Piece(pieceIndex++, this, 1, Piece.PAWN, SQUARES[(rank * 8) + file]);
            break;
          }
          case 'N' :
          {
            new Piece(pieceIndex++, this, 1, Piece.KNIGHT, SQUARES[(rank * 8) + file]);
            break;
          }
          case 'B' :
          {
            new Piece(pieceIndex++, this, 1, Piece.BISHOP, SQUARES[(rank * 8) + file]);
            break;
          }
          case 'R' :
          {
            new Piece(pieceIndex++, this, 1, Piece.ROOK, SQUARES[(rank * 8) + file]);
            break;
          }
          case 'Q' :
          {
            new Piece(pieceIndex++, this, 1, Piece.QUEEN, SQUARES[(rank * 8) + file]);
            break;
          }
          case 'K' :
          {
            new Piece(pieceIndex++, this, 1, Piece.KING, SQUARES[(rank * 8) + file]);
            break;
          }
          default :
          {
            file += Integer.valueOf("" + ch) - 1;
          }
        }
      }
    }
  }

  public Piece getPieceOnSquare(Square square)
  {
    return boardSquares[square.index128].piece;
  }

  public final void setPieceOnSquare(final Piece piece, final Square square)
  {
    boardSquares[square.index128].piece = piece;
    piece.square = square;


    allPieces |= square.mask_on;
    allPiecesRL90 |= square.mask_on_rl90;
    allPiecesRR45 |= square.mask_on_rr45;
    allPiecesRL45 |= square.mask_on_rl45;

    pieceBoards[piece.color][ALL_PIECES] |= square.mask_on;
    pieceBoards[piece.color][piece.type] |= square.mask_on;

    hash1 ^= pieceHash[piece.type + (piece.color == 1 ? 0 : 6)][square.index64];
    if(piece.type == Piece.PAWN/* || piece.type == Piece.KING*/)
    {
      pawnHash ^= pieceHash[piece.type + (piece.color == 1 ? 0 : 6)][square.index64];
    }

    positionScore += piece.color == 1 ? BoardEvaluator.PIECE_VALUE_TABLES[piece.color][piece.type][square.index64] : -BoardEvaluator.PIECE_VALUE_TABLES[piece.color][piece.type][square.index64];
    materialScore += piece.value;
  }


  public final void removePieceFromSquare(final Piece piece, final Square square)
  {
    hash1 ^= pieceHash[piece.type + (piece.color == 1 ? 0 : 6)][square.index64];
    if(piece.type == Piece.PAWN/* || piece.type == Piece.KING*/)
    {
      pawnHash ^= pieceHash[piece.type + (piece.color == 1 ? 0 : 6)][square.index64];
    }

    allPieces &= square.mask_off;
    allPiecesRL90 &= square.mask_off_rl90;
    allPiecesRR45 &= square.mask_off_rr45;
    allPiecesRL45 &= square.mask_off_rl45;
    
    pieceBoards[piece.color][ALL_PIECES] &= square.mask_off;
    pieceBoards[piece.color][piece.type] &= square.mask_off;

    piece.square = null;
    boardSquares[square.index128].piece = null;

    positionScore -= piece.color == 1 ? BoardEvaluator.PIECE_VALUE_TABLES[piece.color][piece.type][square.index64] : -BoardEvaluator.PIECE_VALUE_TABLES[piece.color][piece.type][square.index64];
    materialScore -= piece.value;
  }


  public BoardSquare getSquare(int index)
  {
    if((index & 0x88) == 0)
    {
      return boardSquares[index];
    }

    return null;
  }

  public boolean isPieceOnSquare(Square square)
  {
    return (allPieces & square.mask_on) != 0;
  }

  public boolean isWhitePieceOnSquare(Square square)
  {
    return (pieceBoards[0][ALL_PIECES] & square.mask_on) != 0;
  }

  public boolean isBlackPieceOnSquare(Square square)
  {
    return (pieceBoards[1][ALL_PIECES] & square.mask_on) != 0;
  }

  public class BoardSquare
  {
    public Square square;
    public Piece piece;

    // Maps the 'pawn up by two' move index128 to a yes or no question.
    // The question: "On this move, does this square accept en passent captures?"
    // This is formed as: if(square.enPassentInfo.get(board.moveIndex))
    public boolean[] enPassentInfo = new boolean[1000];

    public BoardSquare(Square square)
    {
      this.square = square;

      // Since this is mapping to moveIndex, we make it large.
      // It should never have to grow, and never return null.
      for(int i = 0;i < 1000;i++)
      {
        enPassentInfo[i] = false;
      }
    }

    public Piece getPiece()
    {
      return piece;
    }


    public void setPiece(Piece piece)
    {
      this.piece = piece;
    }

    public String toString()
    {
      return square.toString() + "*";
    }
  }

  class MoveStack
  {
    private Stack<Move> stack = new Stack<Move>();

    public MoveStack()
    {
    }


    public Move pop()
    {
      Move move = stack.pop();

      unmake(move);

      return move;
    }


    public Move push(Move move)
    {
      make(move);
      return stack.push(move);
    }

  }

  public final void make(Move move)
  {
    // hash move

    // increase piece's move count
    move.moved.moveCount++;

    // make capture
    if(move.taken != null)
    {
/*
      if(move.taken.type == Piece.KING)
      {
        System.err.println("Board: " + this);
        System.err.println("Move: " + move);
        isSquareCheckedByColor(whiteKing.square, 0);
        isSquareCheckedByColor(blackKing.square, 1);
      }
*/
      // remove taken piece
      removePieceFromSquare(move.taken, move.takenSquare);
    }

    // remove moving piece
    removePieceFromSquare(move.moved, move.fromSquare);

    // make promote
    if(move.promoteTo != -1)
    {
      move.moved.type = move.promoteTo;
      move.moved.value = move.moved.getValue();
      move.moved.materialValue = move.moved.getMaterialValue();
      move.moved.moveCount = 0;
    }

    // set moving piece
    setPieceOnSquare(move.moved, move.toSquare);

    // Update Stats
    if(move.moved.type == Piece.KING)
    {
      if(move.moved.color == 1)
      {
        stats.whiteKingMoves++;
      }
      else
      {
        stats.blackKingMoves++;
      }

      // make castling
      if(move.castledRook != null)
      {
        if(move.toSquare.file > Constants.FILE_E)
        {
          if(move.moved.color == 1)
          {
            stats.whiteCastleFlag = Stats.O_O;
          }
          else {
            stats.blackCastleFlag = Stats.O_O;
          }
        }
        else {
          if(move.moved.color == 1)
          {
            stats.whiteCastleFlag = Stats.O_O_O;
          }
          else {
            stats.blackCastleFlag = Stats.O_O_O;
          }
        }
        removePieceFromSquare(move.castledRook, move.castleFromSquare);
        setPieceOnSquare(move.castledRook, move.castleToSquare);
      }
    }
    else if(move.moved.type == Piece.ROOK)
    {
      if(move.moved.color == 1)
      {
        if(move.moved.kingsideRook)
        {
          stats.whiteKingsideRookMoves++;
        }
        else if(move.moved.queensideRook)
        {
          stats.whiteQueensideRookMoves++;
        }
      }
      else {
        if(move.moved.kingsideRook)
        {
          stats.blackKingsideRookMoves++;
        }
        else if(move.moved.queensideRook)
        {
          stats.blackQueensideRookMoves++;
        }
      }
    }

    if(move.moved.color == 1)
    {
      stats.whitePieceMoves[move.moved.type]++;
    }
    else {
      stats.blackPieceMoves[move.moved.type]++;
    }

    turn = turn ^ 1;
    moveIndex++;

    // set en passent
    if(move.enPassentSquare != null)
    {
      boardSquares[move.enPassentSquare.index128].enPassentInfo[moveIndex] = true;
    }
    repetitionTable[moveIndex] = turn == 1 ? hash1 : ~hash1;

  }

  public final void unmake(Move move)
  {
    repetitionTable[moveIndex] = 0;

    // decrease piece's move count
    move.moved.moveCount--;

    // Rollback stats
    if(move.moved.type == Piece.KING)
    {
      if(move.moved.color == 1)
      {
        stats.whiteKingMoves--;
      }
      else
      {
        stats.blackKingMoves--;
      }
      // unmake castling
      if(move.castledRook != null)
      {
        removePieceFromSquare(move.castledRook, move.castleToSquare);
        setPieceOnSquare(move.castledRook, move.castleFromSquare);

        if(move.moved.color == 1)
        {
          stats.whiteCastleFlag = 0;
        }
        else {
          stats.blackCastleFlag = 0;
        }
      }
    }
    else if(move.moved.type == Piece.ROOK)
    {
      if(move.moved.color == 1)
      {
        if(move.moved.kingsideRook)
        {
          stats.whiteKingsideRookMoves--;
        }
        else if(move.moved.queensideRook)
        {
          stats.whiteQueensideRookMoves--;
        }
      }
      else {
        if(move.moved.kingsideRook)
        {
          stats.blackKingsideRookMoves--;
        }
        else if(move.moved.queensideRook)
        {
          stats.blackQueensideRookMoves--;
        }
      }
    }

    if(move.moved.color == 1)
    {
      stats.whitePieceMoves[move.moved.type]--;
    }
    else {
      stats.blackPieceMoves[move.moved.type]--;
    }

    // unmake move
    removePieceFromSquare(move.moved, move.toSquare);

    // unmake promote
    if(move.promoteTo != -1)
    {
      move.moved.type = Piece.PAWN;
      move.moved.value = move.moved.getValue();
      move.moved.materialValue = move.moved.getMaterialValue();
    }

    setPieceOnSquare(move.moved, move.fromSquare);

    // unmake capture
    if(move.taken != null)
    {
      setPieceOnSquare(move.taken, move.takenSquare);
    }


    // unset en passent
    if(move.enPassentSquare != null)
    {
      boardSquares[move.enPassentSquare.index128].enPassentInfo[moveIndex] = false;
    }

    turn = turn ^ 1;
    moveIndex--;
  }

  public static class Stats
  {
    public static int O_O = 1;
    public static int O_O_O = 2;

    public int whiteKingMoves = 0;
    public int blackKingMoves = 0;

    public int whiteCastleFlag = 0;
    public int blackCastleFlag = 0;

    public int whiteKingsideRookMoves = 0;
    public int whiteQueensideRookMoves = 0;
    public int blackKingsideRookMoves = 0;
    public int blackQueensideRookMoves = 0;

    public boolean whiteAttacking = false;
    public boolean blackAttacking = false;

    public int[] whitePieceMoves = new int[6];
    public int[] blackPieceMoves = new int[6];

    // Reset by search
    public int originalMaterial = 0;
    public int originalMaterialDifference = 0;

    public int originalPawns = 0;
    public int originalPawnsDifference = 0;

    public String toString()
    {
      return "Stats: castling (" + (whiteCastleFlag == 1 ? "O-O" : (whiteCastleFlag == 2 ? "O-O-O" : (whiteKingsideRookMoves == 0 && whiteQueensideRookMoves == 0 && whitePieceMoves[Piece.KING] == 0 ? "WAIT" : "CEN"))) + " v " + (blackCastleFlag == 1 ? "O-O" : (blackCastleFlag == 2 ? "O-O-O" : (blackKingsideRookMoves == 0 && blackQueensideRookMoves == 0 && blackPieceMoves[Piece.KING] == 0 ? "WAIT" : "CEN"))) + ")" +
              "\n  whitePieceMoves: " + whitePieceMoves + "\n" +
              "\n  whitePieceMoves: " + blackPieceMoves + "\n"; 
    }
  }

  public String toString()
  {
    StringBuffer out = new StringBuffer();
    out.append("\n---------------------------------\n");
    for(int rank = 7;rank > -1;rank--)
    {
      out.append("|");
      for(int file = 0; file < 8;file++)
      {
        BoardSquare square = boardSquares[(rank * 16) + file];
        if(square.getPiece() != null)
        {
          out.append(" ").append(square.getPiece().toString()).append(" |");
        }
        else {
          out.append("   |");
        }
      }
      out.append("\n");
      out.append("---------------------------------\n");
    }

    return out.toString();
  }

  public long getHash()
  {
    return turn == 1 ? hash1 : ~hash1;
  }

  public boolean isApproachingDraw()
  {
    long hash = turn == 1 ? hash1 : ~hash1;
    for(int t = moveIndex - 2; t > moveIndex - 32 && t > -1;t -= 2)
    {
      if(repetitionTable[t] == hash)
      {
        return true;
      }
    }
    return false;
  }

  public boolean isDraw()
  {
    int hits = 0;
    long hash = getHash();
    for(int t = moveIndex - 2; t > moveIndex - 64 && t > -1;t -= 2)
    {
      if(repetitionTable[t] == hash)
      {
        hits++;
      }
    }
    return hits > 1;
  }

  public static String translateSquares128(List<Integer> squares)
  {
    StringBuffer out = new StringBuffer();
    for(int squareIndex : squares)
    {
      out.append(Board.SQUARES[squareIndex]).append(" ");
    }

    return out.toString();
  }

  public List<Square> getAllSquaresInBitboard(long bitboard)
  {
    List<Square> index = new ArrayList<Square>();
    while(bitboard != 0)
    {
      int squareIndex = Board.getLeastSignificantBit(bitboard);

      Square fromSquare = SQUARES[squareIndex];
      index.add(fromSquare);

      bitboard &= fromSquare.mask_off;

    }

    return index;
  }


  public int getAllSquaresInBitboard(long bitboard, int[] squares)
  {
    int index = 0;
    while(bitboard != 0)
    {
      int squareIndex = Board.getLeastSignificantBit(bitboard);
      squares[index++] = squareIndex;
      bitboard &= Board.SQUARES[squareIndex].mask_off;
    }

    return index;
  }

  static final long debruijn64 = 0x07EDD5E59A4E28C2L;


  // Get the index of a set bit
  public static int getLeastSignificantBit(long board)
  {
//    return index64[(int)(((board & -board) * debruijn64) >>> 58)];
/*
    int fold = ((int) (board ^ (board - 1))) ^ ((int) ((board ^ (board - 1)) >>> 32));
    return lsz64_tbl[(fold * 0x78291ACF) >>> (32 - 6)];
*/

    if ((board >>> 48) != 0)
    {
      return (MoveGeneration.first_one[(int)(board >>> 48)] + 48);
    }
    if (((board >>> 32) & 65535) != 0)
    {
      return (MoveGeneration.first_one[(int)(board >>> 32) & 65535] + 32);
    }
    if (((board >>> 16) & 65535) != 0)
    {
      return (MoveGeneration.first_one[(int)(board >>> 16) & 65535] + 16);
    }
    return (MoveGeneration.first_one[(int)(board & 65535)]);
  }


  private static final int[] index64 = {
     63,  0, 58,  1, 59, 47, 53,  2,
     60, 39, 48, 27, 54, 33, 42,  3,
     61, 51, 37, 40, 49, 18, 28, 20,
     55, 30, 34, 11, 43, 14, 22,  4,
     62, 57, 46, 52, 38, 26, 32, 41,
     50, 36, 17, 19, 29, 10, 13, 21,
     56, 45, 25, 31, 35, 16,  9, 12,
     44, 24, 15,  8, 23,  7,  6,  5
  };

  /**
   * bitScanForward
   * @author Charles E. Leiserson
   *         Harald Prokop
   *         Keith H. Randall
   * "Using de Bruijn Sequences to Index a 1 in a Computer Word"
   * @param bb bitboard to scan
   * @precondition bb != 0
   * @return index (0..63) of least significant one bit
   */
  int bitScanForward(long bb) {
     assert (bb != 0);
     return index64[(int)((bb & -bb) * debruijn64) >>> 58];
  }


  public static int[] lsz64_tbl = {
          63, 30, 3, 32, 59, 14, 11, 33,
          60, 24, 50, 9, 55, 19, 21, 34,
          61, 29, 2, 53, 51, 23, 41, 18,
          56, 28, 1, 43, 46, 27, 0, 35,
          62, 31, 58, 4, 5, 49, 54, 6,
          15, 52, 12, 40, 7, 42, 45, 16,
          25, 57, 48, 13, 10, 39, 8, 44,
          20, 47, 38, 22, 17, 37, 36, 26
  };


  public static int[] MAGIC =
          {0, 1, 48, 2, 57, 49, 28, 3,
           61, 58, 50, 42, 38, 29, 17, 4,
           62, 55, 59, 36, 53, 51, 43, 22,
           45, 39, 33, 30, 24, 18, 12, 5,
           63, 47, 56, 27, 60, 41, 37, 16,
           54, 35, 52, 21, 44, 32, 23, 11,
           46, 26, 40, 15, 34, 20, 31, 10,
           25, 14, 19, 9, 13, 8, 7, 6
          };


  public static long deBrujn = 285870213051386505L;


  // count number of set bits in a word
  static long ONES = 0x5555555555555555L;
  static long TWOS = 0x3333333333333333L;
  static int FOURS = 0x0f0f0f0f;

  public static final int countBits(long set)
  {
    set -= (set >>> 1) & ONES;
    set = (set & TWOS) + ((set >>> 2) & TWOS);
    int result = (int) set + (int) (set >>> 32);
    return (((result & FOURS) + ((result >>> 4) & FOURS)) * 0x01010101) >>> 24;
  }

  public final boolean isSquareCheckedByColor(Square square, int color)
  {
    return ((bishopAttacks(square.index64) & (pieceBoards[color][Piece.BISHOP] | pieceBoards[color][Piece.QUEEN])) |
           (rookAttacks(square.index64) & (pieceBoards[color][Piece.ROOK] | pieceBoards[color][Piece.QUEEN])) |
           (MoveGeneration.attackVectors[color][Piece.KNIGHT][square.index64] & pieceBoards[color][Piece.KNIGHT]) |
           (MoveGeneration.attackVectors[color][Piece.PAWN][square.index64] & pieceBoards[color][Piece.PAWN]) |
           (MoveGeneration.attackVectors[color][Piece.KING][square.index64] & pieceBoards[color][Piece.KING])) != 0;
  }

  public final boolean isSquareAttackedByColor(Square square, int color)
  {
    return ((bishopAttacks(square.index64) & (pieceBoards[color][Piece.BISHOP] | pieceBoards[color][Piece.QUEEN])) |
           (rookAttacks(square.index64) & (pieceBoards[color][Piece.ROOK] | pieceBoards[color][Piece.QUEEN])) |
           (MoveGeneration.attackVectors[color][Piece.KNIGHT][square.index64] & pieceBoards[color][Piece.KNIGHT]) |
           (MoveGeneration.attackVectors[color][Piece.PAWN][square.index64] & pieceBoards[color][Piece.PAWN])) != 0;
  }

  public final long rookAttacks(int a)
  {
    return (MoveGeneration.rook_attacks_r0[a][(int) (allPieces >>> (1 + ((a) & 56))) & 63]) | (MoveGeneration.rook_attacks_rl90[a][(int) (allPiecesRL90 >>> (57 - ((file(a) << 3) & 56))) & 63]);
  }

  public final long bishopAttacks(int a)
  {
    return (MoveGeneration.bishop_attacks_rr45[a][(int) (allPiecesRR45 >>> (MoveGeneration.bishop_shift_rr45[a])) & 63]) | (MoveGeneration.bishop_attacks_rl45[a][(int) (allPiecesRL45 >>> (MoveGeneration.bishop_shift_rl45[a])) & 63]);
  }

  public final long attacksRank(int a)
  {
    return MoveGeneration.rook_attacks_r0[a][(int) (allPieces >>> (1 + ((a) & 56))) & 63];
  }

  public final long attacksFile(int a)
  {
    return MoveGeneration.rook_attacks_rl90[a][(int) (allPiecesRL90 >>> (57 - ((file(a) << 3) & 56))) & 63];
  }

  public final long attacksDiaga1(int a)
  {
    return MoveGeneration.bishop_attacks_rr45[a][(int) (allPiecesRR45 >>> (MoveGeneration.bishop_shift_rr45[a])) & 63];
  }

  public final long attacksDiagh1(int a)
  {
    return MoveGeneration.bishop_attacks_rl45[a][(int) (allPiecesRL45 >>> (MoveGeneration.bishop_shift_rl45[a])) & 63];
  }


  public final long rookMobility(int a)
  {
    return (MoveGeneration.rook_mobility_r0[a][(int) (allPieces >>> (1 + ((a) & 56))) & 63]) | (MoveGeneration.rook_mobility_rl90[a][(int) (allPiecesRL90 >>> (57 - ((file(a) << 3) & 56))) & 63]);
  }

  public final long bishopMobility(int a)
  {
    return (MoveGeneration.bishop_mobility_rr45[a][(int) (allPiecesRR45 >>> (MoveGeneration.bishop_shift_rr45[a])) & 63]) | (MoveGeneration.bishop_mobility_rl45[a][(int) (allPiecesRL45 >>> (MoveGeneration.bishop_shift_rl45[a])) & 63]);
  }

  public final long mobilityRank(int a)
  {
    return MoveGeneration.rook_mobility_r0[a][(int) (allPieces >>> (1 + ((a) & 56))) & 63];
  }

  public final long mobilityFile(int a)
  {
    return MoveGeneration.rook_mobility_rl90[a][(int) (allPiecesRL90 >>> (57 - ((file(a) << 3) & 56))) & 63];
  }

  public final long mobilityDiaga1(int a)
  {
    return MoveGeneration.bishop_mobility_rr45[a][(int) (allPiecesRR45 >>> (MoveGeneration.bishop_shift_rr45[a])) & 63];
  }

  public final long mobilityDiagh1(int a)
  {
    return MoveGeneration.bishop_mobility_rl45[a][(int) (allPiecesRL45 >>> (MoveGeneration.bishop_shift_rl45[a])) & 63];
  }

  public static final int rank(int a)
  {
    return a >> 3;
  }
  public static final int file(int a)
  {
    return a & 7;
  }

}





