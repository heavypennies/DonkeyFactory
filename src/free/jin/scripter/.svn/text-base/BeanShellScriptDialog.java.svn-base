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
import free.util.TableLayout;
import free.util.AWTUtilities;
import free.util.IOUtilities;
import free.util.swing.PlainTextDialog;
import free.workarounds.FixedJTextArea;
import bsh.EvalError;


/**
 * A dialog allowing the user to create a new BeanShellScript.
 */

class BeanShellScriptDialog extends ScriptDialog{


  /**
   * The text of the beanshell code helpfile. Loaded lazily.
   */

  private static String codeHelpText = null;



  /**
   * The text area for the code.
   */

  private JTextArea codeArea;


  /**
   * Creates a new <code>BeanShellScriptDialog</code> with the specified parent
   * and <code>Script</code> whose properties are used as default values.
   */

  public BeanShellScriptDialog(Component parent, Scripter scripter, BeanShellScript templateScript){
    super(parent, "New BeanShell Script", scripter, templateScript);

    createUI();
  }



  /**
   * Creates the user interface for this dialog.
   */

  protected Container createScriptTypeSpecificUI(){
    BeanShellScript templateScript = (BeanShellScript)(this.templateScript);
    String defaultCode = (templateScript == null ? "" : templateScript.getCode());

    codeArea = new FixedJTextArea(10, 60);
    codeArea.setText(defaultCode);
    codeArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    codeArea.setTabSize(2);

    JScrollPane codeScrollPane = new JScrollPane(codeArea);
    codeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    codeScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    JLabel codeLabel = new JLabel("Code:");

    codeLabel.setDisplayedMnemonic('C');
    codeLabel.setLabelFor(codeArea);
    codeLabel.setAlignmentY(Component.TOP_ALIGNMENT);

    JButton codeHelp = new JButton("Help...");
    codeHelp.setMnemonic('l');
    codeHelp.setDefaultCapable(false);

    codeHelp.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        synchronized(BeanShellScriptDialog.class){
          if (codeHelpText == null){
            String resourceLocation = "help/beanshell.txt";
            try{
              codeHelpText = IOUtilities.loadText(BeanShellScriptDialog.class.getResource(resourceLocation));
            } catch (IOException e){
                JOptionPane.showMessageDialog(BeanShellScriptDialog.this, "Unable to load file: "+resourceLocation, "Error", JOptionPane.ERROR_MESSAGE);
                return;
              }
          }
        }
        PlainTextDialog textDialog = new PlainTextDialog(BeanShellScriptDialog.this, "BeanShell code help", codeHelpText);
        textDialog.setTextAreaFont(new Font("Monospaced", Font.PLAIN, 12));
        AWTUtilities.centerWindow(textDialog, BeanShellScriptDialog.this);
        textDialog.setVisible(true);
      }
    });

    JPanel panel = new JPanel(new TableLayout(3, 7, 5));

    panel.add(codeLabel);
    panel.add(codeScrollPane);
    panel.add(codeHelp);

    return panel;
  }




  /**
   * Creates and returns a BeanShellScript from the user specified information.
   */

  protected Script createScriptOnOk(String scriptName, String eventType, String [] selectedSubtypes){
    String code = codeArea.getText();

    try{
      BeanShellScript script = new BeanShellScript(scripter.getPluginContext(), scriptName, 
        eventType, selectedSubtypes, code);

      if (templateScript != null)
        script.setEnabled(templateScript.isEnabled());
      return script;
    } catch (EvalError e){
        JOptionPane.showMessageDialog(this, "Malformed code:\n" + e.getErrorText(), "Malformed Script", JOptionPane.ERROR_MESSAGE);
        return null;
      }
  }


}
