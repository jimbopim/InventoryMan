package com.jimla.inventorymanager.site;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jimla.inventorymanager.R;

import java.util.ArrayList;

public class SiteAdapter extends RecyclerView.Adapter<SiteAdapter.ViewHolder> {

    private final ArrayList<Site> sites;
    private final OnItemClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvDescription;
        private final TextView tvStartDate;

        public ViewHolder(View view, OnItemClickListener listener) {
            super(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(getAdapterPosition());
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onItemLongClick(getAdapterPosition());
                    return true;
                }
            });

            tvName = view.findViewById(R.id.name);
            tvDescription = view.findViewById(R.id.description);
            tvStartDate = view.findViewById(R.id.startDate);
        }

        private TextView getName() {
            return tvName;
        }

        private TextView getDescription() {
            return tvDescription;
        }

        private TextView getStartDate() {
            return tvStartDate;
        }
    }

    public SiteAdapter(ArrayList<Site> sites, OnItemClickListener listener) {
        this.sites = sites;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.main_row_item, viewGroup, false);

        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Site site = sites.get(position);
        viewHolder.getName().setText(site.siteName);
        viewHolder.getDescription().setText(site.description);
        viewHolder.getStartDate().setText(String.valueOf(site.startDate));
    }

    @Override
    public int getItemCount() {
        return sites.size();
    }

    public Site getSite(int position) {
        return sites.get(position);
    }

    interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }
}