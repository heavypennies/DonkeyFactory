/**
 * Jin - a chess client for internet chess servers.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2002 Alexander Maryanovsky.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package free.jin.chessclub;

import free.jin.*;
import free.jin.event.*;
import free.chess.*;
import java.io.*;
import java.util.*;
import free.chess.variants.NoCastlingVariant;
import free.chess.variants.kriegspiel.Kriegspiel;
import free.chess.variants.shuffleboth.ShuffleBoth;
import free.chess.variants.fischerrandom.FischerRandom;
import free.chess.variants.giveaway.Giveaway;
import free.chess.variants.atomic.Atomic;
import free.chessclub.ChessclubConnection;
import free.chessclub.MoveStruct;
import free.chessclub.level2.Datagram;
import free.jin.chessclub.event.CircleEvent;
import free.jin.chessclub.event.ArrowEvent;
import free.jin.chessclub.event.ChessEventEvent;
import java.net.Socket;
import javax.swing.SwingUtilities;
import java.lang.reflect.Array;



/**
 * An implementation of the JinConnection interface (and several subinterfaces) 
 * for the chessclub.com server.
 */

public class JinChessclubConnection extends ChessclubConnection implements JinConnection, 
    SeekJinConnection, GameListJinConnection, PGNJinConnection{



  /**
   * Our listener manager
   */

  private final ChessclubJinListenerManager listenerManager = new ChessclubJinListenerManager(this);




 
  /**
   * Creates a new JinChessclubConnection with the given hostname, port, username
   * and password.
   *
   * @param hostname The name of the host to which to connect.
   * @param port The port on which to connect.
   * @param username The name of the account to log on with.
   * @param password The password of the account to log on with.
   */

  public JinChessclubConnection(String hostname, int port, String username, String password){
    super(hostname, port, username, password, System.out);

    setInterface(Jin.getInterfaceName());
  }




  /**
   * Returns the ChessclubJinListenerManager.
   */

  public ChessclubJinListenerManager getChessclubJinListenerManager(){
    return listenerManager;
  }




  /**
   * Returns the JinListenerManager.
   */

  public JinListenerManager getJinListenerManager(){
    return getChessclubJinListenerManager();
  }





  /**
   * Performs various on-login tasks. Also notifies all interested
   * ConnectionListeners that we've successfully logged in.
   */

  public void onLogin(){
    super.onLogin();

    listenerManager.fireConnectionEvent(new ConnectionEvent(this, ConnectionEvent.LOGGED_IN));

    sendCommand("set-quietly wrap 0");
    sendCommand("set-quietly bell 0");

    // Hack, currently, the server has a bug which causes it not to send us
    // the current event list even if we have turned DG_TOURNEY on at the login
    // line. Remove when Bert fixes it.
    if (isDGOn(Datagram.DG_TOURNEY)){
      sendCommand("set-2 "+Datagram.DG_TOURNEY+" 1");
    }
  }




  /**
   * Converts the given title to a displayable string. If the given title is empty,
   * returns an empty string, otherwise, returns the given string surrounded by
   * parentheses.
   */

  public static String displayableTitle(String title){
    if (title.length()==0)
      return "";
    else
      return "("+title+")";
  }



  /**
   * Given the title of a player determines whether he's a computer player.
   * The title must consist of single title strings separated by spaces.
   * Example: "WGM C *".
   */

  public static boolean isComputer(String title){
    StringTokenizer tokenizer = new StringTokenizer(title," ");
    while (tokenizer.hasMoreTokens()){
      String token = tokenizer.nextToken();
      if (token.equals("C"))
        return true;
    }

    return false;
  }




  /**
   * Given the title of a player determines whether he's an unrated (guest)
   * player. The title must consist of single title strings separated by spaces.
   * Example: "WGM C *".
   */

  public static boolean isUnrated(String title){
    StringTokenizer tokenizer = new StringTokenizer(title," ");
    while (tokenizer.hasMoreTokens()){
      String token = tokenizer.nextToken();
      if (token.equals("U"))
        return true;
    }

    return false;
  }




  /**
   * Overrides createSocket() to fire a ConnectionEvent specifying that the connection
   * was established when super.createSocket() returns (in the Event dispatching
   * thread of course).
   */

  protected Socket createSocket(String hostname, int port) throws IOException{
    Socket sock = new free.chessclub.timestamp.TimestampingSocket(hostname, port);

    execRunnable(new Runnable(){

      public void run(){
        listenerManager.fireConnectionEvent(new ConnectionEvent(JinChessclubConnection.this, ConnectionEvent.ESTABLISHED));
      }

    });

    return sock;
  }





  /**
   * Returns the WildVariant corresponding to the given wild number, or null
   * if that wild variant is not supported.
   */

  private WildVariant getVariant(int variantNumber){
    String variantName = getVariantName(variantNumber);
    if (variantName == null)           // Defend against the server not doing its
      variantName = "w"+variantNumber; // job properly (not sending the variant name).

    switch(variantNumber){
      case 0:  // Chess
        return Chess.getInstance();
      case 1:  // Shuffle both
        return ShuffleBoth.getInstance();
      case 16: // Kriegspiel
        return Kriegspiel.getInstance();
      case 22: // Fischer random
        return FischerRandom.getInstance();
      case 23: // Crazyhouse
        return null;
      case 24: // Bughouse
        return null;
      case 26: // Giveaway (Not ChesslikeGenericVariant because promotion to to king is allowed)
        return Giveaway.getInstance();
      case 27: // Atomic
        return Atomic.getInstance();
      case 28: // Shatranj
        return null;


     /*
       We can use the classic position to initialize all the variants which only differ from classic chess
       by their initial position because we know the server will have to send us the real initial position
       in a DG_POSITION_BEGIN datagram and then we'll use it to *really* initialize the position. The server
       *has* to send it because for most of these variants the initial position is somehow randomized so
       if the server won't tell us, we won't know the position. The server sends the initial position
       on non randomized variants too, according to the protocol (except for w0 of course). On a side note,
       the server may, according to the protocol documentation, send an empty FEN string in DG_POSITION_BEGIN,
       in which case we have to initialize the position to the classic initial position even if wild!=0.
     */

      case 2: // Shuffle mirror
      case 3: // Random mirror
      case 4: // Random shuffle (castling not allowed according to Kiebitz)
      case 5: // Reversed 
        return new NoCastlingVariant(Chess.INITIAL_POSITION_FEN, variantName);

      case 6: // Empty board
      case 7: // KPPP vs KPPP
      case 8: // Advanced pawns
      case 9:  // Two kings each (It seems that the castling rules are the same as in normal chess).
      case 10: // Pawn+move odds
      case 11: // Knight odds
      case 12: // Rook odds
      case 13: // Queen odds
      case 14: // Rook odds a3
      case 15: // KBN vs K
      case 17: // Loser's chess
      case 18: // Power chess
      case 19: // KNN vs KP
      case 20: // Loadgame
      case 21: // Thematic
      case 25: // Three checks
        return new ChesslikeGenericVariant(Chess.INITIAL_POSITION_FEN, variantName);
    }

    return null;
  }




  /**
   * This method informs the user that he tried to use (observe, play etc.)
   * a wild variant not supported by Jin. Please use this method when
   * appropriate instead of sending your own message.
   */

  protected void sendNotSupportedVariantMessage(int variantNumber){
    String variantName = getVariantName(variantNumber);
    if (variantName==null)
      variantName = "w"+variantNumber;
    processLine("This version of Jin does not support the wild variant ("+variantName+") and is thus unable to display the game.");
    processLine("Please activate the appropriate command to abort this game");
  }



  /**
   * Sends the "exit" command to the server.
   */

  public void exit(){
    sendCommand("exit");
  }






  /**
   * Overrides processDisconnection() to fire a ConnectionEvent specifying that
   * the connection was lost.
   */

  protected void processDisconnection(){
    listenerManager.fireConnectionEvent(new ConnectionEvent(this, ConnectionEvent.LOST));
  }





  /**
   * Processes a single line of plain text.
   */

  protected void processLine(String line){
    listenerManager.firePlainTextEvent(new PlainTextEvent(this, line));
  }




  /**
   * Creates and dispatches an appropriate ChatEvent to all registered
   * ChatListeners.
   */

  protected void processPersonalTell(String playername, String titles, String message, int tellType){
    String tellTypeString;
    switch (tellType){
      case TELL: tellTypeString = "tell"; break;
      case SAY: tellTypeString = "say"; break;
      case PTELL: tellTypeString = "ptell"; break;
      case QTELL: tellTypeString = "qtell"; break;
      case ATELL: tellTypeString = "atell"; break;
      default:
        return; // Ignore unknown types.
    }

    String title = displayableTitle(titles);

    ChatEvent evt = new ChatEvent(this, tellTypeString, playername, title, -1, message, null);
    listenerManager.fireChatEvent(evt);
  }




  /**
   * Creates and dispatches an appropriate ChatEvent to all registered
   * ChatListeners.
   */

  protected void processPersonalQTell(String name, String titles, String message){
    listenerManager.fireChatEvent(
      new ChatEvent(this, "qtell", name, displayableTitle(titles), -1, message, null));
  }






  /**
   * Creates and dispatches an appropriate ChatEvent to all registered
   * ChatListeners.
   */

  protected void processShout(String playerName, String titles, int shoutType, String message){
    String tellTypeString;
    switch (shoutType){
      case SHOUT: tellTypeString = "shout"; break;
      case I_SHOUT: tellTypeString = "ishout"; break;
      case SSHOUT: tellTypeString = "sshout"; break;
      case ANNOUNCEMENT: tellTypeString = "announcement"; break;
      default:
        return; // Ignore unknown types.
    }

    String title = displayableTitle(titles);

    ChatEvent evt = new ChatEvent(this, tellTypeString, playerName, title, -1, message, null);
    listenerManager.fireChatEvent(evt);
  }




  
  /**
   * Creates and dispatches an appropriate ChatEvent to all registered
   * ChatListeners.
   */

  protected void processChannelTell(int channel, String playerName, String titles, String message, int tellType){
    String tellTypeString;
    switch (tellType){
      case CHANNEL_TELL: tellTypeString = "channel-tell"; break;
      case CHANNEL_ATELL: tellTypeString = "channel-atell"; break;
      case CHANNEL_QTELL: tellTypeString = "channel-qtell"; break;
      default:
        return; // Ignore unknown types.
    }

    String title = displayableTitle(titles);

    ChatEvent evt = new ChatEvent(this, tellTypeString, playerName, title, -1, message, new Integer(channel));
    listenerManager.fireChatEvent(evt);
  }




  /**
   * Creates and dispatches an appropriate ChatEvent to all registered
   * ChatListeners.
   */

  protected void processChannelQTell(int channel, String name, String titles, String message){
    ChatEvent evt = new ChatEvent(this, "channel-qtell", name, displayableTitle(titles),
      -1, message, new Integer(channel));

    listenerManager.fireChatEvent(evt);
  }




  /**
   * Creates and dispatches an appropriate ChatEvent to all registered
   * ChatListeners.
   */

  protected void processKibitz(int gameNumber, String playerName, String titles, boolean isKibitz, String message){
    String tellTypeString = isKibitz ? "kibitz" : "whisper";

    String title = displayableTitle(titles);

    ChatEvent evt = new ChatEvent(this, tellTypeString, playerName, title, -1, message,
      new Integer(gameNumber));
    listenerManager.fireChatEvent(evt);
  }






  /**
   * This method is called by ChessclubJinListenerManager when the last
   * GameListener is removed.
   */

  void lastGameListenerRemoved(){
    // Why do we clear this? What if another listener registers?
//    gameNumbersToGameInfo.clear();
//    nonStartedGames.clear();
  }





  /**
   * A Hashtable mapping gameNumbers to GameInfo objects about ongoing games.
   */

  private final Hashtable gameNumbersToGameInfo = new Hashtable(1);
  




  /**
   * A Hashtable mapping game numbers to Hashtables containing game properties.
   * This is used for passing information between the various datagrams that
   * tell us a game started and the DG_POSITION_BEGIN datagram when we actually
   * fire the game started event.
   */

  private final Hashtable nonStartedGames = new Hashtable(1); 




  /**
   * A Hashtable mapping Game objects to Vectors of moves which were sent for
   * these games but the server didn't tell us yet whether the move is legal
   * or not.
   */

  private final Hashtable unechoedMoves = new Hashtable(1);




  /**
   * The amount of user played games.
   */

  private int userGamesCount = 0;




  /**
   * <P>Returns the GameInfo object for the given game number. This method 
   * throws a <code>NoSuchGameException</code> if there is no started game with
   * the given number. It is important to throw it here and to force the user to
   * catch the exception and handle it. This can always happen if the server 
   * thinks the game started, but the client doesn't (if the wild variant is 
   * not supported for example). What will happen in this case is that the 
   * datagrams will continue to arrive, but all the datagram processing code 
   * must ignore them. Forcing the datagram processing code to catch the 
   * exception ensures that such a case will be properly handled. The side
   * effect is that this method never returns null.
   * <P>Of course you can always ignore this method and access
   * <code>gameNumbersToGameInfo</code> directly.
   */

  private GameInfo getGameInfo(int gameNumber) throws NoSuchGameException{
    GameInfo gameInfo = (GameInfo)gameNumbersToGameInfo.get(new Integer(gameNumber));
    if (gameInfo == null)
      throw new NoSuchGameException();

    return gameInfo;
  }




  /**
   * Adds the specified <code>GameInfo</code> to the
   * <code>gameNumbersToGameInfo</code> hashtable.
   */

  private void addGameInfo(int gameNumber, GameInfo gameInfo){
    gameNumbersToGameInfo.put(new Integer(gameNumber), gameInfo);
    if (gameInfo.game.getGameType() == Game.MY_GAME)
      userGamesCount++;
  }




  /**
   * Removes the <code>GameInfo</code> of the specified game from the
   * <code>gameNumbersToGameInfo</code> hashtable.
   */

  private GameInfo removeGameInfo(int gameNumber){
    GameInfo gameInfo = (GameInfo)gameNumbersToGameInfo.remove(new Integer(gameNumber));
    if ((gameInfo != null) && (gameInfo.game.getGameType() == Game.MY_GAME))
      userGamesCount--;

    return gameInfo;
  }




  /**
   * Returns <code>true</code> if a game with the given number exists and is
   * a started game. Returns <code>false</code> otherwise.
   */

  private boolean gameExists(int gameNumber){
    return gameNumbersToGameInfo.containsKey(new Integer(gameNumber));
  }





  /**
   * Adds a Hashtable containing the given game information to the list of games
   * we haven't fired a game started event yet. This and the createGameFromNonStarted
   * methods are here essentially for hiding some of the ugliness of passing information
   * from the various DGs notifying of game start and the DG_POSITION_BEGIN arrival
   * when we actually fire the game started event.
   */

  private void addNonStartedGame(int gameType, int gameNumber, String whiteName, String blackName,
          WildVariant variant, String ratingCategoryString, boolean isRated, int whiteInitial, int whiteIncrement,
          int blackInitial, int blackIncrement, boolean isPlayedGame, String exString,
          int whiteRating, int blackRating, long gameID, String whiteTitles, String blackTitles,
          boolean isIrregularLegality, boolean isIrregularSemantics, boolean usesPlunkers,
          String fancyTimeControls){

    Hashtable gameProps = new Hashtable();
    gameProps.put("WhiteName", whiteName);
    gameProps.put("BlackName", blackName);
    gameProps.put("Variant", variant);
    gameProps.put("RatingCategoryString", ratingCategoryString);
    gameProps.put("IsRated", new Boolean(isRated));
    gameProps.put("WhiteInitial", new Integer(whiteInitial));
    gameProps.put("WhiteIncrement", new Integer(whiteIncrement));
    gameProps.put("BlackInitial", new Integer(blackInitial));
    gameProps.put("BlackIncrement", new Integer(blackIncrement));
    gameProps.put("IsPlayedGame", new Boolean(isPlayedGame));
    gameProps.put("WhiteRating", new Integer(whiteRating));
    gameProps.put("BlackRating", new Integer(blackRating));
    gameProps.put("WhiteTitles", displayableTitle(whiteTitles));
    gameProps.put("BlackTitles", displayableTitle(blackTitles));

    gameProps.put("GameType", new Integer(gameType));

    nonStartedGames.put(new Integer(gameNumber), gameProps);
  }




  /**
   * Returns true if there is a non-started game with the given gameNumber.
   */

  private boolean existsNonStarted(int gameNumber){
    return nonStartedGames.containsKey(new Integer(gameNumber));
  }




  /**
   * Returns the given property of a non started game with the given gameNumber.
   */

  private Object getPropertyForNonStarted(int gameNumber, String propertyName){
    Hashtable gameProps = (Hashtable)nonStartedGames.get(new Integer(gameNumber));    
    return gameProps.get(propertyName);
  }



  /**
   * Sets the value of the given property in the given non-started game to the
   * given value.
   */

  private void putPropertyForNonStarted(int gameNumber, String propertyName, Object propertyValue){
    Hashtable gameProps = (Hashtable)nonStartedGames.get(new Integer(gameNumber));    
    gameProps.put(propertyName, propertyValue);
  }





  /**
   * Creates a <code>Game</code> object from the properties saved by the
   * addNonStartedGame method and the given initial position. The Hashtable with
   * game information saved by the addNonStartedGame method is removed from the
   * list of games for whom a game started event hasn't been fired yet.
   */

  private Game createGameFromNonStarted(int gameNumber, Position initialPosition){
    
    Hashtable gameProps = (Hashtable)nonStartedGames.remove(new Integer(gameNumber));

    String whiteName = (String)gameProps.get("WhiteName");
    String blackName = (String)gameProps.get("BlackName");
    String ratingCategoryString = (String)gameProps.get("RatingCategoryString");
    boolean isRated = ((Boolean)gameProps.get("IsRated")).booleanValue();
    int whiteInitial = 1000*60*((Integer)gameProps.get("WhiteInitial")).intValue();
    int whiteIncrement = 1000*((Integer)gameProps.get("WhiteIncrement")).intValue();
    int blackInitial = 1000*60*((Integer)gameProps.get("BlackInitial")).intValue();
    int blackIncrement = 1000*((Integer)gameProps.get("BlackIncrement")).intValue();
    boolean isPlayedGame = ((Boolean)gameProps.get("IsPlayedGame")).booleanValue();
    int whiteRating = ((Integer)gameProps.get("WhiteRating")).intValue();
    int blackRating = ((Integer)gameProps.get("BlackRating")).intValue();
    String whiteTitles = (String)gameProps.get("WhiteTitles");
    String blackTitles = (String)gameProps.get("BlackTitles");
    int gameType = ((Integer)gameProps.get("GameType")).intValue();
    boolean isInitiallyFlipped = ((Boolean)gameProps.get("InitiallyFlipped")).booleanValue();

    Player userPlayer;
    if (gameType == Game.ISOLATED_BOARD)
      userPlayer = null;
    else if (gameType == Game.OBSERVED_GAME)
      userPlayer = null;
    else{ // MY_GAME
      String username = getUsername();
      if (isPlayedGame){
        if (whiteName.equals(username))
          userPlayer = Player.WHITE_PLAYER;
        else
          userPlayer = Player.BLACK_PLAYER;
      }
      else
        userPlayer = null;
    }

    return new Game(gameType, initialPosition, 0, whiteName, blackName, whiteInitial,
      whiteIncrement, blackInitial, blackIncrement, whiteRating, blackRating,
      String.valueOf(gameNumber), ratingCategoryString, isRated, isPlayedGame, whiteTitles,
      blackTitles, isInitiallyFlipped, userPlayer);
  }




  /**
   * Creates the game record for a game played by the user.
   */

  protected void processMyGameStarted(int gameNumber, String whiteName, String blackName,
      int variantNumber, String ratingCategoryString, boolean isRated, int whiteInitial, int whiteIncrement,
      int blackInitial, int blackIncrement, boolean isPlayedGame, String exString,
      int whiteRating, int blackRating, long gameID, String whiteTitles, String blackTitles,
      boolean isIrregularLegality, boolean isIrregularSemantics, boolean usesPlunkers,
      String fancyTimeControls){


    WildVariant variant = getVariant(variantNumber);
    if (variant==null){ // Not a supported variant.
      sendNotSupportedVariantMessage(variantNumber);
      return;
    }

    addNonStartedGame(Game.MY_GAME, gameNumber, whiteName, blackName, variant, ratingCategoryString, isRated,
      whiteInitial, whiteIncrement, blackInitial, blackIncrement, isPlayedGame, exString,
      whiteRating, blackRating, gameID, whiteTitles, blackTitles, isIrregularLegality, isIrregularSemantics,
      usesPlunkers, fancyTimeControls);
  }





  /**
   * Creates the game record for an observed game.
   */

  protected void processStartedObserving(int gameNumber, String whiteName, String blackName,
    int variantNumber, String ratingCategoryString, boolean isRated, int whiteInitial,
    int whiteIncrement, int blackInitial, int blackIncrement, boolean isPlayedGame,
    String exString, int whiteRating, int blackRating, long gameID, String whiteTitles,
    String blackTitles, boolean isIrregularLegality, boolean isIrregularSemantics,
    boolean usesPlunkers, String fancyTimeControls){

    WildVariant variant = getVariant(variantNumber);
    if (variant == null){ // Not a supported variant.
      sendNotSupportedVariantMessage(variantNumber);
      return;
    }

    addNonStartedGame(Game.OBSERVED_GAME, gameNumber, whiteName, blackName, variant, ratingCategoryString, isRated,
      whiteInitial, whiteIncrement, blackInitial, blackIncrement, isPlayedGame, exString,
      whiteRating, blackRating, gameID, whiteTitles, blackTitles, isIrregularLegality, isIrregularSemantics,
      usesPlunkers, fancyTimeControls);
  }



  /**
   * Creates the game record for an isolated board.
   */

  protected void processIsolatedBoard(int gameNumber, String whiteName, String blackName,
      int variantNumber, String ratingCategoryString, boolean isRated, int whiteInitial,
      int whiteIncrement, int blackInitial, int blackIncrement, boolean isPlayedGame,
      String exString, int whiteRating, int blackRating, long gameID, String whiteTitles,
      String blackTitles, boolean isIrregularLegality, boolean isIrregularSemantics, 
      boolean usesPlunkers, String fancyTimeControls){


    WildVariant variant = getVariant(variantNumber);
    if (variant==null){ // Not a supported variant.
      sendNotSupportedVariantMessage(variantNumber);
      return;
    }

    addNonStartedGame(Game.ISOLATED_BOARD, gameNumber, whiteName, blackName, variant, ratingCategoryString, isRated,
      whiteInitial, whiteIncrement, blackInitial, blackIncrement, isPlayedGame, exString,
      whiteRating, blackRating, gameID, whiteTitles, blackTitles, isIrregularLegality, isIrregularSemantics,
      usesPlunkers, fancyTimeControls);
  }




  /**
   * Gets called when a DG_MY_GAME_CHANGE datagram arrives. This method merely
   * calls updateGame() with the new game properties.
   */
  
  protected void processMyGameChange(int gameNumber, String whiteName, String blackName,
      int variantNumber, String ratingCategoryString, boolean isRated, int whiteInitial,
      int whiteIncrement, int blackInitial, int blackIncrement, boolean isPlayedGame,
      String exString, int whiteRating, int blackRating, long gameID, String whiteTitles,
      String blackTitles, boolean isIrregularLegality, boolean isIrregularSemantics,
      boolean usesPlunkers, String fancyTimeControls){


    try{
      GameInfo gameInfo = getGameInfo(gameNumber);
      Game game = gameInfo.game;

      updateGame(game.getGameType(), gameNumber, whiteName, blackName, ratingCategoryString,
        isRated, whiteInitial, whiteIncrement, blackInitial, blackIncrement,
        isPlayedGame, whiteRating, blackRating, String.valueOf(gameID), whiteTitles, blackTitles);
    } catch (NoSuchGameException e){}
  }





  /**
   * Gets called when a DG_MY_GAME_RESULT datagram arrives. This method checks
   * if becomesExamined is true and if so, calls updateGame with the game 
   * properties of the ended game, except for the "isPlayedGame" property, 
   * which now becomes false. Also sets the result of the game on the
   * <code>Game</code> object.
   */

  protected void processMyGameResult(int gameNumber, boolean becomesExamined,
      String gameResultCode, String scoreString, String descriptionString){

    try{
      GameInfo gameInfo = getGameInfo(gameNumber);
      Game game = gameInfo.game;

      try{
        int result;
        if ("1-0".equals(scoreString))
          result = Game.WHITE_WINS;
        else if ("0-1".equals(scoreString))
          result = Game.BLACK_WINS;
        else if ("1/2-1/2".equals(scoreString))
          result = Game.DRAW;
        else
          result = Game.UNKNOWN_RESULT;

        game.setResult(result);

        if (becomesExamined){
          // For our own games, we will get a DG_MY_RELATION_TO_GAME
          // where we will change the state of the game to nonplayed.
          if (game.getGameType() == Game.OBSERVED_GAME){
            updateGame(game.getGameType(), gameNumber, game.getWhiteName(), game.getBlackName(),
              game.getRatingCategoryString(), game.isRated(), game.getWhiteTime(),
              game.getWhiteInc(), game.getBlackTime(), game.getBlackInc(), false, // <-- the change
              game.getWhiteRating(), game.getBlackRating(), game.getID(), game.getWhiteTitles(),
              game.getBlackTitles());
          }

        }
        else if (game.getGameType() == Game.ISOLATED_BOARD){
          fireGameEvent(new GameEndEvent(this, game, result));
        }


      } catch (IllegalStateException e){
          e.printStackTrace();
        }
    } catch (NoSuchGameException e){}
  }







  /**
   * This method changes the settings of the given game to the given settings.
   * Since Game objects are immutable, it does this by stopping the current game
   * and then creating a new one and bringing it to the state of the old one.
   * This method only works for started games.
   */
  
  protected void updateGame(int gameType, int gameNumber, String whiteName, String blackName,
      String ratingCategoryString, boolean isRated, int whiteInitial, int whiteIncrement,
      int blackInitial, int blackIncrement, boolean isPlayedGame, int whiteRating, 
      int blackRating, Object gameID, String whiteTitles, String blackTitles){

    GameInfo gameInfo = removeGameInfo(gameNumber);
    Game game = gameInfo.game;

    int result = 
      (game.getResult() == Game.GAME_IN_PROGRESS) ? Game.UNKNOWN_RESULT : game.getResult();
    if (game.getResult() == Game.GAME_IN_PROGRESS) // Make sure the game doesn't remain in progress
      game.setResult(result);

    fireGameEvent(new GameEndEvent(this, game, result));

    Game newGame = new Game(gameType, game.getInitialPosition(), game.getPliesSinceStart(),
      whiteName, blackName, whiteInitial, whiteIncrement, blackInitial, blackIncrement,
      whiteRating, blackRating, String.valueOf(gameNumber), ratingCategoryString, isRated,
      isPlayedGame, whiteTitles, blackTitles, game.isBoardInitiallyFlipped(),
      game.getUserPlayer());

    GameInfo newGameInfo = new GameInfo(newGame, new Position(newGame.getInitialPosition()),
      gameInfo.numMovesToFollow);
    addGameInfo(gameNumber, newGameInfo);

    fireGameEvent(new GameStartEvent(this, newGame));

    Position position = newGameInfo.position;
    int numMoves = gameInfo.moves.size();
    for (int i = 0; i < numMoves; i++){
      Move move = (Move)gameInfo.moves.elementAt(i);

      position.makeMove(move);
      newGameInfo.moves.addElement(move);

      fireGameEvent(new MoveMadeEvent(this, newGame, move, false));
    }

    newGameInfo.isFlipped = gameInfo.isFlipped;
    fireGameEvent(new BoardFlipEvent(this, newGame, newGameInfo.isFlipped));    

    int whiteTime = gameInfo.getWhiteTime();
    if (gameInfo.isWhiteClockRunning())
      whiteTime -= (System.currentTimeMillis()-gameInfo.getWhiteTimestamp());
    newGameInfo.setWhiteClock(whiteTime, gameInfo.isWhiteClockRunning());
    fireGameEvent(new ClockAdjustmentEvent(this, newGame, Player.WHITE_PLAYER, whiteTime,
      newGameInfo.isWhiteClockRunning()));

    int blackTime = gameInfo.getBlackTime();
    if (gameInfo.isBlackClockRunning())
      blackTime -= (System.currentTimeMillis()-gameInfo.getBlackTimestamp());
    newGameInfo.setBlackClock(blackTime, gameInfo.isBlackClockRunning());
    fireGameEvent(new ClockAdjustmentEvent(this, newGame, Player.BLACK_PLAYER, blackTime,
      newGameInfo.isBlackClockRunning()));
  }




  
  /**
   * Fires a game start event for the game specified by <code>gameNumber</code>.
   */

  protected void processPositionBegin(int gameNumber, String initFEN, int numMovesToFollow){
    if (existsNonStarted(gameNumber)){
      WildVariant variant = (WildVariant)getPropertyForNonStarted(gameNumber, "Variant");
      Position pos = new Position(variant);
      if (!initFEN.equals(""))
        pos.setFEN(initFEN);

      Game newGame = createGameFromNonStarted(gameNumber, pos);
      GameInfo gameInfo = new GameInfo(newGame, new Position(pos), numMovesToFollow);
      addGameInfo(gameNumber, gameInfo);

      fireGameEvent(new GameStartEvent(this, newGame));
    }
    else{ // This can happen during an examined game on a "p@a2", "clearboard" and "loadgame" for example.
      try{
        GameInfo gameInfo = getGameInfo(gameNumber);
        Game game = gameInfo.game;

        Position newInitPos = new Position(game.getVariant());
        newInitPos.setFEN(initFEN);

        game.setInitialPosition(newInitPos, 0);
        gameInfo.moves.removeAllElements();
        gameInfo.position.copyFrom(game.getInitialPosition());
        gameInfo.numMovesToFollow = numMovesToFollow;

        fireGameEvent(new PositionChangedEvent(this, game, gameInfo.position));
      } catch (NoSuchGameException e){
          e.printStackTrace();
        }
    }

  }





  /**
   * If playerState is {@link ChessclubConnection#DOING_NOTHING}, fires the
   * appropriate GameEndEvent to all interested GameListeners.
   */

  protected void processMyRelationToGame(int gameNumber, int playerState){
    if (playerState == DOING_NOTHING){

      GameInfo gameInfo = removeGameInfo(gameNumber);

      // Game wasn't set up properly, probably because the wild variant is not supported.
      if (gameInfo == null) 
        return;

      Game game = gameInfo.game;
      unechoedMoves.remove(game);

      int result = 
        (game.getResult() == Game.GAME_IN_PROGRESS) ? Game.UNKNOWN_RESULT : game.getResult();

      // Make sure the game doesn't stay in progress...
      if (game.getResult() == Game.GAME_IN_PROGRESS) 
        game.setResult(result);

      fireGameEvent(new GameEndEvent(this, game, result));
    }
    else{
      int newGameType = (playerState == OBSERVING) ? Game.OBSERVED_GAME : Game.MY_GAME;

      if (gameExists(gameNumber)){
        try{
          GameInfo gameInfo = getGameInfo(gameNumber);
          Game game = gameInfo.game;

          boolean isPlayedGame = game.isPlayed();
          if (playerState == EXAMINING)
            isPlayedGame = false;

          if ((game.isPlayed() != isPlayedGame) || (newGameType != game.getGameType())){
            updateGame(newGameType, gameNumber, game.getWhiteName(), game.getBlackName(),
              game.getRatingCategoryString(), game.isRated(), game.getWhiteTime(),
              game.getWhiteInc(), game.getBlackTime(), game.getBlackInc(), isPlayedGame,
              game.getWhiteRating(), game.getBlackRating(), game.getID(), game.getWhiteTitles(),
              game.getBlackTitles());
          }
        } catch (NoSuchGameException e){
            throw new IllegalStateException("This isn't supposed to happen - we checked that the game exists");
          }
      }
      else{ // The game doesn't exist, either because it's a non started game or a game 
            // that wasn't set up properly (usually happens for non-supported variants).

        if (existsNonStarted(gameNumber)) // This way we know it's a non-started game.
          putPropertyForNonStarted(gameNumber, "GameType", new Integer(newGameType));
        else 
          return;
          // A datagram for a game that wasn't processed, probably 
          // because the variant is not supported.
      }
    }
  }




  /**
   * Fires the appropriate MoveEvent to all interested GameListeners.
   */

  protected void processSendMoves(int gameNumber, MoveStruct moveInfo){
    try{
      GameInfo gameInfo = getGameInfo(gameNumber);
      Game game = gameInfo.game;
      Position position = gameInfo.position;
      WildVariant variant = game.getVariant();
      Move move = parseWarrenSmith(moveInfo.smithMove, position, variant, moveInfo.algebraicMove); 

      position.makeMove(move);
      gameInfo.moves.addElement(move);

      boolean isNewMove = (moveInfo.variationCode != MoveStruct.MOVE_INITIAL) &&
                          (moveInfo.variationCode != MoveStruct.MOVE_FORWARD);

      fireGameEvent(new MoveMadeEvent(this, game, move, isNewMove));

      if (gameInfo.numMovesToFollow > 0){
        gameInfo.numMovesToFollow--;
      }
      else{
        Vector unechoedGameMoves = (Vector)unechoedMoves.get(game);
        if ((unechoedGameMoves != null) && (unechoedGameMoves.size() != 0)){ // Looks like it's our move.
          Move madeMove = (Move)unechoedGameMoves.elementAt(0);
          if (moveToString(game, move).equals(moveToString(game, madeMove))) // Same move.
            unechoedGameMoves.removeElementAt(0); 
        }
      }
    } catch (NoSuchGameException e){}
  }




  /**
   * Breaks the given move string (assuming it's in Smith Warren format) into
   * starting square, ending square and promotion target, and using WildVariant.parsePiece(String)
   * and WildVariant.createMove(Position, Square, Square, Piece) creates a Move object.
   */

  private Move parseWarrenSmith(String moveString, Position position, WildVariant variant, String moveSAN){
    if (variant.equals(Kriegspiel.getInstance()) && (moveString.indexOf("?") != -1)){
      if (moveString.equals("?")){ // Completely hidden
        return variant.createMove(position, null, null, null, moveSAN);
      }
      else{
        Square endSquare = Square.parseSquare(moveString.substring(2, 4));
        return variant.createMove(position, null, endSquare, null, moveSAN);
      }
    }
    else{
      char lastChar = moveString.charAt(moveString.length() - 1);
      if (lastChar == 'c') // Short castling
        return variant.createShortCastling(position);
      else if (lastChar == 'C') // Long castling
        return variant.createLongCastling(position);

      Square startSquare = Square.parseSquare(moveString.substring(0, 2));
      Square endSquare = Square.parseSquare(moveString.substring(2, 4));
      
      Piece promotionTarget = null;
      if ("NBRQK".indexOf(lastChar) != -1){
        // The 'K' can happen in Giveaway, where you can promote to a king
        String promotionTargetString = String.valueOf(moveString.charAt(moveString.length() - 1));
        if (position.getCurrentPlayer().isBlack())
          promotionTargetString = promotionTargetString.toLowerCase();
        promotionTarget = variant.parsePiece(promotionTargetString);
      }

      return variant.createMove(position, startSquare, endSquare, promotionTarget, moveSAN);
    }
  }




  /**
   * Fires a Takeback event.
   */

  protected void processBackward(int gameNumber, int backwardCount){
    try{
      GameInfo gameInfo = getGameInfo(gameNumber);
      Game game = gameInfo.game;
      Position pos = gameInfo.position;
      Vector moves = gameInfo.moves;

      int numMadeMoves = moves.size()-backwardCount;
      for (int i=moves.size() - 1; i >= numMadeMoves; i--)
        moves.removeElementAt(i);

      pos.copyFrom(game.getInitialPosition());
      for (int i = 0; i < numMadeMoves; i++)
        pos.makeMove((Move)moves.elementAt(i));

      fireGameEvent(new TakebackEvent(this, game, backwardCount));
    } catch (NoSuchGameException e){}
  }





  /**
   * Fires a Takeback event.
   */

  protected void processTakeback(int gameNumber, int takebackCount){
    try{
      GameInfo gameInfo = getGameInfo(gameNumber);
      Game game = gameInfo.game;
      Position pos = gameInfo.position;
      Vector moves = gameInfo.moves;

      int numMadeMoves = moves.size() - takebackCount;
      for (int i = moves.size() - 1; i >= numMadeMoves; i--)
        moves.removeElementAt(i);

      pos.copyFrom(game.getInitialPosition());
      for (int i = 0; i < numMadeMoves; i++)
        pos.makeMove((Move)moves.elementAt(i));

      fireGameEvent(new TakebackEvent(this, game, takebackCount));
    } catch (NoSuchGameException e){}
  }





  /**
   * Fires an IllegalMoveEvent.
   */

  protected void processIllegalMove(int gameNumber, String moveString, int reasonCode){
    try{
      GameInfo gameInfo = getGameInfo(gameNumber);
      Game game = gameInfo.game;

      Vector unechoedGameMoves = (Vector)unechoedMoves.get(game);
      if ((unechoedGameMoves == null) || (unechoedGameMoves.size() == 0)) // Not a move we made (probably the user typed it in)
        return;

      Move move = (Move)unechoedGameMoves.elementAt(0);
      if (moveToString(game, move).equals(moveString)){ // Our move
        unechoedGameMoves.removeAllElements();
        fireGameEvent(new IllegalMoveEvent(this, game, move));
      }
    } catch (NoSuchGameException e){}
  }





  /**
   * Fires a ClockAdjustmentEvent.
   */

  protected void processMsec(int gameNumber, int color, int msec, boolean isRunning){
    try{
      GameInfo gameInfo = getGameInfo(gameNumber);
      Game game = gameInfo.game;

      if (color==WHITE)
        gameInfo.setWhiteClock(msec, isRunning);
      else
        gameInfo.setBlackClock(msec, isRunning);

      Player player = (color==WHITE ? Player.WHITE_PLAYER : Player.BLACK_PLAYER);
      fireGameEvent(new ClockAdjustmentEvent(this, game, player, msec, isRunning));
    } catch (NoSuchGameException e){}
  }




  /**
   * Fires a ClockAdjustmentEvent.
   */

  protected void processMoretime(int gameNumber, int color, int seconds){
    try{
      GameInfo gameInfo = getGameInfo(gameNumber);
      Game game = gameInfo.game;

      if (color==WHITE){
        int newTime = gameInfo.getWhiteTime()+seconds*1000;
        boolean isRunning = gameInfo.isWhiteClockRunning();
        if (isRunning)
          newTime -= (int)(System.currentTimeMillis()-gameInfo.getWhiteTimestamp());

        gameInfo.setWhiteClock(newTime, isRunning);
        fireGameEvent(new ClockAdjustmentEvent(this, game, Player.WHITE_PLAYER, newTime, isRunning));
      }
      else{
        int newTime = gameInfo.getBlackTime()+seconds*1000;
        boolean isRunning = gameInfo.isBlackClockRunning();
        if (isRunning)
          newTime -= (int)(System.currentTimeMillis()-gameInfo.getBlackTimestamp());

        gameInfo.setBlackClock(newTime, isRunning);
        fireGameEvent(new ClockAdjustmentEvent(this, game, Player.BLACK_PLAYER, newTime, isRunning));
      }
    } catch (NoSuchGameException e){}
  }






  /**
   * Fires a BoardFlipEvent if the game has started, or sets the "InitiallyFlipped"
   * property of the new Game if not.
   */

  protected void processFlip(int gameNumber, boolean isFlipped){
    if (existsNonStarted(gameNumber)){
      putPropertyForNonStarted(gameNumber, "InitiallyFlipped", new Boolean(isFlipped));
    }
    else{
      try{
        GameInfo gameInfo = getGameInfo(gameNumber);
        Game game = gameInfo.game;
        gameInfo.isFlipped = isFlipped;

        fireGameEvent(new BoardFlipEvent(this, game, isFlipped));
      } catch (NoSuchGameException e){}
    }
  }





  /**
   * Gets called when a DG_CIRCLE datagram arrives. Fires a CircleEvent to all
   * registered ChessclubGameListeners.
   */

  protected void processCircle(int gameNumber, String examiner, String coordinate){
    try{
      GameInfo gameInfo = getGameInfo(gameNumber);
      Game game = gameInfo.game;

      Square circleSquare = Square.parseSquare(coordinate);

      fireGameEvent(new CircleEvent(this, game, circleSquare));
    } catch (NoSuchGameException e){}
  }




  /**
   * Gets called when a DG_ARROW datagram arrives.
   */

  protected void processArrow(int gameNumber, String examiner, String origin, String destination){
    try{
      GameInfo gameInfo = getGameInfo(gameNumber);
      Game game = gameInfo.game;

      Square fromSquare = Square.parseSquare(origin);
      Square toSquare = Square.parseSquare(destination);

      fireGameEvent(new ArrowEvent(this, game, fromSquare, toSquare));
    } catch (NoSuchGameException e){}
  }




  /**
   * Dispatches the given GameEvent to all interested GameListeners.
   */
  
  private void fireGameEvent(GameEvent evt){
    listenerManager.fireGameEvent(evt);
  }




  /**
   * Depending on the type of the game, issues either "unobserve <gameNumber>",
   * "unexamine" or "resign" command.
   */

  public void quitGame(Game game){
    Object id = game.getID();
    switch (game.getGameType()){
      case Game.MY_GAME:
        if (game.isPlayed())
          resign(game);
        else
          sendCommand("unexamine");
        break;
      case Game.OBSERVED_GAME:
        sendCommand("unobserve "+id);
        break;
      case Game.ISOLATED_BOARD:
        break;
    }
  }




  /**
   * Makes the given move in the given game.
   */

  public void makeMove(Game game, Move move){
    String moveString = moveToString(game, move);
    if (userGamesCount > 1)
      // It seems that "; goto <gamenum> ; <movestring>" will abort making the
      // move (or whatever other command follows it) if you aren't playing
      // a game with the specified number.
      sendCommand("; goto " + game.getID() + " ; " + moveString);
    else
      sendCommand(moveString);

    Vector unechoedGameMoves = (Vector)unechoedMoves.get(game);
    if (unechoedGameMoves == null){
      unechoedGameMoves = new Vector(2);
      unechoedMoves.put(game, unechoedGameMoves);
    }
    unechoedGameMoves.addElement(move);
  }




  /**
   * Converts the given move into a string we can send to the server.
   */

  private static String moveToString(Game game, Move move){
    WildVariant variant = game.getVariant();
    if (move instanceof ChessMove){
      ChessMove cmove = (ChessMove)move;
      if (cmove.isShortCastling())
        return "O-O";
      else if (cmove.isLongCastling())
        return "O-O-O";

      String s = cmove.getStartingSquare().toString() + cmove.getEndingSquare().toString();
      if (cmove.isPromotion())
        return s + "=" + variant.pieceToString(cmove.getPromotionTarget());
      else
        return s;
    }
    else
      throw new IllegalArgumentException("Unsupported Move type: "+move.getClass());
  }






  /**
   * Throws an IllegalArgumentException if the given Game is not of type 
   * Game.MY_GAME or is not a played game. Otherwise, simply returns.
   */

  private void checkGameMineAndPlayed(Game game){
    if ((game.getGameType() != Game.MY_GAME) || !game.isPlayed())
      throw new IllegalArgumentException("The given game must be of type Game.MY_GAME and a played one");
  }




  /**
   * Throws an IllegalArgumentException if the given Game is not of type 
   * Game.MY_GAME or is a played game. Otherwise, simply returns.
   */

  private void checkGameMineAndExamined(Game game){
    if ((game.getGameType() != Game.MY_GAME) || game.isPlayed())
      throw new IllegalArgumentException("The given game must be of type Game.MY_GAME and an examined one");
  }




  /**
   * Resigns the given game. The given game must be a played game and of type
   * Game.MY_GAME.
   */

  public void resign(Game game){
    checkGameMineAndPlayed(game);

    if (userGamesCount > 1)
      sendCommand("; goto "+game.getID()+" ; resign");
    else
      sendCommand("resign");
  }




  /**
   * Sends a request to draw the given game. The given game must be a played 
   * game and of type Game.MY_GAME.
   */

  public void requestDraw(Game game){
    checkGameMineAndPlayed(game);

    if (userGamesCount > 1)
      sendCommand("; goto "+game.getID()+" ; draw");
    else
      sendCommand("draw");
  }




  /**
   * Returns true.
   */

  public boolean isAbortSupported(){
    return true;
  }




  /**
   * Sends a request to abort the given game. The given game must be a played 
   * game and of type Game.MY_GAME.
   */

  public void requestAbort(Game game){
    checkGameMineAndPlayed(game);

    if (userGamesCount > 1)
      sendCommand("; goto "+game.getID()+" ; abort");
    else
      sendCommand("abort");
  }



  /**
   * Returns true.
   */

  public boolean isAdjournSupported(){
    return true;
  }



  /**
   * Sends a request to adjourn the given game. The given game must be a played
   * game and of type Game.MY_GAME.
   */

  public void requestAdjourn(Game game){
    checkGameMineAndPlayed(game);

    if (userGamesCount > 1)
      sendCommand("; goto "+game.getID()+" ; adjourn");
    else
      sendCommand("adjourn");
  }




   /**
   * Goes back the given amount of plies in the given game. If the given amount
   * of plies is bigger than the amount of plies since the beginning of the game,
   * goes to the beginning of the game.
   */

  public void goBackward(Game game, int plyCount){
    checkGameMineAndExamined(game);

    sendCommand("backward "+plyCount);
  }




  /**
   * Goes forward the given amount of plies in the given game. If the given amount
   * of plies is bigger than the amount of plies remaining until the end of the
   * game, goes to the end of the game.
   */

  public void goForward(Game game, int plyCount){
    checkGameMineAndExamined(game);

    sendCommand("forward "+plyCount);
  }




  /**
   * Goes to the beginning of the given game.
   */

  public void goToBeginning(Game game){
    checkGameMineAndExamined(game);

    sendCommand("backward 9999");
  }



  /**
   * Goes to the end of the given game.
   */

  public void goToEnd(Game game){
    checkGameMineAndExamined(game);

    sendCommand("forward 9999");
  }




  /**
   * A container for various game information, such as the Game object, the current
   * Position and a list of made moves.
   */

  private static class GameInfo{


    /**
     * The Game.
     */
    
    public final Game game;



    /**
     * The current position.
     */

    public final Position position;




    /**
     * The list of moves.
     */

    public final Vector moves;




    /**
     * The amount of moves to follow before the game actually starts.
     */

    public int numMovesToFollow;





    /**
     * True if the board should be flipped.
     */

    public boolean isFlipped;




    /**
     * White's time, in milliseconds.
     */

    private int whiteTime;



    /**
     * Whether white's clock is running.
     */

    private boolean isWhiteRunning;



    /**
     * The value of System.currentTimeMillis() when whiteTime and isWhiteRunning were
     * last updated.
     */

    private long whiteTimestamp;



    /**
     * Black's time, in milliseconds.
     */

    private int blackTime;



    /**
     * Whether black's clock is running.
     */

    private boolean isBlackRunning;



    /**
     * The value of System.currentTimeMillis() when whiteTime and isBlackRunning were
     * last updated.
     */

    private long blackTimestamp;



    /**
     * Creates a new GameInfo object with the given Game, initial position and
     * the amount of moves to follow before the game actually starts (although
     * the start event is fired right away).
     */

    public GameInfo(Game game, Position initialPos, int numMovesToFollow){
      this.game = game;
      this.position = initialPos;
      this.moves = new Vector();
      this.numMovesToFollow = numMovesToFollow;
      this.isFlipped = game.isBoardInitiallyFlipped();

      setWhiteClock(game.getWhiteTime(), false);
      setBlackClock(game.getBlackTime(), false);
    }



    /**
     * Sets the amount of time remaining on white's clock, in milliseconds.
     */

    public void setWhiteClock(int time, boolean running){
      whiteTime = time;
      isWhiteRunning = running;
      whiteTimestamp = System.currentTimeMillis();
    }



    /**
     * Sets the amount of time remaining on black's clock, in milliseconds.
     */

    public void setBlackClock(int time, boolean running){
      blackTime = time;
      isBlackRunning = running;
      blackTimestamp = System.currentTimeMillis();
    }



    /**
     * Returns the amount of time on white's clock when it was last updated,
     * in milliseconds.
     */

    public int getWhiteTime(){
      return whiteTime;
    }



    /**
     * Returns true if white's clock is currently running.
     */

    public boolean isWhiteClockRunning(){
      return isWhiteRunning;
    }



    /**
     * Returns the value of System.currentTimeMillis() the last time white's clock
     * was updated.
     */

    public long getWhiteTimestamp(){
      return whiteTimestamp;
    }



    /**
     * Returns the amount of time on black's clock when it was last updated,
     * in milliseconds.
     */

    public int getBlackTime(){
      return blackTime;
    }



    /**
     * Returns true if black's clock is currently running.
     */

    public boolean isBlackClockRunning(){
      return isBlackRunning;
    }



    /**
     * Returns the value of System.currentTimeMillis() the last time white's clock
     * was updated.
     */

    public long getBlackTimestamp(){
      return blackTimestamp;
    }

  }





  /**
   * Maps seek IDs to Seek objects currently in the sought list.
   */

  private final Hashtable seeks = new Hashtable();




  /**
   * Returns the SeekJinListenerManager via which you can register and
   * unregister SeekListeners.
   */

  public SeekJinListenerManager getSeekJinListenerManager(){
    return getChessclubJinListenerManager();
  }



  /**
   * This method is called by our ChessclubJinListenerManager when a new
   * SeekListener is added and we already had registered listeners (meaning that
   * DG_SEEK was already on, so we need to notify the new listeners of all
   * existing seeks as well).
   */

  void notFirstListenerAdded(SeekListener listener){
    Enumeration seeksEnum = seeks.elements();
    while (seeksEnum.hasMoreElements()){
      Seek seek = (Seek)seeksEnum.nextElement();
      SeekEvent evt = new SeekEvent(this, SeekEvent.SEEK_ADDED, seek);
      listener.seekAdded(evt);
    }
  }





  /**
   * This method is called by our ChessclubJinListenerManager when the last
   * SeekListener is removed.
   */

  void lastSeekListenerRemoved(){
    seeks.clear();
  }





  /**
   * Fires the appropriate SeekEvent indicating a seek was added.
   */

  protected void processSeek(int index, String name, String titles, int rating, int ratingType, int wild,
      String ratingCategoryString, int time, int inc, boolean isRated, int color, int minRating, int maxRating, 
      boolean autoaccept, boolean formula, String fancyTimeControl){
    
    WildVariant variant = getVariant(wild);
    if (variant==null)
      return;

    boolean isProvisional = (ratingType!=ESTABLISHED_RATING);
    Player player;
    if (color==WHITE)
      player = Player.WHITE_PLAYER;
    else if (color==BLACK)
      player = Player.BLACK_PLAYER;
    else
      player = null;

    boolean isRegistered = (rating != 0);
    boolean isSeekerRated = !isUnrated(titles);
    boolean isComputer = isComputer(titles);
    boolean isRatingLimited = ((minRating!=0)||(maxRating!=9999));

    String title = displayableTitle(titles);

    Seek seek = new Seek(String.valueOf(index), name, title, rating, isProvisional, isRegistered, isSeekerRated, isComputer, variant,
      ratingCategoryString, time*60*1000, inc*1000, isRated, player, isRatingLimited, minRating, maxRating, !autoaccept, formula);

    seeks.put(new Integer(index), seek);

    listenerManager.fireSeekEvent(new SeekEvent(this, SeekEvent.SEEK_ADDED, seek));
  }





  /**
   * Fires the appropriate SeekEvent indicating a seek was removed.
   */

  protected void processSeekRemoved(int index, int reasonCode){
    Seek seek = (Seek)seeks.remove(new Integer(index));

    if (seek==null)
      return;

    listenerManager.fireSeekEvent(new SeekEvent(this, SeekEvent.SEEK_REMOVED, seek));    
  }




  /**
   * Accepts the given seek. Note that the given seek must be an instance generated
   * by this SeekJinConnection and it must be in the current sought list.
   */

  public void acceptSeek(Seek seek){
    if (!seeks.contains(seek))
      throw new IllegalArgumentException("The specified seek is not on the seek list");

    sendCommand("play "+seek.getID());
  }





  /**
   * The GameListInfo for the list we're currently reading. I'm not holding
   * a hashtable in hope that different lists will not be mixed.
   */

  private GameListInfo curGameListInfo = null;




  /**
   * Returns the GameListJinListenerManager via which you can register and
   * unregister GameListListeners.
   */

  public GameListJinListenerManager getGameListJinListenerManager(){
    return getChessclubJinListenerManager();
  }




  /**
   * Gets called when a DG_GAMELIST_BEGIN datagram arrives. Prepares for
   * receiving the list.
   */

  protected void processGamelistBegin(String command, String argsString,  int hitsCount, 
      int firstIndex, int lastIndex, String summary){

    int id;

    if (command.equalsIgnoreCase("history"))
      id = GameListEvent.HISTORY_LIST_EVENT_ID;
    else if(command.equalsIgnoreCase("search"))
      id = GameListEvent.SEARCH_LIST_EVENT_ID;
    else if (command.equalsIgnoreCase("liblist"))
      id = GameListEvent.LIBLIST_EVENT_ID;
    else if (command.equalsIgnoreCase("stored"))
      id = GameListEvent.STORED_LIST_EVENT_ID;
    else
      return; // Ignore unknown lists

    curGameListInfo = new GameListInfo(id, command, argsString, hitsCount, firstIndex, lastIndex, summary);
  }




  /**
   * Gets called when a DG_GAMELIST_ITEM datagram arrives. Fires a GameListEvent
   * to registered listeners.
   */

  protected void processGamelistItem(int index, String id, String event, String dateString,
      String timeString, String whiteName, int whiteRating, String blackName, int blackRating,
      boolean isRated, int ratingCategory, int wildType, int whiteTime, int whiteInc,
      int blackTime, int blackInc, String eco, int status, int color, int mode, String note,
      boolean isHere){

    if (curGameListInfo == null)
      return; // This shouldn't happen, but let's be a little defensive.


    String variantName = "w"+wildType;
    String ratingCategoryName = getRatingCategoryName(ratingCategory);

    String endExplanationString = getEndExplanationString(status, mode, color);
    int resultStatus;
    switch (status){
      case 0:
        if (color==WHITE)
          resultStatus = WHITE_LOST;
        else
          resultStatus = WHITE_WON;
        break;
      case 1:
        resultStatus = DRAWN;
        break;
      case 2:
        resultStatus = ADJOURNED;
        break;
      case 3:
        resultStatus = ABORTED;
        break;
      default:
        throw new IllegalArgumentException("Unknown result status encountered");
    }

    GameListItem item;
    switch (curGameListInfo.gameListEventID){
      case GameListEvent.HISTORY_LIST_EVENT_ID:{
        Player player;
        if (curGameListInfo.args.indexOf(whiteName)!=-1)
          player = Player.WHITE_PLAYER;
        else if (curGameListInfo.args.indexOf(blackName)!=-1)
          player = Player.BLACK_PLAYER;
        else
          throw new IllegalArgumentException("Unable to determine the player whose history is being retrieved"); 

        item = new HistoryListItem(index, id, dateString, timeString, whiteName, blackName, whiteTime*60*1000,
            whiteInc*1000, blackTime*60*1000, blackInc*1000, whiteRating, blackRating,
            isRated, variantName, ratingCategoryName, eco, endExplanationString, resultStatus, player);
        break;
      }

      case GameListEvent.SEARCH_LIST_EVENT_ID:{
        item = new SearchListItem(index, id, dateString, timeString, whiteName, blackName, whiteTime*60*1000,
            whiteInc*1000, blackTime*60*1000, blackInc*1000, whiteRating, blackRating,
            isRated, variantName, ratingCategoryName, eco, endExplanationString, resultStatus);
        break;
      }

      case GameListEvent.LIBLIST_EVENT_ID:{
        item = new LibListItem(index, id, dateString, timeString, whiteName, blackName, whiteTime*60*1000,
            whiteInc*1000, blackTime*60*1000, blackInc*1000, whiteRating, blackRating,
            isRated, variantName, ratingCategoryName, eco, endExplanationString, resultStatus, note);
        break;
      }

      case GameListEvent.STORED_LIST_EVENT_ID:{
        Player player;
        if (curGameListInfo.args.indexOf(whiteName)!=-1)
          player = Player.WHITE_PLAYER;
        else if (curGameListInfo.args.indexOf(blackName)!=-1)
          player = Player.BLACK_PLAYER;
        else
          throw new IllegalArgumentException("Unable to determine the player whose stored game is being retrieved"); 

        if (resultStatus!=ADJOURNED)
          throw new IllegalArgumentException("ResultStatus is not \"ADJOURNED\" for a stored game");

        String adjournmentReason = getAdjournmentReason(mode, color==WHITE ? whiteName : blackName);

        item = new StoredListItem(index, id, dateString, timeString, whiteName, blackName, whiteTime*60*1000,
            whiteInc*1000, blackTime*60*1000, blackInc*1000, whiteRating, blackRating,
            isRated, variantName, ratingCategoryName, eco, isHere, adjournmentReason, player);
        break;
      }
      default:
        throw new IllegalStateException("Attempted to create a GameListInfo with an unknown item type");
    }

    curGameListInfo.gameList[curGameListInfo.gameList.length-curGameListInfo.numItemsLeft] = item;
    curGameListInfo.numItemsLeft--;

    if (curGameListInfo.numItemsLeft==0){
      String title = curGameListInfo.summary+" ("+curGameListInfo.command+" "+curGameListInfo.args+")";
      GameListEvent evt = new GameListEvent(this, curGameListInfo.gameListEventID, curGameListInfo.gameList, 
        title, curGameListInfo.totalNumItems, curGameListInfo.firstIndex, curGameListInfo.lastIndex);
      listenerManager.fireGameListEvent(evt);

      curGameListInfo = null;
    }

  }





  /**
   * Returns the end game explanation string corresponding to the given status,
   * mode and the color of the player who lost.
   */

  protected String getEndExplanationString(int status, int mode, int color){
    String winner = (color==WHITE ? "Black" : "White");
    String loser = (color==WHITE ? "White" : "Black");
    String result = (color==WHITE ? "(0-1)" : "(1-0)");

    String endExplanationString = "(?) No result [specific reason unknown]";
    switch (status){
      case 0:
        switch (mode){
          case 0:
            endExplanationString = "(Res) "+loser+" resigns";
            break;
          case 1:
            endExplanationString = "(Mat) "+loser+" checkmated";
            break;
          case 2:
            endExplanationString = "(Fla) "+loser+" forfeits on time.";
            break;
          case 3:
            endExplanationString = "(Adj) "+winner+" declared the winner by adjudication";
            break;
          case 4:
            endExplanationString = "(BQ) "+loser+" disconnected and forfeits";
            break;
          case 5:
            endExplanationString = "(BQ) "+loser+" got disconnected and forfeits";
            break;
          case 6:
            endExplanationString = "(BQ) Unregistered player "+loser+" disconnected and forfeits";
            break;
          case 7:
            endExplanationString = "(Res) "+loser+"'s partner resigns";
            break;
          case 8:
            endExplanationString = "(Mat) "+loser+"'s partner checkmated";
            break;
          case 9:
            endExplanationString = "(Fla) "+loser+"'s partner forfeits on time";
            break;
          case 10:
            endExplanationString = "(BQ) "+loser+"'s partner disconnected and forfeits";
            break;
          case 11:
            endExplanationString = "(BQ) "+loser+" disconnected and forfeits [obsolete?]";
            break;
          case 12:
            endExplanationString = result+" "+winner+" wins [specific reason unknown]";
            break;
        }
        break;
          
      case 1:
        switch (mode){
          case 0:
            endExplanationString = "(Agr) Game drawn by mutual agreement";
            break;
          case 1:
            endExplanationString = "(Sta) "+loser+" stalemated";
            break;
          case 2:
            endExplanationString = "(Rep) Game drawn by repetition";
            break;
          case 3:
            endExplanationString = "(50) Game drawn by the 50 move rule";
            break;
          case 4:
            endExplanationString = "(TM) "+loser+" ran out of time and "+winner+" has no material to mate";
            break;
          case 5:
            endExplanationString = "(NM) Game drawn because neither player has mating material";
            break;
          case 6:
            endExplanationString = "(NT) Game drawn because both players ran out of time";
            break;
          case 7:
            endExplanationString = "(Adj) Game drawn by adjudication";
            break;
          case 8:
            endExplanationString = "(Agr) Partner's game drawn by mutual agreement";
            break;
          case 9:
            endExplanationString = "(NT) Partner's game drawn because both players ran out of time";
            break;
          case 10:
            endExplanationString = "(1/2) Game drawn [specific reason unknown]";
            break;
        } 
        break;
          
      case 2:
        switch (mode){
          case 0:
            endExplanationString = "(?) Game adjourned by mutual agreement";
            break;
          case 1:
            endExplanationString = "(?) Game adjourned when "+loser+" disconnected";
            break;
          case 2:
            endExplanationString = "(?) Game adjourned by system shutdown";
            break;
          case 3:
            endExplanationString = "(?) Game courtesyadjourned by "+loser+"";
            break;
          case 4:
            endExplanationString = "(?) Game adjourned by an administrator";
            break;
          case 5:
            endExplanationString = "(?) Game adjourned when "+loser+" got disconnected";
            break;
        }
        break;
      case 3:
        switch (mode){
          case 0:
            endExplanationString = "(Agr) Game aborted by mutual agreement";
            break;
          case 1:
            endExplanationString = "(BQ) Game aborted when "+loser+" disconnected";
            break;
          case 2:
            endExplanationString = "(SD) Game aborted by system shutdown";
            break;
          case 3:
            endExplanationString = "(BA) Game courtesyaborted by "+loser+"";
            break;
          case 4:
            endExplanationString = "(Adj) Game aborted by an administrator";
            break;
          case 5:
            endExplanationString = "(Sho) Game aborted because it's too short to adjourn";
            break;
          case 6:
            endExplanationString = "(BQ) Game aborted when "+loser+"'s partner disconnected";
            break;
          case 7:
            endExplanationString = "(Sho) Game aborted by "+loser+" at move 1";
            break;
          case 8:
            endExplanationString = "(Sho) Game aborted by "+loser+"'s partner at move 1";
            break;
          case 9:
            endExplanationString = "(Sho) Game aborted because it's too short";
            break;
          case 10:
            endExplanationString = "(Adj) Game aborted because "+loser+"'s account expired";
            break;
          case 11:
            endExplanationString = "(BQ) Game aborted when "+loser+" got disconnected";
            break;
          case 12:
            endExplanationString = "(?) No result [specific reason unknown]";
            break;
        }
        break;
    }

    return endExplanationString;
  }



  /**
   * Returns the adjournment reason corresponding to the given mode. Status
   * is assumed to be ADJOURNED.
   */

  protected String getAdjournmentReason(int mode, String actor){
    String adjournmentReason = "";
    switch (mode){
      case 0: adjournmentReason = "By mutual agreement"; break;
      case 1: adjournmentReason = "When "+actor+" disconnected"; break;
      case 2: adjournmentReason = "By system shutdown"; break;
      case 3: adjournmentReason = "Courtesyadjourned by "+actor; break;
      case 4: adjournmentReason = "By an administrator"; break;
      case 5: adjournmentReason = "When "+actor+" got disconnected"; break;
    }

    return adjournmentReason;
  }





  private static class GameListInfo{



    /**
     * The GameListEvent id identifying the type of the game list items.
     */

    public final int gameListEventID;


    /**
     * The name of the command that triggered the game list being sent.
     */

    public final String command;



    /**
     * The arguments to the command that triggered the game list being sent.
     */

    public final String args;



    /**
     * The amount of items in the complete list.
     */

    public final int totalNumItems;



    /**
     * The index of the first sent item in the complete list.
     */

    public final int firstIndex;




    /**
     * The index of the last sent item in the complete list.
     */

    public final int lastIndex;




    /**
     * A summary.
     */

    public final String summary;




    /**
     * The amount of items left to be sent in the list.
     */

    public int numItemsLeft;




    /**
     * An array of the game list items.
     */

    public final GameListItem [] gameList;




    /**
     * Creates a new GameListInfo.
     */

    public GameListInfo(int id, String command, String args, int totalNumItems, int firstIndex,
        int lastIndex, String summary){

      this.gameListEventID = id;
      this.command = command;
      this.args = args;
      this.totalNumItems = totalNumItems;
      this.firstIndex = firstIndex;
      this.lastIndex = lastIndex;
      this.summary = summary;
      this.numItemsLeft = lastIndex-firstIndex+1;

      Class gameListItemType;
      switch (gameListEventID){
        case GameListEvent.HISTORY_LIST_EVENT_ID:
          gameListItemType = HistoryListItem.class;
          break;
        case GameListEvent.SEARCH_LIST_EVENT_ID:
          gameListItemType = SearchListItem.class;
          break;
        case GameListEvent.LIBLIST_EVENT_ID:
          gameListItemType = LibListItem.class;
          break;
        case GameListEvent.STORED_LIST_EVENT_ID:
          gameListItemType = StoredListItem.class;
          break;
        default:
          throw new IllegalStateException("Attempted to create a GameListInfo with an unknown item type");
      }

      gameList = (GameListItem [])Array.newInstance(gameListItemType, numItemsLeft);
    }

  }




  /**
   * Maps ChessEvent IDs (as Integer objects) to ChessEvent objects.
   * Contains the currently existing events.
   */

  private final Hashtable chessEvents = new Hashtable();




  /**
   * Gets called when a DG_TOURNEY event arrives. Fires the appropriate
   * ChessEvent to any registered listeners.
   */

  protected void processTourney(int id, boolean canGuestsWatchJoin, boolean makeNewWindowOnJoin, boolean makeNewWindowOnWatch,
    boolean makeNewWindowOnInfo, String description, String [] joinCommands, String [] watchCommands, String [] infoCommands,
    String confirmText){

    ChessEvent newEvent = new ChessEvent(id, description, joinCommands.length == 0 ? null : joinCommands, 
      watchCommands.length == 0 ? null : watchCommands, infoCommands.length == 0 ? null : infoCommands, confirmText);
    ChessEvent existingEvent = (ChessEvent)chessEvents.put(new Integer(id), newEvent);

    if (existingEvent != null)
      listenerManager.fireChessEventEvent(new ChessEventEvent(this, ChessEventEvent.EVENT_REMOVED, existingEvent));

    listenerManager.fireChessEventEvent(new ChessEventEvent(this, ChessEventEvent.EVENT_ADDED, newEvent));
  }




  /**
   * Gets called when a DG_REMOVE_TOURNEY event arrives. Fires the appropriate
   * ChessEvent to any registered listeners.
   */

  protected void processRemoveTourney(int id){
    ChessEvent evt = (ChessEvent)chessEvents.get(new Integer(id));
    if (evt == null) // Ignore DG_REMOVE_TOURNEY for events we didn't get a DG_TOURNEY for.
      return;

    listenerManager.fireChessEventEvent(new ChessEventEvent(this, ChessEventEvent.EVENT_REMOVED, evt));
  }



  
  
  /**
   * Overrides ChessclubConnection.execRunnable(Runnable) to execute the
   * runnable on the AWT thread using SwingUtilities.invokeLater(Runnable), 
   * since this class is meant to be used by Jin, a graphical interface using 
   * Swing.
   *
   * @see ChessclubConnection#execRunnable(Runnable)
   * @see SwingUtilities.invokeLater(Runnable)
   */

  public void execRunnable(Runnable runnable){
    SwingUtilities.invokeLater(runnable);
  }


}
