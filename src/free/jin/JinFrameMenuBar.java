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

package free.jin;

import free.jin.plugin.BadChangesException;
import free.jin.plugin.Plugin;
import free.jin.plugin.PreferencesPanel;
import free.util.AWTUtilities;
import free.util.StringEncoder;
import free.util.WindowDisposingListener;
import free.util.swing.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;



/**
 * The JMenuBar on Jin's main frame.
 */

public class JinFrameMenuBar extends JMenuBar{



  /**
   * The JinFrame assosiated with this JinFrameMenuBar.
   */

  private JinFrame jinFrame;



  /**
   * The "Connection" menu.
   */

  private JMenu connectionMenu;



  /**
   * The "Preferences" menu.
   */

  private JMenu preferencesMenu;




  /**
   * Holds a list of all the plugin menus we've added.
   */

  private Vector pluginMenus = new Vector(4);



  /**
   * Holds a list of all the plugin UI menu items we've added.
   */

  private Vector pluginUIMenuItems = new Vector(4);




  /**
   * Holds a list of JMenuItems whose state (enabled/disabled) needs to be
   * flipped when a connection/disconnection occurs.
   */

  private Vector connectionSensitiveMenuItems = new Vector();





  /**
   * The ActionListener for JMenuItems representing user connections. The
   * ActionListener invokes the JinFrame.showLoginDialog(User) on the JinFrame
   * of this application when its actionPerformed(ActionEvent) method is invoked.
   */

  private ActionListener userConnectionListener = new ActionListener(){

    public void actionPerformed(ActionEvent evt){
      String path = ((UserMenuItem)evt.getSource()).getPath();
      User user = Jin.loadUser(path);
      if (user == null)
        return;

      jinFrame.showLoginDialog(user.getServer(), user);
    }

  };



  /**
   * The JSeparator starting the user connections list.
   */

  private JSeparator startUserConnSep = new JSeparator();




  /**
   * The JSeparator ending the user connections list.
   */

  private JSeparator endUserConnSep = new JSeparator();



  
  /**
   * Creates a new JinFrameMenuBar.
   * 
   * @param jinFrame The JinFrame to which this JinFrameMenuBar will be added.
   */

  public JinFrameMenuBar(JinFrame jinFrame){
    this.jinFrame = jinFrame;

    add(connectionMenu = createConnectionMenu());
    add(new LookAndFeelMenu(jinFrame.getRootPane()));
    add(preferencesMenu = createPreferencesMenu());
    add(createHelpMenu());

    setBorderPainted(true);
  }



  /**
   * Creates and returns a "Connection" menu.
   */

  public JMenu createConnectionMenu(){
    JMenu connMenu = new JMenu("Connection");
    connMenu.setMnemonic('C');

    JMenuItem newConnMenuItem = new JMenuItem("New Connection...");
    newConnMenuItem.setMnemonic('N');
    newConnMenuItem.addActionListener(new ActionListener(){

      public void actionPerformed(ActionEvent evt){
        jinFrame.showConnectionCreationUI();
      }

    });
    connectionSensitiveMenuItems.addElement(newConnMenuItem);

    JMenuItem closeConnMenuItem = new JMenuItem("Close Connection");
    closeConnMenuItem.setMnemonic('l'); 
    closeConnMenuItem.addActionListener(new ActionListener(){

      public void actionPerformed(ActionEvent evt){
        int result = JOptionPane.YES_OPTION;
        if (jinFrame.getConnection().isConnected())
          result = JOptionPane.showConfirmDialog(jinFrame, "Are you sure you want to log out and close the connection?", "Select an option", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION)
          jinFrame.closeConnection();
      }

    });
    closeConnMenuItem.setEnabled(false);
    connectionSensitiveMenuItems.addElement(closeConnMenuItem);

    JMenuItem exitMenuItem = new JMenuItem("Exit");
    exitMenuItem.setMnemonic(KeyEvent.VK_X);
    exitMenuItem.addActionListener(new ActionListener(){

      public void actionPerformed(ActionEvent evt){
        Jin.close();
      }

    });

    connMenu.add(newConnMenuItem);
    connMenu.add(closeConnMenuItem);
    connMenu.add(startUserConnSep);
    connMenu.add(endUserConnSep);
    updateConnectionMenu(connMenu);
    connMenu.add(exitMenuItem);

    return connMenu;
  } 





  /**
   * Creates the Preferences menu.
   */

