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

import free.workarounds.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import free.util.swing.IntegerStrictPlainDocument;
import free.util.swing.SwingUtils;
import free.util.TextUtilities;
import free.util.AWTUtilities;
import free.util.WindowDisposingListener;



/**
 * A partial, basic implementation of a LoginDialog.
 */

public abstract class StandardLoginDialog implements LoginDialog{


  /**
   * The <code>Server</code> we're trying to login to.
   */

  protected final Server server;




  /**
   * The title of the dialog.
   */

  private final String title;



 /**
   * The "hint" user that was given to us.
   */

  protected final User hintUser;



  /**
   * The <code>User</code> we're going to return. Also used as a flag to
   * indicate whether the user cancelled the dialog.
   */

  private User resultUser = null;




  /**
   * The JTextField containing the user's username.
   */

  private final JTextField usernameField;




  /**
   * The JPasswordField containing the user's password.
   */

  private final JPasswordField passwordField;



  /**
   * The JComboBox containing the hostname of the server to connect to.
   */

  private final JComboBox hostnameBox;



  /**
   * The JTextField containing the port to connect on.
   */

  private final JTextField portField;



  /**
   * The JCheckBox saying whether to save the user's password.
   */

  private final JCheckBox savePWCheckBox;



  /**
   * The JDialog we show the user.
   */

  protected JDialog dialog = null;




  /**
   * The real constructor.
   */

  private StandardLoginDialog(String title, Server server, User hintUser){
    if (server == null)
      throw new IllegalArgumentException("Server may not be null");

    this.title = title;
    this.hintUser = hintUser;
    this.server = server;

    String username = getProperty("login.username", "");
    String password = getProperty("login.password", "");
    String [] hostnames = TextUtilities.getTokens(getProperty("login.hosts", null), ";");
    String hostname = getProperty("login.hostname", hostnames[0]);
    String port = getProperty("login.port", "");
    boolean savePassword = "true".equalsIgnoreCase(getProperty("login.savepassword", "false"));

    usernameField = new FixedJTextField(username);
    passwordField = new FixedJPasswordField(password);
    hostnameBox = new FixedJComboBox(hostnames);
    hostnameBox.setSelectedItem(hostname);
    portField = new FixedJTextField(new IntegerStrictPlainDocument(0, 65535, 10), port, 5);
    savePWCheckBox = new JCheckBox("Save password", savePassword);
  }



  /**
   * Creates a new <code>StandardLoginDialog</code> with the specified title and
   * <code>User</code> whose properties will be used as default values for the
   * dialog fields.
   */

  public StandardLoginDialog(String title, User hintUser){
    this(title, hintUser.getServer(), hintUser);
  }




  /**
   * Creates a new <code>StandardLoginDialog</code> with the specified title and
   * <code>Server</code> whose properties will be used as default values for the
   * dialog fields.
   */

  public StandardLoginDialog(String title, Server server){
    this(title, server, null);
  }




  /**
   * Returns the property with the specified name. If the hint user is not
   * <code>null</code>, its property with the specified name is returned,
   * otherwise, if not null, the server's property, otherwise, the specified
   * default value.
   */

  protected String getProperty(String propertyName, String defaultValue){
    String val = hintUser == null ? 
      server.getProperty(propertyName) : hintUser.getProperty(propertyName);
    return val == null ? defaultValue : val;
  }




  /**
   * Shows the user the login dialog and waits for the user to either ok or
   * cancel.
   */

  public void show(Component parent){
    if (dialog != null)
      throw new IllegalStateException("Dialog already shown");

    dialog = new JDialog(AWTUtilities.frameForComponent(parent), title ,true){

      private boolean painted = false;
    
      // Hack to set the focus where we want it.
      public void paint(Graphics g){ 
                                     
        if (!painted){
          painted = true;
          if (usernameField.getText().length() == 0)
            usernameField.requestFocus();
          else if (passwordField.getPassword().length == 0)
            passwordField.requestFocus();
          else{
            JButton defaultButton = getRootPane().getDefaultButton();
            if (defaultButton != null)
              defaultButton.requestFocus();
          }
        }
        super.paint(g);
      }
    
    };

    addComponents(dialog, usernameField, passwordField, hostnameBox, portField, savePWCheckBox);

    AWTUtilities.centerWindow(dialog, parent);
    dialog.setVisible(true);
  }




