package free.chessclub.bot;
/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.*;
import java.util.Scanner;

/**
 * Example application that uses OAuth method to acquire access to your account.<br>
 * This application illustrates how to use OAuth method with Twitter4J.<br>
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public final class TwitterManager {
  Twitter twitter;

  public static void main(String[] args) {
    TwitterManager tm = new TwitterManager();
    tm.connect();
    try {
      tm.tweet("I am DonKEyFacTOrY!@#");
    } catch (TwitterException e) {
      e.printStackTrace();
    }
  }

  public void connect() {
    try {
      File configInfo = new File("twitter_Auth.cfg");
      Scanner fileReader = new Scanner(configInfo);
      String consumerKeyStr = fileReader.nextLine().trim();
      String consumerSecretStr = fileReader.nextLine().trim();
      String accessTokenStr = fileReader.nextLine().trim();
      String tokenSecretStr = fileReader.nextLine().trim();
      //String pinStr = fileReader.nextLine().trim();

      twitter = new TwitterFactory().getInstance();
      twitter.setOAuthConsumer(consumerKeyStr, consumerSecretStr);
      twitter.setOAuthAccessToken(new AccessToken(accessTokenStr, tokenSecretStr));
    } catch (IOException ioe) {
      ioe.printStackTrace();
      System.out.println("Failed to read the system input.");
      System.exit(-1);
    }
  }

  public void tweet(String tweet) throws TwitterException {
    Status status = twitter.updateStatus(tweet);
    System.out.println("Successfully updated the status to [" + status.getText() + "].");
  }
}
