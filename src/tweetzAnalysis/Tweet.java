package tweetzAnalysis;

import java.util.Date;

/**
 * Created by Sebastian Ruf on 28.03.14.
 */
public class Tweet {


    private int id = 0;
    private Date datum = null;
    private String message = "";
    private String author = "";
    private int follower = 0;
    private String location = "";
    private String lang = "";
    private String foundInQuery = "";

    public Tweet(int id, Date datum, String message, String author, int follower, String location, String lang, String foundInQuery) {
        this.id = id;
        this.datum = datum;
        this.message = message;
        this.author = author;
        this.follower = follower;
        this.location = location;
        this.lang = lang;
        this.foundInQuery = foundInQuery;
    }
}
