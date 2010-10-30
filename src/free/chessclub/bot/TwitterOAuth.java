package free.chessclub.bot;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.OAuth;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.File;

public class TwitterOAuth {

    // This is the consumer key for this application
    private String consumer_key = "zrt6HHIid1RkxHCJbjmpA";

    // This is the consumer secret for this application
    private String consumer_secret = "EmAT1is5r9GeGHOcF9CNIyVvJet8lNDuNuzqSOj2M";
    private String twitter_request_token = "http://twitter.com/oauth/request_token";
    private String twitter_access_token = "http://twitter.com/oauth/access_token";
    private String twitter_authorize = "http://twitter.com/oauth/authorize";
    private OAuthConsumer consumer = new DefaultOAuthConsumer(consumer_key, consumer_secret);
    private OAuthProvider provider = new DefaultOAuthProvider(twitter_request_token, twitter_access_token, twitter_authorize);

    public void connectToTwitter() throws Exception {

        // This file stores the authentication information
        File configInfo = new File("twitter_Auth.cfg");

        // If this file does not exist, this is the first run of the application
        if (!configInfo.exists()) {

            String authURL = provider.retrieveRequestToken(consumer, OAuth.OUT_OF_BAND);

            System.out.println("\n\n Looks like this is the first time you are running this application!\n");

            Scanner stdin = new Scanner(System.in);

            System.out.println("Please enter the PIN code that you see in your browser");

            // Now, we open a web browser pointing to this URL in order to get the PIN code of the user
            Desktop desktopAccess = Desktop.getDesktop();
            desktopAccess.browse(new URI(authURL));

            System.out.print("\nPIN : ");

            String PIN = stdin.nextLine();

            provider.retrieveAccessToken(consumer, PIN);

            String access_token = consumer.getToken();
            String token_secret = consumer.getTokenSecret();

            consumer.setTokenWithSecret(access_token, token_secret);

            // Write the token, secret, and pin to a file

            FileWriter fWrite = new FileWriter(configInfo);
            fWrite.write(access_token + "\n");
            fWrite.write(token_secret + "\n");
            fWrite.write(PIN + "\n");
            fWrite.close();

            URL url = new URL("http://twitter.com/statuses/mentions.xml");
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            consumer.sign(request);
            request.connect();

            // A response code of 200 indicates success
            System.out.println(request.getResponseCode() + " : " + request.getResponseMessage());

        } else {

            //If the config file does exist, load the key, secret, and pin
            Scanner fileReader = new Scanner(configInfo);

            String access_token = fileReader.nextLine().trim(); // Line 1
            String token_secret = fileReader.nextLine().trim(); // Line 2

            consumer.setTokenWithSecret(access_token, token_secret);

            URL url = new URL("http://twitter.com/statuses/mentions.xml");
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            consumer.sign(request);
            request.connect();

            // A response code of 200 indicates success
            System.out.println(request.getResponseCode() + " : " + request.getResponseMessage());

        }
    }

    public static void main (String [] args) throws Exception {
        TwitterOAuth oauthTester = new TwitterOAuth();
        oauthTester.connectToTwitter();
    }
}
