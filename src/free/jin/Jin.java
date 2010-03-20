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

import free.util.IOUtilities;
import free.util.MemoryFile;
import free.util.StringEncoder;
import free.util.StringParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;


/**
 * Jin's main class.
 */

public class Jin{



  /**
   * Causes the <code>free.workarounds.SwingFix</code> class to be loaded.
   */

  static{
    try{
      Class.forName("free.workarounds.SwingFix");
    } catch (ClassNotFoundException e){
        e.printStackTrace();
      }
  }



  /**
   * Compatibility version for the user properties and server user settings
   * files. Whenever the user.properties file contains a version different than 
   * this, the user.properties and server user settings files will be
   * deleted/ignored.
   */

  private static String propsVersion = "5";
  


  /**
   * Jin's main frame.
   */

  private static JinFrame mainFrame;


  
  /**
   * Jin's properties.
   */

  private static Properties jinProps = new Properties();




  /**
   * The user's properties. The 'user' here isn't the chess server account/user,
   * but the operating system user.
   */

  private static Properties userProps = new Properties();




  /**
   * The folder where all jin settings files should be written to and read from.
   * Jin and all plugins should only write to that directory.
   */

  private static File jinUserHome = new File(System.getProperty("user.home"), ".jin");




  /**
   * The folder where the settings of Users are kept.
   */

  private static File usersDir = new File(jinUserHome, "users");


  

  /**
   * Maps server names to supported servers.
   */

  private static Hashtable servers = new Hashtable();



  /**
   * Maps <code>User</code> object to directories from which they were loaded by
   * the <code>loadUser(String)</code> method or saved into by the
   * <code>saveUser(User)</code> method.
   */

  private static Hashtable userDirs = new Hashtable();




  /**
   * Creates the jin settings directory.
   */

  static{
    if (!jinUserHome.exists()){
      if (!jinUserHome.mkdirs()){
        System.err.println("Unable to create directory "+jinUserHome.getAbsolutePath());
        System.exit(1);
      }
    }
  }




  /**
   * Loads the Jin and user properties.
   */

  static{
    try{
      InputStream propsIn = Jin.class.getResourceAsStream("resources/jin.properties");
      jinProps.load(propsIn);
      propsIn.close();
    } catch (IOException e){
        System.err.println("Unable to load Jin's properties:");
        e.printStackTrace();
        System.exit(1);
      }

    try{
      File propsFile = new File(jinUserHome, "user.properties");
      if (propsFile.canRead()){
        InputStream propsIn = new FileInputStream(propsFile);
        userProps.load(propsIn);
        propsIn.close();
      }

      String savedPropsVersion = userProps.getProperty("props.version");
      if ((savedPropsVersion == null) || !savedPropsVersion.equals(propsVersion)){
        if (propsFile.exists())
          propsFile.delete();
        if (usersDir.isDirectory())
          IOUtilities.rmdir(usersDir);
        userProps.clear();
        userProps.put("props.version", propsVersion);
      }
    } catch (IOException e){
        e.printStackTrace();
      }
  }




  /**
   * Loads the supported servers.
   */

  static{
    try{
      InputStream serverListInputStream = Jin.class.getResourceAsStream("resources/servers/list.txt");
      ByteArrayOutputStream buf = new ByteArrayOutputStream();
      IOUtilities.pump(serverListInputStream, buf);
      StringTokenizer serversTokenizer = new StringTokenizer(new String(buf.toByteArray(), "8859_1"), "\n\r");

      while (serversTokenizer.hasMoreTokens()){
        String serverFileName = serversTokenizer.nextToken();
        Server server = Server.load(Jin.class.getResourceAsStream("resources/servers/"+serverFileName));
        servers.put(server.getID(), server);
      }
    } catch (IOException e){
        System.err.println("Unable to load the server list:");
        e.printStackTrace();
        System.exit(0);
      }
      catch (RuntimeException e){
        e.printStackTrace();
      }
  }





  /**
   * Creates the users directories.
   */

  static{
    if (!usersDir.exists()){
      if (!usersDir.mkdirs()){
        System.err.println("Unable to create directory " + usersDir);
        System.exit(1);
      }

      Server [] servers = getServers();
      for (int i = 0; i < servers.length; i++){
        Server server = servers[i];
        String dirName = server.getID();
        File dir = new File(usersDir, dirName);
        if (!dir.mkdirs()){
          System.err.println("Unable to create directory " + dir);
          System.exit(1);
        }
      }
    }
  }




