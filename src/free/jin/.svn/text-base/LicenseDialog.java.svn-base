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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import free.util.*;
import free.util.swing.LinkLabel;
import free.util.swing.PlainTextDialog;
import free.util.swing.SwingUtils;
import java.io.IOException;


/**
 * Displays licensing and copyright information about Jin and bundled software.
 */

public class LicenseDialog extends JDialog{



  /**
   * The font of the text area displaying the license text.
   */

  private static final Font LICENSE_TEXT_FONT = new Font("Monospaced", Font.PLAIN, 12);



  /**
   * The text of the GPL, loaded lazily.
   */

  private String gplText = null;



  /**
   * The text of the LGPL, loaded lazily.
   */

  private String lgplText = null;



  /**
   * The text of the jregex copyright, loaded lazily.
   */

  private String jregexCopyrightText = null;



  /**
   * The text of the BeanShell license/copyright notice, loaded lazily.
   */

  private String beanshellCopyrightText = null;
  


  /**
   * Creates a new LicenseDialog with the given parent frame.
   */

  public LicenseDialog(Frame parent){
    super(parent, "Credits and Copyrights in Jin", true);

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    SwingUtils.registerEscapeCloser(this);

    createUI();
  }



  /**
   * The component to get the focus.
   */

  private Component focusComponent;



  /**
   * A workaround for ESCAPE not working in this dialog, under MS VM at least,
   * because we assign tooltips to some of the labels which makes them grab the
   * focus.
   */

  public void paint(Graphics g){
    super.paint(g);

    if (focusComponent != null){
      focusComponent.requestFocus();
      focusComponent = null;
    }
  }



  /**
   * Creates the user interface.
   */

