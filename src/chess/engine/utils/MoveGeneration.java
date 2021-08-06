/* $Id$ */
/* $Id$ */

package chess.engine.utils;

import chess.engine.model.*;
import chess.engine.search.ABSearch;

/**
 * @author Joshua Levine <levinester@gmail.com>
 * @version $Revision$ $Name$ $Date$
 */
public class MoveGeneration implements MoveGenerationConstants {

  public static long[] zerosL = new long[64];
  public static int[] zeros = new int[64];
  public static int[][] checks = new int[2][64];
/*
  public static int[][] pawnChecks = new int[2][64];
  public static int[][] knightChecks = new int[2][64];
  public static int[][] bishopChecks = new int[2][64];
  public static int[][] rookChecks = new int[2][64];
  public static int[][] queenChecks = new int[2][64];
  public static int[][] kingChecks = new int[2][64];
*/

  /**
   * <code>
   * 0     1          0      1      2      3     4      5     6        0-64
   * [black,white] [pawn, knight, bishop, rook, queen, king, all ]  [ SQUARES ]
   * <p/>
   * 1  2  3
   * 4  0  5
   * 6  7  8
   * </code>
   */
  public static long[][][] attackVectors = new long[2][9][64];
  public static boolean[][][][] attacksFromTo = new boolean[2][9][64][64];
  public static long[][] rook_attacks_r0 = new long[64][64];
  public static long[][] rook_attacks_rl90 = new long[64][64];
  public static long[][] bishop_attacks_rl45 = new long[64][64];
  public static long[][] bishop_attacks_rr45 = new long[64][64];
  public static long[][] rook_mobility_r0 = new long[64][64];
  public static long[][] rook_mobility_rl90 = new long[64][64];
  public static long[][] bishop_mobility_rl45 = new long[64][64];
  public static long[][] bishop_mobility_rr45 = new long[64][64];


  /**
   * [color][piece type][from square][to square]
   */
  public static int[][][][] attackSquares128 = new int[2][8][64][64];

  // represents a bitboard of the SQUARES between two SQUARES.
  // if there is no path for a sliding piece between the two SQUARES, the bitboard will be empty.
  public static long[][] distanceSpans = new long[128][128];
  public static long[][] notDistanceSpans = new long[128][128];
  public static long[][] shadowVectors = new long[128][128];


  public MoveGeneration() {
    Move[] moves = Move.createMoves(30);

    System.err.println("Initializing attack vectors...");
    // initialize attack and move vectors
    for (int color = 0; color < 2; color++) {
      Board board = new Board();
      // generate fake pawn captures
      Piece piece;
      Piece target1;
      Piece target2;
      for (Square square : Board.SQUARES) {
/*
        if (square.rank == Constants.RANK_1 || square.rank == Constants.RANK_8) {
          continue;
        }
*/
        board.turn = color;

        piece = new Piece(0, board, color, Piece.PAWN, square);
        if (color == 0) {
          if (((square.index128 - 15) & 0x88) == 0) {
            target1 = new Piece(1, board, color ^ 1, Piece.KNIGHT, board.boardSquares[square.index128 - 15].square);
          } else {
            target1 = null;
          }
          if (((square.index128 - 17) & 0x88) == 0) {
            target2 = new Piece(2, board, color ^ 1, Piece.KNIGHT, board.boardSquares[square.index128 - 17].square);
          } else {
            target2 = null;
          }
        } else {
          if (((square.index128 + 15) & 0x88) == 0) {
            target1 = new Piece(1, board, color ^ 1, Piece.KNIGHT, board.boardSquares[square.index128 + 15].square);
          } else {
            target1 = null;
          }
          if (((square.index128 + 17) & 0x88) == 0) {
            target2 = new Piece(2, board, color ^ 1, Piece.KNIGHT, board.boardSquares[square.index128 + 17].square);
          } else {
            target2 = null;
          }
        }

        generateFullMoves(moves, board);

        for (Move move : moves) {
          if (move.moved == null) {
            break;
          }

          if (move.taken != null && move.taken.square != null) {
            attackVectors[color][Piece.PAWN][move.toSquare.index64] |= move.fromSquare.mask_on;
            attackVectors[color][Board.ALL_PIECES][move.toSquare.index64] |= move.fromSquare.mask_on;
          }
        }

        board.removePieceFromSquare(piece, square);
        if (target1 != null) {
          board.removePieceFromSquare(target1, target1.square);
        }
        if (target2 != null) {
          board.removePieceFromSquare(target2, target2.square);
        }
      }

      board = new Board();

      // generate piece moves
      for (int pieceType = 1; pieceType < 6; pieceType++) {
        for (Square square : Board.SQUARES) {
          board.turn = color;
          piece = new Piece(0, board, color, pieceType, square);

          generateFullMoves(moves, board);

          for (Move move : moves) {
            if (move.moved == null) {
              break;
            }

            attackVectors[color][pieceType][move.toSquare.index64] |= move.fromSquare.mask_on;
            attackVectors[color][Board.ALL_PIECES][move.toSquare.index64] |= move.fromSquare.mask_on;

            attacksFromTo[color][pieceType][move.fromSquare.index64][move.toSquare.index64] = true;
            attacksFromTo[color][Board.ALL_PIECES][move.fromSquare.index64][move.toSquare.index64] = true;
          }

          if (pieceType == Piece.BISHOP) {
            for (int i : bishopMoves) {
              int squareIndex = piece.square.index128 + i;
              while ((squareIndex & 0x88) == 0) {
                Board.BoardSquare toSquare = board.boardSquares[squareIndex];
                long squareMask = toSquare.square.mask_on;
                if (i == 17 || i == -17) {
                  attackVectors[1][7][piece.square.index64] |= squareMask;
                  attackVectors[0][7][piece.square.index64] |= squareMask;
                } else {
                  attackVectors[1][8][piece.square.index64] |= squareMask;
                  attackVectors[0][8][piece.square.index64] |= squareMask;
                }
                squareIndex += i;
              }
            }
          }
          board.removePieceFromSquare(piece, square);
        }
      }
    }

    System.err.println("Initializing attack SQUARES...");
    for (int color = 0; color < 2; color++) {
      for (int pieceType = 0; pieceType < 7; pieceType++) {
        for (int square = 0; square < 64; square++) {
          int index = 0;
          int[] squares = new int[64];
          Square attackSquare;
          long attacks = attackVectors[color][pieceType][square];
          while (attacks != 0) {

            attackSquare = Board.SQUARES[Long.numberOfTrailingZeros(attacks)];
            attacks &= attackSquare.mask_off;

            squares[index++] = attackSquare.index128;
            squares[index] = -1;
          }
          attackSquares128[color][pieceType][square] = squares;
          attackSquares128[color][Board.ALL_PIECES][square] = squares;
        }
      }
    }

    System.err.println("Initializing distance spans...");
    // generate distance spans

    Board board = new Board();
    for (int fromSquare = 0; fromSquare < 128; fromSquare++) {
      for (int toSquare = 0; toSquare < 128; toSquare++) {
        notDistanceSpans[fromSquare][toSquare] = -1;
        if (fromSquare == toSquare || (fromSquare & 0x88) != 0 || (toSquare & 0x88) != 0) {
          distanceSpans[fromSquare][toSquare] = -1;
          continue;
        }

        int distance = fromSquare - toSquare;

        if (distance < 0) {
          // right
          if (distance > -8) {
            if (fromSquare + 1 == toSquare && ((fromSquare + 1) & 0x88) == 0) {
              distanceSpans[fromSquare][toSquare] = 0;
            }
            for (int squareIndex = fromSquare + 1; squareIndex < toSquare && (squareIndex & 0x88) == 0; squareIndex++) {
              Square square = board.boardSquares[squareIndex].square;
              distanceSpans[fromSquare][toSquare] |= square.mask_on;
              notDistanceSpans[fromSquare][toSquare] &= square.mask_off;
            }
            for (int squareIndex = toSquare + 1; (squareIndex & 0x88) == 0; squareIndex++) {
              Square square = board.boardSquares[squareIndex].square;
              shadowVectors[fromSquare][toSquare] |= square.mask_on;
            }
          }
          // up-and-left
          else if (Math.abs(distance) % 15 == 0) {
            if (fromSquare + 15 == toSquare && ((fromSquare + 15) & 0x88) == 0) {
              distanceSpans[fromSquare][toSquare] = 0;
            }
            for (int squareIndex = fromSquare + 15; squareIndex < toSquare && (squareIndex & 0x88) == 0; squareIndex += 15) {
              Square square = board.boardSquares[squareIndex].square;
              distanceSpans[fromSquare][toSquare] |= square.mask_on;
              notDistanceSpans[fromSquare][toSquare] &= square.mask_off;
            }
            for (int squareIndex = toSquare + 15; (squareIndex & 0x88) == 0; squareIndex += 15) {
              Square square = board.boardSquares[squareIndex].square;
              shadowVectors[fromSquare][toSquare] |= square.mask_on;
            }
          }
          // up
          else if (Math.abs(distance) % 16 == 0) {
            if (fromSquare + 16 == toSquare && ((fromSquare + 16) & 0x88) == 0) {
              distanceSpans[fromSquare][toSquare] = 0;
            }
            for (int squareIndex = fromSquare + 16; squareIndex < toSquare && (squareIndex & 0x88) == 0; squareIndex += 16) {
              Square square = board.boardSquares[squareIndex].square;
              distanceSpans[fromSquare][toSquare] |= square.mask_on;
              notDistanceSpans[fromSquare][toSquare] &= square.mask_off;
            }
            for (int squareIndex = toSquare + 16; (squareIndex & 0x88) == 0; squareIndex += 16) {
              Square square = board.boardSquares[squareIndex].square;
              shadowVectors[fromSquare][toSquare] |= square.mask_on;
            }
          }
          // up-and-right
          else if (Math.abs(distance) % 17 == 0) {
            if (fromSquare + 17 == toSquare && ((fromSquare + 17) & 0x88) == 0) {
              distanceSpans[fromSquare][toSquare] = 0;
            }
            for (int squareIndex = fromSquare + 17; squareIndex < toSquare && (squareIndex & 0x88) == 0; squareIndex += 17) {
              Square square = board.boardSquares[squareIndex].square;
              distanceSpans[fromSquare][toSquare] |= square.mask_on;
              notDistanceSpans[fromSquare][toSquare] &= square.mask_off;
            }
            for (int squareIndex = toSquare + 17; (squareIndex & 0x88) == 0; squareIndex += 17) {
              Square square = board.boardSquares[squareIndex].square;
              shadowVectors[fromSquare][toSquare] |= square.mask_on;
            }
          }
        } else {
          // left
          if (distance < 8) {
            if (fromSquare - 1 == toSquare && ((fromSquare - 1) & 0x88) == 0) {
              distanceSpans[fromSquare][toSquare] = 0;
            }
            for (int squareIndex = fromSquare - 1; squareIndex > toSquare && (squareIndex & 0x88) == 0; squareIndex--) {
              Square square = board.boardSquares[squareIndex].square;
              distanceSpans[fromSquare][toSquare] |= square.mask_on;
              notDistanceSpans[fromSquare][toSquare] &= square.mask_off;
            }
            for (int squareIndex = toSquare - 1; (squareIndex & 0x88) == 0; squareIndex -= 1) {
              Square square = board.boardSquares[squareIndex].square;
              shadowVectors[fromSquare][toSquare] |= square.mask_on;
            }
          }
          // down-and-right
          else if (distance % 15 == 0) {
            if (fromSquare - 15 == toSquare && ((fromSquare - 15) & 0x88) == 0) {
              distanceSpans[fromSquare][toSquare] = 0;
            }
            for (int squareIndex = fromSquare - 15; squareIndex > toSquare && (squareIndex & 0x88) == 0; squareIndex -= 15) {
              Square square = board.boardSquares[squareIndex].square;
              distanceSpans[fromSquare][toSquare] |= square.mask_on;
              notDistanceSpans[fromSquare][toSquare] &= square.mask_off;
            }
            for (int squareIndex = toSquare - 15; (squareIndex & 0x88) == 0; squareIndex -= 15) {
              Square square = board.boardSquares[squareIndex].square;
              shadowVectors[fromSquare][toSquare] |= square.mask_on;
            }
          }
          // down
          else if (distance % 16 == 0) {
            if (fromSquare - 16 == toSquare && ((fromSquare - 16) & 0x88) == 0) {
              distanceSpans[fromSquare][toSquare] = 0;
            }
            for (int squareIndex = fromSquare - 16; squareIndex > toSquare && (squareIndex & 0x88) == 0; squareIndex -= 16) {
              Square square = board.boardSquares[squareIndex].square;
              distanceSpans[fromSquare][toSquare] |= square.mask_on;
              notDistanceSpans[fromSquare][toSquare] &= square.mask_off;
            }
            for (int squareIndex = toSquare - 16; (squareIndex & 0x88) == 0; squareIndex -= 16) {
              Square square = board.boardSquares[squareIndex].square;
              shadowVectors[fromSquare][toSquare] |= square.mask_on;
            }
          }
          // down-and-left
          else if (distance % 17 == 0) {
            if (fromSquare - 17 == toSquare && ((fromSquare - 17) & 0x88) == 0) {
              distanceSpans[fromSquare][toSquare] = 0;
            }
            for (int squareIndex = fromSquare - 17; squareIndex > toSquare && (squareIndex & 0x88) == 0; squareIndex -= 17) {
              Square square = board.boardSquares[squareIndex].square;
              distanceSpans[fromSquare][toSquare] |= square.mask_on;
              notDistanceSpans[fromSquare][toSquare] &= square.mask_off;
            }
            for (int squareIndex = toSquare - 17; (squareIndex & 0x88) == 0; squareIndex -= 17) {
              Square square = board.boardSquares[squareIndex].square;
              shadowVectors[fromSquare][toSquare] |= square.mask_on;
            }
          }
        }
      }
    }
    System.err.println("Done.\n\n");
  }


