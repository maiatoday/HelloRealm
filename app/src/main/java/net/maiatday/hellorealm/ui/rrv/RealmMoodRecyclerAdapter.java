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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.maiatday.hellorealm.R;
import net.maiatday.hellorealm.model.Mood;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class RealmMoodRecyclerAdapter extends RealmRecyclerViewAdapter<Mood, RealmMoodRecyclerAdapter.MyViewHolder> {

    private final MainActivity activity;

    public RealmMoodRecyclerAdapter(MainActivity activity, OrderedRealmCollection<Mood> data) {
        super(activity, data, true);
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_mood, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Mood obj = getData().get(position);
        holder.data = obj;
        holder.title.setText(obj.getNote());
        holder.timestamp.setText(Mood.shortDateString(obj.getTimestamp()));
        holder.imageMood.setImageResource(Mood.moodToDrawableId(obj.getMood()));
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        public TextView title;
        public TextView timestamp;
        public ImageView imageMood;
        public Mood data;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.textNote);
            timestamp = (TextView) view.findViewById(R.id.textDate);
            imageMood = (ImageView) view.findViewById(R.id.imageMoodRow);
            view.setOnLongClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            activity.deleteItem(data);
            return true;
        }

        @Override
        public void onClick(View view) {
            activity.openItem(data);
        }
    }
}