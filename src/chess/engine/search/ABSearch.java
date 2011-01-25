/* $Id$ */

package chess.engine.search;

import chess.engine.model.Board;
import chess.engine.model.Move;
import chess.engine.model.Piece;
import chess.engine.utils.LineScorer;
import chess.engine.utils.MoveGeneration;

/**
 * @author Joshua Levine <jlevine@theladders.com>
 * @version $Revision$ $Name$ $Date$
 */
public class ABSearch implements Searcher
{
  private static long MAX_EXTENSIONS = 12;
  private static boolean REDUCE = true;

  private static boolean debug = false;
  private BoardEvaluator eval;
  private MoveGeneration moveGeneration;
  private PositionHashtable abHashtable = new PositionHashtable();

  public int searchExtensions;

  public SearchStats stats;

  // indexed as [fromSquare.index64][toSquare.index64]
  public int[][] moveHistory = new int[64][64];

  private Move NULL_MOVE = new Move();
  private boolean running = false;
  private boolean inPawnEnding = false;
  private static final int[] MARGIN = {   50,   100,  200,  300,  500,  500,  900, 900,  900, 1500, 1500, 1500,
                                        2200, 2200, 2200, 2200, 2200,
                                        3000, 3000, 3000, 3000, 3000,
                                        3000, 3000, 3000, 3000, 3000,
                                        3000, 3000, 3000, 3000, 3000,
                                        3000, 3000, 3000, 3000, 3000,
                                        3000, 3000, 3000, 3000, 3000,
                                        3000, 3000, 3000, 3000, 3000,
                                        3000, 3000, 3000, 3000, 3000,
                                        3000, 3000, 3000, 3000, 3000,
                                        3000, 3000, 3000, 3000, 3000,
                                        3000, 3000, 3000, 3000, 3000
  };


  public void reset()
  {
    abHashtable.age();
    moveHistory = new int[64][64];
    eval.reset();
    done = false;
  }

  public SearchStats getStats()
  {
    return stats;
  }


  public void setStats(SearchStats stats)
  {
    this.stats = stats;
  }


  /* read write */
  public volatile boolean done = true;
  /* read only please */
  public Move[][] pv = new Move[100][100];
  /* read only please */
  private Move[] currentLine = Move.createMoves(100);

  private Move[] killer1 = Move.createMoves(100);
  private Move[] killer2 = Move.createMoves(100);
  private Move[] killer3 = Move.createMoves(100);
  private boolean[] inCheck = new boolean[100];
  private int checks = 0;

  public Move[] getPV()
  {
    return pv[0];
  }


  private Move[][] moveLists = new Move[100][100];
  private int[][] extensions = new int[64][64];


  private int ply;

  public ABSearch(MoveGeneration moveGeneration, BoardEvaluator eval)
  {
    this.moveGeneration = moveGeneration;
    this.eval = eval;

    for (int i = 0; i < 100; i++)
    {
      moveLists[i] = Move.createMoves(100);
      pv[i] = Move.createMoves(100);
    }
  }

  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////
  ////////////////// ROOT SEARCH /////////////////////////
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////

  public final int search(Board board, int depth)
  {
    done = false;
    searchExtensions = 0;
    running = true;
    inPawnEnding = board.stats.originalMaterial == 0;

    long start = System.currentTimeMillis();

    ply = 0;
    inCheck[ply] = board.isSquareCheckedByColor(board.turn == 1 ? board.whiteKing.square : board.blackKing.square, board.turn ^ 1);
    int score = rootSearch(-MATE, MATE, depth, board);

    long time = System.currentTimeMillis() - start;

    stats.time = (double) time / 1000;

    running = false;

    return score;
  }


