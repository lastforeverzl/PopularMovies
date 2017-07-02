package com.android.popularmovies.popularmovies;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.popularmovies.popularmovies.databinding.ReviewListItemBinding;
import com.android.popularmovies.popularmovies.entity.Review;

import java.util.List;

/**
 * Created by lei on 6/30/17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>{

    private Context mContext;
    private List<Review> mReviews;

    public ReviewsAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.review_list_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Review review = mReviews.get(position);
        holder.getBinding().setVariable(BR.review, review);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        if (mReviews == null) {
            return 0;
        }
        return mReviews.size();
    }

    public void setReviews(List<Review> reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        private ReviewListItemBinding mReviewListItemBinding;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            mReviewListItemBinding = DataBindingUtil.bind(itemView);
        }

        public ReviewListItemBinding getBinding() {
            return mReviewListItemBinding;
        }

    }

}
