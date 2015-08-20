package motion.in.education.popularmovies;

import android.graphics.Bitmap;
import android.media.Image;

import java.util.Date;

/**
 * Created by Daniel on 8/18/2015.
 */
public class Movie {

   public Movie(){}

   public Movie(String title,
                Bitmap thumbnail,
                String overview,
                String releaseDate,
                String userRating){

      this.title = title;
      this.thumbnail = thumbnail;
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
   Bitmap thumbnail;

   public void setThumbnail(Bitmap thumbnail){
      this.thumbnail = thumbnail;
   }

   public Bitmap getThumbnail(){
      return thumbnail;
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

   String userRating;

   public void setUserRating(String userRating){
      this.userRating = userRating;
   }

   public String getUserRating(){
      return userRating;
   }

   String releaseDate;

   public void setReleaseDate(String releaseDate){
      this.releaseDate = releaseDate;
   }

   public String getReleaseDate(){
      return releaseDate;
   }

   String popularity;

   public void setPopularity(String popularity){
      this.popularity = popularity;
   }

   public String getPopularity (){
      return this.popularity;
   }

}
