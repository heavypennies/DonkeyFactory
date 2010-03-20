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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;
import free.util.GraphicsUtilities;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.lang.reflect.*;


/**
 * An extension of JTextPane which is used as the output component in a Console.
 * Implements the following features:
 * <UL>
 *   <LI> On right click, displays a popup menu which lets the user execute
 *        various commands with the current selection as the command argument.
 *   <LI> Allows marking parts of the text as Links. This allows a certain action
 *        to be performed 
 * </UL>
 */

public class ConsoleTextPane extends JTextPane{

  
  /**
   * The Console we're a part of.
   */

  protected final Console console;




  /**
   * The default Popup.
   */

  protected JPopupMenu defaultPopupMenu = null;




  /**
   * We keep the links here.
   */

  private Vector links = new Vector();




  /**
   * Our regular cursor (our real cursor might be hand while over a link).
   */

  private Cursor regCursor = Cursor.getDefaultCursor();




  /**
   * What is the link we're currently over? Null if none.
   */

  private Link curLink = null;




  /**
   * Creates a new ConsoleTextPane which will be a part of the given Console.
   */

  public ConsoleTextPane(Console console){
    this.console = console;

    setEditable(false);
    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

    ToolTipManager tooltipManager = ToolTipManager.sharedInstance();
    if (!tooltipManager.isEnabled())
      tooltipManager.setEnabled(true);

    tooltipManager.registerComponent(this);

    Action copyAction = new DefaultEditorKit.CopyAction();
    Keymap keymap = getKeymap();
    keymap.removeBindings();
    keymap.setResolveParent(null);
    keymap.addActionForKeyStroke(
      KeyStroke.getKeyStroke(KeyEvent.VK_COPY, 0), copyAction);
    keymap.addActionForKeyStroke(
      KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK), copyAction);
    keymap.addActionForKeyStroke(
      KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.META_MASK), copyAction);

    enableEvents(MouseEvent.MOUSE_EVENT_MASK|MouseEvent.MOUSE_MOTION_EVENT_MASK);
  }






  /**
   * Re-reads all the plugin properties used by this instance and/or clears any
   * cached values of such properties.
   */

  public void refreshFromProperties(){
    defaultPopupMenu = null;
    renderingHints = null;
    repaint();
  }




  /**
   * A Hashtable mapping <code>RenderingHints$Key</code> objects (as strings,
   * so that they can be used via BeanShell to their current values (again, as
   * strings).
   */

  private Hashtable renderingHints = null;



  /**
   * This is set to <code>false</code> if we find out that we're not running
   * under a Java2D capable JVM.
   */

  private static boolean renderingHintsSupported = true;



  
  /**
   * Some classes, methods and fields we use for rendering hints setting.
   */

  private Class g2Class, rhClass, rhKeyClass;
  private Class [] argumentTypes;
  private Method setRenderingHint;




  /**
   * Overrides <code>paintComponent</code> to enable/disable antialiasing and
   * perhaps other expensive rendering hints.
   */

  protected void paintComponent(Graphics g){
    if (renderingHints == null){
      renderingHints = new Hashtable(10, 0.1f);
      boolean antialias = new Boolean(console.getProperty("output-text.antialias")).booleanValue();
//      String fractionalMetrics = console.getProperty("output-text.fractionalMetrics");

      String textAntialiasValue = "VALUE_TEXT_ANTIALIAS_" + (antialias ? "ON" : "OFF");
      renderingHints.put("KEY_TEXT_ANTIALIASING", textAntialiasValue);
//      if (fractionalMetrics != null)
//        renderingHints.put("KEY_FRACTIONALMETRICS", fractionalMetrics);
    }

    if (renderingHintsSupported && !renderingHints.isEmpty()){
      try{
        if (g2Class == null){
          g2Class = Class.forName("java.awt.Graphics2D");
          rhClass = Class.forName("java.awt.RenderingHints");
          rhKeyClass = Class.forName("java.awt.RenderingHints$Key");
          argumentTypes = new Class[]{rhKeyClass, Object.class};
          setRenderingHint = g2Class.getDeclaredMethod("setRenderingHint", argumentTypes);
        }

        Enumeration renderingHintsEnum = renderingHints.keys();
        while (renderingHintsEnum.hasMoreElements()){
          String keyName = (String)renderingHintsEnum.nextElement();
          String valueName = (String)renderingHints.get(keyName);
          Object key = rhClass.getField(keyName).get(null);
          Object value = rhClass.getField(valueName).get(null);
          Object [] args = new Object[]{key, value};
          setRenderingHint.invoke(g, args);
        }
      } catch (Exception e){
          System.err.println("Failed to set rendering hints. Probably not running under a Java2D capable JVM.");
          renderingHintsSupported = false;
        }
    }

    super.paintComponent(g);
  }




  /**
   * Are we running inside a Java 2 (or later) Virtual Machine? This is used to
   * determine whether we need the hack that prevents the console from becoming
   * taller than Short.MAX_VALUE pixels.
   */

  private static final boolean isJava2 =
    System.getProperty("java.version").compareTo("1.2") >= 0;



  /**
   * Is this a Windows 95/98/Me machine?
   */

  private static final boolean isOldWindows =
    System.getProperty("os.name").startsWith("Windows") &&
    (System.getProperty("os.version").compareTo("5.0") < 0) &&
    !System.getProperty("os.name").startsWith("Windows NT");




  /**
   * Is this a Solaris machine?
   */

  private static final boolean isSolaris =
    System.getProperty("os.name").startsWith("Solaris") ||
    System.getProperty("os.name").startsWith("SunOS");




  /**
   * Should we use the hack that prevents the text pane from becoming taller
   * than Short.MAX_VALUE pixels?
   */

  private static final boolean shouldUse16BitGraphicsHack = 
    !isJava2 && (isOldWindows || isSolaris);




  /**
   * Overrides <code>reshape(int, int, int, int)</code> to possibly prevent
   * the text pane from becoming taller than Short.MAX_VALUE pixels tall.
   * See bug http://developer.java.sun.com/developer/bugParade/bugs/4138673.html
   * for details.
   */

  public void reshape(int x, int y, int width, int height){
    if ((height > Short.MAX_VALUE) && shouldUse16BitGraphicsHack){
      try{
        // Remove lines until our preferred height is less than Short.MAX_VALUE
        Document document = getDocument();
        while (getPreferredSize().height >= Short.MAX_VALUE){

          // Find the first newline
          int documentLength = document.getLength();
          String text = document.getText(0, Math.min(200, documentLength));
          int newlineIndex;
          while ((newlineIndex = text.indexOf('\n')) == -1)
            text = document.getText(0, Math.min(text.length() * 2, documentLength));

          // Remove the first line
          document.remove(0, newlineIndex + 1);
        }
      } catch (BadLocationException e){e.printStackTrace();}

      Container parent = getParent();
      if (parent != null){
        parent.invalidate();
        parent.validate();
        parent.doLayout();
      }
    }
    else
      super.reshape(x, y, width, height);
  }




    
  /**
   * We override this to prevent unnecessary Toolkit.beep()s which are caused
   * when you press a key when the text pane has focus because it's not
   * editable. The reason why we don't want beeps to be generated is because
   * we're relaying the events to the console's input field. See
   * {@link free.jin.console.Console#keyPressed(java.awt.event.KeyEvent)} for
   * more details.
   */

  protected void processComponentKeyEvent(KeyEvent evt){
    if ((!isEditable()) && (getKeymap().getAction(KeyStroke.getKeyStrokeForEvent(evt)) == null)){
      evt.consume(); // Otherwise the parent scrollpane gets it and starts doing
                     // things we already handle, like scrolling on PAGE_UP
      return;
    }

    super.processComponentKeyEvent(evt);
  }




  /**
   * Returns a JPopupMenu for the given mouse event. This method is called each
   * time the right mouse button is clicked over the ConsoleTextPane, if it
   * returns null, no popup is shown. The default implementation returns obtains
   * the popup items from the plugin properties.
   */

  protected JPopupMenu getPopupMenu(MouseEvent evt){
    String selection = getSelectedText();
    int selectionStart = getSelectionStart();
    int selectionEnd = getSelectionEnd();

    if ((selection == null) || (selection.length() == 0) ||
        !isOverText(evt.getX(), evt.getY(), selectionStart, selectionEnd)){
      int pressedLocation = viewToModel(evt.getPoint());
      if (pressedLocation == -1)
        return null;

      try{
        int wordStart = getWordStart(pressedLocation);
        int wordEnd = getWordEnd(pressedLocation);
        String text = getText();
        if (isOverText(evt.getX(), evt.getY(), wordStart, wordEnd)){
          selection = getDocument().getText(wordStart, wordEnd - wordStart);
          if (selection.trim().length() != 0) // Don't override current selection with whitespace selection
            select(wordStart, wordEnd);

          selection = getSelectedText();
        }
      } catch (BadLocationException e){
          e.printStackTrace();
          return null;
        }
    }

    if ((selection == null) || (selection.length() == 0))
      return null;

    if (defaultPopupMenu == null){
      int numCommands = Integer.parseInt(console.getProperty("output-popup.num-commands", "0"));
      if (numCommands == 0)
        return null;

      defaultPopupMenu = new JPopupMenu();

      for (int i=0;i<numCommands;i++){
        String command = console.getProperty("output-popup.command-"+i);

        if (command.equalsIgnoreCase("separator")){
          defaultPopupMenu.addSeparator();
          continue;
        }

        String commandName = console.getProperty("output-popup.command-"+i+"-name");

        JMenuItem menuItem = new JMenuItem(commandName);
        menuItem.setActionCommand(command);
        menuItem.addActionListener(new ActionListener(){

          public void actionPerformed(ActionEvent evt){
            String actionCommand = ((AbstractButton)evt.getSource()).getActionCommand();
            String curSelection = getSelectedText();
            if (actionCommand.startsWith("$")){
              executePopupCommand(actionCommand.substring(1), curSelection);
              return;
            }
            String actualCommand = actionCommand;
            int index;
            while ((index = actualCommand.indexOf("!@"))!=-1)
              actualCommand = actualCommand.substring(0,index) + curSelection + actualCommand.substring(index+"!@".length());

            console.issueCommand(new Command(actualCommand,0));
          }
          
        });
        defaultPopupMenu.add(menuItem);
      }

      defaultPopupMenu.setSize(defaultPopupMenu.getPreferredSize());
    }
    
    return defaultPopupMenu;
  }




  /**
   * Returns the start of the word at the specified location.
   */

  private int getWordStart(int location) throws BadLocationException{
    Document document = getDocument();
    Element lineElement = Utilities.getParagraphElement(this, location);
    int lineStart = lineElement.getStartOffset();
    int lineEnd = Math.min(lineElement.getEndOffset(), document.getLength());
    
    String text = document.getText(lineStart, lineEnd - lineStart);
    if ((text == null) || (text.length() == 0))
      return location;

    location -= lineStart;

    int textLength = text.length();

    char locationChar = text.charAt(location);
    boolean isWhitespaceWord;
    if (isWhitespaceChar(locationChar))
      isWhitespaceWord = true;
    else if (isWordChar(locationChar))
      isWhitespaceWord = false;
    else 
      return location + lineStart;

    for (int i = location; i >= 0; i--){
      char c = text.charAt(i);
      if ((isWhitespaceWord && !isWhitespaceChar(c)) || 
           !isWhitespaceWord && !isWordChar(c))
        return lineStart + i + 1;
    }

    return lineStart;
  }




  /**
   * Returns the end of the word at the specified location.
   */

  private int getWordEnd(int location) throws BadLocationException{
    Document document = getDocument();
    Element lineElement = Utilities.getParagraphElement(this, location);
    int lineStart = lineElement.getStartOffset();
    int lineEnd = Math.min(lineElement.getEndOffset(), document.getLength());
    
    String text = document.getText(lineStart, lineEnd - lineStart);
    if ((text == null) || (text.length() == 0))
      return location;

    location -= lineStart;

    int textLength = text.length();

    char locationChar = text.charAt(location);
    boolean isWhitespaceWord;
    if (isWhitespaceChar(locationChar))
      isWhitespaceWord = true;
    else if (isWordChar(locationChar))
      isWhitespaceWord = false;
    else 
      return location + lineStart + 1;

    for (int i = location; i < textLength; i++){
      char c = text.charAt(i);
      if ((isWhitespaceWord && !isWhitespaceChar(c)) || 
           !isWhitespaceWord && !isWordChar(c))
        return lineStart + i;
    }

    return lineEnd;
  }




  /**
   * Returns <code>true</code> if the specified character should be considered
   * a word character by the text selection mechanism, <code>false</code>
   * otherwise.
   */

  protected boolean isWordChar(char c){
    return Character.isLetter(c) || Character.isDigit(c);
  }




  /**
   * Returns <code>true</code> if the specified character should be considered
   * a whitespace character by the text selection mechanism, <code>false</code>
   * otherwise.
   */

  protected boolean isWhitespaceChar(char c){
    return Character.isWhitespace(c);
  }




  /**
   * Executes a popup command, these are commands that start with '$' in
   * the properties. The default recognized commands:
   * <UL>
   *   <LI> copy - copies the current ConsoleTextPane's selection to the clipboard.
   *   <LI> execute - executes the currently selected text as a command.
   *   <LI> url - Treats the currently selected text as a URL and displays the URL
   *        in a browser.
   *   <LI> expurgate - Replaces all none-whitespace characters in the current
   *        selection with asterisks.
   * </UL>
   */

  protected void executePopupCommand(String command, String selection){
    if (command.equalsIgnoreCase("copy")){
      copy();
    }
    else if (command.equalsIgnoreCase("execute")){
      console.issueCommand(new Command(selection, 0));
    }
    else if (command.equalsIgnoreCase("expurgate"))
      expurgateSelection();
    else{
      console.addToOutput("Unknown popup command: "+command, "regular");
    }
  }

  



  /**
   * Expurgates the current selection by replacing all non-whitespace characters
   * with asterisks.
   */

  protected void expurgateSelection(){
    String selection = getSelectedText();
    int selectionLength = selection.length();
    StringBuffer asterisks = new StringBuffer(selectionLength);
    for (int i=0;i<selectionLength;i++){
      char c = selection.charAt(i);
      if (Character.isWhitespace(c))
        asterisks.append(c);
      else
        asterisks.append('*');
    }

    boolean isEditable = isEditable();
    setEditable(true);
    replaceSelection(asterisks.toString());
    setEditable(isEditable);
  }




  
  /**
   * Adds the given Link to this ConsoleTextPane, making the text between the
   * starting index and the ending index clickable.
   */

  public void addLink(Link link){
    links.addElement(link);
  }




  /**
   * Removes all the links.
   */

  public void removeLinks(){
    links.removeAllElements();
  }




  /**
   * True if we're running under 1.1 MS VM.
   */

  private static final boolean MS_VM = (System.getProperty("java.version").compareTo("1.2") < 0) &&
                                        System.getProperty("java.vendor").startsWith("Microsoft");




  /**
   * Due to a bug in MS VM, which never sends mouse events with clickCount more
   * than 2, we're forced to count clicks ourselves. 
   */

  private int clickCount = 0;
  private long lastPressTime, lastReleaseTime;



  /**
   * Updates the click count, if necessary.
   */

  private void updateClickCount(MouseEvent evt){
    if (MS_VM){
      switch (evt.getID()){
        case MouseEvent.MOUSE_MOVED:
        case MouseEvent.MOUSE_DRAGGED:
        case MouseEvent.MOUSE_EXITED:
        case MouseEvent.MOUSE_ENTERED:
          clickCount = 0;
          break;
        case MouseEvent.MOUSE_PRESSED:
          if (evt.getWhen() - lastReleaseTime < 500)
            clickCount++;
          else
            clickCount = 1;
          break;
        case MouseEvent.MOUSE_RELEASED:
          lastReleaseTime = evt.getWhen();
          break;
      }
    }
  }



  /**
   * Returns the click count for the specified mouse event.
   */

  private int getClickCount(MouseEvent evt){
    if (MS_VM)
      return clickCount;
    else
      return evt.getClickCount();
  }



  /**
   * Processes the given MouseEvent.
   */

  protected void processMouseEvent(MouseEvent evt){
    updateClickCount(evt);

    // We want to handle events where clickCount >= 2 ourselves
    if (evt.getClickCount() < 2)
      super.processMouseEvent(evt);

    int pressedLoc = viewToModel(evt.getPoint());
    int selectionStart = getSelectionStart();
    int selectionEnd = getSelectionEnd();

    if (pressedLoc != -1){
      try{
        if ((getClickCount(evt) >= 2) && (evt.getID() == MouseEvent.MOUSE_PRESSED)
            && SwingUtilities.isLeftMouseButton(evt)){
          if ((getClickCount(evt) % 2) == 0){
            int start = getWordStart(pressedLoc);
            int end = getWordEnd(pressedLoc);

            select(start, end);
          }
          else{
            Document document = getDocument();
            Element paragraphElement = Utilities.getParagraphElement(this, pressedLoc);
            int start = paragraphElement.getStartOffset();
            int end = Math.min(paragraphElement.getEndOffset(), document.getLength());

            select(start, end);
          }
        }
      } catch (BadLocationException e){}
    }

    if (evt.isPopupTrigger()){
      JPopupMenu popup = getPopupMenu(evt);
      if (popup != null){ 
        Component rootPane = SwingUtilities.getRootPane(this);
        Dimension rootPaneSize = rootPane.getSize();
        Point clickPointOnRootPane = SwingUtilities.convertPoint(this, evt.getPoint(), rootPane);
        int x = evt.getX();
        int y = evt.getY();
        int width = popup.getWidth();
        int height = popup.getHeight();
        if (clickPointOnRootPane.x + width > rootPaneSize.width)
          x = x - width;
        if (clickPointOnRootPane.y + height > rootPaneSize.height)
          y = y - height;
        
        popup.setSelected(null);
        popup.show(this,x,y);
      }
    }

    if (evt.getID() == MouseEvent.MOUSE_EXITED){
      curLink = null;
      setCursor(regCursor);
    }

        // We're not doing anything on a MOUSE_ENTERED event (although we should)
        // because if we do, and the user clicks in a popup directly over a link
        // we get a MOUSE_ENTERED event immediately followed by a MOUSE_CLICK
        // event (bug http://developer.java.sun.com/developer/bugParade/bugs/4119993.html probably)
        // and so we run the link.
    if (/*(evt.getID() == MouseEvent.MOUSE_ENTERED) || */
        (evt.getID() == MouseEvent.MOUSE_RELEASED)){
      processPossibleLinkUpdate(evt);
    } 

    if ((evt.getID() == MouseEvent.MOUSE_CLICKED) && SwingUtilities.isLeftMouseButton(evt)){
      if (curLink != null)
        console.issueCommand(curLink.getCommand());
    } 

  }




  /** 
   * Processes the given Mouse(Motion)Event.
   */

  protected void processMouseMotionEvent(MouseEvent evt){
    updateClickCount(evt);

    super.processMouseMotionEvent(evt);

    if (evt.getID() == MouseEvent.MOUSE_MOVED){
      processPossibleLinkUpdate(evt);
    }
  }




  /**
   * This method is called when a mouse event occurs that might change whether
   * we're over a link. This method determines whether it changed and updates
   * all the variables.
   */

  private void processPossibleLinkUpdate(MouseEvent evt){
    Link newLink = getLink(evt.getX(), evt.getY());

    if (newLink == null){
      if (curLink != null){
        curLink = null;
        setCursor(regCursor, false);
      }
    }
    else if (!newLink.equals(curLink)){
      curLink = newLink;
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR), false);
    }
  }




  /**
   * Returns the link at the given location, or null if none.
   */

  protected Link getLink(int x, int y){
    int numLinks = links.size();
    for (int i = 0; i < numLinks; i++){
      Link link = (Link)links.elementAt(i);
      int linkStart = link.getStartPosition().getOffset();
      int linkEnd = link.getEndPosition().getOffset();

      if (isOverText(x, y, linkStart, linkEnd))
        return link;
    }

    return null;
  }




  /**
   * Returns <code>true</code> if the specified location (in pixels) is on a
   * character between the two specified positions in the text.
   */

  protected boolean isOverText(int x, int y, int startPos, int endPos){
    Rectangle startCharRect, endCharRect;
    try{
      startCharRect = modelToView(startPos);
      endCharRect = modelToView(endPos);
    } catch (BadLocationException e){ // Shouldn't happen
        e.printStackTrace();
        return false;
      }

    // Sometimes modelToView returns null, not sure why.
    if ((startCharRect == null) || (endCharRect == null)) 
      return false;

    if (startCharRect.y + startCharRect.height <= endCharRect.y){ // Separate lines.
      if (y > startCharRect.y){
        if (y <= startCharRect.y + startCharRect.height){
          if (x > startCharRect.x){
            try{
              int lineEnd = Utilities.getRowEnd(this, startPos);
              if (lineEnd == -1)
                return false;

              Rectangle lineEndCharRect = modelToView(lineEnd);
              if (x <= lineEndCharRect.x + lineEndCharRect.width)
                return true;
            } catch (BadLocationException e){ // Shouldn't happen
                e.printStackTrace();
                return false;
              }
          }
        }
        else if (y < endCharRect.y)
          return true;
        else if ((y < endCharRect.y+endCharRect.height) && (x < endCharRect.x+endCharRect.width))
          return true;
      }
      if ((y > startCharRect.y+startCharRect.height) && (y < endCharRect.y))
        return true;
    }
    else{ // Same line
      if (startCharRect.union(endCharRect).contains(x, y))
        return true;
    }

    return false;
  }




  /**
   * Returns the tooltip to display.
   */

  public String getToolTipText(MouseEvent evt){
    Link link = getLink(evt.getX(), evt.getY());
    if (link != null){
      Command command = link.getCommand();
      if (!command.isSpecial()){
        String commandString = command.getCommandString();

        try{
          int startIndex = link.getStartPosition().getOffset();
          int length = link.getEndPosition().getOffset() - startIndex;
          String linkText = getText(startIndex, length);

          if (linkText.equals(commandString))
            return null;
          else
            return commandString;
        } catch (BadLocationException e){}
      }
    }
    
    return null;
  }




  /**
   * Overrides setCursor to save information about the regular cursor.
   */

  public void setCursor(Cursor cursor){
    setCursor(cursor, true);
  }




  /**
   * Sets the cursor to the given Cursor, if the given boolean value is true,
   * saves the given cursor into regCursor.
   */

  private void setCursor(Cursor cursor, boolean save){
    super.setCursor(cursor);
    if (save)
      regCursor = cursor;
  }




  /**
   * Returns the unit scroll amount.
   */

  public int getScrollableUnitIncrement(Rectangle viewRect, int orientation, int direction){
    if (orientation == SwingConstants.HORIZONTAL)
      return super.getScrollableUnitIncrement(viewRect, orientation, direction);

    FontMetrics metrics = GraphicsUtilities.getFontMetrics(getFont());
    return metrics.getHeight();
  }

}
