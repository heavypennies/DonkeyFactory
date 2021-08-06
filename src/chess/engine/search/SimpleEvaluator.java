/* $Id$ */
/* $Id$ */

package chess.engine.search;

import chess.engine.model.Board;
import chess.engine.model.Piece;
import chess.engine.model.Square;
import chess.engine.utils.MoveGeneration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Joshua Levine <levinester@gmail.com>
 * @version $Revision$ $Name$ $Date$
 */
public class SimpleEvaluator implements BoardEvaluator {
  private static boolean DEBUG = false;

  private MoveGeneration moveGeneration;

  public PawnHashtable pawnHash = new PawnHashtable();
  int i;

  public SimpleEvaluator(MoveGeneration moveGeneration) {
    this.moveGeneration = moveGeneration;
    for (int square = 0; square < 64; square++) {

      for (int color = 0; color < 2; color++) {
        long kingNearArea = Board.getTinyKingArea(Board.SQUARES[square], color);

        List<Square> squareList = new ArrayList<Square>();

        while (kingNearArea != 0) {
          int nearSquareIndex = Long.numberOfTrailingZeros(kingNearArea);
          kingNearArea ^= 1L << nearSquareIndex;

          squareList.add(Board.SQUARES[nearSquareIndex]);
        }
        KING_NEAR_AREA[color][square] = squareList.toArray(new Square[0]);

        squareList.clear();
        long kingPawnArea = Board.getPawnKingArea(Board.SQUARES[square], color);

        while (kingPawnArea != 0) {
          int PawnSquareIndex = Long.numberOfTrailingZeros(kingPawnArea);
          kingPawnArea ^= 1L << PawnSquareIndex;

          squareList.add(Board.SQUARES[PawnSquareIndex]);
        }
        KING_PAWN_AREA[color][square] = squareList.toArray(new Square[0]);

        squareList.clear();

        long kingStagingArea = Board.getStagingKingArea(Board.SQUARES[square], color);
        while (kingStagingArea != 0) {
          int stagingSquareIndex = Long.numberOfTrailingZeros(kingStagingArea);
          kingStagingArea ^= 1L << stagingSquareIndex;

          squareList.add(Board.SQUARES[stagingSquareIndex]);
        }

        KING_STAGING_AREA[color][square] = squareList.toArray(new Square[0]);
      }
      KING_AREA_A1_H8[square] = getKingAreaA1H8(Board.SQUARES[square]);
      KING_AREA_H1_A8[square] = getKingAreaH1A8(Board.SQUARES[square]);
    }
    // ABC vs ABC
    // ABC vs AB
    // ABC vs A C
    // ABC vs  BC
    // ABC vs A
    // ABC vs  B
    // ABC vs   C
    // ABC vs
    // etc
    for (int blackPawns = 0; blackPawns < 8; blackPawns++) {
      for (int whitePawns = 0; whitePawns < 8; whitePawns++) {
        PAWN_SCORES[(whitePawns << 3) | blackPawns] = scorePawnWing(whitePawns, blackPawns);
      }
    }

    for (int pawns = 0; pawns < 2; pawns++) {
      for (int knights = 0; knights < 2; knights++) {
        for (int bishops = 0; bishops < 2; bishops++) {
          for (int rooks = 0; rooks < 2; rooks++) {
            for (int queens = 0; queens < 2; queens++) {
              for (int king = 0; king < 2; king++) {

                int pieces = (pawns + (bishops + knights) + rooks + king);
                int piecesAndQueens = pieces + queens;
                int pawnValue = (20 * pawns * max(0, piecesAndQueens - pawns)) + (3 * pawns);
                int minorValue = (55 * bishops * max(0, piecesAndQueens - bishops)) + (5 * bishops);
                minorValue += (45 * knights * max(0, piecesAndQueens - knights)) + (3 * knights);
                int rookValue = (70 * rooks * piecesAndQueens) + (5 * rooks);
                int queenValue = (180 * queens * pieces) + (9 * queens);

/*
                return pawnValue + minorValue + rookValue + queenValue + king;

                int pieces = (pawns + (bishops + knights) + rooks + king);
                int pawnValue = (1 * pawns * pieces) + (30 * pawns * queens);
                int minorValue = (5 * (bishops + knights) * pieces) + (50 * (bishops + knights) * queens);
                int rookValue = (15 * rooks * pieces) + (90 * rooks * queens);
                int queenValue = (10 * queens * pieces);
*/

                int index = pawns |
                        (knights << Piece.ATTACKER_SHIFT_KNIGHT) |
                        (bishops << Piece.ATTACKER_SHIFT_BISHOP) |
                        (rooks << Piece.ATTACKER_SHIFT_ROOK) |
                        (queens << Piece.ATTACKER_SHIFT_QUEEN) |
                        (king << Piece.ATTACKER_SHIFT_KING);
                attackScores[index] = pawnValue + minorValue + rookValue + queenValue + king;

                attackerCount[index] = piecesAndQueens;
                smallestAttacker[index] = pawns > 0 ? Piece.PAWN :
                        (knights > 0 ? Piece.BISHOP :
                                (bishops > 0 ? Piece.BISHOP :
                                        (rooks > 0 ? Piece.ROOK :
                                                (queens > 0 ? Piece.QUEEN :
                                                        (king > 0 ? Piece.KING : 99999)))));
              }
            }
          }
        }
      }
    }

/*
    int[][] attackers = new int[2][Piece.TYPE_VALUES.length + 1];
    for(int w = 0;w < attackerCount.length;w++) {
      for(int b = 0;b < attackerCount.length;b++) {

        for(int c = 0;c < 2;c++) {
          for(int type = 0;type < Piece.KING; type++) {
            attackers[1][Piece.PAWN] = w & 3;
            attackers[1][Piece.KNIGHT] = (w >> Piece.ATTACKER_SHIFT[Piece.KNIGHT]) & 3;
            attackers[1][Piece.BISHOP] = (w >> Piece.ATTACKER_SHIFT[Piece.BISHOP]) & 3;
            attackers[1][Piece.ROOK] = (w >> Piece.ATTACKER_SHIFT[Piece.ROOK]) & 3;
            attackers[1][Piece.QUEEN] = (w >> Piece.ATTACKER_SHIFT[Piece.QUEEN]) & 3;
            attackers[1][Piece.KING] = (w >> Piece.ATTACKER_SHIFT[Piece.KING]) & 3;
            attackers[1][6] = attackers[1][Piece.KING] + attackers[1][Piece.QUEEN] + attackers[1][Piece.ROOK] + attackers[1][Piece.BISHOP] + attackers[1][Piece.KNIGHT] + attackers[1][Piece.PAWN];

            attackers[0][Piece.PAWN] = b & 3;
            attackers[0][Piece.KNIGHT] = (b >> Piece.ATTACKER_SHIFT[Piece.KNIGHT]) & 3;
            attackers[0][Piece.BISHOP] = (b >> Piece.ATTACKER_SHIFT[Piece.BISHOP]) & 3;
            attackers[0][Piece.ROOK] = (b >> Piece.ATTACKER_SHIFT[Piece.ROOK]) & 3;
            attackers[0][Piece.QUEEN] = (b >> Piece.ATTACKER_SHIFT[Piece.QUEEN]) & 3;
            attackers[0][Piece.KING] = (b >> Piece.ATTACKER_SHIFT[Piece.KING]) & 3;
            attackers[0][6] = attackers[0][Piece.KING] + attackers[0][Piece.QUEEN] + attackers[0][Piece.ROOK] + attackers[0][Piece.BISHOP] + attackers[0][Piece.KNIGHT] + attackers[0][Piece.PAWN];

            if(attackers[0][Piece.KING] > 1 || attackers[1][Piece.KING] > 1 || attackers[0][Piece.PAWN] > 2 || attackers[1][Piece.PAWN] > 2) {
              continue;
            }

            int attackedPiece = 0;
            int swapIndex = 1;
            if(attackers[c][type] > 0) {
              attackedPiece = Piece.TYPE_VALUES[type];
              attackers[c][type] --;
              attackers[c][6] --;
            }
            else {
              continue;
            }
            swapScores[0] = 0;
            int color = c ^ 1;

            while (attackers[color][6] > 0)
            {
              swapScores[swapIndex] = -swapScores[swapIndex - 1] + attackedPiece;
              ++swapIndex;
              attackers[color][6] --;

              if (attackers[color][Piece.PAWN] != 0)
              {
                attackedPiece = Piece.TYPE_VALUES[Piece.PAWN];
                attackers[color][Piece.PAWN] --;
              }
              else if (attackers[color][Piece.KNIGHT] != 0)
              {
                attackedPiece = Piece.TYPE_VALUES[Piece.KNIGHT];
                attackers[color][Piece.KNIGHT] --;
              }
              else if (attackers[color][Piece.BISHOP] != 0)
              {
                attackedPiece = Piece.TYPE_VALUES[Piece.BISHOP];
                attackers[color][Piece.BISHOP] --;
              }
              else if (attackers[color][Piece.ROOK] != 0)
              {
                attackedPiece = Piece.TYPE_VALUES[Piece.ROOK];
                attackers[color][Piece.ROOK] --;
              }
              else if (attackers[color][Piece.QUEEN] != 0)
              {
                attackedPiece = Piece.TYPE_VALUES[Piece.QUEEN];
                attackers[color][Piece.QUEEN] --;
              }
              else if (attackers[color][Piece.KING] != 0)
              {
                attackedPiece = Piece.TYPE_VALUES[Piece.KING];
                attackers[color][Piece.KING] --;
              }
              else
              {
                break;
              }
              color = color ^ 1;
            }


            while (--swapIndex > 0)
            {
              if (swapScores[swapIndex] >= -swapScores[swapIndex - 1])
              {
                swapScores[swapIndex - 1] = -swapScores[swapIndex];
              }
            }
            swap[c][type][w][b] = swapScores[0];
          }
        }
      }
    }
*/
  }


  int scorePawnWing(int whitePawns, int blackPawns) {
    return PAWN_WING_SCORES[whitePawns][blackPawns] >> 2;

    //return new PawnWingScorer(moveGeneration, this).scorePawnWing(whitePawns, blackPawns);
  }

  public void reset() {
    //pawnHash.clear();
  }

  Square[] centerSquares = {
          Square.C3,
          Square.C4,
          Square.C5,
          Square.C6,
          Square.D3,
          Square.D4,
          Square.D5,
          Square.D6,
          Square.E3,
          Square.E4,
          Square.E5,
          Square.E6,
          Square.F3,
          Square.F4,
          Square.F5,
          Square.F6,
  };

  int whiteMobility;
  int blackMobility;

