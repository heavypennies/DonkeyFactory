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
 * @author Joshua Levine <levinester@gmail.com>
 * @version $Revision$ $Name$ $Date$
 */
public class Board {
  public static final int APPROACHING_FIFTY_MOVE_THRESHOLD = 40;
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

  public static final Square[] SQUARES = Square.values();
  public final BoardSquare[] boardSquares = new BoardSquare[128];
  public Piece[] pieces = new Piece[32];


  // indexed like this:
  public static int ALL_PIECES = 6;
  public static int QUEENS_ROOKS = 7;
  public static int QUEENS_ROOKS_RL_90 = 8;
  public static int QUEENS_BISHOPS = 9;
  public static int QUEENS_BISHOPS_RL_45 = 10;
  public static int QUEENS_BISHOPS_RR_45 = 11;

  /**
   * <pre>
   *     0     1
   *  [black,white]
   *   0      1      2      3     4      5      6     7      8      9      10      11
   * [pawn, knight, bishop, rook, queen, king, all, R/Q 0, R/Q 90, B/Q , B/Q 45, B/Q -45</code>
   */
  public final long[][] pieceBoards = new long[2][12];

  public long allPieces = 0;
  public long allPiecesRL90 = 0;
  public long allPiecesRL45 = 0;
  public long allPiecesRR45 = 0;
  public long allPawns = 0;
  public long allPawnsRL90 = 0;
  public long allPawnsRL45 = 0;
  public long allPawnsRR45 = 0;

  public int materialScore = 0;
  public int pieceValues = 0;
  public int positionScore = 0;
  public final long[] attacks = new long[2];
  public final int[] materialValue = new int[2];
  public final long[] squareAttackers = new long[64];
  public final long[] squareRammers = new long[64];
  public final int[][] attackState = new int[2][64];

  // keeps board hashes for draw by rep
  public long[] repetitionTable = new long[500];
  public int[] fiftyMoveTable = new int[500];


  private static Random random = new Random();


  private static long[][] pieceHash = new long[16][64];