  private void createUI(){
    JPanel contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
    contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

    ActionListener gplActionListener = new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        if (gplText == null){
          try{
            gplText = IOUtilities.loadText(LicenseDialog.class.getResource("legal/gpl.txt"));
          } catch (IOException e){
              JOptionPane.showMessageDialog(LicenseDialog.this, "Unable to load the text of the GPL, see http://www.gnu.org/copyleft/gpl.html for the text.", "Error", JOptionPane.ERROR_MESSAGE);
              return;
            }
        }

        PlainTextDialog textDialog = new PlainTextDialog(LicenseDialog.this, "The GNU General Public License", gplText);
        textDialog.setTextAreaFont(LICENSE_TEXT_FONT);
        AWTUtilities.centerWindow(textDialog, getParent());
        textDialog.setVisible(true);
      }
    };

    ActionListener lgplActionListener = new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        if (lgplText == null){
          try{
            lgplText = IOUtilities.loadText(LicenseDialog.class.getResource("legal/lgpl.txt"));
          } catch (IOException e){
              JOptionPane.showMessageDialog(LicenseDialog.this, "Unable to load the text of the LGPL, see http://www.gnu.org/copyleft/lesser.html for the text.", "Error", JOptionPane.ERROR_MESSAGE);
              return;
            }
        }

        PlainTextDialog textDialog = new PlainTextDialog((Frame)getParent(), "The GNU Lesser General Public License", lgplText);
        textDialog.setTextAreaFont(LICENSE_TEXT_FONT);
        AWTUtilities.centerWindow(textDialog, getParent());
        textDialog.setVisible(true);
      }
    };

    ActionListener jregexActionListener = new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        if (jregexCopyrightText == null){
          try{
            jregexCopyrightText = IOUtilities.loadText(LicenseDialog.class.getResource("legal/jregex.txt"));
          } catch (IOException e){
              JOptionPane.showMessageDialog(LicenseDialog.this, "Unable to load the text of the jregex license, see http://jregex.sourceforge.net/license.txt for the text.", "Error", JOptionPane.ERROR_MESSAGE);
              return;
            }
        }

        PlainTextDialog textDialog = new PlainTextDialog(LicenseDialog.this, "The jregex License (BSD)", jregexCopyrightText);
        textDialog.setTextAreaFont(LICENSE_TEXT_FONT);
        AWTUtilities.centerWindow(textDialog, getParent());
        textDialog.setVisible(true);
      }
    };

    ActionListener beanshellActionListener = new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        if (beanshellCopyrightText == null){
          try{
            beanshellCopyrightText = IOUtilities.loadText(LicenseDialog.class.getResource("legal/beanshell.txt"));
          } catch (IOException e){
              JOptionPane.showMessageDialog(LicenseDialog.this, "Unable to load the text of the beanshell license, see http://www.beanshell.org/license.html for the text.", "Error", JOptionPane.ERROR_MESSAGE);
              return;
            }
        }

        PlainTextDialog textDialog = new PlainTextDialog(LicenseDialog.this, "The BeanShell License", beanshellCopyrightText);
        textDialog.setTextAreaFont(LICENSE_TEXT_FONT);
        AWTUtilities.centerWindow(textDialog, getParent());
        textDialog.setVisible(true);
      }
    };



    JPanel jinPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    jinPanel.add(new JLabel("<html>Jin is distributed under the&nbsp</html>"));
    LinkLabel jinGPLLabel = new LinkLabel("GNU General Public License");
    jinGPLLabel.addActionListener(gplActionListener);
    jinPanel.add(jinGPLLabel);
    jinPanel.add(new JLabel("<html>.</html>"));
    contentPane.add(jinPanel);
    contentPane.add(Box.createVerticalStrut(5));

    JPanel jinWebsitePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    jinWebsitePanel.add(new JLabel("<html>More information about Jin is available at&nbsp</html>"));
    LinkLabel jinWebsiteLabel = new LinkLabel("the Jin website");
    jinWebsiteLabel.setToolTipText("http://www.jinchess.com");
    jinWebsiteLabel.addActionListener(new URLActionListener("http://www.jinchess.com"));
    jinWebsitePanel.add(jinWebsiteLabel);
    jinWebsitePanel.add(new JLabel("<html>.</html>"));
    contentPane.add(jinWebsitePanel);

    contentPane.add(Box.createVerticalStrut(10));
    contentPane.add(new JSeparator());
    contentPane.add(Box.createVerticalStrut(10));

    JPanel jregexPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    jregexPanel.add(new JLabel("<html>Jin uses the&nbsp</html>"));
    LinkLabel jregexWebsiteLabel = new LinkLabel("jregex regular expression library");
    jregexWebsiteLabel.setToolTipText("http://jregex.sourceforge.net");
    jregexWebsiteLabel.addActionListener(new URLActionListener("http://jregex.sourceforge.net"));
    jregexPanel.add(jregexWebsiteLabel);
    jregexPanel.add(new JLabel("<html>, licensed under the&nbsp</html>"));
    LinkLabel jregexLicenseLabel = new LinkLabel("BSD license");
    jregexLicenseLabel.addActionListener(jregexActionListener);
    jregexPanel.add(jregexLicenseLabel);
    jregexPanel.add(new JLabel("<html>.</html>"));
    contentPane.add(jregexPanel);
    contentPane.add(Box.createVerticalStrut(5));

    JPanel beanshellPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    beanshellPanel.add(new JLabel("<html>Jin uses the&nbsp</html>"));
    LinkLabel beanshellWebsiteLabel = new LinkLabel("BeanShell embeddable script interpreter");
    beanshellWebsiteLabel.setToolTipText("http://www.beanshell.org");
    beanshellWebsiteLabel.addActionListener(new URLActionListener("http://www.beanshell.org"));
    beanshellPanel.add(beanshellWebsiteLabel);
    beanshellPanel.add(new JLabel("<html>, licensed under the&nbsp</html>"));
    LinkLabel beanshellLicenseLabel = new LinkLabel("GNU Lesser General Public License");
    beanshellLicenseLabel.addActionListener(beanshellActionListener);
    beanshellPanel.add(beanshellLicenseLabel);
    beanshellPanel.add(new JLabel("<html>.</html>"));
    contentPane.add(beanshellPanel);
    contentPane.add(Box.createVerticalStrut(5));

    JPanel xboardPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    xboardPanel.add(new JLabel("<html>Jin contains a piece set from&nbsp</html>"));
    LinkLabel xboardWebsiteLabel = new LinkLabel("xboard/winboard");
    xboardWebsiteLabel.setToolTipText("http://www.tim-mann.org/xboard.html");
    xboardWebsiteLabel.addActionListener(new URLActionListener("http://www.tim-mann.org/xboard.html"));
    xboardPanel.add(xboardWebsiteLabel);
    xboardPanel.add(new JLabel("<html>, licensed under the&nbsp</html>"));
    LinkLabel xboardLicenseLabel = new LinkLabel("GNU General Public License");
    xboardLicenseLabel.addActionListener(gplActionListener);
    xboardPanel.add(xboardLicenseLabel);
    xboardPanel.add(new JLabel("<html>.</html>"));
    contentPane.add(xboardPanel);
    contentPane.add(Box.createVerticalStrut(5));

    JPanel eboardPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    eboardPanel.add(new JLabel("<html>Jin includes a piece set from&nbsp</html>"));
    LinkLabel eboardWebsiteLabel = new LinkLabel("eboard");
    eboardWebsiteLabel.setToolTipText("http://eboard.sourceforge.net/");
    eboardWebsiteLabel.addActionListener(new URLActionListener("http://eboard.sourceforge.net/"));
    eboardPanel.add(eboardWebsiteLabel);
    eboardPanel.add(new JLabel("<html>, licensed under the&nbsp</html>"));
    LinkLabel eboardLicenseLabel = new LinkLabel("GNU General Public License");
    eboardLicenseLabel.addActionListener(gplActionListener);
    eboardPanel.add(eboardLicenseLabel);
    eboardPanel.add(new JLabel("<html>.</html>"));
    contentPane.add(eboardPanel);
    contentPane.add(Box.createVerticalStrut(5));

    JPanel blitzinPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    blitzinPanel.add(new JLabel("<html>Jin includes piece sets, boards and sounds from&nbsp</html>"));
    LinkLabel blitzinWebsiteLabel = new LinkLabel("Internet Chess Club's Blitzin");
    blitzinWebsiteLabel.setToolTipText("http://www.chessclub.com/interface/download_w32.html");
    blitzinWebsiteLabel.addActionListener(new URLActionListener("http://www.chessclub.com/interface/download_w32.html"));
    blitzinPanel.add(blitzinWebsiteLabel);
    blitzinPanel.add(new JLabel("<html>, used with permission.</html>"));
    contentPane.add(blitzinPanel);
    contentPane.add(Box.createVerticalStrut(5));

    JPanel kunststoffPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    kunststoffPanel.add(new JLabel("<html>Jin is distributed with the&nbsp</html>"));
    LinkLabel kunststoffWebsiteLabel = new LinkLabel("Kunststoff Look And Feel");
    kunststoffWebsiteLabel.setToolTipText("http://www.incors.org/");
    kunststoffWebsiteLabel.addActionListener(new URLActionListener("http://www.incors.org/"));
    kunststoffPanel.add(kunststoffWebsiteLabel);
    kunststoffPanel.add(new JLabel("<html>, licensed under the&nbsp</html>"));
    LinkLabel kunststoffLicenseLabel = new LinkLabel("GNU Lesser General Public License");
    kunststoffLicenseLabel.addActionListener(lgplActionListener);
    kunststoffPanel.add(kunststoffLicenseLabel);
    kunststoffPanel.add(new JLabel("<html>.</html>"));
    contentPane.add(kunststoffPanel);
    contentPane.add(Box.createVerticalStrut(5));

    JPanel threeDlfPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    threeDlfPanel.add(new JLabel("<html>Jin is distributed with the&nbsp</html>"));
    LinkLabel threeDlfWebsiteLabel = new LinkLabel("3D Look And Feel");
    threeDlfWebsiteLabel.setToolTipText("http://www.markus-hillenbrand.de/3dlf/index.html");
    threeDlfWebsiteLabel.addActionListener(
      new URLActionListener("http://www.markus-hillenbrand.de/3dlf/index.html"));
    threeDlfPanel.add(threeDlfWebsiteLabel);
    threeDlfPanel.add(new JLabel("<html>, licensed under the&nbsp</html>"));
    LinkLabel threeDlfLicenseLabel = new LinkLabel("GNU General Public License");
    threeDlfLicenseLabel.addActionListener(gplActionListener);
    threeDlfPanel.add(threeDlfLicenseLabel);
    threeDlfPanel.add(new JLabel("<html>.</html>"));
    contentPane.add(threeDlfPanel);
    contentPane.add(Box.createVerticalStrut(5));

    JPanel denisDesLauriersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    denisDesLauriersPanel.add(new JLabel("<html>The Jin logo was designed by&nbsp</html>"));
    LinkLabel denisDesLauriersWebsiteLabel = new LinkLabel("Denis DesLauriers");
    denisDesLauriersWebsiteLabel.setToolTipText("http://www.chess-art.com");
    denisDesLauriersWebsiteLabel.addActionListener(
      new URLActionListener("http://www.chess-art.com"));
    denisDesLauriersPanel.add(denisDesLauriersWebsiteLabel);
    denisDesLauriersPanel.add(new JLabel("<html>, licensed under the&nbsp</html>"));
    LinkLabel logoLicenseLabel = new LinkLabel("GNU General Public License");
    logoLicenseLabel.addActionListener(gplActionListener);
    denisDesLauriersPanel.add(logoLicenseLabel);
    denisDesLauriersPanel.add(new JLabel("<html>.</html>"));
    contentPane.add(denisDesLauriersPanel);
    

    contentPane.add(Box.createVerticalStrut(15));

    JButton okButton = new JButton("OK");
    okButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        dispose();
      }
    });
    okButton.setAlignmentX(CENTER_ALIGNMENT);
    contentPane.add(okButton);

    setContentPane(contentPane);
    getRootPane().setDefaultButton(okButton);

    focusComponent = okButton;
  }




  /**
   * An ActionListener which when activated, displays the given URL.
   */

  private class URLActionListener implements ActionListener{


    /**
     * The URL.
     */

    private final String url;



    /**
     * Creates a new URLActionListener with the given URL.
     */

    public URLActionListener(String url){
      this.url = url;
    }



    /**
     * Displays the url.
     */
    
    public void actionPerformed(ActionEvent evt){
      try{
        BrowserControl.displayURL(url);
      } catch (IOException e){
          JOptionPane.showMessageDialog(LicenseDialog.this, "Unable to display url: "+url, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

  }


}
