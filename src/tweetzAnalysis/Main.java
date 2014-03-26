package tweetzAnalysis;

import java.util.Observable;
import java.util.Observer;

import twitter4j.Query;
import twitter4j.Status;
import twitter4j.TwitterException;

public class Main implements Observer {

    /**
     * Total number of requests to the servers of Twitter
     */
    private int requests = 10;
    /**
     * number of current request
     */
    private int j = 0;

    public static void main(String[] args) throws Exception {
        // Initialize tweetzAnalysis.Main class
        Main main = new Main();
        // get tweetzAnalysis.TweetsProvider
        TweetsProvider provider = TweetsReader.getInstance();
        // register Observer that is notified whenever a sample of Tweets is
        // available
        provider.addObserver(main);
        // initialize the tweetzAnalysis.TweetsProvider with the "login" data , TWEETFACTZ
        provider.init("gURk1pnbKhtE7fT6lZEU5g",
                "HdVDK0yOO59kzyjG7iBEbp5Ic6g3wE8qvXjprokC9ko",
                "2384961332-lDTNArIrRvIYvliCkKa2S5nwziuE73KUMjr6nHG",
                "BNDLic7yXJ0teMwj1UCv3skSV7VtmSEWKUMGxWmwMm1o9");
        // initialize a query used to filter the incoming data stream
        Query query = new Query("Obama:)"); // " " wie Oder ;  "-" ohne
        // set language to filter
        query.setLang("en");
        // start loading the samples from the server...
        // hint: use null as input for loadSamples-method in order to access any Tweets
        // to know: Use Query query = new Query() without any configurations may
        // cause an exception!
        provider.loadSample(query);
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        // TODO Auto-generated method stub
        // state determines who (tweetzAnalysis.TweetsProvider or tweetzAnalysis.Timer-Thread) invoked that
        // method
        int state;
        state =  (Integer)arg1;
        TweetsProvider provider = TweetsReader.getInstance();
        switch (state) {
            case TweetsReader.LOADED_TWEETS_AVAILABLE:
                // the Tweets are saved in the statuses array
                Status[] statuses = provider.getSample();
                // do something
                int i = 0;
                while (statuses[i] != null) {
                    i++;
                }
                Status tweet = statuses[0];
                if (tweet != null) {
                    String userData = "Tweet ist null..";
                    if (tweet.getUser() != null) {
                        userData = "\nAuthor: "+ tweet.getUser().getName() +"\nPlace: "+ tweet.getUser().getLocation() +
                                "\nLang: "+ tweet.getUser().getLang() +"\n";
                    }
                    System.out.println("TweetId: "+ tweet.getId() +"\nText: "+ tweet.getText() + userData);
                } else {
                    System.out.println("Tweet ist Null..");
                }
                System.out.println("Number of Tweets in lap "+ j +": "+ i);
                if (++j < requests) {
                    // call a new tweetzAnalysis.Timer Thread that will signal the update method
                    // (flag WAITING_FINISHED), if further Tweets
                    // can be obtained or rather the 15 minute (+ 2 safety margin!)
                    // window is over.
                    waitForMillis(17 * 60000);
                } else {
                    // close data stream to Twitter..
                    provider.close();
                    System.exit(0);
                }
                break;
            case Timer.WAITING_FINISHED:
                try {
                    // start loading another sample...
                    // WARNING! Do not call the loadSample method before
                    // WAITINIG_FINISHED has been signaled!
                    // That may cause an exception, because there is a 15 minute
                    // window to wait,
                    Query query = new Query("AcDc");
                    query.setLang("en");
                    provider.loadSample(query);
                } catch (TwitterException e) {
                    // TODO Auto-generated catch block
                    System.out.println("Exception: " + e.getMessage());
                    System.out.println("Cause: " + e.getCause());
                    System.out.println("cause is rate limit exceeded: "
                            + e.exceededRateLimitation());
                }
                break;
        }
    }

    /**
     * Calls a new instance of tweetzAnalysis.Timer Thread, which waits for specified time.
     *
     * @param millis
     *            time
     */
    private void waitForMillis(int millis) {
        Timer timer = new Timer(millis, false);
        timer.addObserver(this);
        timer.start();
    }



}

