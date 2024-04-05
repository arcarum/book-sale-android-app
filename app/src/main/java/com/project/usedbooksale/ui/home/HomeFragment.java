package com.project.usedbooksale.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.usedbooksale.ItemActivity;
import com.project.usedbooksale.R;
import com.project.usedbooksale.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener {

    private FragmentHomeBinding binding;
    private FirebaseFirestore mDatabase;
    private ListView listView;
    private final HashMap<Integer, Map<String, Object>> map = new HashMap<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = binding.listViewHome;
        listView.setOnItemClickListener(this);
        mDatabase = FirebaseFirestore.getInstance();

        updateDisplay();

        return root;
    }

    private void updateDisplay() {

        mDatabase.collection("books_on_sale").get().addOnSuccessListener(queryDocumentSnapshots -> {
            int i = 0;
            for (QueryDocumentSnapshot querySnapshot : queryDocumentSnapshots) {
                map.put(i, querySnapshot.getData());
                i++;
            }

            // create a List of Map<String, ?> objects
            ArrayList<HashMap<String, String>> data = new ArrayList<>();
            for (Integer key : map.keySet()) {
                HashMap<String, String> bookInfoMap = new HashMap<>();
                bookInfoMap.put("title", (String) map.get(key).get("Title"));
                bookInfoMap.put("date", convertTimestamp((Timestamp) map.get(key).get("Date")));
                bookInfoMap.put("price", map.get(key).get("Price") + " AED");
                data.add(bookInfoMap);
            }

            // create the resource, from, and to variables
            int resource = R.layout.listview_item;
            String[] from = {"date", "title", "price"};
            int[] to = { R.id.textViewDate, R.id.textViewTitle, R.id.textViewPrice};

            // create and set the adapter
            SimpleAdapter adapter = new SimpleAdapter(getContext(), data, resource, from, to);
            listView.setAdapter(adapter);
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        Intent intent = new Intent(getContext(), ItemActivity.class);

        intent.putExtra("date", convertTimestamp( (Timestamp) map.get(position).get("Date")));
        intent.putExtra("title", (String) map.get(position).get("Title"));
        //intent.putExtra("description", Objects.requireNonNull(map.get(position)).get("Title").toString());

        this.startActivity(intent);
    }

    private String convertTimestamp(Timestamp timestamp) {
        // Converting date from https://stackoverflow.com/questions/18929929/convert-timestamp-into-current-date-in-android#18930056
        return (String) DateFormat.format("MMMM dd, yyyy", timestamp.toDate());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}