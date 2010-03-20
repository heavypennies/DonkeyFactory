/**
 * The chessclub.com connection library.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2002-2003 Alexander Maryanovsky.
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

package free.chessclub;

import java.io.*;
import java.util.*;
import free.chessclub.level2.*;


/**
 * This class is responsible for connecting to the chessclub.com
 * server, logging on and further processing of information as it
 * arrives from the server. Before using this class you should read 
 * <A HREF="ftp://ftp.chessclub.com/pub/icc/formats/formats.txt">ftp://ftp.chessclub.com/pub/icc/formats/formats.txt</A>
 * which describes ICC server's output. All the processXXX methods receive arguments
 * with the meaning and format described there unless specified otherwise
 * in the method documentation.
 */

public class ChessclubConnection extends free.util.Connection{


  /**
   * The code for a regular personal tell.
   */

  public static final int TELL = 0;



  
  /**
   * The code for a personal tell sent with the "say" command.
   */

  public static final int SAY = 1;




  /**
   * The code for a "ptell" (sent from a bughouse partner).
   */

  public static final int PTELL = 2;




  /**
   * The code for a personal qtell (by a TD).
   */

  public static final int QTELL = 3;




  /**
   * The code for an "atell" (admin tell).
   */

  public static final int ATELL = 4;




  /**
   * The for a regular shout.
   */

  public static final int SHOUT = 100;




  /**
   * The code for an "I" type shout. This is the same as a regular
   * shout only it is issued in the form "I <message>" because the 
   * server transforms the message sent by the user into 3rd form.
   */

  public static final int I_SHOUT = 101;




  /**
   * The code for an "sshout".
   */

  public static final int SSHOUT = 102;





  /**
   * The code for an announcement. An announcement is a really
   * important message that goes to all the users online.
   */

  public static final int ANNOUNCEMENT = 103;




  /**
   * The codoe for a regular channel tell.
   */

  public static final int CHANNEL_TELL = 201;




  /**
   * The code for a channel atell, a special channel tell sent
   * by an admin which the user should pay special attention to.
   */

  public static final int CHANNEL_ATELL = 202;




  /**
   * The code for a channel qtell, a channel tell sent by a
   * tournament director.
   */

  public static final int CHANNEL_QTELL = 203;




  /**
   * The code for a kibitz. A kibitz is a message that goes to all the
   * observers and players of a certain game.
   */

  public static final int KIBITZ = 300;


  

  /**
   * The code for a whisper. A whisper is the same as a kibitz only
   * it doesn't go to the players in the game, only to observers.
   */

  public static final int WHISPER = 301;




  /**
   * The rating type for no rating.
   */

  public static final int NO_RATING = 0;



  /**
   * The rating type for a provisional rating.
   */

  public static final int PROVISIONAL_RATING = 1;


  /**
   * The rating type for an established rating.
   */

  public static final int ESTABLISHED_RATING = 2;




  /**
   * The code for player state - doing nothing. This is sometimes used as
   * actual "doing nothing" and other times as an indication that a player
   * stopped doing something he used to do - "left the table" in
   * DG_PLAYERS_IN_MY_GAME for example.
   */

  public static final int DOING_NOTHING = 0;



  /**
   * The code for player state - playing a game.
   */

  public static final int PLAYING = 1;



  /**
   * The code for player state - examining.
   */

  public static final int EXAMINING = 2;



  /**
   * The code for player state - playing a simul.
   */

  public static final int PLAYING_SIMUL = 3;




  /**
   * The code for player state - observing a game.
   */

  public static final int OBSERVING = 4;




  /**
   * The code for player state - playing a game as white.
   */

  public static final int PLAYING_WHITE = 5;




  /**
   * The code for player state - playing a game as black.
   */

  public static final int PLAYING_BLACK = 6;




  /**
   * The code for player state - playing a simul as white.
   */

  public static final int PLAYING_SIMUL_WHITE = 7;




  /**
   * The code for player state - playing a simul as black.
   */

  public static final int PLAYING_SIMUL_BLACK = 8;





  /**
   * The code for white color.
   */

  public static final int WHITE = 0;




  /**
   * The code for black color.
   */

  public static final int BLACK = 1;




  /**
   * The code for no color.
   */

  public static final int COLORLESS = -1;





  /**
   * The code for a stored game.
   */

  public static final int STORED_GAME = 0;




  /**
   * The code for an examined game.
   */

  public static final int EXAMINED_GAME = 1;




  /**
   * The code for a played game.
   */

  public static final int PLAYED_GAME = 2;





  /**
   * The index of the wild rating.
   */

  public static final int WILD = 0;


  
  /**
   * The index of the blitz rating.
   */

  public static final int BLITZ = 1;



  /**
   * The index of the standard rating.
   */

  public static final int STANDARD = 2;



  /**
   * The index of the bullet rating.
   */

  public static final int BULLET = 3;



  /**
   * The index of the bughouse rating.
   */

  public static final int BUGHOUSE = 4;



  /**
   * The index of the losers rating.
   */

  public static final int LOSERS = 5;



  /**
   * The amount of rating categories.
   */

  public static final int RATING_CATEGORIES_COUNT = 6;




  /**
   * Maps rating keys to their english names.
   */

  private final Hashtable ratingCategoryNames = new Hashtable();




  /**
   * Maps wild variant indices to their english names.
   */

  private final Hashtable variantNames = new Hashtable();




  /**
   * The PrintStream where this ChessclubConnection echoes the information sent by the 
   * server and the commands sent by it to the standard output stream. Null
   * if it doesn't echo.
   */

  private final PrintStream echoStream;

  


  /**
   * The level2 settings requested by the client. The bit at each index
   * specifies whether the Datagram with that index
   * will be turned on. <P>
   * Note that these aren't the "real" settings because the settings only take
   * effect once the server reads them and responds with a DG_SET2. The
   * "real" level 2 settings are stored in level2settings.
   */

  private BitSet requestedLevel2Settings = new BitSet();




  /**
   * The current board sending "style".
   */

  private int style = 1;




  /**
   * The value we're supposed to assign to the interface variable during login.
   */

  private String interfaceVar = "Java chessclub.com library (http://www.jinchess.com/)";
     



  /**
   * The "real" level 2 settings on which currently the client and the server
   * agree.
   */

  private BitSet level2Settings = new BitSet();




  /**
   * This is set to <code>true</code> when the "level2settings=..." line has
   * been sent.
   */

  private boolean level2SettingsSent = false;



  /**
   * The OutputStream to the server.
   */

  private OutputStream out;



  /**
   * The lock we synchronize the login on.
   */

  private final Object loginLock = new String("Login Lock");




  /**
   * Creates a new ChessclubConnection with a chessclub.com server, the 
   * ChessclubConnection is initially unconnected. After creating the 
   * ChessclubConnection, you can set the various settings (level2 settings for 
   * example) and then call the connect() method.
   *
   * @param hostname The server we will connect to.
   * @param port The port we will connect on.
   * @param username The requested username, note that the actual username is
   * unknown until after the login.
   * @param password The password of the account.
   * @param echoStream The PrintStream where this ChessclubConnection will echo
   * all information sent by the server and commands sent by this
   * ChessclubConnection. Pass null if you don't want echoing.
   *
   * @see #setDGState(int, boolean)
   */

  public ChessclubConnection(String hostname, int port, String username, String password, PrintStream echoStream){
    super(hostname, port, username, password);

    this.echoStream = echoStream;

    // We need this to get the real username
    setDGState(Datagram.DG_WHO_AM_I, true); 

    // We need this to set the login error message properly
    setDGState(Datagram.DG_LOGIN_FAILED, true); 

    // We need this to know the key to rating name mapping.
    setDGState(Datagram.DG_RATING_TYPE_KEY, true); 

    // We need this to know the wild number to wild name mapping.
    setDGState(Datagram.DG_WILD_KEY, true); 

    // We need this to know which datagrams are actually on.
    setDGState(Datagram.DG_SET2, true);
  }




  /**
   * Sets the given level2 datagram on or off. If the ChessclubConnection is already
   * logged in, then the "set-2 [DG number] [0/1]" string is sent to the
   * server, otherwise the setting is saved, and and in the login procedure all 
   * the level2 settings are sent on the login line in the "level2settings=0011011011..." format.
   * Note that some datagrams are necessary for the correct behaviour of this
   * class, and cannot be turned off (DG_WHO_AM_I and DG_SET2 for example).
   *
   * @param dgNumber The number of the datagram.
   * @param state Whether turn the datagram on or off.
   *
   * @return Whether the state of the datagram was modified successfully. This
   * always returns true when setting a datagram on, and only returns false
   * when trying to set an essential datagram off.
   *
   * @see #isEssentialDG(int)
   * @see #isDGOn(int)
   */

  public final synchronized boolean setDGState(int dgNumber, boolean state){
    if ((state == false) && isEssentialDG(dgNumber))
      return false;

    if (state)
      requestedLevel2Settings.set(dgNumber);
    else
      requestedLevel2Settings.clear(dgNumber);

    if (level2SettingsSent){
      if (isLoggedIn())
        sendCommand("set-2 "+dgNumber+" "+(state ? "1" : "0"));
      // Otherwise, we will set it onLogin(). We don't do it here because it's
      // not a good idea to send anything in the middle of the login procedure.
    }
    else{
      if (state)
        level2Settings.set(dgNumber);
      else
        level2Settings.clear(dgNumber);
    }

    return true;
  }





  /**
   * Sets the given datagram on again. This is needed because some datagrams
   * won't correctly keep you up-to-date with the current state of events, and
   * you need (sigh, this is definitely not safe) to set them again to get a
   * refresh (DG_NOTIFY_ARRIVED for example). 
   *
   * @throws IllegalStateException if the datagram is not on already or if we're
   * not logged in yet.
   */

  public synchronized void setDGOnAgain(int dgNumber){
    if (!isDGOn(dgNumber))
      throw new IllegalStateException("Cannot set on again a datagram which is not on");
    if (!isLoggedIn())
      throw new IllegalStateException("Cannot set on again a datagram when not yet logged in");

    sendCommand("set-2 "+dgNumber+" 1");
  }




  /**
   * Sets the interface variable to have the given value. This works only if the
   * ChessclubConnection is not logged on yet, otherwise, throws an 
   * IllegalArgumentException. The actual interface variable will be set during 
   * the login procedure.
   */

  public final synchronized void setInterface(String interfaceVar){
    if (isLoggedIn())
      throw new IllegalStateException();
    this.interfaceVar = interfaceVar;
  }





  /**
   * Sets the style. If the ChessclubConnection is already logged in, then
   * a "set-quietly style <style>" command is send immediately, otherwise, the setting
   * is saved and sent immediately after logging in. If the <code>getEssentialStyle()</code>
   * mehod returns a value different than 0 and different from the given style,
   * this method will throw an IllegalAccessException.
   */

  public final synchronized boolean setStyle(int style){
    int essentialStyle = getEssentialStyle();
    if ((essentialStyle!=-1)&&(essentialStyle!=style))
      return false;

    this.style = style;
    if (isLoggedIn())
      sendCommand("set-quietly style "+style);

    return true;
  }






