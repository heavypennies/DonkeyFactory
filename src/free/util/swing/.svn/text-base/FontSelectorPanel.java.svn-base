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

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.Hashtable;
import free.util.AWTUtilities;


/**
 * A panel allowing the user to select a font from a given list of fonts.
 */

public class FontSelectorPanel extends JPanel{



  /**
   * The textfield showing the currently selected font name.
   */

  private final JTextField fontNameField = new free.workarounds.FixedJTextField();




  /**
   * The list displaying possible font names.
   */

  private final JList fontNamesList;




  /**
   * The textfield showing the currently selected font size.
   */

  private final JTextField fontSizeField = new free.workarounds.FixedJTextField(new IntegerStrictPlainDocument(1, 72), "", 2);




  /**
   * The list displaying possible font sizes.
   */

  private final JList fontSizesList = new JList(new String[]{"5", "6", "7", "8", "9",
    "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "36", "48", "72"});




  /**
   * A Hashtable mapping <code>BooleanFontOption</code> names to
   * <code>BooleanFontOptions</code>.
   */

  private Hashtable fontOptions = new Hashtable();




  /**
   * A Hashtable mapping <code>BooleanFontOptions</code> to JCheckBoxes
   * representing them.
   */

  private Hashtable fontOptionCheckBoxes = new Hashtable();





  /**
   * The preview panel.
   */

  private JComponent previewPanel;





  /**
   * The preview panel holder;
   */

  private final JPanel previewPanelHolder;





  /**
   * The sole ChangeEvent we need.
   */

  private final ChangeEvent changeEvent = new ChangeEvent(this);






  /**
   * Creates a new <code>FontSelectorPanel</code> which will allow choosing from
   * the list of fonts available to the default Toolkit.
   */

  public FontSelectorPanel(){
    this(AWTUtilities.getAvailableFontNames(), new BooleanFontOption[]{
      createBoldFontOption(),
      createItalicFontOption()
    });
  }




  /**
   * Creates a new </code>FontSelectorPanel</code> which will allow choosing
   * from the list of fonts available to the default Toolkit and will allow the
   * user to select from the specified BooleanFontOptions.
   */

  public FontSelectorPanel(BooleanFontOption [] fontOptions){
    this(AWTUtilities.getAvailableFontNames(), fontOptions);
  }




  /**
   * Creates a new </code>FontSelectorPanel</code> which will allow choosing
   * from the list of specified fonts.
   */

  public FontSelectorPanel(String [] fontNames){
    this(fontNames, new BooleanFontOption[]{
      createBoldFontOption(),
      createItalicFontOption()
    });
  }


  

  /**
   * Creates a new FontSelectorPanel which will allow choosing from the
   * specified list of fonts.
   */

  public FontSelectorPanel(String [] fontNames, BooleanFontOption [] fontOptions){
    fontNamesList = new JList(fontNames);
    fontNamesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    fontSizesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    for (int i = 0; i < fontOptions.length; i++){
      BooleanFontOption fontOption = fontOptions[i];
      this.fontOptions.put(fontOption.getOptionName(), fontOption);
      fontOption.setFontSelectorPanel(this);
    }

    previewPanelHolder = new JPanel(new BorderLayout());
    Border outsideBorder = new EtchedBorder(javax.swing.border.EtchedBorder.LOWERED);
    Border insideBorder = new EmptyBorder(10, 10, 10, 10);
    previewPanelHolder.setBorder(new CompoundBorder(outsideBorder, insideBorder));

    setPreviewPanel(new DefaultPreviewPanel(this));

    createUI(fontOptions);

    fontNamesList.addListSelectionListener(new ListSelectionListener(){
      public void valueChanged(ListSelectionEvent evt){
        String selectedItem = (String)fontNamesList.getSelectedValue();
        fontNameField.setText(selectedItem);

        fireStateChanged();
      }
    });

    fontSizesList.addListSelectionListener(new ListSelectionListener(){
      public void valueChanged(ListSelectionEvent evt){
        String selectedItem = (String)fontSizesList.getSelectedValue();
        fontSizeField.setText(selectedItem);

        fireStateChanged();
      }
    });

  }




  /**
   * Returns the <code>BooleanFontOption</code> with the specified name, or
   * <code>null</code> if no such <code>BooleanFontOption</code> exists.
   */

  public BooleanFontOption getFontOption(String optionName){
    return (BooleanFontOption)fontOptions.get(optionName);
  }




  /**
   * Returns the value of the <code>BooleanFontOption</code> with the specified
   * name.
   *
   * @throws IllegalArgumentException if no <code>BooleanFontOption</code> with
   * the specified name exists.
   */

  public boolean getFontOptionValue(String optionName){
    BooleanFontOption fontOption = getFontOption(optionName);
    if (fontOption == null)
      throw new IllegalArgumentException();
    return fontOption.getValue();
  }




  /**
   * Creates a <code>BooleanFontOption</code> for specifying the whether the
   * font is bold or not.
   */

  public static BooleanFontOption createBoldFontOption(){
    return new BooleanFontOption("Bold", 'B', false);
  }




  /**
   * Creates a <code>BooleanFontOption</code> for specifying the whether the
   * font is italic or not.
   */

  public static BooleanFontOption createItalicFontOption(){
    return new BooleanFontOption("Italic", 'I', false);
  }




  /**
   * Creates the UI.
   */

  private void createUI(BooleanFontOption [] fontOptions){
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    JPanel topPanel = new JPanel(new BorderLayout(5, 5));

    JPanel fontNamePanel = new JPanel(new BorderLayout());
    JScrollPane fontNamesListScrollPane = new JScrollPane(fontNamesList);
    JPanel fontNameLabelAndField = new JPanel(new BorderLayout(2, 2));
    JLabel fontNameLabel = new JLabel("Font name", JLabel.CENTER);
    fontNameLabel.setDisplayedMnemonic('n');
    fontNameLabel.setLabelFor(fontNameField);
    fontNameLabelAndField.add(fontNameLabel, BorderLayout.NORTH);
    fontNameLabelAndField.add(fontNameField, BorderLayout.SOUTH);
    fontNamePanel.add(fontNameLabelAndField, BorderLayout.NORTH);
    fontNamePanel.add(fontNamesListScrollPane, BorderLayout.CENTER);

    JPanel fontSizePanel = new JPanel(new BorderLayout());
    JScrollPane fontSizesListScrollPane = new JScrollPane(fontSizesList);
    JPanel fontSizeLabelAndField = new JPanel(new BorderLayout(2, 2));
    JLabel fontSizeLabel = new JLabel("Font size", JLabel.CENTER);
    fontSizeLabel.setDisplayedMnemonic('s');
    fontSizeLabel.setLabelFor(fontSizeField);
    fontSizeLabelAndField.add(fontSizeLabel, BorderLayout.NORTH);
    fontSizeLabelAndField.add(fontSizeField, BorderLayout.SOUTH);
    fontSizePanel.add(fontSizeLabelAndField, BorderLayout.NORTH);
    fontSizePanel.add(fontSizesListScrollPane, BorderLayout.CENTER);

    topPanel.add(fontNamePanel, BorderLayout.CENTER);
    topPanel.add(fontSizePanel, BorderLayout.EAST);

    ChangeListener checkBoxChangeListener = new ChangeListener(){
      public void stateChanged(ChangeEvent evt){
        AbstractButton checkbox = (AbstractButton)evt.getSource();
        String name = checkbox.getActionCommand();
        BooleanFontOption fontOption = (BooleanFontOption)FontSelectorPanel.this.fontOptions.get(name);
        fontOption.setValue(checkbox.isSelected());
      }
    };

    JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));

    Box checkBoxesPanel = Box.createVerticalBox();

    for (int i = 0; i < fontOptions.length; i++){
      BooleanFontOption fontOption = fontOptions[i];
      JCheckBox checkbox = new JCheckBox(fontOption.getOptionName(), fontOption.getValue());
      checkbox.setMnemonic(fontOption.getMnemonic());
      checkbox.addChangeListener(checkBoxChangeListener);
      checkBoxesPanel.add(checkbox);
      fontOptionCheckBoxes.put(fontOption, checkbox);
    }

    bottomPanel.add(checkBoxesPanel, BorderLayout.WEST);
    bottomPanel.add(previewPanelHolder, BorderLayout.CENTER);

    add(topPanel);
    add(Box.createVerticalStrut(10));
    add(bottomPanel);
  }