  public JMenu createPreferencesMenu(){
    JMenu prefsMenu = new JMenu("Preferences");
    prefsMenu.setMnemonic('p');

    JMenuItem backgroundMenuItem = new JMenuItem("Background");
    backgroundMenuItem.setMnemonic('B');
    backgroundMenuItem.addActionListener(new ActionListener(){

      public void actionPerformed(ActionEvent evt){
        AdvancedJDesktopPane desktop = (AdvancedJDesktopPane)jinFrame.getDesktop();
        String wallpaperFilename = Jin.getProperty("desktop.wallpaper");
        File currentImageFile = wallpaperFilename == null ? null : new File(wallpaperFilename);
        BackgroundChooser bChooser = new BackgroundChooser(jinFrame, desktop, null, AdvancedJDesktopPane.TILE, currentImageFile);
        bChooser.setVisible(true);

        Color chosenColor = bChooser.getChosenColor();
        File chosenImageFile = bChooser.getChosenImageFile();
        int chosenLayoutStyle = bChooser.getChosenImageLayoutStyle();

        Jin.setProperty("desktop.background.color", chosenColor == null ? null : StringEncoder.encodeColor(chosenColor));

        Jin.setProperty("desktop.wallpaper", chosenImageFile == null ? null : chosenImageFile.getAbsolutePath());

        switch (chosenLayoutStyle){
          case AdvancedJDesktopPane.CENTER:
            Jin.setProperty("desktop.wallpaper.layout", "center");
            break;
          case AdvancedJDesktopPane.TILE:
            Jin.setProperty("desktop.wallpaper.layout", "tile");
            break;
          case AdvancedJDesktopPane.STRETCH:
            Jin.setProperty("desktop.wallpaper.layout", "stretch");
            break;
          case -1:
            Jin.setProperty("desktop.wallpaper.layout", null);
            break;
        }
      }

    });

    prefsMenu.add(backgroundMenuItem);

    return prefsMenu;
  }




  /**
   * This method is called when a connection is about to be established. It
   * performes whatever necessary modifications to the menubar, such as
   * adding certain menus.
   *
   * @param conn The <code>JinConnection</code> about to be established.
   * @param user The connecting <code>User</code>.
   * @param plugins An <code>Enumeration</code> of all the plugins for the
   * connection.
   */

  void connecting(JinConnection conn, User user, Enumeration plugins){
    while (plugins.hasMoreElements()){
      Plugin plugin = (Plugin)plugins.nextElement();

      JMenu pluginMenu = plugin.createPluginMenu();
      if (pluginMenu != null)
        addPluginMenu(pluginMenu);

      if (plugin.hasPreferencesUI())
        addPluginPreferenceUIMenuItem(plugin);
    }

    int size = connectionSensitiveMenuItems.size();
    for (int i = 0; i < size; i++){
      JMenuItem menuItem = (JMenuItem)connectionSensitiveMenuItems.elementAt(i);
      menuItem.setEnabled(!menuItem.isEnabled());
    }
    
    // Bugfix
    invalidate();
    validate();
  }





  /**
   * This method is called when the connection has been broken. It should return
   * the menubar to the state it was in before
   * <code>connecting(JinConnection, Enumeration)</code> was called.
   */

  void disconnected(JinConnection conn, User user){
    String userPath = Jin.getSettingsPath(user);
    if ((userPath != null) && !user.isGuest()){
      // Update the recent user properties
      int recentUsersCount = Integer.parseInt(Jin.getProperty("recent.users.count", "0"));
      int existingUserIndex = recentUsersCount + 2;
      for (int i = 1; i <= recentUsersCount; i++){
        String path = Jin.getProperty("recent.users."+i+".path");
        if (path.equals(userPath)){
          existingUserIndex = i;
          break;
        }
      }

      for (int i = existingUserIndex; i > 1; i--){
        String description = Jin.getProperty("recent.users."+(i-1)+".description");
        String path = Jin.getProperty("recent.users."+(i-1)+".path");
        Jin.setProperty("recent.users."+i+".description", description);
        Jin.setProperty("recent.users."+i+".path", path);
      }
      Jin.setProperty("recent.users.1.description", descriptionForUser(user));
      Jin.setProperty("recent.users.1.path", userPath);

      if (existingUserIndex == recentUsersCount + 2)
        Jin.setProperty("recent.users.count", String.valueOf(recentUsersCount + 1));

      updateConnectionMenu(connectionMenu);
    }


    removePluginMenus();
    removePluginPreferenceUIMenuItems();

    int size = connectionSensitiveMenuItems.size();
    for (int i = 0; i < size; i++){
      JMenuItem menuItem = (JMenuItem)connectionSensitiveMenuItems.elementAt(i);
      menuItem.setEnabled(!menuItem.isEnabled());
    }
  }




