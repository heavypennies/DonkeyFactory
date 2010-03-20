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

package free.jin.chessclub;

import java.awt.*;
import javax.swing.*;
import java.net.URL;
import java.awt.image.ImageObserver;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import free.util.BrowserControl;


/**
 * An extension of JInternalFrame which implements the image frame you see when
 * ask to see the image of a player.
 */

public class UserImageInternalFrame extends JInternalFrame{



  /**
   * The panel where the image and the "please wait" label go.
   */

  private final JPanel imagePanel;



  /**
   * The loaded image.
   */

  private final Image image;




  /**
   * The JLabel saying "please wait... blah blah..." to the user.
   */

  private final JLabel waitLabel;




  /**
   * The button which opens the browser on the image submission page.
   */

  private final JButton submitButton = new JButton("Submit a picture");




  /**
   * Creates a new UserImageInternalFrame with the given URL from where the Image
   * should be loaded and the name of the picture.
   */

  public UserImageInternalFrame(URL imageURL, String picName){
    super(picName, true, true, true, true);

    this.image = Toolkit.getDefaultToolkit().getImage(imageURL);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    imagePanel = new JPanel(){

      public void doLayout(){
        Dimension size = this.getSize();
        Component [] children = this.getComponents();
        for (int i=0;i<children.length;i++)
          children[i].setBounds(0, 0, size.width, size.height);
      }


      public Dimension getPreferredSize(){
        Dimension prefSize = new Dimension();
        Component [] children = this.getComponents();
        for (int i=0;i<children.length;i++){
          Dimension compPrefSize = children[i].getPreferredSize();
          prefSize.width = prefSize.width>compPrefSize.width ? prefSize.width : compPrefSize.width;
          prefSize.height = prefSize.height>compPrefSize.height ? prefSize.height : compPrefSize.height;
        }

        return prefSize;
      }
      
    };

    getContentPane().setLayout(new BorderLayout());

    waitLabel = new JLabel("Please wait for the image to finish loading");
    if (!Toolkit.getDefaultToolkit().prepareImage(image, -1, -1, null))
      imagePanel.add(waitLabel);

    imagePanel.add(new ImageComponent());

    getContentPane().add(imagePanel, BorderLayout.CENTER);
    getContentPane().add(submitButton, BorderLayout.SOUTH);

    submitButton.addActionListener(new ActionListener(){

      public void actionPerformed(ActionEvent evt){
        try{
          BrowserControl.displayURL("https://www.chessclub.com/mugshots/submit.php");
        } catch (IOException e){
            e.printStackTrace();
          }
      }

    });
  }




  /**
   * The component displaying the image.
   */

  private class ImageComponent extends JComponent{


    /**
     * Do we know the size of the image yet?
     */

    private boolean sizeKnown = false;




    /**
     * Creates a new ImageComponent.
     */

    public ImageComponent(){
      sizeKnown = Toolkit.getDefaultToolkit().prepareImage(image, -1, -1, this);

      this.enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK); // Otherwise the cursor doesn't change in 1.1
    }



    /**
     * Paints this ImageComponent on the given Graphics.
     */

    public void paintComponent(Graphics g){
      g.drawImage(image, 0, 0, this);
    }



    /**
     * If the width and height of the image are already known makes sure to
     * resize the internal frame so it shows the image completely.
     */
    
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height){
      if ((!sizeKnown)&&((infoflags&ImageObserver.WIDTH)!=0)&&((infoflags&ImageObserver.HEIGHT)!=0)){
        sizeKnown = true;
        UserImageInternalFrame.this.setSize(UserImageInternalFrame.this.getPreferredSize());
      }

      if ((infoflags&ImageObserver.ALLBITS)!=0){
        imagePanel.remove(waitLabel);
        UserImageInternalFrame.this.invalidate();
        UserImageInternalFrame.this.validate();
        UserImageInternalFrame.this.setSize(UserImageInternalFrame.this.getPreferredSize());
      }

      return super.imageUpdate(img, infoflags, x, y, width, height);
    }



    /**
     * Returns the preferred size of this component.
     */

    public Dimension getPreferredSize(){
      if (sizeKnown)
        return new Dimension(image.getWidth(this), image.getHeight(this));
      else
        return new Dimension();
    }


  }

}
