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

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import free.jin.plugin.PreferencesPanel;
import free.util.swing.ColorChooserButton;


/**
 * The preferences panel for the BoardManager plugin.
 */

public class BoardPreferencesPanel extends PreferencesPanel{


  
  /**
   * The BoardManager whose preferences panel this BoardPreferencesPanel is.
   */

  private BoardManager boardManager;




  /**
   * The ColorChooserButton for selecting the color of the white pieces.
   */

  private ColorChooserButton whiteColorChooser;




  /**
   * The ColorChooserButton for selecting the color of the black pieces.
   */

  private ColorChooserButton blackColorChooser;




  /**
   * The ColorChooserButton for selecting the color of the outline for white
   * pieces.
   */

  private ColorChooserButton whiteOutlineChooser;




  /**
   * The ColorChooserButton for selecting the color of the outline for black
   * pieces.
   */

  private ColorChooserButton blackOutlineChooser;




  /**
   * The ColorChooserButton for selecting the color of the light SQUARES.
   */

  private ColorChooserButton lightColorChooser;




  /**
   * The ColorChooserButton for selecting the color of the dark SQUARES.
   */

  private ColorChooserButton darkColorChooser;



  /**
   * The ColorChooserButton for selecting the move highlighting color.
   */

  private ColorChooserButton moveHighlightingColorChooser;



  /**
   * The ColorChooserButton for selecting the drag square highlighting color.
   */

  private ColorChooserButton dragSquareHighlightingColorChooser;




  /**
   * Creates a new BoardPreferencesPanel with the given BoardManager.
   */

  public BoardPreferencesPanel(BoardManager boardManager){
    this.boardManager = boardManager;

    whiteColorChooser = new ColorChooserButton("White Pieces", boardManager.getWhitePieceColor());
    blackColorChooser = new ColorChooserButton("Black Pieces", boardManager.getBlackPieceColor());
    whiteOutlineChooser = new ColorChooserButton("White Pieces' Outline",
      boardManager.getWhiteOutlineColor());
    blackOutlineChooser = new ColorChooserButton("Black Pieces' Outline",
      boardManager.getBlackOutlineColor());

    whiteColorChooser.setMnemonic('W');
    blackColorChooser.setMnemonic('B');
    whiteOutlineChooser.setMnemonic('P');
    blackOutlineChooser.setMnemonic('k');


    lightColorChooser = new ColorChooserButton("Light Squares", boardManager.getLightSquareColor());
    darkColorChooser = new ColorChooserButton("Dark Squares", boardManager.getDarkSquareColor());

    lightColorChooser.setMnemonic('L');
    darkColorChooser.setMnemonic('D');


    moveHighlightingColorChooser = new ColorChooserButton("Move Highlighting",
      boardManager.getMoveHighlightingColor());
    dragSquareHighlightingColorChooser = new ColorChooserButton("Drag Square Highlighting",
      boardManager.getDragSquareHighlightingColor());

    moveHighlightingColorChooser.setMnemonic('M');
    dragSquareHighlightingColorChooser.setMnemonic('S');


    ChangeListener changeNotifyListener = new ChangeListener(){
      public void stateChanged(ChangeEvent evt){
        fireStateChanged();
      }
    };

    whiteColorChooser.addChangeListener(changeNotifyListener);
    blackColorChooser.addChangeListener(changeNotifyListener);
    whiteOutlineChooser.addChangeListener(changeNotifyListener);
    blackOutlineChooser.addChangeListener(changeNotifyListener);
    lightColorChooser.addChangeListener(changeNotifyListener);
    darkColorChooser.addChangeListener(changeNotifyListener);
    moveHighlightingColorChooser.addChangeListener(changeNotifyListener);
    dragSquareHighlightingColorChooser.addChangeListener(changeNotifyListener);

    createUI();
  }



  /**
   * Creates the UI.
   */

  private void createUI(){
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    JPanel piecesPanel = new JPanel(new GridLayout(2, 2, 5, 5));
    piecesPanel.add(whiteColorChooser);
    piecesPanel.add(blackColorChooser);
    piecesPanel.add(whiteOutlineChooser);
    piecesPanel.add(blackOutlineChooser);
    Border innerBorder = new EmptyBorder(5, 5, 5, 5);
    Border outerBorder = new TitledBorder(" Piece preferences (*) ");
    piecesPanel.setBorder(new CompoundBorder(outerBorder, innerBorder));

    JPanel boardPanel = new JPanel(new GridLayout(1, 2, 5, 5));
    boardPanel.add(lightColorChooser);
    boardPanel.add(darkColorChooser);
    innerBorder = new EmptyBorder(5, 5, 5, 5);
    outerBorder = new TitledBorder(" Board preferences (*) ");
    boardPanel.setBorder(new CompoundBorder(outerBorder, innerBorder));

    JPanel highlightPanel = new JPanel(new GridLayout(1, 2, 5, 5));
    highlightPanel.add(moveHighlightingColorChooser);
    highlightPanel.add(dragSquareHighlightingColorChooser);
    innerBorder = new EmptyBorder(5, 5, 5, 5);
    outerBorder = new TitledBorder(" Highlighting preferences ");
    highlightPanel.setBorder(new CompoundBorder(outerBorder, innerBorder));

    JPanel notePanel = new JPanel(new BorderLayout());
    notePanel.add(new JLabel("* - Note that the color preferences only affect vector pieces and the solid color board"), BorderLayout.CENTER);

    add(piecesPanel);
    add(Box.createVerticalStrut(10));
    add(boardPanel);
    add(Box.createVerticalStrut(10));
    add(highlightPanel);
    add(Box.createVerticalStrut(10));
    add(notePanel);
  }




  /**
   * Applies the changes done by the user.
   */

  public void applyChanges(){
    boardManager.setWhitePieceColor(whiteColorChooser.getColor());
    boardManager.setBlackPieceColor(blackColorChooser.getColor());
    boardManager.setWhiteOutlineColor(whiteOutlineChooser.getColor());
    boardManager.setBlackOutlineColor(blackOutlineChooser.getColor());

    boardManager.setLightSquareColor(lightColorChooser.getColor());
    boardManager.setDarkSquareColor(darkColorChooser.getColor());

    boardManager.setMoveHighlightingColor(moveHighlightingColorChooser.getColor());
    boardManager.setDragSquareHighlightingColor(dragSquareHighlightingColorChooser.getColor());
  }


}