  /**
   * Returns true if the datagram with the given id is essential for the normal
   * operation of the instance and therefore cannot be turned off. Overriding
   * methods MUST check with the superclass method before returning false.
   */

  protected boolean isEssentialDG(int dgNumber){
    switch (dgNumber){
      case Datagram.DG_WHO_AM_I:
      case Datagram.DG_LOGIN_FAILED:
      case Datagram.DG_RATING_TYPE_KEY:
      case Datagram.DG_SET2:
        return true;
      default: 
        return false;
    }
  }





  /**
   * Returns the style which is essential for the normal operation of this class
   * and therefore cannot be changed. Returns -1 if the style setting is not
   * essential. Overriding methods MUST check with the superclass method before
   * returning a value. This method returns -1.
   */

  protected int getEssentialStyle(){
    return -1;
  }





  /**
   * Returns true if the given level2 datagram is turned on.
   * Note that this method returns the actual level2 settings, not the ones
   * requested by the client. These may differ because during the time from the
   * moment when the client sends a request to turn on/off a datagram and until 
   * the server receives it, the server will keep sending DGs thinking that
   * particular DG is in its previous state.
   *
   * @param dg The datagram number whose status you want to check.
   *
   * @see #setDGState(int,boolean)
   */

  public synchronized boolean isDGOn(int dg){
    return level2Settings.get(dg);
  }

  




  /**
   * Goes through the login procedure on a chessclub.com server.
   *
   * @throws IOException if an I/O error occured when connecting or logging on
   * to the server.
   *
   * @see #isConnected()
   * @see #disconnect()
   */

  protected boolean login() throws IOException{
    out = sock.getOutputStream();

    synchronized(this){
      int largestSetDGNumber = level2Settings.size();
      while ((largestSetDGNumber >= 0) && !level2Settings.get(largestSetDGNumber))
        largestSetDGNumber--;
      if (largestSetDGNumber >= 0){
        StringBuffer buf = new StringBuffer("level2settings=");
        for (int i = 0; i <= largestSetDGNumber; i++){
          buf.append(level2Settings.get(i) ? "1" : "0");
        }
        sendCommand(buf.toString());
        level2SettingsSent = true;
      }

      String requestedUsername = getRequestedUsername();
      String password = getPassword();
      if ((password == null) || (password.length() == 0))
        sendCommand(requestedUsername);
      else
        sendCommand(requestedUsername+" "+password);
    }

    synchronized(loginLock){
      try{
        loginLock.wait(); // Wait until we receive DG_WHO_AM_I or DG_LOGIN_FAILED
      } catch (InterruptedException e){
          throw new InterruptedIOException(e.getMessage());
        } 
    }

    // We always set the login error message on error, so this is a valid way
    // to check whether there was an error.
    return getLoginErrorMessage() == null; 
  }




  /**
   * Sets the various things we need to set on login.
   */

  protected void onLogin(){
    super.onLogin();

    for (int i = 0; i < requestedLevel2Settings.size(); i++){
      boolean state = requestedLevel2Settings.get(i);
      if (state != level2Settings.get(i))
        sendCommand("set-2 "+i+" "+(state ? "1" : "0"));
    }

    sendCommand("set-quietly prompt 0");
    sendCommand("set-quietly style "+style);
    sendCommand("set-quietly interface "+interfaceVar);
  }




  /**
   * Sends the "exit" command to the server.
   */

  public void quit(){
    sendCommand("exit");
  }





  /**
   * Creates a new ReaderThread that will do the reading from the server.
   */

  protected Thread createReaderThread() throws IOException{
    return new ReaderThread(new BufferedInputStream(sock.getInputStream()), this);
  }





  /**
   * Sends the given command to the server.
   */

  public synchronized void sendCommand(String command){
    if (echoStream!=null){
      echoStream.println("SENDING COMMAND: "+command);
    }

    if (!isConnected())
      throw new IllegalStateException("Not connected");
    try{
      out.write(command.getBytes());
      out.write('\n');
      out.flush();
    } catch (IOException e){
        e.printStackTrace();
        try{
          sock.close(); // Disconnect
        } catch (IOException ex){
            ex.printStackTrace();
          }
      }
  }





  /**
   * Returns the name of the rating category with the given index. Note that
   * this is unknown until the login procedure is done (which may be even after
   * login() returns, but it's pretty much guaranteed to be known before you
   * receive notification of any datagrams, except for some special ones, 
   * like DG_WHO_AM_I). Returns null if no such rating category exists, or it's
   * unknown yet.
   */

  public String getRatingCategoryName(int index){
    return (String)ratingCategoryNames.get(new Integer(index));
  }




  /**
   * Returns the name of the wild variant with the given number.Note that
   * this is unknown until the login procedure is done (which may be even after
   * login() returns, but it's pretty much guaranteed to be known before you
   * receive notification of any datagrams, except for some special ones, 
   * like DG_WHO_AM_I). Returns null if no such wild variant exists, or it's
   * unknown yet.
   */

  public String getVariantName(int number){
    return (String)variantNames.get(new Integer(number));
  }





  /**
   * This method is called by the ReaderThread when a new level2 datagram 
   * arrives from the server.
   *
   * @param datagram The level2 datagram that was received.
   *
   * @see #processDatagram(Datagram)
   */

  public final void handleDatagram(Datagram datagram){
    if (echoStream != null)
      echoStream.println(datagram);

    if ((datagram.getType() == Datagram.DG_WHO_AM_I) && !isLoggedIn()){
      synchronized(loginLock){
        setUsername(datagram.getString(0));
        loginLock.notify();
      }
    }
    else if ((datagram.getType() == Datagram.DG_LOGIN_FAILED) && !isLoggedIn()){
      synchronized(loginLock){
        setLoginErrorMessage(datagram.getString(1));
        loginLock.notify();
      }
    }
    else if (datagram.getType() == Datagram.DG_RATING_TYPE_KEY){
      int index = datagram.getInteger(0);
      String name = datagram.getString(1);
      ratingCategoryNames.put(new Integer(index), name);
    }
    else if (datagram.getType() == Datagram.DG_WILD_KEY){
      int number = datagram.getInteger(0);
      String name = datagram.getString(1);
      variantNames.put(new Integer(number), name);
    }
    else if (datagram.getType() == Datagram.DG_SET2){
      int dgType = datagram.getInteger(0);
      boolean state = datagram.getBoolean(1);
      if (state)
        level2Settings.set(dgType);
      else
        level2Settings.clear(dgType);
    }

    processDatagram(datagram);
  }





  /**
   * Gets called when a DG_WHO_AM_I datagram arrives.
   */

  protected void processWhoAmI(String username, String titles){
    
  }





  /**
   * Gets called when a DG_PLAYER_ARRIVED datagram arrives. 
   * Note that since this datagram's data depends on other datagrams' settings,
   * each field in the given Player object (except username, which is always valid)
   * contains a valid value only if the datagram that sets its sending is on.
   * For example, ratings[BULLET] and ratingTypes[BULLET] will only contain 
   * correct values if DG_BULLET is on, otherwise they will contain meaningless values.
   * See <A HREF="ftp://ftp.chessclub.com/pub/icc/formats/formats.txt">ftp://ftp.chessclub.com/pub/icc/formats/formats.txt</A>
   * for an explanation on which datagrams turn on/off which fields.
   */

  protected void processPlayerArrived(Player player){

  }





  /**
   * Gets called when a DG_PLAYER_LEFT datagram arrives.
   */

  protected void processPlayerLeft(String username){

  }




  /**
   * Gets called when a DG_BULLET datagram arrives.
   * The rating type is {@link #NO_RATING}, {@link #PROVISIONAL_RATING}
   * or {@link #ESTABLISHED_RATING}.
   */

  protected void processBulletRatingChanged(String username, int newRating, int ratingType){

  }



  

  /**
   * Gets called when a DG_BLITZ datagram arrives.
   * The rating type is {@link #NO_RATING}, {@link #PROVISIONAL_RATING}
   * or {@link #ESTABLISHED_RATING}.
   */

  protected void processBlitzRatingChanged(String username, int newRating, int ratingType){

  }

  



  /**
   * Gets called when a DG_STANDARD datagram arrives.
   * The rating type is {@link #NO_RATING}, {@link #PROVISIONAL_RATING}
   * or {@link #ESTABLISHED_RATING}.
   */

  protected void processStandardRatingChanged(String username, int newRating, int ratingType){

  }




  /**
   * Gets called when a DG_WILD datagram arrives.
   * The rating type is {@link #NO_RATING}, {@link #PROVISIONAL_RATING}
   * or {@link #ESTABLISHED_RATING}.
   */

  protected void processWildRatingChanged(String username, int newRating, int ratingType){

  }




  /**
   * Gets called when a DG_BUGHOUSE datagram arrives.
   * The rating type is {@link #NO_RATING}, {@link #PROVISIONAL_RATING}
   * or {@link #ESTABLISHED_RATING}.
   */

  protected void processBughouseRatingChanged(String username, int newRating, int ratingType){

  }




  /**
   * Gets called when a DG_LOSERS datagram arrives.
   * The rating type is {@link #NO_RATING}, {@link #PROVISIONAL_RATING}
   * or {@link #ESTABLISHED_RATING}.
   */

  protected void processLosersRatingChanged(String username, int newRating, int ratingType){

  }





  /**
   * Gets called when a DG_TITLES datagram arrives. This happens rarely,
   * usually when an admin turns his '*' on or off.
   */

  protected void processTitlesChanged(String username, String newTitles){

  }




  /**
   * Gets called when a DG_OPEN datagram arrives.
   */

  protected void processOpenStateChanged(String username, boolean newOpenState){

  }






  /**
   * Gets called when a DG_STATE datagram arrives. Possible player state values are
   * {@link #DOING_NOTHING}, {@link #PLAYING},
   * {@link #EXAMINING} and {@link #PLAYING_SIMUL}.
   */

  protected void processPlayerStateChanged(String username, int newPlayerState, int gameNumber){

  }





  /**
   * Gets called when a DG_GAME_STARTED datagram arrives.
   */
  
  protected void processGameStarted(int gameNumber, String whiteName, String blackName,
    int wildNumber, String ratingCategoryString, boolean isRated, int whiteInitial, int whiteIncrement,
    int blackInitial, int blackIncrement, boolean isPlayedGame, String exString,
    int whiteRating, int blackRating, long gameID, String whiteTitles, String blackTitles,
    boolean isIrregularLegality, boolean isIrregularSemantics, boolean usesPlunkers,
    String fancyTimeControls){

  }






  /**
   * Gets called when a DG_GAME_RESULT datagram arrives.
   */

  protected void processGameResult(int gameNumber, boolean becomesExamined,
    String gameResultCode, String scoreString, String descriptionString){

  }




  /**
   * Gets called when a DG_EXAMINED_GAME_IS_GONE datagram arrives.
   */

  protected void processExaminedGameIsGone(int gameNumber){
    
  }






  /**
   * Gets called when a DG_MY_GAME_STARTED datagram arrives.
   */
  
