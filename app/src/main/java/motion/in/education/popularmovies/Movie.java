package motion.in.education.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Daniel on 8/18/2015.
 */
public class Movie implements Parcelable{

   private int mData;

   public Movie(){}

   public Movie(String title,

                String overview,
                String releaseDate,
                String userRating){

      this.title = title;

      this.overview = overview;
      this.releaseDate = releaseDate;
      this.userRating = userRating;

   }

   /**
    *
    */
   private String title;

   public void setTitle(String title){
      this.title = title;
   }

   public String getTitle(){
      return title;
   }
   
   /**
    *
    */
   String posterPath;

   public void setPosterPath(String posterPath){
      this.posterPath = posterPath;
   }

   public String getPosterPath(){
      return posterPath;
   }

   /**
    *
    */
   String overview;

   public void setOverview(String overview){
      this.overview = overview;
   }

   public String getOverview(){
      return overview;
   }

   /**
    *
    */
   String userRating;

   public void setUserRating(String userRating){
      this.userRating = userRating;
   }

   public String getUserRating(){
      return userRating;
   }

   /**
    *
    */
   String releaseDate;

   public void setReleaseDate(String releaseDate){
      this.releaseDate = releaseDate;
   }

   public String getReleaseDate(){
      return releaseDate;
   }

   /**
    *
    */
   String popularity;

   public void setPopularity(String popularity){
      this.popularity = popularity;
   }

   public String getPopularity (){
      return this.popularity;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeInt(this.mData);
      dest.writeString(this.title);
      dest.writeString(this.posterPath);
      dest.writeString(this.overview);
      dest.writeString(this.userRating);
      dest.writeString(this.releaseDate);
      dest.writeString(this.popularity);
   }

   protected Movie(Parcel in) {
      this.mData = in.readInt();
      this.title = in.readString();
      this.posterPath = in.readString();
      this.overview = in.readString();
      this.userRating = in.readString();
      this.releaseDate = in.readString();
      this.popularity = in.readString();
   }

   public static final Creator<Movie> CREATOR = new Creator<Movie>() {
      public Movie createFromParcel(Parcel source) {
         return new Movie(source);
      }

      public Movie[] newArray(int size) {
         return new Movie[size];
      }
   };
}