  public final int rootSearch(int alpha, int beta, int depth, Board board)
  {
    if (done)
    {
      return -INFINITY;
    }

    if (depth < 1)
    {
      return quiescenceSearch(alpha, beta, board);
    }

    ++stats.nodes;

    boolean whiteToMove = board.turn == 1;

    // Hash Probe
    boolean mateThreat = false;
    PositionHashtable.HashEntry hashEntry = abHashtable.getEntry(whiteToMove ? board.hash1 : ~board.hash1);

    if(hashEntry != null)
    {
      mateThreat = hashEntry.mateThreat;
      ++stats.softHashHits;
    }
    else
    {
      ++stats.hashMisses;
      // IID
/*
      if (depth > 2)
      {
        int score = abSearch(alpha, beta, depth - 2, board, doNull);
        if(depth < 3 && alpha > score + MARGIN[depth])
        {
          ++stats.reductions;
          depth--;
        }
      }
*/
    }

    int score;

    boolean pvFound = false;
    Move[] moveList = moveLists[ply];
    int movesGenerated;
//    moveGeneration.setHashEntry(hashEntry);
    if (inCheck[ply])
    {
      movesGenerated = moveGeneration.generateEvasions(moveList, board);
    }
    else
    {
      movesGenerated = moveGeneration.generateMoves(moveList, board);
    }

    for (int i = 0; i < movesGenerated; ++i)
    {
      if (hashEntry != null && moveList[i].matches(hashEntry.move))
      {
        moveList[i].score = INFINITY;
      }
      else if(moveList[i].matches(killer1[ply]))
      {
        moveList[i].score = INFINITY - 1;
      }
      else if(moveList[i].matches(killer1[ply + 2]))
      {
        moveList[i].score = INFINITY - 3;
      }
      else if(moveList[i].matches(killer2[ply]))
      {
        moveList[i].score = INFINITY - 30000;
      }
      else if(moveList[i].matches(killer2[ply + 2]))
      {
        moveList[i].score = INFINITY - 30002;
      }
      else if(moveList[i].taken != null)
      {
        int swap = swap(board, moveList[i]);
        if(swap <= -100)
        {
          moveList[i].score += moveHistory[moveList[i].moved.type][moveList[i].toSquare.index64] - 40000;
        }
      }
      else if(moveList[i].promoteTo != -1)
      {
        if(swap(board, moveList[i]) <= -100)
        {
          moveList[i].score += moveHistory[moveList[i].moved.type][moveList[i].toSquare.index64] - 50000;
        }
      }
      else
      {
        moveList[i].score += Math.min(moveHistory[moveList[i].moved.type][moveList[i].toSquare.index64], 8000);
      }
    }

    ////////////////////////////////////////////////////////
    //////////////// LOOP THROUGH ALL MOVES ////////////////
    ////////////////////////////////////////////////////////
    int moveCount = 0;

    for (int moveIndex = 0; moveIndex < movesGenerated; ++moveIndex)
    {
      nextMove(moveList, moveIndex);

//      System.out.print("Root: " + move + "("+moveList[moveIndex].score+")");

      // make the move
      board.make(moveList[moveIndex]);

      // unmake if we are in check
      if (board.isSquareCheckedByColor(!whiteToMove ?
                                       board.blackKing.square :
                                       board.whiteKing.square, board.turn))
      {
        board.unmake(moveList[moveIndex]);
        continue;
      }

      currentLine[ply].reset(moveList[moveIndex]);
      currentLine[ply + 1].moved = null;

      // if no check, count this move, and then recurse
      ++moveCount;

      ++ply;
      pv[ply][ply].moved = null;

      int extend = 0;
      inCheck[ply] = board.isSquareCheckedByColor(board.turn == 1 ? board.whiteKing.square : board.blackKing.square, board.turn ^ 1);
      if (searchExtensions < MAX_EXTENSIONS && extensions[moveList[moveIndex].fromSquare.index64][moveList[moveIndex].toSquare.index64] == 0)
      {
        // Extend Check
        if (inCheck[ply])
        {
          ++extend;
          ++stats.checkExtensions;
        }
        // Extend mate threat from null-move
        else if(mateThreat)
        {
          ++extend;
        }
        // Extend promotion
        else if(moveList[moveIndex].promoteTo == Piece.QUEEN || (moveList[moveIndex].moved.type == Piece.PAWN && (moveList[moveIndex].toSquare.rank == 6 || moveList[moveIndex].toSquare.rank == 1)))
        {
          ++extend;
          ++stats.promotionExtensions;
        }
      }

      extensions[moveList[moveIndex].fromSquare.index64][moveList[moveIndex].toSquare.index64]+=extend;
      searchExtensions += extend;

      score = -abSearch(-beta, -alpha, (depth - 1) + extend, board, true);

//      System.out.println(" s:("+score+")");

      extensions[moveList[moveIndex].fromSquare.index64][moveList[moveIndex].toSquare.index64]-=extend;
      searchExtensions -= extend;
      extend = 0;

/*
      if(ply == 1)
      {
        System.err.println("S: " + score + " O: " + moveList[moveIndex].score + " Moves: " + move + " " + moveList[moveIndex].toString(pv[ply+1],1));
      }
*/


      ply--;
      currentLine[ply].check = moveList[moveIndex].check = inCheck[ply+1];

      // unmake move
      board.unmake(moveList[moveIndex]);

      if (score > alpha && !done)
      {
        if (moveList[moveIndex].taken == null && moveList[moveIndex].promoteTo == -1)
        {
          killer2[ply].reset(moveList[moveIndex]);
        }

        moveList[moveIndex].score = score;
        pv[ply][ply].reset(moveList[moveIndex]);

        int t = ply + 1;
        pv[ply][t].reset(pv[ply + 1][t]);
        while (pv[ply + 1][t++].moved != null)
        {
          pv[ply][t].reset(pv[ply + 1][t]);
        }

/*
        System.err.println(new StringBuilder("depth: ").append(depth).append(" -> pv: ").append(moveList[moveIndex].toString(pv[ply])).toString());
*/

        alpha = score;
        pvFound = true;
      }
      else
      {
        moveHistory[moveList[moveIndex].moved.type][moveList[moveIndex].toSquare.index64]--;
      }
    }

    ////////////////////////////////////////////////////////
    //////////////// TEST FOR MATE OR DRAW /////////////////
    ////////////////////////////////////////////////////////

    if (!done)
    {
      if (moveCount == 0)
      {
        pv[ply][ply].reset(NULL_MOVE);
        if (inCheck[ply])
        {
          alpha = -MATE + ply;
          abHashtable.putEntry(depth, PositionHashtable.EXACT_VALUE, alpha, whiteToMove ? board.hash1 : ~board.hash1, pv[ply][ply], mateThreat);
        }
        else
        {
          alpha = 0;
        }
      }
      else
      {
        abHashtable.putEntry(depth, !pvFound ? PositionHashtable.UPPER_BOUND : PositionHashtable.EXACT_VALUE, alpha, whiteToMove ? board.hash1 : ~board.hash1, pv[ply][ply], mateThreat);
      }
      pv[ply][ply].score = alpha;
    }

    return alpha;
  }


  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////
  /////////////////// AB SEARCH //////////////////////////
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////

