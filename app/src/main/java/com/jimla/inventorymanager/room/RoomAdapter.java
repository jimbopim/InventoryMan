package com.jimla.inventorymanager.room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jimla.inventorymanager.R;

import java.util.ArrayList;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    private final ArrayList<Room> rooms;
    private final OnItemClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView description;

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
        }

        private TextView getName() {
            return name;
        }

        private TextView getDescription() {
            return description;
        }
    }

    public RoomAdapter(ArrayList<Room> rooms, OnItemClickListener listener) {
        this.rooms = rooms;
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

        Room room = rooms.get(position);
        viewHolder.getName().setText(room.roomName);
        viewHolder.getDescription().setText(room.roomDescription);
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public Room getRoom(int position) {
        return rooms.get(position);
    }

    interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }
}