package fyp.yuki.vitrox2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import fyp.yuki.vitrox2.Intro.IntroPage;
import fyp.yuki.vitrox2.Recipe.Recipe;
import fyp.yuki.vitrox2.Recipe.RecipeAdapter;
import fyp.yuki.vitrox2.Recipe2.Recipe2;
import fyp.yuki.vitrox2.Recipe2.RecipeAdapter2;
import fyp.yuki.vitrox2.Recipe3.Recipe3;
import fyp.yuki.vitrox2.Recipe3.RecipeAdapter3;
import fyp.yuki.vitrox2.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    TextView textView6;
    CircularProgressIndicator circularProgressIndicator;

    int dailyCalorieLimit = 2000;
    String totalCaloriesConsumed;

    boolean canCalculate = false;
    int totalCalories;
    int newInputCalories;
    String displaycalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        textView6 = findViewById(R.id.textView6);
        circularProgressIndicator = findViewById(R.id.circularProgressIndicator);
        ImageView clearSP = findViewById(R.id.clearSharedPreferences);

        recipeList();
        recipeList2();
        recipeList3();

        ImageView scanBt = findViewById(R.id.scan_image);
        LinearLayout homeBt = findViewById(R.id.home);
        LinearLayout bmiBt = findViewById(R.id.bmi_icon);
        LinearLayout ARBt = findViewById(R.id.AR);
        LinearLayout grpchatBt = findViewById(R.id.chatBot);
        LinearLayout userBt = findViewById(R.id.user_icon);
        LinearLayout communityBt = findViewById(R.id.community);
        ImageView chatBt = findViewById(R.id.chatbot_image);
        ImageView infoBt = findViewById(R.id.info_image);

        // food scanning
        scanBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FoodScanningPage.class);
                startActivity(intent);
                finish();
            }
        });

        // user profile
        userBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserProfile.class);
                startActivity(intent);
                finish();
            }
        });

        // chat
        grpchatBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GroupListPage.class);
                startActivity(intent);
                finish();
            }
        });

        // community
        communityBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Community.class);
                startActivity(intent);
                finish();
            }
        });

        // main activity
