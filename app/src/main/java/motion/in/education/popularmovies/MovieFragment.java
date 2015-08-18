package motion.in.education.popularmovies;

import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

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

   ArrayAdapter<Image> movieThumbnailAdapter;

   GridView gridView;

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

      movieThumbnailAdapter = new ArrayAdapter<>(
            getActivity(),
            R.layout.grid_item_movie,
            R.id.grid_item_movie_imageview,
            new ArrayList<Image>()
      );

      gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
      gridView.setAdapter(movieThumbnailAdapter);
      gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

         }
      });

      return rootView;
   }

   private void updateMovies(){
      FetchMovieTask fetchMovies = new FetchMovieTask();
      fetchMovies.execute("popularity");
   }

   public class FetchMovieTask extends AsyncTask<String, Void, Void>{

      private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

      /**
       * Override this method to perform a computation on a background thread. The
       * specified parameters are the parameters passed to {@link #execute}
       * by the caller of this task.
       * <p/>
       * This method can call {@link #publishProgress} to publish updates
       * on the UI thread.
       *
       * @param params The parameters of the task.
       * @return A result, defined by the subclass of this task.
       * @see #onPreExecute()
       * @see #onPostExecute
       * @see #publishProgress
       */
      @Override
      protected Void doInBackground(String... params) {

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
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

            Uri.Builder uriBuilder = new Uri.Builder();

            uriBuilder.scheme("https")
                  .authority("api.themoviedb.org")
                  .appendPath("3")
                  .appendPath("discover")
                  .appendPath("movie")
                  .appendPath("550");

            uriBuilder.appendQueryParameter("api_key", "");
            //uriBuilder.appendQueryParameter("callback", "JSON");

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

            // Create the request to OpenWeatherMap, and open the connection
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

         return null;
      }
   }


}
