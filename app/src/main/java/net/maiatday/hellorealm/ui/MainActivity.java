package net.maiatday.hellorealm.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.maiatday.hellorealm.R;
import net.maiatday.hellorealm.model.Mood;
import net.maiatday.hellorealm.service.WorkerIntentService;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    Realm realm;
    Mood firstMood;
    private TextView textView;
    private EditText editTextMain;
    private EditText editTextWorker;

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
        textView = (TextView) findViewById(R.id.text_first_mood);
        editTextMain = (EditText) findViewById(R.id.text_main);
        editTextWorker = (EditText) findViewById(R.id.text_worker);
        realm = Realm.getDefaultInstance();
        realm.addChangeListener(realmChangeListener);
        RealmResults<Mood> moods = realm.where(Mood.class).findAll();
        if (moods.isEmpty()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Mood m = realm.createObject(Mood.class);
                    m.setId(UUID.randomUUID().toString());
                    m.setEnergyLevel(-2);
                    m.setMood(Mood.MEH);
                    m.setNote("testing testing");
                    m.setTimestamp(new Date());
                }
            });
        }

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
        if (view.getId() == R.id.button_update_main) {
            realm.beginTransaction();
            Mood m = realm.where(Mood.class).findFirst();
            m.setNote(editTextMain.getText().toString());
            m.setTimestamp(new Date());
            realm.commitTransaction();
        } else {
            WorkerIntentService.startUpdateFirstNote(this, editTextWorker.getText().toString());
        }
    }
}