  long pieces;
  Piece piece;
  Square pieceSquare;
  int index;
  public int generateMoves(Move[] moves, Board board) {
    index = 0;
    pieces = board.pieceBoards[board.turn][Board.ALL_PIECES];
    while (pieces != 0) {
      pieceSquare = Board.SQUARES[Long.numberOfTrailingZeros(pieces)];
      pieces &= pieceSquare.mask_off;
      piece = board.boardSquares[pieceSquare.index128].piece;
      switch (piece.type) {
        case Piece.PAWN: {
          if (piece.color == 1) {
            index += generateFullWhitePawnMoves(index, moves, board, piece);
          } else {
            index += generateFullBlackPawnMoves(index, moves, board, piece);
          }
          break;
        }
        case Piece.KNIGHT:
        case Piece.BISHOP:
        case Piece.ROOK:
        case Piece.QUEEN: {
          index += generateMovesFromAttacks(index, moves, board, piece);
          break;
        }
        case Piece.KING: {
          if (piece.color == 1) {
            index += generateFullWhiteKingMoves(index, moves, board, piece);
          } else {
            index += generateFullBlackKingMoves(index, moves, board, piece);
          }
          break;
        }
      }
    }
    moves[index].moved = null;
    return index;
  }

  public int generatePawnMoves(Move[] moves, Board board) {
    int index = 0;
    long pieces = board.pieceBoards[board.turn][Board.ALL_PIECES];
    while (pieces != 0) {
      Square pieceSquare = Board.SQUARES[Long.numberOfTrailingZeros(pieces)];
      pieces &= pieceSquare.mask_off;
      Piece piece = board.boardSquares[pieceSquare.index128].piece;
      switch (piece.type) {
        case Piece.PAWN: {
          if (piece.color == 1) {
            index += generateFullWhitePawnMoves(index, moves, board, piece);
          } else {
            index += generateFullBlackPawnMoves(index, moves, board, piece);
          }
          break;
        }
      }
    }
    moves[index].moved = null;
    return index;
  }


  public int generateKnightMoves(int index, Move[] moves, Board board, Piece piece) {
    int originalIndex = index;

    long knightMoves = piece.attacks;

    long capturesBoard = knightMoves & board.pieceBoards[board.turn ^ 1][Board.ALL_PIECES];
    while (capturesBoard != 0) {
      int toSquareIndex = Long.numberOfTrailingZeros(capturesBoard);
      Square square = Board.SQUARES[toSquareIndex];
      capturesBoard &= square.mask_off;
      Board.BoardSquare toSquare = board.boardSquares[square.index128];
      moves[index++].reset(piece.square, toSquare.square, toSquare.square, piece, toSquare.piece);
    }

    capturesBoard = knightMoves & ~board.allPieces;
    while (capturesBoard != 0) {
      int toSquareIndex = Long.numberOfTrailingZeros(capturesBoard);
      Square square = Board.SQUARES[toSquareIndex];
      capturesBoard &= square.mask_off;
      moves[index++].reset(piece.square, square, piece);
    }

    return index - originalIndex;
  }

  public int generateMovesFromAttacks(int index, Move[] moves, Board board, Piece piece) {
    int originalIndex = index;
    Square square;
    long toSquares = piece.attacks & board.pieceBoards[board.turn ^ 1][Board.ALL_PIECES];
    while (toSquares != 0) {
      square = Board.SQUARES[Long.numberOfTrailingZeros(toSquares)];
      toSquares &= square.mask_off;
      moves[index++].reset(piece.square, board.boardSquares[square.index128].square, board.boardSquares[square.index128].square, piece, board.boardSquares[square.index128].piece);
    }

    toSquares = piece.attacks & ~board.allPieces;
    while (toSquares != 0) {
      square = Board.SQUARES[Long.numberOfTrailingZeros(toSquares)];
      toSquares &= square.mask_off;
      moves[index++].reset(piece.square, square, piece);
    }
    return index - originalIndex;
  }

  public int generateKnightChecksFromAttacks(int index, Move[] moves, Board board, Piece piece, Square kingSquare) {
    int originalIndex = index;

    long possibleKnightChecks = MoveGeneration.attackVectors[0][Piece.KNIGHT][kingSquare.index64];

    long slidingMoves = piece.attacks;
    long squares = possibleKnightChecks & slidingMoves & ~board.pieceBoards[board.turn][Board.ALL_PIECES];
    while (squares != 0) {
      int toSquareIndex = Long.numberOfTrailingZeros(squares);
      Square square = Board.SQUARES[toSquareIndex];
      squares &= square.mask_off;
      if((board.pieceBoards[board.turn^1][Board.ALL_PIECES] & square.mask_on) != 0) {
        moves[index++].reset(piece.square, square, square, piece, board.boardSquares[square.index128].piece);
      }
      else {
        moves[index++].reset(piece.square, square, piece);
      }
      moves[index-1].score = Move.CHECK_SCORE - index;
      moves[index-1].check = true;
    }
    return index - originalIndex;
  }

  public int generateBishopQueenChecksFromAttacks(int index, Move[] moves, Board board, Piece piece, Square kingSquare) {
    int originalIndex = index;

    long possibleBishopQueenChecks = board.bishopAttacks(kingSquare.index64);

    long slidingMoves = piece.attacks;

    long squares = possibleBishopQueenChecks & slidingMoves & ~board.pieceBoards[board.turn][Board.ALL_PIECES];
    while (squares != 0) {
      int toSquareIndex = Long.numberOfTrailingZeros(squares);
      Square square = Board.SQUARES[toSquareIndex];
      squares &= square.mask_off;
      if((board.pieceBoards[board.turn^1][Board.ALL_PIECES] & square.mask_on) != 0) {
        moves[index++].reset(piece.square, square, square, piece, board.boardSquares[square.index128].piece);
      }
      else {
        moves[index++].reset(piece.square, square, piece);
      }
      moves[index-1].score = Move.CHECK_SCORE - index;
      moves[index-1].check = true;
    }
    return index - originalIndex;
  }

  public int generateRookQueenChecksFromAttacks(int index, Move[] moves, Board board, Piece piece, Square kingSquare) {
    int originalIndex = index;

    long possibleRookQueenChecks = board.rookAttacks(kingSquare.index64);

    long slidingMoves = piece.attacks;
    long squares = possibleRookQueenChecks & slidingMoves & ~board.pieceBoards[board.turn][Board.ALL_PIECES];
    while (squares != 0) {
      int toSquareIndex = Long.numberOfTrailingZeros(squares);
      Square square = Board.SQUARES[toSquareIndex];
      squares &= square.mask_off;
      if((board.pieceBoards[board.turn^1][Board.ALL_PIECES] & square.mask_on) != 0) {
        moves[index++].reset(piece.square, square, square, piece, board.boardSquares[square.index128].piece);
      }
      else {
        moves[index++].reset(piece.square, square, piece);
      }
      moves[index-1].score = Move.CHECK_SCORE - index;
      moves[index-1].check = true;
    }
    return index - originalIndex;
  }


