package com.vineet.campusconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.chip.Chip;
import com.vineet.campusconnect.R;
import com.vineet.campusconnect.models.Doubt;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DoubtAdapter extends FirestoreRecyclerAdapter<Doubt, DoubtAdapter.DoubtViewHolder> {

    private OnDoubtClickListener listener;

    public DoubtAdapter(@NonNull FirestoreRecyclerOptions<Doubt> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull DoubtViewHolder holder, int position, @NonNull Doubt model) {
        // 1. Set basic text
        holder.tvTitle.setText(model.getTitle());
        holder.tvPreview.setText(model.getDescription());
        holder.chipTag.setText(model.getTag());
        holder.tvAnswerCount.setText(model.getAnswerCount() + " answers");

        // 2. Format timestamp (e.g., "Asked by Vineet • 2h ago")
        // For simplicity, we'll just show the date for now.
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());
        String dateStr = sdf.format(new Date(model.getTimestamp()));
        holder.tvAuthor.setText("Asked by " + model.getAuthorName() + " • " + dateStr);

        // 3. Show/Hide "SOLVED" badge
        if (model.isSolved()) {
            holder.chipSolved.setVisibility(View.VISIBLE);
        } else {
            holder.chipSolved.setVisibility(View.GONE);
        }

        // 4. Handle clicks
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                // We pass the snapshot ID so we know WHICH document was clicked
                listener.onDoubtClick(getSnapshots().getSnapshot(position).getId());
            }
        });
    }

    @NonNull
    @Override
    public DoubtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doubt, parent, false);
        return new DoubtViewHolder(view);
    }

    // --- Interface for clicks ---
    public interface OnDoubtClickListener {
        void onDoubtClick(String doubtId);
    }

    public void setOnDoubtClickListener(OnDoubtClickListener listener) {
        this.listener = listener;
    }

    // --- ViewHolder Class ---
    static class DoubtViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvPreview, tvAuthor, tvAnswerCount;
        Chip chipSolved, chipTag;

        public DoubtViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_doubt_title);
            tvPreview = itemView.findViewById(R.id.tv_doubt_preview);
            tvAuthor = itemView.findViewById(R.id.tv_doubt_author);
            tvAnswerCount = itemView.findViewById(R.id.tv_answer_count);
            chipSolved = itemView.findViewById(R.id.chip_solved_badge);
            chipTag = itemView.findViewById(R.id.chip_doubt_tag);
        }
    }
}