  protected void processMyGameStarted(int gameNumber, String whiteName, String blackName,
    int wildNumber, String ratingCategoryString, boolean isRated, int whiteInitial, int whiteIncrement,
    int blackInitial, int blackIncrement, boolean isPlayedGame, String exString,
    int whiteRating, int blackRating, long gameID, String whiteTitles, String blackTitles,
    boolean isIrregularLegality, boolean isIrregularSemantics, boolean usesPlunkers,
    String fancyTimeControls){

  }





  /**
   * Gets called when a DG_MY_GAME_RESULT datagram arrives.
   */

  protected void processMyGameResult(int gameNumber, boolean becomesExamined,
    String gameResultCode, String scoreString, String descriptionString){

  }






  /**
   * Gets called when a DG_MY_GAME_ENDED datagram arrives.
   */

  protected void processMyGameEnded(int gameNumber){

  }





  /**
   * Gets called when a DG_STARTED_OBSERVING datagram arrives.
   */
  
  protected void processStartedObserving(int gameNumber, String whiteName, String blackName,
    int wildNumber, String ratingCategoryString, boolean isRated, int whiteInitial, int whiteIncrement,
    int blackInitial, int blackIncrement, boolean isPlayedGame, String exString,
    int whiteRating, int blackRating, long gameID, String whiteTitles, String blackTitles,
    boolean isIrregularLegality, boolean isIrregularSemantics, boolean usesPlunkers,
    String fancyTimeControls){

  }





  /**
   * Gets called when a DG_STOP_OBSERVING datagram arrives.
   */

  protected void processStoppedObserving(int gameNumber){

  }




  /**
   * Gets called when a DG_PLAYERS_IN_MY_GAME datagram arrives.
   *
   * @param gameNumber The number of the game.
   * @param playerName The name of the player.
   * @param playerState The state of the player in the game. Possible values are
   * {@link #DOING_NOTHING} (left the board), {@link #OBSERVING},
   * {@link #PLAYING_WHITE}, {@link #PLAYING_BLACK},
   * {@link #PLAYING_SIMUL_WHITE}, {@link #PLAYING_SIMUL_BLACK} and
   * {@link #EXAMINING}.
   * @param hearsKibitzed True if the player hears kibitzes and whispers, false otherwise.
   */

  protected void processPlayersInMyGame(int gameNumber, String playerName, int playerState,
    boolean hearsKibitzes){

  }






  /**
   * Gets called when a DG_OFFERS_IN_MY_GAME datagram arrives.
   */

  protected void processOffersInMyGame(int gameNumber, boolean whiteDraw, boolean blackDraw,
    boolean whiteAdjourn, boolean blackAdjourn, boolean whiteAbort, boolean blackAbort,
    int whiteTakeback, int blackTakeback){

  }





  /**
   * Gets called when a DG_TAKEBACK datagram arrives.
   */

  protected void processTakeback(int gameNumber, int takebackCount){

  }





  /**
   * Gets called when a DG_BACKWARD datagram arrives.
   */

  protected void processBackward(int gameNumber, int backwardCount){

  }






  /** 
   * Gets called when a DG_SEND_MOVES datagram arrives. 
   */

  protected void processSendMoves(int gameNumber, MoveStruct moveInfo){
    
  }






  /**
   * Gets called when a DG_MOVE_LIST datagram arrives.
   */

  protected void processMoveList(int gameNumber, String initialPosition, MoveStruct [] moves){

  }





  /**
   * Gets called when a DG_KIBITZ datagram arrives. <code>isKibitz</code> is
   * true if it's a kibitz, false if it's a whisper.
   */

  protected void processKibitz(int gameNumber, String playerName, String titles,
    boolean isKibitz, String message){

  }




  /**
   * Gets called when a DG_PEOPLE_IN_MY_CHANNEL datagram arrives.
   * <code>isInChannel==true</code> indicates the player joined a channel,
   * <code>false</code> indicates he left it.
   */

  protected void processPeopleInMyChannel(int channel, String playerName, boolean isInChannel){
    
  }





  /**
   * Gets called when a DG_CHANNEL_TELL datagram arrives. <code>tellType</code>
   * is either {@link #CHANNEL_TELL} or {@link #CHANNEL_ATELL}.
   */

  protected void processChannelTell(int channel, String playerName, String titles,
    String message, int tellType){

  }





  /**
   * Gets called when a DG_MATCH datagram arrives.
   * <code>challengerRatingType</code> and <code>receiverRatingType</code> are
   * {@link #NO_RATING}, {@link #PROVISIONAL_RATING} or
   * {@link #ESTABLISHED_RATING}. <code>colorRequest</code> is
   * {@link #COLORLESS}, {@link #WHITE} or {@link #BLACK}. The
   * <code>assessLoss</code>, <code>assessDraw</code> and <code>assessWin</code>
   * arguments only contain valid values if DG_MATCH_ASSESSMENT is on.
   */

  protected void processMatch(String challengerName, int challengerRating, int challengerRatingType,
    String challengerTitles, String receiverName, int receiverRating, int receiverRatingType,
    String receiverTitles, int wildNumber, String ratingCategoryString, boolean isRated,
    boolean isAdjourned, int challengerTime, int challengerInc, int receiverTime,
    int receiverInc, int colorRequest, int assessLoss, int assessDraw, int assessWin,
    String fancyTimeControls){

  }





  /**
   * Gets called when a DG_MATCH_REMOVED datagram arrives.
   */

  protected void processMatchRemoved(String challengerName, String receiverName, String explanationString){
    
  }





  /**
   * Gets called when a DG_PERSONAL_TELL datagram arrives. <code>tellType</code>
   * is {@link #TELL}, {@link #SAY}, {@link #PTELL}, {@link #QTELL}
   * or {@link #ATELL}.
   */

  protected void processPersonalTell(String playerName, String titles, String message, int tellType){

  }





  /**
   * Gets called when a DG_SHOUT datagram arrives. <code>shoutType</code> is
   * {@link #SHOUT}, {@link #I_SHOUT},
   * {@link #SSHOUT} or {@link #ANNOUNCEMENT}.
   */

  protected void processShout(String playerName, String titles, int shoutType, String message){

  }


  


  
  /**
   * Gets called when a DG_BUGHOUSE_HOLDINGS datagram arrives.
   */

  protected void processBughouseHoldings(int gameNumber, String whiteHoldings, String blackHoldings){

  }





  /**
   * Gets called when a DG_SET_CLOCK datagram arrives.
   */

  protected void processSetClock(int gameNumber, int whiteClock, int blackClock){

  }




  /**
   * Gets called when a DG_FLIP datagram arrives.
   */

  protected void processFlip(int gameNumber, boolean isFlipped){

  }
  




  /**
   * Gets called when a DG_ISOLATED_BOARD datagram arrives.
   */
  
  protected void processIsolatedBoard(int gameNumber, String whiteName, String blackName,
    int wildNumber, String ratingCategoryString, boolean isRated, int whiteInitial, int whiteIncrement,
    int blackInitial, int blackIncrement, boolean isPlayedGame, String exString,
    int whiteRating, int blackRating, long gameID, String whiteTitles, String blackTitles,
    boolean isIrregularLegality, boolean isIrregularSemantics, boolean usesPlunkers,
    String fancyTimeControls){

  }





  /**
   * Gets called when a DG_REFRESH datagram arrives.
   */

  protected void processRefresh(int gameNumber){
    
  }





  /**
   * Gets called when a DG_ILLEGAL_MOVE datagram arrives. Reason codes are
   * given in
   * <A HREF="ftp://ftp.chessclub.com/pub/icc/formats/formats.txt">ftp://ftp.chessclub.com/pub/icc/formats/formats.txt</A>
   */

  protected void processIllegalMove(int gameNumber, String moveString, int reasonCode){

  }





  /**
   * Gets called when a DG_MY_RELATION_TO_GAME datagram arrives.
   * <code>playerState</code> is {@link #DOING_NOTHING} (left the board),
   * {@link #OBSERVING}, {@link #PLAYING_WHITE}, {@link #PLAYING_BLACK},
   * {@link #PLAYING_SIMUL_WHITE}, {@link #PLAYING_SIMUL_BLACK} or
   * {@link #EXAMINING}.
   */

  protected void processMyRelationToGame(int gameNumber, int playerState){

  }





  /**
   * Gets called when a DG_PARTNERSHIP datagram arrives.
   */

  protected void processPartnership(String playerName1, String playerName2, boolean isForming){

  }





  /**
   * Gets called when a DG_SEES_SHOUTS datagram arrives.
   */

  protected void processSeesShouts(String playerName, boolean seesShouts, boolean seesSShouts){

  }





  /**
   * Gets called when a DG_CHANNELS_SHARED datagram arrives.
   */

  protected void processChannelsShared(String playerName, int [] sharedChannels){

  }





  /**
   * Gets called when a DG_MY_VARIABLE datagram arrives.
   */

  protected void processMyVariable(String variableName, int value){

  }





  /**
   * Gets called when a DG_MY_STRING_VARIABLE datagram arrives.
   */

  protected void processMyStringVariable(String variableName, String value){

  }





  /**
   * Gets called when a DG_JBOARD datagram arrives. <code>sideToMove</code> is
   * either {@link #WHITE} or {@link #BLACK}. <code>gameType</code> is 
   * {@link #STORED_GAME}, {@link #EXAMINED_GAME} or
   * {@link #PLAYED_GAME}.
   */

  protected void processJBoard(int gameNumber, String board, int sideToMove, 
    int doublePushFile, boolean canWhiteCastleShort, boolean canWhiteCastleLong,
    boolean canBlackCastleShort, boolean canBlackCastleLong, int nextMoveNum,
    String algebraicLastMove, String smithLastMove, int whiteClock, int blackClock,
    int gameType, boolean isFlipped){

  }





  /**
   * Gets called when a DG_SEEK datagram arrives. <code>ratingType</code> is
   * {@link #NO_RATING}, {@link #PROVISIONAL_RATING} or 
   * {@link #ESTABLISHED_RATING}. <code>color</code> is {@link #WHITE},
   * {@link #BLACK} or {@link #COLORLESS}.
   */

  protected void processSeek(int index, String name, String titles, int rating, int ratingType, int wild,
    String ratingCategoryString, int time, int inc, boolean isRated, int color, int minRating, int maxRating, 
    boolean autoaccept, boolean formula, String fancyTimeControl){

  }





  /**
   * Gets called when a DG_SEEK_REMOVED datagram arrives.
   */

  protected void processSeekRemoved(int index, int reasonCode){

  }





  /**
   * Gets called when a DG_MY_RATING datagram arrives.
   * fishbait says this datagram shouldn't be used - use DG_NEW_MY_RATING instead.
   */

  protected void processMyRating(int bulletRating, int blitzRating, int standardRating,
    int wildRating, int bughouseRating){

  }





  /**
   * Gets called when a DG_SOUND datagram arrives.
   */

  protected void processSound(int code){

  }





  /**
   * Gets called when a DG_PLAYER_ARRIVED_SIMPLE datagram arrives.
   */

  protected void processPlayerArrivedSimple(String playerName){

  }




  /**
   * Gets called when a DG_MSEC datagram arrives. <code>color</code> is either
   * {@link #WHITE} or {@link #BLACK}.
   */

  protected void processMsec(int gameNumber, int color, int msec, boolean isRunning){

  }