  public int generateBishopMoves(int index, Move[] moves, Board board, Piece piece) {
    int originalIndex = index;

    long bishopMoves = (board.attacksDiaga1(piece.square.index64) | board.attacksDiagh1(piece.square.index64));
    long capturesBoard = bishopMoves & board.pieceBoards[board.turn ^ 1][Board.ALL_PIECES];
    while (capturesBoard != 0) {
      int toSquareIndex = Long.numberOfTrailingZeros(capturesBoard);
      Square square = Board.SQUARES[toSquareIndex];
      capturesBoard &= square.mask_off;
      Board.BoardSquare toSquare = board.boardSquares[square.index128];
      moves[index++].reset(piece.square, toSquare.square, toSquare.square, piece, toSquare.piece);
    }

    capturesBoard = bishopMoves & ~board.allPieces;
    while (capturesBoard != 0) {
      int toSquareIndex = Long.numberOfTrailingZeros(capturesBoard);
      Square square = Board.SQUARES[toSquareIndex];
      capturesBoard &= square.mask_off;
      moves[index++].reset(piece.square, square, piece);
    }
    return index - originalIndex;
  }

  public int generateRookMoves(int index, Move[] moves, Board board, Piece piece) {
    int originalIndex = index;

    long rookMoves = (board.attacksRank(piece.square.index64) | board.attacksFile(piece.square.index64));
    long capturesBoard = rookMoves & board.pieceBoards[board.turn ^ 1][Board.ALL_PIECES];
    while (capturesBoard != 0) {
      int toSquareIndex = Long.numberOfTrailingZeros(capturesBoard);
      Square square = Board.SQUARES[toSquareIndex];
      capturesBoard &= square.mask_off;
      Board.BoardSquare toSquare = board.boardSquares[square.index128];
      moves[index++].reset(piece.square, toSquare.square, toSquare.square, piece, toSquare.piece);
    }

    capturesBoard = rookMoves & ~board.allPieces;
    while (capturesBoard != 0) {
      int toSquareIndex = Long.numberOfTrailingZeros(capturesBoard);
      Square square = Board.SQUARES[toSquareIndex];
      capturesBoard &= square.mask_off;
      moves[index++].reset(piece.square, square, piece);
    }
    return index - originalIndex;
  }

  public int generateFullMoves(Move[] moves, Board board) {
    int index = 0;
    long pieces = board.pieceBoards[board.turn][Board.ALL_PIECES];
    while (pieces != 0) {
      Square pieceSquare = Board.SQUARES[Long.numberOfTrailingZeros(pieces)];
      Piece piece = board.boardSquares[pieceSquare.index128].piece;
      pieces &= pieceSquare.mask_off;
      switch (piece.type) {
        case Piece.PAWN: {
          if (piece.color == 1) {
            index += generateFullWhitePawnMoves(index, moves, board, piece);
          } else {
            index += generateFullBlackPawnMoves(index, moves, board, piece);
          }
          break;
        }
        case Piece.KNIGHT: {
          index += generateFullKnightMoves(index, moves, board, piece);
          break;
        }
        case Piece.BISHOP: {
          index += generateFullBishopMoves(index, moves, board, piece);
          break;
        }
        case Piece.ROOK: {
          index += generateFullRookMoves(index, moves, board, piece);
          break;
        }
        case Piece.QUEEN: {
          index += generateFullQueenMoves(index, moves, board, piece);
          break;
        }
        case Piece.KING: {
          if (piece.color == 1) {
            index += generateFullWhiteKingMoves(index, moves, board, piece);
          } else {
            index += generateFullBlackKingMoves(index, moves, board, piece);
          }
          break;
        }
      }
    }
    moves[index].moved = null;
    return index;
  }


