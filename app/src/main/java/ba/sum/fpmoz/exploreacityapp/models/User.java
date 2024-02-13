package ba.sum.fpmoz.exploreacityapp.models;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String firstname;
    public String lastname;
    public String profileImageUrl;

    // NOVO
    // Bezargumetni konstruktor
    public User() {
        // Prazan konstruktor
    }

    public User(String firstname, String lastname, String profileImageUrl) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.profileImageUrl = profileImageUrl;
    }
}