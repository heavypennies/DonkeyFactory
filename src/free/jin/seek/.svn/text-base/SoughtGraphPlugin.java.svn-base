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

package free.jin.seek;

import java.awt.*;
import javax.swing.*;
import free.jin.plugin.Plugin;
import free.jin.plugin.PluginContext;
import free.jin.plugin.UnsupportedContextException;
import free.jin.SeekJinConnection;
import free.jin.event.SeekListener;
import free.jin.event.SeekEvent;
import free.jin.seek.event.SeekSelectionListener;
import free.jin.seek.event.SeekSelectionEvent;
import free.util.StringParser;
import free.util.StringEncoder;
import free.workarounds.FixedJInternalFrame;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.URL;


/**
 * The plugin which implements the SoughtGraph. Even though I haven't put this
 * plugin in one of the server specific packages, it's pretty specific to the
 * ICS derived servers (ICC, FICS, chess.net).
 */

public class SoughtGraphPlugin extends Plugin implements SeekListener, SeekSelectionListener{



  /**
   * The SoughtGraph.
   */

  protected SoughtGraph soughtGraph;



  /**
   * The JInternalFrame which contains the sought graph.
   */

  protected JInternalFrame soughtGraphFrame;




  /**
   * The JRadioButtonMenuItem indicating whether the sought graph is visible.
   */

  private JRadioButtonMenuItem visibleRB; 




  /**
   * The JRadioButtonMenuItem indicating whether the sought graph is invisible.
   */

  private JRadioButtonMenuItem nonVisibleRB;





  /**
   * Sets the plugin context - if the connection is not an instance of
   * SeekJinConnection, this method throws an UnsupportedContextException.
   */

  public void setContext(PluginContext context) throws UnsupportedContextException{
    if (!(context.getConnection() instanceof SeekJinConnection))
      throw new UnsupportedContextException("The connection does not implement seeking functionality");

    super.setContext(context);
  }




 
  /**
   * Starts this plugin.
   */

  public void start(){
    initSoughtGraph();
    registerListeners();
  }





  /**
   * Stops this plugin.
   */

  public void stop(){
    unregisterListeners();
    closeSoughtGraph();
  }





  /**
   * Initializes the sought graph.
   */

  protected void initSoughtGraph(){
    soughtGraph = createSoughtGraph();
    soughtGraphFrame = createSoughtGraphFrame();

    Container content = soughtGraphFrame.getContentPane();
    content.setLayout(new BorderLayout());
    content.add(soughtGraph, BorderLayout.CENTER);

    JDesktopPane desktop = getPluginContext().getMainFrame().getDesktop();

    Rectangle desktopBounds = new Rectangle(desktop.getSize());
    String boundsString = getProperty("frame-bounds");
    Rectangle bounds = null;
    if (boundsString!=null)
      bounds = StringParser.parseRectangle(boundsString);

    if (bounds == null){
      soughtGraphFrame.setBounds(desktopBounds.width/2, desktopBounds.height/2, desktopBounds.width/2, desktopBounds.height/2);
//      soughtGraphFrame.setBounds(desktopBounds.width - 650, desktopBounds.height - 450, 650, 450);
    }
    else
      soughtGraphFrame.setBounds(bounds);

    boolean isMaximized = Boolean.valueOf(getProperty("maximized", "false")).booleanValue();
    if (isMaximized){
      try{
        soughtGraphFrame.setMaximum(true);
      } catch (PropertyVetoException e){}
    }

    boolean isIconified = Boolean.valueOf(getProperty("iconified", "false")).booleanValue();
    if (isIconified){
      try{
        soughtGraphFrame.setIcon(true);
      } catch (PropertyVetoException e){}
    }

    JComponent icon = soughtGraphFrame.getDesktopIcon();
    String iconBoundsString = getProperty("frame-icon-bounds");
    if (iconBoundsString != null){
      Rectangle iconBounds = StringParser.parseRectangle(iconBoundsString);
      icon.setBounds(iconBounds);
    }

    boolean isSelected = Boolean.valueOf(getProperty("selected", "true")).booleanValue();

    if (Boolean.valueOf(getProperty("visible", "true")).booleanValue())
      showSoughtGraphFrame(isSelected);

    if (isSelected){
      // We can't do this immediately because if some other plugin adds another frame
      // afterwards, our frame will lose selection.
      SwingUtilities.invokeLater(new Runnable(){
        public void run(){
          try{
            soughtGraphFrame.toFront();
            soughtGraphFrame.setSelected(true);
          } catch (PropertyVetoException e){}
        }
      });
    }


    /* See http://developer.java.sun.com/developer/bugParade/bugs/4176136.html for the 
       reason I do this instead of adding an InternalFrameListener like a sane person. */
    soughtGraphFrame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
    soughtGraphFrame.addVetoableChangeListener(new VetoableChangeListener(){

      public void vetoableChange(PropertyChangeEvent pce) throws PropertyVetoException{
        if (closingFrame) // Ignore our own close frame calls
          return;

        if (pce.getPropertyName().equals(JInternalFrame.IS_CLOSED_PROPERTY)&&
            pce.getOldValue().equals(Boolean.FALSE)&&pce.getNewValue().equals(Boolean.TRUE)){

          hideSoughtGraphFrame();
          throw new PropertyVetoException("Canceled closing", pce);
        }
      }
    });
  }




