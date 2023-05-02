package com.example.dutmaintenance;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AdminImageAdaptor extends RecyclerView.Adapter<AdminImageAdaptor.AdminViewHolder> {
    private Context mContext;
    private List<Upload> mUploads;
    private OnItemClickListener mListener;

    public AdminImageAdaptor(Context context, List<Upload> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.admin_item, parent, false);
        return new AdminViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        Upload uploadCurrent = mUploads.get(position);
        holder.textViewBlock.setText(uploadCurrent.getBlock());
        holder.textViewCampus.setText(uploadCurrent.getCampus());
        holder.textViewFloor.setText(uploadCurrent.getFloor());
        holder.textViewProblemC.setText(uploadCurrent.getProblemS());
        holder.textViewProblemD.setText(uploadCurrent.getProblem());

        Picasso.with(mContext)
                .load(uploadCurrent.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class AdminViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView textViewBlock, textViewCampus, textViewFloor, textViewProblemD, textViewProblemC;
        public ImageView imageView;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewBlock = itemView.findViewById(R.id.ABlock);
            textViewCampus = itemView.findViewById(R.id.ACampus);
            textViewFloor = itemView.findViewById(R.id.Afloor);
            textViewProblemC = itemView.findViewById(R.id.AProblemC);
            textViewProblemD = itemView.findViewById(R.id.AProblemD);
            imageView = itemView.findViewById(R.id.Image12);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select Action");
            MenuItem querySeen = contextMenu.add(Menu.NONE, 1, 1, "Problem Seen");
            MenuItem queryFixed = contextMenu.add(Menu.NONE, 2, 2, "Problem Fixed");
            MenuItem archive = contextMenu.add(Menu.NONE, 3, 3, "Archive");
            querySeen.setOnMenuItemClickListener(this);
            queryFixed.setOnMenuItemClickListener(this);
            archive.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    switch (menuItem.getItemId()) {
                        case 1:
                            mListener.onQueryRecieved(position);
                            return true;
                        case 2:
                            mListener.onQueryFixed(position);
                            return true;
                        case 3:
                            mListener.onArchiveQuery(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onQueryRecieved(int position);
        void onQueryFixed(int position);
        void onArchiveQuery(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}


