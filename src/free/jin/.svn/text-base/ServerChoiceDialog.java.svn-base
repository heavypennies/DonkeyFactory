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

package free.jin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.border.EmptyBorder;
import free.util.swing.SwingUtils;
import free.util.BrowserControl;


/**
 * The dialog that lets the user choose the server he wants to connect to.
 */

public class ServerChoiceDialog extends JDialog{


  /**
   * The ListModel holding the server list.
   */

  private final DefaultListModel serverListModel;



  /**
   * The selected Server.
   */

  private Server server = null;



  /**
   * Creates a new <code>ServerChoiceDialog</code> with the specified parent
   * <code>Frame</code> and list of servers.
   */

  public ServerChoiceDialog(Frame parent, Server [] serverList){
    super(parent, "Choose", true);

    SwingUtils.registerEscapeCloser(this);

    serverListModel = new DefaultListModel();
    for (int i = 0; i < serverList.length; i++)
      serverListModel.addElement(serverList[i]);

    createUI();
  }



  /**
   * Shows the dialog and returns the selected <code>Server</code> or
   * <code>null</code> if the user canceled.
   */

  public Server getChoice(){
    setVisible(true);

    return server;
  }



  /**
   * Creates the UI for this dialog.
   */

  private void createUI(){
    final JList list = new JList(serverListModel);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scrollPane = new JScrollPane(list);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setPreferredSize(new Dimension(300, 80));

    JLabel chooseLabel = new JLabel("Choose a server to connect to");
    chooseLabel.setDisplayedMnemonic('C');
    chooseLabel.setLabelFor(list);

    final JButton okButton = new JButton("OK");
    JButton cancelButton = new JButton("Cancel");
    final JButton showWebsiteButton = new JButton("Server's Website");
    showWebsiteButton.setMnemonic('s');

    okButton.setEnabled(false);
    showWebsiteButton.setEnabled(false);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);
    buttonPanel.add(showWebsiteButton);

    list.addListSelectionListener(new ListSelectionListener(){
      public void valueChanged(ListSelectionEvent evt){
        int selectedIndex = list.getSelectedIndex();
        boolean enabled = (selectedIndex != -1);
        okButton.setEnabled(enabled);
        showWebsiteButton.setEnabled(enabled);
      }
    });

    okButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        server = (Server)list.getSelectedValue();
        dispose();
      }
    });

    list.addMouseListener(new MouseAdapter(){
      public void mouseClicked(MouseEvent evt){
        if ((evt.getClickCount() == 2) && (evt.getModifiers() == KeyEvent.BUTTON1_MASK)){
          server = (Server)list.getSelectedValue();
          if (server != null)
            dispose();
        }
      }
    });

    cancelButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        server = null;
        dispose();
      }
    });

    showWebsiteButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        server = (Server)list.getSelectedValue();
        String url = server.getWebsite().toExternalForm();
        try{
          BrowserControl.displayURL(url);
        } catch (java.io.IOException e){
            JOptionPane.showMessageDialog(getParent(), "Unable to display URL: "+url, "Error", JOptionPane.ERROR_MESSAGE);
          }
      }
    });

    JPanel content = new JPanel(new BorderLayout(5, 5));
    content.setBorder(new EmptyBorder(5, 5, 5, 5));
    content.add(chooseLabel, BorderLayout.NORTH);
    content.add(scrollPane, BorderLayout.CENTER);
    content.add(buttonPanel, BorderLayout.SOUTH);
    setContentPane(content);

    getRootPane().setDefaultButton(okButton);
  }


}
