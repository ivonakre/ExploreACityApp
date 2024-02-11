package ba.sum.fpmoz.exploreacityapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import ba.sum.fpmoz.exploreacityapp.R;
import ba.sum.fpmoz.exploreacityapp.models.City;

public class CityAdapter extends FirebaseRecyclerAdapter<City, CityAdapter.CityViewHolder> {
    public interface OnRatingChangedListener {
        void onRatingChanged(String cityId, float rating);
    }

    OnRatingChangedListener ratingChangedListener;
    Context ctx;


    public CityAdapter(@NonNull FirebaseRecyclerOptions<City> options, OnRatingChangedListener listener) {
        super(options);
        this.ratingChangedListener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull CityViewHolder holder, int position, @NonNull City model) {
        holder.cityItemName.setText(model.name);
        holder.cityItemPlace.setText(model.place);
        holder.cityItemRestaurant.setText(model.restaurant);
        holder.cityItemText.setText(model.text);
        Picasso.get().load(model.image).into(holder.cityItemImageView);
        holder.cityItemRating.setRating(model.getAverageRating());
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.ctx = parent.getContext();
        View view = LayoutInflater.from(this.ctx).inflate(R.layout.city_item_list_view, parent, false);
        return new CityViewHolder(view);
    }

    public class CityViewHolder extends RecyclerView.ViewHolder {
        ImageView cityItemImageView;
        TextView cityItemName, cityItemPlace, cityItemRestaurant, cityItemText;
        RatingBar cityItemRating;

        public CityViewHolder(@NonNull View itemView) {
            super(itemView);
            this.cityItemImageView = itemView.findViewById(R.id.cityItemImageView);
            this.cityItemName = itemView.findViewById(R.id.cityItemName);
            this.cityItemPlace = itemView.findViewById(R.id.cityItemPlace);
            this.cityItemRestaurant = itemView.findViewById(R.id.cityItemRestaurant);
            this.cityItemText = itemView.findViewById(R.id.cityItemText);
            this.cityItemRating = itemView.findViewById(R.id.cityItemRating);

            cityItemRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if (fromUser) {
                        String cityId = getRef(getAdapterPosition()).getKey();
                        ratingChangedListener.onRatingChanged(cityId, rating);
                    }
                }
            });
        }
    }
}