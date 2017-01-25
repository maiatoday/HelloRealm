package net.maiatday.hellorealm.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
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
    private ImageView currentMood;
    private SeekBar energyBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.text_first_mood);
        editTextMain = (EditText) findViewById(R.id.text_main);
        editTextWorker = (EditText) findViewById(R.id.text_worker);
        energyBar = (SeekBar) findViewById(R.id.energyBar);
        energyBar.setOnSeekBarChangeListener(seekBarChangeListener);
        currentMood = (ImageView) findViewById(R.id.imageCurrentMood);
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
        if (textView != null &&
                energyBar != null &&
                currentMood != null &&
                firstMood != null) {
            //energyBar.setProgress(firstMood.getEnergyLevel(), true);
            textView.setText(firstMood.toString());
            switch (firstMood.getMood()) {
                case Mood.SUPER_SAD:
                    currentMood.setImageResource(R.drawable.ic_mood_super_sad);
                    break;
                case Mood.SAD:
                    currentMood.setImageResource(R.drawable.ic_mood_sad);
                    break;
                case Mood.MEH:
                    currentMood.setImageResource(R.drawable.ic_mood_meh);
                    break;
                case Mood.HAPPY:
                    currentMood.setImageResource(R.drawable.ic_mood_happy);
                    break;
                case Mood.SUPER_HAPPY:
                    currentMood.setImageResource(R.drawable.ic_mood_super_happy);
                    break;
            }
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

    public void onMoodClick(View view) {
        int id = view.getId();
        @Mood.PossibleMood int mood = Mood.MEH;
        switch (id) {
            case R.id.buttonSuperSad:
                mood = Mood.SUPER_SAD;
                break;
            case R.id.buttonSad:
                mood = Mood.SAD;
                break;
            case R.id.buttonMeh:
                mood = Mood.MEH;
                break;
            case R.id.buttonHappy:
                mood = Mood.HAPPY;
                break;
            case R.id.buttonSuperHappy:
                mood = Mood.SUPER_HAPPY;
                break;
        }
        realm.beginTransaction();
        Mood m = realm.where(Mood.class).findFirst();
        m.setMood(mood);
        m.setTimestamp(new Date());
        realm.commitTransaction();
    }


    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
            realm.beginTransaction();
            Mood m = realm.where(Mood.class).findFirst();
            m.setEnergyLevel(progressValue);
            m.setTimestamp(new Date());
            realm.commitTransaction();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
           // do nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
           // do nothing
        }
    };


    private RealmChangeListener<Realm> realmChangeListener = new RealmChangeListener<Realm>() {
        @Override
        public void onChange(Realm element) {
            updateUI();
        }
    };
}
