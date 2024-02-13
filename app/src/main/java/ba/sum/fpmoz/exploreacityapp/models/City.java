package ba.sum.fpmoz.exploreacityapp.models;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class City {
    public String name;
    public String place;
    public String restaurant;
    public String text;
    public String image;
    public Map<String, Float> ratings;

    // NOVO
    // Bezargumetni konstruktor
    public City() {
        // Prazan konstruktor
    }

    public City(String name, String place, String restaurant, String text, String image, HashMap<String, Float> ratings){
        this.name = name;
        this.place = place;
        this.restaurant = restaurant;
        this.text = text;
        this.image = image;
        this.ratings = ratings;
    }

    public float getAverageRating() {
        if (ratings == null || ratings.isEmpty()) {
            return 0;
        }
        float sum = 0;
        for (Float rating : ratings.values()) {
            sum += rating;
        }
        return sum / ratings.size();
    }
}