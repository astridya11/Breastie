package fyp.yuki.vitrox2.Recipe;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fyp.yuki.vitrox2.databinding.ReceipeItemBinding;


public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.Viewholder> {

    private ArrayList<Recipe> recipe_items;
    Context context;
    ReceipeItemBinding binding;

    public RecipeAdapter(ArrayList<Recipe> recipe_items) {

        this.context = context;
        this.recipe_items = recipe_items;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ReceipeItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        context = parent.getContext();
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        binding.recipeName.setText(recipe_items.get(position).getTitle());
        binding.recipeCaloriesTxt.setText(recipe_items.get(position).getCaloriesTxt());
        binding.recipeScore.setText(recipe_items.get(position).getScore());

        int drawableResourceId = holder.itemView.getResources()
                .getIdentifier(recipe_items.get(position).getPicPath(), "drawable", holder.itemView.getContext().getPackageName());

        binding.recipePic.setImageResource(drawableResourceId);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetail.class);
            intent.putExtra("object", recipe_items.get(position));
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return recipe_items.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        public Viewholder(ReceipeItemBinding binding) {

            super(binding.getRoot());
        }
    }
}
