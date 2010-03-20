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

package free.chessclub.level2;

import free.util.TextUtilities;


/**
 * Represents a datagram sent by the chessclub.com server in the format 
 * described at <a HREF="ftp://ftp.chessclub.com/pub/icc/formats/formats.txt">ftp://ftp.chessclub.com/pub/icc/formats/formats.txt</a>
 * This class defines methods useful for parsing a datagram and retrieving
 * arguments from it.
 */

public class Datagram{


  private static final boolean DEBUG = true;
    // Are we still debugging this class?



  /**
   * The standard delimiter in datagrams. 
   */
  public static final char DG_DELIM = '\u0019'; 
    

  /**
   * A string specifying the start of a datagram.
   */
  public static final String DG_START = ""+DG_DELIM+"("; 


  /**
   * A string specifying the end of a datagram.
   */
  public static final String DG_END = ""+DG_DELIM+")";
    
  
  /**
   * A string specifying the start of a string (type 1).
   */
  private static final String STRING_START_1 = ""+DG_DELIM+"{";


  /**
   * A string specifying the end of a string (type 1).
   */
  private static final String STRING_END_1 = ""+DG_DELIM+"}";


  /**
   * A string specifying the start of a string (type 2).
   */
  private static final String STRING_START_2 = "{";
    
  
  /**
   * A string specifying the end of a string (type 2).
   */
  private static final String STRING_END_2 = "}";
    







  /**
   * The length of DG_START, so that we don't have to get it every time.
   */
  private static final int DG_START_LENGTH = DG_START.length();


  /**
   * The length of DG_END, so that we don't have to get it every time.
   */
  private static final int DG_END_LENGTH = DG_END.length();


  /**
   * The length of STRING_END_1 , so that we don't have to get it every time.  
   */  
  private static final int STRING_END_1_LENGTH = STRING_END_1.length();


  /**
   * The length of STRING_START_1 , so that we don't have to get it every time.
   */
  private static final int STRING_START_1_LENGTH = STRING_START_1.length();


  /**
   * The length of STRING_START_2 , so that we don't have to get it every time.
   */
  private static final int STRING_START_2_LENGTH = STRING_START_2.length();


  /**
   * The length of STRING_END_2 , so that we don't have to get it every time.
   */
  private static final int STRING_END_2_LENGTH = STRING_END_1.length();




	/**
	 * Definitions of all the DG type numbers. See their description at 
	 * <a href="ftp://ftp.chessclub.com/pub/icc/formats/formats.txt">ftp://ftp.chessclub.com/pub/icc/formats/formats.txt</a>
	 */

