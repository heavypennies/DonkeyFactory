/**
 * Jin - a chess client for internet chess servers.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2002, 2003 Alexander Maryanovsky.
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

package free.jin.console;

import free.workarounds.FixedJTextField;
import free.workarounds.FixUtils;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.util.Vector;
import javax.swing.KeyStroke;


/**
 * The JTextField used as the input component in a Console.
 * Implements the following:
 * <UL>
 *   <LI> On ENTER, adds the text to the Console and clears itself.
 *   <LI> On CTRL+ENTER, the command is issued as a special command processed
 *        by the Console.
 *   <LI> On SHIFT+ENTER, the command is issued as a blanked command - it's not
 *        echoed to the console nor is it saved into the history.
 *   <LI> On ESCAPE, clears itself.
 *   <LI> Keeps a history of commands traversable with the UP and DOWN keys.
 *   <LI> On CTRL+R, reverses the currently selected text (Watauba feature).
 * </UL>
 */

public class ConsoleTextField extends FixedJTextField{


  /**
   * The Console we're a part of.
   */

  protected final Console console;



  /**
   * The KeyStroke used for telling the last teller.
   */

  private KeyStroke tellLastTellerKeyStroke;



  /**
   * The KeyStroke used for telling the next teller.
   */

  private KeyStroke tellNextTellerKeyStroke;




  /**
   * The history of all the commands entered by the user.
   */
  
  private final Vector history = new Vector();



  /**
   * The index of the history entry currently displayed, -1 if the
   * string currently displayed is not in the history.
   */

  private int currentHistoryIndex = -1;



  /**
   * The string that keeps the text the user typed in before he started
   * traversing the history buffer.
   */

  private String typedInString = "";



  /**
   * The index of the current teller, or -1 if none.
   */

  private int tellerIndex = -1;




  /**
   * Creates a new ConsoleTextField for the given Console.
   */

  public ConsoleTextField(Console console){
    this.console = console;

    enableEvents(KeyEvent.KEY_EVENT_MASK|FocusEvent.FOCUS_EVENT_MASK);

    initFromProperties();
  }




  /**
   * Initializes this <code>ConsoleTextField</code> from the plugin properties.
   */

  protected void initFromProperties(){
    if (tellLastTellerKeyStroke != null)
      unregisterKeyboardAction(tellLastTellerKeyStroke);

    if (tellNextTellerKeyStroke != null)
      unregisterKeyboardAction(tellNextTellerKeyStroke);

    String tellLastTellerKeyStrokeString = console.getProperty("tell-last-teller-keystroke");
    String tellNextTellerKeyStrokeString = console.getProperty("tell-next-teller-keystroke");

    if (tellLastTellerKeyStrokeString != null){
      tellLastTellerKeyStroke = KeyStroke.getKeyStroke(tellLastTellerKeyStrokeString);
      registerKeyboardAction(new TellLastTellerAction(), tellLastTellerKeyStroke, WHEN_FOCUSED);
    }

    if (tellNextTellerKeyStrokeString != null){
      tellNextTellerKeyStroke = KeyStroke.getKeyStroke(tellNextTellerKeyStrokeString);
      registerKeyboardAction(new TellNextTellerAction(), tellNextTellerKeyStroke, WHEN_FOCUSED);
    }
  }




  /**
   * Re-reads all the plugin properties used by this instance and/or clears any
   * cached values of such properties.
   */

  public void refreshFromProperties(){
    initFromProperties();
  }




  /**
   * Processes the key event.
   */

  protected void processKeyEvent(KeyEvent evt){
    int keyCode = evt.getKeyCode();
    boolean isShiftDown = evt.isShiftDown();
    boolean isMetaDown = evt.isMetaDown();

    if (evt.getID() == KeyEvent.KEY_PRESSED){
      if (((keyCode == KeyEvent.VK_INSERT) && isShiftDown) || // The shift-insert keybinding
          ((keyCode == KeyEvent.VK_V) && isMetaDown)){        // The Command-v keybinding
        paste();
        evt.consume(); // We don't want to paste twice.
      }
    }

    super.processKeyEvent(evt);
  }



  /**
   * Processes the KeyEvent.
   */

