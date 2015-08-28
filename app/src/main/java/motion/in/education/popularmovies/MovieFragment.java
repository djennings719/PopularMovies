package motion.in.education.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment {

   GridView gridView;
   ImageAdapter movieAdapter;

   public MovieFragment() {
   }

   /**
    * Called when the Fragment is visible to the user.  This is generally
    * tied to {@link Activity#onStart() Activity.onStart} of the containing
    * Activity's lifecycle.
    */
   @Override
   public void onStart() {
      super.onStart();
      updateMovies();
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {

      View rootView = inflater.inflate(R.layout.fragment_main, container, false);

      movieAdapter = new ImageAdapter(
            getActivity(),
            R.layout.grid_item_movie,
            R.id.grid_item_movie_imageview,
            new ArrayList<Movie>()
      );
      //movieAdapter = new ImageAdapter(getActivity());

      movieAdapter.notifyDataSetChanged();

      gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
      gridView.setAdapter(movieAdapter);
      gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Movie movie = (Movie) movieAdapter.getItem(position);
            Intent movieDetailIntent = new Intent(getActivity(), DetailActivity.class)
                  .putExtra("movieDetails", movie);

            startActivity(movieDetailIntent);
         }
      });

      return rootView;
   }

   private void updateMovies(){
      FetchMovieTask fetchMovies = new FetchMovieTask();
      fetchMovies.execute(getSelectionSetting());
   }

   private String getSelectionSetting() {
      return PreferenceManager.getDefaultSharedPreferences(getActivity())
            .getString(getString(R.string.pref_sort_method_key),
                  getString(R.string.pref_sort_method_default));
   }

   public class FetchMovieTask extends AsyncTask<String, Void, Movie[]>{

      private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

      /**
       * <p>Runs on the UI thread after {@link #doInBackground}. The
       * specified result is the value returned by {@link #doInBackground}.</p>
       * <p/>
       * <p>This method won't be invoked if the task was cancelled.</p>
       *
       * @param movies The result of the operation computed by {@link #doInBackground}.
       * @see #onPreExecute
       * @see #doInBackground
       * @see #onCancelled(Object)
       */
      @Override
      protected void onPostExecute(Movie[] movies) {
         super.onPostExecute(movies);

         movieAdapter.clear();
         for(int i = 0; i < movies.length; i++) {
            movieAdapter.add(movies[i]);
            movieAdapter.notifyDataSetChanged();
         }
      }

      /**
       * Override this method to perform a computation on a background thread. The
       * specified parameters are the parameters passed to {@link #execute}
       * by the caller of this task.
       * <p/>
       * This method can call {@link #publishProgress} to publish updates
       * on the UI thread.
       *
       * The boiler plate code in this method was taken from Udacity Sunshine
       * github page and modified to fit TMDB
       *
       * @param params The parameters of the task.
       * @return A result, defined by the subclass of this task.
       * @see #onPreExecute()
       * @see #onPostExecute
       * @see #publishProgress
       */
      @Override
      protected Movie[] doInBackground(String... params) {

         if(params.length == 0){
            return null;
         }

         // These two need to be declared outside the try/catch
         // so that they can be closed in the finally block.
         HttpURLConnection urlConnection = null;
         BufferedReader reader = null;

         // Will contain the raw JSON response as a string.
         String movieJsonStr = null;

         try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at TMDB's movie API page, at
            // https://www.themoviedb.org/documentation/api

            Uri.Builder uriBuilder = new Uri.Builder();

            uriBuilder.scheme("https")
                  .authority("api.themoviedb.org")
                  .appendPath("3")
                  .appendPath("discover")
                  .appendPath("movie");

            uriBuilder.appendQueryParameter("api_key", "");

            switch(params[0]){

               case "popularity":
                  uriBuilder.appendQueryParameter("sort_by", "popularity.desc");
                  break;
               case "ratings":
                  uriBuilder.appendQueryParameter("sort_by", "vote_average.desc");
                  break;
               default:
                  return null;
            }

            URL url = new URL(uriBuilder.build().toString());

            // Create the request to TMDB, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
               // Nothing to do.
               return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
               // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
               // But it does make debugging a *lot* easier if you print out the completed
               // buffer for debugging.
               buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
               // Stream was empty.  No point in parsing.
               return null;
            }
            movieJsonStr = buffer.toString();
            Log.v("******movieJsonStr", movieJsonStr);

         } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
         } finally {
            if (urlConnection != null) {
               urlConnection.disconnect();
            }
            if (reader != null) {
               try {
                  reader.close();
               } catch (final IOException e) {
                  Log.e("PlaceholderFragment", "Error closing stream", e);
               }
            }
         }

         try{
            return getMoviesFromJson(movieJsonStr);
         }
         catch(JSONException e){
            e.printStackTrace();
            return null;
         }
      }

      private Movie[] getMoviesFromJson(String movieJsonStr)
         throws JSONException {

         Movie[] movies;

         final String TMDB_RESULTS = "results";

         final String TMDB_TITLE = "original_title";
         final String TMDB_POSTER_PATH = "poster_path";
         final String TMDB_OVERVIEW = "overview";
         final String TMDB_RELEASE_DATE = "release_date";
         final String TMDB_USER_RATING = "vote_average";
         final String TMDB_POPULARITY = "popularity";

         JSONObject movieJson = new JSONObject(movieJsonStr);
         JSONArray movieArray = movieJson.getJSONArray(TMDB_RESULTS);

         movies = new Movie[movieArray.length()];

         for(int i = 0; i < movieArray.length(); i++){
            //Movie movie = new Movie();
            movies[i] = new Movie();
            movies[i].setTitle(movieArray.getJSONObject(i).getString(TMDB_TITLE));
            movies[i].setOverview(movieArray.getJSONObject(i).getString(TMDB_OVERVIEW));
            movies[i].setReleaseDate(movieArray.getJSONObject(i).getString(TMDB_RELEASE_DATE));
            movies[i].setUserRating(movieArray.getJSONObject(i).getString(TMDB_USER_RATING));
            movies[i].setPopularity(movieArray.getJSONObject(i).getString(TMDB_POPULARITY));
            movies[i].setPosterPath("http://image.tmdb.org/t/p/w500" + movieArray.getJSONObject(i).getString(TMDB_POSTER_PATH));
         }

         return movies;
      }
   }


}