  /**
   * Gets called when a DG_BUGHOUSE_PASS datagram arrives. </code>color</code>
   * is either {@link #WHITE} or {@link #BLACK}.
   */

  protected void processBughousePass(int gameNumber, int color, String piece){

  }




  /**
   * Gets called when a DG_CIRCLE datagram arrives.
   */

  protected void processCircle(int gameNumber, String examiner, String coordinate){

  }




  /**
   * Gets called when a DG_ARROW datagram arrives.
   */

  protected void processArrow(int gameNumber, String examiner, String origin, String destination){

  }




  /**
   * Gets called when a DG_MORETIME datagram arrives. <code>color</code> is
   * either {@link #WHITE} or {@link #BLACK}.
   */

  protected void processMoretime(int gameNumber, int color, int seconds){

  }





  /**
   * Gets called when a DG_PERSONAL_TELL_ECHO datagram arrives.
   * <code>tellType</code> is {@link #TELL}, {@link #SAY}, {@link #PTELL},
   * {@link #QTELL} or {@link #ATELL}.
   */

  protected void processPersonalTellEcho(String receiverName, String message, int tellType){

  }





  /**
   * Gets called when a DG_SUGGESTION datagram arrives.
   */

  protected void processSuggestion(String commandString, String textString, int priority,
    String suggester, String subject, String id){

  }





  /**
   * Gets called when a player on our notify list arrives. The player field
   * contains the same optional fields like in
   * <code>processPlayerArrived</code>.
   */

  protected void processNotifyArrived(Player player){

  }




  /**
   * Gets called when a player on our notify list departs.
   */

  protected void processNotifyLeft(String playerName){

  }





  /**
   * Gets called when the open state of someone on your notify list changes.
   */

  protected void processNotifyOpen(String playerName, boolean isOpenNow){
    
  }




  /**
   * Gets called when a DG_NOTIFY_STATE datagram arrives. Possible player state
   * values are {@link #DOING_NOTHING}, {@link #PLAYING}, {@link #EXAMINING}
   * and {@link #PLAYING_SIMUL}.
   */

  protected void processNotifyState(String username, int newPlayerState, int gameNumber){

  }




  /**
   * Gets called when a DG_MY_NOTIFY_LIST datagram arrives. <code>added</code>
   * is true when the player was added to the list and false when he was
   * removed.
   */

  protected void processMyNotifyList(String name, boolean added){

  }





  /**
   * Gets called when a DG_LOGIN_FAILED datagram arrives.
   */

  protected void processLoginFailed(int errorCode, String explanation){
    
  }





  /**
   * Gets called when a DG_FEN datagram arrives.
   * <A HREF="http://www.very-best.de/pgn-spec.htm#16.1">http://www.very-best.de/pgn-spec.htm#16.1</A>
   * has a description of FEN.
   */

  protected void processFEN(int gameNumber, String fenString){

  }




  /**
   * Gets called when a DG_GAMELIST_BEGIN datagram arrives.
   */

  protected void processGamelistBegin(String command, String argsString, int hitsCount, 
    int firstIndex, int lastIndex, String summary){

  }





  /**
   * Gets called when a DG_GAMELIST_ITEM datagram arrives. <code>color</code>
   * is either {@link #WHITE} or {@link #BLACK}. Note that color
   * is the player who LOST, not the one who WON in case of status=win. Note also
   * that the server sends black=0, white=1, when usually it's vice versa (but
   * it's converted into a defined constant). The ratings are -1 if unrated.
   */

  protected void processGamelistItem(int index, String id, String event, String date,
    String time, String whiteName, int whiteRating, String blackName, int blackRating,
    boolean isRated, int ratingCategory, int wildType, int whiteInit, int whiteInc,
    int blackInit, int blackInc, String eco, int status, int color, int mode, String note,
    boolean isHere){

  }





  /**
   * Gets called when a DG_IDLE datagram arrives.
   */

  protected void processIdle(String name, int idleTime, int timeSinceRead){

  }





  /**
   * Gets called when a DG_ACK_PING datagram arrives.
   */

  protected void processAckPing(String name, int lag){

  }






  /**
   * Gets called when a DG_RATING_TYPE_KEY datagram arrives.
   */

  protected void processRatingTypeKey(int index, String ratingCategoryString){

  }




  /**
   * Gets called when a DG_GAME_MESSAGE datagram arrives.
   */

  protected void processGameMessage(int gameNumber, String string){

  }





  /**
   * Gets called when a DG_STRINGLIST_BEGIN datagram arrives.
   */

  protected void processStringlistBegin(int amount, String description){
    
  }





  /**
   * Gets called when a DG_STRINGLIST_ITEM datagram arrives.
   */

  protected void processStringlistItem(String string){

  }





  /**
   * Gets called when a DG_DUMMY_RESPONSE datagram arrives.
   */

  protected void processDummyResponse(){

  }





  /**
   * Gets called when a DG_CHANNEL_QTELL datagram arrives.
   */

  protected void processChannelQTell(int channel, String name, String titles, String message){

  }





  /**
   * Gets called when a DG_PERSONAL_QTELL datagram arrives.
   */

  protected void processPersonalQTell(String name, String titles, String message){

  }





  /**
   * Gets called when a DG_SET_BOARD datagram arrives. <code>sideToMove</code>
   * is either {@link #BLACK} or {@link #WHITE}.
   */

  protected void processSetBoard(int gameNumber, String board, int sideToMove){

  }




  /**
   * Gets called when a DG_LOG_PGN datagram arrives.
   */

  protected void processLogPGN(String [] pgnLines){

  }






  /**
   * Gets called when a DG_NEW_MY_RATING datagram arrives.
   */

  protected void processNewMyRating(int [] ratings){

  }






  /**
   * Gets called when a DG_UNCIRCLE datagram arrives.
   */

  protected void processUncircle(int gameNumber, String examiner, String coordinate){

  }




  /**
   * Gets called when a DG_UNARROW datagram arrives.
   */

  protected void processUnarrow(int gameNumber, String examiner, String origin, String destination){

  }
  





  /**
   * Gets called when a DG_WSUGGEST datagram arrives.
   */

  protected void processWSuggest(String url, String text, int priority, String suggester, 
    String subject, String id){

  }




  /**
   * Gets called when a DG_MESSAGELIST_BEGIN datagram arrives.
   */

  protected void processMessagelistBegin(String commandString){

  }





  /**
   * Gets called when a DG_MESSAGELIST_ITEM datagram arrives.
   */

  protected void processMessagelistItem(int index, String sender, String time, String date, String message){

  }





  /**
   * Gets called when a DG_LIST datagram arrives.
   */

  protected void processList(String header, String rowStart, String [] items){

  }



  /**
   * Gets called when a DG_SJI_AD datagram arrives.
   */

  protected void processSJIad(String ad){

  }



  /**
   * Gets called when a DG_RETRACT datagram arrives.
   */

  protected void processRetract(String id){

  }





  /**
   * Gets called when a DG_MY_GAME_CHANGE datagram arrives.
   */
  
  protected void processMyGameChange(int gameNumber, String whiteName, String blackName,
    int wildNumber, String ratingCategoryString, boolean isRated, int whiteInitial, int whiteIncrement,
    int blackInitial, int blackIncrement, boolean isPlayedGame, String exString,
    int whiteRating, int blackRating, long gameID, String whiteTitles, String blackTitles,
    boolean isIrregularLegality, boolean isIrregularSemantics, boolean usesPlunkers,
    String fancyTimeControls){

  }




  /**
   * Gets called when a DG_POSITION_BEGIN datagram arrives.
   */

  protected void processPositionBegin(int gameNumber, String initFEN, int numMovesToFollow){

  }





  /**
   * Gets called when a DG_TOURNEY datagram arrives.
   */

  protected void processTourney(int id, boolean canGuestsWatchJoin, boolean makeNewWindowOnJoin, boolean makeNewWindowOnWatch,
    boolean makeNewWindowOnInfo, String description, String [] joinCommands, String [] watchCommands, String [] infoCommands,
    String confirmText){

  }





  /**
   * Gets called when a DG_REMOVE_TOURNEY datagram arrives.
   */

  protected void processRemoveTourney(int id){

  }





  /**
   * Gets called when a DG_DIALOG_START datagram arrives.
   */

  protected void processDialogStart(int sizeInBytes){

  }




  /**
   * Gets called when a DG_DIALOG_DATA datagram arrives.
   */

  protected void processDialogData(String data){

  }




  /**
   * Gets called when a DG_DIALOG_DEFAULT datagram arrives.
   */

  protected void processDialogDefault(int controlNumber, String type, String value){

  }





  /**
   * Gets called when a DG_DIALOG_END datagram arrives.
   */

  protected void processDialogEnd(boolean isModal, int focusedControlIndex, String commandOnOK,
    String commandOnCancel, String command3, String command4){

  }





  /**
   * Gets called when a DG_DIALOG_RELEASE datagram arrives.
   */

  protected void processDialogRelease(){

  }





  /**
   * Gets called when a DG_POSITION_BEGIN2 datagram arrives.
   */

  protected void processPositionBegin2(int gameNumber, String initFEN, int numMovesToFollow){

  }





  /**
   * Gets called when a DG_PAST_MOVE datagram arrives.
   */

  protected void processPastMove(int gameNumber, MoveStruct moveInfo){

  }




  /**
   * Gets called when a DG_PGN_TAG datagram arrives.
   */

  protected void processPGNTag(int gameNumber, String tagName, String value){

  }




  /**
   * Gets called when a DG_PASSWORD datagram arrives.
   */

  protected void processPassword(String newPassword, String handle){

  }





  /**
   * Gets called when a DG_WILD_KEY datagram arrives.
   */

  protected void processWildKey(int index, String wildName){

  }





  /**
   * Gets called when a DG_SET2 datagram arrives.
   */

  protected void processSet2(int dgNumber, boolean state){

  }







  /**
   * This method is called to process a datagram. It calls the appropriate 
   * processXXX method depending on the type of the datagram.
   */
  