  /**
   * Returns the currently selected font, or null if none.
   */

  public Font getSelectedFont(){
    String fontName = fontNameField.getText();
    String fontSizeString = fontSizeField.getText();

    if ((fontName == null) || fontName.equals(""))
      return null;

    if ((fontSizeString == null) || fontSizeString.equals(""))
      return null;

    int style = 0;
    BooleanFontOption boldOption = getFontOption("Bold");
    BooleanFontOption italicOption = getFontOption("Italic");
    if ((boldOption != null) && boldOption.getValue())
      style |= Font.BOLD;
    if ((italicOption != null) && italicOption.getValue())
      style |= Font.ITALIC;

    return new Font(fontName, style, Integer.parseInt(fontSizeString));
  }




  /**
   * Sets the currently selected font.
   */

  public void setSelectedFont(Font font){
    fontNameField.setText(font.getFamily());
    fontSizeField.setText(String.valueOf(font.getSize()));

    BooleanFontOption boldOption = getFontOption("Bold");
    BooleanFontOption italicOption = getFontOption("Italic");

    if (boldOption != null)
      boldOption.setValue(font.isBold());
    if (italicOption != null)
      italicOption.setValue(font.isItalic());

    fontNamesList.setSelectedValue(font.getFamily(), true);
    fontSizesList.setSelectedValue(String.valueOf(font.getSize()), true);

    fireStateChanged();
  }




