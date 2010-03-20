/**
 * The utillib library.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2003 Alexander Maryanovsky.
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

package free.util;

import java.util.Hashtable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * This class provides the functionality of storing and retrieving bean
 * properties while automatically notifying any listeners. It can be used to
 * delegate the bean storing, retrieving and notifying work to it from the
 * actual bean. It doesn't, however, support constrained properties.
 * <STRONG>Note:</STRONG>This class is not thread safe.
 */

public class BeanProperties{


  /**
   * The source bean.
   */

  private final Object source;



  /**
   * The hashtable where we keep the properties.
   */

  private final Hashtable props = new Hashtable();



  /**
   * The property change listeners.
   */

  private final EventListenerList listenerList = new EventListenerList();




  /**
   * Creates a new BeanProperties with the specified source bean.
   */

  public BeanProperties(Object source){
    this.source = source;
  }



  /**
   * Registers the specified <code>PropertyChangeListener</code>.
   */

  public void addPropertyChangeListener(PropertyChangeListener listener){
    listenerList.add(PropertyChangeListener.class, listener);
  }



  /**
   * Unregisters the specified <code>PropertyChangeListener</code>.
   */

  public void removePropertyChangeListener(PropertyChangeListener listener){
    listenerList.remove(PropertyChangeListener.class, listener);
  }



  /**
   * Fires a <code>PropertyChangeEvent</code> to all registered property change
   * listeners.
   */

  private void firePropertyChanged(String propertyName, Object oldValue, Object newValue){
    PropertyChangeEvent evt = new PropertyChangeEvent(source, propertyName, oldValue, newValue);

    Object [] listeners = listenerList.getListenerList();
    for (int i = 0; i < listeners.length; i += 2){
      if (listeners[i] == PropertyChangeListener.class){
        PropertyChangeListener listener = (PropertyChangeListener)listeners[i+1];
        listener.propertyChange(evt);
      }
    }
  }



  /**
   * Sets the property with the specified name to the specified value.
   */

  public void setProperty(String propertyName, Object propertyValue){
    Object oldValue = props.put(propertyName, propertyValue);
    firePropertyChanged(propertyName, oldValue, propertyValue);
  }



  /**
   * Returns the value of the property with the specified name. Returns
   * <code>null</code> if there is no property with the specified name.
   */

  public Object getProperty(String propertyName){
    return props.get(propertyName);
  }



  /**
   * Sets the property with the specified name to the specified
   * <code>boolean</code> value.
   */

  public void setBooleanProperty(String propertyName, boolean propertyValue){
    setProperty(propertyName, propertyValue ? Boolean.TRUE : Boolean.FALSE);
  }



  /**
   * Returns the value of the <code>boolean</code> property with the specified
   * name. Throws a <code>ClassCastException</code> if current value of the
   * property is not an instance of <code>java.lang.Boolean</code>. If there is
   * no property with the specified name, a <code>NullPointerException</code> is
   * thrown.
   */

  public boolean getBooleanProperty(String propertyName){
    Boolean value = (Boolean)getProperty(propertyName);
    return value.booleanValue();
  }




  /**
   * Returns the value of the <code>boolean</code> property with the specified
   * name. Throws a <code>ClassCastException</code> if current value of the
   * property is not an instance of <code>java.lang.Boolean</code>. If there is
   * no property with the specified name, the specified default value is
   * returned.
   */

  public boolean getBooleanProperty(String propertyName, boolean defaultValue){
    Boolean value = (Boolean)getProperty(propertyName);
    if (value == null)
      return defaultValue;
    else
      return value.booleanValue();
  }



  /**
   * Sets the property with the specified name to the specified
   * <code>byte</code> value.
   */

  public void setByteProperty(String propertyName, byte propertyValue){
    setProperty(propertyName, new Byte(propertyValue));
  }



  /**
   * Returns the value of the <code>byte</code> property with the specified
   * name. Throws a <code>ClassCastException</code> if current value of the
   * property is not an instance of <code>java.lang.Byte</code>. If there is
   * no property with the specified name, a <code>NullPointerException</code> is
   * thrown.
   */

