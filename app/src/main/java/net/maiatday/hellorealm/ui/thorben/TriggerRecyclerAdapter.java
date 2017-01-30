package net.maiatday.hellorealm.ui.thorben;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.maiatday.hellorealm.R;
import net.maiatday.hellorealm.model.Mood;
import net.maiatday.hellorealm.model.Trigger;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

/**
 * Another recycler adapter to show moods
 * Created by maia on 2017/01/27.
 */

public class TriggerRecyclerAdapter extends RealmBasedRecyclerViewAdapter<Trigger, TriggerRecyclerAdapter.ViewHolder> {

    private OnTriggerItemClickListener onClickListener;

    public class ViewHolder extends RealmViewHolder implements View.OnClickListener {

    public TextView title;
    public TextView timestamp;
    public TextView description;
    public Trigger data;
    public ViewHolder(View container) {
        super(container);
        title = (TextView) container.findViewById(R.id.textTriggerTitle);
        timestamp = (TextView) container.findViewById(R.id.textTriggerDate);
        description = (TextView) container.findViewById(R.id.textTriggerDescription);
        container.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        onClickListener.onItemClick(data);
    }
}

    public TriggerRecyclerAdapter(
            Context context,
            RealmResults<Trigger> realmResults,
            boolean automaticUpdate,
            boolean animateResults,
            OnTriggerItemClickListener clickListener) {
        super(context, realmResults, automaticUpdate, animateResults);
        onClickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
        View v = inflater.inflate(R.layout.row_trigger, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindRealmViewHolder(ViewHolder viewHolder, int position) {
        final Trigger item = realmResults.get(position);
        viewHolder.title.setText(item.getTitle());
        viewHolder.description.setText(item.getDescription());
        viewHolder.timestamp.setText(Mood.shortDateString(item.getTimestamp()));
        viewHolder.data = item;
    }

    interface OnTriggerItemClickListener {
        void onItemClick(Trigger data);
    }

}