  protected void processComponentKeyEvent(KeyEvent evt){
    super.processComponentKeyEvent(evt); // We want the listeners to get the 
                                         // event before we clear the text.


    boolean isControlDown = evt.isControlDown();
    boolean isShiftDown = evt.isShiftDown();

    if (evt.getID() == KeyEvent.KEY_PRESSED){
      switch (evt.getKeyCode()){
        case KeyEvent.VK_ENTER:
          String command = getText();
          long modifiers = 0;
          if (isShiftDown)
            modifiers |= Command.BLANKED_MASK;
          if (isControlDown)
            modifiers |= Command.SPECIAL_MASK;
          
          if ((modifiers&Command.BLANKED_MASK)==0){
            history.removeElement(command);
            history.insertElementAt(command,0);
          }

          typedInString = "";
          setText(typedInString);
          currentHistoryIndex = -1;

          console.issueCommand(new Command(command, modifiers));
          break;
        case KeyEvent.VK_ESCAPE:
          if (evt.getModifiers() == 0){
            typedInString = "";
            setText("");
            currentHistoryIndex = -1;
          }
          break;
        case KeyEvent.VK_UP:
          if (evt.getModifiers() == 0){
            if (currentHistoryIndex == -1)
              typedInString = getText();

            int newHistoryIndex = currentHistoryIndex;
            while (++newHistoryIndex < history.size()){
              String historyItem = (String)history.elementAt(newHistoryIndex);
              if (historyItem.startsWith(typedInString)){
                currentHistoryIndex = newHistoryIndex;
                break;
              }
            }
                                  
            if (newHistoryIndex == history.size()){
              getToolkit().beep();
              break;
            }

            setText((String)history.elementAt(currentHistoryIndex));
          }
          break;
        case KeyEvent.VK_DOWN:
          if (evt.getModifiers() == 0){
            if (currentHistoryIndex == -1){
              getToolkit().beep();
              break;
            }

            int newHistoryIndex = currentHistoryIndex;
            while (--newHistoryIndex >= 0){
              String historyItem = (String)history.elementAt(newHistoryIndex);
              if (historyItem.startsWith(typedInString)){
                currentHistoryIndex = newHistoryIndex;
                break;
              }
            }

            if (newHistoryIndex == -1){
              setText(typedInString);
              currentHistoryIndex = -1;
            }
            else
              setText((String)history.elementAt(currentHistoryIndex));
          }
          break;
        case KeyEvent.VK_R:
          if (isControlDown){ // Watauba feature :-)
            String curText = getText();
            String selectedText = getSelectedText();
            String reversedSelectedText = new StringBuffer(selectedText).reverse().toString();
            int selectionStart = getSelectionStart();
            int selectionEnd = getSelectionEnd();
            setText(curText.substring(0,selectionStart)+reversedSelectedText+curText.substring(selectionEnd));
            setSelectionStart(selectionStart);
            setSelectionEnd(selectionEnd);
          }
          break;
      }

      if (evt.getKeyChar() != FixUtils.CHAR_UNDEFINED)
        tellerIndex = -1;
    }
  }





  /**
   * Processes the Focus Events of this ConsoleTextField.
   */

  protected void processFocusEvent(FocusEvent evt){
    int oldSelectionStart = getSelectionStart();
    int oldSelectionEnd = getSelectionEnd();
    super.processFocusEvent(evt);
    if (evt.getID()==FocusEvent.FOCUS_GAINED){
      setSelectionStart(oldSelectionStart);
      setSelectionEnd(oldSelectionEnd);
    }
  }




  /**
   * Overrides paste() to implement flattening the text to a single line.
   */

  public void paste(){
    // Replace the contents of the clipboard with a flat version of the text, call super.paste()
    // and then change the contents back.
    Clipboard clipboard = getToolkit().getSystemClipboard();
    Transferable content = clipboard.getContents(this);
    if (content != null){
      try{
        String data = (String)(content.getTransferData(DataFlavor.stringFlavor));
        int index;
        while ((index = data.indexOf("\r\n"))!=-1)
          data = data.substring(0,index)+" "+data.substring(index+2);

        data = data.replace('\n',' ');
        data = data.replace('\r',' ');

        StringSelection tempContents = new StringSelection(data);
        clipboard.setContents(tempContents, null);
      } catch (Exception e){}
    }

    super.paste();

    clipboard.setContents(content,null);
  }




  /**
   * Sets the textfield to be ready to send a tell to the given player.
   * The default implementation does nothing.
   */

  protected void setTellPersonState(String playerName){}




  /**
   * The ActionListener setting the current console textfield text to something
   * that will send a tell to the last player who told us something.
   */

  private class TellLastTellerAction implements ActionListener{

    public void actionPerformed(ActionEvent evt){
      int traversedTellerCount = console.getTellerRingSize();

      tellerIndex++;
      if ((tellerIndex == console.getTellerCount()) || (tellerIndex == traversedTellerCount))
        tellerIndex = 0;

      String teller = console.getTeller(tellerIndex);
      if (teller != null)
        setTellPersonState(teller);
    }

  }



  /**
   * The ActionListener setting the current console textfield text to something
   * that will send a tell to the next player who told us something.
   */

  private class TellNextTellerAction implements ActionListener{

    public void actionPerformed(ActionEvent evt){
      int traversedTellerCount = console.getTellerRingSize();

      tellerIndex--;
      if (tellerIndex < 0)
        tellerIndex = Math.min(traversedTellerCount, console.getTellerCount()) - 1;

      String teller = console.getTeller(tellerIndex);
      if (teller != null)
        setTellPersonState(teller);
    }

  }

}
