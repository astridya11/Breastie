package fyp.yuki.vitrox2.Recipe;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import fyp.yuki.vitrox2.databinding.ReceipeDetailBinding;


public class RecipeDetail extends AppCompatActivity {

    ReceipeDetailBinding binding;
    private Recipe object;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ReceipeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getBundles();
        setVariable();
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v->finish());

        int recipeDetailResourceID = getResources().getIdentifier(object.getPicPath(),"drawable",RecipeDetail.this.getPackageName());
        binding.recipeDetailPic.setImageResource(recipeDetailResourceID);

        binding.recipeName.setText(object.getTitle());
        binding.recipeScore.setText(object.getScore());
        binding.recipeDescriptionTxt.setText(object.getDescription());
        binding.recipeDescription.setText(object.getTitle());
        binding.IngredientsTxt.setText(object.getIngredients());
        binding.proteinPortion.setText(String.valueOf(object.getProteinPortion()) + "g"); // Convert to String
        binding.carboPortion.setText(String.valueOf(object.getCarboPortion()) + "g"); // Convert to String
        binding.FatsPortion.setText(String.valueOf(object.getFatsPortion()) + "g"); // Convert to String

    }


    private void getBundles() {
        object=(Recipe) getIntent().getSerializableExtra("object");
    }
}