  public static final int DG_WHO_AM_I						     = 0;
  public static final int DG_PLAYER_ARRIVED	         = 1;
  public static final int DG_PLAYER_LEFT	           = 2;
  public static final int DG_BULLET                  = 3;
  public static final int DG_BLITZ	                 = 4;
  public static final int DG_STANDARD                = 5;
  public static final int DG_WILD                    = 6;
  public static final int DG_BUGHOUSE                = 7;
  public static final int DG_TIMESTAMP               = 8;
  public static final int DG_TITLES							     = 9;
  public static final int DG_OPEN	                   = 10;
  public static final int DG_STATE                   = 11;
  public static final int DG_GAME_STARTED            = 12;
  public static final int DG_GAME_RESULT             = 13;
  public static final int DG_EXAMINED_GAME_IS_GONE   = 14;
  public static final int DG_MY_GAME_STARTED         = 15;
  public static final int DG_MY_GAME_RESULT          = 16;
  public static final int DG_MY_GAME_ENDED           = 17;
  public static final int DG_STARTED_OBSERVING       = 18;
  public static final int DG_STOP_OBSERVING          = 19;
  public static final int DG_PLAYERS_IN_MY_GAME      = 20;
  public static final int DG_OFFERS_IN_MY_GAME       = 21;
  public static final int DG_TAKEBACK                = 22;
  public static final int DG_BACKWARD                = 23;
  public static final int DG_SEND_MOVES              = 24;
  public static final int DG_MOVE_LIST               = 25;
  public static final int DG_KIBITZ                  = 26;
  public static final int DG_PEOPLE_IN_MY_CHANNEL    = 27;
  public static final int DG_CHANNEL_TELL            = 28;
  public static final int DG_MATCH                   = 29;
  public static final int DG_MATCH_REMOVED           = 30;
  public static final int DG_PERSONAL_TELL           = 31;
  public static final int DG_SHOUT                   = 32;
  public static final int DG_MOVE_ALGEBRAIC          = 33;
  public static final int DG_MOVE_SMITH              = 34;
  public static final int DG_MOVE_TIME               = 35;
  public static final int DG_MOVE_CLOCK              = 36;
  public static final int DG_BUGHOUSE_HOLDINGS       = 37;
  public static final int DG_SET_CLOCK               = 38;
  public static final int DG_FLIP                    = 39;
  public static final int DG_ISOLATED_BOARD          = 40;
  public static final int DG_REFRESH                 = 41;
  public static final int DG_ILLEGAL_MOVE            = 42;
  public static final int DG_MY_RELATION_TO_GAME     = 43;
  public static final int DG_PARTNERSHIP             = 44;
  public static final int DG_SEES_SHOUTS             = 45;
  public static final int DG_CHANNELS_SHARED         = 46;
  public static final int DG_MY_VARIABLE             = 47;
  public static final int DG_MY_STRING_VARIABLE      = 48;
  public static final int DG_JBOARD                  = 49;
  public static final int DG_SEEK                    = 50;
  public static final int DG_SEEK_REMOVED            = 51;
  public static final int DG_MY_RATING               = 52;
  public static final int DG_SOUND                   = 53;
  public static final int DG_PLAYER_ARRIVED_SIMPLE   = 55;
  public static final int DG_MSEC                    = 56;
  public static final int DG_BUGHOUSE_PASS           = 57;
  public static final int DG_IP                      = 58;
  public static final int DG_CIRCLE                  = 59;
  public static final int DG_ARROW                   = 60;
  public static final int DG_MORETIME                = 61;
  public static final int DG_PERSONAL_TELL_ECHO      = 62;
  public static final int DG_SUGGESTION              = 63;
  public static final int DG_NOTIFY_ARRIVED          = 64;
  public static final int DG_NOTIFY_LEFT             = 65;
  public static final int DG_NOTIFY_OPEN             = 66;
  public static final int DG_NOTIFY_STATE            = 67;
  public static final int DG_MY_NOTIFY_LIST          = 68;
  public static final int DG_LOGIN_FAILED            = 69;
  public static final int DG_FEN                     = 70;
  public static final int DG_TOURNEY_MATCH           = 71;
  public static final int DG_GAMELIST_BEGIN          = 72;
  public static final int DG_GAMELIST_ITEM           = 73;
  public static final int DG_IDLE                    = 74;
  public static final int DG_ACK_PING                = 75;
  public static final int DG_RATING_TYPE_KEY         = 76;
  public static final int DG_GAME_MESSAGE            = 77;
  public static final int DG_UNACCENTED              = 78;
  public static final int DG_STRINGLIST_BEGIN        = 79;
  public static final int DG_STRINGLIST_ITEM         = 80;
  public static final int DG_DUMMY_RESPONSE          = 81;
  public static final int DG_CHANNEL_QTELL           = 82;
  public static final int DG_PERSONAL_QTELL          = 83;
  public static final int DG_SET_BOARD               = 84;
  public static final int DG_MATCH_ASSESSMENT        = 85;
  public static final int DG_LOG_PGN                 = 86;
  public static final int DG_NEW_MY_RATING           = 87;
  public static final int DG_LOSERS                  = 88;
  public static final int DG_UNCIRCLE                = 89;
  public static final int DG_UNARROW                 = 90;
  public static final int DG_WSUGGEST                = 91;
  public static final int DG_TEMPORARY_PASSWORD      = 93;
  public static final int DG_MESSAGELIST_BEGIN       = 94;
  public static final int DG_MESSAGELIST_ITEM        = 95;
  public static final int DG_LIST                    = 96;
  public static final int DG_SJI_AD                  = 97;
  public static final int DG_RETRACT                 = 99;
  public static final int DG_MY_GAME_CHANGE          = 100;
  public static final int DG_POSITION_BEGIN          = 101;
  public static final int DG_TOURNEY                 = 103;
  public static final int DG_REMOVE_TOURNEY          = 104;
  public static final int DG_DIALOG_START            = 105;
  public static final int DG_DIALOG_DATA             = 106;
  public static final int DG_DIALOG_DEFAULT          = 107;
  public static final int DG_DIALOG_END              = 108;
  public static final int DG_DIALOG_RELEASE          = 109;
  public static final int DG_POSITION_BEGIN2         = 110;
  public static final int DG_PAST_MOVE               = 111;
  public static final int DG_PGN_TAG                 = 112;
  public static final int DG_IS_VARIATION            = 113;
  public static final int DG_PASSWORD                = 114;
  public static final int DG_WILD_KEY                = 116;
  public static final int DG_SET2                    = 124;
  public static final int DG_KNOWS_FISCHER_RANDOM    = 132;
                         

   
  private final int dgNumber;
    // The data type number of the datagram.