  public int generateFullWhitePawnMoves(int index, Move[] moves, Board board, Piece piece) {
    int originalIndex = index;
    // up by one
    int squareIndex = piece.square.index128 + 16;
    if ((squareIndex & 0x88) == 0) {
      Board.BoardSquare toSquare = board.boardSquares[squareIndex];
      long squareMask = toSquare.square.mask_on;

      if ((board.allPieces & squareMask) == 0) {
        // Promote
        if ((squareMask & Constants.RANK_8_MASK) != 0) {
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.QUEEN);
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.ROOK);
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.BISHOP);
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.KNIGHT);
        } else {
          moves[index++].reset(piece.square, toSquare.square, piece);
        }

        // up by two
        if ((piece.square.mask_on & Constants.RANK_2_MASK) != 0) {
          squareIndex = piece.square.index128 + 32;
          if ((squareIndex & 0x88) == 0) {
            toSquare = board.boardSquares[squareIndex];
            squareMask = toSquare.square.mask_on;
            if ((board.allPieces & squareMask) == 0) {
              moves[index++].reset(piece.square, toSquare.square, piece, board.boardSquares[piece.square.index128 + 16].square);
            }
          }
        }
      }
    }

    // capture left
    squareIndex = piece.square.index128 + 15;
    if ((squareIndex & 0x88) == 0) {
      Board.BoardSquare toSquare = board.boardSquares[squareIndex];
      long squareMask = toSquare.square.mask_on;

      if ((board.pieceBoards[0][Board.ALL_PIECES] & squareMask) != 0) {
        if ((squareMask & Constants.RANK_8_MASK) != 0) {
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.QUEEN);
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.ROOK);
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.BISHOP);
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.KNIGHT);
        } else {
          moves[index++].reset(piece.square, toSquare.square, toSquare.square, piece, toSquare.piece);
        }
      }
    }

    // capture right
    squareIndex = piece.square.index128 + 17;
    if ((squareIndex & 0x88) == 0) {
      Board.BoardSquare toSquare = board.boardSquares[squareIndex];
      long squareMask = toSquare.square.mask_on;

      if ((board.pieceBoards[0][Board.ALL_PIECES] & squareMask) != 0) {
        if ((squareMask & Constants.RANK_8_MASK) != 0) {
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.QUEEN);
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.ROOK);
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.BISHOP);
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.KNIGHT);
        } else {
          moves[index++].reset(piece.square, toSquare.square, toSquare.square, piece, toSquare.piece);
        }
      }
    }

    index += generateWhiteEnPassentMoves(index, moves, board, piece);

    return index - originalIndex;
  }

  public int generateFullWhitePawnQMoves(int index, Move[] moves, Board board, Piece piece) {
    int originalIndex = index;

    // up by one
    int squareIndex = piece.square.index128 + 16;
    if ((squareIndex & 0x88) == 0) {
      Board.BoardSquare toSquare = board.boardSquares[squareIndex];
      if ((board.allPieces & toSquare.square.mask_on) == 0) {
        // Promote
        if ((toSquare.square.mask_on & Constants.RANK_8_MASK) != 0) {
          int index1 = index;
          int originalIndex1 = index1;
          moves[index1++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.QUEEN);
          index += index1 - originalIndex1;
        }
        else if (board.isEndgame() && (toSquare.square.mask_on & (Constants.RANK_7_MASK | Constants.RANK_6_MASK)) != 0) {
          int index1 = index;
          int originalIndex1 = index1;
          moves[index1++].reset(piece.square, toSquare.square, piece);
          index += index1 - originalIndex1;
        }
      }
    }

    // capture left
    squareIndex = piece.square.index128 + 15;
    if ((squareIndex & 0x88) == 0) {
      Board.BoardSquare toSquare = board.boardSquares[squareIndex];
      if ((board.pieceBoards[0][Board.ALL_PIECES] & toSquare.square.mask_on) != 0) {
        if ((toSquare.square.mask_on & Constants.RANK_8_MASK) != 0) {
          int index1 = index;
          int originalIndex1 = index1;
          moves[index1++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.QUEEN);
          index += index1 - originalIndex1;
        }
        else if (board.isEndgame() && toSquare.square.rank == Constants.RANK_7) {
          int index1 = index;
          int originalIndex1 = index1;
          moves[index1++].reset(piece.square, toSquare.square, toSquare.square, piece, toSquare.piece);
          index += index1 - originalIndex1;
        }
        else {
          moves[index++].reset(piece.square, toSquare.square, toSquare.square, piece, toSquare.piece);
        }
      }
    }

    // capture right
    squareIndex = piece.square.index128 + 17;
    if ((squareIndex & 0x88) == 0) {
      Board.BoardSquare toSquare = board.boardSquares[squareIndex];

      if ((board.pieceBoards[0][Board.ALL_PIECES] & toSquare.square.mask_on) != 0) {
        if ((toSquare.square.mask_on & Constants.RANK_8_MASK) != 0) {
          int index1 = index;
          int originalIndex1 = index1;
          moves[index1++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.QUEEN);
          index += index1 - originalIndex1;
        }
        else if (board.isEndgame() && toSquare.square.rank == Constants.RANK_7) {
          int index1 = index;
          int originalIndex1 = index1;
          moves[index1++].reset(piece.square, toSquare.square, toSquare.square, piece, toSquare.piece);
          index += index1 - originalIndex1;
        }
        else {
          moves[index++].reset(piece.square, toSquare.square, toSquare.square, piece, toSquare.piece);
        }
      }
    }

    index += generateWhiteEnPassentMoves(index, moves, board, piece);

    return index - originalIndex;
  }


  public int generateWhiteEnPassentMoves(int index, Move[] moves, Board board, Piece piece) {
    int originalIndex = index;
    // capture left
    int squareIndex = piece.square.index128 + 15;
    if ((squareIndex & 0x88) == 0) {
      Board.BoardSquare toSquare = board.boardSquares[squareIndex];
      if (toSquare.enPassentInfo[board.moveIndex]) {
        Board.BoardSquare enPassentSquare = board.boardSquares[piece.square.index128 - 1];
        moves[index++].reset(piece.square, toSquare.square, enPassentSquare.square, piece, enPassentSquare.piece);
      }
    }

    // capture right
    squareIndex = piece.square.index128 + 17;
    if ((squareIndex & 0x88) == 0) {
      Board.BoardSquare toSquare = board.boardSquares[squareIndex];
      // en passent
      if (toSquare.enPassentInfo[board.moveIndex]) {
        Board.BoardSquare enPassentSquare = board.boardSquares[piece.square.index128 + 1];
        moves[index++].reset(piece.square, toSquare.square, enPassentSquare.square, piece, enPassentSquare.piece);
      }
    }
    return index - originalIndex;
  }


  public int generateFullBlackPawnMoves(int index, Move[] moves, Board board, Piece piece) {
    int originalIndex = index;
    // up by one
    int squareIndex = piece.square.index128 - 16;
    if ((squareIndex & 0x88) == 0) {
      Board.BoardSquare toSquare = board.boardSquares[squareIndex];
      long squareMask = toSquare.square.mask_on;

      if ((board.allPieces & squareMask) == 0) {
        // Promote
        if ((squareMask & Constants.RANK_1_MASK) != 0) {
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.QUEEN);
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.ROOK);
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.BISHOP);
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.KNIGHT);
        } else {
          moves[index++].reset(piece.square, toSquare.square, piece);
        }

        // up by two
        if ((piece.square.mask_on & Constants.RANK_7_MASK) != 0) {
          squareIndex = piece.square.index128 - 32;
          if ((squareIndex & 0x88) == 0) {
            toSquare = board.boardSquares[squareIndex];
            squareMask = toSquare.square.mask_on;
            if ((board.allPieces & squareMask) == 0) {
              moves[index++].reset(piece.square, toSquare.square, piece, board.boardSquares[piece.square.index128 - 16].square);
            }
          }
        }
      }
    }


    // capture right
    if (((piece.square.index128 - 15) & 0x88) == 0) {
      Board.BoardSquare toSquare = board.boardSquares[piece.square.index128 - 15];
      if (toSquare.piece != null && toSquare.piece.color == 1) {
        if (toSquare.square.rank == Constants.RANK_1) {
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.QUEEN);
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.ROOK);
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.BISHOP);
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.KNIGHT);
        } else {
          moves[index++].reset(piece.square, toSquare.square, toSquare.square, piece, toSquare.piece);
        }
      }
    }

    // capture left
    if (((piece.square.index128 - 17) & 0x88) == 0) {
      Board.BoardSquare toSquare = board.boardSquares[piece.square.index128 - 17];
      if (toSquare.piece != null && toSquare.piece.color == 1) {
        if (toSquare.square.rank == Constants.RANK_1) {
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.QUEEN);
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.ROOK);
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.BISHOP);
          moves[index++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.KNIGHT);
        } else {
          moves[index++].reset(piece.square, toSquare.square, toSquare.square, piece, toSquare.piece);
        }
      }
    }

    index += generateBlackEnPassentMoves(index, moves, board, piece);
    return index - originalIndex;
  }

  public int generateFullBlackPawnQMoves(int index, Move[] moves, Board board, Piece piece) {
    int originalIndex = index;
    // up by one
    int squareIndex = piece.square.index128 - 16;
    if ((squareIndex & 0x88) == 0) {
      Board.BoardSquare toSquare = board.boardSquares[squareIndex];
      long squareMask = toSquare.square.mask_on;

      if ((board.allPieces & squareMask) == 0) {
        // Promote
        if ((squareMask & Constants.RANK_1_MASK) != 0) {
          int index1 = index;
          int originalIndex1 = index1;
          moves[index1++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.QUEEN);
          index += index1 - originalIndex1;
        }
        else if (board.isEndgame() && (toSquare.square.mask_on & (Constants.RANK_2_MASK | Constants.RANK_3_MASK)) != 0) {
          int index1 = index;
          int originalIndex1 = index1;
          moves[index1++].reset(piece.square, toSquare.square, piece);
          index += index1 - originalIndex1;
        }
      }
    }

    // capture right
    if (((piece.square.index128 - 15) & 0x88) == 0) {
      Board.BoardSquare toSquare = board.boardSquares[piece.square.index128 - 15];
      if (toSquare.piece != null && toSquare.piece.color == 1) {
        if (toSquare.square.rank == Constants.RANK_1) {
          int index1 = index;
          int originalIndex1 = index1;
          moves[index1++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.QUEEN);
          index += index1 - originalIndex1;
        }
        else if (board.isEndgame() && toSquare.square.rank == Constants.RANK_2) {
          int index1 = index;
          int originalIndex1 = index1;
          moves[index1++].reset(piece.square, toSquare.square, toSquare.square, piece, toSquare.piece);
          index += index1 - originalIndex1;
        }
        else {
          moves[index++].reset(piece.square, toSquare.square, toSquare.square, piece, toSquare.piece);
        }
      }
    }

    // capture left
    if (((piece.square.index128 - 17) & 0x88) == 0) {
      Board.BoardSquare toSquare = board.boardSquares[piece.square.index128 - 17];
      if (toSquare.piece != null && toSquare.piece.color == 1) {
        if (toSquare.square.rank == Constants.RANK_1) {
          int index1 = index;
          int originalIndex1 = index1;
          moves[index1++].reset(piece.square, toSquare.square, piece, toSquare.square, toSquare.piece, Piece.QUEEN);
          index += index1 - originalIndex1;
        }
        else if (board.isEndgame() && toSquare.square.rank == Constants.RANK_2) {
          int index1 = index;
          int originalIndex1 = index1;
          moves[index1++].reset(piece.square, toSquare.square, toSquare.square, piece, toSquare.piece);
          index += index1 - originalIndex1;
        }
        else {
          moves[index++].reset(piece.square, toSquare.square, toSquare.square, piece, toSquare.piece);
        }
      }
    }

    index += generateBlackEnPassentMoves(index, moves, board, piece);
    return index - originalIndex;
  }


  public int generateBlackEnPassentMoves(int index, Move[] moves, Board board, Piece piece) {
    int originalIndex = index;
    // capture right
    if (((piece.square.index128 - 15) & 0x88) == 0) {
      Board.BoardSquare toSquare = board.boardSquares[piece.square.index128 - 15];
      // en passent
      if (toSquare.enPassentInfo[board.moveIndex]) {
        Board.BoardSquare enPassentSquare = board.boardSquares[piece.square.index128 + 1];
        moves[index++].reset(piece.square, toSquare.square, enPassentSquare.square, piece, enPassentSquare.piece);
      }
    }

    // capture left
    if (((piece.square.index128 - 17) & 0x88) == 0) {
      Board.BoardSquare toSquare = board.boardSquares[piece.square.index128 - 17];
      // en passent
      if (toSquare.enPassentInfo[board.moveIndex]) {
        Board.BoardSquare enPassentSquare = board.boardSquares[piece.square.index128 - 1];
        moves[index++].reset(piece.square, toSquare.square, enPassentSquare.square, piece, enPassentSquare.piece);
      }
    }
    return index - originalIndex;
  }


  public int generateFullKnightMoves(int index, Move[] moves, Board board, Piece piece) {
    int originalIndex = index;
    for (int x = 0; x < knightMoves.length; x++) {
      int i = knightMoves[x];
      if (((piece.square.index128 + i) & 0x88) != 0) {
        continue;
      }

      Board.BoardSquare toSquare = board.boardSquares[piece.square.index128 + i];
      if (toSquare.piece != null) {
        if (toSquare.piece.color != piece.color) {
          moves[index++].reset(piece.square, toSquare.square, toSquare.square, piece, toSquare.piece);
        }
      } else {
        moves[index++].reset(piece.square, toSquare.square, piece);
      }
    }

    return index - originalIndex;
  }


  public int generateFullBishopMoves(int index, Move[] moves, Board board, Piece piece) {
    int originalIndex = index;
    for (int x = 0; x < bishopMoves.length; x++) {
      int i = bishopMoves[x];
      int squareIndex = piece.square.index128 + i;
      while ((squareIndex & 0x88) == 0) {
        Board.BoardSquare toSquare = board.boardSquares[squareIndex];
        long squareMask = toSquare.square.mask_on;
        // If we run into a piece
        if ((board.allPieces & squareMask) != 0) {
          // If the piece is not our color
          if ((board.pieceBoards[piece.color ^ 1][Board.ALL_PIECES] & squareMask) != 0) {
            moves[index++].reset(piece.square, toSquare.square, toSquare.square, piece, toSquare.piece);
          }
          break;
        } else {
          moves[index++].reset(piece.square, toSquare.square, piece);
        }
        squareIndex += i;
      }
    }
    return index - originalIndex;
  }


  public int generateFullRookMoves(int index, Move[] moves, Board board, Piece piece) {
    int originalIndex = index;
    for (int x = 0; x < rookMoves.length; x++) {
      int i = rookMoves[x];
      int squareIndex = piece.square.index128 + i;
      while ((squareIndex & 0x88) == 0) {
        Board.BoardSquare toSquare = board.boardSquares[squareIndex];
        long squareMask = toSquare.square.mask_on;
        // If we run into a piece
        if ((board.allPieces & squareMask) != 0) {
          // If the piece is not our color
          if ((board.pieceBoards[piece.color ^ 1][Board.ALL_PIECES] & squareMask) != 0) {
            moves[index++].reset(piece.square, toSquare.square, toSquare.square, piece, toSquare.piece);
          }
          break;
        } else {
          moves[index++].reset(piece.square, toSquare.square, piece);
        }
        squareIndex += i;
      }
    }
    return index - originalIndex;
  }


  public int generateFullQueenMoves(int index, Move[] moves, Board board, Piece piece) {
    int originalIndex = index;
    for (int x = 0; x < queenMoves.length; x++) {
      int i = queenMoves[x];
      int squareIndex = piece.square.index128 + i;
      while ((squareIndex & 0x88) == 0) {
        Board.BoardSquare toSquare = board.boardSquares[squareIndex];
        // If we run into a piece
        if (toSquare.piece != null) {
          // If the piece is not our color
          if (toSquare.piece.color != piece.color) {
            moves[index++].reset(piece.square, toSquare.square, toSquare.square, piece, toSquare.piece);
          }
          break;
        } else {
          moves[index++].reset(piece.square, toSquare.square, piece);
        }
        squareIndex += i;
      }
    }
    return index - originalIndex;
  }


  public final int generateFullWhiteKingMoves(int index, Move[] moves, Board board, Piece piece) {
    int originalIndex = index;
    index += generateNormalKingMoves(index, moves, board, piece);
    index += generateWhiteCastlingMoves(index, moves, board, piece);

    return index - originalIndex;
  }


  public final int generateWhiteCastlingMoves(int index, Move[] moves, Board board, Piece piece) {
    int originalIndex = index;
    if (piece.square == Square.E1) {
      if (board.stats.whiteKingMoves == 0) {
        if (board.stats.whiteKingsideRookMoves == 0) {
          Piece castledRook = board.boardSquares[Square.H1.index128].piece;
          if (castledRook != null
                  && board.boardSquares[Square.F1.index128].piece == null
                  && board.boardSquares[Square.G1.index128].piece == null
                  && !board.isSquareCheckedByColor(Square.E1, 0)
                  && !board.isSquareCheckedByColor(Square.F1, 0)
                  && !board.isSquareCheckedByColor(Square.G1, 0)) {
            moves[index++].reset(piece.square, Square.G1, piece, castledRook, Square.H1, Square.F1);
          }
        }
        if (board.stats.whiteQueensideRookMoves == 0) {
          Piece castledRook = board.boardSquares[Square.A1.index128].piece;
          if (castledRook != null
                  && board.boardSquares[Square.D1.index128].piece == null
                  && board.boardSquares[Square.C1.index128].piece == null
                  && board.boardSquares[Square.B1.index128].piece == null
                  && !board.isSquareCheckedByColor(Square.E1, 0)
                  && !board.isSquareCheckedByColor(Square.D1, 0)
                  && !board.isSquareCheckedByColor(Square.C1, 0)) {
            moves[index++].reset(piece.square, Square.C1, piece, castledRook, Square.A1, Square.D1);
          }
        }
      }
    }
    return index - originalIndex;
  }


  public final int generateFullBlackKingMoves(int index, Move[] moves, Board board, Piece piece) {
    int originalIndex = index;
    index += generateNormalKingMoves(index, moves, board, piece);
    index += generateBlackCastlingMoves(index, moves, board, piece);
    return index - originalIndex;
  }


  public final int generateBlackCastlingMoves(int index, Move[] moves, Board board, Piece piece) {
    int originalIndex = index;
    // Castling
    if (piece.square == Square.E8) {
      if (board.stats.blackKingMoves == 0) {
        if (board.stats.blackKingsideRookMoves == 0) {
          Piece castledRook = board.boardSquares[Square.H8.index128].piece;
          if (castledRook != null
                  && board.boardSquares[Square.F8.index128].piece == null
                  && board.boardSquares[Square.G8.index128].piece == null
                  && !board.isSquareCheckedByColor(Square.E8, 1)
                  && !board.isSquareCheckedByColor(Square.F8, 1)
                  && !board.isSquareCheckedByColor(Square.G8, 1)) {
            moves[index++].reset(piece.square, Square.G8, piece, castledRook, Square.H8, Square.F8);
          }
        }
        if (board.stats.blackQueensideRookMoves == 0) {
          Piece castledRook = board.boardSquares[Square.A8.index128].piece;
          if (castledRook != null
                  && board.boardSquares[Square.D8.index128].piece == null
                  && board.boardSquares[Square.C8.index128].piece == null
                  && board.boardSquares[Square.B8.index128].piece == null
                  && !board.isSquareCheckedByColor(Square.E8, 1)
                  && !board.isSquareCheckedByColor(Square.D8, 1)
                  && !board.isSquareCheckedByColor(Square.C8, 1)) {
            moves[index++].reset(piece.square, Square.C8, piece, castledRook, Square.A8, Square.D8);
          }
        }
      }
    }
    return index - originalIndex;
  }


  private int generateNormalKingMoves(int index, Move[] moves, Board board, Piece piece) {
    int originalIndex = index;
    for (int x = 0; x < kingMoves.length; x++) {
      int i = kingMoves[x];
      // Be sure the destination square is on the board
      if (((piece.square.index128 + i) & 0x88) == 0) {
        Board.BoardSquare toSquare = board.boardSquares[piece.square.index128 + i];
        // If we run into a piece
        if (toSquare.piece != null) {
          // If the piece is not our color
          if (toSquare.piece.color != piece.color) {
            if (board.isSquareCheckedByColor(toSquare.square, piece.color ^ 1)) {
              continue;
            }
            moves[index++].reset(piece.square, toSquare.square, toSquare.square, piece, toSquare.piece);
          }
        } else {
          if (board.isSquareCheckedByColor(toSquare.square, piece.color ^ 1)) {
            continue;
          }
          moves[index++].reset(piece.square, toSquare.square, piece);
        }
      }
    }
    return index - originalIndex;
  }

  // Evasion generation
  public int generateEvasions(int index, Move[] moves, Board board) {
    long checkers;
    Square kingSquare;
    // generate king moves
    if (board.turn == 1) {
      index += generateFullWhiteKingMoves(index, moves, board, board.whiteKing);
      kingSquare = board.whiteKing.square;

    } else {
      index += generateFullBlackKingMoves(index, moves, board, board.blackKing);
      kingSquare = board.blackKing.square;
    }
    checkers = board.squareAttackers[kingSquare.index64] & board.pieceBoards[board.turn ^ 1][6];

    while (checkers != 0) {
      int attackerSquareIndex = Long.numberOfTrailingZeros(checkers);
      Square attackerSquare = Board.SQUARES[attackerSquareIndex];
      checkers ^= attackerSquare.mask_on;

      if (checkers != 0) {
        break;
      }

      // include king moves and moves where tosquare is in the checkvector, or takenSq == checkDquare
      long checkVector = distanceSpans[kingSquare.index128][attackerSquare.index128] | attackerSquare.mask_on;
      // for each square in check vector
      while (checkVector != 0) {
        int squareIndex = Long.numberOfTrailingZeros(checkVector);
        Square savingSquare = Board.SQUARES[squareIndex];
        checkVector ^= savingSquare.mask_on;
        // find all attacking pieces
        long defenders = board.squareAttackers[savingSquare.index64] & board.pieceBoards[board.turn][6];
        while (defenders != 0) {
          int defenderSquareIndex = Long.numberOfTrailingZeros(defenders);
          Square defenderSquare = Board.SQUARES[defenderSquareIndex];
          defenders ^= defenderSquare.mask_on;

          // generate their moves
          Piece moved = board.boardSquares[defenderSquare.index128].piece;
          // include pawn promotions
          if (moved.type == Piece.PAWN) {
            Piece taken = board.boardSquares[savingSquare.index128].piece;
            if (taken != null) {
              if (savingSquare.rank == 7 || savingSquare.rank == 0) {
                int index1 = index;
                int originalIndex = index1;
                moves[index1++].reset(moved.square, board.boardSquares[savingSquare.index128].square, moved, board.boardSquares[savingSquare.index128].square, board.boardSquares[savingSquare.index128].piece, Piece.QUEEN);
                moves[index1++].reset(moved.square, board.boardSquares[savingSquare.index128].square, moved, board.boardSquares[savingSquare.index128].square, board.boardSquares[savingSquare.index128].piece, Piece.ROOK);
                moves[index1++].reset(moved.square, board.boardSquares[savingSquare.index128].square, moved, board.boardSquares[savingSquare.index128].square, board.boardSquares[savingSquare.index128].piece, Piece.BISHOP);
                moves[index1++].reset(moved.square, board.boardSquares[savingSquare.index128].square, moved, board.boardSquares[savingSquare.index128].square, board.boardSquares[savingSquare.index128].piece, Piece.KNIGHT);
                index += index1 - originalIndex;
              } else {
                moves[index++].reset(moved.square, savingSquare, savingSquare, moved, taken);
              }
            }
          } else if (moved.type == Piece.KING) {
            continue;
          } else {
            Piece taken = board.boardSquares[savingSquare.index128].piece;
            if (taken != null) {
              moves[index++].reset(moved.square, savingSquare, savingSquare, moved, taken);
            } else {
              moves[index++].reset(moved.square, savingSquare, moved);
            }
          }
        }

        // check normal pawn moves
        if (board.turn == 1) {
          if (savingSquare.rank > 0 && board.boardSquares[savingSquare.index128].piece == null) {
            if ((board.pieceBoards[1][Piece.PAWN] & Board.SQUARES[savingSquare.index64 - 8].mask_on) != 0) {
              Piece moved = board.boardSquares[Board.SQUARES[savingSquare.index64 - 8].index128].piece;
              if (savingSquare.rank == 7 || savingSquare.rank == 0) {
                int index1 = index;
                int originalIndex = index1;
                moves[index1++].reset(moved.square, board.boardSquares[savingSquare.index128].square, moved, board.boardSquares[savingSquare.index128].square, board.boardSquares[savingSquare.index128].piece, Piece.QUEEN);
                moves[index1++].reset(moved.square, board.boardSquares[savingSquare.index128].square, moved, board.boardSquares[savingSquare.index128].square, board.boardSquares[savingSquare.index128].piece, Piece.ROOK);
                moves[index1++].reset(moved.square, board.boardSquares[savingSquare.index128].square, moved, board.boardSquares[savingSquare.index128].square, board.boardSquares[savingSquare.index128].piece, Piece.BISHOP);
                moves[index1++].reset(moved.square, board.boardSquares[savingSquare.index128].square, moved, board.boardSquares[savingSquare.index128].square, board.boardSquares[savingSquare.index128].piece, Piece.KNIGHT);
                index += index1 - originalIndex;
              } else {
                moves[index++].reset(moved.square, savingSquare, moved);
              }
            } else if (savingSquare.rank == 3 &&
                    (board.allPieces & Board.SQUARES[savingSquare.index64 - 8].mask_on) == 0 &&
                    (board.pieceBoards[1][Piece.PAWN] & Board.SQUARES[savingSquare.index64 - 16].mask_on) != 0) {
              Piece moved = board.boardSquares[Board.SQUARES[savingSquare.index64 - 16].index128].piece;
              moves[index++].reset(moved.square, savingSquare, moved, Board.SQUARES[savingSquare.index64 - 8]);
            }
          } else {
            //  check for enpassent moves
            if (savingSquare.file > 0) {
              Piece piece = board.boardSquares[savingSquare.index128 - 1].piece;
              if (piece != null && piece.type == Piece.PAWN && piece.color == 1 && board.boardSquares[savingSquare.index128 + 16].enPassentInfo[board.moveIndex]) {
                moves[index++].reset(piece.square, board.boardSquares[savingSquare.index128 + 16].square, savingSquare, piece, board.boardSquares[savingSquare.index128].piece);
              }
            }

            if (savingSquare.file < 7) {
              Piece piece = board.boardSquares[savingSquare.index128 + 1].piece;
              if (piece != null && piece.type == Piece.PAWN && piece.color == 1 && board.boardSquares[savingSquare.index128 + 16].enPassentInfo[board.moveIndex]) {
                moves[index++].reset(piece.square, board.boardSquares[savingSquare.index128 + 16].square, savingSquare, piece, board.boardSquares[savingSquare.index128].piece);
              }
            }
          }
        } else {
          if (savingSquare.rank < 7 && board.boardSquares[savingSquare.index128].piece == null) {
            if ((board.pieceBoards[0][Piece.PAWN] & Board.SQUARES[savingSquare.index64 + 8].mask_on) != 0) {
              Piece moved = board.boardSquares[Board.SQUARES[savingSquare.index64 + 8].index128].piece;
              if (savingSquare.rank == 7 || savingSquare.rank == 0) {
                int index1 = index;
                int originalIndex = index1;
                moves[index1++].reset(moved.square, board.boardSquares[savingSquare.index128].square, moved, board.boardSquares[savingSquare.index128].square, board.boardSquares[savingSquare.index128].piece, Piece.QUEEN);
                moves[index1++].reset(moved.square, board.boardSquares[savingSquare.index128].square, moved, board.boardSquares[savingSquare.index128].square, board.boardSquares[savingSquare.index128].piece, Piece.ROOK);
                moves[index1++].reset(moved.square, board.boardSquares[savingSquare.index128].square, moved, board.boardSquares[savingSquare.index128].square, board.boardSquares[savingSquare.index128].piece, Piece.BISHOP);
                moves[index1++].reset(moved.square, board.boardSquares[savingSquare.index128].square, moved, board.boardSquares[savingSquare.index128].square, board.boardSquares[savingSquare.index128].piece, Piece.KNIGHT);
                index += index1 - originalIndex;
              } else {
                moves[index++].reset(moved.square, savingSquare, moved);
              }
            } else if (savingSquare.rank == 4 &&
                    (board.allPieces & Board.SQUARES[savingSquare.index64 + 8].mask_on) == 0 &&
                    (board.pieceBoards[0][Piece.PAWN] & Board.SQUARES[savingSquare.index64 + 16].mask_on) != 0) {
              Piece moved = board.boardSquares[Board.SQUARES[savingSquare.index64 + 16].index128].piece;
              moves[index++].reset(moved.square, savingSquare, moved, Board.SQUARES[savingSquare.index64 + 8]);
            }
          }
          //  check for enpassent moves
          if (savingSquare.file > 0) {
            Piece piece = board.boardSquares[savingSquare.index128 - 1].piece;
            if (piece != null && piece.type == Piece.PAWN && piece.color == 1 && board.boardSquares[savingSquare.index128 - 16].enPassentInfo[board.moveIndex]) {
              moves[index++].reset(piece.square, board.boardSquares[savingSquare.index128 - 16].square, savingSquare, piece, board.boardSquares[savingSquare.index128].piece);
            }
          }

          if (savingSquare.file < 7) {
            Piece piece = board.boardSquares[savingSquare.index128 + 1].piece;
            if (piece != null && piece.type == Piece.PAWN && piece.color == 1 && board.boardSquares[savingSquare.index128 - 16].enPassentInfo[board.moveIndex]) {
              moves[index++].reset(piece.square, board.boardSquares[savingSquare.index128 - 16].square, savingSquare, piece, board.boardSquares[savingSquare.index128].piece);
            }
          }
        }
      }
    }

    // generate enpassent moves
    moves[index].moved = null;

    return index;
  }

  public int generateChecks(int index, Move[] moves, Board board) {
    Square kingSquare;
    // generate king moves
    if (board.turn == 1) {
      kingSquare = board.blackKing.square;

    } else {
      kingSquare = board.whiteKing.square;
    }

    long pieces = board.pieceBoards[board.turn][Board.ALL_PIECES];
    while (pieces != 0) {
      Square pieceSquare = Board.SQUARES[Long.numberOfTrailingZeros(pieces)];
      pieces &= pieceSquare.mask_off;
      Piece piece = board.boardSquares[pieceSquare.index128].piece;

      switch (piece.type) {
/*
        case Piece.PAWN: {
*/
/*
          if (piece.color == 1) {
            index += generateWhitePawnChecks(index, moves, board, piece);
          } else {
            index += generateBlackPawnChecks(index, moves, board, piece);
          }
*//*

          break;
        }
*/
        case Piece.KNIGHT:
          index += generateKnightChecksFromAttacks(index, moves, board, piece, kingSquare);
          break;
        case Piece.BISHOP:
          index += generateBishopQueenChecksFromAttacks(index, moves, board, piece, kingSquare);
          break;
        case Piece.ROOK:
          index += generateRookQueenChecksFromAttacks(index, moves, board, piece, kingSquare);
          break;
        case Piece.QUEEN: {
          index += generateBishopQueenChecksFromAttacks(index, moves, board, piece, kingSquare);
          index += generateRookQueenChecksFromAttacks(index, moves, board, piece, kingSquare);
          break;
        }
      }
    }
    moves[index].moved = null;

    return index;
  }


  //*******************************************************
  //***   GET ATTACKERS
  //*******************************************************

  /*
    public long getAllAttackers(Board board, Square square, int color) {
      long bishopAndQueenAttackers = getBishopAndQueenXRayAttackers(board, square, color);
      long rookAndQueenAttackers = getRookAndQueenXRayAttackers(board, square, color);

      return (attackVectors[color][Piece.PAWN][square.index64] & board.pieceBoards[color][Piece.PAWN]) |
             (attackVectors[color][Piece.KNIGHT][square.index64] & board.pieceBoards[color][Piece.KNIGHT]) |
             bishopAndQueenAttackers |
             rookAndQueenAttackers |
             (attackVectors[color][Piece.KING][square.index64] & board.pieceBoards[color][Piece.KING]);
    }
  */
  public static final long getAllAttackers(Board board, Square square, int color) {
    long attackers = (attackVectors[color][Piece.PAWN][square.index64] & board.pieceBoards[color][Piece.PAWN]) |
            (attackVectors[color][Piece.KNIGHT][square.index64] & board.pieceBoards[color][Piece.KNIGHT]) |
            (attackVectors[color][Piece.KING][square.index64] & board.pieceBoards[color][Piece.KING]);
    Square attackerSquare;
    long candidateAttackers;
    long rooksAndQueens = board.pieceBoards[color][Piece.QUEEN] | board.pieceBoards[color][Piece.ROOK];
    long bishopsAndQueens = board.pieceBoards[color][Piece.QUEEN] | board.pieceBoards[color][Piece.BISHOP];
    long notRooksAndQueens = board.allPieces ^ rooksAndQueens;
    long notBishopsAndQueens = board.allPieces ^ bishopsAndQueens;

    for (candidateAttackers = attackVectors[color][Piece.BISHOP][square.index64] & bishopsAndQueens;
         candidateAttackers != 0;
         candidateAttackers ^= attackerSquare.mask_on) {
      attackerSquare = Board.SQUARES[Long.numberOfTrailingZeros(candidateAttackers)];
      if ((notBishopsAndQueens & distanceSpans[square.index128][attackerSquare.index128]) == 0) {
        attackers |= attackerSquare.mask_on;
      }
    }

/*
    long attackers = (board.pieceBoards[color][Piece.BISHOP] | board.pieceBoards[color][Piece.QUEEN]) & (board.attacksDiaga1(square.index64) | board.attacksDiagh1(square.index64));
    attackers |= (board.pieceBoards[color][Piece.ROOK] | board.pieceBoards[color][Piece.QUEEN]) & (board.attacksFile(square.index64) | board.attacksRank(square.index64));
*/

    for (candidateAttackers = attackVectors[color][Piece.ROOK][square.index64] & rooksAndQueens;
         candidateAttackers != 0;
         candidateAttackers ^= attackerSquare.mask_on) {
      attackerSquare = Board.SQUARES[Long.numberOfTrailingZeros(candidateAttackers)];
      if ((notRooksAndQueens & distanceSpans[square.index128][attackerSquare.index128]) == 0) {
        attackers |= attackerSquare.mask_on;
      }
    }

    return attackers;
  }

  public long getBishopAndQueenXRayAttackers(Board board, Square square, int color) {
    long attackers, eachAttacker;

    attackers = eachAttacker = (board.pieceBoards[color][Piece.BISHOP] | board.pieceBoards[color][Piece.QUEEN]) &
            (board.attacksDiaga1(square.index64) | board.attacksDiagh1(square.index64));


    while (eachAttacker != 0) {
      Square pieceSquare = Board.SQUARES[Long.numberOfTrailingZeros(eachAttacker)];
      eachAttacker &= pieceSquare.mask_off;
      long xrayAttackers = (board.pieceBoards[color][Piece.BISHOP] | board.pieceBoards[color][Piece.QUEEN]) &
              (board.attacksDiaga1(pieceSquare.index64) | board.attacksDiagh1(pieceSquare.index64)) &
              shadowVectors[square.index128][pieceSquare.index128];
      attackers |= xrayAttackers;
      eachAttacker |= xrayAttackers;
    }

    return attackers;
  }

  public long getRookAndQueenXRayAttackers(Board board, Square square, int color) {
    long attackers, eachAttacker;

    attackers = eachAttacker = (board.pieceBoards[color][Piece.ROOK] | board.pieceBoards[color][Piece.QUEEN]) &
            (board.attacksRank(square.index64) | board.attacksFile(square.index64));


    while (eachAttacker != 0) {
      Square pieceSquare = Board.SQUARES[Long.numberOfTrailingZeros(eachAttacker)];
      eachAttacker &= pieceSquare.mask_off;
      long xrayAttackers = (board.pieceBoards[color][Piece.ROOK] | board.pieceBoards[color][Piece.QUEEN]) &
              (board.attacksRank(pieceSquare.index64) | board.attacksFile(pieceSquare.index64)) &
              shadowVectors[square.index128][pieceSquare.index128];
      attackers |= xrayAttackers;
      eachAttacker |= xrayAttackers;
    }

    return attackers;
  }

  public long getNewAttackers(Board board, Square emptySquare, Square square, int color) {
    long bishopAndQueenAttackers = 0;
    Square attackerSquare;
    for (long bishopAttacker = board.squareAttackers[emptySquare.index64] &
            (board.pieceBoards[color][Piece.BISHOP] | board.pieceBoards[color][Piece.QUEEN]) &
            shadowVectors[square.index128][emptySquare.index128];
         bishopAttacker != 0;
         bishopAttacker ^= attackerSquare.mask_on) {
      int squareIndex = Long.numberOfTrailingZeros(bishopAttacker);
      attackerSquare = Board.SQUARES[squareIndex];
      bishopAndQueenAttackers |= attackerSquare.mask_on;
    }

    long rookAndQueenAttackers = 0;
    for (long rookAttacker = board.squareAttackers[emptySquare.index64] &
            (board.pieceBoards[color][Piece.ROOK] | board.pieceBoards[color][Piece.QUEEN]) &
            shadowVectors[square.index128][emptySquare.index128];
         rookAttacker != 0;
         rookAttacker ^= attackerSquare.mask_on) {
      int squareIndex = Long.numberOfTrailingZeros(rookAttacker);
      attackerSquare = Board.SQUARES[squareIndex];
      rookAndQueenAttackers |= attackerSquare.mask_on;
    }

    return bishopAndQueenAttackers | rookAndQueenAttackers;
  }

  public long getAttackersNoXRay(Board board, Square square, int color) {
    return (attackVectors[color][Piece.PAWN][square.index64] & board.pieceBoards[color][Piece.PAWN]) |
            (attackVectors[color][Piece.KNIGHT][square.index64] & board.pieceBoards[color][Piece.KNIGHT]) |
            (((board.attacksDiaga1(square.index64) | board.attacksDiagh1(square.index64)) & (board.pieceBoards[color][Piece.QUEEN] | board.pieceBoards[color][Piece.BISHOP]))) |
            (((board.attacksRank(square.index64) | board.attacksFile(square.index64)) & (board.pieceBoards[color][Piece.QUEEN] | board.pieceBoards[color][Piece.ROOK]))) |
            (attackVectors[color][Piece.KING][square.index64] & board.pieceBoards[color][Piece.KING]);
  }

  public long getAllAttackVectors(Board board, Square square, int color) {
    return attackVectors[color][Piece.PAWN][square.index64] & board.pieceBoards[color][Piece.PAWN] |
            attackVectors[color][Piece.KNIGHT][square.index64] & board.pieceBoards[color][Piece.KNIGHT] |
            getBishopAndQueenAttackVectors(board, square, color) |
            getRookAndQueenAttackVectors(board, square, color) |
            attackVectors[color][Piece.KING][square.index64] & board.pieceBoards[color][Piece.KING];
  }

  public long getPawnAttackVectors(Board board, Square square, int color) {
    return attackVectors[color][Piece.PAWN][square.index64] & board.pieceBoards[color][Piece.PAWN];
  }

  public long getKingAttackVectors(Board board, Square square, int color) {
    return attackVectors[color][Piece.KING][square.index64] & board.pieceBoards[color][Piece.KING];
  }

  public long getKnightAttackVectors(Board board, Square square, int color) {
    return attackVectors[color][Piece.KNIGHT][square.index64] & board.pieceBoards[color][Piece.KNIGHT];
  }

  public long getBishopAndQueenAttackVectors(Board board, Square square, int color) {
    long bishopAttackVectors = 0;
    long attackVector = attackVectors[color][Piece.BISHOP][square.index64];
    Square attackerSquare = null;
    for (long bishopAttacker = attackVector & (board.pieceBoards[color][Piece.BISHOP] | board.pieceBoards[color][Piece.QUEEN]);
         bishopAttacker != 0;
         bishopAttacker ^= attackerSquare.mask_on) {
      int squareIndex = Long.numberOfTrailingZeros(bishopAttacker);
      attackerSquare = Board.SQUARES[squareIndex];
      long distanceSpan = distanceSpans[square.index128][attackerSquare.index128];
      if ((board.allPieces & distanceSpan) == 0) {
        bishopAttackVectors |= (attackerSquare.mask_on | distanceSpan);
      }
    }
    return bishopAttackVectors;
  }

  public long getRookAndQueenAttackVectors(Board board, Square square, int color) {
    long rookAttackVectors = 0;
    long attackVector = attackVectors[color][Piece.ROOK][square.index64];
    Square attackerSquare = null;
    for (long rookAttacker = attackVector & (board.pieceBoards[color][Piece.ROOK] | board.pieceBoards[color][Piece.QUEEN]);
         rookAttacker != 0;
         rookAttacker ^= attackerSquare.mask_on) {
      int squareIndex = Long.numberOfTrailingZeros(rookAttacker);
      attackerSquare = Board.SQUARES[squareIndex];
      long distanceSpan = distanceSpans[square.index128][attackerSquare.index128];
      if ((board.allPieces & distanceSpan) == 0) {
        rookAttackVectors |= (attackerSquare.mask_on | distanceSpan);
      }
    }
    return rookAttackVectors;
  }

  public int countPawnAttackers(Board board, Square square, int color) {
    return Long.bitCount(attackVectors[color][Piece.KING][square.index64] & board.pieceBoards[color][Piece.PAWN]);
  }

  public int countKingAttackers(Board board, Square square, int color) {
    return Long.bitCount(attackVectors[color][Piece.KING][square.index64] & board.pieceBoards[color][Piece.KING]);
  }

  public int countKnightAttackers(Board board, Square square, int color) {
    return Long.bitCount(attackVectors[color][Piece.KNIGHT][square.index64] & board.pieceBoards[color][Piece.KNIGHT]);
  }

  public int countBishopAndQueenAttackers(Board board, Square square, int color) {
    int bishopAttackers = 0;
    Square attackerSquare = null;
    for (long bishopAttacker = attackVectors[color][Piece.BISHOP][square.index64] & (board.pieceBoards[color][Piece.BISHOP] | board.pieceBoards[color][Piece.QUEEN]);
         bishopAttacker != 0;
         bishopAttacker ^= attackerSquare.mask_on) {
      int squareIndex = Long.numberOfTrailingZeros(bishopAttacker);
      attackerSquare = Board.SQUARES[squareIndex];
      long distanceSpan = distanceSpans[square.index128][attackerSquare.index128];
      if (distanceSpan != -1 && ((board.allPieces ^ (board.pieceBoards[color][Piece.BISHOP] | board.pieceBoards[color][Piece.QUEEN])) & distanceSpan) == 0) {
        bishopAttackers++;
      }
    }
    return bishopAttackers;
  }

  public int countRookAndQueenAttackers(Board board, Square square, int color) {
    int rookAttackers = 0;
    Square attackerSquare = null;
    for (long rookAttacker = attackVectors[color][Piece.ROOK][square.index64] & (board.pieceBoards[color][Piece.ROOK] | board.pieceBoards[color][Piece.QUEEN]);
         rookAttacker != 0;
         rookAttacker ^= attackerSquare.mask_on) {
      int squareIndex = Long.numberOfTrailingZeros(rookAttacker);
      attackerSquare = Board.SQUARES[squareIndex];
      long distanceSpan = distanceSpans[square.index128][attackerSquare.index128];
      if (distanceSpan != -1 && ((board.allPieces ^ (board.pieceBoards[color][Piece.QUEEN] | board.pieceBoards[color][Piece.ROOK])) & distanceSpan) == 0) {
        rookAttackers++;
      }
    }
    return rookAttackers;
  }

  //*******************************************************
  //***   Captures
  //*******************************************************


  public int generateCaptures(int index, Move[] moves, Board board) {
    long pieces = board.pieceBoards[board.turn][Board.ALL_PIECES];
    while (pieces != 0) {

      Square pieceSquare = Board.SQUARES[Long.numberOfTrailingZeros(pieces)];
      pieces ^= pieceSquare.mask_on;
      Piece piece = board.boardSquares[pieceSquare.index128].piece;

      if (piece.type == Piece.PAWN) {
        if (piece.color == 1) {
          index += generateFullWhitePawnQMoves(index, moves, board, piece);
        } else {
          index += generateFullBlackPawnQMoves(index, moves, board, piece);
        }
      } else if(piece.type == Piece.KING) {
        long attacks = piece.attacks & board.pieceBoards[board.turn ^ 1][Board.ALL_PIECES];
        while (attacks != 0) {
          Square toSquare = Board.SQUARES[Long.numberOfTrailingZeros(attacks)];
          attacks ^= toSquare.mask_on;
          if(!board.isSquareCheckedByColor(toSquare, board.turn ^ 1)) {
            moves[index++].reset(pieceSquare, toSquare, toSquare, piece, board.boardSquares[toSquare.index128].piece);
          }
        }
      } else {
        long attacks = piece.attacks & board.pieceBoards[board.turn ^ 1][Board.ALL_PIECES];
        while (attacks != 0) {
          Square toSquare = Board.SQUARES[Long.numberOfTrailingZeros(attacks)];
          attacks ^= toSquare.mask_on;
          moves[index++].reset(pieceSquare, toSquare, toSquare, piece, board.boardSquares[toSquare.index128].piece);
        }
      }
    }
    moves[index].moved = null;

    return index;
  }

  private int generateNormalKingCaptures(int index, Move[] moves, Board board, Piece piece) {
    int originalIndex = index;
    for (int x = 0; x < kingMoves.length; x++) {
      int i = kingMoves[x];
      Board.BoardSquare toSquare = board.boardSquares[piece.square.index128 + i];
      // Be sure the destination square is on the board
      if (toSquare != null) {
        // If we run into a piece
        if (toSquare.piece != null) {
          // If the piece is not our color
          if (toSquare.piece.color != piece.color) {
            moves[index++].reset(piece.square, toSquare.square, toSquare.square, piece, toSquare.piece);
          }
        }
      }
    }
    return index - originalIndex;
  }


  public static int init_r90[] = {
          56, 48, 40, 32, 24, 16, 8, 0,
          57, 49, 41, 33, 25, 17, 9, 1,
          58, 50, 42, 34, 26, 18, 10, 2,
          59, 51, 43, 35, 27, 19, 11, 3,
          60, 52, 44, 36, 28, 20, 12, 4,
          61, 53, 45, 37, 29, 21, 13, 5,
          62, 54, 46, 38, 30, 22, 14, 6,
          63, 55, 47, 39, 31, 23, 15, 7
  };

  public static int init_l90[] = {
          7, 15, 23, 31, 39, 47, 55, 63,
          6, 14, 22, 30, 38, 46, 54, 62,
          5, 13, 21, 29, 37, 45, 53, 61,
          4, 12, 20, 28, 36, 44, 52, 60,
          3, 11, 19, 27, 35, 43, 51, 59,
          2, 10, 18, 26, 34, 42, 50, 58,
          1, 9, 17, 25, 33, 41, 49, 57,
          0, 8, 16, 24, 32, 40, 48, 56
  };

  public static int init_l45[] = {
          0, 2, 5, 9, 14, 20, 27, 35,
          1, 4, 8, 13, 19, 26, 34, 42,
          3, 7, 12, 18, 25, 33, 41, 48,
          6, 11, 17, 24, 32, 40, 47, 53,
          10, 16, 23, 31, 39, 46, 52, 57,
          15, 22, 30, 38, 45, 51, 56, 60,
          21, 29, 37, 44, 50, 55, 59, 62,
          28, 36, 43, 49, 54, 58, 61, 63
  };

  public static int init_ul45[] = {
          0, 8, 1, 16, 9, 2, 24, 17,
          10, 3, 32, 25, 18, 11, 4, 40,
          33, 26, 19, 12, 5, 48, 41, 34,
          27, 20, 13, 6, 56, 49, 42, 35,
          28, 21, 14, 7, 57, 50, 43, 36,
          29, 22, 15, 58, 51, 44, 37, 30,
          23, 59, 52, 45, 38, 31, 60, 53,
          46, 39, 61, 54, 47, 62, 55, 63
  };

  public static int init_r45[] = {
          28, 21, 15, 10, 6, 3, 1, 0,
          36, 29, 22, 16, 11, 7, 4, 2,
          43, 37, 30, 23, 17, 12, 8, 5,
          49, 44, 38, 31, 24, 18, 13, 9,
          54, 50, 45, 39, 32, 25, 19, 14,
          58, 55, 51, 46, 40, 33, 26, 20,
          61, 59, 56, 52, 47, 41, 34, 27,
          63, 62, 60, 57, 53, 48, 42, 35
  };

  public static int init_ur45[] = {
          7, 6, 15, 5, 14, 23, 4, 13,
          22, 31, 3, 12, 21, 30, 39, 2,
          11, 20, 29, 38, 47, 1, 10, 19,
          28, 37, 46, 55, 0, 9, 18, 27,
          36, 45, 54, 63, 8, 17, 26, 35,
          44, 53, 62, 16, 25, 34, 43, 52,
          61, 24, 33, 42, 51, 60, 32, 41,
          50, 59, 40, 49, 58, 48, 57, 56
  };

  public static int diagonal_length[] = {
          1, 2, 2, 3, 3, 3, 4, 4,
          4, 4, 5, 5, 5, 5, 5, 6,
          6, 6, 6, 6, 6, 7, 7, 7,
          7, 7, 7, 7, 8, 8, 8, 8,
          8, 8, 8, 8, 7, 7, 7, 7,
          7, 7, 7, 6, 6, 6, 6, 6,
          6, 5, 5, 5, 5, 5, 4, 4,
          4, 4, 3, 3, 3, 2, 2, 1
  };

  public static int bishop_shift_rl45[] = {
          64, 62, 59, 55, 50, 44, 37, 29,
          62, 59, 55, 50, 44, 37, 29, 22,
          59, 55, 50, 44, 37, 29, 22, 16,
          55, 50, 44, 37, 29, 22, 16, 11,
          50, 44, 37, 29, 22, 16, 11, 7,
          44, 37, 29, 22, 16, 11, 7, 4,
          37, 29, 22, 16, 11, 7, 4, 2,
          29, 22, 16, 11, 7, 4, 2, 1
  };
  public static int bishop_shift_rr45[] = {
          29, 37, 44, 50, 55, 59, 62, 64,
          22, 29, 37, 44, 50, 55, 59, 62,
          16, 22, 29, 37, 44, 50, 55, 59,
          11, 16, 22, 29, 37, 44, 50, 55,
          7, 11, 16, 22, 29, 37, 44, 50,
          4, 7, 11, 16, 22, 29, 37, 44,
          2, 4, 7, 11, 16, 22, 29, 37,
          1, 2, 4, 7, 11, 16, 22, 29
  };

  {
    int diag_sq[] = {
            0, 1, 0, 2, 1, 0, 3, 2,
            1, 0, 4, 3, 2, 1, 0, 5,
            4, 3, 2, 1, 0, 6, 5, 4,
            3, 2, 1, 0, 7, 6, 5, 4,
            3, 2, 1, 0, 6, 5, 4, 3,
            2, 1, 0, 5, 4, 3, 2, 1,
            0, 4, 3, 2, 1, 0, 3, 2,
            1, 0, 2, 1, 0, 1, 0, 0
    };

    int bias_rl45[] = {
            0, 1, 1, 3, 3, 3, 6, 6,
            6, 6, 10, 10, 10, 10, 10, 15,
            15, 15, 15, 15, 15, 21, 21, 21,
            21, 21, 21, 21, 28, 28, 28, 28,
            28, 28, 28, 28, 36, 36, 36, 36,
            36, 36, 36, 43, 43, 43, 43, 43,
            43, 49, 49, 49, 49, 49, 54, 54,
            54, 54, 58, 58, 58, 61, 61, 63
    };

    int square, pcs, attacks;
    int rsq, tsq;
    int mask;
    int sq;
    int i;

/*
 initialize the rotated attack board that is based on the
 normal chess
 */
    for (square = 0; square < 64; square++) {
      for (i = 0; i < 64; i++) {
        rook_attacks_r0[square][i] = 0;
        rook_mobility_r0[square][i] = 0;
      }
      for (pcs = 0; pcs < 64; pcs++) {
        attacks = initializeFindAttacks((square & 7), pcs << 1, 8);
        while (attacks != 0) {
          sq = 7 - first_one_8bit[attacks];
          rook_attacks_r0[square][pcs] |= Board.SQUARES[(square & 56) + (sq)].mask_on;
          attacks = attacks & (~(1 << (sq)));
        }
        rook_mobility_r0[square][pcs] = Long.bitCount(rook_attacks_r0[square][pcs]);
      }
    }
/*
 initialize the rotated attack board that is based on one that
 rotated left 90 degrees (which lines up a file horizontally,
 rather than its normal vertical orientation.)
 */
    for (square = 0; square < 64; square++) {
      for (i = 0; i < 64; i++) {
        rook_attacks_rl90[square][i] = 0;
        rook_mobility_rl90[square][i] = 0;
      }
      for (pcs = 0; pcs < 64; pcs++) {
        attacks = initializeFindAttacks(Board.rank(square), pcs << 1, 8);
        while (attacks != 0) {
          sq = first_one_8bit[attacks];
          rook_attacks_rl90[square][pcs] |= Board.SQUARES[init_r90[((square & 7) << 3) + (sq)]].mask_on;
          attacks = attacks & (~(1 << (7 - sq)));
        }
        rook_mobility_rl90[square][pcs] =
                Long.bitCount(rook_attacks_rl90[square][pcs]);
      }
    }
/*
 initialize the rotated attack board that is based on one that is
 rotated left 45 degrees (which lines up the (a8-h1) diagonal
 horizontally.
 */
    for (square = 0; square < 64; square++) {
      for (i = 0; i < 64; i++) {
        bishop_attacks_rl45[square][i] = 0;
        bishop_mobility_rl45[square][i] = 0;
      }
      mask = (1 << diagonal_length[init_l45[square]]) - 1;
      for (pcs = 0; pcs < 64; pcs++) {
        rsq = init_l45[square];
        tsq = diag_sq[rsq];
        attacks =
                initializeFindAttacks(tsq, (pcs << 1) & mask,
                        diagonal_length[rsq]) << (8 - diagonal_length[rsq]);
        while (attacks != 0) {
          sq = first_one_8bit[attacks];
          bishop_attacks_rl45[square][pcs] |= Board.SQUARES[init_ul45[sq + bias_rl45[rsq]]].mask_on;
          attacks = attacks & (~(1 << (7 - sq)));
        }
      }
      for (pcs = 0; pcs < 64; pcs++) {
        bishop_mobility_rl45[square][pcs] =
                Long.bitCount(bishop_attacks_rl45[square][pcs]);
      }
    }
/*
 initialize the rotated attack board that is based on one that is
 rotated right 45 degrees (which lines up the (a1-h8) diagonal
 horizontally,
 */
    for (square = 0; square < 64; square++) {
      for (i = 0; i < 64; i++) {
        bishop_attacks_rr45[square][i] = 0;
        bishop_mobility_rr45[square][i] = 0;
      }
      mask = (1 << diagonal_length[init_r45[square]]) - 1;
      for (pcs = 0; pcs < 64; pcs++) {
        rsq = init_r45[square];
        tsq = diag_sq[rsq];
        attacks =
                initializeFindAttacks(tsq, (pcs << 1) & mask,
                        diagonal_length[rsq]) << (8 - diagonal_length[rsq]);
        while (attacks != 0) {
          sq = first_one_8bit[attacks];
          bishop_attacks_rr45[square][pcs] |= Board.SQUARES[init_ur45[sq + bias_rl45[rsq]]].mask_on;
          attacks = attacks & (~(1 << (7 - sq)));
        }
      }
      for (pcs = 0; pcs < 64; pcs++) {
        bishop_mobility_rr45[square][pcs] =
                Long.bitCount(bishop_attacks_rr45[square][pcs]);
      }
    }
  }


  /*
  *******************************************************************************
  *                                                                             *
  *   InitlializeFindAttacks() is used to find the attacks from <square> that   *
  *   exist on the 8-bit vector supplied as <pieces>.  <pieces> represents a    *
  *   rank, file or diagonal, based on the rotated bit-boards.                  *
  *                                                                             *
  *******************************************************************************
  */
  public static int initializeFindAttacks(int square, int pieces, int length) {
    int result, start;

    result = 0;
    /*
    ************************************************************
    *                                                          *
    *   find attacks to left of <square>.                      *
    *                                                          *
    ************************************************************
    */
    if (square < 7) {
      start = 1 << (square + 1);
      while (start < 256) {
        result = result | start;
        if ((pieces & start) != 0)
          break;
        start = start << 1;
      }
    }
    /*
    ************************************************************
    *                                                          *
    *   find attacks to right of <square>.                     *
    *                                                          *
    ************************************************************
    */
    if (square > 0) {
      start = 1 << (square - 1);
      while (start > 0) {
        result = result | start;
        if ((pieces & start) != 0)
          break;
        start = start >>> 1;
      }
    }
    return (result & ((1 << length) - 1));
  }

  public static int[] first_one_8bit = new int[256];
  public static int[] last_one_8bit = new int[256];
  public static int[] first_one = new int[65536];
  public static int[] last_one = new int[65536];
  public static int[] bit_cnt_8bit = new int[256];
  public static int[] connected_passed = new int[256];

  static {
    int i;
    int j;

    int maskl, maskr;

    first_one[0] = 16;
    last_one[0] = 16;
    for (i = 1; i < 65536; i++) {
      maskl = 32768;
      for (j = 0; j < 16; j++) {
        if ((maskl & i) != 0) {
          first_one[i] = 15 - j;
          break;
        }
        maskl = maskl >>> 1;
      }
      maskr = 1;
      for (j = 0; j < 16; j++) {
        if ((maskr & i) != 0) {
          last_one[i] = j - 15;
          break;
        }
        maskr = maskr << 1;
      }
    }

    first_one_8bit[0] = 8;
    last_one_8bit[0] = 8;
    bit_cnt_8bit[0] = 0;
    connected_passed[0] = 0;
    for (i = 0; i < 256; i++) {
      bit_cnt_8bit[i] = 0;
      connected_passed[i] = 0;
      for (j = 0; j < 8; j++)
        if ((i & (1 << j)) != 0)
          bit_cnt_8bit[i]++;
      for (j = 0; j < 8; j++) {
        if ((i & (1 << (7 - j))) != 0) {
          first_one_8bit[i] = (char) j;
          break;
        }
      }
      for (j = 7; j >= 0; j--) {
        if ((i & (1 << (7 - j))) != 0) {
          last_one_8bit[i] = (char) j;
          break;
        }
      }
      for (j = 7; j > 0; j--) {
        if ((i & (3 << (7 - j))) == (3 << (7 - j))) {
          connected_passed[i] = (char) j;
          break;
        }
      }
    }
  }

}

