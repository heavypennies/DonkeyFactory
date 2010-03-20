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

package free.jin.event;

import free.jin.event.JinEvent;
import free.jin.JinConnection;


/**
 * The event that is sent to ChatListeners when a chat related message
 * arrives from the server. The ChatEvent contains the type of the message,
 * the player who sent it, his title, the message itself and optionally
 * the forum on which it was sent (channel, game). For one-on-one (personal)
 * tells or tells that go to everyone the forum is null. <br>
 *
 * Note that this class isn't aware of the value of the tell type, it's a server
 * specific string which should be dealt by a server specific class.<br>
 *
 * The source of the event is the JinConnection it came from.
 */

public class ChatEvent extends JinEvent{



  /**
   * The type of the ChatEvent. This is something server specific and thus should
   * only be handled by server specific classes.
   */

  private final String type;




  /**
   * The name/handle of the player who sent the message.
   */

  private final String sender;




  /**
   * The title of the player who sent the string, must be a non-null
   * string.
   */

  private final String senderTitle;




  /**
   * The rating of the player who sent the string, -1 if unknown.
   */

  private final int senderRating;




  /**
   * The message itself. Must be a non-null string.
   */

  private final String message;




  /**
   * The forum on which the message was sent. Only applies to
   * messages that aren't one-on-one or go to everyone. For kibitzes
   * and whispers this is an Integer specifying the game number for example,
   * for channel tells, the channel number.
   */

  private final Object forum;





  /**
   * Creates a new ChatEvent with the given type, sender, sender titles, sender
   * rating (-1 if unknown), message and forum.
   */

  public ChatEvent(JinConnection conn, String type, String sender, String senderTitle,
      int senderRating, String message, Object forum){
    super(conn);

    if (type == null)
      throw new IllegalArgumentException("ChatEvent type may not be null");

    this.type = type;
    this.sender = sender;
    this.senderTitle = senderTitle;
    this.senderRating = senderRating;
    this.message = message;
    this.forum = forum;
  }





  /**
   * Returns the type of this message. This is a server specific string and
   * should be dealt by a server specific class.
   */

  public String getType(){
    return type;
  }




  /**
   * Returns the name/handle of the player who sent the message.
   */

  public String getSender(){
    return sender;
  }




  /**
   * Returns the title of the player who sent the message.
   */

  public String getSenderTitle(){
    return senderTitle;
  }




  /**
   * Returns the rating of the sender, or -1 if unknown.
   */

  public int getSenderRating(){
    return senderRating;
  }




  /**
   * Returns the message itself.
   */

  public String getMessage(){
    return message;
  }





  /**
   * Returns the forum on which the message was sent. This may be null
   * for messages that are one-on-one (such as personal tells) or are
   * sent to everyone (such as shouts). For channel tells this is an Integer
   * specifying the channel number, for kibitzes/whispers, the game number. Since
   * this value is somewhat server specific, it should be handled by a server
   * specific class.
   */

  public Object getForum(){
    return forum;
  }




  /**
   * Returns a textual representation of this ChatEvent.
   */

  public String toString(){
    return getClass().getName()+"[Sender="+getSender()+";Title="+getSenderTitle()+";Forum="+getForum()+";Message="+getMessage()+"]";
  } 
  
  

}
