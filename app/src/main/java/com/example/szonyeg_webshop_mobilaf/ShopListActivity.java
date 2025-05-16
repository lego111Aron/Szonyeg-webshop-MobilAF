package com.example.szonyeg_webshop_mobilaf;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShopListActivity extends AppCompatActivity {
    private static final String LOG_TAG = ShopListActivity.class.getName();
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private RecyclerView mRecyclerView;
    private ArrayList<ShoppingItem> mItemList;
    private ShoppingItemAdapter mAdapter;

    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;

    private NotificationHandler mNotificationHandler;

    private int gridNumber = 1;
    private boolean viewRow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user!");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user! Redirecting...");
            finish();
        }

        TextView titleText = findViewById(R.id.titleText);
        TextView subtitleText = findViewById(R.id.subtitleText);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        titleText.startAnimation(fadeIn);
        subtitleText.startAnimation(fadeIn);

        /*Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(view -> {
            mAuth.signOut();
            Log.d(LOG_TAG, "User signed out");

            Intent intent = new Intent(ShopListActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });*/

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItemList = new ArrayList<>();

        mAdapter = new ShoppingItemAdapter(this, mItemList);
        mRecyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");

        queryData();

        mNotificationHandler = new NotificationHandler(this);
    }

    private void queryData() {
        mItemList.clear();
//        mItems.whereEqualTo()
        mItems.orderBy("cartedCount", Query.Direction.DESCENDING).limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                ShoppingItem item = document.toObject(ShoppingItem.class);
                item.setId(document.getId());
                mItemList.add(item);
//                intializeData();
            }
            if (mItemList.size() == 0) {
                intializeData();
                queryData();
            }
            mAdapter.notifyDataSetChanged();
        });
    }

    private void intializeData() {
        String[] itemsList = getResources().getStringArray(R.array.shopping_item_names);
        String[] itemsInfo = getResources().getStringArray(R.array.shopping_item_desc);
        String[] itemsPrice = getResources().getStringArray(R.array.shopping_item_prices);
        TypedArray itemsImageResource = getResources().obtainTypedArray(R.array.shopping_item_images);
        TypedArray itemsRate = getResources().obtainTypedArray(R.array.shopping_item_rates);

//        mItemList.clear();
        for (int i = 0; i < itemsList.length; i++) {
            String name = itemsList[i];
            String info = itemsInfo[i];
            String price = itemsPrice[i];
            float rate = itemsRate.getFloat(i, 0);
            String imageUrl = "android.resource://" + getPackageName() + "/" + itemsImageResource.getResourceId(i, 0);
//            Log.d(LOG_TAG, "Kép erőforrás azonosító: " + imageUrl);
            int cartedCount = 0;

            mItems.add(new ShoppingItem(name, info, price, rate, imageUrl, cartedCount));
        }
        itemsImageResource.recycle();
//        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.shop_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.log_out_button) {
            Log.d(LOG_TAG, "Log out button clicked");
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        } else if (item.getItemId() == R.id.setting_button) {
            Log.d(LOG_TAG, "Settings button clicked");
            return true;
        } else if (item.getItemId() == R.id.cart) {
            Log.d(LOG_TAG, "Cart button clicked");
            return true;
        } else if (item.getItemId() == R.id.view_selector) {
            Log.d(LOG_TAG, "View selector button clicked");

            if(viewRow) {
                changeSpanCount(item, R.drawable.view_grid, 1);

            } else {
                changeSpanCount(item, R.drawable.view_row, 2);
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
//        return super.onOptionsItemSelected(item);
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

//        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
//        contentTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        /*rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });*/
        return super.onPrepareOptionsMenu(menu);
    }

    public void deleteItem(ShoppingItem item) {
        //delete
        DocumentReference ref = mItems.document(item._getId());

        ref.delete().addOnSuccessListener(success -> {
                    Log.d(LOG_TAG, "Item is successfuly deleted"+ item._getId());
                })
                .addOnFailureListener(failure -> {
                    Toast.makeText(this, "Item"+ item._getId()+ "cannot be deleted.", Toast.LENGTH_LONG).show();
                });
        queryData();
        mNotificationHandler.cancel();
    }
    public void addToCart(ShoppingItem item) {
        //update
        mItems.document(item._getId()).update("cartedCount", item.getCartedCount() + 1)
//                .addOnSuccessListener(success -> {
//                    Log.d(LOG_TAG, "Item is successfuly added to cart"+ item._getId());
//                })
                .addOnFailureListener(failure -> {
                    Toast.makeText(this, "Item"+ item._getId()+ "cannot be added to cart.", Toast.LENGTH_LONG).show();
                });

        mNotificationHandler.send(item.getName());
        queryData();
    }
}