package tweetzAnalysis;

import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

//twitter4j
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Provides ONE standing connection to Twitter public streaming points used to
 * obtain Tweets of several unknown users.
 *
 * @author Christopher Kerth
 *
 */
public class TweetsReader extends Observable implements TweetsProvider,
        RateLimitStatusListener {


    //constants
    public static final int MAX_TWEETS_PER_QUERY = 1500;
    public static final int MIN_TWEETS_PER_QUERY = 0;
    public static final int MAX_NUMBER_OF_REQUESTS = 1500;
    public static final int LOADED_TWEETS_AVAILABLE = 500;


    private int numberOfTweetsPerRequest = MAX_TWEETS_PER_QUERY;

    private Status[] statuses; // Storage for the Tweets

    private Twitter twitterStream; //Connection to Twitter public streaming endpoint

    private boolean holdOn;// holdOn == true => statuses list is filled with, Tweets holdOn == false => currently no Tweets are recorded

    private static TweetsReader instance;



    //Constructor, initializes Status array
    private TweetsReader() {
        statuses = new Status[MAX_NUMBER_OF_REQUESTS * MAX_TWEETS_PER_QUERY];
    }

    public static synchronized TweetsReader getInstance() {
        if (instance == null) {
            instance = new TweetsReader();
        }
        return instance;
    }

    // Implementation of interface tweetzAnalysis.TweetsProvider

    //establish connection to twitter

    public void init(String consumerKey, String consumerSecret,
                     String accessToken, String accessTokenSecret) {
        if (getTwitterStream() != null) { //cancel if connections is already established
            return;
        }

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret)
                .setJSONStoreEnabled(true);
        openTwitterStream(cb);
    }


    public void loadSample(Query query) throws TwitterException {

        setHoldOn(false);

        //check connection
        if (getTwitterStream() == null) {
            return;
        }

        // reset old values retrieved from the previous query
        for (int i = 0; i < statuses.length; i++) {
            statuses[i] = null;
        }


        int pos = 0;
        int oldSize = 0;

        //catch empty query
        if (query == null) {
            query = createDefaultQuery();
        }


        Set<Long> set = new HashSet<Long>();
        query.setCount(getNumberOfTweetsPerRequest());
        try {
            QueryResult result = null;
            if (!isHoldOn()) {
                result = getTwitterStream().search(query);
            }

            if (result == null) {
                return;
            }
            do {
                List<Status> tweets = result.getTweets();
                //Liste mit Tweets durchlaufen und in Status Array abspeichern
                for (Status tweet : tweets) {
                    long id = tweet.getId();
                    oldSize = set.size();
                    set.add(id); // IDs werden zu Menge set hinzugefügt
                    if (set.size() > oldSize) {
                        oldSize = set.size();
                        statuses[pos++] = tweet; //Tweets werden in Status Array gespeichert
                    }
                }

                // get tweets in the next page if any
                query = result.nextQuery();
                if (query != null) {
                    result = getTwitterStream().search(query);
                }
            } while (!isHoldOn() && query != null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        notifyChange();
    }

    private Query createDefaultQuery() {
        String search = "";
        for (int i = 65; i < 91; i++) {
            search += " OR " + (char) i;
        }
        return new Query(search);
    }


    public Status[] getSample() {
        // TODO Auto-generated method stub
        return statuses;
    }


    public void addObserver(Observer observer) {
        // TODO Auto-generated method stub
        super.addObserver(observer);
    }


//    public boolean close() {
//        // TODO Auto-generated method stub
//        closeTwitterStream();
//        return true;
//    }

    // Helper methods

    /**
     * Establishes a connection to the server using the provided settings.
     *
     * @param settings
     *            OAuth consumer key, OAuth secret key, access token secret, ...
     */
    private void openTwitterStream(ConfigurationBuilder settings) {
        Twitter twitter = new TwitterFactory(settings.build()).getInstance();
        twitter.addRateLimitStatusListener(this);
        setTwitterStream(twitter);
    }

    /**
     * Closes the connection to the server.
     */
//    private void closeTwitterStream() {
//        if (getTwitterStream() != null) {
//           getTwitterStream().shutdown();
//        }
//    }




    // Getters and Setters
    private Twitter getTwitterStream() {
        return twitterStream;
    }

    private void setTwitterStream(Twitter twitterStream) {
        this.twitterStream = twitterStream;
    }

    private boolean isHoldOn() {
        return holdOn;
    }

    private void setHoldOn(boolean holdOn) {
        this.holdOn = holdOn;
    }

    public int getNumberOfTweetsPerRequest() {
        return numberOfTweetsPerRequest;
    }


    public void onRateLimitReached(RateLimitStatusEvent arg0) {
        // TODO Auto-generated method stub

    }


    public void onRateLimitStatus(RateLimitStatusEvent arg0) {
        // TODO Auto-generated method stub
        RateLimitStatus rateLimit = arg0.getRateLimitStatus();
        if (isHoldOn() && rateLimit.getRemaining() != rateLimit.getLimit()) {
            return;
        }
        setHoldOn(false);
        int remaining = rateLimit.getRemaining();
        if (remaining == 2) {
            notifyChange();
        }
    }

    /**
     * Invokes the update method of each Observer.
     */
    private void notifyChange() {
        setHoldOn(true);
        setChanged();
        notifyObservers(LOADED_TWEETS_AVAILABLE);
    }

}

