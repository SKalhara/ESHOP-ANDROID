package com.kalhara.eshopfinal.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.kalhara.eshopfinal.R;
import com.kalhara.eshopfinal.adapter.ListingAdapter;
import com.kalhara.eshopfinal.databinding.FragmentListingBinding;
import com.kalhara.eshopfinal.model.Product;

import java.util.Arrays;
import java.util.List;


public class ListingFragment extends Fragment {

    private FragmentListingBinding binding;
    private ListingAdapter adapter;
    private String categoryId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentListingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recyclerViewListing.setLayoutManager(new GridLayoutManager(getContext(), 2));
        FirebaseFirestore db = FirebaseFirestore.getInstance();

//        Product c1 = new Product("p1", "Toy1", "test des 1", 1500.00, "cat1", Arrays.asList("image1","image2"),5,true);
//        Product c2 = new Product("p2", "Toy2", "test des 2", 1500.00, "cat1", Arrays.asList("image1","image2"),10,true);
//        Product c3 = new Product("p3", "Toy3", "test des 3", 1500.00, "cat1", Arrays.asList("image1","image2"),15,true);
//        Product c4 = new Product("p4", "Toy4", "test des 4", 1500.00, "cat1", Arrays.asList("image1","image2"),20,true);
//        Product c5 = new Product("p5", "Toy5", "test des 5", 1500.00, "cat1", Arrays.asList("image1","image2"),25,true);
//        Product c6 = new Product("p6", "Toy6", "test des 6", 1500.00, "cat1", Arrays.asList("image1","image2"),30,true);
//        Product c7 = new Product("p7", "Toy7", "test des 7", 1500.00, "cat1", Arrays.asList("image1","image2"),35,true);
//        Product c8 = new Product("p8", "Toy8", "test des 8", 1500.00, "cat1", Arrays.asList("image1","image2"),40,true);
//
//        List<Product> cats = List.of(c1, c2, c3, c4, c5, c6, c7, c8);
//
//        WriteBatch batch = db.batch();
//
//        for (Product c : cats) {
//            DocumentReference ref = db.collection("products").document();
//            batch.set(ref, c);
//        }
//
//        batch.commit();

        db.collection("products")
                .whereEqualTo("categoryId", categoryId)
                .orderBy("title", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(ds -> {
                    if (!ds.isEmpty()) {
                        List<Product> products = ds.toObjects(Product.class);
                        adapter = new ListingAdapter(products, product -> {

                            Bundle bundle = new Bundle();
                            bundle.putString("productId", product.getProductId());
                            ProductDetailsFragment fragment = new ProductDetailsFragment();
                            fragment.setArguments(bundle);

                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        });
                        binding.recyclerViewListing.setAdapter(adapter);
                    }
                });

        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

    }
}