  /**
   * This method is called by <code>BooleanFontOptions</code> when their value
   * changes.
   */

  protected void booleanOptionChanged(BooleanFontOption option){
    AbstractButton checkbox = (AbstractButton)fontOptionCheckBoxes.get(option);
    checkbox.setSelected(option.getValue());
    fireStateChanged();
  }




  /**
   * Adds a ChangeListener to the list of listeners receiving notifications when
   * the selected font changes.
   */

  public void addChangeListener(ChangeListener listener){
    listenerList.add(ChangeListener.class, listener);
  }




  /**
   * Removes the given Changelistener from the list of listeners receiving
   * notifications when the selected font changes.
   */

  public void removeChangeListener(ChangeListener listener){
    listenerList.remove(ChangeListener.class, listener);
  }




  /**
   * Fires a ChangeEvent to all interested listeners.
   */

  protected void fireStateChanged(){
    Object [] listeners = listenerList.getListenerList();
    for (int i = 0; i < listeners.length; i += 2){
      if (listeners[i] == ChangeListener.class){
        ChangeListener listener = (ChangeListener)listeners[i+1];
        listener.stateChanged(changeEvent);
      }
    }
  }





  /**
   * Sets the preview panel.
   */

  public void setPreviewPanel(JComponent component){
    if (previewPanel != null)
      previewPanelHolder.remove(previewPanel);

    previewPanel = component;
    if (previewPanel != null)
      previewPanelHolder.add(previewPanel, BorderLayout.CENTER);
  }




  /**
   * Returns the preview panel.
   */

  public JComponent getPreviewPanel(){
    return previewPanel;
  }




  /**
   * The default preview panel.
   */

  public static class DefaultPreviewPanel extends JLabel implements ChangeListener{



    /**
     * The FontSelectorPanel we're a part of.
     */

    protected final FontSelectorPanel fontSelectorPanel;




    /**
     * Creates a new DefaultPreviewPanel with the specified FontSelectorPanel
     * as the user. 
     */

    public DefaultPreviewPanel(FontSelectorPanel fontSelectorPanel){
      super("AaBbYyZz123", JLabel.CENTER);

      this.fontSelectorPanel = fontSelectorPanel;

      fontSelectorPanel.addChangeListener(this);

      setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }




    /**
     * ChangeListener implementation.
     */

    public void stateChanged(ChangeEvent evt){
      setFont(fontSelectorPanel.getSelectedFont());
      repaint();
    }

  }




  /**
   * A class allowing to specify a boolean option for the font, such as "Bold"
   * or "Italic".
   */

  public static class BooleanFontOption{



    /**
     * The name of the option, something like "Bold", or "Italic".
     */

    private final String name;



    /**
     * The character that should be used for the mnemonic of the checkbox
     * displaying the choice for this option.
     */

    private final char mnemonic;



    /**
     * The current boolean value.
     */

    private boolean value;




    /**
     * The FontSelectorPanel using us.
     */

    private FontSelectorPanel fontSelectorPanel;




    /**
     * Creates a new BooleanFontOption with the specified name, mnemonic and
     * initial value.
     */

    public BooleanFontOption(String name, char mnemonic, boolean initValue){
      this.name = name;
      this.mnemonic = mnemonic;
      this.value = initValue;
    }




    /**
     * Sets the <code>FontSelectorPanel</code> using this
     * <code>BooleanFontOption</code>.
     */

    private void setFontSelectorPanel(FontSelectorPanel fontSelectorPanel){
      this.fontSelectorPanel = fontSelectorPanel;
    }




    /**
     * Returns the name of the <code>BooleanFontOption</code>.
     */

    public String getOptionName(){
      return name;
    }




    /**
     * Returns the mnemonic used for the checkbox of this option.
     */

    public char getMnemonic(){
      return mnemonic;
    }




    /**
     * Returns the current value of the option.
     */

    public boolean getValue(){
      return value;
    }




    /**
     * Sets the current value of the option and notifies the user
     * <code>FontSelectorPanel</code>.
     */

    public void setValue(boolean value){
      if (this.value != value){
        this.value = value;
        fontSelectorPanel.booleanOptionChanged(this);
      }
    }

  }


}
