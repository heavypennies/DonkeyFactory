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

package free.jin.board;

import free.chess.*;
import free.jin.Game;
import free.jin.User;
import free.jin.board.event.UserMoveEvent;
import free.jin.board.event.UserMoveListener;
import free.jin.event.*;
import free.jin.plugin.Plugin;
import free.jin.plugin.PreferencesPanel;
import free.jin.sound.SoundManager;
import free.util.BeanProperties;
import free.util.StringEncoder;
import free.util.StringParser;
import free.workarounds.FixedJInternalFrame;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * The plugin responsible for displaying boards and handling all things related
 * to that.
 */

public class BoardManager extends Plugin implements GameListener, UserMoveListener,
    VetoableChangeListener, InternalFrameListener{



  /**
   * The code for move sending mode where the user is not allowed to move his
   * pieces when it's not his turn.
   */

  public static final int LEGAL_CHESS_MOVE_SENDING_MODE = 1;




  /**
   * The code for move sending mode where the user is allowed to pick up his
   * pieces (more than once) and make moves when it's not his turn. The moves
   * are then sent immediately to the server.
   */

  public static final int PREDRAG_MOVE_SENDING_MODE = 2;




  /**
   * The code for premove move sending mode. The user is allowed to pick up
   * a piece and make a move when it's not his turn. The move is then not sent
   * until it is the user's move. After making the "premove", the user cannot
   * make additional "premoves".
   */

  public static final int PREMOVE_MOVE_SENDING_MODE = 3;




  /**
   * A reference to the sound manager, if one exists.
   */

  private SoundManager soundManager = null;




  /**
   * A hashtable mapping class names to already loaded PiecePainters.
   */

  private final Hashtable loadedPiecePainters = new Hashtable();




  /**
   * A hashtable mapping class names to already loaded BoardPainters.
   */

  private Hashtable loadedBoardPainters = new Hashtable();




  /**
   * A Hashtable mapping Game objects to BoardPanel objects which are currently
   * used.
   */

  protected final Hashtable gamesToBoardPanels = new Hashtable();




  /**
   * A list of JInternalFrames which are still on the screen but contain
   * inactive BoardPanels.
   */

  protected final Vector unusedInternalFrames = new Vector();




  /**
   * A Hashtable mapping JInternalFrame objects to BoardPanels they contain.
   */

  protected final Hashtable internalFramesToBoardPanels = new Hashtable();




  /**
   * A Hashtable mapping BoardPanels to their JInternalFrame containers.
   */

  protected final Hashtable boardPanelsToInternalFrames = new Hashtable();




  /**
   * A list of the JInternalFrames in the order they were created.
   */

  protected final Vector internalFrames = new Vector();




  /**
   * A <code>BeanProperties</code> object where we keep our properties.
   */

  private final BeanProperties props = new BeanProperties(this);




  /**
   * Starts this plugin.
   */

  public void start(){
    obtainSoundManager();
    initPreferences();
    registerConnListeners();
  }



  /**
   * Stops this plugin.
   */

  public void stop(){
    unregisterConnListeners();
    removeBoards();
  }




  /**
   * Obtains a reference to the SoundManager, if one exists.
   */

  private void obtainSoundManager(){
    String pluginName = getProperty("sound-manager-plugin.name");
    if (pluginName != null)
      soundManager = (SoundManager)getPluginContext().getPlugin(pluginName);
  }




  /**
   * Returns the <code>SoundManager</code>, or <code>null</code> if none exists.
   */

  public SoundManager getSoundManager(){
    return soundManager;
  }



  /**
   * Initializes various preferences from the user's properties.
   */

  protected void initPreferences(){
    setAutoPromote(Boolean.valueOf(getProperty("auto-promote", "false")).booleanValue());

    setMoveInputStyle("click'n'click".equals(getProperty("move-input-style")) ? 
      JBoard.CLICK_N_CLICK : JBoard.DRAG_N_DROP);

    setDraggedPieceStyle("target-cursor".equals(getProperty("dragged-piece-style")) ?
      JBoard.CROSSHAIR_DRAGGED_PIECE : JBoard.NORMAL_DRAGGED_PIECE);

    String moveHighlightingStyleString = getProperty("move-highlight.style", "square");
    if ("square".equals(moveHighlightingStyleString))
      setMoveHighlightingStyle(JBoard.SQUARE_MOVE_HIGHLIGHTING);
    else if ("arrow".equals(moveHighlightingStyleString))
      setMoveHighlightingStyle(JBoard.ARROW_MOVE_HIGHLIGHTING);
    else
      setMoveHighlightingStyle(JBoard.NO_MOVE_HIGHLIGHTING);

    setHighlightingOwnMoves(
      Boolean.valueOf(getProperty("move-highlight.highlight-own", "false")).booleanValue());

    setMoveHighlightingColor(
      StringParser.parseColor(getProperty("move-highlight.color", "00b2b2")));

    setDragSquareHighlightingColor(
      StringParser.parseColor(getProperty("drag-square-highlighting.color", "0000ff")));

    String moveSendingModeString = getProperty("move-sending-mode", "predrag");
    if ("legal-chess".equals(moveSendingModeString))
      setMoveSendingMode(LEGAL_CHESS_MOVE_SENDING_MODE);
    else if ("premove".equals(moveSendingModeString))
      setMoveSendingMode(PREMOVE_MOVE_SENDING_MODE);
    else
      setMoveSendingMode(PREDRAG_MOVE_SENDING_MODE);

    
    setWhitePieceColor(parseColor(getProperty("white-piece-color")));
    setBlackPieceColor(parseColor(getProperty("black-piece-color")));
    setWhiteOutlineColor(parseColor(getProperty("white-outline-color")));
    setBlackOutlineColor(parseColor(getProperty("black-outline-color")));

    setLightSquareColor(parseColor(getProperty("light-square-color")));
    setDarkSquareColor(parseColor(getProperty("dark-square-color")));


    setPiecePainter(getProperty("piece-painter-class-name"));
    setBoardPainter(getProperty("board-painter-class-name"));
  }



  /**
   * Returns null if the specified <code>String</code> is <code>null</code>,
   * otherwise parses it as a color and returns the resulting color.
   */

  private static Color parseColor(String color){
    return color == null ? null : StringParser.parseColor(color);
  }



  /**
   * Registers the specified <code>PropertyChangeListener</code>.
   */

  public void addPropertyChangeListener(PropertyChangeListener listener){
    props.addPropertyChangeListener(listener);
  }

  


  /**
   * Unregisters the specified <code>PropertyChangeListener</code>.
   */

  public void removePropertyChangeListener(PropertyChangeListener listener){
    props.removePropertyChangeListener(listener);
  }




  /**
   * Returns an instance of the specified PiecePainter class or
   * <code>null</code> if can't.
   */

  private PiecePainter getPiecePainter(String className){
    if (className == null)
      return null;

    PiecePainter piecePainter = (PiecePainter)loadedPiecePainters.get(className);

    if (piecePainter == null){
      try{
        piecePainter = (PiecePainter)Class.forName(className).newInstance();
      } catch (ClassNotFoundException e){
          System.err.println("Unable to find class "+className);
        }
        catch (InstantiationException e){
          System.err.println("Unable to instantiate class "+className); 
        }
        catch (IllegalAccessException e){
          System.err.println("Unable to instantiate class "+className+" due to access restrictions."); 
        }
        catch (ClassCastException e){
          System.err.println("Unable to cast "+className+" into PiecePainter"); 
        }
    }

    if (piecePainter == null)
      return null;

    loadedPiecePainters.put(className, piecePainter);

    configurePiecePainter(piecePainter);

    return piecePainter;
  }



  /**
   * Configures the given PiecePainter to prepare it for usage.
   */

  protected void configurePiecePainter(PiecePainter piecePainter){
    if (piecePainter instanceof ColoredPiecePainter){
      ColoredPiecePainter coloredPiecePainter = (ColoredPiecePainter)piecePainter;

      Color whiteColor = getWhitePieceColor(); 
      Color blackColor = getBlackPieceColor(); 
      Color whiteOutline = getWhiteOutlineColor();
      Color blackOutline = getBlackOutlineColor();

      if (whiteColor != null)
        coloredPiecePainter.setWhiteColor(whiteColor);
      if (blackColor != null)
        coloredPiecePainter.setBlackColor(blackColor);
      if (whiteOutline != null)
        coloredPiecePainter.setWhiteOutline(whiteOutline);
      if (blackOutline != null)
        coloredPiecePainter.setBlackOutline(blackOutline);
    }
  }




  /**
   * Returns an instance of the specified BoardPainter class or
   * <code>null</code> if can't.
   */

  private BoardPainter getBoardPainter(String className){
    if (className == null)
      return null;

    BoardPainter boardPainter = (BoardPainter)loadedBoardPainters.get(className);

    if (boardPainter == null){
      try{
        boardPainter = (BoardPainter)Class.forName(className).newInstance();
      } catch (ClassNotFoundException e){
          System.err.println("Unable to find class "+className);
        }
        catch (InstantiationException e){
          System.err.println("Unable to instantiate class "+className); 
        }
        catch (IllegalAccessException e){
          System.err.println("Unable to instantiate class "+className+" due to access restrictions."); 
        }
        catch (ClassCastException e){
          System.err.println("Unable to cast "+className+" into BoardPainter"); 
        }
    }

    if (boardPainter == null)
      return null;

    loadedBoardPainters.put(className, boardPainter);

    configureBoardPainter(boardPainter);

    return boardPainter;
  }




  /**
   * Configures the given BoardPainter preparing it to be used.
   */

  protected void configureBoardPainter(BoardPainter boardPainter){
    if (boardPainter instanceof ColoredBoardPainter){
      ColoredBoardPainter coloredBoardPainter = (ColoredBoardPainter)boardPainter;

      Color lightColor = getLightSquareColor();
      Color darkColor = getDarkSquareColor();

      if (lightColor != null)
        coloredBoardPainter.setLightColor(lightColor);
      if (darkColor != null)
        coloredBoardPainter.setDarkColor(darkColor);
    }
  }




  /**
   * Returns the current white pieces color.
   */

  public Color getWhitePieceColor(){
    return (Color)props.getProperty("whitePieceColor");
  }



  /**
   * Sets the white piece color to the specified value.
   */

  public void setWhitePieceColor(Color color){
    props.setProperty("whitePieceColor", color);
    configurePiecePainter(getPiecePainter());
  }



  /**
   * Returns the current black pieces color.
   */

  public Color getBlackPieceColor(){
    return (Color)props.getProperty("blackPieceColor");
  }



  /**
   * Sets the black piece color to the specified value.
   */

  public void setBlackPieceColor(Color color){
    props.setProperty("blackPieceColor", color);
    configurePiecePainter(getPiecePainter());
  }



  /**
   * Returns the current white pieces outline color.
   */

  public Color getWhiteOutlineColor(){
    return (Color)props.getProperty("whiteOutlineColor");
  }



  /**
   * Sets the white piece outline color to the specified value.
   */

  public void setWhiteOutlineColor(Color color){
    props.setProperty("whiteOutlineColor", color);
    configurePiecePainter(getPiecePainter());
  }



  /**
   * Returns the current black pieces outline color.
   */

  public Color getBlackOutlineColor(){
    return (Color)props.getProperty("blackOutlineColor");
  }



  /**
   * Sets the black piece outline color to the specified value.
   */

  public void setBlackOutlineColor(Color color){
    props.setProperty("blackOutlineColor", color);
    configurePiecePainter(getPiecePainter());
  }



  /**
   * Returns the current light SQUARES color.
   */

  public Color getLightSquareColor(){
    return (Color)props.getProperty("lightSquareColor");
  }



  /**
   * Sets the light SQUARES color to the specified value.
   */

  public void setLightSquareColor(Color color){
    props.setProperty("lightSquareColor", color);
    configureBoardPainter(getBoardPainter());
  }



  /**
   * Returns the current dark SQUARES color.
   */

  public Color getDarkSquareColor(){
    return (Color)props.getProperty("darkSquareColor");
  }



  /**
   * Sets the dark SQUARES color to the specified value.
   */

  public void setDarkSquareColor(Color color){
    props.setProperty("darkSquareColor", color);
    configureBoardPainter(getBoardPainter());
  }



  /**
   * Returns the current move input style. Possible values are defined in
   * <code>free.chess.JBoard</code>.
   */

  public int getMoveInputStyle(){
    return props.getIntegerProperty("moveInputStyle");
  }




  /**
   * Sets the move input style to the specified value.
   *
   * @param moveInputStyle The new move input style. Possible values are defined
   * in <code>free.chess.JBoard</code>.
   */

  public void setMoveInputStyle(int moveInputStyle){
    switch (moveInputStyle){
      case JBoard.CLICK_N_CLICK:
      case JBoard.DRAG_N_DROP:
        break;
      default:
        throw new IllegalArgumentException("Unknown move input style: "+moveInputStyle);
    }

    props.setIntegerProperty("moveInputStyle", moveInputStyle);
  }




  /**
   * Returns the current dragged piece style. Possible values defined in
   * <code>free.chess.JBoard</code>.
   */

  public int getDraggedPieceStyle(){
    return props.getIntegerProperty("draggedPieceStyle");
  }




  /**
   * Sets the dragged piece style to the specified value.
   *
   * @param draggedPieceStyle The new dragged piece style. Possible values are
   * defined in <code>free.chess.JBoard</code>.
   */

  public void setDraggedPieceStyle(int draggedPieceStyle){
    switch (draggedPieceStyle){
      case JBoard.CROSSHAIR_DRAGGED_PIECE:
      case JBoard.NORMAL_DRAGGED_PIECE:
        break;
      default:
        throw new IllegalArgumentException("Unknown dragged piece style: "+draggedPieceStyle);
    }

    props.setIntegerProperty("draggedPieceStyle", draggedPieceStyle);
  }




  /**
   * Returns <code>true</code> if promotable pieces are automatically promoted
   * to the default promotion piece. Otherwise, returns <code>false</code>
   */

  public boolean isAutoPromote(){
    return props.getBooleanProperty("autoPromote");
  }




  /**
   * Sets whether promotable pieces are promoted automatically to the default
   * promotion piece.
   *
   * @param autoPromote <code>true</code> if promotion should be automatic.
   */

  public void setAutoPromote(boolean isAutoPromote){
    props.setBooleanProperty("autoPromote", isAutoPromote);
  }




  /**
   * Returns the current move highlighting style. Posssible values are defined
   * in <code>free.chess.JBoard</code>.
   */

  public int getMoveHighlightingStyle(){
    return props.getIntegerProperty("moveHighlightingStyle");
  }




  /**
   * Sets the current move highlighting style to the specified value.
   * 
   * @param moveHighlightingStyle The new move highlighting style. Possible
   * values are defined in <code>free.chess.JBoard</code>.
   */

  public void setMoveHighlightingStyle(int moveHighlightingStyle){
    switch (moveHighlightingStyle){
      case JBoard.NO_MOVE_HIGHLIGHTING:
      case JBoard.SQUARE_MOVE_HIGHLIGHTING:
      case JBoard.ARROW_MOVE_HIGHLIGHTING:
        break;
      default:
        throw new IllegalStateException("Unknown move higlighting style: "+moveHighlightingStyle);
    }

    props.setIntegerProperty("moveHighlightingStyle", moveHighlightingStyle);
  }





  /**
   * Returns <code>true</code> if own moves are highlighted as well as the
   * opponent's moves. Returns <code>false</code> if only the opponent's moves
   * are highlighted (if move highlighting is enabled of course). 
   */

  public boolean isHighlightingOwnMoves(){
    return props.getBooleanProperty("highlightingOwnMoves");
  }




  /**
   * Sets whether own moves will be highlighted.
   *
   * @param isHighlightingOwnMoves <code>true</code> if own moves should be
   * highlighted as well as the opponent's moves. <code>false</code> if only
   * the opponent's moves should be highlighted.
   */

  public void setHighlightingOwnMoves(boolean isHighlightingOwnMoves){
    props.setBooleanProperty("highlightingOwnMoves", isHighlightingOwnMoves);
  }




  /**
   * Returns the current move highlighting color.
   */

  public Color getMoveHighlightingColor(){
    return (Color)props.getProperty("moveHighlightingColor");
  }




  /**
   * Sets the current move highlighting color to the specified value.
   *
   * @param moveHighlightingColor The new move highlighting color.
   */

  public void setMoveHighlightingColor(Color moveHighlightingColor){
    props.setProperty("moveHighlightingColor", moveHighlightingColor);
  }




  /**
   * Returns the current drag square highlighting color.
   */

  public Color getDragSquareHighlightingColor(){
    return (Color)props.getProperty("dragSquareHighlightingColor");
  }




  /**
   * Sets the drag square highlighting color to the specified value.
   *
   * @param dragSquareHighlightingColor The new drag square highlighting color.
   */

  public void setDragSquareHighlightingColor(Color dragSquareHighlightingColor){
    props.setProperty("dragSquareHighlightingColor", dragSquareHighlightingColor);
  }




  /**
   * Returns the current move sending mode.
   */

  public int getMoveSendingMode(){
    return props.getIntegerProperty("moveSendingMode");
  }




  /**
   * Sets the move sending mode to the specified value. Possible values are
   * <UL>
   *   <LI><code>LEGAL_CHESS_MOVE_SENDING_MODE</code>
   *   <LI><code>PREDRAG_MOVE_SENDING_MODE</code>
   *   <LI><code>PREMOVE_MOVE_SENDING_MODE</code>
   * </UL>
   */

  public void setMoveSendingMode(int moveSendingMode){
    switch(moveSendingMode){
      case LEGAL_CHESS_MOVE_SENDING_MODE:
      case PREDRAG_MOVE_SENDING_MODE:
      case PREMOVE_MOVE_SENDING_MODE:
        break;
      default:
        throw new IllegalArgumentException("Unrecognized value for move sending mode: " +
          moveSendingMode);
    }

    props.setIntegerProperty("moveSendingMode", moveSendingMode);
  }



  /**
   * Returns an the current <code>PiecePainter</code>.
   */

  public PiecePainter getPiecePainter(){
    return (PiecePainter)props.getProperty("piecePainter");
  }




  /**
   * Returns the current <code>BoardPainter</code>.
   */

  public BoardPainter getBoardPainter(){
    return (BoardPainter)props.getProperty("boardPainter");
  }




  /**
   * Sets the current piece painter to be of the specified type.
   *
   * @param piecePainterClassName The class name of the PiecePainter.
   */

  public void setPiecePainter(String className){
    props.setProperty("piecePainter", getPiecePainter(className));
  }




  /**
   * Sets the current board painter to be of the specified type.
   */

  public void setBoardPainter(String className){
    props.setProperty("boardPainter", getBoardPainter(className));
  }



  /**
   * Registers all the necessary listeners with the Connection.
   */

  protected void registerConnListeners(){
    JinListenerManager listenerManager = getConnection().getJinListenerManager();

    listenerManager.addGameListener(this);
  }




  /**
   * Creates and returns the JMenu for this plugin.
   */

  public JMenu createPluginMenu(){
    JMenu myMenu = new JMenu(getName());
    
    myMenu.add(createMoveInputMenu());

    myMenu.add(createMoveHighlightingMenu());

    JMenu pieceSetsMenu = createPieceSetsMenu();
    if (pieceSetsMenu != null)
      myMenu.add(pieceSetsMenu);

    JMenu boardsMenu = createBoardsMenu();
    if (boardsMenu != null)
      myMenu.add(boardsMenu);

    return myMenu;
  }




  /**
   * Creates and returns the "Move Input" menu.
   */

  protected JMenu createMoveInputMenu(){
    JMenu moveInputMenu = new JMenu("Move Input");
    moveInputMenu.setMnemonic('M');

    int moveInputStyle = getMoveInputStyle();
    JRadioButtonMenuItem dragndropMenuItem = new JRadioButtonMenuItem("Drag'n'Drop", moveInputStyle == JBoard.DRAG_N_DROP);
    JRadioButtonMenuItem clicknclickMenuItem = new JRadioButtonMenuItem("Click'n'Click", moveInputStyle == JBoard.CLICK_N_CLICK);

    dragndropMenuItem.setMnemonic('D');
    clicknclickMenuItem.setMnemonic('C');

    ButtonGroup inputModeGroup = new ButtonGroup();
    inputModeGroup.add(dragndropMenuItem);
    inputModeGroup.add(clicknclickMenuItem);

    dragndropMenuItem.setActionCommand("dragndrop");
    clicknclickMenuItem.setActionCommand("clicknclick");

    ActionListener inputModeListener = new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        String actionCommand = evt.getActionCommand();
        if ("dragndrop".equals(actionCommand))
          setMoveInputStyle(JBoard.DRAG_N_DROP);
        else if ("clicknclick".equals(actionCommand))
          setMoveInputStyle(JBoard.CLICK_N_CLICK);
        else
          throw new IllegalStateException("Unknown action command: "+actionCommand);
      }
    };

    dragndropMenuItem.addActionListener(inputModeListener);
    clicknclickMenuItem.addActionListener(inputModeListener);

    int draggedPieceStyle = getDraggedPieceStyle();
    JRadioButtonMenuItem normalDraggedPieceStyleMenuItem = new JRadioButtonMenuItem("Normal Dragged Piece",
      draggedPieceStyle == JBoard.NORMAL_DRAGGED_PIECE);
    JRadioButtonMenuItem targetDraggedPieceStyleMenuItem = new JRadioButtonMenuItem("Target Cursor",
      draggedPieceStyle == JBoard.CROSSHAIR_DRAGGED_PIECE);

    normalDraggedPieceStyleMenuItem.setMnemonic('N');
    targetDraggedPieceStyleMenuItem.setMnemonic('T');

    ButtonGroup draggedPieceStyleGroup = new ButtonGroup();
    draggedPieceStyleGroup.add(normalDraggedPieceStyleMenuItem);
    draggedPieceStyleGroup.add(targetDraggedPieceStyleMenuItem);

    normalDraggedPieceStyleMenuItem.setActionCommand("normal");
    targetDraggedPieceStyleMenuItem.setActionCommand("target");

    ActionListener draggedPieceStyleListener = new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        String actionCommand = evt.getActionCommand();

        if ("normal".equals(actionCommand))
          setDraggedPieceStyle(JBoard.NORMAL_DRAGGED_PIECE);
        else if ("target".equals(actionCommand))
          setDraggedPieceStyle(JBoard.CROSSHAIR_DRAGGED_PIECE);
        else
          throw new IllegalStateException("Unknown action command: "+actionCommand);
      }
    };

    normalDraggedPieceStyleMenuItem.addActionListener(draggedPieceStyleListener);
    targetDraggedPieceStyleMenuItem.addActionListener(draggedPieceStyleListener);

    JRadioButtonMenuItem legalChess = new JRadioButtonMenuItem("Legal Chess");
    JRadioButtonMenuItem predrag = new JRadioButtonMenuItem("Predrag");
    JRadioButtonMenuItem premove = new JRadioButtonMenuItem("Premove");

    switch (getMoveSendingMode()){
      case LEGAL_CHESS_MOVE_SENDING_MODE: legalChess.setSelected(true); break;
      case PREDRAG_MOVE_SENDING_MODE: predrag.setSelected(true); break;
      case PREMOVE_MOVE_SENDING_MODE: premove.setSelected(true); break;
      default:
        throw new IllegalStateException("Unrecognized move sending mode: "+getMoveSendingMode());
    }

    legalChess.setMnemonic('l');
    predrag.setMnemonic('p');
    premove.setMnemonic('m');

    legalChess.setActionCommand("legalchess");
    predrag.setActionCommand("predrag");
    premove.setActionCommand("premove");

    ActionListener moveSendingModeActionListener = new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        String actionCommand = evt.getActionCommand();

        if ("legalchess".equals(actionCommand))
          setMoveSendingMode(LEGAL_CHESS_MOVE_SENDING_MODE);
        else if ("predrag".equals(actionCommand))
          setMoveSendingMode(PREDRAG_MOVE_SENDING_MODE);
        else if ("premove".equals(actionCommand))
          setMoveSendingMode(PREMOVE_MOVE_SENDING_MODE);
        else
          throw new IllegalStateException("Unknown action command: "+actionCommand);
      }
    };

    legalChess.addActionListener(moveSendingModeActionListener);
    predrag.addActionListener(moveSendingModeActionListener);
    premove.addActionListener(moveSendingModeActionListener);

    ButtonGroup moveSendingModeButtonGroup = new ButtonGroup();
    moveSendingModeButtonGroup.add(legalChess);
    moveSendingModeButtonGroup.add(predrag);
    moveSendingModeButtonGroup.add(premove);

   
    final JCheckBoxMenuItem autoQueenMenuItem =
      new JCheckBoxMenuItem("Auto Queen", isAutoPromote());
    autoQueenMenuItem.setMnemonic('A');
    autoQueenMenuItem.addChangeListener(new ChangeListener(){
      public void stateChanged(ChangeEvent evt){
        setAutoPromote(autoQueenMenuItem.isSelected());
      }
    });

    moveInputMenu.add(dragndropMenuItem);
    moveInputMenu.add(clicknclickMenuItem);
    moveInputMenu.addSeparator();
    moveInputMenu.add(normalDraggedPieceStyleMenuItem);
    moveInputMenu.add(targetDraggedPieceStyleMenuItem);
    moveInputMenu.addSeparator();
    moveInputMenu.add(legalChess);
    moveInputMenu.add(predrag);
    moveInputMenu.add(premove);
    moveInputMenu.addSeparator();
    moveInputMenu.add(autoQueenMenuItem);

    return moveInputMenu;
  }




  /**
   * Creates and returns the "Move Highlighting" menu.
   */

  protected JMenu createMoveHighlightingMenu(){
    int moveHighlightingStyle = getMoveHighlightingStyle();
    JMenu moveHighlightingMenu =  new JMenu("Move Highlighting");
    moveHighlightingMenu.setMnemonic('H');
    
    JRadioButtonMenuItem noMoveHighlightingStyleMenuItem = new JRadioButtonMenuItem("No Highlighting",
      moveHighlightingStyle == JBoard.NO_MOVE_HIGHLIGHTING);
    JRadioButtonMenuItem squareMoveHighlightingStyleMenuItem = new JRadioButtonMenuItem("Square Highlighting",
      moveHighlightingStyle == JBoard.SQUARE_MOVE_HIGHLIGHTING);
    JRadioButtonMenuItem arrowMoveHighlightingStyleMenuItem = new JRadioButtonMenuItem("Arrow Highlighting",
      moveHighlightingStyle == JBoard.ARROW_MOVE_HIGHLIGHTING);

    noMoveHighlightingStyleMenuItem.setMnemonic('N');
    squareMoveHighlightingStyleMenuItem.setMnemonic('S');
    arrowMoveHighlightingStyleMenuItem.setMnemonic('A');

    ButtonGroup moveHighlightStyleGroup = new ButtonGroup();
    moveHighlightStyleGroup.add(noMoveHighlightingStyleMenuItem);
    moveHighlightStyleGroup.add(squareMoveHighlightingStyleMenuItem);
    moveHighlightStyleGroup.add(arrowMoveHighlightingStyleMenuItem);

    noMoveHighlightingStyleMenuItem.setActionCommand("none");
    squareMoveHighlightingStyleMenuItem.setActionCommand("square");
    arrowMoveHighlightingStyleMenuItem.setActionCommand("arrow");

    ActionListener moveHighlightingStyleListener = new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        String actionCommand = evt.getActionCommand();

        if ("none".equals(actionCommand))
          setMoveHighlightingStyle(JBoard.NO_MOVE_HIGHLIGHTING);
        else if ("square".equals(actionCommand))
          setMoveHighlightingStyle(JBoard.SQUARE_MOVE_HIGHLIGHTING);
        else if ("arrow".equals(actionCommand))
          setMoveHighlightingStyle(JBoard.ARROW_MOVE_HIGHLIGHTING);
        else
          throw new IllegalStateException("Unknown action command: "+actionCommand);
      }
    };

    noMoveHighlightingStyleMenuItem.addActionListener(moveHighlightingStyleListener);
    squareMoveHighlightingStyleMenuItem.addActionListener(moveHighlightingStyleListener);
    arrowMoveHighlightingStyleMenuItem.addActionListener(moveHighlightingStyleListener);


    boolean highlightOwnMoves = isHighlightingOwnMoves();
    final JCheckBoxMenuItem highlightOwnMovesCheckBox = new JCheckBoxMenuItem("Highlight Own Moves", highlightOwnMoves);
    highlightOwnMovesCheckBox.setMnemonic('H');
    highlightOwnMovesCheckBox.addChangeListener(new ChangeListener(){
      public void stateChanged(ChangeEvent evt){
        setHighlightingOwnMoves(highlightOwnMovesCheckBox.isSelected());
      }
    });
    
    moveHighlightingMenu.add(noMoveHighlightingStyleMenuItem);
    moveHighlightingMenu.add(squareMoveHighlightingStyleMenuItem);
    moveHighlightingMenu.add(arrowMoveHighlightingStyleMenuItem);
    moveHighlightingMenu.addSeparator();
    moveHighlightingMenu.add(highlightOwnMovesCheckBox);

    return moveHighlightingMenu;
  }




  /**
   * Creates and returns the "Piece Sets" menu. This may return null so
   * that no such menu is displayed. The default implementation will return null
   * if less than 2 piece sets are specified in parameters.
   */

  protected JMenu createPieceSetsMenu(){
    int pieceSetCount = Integer.parseInt(getProperty("piece-set-count", "0"));
    if (pieceSetCount < 2)
      return null;

    ActionListener pieceSetListener = new ActionListener(){

      public void actionPerformed(ActionEvent evt){
        AbstractButton button = (AbstractButton)evt.getSource();
        setPiecePainter(button.getActionCommand());
      } 
    };

    JMenu pieceSetsMenu = new JMenu("Piece Sets");
    pieceSetsMenu.setMnemonic('P');
    String piecePainterClassName = getPiecePainter().getClass().getName();
    ButtonGroup pieceSetsCheckBoxGroup = new ButtonGroup();
    for (int i = 0; i < pieceSetCount; i++){
      String pieceSet = getProperty("piece-set-"+i);
      StringTokenizer tokenizer = new StringTokenizer(pieceSet, ";");
      String pieceSetName = tokenizer.nextToken();
      String className = tokenizer.nextToken();
      if (pieceSet == null){
        System.err.println("Piece set with index "+i+" is not specified");
        continue;
      }
      JRadioButtonMenuItem menuCheckBox = new JRadioButtonMenuItem(pieceSetName);
      menuCheckBox.setActionCommand(className);
      if (className.equals(piecePainterClassName))
        menuCheckBox.setSelected(true);
      menuCheckBox.addActionListener(pieceSetListener);
      pieceSetsCheckBoxGroup.add(menuCheckBox);
      pieceSetsMenu.add(menuCheckBox);
    }
    
    return pieceSetsMenu;
  }




  /**
   * Creates and returns the "Boards" menu. This may return null so
   * that no such menu is displayed. The default implementation will return null
   * if less than 2 piece sets are specified in parameters.
   */

  protected JMenu createBoardsMenu(){
    int boardCount = Integer.parseInt(getProperty("board-count", "0"));
    if (boardCount < 2)
      return null;

    ActionListener boardChangeListener = new ActionListener(){

      public void actionPerformed(ActionEvent evt){
        AbstractButton button = (AbstractButton)evt.getSource();
        setBoardPainter(button.getActionCommand());
      } 
    };

    JMenu boardsMenu = new JMenu("Boards");
    boardsMenu.setMnemonic('B');
    String boardPainterClassName = getBoardPainter().getClass().getName();
    ButtonGroup boardsCheckBoxGroup = new ButtonGroup();
    for (int i = 0 ; i < boardCount; i++){
      String board = getProperty("board-"+i);
      StringTokenizer tokenizer = new StringTokenizer(board, ";");
      String boardName = tokenizer.nextToken();
      String className = tokenizer.nextToken();
      if (board == null){
        System.err.println("Board with index "+i+" is not specified");
        continue;
      }
      JRadioButtonMenuItem menuCheckBox = new JRadioButtonMenuItem(boardName);
      menuCheckBox.setActionCommand(className);
      if (className.equals(boardPainterClassName))
        menuCheckBox.setSelected(true);
      menuCheckBox.addActionListener(boardChangeListener);
      boardsCheckBoxGroup.add(menuCheckBox);
      boardsMenu.add(menuCheckBox);
    }
    
    return boardsMenu;
  }




  /**
   * Unregisters all the necessary listeners from the Connection. 
   */

  protected void unregisterConnListeners(){
    JinListenerManager listenerManager = getConnection().getJinListenerManager();

    listenerManager.removeGameListener(this);
  } 



  /**
   * Removes the JInternalFrames of all the displayed BoardPanels. Also loses
   * references to all the BoardPanels.
   */

  private void removeBoards(){
    Enumeration frames = internalFramesToBoardPanels.keys();
    while (frames.hasMoreElements()){
      JInternalFrame boardFrame = (JInternalFrame)frames.nextElement();
      boardFrame.dispose();
    }

    internalFramesToBoardPanels.clear();
    gamesToBoardPanels.clear();
    boardPanelsToInternalFrames.clear();
    unusedInternalFrames.removeAllElements();
  }




  /**
   * Returns the BoardPanel displaying the given Game.
   */

  public BoardPanel getBoardPanel(Game game){
    return (BoardPanel)gamesToBoardPanels.get(game);
  }





  /**
   * Gets called when a game starts. Creates a new BoardPanel and a
   * JInternalFrame to put it in and displays it.
   */

  public void gameStarted(GameStartEvent evt){
    Game game = evt.getGame();
    BoardPanel boardPanel = createBoardPanel(game);
    initBoardPanel(game, boardPanel);

    // Artificially invoke this, since it would not get called otherwise
    // (we're already in the middle of the dispatching of that event).
    boardPanel.gameStarted(evt);
  }




  /**
   * Creates and configures a new JInternalFrame for use.
   */

  private JInternalFrame createNewBoardFrame(BoardPanel boardPanel){
    JInternalFrame boardFrame = createBoardFrame(boardPanel);

    int index = internalFrames.size();
    for (int i = 0; i < internalFrames.size(); i++){
      if (internalFrames.elementAt(i) == null){
        index = i;
        break;
      }
    }
    internalFrames.insertElementAt(boardFrame, index);
    boardFrame.addInternalFrameListener(this);

    JDesktopPane desktop = getPluginContext().getMainFrame().getDesktop();
    desktop.add(boardFrame);

    configureBoardFrame(boardFrame, index);

    Container boardFrameContentPane = boardFrame.getContentPane();
    boardFrameContentPane.setLayout(new BorderLayout());
    boardFrameContentPane.add(boardPanel, BorderLayout.CENTER);

    /* See http://developer.java.sun.com/developer/bugParade/bugs/4176136.html for the 
       reason I do this instead of adding an InternalFrameListener like a sane person. */
    boardFrame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
    boardFrame.addVetoableChangeListener(this);

    return boardFrame;
  }





  /**
   * Takes a JInternalFrame from the unused board frames list and configures it
   * to be reused.
   */

  private JInternalFrame reuseBoardFrame(BoardPanel boardPanel){
    JInternalFrame boardFrame = (JInternalFrame)unusedInternalFrames.lastElement();
    unusedInternalFrames.removeElementAt(unusedInternalFrames.size()-1);

    BoardPanel oldBoardPanel = (BoardPanel)internalFramesToBoardPanels.remove(boardFrame);
    oldBoardPanel.done();
    boardPanelsToInternalFrames.remove(oldBoardPanel);

    Container contentPane = boardFrame.getContentPane();
    contentPane.removeAll();
    contentPane.add(boardPanel, BorderLayout.CENTER);
    contentPane.invalidate();
    contentPane.validate();

    if (boardFrame.isIcon()){
      try{
        boardFrame.setIcon(false);
      } catch (PropertyVetoException e){}
    }

    return boardFrame;
  }

  


  /**
   * Creates and configures a new BoardPanel for the given Game.
   */

  protected BoardPanel createBoardPanel(Game game){
    BoardPanel boardPanel = new BoardPanel(this, game);

    return boardPanel;
  }



  
  /**
   * Initializes the given BoardPanel, making it ready for use.
   */

  protected void initBoardPanel(Game game, BoardPanel boardPanel){
    boardPanel.addUserMoveListener(this);
    getConnection().getJinListenerManager().addGameListener(boardPanel);
    gamesToBoardPanels.put(game, boardPanel);

    JInternalFrame boardFrame;
    if (unusedInternalFrames.isEmpty()){
      boardFrame = createNewBoardFrame(boardPanel);
    }
    else{
      boardFrame = reuseBoardFrame(boardPanel);
    }

    boardFrame.setTitle(boardPanel.getTitle());
    boardFrame.repaint(); // It doesn't seem to repaint itself automatically.

    internalFramesToBoardPanels.put(boardFrame, boardPanel);
    boardPanelsToInternalFrames.put(boardPanel, boardFrame);

    if (!boardFrame.isVisible())
      boardFrame.setVisible(true);

    boardFrame.toFront();
    try{
      boardFrame.setSelected(true);
    } catch (java.beans.PropertyVetoException e){} // Ignore.
  }




  /**
   * Creates a JInternalFrame to be used for displaying the given
   * BoardPanel. 
   */

  protected JInternalFrame createBoardFrame(BoardPanel boardPanel){
    JInternalFrame boardFrame = new FixedJInternalFrame();

    boardFrame.setResizable(true);
    boardFrame.setClosable(true);
    boardFrame.setMaximizable(true);
    boardFrame.setIconifiable(true);

    return boardFrame;
  }





  /**
   * Sets the various properties of the given JInternalFrame from the saved
   * properties of the JInternalFrame with the same index. If no JInternalFrame
   * with the given index ever existed, sets those properties to some reasonable
   * defaults.
   */

  private void configureBoardFrame(JInternalFrame boardFrame, int index){
    JDesktopPane desktop = getPluginContext().getMainFrame().getDesktop();

    Rectangle desktopBounds = new Rectangle(desktop.getSize());
    String boundsString = getProperty("frame-bounds-"+index);
    Rectangle bounds = null;
    if (boundsString!=null)
      bounds = StringParser.parseRectangle(boundsString);

    if (bounds==null)
      boardFrame.setBounds(desktopBounds.width/4, 0, desktopBounds.width*3/4, desktopBounds.height*3/4);
    else
      boardFrame.setBounds(bounds);

    boolean isMaximized = Boolean.valueOf(getProperty("maximized-"+index,"false")).booleanValue();
    if (isMaximized){
      try{
        boardFrame.setMaximum(true);
      } catch (java.beans.PropertyVetoException e){}
    }

    boolean isIconified = Boolean.valueOf(getProperty("iconified-"+index,"false")).booleanValue();
    if (isIconified){
      try{
        boardFrame.setIcon(true);
      } catch (java.beans.PropertyVetoException e){}
    }

    JComponent icon = boardFrame.getDesktopIcon();
    String iconBoundsString = getProperty("frame-icon-bounds-"+index);
    if (iconBoundsString!=null){
      Rectangle iconBounds = StringParser.parseRectangle(iconBoundsString);
      icon.setBounds(iconBounds);
    }

    String iconImageName = getProperty("icon-image");
    if (iconImageName != null){
      URL iconImageURL = BoardManager.class.getResource(iconImageName);
      if (iconImageURL!= null)
        boardFrame.setFrameIcon(new ImageIcon(iconImageURL));
    } 
  }




  /**
   * GameListener implementation.
   */

  public void moveMade(MoveMadeEvent evt){}
  public void positionChanged(PositionChangedEvent evt){}
  public void takebackOccurred(TakebackEvent evt){}
  public void illegalMoveAttempted(IllegalMoveEvent evt){}
  public void clockAdjusted(ClockAdjustmentEvent evt){}
  public void boardFlipped(BoardFlipEvent evt){}





  /**
   * Gets called when a game ends. Notifies the BoardPanel displaying the board
   * marking it unused.
   */

  public void gameEnded(GameEndEvent evt){
    BoardPanel boardPanel = (BoardPanel)gamesToBoardPanels.remove(evt.getGame());
    if (boardPanel != null){
      getConnection().getJinListenerManager().removeGameListener(boardPanel);
      boardPanel.removeUserMoveListener(this);
      boardPanel.setInactive();
      JInternalFrame boardFrame = (JInternalFrame)boardPanelsToInternalFrames.get(boardPanel);
//      JInternalFrame boardFrame = (JInternalFrame)boardPanelsToInternalFrames.remove(boardPanel);
//      internalFramesToBoardPanels.remove(boardFrame);
      boardFrame.setTitle(boardPanel.getTitle());
      boardFrame.repaint(); // It doesn't seem to repaint itself.
      if (boardFrame.isClosed()){
        internalFramesToBoardPanels.remove(boardFrame);
        boardPanelsToInternalFrames.remove(boardPanel);
      }
      else
        unusedInternalFrames.addElement(boardFrame);
    }
  }





  /**
   * Gets called when the user makes a move on the board.
   */

  public void userMadeMove(UserMoveEvent evt){
    Object src = evt.getSource();

    if (src instanceof BoardPanel){
      BoardPanel boardPanel = (BoardPanel)src;
      Game game = boardPanel.getGame();
      getConnection().makeMove(game, evt.getMove());
    }
  }




  /**
   * VetoableChangeListener implementation. See http://developer.java.sun.com/developer/bugParade/bugs/4176136.html
   * for the reason this is needed.
   */

  public void vetoableChange(PropertyChangeEvent pce) throws PropertyVetoException{
    Object source = pce.getSource();

    if (source instanceof JInternalFrame){
      if (pce.getPropertyName().equals(JInternalFrame.IS_CLOSED_PROPERTY) &&
          pce.getOldValue().equals(Boolean.FALSE)&&pce.getNewValue().equals(Boolean.TRUE)){
        JInternalFrame boardFrame =  (JInternalFrame)source;
        BoardPanel boardPanel = (BoardPanel)internalFramesToBoardPanels.get(boardFrame);

        if (boardPanel.isActive()){ // isActive()==true, otherwise, the user is just closing a "dead" frame.
          Game game = ((BoardPanel)internalFramesToBoardPanels.get(boardFrame)).getGame();

          boolean shouldAsk = false;
          String question = null;

          if (game.getGameType() == Game.MY_GAME){
            shouldAsk = true;
            if (game.isPlayed())
              question = "Are you sure you want to RESIGN this game?";
            else
              question = "Are you sure you want to stop examining this game?";
          }

          int result;
          if (shouldAsk)
            result = JOptionPane.showConfirmDialog(getPluginContext().getMainFrame(), question, "Select an option", JOptionPane.YES_NO_OPTION);
          else
            result = JOptionPane.YES_OPTION;
          if (result == JOptionPane.YES_OPTION)
            getConnection().quitGame(game);
          else
            throw new PropertyVetoException("Canceled closing", pce);
        }
      }
    }
  }





  /**
   * InternalFrameListener implementation.
   */

  public void internalFrameClosed(InternalFrameEvent e){
    JInternalFrame frame = (JInternalFrame)e.getSource();

    int index = -1;
    for (int i = 0 ; i < internalFrames.size(); i++){
      if (internalFrames.elementAt(i) == frame){
        index = i;
        break;
      }
    }
    if (index==-1)
      throw new IllegalStateException("No matching frame found");

    User user = getUser();
    String prefix = getID()+".";

    boolean isMaximized = frame.isMaximum();
    user.setProperty(prefix+"maximized-"+index, String.valueOf(isMaximized));

    boolean isIconified = frame.isIcon();
    user.setProperty(prefix+"iconified-"+index, String.valueOf(isIconified));

    // This is the only way to retrieve the "normal" bounds of the frame under
    // JDK1.2 and earlier. JDK1.3 has a getNormalBounds() method.
    if (isMaximized){
      try{
        frame.setMaximum(false);
      } catch (java.beans.PropertyVetoException ex){}
    }

    Rectangle frameBounds = frame.getBounds();
    // If something bad happened, let's not save that state.
    if ((frameBounds.width > 10) && (frameBounds.height > 10))
      user.setProperty(prefix + "frame-bounds-" + index,
        StringEncoder.encodeRectangle(frameBounds));

    Rectangle iconBounds = frame.getDesktopIcon().getBounds();
    user.setProperty(prefix + "frame-icon-bounds-" + index,
      StringEncoder.encodeRectangle(iconBounds));

    internalFrames.setElementAt(null, index);
    frame.removeInternalFrameListener(this);
    unusedInternalFrames.removeElement(frame);

    BoardPanel boardPanel = (BoardPanel)internalFramesToBoardPanels.get(frame);

    if (!boardPanel.isActive()){
      internalFramesToBoardPanels.remove(frame);
      boardPanelsToInternalFrames.remove(boardPanel);
    }

    boardPanel.done();
  }



  /**
   * InternalFrameListener implementation.
   */

  public void internalFrameActivated(InternalFrameEvent e){}
  public void internalFrameClosing(InternalFrameEvent e){}
  public void internalFrameDeactivated(InternalFrameEvent e){}
  public void internalFrameDeiconified(InternalFrameEvent e){}
  public void internalFrameIconified(InternalFrameEvent e){}
  public void internalFrameOpened(InternalFrameEvent e){}




  /**
   * Overrides <code>Plugin.hasPreferencesUI()</code> to return
   * <code>true</code>.
   */
  
  public boolean hasPreferencesUI(){
    return true;
  }




  /**
   * Creates and returns a PreferencesPanel which allows the user to modify
   * some of this plugin's settings.
   */

  public PreferencesPanel getPreferencesUI(){
    return new BoardPreferencesPanel(this);
  }




  /**
   * Saves the user preferences.
   */

  public void saveState(){
    setProperty("piece-painter-class-name", getPiecePainter().getClass().getName());    

    setProperty("board-painter-class-name", getBoardPainter().getClass().getName());

    setProperty("auto-promote", String.valueOf(isAutoPromote()));

    setProperty("move-input-style", getMoveInputStyle() == JBoard.CLICK_N_CLICK ?
      "click'n'click" : "drag'n'drop");

    setProperty("dragged-piece-style", getDraggedPieceStyle() == JBoard.CROSSHAIR_DRAGGED_PIECE ?
      "target-cursor" : "normal-cursor");

    int moveHighlightingStyle = getMoveHighlightingStyle();
    String moveHighlightingString;
    if (moveHighlightingStyle == JBoard.NO_MOVE_HIGHLIGHTING)
      moveHighlightingString = "none";
    else if (moveHighlightingStyle == JBoard.SQUARE_MOVE_HIGHLIGHTING)
      moveHighlightingString = "square";
    else // if (moveHighlightingStyle == JBoard.ARROW_MOVE_HIGHLIGHTING)
      moveHighlightingString = "arrow";
    setProperty("move-highlight.style", moveHighlightingString);

    setProperty("move-highlight.highlight-own", String.valueOf(isHighlightingOwnMoves()));

    setProperty("move-highlight.color", StringEncoder.encodeColor(getMoveHighlightingColor()));

    setProperty("drag-square-highlighting.color",
      StringEncoder.encodeColor(getDragSquareHighlightingColor()));

    int moveSendingMode = getMoveSendingMode();
    String moveSendingModeString;
    if (moveSendingMode == LEGAL_CHESS_MOVE_SENDING_MODE)
      moveSendingModeString = "legal-chess";
    else if (moveSendingMode == PREDRAG_MOVE_SENDING_MODE)
      moveSendingModeString = "predrag";
    else // if (moveSendingMode == PREMOVE_MOVE_SENDING_MODE)
      moveSendingModeString = "premove";
    setProperty("move-sending-mode", moveSendingModeString);

    setProperty("white-piece-color", encodeColor(getWhitePieceColor()));
    setProperty("black-piece-color", encodeColor(getBlackPieceColor()));
    setProperty("white-outline-color", encodeColor(getWhiteOutlineColor()));
    setProperty("black-outline-color", encodeColor(getBlackOutlineColor()));

    setProperty("light-square-color", encodeColor(getLightSquareColor()));
    setProperty("dark-square-color", encodeColor(getDarkSquareColor()));
  }



  /**
   * If the specified color is null, returns null. Otherwise encodes it and
   * returns the resulting string.
   */

  private static String encodeColor(Color color){
    if (color == null)
      return null;
    else
      return StringEncoder.encodeColor(color);
  }


}