  public byte getByteProperty(String propertyName){
    Byte value = (Byte)getProperty(propertyName);
    return value.byteValue();
  }




  /**
   * Returns the value of the <code>byte</code> property with the specified
   * name. Throws a <code>ClassCastException</code> if current value of the
   * property is not an instance of <code>java.lang.Byte</code>. If there is
   * no property with the specified name, the specified default value is
   * returned.
   */

  public byte getByteProperty(String propertyName, byte defaultValue){
    Byte value = (Byte)getProperty(propertyName);
    if (value == null)
      return defaultValue;
    else
      return value.byteValue();
  }




  /**
   * Sets the property with the specified name to the specified
   * <code>short</code> value.
   */

  public void setShortProperty(String propertyName, short propertyValue){
    setProperty(propertyName, new Short(propertyValue));
  }



  /**
   * Returns the value of the <code>short</code> property with the specified
   * name. Throws a <code>ClassCastException</code> if current value of the
   * property is not an instance of <code>java.lang.Short</code>. If there is
   * no property with the specified name, a <code>NullPointerException</code> is
   * thrown.
   */

  public short getShortProperty(String propertyName){
    Short value = (Short)getProperty(propertyName);
    return value.shortValue();
  }




  /**
   * Returns the value of the <code>short</code> property with the specified
   * name. Throws a <code>ClassCastException</code> if current value of the
   * property is not an instance of <code>java.lang.Short</code>. If there is
   * no property with the specified name, the specified default value is
   * returned.
   */

  public short getShortProperty(String propertyName, short defaultValue){
    Short value = (Short)getProperty(propertyName);
    if (value == null)
      return defaultValue;
    else
      return value.shortValue();
  }




  /**
   * Sets the property with the specified name to the specified
   * <code>int</code> value.
   */

  public void setIntegerProperty(String propertyName, int propertyValue){
    setProperty(propertyName, new Integer(propertyValue));
  }



  /**
   * Returns the value of the <code>int</code> property with the specified
   * name. Throws a <code>ClassCastException</code> if current value of the
   * property is not an instance of <code>java.lang.Integer</code>. If there is
   * no property with the specified name, a <code>NullPointerException</code> is
   * thrown.
   */

  public int getIntegerProperty(String propertyName){
    Integer value = (Integer)getProperty(propertyName);
    return value.intValue();
  }




  /**
   * Returns the value of the <code>int</code> property with the specified
   * name. Throws a <code>ClassCastException</code> if current value of the
   * property is not an instance of <code>java.lang.Integer</code>. If there is
   * no property with the specified name, the specified default value is
   * returned.
   */

  public int getIntegerProperty(String propertyName, int defaultValue){
    Integer value = (Integer)getProperty(propertyName);
    if (value == null)
      return defaultValue;
    else
      return value.intValue();
  }




  /**
   * Sets the property with the specified name to the specified
   * <code>long</code> value.
   */

  public void setLongProperty(String propertyName, int propertyValue){
    setProperty(propertyName, new Long(propertyValue));
  }




  /**
   * Returns the value of the <code>long</code> property with the specified
   * name. Throws a <code>ClassCastException</code> if current value of the
   * property is not an instance of <code>java.lang.Long</code>. If there is
   * no property with the specified name, a <code>NullPointerException</code> is
   * thrown.
   */

  public long getLongProperty(String propertyName){
    Long value = (Long)getProperty(propertyName);
    return value.longValue();
  }




  /**
   * Returns the value of the <code>long</code> property with the specified
   * name. Throws a <code>ClassCastException</code> if current value of the
   * property is not an instance of <code>java.lang.Long</code>. If there is
   * no property with the specified name, the specified default value is
   * returned.
   */

  public long getLongProperty(String propertyName, long defaultValue){
    Long value = (Long)getProperty(propertyName);
    if (value == null)
      return defaultValue;
    else
      return value.longValue();
  }





  /**
   * Sets the property with the specified name to the specified
   * <code>float</code> value.
   */

