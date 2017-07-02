package com.android.popularmovies.popularmovies.utility;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by lei on 6/30/17.
 */

public class ImageBinder {

    private ImageBinder() {
        //NO-OP
    }

    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView imageView, String posterPath) {
        Uri uri = Uri.parse("http://image.tmdb.org/t/p/w185").buildUpon()
                .appendEncodedPath(posterPath)
                .build();
        Context context = imageView.getContext();
        Picasso.with(context)
                .load(uri)
                .fit()
                .into(imageView);
    }
}
