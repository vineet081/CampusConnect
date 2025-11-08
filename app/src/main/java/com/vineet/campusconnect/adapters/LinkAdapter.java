package com.vineet.campusconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.chip.Chip;
import com.vineet.campusconnect.R;
import com.vineet.campusconnect.models.LinkItem;

public class LinkAdapter extends FirestoreRecyclerAdapter<LinkItem, LinkAdapter.LinkViewHolder> {

    private Context context;

    public LinkAdapter(@NonNull FirestoreRecyclerOptions<LinkItem> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull LinkViewHolder holder, int position, @NonNull LinkItem model) {
        // 1. Set the text data
        holder.tvTitle.setText(model.getTitle());
        holder.tvDescription.setText(model.getDescription());
        holder.chipCategory.setText(model.getCategory());

        // 2. Handle the "Important" badge visibility
        if (model.isImportant()) {
            holder.ivImportantBadge.setVisibility(View.VISIBLE);
        } else {
            holder.ivImportantBadge.setVisibility(View.GONE);
        }

        // 3. Handle clicks: Open the URL
        holder.itemView.setOnClickListener(v -> openLink(model.getUrl()));
    }

    @NonNull
    @Override
    public LinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quick_link, parent, false);
        return new LinkViewHolder(view);
    }

    // Helper method to open links using Chrome Custom Tabs (modern & fast)
    private void openLink(String url) {
        try {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(context, Uri.parse(url));
        } catch (Exception e) {
            // Fallback if Chrome is not installed: open in standard browser
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(browserIntent);
        }
    }

    // --- ViewHolder Class ---
    static class LinkViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription;
        Chip chipCategory;
        ImageView ivImportantBadge;

        public LinkViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_link_title);
            tvDescription = itemView.findViewById(R.id.tv_link_description);
            chipCategory = itemView.findViewById(R.id.chip_link_category);
            ivImportantBadge = itemView.findViewById(R.id.iv_important_badge);
        }
    }
}