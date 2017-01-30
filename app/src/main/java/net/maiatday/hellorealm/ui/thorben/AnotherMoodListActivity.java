package net.maiatday.hellorealm.ui.thorben;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import net.maiatday.hellorealm.R;
import net.maiatday.hellorealm.model.Mood;
import net.maiatday.hellorealm.ui.DividerItemDecoration;
import net.maiatday.hellorealm.ui.OneMoodActivity;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class AnotherMoodListActivity extends AppCompatActivity implements MoodRecyclerAdapter.OnMoodItemClickListener {
    private static final int REQUEST_NEW_MOOD = 200;
    private static final int REQUEST_OLD_MOOD = 201;
    private Realm realm;
    private MoodRecyclerAdapter moodRealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_mood_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        realm = Realm.getDefaultInstance();
        setUpRecyclerView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(OneMoodActivity.newIntent(AnotherMoodListActivity.this, ""), REQUEST_NEW_MOOD);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }

    private void setUpRecyclerView() {
        //Shouldn't this query by async?
        RealmResults<Mood> moodItems = realm.where(Mood.class).findAllSorted(Mood.TIMESTAMP, Sort.ASCENDING);
        moodRealAdapter = new MoodRecyclerAdapter(this, moodItems, true, true, this);
        RealmRecyclerView realmRecyclerView = (RealmRecyclerView) findViewById(R.id.realm_recycler_view);
        realmRecyclerView.setAdapter(moodRealAdapter);
        realmRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case REQUEST_NEW_MOOD:
            case REQUEST_OLD_MOOD:
                moodRealAdapter.notifyDataSetChanged(); //Why do I need to do this to update?
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(Mood item) {
        final String id = item.getId();
        startActivityForResult(OneMoodActivity.newIntent(AnotherMoodListActivity.this, id), REQUEST_OLD_MOOD);
    }
}
