/*
 * Copyright 2016 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
                .inflate(R.layout.mood_row, parent, false);
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