  public int scorePosition(Board board, int alpha, int beta) {
    //if(DEBUG)System.err.println(board.toString());

    int score = board.materialScore;

    // pawn position (with hash)
    PawnFlags pawnFlags = scorePawns(board);

    int pieceValueScore = board.positionScore;

    // Count Undeveloped Pieces
    final long undevelopedWhitePieces =
            ((Board.SQUARES[Square.D2.index64].mask_on | Board.SQUARES[Square.E2.index64].mask_on) & board.pieceBoards[1][Piece.PAWN]) |
                    (RANKS[0] & (board.pieceBoards[1][Piece.KNIGHT] | board.pieceBoards[1][Piece.BISHOP])) |
                    (Square.E1.mask_on & board.pieceBoards[1][Piece.KING]);
    final long undevelopedBlackPieces =
            ((Board.SQUARES[Square.D7.index64].mask_on | Board.SQUARES[Square.E7.index64].mask_on) & board.pieceBoards[0][Piece.PAWN]) |
                    (RANKS[7] & (board.pieceBoards[0][Piece.KNIGHT] | board.pieceBoards[0][Piece.BISHOP])) |
                    (Square.E8.mask_on & board.pieceBoards[0][Piece.KING]);

    // Eval Piece by piece
    int squareIndex;
    Square square;
    Piece piece;

    long pieces = pawnFlags.whiteWeakPawns & board.attacks[0];
    while(pieces != 0) {
      squareIndex = Long.numberOfTrailingZeros(pieces);
      square = Board.SQUARES[squareIndex];
      pieces &= square.mask_off;

      pieceValueScore -= (board.isEndgame() ? WEAK_PAWN_PRESSURE : WEAK_PAWN_PRESSURE_EG) * Long.bitCount(board.squareAttackers[squareIndex] | board.squareRammers[squareIndex]);
    }

    pieces = pawnFlags.blackWeakPawns & board.attacks[1];
    while(pieces != 0) {
      squareIndex = Long.numberOfTrailingZeros(pieces);
      square = Board.SQUARES[squareIndex];
      pieces &= square.mask_off;

      pieceValueScore += (board.isEndgame() ? WEAK_PAWN_PRESSURE : WEAK_PAWN_PRESSURE_EG) * Long.bitCount(board.squareAttackers[squareIndex] | board.squareRammers[squareIndex]);
    }

    whiteMobility = 0;
    blackMobility = 0;

    // WHITE KNIGHT
    pieces = board.pieceBoards[1][Piece.KNIGHT];
    while (pieces != 0) {
      squareIndex = Long.numberOfTrailingZeros(pieces);
      square = Board.SQUARES[squareIndex];
      pieces &= square.mask_off;
      piece = board.boardSquares[square.index128].piece;
      whiteMobility += Long.bitCount(~board.pieceBoards[1][Board.ALL_PIECES] & piece.attacks & BLACK_HALF[Square.A4.index64] & ~board.attacks[0]) << 1;

/*
      if((board.squareAttackers[squareIndex] & board.pieceBoards[0][Board.ALL_PIECES]) != 0 &&
         (board.squareAttackers[squareIndex] & board.pieceBoards[1][Board.ALL_PIECES]) == 0 &&
         board.turn == 0) {
        score -= Piece.TYPE_VALUES[Piece.KNIGHT];
      }
*/

//      pieceValueScore -= Math.min(30, (board.boardSquares[square.index128].piece.moveCount - 1) * board.boardSquares[square.index128].piece.moveCount);
//      pieceValueScore += (((Long.bitCount(BLACK_HALF[squareIndex] & MoveGeneration.attackVectors[1][Piece.KNIGHT][squareIndex] & ~board.pieceBoards[1][6]))) - 3) << 1;
      if ((WHITE_KNIGHT_OUTPOST_MASK[squareIndex] & board.pieceBoards[0][Piece.PAWN]) == 0) {
        pieceValueScore += PIECE_VALUE_TABLES[1][Piece.PAWN][squareIndex]; // KNIGHT_OUTPOST_VALUE + (2 * (Board.rank(squareIndex) - 4));
      }
    }

    // BLACK KNIGHT
    pieces = board.pieceBoards[0][Piece.KNIGHT];
    while (pieces != 0) {
      squareIndex = Long.numberOfTrailingZeros(pieces);
      square = Board.SQUARES[squareIndex];
      pieces &= square.mask_off;
      piece = board.boardSquares[square.index128].piece;
      blackMobility += Long.bitCount(~board.pieceBoards[0][Board.ALL_PIECES] & piece.attacks & WHITE_HALF[Square.H5.index64] & ~board.attacks[1]) << 1;

/*
      if((board.squareAttackers[squareIndex] & board.pieceBoards[1][Board.ALL_PIECES]) != 0 &&
         (board.squareAttackers[squareIndex] & board.pieceBoards[0][Board.ALL_PIECES]) == 0 &&
         board.turn == 1) {
        score += Piece.TYPE_VALUES[Piece.KNIGHT];
      }
*/

//      pieceValueScore += Math.min(30, (board.boardSquares[square.index128].piece.moveCount - 1) * board.boardSquares[square.index128].piece.moveCount);
//      pieceValueScore -= (((Long.bitCount(WHITE_HALF[squareIndex] & MoveGeneration.attackVectors[0][Piece.KNIGHT][squareIndex] & ~board.pieceBoards[0][6]))) - 3) << 1;

      if ((BLACK_KNIGHT_OUTPOST_MASK[squareIndex] & board.pieceBoards[1][Piece.PAWN]) == 0) {
        pieceValueScore -= PIECE_VALUE_TABLES[0][Piece.PAWN][squareIndex];//KNIGHT_OUTPOST_VALUE + (2 * (4 - Board.rank(squareIndex)));
      }
    }

    // WHITE BISHOP
    pieces = board.pieceBoards[1][Piece.BISHOP];
    while (pieces != 0) {
      squareIndex = Long.numberOfTrailingZeros(pieces);
      square = Board.SQUARES[squareIndex];
      pieces &= square.mask_off;
      piece = board.boardSquares[square.index128].piece;
      whiteMobility += board.bishopMobility(squareIndex);
//      pieceValueScore -= PIECE_VALUE_TABLES[1][Piece.BISHOP][squareIndex];

/*
      if((board.squareAttackers[squareIndex] & board.pieceBoards[0][Board.ALL_PIECES]) != 0 &&
         (board.squareAttackers[squareIndex] & board.pieceBoards[1][Board.ALL_PIECES]) == 0 &&
         board.turn == 0) {
        score -= Piece.TYPE_VALUES[Piece.BISHOP];
      }
*/

      if (square == Square.A7 && (board.pieceBoards[0][Piece.PAWN] & Square.B6.mask_on & board.attacks[0]) != 0) {
        score -= 200;
      }
      if (square == Square.H7 && (board.pieceBoards[0][Piece.PAWN] & Square.G6.mask_on & board.attacks[0]) != 0) {
        score -= 200;
      }

      if (pieces != 0) {
        pieceValueScore += TWO_BISHOPS_VALUE;
      }

/*
      if(((MoveGeneration.attackVectors[1][Piece.KING][squareIndex] & ~MoveGeneration.attackVectors[1][Piece.ROOK][squareIndex]) & ~board.pieceBoards[1][Board.ALL_PIECES]) == 0)
      {
        pieceValueScore -= BISHOP_TRAPPED_VALUE;
      }
*/
//      pieceValueScore -= Math.min(30, (board.boardSquares[square.index128].piece.moveCount - 1) * board.boardSquares[square.index128].piece.moveCount);

      // PINS
      long pinBoard = board.bishopPins(square.index64);
      if ((pinBoard & board.pieceBoards[0][Piece.ROOK]) != 0) {
        pieceValueScore += 10;
      }
      if ((pinBoard & board.pieceBoards[0][Piece.QUEEN]) != 0) {
        pieceValueScore += 7;
      }
      if ((pinBoard & board.pieceBoards[0][Piece.KING]) != 0) {
        pieceValueScore += 10;
      }
    }

    // BLACK BISHOP
    pieces = board.pieceBoards[0][Piece.BISHOP];
    while (pieces != 0) {
      squareIndex = Long.numberOfTrailingZeros(pieces);
      square = Board.SQUARES[squareIndex];
      pieces &= square.mask_off;
      piece = board.boardSquares[square.index128].piece;
      blackMobility += board.bishopMobility(squareIndex);
//      pieceValueScore += PIECE_VALUE_TABLES[0][Piece.BISHOP][squareIndex];

/*
      if((board.squareAttackers[squareIndex] & board.pieceBoards[1][Board.ALL_PIECES]) != 0 &&
         (board.squareAttackers[squareIndex] & board.pieceBoards[0][Board.ALL_PIECES]) == 0 &&
         board.turn == 1) {
        score += Piece.TYPE_VALUES[Piece.BISHOP];
      }
*/

      if (square == Square.A2 && (board.pieceBoards[1][Piece.PAWN] & Square.B3.mask_on & board.attacks[1]) != 0) {
        score += 200;
      }
      if (square == Square.H2 && (board.pieceBoards[1][Piece.PAWN] & Square.G3.mask_on & board.attacks[1]) != 0) {
        score += 200;
      }

      if (pieces != 0) {
        pieceValueScore -= TWO_BISHOPS_VALUE;
      }

/*
      if(((MoveGeneration.attackVectors[0][Piece.KING][squareIndex] & ~MoveGeneration.attackVectors[0][Piece.ROOK][squareIndex]) & ~board.pieceBoards[0][Board.ALL_PIECES]) == 0)
      {
        pieceValueScore += BISHOP_TRAPPED_VALUE;
      }
*/
//      pieceValueScore += Math.min(30, (board.boardSquares[square.index128].piece.moveCount - 1) * board.boardSquares[square.index128].piece.moveCount);

      // PINS
      long pinBoard = board.bishopPins(square.index64);
      if ((pinBoard & board.pieceBoards[1][Piece.ROOK]) != 0) {
        pieceValueScore -= 10;
      }
      if ((pinBoard & board.pieceBoards[1][Piece.QUEEN]) != 0) {
        pieceValueScore -= 7;
      }
      if ((pinBoard & board.pieceBoards[1][Piece.KING]) != 0) {
        pieceValueScore -= 10;
      }
    }

    // WHITE ROOK
    pieces = board.pieceBoards[1][Piece.ROOK];
    while (pieces != 0) {
      squareIndex = Long.numberOfTrailingZeros(pieces);
      square = Board.SQUARES[squareIndex];
      pieces &= square.mask_off;
      piece = board.boardSquares[square.index128].piece;
      whiteMobility += board.mobilityRank(squareIndex) + (board.mobilityFile(squareIndex) << 1);

/*
      if((board.squareAttackers[squareIndex] & board.pieceBoards[0][Board.ALL_PIECES]) != 0 &&
         (board.squareAttackers[squareIndex] & board.pieceBoards[1][Board.ALL_PIECES]) == 0 &&
         board.turn == 0) {
        score -= Piece.TYPE_VALUES[Piece.ROOK];
      }
*/

      // PINS
      long pinBoard = board.rookPins(square.index64);
      if ((pinBoard & board.pieceBoards[0][Piece.QUEEN]) != 0) {
        pieceValueScore += 15;
      }
      if ((pinBoard & board.pieceBoards[0][Piece.KING]) != 0) {
        pieceValueScore += 25;
      }

      if (squareIndex == Square.A1.index64 && board.whiteKing.square.index64 == Square.B1.index64) {
        pieceValueScore -= TRAPPED_ROOK_VALUE;
      } else if (squareIndex == Square.H1.index64 && board.whiteKing.square.index64 == Square.G1.index64) {
        pieceValueScore -= TRAPPED_ROOK_VALUE;
      }

      if (((square.mask_on & pawnFlags.openFiles) != 0 && (piece.attacks & BLACK_HALF[Square.A6.index64]) != 0)) {
        pieceValueScore += board.isEndgame() ? ROOK_ON_OPEN_FILE_EG : ROOK_ON_OPEN_FILE;
      } else if (((piece.attacks & pawnFlags.blackWeakPawns) != 0)) {
        pieceValueScore += ROOK_ON_HALF_OPEN_FILE;
      }
      if((pawnFlags.whitePassedPawns & FILES[square.file]) != 0) {
        if((BLACK_PASSED_MASK[squareIndex] & square.mask_on) != 0) {
          pieceValueScore += ROOK_ON_OPEN_FILE_EG_PP;
        }
        else if((WHITE_PASSED_MASK[squareIndex] & square.mask_on) != 0) {
          pieceValueScore -= ROOK_ON_OPEN_FILE_EG_PP;
        }
      }
      if ((piece.attacks & board.pieceBoards[1][Piece.ROOK]) != 0) {
        pieceValueScore += ROOK_ON_HALF_OPEN_FILE;
      }
/*
      else if (((Board.SQUARES[square].mask_on & pawnFlags.lockedFiles) != 0))
      {
        pieceValueScore -= ROOK_ON_OPEN_FILE;
      }
*/
      //pieceValueScore -= (undevelopedWhitePieces == 0 ? 1 : 2 + board.boardSquares[square.index128].piece.moveCount) * board.boardSquares[square.index128].piece.moveCount;

    }

    // BLACK ROOK
    pieces = board.pieceBoards[0][Piece.ROOK];
    while (pieces != 0) {
      squareIndex = Long.numberOfTrailingZeros(pieces);
      square = Board.SQUARES[squareIndex];
      pieces &= square.mask_off;
      piece = board.boardSquares[square.index128].piece;
      blackMobility += board.mobilityRank(squareIndex) + (board.mobilityFile(squareIndex) << 1);

/*
      if((board.squareAttackers[squareIndex] & board.pieceBoards[1][Board.ALL_PIECES]) != 0 &&
         (board.squareAttackers[squareIndex] & board.pieceBoards[0][Board.ALL_PIECES]) == 0 &&
         board.turn == 1) {
        score += Piece.TYPE_VALUES[Piece.ROOK];
      }
*/

      // PINS
      long pinBoard = board.rookPins(square.index64);
      if ((pinBoard & board.pieceBoards[1][Piece.QUEEN]) != 0) {
        pieceValueScore -= 15;
      }
      if ((pinBoard & board.pieceBoards[1][Piece.KING]) != 0) {
        pieceValueScore -= 25;
      }


      if (squareIndex == Square.A8.index64 && board.blackKing.square.index64 == Square.B8.index64) {
        pieceValueScore += TRAPPED_ROOK_VALUE;
      } else if (squareIndex == Square.H8.index64 && board.blackKing.square.index64 == Square.G8.index64) {
        pieceValueScore += TRAPPED_ROOK_VALUE;
      }

      if (((square.mask_on & pawnFlags.openFiles) != 0 && (piece.attacks & WHITE_HALF[Square.H3.index64]) != 0)) {
        pieceValueScore -= board.isEndgame() ? ROOK_ON_OPEN_FILE_EG : ROOK_ON_OPEN_FILE;
      }
      else if (((piece.attacks & pawnFlags.whiteWeakPawns) != 0)) {
        pieceValueScore -= ROOK_ON_HALF_OPEN_FILE;
      }

      if((pawnFlags.blackPassedPawns & FILES[square.file]) != 0) {
        if((WHITE_PASSED_MASK[squareIndex] & square.mask_on) != 0) {
          pieceValueScore += ROOK_ON_OPEN_FILE_EG_PP;
        }
        else if((BLACK_PASSED_MASK[squareIndex] & square.mask_on) != 0) {
          pieceValueScore -= ROOK_ON_OPEN_FILE_EG_PP;
        }
      }

      if ((piece.attacks & board.pieceBoards[0][Piece.ROOK]) != 0) {
        pieceValueScore -= ROOK_ON_HALF_OPEN_FILE;
      }

      //pieceValueScore += Math.min(30, (undevelopedBlackPieces == 0 ? 1 : 2 + board.boardSquares[square.index128].piece.moveCount) * board.boardSquares[square.index128].piece.moveCount);
    }

    // WHITE QUEEN
    pieces = board.pieceBoards[1][Piece.QUEEN];
    while (pieces != 0) {
      squareIndex = Long.numberOfTrailingZeros(pieces);
      pieces &= ~(1L << squareIndex);

/*
      if((board.squareAttackers[squareIndex] & board.pieceBoards[0][Board.ALL_PIECES]) != 0 &&
         (board.squareAttackers[squareIndex] & board.pieceBoards[1][Board.ALL_PIECES]) == 0 &&
         board.turn == 0) {
        score -= Piece.TYPE_VALUES[Piece.QUEEN];
      }
*/

      if (undevelopedWhitePieces == 0) {
        whiteMobility += board.bishopMobility(squareIndex);
        whiteMobility += board.rookMobility(squareIndex);
        pieceValueScore += PIECE_VALUE_TABLES[1][Piece.QUEEN][squareIndex];
      }
      //pieceValueScore -= Math.min(30, (undevelopedWhitePieces == 0 ? 1 : 2 + board.boardSquares[square.index128].piece.moveCount) * board.boardSquares[square.index128].piece.moveCount);
    }

    // BLACK QUEEN
    pieces = board.pieceBoards[0][Piece.QUEEN];
    while (pieces != 0) {
      squareIndex = Long.numberOfTrailingZeros(pieces);
      pieces &= ~(1L << squareIndex);

/*
      if((board.squareAttackers[squareIndex] & board.pieceBoards[1][Board.ALL_PIECES]) != 0 &&
         (board.squareAttackers[squareIndex] & board.pieceBoards[0][Board.ALL_PIECES]) == 0 &&
         board.turn == 1) {
        score += Piece.TYPE_VALUES[Piece.QUEEN];
      }
*/

      if (undevelopedBlackPieces == 0) {
        blackMobility += board.bishopMobility(squareIndex);
        blackMobility += board.rookMobility(squareIndex);
        pieceValueScore -= PIECE_VALUE_TABLES[0][Piece.QUEEN][squareIndex];
      }
    }


    score += pieceValueScore;
    if (whiteMobility > blackMobility) {
      score += MOBILITY[whiteMobility - blackMobility];
    } else if (blackMobility > whiteMobility) {
      score -= MOBILITY[blackMobility - whiteMobility];
    }

/*
    score += pawnFlags.endgameScore * (pawnFlags.endgameScore > 0 ?  blackMaterialRatio : whiteMaterialRatio);
    score += pawnFlags.centerScore * (pawnFlags.centerScore > 0 ? (1D-whiteMaterialRatio) : (1D-blackMaterialRatio));
*/


    // CENTER CONTROL
    int centerScore = 0;
    if (pawnFlags.whitePawnCount > 4 && pawnFlags.blackPawnCount > 4) {
      for (i = 0; i < centerSquares.length; i++) {
        square = centerSquares[i];
        {
          if ((board.attackState[1][square.index64] & Piece.ATTACKER_UMASK[Piece.QUEEN]) == 0 && (board.attackState[0][square.index64] & Piece.ATTACKER_UMASK[Piece.QUEEN]) == 0) {
            // no control by white or black
          } else if (smallestAttacker[board.attackState[1][square.index64]] < smallestAttacker[board.attackState[0][square.index64]]) {
            // white has control, black does not
            centerScore += CENTER_VALUE_TABLES[1][square.index64];
          } else if (smallestAttacker[board.attackState[0][square.index64]] < smallestAttacker[board.attackState[1][square.index64]]) {
            // black has control, white does not
            centerScore -= CENTER_VALUE_TABLES[0][square.index64];
          } else if (board.boardSquares[square.index128].piece != null) {
            if (board.boardSquares[square.index128].piece.color == 1) {
              // white has control, black does not
              centerScore += CENTER_VALUE_TABLES[1][square.index64];
            } else if (board.boardSquares[square.index128].piece.color == 0) {
              // black has control, white does not
              centerScore -= CENTER_VALUE_TABLES[0][square.index64];
            }
          }
        }
      }
    }

    score += centerScore;

    if (undevelopedWhitePieces != 0 || undevelopedBlackPieces != 0) {
      // Develop pieces
      score += ((Long.bitCount(undevelopedBlackPieces) - Long.bitCount(undevelopedWhitePieces)) * DEVELOPMENT_VALUE);

      // Don't move the queen too much too early
      if (board.stats.whiteKingsideRookMoves == 0 && board.stats.whiteQueensideRookMoves == 0 && board.pieceBoards[1][Piece.QUEEN] != 0 && board.stats.whitePieceMoves[Piece.QUEEN] > 1) {
        score -= QUEEN_TOO_EARLY_VALUE;
      }
      if (board.stats.blackKingsideRookMoves == 0 && board.stats.blackQueensideRookMoves == 0  && board.pieceBoards[0][Piece.QUEEN] != 0 && board.stats.blackPieceMoves[Piece.QUEEN] > 1) {
        score += QUEEN_TOO_EARLY_VALUE;
      }

      if ((board.pieceBoards[1][Piece.PAWN] & Square.E2.mask_on) != 0) {
        score -= UNMOVED_CENTER_PAWN;
        if ((board.pieceBoards[1][Board.ALL_PIECES] & Square.E3.mask_on) != 0) {
          score -= UNMOVED_CENTER_PAWN_BLOCKED;
        }
      }
      if ((board.pieceBoards[1][Piece.PAWN] & Square.D2.mask_on) != 0) {
        score -= UNMOVED_CENTER_PAWN;
        if ((board.pieceBoards[1][Board.ALL_PIECES] & Square.D3.mask_on) != 0) {
          score -= UNMOVED_CENTER_PAWN_BLOCKED;
        }
      }
      if ((board.pieceBoards[0][Piece.PAWN] & Square.E7.mask_on) != 0) {
        score += UNMOVED_CENTER_PAWN;
        if ((board.pieceBoards[0][Board.ALL_PIECES] & Square.E6.mask_on) != 0) {
          score += UNMOVED_CENTER_PAWN_BLOCKED;
        }
      }
      if ((board.pieceBoards[0][Piece.PAWN] & Square.D7.mask_on) != 0) {
        score += UNMOVED_CENTER_PAWN;
        if ((board.pieceBoards[0][Board.ALL_PIECES] & Square.D6.mask_on) != 0) {
          score += UNMOVED_CENTER_PAWN_BLOCKED;
        }
      }
    }

    // Don't trade when down material
    if (board.stats.originalMaterialDifference < 0 && board.materialValue[0] > board.materialValue[1]) {
      if (board.materialValue[1] + board.materialValue[0] < board.stats.originalMaterial) {
        score -= TRADE_WHEN_LOSING_VALUE;
      }
    } else if (board.stats.originalMaterialDifference > 0 && board.materialValue[1] > board.materialValue[0]) {
      if (board.materialValue[1] + board.materialValue[0] < board.stats.originalMaterial) {
        score += TRADE_WHEN_LOSING_VALUE;
      }
    }

    // Pawns
    score += pawnFlags.score;

    //if(DEBUG)System.err.println("Material Ratio: w: " + whiteMaterialRatio + " b: " + blackMaterialRatio);
    //if(DEBUG)System.err.println("Pawns: " + pawnScore);

    // Passed Pawns
    if ((pawnFlags.whitePassedPawns | pawnFlags.blackPassedPawns) != 0) {
      score += scorePassedPawns(board,
              pawnFlags.whitePassedPawns,
              pawnFlags.blackPassedPawns);
      //if(DEBUG)System.err.println("Passed Pawns: " + passedPawnScore);
    }

/*
    if((board.turn == 1 ? score : -score) < alpha - 500)
    {
      return board.turn == 1 ?
             score :
             -score;
    }
*/


    // King Evals
    score += evalWhiteKing(board, pawnFlags);
    score -= evalBlackKing(board, pawnFlags);

    //if(DEBUG)System.err.println("Pieces: " + pieceValueScore);
    //if(DEBUG)System.err.println("White King: " + whiteKingScore);
    //if(DEBUG)System.err.println("Black King: " + blackKingScore);
    //if(DEBUG)System.err.println("Score: " + score);

/*
    evalWhiteKing(board, pawnFlags);
    evalBlackKing(board, pawnFlags);
*/

    if (board.materialValue[1] < 5 && pawnFlags.whitePawnCount == 0) {
      score = min(score, 0);
    }
    if (board.materialValue[0] < 5 && pawnFlags.blackPawnCount == 0) {
      score = max(score, 0);
    }

    return board.turn == 1 ?
            score :
            -score;
  }