  /**
   * Adds all the components to the dialog, in the appropriate layout.
   */
                             
  protected void addComponents(JDialog dialog, JTextField usernameField,
    JPasswordField passwordField, JComboBox hostnameBox, JTextField portField,
    JCheckBox savePWCheckBox){

    dialog.setResizable(true); // Setting it to false causes problems on KDE.
    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    JComponent content = new JPanel(new BorderLayout());
    dialog.setContentPane(content);

    SwingUtils.registerEscapeCloser(dialog);

    Component guestPanel = createGuestsPanel(dialog);
    Component advancedOptionsPanel = createAdvancedOptionsPanel(dialog, hostnameBox, portField);
    Component membersPanel = 
      createMembersPanel(dialog, usernameField, passwordField, savePWCheckBox);
    Component cancelButtonPanel = createCancelButtonPanel(dialog);

    Box upperPanel = new Box(BoxLayout.X_AXIS);
    Box lowerPanel = new Box(BoxLayout.X_AXIS);

    upperPanel.add(guestPanel);
    upperPanel.add(Box.createHorizontalStrut(10));
    upperPanel.add(advancedOptionsPanel);

    lowerPanel.add(membersPanel);
    lowerPanel.add(Box.createHorizontalStrut(10));
    lowerPanel.add(cancelButtonPanel);

    Box vpanel = new Box(BoxLayout.Y_AXIS);
    vpanel.add(Box.createVerticalStrut(10));
    vpanel.add(upperPanel);
    vpanel.add(Box.createVerticalStrut(10));
    vpanel.add(lowerPanel);
    vpanel.add(Box.createVerticalStrut(10));

    Box hpanel = new Box(BoxLayout.X_AXIS);
    hpanel.add(Box.createHorizontalStrut(10));
    hpanel.add(vpanel);
    hpanel.add(Box.createHorizontalStrut(10));

    content.add(hpanel, BorderLayout.CENTER);
  }




  /**
   * Creates and returns the "connect as guest" button. Should return null if
   * there is no such option on the server. The default implementation returns
   * a simple JButton with the label "Connect as guest".
   */

  protected JButton createConnectAsGuestButton(){
    JButton button = new JButton("Login as Guest");
    button.setMnemonic('G');
    return button;
  }




  /**
   * This method is called when the "connect as guest" button is pressed or an
   * equivalent action is taken by the user. The default implementation sets the
   * username field to "guest", clears the password field, creates a guest
   * <code>User</code> and calls <code>proceed()</code>.
   */

  protected void connectAsGuestActionPerformed(){
    String hostname = (String)hostnameBox.getSelectedItem();
    String port = portField.getText();

    String inputIllegalityReason = findInputIllegalityReason("", "", hostname, port, false);
    if (inputIllegalityReason != null){
      JOptionPane.showMessageDialog(dialog, inputIllegalityReason, "Wrong Connection Settings", JOptionPane.ERROR_MESSAGE);
      return;
    }

    resultUser = server.getGuest();

    resultUser.setProperty("login.hostname", hostname);
    resultUser.setProperty("login.port", port);

    dialog.dispose();
  }




  /**
   * Creates and returns a new "register" button. This button should take the
   * user to the registration page. This method may not return null. The
   * current implementation returns a simple JButton with the label "Register".
   */

  protected JButton createRegisterButton(){
    JButton button = new JButton("Register");
    button.setMnemonic('R');
    return button;
  }

 

  /**
   * This method is called when the "register" button is pressed or an
   * equivalent action is taken by the user.
   */

  protected abstract void registerActionPerformed();



  

  /**
   * Creates the "Guests" panel, the panel with options for guests.
   *
   * @param parentDialog The dialog to which the panel will be added.
   */

