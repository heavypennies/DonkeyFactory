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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import free.jin.Game;
import free.jin.JinConnection;
import free.jin.plugin.Plugin;
import free.workarounds.FixedJPanel;


/**
 * The panel which contains all the action buttons for a played game of type
 * Game.MY_GAME.
 */

public class PlayedGameButtonPanel extends FixedJPanel implements ActionListener{



  /**
   * The Plugin we're being used by.
   */

  protected final Plugin plugin;




  /**
   * The Game for which this PlayedGameButtonPanel is used.
   */

  protected final Game game;




  /**
   * The component over which JOptionPane dialogs are displayed.
   */

  protected final Component parentComponent;




  /**
   * The "Resign" button.
   */

  protected JButton resignButton;
  



  /**
   * The "Draw" button.
   */

  protected JButton drawButton;




  /**
   * The "Abort" button.
   */

  protected JButton abortButton;




  /**
   * The "Adjourn" button.
   */

  protected JButton adjournButton;





  /**
   * Creates a new PlayedGameButtonPanel. It will be used by the given Plugin
   * for the given Game. The given parent Component determines over which component
   * JOptionPane dialogs will be displayed.
   */

  public PlayedGameButtonPanel(Plugin plugin, Game game, Component parentComponent){
    this.plugin = plugin;
    this.game = game;
    this.parentComponent = parentComponent;

    init(plugin, game);
  }




  /**
   * Initializes this PlayedGameButtonPanel. This method calls delegates to
   * {@link #createComponents(Plugin, Game)} and
   * {@link #addComponents(Plugin, Game)}
   */

  protected void init(Plugin plugin, Game game){
    createComponents(plugin, game);
    addComponents(plugin, game);
  }




  /**
   * Creates all the components of this PlayedGameButtonPanel.
   */

  protected void createComponents(Plugin plugin, Game game){
    JinConnection conn = plugin.getConnection();

    resignButton = createResignButton(plugin, game);
    drawButton = createDrawButton(plugin, game);
    abortButton = conn.isAbortSupported() ? createAbortButton(plugin, game) : null;
    adjournButton = conn.isAdjournSupported() ? createAdjournButton(plugin, game) : null;
  }




  /**
   * Creates the "Resign" button.
   */

  protected JButton createResignButton(Plugin plugin, Game game){
    JButton button = new JButton("Resign");
    button.setFont(new Font("SansSerif", Font.BOLD, 15));
    button.setToolTipText("Resign the game");
    button.addActionListener(this);
    button.setFocusPainted(false);

    return button;
  }




  /**
   * Creates the "Draw" button.
   */

  protected JButton createDrawButton(Plugin plugin, Game game){
    JButton button = new JButton("Draw");
    button.setFont(new Font("SansSerif", Font.BOLD, 15));
    button.setToolTipText("Offer or claim draw");
    button.addActionListener(this);
    button.setFocusPainted(false);

    return button;
  }




  /**
   * Creates the "Abort" button.
   */

  protected JButton createAbortButton(Plugin plugin, Game game){
    JButton button = new JButton("Abort");
    button.setFont(new Font("SansSerif", Font.BOLD, 15));
    button.setToolTipText("Abort or request to abort the game");
    button.addActionListener(this);
    button.setFocusPainted(false);

    return button;
  }




  /**
   * Creates the "Adjourn" button.
   */

  protected JButton createAdjournButton(Plugin plugin, Game game){
    JButton button = new JButton("Adjourn");
    button.setFont(new Font("SansSerif", Font.BOLD, 15));
    button.setToolTipText("Adjourn or request to adjourn the game");
    button.addActionListener(this);
    button.setFocusPainted(false);

    return button;
  }




  /**
   * Adds all the components to this PlayedGameButtonPanel.
   */

  protected void addComponents(Plugin plugin, Game game){
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    Box upperBox = Box.createHorizontalBox();
    upperBox.add(drawButton);
    upperBox.add(Box.createHorizontalStrut(10));
    upperBox.add(resignButton);

    add(upperBox);

    if ((abortButton!=null)||(adjournButton!=null)){
      Box lowerBox = Box.createHorizontalBox();
      if (abortButton!=null){
        lowerBox.add(abortButton);
        lowerBox.add(Box.createHorizontalStrut(10));
      }
      if (adjournButton!=null)
        lowerBox.add(adjournButton);

      add(Box.createVerticalStrut(10));
      add(lowerBox);
    }

  }



  /**
   * ActionListener implementation. Executes the appropriate command depending
   * on the button that was pressed.
   */

  public void actionPerformed(ActionEvent evt){
    Object source = evt.getSource();

    JinConnection conn = plugin.getConnection();
    if (source==resignButton){
      int result = JOptionPane.showConfirmDialog(parentComponent, "Are you sure you want to resign?", "Resign?", JOptionPane.YES_NO_OPTION);
      if (result==JOptionPane.YES_OPTION)
        conn.resign(game);
    }
    else if (source==drawButton){
      conn.requestDraw(game);
    }
    else if (source==abortButton){
      conn.requestAbort(game);
    }
    else if (source==adjournButton){
      conn.requestAdjourn(game);
    }
  }




  /**
   * Overrides getMaximumSize() to return the value of getPreferredSize().
   */

  public Dimension getMaximumSize(){
    return getPreferredSize();
  }


}
