package chess.engine.search;

import chess.engine.model.Board;
import chess.engine.model.Piece;
import chess.engine.model.Square;
import chess.engine.model.Move;
import chess.engine.utils.MoveGeneration;

import java.util.List;

/**
 * PawnWingScorer
 *
 * @author jlevine
 */
public class PawnWingScorer
{
  private MoveGeneration moveGeneration;
  private SimpleEvaluator eval;
  private Board board = new Board();

  private Move[][] moveLists = new Move[100][100];

  private int ply;


  public PawnWingScorer(MoveGeneration moveGeneration, SimpleEvaluator eval) {
    this.moveGeneration = moveGeneration;
    this.eval = eval;
    for (int i = 0; i < 100; i++)
    {
      moveLists[i] = Move.createMoves(100);
    }
  }

  public int scorePawnWing(int whitePawns, int blackPawns)
  {
    int pieceIndex = 0;

    List<Square> whitePawnSquares = board.getAllSquaresInBitboard(whitePawns);
    List<Square> blackPawnSquares = board.getAllSquaresInBitboard(blackPawns);

    for(Square square : whitePawnSquares)
    {
      new Piece(pieceIndex++, board, 1, Piece.PAWN, Board.SQUARES[square.index64 + 8]);
    }
    for(Square square : blackPawnSquares)
    {
      new Piece(pieceIndex++, board, 0, Piece.PAWN, Board.SQUARES[square.index64 + 48]);
    }
    new Piece(pieceIndex++, board, 1, Piece.KING, Square.H1);
    new Piece(pieceIndex++, board, 0, Piece.KING, Square.H8);

    int score = searchPawnWing(-99999, 99999, 12) / 5;

    if(score > 0)
    {
      return Math.min(50,score);
    }
    else
    {
      return Math.max(-50, score);
    }
  }

  int searchPawnWing(int alpha, int beta, int depth)
  {
    int score;

    if(depth < 1)
    {
      return eval.scorePosition(board, alpha, beta);
    }

    Move[] moveList = moveLists[ply];
    int movesGenerated = moveGeneration.generatePawnMoves(moveList, board);

    if(movesGenerated == 0)
    {
      board.moveIndex++;
      board.turn ^= 1;
      ply++;
      score = -searchPawnWing(-beta, -alpha, depth - 1);
      ply--;
      board.moveIndex--;
      board.turn ^= 1;
    }

    for(int t = 0;t < movesGenerated;t++)
    {
      Move move = moveList[t];

      if(move.promoteTo != -1)
      {
        return 100;
      }

      board.make(move);
      ply++;
      score = -searchPawnWing(-beta, -alpha, depth - 1);
      ply--;
      board.unmake(move);

      if(score > alpha)
      {
        if(score >= beta)
        {
          return beta;
        }
        alpha = score;
      }
    }

    return alpha;
  }
}
