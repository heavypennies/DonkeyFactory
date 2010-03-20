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

package free.jin.gamelogger;

import bsh.EvalError;
import free.jin.plugin.BadChangesException;
import free.jin.plugin.PreferencesPanel;
import free.util.AWTUtilities;
import free.util.IOUtilities;
import free.util.swing.ExtensionFileFilter;
import free.util.swing.PlainTextDialog;
import free.workarounds.FixedJTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;



/**
 * The preferences panel for the game logger.
 */

public class GameLoggerPreferencesPanel extends PreferencesPanel{



  /**
   * The GameLogger this panel shows preferences for.
   */

  protected GameLogger gameLogger;




  /**
   * The radio button that specifies a LOG_NONE mode.
   */

  private JRadioButton logNoneButton;




  /**
   * The radio button that specifies a LOG_ALL mode.
   */

  private JRadioButton logAllButton;




  /**
   * The radio button that specifies a USE_RULES mode.
   */

  private JRadioButton useRulesButton;



  /**
   * The text field where the user can specify the name of the file to log all 
   * the games into.
   */

  private JTextField allGamesLogFileField;




  /**
   * The list holding all the logging rules.
   */

  private JList loggingRulesList;



  /**
   * The text field for the rule name.
   */

  private JTextField rulenameField;




  /**
   * The text field for the filename where the rule specific games will be
   * logged.
   */

  private JTextField filenameField;




  /**
   * The text field for the rule condition.
   */

  private JTextField conditionField;




  /**
   * This variable is set to <code>true</code> when the values of the rule
   * fields is being changed programmatically because a new rule is selected.
   * This is done so that the DocumentListener knows not to fire a ChangeEvent.
   */

  private boolean ignoreRuleFieldsDocumentChange = false;



  /**
   * The currently selected index in the rules list. We need to keep this in
   * order to be able to roll back if the user tries to change the selection
   * when the current selection is incomplete.
   */

  private int rulesListSelectedIndex = -1;





  /**
   * A DocumentListener we register with all text components that require a
   * change event to be fired by this PreferencesPanel whenever their text
   * changes.
   */

  private final DocumentListener changeFiringDocumentListener = new DocumentListener(){
    public void changedUpdate(DocumentEvent e){if (!ignoreRuleFieldsDocumentChange) fireStateChanged();}
    public void insertUpdate(DocumentEvent e){if (!ignoreRuleFieldsDocumentChange) fireStateChanged();}
    public void removeUpdate(DocumentEvent e){if (!ignoreRuleFieldsDocumentChange) fireStateChanged();}
  };





  /**
   * Creates a new <code>GameLoggerPreferencesPanel</code> for the specified
   * GameLogger.
   */

  public GameLoggerPreferencesPanel(GameLogger gameLogger){
    this.gameLogger = gameLogger;

    createUI();
  }




  /**
   * Creates the user interface for this preferences panel.
   */

