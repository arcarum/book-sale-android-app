package com.project.usedbooksale.ui.home;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.usedbooksale.ItemActivity;
import com.project.usedbooksale.R;
import com.project.usedbooksale.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener {

    private FragmentHomeBinding binding;
    private FirebaseFirestore database;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<HashMap<String, String>> data;
    private ExecutorService executor;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = binding.listViewHome;
        listView.setOnItemClickListener(this);
        database = FirebaseFirestore.getInstance();

        // set separator color from
        // https://stackoverflow.com/questions/2372415/how-to-change-color-of-android-listview-separator-line
        int[] colors = {0, 0xFF000000, 0};
        listView.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
        listView.setDividerHeight(4);

        executor = Executors.newCachedThreadPool();
        executor.execute(this::updateDisplay);

        swipeRefreshLayout = binding.swipeRefreshLayout;

        // from https://developer.android.com/develop/ui/views/touch-and-input/swipe/respond-refresh-request#java
        swipeRefreshLayout.setOnRefreshListener(() -> {
            executor.execute(this::updateDisplay);
            Toast.makeText(getContext(), "New List Fetched", Toast.LENGTH_SHORT).show();
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        executor.execute(this::updateDisplay);
    }

    private void updateDisplay()
    {
        HashMap<Integer, Map<String, Object>> map = new HashMap<>();
        (database.collection("books_on_sale").orderBy("Date", Query.Direction.DESCENDING)).get().addOnSuccessListener(queryDocumentSnapshots -> {
            int i = 0;
            for (QueryDocumentSnapshot querySnapshot : queryDocumentSnapshots) {
                map.put(i, querySnapshot.getData());
                i++;
            }

            data = new ArrayList<>();
            for (Integer key : map.keySet()) {
                HashMap<String, String> bookInfoMap = new HashMap<>();
                bookInfoMap.put("title", (String) map.get(key).get("Title"));
                bookInfoMap.put("date", convertTimestamp(Long.parseLong(String.valueOf(map.get(key).get("Date")))));
                bookInfoMap.put("timeInMilliSec", String.valueOf(map.get(key).get("Date")));
                bookInfoMap.put("price", map.get(key).get("Price") + " AED");
                bookInfoMap.put("desc", (String) map.get(key).get("Description"));
                bookInfoMap.put("email", (String) map.get(key).get("Email"));
                bookInfoMap.put("name", (String) map.get(key).get("Name"));
                data.add(bookInfoMap);
            }

            // create the resource, from, and to variables
            int resource = R.layout.listview_item;
            String[] from = {"date", "title", "price"};
            int[] to = { R.id.textViewDate, R.id.textViewTitle, R.id.textViewPrice};

            // create and set the adapter
            SimpleAdapter adapter = new SimpleAdapter(getContext(), data, resource, from, to);
            listView.setAdapter(adapter);
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        Intent intent = new Intent(getContext(), ItemActivity.class);

        intent.putExtra("date", data.get(position).get("date"));
        intent.putExtra("title", data.get(position).get("title"));
        intent.putExtra("price", data.get(position).get("price"));
        intent.putExtra("desc", data.get(position).get("desc"));
        intent.putExtra("name", data.get(position).get("name"));
        intent.putExtra("email", data.get(position).get("email"));
        intent.putExtra("timeInMilliSec", data.get(position).get("timeInMilliSec"));

        startActivity(intent);
    }


    private String convertTimestamp(long timestamp) {
        // Converting date from
        // https://stackoverflow.com/questions/18929929/convert-timestamp-into-current-date-in-android#18930056
        return (String) DateFormat.format("MMMM dd, yyyy HH:mm:ss", timestamp);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}