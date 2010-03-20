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

package free.chessclub;


/**
 * Represents a player on chessclub.com.
 */

public class Player implements Cloneable{


  /**
   * The username of the player.
   */

  private final String username;




  /**
   * The titles of the the player.
   */

  private String titles;



  /**
   * An array containing the player's ratings in various categories. The indices
   * of different categories are defined in {@link free.chessclub.Session}.
   */

  private final int [] ratings;




  /**
   * An array containing the player's rating types in various categories.
   * The indices of different categories are defined in {@link free.chessclub.Session}.
   * The rating types are {@link Session#NO_RATING}, {@link Session#PROVISIONAL_RATING}
   * or {@link Session#ESTABLISHED_RATING}.
   */

  private final int [] ratingTypes;




  /**
   * The id of the timestamp client of the user.
   */

  private final /*transient*/ int timestampClientNumber;




  /**
   * True if the player is open for matches.
   */

  private boolean isOpen;




  /**
   * The state of the player, {@link ChessclubConnection#DOING_NOTHING},
   * {@link ChessclubConnection#PLAYING}, {@link ChessclubConnection#EXAMINING}
   * or {@link ChessclubConnection#PLAYING_SIMUL}.
   */

  private int playerState;




  /**
   * The game the player is in, this is invalid if playerState==DOING_NOTHING.
   */

  private int gameNumber;





  /**
   * Creates a new Player with the given username, titles, ratings, ratingTypes,
   * timestampClientNumber, open flag, state and the number of the game he's playing.
   * The ratings array should be such that ratings[BLITZ] is the blitz rating for
   * example. The indices of the ratings are defined in
   * {@link free.chessclub.ChessclubConnection}.
   * The rating types are {@link ChessclubConnection#NO_RATING}, 
   * {@link ChessclubConnection#PROVISIONAL_RATING} or
   * {@link ChessclubConnection#ESTABLISHED_RATING}. The player state is
   * {@link ChessclubConnection#DOING_NOTHING}, {@link ChessclubConnection#PLAYING},
   * {@link ChessclubConnection#EXAMINING} or {@link ChessclubConnection#PLAYING_SIMUL}.
   */

  public Player(String username, String titles, int [] ratings, int [] ratingTypes,
    int timestampClientNumber, boolean isOpen, int playerState, int gameNumber){

    this.username = username;
    this.titles = titles;
    this.ratings = (int [])ratings.clone();
    this.ratingTypes = (int [])ratingTypes.clone();
    this.timestampClientNumber = timestampClientNumber;
    this.isOpen = isOpen;
    this.playerState = playerState;
    this.gameNumber = gameNumber;
  }




  /**
   * Returns the username of the player.
   */

  public final String getUsername(){
    return username;
  }



  /**
   * Returns the titles of the player.
   */

  public final String getTitles(){
    return titles;
  }



  /**
   * Returns the rating at the given index.
   */

  public final int getRating(int ratingIndex){
    return ratings[ratingIndex];
  }



  /**
   * Returns the rating type at the given index.
   */

  public final int getRatingType(int ratingIndex){
    return ratingTypes[ratingIndex];
  }




  /**
   * Returns the timestamping client number of this player.
   */

  public final int getTimestampClientNumber(){
    return timestampClientNumber;
  }



  /**
   * Returns true if this player is open for matches.
   */

  public final boolean isOpen(){
    return isOpen;
  } 




  /**
   * Returns the state of the player, {@link ChessclubConnection#DOING_NOTHING},
   * {@link ChessclubConnection#PLAYING}, {@link ChessclubConnection#EXAMINING}
   * or {@link ChessclubConnection#PLAYING_SIMUL}.
   */

  public final int getPlayerState(){
    return playerState;
  }




  /**
   * Returns the game the player is in. This is invalid if player state is DOING_NOTHING.
   */

  public final int getGameNumber(){
    return gameNumber;
  }



  /**
   * Sets this player's title to the given value.
   */

  public final void setTitles(String titles){
    this.titles = titles;
  }



  /**
   * Sets the player's rating and ratingType at the given index to the given value.
   */

  public final void setRating(int ratingIndex, int newRating, int newRatingType){
    ratings[ratingIndex] = newRating;
    ratingTypes[ratingIndex] = newRatingType;
  }




  /**
   * Sets this player's open flag to the given value.
   */

  public final void setOpen(boolean isOpen){
    this.isOpen = isOpen;
  } 




  /**
   * Sets this player's state to the given state and gameNumber. The gameNumber
   * specifies which game he is playing, examining or is currently at if playing
   * a simul, pass any value if the player is DOING_NOTHING.
   */

  public final void setState(int playerState, int gameNumber){
    this.playerState = playerState;
    this.gameNumber = gameNumber;
  }

}
