package fyp.yuki.vitrox2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import android.content.Context;
public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {
    private List<Group> groups;
    private Context context;

    public GroupListAdapter(List<Group> groups, Context context) {
        this.groups = groups;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_adapter_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.bind(group);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("groupId", group.getGroupId());
                intent.putExtra("groupName", group.getGroupName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView groupNameTextView;
        private ImageView groupIconImageView;
        private TextView groupDescriptionTextView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupIconImageView = itemView.findViewById(R.id.groupProfilePic);
            groupNameTextView = itemView.findViewById(R.id.groupName);
            groupDescriptionTextView = itemView.findViewById(R.id.groupDescription);
        }

        public void bind(Group group) {
            groupNameTextView.setText(group.getGroupName());
            groupDescriptionTextView.setText(group.getGroupDescription());
            Glide.with(itemView.getContext())
                    .load(group.getGroupIcon())
                    .placeholder(R.drawable.group_icon)
                    .error(R.drawable.group_icon)
                    .into(groupIconImageView);
        }
    }
}
