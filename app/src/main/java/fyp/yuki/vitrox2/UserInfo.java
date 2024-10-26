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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserInfo extends AppCompatActivity {
    private TextView textViewStage;
    private EditText editTextDOB, editTextMobile, editTextCountry, editTextStage;
    private RadioGroup radioGroupGender, radioGroupYesNo;
    private RadioButton radioButtonGenderSelected;
    private String textDOB, textGender, textMobile, textCountry, textStage;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_info);

        progressBar = findViewById(R.id.user_info_progress_bar);
        editTextDOB = findViewById(R.id.editText_DOB);
        editTextMobile = findViewById(R.id.editText_mobile);
        editTextCountry = findViewById(R.id.editText_country);
        editTextStage = findViewById(R.id.editText_stage);
        editTextCountry = findViewById(R.id.editText_country);
        textViewStage = findViewById(R.id.textView_stage);

        radioGroupGender = findViewById(R.id.radio_group_gender);
        radioGroupYesNo = findViewById(R.id.radio_group_yes_no);
        authProfile= FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        editTextDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textSADoB[] = textDOB.split("/");

                int day = Integer.parseInt(textSADoB[0]);
                int month = Integer.parseInt(textSADoB[1]) - 1;
                int year = Integer.parseInt(textSADoB[2]);

                DatePickerDialog picker;

                picker = new DatePickerDialog(UserInfo.this, new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
                        // Update the EditText with the selected date
                        editTextDOB.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year));
                    }
                }, year, month, day);
                picker.show();
            }
        });

        RadioButton radioYesSelected=findViewById(R.id.radio_yes);
        radioYesSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewStage.setVisibility(View.VISIBLE);
                editTextStage.setVisibility(View.VISIBLE);
            }
        });
        RadioButton radioNoSelected=findViewById(R.id.radio_no);
        radioNoSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewStage.setVisibility(View.INVISIBLE);
                editTextStage.setVisibility(View.INVISIBLE);
            }
        });

        Button buttonSubmit = findViewById(R.id.button_upload_user_info);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });
    }

    private void updateProfile(FirebaseUser firebaseUser) {
        int selectedGenderID = radioGroupGender.getCheckedRadioButtonId();
        radioButtonGenderSelected = findViewById(selectedGenderID);

        textGender = radioButtonGenderSelected.getText().toString();
        textDOB = editTextDOB.getText().toString();
        textMobile = editTextMobile.getText().toString();

        textStage = editTextStage.getText().toString();
        textCountry = editTextCountry.getText().toString();

        Pattern pattern = Pattern.compile("^01\\d{8}$");
        Matcher phoneMatcher = pattern.matcher(textMobile);

        if(TextUtils.isEmpty(textDOB)){
            Toast.makeText(UserInfo.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
            editTextDOB.setError("Date of Birth is required");
            editTextDOB.requestFocus();
        }else if(TextUtils.isEmpty(textCountry)){
            Toast.makeText(UserInfo.this, "Please enter your country", Toast.LENGTH_LONG).show();
            editTextCountry.setError("Country is required");
            editTextCountry.requestFocus();
        }else if(TextUtils.isEmpty(radioButtonGenderSelected.getText())){
            Toast.makeText(UserInfo.this, "Please select your gender", Toast.LENGTH_LONG).show();
            radioButtonGenderSelected.setError("Gender is required");
            radioButtonGenderSelected.requestFocus();
        }else if(TextUtils.isEmpty(textMobile)){
            Toast.makeText(UserInfo.this, "Please enter your phone number", Toast.LENGTH_LONG).show();
            editTextMobile.setError("Phone number is required");
            editTextMobile.requestFocus();
        }else if(!phoneMatcher.find()){
            Toast.makeText(UserInfo.this, "Please enter your valid phone number in Malaysia", Toast.LENGTH_LONG).show();
            editTextMobile.setError("Phone no. is not valid");
            editTextMobile.requestFocus();
        }else if(textMobile.length() != 10 && textMobile.length() != 11){
            Toast.makeText(UserInfo.this, "Please re-enter your phone number", Toast.LENGTH_LONG).show();
            editTextMobile.setError("Phone number should be 10 or 11 digits");
            editTextMobile.requestFocus();
        }else{
            String email = firebaseUser.getEmail();
            String username = firebaseUser.getDisplayName();
            textGender = radioButtonGenderSelected.getText().toString();
            textDOB = editTextDOB.getText().toString();
            textMobile = editTextMobile.getText().toString();

            textStage = editTextStage.getText().toString();
            textCountry = editTextCountry.getText().toString();

            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(email, username, textDOB, textGender, textMobile, textCountry, textStage);


            progressBar.setVisibility(View.VISIBLE);
//
            // create HashMap to store user data
            HashMap<String,Object> userData = new HashMap<>();
            userData.put("username", username);
            userData.put("email", email);
            userData.put("gender",textGender);
            userData.put("DOB",textDOB);
            userData.put("mobile",textMobile);
            userData.put("country", textCountry);
            userData.put("stage", textStage);


            // save to Firestore
            FirebaseFirestore.getInstance().collection("Registered Users").document(firebaseUser.getUid())
                    .set(userData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(UserInfo.this,"User data saved successfully.", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        //TO_DO: yuki de
 //                       Intent intent = new Intent(UserInfo.this, WelcomeActivity.class);
//
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(UserInfo.this, "Failed to save user data." + e.getMessage(), Toast.LENGTH_SHORT).show());

        }
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
            NavUtils.navigateUpFromSameTask(UserInfo.this);
        }else if(id == R.id.menu_update_profile){
            Intent intent = new Intent(UserInfo.this, UpdateProfile.class);
            startActivity(intent);
            finish();
        }else if(id == R.id.menu_change_password){
            Intent intent = new Intent(UserInfo.this, ChangePassword.class);
            startActivity(intent);
            finish();
        }else if(id == R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(UserInfo.this, "Logged Out", Toast.LENGTH_LONG).show();
//          TO_DO: integrate with min min
//            Intent intent = new Intent(UpdateProfile.this, WelcomeActivity.class);
//
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finish();
        }else{
            Toast.makeText(UserInfo.this, "Something went wrong!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
