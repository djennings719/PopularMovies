package motion.in.education.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment {

   GridView gridView;
   ImageAdapter movieAdapter;
   ArrayList<Movie> movieList;
   String currentSearchOption;

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
   }

   /**
    * Called to ask the fragment to save its current dynamic state, so it
    * can later be reconstructed in a new instance of its process is
    * restarted.  If a new instance of the fragment later needs to be
    * created, the data you place in the Bundle here will be available
    * in the Bundle given to {@link #onCreate(Bundle)},
    * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}, and
    * {@link #onActivityCreated(Bundle)}.
    * <p/>
    * <p>This corresponds to {@link Activity#onSaveInstanceState(Bundle)
    * Activity.onSaveInstanceState(Bundle)} and most of the discussion there
    * applies here as well.  Note however: <em>this method may be called
    * at any time before {@link #onDestroy()}</em>.  There are many situations
    * where a fragment may be mostly torn down (such as when placed on the
    * back stack with no UI showing), but its state will not be saved until
    * its owning activity actually needs to save its state.
    *
    * @param outState Bundle in which to place your saved state.
    */
   @Override
   public void onSaveInstanceState(Bundle outState) {
      outState.putParcelableArrayList("MOVIE_KEY", movieList);
      outState.putString("SEARCH_KEY", this.currentSearchOption);

      super.onSaveInstanceState(outState);
   }

   /**
    * Called when the fragment is visible to the user and actively running.
    * This is generally
    * tied to {@link Activity#onResume() Activity.onResume} of the containing
    * Activity's lifecycle.
    */
   @Override
   public void onResume() {
      super.onResume();
      if(!this.getSelectionSetting().equalsIgnoreCase(this.currentSearchOption)){
         updateMovies();
      }
   }

   @Override
   public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      movieList = new ArrayList<>();

      View rootView = inflater.inflate(R.layout.fragment_main, container, false);

      movieAdapter = new ImageAdapter(
            getActivity(),
            R.layout.grid_item_movie,
            R.id.grid_item_movie_imageview,
            new ArrayList<Movie>()
      );

      if(savedInstanceState != null){
         movieList.addAll(savedInstanceState.<Movie>getParcelableArrayList("MOVIE_KEY"));
         movieAdapter.addAll(movieList);
         this.currentSearchOption = savedInstanceState.getString("SEARCH_KEY");
      }
      else{
         updateMovies();
      }

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
      if(!isNetworkAvailable()){
         Context context = getActivity().getApplicationContext();
         CharSequence text = getActivity().getText(R.string.network_unavailale_message);
         int duration = Toast.LENGTH_LONG;

         Toast toast = Toast.makeText(context, text, duration);
         toast.show();
         return;
      }
      FetchMovieTask fetchMovies = new FetchMovieTask();
      this.currentSearchOption = getSelectionSetting();
      fetchMovies.execute(getSelectionSetting());
   }

   private String getSelectionSetting() {
      return PreferenceManager.getDefaultSharedPreferences(getActivity())
            .getString(getString(R.string.pref_sort_method_key),
                  getString(R.string.pref_sort_method_default));
   }

   //Based on a stackoverflow snippet & performance recommendation
   private boolean isNetworkAvailable() {
      ConnectivityManager connectivityManager
            = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
      return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
         movieList.clear();
         for(int i = 0; i < movies.length; i++) {
            movieAdapter.add(movies[i]);
            movieList.add(movies[i]);
         }
         movieAdapter.notifyDataSetChanged();

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
