package motion.in.education.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

   public DetailActivityFragment() {
   }

   private Movie movie;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {

      View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

      Intent intent = getActivity().getIntent();
      movie = intent.getParcelableExtra("movieDetails");

      TextView titleTextView = (TextView) rootView.findViewById(R.id.movie_title_text_view);
      titleTextView.setText(movie.getTitle());

      ImageView thumbnailImageView = (ImageView) rootView.findViewById(R.id.image_view_thumbnail);
      Picasso.with(getActivity().getApplication()).load(movie.getPosterPath()).into(thumbnailImageView);



      /*TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.release_date);
      releaseDateTextView.setText(movie.getReleaseDate());

      TextView userRatingTextView = (TextView) rootView.findViewById(R.id.user_rating);
      userRatingTextView.setText(movie.getUserRating());

      TextView overviewTextView = (TextView) rootView.findViewById(R.id.overview);
      overviewTextView.setText(movie.getOverview());*/


      //TextView titleView = inflater.inflate(android.R.id)

      Log.v("***Detail Activity", movie.getTitle());



      return rootView;
   }
}
