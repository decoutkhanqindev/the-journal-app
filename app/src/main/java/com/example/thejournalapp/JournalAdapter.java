package com.example.thejournalapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {
    Context context;
    ArrayList<Journal> journalArrayList;

    public JournalAdapter(Context context, ArrayList<Journal> journalArrayList) {
        this.context = context;
        this.journalArrayList = journalArrayList;
    }

    @NonNull
    @Override
    public JournalAdapter.JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View journalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.journal_card, parent, false);
        return new JournalViewHolder(journalView);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull JournalAdapter.JournalViewHolder holder, int position) {
        Journal journal = journalArrayList.get(position);
        if (journal != null){
            holder.userId.setText(journal.getUserId());
            holder.userName.setText(journal.getUserName());
            holder.title.setText(journal.getTitle());

            String imgUrl = journal.getImgUrl();
            Glide.with(context).load(imgUrl).fitCenter().into(holder.imgUrl);

            holder.description.setText(journal.getDescription());

//            String timeAgo = String.valueOf(DateUtils.getRelativeTimeSpanString(journal.getTimeAdded().getSeconds() * 1000));
            Date date = journal.getTimeAdded().toDate();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String timeAdded = simpleDateFormat.format(date);
            holder.timeAdded.setText(timeAdded);
        }
    }

    @Override
    public int getItemCount() {
        return journalArrayList.size();
    }

    public static class JournalViewHolder extends RecyclerView.ViewHolder {
        private final TextView userId;
        private final TextView userName;
        private final TextView title;
        private final TextView description;
        private final TextView timeAdded;
        private final ImageView shareBtn;
        private final ImageView imgUrl;

        public JournalViewHolder(@NonNull View itemView) {
            super(itemView);
            this.userId = itemView.findViewById(R.id.journal_user_id);
            this.userName = itemView.findViewById(R.id.journal_user_name);
            this.title = itemView.findViewById(R.id.journal_title);
            this.description = itemView.findViewById(R.id.journal_description);
            this.shareBtn = itemView.findViewById(R.id.share_btn);
            this.imgUrl = itemView.findViewById(R.id.journal_img_url);
            this.timeAdded = itemView.findViewById(R.id.journal_time_added);

            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }
}
