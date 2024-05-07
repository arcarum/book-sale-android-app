package com.project.usedbooksale.ui.myListings;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.usedbooksale.BookDetailsActivity;
import com.project.usedbooksale.R;
import com.project.usedbooksale.databinding.FragmentMyListingsBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyListingsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private FragmentMyListingsBinding binding;
    private FirebaseFirestore database;
    private ListView listView;
    private View searchView;
    private TextView noBooksTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<HashMap<String, String>> data;
    private ArrayList<HashMap<String, String>> filteredData;
    private ExecutorService executor;
    private String userEmail;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMyListingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = binding.listViewHome;
        listView.setOnItemClickListener(this);
        database = FirebaseFirestore.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        } else {
            userEmail = "";
        }

        noBooksTextView = binding.noBooksMyListingsTextView;

        // set separator color from
        // https://stackoverflow.com/questions/2372415/how-to-change-color-of-android-listview-separator-line
        int[] colors = {0, 0xFF000000, 0};
        listView.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
        listView.setDividerHeight(4);

        executor = Executors.newCachedThreadPool();
        executor.execute(this::updateDisplay);

        swipeRefreshLayout = binding.swipeRefreshLayoutMyListings;

        // from https://developer.android.com/develop/ui/views/touch-and-input/swipe/respond-refresh-request#java
        swipeRefreshLayout.setOnRefreshListener(() -> {
            executor.execute(this::updateDisplay);
            Toast.makeText(getContext(), "New List Fetched", Toast.LENGTH_SHORT).show();
        });

        // from https://stackoverflow.com/questions/77487488/how-to-change-deprecated-onprepareoptionsmenu
        // and https://stackoverflow.com/questions/35802924/android-searchview-setonquerytextlistener-not-working
        // set the searchview
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                /* Do nothing */
            }

            @Override
            public void onPrepareMenu(@NonNull Menu menu) {
                MenuItem item = menu.findItem(R.id.menu_search);
                searchView = item.getActionView();
                if (searchView != null) {
                    ((androidx.appcompat.widget.SearchView) searchView).setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            /* Do nothing */
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            updateDisplay(newText);
                            return false;
                        }
                    });
                }
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                /* Do nothing */
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        executor.execute(this::updateDisplay);
    }

    private void updateDisplay()
    {
        database.collection("books_on_sale")
                .whereEqualTo("Email", userEmail)
                .orderBy("Date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
            data = new ArrayList<>();
            for (QueryDocumentSnapshot querySnapshot : queryDocumentSnapshots) {
                HashMap<String, String> bookInfoMap = new HashMap<>();
                bookInfoMap.put("title", (String) querySnapshot.getData().get("Title"));
                bookInfoMap.put("date", convertTimestamp(Long.parseLong(String.valueOf(querySnapshot.getData().get("Date")))));
                bookInfoMap.put("timeInMilliSec", String.valueOf(querySnapshot.getData().get("Date")));
                bookInfoMap.put("price", querySnapshot.getData().get("Price") + " AED");
                bookInfoMap.put("desc", (String) querySnapshot.getData().get("Description"));
                bookInfoMap.put("email", (String) querySnapshot.getData().get("Email"));
                bookInfoMap.put("name", (String) querySnapshot.getData().get("Name"));
                bookInfoMap.put("category", (String) querySnapshot.getData().get("Category"));
                data.add(bookInfoMap);
            }

            if (data.isEmpty()) {
                noBooksTextView.setVisibility(View.VISIBLE);
            } else {
                noBooksTextView.setVisibility(View.GONE);
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

    private void updateDisplay(String title)
    {
        filteredData = new ArrayList<>();
        if (!title.isEmpty()) {
            title = title.toLowerCase();
            for (HashMap<String, String> info : data) {
                if (info.get("title") != null && info.get("title").toLowerCase().contains(title)) {
                    HashMap<String, String> bookInfoMap = new HashMap<>(info);
                    filteredData.add(bookInfoMap);
                }
            }
        } else if (data != null) {
            filteredData = data;
        } else {
            return;
        }

        // create the resource, from, and to variables
        int resource = R.layout.listview_item;
        String[] from = {"date", "title", "price"};
        int[] to = { R.id.textViewDate, R.id.textViewTitle, R.id.textViewPrice};

        // create and set the adapter
        SimpleAdapter adapter = new SimpleAdapter(getContext(), filteredData, resource, from, to);
        listView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        Intent intent = new Intent(getContext(), BookDetailsActivity.class);

        ArrayList<HashMap<String, String>> info;
        if (searchView != null &&
                !((androidx.appcompat.widget.SearchView) searchView).getQuery().toString().isEmpty()) {
            info = filteredData;
        } else {
            info = data;
        }

        intent.putExtra("date", info.get(position).get("date"));
        intent.putExtra("title", info.get(position).get("title"));
        intent.putExtra("price", info.get(position).get("price").replace(" AED", ""));
        intent.putExtra("desc", info.get(position).get("desc"));
        intent.putExtra("name", info.get(position).get("name"));
        intent.putExtra("email", info.get(position).get("email"));
        intent.putExtra("category", info.get(position).get("category"));
        intent.putExtra("timeInMilliSec", info.get(position).get("timeInMilliSec"));

        startActivity(intent);
    }


    private String convertTimestamp(long timestamp) {
        // Converting date from
        // https://stackoverflow.com/questions/18929929/convert-timestamp-into-current-date-in-android#18930056
        return (String) DateFormat.format("MMMM dd, yyyy", timestamp);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}