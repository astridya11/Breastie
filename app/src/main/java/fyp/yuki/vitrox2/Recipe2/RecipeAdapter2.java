package fyp.yuki.vitrox2.Recipe2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fyp.yuki.vitrox2.databinding.RecipeItem2Binding;


public class RecipeAdapter2 extends RecyclerView.Adapter<RecipeAdapter2.Viewholder> {

    private ArrayList<Recipe2> recipe_items2;
    Context context;
    RecipeItem2Binding binding;

    public RecipeAdapter2(ArrayList<Recipe2> recipe_items2) {

        this.context = context;
        this.recipe_items2 = recipe_items2;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RecipeItem2Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        context = parent.getContext();
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        binding.recipeName2.setText(recipe_items2.get(position).getTitle());
        binding.recipeCaloriesTxt2.setText(recipe_items2.get(position).getCaloriesTxt());
        binding.recipeScore2.setText(recipe_items2.get(position).getScore());

        int drawableResourceId = holder.itemView.getResources()
                .getIdentifier(recipe_items2.get(position).getPicPath(), "drawable", holder.itemView.getContext().getPackageName());

        binding.recipePic2.setImageResource(drawableResourceId);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetail2.class);
            intent.putExtra("object", recipe_items2.get(position));
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return recipe_items2.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        public Viewholder(RecipeItem2Binding binding) {

            super(binding.getRoot());
        }
    }
}