  private final String [] arguments; 
    // An array containing the arguments of the datagram.


  private final byte [] content;
    // A byte array containing the exact contents of the datagram.




  /**
   * Creates a new Datagram with the given data type number and the given
   * arguments. This constructor should not be usually used on the client,
   * but instead the parseDatagram(String) method should be used.
   *
   * @param dgNumber The data type number of the datagram.
   * @param dgArguments An array of strings representing the arguments
   * of the datagram.
   */

  public Datagram(final int dgNumber, final String [] dgArguments, byte [] content){
    this.dgNumber = dgNumber;
    if (dgArguments==null)
      throw new NullPointerException();
    this.arguments = dgArguments;
    this.content = content;
  }





  /**
   * Returns the number of arguments in this Datagram.
   */

  public int getArgumentCount(){
    if (arguments==null)
      throw new DatagramFormatException();
    return arguments.length;
  }


  /**
   * Returns an argument at the given index. The argument is returned as-is,
   * meaning that if it is enclosed in some special delimiters, they will
   * appear in the returned string.
   *
   * @param argumentIndex The index in the list of arguments of the requested 
   * argument
   */ 

  public String getArgument(int argumentIndex){
    if (arguments==null)
      throw new DatagramFormatException();
    return arguments[argumentIndex];
  }




  /**
   * Returns the argument at the specified index parsed as a string.
   *
   * @param argumentIndex The index in the list of arguments of the requested 
   * argument
   */

  public String getString(int argumentIndex){
    String argument = getArgument(argumentIndex);    
    return argument;
  }




  /**
   * Returns the argument at the specified index parsed as an integer.
   *
   * @param argumentIndex The index in the list of arguments of the requested 
   * argument
   */

  public int getInteger(int argumentIndex){
    return Integer.parseInt(getArgument(argumentIndex));
  }





  /**
   * Returns the argument at the specified index parsed as a long.
   *
   * @param argumentIndex The index in the list of arguments of the requested 
   * argument
   */

  public long getLong(int argumentIndex){
    return Long.parseLong(getArgument(argumentIndex));
  }





  /**
   * Returns the argument at the specified index parsed as a boolean.
   * If the value of the argument is "0", false is returned, otherwise
   * true is returned.
   *
   * @param argumentIndex The index in the list of arguments of the requested
   * argument.
   */

  public boolean getBoolean(int argumentIndex){
    return getArgument(argumentIndex).equals("1");
  }






  /**
   * Returns a byte array containing all the contents of the datagram, unparsed,
   * not including the type or the space after it.
   */

  public byte [] getContent(){
    return (byte [])content.clone();
  }




  /**
   * Returns the datagram type number of this Datagram.
   */

  public int getType(){
    return dgNumber;
  }



