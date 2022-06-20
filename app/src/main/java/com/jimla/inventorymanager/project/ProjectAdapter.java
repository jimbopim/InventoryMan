package com.jimla.inventorymanager.project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jimla.inventorymanager.R;

import java.util.ArrayList;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

    private final ArrayList<Site> sites;
    private final OnItemClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView description;
        private final TextView startDate;

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

            name = view.findViewById(R.id.name);
            description = view.findViewById(R.id.description);
            startDate = view.findViewById(R.id.startDate);
        }

        private TextView getName() {
            return name;
        }

        private TextView getDescription() {
            return description;
        }

        private TextView getStartDate() {
            return startDate;
        }
    }

    public ProjectAdapter(ArrayList<Site> sites, OnItemClickListener listener) {
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