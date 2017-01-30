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
 * Class to store mood
 * Created by maia on 2017/01/22.
 */

public class Mood extends RealmObject {
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

    public static final String ID = "id";
    public static final String TIMESTAMP = "timestamp";

    @Required
    @PrimaryKey
    private String id;
    @PossibleMood
    private int mood;
    private String note;
    private int energyLevel;
    @Required
    private Date timestamp;

    RealmList<Trigger> triggers;

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

    // timestamp only set at creation
    private void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public RealmList<Trigger> getTriggers() {
        return triggers;
    }

    public void setTriggers(RealmList<Trigger> triggers) {
        this.triggers = triggers;
    }

    @DrawableRes
    public static int moodToDrawableId(@PossibleMood int m) {
        switch (m) {
            case SUPER_SAD:
                return R.drawable.ic_mood_super_sad;
            case SAD:
                return R.drawable.ic_mood_sad;
            case MEH:
                return R.drawable.ic_mood_meh;
            case HAPPY:
                return R.drawable.ic_mood_happy;
            case SUPER_HAPPY:
                return R.drawable.ic_mood_super_happy;
            default:
                return R.drawable.ic_mood_meh;
        }
    }

    @PossibleMood
    public static int idToMood(@DrawableRes int i) {
        switch (i) {
            case R.drawable.ic_mood_super_sad:
                return SUPER_SAD;
            case R.drawable.ic_mood_sad:
                return SAD;
            case R.drawable.ic_mood_meh:
                return MEH;
            case R.drawable.ic_mood_happy:
                return HAPPY;
            case R.drawable.ic_mood_super_happy:
                return SUPER_HAPPY;
            default:
                return MEH;
        }
    }

    static DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

    public static String shortDateString(Date date) {
        return dateFormat.format(date);
    }

}
