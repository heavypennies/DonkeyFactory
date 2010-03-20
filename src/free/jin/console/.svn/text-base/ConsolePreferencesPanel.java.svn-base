/**
 * Jin - a chess client for internet chess servers.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2002, 2003 Alexander Maryanovsky.
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

package free.jin.console;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.util.Properties;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.Enumeration;
import free.util.StringParser;
import free.util.StringEncoder;
import free.util.swing.ColorChooserButton;
import free.jin.plugin.PreferencesPanel;


/**
 * The preferences panel for the console manager.
 */

public class ConsolePreferencesPanel extends PreferencesPanel{



  /**
   * The ConsoleManager this panel shows preferences for.
   */

  protected final ConsoleManager consoleManager;




  /**
   * The properties we've modified as a result of user actions.
   */

  private final Properties modifiedProps;




  /**
   * A list of panels to be displayed. The items in the list are CategoryPanels.
   */

  private final Vector categoryPanels = new Vector();




  /**
   * The currently visible category panel.
   */

  private CategoryPanel currentCategoryPanel;





  /**
   * The selection color choosing button.
   */

  private ColorChooserButton selectionColorButton;




  /**
   * The selected text color choosing button.
   */

  private ColorChooserButton selectedColorButton;




  /**
   * The default settings panel.
   */

  private CategoryPanel defaultSettingsPanel;




  /**
   * A holder panel for the current category panel.
   */

  private final JPanel categoryPanelHolder = new JPanel(new BorderLayout());




  
  /**
   * Creates a new ConsolePreferencesPanel for the givenConsoleManager.
   */

  public ConsolePreferencesPanel(ConsoleManager consoleManager){
    this.consoleManager = consoleManager;

    modifiedProps = new Properties();

    createSettingsPanels();

    createLayout();
  }




  /**
   * Adds the specified CategoryPanel to the list of displayed panels.
   */

  protected void addCategoryPanel(CategoryPanel panel){
    categoryPanels.addElement(panel);

    TextStyleChooserPanel textStyleChooser = panel.getTextStyleChooser();
    if (textStyleChooser != null)
      textStyleChooser.addChangeListener(settingsChangeListener);
  }





  /**
   * Returns the value of the specified property. It first looks for such a
   * property in our local properties, which we may possibly have modified. If
   * there's no such property there, the plugin is asked for such a property.
   */

  protected String getProperty(String propertyName){
    String value = modifiedProps.getProperty(propertyName);
    return value == null ? consoleManager.getProperty(propertyName) : value; 
  }




  /**
   * Returns the value of the specified property in the same manner
   * <code>getProperty(String)</code> does, only if the returned value would be
   * <code>null</code>, returns the 2nd specified string instead.
   */

  protected String getProperty(String propertyName, String defaultValue){
    String propertyValue = getProperty(propertyName);
    return propertyValue == null ? defaultValue : propertyValue;
  }




  /**
   * Looks up the value of the specified property. The search is done
   * recursively, each time removing the prefix of the property name, delimited
   * by a '.' character.
   */

  protected String lookupProperty(String propertyName){
    String propertyValue = getProperty(propertyName);
    if (propertyValue == null){
      int dotIndex = propertyName.lastIndexOf(".");
      if (dotIndex == -1)
        return null;
      return lookupProperty(propertyName.substring(0,dotIndex));
    }
    else
      return propertyValue;
  }




  /**
   * A flag we set to true so we know not to handle echo change events.
   */

  private boolean handlingChangeEvent = false;




  /**
   * The listener that listens to settings changes in the various panels
   * and updates the properties accordingly. You may register this
   * listener as the change listener of any custom components you're adding.
   * Note that if you do that, you should also override
   * <code>updatePropertiesFrom</code> and <code>updatePanels</code> to apply
   * changes and update your components respectively.
   */

  protected final ChangeListener settingsChangeListener = new ChangeListener(){
    public void stateChanged(ChangeEvent evt){
      if (handlingChangeEvent)
        return;

      handlingChangeEvent = true;

      updatePropertiesFrom(currentCategoryPanel);
      updatePanels();
      fireStateChanged();

      handlingChangeEvent = false;
    }
  };





  /**
   * Updates the properties from the settings of the specified CategoryPanel.
   * This method is called whenever the settings on the current CategoryPanel
   * change.
   */

