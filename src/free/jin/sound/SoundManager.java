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

package free.jin.sound;

import free.jin.Game;
import free.jin.JinConnection;
import free.jin.event.*;
import free.jin.plugin.Plugin;
import free.util.audio.AudioClip;
import jregex.Matcher;
import jregex.Pattern;
import jregex.PatternSyntaxException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;


/**
 * The plugin responsible for producing sound on all the relevant events.
 */

public class SoundManager extends Plugin implements PlainTextListener, ChatListener, ConnectionListener, GameListener{


  /**
   * Maps chat patterns to filenames containing the sound data.
   */

  protected Hashtable chatPatternsToFilenames = new Hashtable();



  /**
   * Maps text patterns to filenames containing the sound data.
   */

  protected Hashtable textPatternsToFilenames = new Hashtable();



  /**
   * Maps sound filenames to AudioClips loaded from those filenames.
   */

  protected Hashtable filenamesToAudioClips = new Hashtable();



  /**
   * Maps event names such as ("OnConnect") to AudioClips.
   */

  protected Hashtable eventsToAudioClips = new Hashtable();




  /**
   * True when the plugin is "on", i.e. sounds are on.
   */

  protected boolean isOn;




  /**
   * Initializes the state of the plugin from user properties, loads the sounds
   * and registers all the listeners.
   */

  public void start(){
    init();
    loadSounds();
    registerListeners();
  }




  /**
   * Undoes what the <code>start()</code> method does.
   */

  public void stop(){
    unregisterListeners();
    unloadSounds();
  }





  /**
   * Initializes the state of the plugin from the state specified in the user
   * properties.  This method is called from the start method of the plugin.
   */

  protected void init(){
    isOn = getProperty("on", "true").toLowerCase().equals("true");
  }





  /**
   * Saves the current state of the plugin into the user properties. This method
   * is called from the start method of the plugin.
   */

  public void saveState(){
    setProperty("on", isOn ? "true" : "false");
  }




  /**
   * Creates and returns the JMenu for this plugin.
   */

