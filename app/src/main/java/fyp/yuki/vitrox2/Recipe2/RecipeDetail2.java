package fyp.yuki.vitrox2.Recipe2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import fyp.yuki.vitrox2.databinding.ReceipeDetailBinding;
import fyp.yuki.vitrox2.databinding.RecipeDetail2Binding;


public class RecipeDetail2 extends AppCompatActivity {

    RecipeDetail2Binding binding;
    private Recipe2 object;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= RecipeDetail2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getBundles();
        setVariable();
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v->finish());

        int recipeDetailResourceID = getResources().getIdentifier(object.getPicPath(),"drawable", RecipeDetail2.this.getPackageName());
        binding.recipeDetailPic2.setImageResource(recipeDetailResourceID);

        binding.recipeName2.setText(object.getTitle());
        binding.recipeScore2.setText(object.getScore());
        binding.recipeDescriptionTxt2.setText(object.getDescription());
        binding.recipeDescription2.setText(object.getTitle());
        binding.IngredientsTxt2.setText(object.getIngredients());
        binding.proteinPortion2.setText(String.valueOf(object.getProteinPortion()) + "g"); // Convert to String
        binding.carboPortion2.setText(String.valueOf(object.getCarboPortion()) + "g"); // Convert to String
        binding.FatsPortion2.setText(String.valueOf(object.getFatsPortion()) + "g"); // Convert to String

    }


    private void getBundles() {
        object=(Recipe2) getIntent().getSerializableExtra("object");
    }
}