  /**
   * Returns the string that will be used as a description of a
   * <code>User</code> object for the user.
   */

  private static String descriptionForUser(User user){
    return user.getUsername()+" on "+user.getServer().getName();
  } 






  /**
   * Adds a menu item which shows the given plugin's preferences UI to the
   * "Preferences" menu.
   */

  private void addPluginPreferenceUIMenuItem(Plugin plugin){
    if (!plugin.hasPreferencesUI())
      return;

    if (pluginUIMenuItems.size() == 0)
      preferencesMenu.addSeparator(); 

    JMenuItem menuItem = new JMenuItem(plugin.getName());
    menuItem.addActionListener(new ShowPreferencesUI(plugin));
    preferencesMenu.add(menuItem);

    pluginUIMenuItems.addElement(menuItem);
  }




  /**
   * An ActionListener implementation which opens a dialog with the given
   * Plugin's preferences UI.
   */

  private class ShowPreferencesUI implements ActionListener{

    private final Plugin targetPlugin;

    public ShowPreferencesUI(Plugin plugin){
      this.targetPlugin = plugin; 
    }

    public void actionPerformed(ActionEvent evt){
      final JDialog dialog = new JDialog(jinFrame, targetPlugin.getName()+" preferences", true);
      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

      SwingUtils.registerEscapeCloser(dialog);

      dialog.getContentPane().setLayout(new BorderLayout());

      final PreferencesPanel prefPanel = targetPlugin.getPreferencesUI();
      JPanel prefWrapperPanel = new JPanel(new BorderLayout());
      prefWrapperPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
      prefWrapperPanel.add(prefPanel, BorderLayout.CENTER);
      dialog.getContentPane().add(prefWrapperPanel, BorderLayout.CENTER);

      JPanel bottomPanel = new JPanel(new BorderLayout());
      bottomPanel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.CENTER);
      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

      JButton okButton = new JButton("OK");
      JButton cancelButton = new JButton("Cancel");
      final JButton applyButton = new JButton("Apply");
      applyButton.setEnabled(false);

      applyButton.setMnemonic('A');
      prefPanel.addChangeListener(new ChangeListener(){
        public void stateChanged(ChangeEvent evt){
          applyButton.setEnabled(true);
        }
      });

      buttonPanel.add(okButton);
      buttonPanel.add(cancelButton);
      buttonPanel.add(applyButton);
      bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