  public final int abSearch(int alpha, int beta, int depth, Board board, boolean doNull)
  {
    if (done)
    {
      return -INFINITY;
    }

    if (depth < 1 && !inCheck[ply])
    {
      return quiescenceSearch(alpha, beta, board);
    }

    ++stats.nodes;

    boolean whiteToMove = board.turn == 1;
    if (ply > 0 && board.isApproachingDraw())
    {
      pv[ply][ply].moved = null;
      return 0;
    }


    // make quicker mates better
    int mateDistance = MATE - ply;
    if (beta > mateDistance)
    {
      beta = mateDistance;
      if (alpha >= mateDistance)
      {
        pv[ply][ply].moved = null;
        return mateDistance;
      }
    }
    mateDistance = -MATE + ply;
    if(alpha < mateDistance) {
       alpha = mateDistance;
       if(mateDistance >= beta)
       {
         pv[ply][ply].moved = null;
         return mateDistance;
       }
    }

    // Hash Probe
    boolean mateThreat = false;
    PositionHashtable.HashEntry hashEntry = abHashtable.getEntry(whiteToMove ? board.hash1 : ~board.hash1);

    if(hashEntry != null)
    {
      if(hashEntry.depth >= depth)
      {
        switch (hashEntry.type)
        {
          case PositionHashtable.LOWER_BOUND:
          {
            if (hashEntry.score >= beta)
            {
              ++stats.hardHashHits;
              return hashEntry.score;
            }
            break;
          }
          case PositionHashtable.UPPER_BOUND:
          {
            if (hashEntry.score <= alpha)
            {
              ++stats.hardHashHits;
              return hashEntry.score;
            }
            break;
          }
          case PositionHashtable.EXACT_VALUE:
          {
            pv[ply][ply].reset(hashEntry.move);
            pv[ply][ply+1].moved = null;
            ++stats.hardHashHits;
            return hashEntry.score;
          }
        }
      }
      mateThreat = hashEntry.mateThreat;
      ++stats.softHashHits;
    }
    else
    {
      ++stats.hashMisses;
    }

    int score;

    // Pawn Ending Extension
    if((board.allPieces ^ (board.whiteKing.square.mask_on | board.blackKing.square.mask_on | board.pieceBoards[0][Piece.PAWN] | board.pieceBoards[1][Piece.PAWN])) == 0 && !inPawnEnding  &&
       board.stats.originalMaterial > 0)
    {
      inPawnEnding = true;
      searchExtensions += 3;
      ++stats.endgameExtensions;
      score = abSearch(alpha, beta, depth + 3, board, false);
      abHashtable.putEntry(depth + 3, pv[ply][ply].moved == null ? PositionHashtable.UPPER_BOUND : PositionHashtable.EXACT_VALUE, score, whiteToMove ? board.hash1 : ~board.hash1, pv[ply][ply], false);
      searchExtensions -= 3;
      inPawnEnding = false;
      return score;
    }

    // Null Move
    if (doNull && ply > 0 && !inCheck[ply] && !mateThreat && board.stats.originalMaterial > 5 && depth > 1)
    {
//      long oldHashKey = whiteToMove ? board.hash1 : ~board.hash1;
      int nullMoveReducation = depth > 6 && board.stats.originalMaterial > 8 ?
                               3 :
                               2;
      ++board.moveIndex;
      board.turn ^= 1;
      board.repetitionTable[board.moveIndex] = 1;
      ++ply;
      inCheck[ply] = false;
      score = -zwSearch(1-beta, (depth - nullMoveReducation) - 1, board, false, true);
      ply--;
      board.turn ^= 1;
      board.repetitionTable[board.moveIndex] = whiteToMove ? board.hash1 : ~board.hash1;
      board.moveIndex--;

      if (score < -MATE + 300)
      {
        mateThreat = true;
        //depth++;
      }
      if (score >= beta)
      {
        abHashtable.putEntry(depth, PositionHashtable.LOWER_BOUND, score, whiteToMove ? board.hash1 : ~board.hash1, NULL_MOVE, mateThreat);
        pv[ply][ply].moved = null;
        return score;
      }
    }

/*
    if(hashEntry == null || (hashEntry != null && hashEntry.move == NULL_MOVE))
    {
      // IID
      if (depth > 2)
      {
        abSearch(alpha, beta, depth - 2, board, false);
      }
      hashEntry = abHashtable.getEntry(whiteToMove ? board.hash1 : ~board.hash1);
    }
*/
    boolean pvFound = false;
    final Move[] moveList = moveLists[ply];
    final int movesGenerated;
//    moveGeneration.setHashEntry(hashEntry);
    if (inCheck[ply])
    {
      movesGenerated = moveGeneration.generateEvasions(moveList, board);
    }
    else
    {
      movesGenerated = moveGeneration.generateMoves(moveList, board);
    }


    for (int i = 0; i < movesGenerated; ++i)
    {
      if (hashEntry != null && moveList[i].matches(hashEntry.move))
      {
        moveList[i].score = INFINITY;
      }
      else if(moveList[i].matches(killer1[ply]))
      {
        moveList[i].score = INFINITY - 1;
      }
      else if(ply > 1 && moveList[i].matches(killer1[ply - 2]))
      {
        moveList[i].score = INFINITY - 2;
      }
      else if(moveList[i].matches(killer2[ply]))
      {
        moveList[i].score = INFINITY - 30000;
      }
      else if(ply > 1 && moveList[i].matches(killer2[ply - 2]))
      {
        moveList[i].score = INFINITY - 30001;
      }
      else if(moveList[i].taken != null)
      {
        if(moveList[i].score - 40000 <= 0 && swap(board, moveList[i]) <= -100)
        {
          moveList[i].score -= 40000;
        }
      }
      else if(moveList[i].promoteTo != -1)
      {
        if(swap(board, moveList[i]) <= -100)
        {
          moveList[i].score -= 50000;
        }
      }
      else
      {
        moveList[i].score += Math.min(moveHistory[moveList[i].moved.type][moveList[i].toSquare.index64], 8000);
      }
    }

    ////////////////////////////////////////////////////////
    //////////////// LOOP THROUGH ALL MOVES ////////////////
    ////////////////////////////////////////////////////////
    int moveCount = 0;

    for (int moveIndex = 0; moveIndex < movesGenerated; ++moveIndex)
    {
      nextMove(moveList, moveIndex);

      // make the move
      board.make(moveList[moveIndex]);

      // unmake if we are in check
      if (board.isSquareCheckedByColor(!whiteToMove ?
                                       board.blackKing.square :
                                       board.whiteKing.square, board.turn))
      {
        board.unmake(moveList[moveIndex]);
        continue;
      }

      currentLine[ply].reset(moveList[moveIndex]);
      currentLine[ply + 1].moved = null;

      // if no check, count this move, and then recurse
      ++moveCount;

      ++ply;
      pv[ply][ply].moved = null;

      /*if(doNull &&
         moveList[moveIndex].score < 0 &&
        (hashEntry != null && (hashEntry.score < alpha - 100)) &&
        ply > 1 &&
        !inCheck[ply-1])
      {
        ++stats.reductions;
        score = -abSearch(-alpha - 1, -alpha, (depth - 2) + extend, board, false);
      }
      else */

      int extend = 0;
      inCheck[ply] = board.isSquareCheckedByColor(board.turn == 1 ? board.whiteKing.square : board.blackKing.square, board.turn ^ 1);
      if (searchExtensions < MAX_EXTENSIONS && extensions[moveList[moveIndex].fromSquare.index64][moveList[moveIndex].toSquare.index64] == 0)
      {
        // Extend Check
        if (inCheck[ply])
        {
          ++extend;
          ++stats.checkExtensions;

/*
          if(ply > 6 && inCheck[ply - 4])
          {
            if(inCheck[ply - 2] && inCheck[ply - 6])
            {
              ++extend;
              ++stats.doubleCheckExtensions;
            }
          }
*/
        }
        // Extend recapture
        else if(ply > 1 && moveList[moveIndex].taken != null &&
                           currentLine[ply - 2].taken != null &&
                           (moveList[moveIndex].toSquare == currentLine[ply - 2].takenSquare) &&
                           moveList[moveIndex].taken.materialValue == -currentLine[ply - 2].taken.materialValue)

        {
          ++extend;
          ++stats.recaptureExtensions;
        }
        // Extend mate threat from null-move
        else if(mateThreat)
        {
          ++extend;
        }
        // Extend promotion
        else if(moveList[moveIndex].promoteTo == Piece.QUEEN  || (moveList[moveIndex].moved.type == Piece.PAWN && (moveList[moveIndex].toSquare.rank == 6 || moveList[moveIndex].toSquare.rank == 1)))
        {
          ++extend;
          ++stats.promotionExtensions;
        }
      }

      if(canBeReducedOrPruned(depth, alpha, mateDistance, moveList[moveIndex], moveCount, extend))
      {
        int scoreEstimate = MARGIN[depth] + (whiteToMove ? (board.materialScore + board.positionScore) : -(board.materialScore + board.positionScore));
        if(alpha > scoreEstimate)
        {

          if(depth < 3)
          {
            ply--;
            ++stats.prunes;
            board.unmake(moveList[moveIndex]);
            continue;
          }
//        String line = moveList[moveIndex].toString(currentLine);
//        System.err.println("Prune " + move + " [" + -abSearch(-MATE, MATE, (depth - 1) + extend, board, doNull) + "] : " + line);
          ++stats.reductions;
          searchExtensions--;
          extend--;
        }
      }

      extensions[moveList[moveIndex].fromSquare.index64][moveList[moveIndex].toSquare.index64]+=extend;
      searchExtensions += extend;

      if( !pvFound)
      {
        score = -abSearch(-beta, -alpha, (depth - 1) + extend, board, doNull);
      }
      else
      {

        score = -zwSearch(-alpha, (depth - 1) + extend, board, doNull, true);
        if (score > alpha && score < beta)
        {
          score = -abSearch(-beta, -alpha, (depth - 1) + extend, board, doNull);
        }
      }
      extensions[moveList[moveIndex].fromSquare.index64][moveList[moveIndex].toSquare.index64]-=extend;
      searchExtensions -= extend;
      extend = 0;

/*
      if(ply == 1)
      {
        System.err.println("S: " + score + " O: " + moveList[moveIndex].score + " Moves: " + move + " " + moveList[moveIndex].toString(pv[ply+1],1));
      }
*/


      ply--;
      currentLine[ply].check = moveList[moveIndex].check = inCheck[ply+1];

      // unmake move
      board.unmake(moveList[moveIndex]);

      if (score > alpha && !done)
      {
        ++moveHistory[moveList[moveIndex].moved.type][moveList[moveIndex].toSquare.index64];
        if (score >= beta)
        {
          abHashtable.putEntry(depth, PositionHashtable.LOWER_BOUND, score, whiteToMove ? board.hash1 : ~board.hash1, moveList[moveIndex], mateThreat);

          if (score > MATE - 300)
          {
            killer1[ply].reset(moveList[moveIndex]);
          }
          else if (moveList[moveIndex].taken == null && moveList[moveIndex].promoteTo == -1)
          {
            killer2[ply].reset(moveList[moveIndex]);
          }
          return score;
        }

//        moveList[moveIndex].score = score;
        pv[ply][ply].reset(moveList[moveIndex]);

        int t = ply + 1;
        pv[ply][t].reset(pv[ply + 1][t]);
        while (pv[ply + 1][t++].moved != null)
        {
          pv[ply][t].reset(pv[ply + 1][t]);
        }

        alpha = score;
        pvFound = true;
      }
      else
      {
        moveHistory[moveList[moveIndex].moved.type][moveList[moveIndex].toSquare.index64]--;
      }
    }

    ////////////////////////////////////////////////////////
    //////////////// TEST FOR MATE OR DRAW /////////////////
    ////////////////////////////////////////////////////////

    if (!done)
    {
      if (moveCount == 0)
      {
        if (inCheck[ply])
        {
          alpha = -MATE + ply;
        }
        else
        {
          alpha = 0;
        }
        pv[ply][ply].reset(NULL_MOVE);
        abHashtable.putEntry(depth, PositionHashtable.EXACT_VALUE, alpha, whiteToMove ? board.hash1 : ~board.hash1, pv[ply][ply], mateThreat);
      }
      else
      {
        abHashtable.putEntry(depth, !pvFound ? PositionHashtable.UPPER_BOUND : PositionHashtable.EXACT_VALUE, alpha, whiteToMove ? board.hash1 : ~board.hash1, pv[ply][ply], mateThreat);
      }
      pv[ply][ply].score = alpha;
    }

    return alpha;
  }

  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////
  /////////////////// ZW SEARCH //////////////////////////
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////

