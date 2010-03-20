/**
 * The utillib library.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2002 Alexander Maryanovsky.
 * All rights reserved.
 *
 * The utillib library is free software; you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * The utillib library is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with utillib library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package free.util.swing;

import java.util.*;
import javax.swing.*;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;


/**
 * A JMenu which contains JCheckBoxMenuItems controlling and reflecting the
 * current look&feel.
 */

public class LookAndFeelMenu extends JMenu{


  /**
   * The Components that need to be updated when the look and feel changes.
   */

  private final Vector componentsToUpdate = new Vector();



  /**
   * Maps LookAndFeel class names to AbstractButtons representing them.
   */

  private final Hashtable lookAndFeelClassNamesToButtons = new Hashtable();



  /**
   * Maps AbstractButtons to LookAndFeel class names they represent.
   */

  private final Hashtable buttonsToLookAndFeelClassNames = new Hashtable();




  /**
   * The ButtonGroup of all the look and feel buttons.
   */

  private final ButtonGroup lnfButtonGroup;



  
  /**
   * Creates a new LookAndFeelMenu.
   *
   * @param componentTreeToUpdate A component whose children to update
   * (recursively) when the LookAndFeel changes.
   */

  public LookAndFeelMenu(Component componentTreeToUpdate){
    super("Look&Feel");

    ActionListener lnfActionListener = new LookAndFeelChoiceListener();

    UIManager.LookAndFeelInfo [] lnfs = UIManager.getInstalledLookAndFeels();
    String currentLookAndFeelClassName = UIManager.getLookAndFeel().getClass().getName();
    lnfButtonGroup = new ButtonGroup();
    for (int i=0;i<lnfs.length;i++){
      UIManager.LookAndFeelInfo lnf = lnfs[i];
      JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(lnf.getName());
      menuItem.addActionListener(lnfActionListener);

      lookAndFeelClassNamesToButtons.put(lnf.getClassName(), menuItem);
      buttonsToLookAndFeelClassNames.put(menuItem, lnf.getClassName());

      if (lnf.getClassName().equals(currentLookAndFeelClassName))
        menuItem.setSelected(true);

      menuItem.setMnemonic(lnf.getName().charAt(0));

      lnfButtonGroup.add(menuItem);
      add(menuItem);
    }

    addComponentTreeToBeUpdated(componentTreeToUpdate);

    UIManager.addPropertyChangeListener(new LookAndFeelChangeListener());

    setMnemonic('L');
  }





  /**
   * Adds the given component to have it and all its children (and sub-children,
   * recursively) to be updated when the LookAndFeel changes.
   */

  private void addComponentTreeToBeUpdated(Component component){
    if (!componentsToUpdate.contains(component))
      componentsToUpdate.addElement(component);
  }





  /**
   * Removes the given component from the list of components whose tree is 
   * updated when the LookAndFeel changes.
   */

  private void removeComponentTreeToBeUpdated(Component component){
    componentsToUpdate.removeElement(component);
  }





  /**
   * Updates all the registered components.
   */

  private void updateComponents(){
    for (int i=0;i<componentsToUpdate.size();i++){
      Component component = (Component)componentsToUpdate.elementAt(i);
      SwingUtilities.updateComponentTreeUI(component);
    }
  }

  




  /**
   * The ActionListener listening on the buttons that represent the LookAndFeels
   * and changes the look and feel.
   */

  private class LookAndFeelChoiceListener implements ActionListener{

    /**
     * Changes the Look And Feel to the one specified by the AbstractButton on
     * which the event ocurred.
     */

    public void actionPerformed(ActionEvent evt){
      try{
        String lnf = (String)buttonsToLookAndFeelClassNames.get(evt.getSource());
        UIManager.setLookAndFeel(lnf);
        updateComponents();
      } catch (Exception e){
          System.err.println("Unable to set look and feel:");
          e.printStackTrace();
        }
    }

  }




  /**
   * The PropertyChangeListener listenening on the current LookAndFeel changing,
   * then modifying the AbstractButton representing the new LookAndFeel to be
   * pressed and updating all the necessary component trees.
   */

  private class LookAndFeelChangeListener implements PropertyChangeListener{
    
    /**
     * Changes the button corresponding to the new LookAndFeel to be pressed
     * and updates the necessary component trees.
     */

    public void propertyChange(PropertyChangeEvent evt){
      LookAndFeel lnf = UIManager.getLookAndFeel();
      String newlnfClassName = UIManager.getLookAndFeel().getClass().getName();

      AbstractButton button = (AbstractButton)lookAndFeelClassNamesToButtons.get(newlnfClassName);
      if (button==null){
        JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(lnf.getName());
        menuItem.addActionListener(new LookAndFeelChoiceListener());

        lookAndFeelClassNamesToButtons.put(newlnfClassName, menuItem);
        buttonsToLookAndFeelClassNames.put(menuItem, newlnfClassName);

        lnfButtonGroup.add(menuItem);
        add(menuItem);

        button = menuItem;
      }

      button.setSelected(true);
      updateComponents();
    }
  }


}
