package com.vineet.campusconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.vineet.campusconnect.R;
import com.vineet.campusconnect.models.Faculty;

public class FacultyAdapter extends FirestoreRecyclerAdapter<Faculty, FacultyAdapter.FacultyViewHolder> {

    private Context context;
    private OnItemClickListener listener;

    public FacultyAdapter(@NonNull FirestoreRecyclerOptions<Faculty> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull FacultyViewHolder holder, int position, @NonNull Faculty model) {
        holder.tvName.setText(model.getName());
        holder.tvDesignation.setText(model.getDesignation());

        if (model.getImageUrl() != null && !model.getImageUrl().isEmpty()) {
            Glide.with(context).load(model.getImageUrl()).into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.ic_nav_profile); // Default icon
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                String docId = getSnapshots().getSnapshot(position).getId();
                model.setId(docId); // Save ID to model
                listener.onItemClick(model);
            }
        });
    }

    @NonNull
    @Override
    public FacultyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_faculty, parent, false);
        return new FacultyViewHolder(view);
    }

    public interface OnItemClickListener {
        void onItemClick(Faculty faculty);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    static class FacultyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesignation;
        ImageView ivImage;
        public FacultyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_faculty_name);
            tvDesignation = itemView.findViewById(R.id.tv_faculty_designation);
            ivImage = itemView.findViewById(R.id.iv_faculty_image);
        }
    }
}