  //////////////
  //   PAWNS
  //////////////
  public PawnFlags scorePawns(Board board) {
    // probe pawn hash
    PawnHashtable.HashEntry pawnHashEntry = pawnHash.getEntryNoNull(board);

    // if the entry is good
    if (pawnHashEntry.hash == board.pawnHash) {
      // return the score from the entry
      return pawnHashEntry.pawnFlags;
    }

    int score = 0;

    pawnHashEntry.pawnFlags.reset();

    long whitePawns = board.pieceBoards[1][Piece.PAWN];
    long blackPawns = board.pieceBoards[0][Piece.PAWN];

    int whitePawnScore = 0;
    int blackPawnScore = 0;

    int[] whitePawnValueTable = PIECE_VALUE_TABLES[1][Piece.PAWN];
    Square pawnSquare;
    while (whitePawns != 0) {
      int pawnSquareIndex = Long.numberOfTrailingZeros(whitePawns);
      pawnSquare = Board.SQUARES[pawnSquareIndex];
      whitePawns ^= pawnSquare.mask_on;

      //if(DEBUG)System.err.println("  P@" + pawnSquare.toString());
//      whitePawnScore += Piece.TYPE_VALUES[Piece.PAWN];
      pawnHashEntry.pawnFlags.whitePawnCount++;
      pawnHashEntry.pawnFlags.openFiles &= ~FILES[pawnSquare.file];
      pawnHashEntry.pawnFlags.openRanks &= ~RANKS[pawnSquare.rank];
      //whitePawnScore += whitePawnValueTable[pawnSquareIndex];
      pawnHashEntry.pawnFlags.closedFiles[1] |= FILES[pawnSquare.file] & WHITE_HALF[pawnSquareIndex];
      pawnHashEntry.pawnFlags.pawnAttacks[1] |= MoveGeneration.attackVectors[0][Piece.PAWN][pawnSquareIndex];

      if (!board.isEndgame()) {
        if (pawnSquare.file == 0) {
          // white pawn chain (H1-A8)
          /*
                  if ((board.pieceBoards[1][Piece.PAWN] & Board.SQUARES[pawnSquareIndex - 7].mask_on) != 0)
                  {
                    whitePawnScore += whitePawnValueTable[pawnSquareIndex] >> 2;
                    pawnHashEntry.pawnFlags.closedFiles[0] |= FILES[pawnSquare.file] & BLACK_HALF[pawnSquareIndex];
                    //if(DEBUG)System.err.println("    H1-A8 Chain");
                  }
          */
        } else if (pawnSquare.file == 7) {
          // white pawn phalanx
          if ((board.pieceBoards[1][Piece.PAWN] & Board.SQUARES[pawnSquareIndex - 1].mask_on) != 0 && pawnSquare.rank > 1) {
            whitePawnScore += whitePawnValueTable[pawnSquareIndex] >> 1;

            // pawn phalanx can create passed pawn?
            // pawn phalanx is immediately challenged
            // pawn phalanx is immediately challenged
            // pawn phalanx is opposed
            // pawn phalanx is disconnected

            //if(DEBUG)System.err.println("    Phalanx");
          }
          // white pawn chain (A1-H8)
          /*
                  if (((board.pieceBoards[1][Piece.PAWN] & Board.SQUARES[pawnSquareIndex - 9].mask_on) != 0))
                  {
                    whitePawnScore += whitePawnValueTable[pawnSquareIndex] >> 2;
                    pawnHashEntry.pawnFlags.closedFiles[0] |= FILES[pawnSquare.file] & BLACK_HALF[pawnSquareIndex];
                    //if(DEBUG)System.err.println("    A1-H8 Chain");
                  }
          */
        } else {
          // white pawn phalanx
          if ((board.pieceBoards[1][Piece.PAWN] & Board.SQUARES[pawnSquareIndex - 1].mask_on) != 0 && pawnSquare.rank > 1) {
            whitePawnScore += whitePawnValueTable[pawnSquareIndex] >> 1;
            //if(DEBUG)System.err.println("    Phalanx");
          }
          // white pawn chain (A1-H8)
          /*
                  if (((board.pieceBoards[1][Piece.PAWN] & Board.SQUARES[pawnSquareIndex - 9].mask_on) != 0))
                  {
                    whitePawnScore += whitePawnValueTable[pawnSquareIndex] >> 2;
                    pawnHashEntry.pawnFlags.closedFiles[0] |= FILES[pawnSquare.file] & BLACK_HALF[pawnSquareIndex];
                    //if(DEBUG)System.err.println("    A1-H8 Chain");
                  }
                  // white pawn chain (H1-A8)
                  if (((board.pieceBoards[1][Piece.PAWN] & Board.SQUARES[pawnSquareIndex - 7].mask_on) != 0))
                  {
                    whitePawnScore += whitePawnValueTable[pawnSquareIndex] >> 2;
                    pawnHashEntry.pawnFlags.closedFiles[0] |= FILES[pawnSquare.file] & BLACK_HALF[pawnSquareIndex];
                    //if(DEBUG)System.err.println("    H1-A8 Chain");
                  }
          */
        }
      }
      // locked files
      if ((board.pieceBoards[0][Piece.PAWN] & Board.SQUARES[pawnSquareIndex + 8].mask_on) != 0) {
        pawnHashEntry.pawnFlags.closedFiles[1] |= FILES[pawnSquare.file] & WHITE_HALF[pawnSquareIndex];
        pawnHashEntry.pawnFlags.lockedFiles |= FILES[pawnSquare.file];
        //if(DEBUG)System.err.println("    Locked File");
//        continue;
      }

      // white passed pawns
      if ((board.pieceBoards[0][Piece.PAWN] & WHITE_PASSED_MASK[pawnSquareIndex]) == 0 &&
              (board.pieceBoards[1][Piece.PAWN] & FILES[pawnSquare.file] & BLACK_HALF[pawnSquareIndex + 8]) == 0) {
        pawnHashEntry.pawnFlags.whitePassedPawns |= pawnSquare.mask_on;
        //if(DEBUG)System.err.println("    Passed");
      }

      // white isolated pawns
      else if ((board.pieceBoards[1][Piece.PAWN] & WHITE_BACKWARDS_MASK[pawnSquareIndex]) == 0) {
        //if(DEBUG)System.err.println("    Double Backwards");
        whitePawnScore -= PAWN_BACKWARDS_VALUE[pawnSquare.file];
        pawnHashEntry.pawnFlags.whiteWeakPawns |= pawnSquare.mask_on;
        //if(DEBUG)System.err.println("  `  Backwards");
        // doubled pawns
        if (((board.pieceBoards[1][Piece.PAWN] & pawnSquare.mask_off & FILES[pawnSquare.file])) != 0) {
          if ((board.pieceBoards[1][Piece.PAWN] & FILES[pawnSquare.file] & WHITE_HALF[pawnSquareIndex - 8]) == 0) {
            whitePawnScore -= PAWN_DOUBLED_VALUE;
          }
          //if(DEBUG)System.err.println("    Doubled Pawn");
          pawnHashEntry.pawnFlags.whiteWeakPawns |= pawnSquare.mask_on;
        }
      }
      // white backward pawns
      else if ((board.pieceBoards[1][Piece.PAWN] & WHITE_BACKWARDS_MASK[pawnSquareIndex]) == 0 ||
              (board.pieceBoards[1][Piece.PAWN] & WHITE_ISO_MASK[pawnSquareIndex]) == 0) {
        //if(DEBUG)System.err.println("    Double Backwards");
        whitePawnScore -= PAWN_BACKWARDS_VALUE[pawnSquare.file];
        pawnHashEntry.pawnFlags.whiteWeakPawns |= pawnSquare.mask_on;
        //if(DEBUG)System.err.println("  `  Backwards");
      }
    }

    int[] blackPawnValueTable = PIECE_VALUE_TABLES[0][Piece.PAWN];
    while (blackPawns != 0) {
      int pawnSquareIndex = Long.numberOfTrailingZeros(blackPawns);
      pawnSquare = Board.SQUARES[pawnSquareIndex];
      blackPawns ^= pawnSquare.mask_on;

      //if(DEBUG)System.err.println("  p@" + pawnSquare.toString());

//      blackPawnScore += Piece.TYPE_VALUES[Piece.PAWN];
      pawnHashEntry.pawnFlags.blackPawnCount++;
      pawnHashEntry.pawnFlags.openFiles &= ~FILES[pawnSquare.file];
      pawnHashEntry.pawnFlags.openRanks &= ~RANKS[pawnSquare.rank];
      //blackPawnScore += blackPawnValueTable[pawnSquareIndex];
      pawnHashEntry.pawnFlags.closedFiles[0] |= FILES[pawnSquare.file] & BLACK_HALF[pawnSquareIndex];
      pawnHashEntry.pawnFlags.pawnAttacks[0] |= MoveGeneration.attackVectors[1][Piece.PAWN][pawnSquareIndex];


      if (!board.isEndgame()) {

        if (pawnSquare.file == 0) {
          // black pawn chain (A1-H8)
          /*
                  if (((board.pieceBoards[0][Piece.PAWN] & Board.SQUARES[pawnSquareIndex + 9].mask_on) != 0))
                  {
                    blackPawnScore += blackPawnValueTable[pawnSquareIndex] >> 2;
                    pawnHashEntry.pawnFlags.closedFiles[1] |= FILES[pawnSquare.file] & WHITE_HALF[pawnSquareIndex];
                    //if(DEBUG)System.err.println("    A1-H8 Chain");
                  }
          */
        } else if (pawnSquare.file == 7) {
          // black pawn phalanx
          if ((board.pieceBoards[0][Piece.PAWN] & Board.SQUARES[pawnSquareIndex - 1].mask_on) != 0 && pawnSquare.rank < 6) {
            blackPawnScore += blackPawnValueTable[pawnSquareIndex] >> 1;
            //if(DEBUG)System.err.println("    Phalanx");
          }
          // black pawn chain (H1-A8)
          /*
                  if (((board.pieceBoards[0][Piece.PAWN] & Board.SQUARES[pawnSquareIndex + 7].mask_on) != 0))
                  {
                    blackPawnScore += blackPawnValueTable[pawnSquareIndex] >> 2;
                    pawnHashEntry.pawnFlags.closedFiles[1] |= FILES[pawnSquare.file] & WHITE_HALF[pawnSquareIndex];
                    //if(DEBUG)System.err.println("    H1-A8 Chain");
                  }
          */
        } else {
          // black pawn phalanx
          if ((board.pieceBoards[0][Piece.PAWN] & Board.SQUARES[pawnSquareIndex - 1].mask_on) != 0 && pawnSquare.rank < 6) {
            blackPawnScore += blackPawnValueTable[pawnSquareIndex] >> 1;
            //if(DEBUG)System.err.println("    Phalanx");
          }
          // black pawn chain (A1-H8)
          /*
                  if (((board.pieceBoards[0][Piece.PAWN] & Board.SQUARES[pawnSquareIndex + 9].mask_on) != 0))
                  {
                    blackPawnScore += blackPawnValueTable[pawnSquareIndex] >> 2;
                    pawnHashEntry.pawnFlags.closedFiles[1] |= FILES[pawnSquare.file] & WHITE_HALF[pawnSquareIndex];
                    //if(DEBUG)System.err.println("    A1-H8 Chain");
                  }
                  // black pawn chain (H1-A8)
                  if (((board.pieceBoards[0][Piece.PAWN] & Board.SQUARES[pawnSquareIndex + 7].mask_on) != 0))
                  {
                    blackPawnScore += blackPawnValueTable[pawnSquareIndex] >> 2;
                    pawnHashEntry.pawnFlags.closedFiles[1] |= FILES[pawnSquare.file] & WHITE_HALF[pawnSquareIndex];
                    //if(DEBUG)System.err.println("    H1-A8 Chain");
                  }
          */
        }
      }

      // locked files
      if ((board.pieceBoards[1][Piece.PAWN] & Board.SQUARES[pawnSquareIndex - 8].mask_on) != 0) {
        pawnHashEntry.pawnFlags.closedFiles[0] |= FILES[pawnSquare.file] & BLACK_HALF[pawnSquareIndex];
        pawnHashEntry.pawnFlags.lockedFiles |= FILES[pawnSquare.file];
        //if(DEBUG)System.err.println("    Locked File");
//        continue;
      }

      // black passed pawns
      if ((board.pieceBoards[1][Piece.PAWN] & BLACK_PASSED_MASK[pawnSquareIndex]) == 0 &&
              (board.pieceBoards[0][Piece.PAWN] & FILES[pawnSquare.file] & WHITE_HALF[pawnSquareIndex - 8]) == 0) {
        pawnHashEntry.pawnFlags.blackPassedPawns |= pawnSquare.mask_on;
        //if(DEBUG)System.err.println("    Passed");
      }
      // black isolated pawns
      else if((board.pieceBoards[0][Piece.PAWN] & BLACK_ISO_MASK[pawnSquareIndex]) == 0) {
        blackPawnScore -= PAWN_BACKWARDS_VALUE[pawnSquare.file];
        pawnHashEntry.pawnFlags.blackWeakPawns |= pawnSquare.mask_on;
        //if(DEBUG)System.err.println("    Backwards");
        // doubled pawns
        if ((board.pieceBoards[0][Piece.PAWN] & pawnSquare.mask_off & FILES[pawnSquare.file]) != 0) {
          if ((board.pieceBoards[0][Piece.PAWN] & FILES[pawnSquare.file] & BLACK_HALF[pawnSquareIndex + 8]) == 0) {
            blackPawnScore -= PAWN_DOUBLED_VALUE;
          }
          pawnHashEntry.pawnFlags.blackWeakPawns |= pawnSquare.mask_on;
          //if(DEBUG)System.err.println("    Doubled Pawn");
        }
      }
      // black backward pawns
      else if ((board.pieceBoards[0][Piece.PAWN] & BLACK_BACKWARDS_MASK[pawnSquareIndex]) == 0) {
        blackPawnScore -= PAWN_BACKWARDS_VALUE[pawnSquare.file];
        pawnHashEntry.pawnFlags.blackWeakPawns |= pawnSquare.mask_on;
        //if(DEBUG)System.err.println("    Backwards");
      }
    }

    score += whitePawnScore - blackPawnScore;

/*
    int whiteQueenside = normalizeQueensidePawns(board, pawnHashEntry.pawnFlags.lockedFiles, 1);
    int blackQueenside = normalizeQueensidePawns(board, pawnHashEntry.pawnFlags.lockedFiles, 0);
    int whiteKingside = normalizeKingsidePawns(board, pawnHashEntry.pawnFlags.lockedFiles, 1);
    int blackKingside = normalizeKingsidePawns(board, pawnHashEntry.pawnFlags.lockedFiles, 0);
    int whiteCenter = normalizeCenterPawns(board, 1);
    int blackCenter = normalizeCenterPawns(board, 0);

    int queensideScore = PAWN_SCORES[(whiteQueenside << 3) + blackQueenside];
    int kingsideScore = PAWN_SCORES[(whiteKingside << 3) + blackKingside];
    int centerScore = PAWN_SCORES[(whiteCenter << 3) + blackCenter];

    pawnHashEntry.pawnFlags.centerScore = centerScore;
    pawnHashEntry.pawnFlags.endgameScore = (queensideScore + kingsideScore);
*/

    pawnHashEntry.pawnFlags.score = (int)(score);

    // Store pawn hash
    pawnHashEntry.hash = board.pawnHash;

    return pawnHashEntry.pawnFlags;
  }