  /**
   * Closes the sought graph and undoes everything done by the initSoughtGraph
   * method.
   */

  protected void closeSoughtGraph(){
    hideSoughtGraphFrame();
    soughtGraphFrame.dispose();
    soughtGraphFrame = null;
    soughtGraph = null;
  }




  /**
   * Creates and returns the sought graph.
   */

  protected SoughtGraph createSoughtGraph(){
    return new SoughtGraph(this);
  }




  /**
   * Creates and returns the JInternalFrame which will contain the sought graph.
   */

  protected JInternalFrame createSoughtGraphFrame(){
    JInternalFrame frame = new FixedJInternalFrame("Seek graph", true, true, true, true);

    String iconImageName = getProperty("icon-image");
    if (iconImageName != null){
      URL iconImageURL = SoughtGraphPlugin.class.getResource(iconImageName);
      if (iconImageURL!= null)
        frame.setFrameIcon(new ImageIcon(iconImageURL));
    }

    return frame;
  }




  /**
   * Shows the sought graph frame. Showing the sought graph frame should only be
   * done through this method, since it also registers this SoughtGraphPlugin as
   * a SeekListener. I haven't used a ComponentListener to automatically 
   * register the listener because it seems to be broken in JDK 1.1
   */

  protected void showSoughtGraphFrame(boolean bringToFront){
    if (soughtGraphFrame.getParent() != null)
      return;

    try{ 
      getPluginContext().getMainFrame().getDesktop().add(soughtGraphFrame);
      soughtGraphFrame.setVisible(true);

      if (bringToFront)
        soughtGraphFrame.toFront();
      else
        soughtGraphFrame.toBack();

      // The documentation of JInternalFrame says not to do this,
      // but this seems to be the only way to get the isClosed flag of a JInternalFrame
      // set to false.
      soughtGraphFrame.setClosed(false);

      soughtGraphFrame.setSelected(bringToFront);
    } catch (PropertyVetoException e){}

    // It may be null if the graph is shown before it's created.
    if ((visibleRB != null) && (!visibleRB.isSelected())) 
      visibleRB.setSelected(true);
    SeekJinConnection conn = (SeekJinConnection)getConnection();
    conn.getSeekJinListenerManager().addSeekListener(SoughtGraphPlugin.this);
  }



  /**
   * This is used to avoid recursion when closing the frame.
   * The recursion results from the VetoableChangeListener being invoked when
   * the frame is being closed which in turn invokes hideSoughtGraphFrame.
   */

  private boolean closingFrame = false;




  /**
   * Hides the sought graph frame. Hiding the sought graph frame should only be
   * done through this method, since it also unregisters this SoughtGraphPlugin
   * as a SeekListener. I haven't used a ComponentListener to automatically 
   * unregister the listener because it seems to be broken in JDK 1.1
   */

