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

package free.util.audio;

import javax.sound.sampled.*;
import java.util.Hashtable;
import free.util.BlockingQueue;


/**
 * This is an AudioPlayer implementation which uses the javax.sound.sampled API
 * to play sounds. This API is only available since JDK1.3.
 */

public class JavaxSampledAudioPlayer implements Runnable, AudioPlayer{


  /**
   * The current thread playing the sound.
   */

  private Thread playerThread = null;



  /**
   * Maps free.util.audio.AudioClip instances to javax.sound.sampled.Clip
   * instances.
   */

  private final Hashtable clips = new Hashtable();



  /**
   * A BlockingQueue of queued AudioClips.
   */

  private final BlockingQueue clipQueue = new BlockingQueue();




  /**
   * Returns true if the value of "java.version" is 1.3 or later.
   */

  public boolean isSupported(){
    return System.getProperty("java.version").compareTo("1.3") >= 0;
  }




  /**
   * Plays the given AudioClip.
   */

  public synchronized void play(AudioClip clip) throws java.io.IOException{
    // Lazily initialize player thread.
    if (playerThread == null){
      playerThread = new Thread(this, "JavaxSampledAudioPlayer");
      playerThread.setDaemon(true);
      playerThread.start();
    }

    clipQueue.push(clip);
  }



  /**
   * <code>Runnable</code> implementation. Plays the queued clips.
   */

  public void run(){
    while (true){
      AudioClip clip;
      try{
        clip = (AudioClip)clipQueue.pop();
      } catch (InterruptedException e){
          e.printStackTrace();
          return;
        }

      Clip newClip = (Clip)clips.get(clip);
      if (newClip == null){
        try{
          newClip = createClip(clip.getData());
        } catch (LineUnavailableException e){
            e.printStackTrace();
            // Ignore, will try again later.
          }
          catch (UnsupportedAudioFileException e){
            e.printStackTrace();
            // Ignore, nothing we can do about it...
          }
          catch (IllegalArgumentException e){
            e.printStackTrace();
            // Apparently it (AudioSystem.getAudioInputStream()) can throw this too.
          }

        clips.put(clip, newClip);
      }

      if (newClip != null)
        startPlaying(newClip);
    }
  }




  /**
   * Actually starts playing the given Clip.
   */

  private void startPlaying(Clip clip){
    clip.setFramePosition(0);
    clip.start();
  }




  /**
   * Creates and loads a Clip from the given audio data.
   */

  private static Clip createClip(byte [] data) throws LineUnavailableException, UnsupportedAudioFileException{
    try{
      AudioInputStream stream = AudioSystem.getAudioInputStream(new java.io.ByteArrayInputStream(data));

      // At present, ALAW and ULAW encodings must be converted
      // to PCM_SIGNED before it can be played
      AudioFormat format = stream.getFormat();
      if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
        format = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                format.getSampleRate(),
                format.getSampleSizeInBits()*2,
                format.getChannels(),
                format.getFrameSize()*2,
                format.getFrameRate(),
                true);        // big endian
        stream = AudioSystem.getAudioInputStream(format, stream);
      }

      // Create the clip
      DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat(), ((int)stream.getFrameLength()*format.getFrameSize()));
      Clip clip = (Clip)AudioSystem.getLine(info);

      if (!clip.isOpen())
        clip.open(stream);

      return clip;
    } catch (java.io.IOException e){
        throw new InternalError("java.io.IOException thrown when reading from ByteArrayInputStream");
      }
  }


}

