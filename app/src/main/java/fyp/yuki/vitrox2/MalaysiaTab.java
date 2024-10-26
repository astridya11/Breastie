package fyp.yuki.vitrox2;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Map;


import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MalaysiaTab extends Fragment {
    private static final String malaysiaTag = MalaysiaTab.class.getSimpleName();

    private RecyclerView recyclerView;
    private ArrayList<RecyclerData> recyclerDataArrayList;
    private RecyclerViewAdapter adapter;
    private String groupType = "malaysia";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_hostel_tab, container, false);
        recyclerView = rootView.findViewById(R.id.hostelRecyclerView);

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // created new array list..
        recyclerDataArrayList=new ArrayList<>();

        // Set up Firestore listener to listen for real-time updates
        db.collection("community").document("group").collection("malaysia")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(malaysiaTag, "Listen failed.", error);
                            return;
                        }

                        recyclerDataArrayList.clear(); // Clear the existing data before adding updated data

                        // Iterate through the documents in the snapshot and add them to the list
                        for (QueryDocumentSnapshot document : snapshot) {
                            Log.d(malaysiaTag, document.getId() + " => " + document.getData());

                            // retrieve data that need to show on recycler view
                            String documentId = document.getId();
                            String groupName = document.getString("groupName");
                            String groupIconUrl = document.getString("groupIcon");
                            String groupDescription = document.getString("groupDescription");
                            String dateCreated = document.getString("dateCreated");
                            int groupMember = document.getLong("groupMember").intValue(); // Use getLong for int values

                            recyclerDataArrayList.add(new RecyclerData(documentId, groupName, groupIconUrl, groupDescription, dateCreated, groupMember));
                        }

                        // Notify the adapter that the data set has changed
                        adapter.notifyDataSetChanged();
                    }
                });

        // Initialize the adapter
        adapter = new RecyclerViewAdapter(recyclerDataArrayList, requireContext(), new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Map<String, Object> allData) {
                // Handle the click event, for example, start a new activity with the document ID
                Intent intent = new Intent(requireContext(), ChatDetails.class);
                intent.putExtra("documentId", (String) allData.get("id"));
                intent.putExtra("groupName", (String) allData.get("name"));
                intent.putExtra("groupIcon", (String) allData.get("imgURL"));
                intent.putExtra("groupDescription", (String) allData.get("grpDescription"));
                intent.putExtra("dateCreated", (String) allData.get("date"));
                intent.putExtra("groupMember", (int) allData.get("member"));
                intent.putExtra("groupType", groupType);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        // setting grid layout manager to implement grid view.
        // in this method '4' represents number of columns to be displayed in grid view.
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(),4);

        // at last set adapter to recycler view.
        recyclerView.setLayoutManager(layoutManager);

        return rootView;
    }
}