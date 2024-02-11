package ba.sum.fpmoz.exploreacityapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ba.sum.fpmoz.exploreacityapp.R;
import ba.sum.fpmoz.exploreacityapp.adapters.CityAdapter;
import ba.sum.fpmoz.exploreacityapp.models.City;

public class CityFragment extends Fragment implements CityAdapter.OnRatingChangedListener {

    FirebaseDatabase cityDatabase = FirebaseDatabase.getInstance();
    CityAdapter cityAdapter;
    RecyclerView cityRecyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_city, container, false);

        this.cityRecyclerView = view.findViewById(R.id.cityListView);
        this.cityRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext())
        );
        FirebaseRecyclerOptions<City> options = new FirebaseRecyclerOptions.Builder<City>().setQuery(
                this.cityDatabase.getReference("city"),
                City.class
        ).build();

        this.cityAdapter = new CityAdapter(options, this);
        this.cityRecyclerView.setAdapter(this.cityAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.cityAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.cityAdapter.stopListening();
    }

    @Override
    public void onRatingChanged(String cityId, float rating) {
        // Update the rating in Firebase
        DatabaseReference cityDbRef = FirebaseDatabase.getInstance().getReference("city").child(cityId);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cityDbRef.child("ratings").child(userId).setValue(rating);
    }
}
