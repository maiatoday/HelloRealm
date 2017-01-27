package net.maiatday.hellorealm.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.maiatday.hellorealm.R;
import net.maiatday.hellorealm.model.Mood;

import io.realm.RealmResults;

/**
 * Another recycler adapter to show moods
 * Created by maia on 2017/01/27.
 */

public class MoodRecyclerAdapter extends RealmBasedRecyclerViewAdapter<Mood, MoodRecyclerAdapter.ViewHolder> {

public class ViewHolder extends RealmViewHolder {

    public TextView todoTextView;
    public ViewHolder(FrameLayout container) {
        super(container);
        this.todoTextView = (TextView) container.findViewById(R.id.todo_text_view);
    }
}

    public ToDoRealmAdapter(
            Context context,
            RealmResults<Mood> realmResults,
            boolean automaticUpdate,
            boolean animateResults) {
        super(context, realmResults, automaticUpdate, animateResults);
    }

    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
        View v = inflater.inflate(R.layout.to_do_item_view, viewGroup, false);
        ViewHolder vh = new ViewHolder((FrameLayout) v);
        return vh;
    }

    @Override
    public void onBindRealmViewHolder(ViewHolder viewHolder, int position) {
        final Mood moodItem = realmResults.get(position);
        viewHolder.todoTextView.setText(moodItem.getDescription());
    }
}
