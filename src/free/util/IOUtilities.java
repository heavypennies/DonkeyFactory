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

package free.util;

import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Various utility methods that have something to do with I/O.
 */

public class IOUtilities{


  /**
   * Returns a DataOutputStream object based on the given OutputStream.
   * If the given OutputStream is already an instance of DataOutputStream,
   * the same (given) OutputStream is casted to DataOutputStream and returned,
   * otherwise, a new wrapper DataOutputStream is created and returned.
   */

  public static DataOutputStream maybeCreateDataOutputStream(OutputStream out){
    if (out instanceof DataOutputStream)
      return (DataOutputStream)out;
    else
      return new DataOutputStream(out);
  }



  /**
   * Returns a DataInputStream object based on the given InputStream.
   * If the given InputStream is already an instance of DataInputStream,
   * the same (given) InputStream is casted to DataInputStream and returned,
   * otherwise, a new wrapper DataInputStream is created and returned.
   */

  public static DataInputStream maybeCreateDataInputStream(InputStream in){
    if (in instanceof DataInputStream)
      return (DataInputStream)in;
    else
      return new DataInputStream(in);
  }




  /**
   * Copies all the files of the given source directory into the given
   * destination directory, optionally recursively.
   */

  public static void copyDir(File source, File destination, boolean recurse) throws IOException{
    if (!source.exists())
      throw new IllegalArgumentException("The source directory ("+source+") doesn't exist");
    if (!source.isDirectory())
      throw new IllegalArgumentException("The source ("+source+") is a file, not a directory");
    if (!destination.exists())
      throw new IllegalArgumentException("The destination directory ("+destination+") doesn't exist");
    if (!destination.isDirectory())
      throw new IllegalArgumentException("The destination ("+destination+") is a file, not a directory");

    String [] filenames = source.list();
    for (int i=0; i<filenames.length; i++){
      String filename = filenames[i];
      File file = new File(source, filename);
      if (file.isDirectory()){
        if (recurse){
          File destSubDir = new File(destination, filename);
          if (!destSubDir.exists())
            if (!destSubDir.mkdirs())
              throw new IOException("Unable to create directory "+destSubDir);

          copyDir(file, destSubDir, true);
        }
      }
      else{
        InputStream in = null;
        OutputStream out = null;
        try{
          in = new FileInputStream(file);
          out = new FileOutputStream(new File(destination, filename));
          pump(in, out);
        } finally{
            if (in!=null)
              in.close();
            if (out!=null)
              out.close();
          }

      }
    }
  }




  /**
   * Removes the given directory and all files within it, recursively. Returns
   * <code>true</code> if successful, <code>false</code> otherwise. Note that if
   * it return <code>false</code>, some (or all) the files in the directory may
   * already be deleted.
   */
  
  public static boolean rmdir(File dir){
    if (!dir.isDirectory())
      throw new IllegalArgumentException();

    String [] filenames = dir.list();
    for (int i = 0; i < filenames.length; i++){
      File file = new File(dir, filenames[i]);
      if (file.isDirectory()){
        if (!rmdir(file))
          return false;
      }
      else if (!file.delete())
        return false;
    }

    return dir.delete();
  }




  /**
   * Writes the bytes read from the given input stream into the given output
   * stream until the end of the input stream is reached. Returns the amount of
   * bytes actually read/written.
   */

  public static int pump(InputStream in, OutputStream out) throws IOException{
    return pump(in, out, new byte[2048]);
  }




  /**
   * Writes up to the given amount of bytes read from the given input stream 
   * into the given output stream until the end of the input stream is reached.
   * Returns the amount of bytes actually read/written.
   */

  public static int pump(InputStream in, OutputStream out, int amount) throws IOException{ 
    return pump(in, out, amount, new byte[2048]);
  }





  /**
   * Writes the bytes read from the given input stream into the given output
   * stream until the end of the input stream is reached. Returns the amount of
   * bytes actually read/written. Uses the given byte array as the buffer.
   */

  public static int pump(InputStream in, OutputStream out, byte [] buf) throws IOException{
    if (buf.length==0)
      throw new IllegalArgumentException("Cannot use a 0 length buffer");

    int count;
    int amountRead = 0;
    while ((count = in.read(buf))!=-1){
      out.write(buf,0,count);
      amountRead += count;
    }

    return amountRead;
  }