  public final int scorePassedPawns(Board board,
                                    long whitePassedPawns,
                                    long blackPassedPawns) {
/*
    if(whiteMaterial > 21 || blackMaterial > 21)
    {
      return 0;
    }
*/

    int whiteScore = 0;
    int blackScore = 0;
    boolean kingTaxed = false;

    while (whitePassedPawns != 0) {
      int pawnSquareIndex = Long.numberOfTrailingZeros(whitePassedPawns);
      whitePassedPawns ^= 1L << pawnSquareIndex;

      Square square = Board.SQUARES[pawnSquareIndex];
      final int[] white_passed_pawn_values;
      white_passed_pawn_values = WHITE_PASSED_PAWN_VALUES;

      int startingIndex = square.rank == 1 ? pawnSquareIndex + 8 : pawnSquareIndex;

      int white_passed_pawn_value = white_passed_pawn_values[square.rank];

      final boolean runawayPawn = (WHITE_RUNAWAY_PAWN_MASK[board.turn == 1 ? startingIndex + 8: startingIndex] & board.blackKing.square.mask_on) == 0;
      if (board.isEndgame() && !runawayPawn && !kingTaxed) {
        int pathToAttackPawns = allShortestPaths(
                board.blackKing.square.mask_on,
                square.mask_on,
                ~(board.pieceBoards[0][Board.ALL_PIECES] | board.attacks[1]) | square.mask_on | board.blackKing.square.mask_on,
                paths, board);
        int pathToDefendPawns = allShortestPaths(
                board.whiteKing.square.mask_on,
                square.mask_on,
                ~(board.pieceBoards[1][Board.ALL_PIECES] | board.attacks[0]) | square.mask_on | board.whiteKing.square.mask_on,
                paths, board);

        if (pathToAttackPawns > 0) {
          if ((board.turn == 0 && pathToAttackPawns < pathToDefendPawns) ||
                  (board.turn == 1 && pathToAttackPawns - 1 < pathToDefendPawns)) {
            kingTaxed = true;
            white_passed_pawn_value = white_passed_pawn_value >> 1;
          }
        }
      }

      if (board.isEndgame() && runawayPawn) {
        whiteScore += white_passed_pawn_value;
      }
      Square advancingSquare = Board.SQUARES[startingIndex + 8];
      if (board.boardSquares[advancingSquare.index128].piece == null) {
        whiteScore += white_passed_pawn_value >> 1;
        if ((board.attackState[0][advancingSquare.index64] == 0)) {
          whiteScore += white_passed_pawn_value >> 1;

          if (startingIndex + 16 < 64) {
            advancingSquare = Board.SQUARES[startingIndex + 16];
            if (board.boardSquares[advancingSquare.index128].piece == null) {
              if ((board.attackState[0][advancingSquare.index64] == 0)) {
                whiteScore += white_passed_pawn_value >> 1;

                if (startingIndex + 24 < 64) {
                  advancingSquare = Board.SQUARES[startingIndex + 24];
                  if (board.boardSquares[advancingSquare.index128].piece == null) {
                    if ((board.attackState[0][advancingSquare.index64] == 0)) {
                      whiteScore += white_passed_pawn_value >> 1;
                    }
                  }
                } else {
                  whiteScore += white_passed_pawn_value >> 1;
                }
              }
            }
          } else {
            whiteScore += white_passed_pawn_value;
          }
        }
      } else {
        whiteScore += white_passed_pawn_value >> 2;
      }
    }

    kingTaxed = false;
    while (blackPassedPawns != 0) {
      int pawnSquareIndex = Long.numberOfTrailingZeros(blackPassedPawns);
      blackPassedPawns ^= 1L << pawnSquareIndex;

      Square square = Board.SQUARES[pawnSquareIndex];


      final int[] black_passed_pawn_values;
      black_passed_pawn_values = BLACK_PASSED_PAWN_VALUES;

      int black_passed_pawn_value = black_passed_pawn_values[square.rank];

      int startingIndex = square.rank == 6 ? pawnSquareIndex - 8 : pawnSquareIndex;

      final boolean runawayPawn = (BLACK_RUNAWAY_PAWN_MASK[board.turn == 0 ? startingIndex - 8 : startingIndex] & board.whiteKing.square.mask_on) == 0;
      if (board.isEndgame() && !runawayPawn && !kingTaxed) {
        int pathToAttackPawns = allShortestPaths(
                board.whiteKing.square.mask_on,
                square.mask_on,
                ~(board.pieceBoards[1][Board.ALL_PIECES] | board.attacks[0]) | square.mask_on | board.whiteKing.square.mask_on,
                paths, board);

        int pathToDefendPawns = allShortestPaths(
                board.blackKing.square.mask_on,
                square.mask_on,
                ~(board.pieceBoards[0][Board.ALL_PIECES] | board.attacks[1]) | square.mask_on | board.blackKing.square.mask_on,
                paths, board);
        if (pathToAttackPawns > 0) {
          if ((board.turn == 1 && pathToAttackPawns < pathToDefendPawns) ||
                  (board.turn == 0 && pathToAttackPawns - 1 < pathToDefendPawns)) {
            kingTaxed = true;
            black_passed_pawn_value = black_passed_pawn_value >> 1;
          }
        }
      }

      if (board.isEndgame() && runawayPawn) {
        blackScore += black_passed_pawn_value;
      }
      Square advancingSquare = Board.SQUARES[startingIndex - 8];
      if (board.boardSquares[advancingSquare.index128].piece == null) {
        blackScore += black_passed_pawn_value >> 1;
        if ((board.attackState[1][advancingSquare.index64] == 0)) {
          blackScore += black_passed_pawn_value >> 1;

          if (startingIndex - 16 > -1) {
            advancingSquare = Board.SQUARES[startingIndex - 16];
            if (board.boardSquares[advancingSquare.index128].piece == null) {
              if ((board.attackState[1][advancingSquare.index64] == 0)) {
                blackScore += black_passed_pawn_value;

                if (startingIndex - 24 > -1) {
                  advancingSquare = Board.SQUARES[startingIndex - 24];
                  if (board.boardSquares[advancingSquare.index128].piece == null) {
                    if ((board.attackState[1][advancingSquare.index64] == 0)) {
                      blackScore += black_passed_pawn_value >> 1;
                    }
                  }
                } else {
                  blackScore += black_passed_pawn_value >> 1;
                }
              }
            }
          } else {
            blackScore += black_passed_pawn_value;
          }
        }
      } else {
        blackScore += black_passed_pawn_value >> 2;
      }
    }

    double whiteMaterialRatio = 1D - (board.materialValue[1] / 43D);
    double blackMaterialRatio = 1D - (board.materialValue[0] / 43D);

    whiteScore *= blackMaterialRatio;
    blackScore *= whiteMaterialRatio;

    return whiteScore - blackScore;
  }

