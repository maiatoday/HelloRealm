package net.maiatday.hellorealm.ui.thorben;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.maiatday.hellorealm.R;
import net.maiatday.hellorealm.model.Mood;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

/**
 * Another recycler adapter to show moods
 * Created by maia on 2017/01/27.
 */

public class MoodRecyclerAdapter extends RealmBasedRecyclerViewAdapter<Mood, MoodRecyclerAdapter.ViewHolder> {

    private OnMoodItemClickListener onClickListener;

    public class ViewHolder extends RealmViewHolder implements View.OnClickListener {

    public TextView title;
    public TextView timestamp;
    public ImageView imageMood;
    public Mood data;
    public ViewHolder(View container) {
        super(container);
        title = (TextView) container.findViewById(R.id.textNote);
        timestamp = (TextView) container.findViewById(R.id.textDate);
        imageMood = (ImageView) container.findViewById(R.id.imageMoodRow);
        container.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        onClickListener.onItemClick(data);
    }
}

    public MoodRecyclerAdapter(
            Context context,
            RealmResults<Mood> realmResults,
            boolean automaticUpdate,
            boolean animateResults,
            OnMoodItemClickListener clickListener) {
        super(context, realmResults, automaticUpdate, animateResults);
        onClickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
        View v = inflater.inflate(R.layout.mood_row, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindRealmViewHolder(ViewHolder viewHolder, int position) {
        final Mood moodItem = realmResults.get(position);
        viewHolder.title.setText(moodItem.getNote());
        viewHolder.timestamp.setText(Mood.shortDateString(moodItem.getTimestamp()));
        viewHolder.imageMood.setImageResource(Mood.moodToDrawableId(moodItem.getMood()));
        viewHolder.data = moodItem;
    }

    interface OnMoodItemClickListener {
        void onItemClick(Mood data);
    }

}
