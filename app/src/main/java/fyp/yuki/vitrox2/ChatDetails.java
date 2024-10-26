package fyp.yuki.vitrox2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatDetails extends AppCompatActivity {
    private static final String chatDetailsTag = ChatDetails.class.getSimpleName();
    private ImageView icon;
    private TextView name;
    private TextView description;
    private TextView date;
    private TextView member;
    String gID;
    String gIcon;
    int gMember;
    String gType;

    FirebaseFirestore dbUser;
    FirebaseFirestore dbGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);

        dbUser = FirebaseFirestore.getInstance();
        dbGroup = FirebaseFirestore.getInstance();

        icon = findViewById(R.id.groupIcon);
        name = findViewById(R.id.inputGroupName);
        description = findViewById(R.id.groupDescription);
        date = findViewById(R.id.date);
        member = findViewById(R.id.member);

        // Create a SimpleDateFormat object to format the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        gID = this.getIntent().getStringExtra("documentId");
        String gName = this.getIntent().getStringExtra("groupName");
        gIcon = this.getIntent().getStringExtra("groupIcon");
        String gDescription = this.getIntent().getStringExtra("groupDescription");
        String gDate = this.getIntent().getStringExtra("dateCreated");
        gMember = this.getIntent().getIntExtra("groupMember", 0);
        gType = this.getIntent().getStringExtra("groupType");

        name.setText(gName);
        description.setText(gDescription);
        date.setText(gDate);
        // Load image from URL using Picasso
        Picasso.get().load(gIcon).into(icon);

        // update member count
        dbUser.collection("Registered Users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(chatDetailsTag, "Listen failed.", error);
                            return;
                        }

                        // Iterate through all users in the snapshot
                        for (QueryDocumentSnapshot document : snapshot) {
                            Log.d(chatDetailsTag, document.getId() + " => " + document.getData());

                            // Reference to the "groupList" collection inside the current document
                            CollectionReference groupListRef = document.getReference().collection("groupList");

                            // Check if the "groupList" collection exists
                            groupListRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task)
                                {
                                    if (task.isSuccessful()) {
                                        int detectedNum = 0; // Clear the existing data before adding updated data

                                        QuerySnapshot groupListSnapshot = task.getResult();
                                        if (groupListSnapshot != null && !groupListSnapshot.isEmpty()) {
                                            // "groupList" collection exists, iterate through its documents
                                            for (DocumentSnapshot groupDoc : groupListSnapshot.getDocuments()) {
                                                Log.d(chatDetailsTag, "GroupList Document ID: " + groupDoc.getId() + " => " + groupDoc.getData());

                                                String documentId = groupDoc.getId();
                                                if (gID.equals(documentId)) {
                                                    detectedNum++;
                                                    break; // No need to continue checking once the group is found for this user
                                                }
                                            }
                                            // only update value if it is different
                                            if (detectedNum != gMember)
                                            {
                                                gMember = detectedNum;
                                            }
                                        }
                                    }
                                    else {
                                        Log.w(chatDetailsTag, "Error getting groupList documents", task.getException());
                                    }
                                }
                            });
                        }

                        //  update group member into firebase cloud store
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("groupMember", gMember);
                        dbGroup.collection("community").document("group").collection(gType).document(gID)
                                .update(updates)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(chatDetailsTag, "groupMember successfully updated!");
                                        member.setText(String.valueOf(gMember));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(chatDetailsTag, "Error updating groupMember", e);
                                    }
                                });
                    }
                });
    }

    public void zoomImage(View view)
    {
        Intent i = new Intent(ChatDetails.this, FullScreenImage.class);
        i.putExtra("imageURL", gIcon);
        startActivity(i);
    }

    public void joinGroup(View view)
    {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            DocumentReference userDocRef = dbUser.collection("Registered Users").document(userId);
            CollectionReference groupListRef = userDocRef.collection("groupList");

            dbUser.collection("Registered Users").document(userId).collection("groupList")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            {
                                if (task.isSuccessful()) {
                                    List<String> groupIds = new ArrayList<>();
                                    for (DocumentSnapshot groupDoc : task.getResult()) {
                                        String documentId = groupDoc.getId();
                                        groupIds.add(documentId);
                                    }

                                    // check if user already joined this group
                                    if (groupIds.contains(gID)) {
                                        Toast.makeText(ChatDetails.this, "You already joined this group!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Map<String, Object> newGroup = new HashMap<>();
                                        groupListRef.document(gID).set(newGroup)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(chatDetailsTag, "Successfully insert new group in user data!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(chatDetailsTag, "Error inserting new group in user data.", e);
                                                    }
                                                });
                                        Toast.makeText(ChatDetails.this, "Successfully joined!", Toast.LENGTH_SHORT).show();
                                    }

                                    //  update group member into firebase cloud store
                                    gMember++;
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("groupMember", gMember);
                                    dbGroup.collection("community").document("group").collection(gType).document(gID)
                                            .update(updates)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(chatDetailsTag, "groupMember successfully updated!");
                                                    member.setText(String.valueOf(gMember));
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(chatDetailsTag, "Error updating groupMember", e);
                                                }
                                            });
                                } else {
                                    Log.w(chatDetailsTag, "Error joining group", task.getException());
                                }
                            }
                        }
                    });

            Intent toChat = new Intent(ChatDetails.this, GroupListPage.class);
            startActivity(toChat);
        }
        else
        {
            Log.d(chatDetailsTag, "Current user is null");
            Toast.makeText(this, "Please sign in to join group chat.", Toast.LENGTH_SHORT).show();

            Intent toWelcome = new Intent(ChatDetails.this, LogOrSignActivity.class);
            startActivity(toWelcome);
        }
    }

    public void goBack(View view)
    {
        onBackPressed();
    }
}