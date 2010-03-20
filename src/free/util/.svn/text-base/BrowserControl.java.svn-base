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

// Taken from http://www.javaworld.com/javaworld/javatips/jw-javatip66.html

package free.util;

import java.io.IOException; 
import java.io.InterruptedIOException;
import java.util.Properties;
import java.util.StringTokenizer;

/** 
 * A simple, static class to display a URL in the system browser.
 * Under Unix, the system browser is hard-coded to be 'netscape'. 
 * Netscape must be in your PATH for this to work. This has been 
 * tested with the following platforms: AIX, HP-UX and Solaris.
 * Under Windows, this will bring up the default browser under windows, 
 * usually either Netscape or Microsoft IE. The default browser is 
 * determined by the OS. This has been tested under Windows 95/98/NT.
 * 
 * Examples:
 * BrowserControl.displayURL("http://www.javaworld.com") 
 * BrowserControl.displayURL("file://c:\\docs\\index.html") 
 * BrowserContorl.displayURL("file:///user/joe/index.html"); 
 * 
 * Note - you must include the url type -- either "http://" or 
 * "file://". 
 */ 

public class BrowserControl{ 



  /**
   * A Properties hashtable containing the environment variables and their
   * values.
   */

  private static Properties environment = null;



  /** 
   * Display a file in the system browser. If you want to display a 
   * file, you must include the absolute path name. 
   * 
   * @param url the file's url (the url must start with either "http://" or 
   * "file://"). 
   */ 

  public static void displayURL(String url) throws IOException{ 
    if (isWindows()){ 
      if (url.endsWith(".html")||url.endsWith(".htm")){

        // url-encode the last character because windows refuses to display URLs
        // ending with ".html" or ".htm", but works fine
        // for ".htm%6c" or ".ht%6d"
        int lastChar = url.charAt(url.length()-1);
        url = url.substring(0,url.length()-1)+"%"+Integer.toHexString(lastChar);
      }
      String cmd = "rundll32 url.dll,FileProtocolHandler "+url;
      Runtime.getRuntime().exec(cmd); 
    } 
    else if (isMacOS()){
      String [] commandLine = {"netscape", url}; 
      Runtime.getRuntime().exec(commandLine); 
    }
    else if (isLinux()){
      synchronized(BrowserControl.class){
        if (environment == null){
          environment = new Properties();
          try{
            Process env = Runtime.getRuntime().exec("env");
            environment.load(env.getInputStream());
          } catch (IOException e){}
        }
      }

      String browsers = environment.getProperty("BROWSER");
      if (browsers!=null){
        StringTokenizer tokenizer = new StringTokenizer(browsers, ":");
        String browser = tokenizer.nextToken();
        int percentPercentIndex;
        while ((percentPercentIndex = browser.indexOf("%%")) != -1)
          browser = browser.substring(0, percentPercentIndex)+"%"+browser.substring(percentPercentIndex+3);
        int urlIndex = browser.indexOf("%s");
        String commandline;
        if (urlIndex != -1)
          commandline = browser.substring(0, urlIndex)+url+browser.substring(urlIndex+3);
        else
          commandline = browser+" "+url;
        Runtime.getRuntime().exec(commandline);
      }
    }
    else{ 
      // Too many unix platforms to check, so we'll just assume it *is* unix.
      // Under Unix, Netscape has to be running for the "-remote" 
      // command to work. So, we try sending the command and 
      // check for an exit value. If the return value is 0, 
      // it worked, otherwise we need to start the browser. 

      // cmd = 'netscape -remote openURL(http://www.javaworld.com)' 
      String cmd = "netscape -remote -raise openURL("+url+")"; 
      Process p = Runtime.getRuntime().exec(cmd); 

      try{ 
        // wait for exit code -- if it's 0, command worked, 
        // otherwise we need to start the browser up. 
        int exitCode = p.waitFor(); 

        if (exitCode != 0){ 
          // Command failed, start up the browser 
      
          // cmd = 'netscape http://www.javaworld.com' 
          cmd = "netscape " + url; 
          p = Runtime.getRuntime().exec(cmd); 
        } 
      } 
      catch(InterruptedException x){ 
        System.err.println("Error bringing up browser, cmd='" + cmd + "'"); 
        System.err.println("Caught: " + x); 
        throw new InterruptedIOException(x.getMessage());
      }
    } 
  } 




  /**
   * Brings up the default mailer with the given address in the "to:" field.
   */

  public static void displayMailer(String address) throws IOException{
    if (isLinux()){
      synchronized(BrowserControl.class){
        if (environment == null){
          try{
            environment = new Properties();
            Process env = Runtime.getRuntime().exec("env");
            environment.load(env.getInputStream());
          } catch (IOException e){}
        }
      }

      String mailer = environment.getProperty("MAILER");
      if (mailer!=null){
        Runtime.getRuntime().exec(mailer+" "+address);
        return;
      }
    }

    displayURL("mailto:"+address);
  }




  /** 
   * Tries to determine whether this application is running under Windows 
   * by examing the "os.name" property. Returns true if the value of the
   * "os.name" property starts (case insensitively) with the string "windows".
   * 
   * @return true if this application is running under Windows.
   */ 

  public static boolean isWindows(){ 
    String os = System.getProperty("os.name"); 

    if ((os!=null) && os.toLowerCase().startsWith("windows"))
      return true; 
    else 
      return false; 
  } 



  /** 
   * Tries to determine whether this application is running under MacOS 
   * by examing the "os.name" property. Returns <code>true</code> if the value
   * of the "os.name" property starts (case insensitively) with the string
   * "mac".
   * 
   * @return true if this application is running under MacOS .
   */ 

  public static boolean isMacOS(){ 
    String os = System.getProperty("os.name"); 

    if ((os!=null) && os.toLowerCase().startsWith("mac"))
      return true; 
    else 
      return false; 
  } 





  /** 
   * Tries to determine whether this application is running under Linux 
   * by examing the "os.name" property. Returns <code>true</code> if the value
   * of the "os.name" property starts (case insensitively) with the string
   * "linux".
   * 
   * @return true if this application is running under Linux.
   */ 

  public static boolean isLinux(){ 
    String os = System.getProperty("os.name"); 

    if ((os!=null) && os.toLowerCase().startsWith("linux"))
      return true; 
    else 
      return false; 
  } 


}