  protected void updatePropertiesFrom(CategoryPanel categoryPanel){
    TextStyleChooserPanel textStyleChooser = categoryPanel.getTextStyleChooser();

    if (categoryPanel == defaultSettingsPanel){
      String newSelectionColor = StringEncoder.encodeColor(selectionColorButton.getColor());
      modifiedProps.put("output-selection", newSelectionColor);

      String newSelectedColor = StringEncoder.encodeColor(selectedColorButton.getColor());
      modifiedProps.put("output-selected", newSelectedColor);

      Color background = textStyleChooser.getSelectedBackground();
      modifiedProps.put("background", StringEncoder.encodeColor(background));

      if (textStyleChooser.isAntialiasingSelectionEnabled()){
        boolean antialias = defaultSettingsPanel.getTextStyleChooser().isAntialias();
        modifiedProps.put("output-text.antialias", String.valueOf(antialias));
      }
    }

    Font font = textStyleChooser.getSelectedFont();
    Color foreground = textStyleChooser.getSelectedForeground();

    String [] categoriesToUpdate = categoryPanel.getCategories();
    for (int i = 0; i < categoriesToUpdate.length; i++){
      String category = categoriesToUpdate[i];

      setProperty(category, "font-family", font.getFamily());
      setProperty(category, "font-size", String.valueOf(font.getSize()));
      setProperty(category, "font-bold", String.valueOf(font.isBold()));
      setProperty(category, "font-italic", String.valueOf(font.isItalic()));
      setProperty(category, "foreground", StringEncoder.encodeColor(foreground));
    }
  }




  /**
   * Updates all the CategoryPanels and their TextStyleChooserPanels from the
   * properties. This method is called whenever the properties change which may
   * require updating some of the category panels.
   */

  protected void updatePanels(){
    selectionColorButton.setColor(StringParser.parseColor(getProperty("output-selection")));
    selectedColorButton.setColor(StringParser.parseColor(getProperty("output-selected")));

    Color background = StringParser.parseColor(getProperty("background"));
    boolean antialias = new Boolean(getProperty("output-text.antialias")).booleanValue();

    for (int i = 0; i < categoryPanels.size(); i++){
      CategoryPanel panel = (CategoryPanel)categoryPanels.elementAt(i);
      TextStyleChooserPanel textStyleChooser = panel.getTextStyleChooser();
      String mainCategory = panel.getMainCategory();

      Font font = getCategoryFont(mainCategory);
      Color foreground = StringParser.parseColor(lookupProperty("foreground."+mainCategory));

      textStyleChooser.setSelectedFont(font);
      textStyleChooser.setSelectedForeground(foreground);
      textStyleChooser.setSelectedBackground(background);
      textStyleChooser.setAntialias(antialias);
    }
  }






  /**
   * Creates all the settings panels.
   */

  protected void createSettingsPanels(){
    createDefaultSettingsPanel();
    createSettingsPanelsFromProperties();
  }





  /**
   * Creates the default settings panel.
   */

  private void createDefaultSettingsPanel(){
    Font font = getCategoryFont("");
    Color foreground = StringParser.parseColor(getProperty("foreground"));
    Color background = StringParser.parseColor(getProperty("background"));

    boolean antialiasingSupported;
    try{
      antialiasingSupported = Class.forName("java.awt.Graphics2D") != null;
    } catch (ClassNotFoundException e){
        antialiasingSupported = false;
      }
    boolean antialiasingValue = new Boolean(getProperty("output-text.antialias")).booleanValue();

    TextStyleChooserPanel defaultSettingsChooserPanel = 
      new TextStyleChooserPanel(font, foreground, background, antialiasingValue, true, antialiasingSupported);

    selectionColorButton = createSelectionColorButton();
    selectedColorButton = createSelectedColorButton();

    defaultSettingsPanel = new CategoryPanel("Default Settings", defaultSettingsChooserPanel, new String[]{""});
    defaultSettingsPanel.setLayout(new BorderLayout(5, 5));
    defaultSettingsPanel.add(defaultSettingsChooserPanel, BorderLayout.CENTER);
    JPanel selectionColorPanel = new JPanel(new GridLayout(1, 2, 5, 5));
    selectionColorPanel.add(selectionColorButton);
    selectionColorPanel.add(selectedColorButton);
    defaultSettingsPanel.add(selectionColorPanel, BorderLayout.SOUTH);

    addCategoryPanel(defaultSettingsPanel);
    selectionColorButton.addChangeListener(settingsChangeListener);
    selectedColorButton.addChangeListener(settingsChangeListener);
  }





  /**
   * Creates settings panels as specified in the properties.
   */

