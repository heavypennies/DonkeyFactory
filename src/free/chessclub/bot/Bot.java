/**
 * The chessclub.com connection library.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2002 Alexander Maryanovsky.
 * All rights reserved.
 *
 * The chessclub.com connection library is free software; you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * The chessclub.com connection library is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the chessclub.com connection library; if not, write to the Free
 * Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package free.chessclub.bot;

import chess.engine.model.Board;
import chess.engine.model.Move;
import chess.engine.model.Piece;
import chess.engine.search.*;
import chess.engine.utils.MoveGeneration;
import free.jin.Game;
import free.jin.JinConnection;
import free.jin.JinFrame;
import free.jin.event.*;
import free.jin.freechess.FreechessJinListenerManager;
import free.jin.freechess.JinFreechessConnection;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.http.AccessToken;
import twitter4j.http.HttpClient;
import twitter4j.http.Response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Bot extends JinFreechessConnection implements GameListener {

  public boolean autoseek = false;
  public boolean tellOwner = true;
  public String owner = "tarabas";

  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss aaa");
  private MoveGeneration moveGeneration = new MoveGeneration();
  private BoardEvaluator eval = new SimpleEvaluator(moveGeneration);
  private ABSearch search = new ABSearch(moveGeneration, eval);
  private Move[] availableMoves = Move.createMoves(100);
  private IterativeSearch iterativeSearch = new IterativeSearch(search, moveGeneration, eval);
  private Thread searchThread;
  private int lastScore = 0;

  private Board gameBoard;

  /**
   * Jin's main frame.
   */

  private static JinFrame mainFrame;

  /**
   * Our listener manager.
   */

  private FreechessJinListenerManager listenerManager = new FreechessJinListenerManager(this);

  /**
   * A hashtable mapping command names to CommandHandlers.
   */

  private Hashtable commandHandlers = new Hashtable();


  /**
   * Jin's properties.
   */

  private static Properties jinProps = new Properties();
  private int thinkCommentIndex;
  private int lastGameCount;
  private String twitterUser;
  private String twitterPassword;
  private boolean foundMate;
  private boolean talkedMate;

  class TwitterStatus {
    public Game currentGame = null;
    public Game lastGame = null;
    public int gameCount;
    public int wins;
    public int losses;
    public int draws;
    public int incomplete;

    public boolean isPlaying() {
      return currentGame != null;
    }

    public Game getGame() {
      return isPlaying() ? currentGame : lastGame;
    }
  }

  private TwitterStatus twitterStatus = new TwitterStatus();


  /**
   * The user's properties. The 'user' here isn't the chess server account/user,
   * but the operating system user.
   */

  private static Properties userProps = new Properties();
  private Twitter twitter;

  /**
   * The main method, duh :-). Creates a new Bot and makes it connect to
   * the server.
   */

  public static void main(String[] args) {
    String hostname = args[0];
    int port = Integer.parseInt(args[1]);
    String username = args[2];
    String password = args[3];
    String owner = args[4];
    String twitterUser = args.length > 5 ? args[5] : null;
    String twitterPassword = args.length > 6 ? args[6] : null;

    try {
      Bot bot = new Bot(hostname, port, username, password, owner, twitterUser, twitterPassword);
      bot.connectAndLogin();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(0);
    }
  }

  /**
   * Creates a new ServerBot which will connect to the given hostname on the
   * given port and will use the given account and password.
   */

  public Bot(String hostname, int port, String username, String password, String owner, String twitterUser, String twitterPassword) {
    super(hostname, port, username, password);


    this.owner = owner;
    this.twitterUser = twitterUser;
    this.twitterPassword = twitterPassword;
    //setDGState(Datagram.DG_PERSONAL_TELL, true); // This lets people talk to us.

    getJinListenerManager().addGameListener(this);
  }


  public void onLogin() {
    setStyle(12);
    super.onLogin();

    if (autoseek) {
      seek();
    }

    twitter = twitterUser != null && twitterUser.length() > 0 ? new Twitter() : null;
    try {
      File configInfo = new File("twitter_Auth.cfg");
      Scanner fileReader = new Scanner(configInfo);
      String consumerKey = fileReader.nextLine().trim();
      String consumerSecret = fileReader.nextLine().trim();
      String accessToken = fileReader.nextLine().trim();
      String tokenSecret = fileReader.nextLine().trim();
      String pin = fileReader.nextLine().trim();

      twitter.setOAuthConsumer(consumerKey, consumerSecret);
      twitter.setOAuthAccessToken(new AccessToken(accessToken, tokenSecret));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

  }

  /**
   * Returns the SeekJinListenerManager via which you can register and
   * unregister SeekListeners.
   */
  /**
   * Writes out usage information into the standard error stream.
   */

  private static void showUsage() {
    System.out.println("Usage: ");
    System.out.println("  java " + Bot.class.getName() + " hostname port username password");
  }

  private class SearchThread implements Runnable {
    private JinConnection connection;
    private Searcher searcher;
    private Board searchBoard;

    public SearchThread(JinConnection connection, Searcher searcher, Board searchBoard) {
      this.connection = connection;
      this.searcher = searcher;
      this.searchBoard = searchBoard;
    }

    public void run() {
      int score = searcher.search(searchBoard, 50);

      SearchStats stats = search.getStats();
      if (stats.nodes < 100 && score == 0) {
        connection.sendCommand("draw");
        connection.sendCommand("tell " + owner + " Detected Draw");
      }

      if (tellOwner) connection.sendCommand("tell " + owner + " s: " + formatScore(score));
      if (tellOwner) connection.sendCommand("tell " + owner + " Moves: " + Move.toString(searcher.getPV()));
      if (tellOwner) {
        connection.sendCommand("tell " + owner + " Stats: " + stats);
      }
      connection = null;
    }


    protected void finalize() throws Throwable {
      System.err.println("finalizing searcher thread");
    }
  }

  private class SearchTimerThread implements Runnable {
    private JinConnection connection;
    private Searcher searcher;
    private long maxTime;
    private String fen;

    public SearchTimerThread(JinConnection connection, IterativeSearch searcher, long maxTime, String fen) {
      this.connection = connection;
      this.searcher = searcher;
      this.maxTime = maxTime;
      this.fen = fen;
    }

    public void run() {
      long start = System.currentTimeMillis();
      long end = System.currentTimeMillis();
      boolean extended = false;
      boolean spoken = false;
      while ((end - start <= maxTime)) {
        if (maxTime > 2000) {
          try {
            Thread.sleep(maxTime / 4);
          }
          catch (InterruptedException e) {

          }
        } else {
          try {
            Thread.sleep(maxTime + 1);
          }
          catch (InterruptedException e) {

          }
        }

        Move pvMove = searcher.getPV()[0];
        int score = pvMove.score;

        if (maxTime > 700) {
          if (maxTime > 10 && score < lastScore - 45 && score > -Searcher.MATE + 100) {
            lastScore = searcher.getPV()[0].score;
            maxTime += maxTime / 2;
            String thinkComment = getThinkComment();
            if (tellOwner) {
              connection.sendCommand("tell " + owner + " " + thinkComment);
            }
            boolean playingWhite = twitterStatus.getGame().getWhiteName().equals(getUsername());
            String oppName = !playingWhite ? twitterStatus.getGame().getWhiteName() : twitterStatus.getGame().getBlackName();
            connection.sendCommand("kib " + oppName + " - " + thinkComment);
            extended = true;
          }
        }

        if (!spoken) {
          if (score > lastScore + 10000) {
            connection.sendCommand("kib Hee-Haw (" + formatScore(score) + ") - " + Move.toString(searcher.getPV()));
            spoken = true;
          } else if (score > lastScore + 900) {
            connection.sendCommand("kib Zoiks! (" + formatScore(score) + ") - " + Move.toString(searcher.getPV()));
            spoken = true;
          } else if (score > lastScore + 300) {
            connection.sendCommand("kib Ruh roh... (" + formatScore(score) + ") - " + Move.toString(searcher.getPV()));
            spoken = true;
          }
        }

        if (Math.abs(score) < Searcher.MATE &&
                Math.abs(score) > Searcher.MATE - 300) {
          System.err.println("Found Mate");
          if (score > Searcher.MATE - 300) {
            foundMate = true;
          }
          break;
        }

        System.err.println("Checking on search (" + lastScore + " -> " + score + ")");
        end = System.currentTimeMillis();
      }
      System.err.println("Stopping Search");
      lastScore = searcher.getPV()[0].score;
      connection.sendCommand(searcher.getPV()[0].toFICSString());
      if (foundMate) {
        maybeTweetMatePuzzle();
      }
      searcher.stop();
    }

    private void maybeTweetMatePuzzle() {
      if (!talkedMate) {
        talkedMate = true;
        Move[] pv = searcher.getPV();
        int movesToMate = 1;
        int color = pv[0].moved.color;
        int movedType = pv[0].moved.type;
        int movedType2 = pv[1].moved.type;
        int complexity = -Math.abs(eval.scorePosition(gameBoard, 0, 0)) / 900;
        System.err.println("MatePV: " + Move.toString(pv));

        for (int i = 0; i < 100 && pv[i].moved != null; i++) {
          if (pv[i].moved.color == color) {
            if (pv[i].promoteTo == -1) {
              if (pv[i].moved.type != Piece.QUEEN && pv[i].moved.type != Piece.ROOK) {
                if (movedType != pv[i].moved.type) {
                  complexity += 1;
                  movedType = pv[i].moved.type;
                }
              } else if (pv[i].taken == null) {
                complexity += 1;
              } else if (!pv[i].check) {
                complexity += 1;
              }
            }
          } else {
            if (pv[i].taken != null) {
              complexity += 1;
            } else if (movedType2 != pv[i].moved.type) {
              complexity += 1;
              movedType2 = pv[i].moved.type;
            }
          }
          movesToMate++;
        }
        movesToMate /= 2;

        String boardURL = "http://www.eddins.net/steve/chess/ChessImager/ChessImager.php?fen=" + fen.substring(0, fen.indexOf(" "));
        String tinyUrl = getTinyUrl(boardURL);


        if (tinyUrl != null && complexity > 0 && movesToMate > 2) {
          // New DonkeyFactory Chess position
          // Tweet
          System.err.println("Tiny URL: " + tinyUrl);
          System.err.println("Full URL: " + boardURL);
          System.err.println((color == 1 ? "White" : "Black") + " mates in " + movesToMate + " (DF-" + complexity + ") -> " + tinyUrl + " @ me the solution!  #new #DonkeyFactory #chess #puzzle");
          tweet((color == 1 ? "White" : "Black") + " mates in " + movesToMate + " (DF-" + complexity + ") -> " + tinyUrl + " @ me the solution!  #donkeyfactory #chess");

          // tweet
          connection.sendCommand("say Thank You! You helped me create a new DonkeyFactory chess puzzle (Mate in " + movesToMate + ", Complexity: " + complexity + ")");
          connection.sendCommand("say http://twitter.com/donkeyfactory");
        } else {
          System.err.println/*tweet*/("(DF-" + complexity + ")-[" + dateFormat.format(new Date()) + "] I found Mate in " + movesToMate + " -> " + boardURL + " #donkeyfactory #chess @ me the solution!");
        }
      }
    }

    private String getThinkComment() {
      String thinkComment;
      switch (thinkCommentIndex) {
        case 0: {
          thinkComment = "Let me look at this position for " + (maxTime / 2) + " more milliseconds (" + "you may wish i'd tell u what i'm thinking..." + ")";
          thinkCommentIndex++;
          break;
        }
        case 1: {
          thinkComment = "Gimme " + (maxTime / 2) + " milliseconds...this line looks interesting: " + "you may wish i'd tell u what i'm thinking...";
          thinkCommentIndex++;
          break;
        }
        case 2: {
          thinkComment = "Please allow me to concentrate for just " + (maxTime / 2) + " more milliseconds (" + "you may wish i'd tell u what i'm thinking..." + ")";
          thinkCommentIndex++;
          break;
        }
        case 3: {
          thinkComment = "Hmmm... I'm gonna take an extra " + (maxTime / 2) + " milliseconds to think - this line looks interesting: " + "you may wish i'd tell u what i'm thinking...";
          thinkCommentIndex++;
          break;
        }
        case 4: {
          thinkComment = "Interesting...I'll need " + (maxTime / 2) + " milliseconds more time to ponder this (" + "you may wish i'd tell u what i'm thinking..." + ")";
          thinkCommentIndex++;
          break;
        }
        case 5: {
          thinkComment = "I'll need to think on this for " + (maxTime / 2) + " milliseconds longer. (" + "you may wish i'd tell u what i'm thinking..." + ")";
          thinkCommentIndex = 0;
          break;
        }
        default: {
          thinkComment = "You may have something there, perhaps continuing with " + "you may wish i'd tell u what i'm thinking...";
          thinkCommentIndex = 0;
        }
      }
      return thinkComment;
    }

    protected void finalize() throws Throwable {
      System.err.println("finalizing timer thread");
    }
  }

  public void gameStarted(GameStartEvent evt) {
    System.err.println("game started");
    if (tellOwner) evt.getConnection().sendCommand("tell " + owner + " Game Started: " + evt.getGame().getID());
    evt.getConnection().sendCommand("say Heee Haw!!");
    Integer gameNumber = findMyGame();
    JinFreechessConnection.InternalGameData gameData =
            (JinFreechessConnection.InternalGameData) ongoingGamesData.get(gameNumber);

    search = new ABSearch(moveGeneration, eval);
    iterativeSearch = new IterativeSearch(search, moveGeneration, eval);

    gameBoard = new Board();
    //((SimpleEvaluator)eval).pawnHash.clear();
    gameBoard.setEPDPosition(evt.getGame().getInitialPosition().getFEN());
    System.err.println("Board: " + evt.getGame().getInitialPosition().getFEN());

    if (gameData != null && gameData.boardData != null) {
      gameBoard.stats.whiteKingsideRookMoves = gameData.boardData.canWhiteCastleKingside() ? 0 : 1;
      gameBoard.stats.whiteQueensideRookMoves = gameData.boardData.canWhiteCastleQueenside() ? 0 : 1;
      gameBoard.stats.blackKingsideRookMoves = gameData.boardData.canBlackCastleKingside() ? 0 : 1;
      gameBoard.stats.blackQueensideRookMoves = gameData.boardData.canBlackCastleQueenside() ? 0 : 1;
    }

    twitterStatus.currentGame = evt.getGame();
    talkedMate = false;
    foundMate = false;
  }

  public void gameEnded(GameEndEvent evt) {
    twitterStatus.gameCount++;
    if (autoseek) {
      sendCommand("rem");
      seek();
    }

    twitterStatus.currentGame = null;
    Game lastGame = evt.getGame();
    twitterStatus.lastGame = lastGame;

    boolean playingWhite = lastGame.getWhiteName().equals(getUsername());
    int myRating = playingWhite ? lastGame.getWhiteRating() : lastGame.getBlackRating();
    int oppRating = !playingWhite ? lastGame.getWhiteRating() : lastGame.getBlackRating();
    String oppName = !playingWhite ? lastGame.getWhiteName() : lastGame.getBlackName();

    boolean iWin = ((lastGame.getResult() == Game.WHITE_WINS && playingWhite) || (lastGame.getResult() == Game.BLACK_WINS && !playingWhite));
    boolean iLost = ((lastGame.getResult() == Game.WHITE_WINS && !playingWhite) || (lastGame.getResult() == Game.BLACK_WINS && playingWhite));

    if (iWin) {
      twitterStatus.wins++;

      if (oppRating - myRating > 50) {
        tweetGreatVictory(oppName, oppRating, myRating);
      }
    } else if (iLost) {
      twitterStatus.losses++;
      if (oppRating < myRating - 200) {
        tweetStunningDefeat(oppName, oppRating, myRating);
      }
    } else if (lastGame.getResult() == Game.DRAW) {
      twitterStatus.draws++;
    } else {
      twitterStatus.incomplete++;
    }
    if (twitterStatus.gameCount > lastGameCount + 9) {
      lastGameCount = twitterStatus.gameCount;
      tweetCurrentStatus();
    }

    System.err.println("Stopping search on game end");
    iterativeSearch.stop();
    if (searchThread != null) {
      try {
        searchThread.join();
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    System.gc();
  }

  public void moveMade(MoveMadeEvent evt) {
    System.err.println("move made");
  }

  String currentSearchEPD = "";

  public void positionChanged(PositionChangedEvent evt) {
    int lastScore;
    System.err.println("position changed");

    Integer gameNumber = findMyGame();
    JinFreechessConnection.InternalGameData gameData =
            (JinFreechessConnection.InternalGameData) ongoingGamesData.get(gameNumber);
    Move move = makeGameMovesOnBoard(gameData, gameBoard);

    if (gameBoard.isDraw()) {
      if (tellOwner) sendCommand("tell " + owner + " Claiming draw");
      evt.getConnection().sendCommand("draw");
    }

//    if(move != null) evt.getConnection().sendCommand("tell " + owner + " board move: " + move.toString());
    if (evt.getGame().isUserAllowedToMovePieces(evt.getPosition().getCurrentPlayer()) &&
            (!currentSearchEPD.equals(evt.getGame().getInitialPosition().getFEN()) || evt.getGame().getPliesSinceStart() == 0)) {
      if (!iterativeSearch.isDone()) {
        System.err.println("Waiting for search...");
        try {
          searchThread.join();
        }
        catch (InterruptedException e) {
          e.printStackTrace();
        }
        System.err.println("Search complete.");
      }

      Board searchBoard = new Board();
//      searchBoard.setEPDPosition(gameData.boardData.getBoardFEN());

      currentSearchEPD = evt.getGame().getInitialPosition().getFEN();
      searchBoard.setEPDPosition(evt.getGame().getInitialPosition().getFEN());
      searchBoard.turn = evt.getGame().getUserPlayer().isWhite() ? 1 : 0;
      searchBoard.stats = gameBoard.stats;
      searchBoard.stats.whiteKingsideRookMoves = gameData.boardData.canWhiteCastleKingside() ? 0 : 1;
      searchBoard.stats.whiteQueensideRookMoves = gameData.boardData.canWhiteCastleQueenside() ? 0 : 1;
      searchBoard.stats.blackKingsideRookMoves = gameData.boardData.canBlackCastleKingside() ? 0 : 1;
      searchBoard.stats.blackQueensideRookMoves = gameData.boardData.canBlackCastleQueenside() ? 0 : 1;
      searchBoard.repetitionTable = gameBoard.repetitionTable;
      searchBoard.moveIndex = gameBoard.moveIndex;

      for (int t = 0; t < 128; t++) {
        if (gameBoard.boardSquares[t] == null) {
          continue;
        }
        System.arraycopy(
                gameBoard.boardSquares[t].enPassentInfo,
                0,
                searchBoard.boardSquares[t].enPassentInfo,
                0,
                searchBoard.boardSquares[t].enPassentInfo.length
        );
      }

//      searchBoard.moveHistory = gameBoard.moveHistory;
/*
      searchBoard.hash1 = gameBoard.hash1;
      searchBoard.pawnHash = gameBoard.pawnHash;
*/

      iterativeSearch.reset();

      String fen = gameData.boardData.getBoardFEN();

      System.err.println("MoveIndex: " + searchBoard.moveIndex);
      System.err.println("GB Approaching Draw: " + gameBoard.isApproachingDraw());
      System.err.println("SB Approaching Draw: " + searchBoard.isApproachingDraw());
      System.err.println("Board: " + fen);
      System.err.println("Stats: " + searchBoard.stats);

      int score = eval.scorePosition(searchBoard, -Searcher.INFINITY, Searcher.INFINITY);

      System.err.println("Searching (" + formatScore(score) + ") ...");
      System.err.println(searchBoard.toString());
      searchThread = new Thread(new SearchThread(evt.getConnection(), iterativeSearch, searchBoard));
      searchThread.setPriority(6);
      searchThread.start();
      long maxTime = getTimeForMove(evt, gameData);
      Thread searchTimerThread = new Thread(new SearchTimerThread(evt.getConnection(), iterativeSearch, maxTime, fen));
      searchTimerThread.setPriority(7);
      searchTimerThread.start();
    } else {
      System.err.println("Waiting for opponent...");
//      System.gc();
    }
  }

  private Move makeGameMovesOnBoard(InternalGameData gameData, Board board) {
    if (gameData.moveList.size() == 0) {
      return null;
    }
    free.chess.Move move = (free.chess.Move) gameData.moveList.lastElement();
    int toSquareIndex = (move.getEndingSquare().getRank() * 8) + move.getEndingSquare().getFile();
    int fromSquareIndex = (move.getStartingSquare().getRank() * 8) + move.getStartingSquare().getFile();

    try {
      moveGeneration.generateFullMoves(availableMoves, board);
    }
    catch (Exception e) {
      return null;
    }

    Move actualMove = null;
    for (Move possibleMove : availableMoves) {
      if (possibleMove.fromSquare == null) {
        break;
      }
      if (possibleMove.fromSquare.index64 == fromSquareIndex &&
              possibleMove.toSquare.index64 == toSquareIndex) {
        if (move.getMoveString().indexOf("=") > -1) {
          char promoteTo = move.getMoveString().toLowerCase().substring(move.getMoveString().indexOf("=") + 1).charAt(0);
          switch (promoteTo) {
            case 'n': {
              if (possibleMove.promoteTo == Piece.KNIGHT) {
                actualMove = possibleMove;
              }
              break;
            }
            case 'b': {
              if (possibleMove.promoteTo == Piece.BISHOP) {
                actualMove = possibleMove;
              }
              break;
            }
            case 'r': {
              if (possibleMove.promoteTo == Piece.ROOK) {
                actualMove = possibleMove;
              }
              break;
            }
            case 'q': {
              if (possibleMove.promoteTo == Piece.QUEEN) {
                actualMove = possibleMove;
              }
              break;
            }
          }
        } else {
          actualMove = possibleMove;
          break;
        }
      }
    }
    if (actualMove != null && actualMove.moved != null) {
      board.make(actualMove);
/*
      if(actualMove.moved.type == Piece.PAWN || actualMove.taken != null)
      {
        board.repetitionTable[board.moveIndex] = board.moveIndex;
      }
*/
    }
    return actualMove;
  }

  private long getTimeForMove(PositionChangedEvent evt, InternalGameData gameData) {
    int timeLeft = (evt.getGame().getUserPlayer().isWhite() ?
            gameData.boardData.getWhiteTime() :
            gameData.boardData.getBlackTime());

    long maxTime;
    if (gameData.boardData.getNextMoveNumber() < 2) {
      maxTime = timeLeft / 36;
    } else if (gameData.boardData.getNextMoveNumber() < 12) {
      maxTime = timeLeft / 32;
    } else if (gameData.boardData.getNextMoveNumber() > 30 && timeLeft < 5000) {
      maxTime = timeLeft / 34;
    }
/*
    else if(timeLeft < 15000 && timeLeft > 10000)
    {
      maxTime = 1000;
    }
*/
    else {
      maxTime = timeLeft / 29;
    }

    System.err.println("Time Left: " + timeLeft);
    System.err.println("MaxTime: " + maxTime);
    return maxTime;
  }

  public void takebackOccurred(TakebackEvent evt) {
    evt.getConnection().sendCommand("refresh");
  }

  public void illegalMoveAttempted(IllegalMoveEvent evt) {
    evt.getConnection().sendCommand("refresh");
  }

  public void clockAdjusted(ClockAdjustmentEvent evt) {
  }

  public void boardFlipped(BoardFlipEvent evt) {
  }

  public void tweet(String tweet) {
    if (twitter == null) return;
    try {
      twitter.updateStatus(tweet);
      sendCommand("tell " + owner + " tweet complete.");
    } catch (TwitterException e) {
      sendCommand("tell " + owner + " tweet failed.");
      e.printStackTrace();
    }
  }

  public void tweetStunningDefeat(String winnersName, int winnersRating, int myRating) {
    tweet("Defeat - I (" + myRating + ") was beaten by " + winnersName + " (" + winnersRating + ") :(");
  }

  public void tweetGreatVictory(String winnersName, int winnersRating, int myRating) {
    tweet("Victory - I (" + myRating + ") beat " + winnersName + " (" + winnersRating + ")!");
  }

  public void tweetCurrentStatus() {
    Game game = twitterStatus.getGame();
    if (game != null) {
      boolean playingWhite = game.getWhiteName().equals(getUsername());
      int myRating = playingWhite ? game.getWhiteRating() : game.getBlackRating();
      tweet(myRating + " - My W/D/L is " + twitterStatus.wins + "-" + twitterStatus.draws + "-" + twitterStatus.losses + " for the past " + twitterStatus.gameCount + " games (" + twitterStatus.incomplete + " games were incomplete).");
    }
  }

  public void tweetCurrentGameStatus() {
    Game game = twitterStatus.getGame();
    if (game != null) {
      boolean playingWhite = game.getWhiteName().equals(getUsername());
      int myRating = playingWhite ? game.getWhiteRating() : game.getBlackRating();
      int oppRating = !playingWhite ? game.getWhiteRating() : game.getBlackRating();
      String oppName = !playingWhite ? game.getWhiteName() : game.getBlackName();

      String play = "played";
      String scoreStatement = "";
      if (twitterStatus.isPlaying()) {

        String score = formatScore(lastScore);
        play = "am playing";
        if (lastScore == 0) {
          scoreStatement = " and I think its an even game";
        }
        if (Math.abs(lastScore) > Searcher.MATE - 300 && Math.abs(lastScore) < Searcher.MATE) {
          scoreStatement = " and I see " + score;
        } else {
          scoreStatement = " and I think I am " + (lastScore > 0 ? "ahead" : "behind") + " by " + score + " pawns ";
        }
        tweet("DonkeyFactory: (" + myRating + ") I " + play + " " + oppName + " (" + oppRating + ") " + scoreStatement);
      }
    }
  }

  private String formatScore(int score) {

    if (score > Searcher.MATE - 300) {
      int mateDistance = (Searcher.MATE - score + 1) / 2;
      return "+MATE in " + mateDistance;
    } else if (score < -Searcher.MATE + 300) {
      int mateDistance = (Searcher.MATE + score) / 2;
      return "-MATE in " + mateDistance;
    }

    return (score > 0 ? "+" : "") + (score / 100D);
  }


  protected void processLine(String line) {
    if (line.indexOf("(adjourned)") > -1) {
      sendCommand("accept");
    }
    super.processLine(line);
  }

  /**
   * Registers a CommandHandler for the given command. All commands registered
   * this way are case insensitive. There can be only one command handler per
   * command, so this method will throw an IllegalArgumentException if you try to
   * register a CommandHandler for a command for which a CommandHandler was
   * already registered.
   */

  public void registerCommandHandler(String command, CommandHandler commandHandler) {
    command = command.toLowerCase();
    CommandHandler oldCommandHandler = (CommandHandler) commandHandlers.put(command, commandHandler);
    if (oldCommandHandler != null) {
      commandHandlers.put(command, oldCommandHandler);
      throw new IllegalArgumentException("Unable to register another CommandHandler for command: " + command);
    }
  }


  /**
   * Sends the given message to the given player as a personal tell.
   */

  public void sendTell(String player, String message) {
    sendCommand("tell " + player + " " + message);
  }


  /**
   * Processes personal tells. If the tell type is TELL, this method parses the
   * message and delegates to processCommand. Otherwise, the tell is ignored.
   */

  protected void processPersonalTell(String playerName, String titles, String message, int tellType) {
/*
    if (tellType!=TELL)
      return;

*/
    StringTokenizer tokenizer = new StringTokenizer(message, " \"\'", true);
    String[] tokens = new String[tokenizer.countTokens()];
    int numTokens = 0;

    try {
      while (tokenizer.hasMoreTokens()) {
        String token = tokenizer.nextToken();
        if (token.equals("\"") || token.equals("\'")) {
          tokens[numTokens++] = tokenizer.nextToken(token);
          tokenizer.nextToken(" \"\'"); // Get rid of the string terminating quote.
        } else if (!token.equals(" "))
          tokens[numTokens++] = token;
      }
    } catch (NoSuchElementException e) {
      sendTell(playerName, "Unterminated string literal");
      return;
    }

    if (numTokens == 0) {
      sendTell(playerName, "You must specify a command");
      return;
    }

    String issuedCommand = tokens[0];
    String[] args = new String[numTokens - 1];
    System.arraycopy(tokens, 1, args, 0, args.length);

    CommandEvent command = new CommandEvent(this, issuedCommand, args, message, playerName, titles);
    processCommand(command);
  }


  /**
   * Asks the appropriate CommandHandler to handle the command.
   */

  protected void processCommand(CommandEvent command) {
    String issuedCommand = command.getCommand();
    CommandHandler commandHandler = (CommandHandler) commandHandlers.get(issuedCommand.toLowerCase());
    if (commandHandler == null)
      sendTell(command.getPlayerName(), "Unknown command: " + issuedCommand);
    else
      commandHandler.handleCommand(command);
  }

  private void seek() {
    sendCommand("seek 1 0 r f");
    sendCommand("seek 3 0 r f");
    sendCommand("seek 5 0 r f");
  }

  protected boolean processPersonalTell(String username, String titles, String message) {
    super.processPersonalTell(username, titles, message);

    if (username.equals(owner)) {
      if (message.indexOf("tellme") > -1) {
        tellOwner = !tellOwner;
        sendCommand("tell " + owner + " tellme: " + tellOwner);
      } else if (message.indexOf("test") > -1) {
        sendCommand("tell " + owner + " challenging testdonkeyfactory");
        sendCommand("ma testdonkeyfactory 3 0 u");
      } else if (message.indexOf("tweet") > -1) {
        sendCommand("tell " + owner + " tweeting '" + message.substring(6) + "'");
        try {
          twitter.updateStatus(message.substring(6));
        } catch (TwitterException e) {
          e.printStackTrace();
        }
      } else if (message.indexOf("seek") > -1) {
        autoseek = !autoseek;
        sendCommand("tell " + owner + " autoseek: " + autoseek);
        if (autoseek) {
          seek();
        }
      } else if (message.indexOf("tweet") > -1) {
        tweetCurrentGameStatus();
      } else {
        sendCommand(message);
      }
    }
    return true;
  }

  public static String getTinyUrl(String fullUrl) {
    HttpClient httpclient = new HttpClient();

    try {
      Response response = httpclient.get("http://tinyurl.com/api-create.php?url=" + fullUrl);
      return response.asString();
    }
    catch (TwitterException e) {
      e.printStackTrace();
    }
    return null;
  }
}