  public final int zwSearch(int beta, int depth, Board board, boolean doNull, boolean cut)
  {
    int alpha = beta-1;
    if (done)
    {
      return -INFINITY;
    }

    if (depth < 1 && !inCheck[ply])
    {
      return quiescenceSearch(alpha, beta, board);
    }


    ++stats.nodes;
    ++stats.zwNodes;

    final boolean whiteToMove = board.turn == 1;
    if (ply > 0 && board.isApproachingDraw())
    {
      pv[ply][ply].moved = null;
      return 0;
    }


    // make quicker mates better
    int mateDistance = MATE - ply;
    if (beta > mateDistance)
    {
      beta = mateDistance;
      if (alpha >= mateDistance)
      {
        pv[ply][ply].moved = null;
        return mateDistance;
      }
    }
    mateDistance = -MATE + ply;
    if(alpha < mateDistance) {
       alpha = mateDistance;
       if(mateDistance >= beta)
       {
         pv[ply][ply].moved = null;
         return mateDistance;
       }
    }

    // Hash Probe
    boolean mateThreat = false;
    PositionHashtable.HashEntry hashEntry = abHashtable.getEntry(whiteToMove ? board.hash1 : ~board.hash1);

    if(hashEntry != null)
    {
      if(hashEntry.depth >= depth)
      {
        switch (hashEntry.type)
        {
          case PositionHashtable.LOWER_BOUND:
          {
            if (hashEntry.score >= beta)
            {
              ++stats.hardHashHits;
              return hashEntry.score;
            }
            break;
          }
          case PositionHashtable.UPPER_BOUND:
          {
            if (hashEntry.score <= alpha)
            {
              ++stats.hardHashHits;
              return hashEntry.score;
            }
            break;
          }
          case PositionHashtable.EXACT_VALUE:
          {
            ++stats.hardHashHits;
            return hashEntry.score;
          }
        }
      }
      mateThreat = hashEntry.mateThreat;
      ++stats.softHashHits;
    }
    else
    {
      ++stats.hashMisses;
    }

    int score;

    // Pawn Ending Extension
    if((board.allPieces ^ (board.whiteKing.square.mask_on | board.blackKing.square.mask_on | board.pieceBoards[0][Piece.PAWN] | board.pieceBoards[1][Piece.PAWN])) == 0 && !inPawnEnding  &&
       board.stats.originalMaterial > 0)
    {
      inPawnEnding = true;
      searchExtensions += 3;
      ++stats.endgameExtensions;
      score = zwSearch(-alpha, depth + 3, board, false, cut);
      abHashtable.putEntry(depth + 3, PositionHashtable.LOWER_BOUND, score, whiteToMove ? board.hash1 : ~board.hash1, pv[ply][ply], false);
      searchExtensions -= 3;
      inPawnEnding = false;
      return score;
    }

    // Null Move
    if (doNull && ply > 0 && !inCheck[ply] && !mateThreat && board.stats.originalMaterial > 5 && depth > 1)
    {
      int nullMoveReducation = depth > 6 && board.stats.originalMaterial > 8 ?
                               3 :
                               2;
//      long oldHashKey = whiteToMove ? board.hash1 : ~board.hash1;
      ++board.moveIndex;
      board.turn ^= 1;
      board.repetitionTable[board.moveIndex] = 1;
      ++ply;
      inCheck[ply] = false;
      score = -zwSearch(1-beta, depth - nullMoveReducation - 1, board, false, true);
      ply--;
      board.turn ^= 1;
      board.repetitionTable[board.moveIndex] = whiteToMove ? board.hash1 : ~board.hash1;
      board.moveIndex--;

      if (score < -MATE + 300)
      {
        mateThreat = true;
        //++depth;
      }
      if (score >= beta)
      {
        abHashtable.putEntry(depth, PositionHashtable.LOWER_BOUND, score, whiteToMove ? board.hash1 : ~board.hash1, NULL_MOVE, mateThreat);
        pv[ply][ply].moved = null;
        return score;
      }
    }

    final Move[] moveList = moveLists[ply];
    final int movesGenerated;
//    moveGeneration.setHashEntry(hashEntry);
    if (inCheck[ply])
    {
      movesGenerated = moveGeneration.generateEvasions(moveList, board);
    }
    else
    {
      movesGenerated = moveGeneration.generateMoves(moveList, board);
    }

    if(cut && (hashEntry == null || hashEntry.move == NULL_MOVE) && depth > 2)
    {
      zwSearch(-alpha, depth - 2, board, false, false);
      hashEntry = abHashtable.getEntry(whiteToMove ? board.hash1 : ~board.hash1);
    }

    for (int i = 0; i < movesGenerated; ++i)
    {
      if (hashEntry != null && moveList[i].matches(hashEntry.move))
      {
        moveList[i].score = INFINITY;
      }
      else if(moveList[i].matches(killer1[ply]))
      {
        moveList[i].score = INFINITY - 1;
      }
      else if(ply > 1 && moveList[i].matches(killer1[ply - 2]))
      {
        moveList[i].score = INFINITY - 2;
      }
      else if(moveList[i].matches(killer2[ply]))
      {
        moveList[i].score = INFINITY - 40000;
      }
      else if(ply > 1 && moveList[i].matches(killer2[ply - 2]))
      {
        moveList[i].score = INFINITY - 40001;
      }
      else if(moveList[i].taken != null)
      {
        if(moveList[i].score - 40000 <= 0 && swap(board, moveList[i]) <= -100)
        {
          moveList[i].score -= 40000;
        }
      }
      else if(moveList[i].promoteTo != -1)
      {
        if(swap(board, moveList[i]) <= -100)
        {
          moveList[i].score -= 50000;
        }
      }
      else
      {
        moveList[i].score += Math.min(moveHistory[moveList[i].moved.type][moveList[i].toSquare.index64], 8000);
      }
    }

    ////////////////////////////////////////////////////////
    //////////////// LOOP THROUGH ALL MOVES ////////////////
    ////////////////////////////////////////////////////////
    int moveCount = 0;

    for (int moveIndex = 0; moveIndex < movesGenerated; ++moveIndex)
    {
      nextMove(moveList, moveIndex);

      // make the move
      board.make(moveList[moveIndex]);

      // unmake if we are in check
      if (board.isSquareCheckedByColor(!whiteToMove ?
                                       board.blackKing.square :
                                       board.whiteKing.square, board.turn))
      {
        board.unmake(moveList[moveIndex]);
        continue;
      }

      currentLine[ply].reset(moveList[moveIndex]);
      currentLine[ply + 1].moved = null;

      // if no check, count this move, and then recurse
      ++moveCount;

      ++ply;
      pv[ply][ply].moved = null;
      int extend = 0;
      inCheck[ply] = board.isSquareCheckedByColor(board.turn == 1 ? board.whiteKing.square : board.blackKing.square, board.turn ^ 1);
      if (searchExtensions < MAX_EXTENSIONS && extensions[moveList[moveIndex].fromSquare.index64][moveList[moveIndex].toSquare.index64] == 0)
      {
        // Extend Check
        if (inCheck[ply])
        {
          ++extend;
          ++stats.checkExtensions;
        }
        // Extend recapture
        else if(ply > 1 && moveList[moveIndex].taken != null &&
                           currentLine[ply - 2].taken != null &&
                           (moveList[moveIndex].toSquare == currentLine[ply - 2].takenSquare) &&
                           moveList[moveIndex].taken.materialValue >= -currentLine[ply - 2].taken.materialValue)

        {
          ++extend;
          ++stats.recaptureExtensions;
        }
        // Extend mate threat from null-move
        else if(mateThreat)
        {
          ++extend;
        }
        // Extend promotion
        else if(moveList[moveIndex].promoteTo == Piece.QUEEN || (moveList[moveIndex].moved.type == Piece.PAWN &&
                (moveList[moveIndex].toSquare.rank == 6 || moveList[moveIndex].toSquare.rank == 1)))
        {
          ++extend;
          ++stats.promotionExtensions;
        }
      }

      if(canBeReducedOrPruned(depth, alpha, mateDistance, moveList[moveIndex], moveCount, extend))
      {
        int scoreEstimate = whiteToMove ? (board.materialScore + board.positionScore) : -(board.materialScore + board.positionScore);
        if(alpha > scoreEstimate + MARGIN[depth])
        {

          if(depth < 3)
          {
            ply--;
            ++stats.prunes;
            board.unmake(moveList[moveIndex]);
            continue;
          }
//        String line = Move.toString(currentLine);
//        System.err.println("Prune " + move + " [" + -abSearch(-MATE, MATE, (depth - 1) + extend, board, doNull) + "] : " + line);
          ++stats.reductions;
          extend--;
        }
      }

      extensions[moveList[moveIndex].fromSquare.index64][moveList[moveIndex].toSquare.index64]+=extend;
      searchExtensions += extend;

      score = -zwSearch(-alpha, (depth - 1) + extend, board, doNull, !cut);
      extensions[moveList[moveIndex].fromSquare.index64][moveList[moveIndex].toSquare.index64]-=extend;
      searchExtensions -= extend;
      extend = 0;

/*
      if(ply == 1)
      {
        System.err.println("S: " + score + " O: " + move.score + " Moves: " + move + " " + Move.toString(pv[ply+1],1));
      }
*/


      ply--;
      currentLine[ply].check = moveList[moveIndex].check = inCheck[ply+1];

      // unmake move
      board.unmake(moveList[moveIndex]);

      if (score >= beta)
      {
        abHashtable.putEntry(depth, PositionHashtable.LOWER_BOUND, score, whiteToMove ? board.hash1 : ~board.hash1, moveList[moveIndex], mateThreat);

        if (score > MATE - 300)
        {
          killer1[ply].reset(moveList[moveIndex]);
        }
        else if (moveList[moveIndex].taken == null && moveList[moveIndex].promoteTo == -1)
        {
          killer2[ply].reset(moveList[moveIndex]);
        }
        ++moveHistory[moveList[moveIndex].moved.type][moveList[moveIndex].toSquare.index64];
        return score;
      }
/*
      else
      {
        moveHistory[move.moved.type][move.toSquare.index64]--;
      }
*/

/*
      if (move.taken == null && move.promoteTo == -1)
      {
        killer2[ply].reset(move);
      }
*/
    }

    ////////////////////////////////////////////////////////
    //////////////// TEST FOR MATE OR DRAW /////////////////
    ////////////////////////////////////////////////////////

    if (!done)
    {
      if (moveCount == 0)
      {
        if (inCheck[ply])
        {
          alpha = -MATE + ply;
        }
        else
        {
          alpha = 0;
        }
        pv[ply][ply].reset(NULL_MOVE);
        abHashtable.putEntry(depth, PositionHashtable.EXACT_VALUE, alpha, whiteToMove ? board.hash1 : ~board.hash1, pv[ply][ply], mateThreat);
      }
      else
      {
        pv[ply][ply].moved = null;
        abHashtable.putEntry(depth, PositionHashtable.UPPER_BOUND, alpha, whiteToMove ? board.hash1 : ~board.hash1, NULL_MOVE, mateThreat);
      }
    }

    return alpha;
  }