  public void setFloatProperty(String propertyName, float propertyValue){
    setProperty(propertyName, new Float(propertyValue));
  }



  /**
   * Returns the value of the <code>float</code> property with the specified
   * name. Throws a <code>ClassCastException</code> if current value of the
   * property is not an instance of <code>java.lang.Float</code>. If there is
   * no property with the specified name, a <code>NullPointerException</code> is
   * thrown.
   */

  public float getFloatProperty(String propertyName){
    Float value = (Float)getProperty(propertyName);
    return value.floatValue();
  }




  /**
   * Returns the value of the <code>float</code> property with the specified
   * name. Throws a <code>ClassCastException</code> if current value of the
   * property is not an instance of <code>java.lang.Float</code>. If there is
   * no property with the specified name, the specified default value is
   * returned.
   */

  public float getFloatProperty(String propertyName, float defaultValue){
    Float value = (Float)getProperty(propertyName);
    if (value == null)
      return defaultValue;
    else
      return value.floatValue();
  }




  /**
   * Sets the property with the specified name to the specified
   * <code>double</code> value.
   */

  public void setDoubleProperty(String propertyName, double propertyValue){
    setProperty(propertyName, new Double(propertyValue));
  }



  /**
   * Returns the value of the <code>double</code> property with the specified
   * name. Throws a <code>ClassCastException</code> if current value of the
   * property is not an instance of <code>java.lang.Double</code>. If there is
   * no property with the specified name, a <code>NullPointerException</code> is
   * thrown.
   */

  public double getDoubleProperty(String propertyName){
    Double value = (Double)getProperty(propertyName);
    return value.doubleValue();
  }




  /**
   * Returns the value of the <code>double</code> property with the specified
   * name. Throws a <code>ClassCastException</code> if current value of the
   * property is not an instance of <code>java.lang.Double</code>. If there is
   * no property with the specified name, the specified default value is
   * returned.
   */

  public double getDoubleProperty(String propertyName, double defaultValue){
    Double value = (Double)getProperty(propertyName);
    if (value == null)
      return defaultValue;
    else
      return value.doubleValue();
  }




  /**
   * Sets the property with the specified name to the specified
   * <code>char</code> value.
   */

  public void setCharacterProperty(String propertyName, char propertyValue){
    setProperty(propertyName, new Character(propertyValue));
  }



  /**
   * Returns the value of the <code>char</code> property with the specified
   * name. Throws a <code>ClassCastException</code> if current value of the
   * property is not an instance of <code>java.lang.Character</code>. If there
   * is no property with the specified name, a
   * <code>NullPointerException</code> is thrown.
   */

  public char getCharacterProperty(String propertyName){
    Character value = (Character)getProperty(propertyName);
    return value.charValue();
  }



  /**
   * Returns the value of the <code>char</code> property with the specified
   * name. Throws a <code>ClassCastException</code> if current value of the
   * property is not an instance of <code>java.lang.Character</code>. If there
   * is no property with the specified name, the specified default value is
   * returned.
   */

  public char getCharacterProperty(String propertyName, char defaultValue){
    Character value = (Character)getProperty(propertyName);
    if (value == null)
      return defaultValue;
    else
      return value.charValue();
  }



  /**
   * Returns the value of the <code>String</code> property with the specified
   * name. Throws a <code>ClassCastException</code> if current value of the
   * property is not an instance of <code>java.lang.String</code>. If there is
   * no property with the specified name, a <code>NullPointerException</code> is
   * thrown (I kid you not).
   */

  public String getStringProperty(String propertyName){
    String value = (String)getProperty(propertyName);
    if (value == null)
      throw new NullPointerException();
    return value;
  }




  /**
   * Returns the value of the <code>String</code> property with the specified
   * name. Throws a <code>ClassCastException</code> if current value of the
   * property is not an instance of <code>java.lang.String</code>. If there is
   * no property with the specified name, the specified default value is
   * returned.
   */

  public String getStringProperty(String propertyName, String defaultValue){
    String value = (String)getProperty(propertyName);
    if (value == null)
      return defaultValue;
    else
      return value;
  }


}