  /**
   * Returns Jin's main frame.
   */

  public static JinFrame getMainFrame(){
    return mainFrame;
  }

  



  /**
   * The OS user's property with the given name. If no such
   * property exists, Jin's property with the given name, is returned.
   */

  public static String getProperty(String propertyName){
    String val = userProps.getProperty(propertyName);

    if (val == null)
      return jinProps.getProperty(propertyName);
    else
      return val;
  }




  /**
   * Same as <code>Jin.getProperty(String)</code> but if neither a user
   * property nor the Jin property with the given name is found, returns
   * the specified default value.
   */

  public static String getProperty(String propertyName, String defaultValue){
    String propertyValue = getProperty(propertyName);
    return propertyValue == null ? defaultValue : propertyValue;
  }




  /**
   * Sets the given user property to have the given value. If the given value is
   * <code>null</code>, the property is removed.
   */

  public static void setProperty(String propertyName, String propertyValue){
    if (propertyValue == null)
      userProps.remove(propertyName);
    else
      userProps.put(propertyName, propertyValue);
  }




  /**
   * Returns the path where the settings for the specified User are kept.
   * Returns <code>null</code> if the specified <code>User</code> is new and his
   * settings weren't saved yet.
   */

  public static String getSettingsPath(User user){
    File file = (File)userDirs.get(user);
    return file == null ? null : file.getAbsolutePath();
    // Not 100% sure we should use the absolute path...
  }




  /**
   * Loads a <code>User</code> from the specified path. The path is not
   * guaranteed to be local - it may be a URL to a remote location. Returns
   * <code>null</code> if unable to load from the specified path.
   */

  public static User loadUser(String path){
    if (path == null)
      throw new IllegalArgumentException("Path may not be null");

    File userDir = new File(path);
    try{
      if (!userDir.exists())
        throw new FileNotFoundException(userDir.toString());
      if (!userDir.isDirectory())
        throw new IOException("Path must be a directory");

      File parent = new File(userDir.getParent());
      String serverID = parent.getName();

      Server server = getServer(serverID);
      if (server == null){
        JOptionPane.showMessageDialog(mainFrame, "Unable to load user file from:\n"+userDir+
         "\nBecause "+serverID+" is not a known server", "Error", JOptionPane.ERROR_MESSAGE);
        return null;
      }

      File settingsFile = new File(userDir, "settings");
      InputStream propsIn = new BufferedInputStream(new FileInputStream(settingsFile));
      Properties props = new Properties();
      props.load(propsIn);
      propsIn.close();

      Hashtable userFiles = new Hashtable();
      File userFilesFile = new File(userDir, "files"); 
      if (userFilesFile.exists()){
        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(userFilesFile)));
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int filesCount = in.readInt();
        for (int i = 0; i < filesCount; i++){
          String filename = in.readUTF();
          int filesize = in.readInt();
          if (IOUtilities.pump(in, buf, filesize) != filesize)
            throw new EOFException("EOF while reading user-file: "+filename);
          byte [] data = buf.toByteArray();
          buf.reset();
          MemoryFile memFile = new MemoryFile(data);
          userFiles.put(filename, memFile);
        }
        in.close();
      }

      User user = server.createUser(props, userFiles);

      userDirs.put(user, userDir);

