/* $Id$ */

package chess.engine.search;

import chess.engine.model.Board;
import chess.engine.model.Move;
import chess.engine.model.Piece;
import chess.engine.model.Square;
import chess.engine.utils.LineScorer;
import chess.engine.utils.MoveGeneration;

/**
 * @author Joshua Levine <levinester@gmail.com>
 * @version $Revision$ $Name$ $Date$
 */
public class ABSearch implements Searcher
{
  private static final int EXTEND_THREAT_EXTENSION = 250;
  private static final int EXTEND_NULL_MATE_THREAT = 125;
  private static final int EXTEND_CHECK = 75;
  private static final int EXTEND_RECAPTURE = 125;
  private static final int EXTEND_PAWN_PUSH = 125;
  private static final int PLY_SIZE = 250;

  private static final int REDUCE_PRUNE = -1000;
  private static final int REDUCE_FUTILE = -300;
  private static final int REDUCE_DEFAULT = -75;
  private static final int REDUCE_BORING = -25;

  private static final int MAX_EXTENSIONS = 10;
  private static final int MAX_REDUCTIONS = 10;
  private static final int THREAT_INDICATOR = 120;
  private static final int THREAT_IMPLIED = 12;
  private static boolean REDUCE = true;

  private static boolean debug = false;
  private BoardEvaluator eval;
  private MoveGeneration moveGeneration;
  private PositionHashtable abHashtable = new PositionHashtable();

  public int searchExtensions;
  public int searchReductions;

  public SearchStats stats;

  // indexed as [fromSquare.index64][toSquare.index64]
  public int[][] moveHistory = new int[64][64];

  private Move NULL_MOVE = new Move();
  private boolean running = false;
  private boolean inPawnEnding = false;
  private static final int[] MARGIN = {   50,   50,   50,   75,   75,
                                          75,   100,  100,  100,  150,
                                         150,  250,  350,  350,  500,
                                         500,  900,  900,  900,  900,
                                        1200, 1200, 1200, 1200, 1200,
                                        3000, 3000, 3000, 3000, 3000,
                                        3000, 3000, 3000, 3000, 3000,
                                        3000, 3000, 3000, 3000, 3000,
                                        3000, 3000, 3000, 3000, 3000,
                                        3000, 3000, 3000, 3000, 3000,
                                        3000, 3000, 3000, 3000, 3000,
                                        3000, 3000, 3000, 3000, 3000
  };

