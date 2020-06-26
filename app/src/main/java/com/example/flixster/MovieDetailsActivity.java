package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.databinding.ActivityMovieDetailsBinding;
import com.example.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class MovieDetailsActivity extends AppCompatActivity {

    // movie being displayed
    Movie movie;

    // view objects
    ImageView ivBackdrop;
    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;

    ActivityMovieDetailsBinding binding;

    String videoId = "";

    TextView tvCast;
    ImageView cast0Img;
    TextView cast0Name;
    TextView cast0Role;
    ImageView cast1Img;
    TextView cast1Name;
    TextView cast1Role;
    ImageView cast2Img;
    TextView cast2Name;
    TextView cast2Role;
    ImageView cast3Img;
    TextView cast3Name;
    TextView cast3Role;
    ImageView cast4Img;
    TextView cast4Name;
    TextView cast4Role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_movie_details);
        binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // resolve view objects
        ivBackdrop = (ImageView) binding.ivBackdrop;
        tvTitle = (TextView) binding.tvTitle;
        tvOverview = (TextView) binding.tvOverview;
        rbVoteAverage = (RatingBar) binding.rbVoteAverage;
        tvCast = (TextView) binding.tvCast;

        // unwrap the movie passed in via intent, using simple name as key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        // set title and overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        Typeface tfTitle = Typeface.createFromAsset(getAssets(),
                "Rubik-Bold.ttf");
        tvTitle.setTypeface(tfTitle);
        tvCast.setTypeface(tfTitle);

        Typeface tfOverview = Typeface.createFromAsset(getAssets(),
                "Rubik-Regular.ttf");
        tvOverview.setTypeface(tfOverview);

        tvTitle.setTextColor(Color.WHITE);
        tvCast.setTextColor(Color.WHITE);
        tvOverview.setTextColor(Color.GRAY);

        // vote average is 0 to 10, convert to 0 to 5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);

        RelativeLayout relativeLayout = binding.movieDetailsRL;
        relativeLayout.setBackgroundColor(Color.rgb(24,24,24));

        String videoUrl = "https://api.themoviedb.org/3/movie/" + movie.getId() + "/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed&language=en-US";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(videoUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d("MovieDetailsActivity", "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");

                    if (results != null) {
                        // get movie YouTube video key from the first JSON object from accessing the API
                        videoId = results.getJSONObject(0).getString("key");
                    }

                    Log.i("MovieDetailsActivity", "Retrieved video key for movie " + videoId);
                } catch (JSONException e) {
                    Log.e("MovieDetailsActivity", "Hit json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("MovieDetailsActivity", "onFailure");
                Log.d("MovieDetailsActivity", throwable.toString());
            }
        });

        int radius = 30;
        int margin = 10;

        Glide.with(MovieDetailsActivity.this)
                .load(movie.getBackdropPath())
                .placeholder(R.drawable.flicks_backdrop_placeholder)
                .fitCenter()
                .transform(new RoundedCornersTransformation(radius, margin))
                .into(ivBackdrop);

        ivBackdrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MovieTrailerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                intent.putExtra("video_id", Parcels.wrap(videoId));
                getBaseContext().startActivity(intent);
            }
        });

        // get cast
        String castUrl = "https://api.themoviedb.org/3/movie/" + movie.getId() + "/credits?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed&language=en-US";

        final String castPicUrl = "https://image.tmdb.org/t/p/w342";

        cast0Img = (ImageView) binding.cast0Img;
        cast0Name = (TextView) binding.cast0Name;
        cast0Role = (TextView) binding.cast0Role;
        cast1Img = (ImageView) binding.cast1Img;
        cast1Name = (TextView) binding.cast1Name;
        cast1Role = (TextView) binding.cast1Role;
        cast2Img = (ImageView) binding.cast2Img;
        cast2Name = (TextView) binding.cast2Name;
        cast2Role = (TextView) binding.cast2Role;
        cast3Img = (ImageView) binding.cast3Img;
        cast3Name = (TextView) binding.cast3Name;
        cast3Role = (TextView) binding.cast3Role;
        cast4Img = (ImageView) binding.cast4Img;
        cast4Name = (TextView) binding.cast4Name;
        cast4Role = (TextView) binding.cast4Role;

        client.get(castUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d("MovieDetailsActivity", "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("cast");

                    Log.d("MovieDetailsActivity", "Results: "+ results);

                    if (results != null) {
                        // get movie YouTube video key from the first JSON object from accessing the API

                        int radius = 30;
                        int margin = 10;

                        Glide.with(MovieDetailsActivity.this)
                                .load(castPicUrl + results.getJSONObject(0).getString("profile_path"))
                                .placeholder(R.drawable.flicks_movie_placeholder)
                                .fitCenter()
                                .transform(new RoundedCornersTransformation(radius, margin))
                                .into(cast0Img);
                        cast0Name.setText(results.getJSONObject(0).getString("name"));
                        cast0Role.setText(results.getJSONObject(0).getString("character"));

                        Glide.with(MovieDetailsActivity.this)
                                .load(castPicUrl + results.getJSONObject(1).getString("profile_path"))
                                .placeholder(R.drawable.flicks_movie_placeholder)
                                .fitCenter()
                                .transform(new RoundedCornersTransformation(radius, margin))
                                .into(cast1Img);
                        cast1Name.setText(results.getJSONObject(1).getString("name"));
                        cast1Role.setText(results.getJSONObject(1).getString("character"));

                        Glide.with(MovieDetailsActivity.this)
                                .load(castPicUrl + results.getJSONObject(2).getString("profile_path"))
                                .placeholder(R.drawable.flicks_movie_placeholder)
                                .fitCenter()
                                .transform(new RoundedCornersTransformation(radius, margin))
                                .into(cast2Img);
                        cast2Name.setText(results.getJSONObject(2).getString("name"));
                        cast2Role.setText(results.getJSONObject(2).getString("character"));

                        Glide.with(MovieDetailsActivity.this)
                                .load(castPicUrl + results.getJSONObject(3).getString("profile_path"))
                                .placeholder(R.drawable.flicks_movie_placeholder)
                                .fitCenter()
                                .transform(new RoundedCornersTransformation(radius, margin))
                                .into(cast3Img);
                        cast3Name.setText(results.getJSONObject(3).getString("name"));
                        cast3Role.setText(results.getJSONObject(3).getString("character"));

                        Glide.with(MovieDetailsActivity.this)
                                .load(castPicUrl + results.getJSONObject(4).getString("profile_path"))
                                .placeholder(R.drawable.flicks_movie_placeholder)
                                .fitCenter()
                                .transform(new RoundedCornersTransformation(radius, margin))
                                .into(cast4Img);
                        cast4Name.setText(results.getJSONObject(4).getString("name"));
                        cast4Role.setText(results.getJSONObject(4).getString("character"));
                    }

                    Log.i("MovieDetailsActivity", "Retrieved cast for movie " + videoId);
                } catch (JSONException e) {
                    Log.e("MovieDetailsActivity", "Hit json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("MovieDetailsActivity", "onFailure");
                Log.d("MovieDetailsActivity", throwable.toString());
            }
        });
    }
}