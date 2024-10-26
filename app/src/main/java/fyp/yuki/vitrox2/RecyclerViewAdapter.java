package fyp.yuki.vitrox2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>{

    private ArrayList<RecyclerData> courseDataArrayList;
    private Context mcontext;

    // interface for click listener
    public interface OnItemClickListener {
        void onItemClick(Map<String, Object> allData);
    }

    // member variable for the click listener
    private OnItemClickListener clickListener;

    public RecyclerViewAdapter(ArrayList<RecyclerData> recyclerDataArrayList, Context mcontext, OnItemClickListener listener) {
        this.courseDataArrayList = recyclerDataArrayList;
        this.mcontext = mcontext;
        clickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        // Set the data to textview and imageview.
        RecyclerData recyclerData = courseDataArrayList.get(position);
        holder.groupName.setText(recyclerData.getName());
        // Load image from URL using Picasso
        Picasso.get().load(recyclerData.getImgURL())
                .transform(new CircleTransformation())
                .into(holder.groupAvatar);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onItemClick(recyclerData.getAllData());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        // this method returns the size of recyclerview
        return courseDataArrayList.size();
    }

    // View Holder Class to handle Recycler View.
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView groupName;
        private ImageView groupAvatar;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.inputGroupName);
            groupAvatar = itemView.findViewById(R.id.groupAvatar);
        }
    }
}