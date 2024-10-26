package fyp.yuki.vitrox2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class bmi extends AppCompatActivity {

    RadioGroup genderGroup;
    EditText ageInput, heightInput, weightInput;
    Button calculateButton;
    RelativeLayout resultContainer;
    TextView resultText, bmiValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bmi);

        genderGroup = findViewById(R.id.genderGroup);
        ageInput = findViewById(R.id.ageInput);
        heightInput = findViewById(R.id.heightInput);
        weightInput = findViewById(R.id.weightInput);
        calculateButton = findViewById(R.id.calculateButton);
        resultContainer = findViewById(R.id.resultContainer);
        resultText = findViewById(R.id.resultText);
        bmiValue = findViewById(R.id.bmiValue);

        // Check if calculateButton is null (although it shouldn't be)
        if (calculateButton == null) {
            Toast.makeText(this, "calculateButton is null", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set onClickListener for calculateButton
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBMI();
            }
        });

        // Initialize backButton ImageView
        ImageView backButton = findViewById(R.id.backButton);

        // Set onClickListener for backButton
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(bmi.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        LinearLayout homeBt = findViewById(R.id.home);
        LinearLayout bmiBt = findViewById(R.id.bmi_icon);
        LinearLayout ARBt = findViewById(R.id.AR);
        LinearLayout grpchatBt = findViewById(R.id.chatBot);
        LinearLayout userBt = findViewById(R.id.user_icon);
        LinearLayout communityBt = findViewById(R.id.community);

        // user profile
        userBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                startActivity(intent);
                finish();
            }
        });

        // chat
        grpchatBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GroupListPage.class);
                startActivity(intent);
                finish();
            }
        });

        // community
        communityBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Community.class);
                startActivity(intent);
                finish();
            }
        });

        // main activity
        homeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

//        // bmi calculator
//        bmiBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), bmi.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        // AR self examination
        ARBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ARSelfCheckActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void calculateBMI() {
        String ageStr = ageInput.getText().toString();
        String heightStr = heightInput.getText().toString();
        String weightStr = weightInput.getText().toString();

        if (ageStr.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageStr);
        int height = Integer.parseInt(heightStr);
        int weight = Integer.parseInt(weightStr);

        if (height == 0) {
            Toast.makeText(this, "Height cannot be zero", Toast.LENGTH_SHORT).show();
            return;
        }

        float heightInMeters = height / 100.0f;
        float bmi = weight / (heightInMeters * heightInMeters);

        String bmiLabel;
        int bmiColor;
        if (bmi < 18.5) {
            bmiLabel = "Underweight";
            bmiColor = getResources().getColor(android.R.color.holo_orange_light);
        } else if (bmi < 25) {
            bmiLabel = "Normal";
            bmiColor = getResources().getColor(android.R.color.holo_green_light);
        } else if (bmi < 30) {
            bmiLabel = "Overweight";
            bmiColor = getResources().getColor(android.R.color.holo_orange_dark);
        } else {
            bmiLabel = "Obese";
            bmiColor = getResources().getColor(android.R.color.holo_red_dark);
        }

        // Update UI with BMI result
        resultContainer.setVisibility(View.VISIBLE);
        bmiValue.setText(String.format("%.1f kg/m² - %s", bmi, bmiLabel));
        bmiValue.setTextColor(bmiColor);
    }
}
