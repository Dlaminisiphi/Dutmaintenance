package com.example.dutmaintenance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
     private Context mContext;
     private List<Upload> mUploads;
     public ImageAdapter(Context context,List<Upload> uploads){
         mContext=context;
         mUploads=uploads;
     }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.image_item,parent,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
         Upload uploadCurrent= mUploads.get(position);
         holder.textViewBlock.setText(uploadCurrent.getBlock());
         holder.textViewCampus.setText(uploadCurrent.getCampus());
         holder.textViewFloor.setText(uploadCurrent.getFloor());
         holder.textViewProblemC.setText(uploadCurrent.getProblemS());
         holder.textViewProblemD.setText(uploadCurrent.getProblem());
         holder.textViewStatus.setText(uploadCurrent.getStatus());

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

    public class  ImageViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewBlock,textViewCampus,textViewFloor,textViewProblemD,textViewProblemC,textViewStatus;
        public ImageView imageView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewBlock=itemView.findViewById(R.id.Block);
            textViewCampus=itemView.findViewById(R.id.Campus);
            textViewFloor=itemView.findViewById(R.id.floor);
            textViewProblemC=itemView.findViewById(R.id.ProblemC);
            textViewProblemD=itemView.findViewById(R.id.ProblemD);
            textViewStatus=itemView.findViewById(R.id.Stutus);
            imageView=itemView.findViewById(R.id.image);


        }
    }
}
