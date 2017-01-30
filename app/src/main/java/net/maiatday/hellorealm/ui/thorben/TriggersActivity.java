package net.maiatday.hellorealm.ui.thorben;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.maiatday.hellorealm.R;
import net.maiatday.hellorealm.model.Mood;
import net.maiatday.hellorealm.model.Trigger;
import net.maiatday.hellorealm.ui.DividerItemDecoration;

import java.util.UUID;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class TriggersActivity extends AppCompatActivity implements TriggerRecyclerAdapter.OnTriggerItemClickListener {

    private Realm realm;
    private TriggerRecyclerAdapter triggerRealmAdapter;

    public static Intent newIntent(Context context) {
        Intent i = new Intent(context, TriggersActivity.class);
        return i;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triggers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        realm = Realm.getDefaultInstance();
        setUpRecyclerView();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildAndShowInputDialog();
            }
        });
    }

    private void setUpRecyclerView() {
//Shouldn't this query by async?
        RealmResults<Trigger> triggerItems = realm.where(Trigger.class).findAllSorted(Mood.TIMESTAMP, Sort.ASCENDING);
        triggerRealmAdapter = new TriggerRecyclerAdapter(this, triggerItems, true, true, this);
        RealmRecyclerView realmRecyclerView = (RealmRecyclerView) findViewById(R.id.trigger_recycler_view);
        realmRecyclerView.setAdapter(triggerRealmAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }

    private void buildAndShowInputDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(TriggersActivity.this);
        builder.setTitle("Record A Trigger");

        LayoutInflater li = LayoutInflater.from(this);
        View dialogView = li.inflate(R.layout.dialog_view_trigger, null);
        final EditText title = (EditText) dialogView.findViewById(R.id.dialog_title);
        final EditText description = (EditText) dialogView.findViewById(R.id.dialog_description);

        builder.setView(dialogView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addTriggerItem(title.getText().toString(), description.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.show();
        title.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE ||
                                (event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                            dialog.dismiss();
                            addTriggerItem(title.getText().toString(), description.getText().toString());
                            return true;
                        }
                        return false;
                    }
                });
    }

    private void addTriggerItem(String title, String description) {
        if (TextUtils.isEmpty(title)) {
            Toast
                    .makeText(this, "no title!", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        realm.beginTransaction();
        String uuid = UUID.randomUUID().toString();
        Trigger item = realm.createObject(Trigger.class, uuid);
        item.setTitle(title);
        item.setDescription(description);
        realm.commitTransaction();
    }

    @Override
    public void onItemClick(Trigger data) {

    }
}
