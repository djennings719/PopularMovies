package motion.in.education.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


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

      ArrayAdapter<String> detailsAdapter = new ArrayAdapter<String>(
            getActivity(),
            R.layout.list_item_movie_details,
            R.id.list_item_movie_details,
            new ArrayList<String>()
      );

      Intent intent = getActivity().getIntent();
      movie = intent.getParcelableExtra("movieDetails");

      TextView titleTextView = (TextView) rootView.findViewById(R.id.movie_title_text_view);
      titleTextView.setText(movie.getTitle());

      ImageView thumbnailImageView = (ImageView) rootView.findViewById(R.id.image_view_thumbnail);
      Picasso.with(getActivity().getApplication()).load(movie.getPosterPath()).into(thumbnailImageView);

      ListView listView = (ListView) rootView.findViewById(R.id.listView_movie_details);

      detailsAdapter.add(movie.getReleaseDate());
      detailsAdapter.add(movie.getUserRating());
      detailsAdapter.add(movie.getOverview());

      listView.setAdapter(detailsAdapter);

      return rootView;
   }
}
