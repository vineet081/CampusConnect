package com.vineet.campusconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.vineet.campusconnect.R;
import com.vineet.campusconnect.models.Comment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.CommentViewHolder> {

    public CommentAdapter(@NonNull FirestoreRecyclerOptions<Comment> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comment model) {
        holder.tvAuthor.setText(model.getAuthorName());
        holder.tvText.setText(model.getText());

        // Format timestamp to something readable like "2 hours ago" or a date
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());
        String dateStr = sdf.format(new Date(model.getTimestamp()));
        holder.tvTime.setText(" • " + dateStr);
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthor, tvTime, tvText;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tv_comment_author);
            tvTime = itemView.findViewById(R.id.tv_comment_time);
            tvText = itemView.findViewById(R.id.tv_comment_text);
        }
    }
}