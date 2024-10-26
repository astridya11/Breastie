package fyp.yuki.vitrox2.Recipe3;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import fyp.yuki.vitrox2.databinding.RecipeDetail2Binding;
import fyp.yuki.vitrox2.databinding.RecipeDetail3Binding;


public class RecipeDetail3 extends AppCompatActivity {

    RecipeDetail3Binding binding;
    private Recipe3 object;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= RecipeDetail3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getBundles();
        setVariable();
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v->finish());

        int recipeDetailResourceID = getResources().getIdentifier(object.getPicPath(),"drawable", RecipeDetail3.this.getPackageName());
        binding.recipeDetailPic3.setImageResource(recipeDetailResourceID);

        binding.recipeName3.setText(object.getTitle());
        binding.recipeScore3.setText(object.getScore());
        binding.recipeDescriptionTxt3.setText(object.getDescription());
        binding.recipeDescription3.setText(object.getTitle());
        binding.IngredientsTxt3.setText(object.getIngredients());
        binding.proteinPortion3.setText(String.valueOf(object.getProteinPortion()) + "g"); // Convert to String
        binding.carboPortion3.setText(String.valueOf(object.getCarboPortion()) + "g"); // Convert to String
        binding.FatsPortion3.setText(String.valueOf(object.getFatsPortion()) + "g"); // Convert to String

    }


    private void getBundles() {
        object=(Recipe3) getIntent().getSerializableExtra("object");
    }
}