  long[] paths = new long[32];

  //////////////////
  // EVAL WHITE KING
  //////////////////

  public int evalWhiteKing(Board board, PawnFlags pawnFlags) {
    int score = 0;

    if (board.pieceValues < 17 || (board.pieceBoards[0][Piece.QUEEN] == 0)) {
      score += PIECE_VALUE_TABLES[1][Piece.KNIGHT][board.whiteKing.square.index64];
/*
      int nearbyPassedPawns = Long.bitCount(MoveGeneration.attackVectors[1][Piece.KING][board.whiteKing.square.index64] & (pawnFlags.whitePassedPawns | pawnFlags.blackPassedPawns));
      score += nearbyPassedPawns * board.whiteKing.square.rank * 3;
*/

      if (board.pieceValues < 9) {
        Square square;
        long targetPawns = pawnFlags.blackWeakPawns & ~board.attacks[0];

        while (targetPawns != 0) {

          square = Board.SQUARES[Long.numberOfTrailingZeros(targetPawns)];
          targetPawns &= square.mask_off;

          int pathToAttackPawns = allShortestPaths(
                  board.whiteKing.square.mask_on,
                  square.mask_on,
                  ~(board.pieceBoards[1][Board.ALL_PIECES] | board.attacks[0]) | square.mask_on | board.whiteKing.square.mask_on,
                  paths, board);
          int pathToDefendPawns = allShortestPaths(
                  board.blackKing.square.mask_on,
                  square.mask_on,
                  ~(board.pieceBoards[0][Board.ALL_PIECES] | board.attacks[1]) | square.mask_on | board.blackKing.square.mask_on,
                  paths, board);

          if (pathToAttackPawns > 0) {
            if (board.turn == 0 && pathToAttackPawns < pathToDefendPawns) {
              score += ENDGAME_KING_PAWN_RACE;
              break;
            } else if (board.turn == 1 && pathToAttackPawns < pathToDefendPawns - 1) {
              score += ENDGAME_KING_PAWN_RACE;
              break;
            }
          }
        }
      }
      return score;
    }

    int kingSafety = scoreAttackingPieces(board, board.whiteKing.square, 0);
    int queensideSafety = 0;
    int kingsideSafety = 0;
    if (board.stats.whiteKingMoves == 0) {
      int options = 7;

      if (board.stats.whiteKingsideRookMoves == 0) {
        kingsideSafety = scoreAttackingPieces(board, Square.G1, 0);
      } else {
        options -= 3;
      }

      if (board.stats.whiteQueensideRookMoves == 0) {
        queensideSafety = scoreAttackingPieces(board, Square.C1, 0);
      } else {
        options -= 3;
      }

      score -= (kingsideSafety + queensideSafety + kingSafety) / options;
    }
    else {
      score -= kingSafety;
    }

    return score;
  }

  //////////////////
  // EVAL BLACK KING
  //////////////////

  public int evalBlackKing(Board board, PawnFlags pawnFlags) {
    int score = 0;

    if (board.pieceValues < 17 || (board.pieceBoards[1][Piece.QUEEN] == 0)) {
      score += PIECE_VALUE_TABLES[0][Piece.KNIGHT][board.blackKing.square.index64];
/*
      int nearbyPassedPawns = Long.bitCount(MoveGeneration.attackVectors[0][Piece.KING][board.blackKing.square.index64] & (pawnFlags.whitePassedPawns | pawnFlags.blackPassedPawns));
      score += nearbyPassedPawns * (7 - board.blackKing.square.rank) * 3;
*/


      if (board.pieceValues < 9) {
        Square square;
        long targetPawns = pawnFlags.whiteWeakPawns & ~board.attacks[1];

        while (targetPawns != 0) {

          square = Board.SQUARES[Long.numberOfTrailingZeros(targetPawns)];
          targetPawns &= square.mask_off;

          int pathToAttackPawns = allShortestPaths(
                  board.blackKing.square.mask_on,
                  square.mask_on,
                  ~(board.pieceBoards[0][Board.ALL_PIECES] | board.attacks[1]) | square.mask_on | board.blackKing.square.mask_on,
                  paths, board);
          int pathToDefendPawns = allShortestPaths(
                  board.whiteKing.square.mask_on,
                  square.mask_on,
                  ~(board.pieceBoards[1][Board.ALL_PIECES] | board.attacks[0]) | square.mask_on | board.whiteKing.square.mask_on,
                  paths, board);

          if (pathToAttackPawns > 0) {
            if (board.turn == 0 && pathToAttackPawns < pathToDefendPawns) {
              score += ENDGAME_KING_PAWN_RACE;
              break;
            } else if (board.turn == 1 && pathToAttackPawns < pathToDefendPawns - 1) {
              score += ENDGAME_KING_PAWN_RACE;
              break;
            }
          }
        }
      }

      return score;
    }

    int kingSafety = scoreAttackingPieces(board, board.blackKing.square, 1);
    int queensideSafety = 0;
    int kingsideSafety = 0;
    if (board.stats.blackKingMoves == 0) {
      int options = 7;

      if (board.stats.blackKingsideRookMoves == 0) {
        kingsideSafety = scoreAttackingPieces(board, Square.G8, 1);
      } else {
        options -= 3;
      }

      if (board.stats.blackQueensideRookMoves == 0) {
        queensideSafety = scoreAttackingPieces(board, Square.C8, 1);
      } else {
        options -= 3;
      }


      score -= (kingsideSafety + queensideSafety + kingSafety) / options;
    }
    else {
      score -= kingSafety;
    }

    return score;
  }

  private static int[][] KING_SAFETY_STAGING_AREA = new int[2][3];
  private static int[][] KING_SAFETY_PAWN_AREA = new int[2][3];
  private static int[][] KING_SAFETY_TINY_AREA = new int[2][5];

  static {
    KING_SAFETY_STAGING_AREA[1] = new int[]{31, 32, 33};
    KING_SAFETY_STAGING_AREA[0] = new int[]{-31, -32, -33};

    KING_SAFETY_PAWN_AREA[1] = new int[]{15, 16, 17};
    KING_SAFETY_PAWN_AREA[0] = new int[]{-15, -16, -17};

    KING_SAFETY_TINY_AREA[1] = new int[]{1, -1, -15, -16, -17};
    KING_SAFETY_TINY_AREA[0] = new int[]{1, -1, 15, 16, 17};

  }

  /**
   * Determine the quality of the pawn shelter for the given kingSquare
   *
   * @param pawnFlags
   * @param board
   * @param kingSquare
   * @param attackerColor
   * @return
   */
  public int scorePawnShelter(PawnFlags pawnFlags,
                              Board board,
                              Square kingSquare,
                              int attackerColor) {
    int pawnScore = 0;
    int defenderColor = attackerColor ^ 1;
    Board.BoardSquare boardSquare;
    int squareIndex;

    for (i = 0; i < 3; ++i) {
      squareIndex = kingSquare.index128 + KING_SAFETY_PAWN_AREA[defenderColor][i];
      if ((squareIndex & 0x88) != 0) {
        continue;
      }
      boardSquare = board.boardSquares[squareIndex];

      if (boardSquare.piece == null || (boardSquare.piece.color != defenderColor ||
              (boardSquare.piece.type != Piece.PAWN && boardSquare.piece.type != Piece.BISHOP))) {
        if ((pawnFlags.openFiles & FILES[boardSquare.square.file]) != 0) {
          pawnScore += 3;
        } else {
          long colorHalf = attackerColor == 1 ? BLACK_HALF[Square.A6.index64] : WHITE_HALF[Square.H3.index64];
          if ((board.pieceBoards[attackerColor][Piece.PAWN] & FILES[boardSquare.square.file]) == 0) {
            pawnScore += 1;
          }
          if ((board.pieceBoards[defenderColor][Piece.PAWN] & FILES[boardSquare.square.file] & colorHalf) == 0) {
            pawnScore += 1;
          }
          pawnScore += 1;
        }
      }
    }
    return 9 - pawnScore;
  }

  /**
   * @param board
   * @param kingSquare
   * @param attackerColor
   * @return
   */
  int adjacentScore = 0;
  int stagingScore = 0;
  int safeSquareCount = 0;
  int defendedAdjacentCount = 1;
  int defendedStagingCount = 1;
  Square kingAreaSquare;
  int defenderColor;
  int attackerState;
  int defenderAttackState;

