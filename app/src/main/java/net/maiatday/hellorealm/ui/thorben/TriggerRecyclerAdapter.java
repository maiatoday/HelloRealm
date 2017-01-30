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
