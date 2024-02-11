package ba.sum.fpmoz.exploreacityapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

import ba.sum.fpmoz.exploreacityapp.models.User;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase db;
    FirebaseAuth mAuth;
    StorageReference storageReference;
    String uuid;
    Uri filePath;
    EditText firstnameTxt;
    EditText lastnameTxt;
    ImageView selectImageButton;
    ImageView navigationUserImage;
    TextView navigationUserFullName;
    NavigationView navigationView;

    //NOVO
    TextView textView;
    private static final int IMAGE_PICK_REQUEST = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        try {
            this.uuid = currentUser.getUid();
        } catch (Exception e) {}

        this.db = FirebaseDatabase.getInstance();

        firstnameTxt = findViewById(R.id.first_name);
        lastnameTxt = findViewById(R.id.last_name);
        selectImageButton = findViewById(R.id.profile_image);

        Button submitBtn = findViewById(R.id.submit_button);
        DatabaseReference usersDbRef = this.db.getReference("users");

        storageReference = FirebaseStorage.getInstance().getReference();

        selectImageButton.setOnClickListener(v -> openFileChooser());


        /* //NOVO
        textView = findViewById(R.id.loginNow);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }); */




        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filePath != null) {
                    StorageReference fileReference = storageReference.child("profile_images/" + UUID.randomUUID().toString());
                    fileReference.putFile(filePath)
                            .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                User u = new User(
                                        firstnameTxt.getText().toString(),
                                        lastnameTxt.getText().toString(),
                                        imageUrl
                                );
                                usersDbRef.child(uuid).setValue(u);
                                Toast.makeText(MainActivity.this, "Podaci uspješno spremljeni", Toast.LENGTH_SHORT).show();
                            }));
                } else {
                    // Handle the case when no image is selected
                }
            }
        });

        this.fetchUserData();
        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_logout) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    FirebaseAuth.getInstance().signOut();
                    return true;
                }
                if (item.getItemId() == R.id.nav_profile) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }
                if (item.getItemId() == R.id.nav_city) {
                    Intent intent = new Intent(getApplicationContext(), CityActivity.class);
                    startActivity(intent);
                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }
                return false;
            }
        });

        View headerView = navigationView.getHeaderView(0);

        navigationUserImage = headerView.findViewById(R.id.user_profile_image);
        navigationUserFullName = headerView.findViewById(R.id.user_firstlastname);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Explore a city aplikacija");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_PICK_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(filePath);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                selectImageButton.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void fetchUserData() {
        DatabaseReference usersDbRef = db.getReference("users");
        try {
            String userId = mAuth.getCurrentUser().getUid();
            usersDbRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                //ovo rekao ispod da ness pobrisem
                 @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        User user = dataSnapshot.getValue(User.class);
                        // Now update your UI with this user data
                        // e.g., firstnameTxt.setText(user.getFirstName());
                        firstnameTxt.setText(user.firstname);
                        lastnameTxt.setText(user.lastname);
                        Picasso.get()
                                .load(user.profileImageUrl)
                                .into(selectImageButton);

                        Picasso.get()
                                .load(user.profileImageUrl)
                                .into(navigationUserImage);

                        navigationUserFullName.setText(user.firstname + " " +  user.lastname);
                    } catch (NullPointerException e){

                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NullPointerException e) {}
    }
}