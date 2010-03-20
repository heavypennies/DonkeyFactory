/**
 * Jin - a chess client for internet chess servers.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2003 Alexander Maryanovsky.
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

package free.jin.scripter;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.util.Vector;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import free.util.AWTUtilities;
import free.util.swing.SwingUtils;
import free.workarounds.FixedJTextField;
import free.workarounds.FixedJComboBox;
import free.util.TableLayout;
import free.util.Utilities;
import free.util.IOUtilities;
import free.util.swing.PlainTextDialog;



/**
 * The superclass for dialogs allowing the user to create a <code>Script</code>.
 */

abstract class ScriptDialog extends JDialog{


  /**
   * The default string we show in the event type choice.
   */

  private static final String selectEventTypeString = "Select Event Type";



  /**
   * The text of the event type helpfile. Loaded lazily.
   */

  private static String eventTypeHelpText = null;



  /**
   * The text of the event subtypes helpfiles. Loaded lazily.
   */

  private static String eventSubtypesHelpText = null;




  /**
   * The scripter we're creating this <code>Script</code> for.
   */

  protected final Scripter scripter;



  /**
   * The template script.
   */

  protected final Script templateScript;



  /**
   * The resulting script.
   */

  private Script script = null;




  /**
   * Creates a new ScriptDialog with the specified parent component and title.
   */

  public ScriptDialog(Component parent, String title, Scripter scripter, Script templateScript){
    super(AWTUtilities.frameForComponent(parent), title, true);

    if (scripter == null)
      throw new IllegalArgumentException("The specified scripter is null");

    this.templateScript = templateScript;
    this.scripter = scripter;

    SwingUtils.registerEscapeCloser(this);
  }



  /**
   * Creates the script type specific UI and returns a <code>Container</code>
   * containing it.
   */

  protected abstract Container createScriptTypeSpecificUI();



  /**
   * This method is called when the user clicks the OK button. It is supposed
   * to collect the script type specific data from the UI and return a script.
   * If the user specified information is invalid, it should handle the problem
   * in some manner (displaying an error message to the user, for example) and
   * return null.
   */

  protected abstract Script createScriptOnOk(String scriptName, String eventType, String [] selectedSubtypes);



  /**
   * Creates the UI.
   */

