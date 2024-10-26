package fyp.yuki.vitrox2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Nullable;

import fyp.yuki.vitrox2.Intro.IntroPage;

public class Community extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String communityTag = Community.class.getSimpleName();
    TabLayout tabLayout;
    ViewPager2 viewPager;
    ViewPagerAdapter viewPagerAdapter;
    Spinner spinner;
    ImageButton fileButton;
    TextInputEditText groupNameInput;
    TextInputEditText groupDescriptionInput;
    Button submitButton;
    Button cancelButton;

    String groupType;
    String groupName;
    String groupDescription;
    Uri imageUri;
    Uri downloadUrl;
    FirebaseFirestore db;
    StorageReference storageRef;
    UploadTask uploadTask;
    private static final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // respond when user click on tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // respond when tab content change
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

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

        // chat
        grpchatBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GroupListPage.class);
                startActivity(intent);
                finish();
            }
        });

//        // community
//        communityBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), Community.class);
//                startActivity(intent);
//                finish();
//            }
//        });

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

    public void addGroup(View view)
    {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_group_layout);
        dialog.show();

        spinner = (Spinner) dialog.findViewById(R.id.groupTypeSpinner);
        fileButton = (ImageButton) dialog.findViewById(R.id.fileButton);
        groupNameInput = (TextInputEditText) dialog.findViewById(R.id.inputGroupName);
        groupDescriptionInput = (TextInputEditText) dialog.findViewById(R.id.inputDescription);
        submitButton = (Button) dialog.findViewById(R.id.submit);
        cancelButton = (Button) dialog.findViewById(R.id.cancel);

        // clear previous input
        groupType = "malaysia";
        groupName = "";
        groupDescription = "";
        imageUri = null;
        downloadUrl = null;

        spinner.setSelection(-1);
        spinner.setOnItemSelectedListener(this);

        // set up spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.group_type_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // set up file button
        fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        // set up submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Get the today's date
                Date currentDate = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = sdf.format(currentDate);

                groupName = groupNameInput.getText().toString();
                groupDescription = groupDescriptionInput.getText().toString();

                Map<String, Object> groupInfo = new HashMap<>();

                // upload group information into firebase
                groupName = groupNameInput.getText().toString();
                groupDescription = groupDescriptionInput.getText().toString();

                if (groupName.isEmpty())
                {
                    Toast.makeText(Community.this, "Please input group name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    // Description and icon are both provided
                    if (!groupDescription.isEmpty() && downloadUrl != null) {
                        groupInfo.put("groupName", groupName);
                        groupInfo.put("groupDescription", groupDescription);
                        groupInfo.put("dateCreated", formattedDate);
                        groupInfo.put("groupMember", 0);
                        groupInfo.put("groupIcon", downloadUrl);
                    }
                    // Description is provided but icon is not
                    else if (!groupDescription.isEmpty()) {
                        groupInfo.put("groupName", groupName);
                        groupInfo.put("groupDescription", groupDescription);
                        groupInfo.put("dateCreated", formattedDate);
                        groupInfo.put("groupMember", 0);
                        groupInfo.put("groupIcon", getString(R.string.emptyGroupIcon));
                    }
                    // Icon is provided but description is not
                    else if (downloadUrl != null) {
                        groupInfo.put("groupName", groupName);
                        groupInfo.put("groupDescription", "");
                        groupInfo.put("dateCreated", formattedDate);
                        groupInfo.put("groupMember", 0);
                        groupInfo.put("groupIcon", downloadUrl);
                    }
                    // Neither description nor icon is provided
                    else {
                        groupInfo.put("groupName", groupName);
                        groupInfo.put("groupDescription", "");
                        groupInfo.put("dateCreated", formattedDate);
                        groupInfo.put("groupMember", 0);
                        groupInfo.put("groupIcon", getString(R.string.emptyGroupIcon));
                    }
                }

                db.collection("community").document("group").collection(groupType)
                        .add(groupInfo)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(communityTag, "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(communityTag, "Error adding document", e);
                            }
                        });

                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0)
        {
            groupType = "malaysia";
        }
        else if (position == 1)
        {
            groupType = "unitedstates";
        }
        else if (position == 2)
        {
            groupType = "japan";
        }
        else if (position == 3)
        {
            groupType = "china";
        }
        else
        {
            groupType = "singapore";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImgToFirebase(imageUri);
        }
    }

    public void uploadImgToFirebase(Uri imgUri)
    {
        // upload group icon into firebase storage
        // groupPicRef is the path in the firebase that image will be stored
        StorageReference groupPicRef = storageRef.child("groupPic/"+ imgUri.getLastPathSegment());
        uploadTask = groupPicRef.putFile(imgUri);
        Toast.makeText(this, "Please wait while we uploading your photo.", Toast.LENGTH_LONG).show();
        submitButton.setClickable(false);

        // detect if upload success
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(Community.this, "Group Icon upload failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Community.this, "Group Icon upload success!", Toast.LENGTH_SHORT).show();
            }
        });

        // to get the download url
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return groupPicRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    downloadUrl = task.getResult();
                    Picasso.get().load(downloadUrl).into(fileButton);
                    submitButton.setClickable(true);
                } else {
                    // do nothing first
                }
            }
        });
    }

    public void goBack(View view)
    {
        onBackPressed();
    }
}