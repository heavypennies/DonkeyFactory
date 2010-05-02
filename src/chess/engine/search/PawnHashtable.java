package chess.engine.search;

import chess.engine.model.Board;

/**
 * Created by IntelliJ IDEA.
 * User: jlevine
 * Date: Jul 30, 2007
 * Time: 9:38:11 PM
 */
public class PawnHashtable {
  private static int HASH_SIZE = PositionHashtable.HASH_SIZE;
  private static int HASH_MASK = HASH_SIZE - 1;

  class HashEntry
  {

    public long hash = 0;
    public SimpleEvaluator.PawnFlags pawnFlags = new SimpleEvaluator.PawnFlags();
  }

  // index128 as [hash_index][fallback]
  public HashEntry[] hash = new HashEntry[HASH_SIZE];

  public PawnHashtable()
  {
    for(int t = 0;t < HASH_SIZE;t++)
    {
      hash[t] = new HashEntry();
    }
    clear();
  }

  public HashEntry getEntry(Board board)
  {
    int index = (int)(board.pawnHash & HASH_MASK);

    HashEntry entry = hash[index];
    if(entry.hash == 0)
    {
      return null;
    }

    if(entry.hash == board.pawnHash)
    {
      return entry;
    }
    return null;
  }

  public HashEntry getEntryNoNull(Board board)
  {
    int index = (int)(board.pawnHash & HASH_MASK);

    return hash[index];
  }

  public void clear()
  {
    for(int t = 0;t < HASH_SIZE;t++)
    {
      hash[t].hash = 0;
    }
  }
}
