package com.jimla.inventorymanager.room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jimla.inventorymanager.R;

import java.util.ArrayList;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    interface OnItemClickListener {
        void onItemClick(int position);
    }

    private final ArrayList<String> localDataSetNames;
    private final ArrayList<String> localDataSetRfid;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView birthdate;

        private static OnItemClickListener listener;

        public ViewHolder(View view) {
            super(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(getAdapterPosition());
                }
            });

            name = view.findViewById(R.id.name);
            birthdate = view.findViewById(R.id.birthdate);
        }

        private TextView getName() {
            return name;
        }

        private TextView getBirthDate() {
            return birthdate;
        }

        public static void setOnItemClickListener(OnItemClickListener listener) {
            ViewHolder.listener = listener;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSetNames String[] containing the data to populate views to be used
     * by RecyclerView.
     * @param dataSetRfid
     */
    public RoomAdapter(ArrayList<String> dataSetNames, ArrayList<String> dataSetRfid) {
        localDataSetNames = dataSetNames;
        localDataSetRfid = dataSetRfid;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.main_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getName().setText(localDataSetNames.get(position));
        viewHolder.getBirthDate().setText(localDataSetRfid.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSetNames.size();
    }
}