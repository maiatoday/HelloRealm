/*
 * MIT License
 *
 * Copyright (c) 2017 Maia Grotepass
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.maiatday.hellorealm.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import net.maiatday.hellorealm.R;
import net.maiatday.hellorealm.model.Mood;
import net.maiatday.hellorealm.model.Trigger;
import net.maiatday.hellorealm.service.WorkerIntentService;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class OneMoodActivity extends AppCompatActivity {
    private static final String TAG = "OneMoodActivity";
    private static final String KEY_MOOD_UUID = "mood_uuid";
    Realm realmForUIThread;
    Mood oneMood;
    private TextView textView;
    private EditText editTextMain;
    private EditText editTextWorker;
    private ImageView currentMoodImage;
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
        currentMoodImage = (ImageView) findViewById(R.id.imageCurrentMood);
        realmForUIThread = Realm.getDefaultInstance();
        // Example add a change listener for the whole db
//        realmForUIThread.addChangeListener(realmChangeListener);
        if (TextUtils.isEmpty(uuid)) {
            realmForUIThread.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    // Example of creating or updating an object and copying it into the realm
//                    uuid = UUID.randomUUID().toString();
//                    Mood m = new Mood();
//                    m.setId(uuid);
//                    realm.copyToRealmOrUpdate(m);

                    // Example of creating a new object
                    uuid = UUID.randomUUID().toString();
                    realm.createObject(Mood.class, uuid);
                }
            });
        }
        oneMood = searchMood(realmForUIThread, uuid);
        //Example add a change listener for only this mood
        RealmObject.addChangeListener(oneMood, moodListener);

        updateUI();
    }

    private Mood searchMood(Realm aRealm, String uuid) {
        return aRealm.where(Mood.class).equalTo(Mood.ID, uuid).findFirst();
    }

    private void updateUI() {
        if (textView != null &&
                energyBar != null &&
                currentMoodImage != null &&
                oneMood != null) {
            //energyBar.setProgress(oneMood.getEnergyLevel(), true);
            textView.setText(oneMood.toString());
            currentMoodImage.setImageResource(Mood.moodToDrawableId(oneMood.getMood()));
            editTextMain.setText(oneMood.getNote());
            editTextWorker.setText(oneMood.getNote());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        realmForUIThread.removeAllChangeListeners();
        RealmObject.removeChangeListener(oneMood, moodListener);
        realmForUIThread.close();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.button_update_main) {
            //Example: of update on the UI thread - not recommended o.O
            realmForUIThread.beginTransaction();
            Mood m = searchMood(realmForUIThread, uuid);
            m.setNote(editTextMain.getText().toString());
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
            }
        });
    }

    public void onTriggerClick(View view) {
        TriggerSelectDialogFragment.show(getSupportFragmentManager(), null, oneMood.getId());
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


//    private RealmChangeListener<Realm> realmChangeListener = new RealmChangeListener<Realm>() {
//        @Override
//        public void onChange(Realm element) {
//            updateUI();
//        }
//    };

    private RealmChangeListener<Mood> moodListener = new RealmChangeListener<Mood>() {
        @Override
        public void onChange(Mood mood) {
            updateUI();
        }
    };

}
