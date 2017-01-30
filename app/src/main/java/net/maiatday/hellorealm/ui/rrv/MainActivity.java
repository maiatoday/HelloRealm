package net.maiatday.hellorealm.ui.rrv;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import net.maiatday.hellorealm.R;
import net.maiatday.hellorealm.model.Mood;
import net.maiatday.hellorealm.ui.DividerItemDecoration;
import net.maiatday.hellorealm.ui.OneMoodActivity;

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
