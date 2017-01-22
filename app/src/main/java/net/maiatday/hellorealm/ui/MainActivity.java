package net.maiatday.hellorealm.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import net.maiatday.hellorealm.R;
import net.maiatday.hellorealm.model.Mood;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Mood m = realm.createObject(Mood.class);
                m.setId(UUID.randomUUID().toString());
                m.setEnergyLevel(-2);
                m.setMood("Happy");
                m.setNote("testing testing " + new Date().getTime());
                m.setTimestamp(new Date());
            }
        });

       RealmResults<Mood> moods = realm.where(Mood.class).findAll();
        for (Mood m:moods) {
            Log.d(TAG, "mood: " + m.getMood() + " " +m.getNote() + " " + m.getId());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
