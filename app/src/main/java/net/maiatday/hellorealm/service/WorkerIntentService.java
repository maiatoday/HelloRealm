package net.maiatday.hellorealm.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import net.maiatday.hellorealm.model.Mood;

import java.util.Date;

import io.realm.Realm;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class WorkerIntentService extends IntentService {
    private static final String ACTION_UPDATE_FIRST_NOTE = "net.maiatday.hellorealm.service.action.UPDATE_FIRST_NOTE";

    private static final String EXTRA_NOTE_STRING = "net.maiatday.hellorealm.service.extra.NOTE_STRING";

    public WorkerIntentService() {
        super("WorkerIntentService");
    }

    /**
     * Starts this service to update the note string of the first Mood
     *
     * @see IntentService
     */
    public static void startUpdateFirstNote(Context context, String note) {
        Intent intent = new Intent(context, WorkerIntentService.class);
        intent.setAction(ACTION_UPDATE_FIRST_NOTE);
        intent.putExtra(EXTRA_NOTE_STRING, note);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_FIRST_NOTE.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_NOTE_STRING);
                handleActionUpdateFirstNoteFoo(param1);
            }
        }
    }

    /**
     * Handle action  update the note string of the first Mood in the provided background thread
     * with the provided parameters.
     */
    private void handleActionUpdateFirstNoteFoo(String param1) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Mood m = realm.where(Mood.class).findFirst();
        m.setNote(param1);
        m.setTimestamp(new Date());
        realm.commitTransaction();
        realm.close();
    }

}