//        homeBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        // info page
        infoBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, IntroPage.class);
                startActivity(intent);
                finish();
            }
        });

        // bmi calculator
        bmiBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, bmi.class);
                startActivity(intent);
                finish();
            }
        });

        // AR self examination
        ARBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ARSelfCheckActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // aichatbot
        chatBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChatbotActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // retrieve the calories and update textview
        SharedPreferences sharedPreferences = getSharedPreferences("CaloriesPreferences", MODE_PRIVATE);
        totalCalories = sharedPreferences.getInt("totalCalories", 0);

        canCalculate = FoodScanningPage.isIntented();
        if (canCalculate)
        {
            Intent intent = getIntent();
            String calories = intent.getStringExtra("calories");//to call the message that stored from foodscanningpage

            if (calories != null) {
                newInputCalories = Integer.parseInt(calories);
                totalCalories += newInputCalories;

                // update total calories in shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("totalCalories", totalCalories);
                editor.apply();
            }
        }

        displaycalories = Integer.toString(totalCalories);
        textView6.setText(displaycalories);

        // check if total calories >2000
        if (totalCalories > 2000)
        {
            textView6.setTextColor(getResources().getColor(R.color.red));
        }
        else
        {
            textView6.setTextColor(getResources().getColor(R.color.white));
        }
    }

    public void clearSharedPreferences(View view)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("CaloriesPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        totalCalories = 0;
        editor.putInt("totalCalories", totalCalories);
        editor.apply();

        displaycalories = Integer.toString(totalCalories);
        textView6.setText(displaycalories);
        textView6.setTextColor(getResources().getColor(R.color.white));
    }

    private void recipeList() {
        ArrayList<Recipe> recipe_items = new ArrayList<>();
        recipe_items.add(new Recipe("Avocado toast", "Avocado toast is a nutritious option for breast cancer patients, offering healthy fats, vitamins, and fiber from avocados and whole-grain bread. It's easily customizable with lean proteins and fresh vegetables for added nutritional benefits.", "Whole-grain bread (1 slice), Avocado (1/2 medium), 1 hard boiled egg", "3.9", 25, 10, 15, "breakfast1", "270"));
        recipe_items.add(new Recipe("Mixed berries yoghurt bowl", "A mixed berry smoothie is a refreshing and nutritious drink for breast cancer patients, packed with antioxidants, vitamins, and fiber. Made with a blend of berries, Greek yogurt, and a touch of honey, it supports overall health and well-being.", "Berries (1 cup mixed), Greek yogurt (1/2 cup), Honey (1 tbsp), Almond milk (1/2 cup)", "4.8", 37, 11, 4, "breakfast2", "210"));
        recipe_items.add(new Recipe("Orange juice", "Orange juice is a refreshing and nutritious beverage for breast cancer patients, rich in vitamin C, antioxidants, and other essential nutrients.", "2 fresh oranges.", "4.5", 26, 2, 0, "breakfast3", "110"));
        recipe_items.add(new Recipe("Strawberry smoothie bowl", "A strawberry smoothie bowl is a delicious and nutritious option for breast cancer patients, offering vitamins, fiber, and antioxidants. Made with blended strawberries, yogurt, and topped with nuts and seeds, it supports overall health.", "Strawberries (1 cup mixed), Banana (1/2 medium), Greek yogurt (1/2 cup), Honey (1 tbsp), Almond milk (1/2 cup), Chia seeds (1 tbsp), Sliced almond (1tbsp)", "4.5", 22, 13, 9, "breakfast4", "285"));
        recipe_items.add(new Recipe("Mixed berries cereal bowl", "A cereal bowl with strawberries and berries can be a nutritious breakfast option for breast cancer patients, providing fiber, vitamins, and antioxidants.", "Cereal (1 cup), Strawberries (1/2 cup sliced), Mixed berries (1/2 cup), Milk (1/2 cup)", "3.9", 45, 0, 0, "breakfast5", "257"));

        // Shuffle the recipe list
        Collections.shuffle(recipe_items);

        binding.recipeView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recipeView.setAdapter(new RecipeAdapter(recipe_items));
    }

    private void recipeList2() {
        ArrayList<Recipe2> recipe_items2 = new ArrayList<>();
        recipe_items2.add(new Recipe2("Baked salmon with vegetables", "Baked or grilled salmon served with a side of roasted or steamed vegetables, providing essential omega-3 fatty acids, protein, fiber, and vitamins.", "Salmon (4-6 oz), Vegetables (1 cup)", "3.9", 25, 10, 15, "lunch1", "300"));
        recipe_items2.add(new Recipe2("Stuffed peppers", "Bell peppers stuffed with a mixture of lean ground turkey or chicken, quinoa or brown rice, black beans, and vegetables, providing protein, fiber, and various nutrients.", "Bell peppers (1 medium), Lean ground turkey or chicken (4 oz), Quinoa or brown rice (1/2 cup), Black beans (1/4 cup)", "4.5", 30, 20, 10, "lunch2", "350"));
        recipe_items2.add(new Recipe2("Vegetable stir-fry", "Colorful mix of stir-fried vegetables (bell peppers, broccoli, snap peas, carrots) with lean protein (tofu, chicken, or shrimp), served over whole grain noodles or brown rice for fiber, protein, and vitamins.", "Mixed vegetables (2 cups), Lean protein (4 oz tofu or chicken), Whole grain noodles or brown rice (1 cup)", "4.2", 40, 15, 8, "lunch3", "320"));
        recipe_items2.add(new Recipe2("Omega Boost Bowl", "A nutrient-rich bowl packed with omega-3 fatty acids and antioxidants.", "Brown rice (1/2 cup cooked), Corn (1/4 cup), Avocado (1/2), Half boiled eggs (1), Lemon (wedge), Lettuce (1 cup), Small tomatoes (3-4), Salmon (3 oz)", "4.0", 25, 15, 10, "intro1", "450"));
        recipe_items2.add(new Recipe2("Protein Power Bowl", "A protein-packed lunch to support muscle health and energy levels.", "Brown rice (1/2 cup cooked), Green peppers (1/2 cup), Black beans (1/4 cup), Fried eggs (1), Chicken breast (3 oz)", "4.2", 30, 20, 12, "intro2", "400"));
        recipe_items2.add(new Recipe2("Fruit & Nut Salad", "A refreshing and antioxidant-rich salad for a sweet treat.", "Strawberries (1/2 cup, sliced), Bananas (1 medium, sliced), Kiwi (1, sliced), Grapes (1/2 cup), Lettuce (1 cup), Almonds or nuts (1/4 cup), Homemade biscuits (1-2 small)", "3.8", 20, 10, 8, "intro7", "380"));

        // Shuffle the recipe list
        Collections.shuffle(recipe_items2);

        binding.recipeView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recipeView2.setAdapter(new RecipeAdapter2(recipe_items2));

    }

    private void recipeList3() {
        ArrayList<Recipe3> recipe_items3 = new ArrayList<>();
        recipe_items3.add(new Recipe3("Chicken Wrap", "Whole grain wrap filled with grilled or baked chicken breast, lettuce, tomatoes, cucumbers, and optional avocado, served with hummus or Greek yogurt dressing.", "Whole grain wrap, Chicken breast (grilled or baked), Lettuce or spinach, Tomatoes, Cucumbers, Avocado (optional), Hummus or Greek yogurt dressing", "4.0", 30, 15, 10, "dinner3", "350"));
        recipe_items3.add(new Recipe3("Balanced Plate", "A balanced meal featuring steamed corn, half boiled eggs, broccoli, roasted pumpkin, segmented oranges, and grilled chicken breast.", "Corn (1/2 cup, steamed), Half boiled eggs (1-2), Broccoli (1 cup, steamed), Pumpkin (1/2 cup, diced and roasted), Oranges (1 medium, segmented), Grilled chicken breast (3 oz)", "4.2", 25, 20, 12, "intro8", "380"));
        recipe_items3.add(new Recipe3("Vegetable Plate", "A vegetable-focused plate with small tomatoes, half boiled eggs, carrots, pumpkins, apples, and oranges.", "Mixed vegetables (small tomatoes, carrots, pumpkins, apples, oranges), Half boiled eggs (1-2)", "3.8", 20, 10, 8, "intro9", "320"));

        // Shuffle the recipe list
        Collections.shuffle(recipe_items3);

        binding.recipeView3.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recipeView3.setAdapter(new RecipeAdapter3(recipe_items3));
    }
}
