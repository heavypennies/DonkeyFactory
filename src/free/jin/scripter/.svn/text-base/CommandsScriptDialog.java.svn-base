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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.StringTokenizer;
import free.util.TableLayout;
import free.util.IOUtilities;
import free.util.swing.PlainTextDialog;
import free.util.AWTUtilities;
import free.workarounds.FixedJTextField;
import free.workarounds.FixedJTextArea;
import bsh.EvalError;
import bsh.Interpreter;


/**
 * A dialog allowing the user to create a new CommandScript.
 */

class CommandsScriptDialog extends ScriptDialog{


  /**
   * The text of the condition helpfile. Loaded lazily.
   */

  private static String conditionHelpText = null;



  /**
   * The text of the commands helpfile. Loaded lazily.
   */

  private static String commandsHelpText = null;



  /**
   * The text field for the condition.
   */

  private JTextField conditionField;



  /**
   * The text area for the commands.
   */

  private JTextArea commandsArea;


  /**
   * Creates a new <code>CommandsScriptDialog</code> with the specified parent
   * and <code>Script</code> whose properties are used as default values.
   */

  public CommandsScriptDialog(Component parent, Scripter scripter, CommandScript templateScript){
    super(parent, "New Commands Script", scripter, templateScript);

    createUI();
  }



  /**
   * Creates the user interface for this dialog.
   */

  protected Container createScriptTypeSpecificUI(){
    CommandScript templateScript = (CommandScript)(this.templateScript);

    String defaultCondition = (templateScript == null ? "" : templateScript.getCondition());
    String [] defaultCommands = (templateScript == null ? new String[0] : templateScript.getCommands());

    conditionField = new FixedJTextField(defaultCondition);
    conditionField.setFont(new Font("Monospaced", Font.PLAIN, 12));

    commandsArea = new FixedJTextArea(5, 40);
    commandsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    for (int i = 0; i < defaultCommands.length; i++)
      commandsArea.append(defaultCommands[i]+"\n");

    JScrollPane commandsScrollPane = new JScrollPane(commandsArea);
    commandsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    commandsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    JLabel conditionLabel = new JLabel("Condition:");
    JLabel commandsLabel = new JLabel("Commands:");

    conditionLabel.setDisplayedMnemonic('C');
    commandsLabel.setDisplayedMnemonic('m');

    conditionLabel.setLabelFor(conditionField);
    commandsLabel.setLabelFor(commandsArea);

    commandsLabel.setAlignmentY(Component.TOP_ALIGNMENT);

    JButton conditionHelp = new JButton("Help...");
    JButton commandsHelp = new JButton("Help...");

    conditionHelp.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        synchronized(CommandsScriptDialog.class){
          if (conditionHelpText == null){
            String resourceLocation = "help/condition.txt";
            try{
              conditionHelpText = IOUtilities.loadText(CommandsScriptDialog.class.getResource(resourceLocation));
            } catch (IOException e){
                JOptionPane.showMessageDialog(CommandsScriptDialog.this, "Unable to load file: "+resourceLocation, "Error", JOptionPane.ERROR_MESSAGE);
                return;
              }
          }
        }
        PlainTextDialog textDialog = new PlainTextDialog(CommandsScriptDialog.this, "Event type help", conditionHelpText);
        textDialog.setTextAreaFont(new Font("Monospaced", Font.PLAIN, 12));
        AWTUtilities.centerWindow(textDialog, CommandsScriptDialog.this);
        textDialog.setVisible(true);
      }
    });

    commandsHelp.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        synchronized(CommandsScriptDialog.class){
          if (commandsHelpText == null){
            String resourceLocation = "help/commands.txt";
            try{
              commandsHelpText = IOUtilities.loadText(CommandsScriptDialog.class.getResource(resourceLocation));
            } catch (IOException e){
                JOptionPane.showMessageDialog(CommandsScriptDialog.this, "Unable to load file: "+resourceLocation, "Error", JOptionPane.ERROR_MESSAGE);
                return;
              }
          }
        }
        PlainTextDialog textDialog = new PlainTextDialog(CommandsScriptDialog.this, "Event type help", commandsHelpText);
        textDialog.setTextAreaFont(new Font("Monospaced", Font.PLAIN, 12));
        AWTUtilities.centerWindow(textDialog, CommandsScriptDialog.this);
        textDialog.setVisible(true);
      }
    });


    conditionHelp.setMnemonic('l');
    commandsHelp.setMnemonic('p');

    conditionHelp.setDefaultCapable(false);
    commandsHelp.setDefaultCapable(false);

    JPanel panel = new JPanel(new TableLayout(3, 7, 5));

    panel.add(conditionLabel);
    panel.add(conditionField);
    panel.add(conditionHelp);

    panel.add(commandsLabel);
    panel.add(commandsScrollPane);
    panel.add(commandsHelp);

    return panel;
  }




  /**
   * Creates and returns a CommandScript from the user specified information.
   */

  protected Script createScriptOnOk(String scriptName, String eventType, String [] selectedSubtypes){
    String condition = conditionField.getText();

    String text = commandsArea.getText();

    StringTokenizer tokenizer = new StringTokenizer(text, "\n");
    String [] commands = new String[tokenizer.countTokens()];
    for (int i = 0; tokenizer.hasMoreTokens(); i++)
      commands[i] = tokenizer.nextToken();

    if ((condition == null) || (condition.length() == 0))
      condition = "true";

    try{
      Object [][] vars = scripter.getAvailableVariables(eventType, selectedSubtypes);
      Interpreter bsh = new Interpreter();
      if (vars != null){
        for (int i = 0; i < vars.length; i++){
          Object [] var = vars[i];
          String name = (String)var[0];
          Object value = var[1];
          bsh.set(name, value);
        }
      }
      Object val = bsh.eval(condition);
      if (!(val instanceof Boolean))
        throw new EvalError("Not a boolean expression");
    } catch (EvalError e){
        int result = JOptionPane.showConfirmDialog(this, "The specified condition seems to be malformed.\nThe error is:\n\"" + format(e.getMessage())+"\"\n\nUse the condition anyway?", "Malformed Script", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
        if (result != JOptionPane.YES_OPTION)
          return null;
      }

    try{
      CommandScript script = new CommandScript(scripter.getPluginContext(), scriptName, eventType,
        selectedSubtypes, condition, commands);

      if (templateScript != null)
        script.setEnabled(templateScript.isEnabled());
      return script;
    } catch (EvalError e){
        JOptionPane.showMessageDialog(this, "Malformed condition or commands:\n" + e.getErrorText(), "Malformed Script", JOptionPane.ERROR_MESSAGE);
        return null;
      }
  }



  /**
   * Formats the specified string inserting newlines so that it can be displayed
   * in a dialog.
   */

  private static String format(String s){
    final int WRAP_BARRIER = 80;
    StringTokenizer tokenizer = new StringTokenizer(s, " ");
    StringBuffer buf = new StringBuffer(s.length()+2*s.length()/WRAP_BARRIER);

    int lineSize = 0;
    while (tokenizer.hasMoreTokens()){
      String token = tokenizer.nextToken();
      if (token.length() + lineSize > WRAP_BARRIER){
        if (lineSize != 0)
          buf.append("\n");
        buf.append(token);
        lineSize = token.length();
      }
      else{
        buf.append(" ");
        buf.append(token);
        lineSize += token.length() + 1;
      }
    }

    return buf.toString();
  }


}