  public final int scoreAttackingPieces(Board board,
                                        Square kingSquare,
                                        int attackerColor) {
    adjacentScore = 0;
    stagingScore = 0;
    defenderColor = attackerColor ^ 1;
    defendedAdjacentCount = 3;
    safeSquareCount = 0;
    defendedStagingCount =  8;

    for (i = 0; i < KING_NEAR_AREA[defenderColor][kingSquare.index64].length; i++) {
      kingAreaSquare = KING_NEAR_AREA[defenderColor][kingSquare.index64][i];
      if ((board.squareAttackers[kingAreaSquare.index64] & board.pieceBoards[attackerColor][Board.ALL_PIECES]) != 0) {
        attackerState = board.attackState[attackerColor][kingAreaSquare.index64];
        defenderAttackState = board.attackState[defenderColor][kingAreaSquare.index64];
        int attackScore = attackScores[attackerState];
        if (smallestAttacker[defenderAttackState] == Piece.KING) {
          // undefended square in the near area
          adjacentScore += attackScore;
        } else if (smallestAttacker[attackerState] <= smallestAttacker[defenderAttackState] &&
                   attackerCount[attackerState] > attackerCount[defenderAttackState]) {
          adjacentScore += attackScore >> 1;
        } else {
          adjacentScore += attackScore >> 2;
        }
      } else {
        if (board.boardSquares[kingAreaSquare.index128].piece == null ||
                board.boardSquares[kingAreaSquare.index128].piece.color != defenderColor) {
          safeSquareCount++;
        }
      }
    }

    for (i = 0; i < KING_PAWN_AREA[defenderColor][kingSquare.index64].length; i++) {
      kingAreaSquare = KING_PAWN_AREA[defenderColor][kingSquare.index64][i];
      if (((board.pieceBoards[defenderColor][Piece.PAWN]) &
              FILES[kingAreaSquare.file] &
              (defenderColor == 0 ? WHITE_HALF[kingAreaSquare.index64] : BLACK_HALF[kingSquare.index64])) == 0) {
        stagingScore += KING_ON_HALF_OPEN_FILE;
        defendedStagingCount -= 1;
      }
      if (((board.pieceBoards[attackerColor][Piece.PAWN]) &
              FILES[kingAreaSquare.file]) == 0) {
        stagingScore += KING_ON_HALF_OPEN_FILE;
        defendedStagingCount -= 2;
      }
      else if (((board.pieceBoards[attackerColor][Piece.PAWN]) &
              FILES[kingAreaSquare.file] &
              (defenderColor == 0 ? WHITE_HALF[kingAreaSquare.index64] : BLACK_HALF[kingSquare.index64])) == 0) {
        stagingScore += KING_ON_HALF_OPEN_FILE >> 2;
        defendedStagingCount -= 1;
      }
      if ((board.squareAttackers[kingAreaSquare.index64] & board.pieceBoards[attackerColor][Board.ALL_PIECES]) != 0) {
        attackerState = board.attackState[attackerColor][kingAreaSquare.index64];
        defenderAttackState = board.attackState[defenderColor][kingAreaSquare.index64];
        int attackScore = attackScores[attackerState];

        if (smallestAttacker[defenderAttackState] == Piece.KING) {
          // undefended square in the near area
          adjacentScore += attackScore;
        } else if (smallestAttacker[attackerState] <= smallestAttacker[defenderAttackState] &&
                   attackerCount[attackerState] > attackerCount[defenderAttackState]) {
          adjacentScore += attackScore >> 1;
        } else {
          adjacentScore += attackScore >> 2;
        }
      } else {
        if (board.boardSquares[kingAreaSquare.index128].piece == null ||
                board.boardSquares[kingAreaSquare.index128].piece.color != defenderColor) {
          safeSquareCount++;
        }
      }
    }

    if (safeSquareCount == 0 && board.attackState[attackerColor][kingSquare.index64] > 0) {
      return ABSearch.MATE;
    }


    for (i = 0; i < KING_STAGING_AREA[defenderColor][kingSquare.index64].length; i++) {
      kingAreaSquare = KING_STAGING_AREA[defenderColor][kingSquare.index64][i];
      if ((board.squareAttackers[kingAreaSquare.index64] & board.pieceBoards[attackerColor][Board.ALL_PIECES]) != 0) {
        attackerState = board.attackState[attackerColor][kingAreaSquare.index64];
        //int attackerState = board.calculateAttackState(attackerColor, kingAreaSquare.index64);
/*
        String attackerStr = visualizeAttackState(attackState[attackerColor][kingAreaSquare.index64]);
        String defenderStr = visualizeAttackState(attackState[defenderColor][kingAreaSquare.index64]);
*/
        if (smallestAttacker[board.attackState[defenderColor][kingAreaSquare.index64]] != Piece.PAWN) {
          defendedStagingCount--;
          stagingScore += attackScores[attackerState] >> 2;
        }
      }
      if (smallestAttacker[attackerState] <= smallestAttacker[board.attackState[defenderColor][kingAreaSquare.index64]] &&
            attackerCount[attackerState] > attackerCount[board.attackState[defenderColor][kingAreaSquare.index64]]) {
        defendedStagingCount--;
        stagingScore += attackScores[attackerState] >> 2;
      }
    }

    if (safeSquareCount == 0 && board.attackState[attackerColor][kingSquare.index64] > 0) {
      return ABSearch.MATE;
    }

    return (adjacentScore /  max(1, safeSquareCount)) + (stagingScore / max(1, defendedStagingCount));


/*
    if(board.turn == defenderColor)
    {
      totalScore = totalScore >> 1;
    }
    if(totalScore > 100)
    {
      System.out.println("attackingPieces [" + kingSquare + "]: (* " + pawnShelter + " " + totalScore + ") " + board);
    }

    return totalScore;
*/
  }

  private static long getKingAreaA1H8(Square square) {
    return MoveGeneration.attackVectors[0][7][square.index64]
            | (square.file > 0 ?
            MoveGeneration.attackVectors[0][7][square.index64 - 1] :
            0)
            | (square.file < 7 ?
            MoveGeneration.attackVectors[0][7][square.index64 + 1] :
            0)
            | (square.rank > 0 ?
            MoveGeneration.attackVectors[0][7][square.index64 - 8] :
            0)
            | (square.rank < 7 ?
            MoveGeneration.attackVectors[0][7][square.index64 + 8] :
            0)
            | (square.rank < 7 && square.file > 0 ?
            MoveGeneration.attackVectors[0][7][square.index64 + 7] :
            0)
            | (square.rank < 7 && square.file < 7 ?
            MoveGeneration.attackVectors[0][7][square.index64 + 9] :
            0)
            | (square.rank > 0 && square.file > 0 ?
            MoveGeneration.attackVectors[0][7][square.index64 - 9] :
            0)
            | (square.rank > 0 && square.file < 7 ?
            MoveGeneration.attackVectors[0][7][square.index64 - 7] :
            0);
  }

  private static long getKingAreaH1A8(Square square) {
    return MoveGeneration.attackVectors[0][8][square.index64]
            | (square.file > 0 ?
            MoveGeneration.attackVectors[0][8][square.index64 - 1] :
            0)
            | (square.file < 7 ?
            MoveGeneration.attackVectors[0][8][square.index64 + 1] :
            0)
            | (square.rank > 0 ?
            MoveGeneration.attackVectors[0][8][square.index64 - 8] :
            0)
            | (square.rank < 7 ?
            MoveGeneration.attackVectors[0][8][square.index64 + 8] :
            0)
            | (square.rank < 7 && square.file > 0 ?
            MoveGeneration.attackVectors[0][8][square.index64 + 7] :
            0)
            | (square.rank < 7 && square.file < 7 ?
            MoveGeneration.attackVectors[0][8][square.index64 + 9] :
            0)
            | (square.rank > 0 && square.file > 0 ?
            MoveGeneration.attackVectors[0][8][square.index64 - 9] :
            0)
            | (square.rank > 0 && square.file < 7 ?
            MoveGeneration.attackVectors[0][8][square.index64 - 7] :
            0);
  }

  private int max(int a, int b) {
    return a > b ?
            a :
            b;
  }

  private int min(int a, int b) {
    return a < b ?
            a :
            b;
  }

  private double max(double a, double b) {
    return a > b ?
            a :
            b;
  }

  private double min(double a, double b) {
    return a > b ?
            a :
            b;
  }

  public static void multiplyAll(int[] original, int factor) {
    for (int i = 0; i < original.length; i++) {
      original[i] *= factor;
    }
  }

  public int getMaterial(Board board) {
    int whiteKnightCount = Long.bitCount(board.pieceBoards[1][Piece.KNIGHT]);
    int blackKnightCount = Long.bitCount(board.pieceBoards[0][Piece.KNIGHT]);

    int whiteBishopCount = Long.bitCount(board.pieceBoards[1][Piece.BISHOP]);
    int blackBishopCount = Long.bitCount(board.pieceBoards[0][Piece.BISHOP]);

    int whiteRookCount = Long.bitCount(board.pieceBoards[1][Piece.ROOK]);
    int blackRookCount = Long.bitCount(board.pieceBoards[0][Piece.ROOK]);

    int whiteQueenCount = Long.bitCount(board.pieceBoards[1][Piece.QUEEN]);
    int blackQueenCount = Long.bitCount(board.pieceBoards[0][Piece.QUEEN]);

    int whiteMaterial = (whiteKnightCount * 3) + (whiteBishopCount * 3) + (whiteRookCount * 5) + (whiteQueenCount * 9);
    int blackMaterial = (blackKnightCount * 3) + (blackBishopCount * 3) + (blackRookCount * 5) + (blackQueenCount * 9);

    return whiteMaterial + blackMaterial;
  }

  public int getMaterialDifference(Board board) {
    int whiteKnightCount = Long.bitCount(board.pieceBoards[1][Piece.KNIGHT]);
    int blackKnightCount = Long.bitCount(board.pieceBoards[0][Piece.KNIGHT]);

    int whiteBishopCount = Long.bitCount(board.pieceBoards[1][Piece.BISHOP]);
    int blackBishopCount = Long.bitCount(board.pieceBoards[0][Piece.BISHOP]);

    int whiteRookCount = Long.bitCount(board.pieceBoards[1][Piece.ROOK]);
    int blackRookCount = Long.bitCount(board.pieceBoards[0][Piece.ROOK]);

    int whiteQueenCount = Long.bitCount(board.pieceBoards[1][Piece.QUEEN]);
    int blackQueenCount = Long.bitCount(board.pieceBoards[0][Piece.QUEEN]);

    int whiteMaterial = (whiteKnightCount * 3) + (whiteBishopCount * 3) + (whiteRookCount * 5) + (whiteQueenCount * 9);
    int blackMaterial = (blackKnightCount * 3) + (blackBishopCount * 3) + (blackRookCount * 5) + (blackQueenCount * 9);

    return whiteMaterial - blackMaterial;
  }


  public int getPawns(Board board) {
    int whitePawnCount = Long.bitCount(board.pieceBoards[1][Piece.KNIGHT]);
    int blackPawnCount = Long.bitCount(board.pieceBoards[0][Piece.KNIGHT]);

    return whitePawnCount + blackPawnCount;
  }

  public int getPawnsDifference(Board board) {
    int whitePawnCount = Long.bitCount(board.pieceBoards[1][Piece.KNIGHT]);
    int blackPawnCount = Long.bitCount(board.pieceBoards[0][Piece.KNIGHT]);

    return whitePawnCount - blackPawnCount;
  }

  int[] swapScores = new int[48];

  public int swapMove(Board board, Square fromSquare, Square toSquare, int color, int movedValue, int takenValue) {
    int swapIndex = 1;

    long rooksAndQueens = board.pieceBoards[0][Board.QUEENS_ROOKS] | board.pieceBoards[1][Board.QUEENS_ROOKS];

    long attackers = board.squareAttackers[toSquare.index64] |
            (board.squareAttackers[fromSquare.index64] & rooksAndQueens & MoveGeneration.shadowVectors[toSquare.index128][fromSquare.index128]);

    swapScores[0] = takenValue;
    int attackedPiece = movedValue;

//    swapScores[swapIndex++] = -swapScores[swapIndex - 1] + attackedPiece;
    attackers &= fromSquare.mask_off;


    while ((attackers & board.pieceBoards[color][Board.ALL_PIECES]) != 0) {
      swapScores[swapIndex] = -swapScores[swapIndex - 1] + attackedPiece;
      ++swapIndex;

      if ((board.pieceBoards[color][Piece.PAWN] & attackers) != 0) {
        attackedPiece = Piece.TYPE_VALUES[Piece.PAWN];
        attackers &= Board.SQUARES[Long.numberOfTrailingZeros(board.pieceBoards[color][Piece.PAWN] & attackers)].mask_off;
      } else if ((board.pieceBoards[color][Piece.KNIGHT] & attackers) != 0) {
        attackedPiece = Piece.TYPE_VALUES[Piece.KNIGHT];
        attackers &= Board.SQUARES[Long.numberOfTrailingZeros(board.pieceBoards[color][Piece.KNIGHT] & attackers)].mask_off;
      } else if ((board.pieceBoards[color][Piece.BISHOP] & attackers) != 0) {
        attackedPiece = Piece.TYPE_VALUES[Piece.BISHOP];
        attackers &= Board.SQUARES[Long.numberOfTrailingZeros(board.pieceBoards[color][Piece.BISHOP] & attackers)].mask_off;
      } else if ((board.pieceBoards[color][Piece.ROOK] & attackers) != 0) {
        attackedPiece = Piece.TYPE_VALUES[Piece.ROOK];
        attackers &= Board.SQUARES[Long.numberOfTrailingZeros(board.pieceBoards[color][Piece.ROOK] & attackers)].mask_off;
      } else if ((board.pieceBoards[color][Piece.QUEEN] & attackers) != 0) {
        attackedPiece = Piece.TYPE_VALUES[Piece.QUEEN];
        attackers &= Board.SQUARES[Long.numberOfTrailingZeros(board.pieceBoards[color][Piece.QUEEN] & attackers)].mask_off;
      } else if ((board.pieceBoards[color][Piece.KING] & attackers) != 0) {
        attackedPiece = Piece.TYPE_VALUES[Piece.KING];
        attackers &= Board.SQUARES[Long.numberOfTrailingZeros(board.pieceBoards[color][Piece.KING] & attackers)].mask_off;
      } else {
        break;
      }

      color = color ^ 1;
    }


    while (--swapIndex != 0) {
      if (swapScores[swapIndex] > -swapScores[swapIndex - 1]) {
        swapScores[swapIndex - 1] = -swapScores[swapIndex];
      }
    }
    return (swapScores[0]);
  }