  public Component createGuestsPanel(JDialog parentDialog){
    Box vpanel = new Box(BoxLayout.Y_AXIS);

    vpanel.add(Box.createVerticalStrut(10));

    JButton connectAsGuestButton = createConnectAsGuestButton();
    if (connectAsGuestButton != null){
      connectAsGuestButton.setDefaultCapable(false);
      connectAsGuestButton.addActionListener(new ActionListener(){

        public void actionPerformed(ActionEvent evt){
          connectAsGuestActionPerformed();
        }

      });
      vpanel.add(connectAsGuestButton);

      vpanel.add(Box.createVerticalStrut(10));
    }

    JButton registerButton = createRegisterButton();
    registerButton.setDefaultCapable(false);
    registerButton.addActionListener(new ActionListener(){
      
      public void actionPerformed(ActionEvent evt){
        registerActionPerformed();
      }

    });
    vpanel.add(registerButton);

    vpanel.add(Box.createVerticalStrut(10));

    Box hpanel = new Box(BoxLayout.X_AXIS);
    hpanel.add(Box.createHorizontalStrut(10));
    hpanel.add(vpanel);
    hpanel.add(Box.createHorizontalStrut(10));

    JPanel outerPanel = new JPanel(new BorderLayout());
    outerPanel.add(hpanel,BorderLayout.CENTER);
    outerPanel.setBorder(new TitledBorder(" Guests "));

    return outerPanel;
  }

  



  /**
   * Creates the "Advanced Options" panel.
   *
   * @param parentDialog The dialog to which the panel will be added.
   */

  public Component createAdvancedOptionsPanel(JDialog parentDialog, JComboBox hostnameBox,
    JTextField passwordField){

    JPanel hostnameLabelPanel = new JPanel(new GridLayout(3,1));
    hostnameLabelPanel.add(new JLabel("Server"));
    hostnameLabelPanel.add(new JLabel("Hostname or"));
    JLabel hostnameLabel = new JLabel("IP Number");
    hostnameLabel.setDisplayedMnemonic('I');
    hostnameLabelPanel.add(hostnameLabel);

    Box hostnamePanel = new Box(BoxLayout.X_AXIS);

    hostnameBox.setFont(UIManager.getFont("TextField.font"));
    hostnameBox.setEditable(true);
    hostnameLabel.setLabelFor(hostnameBox);

    hostnamePanel.add(hostnameBox);
    hostnamePanel.add(Box.createHorizontalStrut(10));
    hostnamePanel.add(hostnameLabelPanel);

    
    Box portPanel = new Box(BoxLayout.X_AXIS);

    portField.setMaximumSize(portField.getPreferredSize());

    portPanel.add(portField);
    portPanel.add(Box.createHorizontalStrut(10));
    JLabel portLabel = new JLabel("Port Number");
    portLabel.setDisplayedMnemonic('n');
    portLabel.setLabelFor(portField);
    portPanel.add(portLabel);
    portPanel.add(Box.createHorizontalGlue());
   
    
    Box vpanel = new Box(BoxLayout.Y_AXIS);
    vpanel.add(hostnamePanel);
    vpanel.add(Box.createVerticalStrut(10));
    vpanel.add(portPanel);
    vpanel.add(Box.createVerticalStrut(10));

    Box hpanel = new Box(BoxLayout.X_AXIS);
    hpanel.add(Box.createHorizontalStrut(10));
    hpanel.add(vpanel);
    hpanel.add(Box.createHorizontalStrut(10));

    JPanel outerPanel = new JPanel(new BorderLayout());
    outerPanel.setBorder(new TitledBorder(" Advanced Options "));
    outerPanel.add(hpanel, BorderLayout.CENTER);

    return outerPanel;
  }




  /**
   * Creates and returns a button labeled "Forgot your password?". This method
   * should return null if there is no password retrieving functionality on the
   * server. The default implementation returns a simple JButton with the label
   * "Forgot your password?".
   */

  protected JButton createForgotPasswordButton(){
    JButton button = new JButton("Retrieve Password");
    button.setMnemonic('t');
    return button;
  }




