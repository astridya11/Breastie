package fyp.yuki.vitrox2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private static final String chatActivityTag = ChatActivity.class.getSimpleName();

    ListView lv;
    ImageView send, returnButton, exitButton;
    EditText ed;
    MessageAdapter adapter;

    private String groupId;
    private String groupName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_msg);

        groupId = getIntent().getStringExtra("groupId");
        groupName = getIntent().getStringExtra("groupName");

        send = findViewById(R.id.send);
        returnButton = findViewById(R.id.returnButton);
        exitButton = findViewById(R.id.exitButton);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        ArrayList<String> al = new ArrayList<>();
        lv = findViewById(R.id.lv);
        lv.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL); // Enable transcript mode
        lv.setStackFromBottom(false);
        ed = findViewById(R.id.edmsg);

        adapter = new MessageAdapter(this, new ArrayList<Message>());
        lv.setAdapter(adapter);


        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Build confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Confirm Exit");
                builder.setMessage("Are you sure you want to exit this group?");
                builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exitGroup();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked Cancel, dismiss the dialog
                        dialog.dismiss();
                    }
                });
                // Show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        if (groupId != null) { // Add null check for groupId
            List<String> collectionNames = Arrays.asList("malaysia", "unitedstates", "japan", "china", "singapore");
            for (String collectionName : collectionNames) {
                FirebaseFirestore.getInstance().collection("community")
                        .document("group")
                        .collection(collectionName)
                        .document(groupId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    // Retrieve group data
                                    String groupName = documentSnapshot.getString("groupName");
                                    String groupIconUrl = documentSnapshot.getString("groupIcon");

                                    // Set group name and profile pic
                                    setGroupInfo(groupName, groupIconUrl);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure
                                Log.e("ChatActivity", "Error fetching group information", e);
                            }
                        });
            }

            // Reference messages for this group
            DatabaseReference messagesRef = db.getReference("Messages").child(groupId);
            Log.d("ChatActivity", "messages: " + messagesRef);

            messagesRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    String messageContent = snapshot.child("content").getValue(String.class);
                    String senderId = snapshot.child("senderID").getValue(String.class);
                    String senderName = snapshot.child("senderName").getValue(String.class);
                    String timestamp = snapshot.child("timestamp").getValue(String.class);

                    if (senderId != null && FirebaseAuth.getInstance().getCurrentUser() != null) {
                        boolean isOutgoing = senderId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        // Create a Message object
                        Message message = new Message(messageContent, senderId, senderName, timestamp, isOutgoing);

                        // Add the message to the adapter
                        adapter.add(message);

                        lv.smoothScrollToPosition(adapter.getCount() - 1);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

                // Implement other methods of ChildEventListener

            });

            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = auth.getCurrentUser();
                    Date currentDate = Calendar.getInstance().getTime();

                    if (currentUser != null) {
                        String userId = currentUser.getUid();
                        String userName = currentUser.getDisplayName();
                        String messageContent = ed.getText().toString();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd HH:mm:ss", Locale.ENGLISH);
                        String timestamp = dateFormat.format(currentDate);

                        if (!messageContent.isEmpty()) {
                            // Push message to Firebase with unique key
                            DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("Messages").child(groupId);
                            String messageId = messagesRef.push().getKey();
                            Message message = new Message(messageContent, userId, userName, timestamp, true);
                            if (messageId != null) {
                                messagesRef.child(messageId).setValue(message);
                            }

                            ed.setText("");
                        }
                    }
                }
            });
        } else {
            // Handle case where groupId is null
            Toast.makeText(this, "Group ID is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void setGroupInfo(String groupName, String groupPicUrl) {
        // Set group name
        TextView groupNameTextView = findViewById(R.id.name);
        groupNameTextView.setText(groupName);

        // Set profile pic using Picasso or Glide library
        CircleImageView profilePicImageView = findViewById(R.id.profilePic);

        Picasso.get().load(groupPicUrl).into(profilePicImageView);
    }

    private void exitGroup() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            DocumentReference userDocRef = FirebaseFirestore.getInstance().collection("Registered Users").document(userId);
            userDocRef.collection("groupList")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error)
                        {
                            if (error != null) {
                                Log.w(chatActivityTag, "Listen failed.", error);
                                return;
                            }

                            assert snapshot != null;
                            for (DocumentSnapshot groupDoc : snapshot.getDocuments()) {
                                String documentId = groupDoc.getId();
                                // match group id, if matched, remove this group from user info
                                if (documentId.equals(groupId))
                                {
                                    userDocRef.collection("groupList").document(groupId).delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Successfully removed group from user's groupList
                                                    // Now decrement groupMember count for each group
                                                    List<String> collectionNames = Arrays.asList("malaysia", "unitedstates", "japan", "china", "singapore");
                                                    for (String collectionName : collectionNames) {
                                                        DocumentReference groupRef = FirebaseFirestore.getInstance()
                                                                .collection("community")
                                                                .document("group")
                                                                .collection(collectionName)
                                                                .document(groupId);

                                                        groupRef.update("groupMember", FieldValue.increment(-1))
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        // Notify the user that they have exited the group
                                                                        Toast.makeText(ChatActivity.this, "You have exited the group", Toast.LENGTH_SHORT).show();

                                                                        // Navigate to GroupListPage
                                                                        Intent intent = new Intent(ChatActivity.this, GroupListPage.class);
                                                                        startActivity(intent);
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.e(chatActivityTag, "Failed to update groupMember count", e);
                                                                    }
                                                                });
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e(chatActivityTag, "Failed to remove groupID from user's groupList", e);
                                                    Toast.makeText(ChatActivity.this, "Failed to exit the group", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    // no need to go through other document after deletion
                                    break;
                                }
                            }
                        }
                    });











            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                    .child("Registered Users").child(userId);

            userRef.child("groupList").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String groupIDInList = snapshot.getValue(String.class);
                        if (groupIDInList.equals(groupId)) {
                            snapshot.getRef().removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Successfully removed group from user's groupList
                                            // Now decrement groupMember count for each group

                                            List<String> collectionNames = Arrays.asList("malaysia", "unitedstates", "japan", "china", "singapore");
                                            for (String collectionName : collectionNames) {
                                                DocumentReference groupRef = FirebaseFirestore.getInstance()
                                                        .collection("community")
                                                        .document("group")
                                                        .collection(collectionName)
                                                        .document(groupId);

                                                groupRef.update("groupMember", FieldValue.increment(-1))
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                // Notify the user that they have exited the group
                                                                Toast.makeText(ChatActivity.this, "You have exited the group", Toast.LENGTH_SHORT).show();

                                                                // Navigate to GroupListPage
                                                                Intent intent = new Intent(ChatActivity.this, GroupListPage.class);
                                                                startActivity(intent);
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
//                                                                Log.e("Firestore", "Failed to update groupMember count", e);
//                                                                Toast.makeText(ChatActivity.this, "Failed to exit the group", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle Realtime Database removal failure
                                            Log.e("Realtime Database", "Failed to remove groupID from user's groupList", e);
                                            Toast.makeText(ChatActivity.this, "Failed to exit the group", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle onCancelled event
                    Log.e("Realtime Database", "onCancelled: " + databaseError.getMessage());
                    Toast.makeText(ChatActivity.this, "Failed to exit the group", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