  static {
    File zobristData = new File("hashKeys.dat");

    if (true || !zobristData.exists()) {
      Random random = new Random(124353460892475679L);
      for (int pieceType = 0; pieceType < 12; pieceType++) {
        for (int squareIndex = 0; squareIndex < 64; squareIndex++) {
          pieceHash[pieceType][squareIndex] = random.nextLong();
        }
      }

      outer:
      while (true) {
        for (int pieceType = 0; pieceType < 12; pieceType++) {
          for (int squareIndex = 0; squareIndex < 64; squareIndex++) {
            for (int pieceType2 = 0; pieceType2 < 12; pieceType2++) {
              for (int squareIndex2 = 0; squareIndex2 < 64; squareIndex2++) {
                if ((pieceType != pieceType2 || squareIndex != squareIndex2) &&
                        (((pieceHash[pieceType][squareIndex] & PositionHashtable.HASH_MASK) == (PositionHashtable.HASH_MASK)) ||
                                (pieceHash[pieceType][squareIndex] & PositionHashtable.HASH_MASK) == 0)
                        ) {
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

    try {
      System.out.println("Reading hash key data: ");
      ObjectInputStream zobristDataInputStream = new ObjectInputStream(new FileInputStream(zobristData));
      pieceHash = (long[][]) zobristDataInputStream.readObject();


      outer:
      while (true) {
        for (int pieceType = 0; pieceType < 12; pieceType++) {
          for (int squareIndex = 0; squareIndex < 64; squareIndex++) {
            for (int pieceType2 = 0; pieceType2 < 12; pieceType2++) {
              for (int squareIndex2 = 0; squareIndex2 < 64; squareIndex2++) {
                if ((pieceType != pieceType2 || squareIndex != squareIndex2) &&
                        (((pieceHash[pieceType][squareIndex] & PositionHashtable.HASH_MASK) == (PositionHashtable.HASH_MASK)) ||
                                (pieceHash[pieceType][squareIndex] & PositionHashtable.HASH_MASK) == 0)
                        ) {
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
    } catch (IOException e) {
      System.err.println("Unable to read hash key data: ");
      e.printStackTrace();
      System.exit(0);
    } catch (ClassNotFoundException e) {
      System.err.println("Unable to read hash key data:  " + e.getMessage());
      e.printStackTrace();
      System.exit(0);
    }

    for(Square square : Square.values()) {
      square.kingArea[0] = Board.getStagingKingArea(square, 0) | Board.getPawnKingArea(square, 0) | Board.getTinyKingArea(square, 0) ;
      square.kingArea[1] = Board.getStagingKingArea(square, 1) | Board.getPawnKingArea(square, 1) | Board.getTinyKingArea(square, 1) ;

    }
  }

  private static void writeZobristData(File zobristData) {
    try {
      System.err.println("Writing hash key data");
      ObjectOutputStream zobristDataOutputStream = new ObjectOutputStream(new FileOutputStream(zobristData));
      zobristDataOutputStream.writeObject(pieceHash);
    } catch (IOException e) {
      System.err.println("Unable to write hash key data");
      e.printStackTrace();
      System.exit(0);
    }
  }

  static final long initialHashValue = (long)(Math.random() * Long.MAX_VALUE);
  static final long initialPawnHashValue = (long)(Math.random() * Long.MAX_VALUE);

  public Board() {
    hash1 = initialHashValue;
    pawnHash = initialPawnHashValue;
    for (Square square : Square.values()) {
      boardSquares[square.index128] = new BoardSquare(square);
    }

    for (int t = 0; t < 32; t++) {
      pieces[t] = new Piece(t, this, 0, 0, Square.A1);
      removePieceFromSquare(pieces[t], Square.A1);
    }

    for (Square square : Square.values()) {
      boardSquares[square.index128] = new BoardSquare(square);
      squareAttackers[square.index64] = 0;
      squareRammers[square.index64] = 0;
      attackState[0][square.index64] = 0;
      attackState[1][square.index64] = 0;
    }

    stats = new Stats();
  }

  public Board(String epd) {
    this();

    for (Square square : Square.values()) {
      squareAttackers[square.index64] = 0;
      squareRammers[square.index64] = 0;
    }
    // r3rnk1/4qpp1/p5np/4pQ2/Pb2N3/1B5P/1P3PP1/R1BR2K1 w

    setEPDPosition(epd);
  }

  public static long getStagingKingArea(Square square, int color)
  {
    if(color == 1)
    {
      return square.rank > 5 ? 0 :
             (square.file > 0 ? SQUARES[square.index64 + 15].mask_on : 0) |
             SQUARES[square.index64 + 16].mask_on |
             (square.file < 7 ? SQUARES[square.index64 + 17].mask_on : 0);
    }
    return square.rank < 2 ? 0 :
           (square.file > 0 ? SQUARES[square.index64 - 17].mask_on : 0) |
           SQUARES[square.index64 - 16].mask_on |
           (square.file < 7 ? SQUARES[square.index64 - 15].mask_on : 0);
  }

  public static long getPawnKingArea(Square square, int color)
  {
    if(color == 1)
    {
      return square.rank == 7 ? 0 :
             (square.file > 0 ? SQUARES[square.index64 + 7].mask_on : 0) |
             SQUARES[square.index64 + 8].mask_on |
             (square.file < 7 ? SQUARES[square.index64 + 9].mask_on : 0);
    }
    return square.rank == 0 ? 0 :
           (square.file > 0 ? SQUARES[square.index64 - 9].mask_on : 0) |
           SQUARES[square.index64 - 8].mask_on |
           (square.file < 7 ? SQUARES[square.index64 - 7].mask_on : 0);
  }

  public static long getTinyKingArea(Square square, int color)
  {
    if(color == 1)
    {
      return (square.rank == 0 ? 0 : (square.file > 0 ? SQUARES[square.index64 - 9].mask_on : 0)) |
             (square.rank == 0 ? 0 : SQUARES[square.index64 - 8].mask_on) |
             (square.rank == 0 ? 0 : (square.file < 7 ? SQUARES[square.index64 - 7].mask_on : 0)) |
             (square.file > 0 ? SQUARES[square.index64 - 1].mask_on : 0) |
             (square.file < 7 ? SQUARES[square.index64 + 1].mask_on : 0);
    }
    return (square.rank == 7 ? 0 : (square.file > 0 ? SQUARES[square.index64 + 7].mask_on : 0)) |
           (square.rank == 7 ? 0 : SQUARES[square.index64 + 8].mask_on) |
           (square.rank == 7 ? 0 : (square.file < 7 ? SQUARES[square.index64 + 9].mask_on : 0)) |
           (square.file > 0 ? SQUARES[square.index64 - 1].mask_on : 0) |
           (square.file < 7 ? SQUARES[square.index64 + 1].mask_on : 0);
  }

  public void setEPDPosition(String epd) {

    for (int color = 0; color < 2; color++) {
      for (int pieceType = 0; pieceType < 7; pieceType++) {
        pieceBoards[color][pieceType] = 0;
      }
    }

    int epdIndex = 0;
    int pieceIndex = 0;
    for (int rank = 7; rank >= 0; rank--) {
      for (int file = 0; file < 8; file++) {
        char ch = epd.charAt(epdIndex++);
        if (ch == '/') {
          ch = epd.charAt(epdIndex++);
        }
        if (epdIndex > 64) {
          throw new RuntimeException("unable to parse epd: " + epd);
        }

        switch (ch) {
          case 'p': {
            new Piece(pieceIndex++, this, 0, Piece.PAWN, SQUARES[(rank * 8) + file]);
            break;
          }
          case 'n': {
            new Piece(pieceIndex++, this, 0, Piece.KNIGHT, SQUARES[(rank * 8) + file]);
            break;
          }
          case 'b': {
            new Piece(pieceIndex++, this, 0, Piece.BISHOP, SQUARES[(rank * 8) + file]);
            break;
          }
          case 'r': {
            new Piece(pieceIndex++, this, 0, Piece.ROOK, SQUARES[(rank * 8) + file]);
            break;
          }
          case 'q': {
            new Piece(pieceIndex++, this, 0, Piece.QUEEN, SQUARES[(rank * 8) + file]);
            break;
          }
          case 'k': {
            new Piece(pieceIndex++, this, 0, Piece.KING, SQUARES[(rank * 8) + file]);
            break;
          }

          case 'P': {
            new Piece(pieceIndex++, this, 1, Piece.PAWN, SQUARES[(rank * 8) + file]);
            break;
          }
          case 'N': {
            new Piece(pieceIndex++, this, 1, Piece.KNIGHT, SQUARES[(rank * 8) + file]);
            break;
          }
          case 'B': {
            new Piece(pieceIndex++, this, 1, Piece.BISHOP, SQUARES[(rank * 8) + file]);
            break;
          }
          case 'R': {
            new Piece(pieceIndex++, this, 1, Piece.ROOK, SQUARES[(rank * 8) + file]);
            break;
          }
          case 'Q': {
            new Piece(pieceIndex++, this, 1, Piece.QUEEN, SQUARES[(rank * 8) + file]);
            break;
          }
          case 'K': {
            new Piece(pieceIndex++, this, 1, Piece.KING, SQUARES[(rank * 8) + file]);
            break;
          }
          default: {
            file += Integer.valueOf("" + ch) - 1;
          }
        }
      }
    }
  }
  long attackers = 0;
  Square pieceSquare;
  Piece attackingPiece;
  public final void setPieceOnSquare(final Piece piece, final Square square) {
//    if(Piece.DEBUG) System.err.println("Set " + piece + "  @  " + square);

    boardSquares[square.index128].piece = piece;
    piece.square = square;


    allPieces |= square.mask_on;
    allPiecesRL90 |= square.mask_on_rl90;
    allPiecesRR45 |= square.mask_on_rr45;
    allPiecesRL45 |= square.mask_on_rl45;

    pieceBoards[piece.color][ALL_PIECES] |= square.mask_on;
    pieceBoards[piece.color][piece.type] |= square.mask_on;

    hash1 ^= pieceHash[piece.type + (piece.color == 1 ? 0 : 6)][square.index64];
    switch(piece.type) {
      case Piece.PAWN : {
        pawnHash ^= pieceHash[piece.type + (piece.color == 1 ? 0 : 6)][square.index64];
        allPawns |= square.mask_on;
        allPawnsRR45 |= square.mask_on_rr45;
        allPawnsRL45 |= square.mask_on_rl45;
        allPawnsRL90 |= square.mask_on_rl90;
        positionScore += piece.color == 1 ? BoardEvaluator.PIECE_VALUE_TABLES[piece.color][piece.type][square.index64] : -BoardEvaluator.PIECE_VALUE_TABLES[piece.color][piece.type][square.index64];
        break;
      }
      case Piece.KNIGHT : {
        pieceValues += piece.color == 1 ? piece.materialValue : -piece.materialValue;
        positionScore += piece.color == 1 ? BoardEvaluator.PIECE_VALUE_TABLES[piece.color][piece.type][square.index64] : -BoardEvaluator.PIECE_VALUE_TABLES[piece.color][piece.type][square.index64];
        materialValue[piece.color] += piece.color == 1 ? piece.materialValue : -piece.materialValue;
        break;
      }
      case Piece.BISHOP : {
        pieceBoards[piece.color][QUEENS_BISHOPS] |= square.mask_on;
        pieceBoards[piece.color][QUEENS_BISHOPS_RL_45] |= square.mask_on_rl45;
        pieceBoards[piece.color][QUEENS_BISHOPS_RR_45] |= square.mask_on_rr45;
        pieceValues += piece.color == 1 ? piece.materialValue : -piece.materialValue;
        positionScore += piece.color == 1 ? BoardEvaluator.PIECE_VALUE_TABLES[piece.color][piece.type][square.index64] : -BoardEvaluator.PIECE_VALUE_TABLES[piece.color][piece.type][square.index64];
        materialValue[piece.color] += piece.color == 1 ? piece.materialValue : -piece.materialValue;
        break;
      }
      case Piece.ROOK : {
        pieceBoards[piece.color][QUEENS_ROOKS] |= square.mask_on;
        pieceBoards[piece.color][QUEENS_ROOKS_RL_90] |= square.mask_on_rl90;
        pieceValues += piece.color == 1 ? piece.materialValue : -piece.materialValue;
        positionScore += piece.color == 1 ? BoardEvaluator.PIECE_VALUE_TABLES[piece.color][piece.type][square.index64] : -BoardEvaluator.PIECE_VALUE_TABLES[piece.color][piece.type][square.index64];
        materialValue[piece.color] += piece.color == 1 ? piece.materialValue : -piece.materialValue;
        break;
      }
      case Piece.QUEEN : {
        pieceBoards[piece.color][QUEENS_BISHOPS] |= square.mask_on;
        pieceBoards[piece.color][QUEENS_BISHOPS_RL_45] |= square.mask_on_rl45;
        pieceBoards[piece.color][QUEENS_BISHOPS_RR_45] |= square.mask_on_rr45;
        pieceBoards[piece.color][QUEENS_ROOKS] |= square.mask_on;
        pieceBoards[piece.color][QUEENS_ROOKS_RL_90] |= square.mask_on_rl90;
        pieceValues += piece.color == 1 ? piece.materialValue : -piece.materialValue;
        materialValue[piece.color] += piece.color == 1 ? piece.materialValue : -piece.materialValue;
        break;
      }
    }

    materialScore += piece.value;

    piece.calculateAttacks(this, square);

    attackers = (squareAttackers[square.index64] | squareRammers[square.index64]) &
            (pieceBoards[0][QUEENS_ROOKS] | pieceBoards[0][QUEENS_BISHOPS] | pieceBoards[1][QUEENS_ROOKS] | pieceBoards[1][QUEENS_BISHOPS]);
    while (attackers != 0) {
      pieceSquare = Board.SQUARES[Long.numberOfTrailingZeros(attackers)];
      attackers &= pieceSquare.mask_off;
      boardSquares[pieceSquare.index128].piece.blockAttacks(this, square, pieceSquare);
    }
//    if(Piece.DEBUG) validateAllAttacks();
  }


  public final void removePieceFromSquare(final Piece piece, final Square square) {

    allPieces &= square.mask_off;
    allPiecesRL90 &= square.mask_off_rl90;
    allPiecesRR45 &= square.mask_off_rr45;
    allPiecesRL45 &= square.mask_off_rl45;

    pieceBoards[piece.color][ALL_PIECES] &= square.mask_off;
    pieceBoards[piece.color][piece.type] &= square.mask_off;

    hash1 ^= pieceHash[piece.type + (piece.color == 1 ? 0 : 6)][square.index64];
    switch(piece.type) {
      case Piece.PAWN : {
        pawnHash ^= pieceHash[piece.type + (piece.color == 1 ? 0 : 6)][square.index64];
        allPawns &= square.mask_off;
        allPawnsRR45 &= square.mask_off_rr45;
        allPawnsRL45 &= square.mask_off_rl45;
        allPawnsRL90 &= square.mask_off_rl90;
        positionScore -= piece.color == 1 ? BoardEvaluator.PIECE_VALUE_TABLES[piece.color][piece.type][square.index64] : -BoardEvaluator.PIECE_VALUE_TABLES[piece.color][piece.type][square.index64];
        break;
      }
      case Piece.KNIGHT : {
        pieceValues -= piece.color == 1 ? piece.materialValue : -piece.materialValue;
        positionScore -= piece.color == 1 ? BoardEvaluator.PIECE_VALUE_TABLES[piece.color][piece.type][square.index64] : -BoardEvaluator.PIECE_VALUE_TABLES[piece.color][piece.type][square.index64];
        materialValue[piece.color] -= piece.color == 1 ? piece.materialValue : -piece.materialValue;
        break;
      }
      case Piece.BISHOP : {
        pieceBoards[piece.color][QUEENS_BISHOPS] &= square.mask_off;
        pieceBoards[piece.color][QUEENS_BISHOPS_RL_45] &= square.mask_off_rl45;
        pieceBoards[piece.color][QUEENS_BISHOPS_RR_45] &= square.mask_off_rr45;
        pieceValues -= piece.color == 1 ? piece.materialValue : -piece.materialValue;
        positionScore -= piece.color == 1 ? BoardEvaluator.PIECE_VALUE_TABLES[piece.color][piece.type][square.index64] : -BoardEvaluator.PIECE_VALUE_TABLES[piece.color][piece.type][square.index64];
        materialValue[piece.color] -= piece.color == 1 ? piece.materialValue : -piece.materialValue;
        break;
      }
      case Piece.ROOK : {
        pieceBoards[piece.color][QUEENS_ROOKS] &= square.mask_off;
        pieceBoards[piece.color][QUEENS_ROOKS_RL_90] &= square.mask_off_rl90;
        pieceValues -= piece.color == 1 ? piece.materialValue : -piece.materialValue;
        positionScore -= piece.color == 1 ? BoardEvaluator.PIECE_VALUE_TABLES[piece.color][piece.type][square.index64] : -BoardEvaluator.PIECE_VALUE_TABLES[piece.color][piece.type][square.index64];
        materialValue[piece.color] -= piece.color == 1 ? piece.materialValue : -piece.materialValue;
        break;
      }
      case Piece.QUEEN : {
        pieceBoards[piece.color][QUEENS_BISHOPS] &= square.mask_off;
        pieceBoards[piece.color][QUEENS_BISHOPS_RL_45] &= square.mask_off_rl45;
        pieceBoards[piece.color][QUEENS_BISHOPS_RR_45] &= square.mask_off_rr45;
        pieceBoards[piece.color][QUEENS_ROOKS] &= square.mask_off;
        pieceBoards[piece.color][QUEENS_ROOKS_RL_90] &= square.mask_off_rl90;
        pieceValues -= piece.color == 1 ? piece.materialValue : -piece.materialValue;
        materialValue[piece.color] -= piece.color == 1 ? piece.materialValue : -piece.materialValue;
        break;
      }
    }

    piece.square = null;
    boardSquares[square.index128].piece = null;

    materialScore -= piece.value;

//    if(Piece.DEBUG) System.err.println("Rem " + piece + "  @  " + square);

    piece.removeAttacks(this, square);

    attackers = (squareAttackers[square.index64] | squareRammers[square.index64]) &
            (pieceBoards[0][QUEENS_ROOKS] | pieceBoards[0][QUEENS_BISHOPS] | pieceBoards[1][QUEENS_ROOKS] | pieceBoards[1][QUEENS_BISHOPS]);
    while (attackers != 0) {
      pieceSquare = Board.SQUARES[Long.numberOfTrailingZeros(attackers)];
      attackers &= pieceSquare.mask_off;
      boardSquares[pieceSquare.index128].piece.unblockAttacks(this, square, pieceSquare);
    }
    //if(Piece.DEBUG) validateAllAttacks();
  }

  public boolean isEndgame() {
    return pieceValues < 17 && (pieceBoards[0][Piece.QUEEN] == 0 || pieceBoards[1][Piece.QUEEN] == 0);
  }


  public class BoardSquare {
    public Square square;
    public Piece piece;

    // Maps the 'pawn up by two' move index128 to a yes or no question.
    // The question: "On this move, does this square accept en passent captures?"
    // This is formed as: if(square.enPassentInfo.get(board.moveIndex))
    public boolean[] enPassentInfo = new boolean[1000];
    public long attackers = 0;

    public BoardSquare(Square square) {
      this.square = square;

      // Since this is mapping to moveIndex, we make it large.
      // It should never have to grow, and never return null.
      for (int i = 0; i < 1000; i++) {
        enPassentInfo[i] = false;
      }
    }

    public Piece getPiece() {
      return piece;
    }


    public void setPiece(Piece piece) {
      this.piece = piece;
    }

    public String toString() {
      return square.toString() + "*";
    }
  }

  public final void make(Move move) {

/*
    if(Piece.DEBUG) System.err.println("Make: " + move);
    if(Piece.DEBUG) System.err.println(this);
*/

    // hash move

    // remove moving piece
    removePieceFromSquare(move.moved, move.fromSquare);

    // make capture
    if (move.taken != null) {
      // remove taken piece
      removePieceFromSquare(move.taken, move.takenSquare);

/*
      if (move.taken.type == Piece.ROOK) {
        if (move.taken.color == 1) {
          if (move.taken.kingsideRook) {
            stats.whiteKingsideRookMoves++;
          } else if (move.taken.queensideRook) {
            stats.whiteQueensideRookMoves++;
          }
        } else {
          if (move.taken.kingsideRook) {
            stats.blackKingsideRookMoves++;
          } else if (move.taken.queensideRook) {
            stats.blackQueensideRookMoves++;
          }
        }
      }
*/
    }

    // make promote
    if (move.promoteTo != -1) {
      move.moved.type = move.promoteTo;
      move.moved.value = move.moved.getValue();
      move.moved.materialValue = move.moved.getMaterialValue();
    }

    // set moving piece
    setPieceOnSquare(move.moved, move.toSquare);

    // Update Stats
    if (move.moved.type == Piece.KING) {
      if (move.moved.color == 1) {
        stats.whiteKingMoves++;
      } else {
        stats.blackKingMoves++;
      }

      // make castling
      if (move.castledRook != null) {
        if (move.toSquare.file > Constants.FILE_E) {
          if (move.moved.color == 1) {
            stats.whiteCastleFlag = Stats.O_O;
          } else {
            stats.blackCastleFlag = Stats.O_O;
          }
        } else {
          if (move.moved.color == 1) {
            stats.whiteCastleFlag = Stats.O_O_O;
          } else {
            stats.blackCastleFlag = Stats.O_O_O;
          }
        }
        removePieceFromSquare(move.castledRook, move.castleFromSquare);
        setPieceOnSquare(move.castledRook, move.castleToSquare);
      }
    } else if (move.moved.type == Piece.ROOK) {
      if (move.moved.color == 1) {
        if (move.moved.kingsideRook) {
          stats.whiteKingsideRookMoves++;
        } else if (move.moved.queensideRook) {
          stats.whiteQueensideRookMoves++;
        }
      } else {
        if (move.moved.kingsideRook) {
          stats.blackKingsideRookMoves++;
        } else if (move.moved.queensideRook) {
          stats.blackQueensideRookMoves++;
        }
      }
    }

    if (move.moved.color == 1) {
      stats.whitePieceMoves[move.moved.type]++;
    } else {
      stats.blackPieceMoves[move.moved.type]++;
    }

    turn = turn ^ 1;
    moveIndex++;

    // set en passent
    if (move.enPassentSquare != null) {
      boardSquares[move.enPassentSquare.index128].enPassentInfo[moveIndex] = true;
    }
    repetitionTable[moveIndex] = turn == 1 ? hash1 : ~hash1;

    if(move.moved.type == Piece.PAWN || move.taken != null || move.promoteTo != -1 || move.castleFromSquare != null) {
      fiftyMoveTable[moveIndex] = 0;
    }
    else {
      fiftyMoveTable[moveIndex] = fiftyMoveTable[moveIndex - 1] + 1;
    }
  }

  public final void unmake(Move move) {
/*
    if(Piece.DEBUG) System.err.println("Unmake: " + move);
    if(Piece.DEBUG) System.err.println(this);
*/

    repetitionTable[moveIndex] = 0;

    // Rollback stats
    if (move.moved.type == Piece.KING) {
      if (move.moved.color == 1) {
        stats.whiteKingMoves--;
      } else {
        stats.blackKingMoves--;
      }
      // unmake castling
      if (move.castledRook != null) {
        removePieceFromSquare(move.castledRook, move.castleToSquare);
        setPieceOnSquare(move.castledRook, move.castleFromSquare);

        if (move.moved.color == 1) {
          stats.whiteCastleFlag = 0;
        } else {
          stats.blackCastleFlag = 0;
        }
      }
    } else if (move.moved.type == Piece.ROOK) {
      if (move.moved.color == 1) {
        if (move.moved.kingsideRook) {
          stats.whiteKingsideRookMoves--;
        } else if (move.moved.queensideRook) {
          stats.whiteQueensideRookMoves--;
        }
      } else {
        if (move.moved.kingsideRook) {
          stats.blackKingsideRookMoves--;
        } else if (move.moved.queensideRook) {
          stats.blackQueensideRookMoves--;
        }
      }
    }

    if (move.moved.color == 1) {
      stats.whitePieceMoves[move.moved.type]--;
    } else {
      stats.blackPieceMoves[move.moved.type]--;
    }

    // unmake move
    removePieceFromSquare(move.moved, move.toSquare);

    // unmake promote
    if (move.promoteTo != -1) {
      move.moved.type = Piece.PAWN;
      move.moved.value = move.moved.getValue();
      move.moved.materialValue = move.moved.getMaterialValue();
    }

    // unmake capture
    if (move.taken != null) {
      setPieceOnSquare(move.taken, move.takenSquare);
/*
      if (move.taken.type == Piece.ROOK) {
        if (move.taken.color == 1) {
          if (move.taken.kingsideRook) {
            stats.whiteKingsideRookMoves--;
          } else if (move.taken.queensideRook) {
            stats.whiteQueensideRookMoves--;
          }
        } else {
          if (move.taken.kingsideRook) {
            stats.blackKingsideRookMoves--;
          } else if (move.taken.queensideRook) {
            stats.blackQueensideRookMoves--;
          }
        }
      }
*/
    }

    setPieceOnSquare(move.moved, move.fromSquare);


    // unset en passent
    if (move.enPassentSquare != null) {
      boardSquares[move.enPassentSquare.index128].enPassentInfo[moveIndex] = false;
    }

    turn = turn ^ 1;
    moveIndex--;
  }

  public static class Stats {
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

    public String toString() {
      return "Stats: castling (" + (whiteCastleFlag == 1 ? "O-O" : (whiteCastleFlag == 2 ? "O-O-O" : (whiteKingsideRookMoves == 0 && whiteQueensideRookMoves == 0 && whitePieceMoves[Piece.KING] == 0 ? "WAIT" : "CEN"))) + " v " + (blackCastleFlag == 1 ? "O-O" : (blackCastleFlag == 2 ? "O-O-O" : (blackKingsideRookMoves == 0 && blackQueensideRookMoves == 0 && blackPieceMoves[Piece.KING] == 0 ? "WAIT" : "CEN"))) + ")" +
              "\n  whitePieceMoves: " + whitePieceMoves + "\n" +
              "\n  whitePieceMoves: " + blackPieceMoves + "\n";
    }
  }

  public String toString() {
    StringBuffer out = new StringBuffer();
    out.append("\n---------------------------------\n");
    for (int rank = 7; rank > -1; rank--) {
      out.append("|");
      for (int file = 0; file < 8; file++) {
        BoardSquare square = boardSquares[(rank * 16) + file];
        if (square.getPiece() != null) {
          out.append(" ").append(square.getPiece().toString()).append(" |");
        } else {
          out.append("   |");
        }
      }
      out.append("\n");
      out.append("---------------------------------\n");
    }

    return out.toString();
  }

  public long getHash() {
    return turn == 1 ? hash1 : ~hash1;
  }

  public final boolean isApproachingDraw() {
    long hash = turn == 1 ? hash1 : ~hash1;

    if(fiftyMoveTable[moveIndex] > APPROACHING_FIFTY_MOVE_THRESHOLD) {
      return true;
    }
    for (int t = moveIndex - 2; t > moveIndex - 32 && t > -1; t -= 2) {
      if (repetitionTable[t] == 0) {
        return false;
      }
      if (repetitionTable[t] == hash) {
        return true;
      }
    }
    return false;
  }

  public boolean isDraw() {
    int hits = 0;
    if(fiftyMoveTable[moveIndex] >= 50) {
      return true;
    }
    long hash = getHash();
    for (int t = moveIndex - 2; t > moveIndex - 64 && t > -1; t -= 2) {
      if (repetitionTable[t] == hash) {
        hits++;
      }
    }
    return hits > 1;
  }

  public static String translateSquares128(List<Integer> squares) {
    StringBuffer out = new StringBuffer();
    for (int squareIndex : squares) {
      out.append(Board.SQUARES[squareIndex]).append(" ");
    }

    return out.toString();
  }

  public List<Square> getAllSquaresInBitboard(long bitboard) {
    List<Square> index = new ArrayList<Square>();
    while (bitboard != 0) {
      int squareIndex = Long.numberOfTrailingZeros(bitboard);

      Square fromSquare = SQUARES[squareIndex];
      index.add(fromSquare);

      bitboard &= fromSquare.mask_off;

    }

    return index;
  }


  public int getAllSquaresInBitboard(long bitboard, int[] squares) {
    int index = 0;
    while (bitboard != 0) {
      int squareIndex = Long.numberOfTrailingZeros(bitboard);
      squares[index++] = squareIndex;
      bitboard &= Board.SQUARES[squareIndex].mask_off;
    }

    return index;
  }

  static final long debruijn64 = 0x07EDD5E59A4E28C2L;

  static public int getLeastSignificantBit3(long b)
  {
      double x = (double)(b & - b);
      int exp = (int) (Double.doubleToLongBits(x) >>> 52);
      return (exp & 2047) - 1023;
  }
  static private final long deBruijn = 0x03f79d71b4cb0a89L;
  static private final int[] magicTable = {
      0, 1,48, 2,57,49,28, 3,
     61,58,50,42,38,29,17, 4,
     62,55,59,36,53,51,43,22,
     45,39,33,30,24,18,12, 5,
     63,47,56,27,60,41,37,16,
     54,35,52,21,44,32,23,11,
     46,26,40,15,34,20,31,10,
     25,14,19, 9,13, 8, 7, 6,
    };

  static public int getLeastSignificantBit4 (long b) {
    if (b >> 48 != 0) return MoveGeneration.first_one[(short)(b >> 48) & 0xFFFF ] + 48;
    if (b >> 32 != 0) return MoveGeneration.first_one[(short)(b >> 32) & 0xFFFF ] + 32;
    if (b >> 16 != 0) return MoveGeneration.first_one[(short)(b >> 16) & 0xFFFF ] + 16;
    return MoveGeneration.first_one[(short)b & 0xFFFF ];
  }

  static final public int getLeastSignificantBit (long b) {
    return Long.numberOfTrailingZeros(b);
  }

  static final public int getLeastSignificantBit5 (long b) {
    return magicTable[(int)(((b & -b) * deBruijn) >>> 58)];
  }

  // Get the index of a set bit
  public static final int getLeastSignificantBit2(long board) {
    int fold = ((int) (board ^ (board - 1))) ^ ((int) ((board ^ (board - 1)) >>> 32));
    return lsz64_tbl[(fold * 0x78291ACF) >>> (32 - 6)];
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


  private static final int[] index64 = {
          63, 0, 58, 1, 59, 47, 53, 2,
          60, 39, 48, 27, 54, 33, 42, 3,
          61, 51, 37, 40, 49, 18, 28, 20,
          55, 30, 34, 11, 43, 14, 22, 4,
          62, 57, 46, 52, 38, 26, 32, 41,
          50, 36, 17, 19, 29, 10, 13, 21,
          56, 45, 25, 31, 35, 16, 9, 12,
          44, 24, 15, 8, 23, 7, 6, 5
  };

  /**
   * bitScanForward
   *
   * @param bb bitboard to scan
   * @return index (0..63) of least significant one bit
   * @author Charles E. Leiserson
   * Harald Prokop
   * Keith H. Randall
   * "Using de Bruijn Sequences to Index a 1 in a Computer Word"
   * @precondition bb != 0
   */
  int bitScanForward(long bb) {
//    assert (bb != 0);
    return index64[(int) ((bb & -bb) * debruijn64) >>> 58];
  }




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

  public static final int countBits(long set) {
    return Long.bitCount(set);
  }
  public static final int countBits2(long set) {
    set -= (set >>> 1) & ONES;
    set = (set & TWOS) + ((set >>> 2) & TWOS);
    int result = (int) set + (int) (set >>> 32);
    return (((result & FOURS) + ((result >>> 4) & FOURS)) * 0x01010101) >>> 24;
  }

  public final boolean isSquareCheckedByColor(Square square, int color) {
    return (squareAttackers[square.index64] & pieceBoards[color][ALL_PIECES]) != 0;
  }

  public final boolean isSquareAttackedByColor(Square square, int color) {
    return (bishopAttacks(square.index64) & (pieceBoards[color][Piece.BISHOP] | pieceBoards[color][Piece.QUEEN])) != 0 ||
            (rookAttacks(square.index64) & (pieceBoards[color][Piece.ROOK] | pieceBoards[color][Piece.QUEEN])) != 0 ||
            (MoveGeneration.attackVectors[color][Piece.KNIGHT][square.index64] & pieceBoards[color][Piece.KNIGHT]) != 0 ||
            (MoveGeneration.attackVectors[color][Piece.PAWN][square.index64] & pieceBoards[color][Piece.PAWN]) != 0;
  }

  public final boolean isSquareDefendedByColor(Square square, int color) {
    return ((bishopAttacks(square.index64) & (pieceBoards[color][Piece.BISHOP] | pieceBoards[color][Piece.QUEEN])) |
            (rookAttacks(square.index64) & (pieceBoards[color][Piece.ROOK] | pieceBoards[color][Piece.QUEEN])) |
            (MoveGeneration.attackVectors[color][Piece.KNIGHT][square.index64] & pieceBoards[color][Piece.KNIGHT])) != 0;
  }

  /**
   * Returns a bitboard of rook attacks from the given square
   * @param square Square.index64
   * @return
   */
  public final long rookAttacks(int square) {
    return (MoveGeneration.rook_attacks_r0[square][(int) (allPieces >>> (1 + ((square) & 56))) & 63]) |
            (MoveGeneration.rook_attacks_rl90[square][(int) (allPiecesRL90 >>> (57 - (((square & 7) << 3) & 56))) & 63]);
  }

  /**
   * Returns a bitboard of rook attacks from the given square
   * @param square Square.index64
   * @return
   */
  public final long rookAttacksXRay(int square, int color) {
    return (MoveGeneration.rook_attacks_r0[square][(int) ((allPieces ^ pieceBoards[color][QUEENS_ROOKS]) >>> (1 + ((square) & 56))) & 63]) |
            (MoveGeneration.rook_attacks_rl90[square][(int) ((allPiecesRL90 ^ pieceBoards[color][QUEENS_ROOKS_RL_90]) >>> (57 - (((square & 7) << 3) & 56))) & 63]);
  }

  /**
   * Returns a bitboard of rook attacks from the given square
   * @param square Square.index64
   * @return
   */
  public final long rookPins(int square) {
    return (MoveGeneration.rook_attacks_r0[square][(int) ((allPawns) >>> (1 + ((square) & 56))) & 63]) |
            (MoveGeneration.rook_attacks_rl90[square][(int) ((allPawnsRL90) >>> (57 - (((square & 7) << 3) & 56))) & 63]);
  }

  /**
   * Returns a bitboard of bishop attacks from the given square
   * @param square Square.index64
   * @return
   */
  public final long bishopAttacks(int square) {
    return (MoveGeneration.bishop_attacks_rr45[square][(int) (allPiecesRR45 >>> (MoveGeneration.bishop_shift_rr45[square])) & 63]) |
            (MoveGeneration.bishop_attacks_rl45[square][(int) (allPiecesRL45 >>> (MoveGeneration.bishop_shift_rl45[square])) & 63]);
  }

  /**
   * Returns a bitboard of bishop attacks from the given square
   * @param square Square.index64
   * @return
   */
  public final long bishopAttacksXRay(int square, int color) {
    return (MoveGeneration.bishop_attacks_rr45[square][(int) ((allPiecesRR45  ^ pieceBoards[color][QUEENS_BISHOPS_RR_45]) >>> (MoveGeneration.bishop_shift_rr45[square])) & 63]) |
            (MoveGeneration.bishop_attacks_rl45[square][(int) ((allPiecesRL45 ^ pieceBoards[color][QUEENS_BISHOPS_RL_45]) >>> (MoveGeneration.bishop_shift_rl45[square])) & 63]);
  }

  /**
   * Returns a bitboard of bishop attacks that hit opposite color rooks, queens, and kings from the given square
   * @param square Square.index64
   * @return
   */
  public final long bishopPins(int square) {
    return (MoveGeneration.bishop_attacks_rr45[square][(int) ((allPawnsRR45) >>> (MoveGeneration.bishop_shift_rr45[square])) & 63]) |
            (MoveGeneration.bishop_attacks_rl45[square][(int) ((allPawnsRL45) >>> (MoveGeneration.bishop_shift_rl45[square])) & 63]);
  }

  public final long attacksRank(int a) {
    return MoveGeneration.rook_attacks_r0[a][(int) (allPieces >>> (1 + ((a) & 56))) & 63];
  }

  public final long attacksFile(int a) {
    return MoveGeneration.rook_attacks_rl90[a][(int) (allPiecesRL90 >>> (57 - (((a & 7) << 3) & 56))) & 63];
  }

  public final long attacksDiaga1(int a) {
    return MoveGeneration.bishop_attacks_rr45[a][(int) (allPiecesRR45 >>> (MoveGeneration.bishop_shift_rr45[a])) & 63];
  }

  public final long attacksDiagh1(int a) {
    return MoveGeneration.bishop_attacks_rl45[a][(int) (allPiecesRL45 >>> (MoveGeneration.bishop_shift_rl45[a])) & 63];
  }


  public final long rookMobility(int a) {
    return (MoveGeneration.rook_mobility_r0[a][(int) (allPieces >>> (1 + ((a) & 56))) & 63]) +
            (MoveGeneration.rook_mobility_rl90[a][(int) (allPiecesRL90 >>> (57 - (((a & 7) << 3) & 56))) & 63]);
  }

  public final long bishopMobility(int a) {
    return (MoveGeneration.bishop_mobility_rr45[a][(int) (allPawnsRR45 >>> (MoveGeneration.bishop_shift_rr45[a])) & 63]) +
            (MoveGeneration.bishop_mobility_rl45[a][(int) (allPawnsRL45 >>> (MoveGeneration.bishop_shift_rl45[a])) & 63]);
  }

  public final long mobilityRank(int a) {
    return MoveGeneration.rook_mobility_r0[a][(int) (allPawns >>> (1 + ((a) & 56))) & 63];
  }

  public final long mobilityFile(int a) {
    return MoveGeneration.rook_mobility_rl90[a][(int) (allPawnsRL90 >>> (57 - (((a & 7) << 3) & 56))) & 63];
  }

  public final long mobilityDiaga1(int a) {
    return MoveGeneration.bishop_mobility_rr45[a][(int) (allPawnsRR45 >>> (MoveGeneration.bishop_shift_rr45[a])) & 63];
  }

  public final long mobilityDiagh1(int a) {
    return MoveGeneration.bishop_mobility_rl45[a][(int) (allPawnsRL45 >>> (MoveGeneration.bishop_shift_rl45[a])) & 63];
  }

  public static final int rank(int a) {
    return a >> 3;
  }

  public static final int file(int a) {
    return a & 7;
  }

  public String visualizeAttackState(int attackState) {
    int pawns = attackState & 3;
    int kings = (attackState >> Piece.ATTACKER_SHIFT_KING) & 3;
    int queens = (attackState >> Piece.ATTACKER_SHIFT_QUEEN) & 3;
    int rooks = (attackState >> Piece.ATTACKER_SHIFT_ROOK) & 3;
    int knights = (attackState >> Piece.ATTACKER_SHIFT_KNIGHT) & 3;
    int bishops = (attackState >> Piece.ATTACKER_SHIFT_BISHOP) & 3;

    return "AttackState { King: " +  kings + ", Queen: " + queens + ", Rook: " + rooks +  ", Bishop: " + bishops + ", Knight: " + knights + ", Pawn: " + pawns + "}";
  }

  public int calculateAttackState(int color, int square) {
    int attackState = 0;

    for(int type = 0;type <= Piece.KING;type++) {
      attackState |= (3 & countBits((squareAttackers[square] | squareRammers[square]) & pieceBoards[color][type])) << Piece.ATTACKER_SHIFT[type];
    }

    return attackState;
  }

  public void validateAllAttacks() {
    for(int color = 0;color < 1;color++) {
      for(Square square : SQUARES) {
        long allAttackers = MoveGeneration.getAllAttackers(this, square, 0) | MoveGeneration.getAllAttackers(this, square, 1);
        if(calculateAttackState(color, square.index64) != attackState[color][square.index64] ||
           (squareAttackers[square.index64] | squareRammers[square.index64]) != allAttackers) {
          System.err.println(this);
          System.err.println(square + " realAttackers: " + getAllSquaresInBitboard(allAttackers));
          System.err.println(square + " attackers: " + getAllSquaresInBitboard(squareAttackers[square.index64] & pieceBoards[color][ALL_PIECES]));
          System.err.println(square + " rammers: " + getAllSquaresInBitboard(squareRammers[square.index64] & pieceBoards[color][ALL_PIECES]));
          System.err.println(square + " r" + visualizeAttackState(attackState[color][square.index64]));
          System.err.println(square + " c" + visualizeAttackState(calculateAttackState(color, square.index64)));
          int x = 0;
          calculateAttackState(color, square.index64);
          allAttackers = MoveGeneration.getAllAttackers(this, square, 0) | MoveGeneration.getAllAttackers(this, square, 1);
        }
      }
    }
  }
}





