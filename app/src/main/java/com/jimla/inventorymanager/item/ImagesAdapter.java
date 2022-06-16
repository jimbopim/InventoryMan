package com.jimla.inventorymanager.item;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.jimla.inventorymanager.R;

import java.util.ArrayList;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    interface OnItemClickListener {
        void onItemClick(int position);
    }

    private final ArrayList<Bitmap> localDataSetBitmaps;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        private static OnItemClickListener listener;

        public ViewHolder(View view) {
            super(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(getAdapterPosition());
                }
            });

            imageView = view.findViewById(R.id.imageViewRec);
        }

        private ImageView getImageView() {
            return imageView;
        }

        public static void setOnItemClickListener(OnItemClickListener listener) {
            ViewHolder.listener = listener;
        }
    }

    public ImagesAdapter(ArrayList<Bitmap> dataSetNames) {
        localDataSetBitmaps = dataSetNames;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.image_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        //viewHolder.getImageView().setText(localDataSetNames.get(position));
        viewHolder.getImageView().setImageBitmap(localDataSetBitmaps.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSetBitmaps.size();
    }
}