  protected void processDatagram(Datagram datagram){
    switch (datagram.getType()){
      case Datagram.DG_WHO_AM_I:{
        processWhoAmI(datagram.getString(0), datagram.getString(1));
        break;
      }
      case Datagram.DG_PLAYER_ARRIVED:{
        int i=0;
        String username = datagram.getString(i++);

        int [] ratings = new int[RATING_CATEGORIES_COUNT];
        int [] ratingTypes = new int[RATING_CATEGORIES_COUNT];

        if (isDGOn(Datagram.DG_BULLET)){
          ratings[BULLET] = datagram.getInteger(i++);
          ratingTypes[BULLET] = convertRatingType(datagram.getInteger(i++));
        }

        if (isDGOn(Datagram.DG_BLITZ)){
          ratings[BLITZ] = datagram.getInteger(i++);
          ratingTypes[BLITZ] = convertRatingType(datagram.getInteger(i++));
        }

        if (isDGOn(Datagram.DG_STANDARD)){
          ratings[STANDARD] = datagram.getInteger(i++);
          ratingTypes[STANDARD] = convertRatingType(datagram.getInteger(i++));
        }

        if (isDGOn(Datagram.DG_WILD)){
          ratings[WILD] = datagram.getInteger(i++);
          ratingTypes[WILD] = convertRatingType(datagram.getInteger(i++));
        }

        if (isDGOn(Datagram.DG_BUGHOUSE)){
          ratings[BUGHOUSE] = datagram.getInteger(i++);
          ratingTypes[BULLET] = convertRatingType(datagram.getInteger(i++));
        }

        if (isDGOn(Datagram.DG_LOSERS)){
          ratings[LOSERS] = datagram.getInteger(i++);
          ratingTypes[LOSERS] = convertRatingType(datagram.getInteger(i++));
        }

        int timestampClientNumber = -1;
        if (isDGOn(Datagram.DG_TIMESTAMP)){
          timestampClientNumber = datagram.getInteger(i++);
        }

        String titles = null;
        if (isDGOn(Datagram.DG_TITLES)){
          titles = datagram.getString(i++);
        }

        boolean isOpen = false;
        if (isDGOn(Datagram.DG_OPEN)){
          isOpen = datagram.getBoolean(i++);
        }

        int playerState = -1;
        int gameNumber = -1;
        if (isDGOn(Datagram.DG_STATE)){
          playerState = convertPlayerState(datagram.getString(i++));
          gameNumber = datagram.getInteger(i++);
        }

        processPlayerArrived(new Player(username, titles, ratings, ratingTypes, timestampClientNumber, isOpen, playerState, gameNumber));
        break;
      }
      case Datagram.DG_PLAYER_LEFT:{
        processPlayerLeft(datagram.getString(0));
        break;
      }
      case Datagram.DG_BULLET:{
        processBulletRatingChanged(datagram.getString(0),datagram.getInteger(1),convertRatingType(datagram.getInteger(2)));
        break;
      }
      case Datagram.DG_BLITZ:{
        processBlitzRatingChanged(datagram.getString(0),datagram.getInteger(1),convertRatingType(datagram.getInteger(2)));
        break;
      }
      case Datagram.DG_STANDARD:{
        processStandardRatingChanged(datagram.getString(0),datagram.getInteger(1),convertRatingType(datagram.getInteger(2)));
        break;
      }
      case Datagram.DG_WILD:{
        processWildRatingChanged(datagram.getString(0),datagram.getInteger(1),convertRatingType(datagram.getInteger(2)));
        break;
      }
      case Datagram.DG_BUGHOUSE:{
        processBughouseRatingChanged(datagram.getString(0),datagram.getInteger(1),convertRatingType(datagram.getInteger(2)));
        break;
      }
      case Datagram.DG_LOSERS:{
        processLosersRatingChanged(datagram.getString(0),datagram.getInteger(1),convertRatingType(datagram.getInteger(2)));
        break;
      }
      case Datagram.DG_TIMESTAMP:{
        throw new IllegalStateException("DG_TIMESTAMP should never be sent");
      }
      case Datagram.DG_TITLES:{
        processTitlesChanged(datagram.getString(0),datagram.getString(1));
        break;
      }
      case Datagram.DG_OPEN:{
        processOpenStateChanged(datagram.getString(0),datagram.getBoolean(1));
        break;
      }
      case Datagram.DG_STATE:{
        processPlayerStateChanged(datagram.getString(0),convertPlayerState(datagram.getString(1)),datagram.getInteger(2));
        break;
      }
      case Datagram.DG_GAME_STARTED:{
        int gameNumber = datagram.getInteger(0);
        String whiteName = datagram.getString(1);
        String blackName = datagram.getString(2);
        int wildNumber = datagram.getInteger(3);
        String ratingCategoryString = datagram.getString(4);
        boolean isRated = datagram.getBoolean(5);
        int whiteInitial = datagram.getInteger(6);
        int whiteIncrement = datagram.getInteger(7);
        int blackInitial = datagram.getInteger(8);
        int blackIncrement = datagram.getInteger(9);
        boolean isPlayedGame = datagram.getBoolean(10);
        String exString = datagram.getString(11);
        int whiteRating = datagram.getInteger(12);
        int blackRating = datagram.getInteger(13);
        long gameID = datagram.getLong(14);
        String whiteTitles = datagram.getString(15);
        String blackTitles = datagram.getString(16);
        boolean isIrregularLegality = datagram.getBoolean(17);
        boolean isIrregularSemantics = datagram.getBoolean(18);
        boolean usesPlunkers = datagram.getBoolean(19);
        String fancyTimeControls = datagram.getString(20);
        processGameStarted(gameNumber,whiteName,blackName,wildNumber,ratingCategoryString,isRated,whiteInitial,
          whiteIncrement,blackInitial,blackIncrement,isPlayedGame,exString,whiteRating,blackRating,
          gameID,whiteTitles,blackTitles,isIrregularLegality,isIrregularSemantics,usesPlunkers,
          fancyTimeControls);
        break;
      }
      case Datagram.DG_GAME_RESULT:{
        int gameNumber = datagram.getInteger(0);
        boolean becomesExamined = datagram.getBoolean(1);
        String gameResultCode = datagram.getString(2);
        String scoreString = datagram.getString(3);
        String descriptionString = datagram.getString(4);
        processGameResult(gameNumber,becomesExamined,gameResultCode,scoreString,descriptionString);
        break;
      }
      case Datagram.DG_EXAMINED_GAME_IS_GONE:{
        processExaminedGameIsGone(datagram.getInteger(0));
        break;
      }
      case Datagram.DG_MY_GAME_STARTED:{
        int gameNumber = datagram.getInteger(0);
        String whiteName = datagram.getString(1);
        String blackName = datagram.getString(2);
        int wildNumber = datagram.getInteger(3);
        String ratingCategoryString = datagram.getString(4);
        boolean isRated = datagram.getBoolean(5);
        int whiteInitial = datagram.getInteger(6);
        int whiteIncrement = datagram.getInteger(7);
        int blackInitial = datagram.getInteger(8);
        int blackIncrement = datagram.getInteger(9);
        boolean isPlayedGame = datagram.getBoolean(10);
        String exString = datagram.getString(11);
        int whiteRating = datagram.getInteger(12);
        int blackRating = datagram.getInteger(13);
        long gameID = datagram.getLong(14);
        String whiteTitles = datagram.getString(15);
        String blackTitles = datagram.getString(16);
        boolean isIrregularLegality = datagram.getBoolean(17);
        boolean isIrregularSemantics = datagram.getBoolean(18);
        boolean usesPlunkers = datagram.getBoolean(19);
        String fancyTimeControls = datagram.getString(20);
        processMyGameStarted(gameNumber,whiteName,blackName,wildNumber,ratingCategoryString,isRated,whiteInitial,
          whiteIncrement,blackInitial,blackIncrement,isPlayedGame,exString,whiteRating,blackRating,
          gameID,whiteTitles,blackTitles,isIrregularLegality,isIrregularSemantics,usesPlunkers,
          fancyTimeControls);
        break;
      }
      case Datagram.DG_MY_GAME_RESULT:{
        int gameNumber = datagram.getInteger(0);
        boolean becomesExamined = datagram.getBoolean(1);
        String gameResultCode = datagram.getString(2);
        String scoreString2 = datagram.getString(3);
        String descriptionString = datagram.getString(4);
        processMyGameResult(gameNumber,becomesExamined,gameResultCode,scoreString2,descriptionString);
        break;
      }
      case Datagram.DG_MY_GAME_ENDED:{
        processMyGameEnded(datagram.getInteger(0));
        break;
      }
      case Datagram.DG_STARTED_OBSERVING:{
        int gameNumber = datagram.getInteger(0);
        String whiteName = datagram.getString(1);
        String blackName = datagram.getString(2);
        int wildNumber = datagram.getInteger(3);
        String ratingCategoryString = datagram.getString(4);
        boolean isRated = datagram.getBoolean(5);
        int whiteInitial = datagram.getInteger(6);
        int whiteIncrement = datagram.getInteger(7);
        int blackInitial = datagram.getInteger(8);
        int blackIncrement = datagram.getInteger(9);
        boolean isPlayedGame = datagram.getBoolean(10);
        String exString = datagram.getString(11);
        int whiteRating = datagram.getInteger(12);
        int blackRating = datagram.getInteger(13);
        long gameID = datagram.getLong(14);
        String whiteTitles = datagram.getString(15);
        String blackTitles = datagram.getString(16);
        boolean isIrregularLegality = datagram.getBoolean(17);
        boolean isIrregularSemantics = datagram.getBoolean(18);
        boolean usesPlunkers = datagram.getBoolean(19);
        String fancyTimeControls = datagram.getString(20);
        processStartedObserving(gameNumber,whiteName,blackName,wildNumber,ratingCategoryString,isRated,whiteInitial,
          whiteIncrement,blackInitial,blackIncrement,isPlayedGame,exString,whiteRating,blackRating,
          gameID,whiteTitles,blackTitles,isIrregularLegality,isIrregularSemantics,usesPlunkers,
          fancyTimeControls);
        break;
      }
      case Datagram.DG_STOP_OBSERVING:{
        processStoppedObserving(datagram.getInteger(0));
        break;
      }
      case Datagram.DG_PLAYERS_IN_MY_GAME:{
        int gameNumber = datagram.getInteger(0);
        String playerName = datagram.getString(1);
        int playerState = convertPlayerState(datagram.getString(2));
        boolean hearsKibitzes = datagram.getBoolean(3);
        processPlayersInMyGame(gameNumber,playerName,playerState,hearsKibitzes);
        break;
      }
      case Datagram.DG_OFFERS_IN_MY_GAME:{
        int gameNumber = datagram.getInteger(0);
        boolean whiteDraw = datagram.getBoolean(1);
        boolean blackDraw = datagram.getBoolean(2);
        boolean whiteAdjourn = datagram.getBoolean(3);
        boolean blackAdjourn = datagram.getBoolean(4);
        boolean whiteAbort = datagram.getBoolean(5);
        boolean blackAbort = datagram.getBoolean(6);
        int whiteTakeback = datagram.getInteger(7);
        int blackTakeback = datagram.getInteger(8);
        processOffersInMyGame(gameNumber,whiteDraw,blackDraw,whiteAdjourn,blackAdjourn,
          whiteAbort,blackAbort,whiteTakeback,blackTakeback);
        break;
      }
      case Datagram.DG_TAKEBACK:{
        processTakeback(datagram.getInteger(0),datagram.getInteger(1));
        break;
      }
      case Datagram.DG_BACKWARD:{
        processBackward(datagram.getInteger(0),datagram.getInteger(1));
        break;
      }
      case Datagram.DG_SEND_MOVES:{
        int i = 0;
        int gameNumber = datagram.getInteger(i++);

        String algebraicMove = null;
        if (isDGOn(Datagram.DG_MOVE_ALGEBRAIC))
          algebraicMove = datagram.getString(i++);

        String smithMove = null;
        if (isDGOn(Datagram.DG_MOVE_SMITH))
          smithMove = datagram.getString(i++);

        int time = -1;
        if (isDGOn(Datagram.DG_MOVE_TIME))
          time = datagram.getInteger(i++);

        int clock = -1;
        if (isDGOn(Datagram.DG_MOVE_CLOCK))
          clock = datagram.getInteger(i++);

        int variationCode = -1;
        if (isDGOn(Datagram.DG_IS_VARIATION))
          variationCode = datagram.getInteger(i++);

        processSendMoves(gameNumber, new MoveStruct(algebraicMove, smithMove, time, clock, variationCode));
        break;
      }
      case Datagram.DG_MOVE_LIST:{
        int gameNumber = datagram.getInteger(0);
        String initialPosition = datagram.getString(1);
        String movesString = (datagram.getArgumentCount() >= 3) ? datagram.getString(2) : "";
        StringTokenizer moveListTokenizer = new StringTokenizer(movesString,"{}");
        MoveStruct [] moves = new MoveStruct[moveListTokenizer.countTokens()];
        int movesCounter = 0;
        while (moveListTokenizer.hasMoreTokens()){
          StringTokenizer moveTokenizer = new StringTokenizer(moveListTokenizer.nextToken());
          String algebraicMove = null;
          if (isDGOn(Datagram.DG_MOVE_ALGEBRAIC))
            algebraicMove = moveTokenizer.nextToken();

          String smithMove = null;
          if (isDGOn(Datagram.DG_MOVE_SMITH))
            smithMove = moveTokenizer.nextToken();

          int time = -1;
          if (isDGOn(Datagram.DG_MOVE_TIME))
            time = Integer.parseInt(moveTokenizer.nextToken());

          int clock = -1;
          if (isDGOn(Datagram.DG_MOVE_CLOCK))
            clock = Integer.parseInt(moveTokenizer.nextToken());

          int variationCode = -1;
          if (isDGOn(Datagram.DG_IS_VARIATION))
            variationCode = Integer.parseInt(moveTokenizer.nextToken());

          moves[movesCounter++] = new MoveStruct(algebraicMove,smithMove,time,clock,variationCode);
        }
        processMoveList(gameNumber,initialPosition,moves);
        break;
      }
      case Datagram.DG_KIBITZ:{
        processKibitz(datagram.getInteger(0),datagram.getString(1),datagram.getString(2),datagram.getBoolean(3),datagram.getString(4));
        break;
      }
      case Datagram.DG_PEOPLE_IN_MY_CHANNEL:{
        processPeopleInMyChannel(datagram.getInteger(0),datagram.getString(1),datagram.getBoolean(2));
        break;
      }
      case Datagram.DG_CHANNEL_TELL:{
        int channel = datagram.getInteger(0);
        String playerName = datagram.getString(1);
        String titles = datagram.getString(2);
        String message = datagram.getString(3);
        processChannelTell(channel, playerName, titles, message,
          convertChannelTellType(datagram.getInteger(4)));
        break;
      }
      case Datagram.DG_MATCH:{
        String challengerName = datagram.getString(0);
        int challengerRating = datagram.getInteger(1);
        int challengerRatingType = convertRatingType(datagram.getInteger(2));
        String challengerTitles = datagram.getString(3);
        String receiverName = datagram.getString(4);
        int receiverRating = datagram.getInteger(5);
        int receiverRatingType = convertRatingType(datagram.getInteger(6));
        String receiverTitles = datagram.getString(7);
        int wildNumber = datagram.getInteger(8);
        String ratingCategoryString = datagram.getString(9);
        boolean isRated = datagram.getBoolean(10);
        boolean isAdjourned = datagram.getBoolean(11);
        int challengerTime = datagram.getInteger(12);
        int challengerInc = datagram.getInteger(13);
        int receiverTime = datagram.getInteger(14);
        int receiverInc = datagram.getInteger(15);
        int colorRequest = convertColorPreference(datagram.getInteger(16));

        int i = 17;
        int assessLoss = 0;
        int assessDraw = 0;
        int assessWin = 0;
        if (isDGOn(Datagram.DG_MATCH_ASSESSMENT)){
          assessLoss = datagram.getInteger(i++);
          assessDraw = datagram.getInteger(i++);
          assessWin = datagram.getInteger(i++);
        }

        String fancyTimeControls = datagram.getString(i++);

        processMatch(challengerName,challengerRating,challengerRatingType,challengerTitles,
          receiverName,receiverRating,receiverRatingType,receiverTitles,wildNumber,
          ratingCategoryString,isRated,isAdjourned,challengerTime,challengerInc,receiverTime,
          receiverInc,colorRequest,assessLoss,assessDraw,assessWin,fancyTimeControls);
        break;
      }
      case Datagram.DG_MATCH_REMOVED:{
        processMatchRemoved(datagram.getString(0),datagram.getString(1),datagram.getString(2));
        break;
      }
      case Datagram.DG_PERSONAL_TELL:{
        processPersonalTell(datagram.getString(0),datagram.getString(1),datagram.getString(2),
          convertPersonalTellType(datagram.getInteger(3)));
        break;
      }
      case Datagram.DG_SHOUT:{
        processShout(datagram.getString(0),datagram.getString(1),convertShoutType(datagram.getInteger(2)),
          datagram.getString(3));
        break;
      }
      case Datagram.DG_MOVE_ALGEBRAIC:{
        throw new IllegalStateException("DG_MOVE_ALGEBRAIC should never be sent");
      }
      case Datagram.DG_MOVE_SMITH:{
        throw new IllegalStateException("DG_MOVE_SMITH should never be sent");
      }
      case Datagram.DG_MOVE_TIME:{
        throw new IllegalStateException("DG_MOVE_TIME should never be sent");
      }
      case Datagram.DG_MOVE_CLOCK:{
        throw new IllegalStateException("DG_MOVE_CLOCK should never be sent");
      }
      case Datagram.DG_BUGHOUSE_HOLDINGS:{
        processBughouseHoldings(datagram.getInteger(0),datagram.getString(1),datagram.getString(2));
        break;
      }
      case Datagram.DG_SET_CLOCK:{
        processSetClock(datagram.getInteger(0),datagram.getInteger(1),datagram.getInteger(2));
        break;
      }
      case Datagram.DG_FLIP:{
        processFlip(datagram.getInteger(0),datagram.getBoolean(1));
        break;
      }
      case Datagram.DG_ISOLATED_BOARD:{
        int gameNumber = datagram.getInteger(0);
        String whiteName = datagram.getString(1);
        String blackName = datagram.getString(2);
        int wildNumber = datagram.getInteger(3);
        String ratingCategoryString = datagram.getString(4);
        boolean isRated = datagram.getBoolean(5);
        int whiteInitial = datagram.getInteger(6);
        int whiteIncrement = datagram.getInteger(7);
        int blackInitial = datagram.getInteger(8);
        int blackIncrement = datagram.getInteger(9);
        boolean isPlayedGame = datagram.getBoolean(10);
        String exString = datagram.getString(11);
        int whiteRating = datagram.getInteger(12);
        int blackRating = datagram.getInteger(13);
        long gameID = datagram.getLong(14);
        String whiteTitles = datagram.getString(15);
        String blackTitles = datagram.getString(16);
        boolean isIrregularLegality = datagram.getBoolean(17);
        boolean isIrregularSemantics = datagram.getBoolean(18);
        boolean usesPlunkers = datagram.getBoolean(19);
        String fancyTimeControls = datagram.getString(20);
        processIsolatedBoard(gameNumber,whiteName,blackName,wildNumber,ratingCategoryString,isRated,whiteInitial,
          whiteIncrement,blackInitial,blackIncrement,isPlayedGame,exString,whiteRating,blackRating,
          gameID,whiteTitles,blackTitles,isIrregularLegality,isIrregularSemantics,usesPlunkers,
          fancyTimeControls);
        break;
      }
      case Datagram.DG_REFRESH:{
        processRefresh(datagram.getInteger(0));
        break;
      }
      case Datagram.DG_ILLEGAL_MOVE:{
        processIllegalMove(datagram.getInteger(0),datagram.getString(1),datagram.getInteger(2));
        break;
      }
      case Datagram.DG_MY_RELATION_TO_GAME:{
        processMyRelationToGame(datagram.getInteger(0),convertPlayerState(datagram.getString(1)));
        break;
      }
      case Datagram.DG_PARTNERSHIP:{
        processPartnership(datagram.getString(0),datagram.getString(1),datagram.getBoolean(2));
        break;
      }
      case Datagram.DG_SEES_SHOUTS:{
        int bitVector = datagram.getInteger(1);
        boolean seesShouts = (bitVector&1)!=0;
        boolean seesSShouts = (bitVector&2)!=0;
        processSeesShouts(datagram.getString(0),seesShouts,seesSShouts);
        break;
      }
      case Datagram.DG_CHANNELS_SHARED:{
        int [] channels = new int[datagram.getArgumentCount()-1];
        for (int i=0;i<channels.length;i++)
          channels[i] = datagram.getInteger(i+1);

        processChannelsShared(datagram.getString(0),channels);
        break;
      }
      case Datagram.DG_MY_VARIABLE:{
        processMyVariable(datagram.getString(0),datagram.getInteger(1));
        break;
      }
      case Datagram.DG_MY_STRING_VARIABLE:{
        processMyStringVariable(datagram.getString(0),datagram.getString(1));
        break;
      }
      case Datagram.DG_JBOARD:{
        int gameNumber = datagram.getInteger(0);
        String board = datagram.getString(1);
        int sideToMove = datagram.getString(2).equals("W") ? WHITE : BLACK;
        int doublePushFile = datagram.getInteger(3);
        boolean canWhiteCastleShort = datagram.getBoolean(4);
        boolean canWhiteCastleLong = datagram.getBoolean(5);
        boolean canBlackCastleShort = datagram.getBoolean(6);
        boolean canBlackCastleLong = datagram.getBoolean(7);
        int nextMoveNum = datagram.getInteger(8);
        String algebraicLastMove = datagram.getString(9);
        String smithLastMove = datagram.getString(10);
        int whiteClock = datagram.getInteger(11);
        int blackClock = datagram.getInteger(12);
        int gameType = convertGameType(datagram.getInteger(13));
        boolean isFlipped = datagram.getBoolean(14);

        processJBoard(gameNumber, board, sideToMove, doublePushFile, canWhiteCastleShort,
          canWhiteCastleLong, canBlackCastleShort, canBlackCastleLong, nextMoveNum,
          algebraicLastMove, smithLastMove, whiteClock, blackClock, gameType, isFlipped);
        break;
      }
      case Datagram.DG_SEEK:{
        int index = datagram.getInteger(0);
        String name = datagram.getString(1);
        String titles = datagram.getString(2);
        int rating = datagram.getInteger(3);
        int ratingType = convertRatingType(datagram.getInteger(4));
        int wild = datagram.getInteger(5);
        String ratingCategoryString = datagram.getString(6);
        int time = datagram.getInteger(7);
        int inc = datagram.getInteger(8);
        boolean isRated = datagram.getBoolean(9);
        int colorPreference = convertColorPreference(datagram.getInteger(10));
        int minRating = datagram.getInteger(11);
        int maxRating = datagram.getInteger(12);
        boolean autoAccept = datagram.getBoolean(13);
        boolean formula = datagram.getBoolean(14);
        String fancyTimeControl = datagram.getString(15);
        processSeek(index,name,titles,rating,ratingType,wild,ratingCategoryString,time,inc,
          isRated,colorPreference,minRating,maxRating,autoAccept,formula,fancyTimeControl);
        break;
      }
      case Datagram.DG_SEEK_REMOVED:{
        processSeekRemoved(datagram.getInteger(0),datagram.getInteger(1));
        break;
      }
      case Datagram.DG_MY_RATING:{
        processMyRating(datagram.getInteger(0),datagram.getInteger(1),datagram.getInteger(2),
          datagram.getInteger(3),datagram.getInteger(4));
        break;
      }
      case Datagram.DG_SOUND:{
        processSound(datagram.getInteger(0));
        break;
      }
      case Datagram.DG_PLAYER_ARRIVED_SIMPLE:{
        processPlayerArrivedSimple(datagram.getString(0));  
        break;
      }
      case Datagram.DG_MSEC:{
        int gameNumber = datagram.getInteger(0);
        int color = datagram.getString(1).equals("W") ? WHITE : BLACK;
        int msec = datagram.getInteger(2);
        boolean isRunning = datagram.getBoolean(3);
        processMsec(gameNumber,color,msec,isRunning);
        break;
      }
      case Datagram.DG_BUGHOUSE_PASS:{
        int gameNumber = datagram.getInteger(0);
        int color = datagram.getString(1).equals("W") ? WHITE : BLACK;
        String piece = datagram.getString(2);        
        processBughousePass(gameNumber,color,piece);
        break;
      }
      case Datagram.DG_IP:{
        throw new IllegalStateException("Bert Enderton says this DG is undocumented intentionally");
      }
      case Datagram.DG_CIRCLE:{
        processCircle(datagram.getInteger(0),datagram.getString(1),datagram.getString(2));
        break;
      }
      case Datagram.DG_ARROW:{
        processArrow(datagram.getInteger(0),datagram.getString(1),datagram.getString(2),datagram.getString(3));
        break;
      }
      case Datagram.DG_MORETIME:{
        int gameNumber = datagram.getInteger(0);
        int color = datagram.getString(1).equals("W") ? WHITE : BLACK;
        int seconds = datagram.getInteger(2);
        processMoretime(gameNumber,color,seconds);
        break;
      }
      case Datagram.DG_PERSONAL_TELL_ECHO:{
        processPersonalTellEcho(datagram.getString(0),datagram.getString(1),
          convertPersonalTellType(datagram.getInteger(2)));
        break;
      }
      case Datagram.DG_SUGGESTION:{
        processSuggestion(datagram.getString(0),datagram.getString(1),datagram.getInteger(2),
          datagram.getString(3),datagram.getString(4),datagram.getString(5));
        break;
      }
      case Datagram.DG_NOTIFY_ARRIVED:{
        int i=0;
        String username = datagram.getString(i++);

        int [] ratings = new int[RATING_CATEGORIES_COUNT];
        int [] ratingTypes = new int[RATING_CATEGORIES_COUNT];

        if (isDGOn(Datagram.DG_BULLET)){
          ratings[BULLET] = datagram.getInteger(i++);
          ratingTypes[BULLET] = convertRatingType(datagram.getInteger(i++));
        }

        if (isDGOn(Datagram.DG_BLITZ)){
          ratings[BLITZ] = datagram.getInteger(i++);
          ratingTypes[BLITZ] = convertRatingType(datagram.getInteger(i++));
        }

        if (isDGOn(Datagram.DG_STANDARD)){
          ratings[STANDARD] = datagram.getInteger(i++);
          ratingTypes[STANDARD] = convertRatingType(datagram.getInteger(i++));
        }

        if (isDGOn(Datagram.DG_WILD)){
          ratings[WILD] = datagram.getInteger(i++);
          ratingTypes[WILD] = convertRatingType(datagram.getInteger(i++));
        }

        if (isDGOn(Datagram.DG_BUGHOUSE)){
          ratings[BUGHOUSE] = datagram.getInteger(i++);
          ratingTypes[BULLET] = convertRatingType(datagram.getInteger(i++));
        }

        if (isDGOn(Datagram.DG_LOSERS)){
          ratings[LOSERS] = datagram.getInteger(i++);
          ratingTypes[LOSERS] = convertRatingType(datagram.getInteger(i++));
        }

        int timestampClientNumber = -1;
        if (isDGOn(Datagram.DG_TIMESTAMP)){
          timestampClientNumber = datagram.getInteger(i++);
        }

        String titles = null;
        if (isDGOn(Datagram.DG_TITLES)){
          titles = datagram.getString(i++);
        }

        boolean isOpen = false;
        if (isDGOn(Datagram.DG_OPEN)){
          isOpen = datagram.getBoolean(i++);
        }

        int playerState = -1;
        int gameNumber = -1;
        if (isDGOn(Datagram.DG_STATE)){
          playerState = convertPlayerState(datagram.getString(i++));
          gameNumber = datagram.getInteger(i++);
        }

        processNotifyArrived(new Player(username, titles, ratings, ratingTypes, timestampClientNumber, isOpen, playerState, gameNumber));
        break;
      }
      case Datagram.DG_NOTIFY_LEFT:{
        processNotifyLeft(datagram.getString(0));
        break;
      }
      case Datagram.DG_NOTIFY_OPEN:{
        processNotifyOpen(datagram.getString(0), datagram.getBoolean(1));
        break;
      }
      case Datagram.DG_NOTIFY_STATE:{
        processNotifyState(datagram.getString(0),convertPlayerState(datagram.getString(1)),datagram.getInteger(2));
        break;
      }
      case Datagram.DG_MY_NOTIFY_LIST:{
        processMyNotifyList(datagram.getString(0),datagram.getBoolean(1));
        break;
      }
      case Datagram.DG_LOGIN_FAILED:{
        processLoginFailed(datagram.getInteger(0),datagram.getString(1));
        break;
      }
      case Datagram.DG_FEN:{
        processFEN(datagram.getInteger(0),datagram.getString(1));
        break;
      }
      case Datagram.DG_TOURNEY_MATCH:{
        break;
      }
      case Datagram.DG_GAMELIST_BEGIN:{
        processGamelistBegin(datagram.getString(0),datagram.getString(1),datagram.getInteger(2),
          datagram.getInteger(3),datagram.getInteger(4),datagram.getString(5));
        break;
      }
      case Datagram.DG_GAMELIST_ITEM:{
        int index = datagram.getInteger(0);
        String id = datagram.getString(1);
        String event = datagram.getString(2);
        String date = datagram.getString(3);
        String time = datagram.getString(4);
        String whiteName = datagram.getString(5);
        int whiteRating = datagram.getString(6).equals("?") ? -1 : datagram.getInteger(6);
        String blackName = datagram.getString(7);
        int blackRating = datagram.getString(8).equals("?") ? -1 : datagram.getInteger(8);
        boolean isRated = datagram.getBoolean(9);
        int ratingCategory = datagram.getInteger(10);
        int wildType = datagram.getInteger(11);
        int whiteInit = datagram.getInteger(12);
        int whiteInc = datagram.getInteger(13);
        int blackInit = datagram.getInteger(14);
        int blackInc = datagram.getInteger(15);
        String eco = datagram.getString(16);
        int status = datagram.getInteger(17);
        int color = datagram.getInteger(18)==0 ? BLACK : WHITE;
        int mode = datagram.getInteger(19);
        String note = datagram.getString(20);
        boolean isOppHere = datagram.getBoolean(21);
        processGamelistItem(index,id,event,date,time,whiteName,whiteRating,blackName,
          blackRating,isRated,ratingCategory,wildType,whiteInit,whiteInc,
          blackInit,blackInc,eco,status,color,mode,note,isOppHere);
        break;
      }
      case Datagram.DG_IDLE:{
        processIdle(datagram.getString(0),datagram.getInteger(1),datagram.getInteger(2));
        break;
      }
      case Datagram.DG_ACK_PING:{
        processAckPing(datagram.getString(0),datagram.getInteger(1));
        break;
      }
      case Datagram.DG_RATING_TYPE_KEY:{
        processRatingTypeKey(datagram.getInteger(0),datagram.getString(1));
        break;
      }
      case Datagram.DG_GAME_MESSAGE:{
        processGameMessage(datagram.getInteger(0),datagram.getString(1));
        break;                    
      }
      case Datagram.DG_UNACCENTED:{
        throw new IllegalStateException("DG_UNACCENTED should never be sent");
      }
      case Datagram.DG_STRINGLIST_BEGIN:{
        processStringlistBegin(datagram.getInteger(0),datagram.getString(1));
        break;
      }
      case Datagram.DG_STRINGLIST_ITEM:{
        processStringlistItem(datagram.getString(0));
        break;
      }
      case Datagram.DG_DUMMY_RESPONSE:{
        processDummyResponse();
        break;
      }
      case Datagram.DG_CHANNEL_QTELL:{
        processChannelQTell(datagram.getInteger(0), datagram.getString(1), datagram.getString(2), datagram.getString(3));
        break;
      }
      case Datagram.DG_PERSONAL_QTELL:{
        processPersonalQTell(datagram.getString(0), datagram.getString(1), datagram.getString(2));
        break;
      }
      case Datagram.DG_SET_BOARD:{
        processSetBoard(datagram.getInteger(0), datagram.getString(1), datagram.getString(2).equals("W") ? WHITE : BLACK);
        break;
      }
      case Datagram.DG_MATCH_ASSESSMENT:{
        throw new IllegalStateException("DG_MATCH_ASSESSMENT should never be sent");
      }
      case Datagram.DG_LOG_PGN:{  
        String [] pgnLines = new String[datagram.getArgumentCount()];
        for (int i=0;i<pgnLines.length;i++)
          pgnLines[i] = datagram.getString(i);
        
        processLogPGN(pgnLines);
        break;
      }
      case Datagram.DG_NEW_MY_RATING:{
        int [] ratings = new int[datagram.getArgumentCount()];
        for (int i=0;i<ratings.length;i++)
          ratings[i] = datagram.getInteger(i);

        processNewMyRating(ratings);
        break;
      }
      case Datagram.DG_UNCIRCLE:{
        processUncircle(datagram.getInteger(0),datagram.getString(1),datagram.getString(2));
        break;
      }
      case Datagram.DG_UNARROW:{
        processUnarrow(datagram.getInteger(0),datagram.getString(1),datagram.getString(2),
          datagram.getString(3));
        break;
      }
      case Datagram.DG_WSUGGEST:{
        processWSuggest(datagram.getString(0),datagram.getString(1),datagram.getInteger(2),
          datagram.getString(3), datagram.getString(4),datagram.getString(5));
        break;
      }
      case Datagram.DG_TEMPORARY_PASSWORD:{
        break;
      }
      case Datagram.DG_MESSAGELIST_BEGIN:{
        processMessagelistBegin(datagram.getString(0));
        break;
      }
      case Datagram.DG_MESSAGELIST_ITEM:{
        processMessagelistItem(datagram.getInteger(0),datagram.getString(1),datagram.getString(2),
          datagram.getString(3),datagram.getString(4));
        break;
      }
      case Datagram.DG_LIST:{
        String [] items = new String[datagram.getArgumentCount()-2];
        for (int i=0;i<items.length;i++)
          items[i] = datagram.getString(i+2);

        processList(datagram.getString(0),datagram.getString(1),items);
        break;
      }
      case Datagram.DG_SJI_AD:{
        processSJIad(datagram.getString(0));
        break;
      }
      case Datagram.DG_RETRACT:{
        processRetract(datagram.getString(0));
        break;
      }
      case Datagram.DG_MY_GAME_CHANGE:{
        int gameNumber = datagram.getInteger(0);
        String whiteName = datagram.getString(1);
        String blackName = datagram.getString(2);
        int wildNumber = datagram.getInteger(3);
        String ratingCategoryString = datagram.getString(4);
        boolean isRated = datagram.getBoolean(5);
        int whiteInitial = datagram.getInteger(6);
        int whiteIncrement = datagram.getInteger(7);
        int blackInitial = datagram.getInteger(8);
        int blackIncrement = datagram.getInteger(9);
        boolean isPlayedGame = datagram.getBoolean(10);
        String exString = datagram.getString(11);
        int whiteRating = datagram.getInteger(12);
        int blackRating = datagram.getInteger(13);
        long gameID = datagram.getLong(14);
        String whiteTitles = datagram.getString(15);
        String blackTitles = datagram.getString(16);
        boolean isIrregularLegality = datagram.getBoolean(17);
        boolean isIrregularSemantics = datagram.getBoolean(18);
        boolean usesPlunkers = datagram.getBoolean(19);
        String fancyTimeControls = datagram.getString(20);
        processMyGameChange(gameNumber,whiteName,blackName,wildNumber,ratingCategoryString,isRated,whiteInitial,
          whiteIncrement,blackInitial,blackIncrement,isPlayedGame,exString,whiteRating,blackRating,
          gameID,whiteTitles,blackTitles,isIrregularLegality,isIrregularSemantics,usesPlunkers,
          fancyTimeControls);
        break;
      }
      case Datagram.DG_POSITION_BEGIN:{
        processPositionBegin(datagram.getInteger(0),datagram.getString(1),datagram.getInteger(2));
        break;
      }
      case Datagram.DG_TOURNEY:{
        int id = datagram.getInteger(0);
        int bitfield = datagram.getInteger(1);
        boolean canGuestsJoinWatch = (bitfield & 1) != 0;
        boolean makeNewWindowOnJoin = (bitfield & 2) != 0;
        boolean makeNewWindowOnWatch = (bitfield & 4) != 0;
        boolean makeNewWindowOnInfo = (bitfield & 8) != 0;
        String description = datagram.getString(2);
        String [] joinCommands = parseDGTourneyCommandList(datagram.getString(3));
        String [] watchCommands = parseDGTourneyCommandList(datagram.getString(4));
        String [] infoCommands = parseDGTourneyCommandList(datagram.getString(5));
        String confirmText = datagram.getString(6);
        processTourney(id, canGuestsJoinWatch, makeNewWindowOnJoin, makeNewWindowOnWatch,
          makeNewWindowOnInfo, description, joinCommands, watchCommands, infoCommands, confirmText);
        break;
      }
      case Datagram.DG_REMOVE_TOURNEY:{
        int id = datagram.getInteger(0);
        processRemoveTourney(id);
      }
      case Datagram.DG_DIALOG_START:{
        processDialogStart(datagram.getInteger(0));
        break;
      }
      case Datagram.DG_DIALOG_DATA:{
        processDialogData(datagram.getString(0));
        break;
      }
      case Datagram.DG_DIALOG_DEFAULT:{
        processDialogDefault(datagram.getInteger(0),datagram.getString(1),datagram.getString(2));
        break;
      }
      case Datagram.DG_DIALOG_END:{
        processDialogEnd(datagram.getBoolean(0),datagram.getInteger(1),datagram.getString(2),
          datagram.getString(3),datagram.getString(4),datagram.getString(5));
        break;
      }
      case Datagram.DG_DIALOG_RELEASE:{
        processDialogRelease();
        break;
      }
      case Datagram.DG_POSITION_BEGIN2:{
        processPositionBegin2(datagram.getInteger(0),datagram.getString(1),datagram.getInteger(2));
        break;
      }
      case Datagram.DG_PAST_MOVE:{
        int i = 0;
        int gameNumber = datagram.getInteger(i++);

        String algebraicMove = null;
        if (isDGOn(Datagram.DG_MOVE_ALGEBRAIC))
          algebraicMove = datagram.getString(i++);

        String smithMove = null;
        if (isDGOn(Datagram.DG_MOVE_SMITH))
          smithMove = datagram.getString(i++);

        int time = -1;
        if (isDGOn(Datagram.DG_MOVE_TIME))
          time = datagram.getInteger(i++);

        int clock = -1;
        if (isDGOn(Datagram.DG_MOVE_CLOCK))
          clock = datagram.getInteger(i++);

        int variationCode = -1;
        if (isDGOn(Datagram.DG_IS_VARIATION))
          variationCode = datagram.getInteger(i++);

        processPastMove(gameNumber, new MoveStruct(algebraicMove, smithMove, time, clock, variationCode));
        break;
      }
      case Datagram.DG_PGN_TAG:{
        processPGNTag(datagram.getInteger(0),datagram.getString(1),datagram.getString(2));
        break;
      }
      case Datagram.DG_IS_VARIATION:{
        throw new IllegalStateException("DG_IS_VARIATION should never be sent");
      }
      case Datagram.DG_PASSWORD:{
        processPassword(datagram.getString(0),datagram.getString(1));
        break;
      }
      case Datagram.DG_WILD_KEY:{
        processWildKey(datagram.getInteger(0),datagram.getString(1));
        break;
      }
      case Datagram.DG_SET2:{
        int dgType = datagram.getInteger(0);
        boolean state = datagram.getBoolean(1);
        processSet2(dgType,state);
        break;
      }
    }
  }





