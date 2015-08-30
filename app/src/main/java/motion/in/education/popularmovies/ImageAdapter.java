package motion.in.education.popularmovies;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 8/19/2015.
 */
public class ImageAdapter extends BaseAdapter {

   private final LayoutInflater mInflater;
   private final int mResource;
   private final int mDropDownResource;
   private final List<Movie> mObjects;
   private final int mFieldId;
   private Context mContext;

   public ImageAdapter (Context context, int resource, int textViewResourceId, List<Movie> objects ){
      mContext = context;
      mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      mResource = mDropDownResource = resource;
      mObjects = objects;
      mFieldId = textViewResourceId;
   }

   public void add(Movie movie){
      mObjects.add(movie);
   }

   /**
    * How many items are in the data set represented by this Adapter.
    *
    * @return Count of items.
    */
   @Override
   public int getCount() {
      return mObjects.size();
   }

   /**
    * Get the data item associated with the specified position in the data set.
    *
    * @param position Position of the item whose data we want within the adapter's
    *                 data set.
    * @return The data at the specified position.
    */
   @Override
   public Object getItem(int position) {
      return this.mObjects.get(position);
   }

   /**
    * Get the row id associated with the specified position in the list.
    *
    * @param position The position of the item within the adapter's data set whose row id we want.
    * @return The id of the item at the specified position.
    */
   @Override
   public long getItemId(int position) {
      return 0;
   }

   public List<Movie> getAll(){
      return this.mObjects;
   }

   /**
    * Get a View that displays the data at the specified position in the data set. You can either
    * create a View manually or inflate it from an XML layout file. When the View is inflated, the
    * parent View (GridView, ListView...) will apply default layout parameters unless you use
    * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
    * to specify a root view and to prevent attachment to the root.
    *
    * @param position    The position of the item within the adapter's data set of the item whose view
    *                    we want.
    * @param convertView The old view to reuse, if possible. Note: You should check that this view
    *                    is non-null and of an appropriate type before using. If it is not possible to convert
    *                    this view to display the correct data, this method can create a new view.
    *                    Heterogeneous lists can specify their number of view types, so that this View is
    *                    always of the right type (see {@link #getViewTypeCount()} and
    *                    {@link #getItemViewType(int)}).
    * @param parent      The parent that this view will eventually be attached to
    * @return A View corresponding to the data at the specified position.
    */
   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
      ImageView imageView = (ImageView) convertView;

      if (imageView == null) {
         imageView = new ImageView(mContext);
         imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
      }

      Movie movie = (Movie) getItem(position);

      Picasso.with(mContext).load(movie.getPosterPath())
            .into(imageView);
      return imageView;
   }

   /**
    * Notifies the attached observers that the underlying data has been changed
    * and any View reflecting the data set should refresh itself.
    */
   @Override
   public void notifyDataSetChanged() {
      super.notifyDataSetChanged();
   }

   public void clear() {
      this.mObjects.clear();
   }

   public void addAll(ArrayList<Movie> movie_key) {
      for(int i = 0; i < movie_key.size(); i++){
         this.mObjects.add(movie_key.get(i));
      }
   }
}
