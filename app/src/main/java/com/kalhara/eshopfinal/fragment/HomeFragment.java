package com.kalhara.eshopfinal.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kalhara.eshopfinal.R;
import com.kalhara.eshopfinal.adapter.SectionAdapter;
import com.kalhara.eshopfinal.databinding.FragmentHomeBinding;
import com.kalhara.eshopfinal.model.Product;

import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public HomeFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadTopSellingProducts();
    }

    private void loadTopSellingProducts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("products")
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot qds) {
//                        if (!qds.isEmpty()) {
//                            List<Product> products = qds.toObjects(Product.class);
//
//                            LinearLayoutManager manager = new LinearLayoutManager(getContext()
//                                    , LinearLayoutManager.HORIZONTAL, false);
//
//                            binding.homeTopSellSection.itemSectionContainer.setLayoutManager(manager);
//                            SectionAdapter adapter = new SectionAdapter(products, product -> {
//
//                            });
//                            binding.homeTopSellSection.itemSectionTitle.setText("Top Selling Products");
//                            binding.homeTopSellSection.itemSectionContainer.setAdapter(adapter);
//                        }
//                    }
//                });
    }
}