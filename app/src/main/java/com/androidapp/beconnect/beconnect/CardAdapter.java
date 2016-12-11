package com.androidapp.beconnect.beconnect;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by mitour on 2016/12/12.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private ImageLoader imageLoader;
    private Context context;

    private List<detail> mItemList = null;

    public CardAdapter(List<detail> mItemList, Context context){
        super();
        this.mItemList = mItemList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_events_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        detail items = mItemList.get(position);

        holder.mId = items.getId();
        holder.tvName.setText(items.getName());
        holder.tvDescription.setText(items.getDescription());
        holder.tvStartAt.setText(items.getStart_at());
        holder.tvPlace.setText(items.getPlace());
        holder.tvQuantity.setText(items.getQuantity());
        holder.tvVacancy.setText(items.getVacancy());

        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader.get(items.feature_img_url, ImageLoader.getImageListener(holder.imageView, R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert));

        holder.imageView.setImageUrl(items.feature_img_url, imageLoader);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public NetworkImageView imageView;
        public TextView tvName;
        public TextView tvDescription;
        public TextView tvStartAt;
        public TextView tvPlace;
        public TextView tvQuantity;
        public TextView tvVacancy;
        public String mId;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (NetworkImageView) itemView.findViewById(R.id.ivImage);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            tvStartAt = (TextView) itemView.findViewById(R.id.tvStartAt);
            tvPlace = (TextView) itemView.findViewById(R.id.tvPlace);
            tvQuantity = (TextView) itemView.findViewById(R.id.tvQuantity);
            tvVacancy = (TextView) itemView.findViewById(R.id.tvVacancy);
            itemView.setOnClickListener(this);
        }

        public void setId(String id) {
            mId = id;
        }

        @Override
        public void onClick(final View v) {
            Intent intent = new Intent(context, Agendas.class);
            intent.putExtra("EXTRA_SESSION_ID", mId);
            context.startActivity(intent);
        }
    }
}