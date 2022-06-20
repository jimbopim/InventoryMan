package com.jimla.inventorymanager.item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.jimla.inventorymanager.R;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private final ArrayList<Image> images;
    private final OnItemClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public ViewHolder(View view, OnItemClickListener listener) {
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
    }

    public ImageAdapter(ArrayList<Image> images, OnItemClickListener listener) {
        this.images = images;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.image_row_item, viewGroup, false);

        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Image image = images.get(position);
        viewHolder.getImageView().setImageBitmap(image.imageBitmap);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public Image getImage(int position) {
        return images.get(position);
    }

    interface OnItemClickListener {
        void onItemClick(int position);
    }
}