  static int[][] PAWN_WING_SCORES = new int[8][8];

  static long DARK_SQUARES;
  static long LIGHT_SQUARES;
  static long[] FILES = new long[8];
  static long[] RANKS = new long[8];

  static long[] WHITE_PASSED_MASK = new long[64];
  static long[] BLACK_PASSED_MASK = new long[64];

  static long[] WHITE_BACKWARDS_MASK = new long[64];
  static long[] BLACK_BACKWARDS_MASK = new long[64];

  static long[] WHITE_ISO_MASK = new long[64];
  static long[] BLACK_ISO_MASK = new long[64];

  static long[] WHITE_RUNAWAY_PAWN_MASK = new long[64];
  static long[] BLACK_RUNAWAY_PAWN_MASK = new long[64];

  private static long[] WHITE_KNIGHT_OUTPOST_MASK = new long[64];
  private static long[] BLACK_KNIGHT_OUTPOST_MASK = new long[64];

  static Square[][][] KING_STAGING_AREA = new Square[2][64][];
  static Square[][][] KING_PAWN_AREA = new Square[2][64][];
  static Square[][][] KING_NEAR_AREA = new Square[2][64][];

  static long[] KING_AREA_A1_H8 = new long[64];
  static long[] KING_AREA_H1_A8 = new long[64];

  static long[] WHITE_HALF = new long[64];
  static long[] BLACK_HALF = new long[64];

  static long FILES_ABCD = FILES[0] | FILES[1] | FILES[2] | FILES[3];
  static long FILES_EFGH = FILES[4] | FILES[5] | FILES[6] | FILES[7];

  static long FILES_AB = FILES[0] | FILES[1];
  static long FILES_CD = FILES[2] | FILES[3];
  static long FILES_EF = FILES[4] | FILES[6];
  static long FILES_GH = FILES[6] | FILES[7];

  static long FILES_DE = FILES[3] | FILES[4];
  static long FILES_CDEF = FILES[2] | FILES[3] | FILES[4] | FILES[5];

  static long CENTER = (FILES_CD | FILES_EF) & (RANKS[3] | RANKS[4]);

  static int[] PAWN_SCORES = new int[64];


  static long mask2 = Square.A1.mask_on | Square.B1.mask_on;
  static long mask3 = Square.A1.mask_on | Square.B1.mask_on | Square.C1.mask_on;

  static long mask3x2 = Square.A1.mask_on | Square.B1.mask_on | Square.C1.mask_on |
          Square.A2.mask_on | Square.B2.mask_on | Square.C2.mask_on;


  // ...
  private static int CENTER_CHECK_BALANCE = 12;

  public static final int[] attackScores = new int[8191];
  public static final int[] attackerCount = new int[8191];
  public static final int[] smallestAttacker = new int[8191];
//  public static int[][][][] swap = new int[2][6][8191][8191];
  public int[] attacks = new int[64];


  // Material Values
  private static double MATERIAL_DIVISOR = 31D;
  private static int TRADE_WHEN_LOSING_VALUE = 20;
  private static int TRADE_WHEN_UP_PAWNS_VALUE = 30;

  // Opening values
  private static int DEVELOPMENT_VALUE = 8;
  private static int PIECE_DOUBLE_MOVE_VALUE = 2;
  private static int QUEEN_TOO_EARLY_VALUE = 26;

  // Pawn values
  private static int[] WHITE_PASSED_PAWN_VALUES = new int[]{0, 7, 13, 25, 90, 185, 250, 250};
  private static int[] WHITE_CONNECTED_PASSED_PAWN_VALUES = WHITE_PASSED_PAWN_VALUES; // new int[]{0, 15, 40, 90, 200, 310, 490, 900};
  private static int[] BLACK_PASSED_PAWN_VALUES = new int[]{250, 250, 185, 90, 25, 13, 7, 0};
  private static int[] BLACK_CONNECTED_PASSED_PAWN_VALUES = BLACK_PASSED_PAWN_VALUES; // new int[]{900, 330, 265, 200, 90, 40,  15, 0};

  private static int PAWN_DOUBLED_VALUE = 20;
  private static int[] PAWN_BACKWARDS_VALUE = new int[]{10, 11, 14, 17, 17, 14, 11, 10}; // new int[]{15, 17, 19, 20, 20, 19, 17, 15};

  private static int WEAK_PAWN_PRESSURE = 12;
  private static int WEAK_PAWN_PRESSURE_EG = 25;

  private static int UNMOVED_CENTER_PAWN = 3;
  private static int UNMOVED_CENTER_PAWN_BLOCKED = 35;
  private static int RUNAWAY_PAWN = 100;
  private static int UNMOVED_CENTER_PAWN_ALMOST_BLOCKED = 8;
  private static int PAWN_LOCKED_VALUE = 9;
  private static int PASSED_PAWN_WIDE_FILE_VALUE = 18;

  private static int PAWN_PHALANX_VALUE = 4;
  private static int[] PAWN_CHAIN_VALUE = {2, 3, 4, 6, 6, 4, 3, 2};

  // Knight Values
  private static int KNIGHT_TRAPPED_VALUE = 50;
  private static int KNIGHT_OUTPOST_VALUE = 8;

  // Bishop Values
  private static int TWO_BISHOPS_VALUE = 19;
  private static int BISHOP_TRAPPED_VALUE = 60;
  private static int BISHOP_OPEN_DIAGONAL_VALUE = 9;

  // Rook Values
  private static int ROOK_ON_OPEN_FILE = 9;
  private static int ROOK_ON_OPEN_FILE_EG = 15;
  private static int ROOK_ON_OPEN_FILE_EG_PP = 30;
  private static int ROOK_ON_HALF_OPEN_FILE = 11;
  private static int TRAPPED_ROOK_VALUE = 240;
  private static int ROOK_OPPOSITE_KING_VALUE = 4;
  private static int ROOK_OPPOSITE_KING_HALF_OPEN_VALUE = 12;
  private static int ROOK_OPPOSITE_KING_OPEN_VALUE = 100;

  // King Values
  private static final int ENDGAME_KING_PAWN_RACE = 30;
  private static int KING_NO_CASTLE_VALUE = 12;
  private static int KING_FORFEIT_CASTLE_VALUE = 45;
  private static int KING_ON_OPEN_FILE = 40;
  private static int KING_ON_HALF_OPEN_FILE = 20;
  private static int WAITING_TO_CASTLE_VALUE = 8;
  private static int KING_OPEN_FILE = 23;
  private static int KING_HALF_OPEN_FILE = 16;
  private static int CASTLE_DESTINATION_VALUE = 12;

  private static int CHECK_IMMEDIATE_KING_VALUE = 3;
  private static int CHECK_NEARBY_KING_VALUE = 3;

  // Piece masks
  private static int PAWN_MASK = 8;
  private static int ROOK_MASK = 16;
  private static int MINOR_MASK = 32;
  private static int QUEEN_MASK = 64;
  private static int KING_MASK = 128;

  private static int[] MOBILITY = {
          0, 0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 8, 10, 11, 13, 14, 17, 19, 21, 24, 27, 30, 33, 36, 39, 42, 46, 49, 53, 56,
          59, 62, 65, 68, 71, 75, 78, 81, 84, 87, 90, 93, 96, 100, 103, 106, 109, 112, 115, 118, 121, 125, 128, 131,
          134, 137, 140, 143, 146, 150, 153, 156, 159, 162, 165, 168, 171, 175, 178, 181, 184, 187, 190, 193, 196,
          200, 203, 206, 209, 212, 215, 218, 221, 225, 228, 231, 234, 237, 240, 243, 246, 250, 253, 256, 259, 262,
          265, 268, 271, 275, 278, 281, 284, 287, 290, 293, 296, 300, 303, 306, 309, 312, 315, 318, 321, 325, 328,
          331, 334, 337, 340, 343, 346, 350, 353, 356, 359, 362, 365, 368, 371, 375, 378, 381, 384, 384, 385, 385,
          385, 386, 386, 387, 387, 387, 388, 388, 389, 389, 389, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390,
          390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390
  };

  static {

    for (int square = 0; square < 64; square++) {
      if (square % 2 == 0) {
        DARK_SQUARES |= Board.SQUARES[square].mask_on;
      } else {
        LIGHT_SQUARES |= Board.SQUARES[square].mask_on;
      }

      FILES[Board.SQUARES[square].file] |= Board.SQUARES[square].mask_on;
      RANKS[Board.SQUARES[square].rank] |= Board.SQUARES[square].mask_on;
    }

    for (int square = 0; square < 64; square++) {
      // Initialize white passed pawn masks
      final int squareRank = Board.SQUARES[square].rank;
      final int squareFile = Board.SQUARES[square].file;
      for (int rank = squareRank + 1; rank < 7; rank++) {
        if (squareFile == 0) {
          WHITE_PASSED_MASK[square] |= Board.SQUARES[squareFile + (8 * rank)].mask_on;
          WHITE_PASSED_MASK[square] |= Board.SQUARES[squareFile + (8 * rank) + 1].mask_on;
        } else if (squareFile == 7) {
          WHITE_PASSED_MASK[square] |= Board.SQUARES[squareFile + (8 * rank) - 1].mask_on;
          WHITE_PASSED_MASK[square] |= Board.SQUARES[squareFile + (8 * rank)].mask_on;
        } else {
          WHITE_PASSED_MASK[square] |= Board.SQUARES[squareFile + (8 * rank) - 1].mask_on;
          WHITE_PASSED_MASK[square] |= Board.SQUARES[squareFile + (8 * rank)].mask_on;
          WHITE_PASSED_MASK[square] |= Board.SQUARES[squareFile + (8 * rank) + 1].mask_on;
        }
      }

      // Initialize black passed pawn masks
      for (int rank = 0; rank < squareRank; rank++) {
        if (squareFile == 0) {
          BLACK_PASSED_MASK[square] |= Board.SQUARES[squareFile + (8 * rank)].mask_on;
          BLACK_PASSED_MASK[square] |= Board.SQUARES[squareFile + (8 * rank) + 1].mask_on;
        } else if (squareFile == 7) {
          BLACK_PASSED_MASK[square] |= Board.SQUARES[squareFile + (8 * rank) - 1].mask_on;
          BLACK_PASSED_MASK[square] |= Board.SQUARES[squareFile + (8 * rank)].mask_on;
        } else {
          BLACK_PASSED_MASK[square] |= Board.SQUARES[squareFile + (8 * rank) - 1].mask_on;
          BLACK_PASSED_MASK[square] |= Board.SQUARES[squareFile + (8 * rank)].mask_on;
          BLACK_PASSED_MASK[square] |= Board.SQUARES[squareFile + (8 * rank) + 1].mask_on;
        }
      }

      for (int file = Math.max(squareFile - (7 - squareRank), 0); file < Math.min(squareFile + 1 + (7 - squareRank), 8); file++) {
        for (int rank = squareRank; rank < 8; rank++) {
          WHITE_RUNAWAY_PAWN_MASK[square] |= Board.SQUARES[file + (8 * rank)].mask_on;
        }
      }

      for (int file = Math.max(squareFile - squareRank, 0); file < Math.min(squareFile + 1 + squareRank, 8); file++) {
        for (int rank = squareRank; rank > -1; rank--) {
          BLACK_RUNAWAY_PAWN_MASK[square] |= Board.SQUARES[file + (8 * rank)].mask_on;
        }
      }


      WHITE_ISO_MASK[square] = BLACK_PASSED_MASK[square];
      if (squareFile > 0) {
        WHITE_ISO_MASK[square] |= Board.SQUARES[square - 1].mask_on;
      }
      if (squareFile < 7) {
        WHITE_ISO_MASK[square] |= Board.SQUARES[square + 1].mask_on;
      }

      BLACK_ISO_MASK[square] = WHITE_PASSED_MASK[square];
      if (squareFile > 0) {
        BLACK_ISO_MASK[square] |= Board.SQUARES[square - 1].mask_on;
      }
      if (squareFile < 7) {
        BLACK_ISO_MASK[square] |= Board.SQUARES[square + 1].mask_on;
      }

      for (int rank = squareRank + (squareRank > 3 ? 4 : 3); rank < 7; rank++) {
        BLACK_ISO_MASK[square] &= ~RANKS[rank];
      }

      for (int rank = squareRank - (squareRank < 5 ? 4 : 3); rank > 0; rank--) {
        WHITE_ISO_MASK[square] &= ~RANKS[rank];
      }

      WHITE_BACKWARDS_MASK[square] = BLACK_PASSED_MASK[square];
      BLACK_BACKWARDS_MASK[square] = WHITE_PASSED_MASK[square];

      if (squareFile > 0) {
        WHITE_BACKWARDS_MASK[square] |= Board.SQUARES[square - 1].mask_on;
        BLACK_BACKWARDS_MASK[square] |= Board.SQUARES[square - 1].mask_on;
      }
      if (squareFile < 7) {
        WHITE_BACKWARDS_MASK[square] |= Board.SQUARES[square + 1].mask_on;
        BLACK_BACKWARDS_MASK[square] |= Board.SQUARES[square + 1].mask_on;
      }

      if (squareFile > 0 && squareFile < 7) {
        WHITE_KNIGHT_OUTPOST_MASK[square] = WHITE_PASSED_MASK[square] & ~FILES[(square & 7)];
        BLACK_KNIGHT_OUTPOST_MASK[square] = BLACK_PASSED_MASK[square] & ~FILES[(square & 7)];
      }

      for (int i = 0; i <= squareRank; i++) {
        WHITE_HALF[square] |= RANKS[i];
      }
      for (int i = 7; i >= squareRank; i--) {
        BLACK_HALF[square] |= RANKS[i];
      }
    }

  }

