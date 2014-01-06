/* $Id$ */

package chess.engine.model;

import java.util.Comparator;

/**
 * @author Joshua Levine <levinester@gmail.com>
 * @version $Revision$ $Name$ $Date$
 */
public class MoveComparator implements Comparator<Move>
{
  public int compare(Move o1, Move o2)
  {
    if(o1 == null && o2 == null) return 0;
    if(o1 == null) return 1;
    if(o2 == null) return -1;
    return o1.score > o2.score ? -1 : 1;
  }
}