  private static final int[] FUTILITY_TABLE = {   300,   300,  500,  500,  500,  700,  700, 900,  900, 1500, 1500, 1500,
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
    kingSafety = new int[2][128];
    moveHistory = new int[64][64];
    eval.reset();
    for(int i = 2;i < killer1.length;i++) {
      killer1[i].reset(killer1[i-2]);
      killer2[i].reset(killer2[i-2]);
      killer3[i].reset(killer3[i-2]);
    }
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
  public volatile boolean researchAtRoot = false;
  /* read only please */
  public Move[][] pv = new Move[128][128];
  /* read only please */
  private Move[] currentLine = Move.createMoves(128);

  private Move[] killer1 = Move.createMoves(128);
  private Move[] killer2 = Move.createMoves(128);
  private Move[] killer3 = Move.createMoves(128);
  private boolean[] inCheck = new boolean[128];
  private int[][] kingSafety = new int[2][128];

  public Move[] getPV()
  {
    return pv[0];
  }


  private Move[][] moveLists = new Move[128][128];
  private int[][] extensions = new int[64][64];


  private int ply;

  public ABSearch(MoveGeneration moveGeneration, BoardEvaluator eval)
  {
    this.moveGeneration = moveGeneration;
    this.eval = eval;

    for (int i = 0; i < 128; i++)
    {
      moveLists[i] = Move.createMoves(128);
      pv[i] = Move.createMoves(128);
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
    inPawnEnding = board.pieceValues == 0;

    long start = System.currentTimeMillis();

    ply = 0;
    inCheck[ply] = board.isSquareCheckedByColor(board.turn == 1 ? board.whiteKing.square : board.blackKing.square, board.turn ^ 1);
    int score = rootSearch(-MATE, MATE, depth * PLY_SIZE, board);
    researchAtRoot = false;

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

    if(board.pieceBoards[0][Piece.QUEEN] != 0) {
      kingSafety[0][ply] = eval.scoreAttackingPieces(board, board.whiteKing.square, 0);
    }
    else {
      kingSafety[0][ply] = 0;
    }
    if(board.pieceBoards[1][Piece.QUEEN] != 0) {
      kingSafety[1][ply] = eval.scoreAttackingPieces(board, board.blackKing.square, 1);
    }
    else {
      kingSafety[1][ply] = 0;
    }

    if (depth < 0)
    {
      return quiescenceSearch(2, alpha, beta, board);
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
    }

    int score;

    boolean pvFound = false;
    Move[] moveList = moveLists[ply];
    int movesGenerated = 0;
    if (inCheck[ply])
    {
      movesGenerated = moveGeneration.generateEvasions(movesGenerated, moveList, board);
    }
    else
    {
      movesGenerated = moveGeneration.generateMoves(moveList, board);
    }

    ////////////////////////////////////////////////////////
    //////////////// LOOP THROUGH ALL MOVES ////////////////
    ////////////////////////////////////////////////////////
    int moveCount = 0;

    for (int moveIndex = 0; moveIndex < movesGenerated; ++moveIndex)
    {
      nextMove(board, moveList, moveIndex, hashEntry);

//      System.out.print("Root: " + move + "("+moveList[moveIndex].score+")");

      // make the move
      final Move move = moveList[moveIndex];
      board.make(move);

      // unmake if we are in check
      if (board.isSquareCheckedByColor(!whiteToMove ?
                                       board.blackKing.square :
                                       board.whiteKing.square, board.turn))
      {
        board.unmake(move);
        continue;
      }

      currentLine[ply].reset(move);
      currentLine[ply + 1].moved = null;

      // if no check, count this move, and then recurse
      ++moveCount;

      ++ply;
      pv[ply][ply].moved = null;

      if(board.pieceBoards[0][Piece.QUEEN] != 0) {
        kingSafety[0][ply] = eval.scoreAttackingPieces(board, board.whiteKing.square, 0);
      }
      else {
        kingSafety[0][ply] = 0;
      }
      if(board.pieceBoards[1][Piece.QUEEN] != 0) {
        kingSafety[1][ply] = eval.scoreAttackingPieces(board, board.blackKing.square, 1);
      }
      else {
        kingSafety[1][ply] = 0;
      }

      int extend = 0;
      inCheck[ply] = board.isSquareCheckedByColor(board.turn == 1 ? board.whiteKing.square : board.blackKing.square, board.turn ^ 1);
      if (searchExtensions < MAX_EXTENSIONS && extensions[move.fromSquare.index64][move.toSquare.index64] == 0)
      {
        // Extend Check
        if (inCheck[ply - 1])
        {
          extend += EXTEND_CHECK;
          ++stats.checkExtensions;
        }
        // Extend mate threat from null-move
        else if(mateThreat)
        {
          extend += EXTEND_NULL_MATE_THREAT;
          ++stats.threatExtensions;
        }
        // Extend pawn push
        else if(((board.squareAttackers[move.toSquare.index64] & board.pieceBoards[board.turn][Board.ALL_PIECES]) == 0) &&
                move.moved.type == Piece.PAWN &&
                ((move.moved.color == 1 && move.toSquare.rank > (board.isEndgame() ? 4 : 5)) || (move.moved.color == 0 && move.toSquare.rank < (board.isEndgame() ? 3 : 2))))
        {
          extend += EXTEND_PAWN_PUSH;
          ++stats.pawnPushExtensions;
        }
        else if(kingSafety[board.turn][ply] > kingSafety[board.turn][ply-1] + THREAT_INDICATOR) {
          extend += EXTEND_THREAT_EXTENSION;
          ++stats.threatExtensions;
        }
      }

      extensions[move.fromSquare.index64][move.toSquare.index64] += extend / PLY_SIZE;
      searchExtensions += extend / PLY_SIZE;

      if(!pvFound)
      {
        score = -abSearch(-beta, -alpha, (depth - PLY_SIZE) + extend, board, true);
      }
      else
      {
        score = -abSearch(-alpha-1, -alpha, (depth - PLY_SIZE) + extend, board, true);
        //score = -zwSearch(1-beta, (depth - 1) + extend, board, true, true);
        if (score > alpha && score <= beta)
        {
          researchAtRoot = true;
          score = -abSearch(-beta, -alpha, (depth - PLY_SIZE) + extend, board, true);
        }
      }
      researchAtRoot = false;

//      System.out.println(" s:("+score+")");

      extensions[move.fromSquare.index64][move.toSquare.index64] -= extend / PLY_SIZE;
      searchExtensions -= extend / PLY_SIZE;

/*
      if(ply == 1)
      {
        System.err.println("S: " + score + " O: " + moveList[moveIndex].score + " Moves: " + move + " " + moveList[moveIndex].toString(pv[ply+1],1));
      }
*/


      ply--;
      currentLine[ply].check = move.check = inCheck[ply+1];

      // unmake move
      board.unmake(move);

      if (score > alpha && !done)
      {
        move.score = score;
        pv[ply][ply].reset(move);

        int t = ply + 1;
        pv[ply][t].reset(pv[ply + 1][t]);
        while (pv[ply + 1][t++].moved != null)
        {
          pv[ply][t].reset(pv[ply + 1][t]);
        }

        alpha = score;
        pvFound = true;
      }
      else if(score < alpha - 100 && move.taken == null)
      {
        moveHistory[move.moved.type][move.toSquare.index64] --;
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
        }
        else
        {
          alpha = 0;
        }
        abHashtable.putEntry(1000000, PositionHashtable.EXACT_VALUE, alpha, whiteToMove ? board.hash1 : ~board.hash1, pv[ply][ply], mateThreat);
      }
      else
      {
        abHashtable.putEntry(depth / PLY_SIZE, !pvFound ? PositionHashtable.UPPER_BOUND : PositionHashtable.EXACT_VALUE, alpha, whiteToMove ? board.hash1 : ~board.hash1, pv[ply][ply], mateThreat);
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
/*
    if(Move.toString(currentLine).equals("1. ... Qd3 2. Qxd3")) {
      debug = true;
    }
    else {
      debug = false;
    }
*/

    if (done)
    {
      return -INFINITY;
    }

    if (depth < PLY_SIZE)
    {
      return quiescenceSearch(board.isEndgame() ? 0 : 2, alpha, beta, board);
    }

    ++stats.nodes;
    if(beta - alpha == 1) {
      ++stats.zwNodes;
    }
    else {
      ++stats.pvNodes;
    }

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
      if(hashEntry.depth >= depth / PLY_SIZE)
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
            if (hashEntry.score < alpha)
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
    int best = -INFINITY;
    // Pawn Ending Extension
    if(board.pieceValues == 0 && !inPawnEnding)
    {
      inPawnEnding = true;
      searchExtensions += 3;
      ++stats.endgameExtensions;
      score = abSearch(alpha, beta, depth + (3 * PLY_SIZE), board, false);
      //abHashtable.putEntry(depth / PLY_SIZE + 3, pv[ply][ply].moved == null ? PositionHashtable.UPPER_BOUND : PositionHashtable.EXACT_VALUE, score, whiteToMove ? board.hash1 : ~board.hash1, pv[ply][ply], false);
      searchExtensions -= 3;
      inPawnEnding = false;
      return score;
    }

    // Null Move
    if (doNull && (hashEntry == null || hashEntry.move.moved == null) && !inCheck[ply] && !mateThreat && board.pieceValues > 0 && depth > PLY_SIZE)
    {
      int nullMoveReduction = depth > (6 * PLY_SIZE) && board.pieceValues > 8 ?
                               (3 * PLY_SIZE) :
                               (2 * PLY_SIZE);
      ++board.moveIndex;
      board.turn ^= 1;
      board.repetitionTable[board.moveIndex] = 1;
      ++ply;
      inCheck[ply] = false;
      score = -abSearch(-beta, 1-beta, (depth - nullMoveReduction) - PLY_SIZE, board, false);
      //score = -zwSearch(1-beta, (depth - nullMoveReduction) - 1, board, false, true);
      ply--;
      board.turn ^= 1;
      board.repetitionTable[board.moveIndex] = whiteToMove ? board.hash1 : ~board.hash1;
      board.moveIndex--;

      if (score < -MATE + 300)
      {
        mateThreat = true;
        killer1[ply+1].reset(pv[ply + 1][ply + 1]);
        //depth++;
      }
      else {
        if(killer2[ply+1].moved == null) {
          killer2[ply+1].reset(pv[ply + 1][ply + 1]);
        }
        else {
          killer3[ply+1].reset(pv[ply + 1][ply + 1]);
        }
      }
      if (score >= beta)
      {
        abHashtable.putEntry(depth / PLY_SIZE, PositionHashtable.LOWER_BOUND, score, whiteToMove ? board.hash1 : ~board.hash1, NULL_MOVE, mateThreat);
        pv[ply][ply].moved = null;
        return score;
      }
    }

    // IID
/*
    if(hashEntry == null && depth > (2 * PLY_SIZE))
    {
      abSearch(alpha, beta, depth - (2 * PLY_SIZE), board, false);
      hashEntry = abHashtable.getEntry(whiteToMove ? board.hash1 : ~board.hash1);
    }
*/

    boolean pvFound = false;
    final Move[] moveList = moveLists[ply];
    int movesGenerated = 0;
//    moveGeneration.setHashEntry(hashEntry);
    if (inCheck[ply])
    {
      movesGenerated = moveGeneration.generateEvasions(movesGenerated, moveList, board);
    }
    else
    {
      movesGenerated = moveGeneration.generateMoves(moveList, board);
    }


    ////////////////////////////////////////////////////////
    //////////////// LOOP THROUGH ALL MOVES ////////////////
    ////////////////////////////////////////////////////////
    int moveCount = 0;
    int reduce = 0;

    for (int moveIndex = 0; moveIndex < movesGenerated; ++moveIndex)
    {
      nextMove(board, moveList, moveIndex, hashEntry);

      // make the move
      final Move move = moveList[moveIndex];
      board.make(move);

      // unmake if we are in check
      if (board.isSquareCheckedByColor(!whiteToMove ?
                                       board.blackKing.square :
                                       board.whiteKing.square, board.turn))
      {
        board.unmake(move);
        continue;
      }

      currentLine[ply].reset(move);
      currentLine[ply + 1].moved = null;

      // if no check, count this move, and then recurse
      ++moveCount;

      ++ply;
      pv[ply][ply].moved = null;

      if(board.pieceBoards[0][Piece.QUEEN] != 0) {
        kingSafety[0][ply] = eval.scoreAttackingPieces(board, board.whiteKing.square, 0);
      }
      else {
        kingSafety[0][ply] = 0;
      }
      if(board.pieceBoards[1][Piece.QUEEN] != 0) {
        kingSafety[1][ply] = eval.scoreAttackingPieces(board, board.blackKing.square, 1);
      }
      else {
        kingSafety[1][ply] = 0;
      }

      int extend = 0;
      inCheck[ply] = board.isSquareCheckedByColor(board.turn == 1 ? board.whiteKing.square : board.blackKing.square, board.turn ^ 1);
      if (searchExtensions < MAX_EXTENSIONS && extensions[move.fromSquare.index64][move.toSquare.index64] == 0)
      {
        // Extend Check
        if (inCheck[ply - 1])
        {
          extend += EXTEND_CHECK;
          ++stats.checkExtensions;
        }
        // Extend recapture
        else if(ply > 1 && move.taken != null &&
                           currentLine[ply - 2].taken != null &&
                           (move.toSquare.index64 == currentLine[ply - 2].takenSquare.index64) &&
                           move.taken.materialValue == -currentLine[ply - 2].taken.materialValue)

        {
          extend += EXTEND_RECAPTURE;
          ++stats.recaptureExtensions;
        }
        // Extend mate threat from null-move
        else if(mateThreat)
        {
          extend += EXTEND_NULL_MATE_THREAT;
          ++stats.threatExtensions;
        }
        // Extend pawn push
        else if(((board.squareAttackers[move.toSquare.index64] & board.pieceBoards[board.turn][Board.ALL_PIECES]) == 0) &&
                move.moved.type == Piece.PAWN &&
                ((move.moved.color == 1 && move.toSquare.rank > (board.isEndgame() ? 4 : 5)) || (move.moved.color == 0 && move.toSquare.rank < (board.isEndgame() ? 3 : 2))))
        {
          extend += EXTEND_PAWN_PUSH;
          ++stats.pawnPushExtensions;
        }
        else if(kingSafety[board.turn][ply] > kingSafety[board.turn][ply-1] + THREAT_INDICATOR) {
          extend += EXTEND_THREAT_EXTENSION;
          ++stats.threatExtensions;
        }
      }

      reduce = 0;
      if(beta - alpha == 1 && (reduce = canBeReducedOrPruned(board, depth, alpha, move, hashEntry, moveCount, extend)) < 0)
      {
        if(depth + reduce <= 0)
        {
          ply--;
          board.unmake(move);
          continue;
        }
      }

      extensions[move.fromSquare.index64][move.toSquare.index64]+=extend/ PLY_SIZE;
      searchExtensions += extend/PLY_SIZE;
      searchReductions += reduce != 0 ? 1 : 0;

      if(!pvFound)
      {
        score = -abSearch(-beta, -alpha, (depth - PLY_SIZE) + extend + reduce, board, doNull);
      }
      else
      {
        score = -abSearch(-alpha-1, -alpha, (depth - PLY_SIZE) + extend + reduce, board, doNull);
        if (score > alpha && score <= beta)
        {
          score = -abSearch(-beta, -alpha, (depth - PLY_SIZE) + extend + reduce, board, doNull);
        }
      }

      //if(debug) System.err.println("S[" + ply + "]: " + score + " Moves: " + Move.toString(currentLine));

      extensions[move.fromSquare.index64][move.toSquare.index64]-=extend/ PLY_SIZE;
      searchExtensions -= extend/PLY_SIZE;
      searchReductions -= reduce != 0 ? 1 : 0;

/*
      if(ply == 1)
      {
        System.err.println("S: " + score + " O: " + moveList[moveIndex].score + " Moves: " + move + " " + moveList[moveIndex].toString(pv[ply+1],1));
      }
*/


      ply--;
      currentLine[ply].check = move.check = inCheck[ply+1];

      // unmake move
      board.unmake(move);

      if (score > best && !done)
      {
        best = score;

        pv[ply][ply].reset(move);

        int t = ply + 1;
        pv[ply][t].reset(pv[ply + 1][t]);
        while (pv[ply + 1][t++].moved != null)
        {
          pv[ply][t].reset(pv[ply + 1][t]);
        }

        if (score >= beta)
        {
          abHashtable.putEntry(depth / PLY_SIZE, PositionHashtable.LOWER_BOUND, score, whiteToMove ? board.hash1 : ~board.hash1, move, mateThreat);

          if (score > MATE - 300)
          {
            killer1[ply].reset(move);
          }
          else if (move.taken == null && move.promoteTo == -1)
          {
            if(killer2[ply].moved == null)
            {
              killer2[ply].reset(move);
            }
            else if(!killer2[ply].matches(move))
            {
              killer3[ply].reset(move);
            }
            moveHistory[move.moved.type][move.toSquare.index64] ++;
          }
          return score;
        }

        if(score > alpha) {
          alpha = score;
          pvFound = true;
        }
      }
      else
      {
        moveHistory[move.moved.type][move.toSquare.index64] --;
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
          best = -MATE + ply;
        }
        else
        {
          best = 0;
        }
        pv[ply][ply].reset(NULL_MOVE);
        abHashtable.putEntry(1000000, PositionHashtable.EXACT_VALUE, best, whiteToMove ? board.hash1 : ~board.hash1, pv[ply][ply], mateThreat);
      }
      else
      {
        if(pvFound && best < -MATE + 300) {
          killer1[ply+1].reset(pv[ply + 1][ply + 1]);
        }
        abHashtable.putEntry(depth / PLY_SIZE, !pvFound ? PositionHashtable.UPPER_BOUND : PositionHashtable.EXACT_VALUE, best, whiteToMove ? board.hash1 : ~board.hash1, pv[ply][ply], mateThreat);
      }
      pv[ply][ply].score = best;
    }

    return best;
  }

  private final int canBeReducedOrPruned(Board board, int depth, int alpha, Move move, PositionHashtable.HashEntry hashEntry, int moveCount, int extend)
  {
    if(!REDUCE || extend != 0 || searchReductions > MAX_REDUCTIONS) {
      return 0;
    }

    if(ply > 0 &&
       !inCheck[ply] &&
       !inCheck[ply-1] &&
//       (hashEntry == null || hashEntry.move == NULL_MOVE || hashEntry.score < alpha) &&
       move.promoteTo != Piece.QUEEN &&
//       move.moved.type != Piece.PAWN &&
       move.castledRook == null &&
//       move.taken == null &&
//       alpha > mateDistance &&
       moveCount > depth / PLY_SIZE/* &&
       move.score <= 0*/
            ) {

      if(move.moved.type == Piece.PAWN) {
        long[] passedMask = move.moved.color == 1 ? SimpleEvaluator.WHITE_PASSED_MASK : SimpleEvaluator.BLACK_PASSED_MASK;
        if((board.pieceBoards[board.turn][Piece.PAWN] & passedMask[move.toSquare.index64]) == 0) {
          return 0;
        }
      }

      if(board.turn == 0) {
        // white attack has improved, don't reduce
        if(kingSafety[1][ply] - kingSafety[1][ply-1] > THREAT_IMPLIED) {
          return 0;
        }
        // white defense has improved, don't reduce
        if(kingSafety[0][ply] - kingSafety[0][ply-1] < -THREAT_IMPLIED) {
          return 0;
        }
      }
      else {
        // black attack has improved, don't reduce
        if(kingSafety[0][ply] - kingSafety[0][ply-1] > THREAT_IMPLIED) {
          return 0;
        }
        // black defense has improved, don't reduce
        if(kingSafety[1][ply] - kingSafety[1][ply-1] < -THREAT_IMPLIED) {
          return 0;
        }
      }

      int threat = 0;
      int scoreEstimate = (board.turn == 1 ? -1 : 1) * (board.materialScore + board.positionScore + kingSafety[1][ply] - kingSafety[0][ply]) ;
      int swap = swap(board, move, board.turn);
      long idleThreats = swap < -50 ? move.moved.attacks : (board.attacks[board.turn] & ~board.attacks[board.turn ^ 1] & board.pieceBoards[board.turn ^ 1][Board.ALL_PIECES]);
      long threats = board.attacks[board.turn ^ 1] & ~idleThreats & ~board.attacks[board.turn] & board.pieceBoards[board.turn][Board.ALL_PIECES];

      while(threats != 0) {
        int threatSquareIndex = Long.numberOfTrailingZeros(threats);
        Square threatSquare = Board.SQUARES[threatSquareIndex];
        threats ^= 1L << threatSquareIndex;
        threat = Math.max(threat, Piece.TYPE_VALUES[board.boardSquares[threatSquare.index128].piece.type]);
      }

      if(alpha > scoreEstimate + swap + (threat >> 1) + MARGIN[depth / PLY_SIZE]) {
        if(depth < (3 * PLY_SIZE) && alpha > scoreEstimate + swap + threat + FUTILITY_TABLE[depth / PLY_SIZE]) {
          ++stats.reducePrune;
          return REDUCE_PRUNE;
        }
        if(alpha > scoreEstimate + swap + (threat) + MARGIN[depth / PLY_SIZE]) {
          ++stats.reduceFutile;
          return REDUCE_FUTILE;
        }
        ++stats.reduceMargin;
        return REDUCE_DEFAULT;
      }

      ++stats.reduceBoring;
      return REDUCE_BORING;
    }

    return 0;
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


  public final int quiescenceSearch(int checkDepth, int alpha, int beta, Board board)
  {
    pv[ply][ply].moved = null;
    if (done)
    {
      return -INFINITY;
    }
    ++stats.nodes;
    ++stats.qNodes;

    int score;
    int best = -INFINITY;

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
          if (hashEntry.score < alpha)
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

    Move[] moveList = moveLists[ply];
    int movesGenerated = 0;
    int checksGenerated = 0;
    int extend = 0;
    boolean foundScore = false;


    if(!inCheck[ply])
    {

      ++stats.evals;
      score = best = eval.scorePosition(board, alpha, beta);
      //if(debug) System.err.println("S: " + (board.turn == 1 ? score : -score)+ " - " + Move.toString(currentLine));

      if (score > alpha)
      {
        if (score >= beta)
        {
        //if(debug) System.err.println("QSearch Initial Cut(" + (whiteToMove ? score : -score)+ "): " + Move.toString(currentLine));
          abHashtable.putEntry(-100, PositionHashtable.LOWER_BOUND, score, whiteToMove ? board.hash1 : ~board.hash1, NULL_MOVE, false);
          return score;
        }
        alpha = score;
        foundScore = true;
      }

      if(checkDepth > 0) {
        checksGenerated = movesGenerated = moveGeneration.generateChecks(0, moveList, board);
      }
      movesGenerated = moveGeneration.generateCaptures(movesGenerated, moveList, board);

    }
    else
    {
      movesGenerated = moveGeneration.generateEvasions(movesGenerated, moveList, board);
      if(checkDepth > 0) {
        if(movesGenerated == 2) {
          checkDepth ++;
          extend = 1;
          ++stats.checkExtensions;
        }
        else if(movesGenerated == 1) {
          checkDepth += 2;
          extend = 2;
          stats.doubleCheckExtensions += 2;
        }
      }

      searchExtensions += extend;

//      alpha = -MATE + ply;
      foundScore = false;
    }

    int moveCount = 0;

    if(board.pieceBoards[0][Piece.QUEEN] != 0) {
      kingSafety[0][ply] = eval.scoreAttackingPieces(board, board.whiteKing.square, 0);
    }
    else {
      kingSafety[0][ply] = 0;
    }
    if(board.pieceBoards[1][Piece.QUEEN] != 0) {
      kingSafety[1][ply] = eval.scoreAttackingPieces(board, board.blackKing.square, 1);
    }
    else {
      kingSafety[1][ply] = 0;
    }
    Move move;
    for (int moveIndex = 0; moveIndex < movesGenerated; ++moveIndex)
    {
      nextMove(board, moveList, moveIndex, hashEntry);
      move = moveList[moveIndex];

/*
      final int staticSwap = SimpleEvaluator.swap[board.turn][move.moved.type][board.attackState[1][move.toSquare.index64]][board.attackState[0][move.toSquare.index64]] +
                (move.taken != null ? Piece.TYPE_VALUES[move.taken.type] : 0);

*/
      if(!move.check &&
         !inCheck[ply] &&
         move.taken != null &&
         move.promoteTo != Piece.QUEEN &&
         ((board.materialScore + board.positionScore + kingSafety[1][ply] - kingSafety[0][ply]) * (whiteToMove ? 1 : -1) +
                 move.score < alpha))
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

      ++moveCount;

      currentLine[ply].reset(move);
      currentLine[ply + 1].moved = null;
      ++ply;

      inCheck[ply] = board.isSquareCheckedByColor(board.turn == 1 ? board.whiteKing.square : board.blackKing.square, board.turn ^ 1);

      score = -quiescenceSearch(checkDepth - 1, -beta, -alpha, board);
      ply--;

      // unmake move
      board.unmake(move);
      currentLine[ply].check = move.check = inCheck[ply+1];

      if (score > best)
      {
        pv[ply][ply].reset(move);
        int t = ply + 1;
        pv[ply][t].reset(pv[ply + 1][t]);
        while (pv[ply + 1][t++].moved != null)
        {
          pv[ply][t].reset(pv[ply + 1][t]);
        }

        best = score;
        if (score >= beta)
        {
//          if(debug) System.err.println("QSearch BCut(" + (whiteToMove ? score : -score)+ "): " + Move.toString(currentLine));
          abHashtable.putEntry(-100, PositionHashtable.LOWER_BOUND, score, whiteToMove ? board.hash1 : ~board.hash1, pv[ply][ply], false);
          searchExtensions -= extend;
          return score;
        }

        //if(debug) System.err.println("QSearch[" + ply + "] PV(" + (whiteToMove ? score : -score)+ "): " + Move.toString(currentLine) + " - " + Move.toString(pv[ply], ply));

        if(score > alpha) {
          foundScore = true;
          alpha = score;
        }
      }
    }

    if(moveCount == 0 && inCheck[ply])
    {
      best = -MATE + ply;
      foundScore = true;
      abHashtable.putEntry(9999, PositionHashtable.EXACT_VALUE, best, whiteToMove ? board.hash1 : ~board.hash1, pv[ply][ply], false);
    }
    else {
      abHashtable.putEntry(-100, !foundScore ? PositionHashtable.UPPER_BOUND : PositionHashtable.EXACT_VALUE, best, whiteToMove ? board.hash1 : ~board.hash1, pv[ply][ply], false);
    }
//    if(debug) System.out.println("QSearch Return(" + (whiteToMove ? alpha : -alpha)+ "): " + Move.toString(currentLine));

    searchExtensions -= extend;
    return best;
  }


  int[] swapScores = new int[32];
  long attackers = 0;
  long rammers = 0;
  int swapIndex = 0;
  int attackedPiece = 0;
  int attackerSquare;

  private int swap(Board board, Move move, int color)
  {
    swapIndex = 1;
    attackers = move.fromSquare.mask_off & (board.squareAttackers[move.toSquare.index64] | (board.squareAttackers[move.fromSquare.index64] & board.squareRammers[move.toSquare.index64]));
    rammers = board.squareRammers[move.toSquare.index64] & ~(board.squareAttackers[move.fromSquare.index64] &  board.squareRammers[move.toSquare.index64]);

    swapScores[0] = move.taken != null ? Piece.TYPE_VALUES[move.taken.type] : 0;
    attackedPiece = Piece.TYPE_VALUES[/*move.promoteTo != -1 ? move.promoteTo : */move.moved.type];
    attackers &= move.fromSquare.mask_off;

    while ((attackers & board.pieceBoards[color][Board.ALL_PIECES]) != 0)
    {
      swapScores[swapIndex] = -swapScores[swapIndex - 1] + attackedPiece;

      if ((board.pieceBoards[color][Piece.PAWN] & attackers) != 0)
      {
        attackedPiece = Piece.TYPE_VALUES[Piece.PAWN];
        attackerSquare = Long.numberOfTrailingZeros(board.pieceBoards[color][Piece.PAWN] & attackers);
        attackers &= Board.SQUARES[attackerSquare].mask_off;
        attackers |= (board.squareAttackers[attackerSquare] & rammers);
        rammers &= ~(board.squareAttackers[attackerSquare] & rammers);
      }
      else if ((board.pieceBoards[color][Piece.KNIGHT] & attackers) != 0)
      {
        attackedPiece = Piece.TYPE_VALUES[Piece.KNIGHT];
        attackers &= Board.SQUARES[Long.numberOfTrailingZeros(board.pieceBoards[color][Piece.KNIGHT] & attackers)].mask_off;
      }
      else if ((board.pieceBoards[color][Piece.BISHOP] & attackers) != 0)
      {
        attackedPiece = Piece.TYPE_VALUES[Piece.BISHOP];
        attackerSquare = Long.numberOfTrailingZeros(board.pieceBoards[color][Piece.BISHOP] & attackers);
        attackers &= Board.SQUARES[attackerSquare].mask_off;
        attackers |= (board.squareAttackers[attackerSquare] & rammers);
        rammers &= ~(board.squareAttackers[attackerSquare] & rammers);
      }
      else if ((board.pieceBoards[color][Piece.ROOK] & attackers) != 0)
      {
        attackedPiece = Piece.TYPE_VALUES[Piece.ROOK];
        attackerSquare = Long.numberOfTrailingZeros(board.pieceBoards[color][Piece.ROOK] & attackers);
        attackers &= Board.SQUARES[attackerSquare].mask_off;
        attackers |= (board.squareAttackers[attackerSquare] & rammers);
        rammers &= ~(board.squareAttackers[attackerSquare] & rammers);
      }
      else if ((board.pieceBoards[color][Piece.QUEEN] & attackers) != 0)
      {
        attackedPiece = Piece.TYPE_VALUES[Piece.QUEEN];
        attackerSquare = Long.numberOfTrailingZeros(board.pieceBoards[color][Piece.QUEEN] & attackers);
        attackers &= Board.SQUARES[attackerSquare].mask_off;
        attackers |= (board.squareAttackers[attackerSquare] & rammers);
        rammers &= ~(board.squareAttackers[attackerSquare] & rammers);
      }
      else if ((board.pieceBoards[color][Piece.KING] & attackers) != 0)
      {
        attackedPiece = Piece.TYPE_VALUES[Piece.KING];
        attackers &= Board.SQUARES[Long.numberOfTrailingZeros(board.pieceBoards[color][Piece.KING] & attackers)].mask_off;
      }
      else
      {
        break;
      }

      ++swapIndex;
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

  public final void nextMove(Board board, Move[] moves, int moveIndex, PositionHashtable.HashEntry hashEntry)
  {
    int bestIndex = -1;
    int best = -Searcher.INFINITY;

    for(int index = moveIndex;moves[index].moved != null;++index)
    {
      if(moveIndex == 0) {
        if (hashEntry != null && moves[index].matches(hashEntry.move))
        {
          moves[index].score = INFINITY;
        }
        else if(moves[index].matches(killer1[ply]))
        {
          moves[index].score = INFINITY - 1;
        }
        else if(ply > 1 && moves[index].matches(killer1[ply - 2]))
        {
          moves[index].score = INFINITY - 2;
        }
        else if(moves[index].matches(killer2[ply]))
        {
          moves[index].score = INFINITY - 60000;
        }
        else if(ply > 1 && moves[index].matches(killer2[ply - 2]))
        {
          moves[index].score = INFINITY - 60001;
        }
        else if(moves[index].matches(killer3[ply]))
        {
          moves[index].score = INFINITY - 60002;
        }
        else if(ply > 1 && moves[index].matches(killer3[ply - 2]))
        {
          moves[index].score = INFINITY - 60003;
        }
        else if(moves[index].taken != null)
        {
          if(moves[index].score - Move.CAPTURE_SCORE < 50)
          {
            int swapScore = swap(board, moves[index], board.turn ^ 1);
            if(swapScore <= -50) {
              moves[index].score = swapScore;
            }
            else
            {
              moves[index].score = swapScore + Move.CAPTURE_SCORE;
            }
          }
        }
        else if(moves[index].promoteTo != -1)
        {
          if(swap(board, moves[index], board.turn ^ 1) <= -50)
          {
            moves[index].score -= Move.PROMOTE_SCORE;
          }
        }
        else
        {
          if((board.attacks[board.turn ^ 1] & ~board.attacks[board.turn] & moves[index].fromSquare.mask_on) != 0 &&
             (board.attacks[board.turn ^ 1] & ~board.attacks[board.turn] & moves[index].toSquare.mask_on) == 0) {
            moves[index].score += 1000;
          }
          else {
            moves[index].score = Math.min(moveHistory[moves[index].moved.type][moves[index].toSquare.index64], 8000);
          }
        }
      }

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
        Move[] currentLine = Move.createMoves(128);
        System.arraycopy(search.currentLine, 0, currentLine, 0, 128);
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

  @Override
  public boolean isResearchAtRoot() {
    return researchAtRoot;
  }

  public PositionHashtable getAbHashtable() {
    return abHashtable;
  }
}