  protected void createUI(){
    int loggingMode = gameLogger.getLoggingMode();
    String allGamesLogFile = gameLogger.getLogFileForAll();
    Vector loggingRules = gameLogger.getLoggingRules();

    logNoneButton = new JRadioButton("Do Not Log Games");
    logAllButton = new JRadioButton("Log All Games to File:");
    useRulesButton = new JRadioButton("Specify Logging Rules");

    logNoneButton.setMnemonic('D');
    logAllButton.setMnemonic('L');
    useRulesButton.setMnemonic('S');

    ButtonGroup modeGroup = new ButtonGroup();
    modeGroup.add(logNoneButton);
    modeGroup.add(logAllButton);
    modeGroup.add(useRulesButton);

    logNoneButton.setActionCommand("none");
    logAllButton.setActionCommand("all");
    useRulesButton.setActionCommand("rules");

    final JPanel logAllPanel = new JPanel();
    final JPanel useRulesPanel = new JPanel();

    ActionListener loggingModeListener = new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        String actionCommand = evt.getActionCommand();

        if ("none".equals(actionCommand)){
          setContainerEnabled(logAllPanel, false);
          setContainerEnabled(useRulesPanel, false);
        }
        if ("all".equals(actionCommand)){
          setContainerEnabled(logAllPanel, true);
          setContainerEnabled(useRulesPanel, false);
        }
        else if ("rules".equals(actionCommand)){
          setContainerEnabled(logAllPanel, false);
          setContainerEnabled(useRulesPanel, true);
        }

        fireStateChanged();
      }
    };

    logNoneButton.addActionListener(loggingModeListener);
    logAllButton.addActionListener(loggingModeListener);
    useRulesButton.addActionListener(loggingModeListener);

    switch(loggingMode){
      case GameLogger.LOG_NONE:  logNoneButton.setSelected(true); break;
      case GameLogger.LOG_ALL:   logAllButton.setSelected(true); break;
      case GameLogger.USE_RULES: useRulesButton.setSelected(true); break;
    }

    allGamesLogFileField = new FixedJTextField(10);
    allGamesLogFileField.setText(allGamesLogFile);
    allGamesLogFileField.getDocument().addDocumentListener(changeFiringDocumentListener);
    JButton browseAllGamesLogFileButton = new JButton("Browse...");
    browseAllGamesLogFileButton.setMnemonic('B');
    browseAllGamesLogFileButton.setDefaultCapable(false);
    browseAllGamesLogFileButton.addActionListener(new PGNFileSelectActionListener(this, allGamesLogFileField));

    logAllPanel.setLayout(new BorderLayout(5, 5));
    logAllPanel.add(allGamesLogFileField, BorderLayout.CENTER);
    logAllPanel.add(browseAllGamesLogFileButton, BorderLayout.EAST);

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    JPanel logNoneHelpPanel = new JPanel(new BorderLayout(5, 5));
    logNoneHelpPanel.add(logNoneButton, BorderLayout.WEST);

    JPanel logAllHelpPanel = new JPanel(new BorderLayout());
    logAllHelpPanel.add(logAllButton, BorderLayout.WEST);
    logAllHelpPanel.add(logAllPanel, BorderLayout.CENTER);

    JPanel useRulesHelpPanel = new JPanel(new BorderLayout());
    useRulesHelpPanel.add(useRulesButton, BorderLayout.WEST);

    JPanel modeButtonsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
    modeButtonsPanel.add(logNoneHelpPanel);
    modeButtonsPanel.add(logAllHelpPanel);
    modeButtonsPanel.add(useRulesHelpPanel);


    final DefaultListModel rulesListModel = new DefaultListModel();
    for (int i = 0; i < loggingRules.size(); i++)
      rulesListModel.addElement(loggingRules.elementAt(i));
    loggingRulesList = new JList(rulesListModel);
    loggingRulesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    loggingRulesList.addListSelectionListener(new ListSelectionListener(){
      private boolean ignoreSelectionChange = false;
      public void valueChanged(ListSelectionEvent evt){
        if (evt.getValueIsAdjusting())
          return;
        if (ignoreSelectionChange)
          return;
        if ((rulesListSelectedIndex != -1) && (rulesListSelectedIndex < rulesListModel.size())){
          try{
            updateRuleFromUI(rulesListSelectedIndex);
          } catch (BadChangesException e){
              ignoreSelectionChange = true;
              loggingRulesList.setSelectedIndex(rulesListSelectedIndex);
              ignoreSelectionChange = false;
              JOptionPane.showMessageDialog(GameLoggerPreferencesPanel.this, e.getMessage(), "Error", 
                JOptionPane.ERROR_MESSAGE);
              if (e.getErrorComponent() != null)
                e.getErrorComponent().requestFocus();
              return;
            }
        }
        rulesListSelectedIndex = loggingRulesList.getSelectedIndex();
        try{
          ignoreRuleFieldsDocumentChange = true;
          if (rulesListSelectedIndex == -1){
            rulenameField.setText("");
            filenameField.setText("");
            conditionField.setText("");
          }
          else{  
            Object selectedItem = loggingRulesList.getSelectedValue();
            if (selectedItem instanceof LoggingRule){
              LoggingRule selectedRule = (LoggingRule)selectedItem;
              rulenameField.setText(selectedRule.getName());
              filenameField.setText(selectedRule.getFilename());
              conditionField.setText(selectedRule.getCondition());
            }
          }
        } finally{
            ignoreRuleFieldsDocumentChange = false;
          }
      }
    });

    JScrollPane loggingListRulesScrollPane = new JScrollPane(loggingRulesList); loggingListRulesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    loggingListRulesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    loggingListRulesScrollPane.setPreferredSize(new Dimension(80, 80));


    JLabel rulesLabel = new JLabel("Rules");
    rulesLabel.setDisplayedMnemonic('R');
    rulesLabel.setLabelFor(loggingRulesList);
    rulesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JButton addRuleButton = new JButton("Add Rule");
    addRuleButton.setMnemonic('A');
    addRuleButton.setDefaultCapable(false);
    addRuleButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        int selectedIndex = loggingRulesList.getSelectedIndex();
        if (selectedIndex != -1){
          try{
            updateRuleFromUI(selectedIndex);
          } catch (BadChangesException e){
              JOptionPane.showMessageDialog(GameLoggerPreferencesPanel.this, e.getMessage(), "Error", 
                JOptionPane.ERROR_MESSAGE);
              if (e.getErrorComponent() != null)
                e.getErrorComponent().requestFocus();
              return;
            }
        }
        rulesListModel.addElement("New Logging Rule");
        loggingRulesList.setSelectedIndex(rulesListModel.size() - 1);

        rulenameField.setText("");
        filenameField.setText("");
        conditionField.setText("");
        rulenameField.requestFocus();
      }
    });

    JButton deleteRuleButton = new JButton("Delete Rule");
    deleteRuleButton.setMnemonic('t');
    deleteRuleButton.setDefaultCapable(false);
    deleteRuleButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        int selectedIndex = loggingRulesList.getSelectedIndex();
        if (selectedIndex != -1){
          String ruleName = rulesListModel.getElementAt(selectedIndex).toString();
          int result = JOptionPane.showConfirmDialog(GameLoggerPreferencesPanel.this, 
            "Are you sure you want to delete the rule \""+ruleName+"\"?", "Confirm rule deletion", JOptionPane.YES_NO_OPTION);
          if (result == JOptionPane.YES_OPTION){
            rulesListModel.removeElementAt(selectedIndex);
            if (selectedIndex < rulesListModel.size())
              loggingRulesList.setSelectedIndex(selectedIndex);
            else if (rulesListModel.size() != 0)
              loggingRulesList.setSelectedIndex(selectedIndex - 1);
            else{
              // Needed because of a bug in earlier versions of swing which causes ListSelectionEvents
              // not to be fired for events when no index is selected.
              try{
                ignoreRuleFieldsDocumentChange = true;
                rulenameField.setText("");
                filenameField.setText("");
                conditionField.setText("");
                rulesListSelectedIndex = -1;
              } finally{
                  ignoreRuleFieldsDocumentChange = false;
                }
            }

            loggingRulesList.setSelectedIndex(-1);
              
            fireStateChanged();
          }
        }
      }
    });

    JPanel buttonHelpPanel = new JPanel(new GridLayout(2, 1, 5, 5));
    buttonHelpPanel.add(addRuleButton);
    buttonHelpPanel.add(deleteRuleButton);
    buttonHelpPanel.setMaximumSize(buttonHelpPanel.getPreferredSize());

    Box buttonsBox = Box.createVerticalBox();
    buttonsBox.add(Box.createVerticalGlue());
    buttonsBox.add(buttonHelpPanel);
    buttonsBox.add(Box.createVerticalGlue());

    Box listAndButtonsPanel = Box.createHorizontalBox();
    listAndButtonsPanel.add(loggingListRulesScrollPane);
    listAndButtonsPanel.add(Box.createHorizontalStrut(10));
    listAndButtonsPanel.add(buttonsBox);
    listAndButtonsPanel.add(Box.createHorizontalGlue());

    JPanel rulesListPanel = new JPanel(new BorderLayout(5, 5));
    rulesListPanel.add(rulesLabel, BorderLayout.NORTH);
    rulesListPanel.add(listAndButtonsPanel, BorderLayout.CENTER);


    rulenameField = new FixedJTextField(10);
    filenameField = new FixedJTextField(10);
    conditionField = new FixedJTextField(20);

    if (rulesListModel.size() != 0)
      loggingRulesList.setSelectedIndex(0);

    rulenameField.getDocument().addDocumentListener(changeFiringDocumentListener);
    filenameField.getDocument().addDocumentListener(changeFiringDocumentListener);
    conditionField.getDocument().addDocumentListener(changeFiringDocumentListener);

    rulenameField.getDocument().addDocumentListener(new DocumentListener(){
      public void changedUpdate(DocumentEvent e){updateRulename();}
      public void insertUpdate(DocumentEvent e){updateRulename();}
      public void removeUpdate(DocumentEvent e){updateRulename();}

      private void updateRulename(){
        Object selectedItem = loggingRulesList.getSelectedValue();
        if (selectedItem instanceof String){
          String text = rulenameField.getText();
          if (text.length() == 0)
            rulesListModel.setElementAt("New Logging Rule", loggingRulesList.getSelectedIndex());
          else
            rulesListModel.setElementAt(text, loggingRulesList.getSelectedIndex());
        }
        else if (selectedItem instanceof LoggingRule){
          LoggingRule loggingRule = (LoggingRule)selectedItem;
          String text = rulenameField.getText();
          if ((text != null) && (text.length() != 0)){ // A temporary state when setting text?
            loggingRule.setName(text);
            loggingRulesList.repaint();
          }
        }
      }
    });

    JLabel rulenameLabel = new JLabel("Rule Name:");
    JLabel filenameLabel = new JLabel("Filename (*.pgn):");
    JLabel conditionLabel = new JLabel("Condition:");

    rulenameLabel.setDisplayedMnemonic('N');
    filenameLabel.setDisplayedMnemonic('F');
    conditionLabel.setDisplayedMnemonic('C');

    rulenameLabel.setLabelFor(rulenameField);
    filenameLabel.setLabelFor(filenameField);
    conditionLabel.setLabelFor(conditionField);

    JButton browseLogFileButton = new JButton("Browse...");
    browseLogFileButton.setMnemonic('B');
    browseLogFileButton.setDefaultCapable(false);
    browseLogFileButton.addActionListener(new PGNFileSelectActionListener(this, filenameField));

    JButton helpConditionButton = new JButton("Help...");
    helpConditionButton.setMnemonic('H');
    helpConditionButton.setDefaultCapable(false);
    helpConditionButton.setPreferredSize(browseLogFileButton.getPreferredSize());
    helpConditionButton.addActionListener(new ActionListener(){
      private String helpText = null;

      public void actionPerformed(ActionEvent evt){
        if (helpText == null)
          try{
            helpText = IOUtilities.loadText(GameLoggerPreferencesPanel.class.getResource("condition-help.txt"));
          } catch (IOException e){
              JOptionPane.showMessageDialog(GameLoggerPreferencesPanel.this, "Unable to load the helpfile", "Error", JOptionPane.ERROR_MESSAGE);
              return;
            }

        PlainTextDialog textDialog = new PlainTextDialog(GameLoggerPreferencesPanel.this, "Logging condition help", helpText);
        textDialog.setTextAreaFont(new Font("Monospaced", Font.PLAIN, 12));
        AWTUtilities.centerWindow(textDialog, getParent());
        textDialog.setVisible(true);
      }
    });


    JPanel rulePropertiesPanel = new JPanel(new GridLayout(3, 1, 5, 5));
    Box rulenameBox = Box.createHorizontalBox();
    Box filenameBox = Box.createHorizontalBox();
    Box conditionBox = Box.createHorizontalBox();

    int maxPrefWidth = Math.max(rulenameLabel.getPreferredSize().width,
      Math.max(filenameLabel.getPreferredSize().width, conditionLabel.getPreferredSize().width));

    rulenameLabel.setPreferredSize(new Dimension(maxPrefWidth + 7, 20));
    filenameLabel.setPreferredSize(new Dimension(maxPrefWidth + 7, 20));
    conditionLabel.setPreferredSize(new Dimension(maxPrefWidth + 7, 20));

    rulenameBox.add(rulenameLabel);
    rulenameBox.add(Box.createHorizontalStrut(5));
    rulenameBox.add(rulenameField);
    rulenameBox.add(Box.createHorizontalStrut(5+browseLogFileButton.getPreferredSize().width));


    filenameBox.add(filenameLabel);
    filenameBox.add(Box.createHorizontalStrut(5));
    filenameBox.add(filenameField);
    filenameBox.add(Box.createHorizontalStrut(5));
    filenameBox.add(browseLogFileButton);

    conditionBox.add(conditionLabel);
    conditionBox.add(Box.createHorizontalStrut(5));
    conditionBox.add(conditionField);
    conditionBox.add(Box.createHorizontalStrut(5));
    conditionBox.add(helpConditionButton);


    rulePropertiesPanel.add(rulenameBox);
    rulePropertiesPanel.add(filenameBox);
    rulePropertiesPanel.add(conditionBox);

    useRulesPanel.setBorder(new EmptyBorder(5, 16, 0, 0));
    useRulesPanel.setLayout(new BorderLayout(5, 20));
    useRulesPanel.add(rulesListPanel, BorderLayout.CENTER);
    useRulesPanel.add(rulePropertiesPanel, BorderLayout.SOUTH);
    

    add(modeButtonsPanel, BorderLayout.NORTH);
    add(useRulesPanel, BorderLayout.CENTER);

    if (loggingMode == GameLogger.LOG_ALL)
      setContainerEnabled(useRulesPanel, false);
    else if (loggingMode == GameLogger.USE_RULES)
      setContainerEnabled(logAllPanel, false);
    else{
      setContainerEnabled(useRulesPanel, false);
      setContainerEnabled(logAllPanel, false);
    }
  }




  /**
   * Enables or disables all the components within the specified container.
   * This is a rather hacky method, which is very specific to this class, as
   * it's impossible to implement the desired functionality in a generic manner.
   */

  private static void setContainerEnabled(Container container, boolean enabled){
    Component [] children = container.getComponents();
    for (int i = 0; i < children.length; i++){
      Component child = children[i];
      if (child instanceof JTextComponent)
        ((JTextComponent)child).setEditable(enabled);
      child.setEnabled(enabled);
      if (child instanceof Container)
        setContainerEnabled((Container)child, enabled);
    }
  }




  /**
   * Updates the currently selected rule from the current rule properties in the
   * UI. Returns <code>true</code> if the rule was successfully updated or
   * <code>false</code> if the properties entered by the user were bad.
   */

  private void updateRuleFromUI(int ruleIndex) throws BadChangesException{
    DefaultListModel rulesModel = (DefaultListModel)loggingRulesList.getModel();
    Object item = rulesModel.getElementAt(ruleIndex);

    LoggingRule rule = (item instanceof LoggingRule) ? (LoggingRule)item : null;

    String rulename = rulenameField.getText();
    String filename = filenameField.getText();
    String ruleString = conditionField.getText();

    String errorMessage = null;
    Component errorComponent = null;

    if ((rulename == null) || (rulename.length() == 0)){
      errorMessage = "You must specify a rule name";
      errorComponent = rulenameField;
    }
    else if ((filename == null) || (filename.length() == 0)){
      errorMessage = "You must specify the name of the file into which the games will be logged";
      errorComponent = filenameField;
    }
    else if ((ruleString == null) || (ruleString.length() == 0)){
      ruleString = "true";
      conditionField.setText(ruleString);
    }

    if (errorMessage != null)
      throw new BadChangesException(errorMessage, errorComponent);

    try{
      if (rule == null){
        rule = new LoggingRule(rulename, ruleString, filename);
        rulesModel.setElementAt(rule, ruleIndex);
      }
      else{
        rule.setName(rulename);
        rule.setFilename(filename);
        rule.setCondition(ruleString);
        loggingRulesList.repaint();        
      }
    } catch (EvalError e){
        throw new BadChangesException("The specified logging condition is not valid", conditionField);
      }
  }


  /**
   * Applies the changed preferences done by the user.
   */

  public void applyChanges() throws BadChangesException{
    int selectedIndex = loggingRulesList.getSelectedIndex();
    if (selectedIndex != -1)
      updateRuleFromUI(selectedIndex);

    String loggingModeString;
    if (logNoneButton.isSelected())
      loggingModeString = "none";
    else if (logAllButton.isSelected())
      loggingModeString = "all";
    else if (useRulesButton.isSelected())
      loggingModeString = "rules";
    else
      throw new IllegalStateException("None of the mode radio buttons are selected");

    String allGamesLogFile = allGamesLogFileField.getText();
    if ("all".equals(loggingModeString) && ((allGamesLogFile == null) || (allGamesLogFile.length() == 0)))
      throw new BadChangesException("You must specify the name of the file into which the games will be logged", 
        allGamesLogFileField);

    gameLogger.setProperty("logging.mode", loggingModeString);

    gameLogger.setProperty("logging.all.filename", allGamesLogFile);

    DefaultListModel loggingRulesModel = (DefaultListModel)loggingRulesList.getModel();
    int rulesCount = loggingRulesModel.size();
    gameLogger.setIntegerProperty("logging.rules.count", rulesCount);
    for (int i = 0; i < rulesCount; i++){
      LoggingRule rule = (LoggingRule)loggingRulesModel.elementAt(i);
      gameLogger.setProperty("logging.rule-"+(i+1)+".name", rule.getName());
      gameLogger.setProperty("logging.rule-"+(i+1)+".condition", rule.getCondition());
      gameLogger.setProperty("logging.rule-"+(i+1)+".filename", rule.getFilename());
    }

    gameLogger.refreshFromProperties();
  }




  /**
   * A small ActionListener implementation which pops up a FileChooserDialog
   * when activated and lets the user choose a pgn file. It then sets the
   * selected filename to the specified JTextField.
   */

  private static class PGNFileSelectActionListener implements ActionListener{


    /**
     * The parent component.
     */

    private Component parent;



    /**
     * The textfield we set the selected filename to.
     */

    private JTextField textfield;



    /**
     * Creates a new PGNFileSelectActionListener with the specified parent
     * component and textfield.
     */

    public PGNFileSelectActionListener(Component parent, JTextField textfield){
      this.parent = parent;
      this.textfield = textfield;
    }




    public void actionPerformed(ActionEvent evt){
      File currentFile = new File(textfield.getText());
      File currentDir = null;
      if (currentFile.isFile()){
        currentDir = new File(currentFile.getParent());
        if (!currentDir.exists()){
          currentDir = null;
          currentFile = null;
        }
      }
      else if (currentFile.exists()){
        currentDir = currentFile;
        currentFile = null;
      }

      JFileChooser fileChooser = new JFileChooser(currentDir);
      fileChooser.setMultiSelectionEnabled(false);
      fileChooser.addChoosableFileFilter(new ExtensionFileFilter("Portable Game Notation files", ".pgn", false));
      fileChooser.setFileHidingEnabled(true);
      fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      int result = fileChooser.showDialog(parent, "Use File");
      if (result == JFileChooser.APPROVE_OPTION){
        String path = null;
        try{
          path = fileChooser.getSelectedFile().getCanonicalPath();
        } catch (IOException e){
            e.printStackTrace(); // Shouldn't happen
          }
        if (path == null)
          path = fileChooser.getSelectedFile().getAbsolutePath();

        if (path.indexOf(".") == -1) // User forgot to specify the pgn extension.
          path = path+".pgn";

        textfield.setText(path);
      }
    }

  }


}