  protected void hideSoughtGraphFrame(){
    if (soughtGraphFrame.getParent() == null)
      return;

    try{ 
      closingFrame = true;
      soughtGraphFrame.setClosed(true);
    } catch (PropertyVetoException e){}
      finally{
        closingFrame = false;
      }

    soughtGraph.removeAllSeeks();

    // It may be null if the graph is shown before it's created.
    if ((visibleRB != null) && (visibleRB.isSelected())) 
      nonVisibleRB.setSelected(true);
    SeekJinConnection conn = (SeekJinConnection)getConnection();
    conn.getSeekJinListenerManager().removeSeekListener(SoughtGraphPlugin.this);
  }




  /**
   * Saves the state of this plugin into the user's properties.
   */

  public void saveState(){
    boolean isMaximized = soughtGraphFrame.isMaximum();
    setProperty("maximized", String.valueOf(isMaximized));

    boolean isIconified = soughtGraphFrame.isIcon();
    setProperty("iconified", String.valueOf(isIconified));

    // This is the only way to retrieve the "normal" bounds of the frame under
    // JDK1.2 and earlier. JDK1.3 has a getNormalBounds() method.
    if (isMaximized){
      try{
        soughtGraphFrame.setMaximum(false);
      } catch (java.beans.PropertyVetoException ex){}
    }

    Rectangle soughtGraphFrameBounds = soughtGraphFrame.getBounds();
    // If something bad happened, let's not save that state.
    if ((soughtGraphFrameBounds.width > 10) && (soughtGraphFrameBounds.height > 10))
      setProperty("frame-bounds",StringEncoder.encodeRectangle(soughtGraphFrameBounds));

    boolean isVisible = (soughtGraphFrame.getParent() != null) ||
                        (soughtGraphFrame.getDesktopIcon().getParent() != null);
    setProperty("visible", String.valueOf(isVisible));

    boolean isSelected = soughtGraphFrame.isSelected();
    setProperty("selected", String.valueOf(isSelected));
  }




  /**
   * Registers the necessary listeners.
   */

  protected void registerListeners(){
    soughtGraph.addSeekSelectionListener(this);
  }




  /**
   * Unregisters all the listeners registered by this SoughtGraphPlugin.
   */

  protected void unregisterListeners(){
    SeekJinConnection conn = (SeekJinConnection)getConnection();

    soughtGraph.removeSeekSelectionListener(this);
    conn.getSeekJinListenerManager().removeSeekListener(this); // Just in case.
  }




  /**
   * Creates and returns the JMenu for this plugin.
   */

  public JMenu createPluginMenu(){
    JMenu myMenu = new JMenu(getName());

    visibleRB = new JRadioButtonMenuItem("Graph Shown", new Boolean(getProperty("visible", "true")).booleanValue());
    nonVisibleRB = new JRadioButtonMenuItem("Graph Hidden", !visibleRB.isSelected());

    visibleRB.setMnemonic('s');
    nonVisibleRB.setMnemonic('h');

    ButtonGroup visibilityGroup = new ButtonGroup();
    visibilityGroup.add(visibleRB);
    visibilityGroup.add(nonVisibleRB);

    visibleRB.setActionCommand("visible");
    nonVisibleRB.setActionCommand("hidden");

    ActionListener visibilityListener = new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        String actionCommand = evt.getActionCommand();

        if ("visible".equals(actionCommand))
          showSoughtGraphFrame(true);
        else if ("hidden".equals(actionCommand))
          hideSoughtGraphFrame();
        else
          throw new IllegalStateException("Unknown action command: "+actionCommand);
      }
    };

    visibleRB.addActionListener(visibilityListener);
    nonVisibleRB.addActionListener(visibilityListener);

    myMenu.add(visibleRB);
    myMenu.add(nonVisibleRB);

    return myMenu;
  }




  /**
   * SeekListener implementation. Gets called when a seek is added.
   */

  public void seekAdded(SeekEvent evt){
    soughtGraph.addSeek(evt.getSeek());
  }




  /**
   * SeekListener implementation. Gets called when a seek is removed.
   */

  public void seekRemoved(SeekEvent evt){
    soughtGraph.removeSeek(evt.getSeek());
  }




  /**
   * SeekSelectionListener implementation. Gets called when the user selects a
   * Seek. This method asks the SeekJinConnection to accept the selected seek.
   */

  public void seekSelected(SeekSelectionEvent evt){
    ((SeekJinConnection)getConnection()).acceptSeek(evt.getSeek());
  }

}