  /**
   * Converts a rating type code from ICC format to either
   * {@link #NO_RATING}, {@link #PROVISIONAL_RATING} or
   * {@link #ESTABLISHED_RATING}.
   */

  private static int convertRatingType(int iccCode){
    switch (iccCode){
      case 0:
        return NO_RATING;
      case 1:
        return PROVISIONAL_RATING;
      case 2:
        return ESTABLISHED_RATING;
    }

    throw new IllegalArgumentException("Unknown rating type code: "+iccCode);
  }




  /**
   * Converts a player state from the ICC format ("P", "O", "SW" etc.) to the
   * player state codes.
   */

  private static int convertPlayerState(String iccCode){
    if ("P".equals(iccCode))
      return PLAYING;
    if ("E".equals(iccCode))
      return EXAMINING;
    if ("S".equals(iccCode))
      return PLAYING_SIMUL;
    if ("O".equals(iccCode))
      return OBSERVING;
    if ("PW".equals(iccCode))
      return PLAYING_WHITE;
    if ("PB".equals(iccCode))
      return PLAYING_BLACK;
    if ("SW".equals(iccCode))
      return PLAYING_SIMUL_WHITE;
    if ("SB".equals(iccCode))
      return PLAYING_SIMUL_BLACK;
    if ("X".equals(iccCode))
      return DOING_NOTHING;

    throw new IllegalArgumentException("Unknown player state code: "+iccCode);
  }




