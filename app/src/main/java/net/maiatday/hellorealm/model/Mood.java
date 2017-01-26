package net.maiatday.hellorealm.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.util.Date;
import java.util.UUID;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Class to store mood
 * Created by maia on 2017/01/22.
 */

@RealmClass
public class Mood implements RealmModel {
    public Mood() {
       // setId(UUID.randomUUID().toString());
        setTimestamp(new Date());
    }

    @Retention(SOURCE)
    @IntDef({SUPER_HAPPY, HAPPY, MEH, SAD, SUPER_SAD})
    public @interface PossibleMood {
    }

    public static final int SUPER_HAPPY = 2;
    public static final int HAPPY = 1;
    public static final int MEH = 0;
    public static final int SAD = -1;
    public static final int SUPER_SAD = -2;

    @Required
    @PrimaryKey
    private String id;
    @PossibleMood
    private int mood;
    private String note;
    private int energyLevel;
    @Required
    private Date timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @PossibleMood
    public int getMood() {
        return mood;
    }

    public void setMood(@PossibleMood int mood) {
        this.mood = mood;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getEnergyLevel() {
        return energyLevel;
    }

    public void setEnergyLevel(int energyLevel) {
        this.energyLevel = energyLevel;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