      return user;
    } catch (IOException e){
        e.printStackTrace();
        JOptionPane.showMessageDialog(mainFrame, "Unable to load user file from:\n"+userDir, "Error", JOptionPane.ERROR_MESSAGE);
        return null;
      }
  }




  /**
   * Loads and returns the guest <code>User</code> for the specified
   * <code>Server</code> or returns <code>null</code> if the guest account
   * hasn't been created yet.
   */

  static User loadGuest(Server server){
    String guestPath = Jin.getProperty(server.getID()+".guestAccountPath");
    return guestPath == null ? null : loadUser(guestPath);
  }




  /**
   * Saves the given User. If this is yet an unknown User and the user doesn't
   * abort the save, it is added to the list of known users. If the process
   * fails for some reason, an appropriate message is displayed to the user, so
   * the caller needn't worry about that. The returned String is the path into
   * which the <code>User</code> was saved, or <code>null</code> if the saving
   * process failed.
   */

  public static String saveUser(User user){
    File userDir = (File)userDirs.get(user);

    if (user.isGuest()){
      System.out.println("Querying user about saving guest settings");
      int result = JOptionPane.showConfirmDialog(getMainFrame(), 
        "Would you like to save the guest preferences?\n(they may be shared with anyone else using this computer)", "Save preferences?", JOptionPane.YES_NO_OPTION);
      if (result == JOptionPane.YES_OPTION){
        if (userDir == null){
          System.out.println("Creating new guest user");
          File serverDir = new File(usersDir, user.getServer().getID());
          userDir = new File(serverDir, user.getUsername());
          if ((!userDir.exists() || !userDir.isDirectory()) && !userDir.mkdirs()){
            JOptionPane.showMessageDialog(mainFrame, "Unable to create directory "+userDir, 
              "Error", JOptionPane.ERROR_MESSAGE);
            return null;
          }
        }
      }
      else
        return null;
    }
    else if (userDir == null){
      System.out.println("Querying user about creating a new account");
      int result = JOptionPane.showConfirmDialog(getMainFrame(),
        "Would you like to save your \"" + user.getUsername() + "\" profile?", "Save profile?", JOptionPane.YES_NO_OPTION);
      if (result == JOptionPane.YES_OPTION){
        System.out.println("Creating new user, named "+user.getUsername());
        File serverDir = new File(usersDir, user.getServer().getID());
        userDir = new File(serverDir, user.getUsername());
        if ((!userDir.exists() || !userDir.isDirectory()) && !userDir.mkdirs()){
          JOptionPane.showMessageDialog(mainFrame, "Unable to create directory "+userDir, "Error", JOptionPane.ERROR_MESSAGE);
          return null;
        }
      }
      else
        return null;
    }

    try{
      Properties props = user.getProperties();
      Hashtable userFiles = user.getUserFiles();

      File propsFile = new File(userDir, "settings");
      OutputStream propsOut = new BufferedOutputStream(new FileOutputStream(propsFile));
      props.save(propsOut, user.getUsername()+"'s properties for "+user.getServer().getLongName());
      propsOut.close();

      if (!userFiles.isEmpty()){
        File userFilesFile = new File(userDir, "files"); 
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(userFilesFile)));
        out.writeInt(userFiles.size());
        Enumeration filenames = userFiles.keys();
        while (filenames.hasMoreElements()){
          String filename = (String)filenames.nextElement();
          MemoryFile memFile = (MemoryFile)userFiles.get(filename);
          out.writeUTF(filename);
          synchronized(memFile){
            out.writeInt(memFile.getSize());
            memFile.writeTo(out);
          }
        }
        out.close();
      }

      userDirs.put(user, userDir);

      String settingsPath = getSettingsPath(user);

      if (user.isGuest())
        setProperty(user.getServer().getID()+".guestAccountPath", settingsPath);

      return settingsPath;
    } catch (IOException e){
        JOptionPane.showMessageDialog(mainFrame, "Unable to save user file into:\n"+userDir, "Error", JOptionPane.ERROR_MESSAGE);
        return null;
      }
  }





  /**
   * Returns an array containing all the supported servers.
   */

  public static Server [] getServers(){
    Enumeration serversEnum = servers.elements();
    Server [] arr = new Server[servers.size()];
    for (int i = 0; i < arr.length; i++)
      arr[i] = (Server)serversEnum.nextElement();

    return arr;
  }




  /**
   * Returns the server with the given id, or null if the server with the
   * given name is not supported.
   */

  public static Server getServer(String serverID){
    return (Server)servers.get(serverID);
  }




  /**
   * Returns a string describing the name and version of the interface.
   */

  public static String getInterfaceName(){
    return getAppName()+" "+getAppVersion()+" ("+System.getProperty("java.vendor")+" "+System.getProperty("java.version")+", "+System.getProperty("os.name")+" "+System.getProperty("os.version")+")";
  }




  /**
   * Returns the official name of the application.
   */

  public static String getAppName(){
    return getProperty("name", "Jin");
  }



  /**
   * Returns the version of the application.
   */

  public static String getAppVersion(){
    return getProperty("version");
  }




  /**
   * Jin's main method.
   */

  public static void main(String [] args){
    try{
      try{
        Class.forName("com.incors.plaf.kunststoff.KunststoffLookAndFeel");
        UIManager.installLookAndFeel("Kunststoff", "com.incors.plaf.kunststoff.KunststoffLookAndFeel");
      } catch (ClassNotFoundException e){}

      try{
        Class.forName("swing.addon.plaf.threeD.ThreeDLookAndFeel");
        UIManager.installLookAndFeel("3D Look&Feel", "swing.addon.plaf.threeD.ThreeDLookAndFeel");
      } catch (ClassNotFoundException e){}

      mainFrame = new JinFrame();

      String lnf = getProperty("default.lf");
//      if (lnf==null){
//        String systemLookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
//        if (systemLookAndFeelClassName != null)
//          UIManager.setLookAndFeel(systemLookAndFeelClassName);
//      }
//      else
      if (lnf != null)
        UIManager.setLookAndFeel(lnf);

      SwingUtilities.updateComponentTreeUI(mainFrame);
      

      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Rectangle screenBounds = new Rectangle(screenSize);
      // Pretend the screen is somewhat bigger to allow frame decorations to be
      // placed outside the screen.
      screenBounds.x -= 10;
      screenBounds.y -= 10;
      screenBounds.width += 20;
      screenBounds.height += 20;

      String frameBoundsString = getProperty("frame.bounds");
      Rectangle frameBounds = null;
      if (frameBoundsString != null)
        frameBounds = StringParser.parseRectangle(frameBoundsString);
      
      if (frameBounds == null){
        mainFrame.setLocation(screenSize.width/16, screenSize.height/16);
        mainFrame.setSize(screenSize.width*7/8, screenSize.height*7/8);
      }
      else
        mainFrame.setBounds(frameBounds);


      String title = getProperty("frame.title", "Jin");
      mainFrame.setTitle(title);

      URL iconURL = Jin.class.getResource(getProperty("icon"));
      Image iconImage = Toolkit.getDefaultToolkit().getImage(iconURL);
      if (iconImage != null)
        mainFrame.setIconImage(iconImage);

      mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      mainFrame.addWindowListener(new JinCloser());

      mainFrame.setVisible(true);
    } catch (Throwable t){
        if (t instanceof ThreadDeath)
          throw (ThreadDeath)t;
        t.printStackTrace();
        System.exit(1);
      }
  }




  /**
   * Are we already showing a closing dialog?
   */

  private static boolean showingCloseDialog = false;




  /**
   * Opens a modal dialog over the closing window, asking the user to confirm
   * he wants to quit. If the user confirms, calls Jin.exit().
   */

  public static void close(){
    if (showingCloseDialog)
      return;
    showingCloseDialog = true;
    int result = JOptionPane.showConfirmDialog(mainFrame, "Close Jin?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
    showingCloseDialog = false;
    switch (result){
      case JOptionPane.CANCEL_OPTION:
      case JOptionPane.CLOSED_OPTION:
        return;
      case JOptionPane.OK_OPTION:
        Jin.exit();
        break;
      default:
        System.err.println("Unknown option type: "+result);
    }
  }





  /**
   * This method is called to execute the exiting from Jin procedure. It's
   * responsible for releasing all the resources allocated by this class, saving
   * the properties etc. The last operation of this method is a call to 
   * System.exit() with the value 0.
   */

  private static void exit(){
    try{
      mainFrame.exiting();

      // Save the l&f class name
      setProperty("default.lf", UIManager.getLookAndFeel().getClass().getName());


      boolean isFrameOK = true;
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Rectangle frameBounds = mainFrame.getBounds();
      if (frameBounds.x+frameBounds.width < 50)
        isFrameOK = false;
      if (frameBounds.y < -10)
        isFrameOK = false;
      if (frameBounds.width < 30)
        isFrameOK = false;
      if (frameBounds.height < 40)
        isFrameOK = false;
      if (frameBounds.x > screenSize.width - 10)
        isFrameOK = false;
      if (frameBounds.y > screenSize.height - 20)
        isFrameOK = false;

      // Save the bounds of the main frame.
      if (isFrameOK)
        setProperty("frame.bounds", StringEncoder.encodeRectangle(frameBounds));

      OutputStream propsOut = new FileOutputStream(new File(jinUserHome, "user.properties"));
      userProps.save(propsOut, "Jin chess client properties");
      propsOut.close();
      System.exit(0);
    } catch (IOException e){
        System.err.println("Unable to complete exit procedure:");
        e.printStackTrace();
      }
  }




  /**
   * This class is a WindowListener responsible for confirming that the user
   * wants to close Jin, and if the user confirms, calling Jin.exit().
   *
   * @see Jin#exit()
   */

  private static class JinCloser extends WindowAdapter{

    /**
     * Calls Jin.close()
     */

    public void windowClosing(WindowEvent evt){
      Jin.close();
    }

  }

}
