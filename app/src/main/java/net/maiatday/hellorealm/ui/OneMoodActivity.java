package net.maiatday.hellorealm.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

public class OneMoodActivity extends AppCompatActivity {
    private static final String TAG = "OneMoodActivity";
    private static final String KEY_MOOD_UUID = "mood_uuid";
    Realm realmForUIThread;
    Mood firstMood;
    private TextView textView;
    private EditText editTextMain;
    private EditText editTextWorker;
    private ImageView currentMood;
    private SeekBar energyBar;
    private String uuid;

    public static Intent newIntent(Context context, String uuid) {
        Intent i = new Intent(context, OneMoodActivity.class);
        i.putExtra(KEY_MOOD_UUID, uuid);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uuid = getIntent().getStringExtra(KEY_MOOD_UUID);
        setContentView(R.layout.activity_one_mood);
        textView = (TextView) findViewById(R.id.text_first_mood);
        editTextMain = (EditText) findViewById(R.id.text_main);
        editTextWorker = (EditText) findViewById(R.id.text_worker);
        energyBar = (SeekBar) findViewById(R.id.energyBar);
        energyBar.setOnSeekBarChangeListener(seekBarChangeListener);
        currentMood = (ImageView) findViewById(R.id.imageCurrentMood);
        realmForUIThread = Realm.getDefaultInstance();
        realmForUIThread.addChangeListener(realmChangeListener);
        if (TextUtils.isEmpty(uuid)) {
            realmForUIThread.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Mood m = new Mood();
                    uuid = m.getId();
//                    m.setEnergyLevel(-2);
//                    m.setMood(Mood.MEH);
//                    m.setNote("testing testing");
//                    m.setTimestamp(new Date());
                    realm.copyToRealmOrUpdate(m);
                }
            });
        }
        firstMood = searchMood(realmForUIThread, uuid);

        RealmResults<Mood> moods = realmForUIThread.where(Mood.class).findAll();
        for (Mood m : moods) {
            Log.d(TAG, "mood: " + m.getMood() + " " + m.getNote() + " " + m.getId());
        }
        updateUI();
    }

    private Mood searchMood(Realm aRealm, String uuid) {
        return aRealm.where(Mood.class).equalTo("id", uuid).findFirst();
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
        realmForUIThread.removeAllChangeListeners();
        realmForUIThread.close();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.button_update_main) {
            //Example: of update on the UI thread - not recommended o.O
            realmForUIThread.beginTransaction();
            Mood m = searchMood(realmForUIThread, uuid);
            m.setNote(editTextMain.getText().toString());
            m.setTimestamp(new Date());
            realmForUIThread.commitTransaction();
        } else {
            //Example: ask the intent service to do the update
            WorkerIntentService.startUpdateFirstNote(this, uuid, editTextWorker.getText().toString());
        }
    }

    public void onMoodClick(View view) {
        int id = view.getId();
        @Mood.PossibleMood int moodInt = Mood.MEH;
        switch (id) {
            case R.id.buttonSuperSad:
                moodInt = Mood.SUPER_SAD;
                break;
            case R.id.buttonSad:
                moodInt = Mood.SAD;
                break;
            case R.id.buttonMeh:
                moodInt = Mood.MEH;
                break;
            case R.id.buttonHappy:
                moodInt = Mood.HAPPY;
                break;
            case R.id.buttonSuperHappy:
                moodInt = Mood.SUPER_HAPPY;
                break;
        }
        final int finalMood = moodInt;
        //Example: update in an async transaction
        realmForUIThread.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Mood m = searchMood(realm, uuid);
                m.setMood(finalMood);
                m.setTimestamp(new Date());
            }
        });
    }


    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, final int progressValue, boolean fromUser) {
            //Example: update in an async transaction
            realmForUIThread.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Mood m = searchMood(realm, uuid);
                    m.setEnergyLevel(progressValue);
                    m.setTimestamp(new Date());
                }
            });
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