  public JMenu createPluginMenu(){
    JMenu myMenu = new JMenu(getName());
    
    JRadioButtonMenuItem onMenu = new JRadioButtonMenuItem("Sound On", isOn);
    JRadioButtonMenuItem offMenu = new JRadioButtonMenuItem("Sound Off", !isOn);

    onMenu.setMnemonic('S');
    offMenu.setMnemonic('o');

    ButtonGroup onOffGroup = new ButtonGroup();
    onOffGroup.add(onMenu);
    onOffGroup.add(offMenu);

    onMenu.setActionCommand("on");
    offMenu.setActionCommand("off");

    ActionListener soundStateListener = new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        String actionCommand = evt.getActionCommand();

        if ("on".equals(actionCommand))
          isOn = true;
        else if ("off".equals(actionCommand))
          isOn = false;
        else
          throw new IllegalStateException("Unknown action command: "+actionCommand);
      }
    };

    onMenu.addActionListener(soundStateListener);
    offMenu.addActionListener(soundStateListener);

    myMenu.add(onMenu);
    myMenu.add(offMenu);

    return myMenu;
  }





  /**
   * Loads all the sounds and maps them to chat patterns.
   */

  protected void loadSounds(){
    loadPatternSounds("chat", chatPatternsToFilenames);
    loadPatternSounds("text", textPatternsToFilenames);

    loadEventAudioClip("OnConnect");
    loadEventAudioClip("OnLogin");
    loadEventAudioClip("OnDisconnect");

    loadEventAudioClip("Move");
    loadEventAudioClip("Capture");
    loadEventAudioClip("Castling");
    loadEventAudioClip("IllegalMove");
    loadEventAudioClip("GameEnd");
    loadEventAudioClip("GameStart");
  }




  /**
   * Loads patterns and their corresponding sounds of the given type, mapping
   * the patterns to sound filenames in the given Hashtable.
   */

  private void loadPatternSounds(String type, Hashtable map){
    int numPatterns = Integer.parseInt(getProperty("num-"+type+"-patterns"));

    for (int i = 0; i < numPatterns; i++){
      try{
        String filename = getProperty(type+"-sound-"+i);
        String pattern = getProperty(type+"-pattern-"+i);
        Pattern regex = new Pattern(pattern);
        
        if (!filenamesToAudioClips.containsKey(filename)){
          URL url = getClass().getResource(filename);
          if (url == null)
            continue;
          filenamesToAudioClips.put(filename, new AudioClip(url));
        }

        map.put(regex, filename);
      } catch (IOException e){
          e.printStackTrace();
        }
        catch (PatternSyntaxException e){
          e.printStackTrace();
        }
    }
  }




  /**
   * Tries to load an AudioClip for the given event and map the event name to the
   * AudioClip in the <code>eventsToAudioClips</code> hashtable. Silently fails if
   * unsuccessful.
   */

  protected final void loadEventAudioClip(String eventName){
    try{
      String resourceName = getProperty(eventName);
      if (resourceName==null)
        return;
      URL url = SoundManager.class.getResource(resourceName);
      if (url == null)
        return;
      eventsToAudioClips.put(eventName, new AudioClip(url));
    } catch (IOException e){
        e.printStackTrace();
      }
  }



  
  /**
   * Registers all the necessary listeners.
   */

  protected void registerListeners(){
    JinConnection conn = getConnection();
    JinListenerManager listenerManager = conn.getJinListenerManager();

    listenerManager.addPlainTextListener(this);
    listenerManager.addChatListener(this);
    listenerManager.addConnectionListener(this);
    listenerManager.addGameListener(this);
  }





  /**
   * Unregisters all the listeners registered by <code>registerListeners()</code>.
   */

  protected void unregisterListeners(){
    JinConnection conn = getConnection();
    JinListenerManager listenerManager = conn.getJinListenerManager();

    listenerManager.removePlainTextListener(this);
    listenerManager.removeChatListener(this);
    listenerManager.removeConnectionListener(this);
    listenerManager.removeGameListener(this);
  }




  /**
   * Unloads all the sounds.
   */

  protected void unloadSounds(){
    chatPatternsToFilenames.clear();
    filenamesToAudioClips.clear();
  }




  /**
   * Listens to ChatEvents and makes appropriate sounds.
   */

  public void chatMessageReceived(ChatEvent evt){
    if (!isOn())
      return;

    String type = evt.getType();
    Object forum = evt.getForum();
    String sender = evt.getSender();
    String chatMessageType = type+"."+(forum == null ? "" : forum.toString())+"."+sender;

    Enumeration enum1 = chatPatternsToFilenames.keys();
    while (enum1.hasMoreElements()){
      Pattern regex = (Pattern)enum1.nextElement();
      Matcher matcher = regex.matcher(chatMessageType);
      if (matcher.find()){
        String filename = (String)chatPatternsToFilenames.get(regex);
        AudioClip clip = (AudioClip)filenamesToAudioClips.get(filename);
        clip.play();
      }
    } 
  }




  /**
   * Listens to PlainTextEvents and makes appropriate sounds.
   */

  public void plainTextReceived(PlainTextEvent evt){
    if (!isOn())
      return;

    String line = evt.getText();

    Enumeration enum1 = textPatternsToFilenames.keys();
    while (enum1.hasMoreElements()){
      Pattern regex = (Pattern)enum1.nextElement();
      Matcher matcher = regex.matcher(line);
      if (matcher.find()){
        String filename = (String)textPatternsToFilenames.get(regex);
        AudioClip clip = (AudioClip)filenamesToAudioClips.get(filename);
        clip.play();
      }
    } 
  }






  /**
   * Plays the event associated with the given event.
   * Currently recognized event names include:
   * <UL>
   *   <LI> OnConnect - A connection was established.
   *   <LI> OnLogin - Login procedure succeeded.
   *   <LI> OnDisconnect - Disconnected.
   *   <LI> Move - A move is made.
   *   <LI> Capture - A capture move is made.
   *   <LI> Castling - A castling move is made.
   *   <LI> IllegalMove - An illegal move was attempted.
   *   <LI> GameStart - A game started.
   *   <LI> GameEnd - A game ended.
   * </UL>
   * Returns true if the given event is recognized, false otherwise. Note that
   * for various reasons (like the user disabling sounds), the sound may not
   * be played.
   */

  public boolean playEventSound(String eventName){
    AudioClip clip = (AudioClip)eventsToAudioClips.get(eventName);
    if (clip!=null){
      if (isOn())
        clip.play();
      return true;
    }

    return false;
  }




  /**
   * Plays the sound mapped to the "OnConnect" event.
   */

  public void connectionEstablished(ConnectionEvent evt){
    playEventSound("OnConnect");
  }




  /**
   * Plays the sound mapped to the "OnLogin" event.
   */

  public void connectionLoggedIn(ConnectionEvent evt){
    playEventSound("OnLogin");
  }




  /**
   * Plays the sound mapped to the "OnDisconnect" event.
   */

  public void connectionLost(ConnectionEvent evt){
    playEventSound("OnDisconnect");
  }



  
  /**
   * Plays the sound mapped to the "GameStart" event.
   */

  public void gameStarted(GameStartEvent evt){
    Game game = evt.getGame();
    if ((game.getGameType() == Game.MY_GAME) && game.isPlayed())
      playEventSound("GameStart");
  }




  /**
   * Plays the sound mapped to the "IllegalMove" event.
   */

  public void illegalMoveAttempted(IllegalMoveEvent evt){
    playEventSound("IllegalMove");
  }




  /**
   * Plays the sound mapped to the "GameEnd" event.
   */

  public void gameEnded(GameEndEvent evt){
    Game game = evt.getGame();
    if ((game.getGameType()==Game.MY_GAME)&&(game.isPlayed()))
      playEventSound("GameEnd");
  }




  public void moveMade(MoveMadeEvent evt){}
  public void positionChanged(PositionChangedEvent evt){}
  public void takebackOccurred(TakebackEvent evt){}
  public void clockAdjusted(ClockAdjustmentEvent evt){}
  public void boardFlipped(BoardFlipEvent evt){}





  /**
   * Returns <code>true</code> if the sound is currently on. Returns
   * <code>false</code> otherwise.
   */

  public boolean isOn(){
    return isOn;
  }


}
