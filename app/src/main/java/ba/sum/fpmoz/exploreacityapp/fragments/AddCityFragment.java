package ba.sum.fpmoz.exploreacityapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

import ba.sum.fpmoz.exploreacityapp.R;
import ba.sum.fpmoz.exploreacityapp.models.City;

public class AddCityFragment extends Fragment {

    FirebaseStorage storage;
    StorageReference storageReference;
    Uri filePath;
    String cityImageUrl;
    private static final int IMAGE_PICK_REQUEST = 22;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.storage = FirebaseStorage.getInstance();
        this.storageReference = this.storage.getReference();
        View view = inflater.inflate(R.layout.city_item_dalog_view, container, false);
        Button selectImageButton = view.findViewById(R.id.buttonSelectImage);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(i, "Odaberite sliku grada"), 22
                );
            }
        });

        Button insertCityButton = view.findViewById(R.id.buttonInsertCity);

        insertCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from EditTexts
                String name = ((EditText) view.findViewById(R.id.editTextCityName)).getText().toString();
                String place = ((EditText) view.findViewById(R.id.editTextPlaceName)).getText().toString();
                String restaurant = ((EditText) view.findViewById(R.id.editTextRestaurantName)).getText().toString();
                String text = ((EditText) view.findViewById(R.id.editTextNewCity)).getText().toString();

                // Validate Inputs
                if (!validateInputs(name, place, restaurant, text) || cityImageUrl == null) {
                    Toast.makeText(getContext(), "Please upload an image and fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add city to database
                addCityToDatabase(name, place, restaurant, text, cityImageUrl);
                resetForm(view);
            }
        });
        return view;
    }

    private void resetForm(View view) {
        ((EditText) view.findViewById(R.id.editTextCityName)).setText("");
        ((EditText) view.findViewById(R.id.editTextPlaceName)).setText("");
        ((EditText) view.findViewById(R.id.editTextRestaurantName)).setText("");
        ((EditText) view.findViewById(R.id.editTextNewCity)).setText("");
        cityImageUrl = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            uploadImage(); // Call uploadImage method here
        }
    }

    private void uploadImage() {
        if (filePath != null) {
            ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Učitavam sliku");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Slika je učitana na server", Toast.LENGTH_LONG).show();
                            ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    cityImageUrl = task.getResult().toString();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Greška pri učitavanju slike: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void addCityToDatabase(String name, String place, String restaurant, String text, String imageUrl) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference cityRef = database.getReference("city");

        String cityId = cityRef.push().getKey();
        City city = new City(name, place, restaurant, text, imageUrl, new HashMap<>());
        cityRef.child(cityId).setValue(city)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "City added successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add city: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private boolean validateInputs(String name, String place, String restaurant, String text) {
        if (name.isEmpty() || place.isEmpty() || restaurant.isEmpty() || text.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}