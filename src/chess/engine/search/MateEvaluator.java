/* $Id$ */

package chess.engine.search;

import chess.engine.utils.MoveGeneration;
import chess.engine.utils.MoveGenerationConstants;
import chess.engine.model.Board;
import chess.engine.model.Piece;
import chess.engine.model.Square;

/**
 * @author Joshua Levine <jlevine@theladders.com>
 * @version $Revision$ $Name$ $Date$
 */
public class MateEvaluator implements BoardEvaluator
{
  private MoveGeneration moveGeneration;

  public MateEvaluator(MoveGeneration moveGeneration)
  {
    this.moveGeneration = moveGeneration;
  }

  public int scorePosition(Board board, int alpha, int beta)
  {
    int blackMaterial = 0;
    int whiteMaterial = 0;

    int score = 0;

/*
    // Material
    for(Piece piece : board.pieces)
    {
      if(piece.square != null)
      {
        if(piece.type != Piece.KING)
          if(piece.color == 1)
          {
            whiteMaterial += piece.type;
          }
          else
          {
            blackMaterial += piece.type;
          }
      }
    }
*/

    score += whiteMaterial * countAdjacentChecks(board, board.blackKing.square, 1) << 3;
    score -= blackMaterial * countAdjacentChecks(board, board.whiteKing.square, 0) << 3;

    return board.turn == 1 ? score : -score;
  }


  public int getMaterial(Board board) {
    return 0;
  }


  public int getMaterialDifference(Board board) {
    return 0;
  }


  public int getKingSafety(Board board) {
    return 0;
  }

  public int getPawns(Board board) {
    return 0;
  }

  public int getPawnsDifference(Board board) {
    return 0;
  }


  public void reset() {
  }

  public int countAdjacentChecks(Board board, Square square, int color)
  {
    int count = 0;
    for(int i : MoveGenerationConstants.kingMoves)
    {
      Board.BoardSquare checkFrom = board.getSquare(square.index128 + i);
      if(checkFrom != null)
      {
        long attackVector = board.attacksDiaga1(checkFrom.square.index64) | board.attacksDiagh1(checkFrom.square.index64);
        count += (attackVector & (board.pieceBoards[color][Piece.QUEEN] | board.pieceBoards[color][Piece.BISHOP])) != 0 ? 3 : 0;
        long attackVector1 = board.attacksRank(checkFrom.square.index64) | board.attacksFile(checkFrom.square.index64);
        count += (attackVector1 & (board.pieceBoards[color][Piece.QUEEN] | board.pieceBoards[color][Piece.ROOK])) != 0 ? 3 : 0;
        count += (MoveGeneration.attackVectors[color][Piece.KNIGHT][checkFrom.square.index64] & board.pieceBoards[color][Piece.KNIGHT]) != 0 ? 2 : 0;
        count += (board.pieceBoards[color][Piece.PAWN] & MoveGeneration.attackVectors[color][Piece.PAWN][checkFrom.square.index64]) != 0 ? 1 : 0;
        count += (MoveGeneration.attackVectors[color][Piece.KING][checkFrom.square.index64] & board.pieceBoards[color][Piece.KING]) != 0 ? 1 : 0;
      }
      else
      {
        count += 4;
      }
    }
    return count;
  }

  public int getBlackKingSafety(Board board)
  {
    int whiteKnightCount = Board.countBits(board.pieceBoards[1][Piece.KNIGHT]);
    int whiteBishopCount = Board.countBits(board.pieceBoards[1][Piece.BISHOP]);
    int whiteRookCount = Board.countBits(board.pieceBoards[1][Piece.ROOK]);
    int whiteQueenCount = Board.countBits(board.pieceBoards[1][Piece.QUEEN]);
    int whiteMaterial = (whiteKnightCount * 3) + (whiteBishopCount * 3) + (whiteRookCount * 5) + (whiteQueenCount * 9);

    return countAdjacentChecks(board, board.blackKing.square, 1);
  }

  public int getWhiteKingSafety(Board board)
  {
    return countAdjacentChecks(board, board.whiteKing.square, 0);
  }

  @Override
  public int getLastBlackKingSafety() {
    return 0;
  }

  @Override
  public int getLastWhiteKingSafety() {
    return 0;
  }
}
