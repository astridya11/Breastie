package fyp.yuki.vitrox2.Recipe3;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fyp.yuki.vitrox2.databinding.RecipeItem3Binding;


public class RecipeAdapter3 extends RecyclerView.Adapter<RecipeAdapter3.Viewholder> {

    private ArrayList<Recipe3> recipe_items3;
    Context context;
    RecipeItem3Binding binding;

    public RecipeAdapter3(ArrayList<Recipe3> recipe_items3) {

        this.context = context;
        this.recipe_items3 = recipe_items3;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RecipeItem3Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        context = parent.getContext();
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        binding.recipeName3.setText(recipe_items3.get(position).getTitle());
        binding.recipeCaloriesTxt3.setText(recipe_items3.get(position).getCaloriesTxt());
        binding.recipeScore3.setText(recipe_items3.get(position).getScore());

        int drawableResourceId = holder.itemView.getResources()
                .getIdentifier(recipe_items3.get(position).getPicPath(), "drawable", holder.itemView.getContext().getPackageName());

        binding.recipePic3.setImageResource(drawableResourceId);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetail3.class);
            intent.putExtra("object", recipe_items3.get(position));
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return recipe_items3.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        public Viewholder(RecipeItem3Binding binding) {

            super(binding.getRoot());
        }
    }
}
