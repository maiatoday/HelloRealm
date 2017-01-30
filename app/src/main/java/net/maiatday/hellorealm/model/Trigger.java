package net.maiatday.hellorealm.model;

import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;

import net.maiatday.hellorealm.R;

import java.lang.annotation.Retention;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Class to store trigger
 * Created by maia on 2017/01/22.
 */

public class Trigger extends RealmObject {
    public Trigger() {
        // setId(UUID.randomUUID().toString());
        setTimestamp(new Date());
    }

    public static final String ID = "id";
    public static final String TIMESTAMP = "timestamp";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";

    @Required
    @PrimaryKey
    private String id;
    private String title;
    private String description;
    @Required
    private Date timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // timestamp only set at creation
    private void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    static DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

    public static String shortDateString(Date date) {
        return dateFormat.format(date);
    }

}
