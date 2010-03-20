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
import javax.swing.table.*;
import bsh.Interpreter;
import bsh.EvalError;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import free.util.swing.SwingUtils;
import free.util.AWTUtilities;
import free.workarounds.FixedJComboBox;
import free.workarounds.FixedTableColumn;
import free.workarounds.FixedJTable;
import java.util.Vector;


/**
 * The menu item we use for user invoked scripts.
 */

class UserInvokedScriptMenuItem extends JMenuItem implements ActionListener{


  /**
   * The parent frame. This is needed because for some reason, menu items don't
   * have the correct ancestor - AWTUtilities.frameForComponent returns null.
   */

  private final Frame parent;

  

  /**
   * The script.
   */

  private final Script script;



  /**
   * The constructor, duh.
   */

  public UserInvokedScriptMenuItem(Script script, Frame parent){
    super(script.getName());

    this.parent = parent;
    this.script = script;

    setMnemonic(script.getName().charAt(0));
    addActionListener(this);
  }



  /**
   * Runs the script.
   */

  public void actionPerformed(ActionEvent evt){
    VariablesDialog varsDialog = new VariablesDialog(parent);
    AWTUtilities.centerWindow(varsDialog, parent);

    Object [][] vars = varsDialog.askVars();
    if (vars == null)
      return;

    script.run(null, null, vars);
  }



  /**
   * Returns the script.
   */

  public Script getScript(){
    return script;
  }



  /**
   * A dialog which allows the user to specify variables to the script.
   */

  private class VariablesDialog extends JDialog{


    /**
     * The available variable types.
     */

    private final String [] VAR_TYPES = new String[]{"String", "Integer", "Boolean", "Real"};


    
    /**
     * The result. A list of arrays of size two where the first element is a
     * String specifying the name of the variable and the 2nd one is the
     * variable value.
     */

    private Object [][] result;



    /**
     * The table model.
     */

    private final DefaultTableModel tableModel;



    /**
     * The column model.
     */

    private final DefaultTableColumnModel columnModel;



    /**
     * The "Run Script" button. We need this because we want to set the focus to
     * it.
     */

    private JButton runScriptButton;



    /**
     * The constructor.
     */

    public VariablesDialog(Frame parent){
      super(parent, "Specify Variables for the Script", true);

      SwingUtils.registerEscapeCloser(this);

      columnModel = new DefaultTableColumnModel();
      JComboBox typeChoice = new FixedJComboBox(VAR_TYPES);
      typeChoice.setEditable(false);
      TableColumn typeColumn = 
        new FixedTableColumn(0, 100, new DefaultTableCellRenderer(), new DefaultCellEditor(typeChoice));
      TableColumn nameColumn = new FixedTableColumn(1, 100);
      TableColumn valueColumn = new FixedTableColumn(2, 150);

      typeColumn.setHeaderValue("Type");
      nameColumn.setHeaderValue("Name");
      valueColumn.setHeaderValue("Value");

      columnModel.addColumn(typeColumn);
      columnModel.addColumn(nameColumn);
      columnModel.addColumn(valueColumn);

      tableModel = new DefaultTableModel(1, 3);
      tableModel.setValueAt(VAR_TYPES[0], 0, 0);

      createUI();
    }



    /**
     * Displays the dialog and returns the variables once the user is done.
     */

    public Object [][] askVars(){
      this.setVisible(true);

      return result;
    }



    /**
     * Creates the UI for this dialog.
     */