      okButton.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent evt){
          try{
            if (applyButton.isEnabled())
              prefPanel.applyChanges();
            dialog.dispose();
          } catch (BadChangesException e){
              JOptionPane.showMessageDialog(dialog, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
              if (e.getErrorComponent() != null)
                e.getErrorComponent().requestFocus();
            }
        }
      });

      cancelButton.addActionListener(new WindowDisposingListener(dialog));

      applyButton.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent evt){
          try{
            prefPanel.applyChanges();
            applyButton.setEnabled(false);
          } catch (BadChangesException e){
              JOptionPane.showMessageDialog(dialog, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
              if (e.getErrorComponent() != null)
                e.getErrorComponent().requestFocus();
            }
        }
      });

      dialog.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
      dialog.getRootPane().setDefaultButton(okButton);

      AWTUtilities.centerWindow(dialog, jinFrame);

      dialog.show();
    }

  }





  /**
   * Removes the menus added via the 
   * <code>addPluginPreferenceUIMenuItem(Plugin)</code> method.
   */

  private void removePluginPreferenceUIMenuItems(){
    boolean hadPreferenceMenus = !pluginUIMenuItems.isEmpty();
    int index = pluginUIMenuItems.size()-1;
    while (index >= 0){
      JMenuItem menuItem = (JMenuItem)pluginUIMenuItems.elementAt(index);
      pluginUIMenuItems.removeElementAt(index);
      preferencesMenu.remove(menuItem);
      index--;
    }

    if (hadPreferenceMenus)
      preferencesMenu.remove(preferencesMenu.getMenuComponentCount()-1); // Separator
  }




  /**
   * Adds the given JMenu as a plugin's menu.
   */

  private void addPluginMenu(JMenu menu){
    add(menu, 3);
    pluginMenus.addElement(menu);
  }




  /**
   * Removes all the menus added via the <code>addPluginMenu</code> method.
   */

  private void removePluginMenus(){
    int index = pluginMenus.size() - 1;
    while (index >= 0){
      JMenu menu = (JMenu)pluginMenus.elementAt(index);
      pluginMenus.removeElementAt(index);
      remove(menu);
      index--;
    }

    invalidate();
    validate();
    repaint();
  }

  



  /**
   * Synchronizes the currently known Users (obtained by invoking Jin.getUsers())
   * with the given JMenu. After this method returns, the given JMenu contains
   * only UserMenuItems representing the known users between the startUserConn
   * JSeparator and endUserConn JSeparator.
   */

  private void updateConnectionMenu(JMenu connMenu){
    int startSeparatorIndex = -1;
    int menuItemCount = connMenu.getMenuComponentCount();
    for (int i = 0 ; i < menuItemCount; i++){
      if (connMenu.getMenuComponent(i) == startUserConnSep){
        startSeparatorIndex = i;
        break;
      }
    }
    if (startSeparatorIndex==-1)
      throw new IllegalStateException("JSeparator starting the user connections list not found.");


    while (connMenu.getMenuComponent(startSeparatorIndex+1)!=endUserConnSep){
      JMenuItem menuItem = connMenu.getItem(startSeparatorIndex+1);
      connMenu.remove(startSeparatorIndex+1);
      connectionSensitiveMenuItems.removeElement(menuItem);
    }


    boolean areMenuItemsEnabled = (jinFrame.getConnection() == null);
    
    int recentUsersCount = Integer.parseInt(Jin.getProperty("recent.users.count", "0"));
    for (int i = recentUsersCount ; i >= 1; i--){
      String description = Jin.getProperty("recent.users."+i+".description");
      String path = Jin.getProperty("recent.users."+i+".path");
      String label = i+" "+description;
      JMenuItem menuItem = new UserMenuItem(label, path);
      menuItem.addActionListener(userConnectionListener);
      if (i <= 9)
        menuItem.setMnemonic(Character.forDigit(i, 10));
      menuItem.setEnabled(areMenuItemsEnabled);
      connMenu.insert(menuItem, startSeparatorIndex + 1);
      connectionSensitiveMenuItems.addElement(menuItem);
    }
  }




  /**
   * Creates and returns the "Help" menu.
   */

  protected JMenu createHelpMenu(){
    JMenu helpMenu = new JMenu("Help");
    helpMenu.setMnemonic('H');

    JMenuItem websiteMenuItem = new JMenuItem("Jin Website", 'J');
    websiteMenuItem.addActionListener(new UrlDisplayingAction(jinFrame, "http://www.jinchess.com"));
    helpMenu.add(websiteMenuItem);

    JMenuItem licenseMenuItem = new JMenuItem("Credits and Copyrights...", 'C');
    licenseMenuItem.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        JDialog licenseDialog = new LicenseDialog(jinFrame);
        AWTUtilities.centerWindow(licenseDialog, jinFrame);
        licenseDialog.setResizable(false);
        licenseDialog.setVisible(true);
      }
    });
    helpMenu.add(licenseMenuItem);

    JMenuItem reportBugMenuItem = new JMenuItem("Report a Bug", 'R');
    reportBugMenuItem.addActionListener(new UrlDisplayingAction(jinFrame,
      "https://sourceforge.net/tracker/?group_id=50386&atid=459537"));
    helpMenu.add(reportBugMenuItem);

    JMenuItem suggestFeatureMenuItem = new JMenuItem("Suggest a Feature", 'S');
    suggestFeatureMenuItem.addActionListener(new UrlDisplayingAction(jinFrame,
      "https://sourceforge.net/tracker/?group_id=50386&atid=459540"));
    helpMenu.add(suggestFeatureMenuItem);


    JMenuItem aboutMenuItem = new JMenuItem("About Jin...", 'A');
    aboutMenuItem.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        JDialog aboutDialog = new AboutDialog(jinFrame);
        AWTUtilities.centerWindow(aboutDialog, jinFrame);
        aboutDialog.setVisible(true);
      }
    });
    helpMenu.add(aboutMenuItem);

    return helpMenu;
  }




  /**
   * A JMenuItem representing an account on the recently user accounts list.
   */

  private class UserMenuItem extends JMenuItem{


    /**
     * The path of the settings file of the User.
     */

    private String path;



    /**
     * Creates a new UserMenuItem which represents the given User.
     */

    public UserMenuItem(String label, String path){
      super(label);
      this.path = path;
    }



    /**
     * Returns the path of the settings file of the user.
     */

    public String getPath(){
      return path;
    }


  }

}
