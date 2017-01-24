package net.maiatday.hellorealm.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.maiatday.hellorealm.R;
import net.maiatday.hellorealm.model.Mood;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    Realm realm;
    Mood firstMood;
    private Button button;
    private TextView textView;
    private RealmChangeListener<Realm> realmChangeListener = new RealmChangeListener<Realm>() {
        @Override
        public void onChange(Realm element) {
            updateUI();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);
        realm = Realm.getDefaultInstance();
        realm.addChangeListener(realmChangeListener);
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
        for (Mood m : moods) {
            Log.d(TAG, "mood: " + m.getMood() + " " + m.getNote() + " " + m.getId());
        }
        firstMood = realm.where(Mood.class).findFirst();
        updateUI();
    }

    private void updateUI() {
        if (textView != null && firstMood != null) {
            textView.setText(firstMood.getNote() + " " + firstMood.getTimestamp().toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.removeAllChangeListeners();
        realm.close();
    }

    public void onClick(View view) {
        realm.beginTransaction();
        Mood m = realm.where(Mood.class).findFirst();
        m.setNote("hehe");
        m.setTimestamp(new Date());
        realm.commitTransaction();
    }
}
