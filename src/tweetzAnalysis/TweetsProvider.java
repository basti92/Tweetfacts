package tweetzAnalysis;

import java.util.Observer;

import twitter4j.Query;
import twitter4j.Status;
import twitter4j.TwitterException;

/**
 * Describes the attributes a Tweets data provider must actually have!
 *
 * @author Christopher Kerth
 *
 */
public interface TweetsProvider {

    /**
     * Initializes the usage of Twitter API. Nothing works without this
     * function.
     *
     * @param consumerKey
     * @param consumerSecret
     * @param accessToken
     * @param accessTokenSecret
     */
    public void init(String consumerKey, String consumerSecret,
                     String accessToken, String accessTokenSecret);

    /**
     * Starts to load a sample of Tweets using a filter query. This method tries
     * to download as many Tweets as possible. The maximum number of Tweets per sample is 18000.
     *
     * @param query
     *            query used to filter the data stream
     */
    public void loadSample(Query query) throws TwitterException;

    /**
     * Retrieves the sample previously loaded by the method loadSample().
     *
     * @return List of Tweets
     */
    public Status[] getSample();

    /**
     * Closes the connection to the server, file system, ...
     */
    public boolean close();

    /**
     * Adds an Observer, which is updated when loading the samples has been
     * completed.
     *
     * @param observer
     */
    public void addObserver(Observer observer);

}