  /**
   * This method is called when the button allowing the user to retrieve his
   * password is pressed or an equivalent action is taken by the user.
   */

  protected abstract void forgotPasswordActionPerformed();





  /**
   * Creates and returns a "Connect" button. The default implementation returns
   * a simple JButton with the label "Connect". This method may not return null.
   */

  protected JButton createConnectButton(){
    JButton button = new JButton("Connect");
    return button;
  }




  /**
   * This method is called when the connect button is pressed or an equivalent
   * action is taken by the user.
   */

  protected void connectActionPerformed(){
    String username = usernameField.getText();
    String password = new String(passwordField.getPassword());
    String hostname = (String)hostnameBox.getSelectedItem();
    String port = portField.getText();
    boolean savePassword = savePWCheckBox.isSelected();

    String inputIllegalityReason = 
      findInputIllegalityReason(username, password, hostname, port, true);
    if (inputIllegalityReason != null){
      JOptionPane.showMessageDialog(dialog, inputIllegalityReason,
        "Wrong Connection Settings", JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (username.equalsIgnoreCase(getProperty("login.username", null))) // Reuse the hint user
      resultUser = hintUser;
    else
      resultUser = server.createUser(username);

    resultUser.setProperty("login.username", username);
    resultUser.setProperty("login.hostname", hostname);
    resultUser.setProperty("login.port", port);
    resultUser.setProperty("login.savepassword", String.valueOf(savePassword));
    resultUser.setProperty("login.password", savePassword ? password : "");

    dialog.dispose();
  }





  /**
   * Creates the "Members" panel, the panel with options for members.
   *
   * @param parentDialog The dialog to which the panel will be added.
   */

  public Component createMembersPanel(JDialog parentDialog, JTextField usernameField,
    JPasswordField passwordField, JCheckBox savePWCheckBox){

    Box usernamePanel = new Box(BoxLayout.X_AXIS);

    usernameField.setPreferredSize(new Dimension(130, 20));
    usernamePanel.add(usernameField);
    usernamePanel.add(Box.createHorizontalStrut(10));
    JLabel handleLabel = new JLabel("Handle (your login name)");
    handleLabel.setDisplayedMnemonic('H');
    handleLabel.setLabelFor(usernameField);
    usernamePanel.add(handleLabel);
    usernamePanel.add(Box.createHorizontalGlue());
    usernamePanel.add(Box.createHorizontalStrut(10));

    
    Box passwordInputPanel = new Box(BoxLayout.X_AXIS);

    passwordField.setPreferredSize(new Dimension(130, 20));
    passwordInputPanel.add(passwordField);
    passwordInputPanel.add(Box.createHorizontalStrut(10));
    JLabel passwordLabel = new JLabel("Password");
    passwordLabel.setDisplayedMnemonic('P');
    passwordLabel.setLabelFor(passwordField);
    passwordInputPanel.add(passwordLabel);
    passwordInputPanel.add(Box.createHorizontalGlue());
    passwordInputPanel.add(Box.createHorizontalStrut(10));

    int fieldsWidth = Math.max(usernameField.getPreferredSize().width, passwordField.getPreferredSize().width);
    usernameField.setMaximumSize(new Dimension(fieldsWidth, usernameField.getPreferredSize().height));
    passwordField.setMaximumSize(new Dimension(fieldsWidth, passwordField.getPreferredSize().height));
    usernameField.setPreferredSize(new Dimension(fieldsWidth, usernameField.getPreferredSize().height));
    passwordField.setPreferredSize(new Dimension(fieldsWidth, passwordField.getPreferredSize().height));

    usernameField.setColumns(0); // Otherwise setPreferredSize is ignored and it will still
    passwordField.setColumns(0); // use the amount of columns to calculate preferred size.

    Box passwordOptionsPanel = new Box(BoxLayout.X_AXIS);

    savePWCheckBox.setMnemonic('S');

    JButton forgotPasswordButton = createForgotPasswordButton();
    if (forgotPasswordButton != null){
      forgotPasswordButton.setDefaultCapable(false);
      forgotPasswordButton.addActionListener(new ActionListener(){

        public void actionPerformed(ActionEvent evt){
          forgotPasswordActionPerformed();
        }

      });
    }

    passwordOptionsPanel.add(savePWCheckBox);
    if (forgotPasswordButton != null){
      passwordOptionsPanel.add(Box.createHorizontalStrut(10));
      passwordOptionsPanel.add(forgotPasswordButton);
    }
    passwordOptionsPanel.add(Box.createHorizontalGlue());


    Box decisionPanel = new Box(BoxLayout.X_AXIS);
    
    final JButton connectButton = createConnectButton();
    connectButton.setDefaultCapable(true);
    connectButton.addActionListener(new ActionListener(){
      
      public void actionPerformed(ActionEvent evt){
        connectActionPerformed();
      }

    });

    dialog.getRootPane().setDefaultButton(connectButton);

    decisionPanel.add(connectButton);
    decisionPanel.add(Box.createHorizontalGlue());

    Box vpanel = new Box(BoxLayout.Y_AXIS);
    vpanel.add(Box.createVerticalStrut(10));
    vpanel.add(usernamePanel);
    vpanel.add(Box.createVerticalStrut(10));
    vpanel.add(passwordInputPanel);
    vpanel.add(Box.createVerticalStrut(10));
    vpanel.add(passwordOptionsPanel);
    vpanel.add(Box.createVerticalStrut(10));
    vpanel.add(decisionPanel);
    vpanel.add(Box.createVerticalStrut(10));

    Box hpanel = new Box(BoxLayout.X_AXIS);
    hpanel.add(Box.createHorizontalStrut(10));
    hpanel.add(vpanel);
    hpanel.add(Box.createHorizontalGlue());


    JPanel outerPanel = new JPanel(new BorderLayout());
    outerPanel.setBorder(new TitledBorder(" Members "));
    outerPanel.add(hpanel,BorderLayout.CENTER);

    return outerPanel;
  }




  /**
   * Creates the panel with the "Cancel" button - the button that cancels the
   * connection.
   *
   * @param parentDialog The dialog to which the panel will be added.
   */

  public Component createCancelButtonPanel(JDialog parentDialog){
    Box panel = new Box(BoxLayout.Y_AXIS);

    panel.add(Box.createVerticalGlue());

    JButton button = new JButton("Cancel");
    button.setDefaultCapable(false);
    button.addActionListener(new WindowDisposingListener(dialog));

    panel.add(button);

    return panel;
  }



  /**
   * Determines whether the currently specified user details are valid. If so,
   * returns <code>null</code>. Otherwise returns a string specifying what's
   * invalid about them. The <code>checkUsernameAndPassword</code> argument
   * specifies whether the username and password fields should be checked.
   * The default implementation checks that the port value is a valid port.
   */

  public String findInputIllegalityReason(String username, String password, String hostname,
      String port, boolean checkUsernameAndPassword){

    try{
      Integer.parseInt(port);
    } catch (NumberFormatException e){
        return "Bad port value: "+port;
      }

    return null;
  }



  /**
   * This method is meant to be overriden by subclasses to actually create the
   * connection based on the specified values.
   */

  protected abstract JinConnection createConnectionImpl(String hostname, int port,
    String username, String password);





  /**
   * Returns true if the user canceled the connection, false otherwise.
   */

  public boolean isCanceled(){
    return resultUser == null;
  }




  /**
   * Returns the User object filled with the information supplied by the user.
   */

  public User getUser(){
    return resultUser;
  }



  /**
   * Returns a JinConnection based on the user's choices.
   */

  public JinConnection createConnection(){
    String hostname = resultUser.getProperty("login.hostname");
    int port = Integer.parseInt(resultUser.getProperty("login.port"));
    String username = resultUser.getProperty("login.username");
    String password = resultUser.isGuest() ? "" : new String(passwordField.getPassword());

    return createConnectionImpl(hostname, port, username, password);
  }


}
