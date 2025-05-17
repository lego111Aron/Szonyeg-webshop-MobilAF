package com.example.szonyeg_webshop_mobilaf;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import com.bumptech.glide.Glide;



public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder> implements Filterable {

    private ArrayList<ShoppingItem> mShoppingItemData;
    private ArrayList<ShoppingItem> mShoppingItemDataAll;
    private Context mContext;
    private int lastPosition = -1;

    private View addToCartButton; // Deklaráció
    private View deleteButton;    // Deklaráció

    private boolean isCartPage; // Új mező, amely jelzi, hogy a kosár oldalon vagy-e

    ShoppingItemAdapter(Context context, ArrayList<ShoppingItem> itemData, boolean isCartPage) {
        this.mShoppingItemData = itemData;
        this.mShoppingItemDataAll = itemData;
        this.mContext = context;
        this.isCartPage = isCartPage; // Inicializálás
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ShoppingItemAdapter.ViewHolder holder, int position) {
        ShoppingItem currentItem = mShoppingItemData.get(position);
        holder.bindTo(currentItem, isCartPage); // Átadjuk az isCartPage értékét
        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.setAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mShoppingItemData.size();
    }

    @Override
    public Filter getFilter() {
        return mshoppingFilter;
    }

    private Filter mshoppingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<ShoppingItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(constraint == null || constraint.length() == 0) {
                results.count = mShoppingItemDataAll.size();
                results.values = mShoppingItemDataAll;
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ShoppingItem item : mShoppingItemDataAll) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mShoppingItemData = (ArrayList<ShoppingItem>) results.values;
            notifyDataSetChanged();

        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleText;
        private TextView mInfoText;
        private TextView mPriceText;
        private ImageView mItemImage;
        private RatingBar mRatingBar;

        public ViewHolder(View itemView) {
            super(itemView);

            mTitleText=itemView.findViewById(R.id.itemTitle);
            mInfoText=itemView.findViewById(R.id.subTitle);
            mPriceText=itemView.findViewById(R.id.price);
            mItemImage=itemView.findViewById(R.id.itemImage);
            mRatingBar=itemView.findViewById(R.id.ratingBar);

            /*itemView.findViewById(R.id.add_to_cart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("Activiy", "Add cart button clicked!");
                }
            });*/

            // Gombok inicializálása
            addToCartButton = itemView.findViewById(R.id.add_to_cart);
            deleteButton = itemView.findViewById(R.id.delete);
        }

        public void bindTo(ShoppingItem currentItem, boolean isCartPage) {
            mTitleText.setText(currentItem.getName());
            mInfoText.setText(currentItem.getInfo());
            mPriceText.setText(currentItem.getPrice());
            mRatingBar.setRating(currentItem.getRetadInfo());

            Glide.with(mContext).load(currentItem.getImageUrl()).into(mItemImage);
            itemView.findViewById(R.id.add_to_cart).setOnClickListener(view -> ((ShopListActivity)mContext).addToCart(currentItem));
            itemView.findViewById(R.id.delete).setOnClickListener(view -> ((ShopListActivity)mContext).deleteItem(currentItem));
        }
    }
}
