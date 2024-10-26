package fyp.yuki.vitrox2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import fyp.yuki.vitrox2.Intro.IntroPage;

public class UserProfile extends AppCompatActivity {

    private TextView showHello, showUsername, showEmail, showDoB, showGender, showPhone, showCountry;
    private ProgressBar progressBar;
    private String username, email, DOB, gender, phone, country;
    private FirebaseAuth authProfile;
    private ImageView genderView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showHello = findViewById(R.id.show_hello);
        showUsername = findViewById(R.id.show_username);
        showEmail = findViewById(R.id.show_email);
        showDoB = findViewById(R.id.show_dob);
        showGender = findViewById(R.id.show_gender);
        showPhone = findViewById(R.id.show_phone);
        showCountry = findViewById(R.id.show_country);
        progressBar = findViewById(R.id.progressBar);


        LinearLayout homeBt = findViewById(R.id.home);
        LinearLayout bmiBt = findViewById(R.id.bmi_icon);
        LinearLayout ARBt = findViewById(R.id.AR);
        LinearLayout grpchatBt = findViewById(R.id.chatBot);
        LinearLayout userBt = findViewById(R.id.user_icon);
        LinearLayout communityBt = findViewById(R.id.community);

//        // food scanning
//        scanBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(UserProfile.this, FoodScanningPage.class);
//                startActivity(intent);
//                finish();
//            }
//        });

//        // user profile
//        userBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(UserProfile.this, UserProfile.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        // chat
        grpchatBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, GroupListPage.class);
                startActivity(intent);
                finish();
            }
        });

        // community
        communityBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, Community.class);
                startActivity(intent);
                finish();
            }
        });

         //main activity
        homeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

//        // info page
//        infoBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(UserProfile.this, IntroPage.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        // bmi calculator
        bmiBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, bmi.class);
                startActivity(intent);
                finish();
            }
        });

        // AR self examination
        ARBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, ARSelfCheckActivity.class);
                startActivity(intent);
                finish();
            }
        });

//        // aichatbot
//        chatBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(UserProfile.this, ChatbotActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });


        imageView = findViewById(R.id.profile);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, UploadProfile.class);
                startActivity(intent);
            }
        });

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser == null){
            Toast.makeText(UserProfile.this, "Something went wrong! User's details are not available at the moment"
                    , Toast.LENGTH_LONG).show();
        } else{
            showUserProfile(firebaseUser);
            checkIfEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
        }

    }

    private void checkIfEmailVerified(FirebaseUser firebaseUser) {
        if(!firebaseUser.isEmailVerified()){
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now. You cannot login without email verification next time");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                //TO_DO: match with logout activity
//                Intent intent = new Intent(UserProfile.this, LoginActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        DocumentReference referenceProfile = FirebaseFirestore.getInstance().collection("Registered Users").document(userID);
        referenceProfile.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ReadWriteUserDetails readWriteUserDetails = document.toObject(ReadWriteUserDetails.class);
                        if (readWriteUserDetails != null) {
                            username = firebaseUser.getDisplayName();
                            email = firebaseUser.getEmail();
                            DOB = readWriteUserDetails.getDOB();
                            gender = readWriteUserDetails.getGender();
                            phone = readWriteUserDetails.getMobile();
                            country = readWriteUserDetails.getCountry();

                            showHello.setText("Hello, " + username + "!");
                            showUsername.setText(username);
                            showEmail.setText(email);
                            showDoB.setText(DOB);
                            showGender.setText(gender);
                            showPhone.setText(phone);
                            showCountry.setText(country);

                            //Load the image from the retrieved URL into the ImageView
                            String imageUrl = readWriteUserDetails.getProfile();
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Picasso.get().load(imageUrl).into(imageView);
                            } else {
                                // Set default profile picture here
                                imageView.setImageResource(R.drawable.no_pic_profile);
                            }
                        } else {
                            Toast.makeText(UserProfile.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(UserProfile.this, "No such document exists!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(UserProfile.this, "Task failed with " + task.getException(), Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfile.this, "Error fetching document!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        }) ;

        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();

       if(id == R.id.menu_update_profile){
            Intent intent = new Intent(UserProfile.this, UpdateProfile.class);
            startActivity(intent);
            finish();
        }else if(id == R.id.menu_change_password){
            Intent intent = new Intent(UserProfile.this, ChangePassword.class);
            startActivity(intent);
            finish();
        }else if(id == R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(UserProfile.this, "Logged Out", Toast.LENGTH_LONG).show();
            //TO_DO: to minmin's page
//            Intent intent = new Intent(UserProfile.this, WelcomeActivity.class);
//
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finish();
        }else{
            Toast.makeText(UserProfile.this, "Something went wrong!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
