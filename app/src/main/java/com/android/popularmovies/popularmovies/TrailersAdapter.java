package com.android.popularmovies.popularmovies;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.popularmovies.popularmovies.databinding.TrailerListItemBinding;
import com.android.popularmovies.popularmovies.entity.Video;

import java.util.List;

/**
 * Created by lei on 6/30/17.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder>{
    private static final String TAG = "TrailersAdapter";

    private Context mContext;
    private TrailersAdapterOnClickHandler mClickHandler;
    private List<Video> mVideos;

    public interface TrailersAdapterOnClickHandler {
        void onClick(String key);
    }

    public TrailersAdapter(Context context, TrailersAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.trailer_list_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        Video video = mVideos.get(position);
        holder.getBinding().setVariable(BR.video, video);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        if (mVideos == null) {
            return 0;
        }
        return mVideos.size();
    }

    public void setVideos(List<Video> videos) {
        mVideos = videos;
        notifyDataSetChanged();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TrailerListItemBinding mTrailerListItemBinding;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            mTrailerListItemBinding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(this);
        }

        public TrailerListItemBinding getBinding() {
            return mTrailerListItemBinding;
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Video video = mVideos.get(adapterPosition);
            mClickHandler.onClick(video.getKey());
        }
    }

}
