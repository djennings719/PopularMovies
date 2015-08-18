package motion.in.education.popularmovies;

import android.media.Image;

import java.util.Date;

/**
 * Created by Daniel on 8/18/2015.
 */
public class Movie {

   public Movie(String title,
                Image thumbnail,
                String overview,
                Date releaseDate,
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

   public void setTitle(){

   }

   public String getTitle(){
      return title;
   }

   Image thumbnail;

   public void setThumbnail(){}

   public Image getThumbnail(){
      return thumbnail;
   }

   String overview;

   public void setOverview(){}

   public String getOverview(){
      return overview;
   }

   String userRating;

   public void setUserRating(){}

   public String getUserRating(){
      return userRating;
   }

   Date releaseDate;

   public void setReleaseDate(){}

   public Date getReleaseDate(){
      return releaseDate;
   }

}