  /**
   * Converts a color code from ICC format to either {@link #WHITE},
   * {@link #BLACK} or {@link #COLORLESS}.
   */

  private static int convertColorPreference(int iccCode){
  
    switch (iccCode){
      case -1:
        return COLORLESS;
      case 1:
        return WHITE;
      case 0:
        return BLACK;
    } 
    throw new IllegalArgumentException("Unknown color code: "+iccCode);
  }





  /**
   * Converts a channel tell type from ICC format to either
   * {@link #CHANNEL_TELL} or {@link #CHANNEL_ATELL}.
   */

  private static int convertChannelTellType(int iccCode){
    switch (iccCode){
      case 1:
        return CHANNEL_TELL;
      case 4:
        return CHANNEL_ATELL;
    }
    throw new IllegalArgumentException("Unknown channel tell type: "+iccCode);
  }






  /**
   * Converts a personal tell type from ICC format to {@link #TELL},
   * {@link #SAY}, {@link #PTELL},
   * {@link #QTELL} or {@link #ATELL}.
   */

  private static int convertPersonalTellType(int iccCode){
    switch (iccCode){
      case 0:
        return SAY;
      case 1:
        return TELL;
      case 2:
        return PTELL;
      case 3:
        return QTELL;
      case 4:
        return ATELL;
    }
    throw new IllegalArgumentException("Unknown tell type: "+iccCode);
  }




