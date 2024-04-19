package com.project.usedbooksale;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.usedbooksale.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    private TextView textViewName;
    private TextView textViewEmail;
    private FirebaseFirestore database;
    private String userEmail;
    private String userFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_my_listings, R.id.nav_profile)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        // ------------------------ Before this line is from Android Studio ------------------------

        // The parentView is from https://stackoverflow.com/questions/35871454/why-findviewbyid-return-null-for-a-view-in-a-drawer-header
        View parentView = navigationView.getHeaderView(0);

        textViewName = parentView.findViewById(R.id.textViewName);
        textViewEmail = parentView.findViewById(R.id.textViewEmail);

        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        userEmail = auth.getCurrentUser().getEmail();

        // Set name and email in navigation view
        database.collection("users").document(userEmail)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String fName = (String) documentSnapshot.get("FirstName");
                    String lName = (String) documentSnapshot.get("LastName");
                    userFullName = fName + " " + lName;
                    textViewName.setText(userFullName);
                });
        textViewEmail.setText(userEmail);

        // from
        // https://stackoverflow.com/questions/62773966/setting-onclick-for-navigationdrawer-items-in-android
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.nav_logout) {
                new MaterialAlertDialogBuilder(this)
                        .setIcon(R.drawable.alert_info)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton("Yes", (dialog, which) -> logout())
                        .setNegativeButton("No", null)
                        .show();
            } else {
                NavigationUI.onNavDestinationSelected(menuItem, navController);
                drawer.closeDrawer(GravityCompat.START);
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sell_book) {
            Intent sellBookIntent = new Intent(this, SellBookActivity.class);
            sellBookIntent.putExtra("userEmail", userEmail);
            sellBookIntent.putExtra("userFullName", userFullName);

            startActivity(sellBookIntent);
            return true;
        }
        if (item.getItemId() == R.id.menu_settings) {
            Toast.makeText(this, "Settings to be implemented in a future update!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void logout() {
        auth.signOut();
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
        Toast.makeText(this, "Successfully logged out", Toast.LENGTH_SHORT).show();
    }
}