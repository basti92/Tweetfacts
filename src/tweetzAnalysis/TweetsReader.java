package tweetzAnalysis;

import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

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

    /**
     * Storage for the Tweets
     */
    private Status[] statuses;

    public static final int MAX_TWEETS_PER_QUERY = 100;
    public static final int MIN_TWEETS_PER_QUERY = 0;
    private int numberOfTweetsPerRequest = MAX_TWEETS_PER_QUERY;

    public static final int MAX_NUMBER_OF_REQUESTS = 180;
    public static final int LOADED_TWEETS_AVAILABLE = 2;

    /**
     * Connection to Twitter public streaming endpoint
     */
    private Twitter twitterStream;
    /**
     * holdOn == true => statuses list is filled with
     * -------------
     * Tweets holdOn == false => currently no Tweets are recorded
     */
    private boolean holdOn;

    private static TweetsReader instance;

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

    @Override
    public void init(String consumerKey, String consumerSecret,
                     String accessToken, String accessTokenSecret) {
        if (getTwitterStream() != null) {
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
        if (getTwitterStream() == null) {
            return;
        }
        // reset old values retrieved from the previous query
        for (int i = 0; i < statuses.length; i++) {
            statuses[i] = null;
        }
        int pos = 0;
        int oldSize = 0;
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
                for (Status tweet : tweets) {
                    long id = tweet.getId();
                    oldSize = set.size();
                    set.add(id);
                    if (set.size() > oldSize) {
                        oldSize = set.size();
                        statuses[pos++] = tweet;
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

    @Override
    public Status[] getSample() {
        // TODO Auto-generated method stub
        return statuses;
    }

    @Override
    public void addObserver(Observer observer) {
        // TODO Auto-generated method stub
        super.addObserver(observer);
    }

    @Override
    public boolean close() {
        // TODO Auto-generated method stub
        closeTwitterStream();
        return true;
    }

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
    private void closeTwitterStream() {
        if (getTwitterStream() != null) {
           getTwitterStream().shutdown();
        }
    }

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

    @Override
    public void onRateLimitReached(RateLimitStatusEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
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

