/* $Id$ */

package chess.engine.search;

/**
 * @author Joshua Levine <jlevine@theladders.com>
 * @version $Revision$ $Name$ $Date$
 */
public class SearchStats
{
  private static final String SPACES = "                                             ";

  public double time;
  public int nodes;
  public int zwNodes;
  public int qNodes;
  public int evals;
  public int lazyEvals;

  public int hashMisses;
  public int hardHashHits;
  public int qHashHits;
  public int softHashHits;

  public int prunes;
  public int reductions;

  public int endgameExtensions;
  public int checkExtensions;
  public int doubleCheckExtensions;
  public int promotionExtensions;
  public int recaptureExtensions;


  public String toString()
  {
    return " T: " + pad(time,8) + "  E/S: " + pad((int)((double)evals / time),8)  + "  N/S: " + pad((int)((double)nodes / time),8) +
           "\nSearch      |   N: " + pad(nodes,8) + "    E: " + pad(evals,8) + "  ZWN: " + pad(zwNodes,8) + "   QN: " + pad(qNodes,8) +
           "\nHash        | HHH: " + pad(hardHashHits,8) + "  SHH: " + pad(softHashHits, 8) + "  QHH: " + pad(qHashHits, 8) + "  Miss: " + pad(hashMisses,8) +
           "\nExtensions  |  EX: " + pad(endgameExtensions, 8) + "   PX: " + pad(promotionExtensions, 8) + "   CX: " + pad(checkExtensions, 8) + "   DCX: " + pad(doubleCheckExtensions, 8) + "   RX: " + pad(recaptureExtensions, 8) +
           "\nReductions  |  LE: " + pad(lazyEvals, 8) + "    R: " + pad(reductions, 8) + "    P: " + pad(prunes, 8);
  }

  private String pad(Number value, int length)
  {
    StringBuffer buffer = new StringBuffer("" + value);
    return buffer.insert(0,SPACES.substring(0,Math.max(1,length - buffer.length()))).toString();
  }


  public void reset()
  {
    time = 0;
    nodes = 0;
    evals = 0;
    lazyEvals = 0;

    hashMisses = 0;
    hardHashHits = 0;
    qHashHits = 0;
    softHashHits = 0;

    prunes = 0;
    reductions = 0;

    endgameExtensions = 0;
    checkExtensions = 0;
    doubleCheckExtensions = 0;
    promotionExtensions = 0;
    recaptureExtensions = 0;
  }
}