  private void createSettingsPanelsFromProperties(){
    Color background = StringParser.parseColor(getProperty("background"));
    boolean antialiasingValue = new Boolean(getProperty("output-text.antialias")).booleanValue();

    int categoriesCount = Integer.parseInt(getProperty("preferences.categories.count", "0"));

    for (int i = 0; i < categoriesCount; i++){
      CategoryPanel categoryPanel;

      boolean isCustomPanel = new Boolean(getProperty("preferences.categories."+i+".custom")).booleanValue();

      if (isCustomPanel){
        String id = getProperty("preferences.categories."+i+".id");
        categoryPanel = createCustomCategoryPanel(id);
      }
      else{
        String categoryName = getProperty("preferences.categories."+i+".name");

        StringTokenizer categoriesTokenizer = new StringTokenizer(getProperty("preferences.categories."+i+".ids"), ";");
        String [] categories = new String[categoriesTokenizer.countTokens()];
        for (int categoryIndex = 0; categoryIndex < categories.length; categoryIndex++)
          categories[categoryIndex] = categoriesTokenizer.nextToken();

        String mainCategory = categories[0];

        Font font = getCategoryFont(mainCategory);
        Color foreground = StringParser.parseColor(lookupProperty("foreground."+mainCategory));
          
        TextStyleChooserPanel textStyleChooserPanel = new TextStyleChooserPanel(font, foreground, background, antialiasingValue, false, false);
        categoryPanel = new CategoryPanel(categoryName, textStyleChooserPanel, categories);
        categoryPanel.setLayout(new BorderLayout());
        categoryPanel.add(textStyleChooserPanel, BorderLayout.CENTER);
      }

      addCategoryPanel(categoryPanel);
    }


  }




  /**
   * <P>Creates a custom CategoryPanel with the specified id. This is used to
   * enable subclasses to create their own, custom, server specific (or
   * otherwise specific) panels. In order to add such a panel, mark the panel
   * as custom (in plugin properties) and give it an ID. Then override this
   * method in the subclass and check the ID when invoked. When the given ID 
   * matches the one you specified in the properties, return your custom
   * <code>CategoryPanel</code>. If the ID does not match, simply invoke the
   * superclass' method and return whatever it returns.
   */

  protected CategoryPanel createCustomCategoryPanel(String id){
    return null;
  }





  /**
   * Creates and returns the <code>ColorChooserButton</code> used for choosing
   * the color of the selection.
   */

  protected ColorChooserButton createSelectionColorButton(){
    Color selectionColor = StringParser.parseColor(getProperty("output-selection"));
    ColorChooserButton button = new ColorChooserButton("Selection", selectionColor);
    button.setMnemonic('l');

    return button;
  }




  /**
   * Creates and returns the <code>ColorChooserButton</code> used for choosing
   * the color of selected text.
   */

  protected ColorChooserButton createSelectedColorButton(){
    Color selectedColor = StringParser.parseColor(getProperty("output-selected"));
    ColorChooserButton button = new ColorChooserButton("Selected Text", selectedColor);
    button.setMnemonic('e');

    return button;
  }





  /**
   * Lays out all the components as necessary.
   */

  protected void createLayout(){
    setLayout(new BorderLayout(10, 10));

    final JList categoryList = new JList(categoryPanels);
    categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane scrollPane = new JScrollPane(categoryList);

    JPanel listPanel = new JPanel(new BorderLayout(2, 2));
    JLabel textTypeLabel = new JLabel("Text type", JLabel.CENTER);
    textTypeLabel.setDisplayedMnemonic('t');
    textTypeLabel.setLabelFor(categoryList);
    listPanel.add(textTypeLabel, BorderLayout.NORTH);
    listPanel.add(scrollPane, BorderLayout.CENTER);

    categoryList.addListSelectionListener(new ListSelectionListener(){
      public void valueChanged(ListSelectionEvent evt){
        CategoryPanel selectedValue = (CategoryPanel)categoryList.getSelectedValue();
        setCurrentPanel(selectedValue);
      }
    });

    add(listPanel, BorderLayout.WEST);
    add(categoryPanelHolder, BorderLayout.CENTER);

    // The call propagates to the registered selection listener which sets the current panel properly
    categoryList.setSelectedIndex(0);
  }




  /**
   * Sets the currently displayed panel to the specified CategoryPanel.
   */

  private void setCurrentPanel(CategoryPanel panel){
    categoryPanelHolder.removeAll();
    currentCategoryPanel = panel;
    if (currentCategoryPanel != null){
      categoryPanelHolder.add(currentCategoryPanel, BorderLayout.CENTER);
      invalidate();
      validate();
      repaint(); // sigh
    }
  }

  


  
  /**
   * Refreshes the given TextStyleChooserPanel's settings from the properties of
   * the given category.
   */

