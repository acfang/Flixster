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

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class MovieDetailsActivity extends AppCompatActivity {

    // movie being displayed
    Movie movie;

    // view objects
    ImageView ivBackdrop;
    TextView tvTitle;
    TextView tvOverview;
    TextView tvRelease;
    RatingBar rbVoteAverage;

    ActivityMovieDetailsBinding binding;

    String videoId = "";

    // the "Cast" header
    TextView tvCast;

    // for the cast pictures, names, and characters
    ArrayList<ImageView> castImages;
    ArrayList<TextView> castNames;
    ArrayList<TextView> castRoles;

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
        tvRelease = (TextView) binding.tvRelease;
        rbVoteAverage = (RatingBar) binding.rbVoteAverage;
        tvCast = (TextView) binding.tvCast;

        // unwrap the movie passed in via intent, using simple name as key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        // set title, overview, and release date
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        tvRelease.setText("Release Date: "+ movie.getReleaseDate());

        // changing font to Rubik Bold and Regular, changing color
        Typeface tfTitle = Typeface.createFromAsset(getAssets(),
                "Rubik-Bold.ttf");
        tvTitle.setTypeface(tfTitle);
        tvCast.setTypeface(tfTitle);

        Typeface tfOverview = Typeface.createFromAsset(getAssets(),
                "Rubik-Regular.ttf");
        tvOverview.setTypeface(tfOverview);
        tvRelease.setTypeface(tfOverview);

        tvTitle.setTextColor(Color.WHITE);
        tvCast.setTextColor(Color.WHITE);
        tvOverview.setTextColor(Color.GRAY);
        tvRelease.setTextColor(Color.GRAY);

        // vote average is 0 to 10, convert to 0 to 5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);

        RelativeLayout relativeLayout = binding.movieDetailsRL;
        relativeLayout.setBackgroundColor(Color.rgb(24,24,24));

        getVideos();

        getCast();
    }

    // retrieves the YouTube trailer videos for each movie when the movie details page is accessed
    private void getVideos() {
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
    }

    // retrieves the cast members' names, pictures, and characters to display on the movie details page
    private void getCast() {
        // get cast
        String castUrl = "https://api.themoviedb.org/3/movie/" + movie.getId() + "/credits?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed&language=en-US";

        final String castPicUrl = "https://image.tmdb.org/t/p/w342";

        castImages = new ArrayList<ImageView>();
        castNames = new ArrayList<TextView>();
        castRoles = new ArrayList<TextView>();

        castImages.add((ImageView) binding.cast0Img);
        castNames.add((TextView) binding.cast0Name);
        castRoles.add((TextView) binding.cast0Role);
        castImages.add((ImageView) binding.cast1Img);
        castNames.add((TextView) binding.cast1Name);
        castRoles.add((TextView) binding.cast1Role);
        castImages.add((ImageView) binding.cast2Img);
        castNames.add((TextView) binding.cast2Name);
        castRoles.add((TextView) binding.cast2Role);
        castImages.add((ImageView) binding.cast3Img);
        castNames.add((TextView) binding.cast3Name);
        castRoles.add((TextView) binding.cast3Role);
        castImages.add((ImageView) binding.cast4Img);
        castNames.add((TextView) binding.cast4Name);
        castRoles.add((TextView) binding.cast4Role);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(castUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d("MovieDetailsActivity", "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("cast");

                    Log.d("MovieDetailsActivity", "Results: "+ results);

                    if (results != null) {
                        // get cast list from credits API

                        int radius = 30;
                        int margin = 10;

                        Typeface rubikReg = Typeface.createFromAsset(getAssets(),
                                "Rubik-Regular.ttf");

                        for (int i = 0; i < 5; i++) {
                            // load the cast images, names, and characters
                            Glide.with(MovieDetailsActivity.this)
                                    .load(castPicUrl + results.getJSONObject(i).getString("profile_path"))
                                    .placeholder(R.drawable.flicks_movie_placeholder)
                                    .fitCenter()
                                    .transform(new RoundedCornersTransformation(radius, margin))
                                    .into(castImages.get(i));
                            castNames.get(i).setText(results.getJSONObject(i).getString("name"));
                            castRoles.get(i).setText(results.getJSONObject(i).getString("character"));

                            // style
                            castNames.get(i).setTextColor(Color.WHITE);
                            castRoles.get(i).setTextColor(Color.GRAY);
                            castNames.get(i).setTypeface(rubikReg);
                            castRoles.get(i).setTypeface(rubikReg);
                        }
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