  static {
    // White Pawn
    PIECE_VALUE_TABLES[1][Piece.PAWN] = new int[]{
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            1, 1, 4, 5, 5, 2, 1, 1,
            2, 2, 16, 20, 20, 3, 2, 2,
            5, 6, 7, 22, 22, 8, 7, 6,
            8, 12, 12, 24, 24, 14, 12, 8,
            10, 14, 16, 20, 20, 16, 14, 10,
            0, 0, 0, 0, 0, 0, 0, 0,
    };

    // Black Pawn
    PIECE_VALUE_TABLES[0][Piece.PAWN] = new int[]{
            0, 0, 0, 0, 0, 0, 0, 0,
            10, 14, 16, 20, 20, 16, 14, 10,
            8, 12, 12, 24, 24, 14, 12, 8,
            5, 6, 7, 22, 22, 8, 7, 6,
            2, 2, 16, 20, 20, 3, 2, 2,
            1, 1, 4, 5, 5, 2, 1, 1,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
    };

    // White Center
    CENTER_VALUE_TABLES[1] = new int[]{
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 5, 8, 8, 5, 0, 0,
            0, 3, 6, 9, 9, 6, 3, 0,
            0, 3, 7, 10, 10, 7, 3, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
    };

    // Black Center
    CENTER_VALUE_TABLES[0] = new int[]{
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 3, 7, 10, 10, 7, 3, 0,
            0, 3, 6, 9, 9, 6, 3, 0,
            0, 0, 5, 8, 8, 5, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
    };

    // White knight
    PIECE_VALUE_TABLES[1][Piece.KNIGHT] = new int[]{
            -20, -3, -7, -5, -5, -7, -3, -20,
            -8, 1, 3, 4, 4, 3, 1, -8,
            -13, 8, 8, 10, 10, 9, 8, -13,
            -6, 5, 9, 14, 14, 9, 5, -6,
            -6, 6, 16, 15, 15, 16, 6, -6,
            -6, 5, 10, 12, 16, 15, 5, -6,
            1, 2, 9, 9, 9, 9, 2, 1,
            -20, -3, -7, -5, -5, -7, -3, -20,
    };
    // Black knight
    PIECE_VALUE_TABLES[0][Piece.KNIGHT] = new int[]{
            -20, -3, -7, -5, -5, -7, -3, -20,
            1, 2, 9, 9, 9, 9, 2, 1,
            -6, 5, 10, 12, 16, 15, 5, -6,
            -6, 6, 13, 15, 15, 16, 6, -6,
            -6, 5, 9, 14, 14, 9, 5, -6,
            -13, 8, 8, 10, 10, 9, 8, -13,
            -8, 1, 3, 4, 4, 3, 1, -8,
            -20, -3, -7, -5, -5, -7, -3, -20,
    };

    // White bishop
    PIECE_VALUE_TABLES[1][Piece.BISHOP] = new int[]{
            -4, -3, -2, -1, -1, -2, -3, -4,
            -5, 6, 1, 7, 7, 1, 6, -5,
            0, 1, 5,  7,  7, 5, 1, 0,
            0, 1, 7,  9,  9, 7, 1, 0,
            0, 1, 7,  9,  9, 7, 1, 0,
            0, 1, 5,  7,  7, 5, 1, 0,
            0, 2, 2, 3, 3, 2, 2, 0,
            -4, -3, -2, -1, -1, -2, -3, -4,
    };
    // Black bishop
    PIECE_VALUE_TABLES[0][Piece.BISHOP] = new int[]{
            -4, -3, -2, -1, -1, -2, -3, -4,
            0, 2, 2, 3, 3, 2, 2, 0,
            0, 1, 5,  7,  7, 5, 1, 0,
            0, 1, 7,  9,  9, 7, 1, 0,
            0, 1, 7,  9,  9, 7, 1, 0,
            0, 1, 5,  7,  7, 5, 1, 0,
            -5, 6, 1, 7, 7, 1, 6, -5,
            -4, -3, -2, -1, -1, -2, -3, -4,
    };

    // White rook
    PIECE_VALUE_TABLES[1][Piece.ROOK] = new int[]{
            -4, -5, 1, 6, 6, 3, -5, -4,
            -5, 0, 1, 6, 6, 1, 0, -5,
            -3, 1, 2, 6, 6, 2, 1, -3,
            -3, 2, 3, 6, 6, 3, 2, -3,
            -3, 3, 4, 6, 6, 4, 3, -3,
            -2, 3, 5, 6, 8, 5, 3, -2,
            2, 4, 6, 8, 8, 6, 4, 2,
            1, 1, 1, 1, 1, 1, 1, 1,
    };
    // Black rook
    PIECE_VALUE_TABLES[0][Piece.ROOK] = new int[]{
             1, 1, 1, 1, 1, 1, 1, 1,
             2, 4, 6, 8, 8, 6, 4, 2,
            -2, 3, 5, 6, 8, 5, 3, -2,
            -3, 3, 4, 6, 6, 4, 3, -3,
            -3, 2, 3, 6, 6, 3, 2, -3,
            -3, 1, 2, 6, 6, 2, 1, -3,
            -5, 0, 1, 6, 6, 1, 0, -5,
            -4, -5, 1, 6, 6, 3, -5, -4,
    };
    // White Queen
    PIECE_VALUE_TABLES[1][Piece.QUEEN] = new int[]{
            -12, -12, -8, 4, 4, -8, -12, -12,
            -6, -5, 1, 5, 5, 1, -5, -6,
            0, 1, 7, 11, 11, 7, 1, 0,
            0, 1, 8, 13, 13, 8, 1, 0,
            0, 1, 5, 11, 11, 5, 1, 0,
            0, 1, 5, 10, 10, 5, 1, 0,
            7, 8, 9, 11, 11, 9, 8, 7,
            5, 6, 6, 8, 8, 6, 6, 5,
    };
    // Black Queen
    PIECE_VALUE_TABLES[0][Piece.QUEEN] = new int[]{
            5, 6, 6, 8, 8, 6, 6, 5,
            7, 8, 9, 11, 11, 9, 8, 7,
            0, 1, 5, 10, 10, 5, 1, 0,
            0, 1, 5, 11, 11, 5, 1, 0,
            0, 1, 8, 13, 13, 8, 1, 0,
            0, 1, 7, 11, 11, 7, 1, 0,
            -6, -5, 1, 5, 5, 1, -5, -6,
            -12, -12, -8, 4, 4, -8, -12, -12,
    };
    // King
    PIECE_VALUE_TABLES[0][Piece.KING] = new int[]{
            2, 1, 1, 2, 1, 3, 1, 2,
            2, 2, 2, 2, 2, 3, 2, 2,
            3, 3, 3, 3, 3, 3, 3, 3,
            3, 4, 4, 4, 4, 4, 4, 3,
            3, 4, 4, 4, 4, 4, 4, 3,
            3, 3, 3, 3, 3, 3, 3, 3,
            2, 2, 2, 2, 2, 3, 2, 2,
            2, 1, 1, 2, 1, 3, 1, 2,
    };

  }

  public static class PawnFlags {
    public long whitePassedPawns;
    public long blackPassedPawns;
    public long whiteWeakPawns;
    public long blackWeakPawns;
    public long openFiles;
    public long openRanks;
    public long lockedFiles;
    public int score;
    public int whitePawnCount;
    public int blackPawnCount;
    public long[] closedFiles;
    public int endgameScore;
    public int centerScore;
    public long[] pawnAttacks;

    public PawnFlags() {
      closedFiles = new long[]{0L, 0L};
      pawnAttacks = new long[2];
      reset();
    }

    public void reset() {
      whitePassedPawns = 0;
      blackPassedPawns = 0;
      whiteWeakPawns = 0;
      blackWeakPawns = 0;
      openFiles = ~0L;
      openRanks = ~0L;
      lockedFiles = 0;
      score = 0;
      whitePawnCount = 0;
      blackPawnCount = 0;
      closedFiles[0] = closedFiles[1] = pawnAttacks[0] = pawnAttacks[1] = 0;

      endgameScore = 0;
      centerScore = 0;
    }
  }

  private int normalizeQueensidePawns(Board board, long lockedFiles, int color) {
    long pawns = board.pieceBoards[color][Piece.PAWN] & ~lockedFiles;
    if ((pawns & FILES[0]) != 0) {
      if ((pawns & FILES[1]) != 0) {
        // ABC
        if ((pawns & FILES[2]) != 0) {
          return 7;
        }
        // AB
        else {
          return 6;
        }

      }
      // A C
      else if ((pawns & FILES[2]) != 0) {
        return 5;
      }
      // A
      else {
        return 4;
      }
    } else if ((pawns & FILES[1]) != 0) {
      //  BC
      if ((pawns & FILES[2]) != 0) {
        return 3;
      }
      //  B
      else {
        return 2;
      }
    }
    //   C
    else if ((pawns & FILES[2]) != 0) {
      return 1;
    }
    return 0;
  }

  private int normalizeKingsidePawns(Board board, long lockedFiles, int color) {
    long pawns = board.pieceBoards[color][Piece.PAWN] & ~lockedFiles;
    if ((pawns & FILES[7]) != 0) {
      if ((pawns & FILES[6]) != 0) {
        // HGF
        if ((pawns & FILES[5]) != 0) {
          return 7;
        }
        // HG
        else {
          return 6;
        }

      }
      // H F
      else if ((pawns & FILES[5]) != 0) {
        return 5;
      }
      // H
      else {
        return 4;
      }
    } else if ((pawns & FILES[6]) != 0) {
      //  GF
      if ((pawns & FILES[5]) != 0) {
        return 3;
      }
      //  G
      else {
        return 2;
      }
    }
    //   F
    else if ((pawns & FILES[5]) != 0) {
      return 1;
    }
    return 0;
  }

  private int normalizeCenterPawns(Board board, int color) {
    long pawns = board.pieceBoards[color][Piece.PAWN];
    if ((pawns & FILES[3]) != 0) {
      // ED
      if ((pawns & FILES[4]) != 0) {
        return 3;
      }
      // E
      else {
        return 2;
      }

    }
    // D
    else if ((pawns & FILES[4]) != 0) {
      return 1;
    }
    return 0;
  }


  long shiftRight(long b) {
    return (b << 1) & 0xfefefefefefefefeL;
  }

  long shiftLeft(long b) {
    return (b >> 1) & 0x7f7f7f7f7f7f7f7fL;
  }

  long shiftUp(long b) {
    return b << 8;
  }

  long shiftDown(long b) {
    return b >>> 8;
  }

/////////////////////////////////////////////////////////////////////////
//
// Returns length of shortest path of set bits present in 'path' that
// 8-way connect any set bit in sq1 to any set bit of sq2. 0 is returned
// if no such path exists. Also fills a sequence of longs asp[length]
// that describes all such shortest paths.

  int allShortestPaths(long sq1, long sq2, long path,
                       long[] asp, Board board) {
    // Do an 8-way flood fill with sq1, masking off bits not in path and
    // storing the fill frontier at every step. Stop when fill reaches
    // any set bit in sq2 or quit if fill cannot progress any further.
    // Then do 8-way flood fill from reached bits in sq2, ANDing the
    // frontiers with those from the first fill in reverse order.

    sq1 &= path;
    sq2 &= path;
    if (sq1 == 0 || sq2 == 0) return 0;
    // Drop bits not in path
    // Early exit if sq1 or sq2 not on any path
    int i = 1;
    asp[0] = sq1;

    while (true)  // Fill from all set bits in sq1, to any set bit in sq2
    {
      if ((sq1 & sq2) != 0) break;                     // Found good path
      long temp = sq1;
      sq1 |= shiftLeft(sq1) | shiftRight(sq1);  // Set all 8 neighbours
      sq1 |= shiftDown(sq1) | shiftUp(sq1);
      sq1 &= path;                              // Drop bits not in path
      if (sq1 == temp) return 0;                // Fill has stopped
      asp[i++] = sq1 & ~temp;                   // Store fill frontier
    }

    int length = i - 1;                           // Remember path length
    asp[--i] = (sq2 &= sq1);                      // Drop unreached bits

    while (i != 0)  // Fill from reached bits in sq2
    {
      long temp = sq2;
      sq2 |= shiftLeft(sq2) | shiftRight(sq2);  // Set all 8 neighbours
      sq2 |= shiftDown(sq2) | shiftUp(sq2);
      sq2 &= path;                              // Drop bits not in path
      asp[--i] &= sq2 & ~temp;                  // Intersect frontiers
    }

    return length;
  }
}