    private void createUI(){
      final JTable table = new FixedJTable(tableModel, columnModel);
      table.setCellSelectionEnabled(false);
      table.setColumnSelectionAllowed(false);
      table.setRowSelectionAllowed(true);
      table.setRowHeight(20);
      table.getTableHeader().setPreferredSize(new Dimension(350, 18));
      table.getTableHeader().setReorderingAllowed(false);

      JScrollPane scrollPane = new JScrollPane(table);
      scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      scrollPane.setPreferredSize(new Dimension(350, 120));

      JLabel variablesLabel = new JLabel("Variables");
      variablesLabel.setDisplayedMnemonic('V');
      variablesLabel.setLabelFor(table);

      JButton add = new JButton("Add Variable");
      final JButton remove = new JButton("Remove Variable");

      add.setMnemonic('A');
      remove.setMnemonic('R');

      add.setDefaultCapable(false);
      remove.setDefaultCapable(false);

      remove.setEnabled(false);

      runScriptButton = new JButton("Run Script");
      JButton cancelButton = new JButton("Cancel");

      ListSelectionModel selectionModel = table.getSelectionModel();
      selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      selectionModel.addListSelectionListener(new ListSelectionListener(){
        public void valueChanged(ListSelectionEvent evt){
          if (evt.getValueIsAdjusting())
            return;

          int selectedRow = table.getSelectedRow();
          if (selectedRow == -1)
            remove.setEnabled(false);
          else
            remove.setEnabled(true);
        }
      });

      add.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent evt){
          tableModel.addRow(new Object[]{VAR_TYPES[0], null, null});
        }
      });

      remove.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent evt){
          int selectedRow = table.getSelectedRow();
          if (selectedRow == -1)
            throw new IllegalStateException("Remove clicked while no row is selected");

          tableModel.removeRow(selectedRow);
          if (tableModel.getRowCount() <= selectedRow){
            if (tableModel.getRowCount() == 0)
              remove.setEnabled(false);
            table.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
          }
          else
            table.setRowSelectionInterval(selectedRow, selectedRow);
        }
      });

      runScriptButton.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent evt){
          if (table.isEditing()){
            // Makes sure that the editing stops so that table.getValueAt() returns up-to-date values.
            TableCellEditor editor = table.getCellEditor();
            editor.stopCellEditing();
          }

          int rowCount = tableModel.getRowCount();
          Vector varsVector = new Vector(rowCount);
          for (int i = 0; i < rowCount; i++){
            String type = (String)tableModel.getValueAt(i, 0);
            String name = (String)tableModel.getValueAt(i, 1);
            String valueString = (String)tableModel.getValueAt(i, 2);
            if ((name == null) || (name.length() == 0))
              continue;

            Object value;
            try{
              if ("Integer".equals(type))
                value = new Integer(valueString);
              else if ("String".equals(type))
                value = valueString;
              else if ("Boolean".equals(type))
                value = new Boolean(valueString);
              else if ("Real".equals(type))
                value = new Double(valueString);
              else throw new IllegalStateException("Unknown variable type: "+type);
            } catch (IllegalArgumentException e){
                JOptionPane.showMessageDialog(parent, "Inappropriate value specified for variable \""+name+"\", must be of type \""+type+"\"", "Bad Variable Value", JOptionPane.ERROR_MESSAGE);
                return;
              }
            varsVector.addElement(new Object[]{name, value});
          }

          Object [][] vars = new Object[varsVector.size()][];
          varsVector.copyInto(vars);

          result = vars;

          dispose();
        }
      });

      cancelButton.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent evt){
          dispose();
        }
      });

      JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
      buttonsPanel.add(add);
      buttonsPanel.add(remove);
      buttonsPanel.setMaximumSize(buttonsPanel.getPreferredSize());

      Box buttonsWrapper = Box.createVerticalBox();
      buttonsWrapper.add(Box.createVerticalGlue());
      buttonsWrapper.add(buttonsPanel);
      buttonsWrapper.add(Box.createVerticalGlue());

      JPanel finishButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      finishButtonsPanel.add(runScriptButton);
      finishButtonsPanel.add(cancelButton);

      JPanel content = new JPanel(new BorderLayout(10, 5));
      content.setBorder(new EmptyBorder(10, 10, 5, 10)); 
      content.add(variablesLabel, BorderLayout.NORTH);
      content.add(scrollPane, BorderLayout.CENTER);
      content.add(buttonsWrapper, BorderLayout.EAST);
      content.add(finishButtonsPanel, BorderLayout.SOUTH);

      setContentPane(content);

      this.getRootPane().setDefaultButton(runScriptButton);

      // So that the default button is activated on ENTER and the dialog closes on ESCAPE
      if (System.getProperty("java.version").compareTo("1.3") >= 0){
        Action defaultButtonAction = new AbstractAction(){
          public void actionPerformed(ActionEvent evt){
            runScriptButton.doClick();
          }
        };
        Action closeDialogAction = new AbstractAction(){
          public void actionPerformed(ActionEvent evt){
            dispose();
          }
        };
        try{
          Interpreter bsh = new Interpreter();
          bsh.set("table", table);
          bsh.set("defaultButtonAction", defaultButtonAction);
          bsh.set("closeDialogAction", closeDialogAction);
          bsh.eval("ActionMap actionMap = table.getActionMap();");
          bsh.eval("InputMap inputMap = table.getInputMap();");
          bsh.eval("actionMap.put(\"defaultButtonAction\", defaultButtonAction);");
          bsh.eval("inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), \"defaultButtonAction\");");
          bsh.eval("actionMap.put(\"closeDialogAction\", closeDialogAction);");
          bsh.eval("inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), \"closeDialogAction\");");
        } catch (EvalError e){
            e.printStackTrace();
          }
      }
      else{
        table.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        table.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
      }

    }


  }


}