  private boolean canBeReducedOrPruned(int depth, int alpha, int mateDistance, Move move, int moveCount, int extend)
  {
    return /*REDUCE &&*/
       extend == 0 &&
       !inCheck[ply] &&
       !inCheck[ply-1] &&
       move.taken == null &&
       (move.moved.type != Piece.QUEEN) &&
       depth < 4 &&
       alpha > mateDistance &&
       moveCount > 2 * depth &&
       moveHistory[move.moved.type][move.toSquare.index64] <= 0;
  }

  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////
  /////////////////// Q SEARCH ///////////////////////////
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////


  public final int quiescenceSearch(int alpha, int beta, Board board)
  {
    pv[ply][ply].moved = null;
    if (done)
    {
      return -INFINITY;
    }
    ++stats.nodes;
    ++stats.qNodes;

    int score = alpha;

    boolean whiteToMove = board.turn == 1;
    if (ply > 0 && board.isApproachingDraw())
    {
      pv[ply][ply].moved = null;
      return 0;
    }

    // make quicker mates better
    int mateDistance = MATE - ply;
    if (beta > mateDistance)
    {
      beta = mateDistance;
      if (alpha >= mateDistance)
      {
        pv[ply][ply].moved = null;
        return mateDistance;
      }
    }
    mateDistance = -MATE + ply;
    if(alpha < mateDistance)
    {
       alpha = mateDistance;
       if(mateDistance >= beta)
       {
         pv[ply][ply].moved = null;
         return mateDistance;
       }
    }

    PositionHashtable.HashEntry hashEntry = abHashtable.getEntry(whiteToMove ? board.hash1 : ~board.hash1);
    if (hashEntry != null)
    {
      ++stats.qHashHits;
      switch (hashEntry.type)
      {
        case PositionHashtable.LOWER_BOUND:
        {
          if (hashEntry.score >= beta)
          {
            pv[ply][ply].reset(hashEntry.move);
            pv[ply][ply+1].moved = null;
            return hashEntry.score;
          }
          break;
        }
        case PositionHashtable.UPPER_BOUND:
        {
          if (hashEntry.score <= alpha)
          {
            pv[ply][ply].reset(hashEntry.move);
            pv[ply][ply+1].moved = null;
            return hashEntry.score;
          }
          break;
        }
        case PositionHashtable.EXACT_VALUE:
        {
          pv[ply][ply].reset(hashEntry.move);
          pv[ply][ply+1].moved = null;
          return hashEntry.score;
        }
      }
    }
    else
    {
      ++stats.hashMisses;
    }

    // Pawn ending extension
    Move[] moveList = moveLists[ply];
    int movesGenerated;
    boolean foundScore = false;

    if(!inCheck[ply])
    {
/*
      int scoreEstimate = (board.materialScore + board.positionScore) * (whiteToMove ? 1 : -1);
      if(alpha > scoreEstimate + MARGIN[4])
      {
        ++stats.lazyEvals;
        score = scoreEstimate;
      }
      else
      {
        ++stats.evals;
        score = eval.scorePosition(board, alpha, beta);
      }
*/

/*
      if(Math.abs(score - scoreEstimate) > 100)
      {
        System.out.println("LE score vs. estimate [" + score + ", " + scoreEstimate + "]: +/-(* " + Math.abs(score - scoreEstimate) + ")\n" + board);
      }
*/

      ++stats.evals;
      score = eval.scorePosition(board, alpha, beta);


      if (score > alpha)
      {
        if (score >= beta)
        {
//        if(debug) System.err.println("QSearch Initial Cut(" + (whiteToMove ? score : -score)+ "): " + Move.toString(currentLine));
          abHashtable.putEntry(-100, PositionHashtable.LOWER_BOUND, score, whiteToMove ? board.hash1 : ~board.hash1, NULL_MOVE, false);
          return score;
        }
        alpha = score;
        foundScore = true;
      }

      movesGenerated = moveGeneration.generateCaptures(moveList, board);
    }
    else
    {
      movesGenerated = moveGeneration.generateEvasions(moveList, board);
      alpha = -MATE + ply;
      foundScore = true;
    }

    int moveCount = 0;

    for (int moveIndex = 0; moveIndex < movesGenerated; ++moveIndex)
    {
      nextMove(moveList, moveIndex);
      Move move = moveList[moveIndex];

      if(!inCheck[ply] && move.taken != null && score + swap(board, move) + 50 <= alpha)
      {
        continue;
      }

      // make the move
      board.make(move);
      // unmake if we are in check
      if (board.isSquareCheckedByColor(!whiteToMove ?
                                       board.blackKing.square :
                                       board.whiteKing.square, board.turn))
      {
        board.unmake(move);
        continue;
      }


      inCheck[ply+1] = board.isSquareCheckedByColor(board.turn == 1 ? board.whiteKing.square : board.blackKing.square, board.turn ^ 1);
      currentLine[ply].reset(move);
      currentLine[ply + 1].moved = null;

      ++moveCount;

      ++ply;
      score = -quiescenceSearch(-beta, -alpha, board);
      ply--;

      // unmake move
      board.unmake(move);
      currentLine[ply].check = move.check = inCheck[ply+1];

      if (score > alpha)
      {
        if (score >= beta)
        {
//          if(debug) System.err.println("QSearch BCut(" + (whiteToMove ? score : -score)+ "): " + Move.toString(currentLine));
          abHashtable.putEntry(-100, PositionHashtable.LOWER_BOUND, score, whiteToMove ? board.hash1 : ~board.hash1, pv[ply][ply], false);
          pv[ply][ply].moved = null;
          return score;
        }
        pv[ply][ply].reset(move);
        int t = ply + 1;
        pv[ply][t].reset(pv[ply + 1][t]);
        while (pv[ply + 1][t++].moved != null)
        {
          pv[ply][t].reset(pv[ply + 1][t]);
        }

//        if(debug) System.err.println("QSearch PV(" + (whiteToMove ? score : -score)+ "): " + Move.toString(currentLine));

        foundScore = true;
        alpha = score;
      }
    }

    if(moveCount == 0 && inCheck[ply])
    {
      alpha = -MATE + ply;
      foundScore = true;
    }

//    if(debug) System.out.println("QSearch Return(" + (whiteToMove ? alpha : -alpha)+ "): " + Move.toString(currentLine));
    abHashtable.putEntry(-100, !foundScore ? PositionHashtable.UPPER_BOUND : PositionHashtable.EXACT_VALUE, alpha, whiteToMove ? board.hash1 : ~board.hash1, pv[ply][ply], false);

    return alpha;
  }