  private void updatePanel(TextStyleChooserPanel chooserPanel, String categoryName, Color background){
    String fontFamily = lookupProperty("font-family."+categoryName);
    int fontSize = Integer.parseInt(lookupProperty("font-size."+categoryName));
    int fontStyle = 0;
    if (new Boolean(lookupProperty("font-bold."+categoryName)).booleanValue())
      fontStyle |= Font.BOLD;
    if (new Boolean(lookupProperty("font-italic."+categoryName)).booleanValue())
      fontStyle |= Font.ITALIC;
    Color foreground = StringParser.parseColor(lookupProperty("foreground."+categoryName));

    Font font = new Font(fontFamily, fontStyle, fontSize);

    chooserPanel.setSelectedFont(font);
    chooserPanel.setSelectedBackground(background);
    chooserPanel.setSelectedForeground(foreground);
  }




  /**
   * Applies the changes done by the user.
   */

  public void applyChanges(){
    Enumeration modifiedPropsEnum = modifiedProps.keys();
    while (modifiedPropsEnum.hasMoreElements()){
      String propertyName = (String)modifiedPropsEnum.nextElement();
      String propertyValue = (String)modifiedProps.get(propertyName);
      if (!consoleManager.lookupProperty(propertyName).equals(propertyValue))
        consoleManager.setProperty(propertyName, propertyValue);
    }

    String newSelectionColor = StringEncoder.encodeColor(selectionColorButton.getColor());
    if (!newSelectionColor.equals(consoleManager.lookupProperty("output-selection")))
      consoleManager.setProperty("output-selection", newSelectionColor);

    String newSelectedColor = StringEncoder.encodeColor(selectedColorButton.getColor());
    if (!newSelectedColor.equals(consoleManager.lookupProperty("output-selected")))
      consoleManager.setProperty("output-selected", newSelectedColor);

    consoleManager.refreshFromProperties();
  }





  /**
   * Returns the font used for the specified category.
   */

  protected Font getCategoryFont(String categoryName){
    String fontFamily = lookupProperty("font-family."+categoryName);
    int fontSize = Integer.parseInt(lookupProperty("font-size."+categoryName));
    int fontStyle = 0;
    if (new Boolean(lookupProperty("font-bold."+categoryName)).booleanValue())
      fontStyle |= Font.BOLD;
    if (new Boolean(lookupProperty("font-italic."+categoryName)).booleanValue())
      fontStyle |= Font.ITALIC;

    return new Font(fontFamily, fontStyle, fontSize);
  }




  /**
   * Sets the value of the given property type in the given category to the
   * specified value. If the property is marked as unmodifiable, the call is
   * silently ignored.
   */

  protected void setProperty(String categoryName, String propertyType, String propertyValue){
    String propertyName = (categoryName.equals("") ? propertyType : propertyType+"."+categoryName);
    if (new Boolean(getProperty(propertyName+".unmodifiable", "false")).booleanValue())
      return;

    modifiedProps.put(propertyName, propertyValue);
  }




  /**
   * An extension of JPanel which also holds category information.
   */

  protected static class CategoryPanel extends JPanel{
    

    /**
     * The long name of the category.
     */

    private final String categoryName;



    /**
     * The TextStyleChooserPanel in this panel.
     */

    private final TextStyleChooserPanel textStyleChooser;



    /**
     * The IDs (short names) of categories that need to be updated when the
     * settings of the text style chooser change.
     */

    private final String [] categories;



    /**
     * Creates a new CategoryPanel with the specified long category name,
     * the TextStyleChooserPanel in this panel and a list of category IDs (short
     * names) that need to be modified when the TextStyleChooserPanel's settings
     * change. The "main" category, which is the category used to display the
     * settings is the first element (at index zero) in the categories array.
     */

    public CategoryPanel(String categoryName, TextStyleChooserPanel textStyleChooser, String [] categories){
      this.categoryName = categoryName;
      this.textStyleChooser = textStyleChooser;
      this.categories = categories;
    }




    /**
     * Returns the TetxStyleChooser in this panel.
     */

    public TextStyleChooserPanel getTextStyleChooser(){
      return textStyleChooser;
    }




    /**
     * Returns the IDs (short names) of categories that need to be updated when
     * the text style chooser's settings change.
     */

    public String [] getCategories(){
      return (String [])categories.clone();
    }




    /**
     * Returns the main category. This is the category used to visualize the
     * setting.
     */

    public String getMainCategory(){
      // If you update this, check ChannelConsolePreferencesPanel.createChannelsCategoryPanel() as well
      // as it assumes the main category is the first element as well.
      return getCategories()[0];
    }




    /**
     * Returns the name specified in the constructor.
     */

    public String toString(){
      return categoryName;
    }

  }


}