  protected final void createUI(){
    String defaultScriptName = (templateScript == null ? "" : templateScript.getName());
    String defaultEventType = (templateScript == null ? selectEventTypeString : templateScript.getEventType());
    String [] defaultSelectedSubtypes = 
      (templateScript == null ? new String[0] : templateScript.getEventSubtypes());

    String [] eventTypes = scripter.getSupportedEventTypes();

    final JTextField scriptNameField = new FixedJTextField(defaultScriptName);

    final JComboBox eventTypeChoice = new FixedJComboBox(eventTypes);
    eventTypeChoice.setEditable(false);
    eventTypeChoice.addItem(selectEventTypeString);
    eventTypeChoice.setSelectedItem(defaultEventType);

    final JPanel subtypesPanel = new JPanel(new TableLayout(2));
    JPanel subtypesHolderPanel = new JPanel(new BorderLayout());
    subtypesHolderPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
    subtypesHolderPanel.add(subtypesPanel, BorderLayout.NORTH);
    JScrollPane subtypesScrollPane = new JScrollPane(subtypesHolderPanel);
    subtypesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    subtypesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    subtypesScrollPane.setPreferredSize(new Dimension(300, 115));
    
    if (templateScript != null)
      updateSubtypesPanel(subtypesPanel, defaultEventType, defaultSelectedSubtypes);

    eventTypeChoice.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        String eventType = (String)eventTypeChoice.getSelectedItem();
        String [] selectedSubtypes = (templateScript == null ? new String[0] : templateScript.getEventSubtypes());
        updateSubtypesPanel(subtypesPanel, eventType, selectedSubtypes);
      }
    });

    JLabel scriptNameLabel = new JLabel("Script Name:");
    JLabel eventTypeLabel = new JLabel("Event Type:");
    JLabel eventSubtypesLabel = new JLabel("Event Subtypes:");

    scriptNameLabel.setDisplayedMnemonic('N');
    eventTypeLabel.setDisplayedMnemonic('T');
    eventSubtypesLabel.setDisplayedMnemonic('S');

    scriptNameLabel.setLabelFor(scriptNameField);
    eventTypeLabel.setLabelFor(eventTypeChoice);
    eventSubtypesLabel.setLabelFor(subtypesPanel);

    eventSubtypesLabel.setAlignmentY(Component.TOP_ALIGNMENT);

    JButton eventTypeHelp = new JButton("Help...");
    JButton eventSubtypesHelp = new JButton("Help...");

    eventTypeHelp.setMnemonic('H');
    eventSubtypesHelp.setMnemonic('e');

    eventTypeHelp.setDefaultCapable(false);
    eventSubtypesHelp.setDefaultCapable(false);

    eventTypeHelp.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        synchronized(ScriptDialog.class){
          if (eventTypeHelpText == null){
            String resourceLocation = "help/eventType.txt";
            try{
              eventTypeHelpText = IOUtilities.loadText(ScriptDialog.class.getResource(resourceLocation));
            } catch (IOException e){
                JOptionPane.showMessageDialog(ScriptDialog.this, "Unable to load file: "+resourceLocation, "Error", JOptionPane.ERROR_MESSAGE);
                return;
              }
          }
        }
        PlainTextDialog textDialog = new PlainTextDialog(ScriptDialog.this, "Event type help", eventTypeHelpText);
        textDialog.setTextAreaFont(new Font("Monospaced", Font.PLAIN, 12));
        AWTUtilities.centerWindow(textDialog, ScriptDialog.this);
        textDialog.setVisible(true);
      }
    });

    eventSubtypesHelp.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        synchronized(ScriptDialog.class){
          if (eventSubtypesHelpText == null){
            String resourceLocation = "help/eventSubtypes.txt";
            try{
              eventSubtypesHelpText = IOUtilities.loadText(ScriptDialog.class.getResource(resourceLocation));
            } catch (IOException e){
                JOptionPane.showMessageDialog(ScriptDialog.this, "Unable to load file: "+resourceLocation, "Error", JOptionPane.ERROR_MESSAGE);
                return;
              }
          }
        }
        PlainTextDialog textDialog = new PlainTextDialog(ScriptDialog.this, "Event subtypes help", eventSubtypesHelpText);
        textDialog.setTextAreaFont(new Font("Monospaced", Font.PLAIN, 12));
        AWTUtilities.centerWindow(textDialog, ScriptDialog.this);
        textDialog.setVisible(true);
      }
    });


    JPanel genericDataPanel = new JPanel(new TableLayout(3, 7, 5));

    genericDataPanel.add(scriptNameLabel);
    genericDataPanel.add(scriptNameField);
    genericDataPanel.add(Box.createVerticalStrut(eventTypeHelp.getPreferredSize().height));

    genericDataPanel.add(eventTypeLabel);
    genericDataPanel.add(eventTypeChoice);
    genericDataPanel.add(eventTypeHelp);

    genericDataPanel.add(eventSubtypesLabel);
    genericDataPanel.add(subtypesScrollPane);
    genericDataPanel.add(eventSubtypesHelp);

    Container scriptTypeSpecificDataPanel = createScriptTypeSpecificUI();

    JPanel dataPanel = new JPanel(null);
    dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
    dataPanel.setBorder(new EmptyBorder(10, 10, 5, 10));

    dataPanel.add(genericDataPanel);
    dataPanel.add(Box.createVerticalStrut(10));
    dataPanel.add(scriptTypeSpecificDataPanel);

    JButton okButton = new JButton("OK");
    JButton cancelButton = new JButton("Cancel");

    okButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        String scriptName = scriptNameField.getText();
        String eventType = (String)eventTypeChoice.getSelectedItem();

        Vector selectedSubtypesVector = new Vector();
        Component [] subtypeCheckboxes = subtypesPanel.getComponents();
        for (int i = 0; i < subtypeCheckboxes.length; i++){
          Component component = subtypeCheckboxes[i];
          if (component instanceof JCheckBox){
            JCheckBox checkbox = (JCheckBox)component;
            if (checkbox.isSelected())
              selectedSubtypesVector.addElement(checkbox.getActionCommand());
          }
        }
        String [] selectedSubtypes = new String[selectedSubtypesVector.size()];
        selectedSubtypesVector.copyInto(selectedSubtypes);

        if ((scriptName == null) || (scriptName.length() == 0)){
          JOptionPane.showMessageDialog(ScriptDialog.this, "You must specify a script name", "Missing Option", JOptionPane.ERROR_MESSAGE);
          return;
        }

        if (selectEventTypeString.equals(eventType)){
          JOptionPane.showMessageDialog(ScriptDialog.this, "You must specify an event type", "Missing Option", JOptionPane.ERROR_MESSAGE);
          return;
        }

        if ((selectedSubtypes.length == 0) && (scripter.getEventSubtypes(eventType) != null)){
          JOptionPane.showMessageDialog(ScriptDialog.this, "You must select at least one event subtype", "Missing Option", JOptionPane.ERROR_MESSAGE);
          return;
        }

        script = createScriptOnOk(scriptName, eventType, selectedSubtypes);
        if (script != null)
          dispose();
      }
    });

    cancelButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        script = null;
        dispose();
      }
    });

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);

    JPanel contentPane = new JPanel(new BorderLayout(5, 10));
    contentPane.add(dataPanel, BorderLayout.CENTER);
    contentPane.add(buttonPanel, BorderLayout.SOUTH);

    setContentPane(contentPane);

    getRootPane().setDefaultButton(okButton);
  }



  /**
   * Puts the appropriate checkboxes in the subtypes panel.
   */

  private void updateSubtypesPanel(JPanel panel, String eventType, String [] selectedSubtypes){
    panel.removeAll();

    if (!selectEventTypeString.equals(eventType)){
      String [] subtypes = scripter.getEventSubtypes(eventType);
      if (subtypes != null){
        for (int i = 0; i < subtypes.length; i++){
          String subtype = subtypes[i];
          JCheckBox checkbox = new JCheckBox(subtype);
          checkbox.setActionCommand(subtype);
          checkbox.setSelected(Utilities.isElementOf(selectedSubtypes, subtype));
          panel.add(checkbox);
        }
      }
    }

    panel.revalidate();
  }



  /**
   * Displays the dialog until the user finishes creating the script and then
   * returns the resulting <code>Script</code>.
   */

  public Script askForScript(){
    setVisible(true);

    return script;
  }


}
