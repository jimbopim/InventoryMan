package com.jimla.inventorymanager.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jimla.inventorymanager.R;
import com.jimla.inventorymanager.item.Item;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final ArrayList<Item> items;
    private final OnItemClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvDescription;
        private final TextView tvItemId;
        private final TextView tvRoomId;

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
            tvItemId = view.findViewById(R.id.tvItemId);
            tvRoomId = view.findViewById(R.id.tvRoom);
        }

        private TextView getName() {
            return tvName;
        }

        private TextView getDescription() {
            return tvDescription;
        }

        private TextView getItem() {
            return tvItemId;
        }

        private TextView getRoomId() {
            return tvRoomId;
        }
    }

    public SearchAdapter(ArrayList<Item> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.search_row_item, viewGroup, false);

        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Item item = this.items.get(position);
        viewHolder.getName().setText(item.itemName);
        viewHolder.getDescription().setText(item.itemDescription);
        String itemId = "Item: " + item.itemId;
        viewHolder.getItem().setText(itemId);
        String roomId = "Room: " + item.roomId;
        viewHolder.getRoomId().setText(roomId);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Item getItem(int position) {
        return items.get(position);
    }

    interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }
}