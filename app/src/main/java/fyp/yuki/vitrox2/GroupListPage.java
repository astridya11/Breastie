package fyp.yuki.vitrox2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupListPage extends AppCompatActivity {
    private static final String groupListTag = GroupListPage.class.getSimpleName();

    private RecyclerView recyclerView;
    private GroupListAdapter adapter;
    private List<Group> groups = new ArrayList<>();
    private ImageView userProfilePic;
    private TextView noGroupsMessage;

    FirebaseFirestore dbUser;

    private static final int DELAY_MILLISECONDS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_list_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.groupMainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbUser = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.groupMessagesRecyclerView);
        noGroupsMessage = findViewById(R.id.noGroupsMessage);

        // Set a layout manager for the RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        userProfilePic = findViewById(R.id.userProfilePic);

        LinearLayout homeBt = findViewById(R.id.home);
        LinearLayout bmiBt = findViewById(R.id.bmi_icon);
        LinearLayout ARBt = findViewById(R.id.AR);
        LinearLayout grpchatBt = findViewById(R.id.chatBot);
        LinearLayout userBt = findViewById(R.id.user_icon);
        LinearLayout communityBt = findViewById(R.id.community);

        // user profile
        userBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                startActivity(intent);
                finish();
            }
        });

//        // chat
//        grpchatBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), GroupListPage.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        // community
        communityBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Community.class);
                startActivity(intent);
                finish();
            }
        });

        // main activity
        homeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // bmi calculator
        bmiBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), bmi.class);
                startActivity(intent);
                finish();
            }
        });

        // AR self examination
        ARBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ARSelfCheckActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchGroupDetails();
            }
        }, DELAY_MILLISECONDS);
    }

    private void fetchGroupDetails() {
        groups.clear();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        fetchUserProfilePic();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            dbUser.collection("Registered Users").document(userId).collection("groupList")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error)
                        {
                            if (error != null) {
                                Log.w(groupListTag, "Listen failed.", error);
                                return;
                            }

                            // "groupList" collection exists, iterate through its documents
                            if (snapshot != null && !snapshot.isEmpty())
                            {
                                List<String> groupIds = new ArrayList<>();
                                for (DocumentSnapshot groupDoc : snapshot.getDocuments()) {
                                    String documentId = groupDoc.getId();
                                    groupIds.add(documentId);
                                }

                                fetchGroupDetailsFromFirestore(groupIds);
                            }
                            else
                            {
                                noGroupsMessage.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Current user is null.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchGroupDetailsFromFirestore(List<String> groupIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Context context = this;

        for (String groupId : groupIds) {
            // Iterate over each collection
            for (String collectionName : Arrays.asList("malaysia", "unitedstates", "japan", "china", "singapore")) {
                db.collection("community").document("group").collection(collectionName).document(groupId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    Group group = documentSnapshot.toObject(Group.class);
                                    group.setGroupId(groupId);
                                    groups.add(group);
                                    if (groups.size() == groupIds.size()) {
                                        adapter = new GroupListAdapter(groups, context);
                                        recyclerView.setAdapter(adapter);
                                    }
                                } else {
                                    // Handle case where the group document does not exist
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle error
                            }
                        });
            }
        }
    }

    private void fetchUserProfilePic() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            dbUser.collection("Registered Users").document(userId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task)
                        {
                            if (task.isSuccessful())
                            {
                                DocumentSnapshot document = task.getResult();

                                if (document.exists())
                                {
                                    // profile key exists
                                    if (document.contains("profile")) {
                                        String profilePic = document.getString("profile");
                                        Picasso.get().load(profilePic).into(userProfilePic);
                                    }
//                                    // profile key does not exists
//                                    else
//                                    {
//                                        String noPicUrl = getString(R.string.noProfilePic);
//                                        Picasso.get().load(noPicUrl).into(userProfilePic);
//                                    }
                                }
                                else
                                {
                                    Log.w(groupListTag, "Error fetching user document", task.getException());
                                }
                            }
                            else
                            {
                                Log.w(groupListTag, "Error fetching user profile", task.getException());
                            }
                        }
                    });
        }
    }
}