  /**
   * Parses the given string and returns a Datagram object corresponding
   * to the datagram represented by the string.
   * Some datagrams contain unparseable data (DG_DIALOG_DATA and DG_SJI_AD for 
   * example), since there is no way to know what these datagrams are, this method
   * will be attempt to parse them. If they turn out to be in a really bad format (one which
   * breaks the parser), the only way to obtain the information sent in the datagram 
   * will be via the {@link #getContent()} method. Even if parsing succeeds, 
   * the get<argumentType>(int) methods will probably return garbage. If parsing
   * does not succeed, all get<argumentType>(int) methods will throw DatagramFormatException.
   *
   * @param datagram The string representing the datagram.
   *
   * @return The Datagram object representing the parsed datagram.
   *
   * @throws DatagramFormatException if the given string is formatted so badly,
   * it doesn't even get recognized as a datagram (if there is no dgNumber for example).
   */

  public static Datagram parseDatagram(String datagram){
    checkFormat(datagram);

    datagram = datagram.substring(DG_START_LENGTH, datagram.length()-DG_END_LENGTH); // Strip DG_START and DG_END

    int maxArgumentCount = 0;             
    for (int i=0;i<datagram.length();i++) // Count the maximum possible amount of arguments.
      if (datagram.charAt(i)==' ')        // The amount of arguments can't be less than the 
        maxArgumentCount++;               // amount of spaces since they are the delimiters 

    String [] args = new String[maxArgumentCount];
    int argCount = 0;

    StringBuffer buf = new StringBuffer(datagram);
    int dgCode = Integer.parseInt(TextUtilities.nextToken(buf, " "));

    while (true){
      if (buf.length()==0)
        break;

      String token = TextUtilities.nextToken(buf, " ");

      if (token.startsWith(STRING_START_1)){
        if (token.endsWith(STRING_END_1))
          token = token.substring(STRING_START_1_LENGTH, token.length()-STRING_START_1_LENGTH);
        else{
          token = token.substring(STRING_START_1_LENGTH)+" "+TextUtilities.nextToken(buf, STRING_END_1);
        }
      }
      else if (token.startsWith(STRING_START_2)){
        if (token.endsWith(STRING_END_2))
          token = token.substring(STRING_START_2_LENGTH, token.length()-STRING_START_2_LENGTH);
        else{
          token = token.substring(STRING_START_2_LENGTH)+" "+TextUtilities.nextToken(buf, STRING_END_2);
        }
      }
      else if (token.length()==0) // Skip trailing spaces
        continue;
      
      args[argCount++] = token;
    }

    String [] dgArguments = new String[argCount];
    for (int i=0;i<argCount;i++)
      dgArguments[i] = args[i];

    return new Datagram(dgCode, dgArguments, null);
  }





  /**
   * Checks whether the given string represents a datagram in the expected
   * format. Throws a DatagramFormatException if the format is wrong or
   * simply returns is the format is ok.
   *
   * @param string The string to check for being in the expected format.
   *
   * @throws DatagramFormatException if the string does not represent a
   * datagram in the expected format.
   */

  private static void checkFormat(String string){
    if (!string.startsWith(DG_START))
      throw new DatagramFormatException("The string ("+string+") does not start with \"^Y(\"");

    if (!string.endsWith(DG_END))
      throw new DatagramFormatException("The string ("+string+") does not end with \"^Y)\"");

    try{
      String strippedDG = string.substring(DG_START_LENGTH, string.length()-DG_END_LENGTH);
      Integer.parseInt(TextUtilities.nextToken(new StringBuffer(strippedDG), " "));
    } catch (NumberFormatException e){
        throw new DatagramFormatException("The DG data type is not a decimal number");
      }
  }





  /**
   * Converts this Datagram into a String.
   *
   * @return A string representation of this datagram.
   */

  public String toString(){
    StringBuffer buf = new StringBuffer("[Datagram ID="+dgNumber+" Arguments: ");
    int argumentCount = getArgumentCount();

    if (argumentCount==0){
      buf.append("None");
    }
    else{
      for (int i=0;i<argumentCount;i++){
        buf.append("{"+getArgument(i)+"}");
        if (i<argumentCount-1)
          buf.append(",");
      }
    }
    buf.append("]");
    return buf.toString();
  }


}
