package com.kalhara.eshopfinal.fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import com.kalhara.eshopfinal.R;
import com.kalhara.eshopfinal.adapter.ProductSliderAdapter;
import com.kalhara.eshopfinal.databinding.FragmentProductDetailsBinding;
import com.kalhara.eshopfinal.model.Product;


public class ProductDetailsFragment extends Fragment {

    private FragmentProductDetailsBinding binding;
    private String productId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getString("productId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().findViewById(R.id.bottom_navigation_view).setVisibility(View.GONE);

        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });


        // Load Product Details

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("products")
                .whereEqualTo("productId", productId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot qds) {
                        if (!qds.isEmpty()) {
                            Product product = qds.getDocuments().get(0).toObject(Product.class);

                            ProductSliderAdapter adapter = new ProductSliderAdapter(product.getImages());
                            binding.productImageSlider.setAdapter(adapter);

                            binding.dotsIndicator.attachTo(binding.productImageSlider);


                            binding.productDetailsTitle.setText(product.getTitle());

                            binding.productDetailsRating.setRating(product.getRating());

                            binding.productDetailsPrice.setText("LKR " + product.getPrice());
                            binding.productDetailsAvbQuantity.setText(String.valueOf(product.getStockCount()));


                            if (product.getAttributes() != null) {

                                product.getAttributes().forEach(attribute -> {
                                    renderAttribute(attribute, binding.productDetailsAttributeContainer);

                                });

                            }

                        }
                    }
                });

    }

    private void renderAttribute(Product.Attribute attribute, ViewGroup container) {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);

        //Create Label
        TextView label = new TextView(getContext());
        label.setText(attribute.getName());

        row.addView(label);

        //Create Options
        ChipGroup group = new ChipGroup(getContext());
        group.setPadding(0, 0, 0, 2);
        group.setSelectionRequired(true);
        group.setSingleSelection(true);

        attribute.getValue().forEach(value -> {
            Chip chip = new Chip(getContext());
            chip.setCheckable(true);

            if ("color".equals(attribute.getType())) {
                chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor(value)));
            } else {
                chip.setText(value);

            }
            group.addView(chip);
        });

        row.addView(group);

        container.addView(row);

    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().findViewById(R.id.bottom_navigation_view).setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.bottom_navigation_view).setVisibility(View.GONE);
    }
}