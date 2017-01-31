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

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;

import net.maiatday.hellorealm.model.Mood;
import net.maiatday.hellorealm.model.Trigger;

import java.util.ArrayList;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;


/**
 * A simple {@link DialogFragment} subclass. It provides a list of triggers to choose from.
 * The user can choose none, one or many.
 * Use the {@link TriggerSelectDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TriggerSelectDialogFragment extends AppCompatDialogFragment implements DialogInterface.OnMultiChoiceClickListener {
    private static final String TAG = "TriggerSelectDialogFrag";

    private static final String ARG_MOOD_ID = "MOOD_UUID";

    private String moodUuid;

    /// Text list of the item titles
    private CharSequence[] mItemTitles = {"flopsy", "mopsy", "cottontail"};
    /// Initial checked values. The dialog wants an array of booleans.
    private boolean[] mOriginalCheckedItems = {true, false, false};
    /// Uuids of all the triggers in the list
    private String[] triggerUuids;

    private AppCompatDialog dialog;
    private Realm realmForUIThread;
    private Mood oneMood;
    private OrderedRealmCollection<Trigger> allTriggers;

    public TriggerSelectDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param moodUuid associated mood id
     * @return A new instance of fragment OnMultiSelectListener.
     */
    public static TriggerSelectDialogFragment newInstance(String moodUuid) {
        TriggerSelectDialogFragment fragment = new TriggerSelectDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MOOD_ID, moodUuid);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Static method to show this MultiSelect dialog fragment
     *
     * @param fm             fragment manager
     * @param targetFragment Target fragment if required
     * @param moodUuid       associated alert id
     */
    public static void show(FragmentManager fm,
                            @Nullable Fragment targetFragment,
                            String moodUuid) {
        DialogFragment newFragment = TriggerSelectDialogFragment.newInstance(moodUuid);
        if (targetFragment != null) {
            newFragment.setTargetFragment(targetFragment, 0);
        }
        newFragment.show(fm, TAG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getBundledArguments();

        realmForUIThread = Realm.getDefaultInstance();
        oneMood = realmForUIThread.where(Mood.class).equalTo(Mood.ID, moodUuid).findFirst();
        allTriggers = realmForUIThread.where(Trigger.class).findAll();
        int allTriggersSize = allTriggers.size();
        mItemTitles = new CharSequence[allTriggersSize];
        mOriginalCheckedItems = new boolean[allTriggersSize];
        for (int i = 0; i < allTriggersSize; i++) {
            Trigger t = allTriggers.get(i);
            mItemTitles[i] = t.getTitle();
            mOriginalCheckedItems[i] = oneMood.getTriggers().contains(t) ? true : false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realmForUIThread.close();
    }


    @NonNull
    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        getBundledArguments();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Choose triggers")
                .setMultiChoiceItems(mItemTitles, mOriginalCheckedItems, this)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dialog = builder.create();

        return dialog;
    }


    private void getBundledArguments() {
        if (getArguments() != null) {
            moodUuid = getArguments().getString(ARG_MOOD_ID);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, final int which, final boolean isChecked) {
        realmForUIThread.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                //since we are not using the ui thread realm instance we have to get the objects again
                OrderedRealmCollection<Trigger> triggers = realm.where(Trigger.class).findAll();
                Trigger oneTrigger = triggers.get(which);
                Mood mood = realm.where(Mood.class).equalTo(Mood.ID, moodUuid).findFirst();
                if (isChecked) {
                    if (!mood.getTriggers().contains(oneTrigger)) {
                        mood.getTriggers().add(oneTrigger);
                    }
                } else {
                    if (mood.getTriggers().contains(oneTrigger)) {
                        mood.getTriggers().remove(oneTrigger);
                    }

                }
            }
        });
    }

}
