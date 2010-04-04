/* $Id$ */

package chess.engine.search;

import chess.engine.model.Move;

/**
 * @author Joshua Levine <jlevine@theladders.com>
 * @version $Revision$ $Name$ $Date$
 */
public class PositionHashtable
{
  public static final int UPPER_BOUND = 0;
  public static final int LOWER_BOUND = 1;
  public static final int EXACT_VALUE = 2;

  public static int HASH_SIZE = (int)1L<<18;
  public static int HASH_MASK = HASH_SIZE - 1;

  public static class HashEntry
  {

    public int depth;
    public long hash = 0;
    public int type = -1;
    public int score = 0;
    public Move move = new Move();
    public boolean mateThreat;

    public void reset()
    {
      depth = 0;
      hash = 0;
      type = -1;
      score = 0;
      mateThreat = false;
      move.moved = null;
    }
  }

  // index128 as [hash_index][fallback]
  private HashEntry[] DEPTH_FIRST_HASH = new HashEntry[HASH_SIZE];
  private HashEntry[] ALWAYS_STORE_HASH = new HashEntry[HASH_SIZE];

  public PositionHashtable()
  {
    for(int t = 0;t < HASH_SIZE;t++)
    {
      DEPTH_FIRST_HASH[t] = new HashEntry();
      ALWAYS_STORE_HASH[t] = new HashEntry();
    }

    clear();
  }

  public HashEntry getEntry(long boardHash)
  {
    int index = (int)(boardHash & HASH_MASK);

    PositionHashtable.HashEntry depthFirstEntry = DEPTH_FIRST_HASH[index];
    if(depthFirstEntry.hash == boardHash)
    {
      return depthFirstEntry;
    }

    PositionHashtable.HashEntry alwaysStoreEntry = ALWAYS_STORE_HASH[index];
    if(alwaysStoreEntry.hash == boardHash)
    {
      return alwaysStoreEntry;
    }


    return null;
  }

  public void putEntry(int depth, int type, int score, long boardHash, Move move, boolean mateThreat)
  {
    int index = (int)(boardHash & HASH_MASK);

    HashEntry depthFirstEntry = DEPTH_FIRST_HASH[index];

    if(depth > depthFirstEntry.depth || depthFirstEntry.hash == 0 || (type > depthFirstEntry.type && depthFirstEntry.hash == boardHash))
    {
      depthFirstEntry.hash = boardHash;
      depthFirstEntry.depth = depth;
      depthFirstEntry.score = score;
      depthFirstEntry.type = type;
      depthFirstEntry.mateThreat = mateThreat;
      depthFirstEntry.move.fromSquare = move.fromSquare;
      depthFirstEntry.move.toSquare = move.toSquare;
      depthFirstEntry.move.takenSquare = move.takenSquare;
      depthFirstEntry.move.moved = move.moved;
      depthFirstEntry.move.taken = move.taken;
      depthFirstEntry.move.promoteTo = move.promoteTo;
      depthFirstEntry.move.castledRook = move.castledRook;
      depthFirstEntry.move.castleFromSquare = move.castleFromSquare;
      depthFirstEntry.move.castleToSquare = move.castleToSquare;
      depthFirstEntry.move.enPassentSquare = move.enPassentSquare;
      depthFirstEntry.move.score = move.score;
      depthFirstEntry.move.check = move.check;
      return;
    }

    HashEntry alwaysStoreEntry = ALWAYS_STORE_HASH[index];
    alwaysStoreEntry.hash = boardHash;
    alwaysStoreEntry.depth = depth;
    alwaysStoreEntry.score = score;
    alwaysStoreEntry.type = type;
    alwaysStoreEntry.mateThreat = mateThreat;
    alwaysStoreEntry.move.fromSquare = move.fromSquare;
    alwaysStoreEntry.move.toSquare = move.toSquare;
    alwaysStoreEntry.move.takenSquare = move.takenSquare;
    alwaysStoreEntry.move.moved = move.moved;
    alwaysStoreEntry.move.taken = move.taken;
    alwaysStoreEntry.move.promoteTo = move.promoteTo;
    alwaysStoreEntry.move.castledRook = move.castledRook;
    alwaysStoreEntry.move.castleFromSquare = move.castleFromSquare;
    alwaysStoreEntry.move.castleToSquare = move.castleToSquare;
    alwaysStoreEntry.move.enPassentSquare = move.enPassentSquare;
    alwaysStoreEntry.move.score = move.score;
    alwaysStoreEntry.move.check = move.check;
  }

  public void clear()
  {
    for(int t = 0;t < HASH_SIZE;t++)
    {
      DEPTH_FIRST_HASH[t].reset();
      ALWAYS_STORE_HASH[t].reset();
    }
  }

  public void age()
  {
    for(int t = 0;t < HASH_SIZE;t++)
    {
      DEPTH_FIRST_HASH[t].depth = 0;
      ALWAYS_STORE_HASH[t].depth = 0;
      if(Math.abs(DEPTH_FIRST_HASH[t].score) > Searcher.MATE - 300)
      {
        DEPTH_FIRST_HASH[t].hash = 0;
      }
      if(Math.abs(ALWAYS_STORE_HASH[t].score) > Searcher.MATE - 300)
      {
        ALWAYS_STORE_HASH[t].hash = 0;
      }
    }
  }
}
