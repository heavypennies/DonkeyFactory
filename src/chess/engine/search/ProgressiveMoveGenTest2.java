/* $Id$ */

package chess.engine.search;

import chess.engine.model.Board;
import chess.engine.model.Move;
import chess.engine.model.Piece;
import chess.engine.model.Square;
import chess.engine.utils.MoveGeneration;

import java.util.Date;

/**
 * @author Joshua Levine <levinester@gmail.com>
 * @version $Revision$ $Name$ $Date$
 */
public class ProgressiveMoveGenTest2 //extends TestCase
{
  public void testRammingAttacks()
  {

    MoveGeneration moveGen = new MoveGeneration();
    Board board = new Board();
    board.turn = 1;

    System.err.println(new Date());
    int pieceIndex = 0;
    Piece blackKing  = new Piece(pieceIndex++, board, 0, Piece.KING, Square.G8);
    Piece blackQueen  = new Piece(pieceIndex++, board, 0, Piece.QUEEN, Square.G7);
    Piece whiteKing  = new Piece(pieceIndex++, board, 1, Piece.KING, Square.A2);
    Piece whiteQueen  = new Piece(pieceIndex++, board, 1, Piece.QUEEN, Square.H4);
    Piece whiteRook2 = new Piece(pieceIndex++, board, 1, Piece.ROOK, Square.H2);
    Piece whiteRook = new Piece(pieceIndex++, board, 1, Piece.ROOK, Square.H1);
    Piece whiteKnight  = new Piece(pieceIndex++, board, 1, Piece.KNIGHT, Square.H5);

    System.err.println(board);

    System.err.println(board.visualizeAttackState(board.attackState[1][Square.H8.index64]));
    board.removePieceFromSquare(whiteKnight, Square.H5);
    System.err.println(board.visualizeAttackState(board.attackState[1][Square.H8.index64]));
    board.setPieceOnSquare(whiteKnight, Square.H3);
    System.err.println(board.visualizeAttackState(board.attackState[1][Square.H8.index64]));
    System.err.println(board.getAllSquaresInBitboard(board.squareAttackers[Square.H8.index64]));
    System.err.println(board.getAllSquaresInBitboard(whiteQueen.attacks));
    System.err.println(board.getAllSquaresInBitboard(whiteRook.attacks));
    System.err.println(board.getAllSquaresInBitboard(whiteRook2.attacks));
    System.err.println(board.getAllSquaresInBitboard(whiteQueen.rams));
    System.err.println(board.getAllSquaresInBitboard(whiteRook.rams));
    System.err.println(board.getAllSquaresInBitboard(whiteRook2.rams));
    board.removePieceFromSquare(whiteKnight, Square.H3);
    System.err.println(board.visualizeAttackState(board.attackState[1][Square.H8.index64]));
    board.removePieceFromSquare(whiteQueen, Square.H4);
    System.err.println(board.visualizeAttackState(board.attackState[1][Square.H8.index64]));
    board.setPieceOnSquare(whiteQueen, Square.H4);
    System.err.println(board.visualizeAttackState(board.attackState[1][Square.H8.index64]));
  }

  public static void main(String[] args)
  {
    new ProgressiveMoveGenTest2().testRammingAttacks();
  }
}