  int[] swapScores = new int[32];

  private int swap(Board board, Move move)
  {
    int swapIndex = 1;

    long attackers = moveGeneration.getAttackersNoXRay(board, move.toSquare, board.turn ^ 1) |
                     moveGeneration.getAttackersNoXRay(board, move.toSquare, board.turn);

    int color = board.turn ^ 1;
    swapScores[0] = move.taken != null ? Piece.TYPE_VALUES[move.taken.type] : 0;
    int attackedPiece = Piece.TYPE_VALUES[move.moved.type];

    attackers &= move.fromSquare.mask_off;


    while ((attackers & board.pieceBoards[color][Board.ALL_PIECES]) != 0)
    {
      swapScores[swapIndex] = -swapScores[swapIndex - 1] + attackedPiece;
      ++swapIndex;

      if ((board.pieceBoards[color][Piece.PAWN] & attackers) != 0)
      {
        attackedPiece = Piece.TYPE_VALUES[Piece.PAWN];
        attackers &= Board.SQUARES[Board.getLeastSignificantBit(board.pieceBoards[color][Piece.PAWN] & attackers)].mask_off;
      }
      else if ((board.pieceBoards[color][Piece.KNIGHT] & attackers) != 0)
      {
        attackedPiece = Piece.TYPE_VALUES[Piece.KNIGHT];
        attackers &= Board.SQUARES[Board.getLeastSignificantBit(board.pieceBoards[color][Piece.KNIGHT] & attackers)].mask_off;
      }
      else if ((board.pieceBoards[color][Piece.BISHOP] & attackers) != 0)
      {
        attackedPiece = Piece.TYPE_VALUES[Piece.BISHOP];
        attackers &= Board.SQUARES[Board.getLeastSignificantBit(board.pieceBoards[color][Piece.BISHOP] & attackers)].mask_off;
      }
      else if ((board.pieceBoards[color][Piece.ROOK] & attackers) != 0)
      {
        attackedPiece = Piece.TYPE_VALUES[Piece.ROOK];
        attackers &= Board.SQUARES[Board.getLeastSignificantBit(board.pieceBoards[color][Piece.ROOK] & attackers)].mask_off;
      }
      else if ((board.pieceBoards[color][Piece.QUEEN] & attackers) != 0)
      {
        attackedPiece = Piece.TYPE_VALUES[Piece.QUEEN];
        attackers &= Board.SQUARES[Board.getLeastSignificantBit(board.pieceBoards[color][Piece.QUEEN] & attackers)].mask_off;
      }
      else if ((board.pieceBoards[color][Piece.KING] & attackers) != 0)
      {
        attackedPiece = Piece.TYPE_VALUES[Piece.KING];
        attackers &= Board.SQUARES[Board.getLeastSignificantBit(board.pieceBoards[color][Piece.KING] & attackers)].mask_off;
      }
      else
      {
        break;
      }

      color = color ^ 1;
    }


    while (--swapIndex != 0)
    {
      if (swapScores[swapIndex] > -swapScores[swapIndex - 1])
      {
        swapScores[swapIndex - 1] = -swapScores[swapIndex];
      }
    }
    return (swapScores[0]);
  }