  /**
   * Converts a shout type from ICC format to {@link #SHOUT},
   * {@link #I_SHOUT}, {@link #SSHOUT} or
   * {@link #ANNOUNCEMENT}.
   */

  private static int convertShoutType(int iccCode){
    switch (iccCode){
      case 0:
        return SHOUT;
      case 1:
        return I_SHOUT;
      case 2:
        return SSHOUT;
      case 3:
        return ANNOUNCEMENT;
    }
    throw new IllegalArgumentException("Unknown shout type: "+iccCode);
  }





  /**
   * Converts a game type from ICC format to {@link #STORED_GAME},
   * {@link #EXAMINED_GAME} or {@link #PLAYED_GAME}.
   */

  private static int convertGameType(int iccCode){
    switch (iccCode){
      case -1:
        return STORED_GAME;
      case 0:
        return EXAMINED_GAME;
      case 1:
        return PLAYED_GAME;
    }
    throw new IllegalArgumentException("Unknown game type: "+iccCode);
  }






  /**
   * Parses the given command list as is sent in the DG_TOURNEY datagram.
   * Returns the list of commands.
   */

  private static String [] parseDGTourneyCommandList(String commands){
    StringTokenizer tokenizer = new StringTokenizer(commands, "&");
    String [] commandList = new String[tokenizer.countTokens()];
    int commandCount = 0;

    while (tokenizer.hasMoreTokens()){
      String escaped = tokenizer.nextToken();
      StringBuffer unescaped = new StringBuffer(escaped.length());
      int length = escaped.length();
      int i = 0;
      while (i < length){
        if (escaped.charAt(i) == '\\'){
          unescaped.append(escaped.charAt(i+1));
          i += 2;
        }
        else{
          unescaped.append(escaped.charAt(i));
          i++;
        }
      }
      commandList[commandCount++] = unescaped.toString();
    }

    return commandList;
  }


  /**
   * This method is called by the ReaderThread when a new line of plain text
   * arrives from the server.
   *
   * @param line The line that was received, '\n' not included.
   *
   * @see #processLine(String)
   */

  public final void handleLine(String line){
    if (echoStream!=null)
      echoStream.println(line);
    processLine(line);
  }





  /**
   * This method is called to process a single line of text.
   *
   * @param line The line that was received, '\n' not included.
   */

  protected void processLine(String line){
    
  }




  /**
   * This method is called by the ReaderThread when the connection the server
   * is terminated.
   */

  final synchronized void handleDisconnection(){
    if (echoStream!=null)
      echoStream.println("DISCONNECTED");

    if (isConnected())
      try{
        disconnect();
      } catch (IOException e){
          e.printStackTrace();
        }

    processDisconnection();
  }




  /**
   * This method is called to process disconnection from the server.
   */

  protected void processDisconnection(){

  }


}
