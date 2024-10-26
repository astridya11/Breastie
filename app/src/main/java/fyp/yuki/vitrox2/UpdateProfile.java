package fyp.yuki.vitrox2;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfile extends AppCompatActivity {

    private EditText editTextUpdateName, editTextUpdateDoB, editTextUpdateMobile, editTextUpdateCountry, editTextUpdateStage;
    private RadioGroup radioGroupUpdateGender;
    private RadioButton radioButtonUpdateGenderSelected;
    private String textUsername, textDoB, textGender, textMobile, textCountry, textStage;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;
    private ImageView back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressBar);
        editTextUpdateName = findViewById(R.id.editText_update_profile_name);
        editTextUpdateDoB = findViewById(R.id.editText_update_profile_dob);
        editTextUpdateMobile = findViewById(R.id.editText_update_profile_mobile);
        editTextUpdateCountry = findViewById(R.id.editText_update_profile_country);
        editTextUpdateStage = findViewById(R.id.editText_update_profile_stage);
        back_button = findViewById(R.id.back_update_profile);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfile.this, UserProfile.class);
                startActivity(intent);
                finish();
            }
        });

        radioGroupUpdateGender = findViewById(R.id.radio_group_update_gender);
        authProfile= FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        showProfile(firebaseUser);

        Button buttonUploadProfilePic = findViewById(R.id.button_upload_profile_pic);
        buttonUploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfile.this, UploadProfile.class);
                startActivity(intent);
                finish();
            }
        });

        editTextUpdateDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textSADoB[] = textDoB.split("/");

                int day = Integer.parseInt(textSADoB[0]);
                int month = Integer.parseInt(textSADoB[1]) - 1;
                int year = Integer.parseInt(textSADoB[2]);

                DatePickerDialog picker;

                picker = new DatePickerDialog(UpdateProfile.this, new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
                        // Update the EditText with the selected date
                        editTextUpdateDoB.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year));
                    }
                }, year, month, day);
                picker.show();
            }
        });

        Button buttonUpdateProfile = findViewById(R.id.button_update_profile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });
    }

    private void updateProfile(FirebaseUser firebaseUser) {
        int selectedGenderID = radioGroupUpdateGender.getCheckedRadioButtonId();
        radioButtonUpdateGenderSelected = findViewById(selectedGenderID);

        String phoneRegex = "^(01)[0-9]-?[0-9]{7,8}$";
        Matcher phoneMatcher;
        Pattern phonePattern = Pattern.compile(phoneRegex);
        phoneMatcher = phonePattern.matcher(textMobile);

        if (TextUtils.isEmpty(textUsername)) {
            Toast.makeText(UpdateProfile.this, "Please enter your username", Toast.LENGTH_LONG).show();
            editTextUpdateName.setError("Username is required");
            editTextUpdateName.requestFocus();

        }else if(TextUtils.isEmpty(textDoB)){
            Toast.makeText(UpdateProfile.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
            editTextUpdateDoB.setError("Date of Birth is required");
            editTextUpdateDoB.requestFocus();
        }else if(TextUtils.isEmpty(textCountry)){
            Toast.makeText(UpdateProfile.this, "Please enter your country", Toast.LENGTH_LONG).show();
            editTextUpdateCountry.setError("Country is required");
            editTextUpdateCountry.requestFocus();
        }else if(TextUtils.isEmpty(radioButtonUpdateGenderSelected.getText())){
            Toast.makeText(UpdateProfile.this, "Please select your gender", Toast.LENGTH_LONG).show();
            radioButtonUpdateGenderSelected.setError("Gender is required");
            radioButtonUpdateGenderSelected.requestFocus();
        }else if(TextUtils.isEmpty(textMobile)){
            Toast.makeText(UpdateProfile.this, "Please enter your phone number", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Phone number is required");
            editTextUpdateMobile.requestFocus();
        }else if(!phoneMatcher.find()){
            Toast.makeText(UpdateProfile.this, "Please enter your valid phone number in Malaysia", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Phone no. is not valid");
            editTextUpdateMobile.requestFocus();
        }else if(textMobile.length() != 10 && textMobile.length() != 11){
            Toast.makeText(UpdateProfile.this, "Please re-enter your phone number", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Phone number should be 10 or 11 digits");
            editTextUpdateMobile.requestFocus();
        }else{
            String email = firebaseUser.getEmail();
            textGender = radioButtonUpdateGenderSelected.getText().toString();
            textUsername = editTextUpdateName.getText().toString();
            textDoB = editTextUpdateDoB.getText().toString();
            textMobile = editTextUpdateMobile.getText().toString();
            textCountry = editTextUpdateCountry.getText().toString();
            textStage = editTextUpdateStage.getText().toString();

            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(email, textUsername, textDoB, textGender, textMobile,textCountry,null);
//            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
            String userID = firebaseUser.getUid();
            DocumentReference referenceProfile = FirebaseFirestore.getInstance().collection("Registered Users").document(userID);
            progressBar.setVisibility(View.VISIBLE);
//            referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if(task.isSuccessful()){
//                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                                .setDisplayName(textUsername).build();
//                        firebaseUser.updateProfile(profileUpdates);
//
//                        Toast.makeText(UpdateProfile.this, "Update Successfully!", Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(UpdateProfile.this, UserProfile.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        try {
//                            throw task.getException();
//                        } catch (Exception e){
//                            Toast.makeText(UpdateProfile.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                    progressBar.setVisibility(View.GONE);
//                }
//            });

            referenceProfile.set(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(textUsername).build();
                        firebaseUser.updateProfile(profileUpdates);

                        Toast.makeText(UpdateProfile.this, "Update Successfully!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(UpdateProfile.this, UserProfile.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(UpdateProfile.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UpdateProfile.this, "Error updating profile!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

//    private void showProfile(FirebaseUser firebaseUser) {
//        String userIDofRegistered = firebaseUser.getUid();
//
//        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
//        progressBar.setVisibility(View.VISIBLE);
//        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
//                if(readUserDetails != null){
//                    textUsername = firebaseUser.getDisplayName();
//                    textDoB = readUserDetails.getDOB();
//                    textGender = readUserDetails.getGender();
//                    textMobile = readUserDetails.getMobile();
//                    textCountry = readUserDetails.getCountry();
//
//                    editTextUpdateName.setText(textUsername);
//                    editTextUpdateDoB.setText(textDoB);
//                    editTextUpdateMobile.setText(textMobile);
//                    editTextUpdateCountry.setText(textCountry);
//
//                    if(textGender.equals("Male")){
//                        radioButtonUpdateGenderSelected = findViewById(R.id.radio_male);
//                    }else{
//                        radioButtonUpdateGenderSelected = findViewById(R.id.radio_female);
//                    }
//
//                    radioButtonUpdateGenderSelected.setChecked(true);
//                }else{
//                    Toast.makeText(UpdateProfile.this,"Something went wrong!", Toast.LENGTH_LONG).show();
//                }
//                progressBar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(UpdateProfile.this,"Something went wrong!", Toast.LENGTH_LONG).show();
//                progressBar.setVisibility(View.GONE);
//            }
//        });
//    }
private void showProfile(FirebaseUser firebaseUser) {
    String userID = firebaseUser.getUid();
    DocumentReference referenceProfile = FirebaseFirestore.getInstance().collection("Registered Users").document(userID);
    progressBar.setVisibility(View.VISIBLE);

    referenceProfile.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    ReadWriteUserDetails readUserDetails = document.toObject(ReadWriteUserDetails.class);
                    if (readUserDetails != null) {
                        textUsername = firebaseUser.getDisplayName();
                        textDoB = readUserDetails.getDOB();
                        textGender = readUserDetails.getGender();
                        textMobile = readUserDetails.getMobile();
                        textCountry = readUserDetails.getCountry();

                        editTextUpdateName.setText(textUsername);
                        editTextUpdateDoB.setText(textDoB);
                        editTextUpdateMobile.setText(textMobile);
                        editTextUpdateCountry.setText(textCountry);

                        if ("Male".equals(textGender)) {
                            radioButtonUpdateGenderSelected = findViewById(R.id.radio_male);
                        } else {
                            radioButtonUpdateGenderSelected = findViewById(R.id.radio_female);
                        }

                        radioButtonUpdateGenderSelected.setChecked(true);
                    } else {
                        Toast.makeText(UpdateProfile.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(UpdateProfile.this, "No such document exists!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(UpdateProfile.this, "Task failed with " + task.getException(), Toast.LENGTH_LONG).show();
            }
            progressBar.setVisibility(View.GONE);
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(UpdateProfile.this, "Error fetching document!", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    });
}


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();

        if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(UpdateProfile.this);
        }else if(id == R.id.menu_update_profile){
            Intent intent = new Intent(UpdateProfile.this, UpdateProfile.class);
            startActivity(intent);
            finish();
        }else if(id == R.id.menu_change_password){
            Intent intent = new Intent(UpdateProfile.this, ChangePassword.class);
            startActivity(intent);
            finish();
        }else if(id == R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(UpdateProfile.this, "Logged Out", Toast.LENGTH_LONG).show();
//          TO_DO: integrate with min min
            Intent intent = new Intent(UpdateProfile.this, LogOrSignActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(UpdateProfile.this, "Something went wrong!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }
}