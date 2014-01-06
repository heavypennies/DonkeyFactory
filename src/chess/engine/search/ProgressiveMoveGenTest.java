/* $Id$ */

package chess.engine.search;

import chess.engine.model.Board;
import chess.engine.model.Move;
import chess.engine.model.Square;
import chess.engine.utils.MoveGeneration;

import java.util.Date;

/**
 * @author Joshua Levine <levinester@gmail.com>
 * @version $Revision$ $Name$ $Date$
 */
public class ProgressiveMoveGenTest //extends TestCase
{
  public void testRammingAttacks()
  {

    MoveGeneration moveGen = new MoveGeneration();
    Board board = new Board("6k1/6qp/8/8/8/7Q/K7/7R");
    board.stats.whiteCastleFlag = 1;
    board.stats.blackCastleFlag = 1;
    board.turn = 0;
    System.out.println(new Date());

    BoardEvaluator eval = new SimpleEvaluator(moveGen);

    Move[][] moves = new Move[100][];
    moves[0] = Move.createMoves(100);
    moves[1] = Move.createMoves(100);
    moves[2] = Move.createMoves(100);
    moves[3] = Move.createMoves(100);
    moveGen.generatePawnMoves(moves[0], board);

    System.err.println("Moves: " + Move.toString(moves[0]));
    System.err.println(board);

    System.err.println("");
    System.err.println(board.visualizeAttackState(board.attackState[1][Square.H6.index64]));

    System.err.println("Make: " + moves[0][0]);
    board.make(moves[0][0]);
    System.err.println("UnMk: " + moves[0][0]);
    board.unmake(moves[0][0]);

    System.err.println("");
    System.err.println(board.visualizeAttackState(board.attackState[1][Square.H6.index64]));

    System.err.println("Make: " + moves[0][0]);
    board.make(moves[0][0]);
    moveGen.generateRookMoves(0, moves[1], board, board.pieces[5]);

    System.err.println("Make: " + moves[1][0]);
    board.make(moves[1][0]);

    System.err.println("");
    System.err.println(board.visualizeAttackState(board.attackState[1][Square.H6.index64]));

    System.err.println("UnMk: " + moves[1][0]);
    board.unmake(moves[1][0]);

    moveGen.generateFullQueenMoves(0, moves[2], board, board.pieces[3]);

    System.err.println("Make: " + moves[2][7]);
    board.make(moves[2][7]);
    System.err.println("");
    System.err.println(board.visualizeAttackState(board.attackState[1][Square.H6.index64]));

    board.turn = 1;
    moveGen.generateFullQueenMoves(0, moves[3], board, board.pieces[3]);

    System.err.println("QAttacks: " + board.getAllSquaresInBitboard(board.pieces[3].attacks));
    System.err.println("Make: " + moves[3][2]);
    board.make(moves[3][2]);
    System.err.println("QAttacks: " + board.getAllSquaresInBitboard(board.pieces[3].attacks));
    System.err.println("");
    System.err.println(board.visualizeAttackState(board.attackState[1][Square.H6.index64]));


    System.err.println("UnMk: " + moves[3][2]);
    board.unmake(moves[3][2]);
    System.err.println("");
    System.err.println(board.visualizeAttackState(board.attackState[1][Square.H6.index64]));

    System.err.println("UnMk: " + moves[2][7]);
    board.unmake(moves[2][7]);
    System.err.println("");
    System.err.println(board.visualizeAttackState(board.attackState[1][Square.H6.index64]));
  }

  public static void main(String[] args)
  {
    new ProgressiveMoveGenTest().testRammingAttacks();
  }
}