  /**
   * Writes up to the given amount of bytes read from the given input stream 
   * into the given output stream until the end of the input stream is reached.
   * Returns the amount of bytes actually read/written. Uses the given byte array
   * as the buffer.
   */

  public static int pump(InputStream in, OutputStream out, int amount, byte [] buf) throws IOException{ 
    if (buf.length==0)
      throw new IllegalArgumentException("Cannot use a 0 length buffer");

    int amountRead = 0;
    while (amount > 0){
      int amountToRead = amount > buf.length ? buf.length : amount;
      int count = in.read(buf, 0, amountToRead);
      if (count==-1)
        break;

      out.write(buf,0,count);
      amount -= count;
      amountRead += count;
    }

    return amountRead;
  }





  /**
   * Reads from the given InputStream until its end and returns a byte array
   * of the contents. Note that this method doesn't close the given InputStream,
   * that is left to the user.
   */

  public static byte [] readToEnd(InputStream in) throws IOException{
    byte [] buf = new byte[2048];

    int amountRead = 0;
    int count = 0;
    while ((count = in.read(buf, amountRead, buf.length-amountRead)) > 0){
      amountRead += count;

      if (amountRead == buf.length){
        byte [] oldBuf = buf;
        buf = new byte[oldBuf.length*2];
        System.arraycopy(oldBuf, 0, buf, 0, amountRead);
      }
    }

    byte [] arr = new byte[amountRead];
    System.arraycopy(buf, 0, arr, 0, amountRead);
    return arr;
  }




  /**
   * Reads all the information from the given InputStream and returns it as
   * plain text by using the default system encoding. Note that this method
   * doesn't close the given InputStream, that is left to the user.
   */

  public static String loadText(InputStream in) throws IOException{
    return new String(readToEnd(in));
  }




  /**
   * Loads the text from the given URL and returns it as a string.
   *
   * @throws IOException if the given URL does not exist or an I/O error occurs
   * while accessing it.
   */

  public static String loadText(URL url) throws IOException{
    InputStream in = url.openStream();
    String text = loadText(in);
    in.close();
    return text;
  }




  /**
   * Loads the given text file from the local drive, converts it to a String and
   * returns the String. 
   *
   * @throws IOException if the file does not exist or loading failed.
   */

  public static String loadTextFile(File file) throws IOException{
    if (!file.exists())
      throw new IOException("File does not exist");

    InputStream in = new FileInputStream(file);
    String text = loadText(in);
    in.close();
    return text;
  }




  /**
   * Loads a text file with the given name from the local drive, converts it to
   * a String and returns the String.
   *
   * @throws IOException if the file does not exist or loading failed.
   */

  public static String loadTextFile(String filename) throws IOException{
    return loadTextFile(new File(filename));
  }




  /**
   * Compares the 2 given sub arrays. Returns true if they are equal, false
   * otherwise.
   *
   * @throws ArrayIndexOutOfBounds if
   * <UL>
   *   <LI> <code>offset1</code> or <code>offset2</code> are negative.
   *   <LI> length is negative.
   *   <LI> <code>offset1+length</code> is bigger than <code>arr1.length</code>
   *   <LI> <code>offset2+length</code> is bigger than <code>arr2.length</code>
   * </UL>
   */

  public static boolean equal(byte [] arr1, int offset1, byte [] arr2, int offset2, int length){
    if ((offset1<0)||(offset2<0)||(length<0)||(offset1+length>arr1.length)||(offset2+length>arr2.length))
      throw new ArrayIndexOutOfBoundsException();

    for (int i=0;i<length;i++){
      if (arr1[offset1+i]!=arr2[offset2+i])
        return false;
    }

    return true;
  }




  /**
   * Returns a <code>URL</code> corresponding to the specified <code>File</code>
   * or <code>null</code> if the <code>File</code> cannot be converted into a
   * <code>URL</code>.
   * NOTE: This is copied from the JDK1.3 source, File.java
   */

  public static URL fileToURL(File file){
    try{
      String path = file.getAbsolutePath();
      if (File.separatorChar != '/')
        path = path.replace(File.separatorChar, '/');
      if (!path.startsWith("/"))
        path = "/" + path;
      if (!path.endsWith("/") && file.isDirectory())
        path = path + "/";
      return new URL("file", "", path);
    } catch (MalformedURLException e){
        return null;
      }
  }

}
