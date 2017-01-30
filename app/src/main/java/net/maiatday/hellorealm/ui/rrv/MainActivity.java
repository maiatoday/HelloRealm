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

package net.maiatday.hellorealm.ui.rrv;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import net.maiatday.hellorealm.R;
import net.maiatday.hellorealm.model.Mood;
import net.maiatday.hellorealm.ui.DividerItemDecoration;
import net.maiatday.hellorealm.ui.OneMoodActivity;
import net.maiatday.hellorealm.ui.thorben.AnotherMoodListActivity;
import net.maiatday.hellorealm.ui.thorben.TriggersActivity;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_NEW_MOOD = 100;
    private static final int REQUEST_OLD_MOOD = 101;

    private Realm realm;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        realm = Realm.getDefaultInstance();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        setUpRecyclerView();

        RealmResults<Mood> moods = realm.where(Mood.class).findAll();
        for (Mood m : moods) {
            Log.d(TAG, "mood: " + m.getMood() + " " + m.getNote() + " " + m.getId());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(OneMoodActivity.newIntent(MainActivity.this, ""), REQUEST_NEW_MOOD);
            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_triggers:
                                startActivity(TriggersActivity.newIntent(MainActivity.this));
                                break;

                            case R.id.action_alternative:
                                startActivity(AnotherMoodListActivity.newIntent(MainActivity.this));
                                break;

                            case R.id.action_settings:
                                break;

                        }
                        return true;
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RealmMoodRecyclerAdapter(this, realm.where(Mood.class).findAllSortedAsync(Mood.TIMESTAMP, Sort.ASCENDING)));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    public void deleteItem(Mood item) {
        final String id = item.getId();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Mood.class).equalTo(Mood.ID, id)
                        .findAll()
                        .deleteAllFromRealm();
            }
        });
    }

    public void openItem(Mood item) {
        final String id = item.getId();
        startActivityForResult(OneMoodActivity.newIntent(MainActivity.this, id), REQUEST_OLD_MOOD);
    }

}
