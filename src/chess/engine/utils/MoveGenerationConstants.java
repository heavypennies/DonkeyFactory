/* $Id$ */

package chess.engine.utils;

/**
 * @author Joshua Levine <jlevine@theladders.com>
 * @version $Revision$ $Name$ $Date$
 */
public interface MoveGenerationConstants
{

  public static int[] knightMoves = { 31, 33, 18, 14, -18, -14, -31, -33 };
  public static int[] bishopMoves = { 15, 17, -15, -17 };
  public static int[] rookMoves = { 16, -1, 1, -16 };
  public static int[] queenMoves = { 15, 16, 17, 1, -17, -16, -15, -1 };
  public static int[] kingMoves = queenMoves;
}