  public final void nextMove(Move[] moves, int moveIndex)
  {
    int bestIndex = -1;
    int best = -Searcher.INFINITY;

    for(int index = moveIndex;moves[index].moved != null;++index)
    {
      if(moves[index].score > best)
      {

        bestIndex = index;
        best = moves[index].score;
      }
    }
    if(bestIndex > -1)
    {
      Move temp = moves[bestIndex];
      moves[bestIndex] = moves[moveIndex];
      moves[moveIndex] = temp;
    }
  }

  public void stop()
  {
    System.err.println("ABSearch stopping!");
    done = true;
  }


  public boolean isDone()
  {
    return !running;
  }


  public static class SearchInspector implements Runnable
  {
    Board startingBoard;
    LineScorer lineScorer;
    ABSearch search;


    public SearchInspector(ABSearch search, Board startingBoard)
    {
      this.search = search;
      this.startingBoard = startingBoard;
//      lineScorer = new LineScorer(search.eval);
    }


    public void start()
    {
      Thread runner = new Thread(this);
      runner.start();
    }


    public void run()
    {
      while (!search.done)
      {
        Move[] currentLine = Move.createMoves(100);
        System.arraycopy(search.currentLine, 0, currentLine, 0, 100);
        System.err.print(Move.toString(currentLine));

        System.err.println(": " + lineScorer.scoreLine(startingBoard, currentLine));
        startingBoard.toString();
        try
        {
          Thread.sleep(500);
        }
        catch (InterruptedException e)
        {
          e.printStackTrace();
        }
      }
    }
  }


  public PositionHashtable getAbHashtable() {
    return abHashtable;
  }
}

