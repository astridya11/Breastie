package fyp.yuki.vitrox2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter<Message> {

    private LayoutInflater inflater;
    private ArrayList<Message> messages;
    private final int OUTGOING_MESSAGE = 0;
    private final int INCOMING_MESSAGE = 1;

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        super(context, 0, messages);
        this.messages = messages;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.isOutgoing()) {
            return OUTGOING_MESSAGE;
        } else {
            return INCOMING_MESSAGE;
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int viewType = getItemViewType(position);

        if (convertView == null) {
            switch (viewType) {
                case OUTGOING_MESSAGE:
                    convertView = inflater.inflate(R.layout.message_item_outgoing, parent, false);
                    break;
                case INCOMING_MESSAGE:
                    convertView = inflater.inflate(R.layout.message_item_incoming, parent, false);
                    break;
            }
        }

        Message message = messages.get(position);

        TextView messageTextView;
        TextView nameTextView;
        TextView timestampTextView;
        ImageView profileImageView; // Add ImageView for profile image

        if (viewType == OUTGOING_MESSAGE) {
            messageTextView = convertView.findViewById(R.id.outgoing_text_message);
            nameTextView = convertView.findViewById(R.id.outgoing_text_name);
            timestampTextView = convertView.findViewById(R.id.outgoing_text_timestamp);
            profileImageView = convertView.findViewById(R.id.outgoing_image_profile); // Initialize profile image ImageView
        } else {
            messageTextView = convertView.findViewById(R.id.incoming_text_message);
            nameTextView = convertView.findViewById(R.id.incoming_text_name);
            timestampTextView = convertView.findViewById(R.id.incoming_text_timestamp);
            profileImageView = convertView.findViewById(R.id.incoming_image_profile); // Initialize profile image ImageView
        }

        // Update the views with message content
        messageTextView.setText(message.getContent());
        nameTextView.setText(message.getSenderName());
        timestampTextView.setText(message.getTimestamp());

        String senderId = message.getSenderID();
        Log.d("MessageAdapter", "senderID: " + senderId);
        if (senderId != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(senderId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String userProfile = snapshot.child("profile").getValue(String.class);
                        Picasso.get().load(userProfile).into(profileImageView);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle any errors
                }
            });
        }
        RelativeLayout messageContainer = convertView.findViewById(R.id.message_container);
        ViewGroup.LayoutParams layoutParams = messageContainer.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        messageContainer.setLayoutParams(layoutParams);

        // Return the updated convertView
        return convertView;
    }


}