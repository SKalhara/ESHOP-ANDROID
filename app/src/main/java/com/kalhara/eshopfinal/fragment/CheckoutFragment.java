package com.kalhara.eshopfinal.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kalhara.eshopfinal.R;
import com.kalhara.eshopfinal.databinding.FragmentCheckoutBinding;
import com.kalhara.eshopfinal.listener.FirestoreCallback;
import com.kalhara.eshopfinal.model.CartItem;
import com.kalhara.eshopfinal.model.Order;
import com.kalhara.eshopfinal.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class CheckoutFragment extends Fragment {

    private FragmentCheckoutBinding binding;
    FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false);

        binding.shippingLayoutBtn.setOnClickListener(v -> {
            if (binding.shippingLayoutBody.getVisibility() == View.GONE) {
                binding.shippingLayoutBody.setVisibility(View.VISIBLE);
                binding.shippingLayoutBtn.setRotation(180f);
            } else {
                binding.shippingLayoutBody.setVisibility(View.GONE);
                binding.shippingLayoutBtn.setRotation(0f);
            }
        });
        binding.billingLayoutBtn.setOnClickListener(v -> {
            if (binding.billingLayoutBody.getVisibility() == View.GONE) {
                binding.billingLayoutBody.setVisibility(View.VISIBLE);
                binding.billingLayoutBtn.setRotation(180f);
            } else {
                binding.billingLayoutBody.setVisibility(View.GONE);
                binding.billingLayoutBtn.setRotation(0f);


            }
        });

        binding.shippingDetailsCheckBilling.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.billingLayout.setVisibility(View.GONE);
            } else {
                binding.billingLayout.setVisibility(View.VISIBLE);
                binding.billingLayoutBody.setVisibility(View.VISIBLE);
            }
        });


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        double shippingCost = 400;

        getCartItems(cartItems -> {
            ArrayList<String> productIds = new ArrayList<>();
            cartItems.forEach(cartItem -> {
                productIds.add(cartItem.getProductId());
            });

            getProductsById(productIds, data -> {
                double subTotal = 0;
                for (CartItem cartItem : cartItems) {
                    Product product = data.get(cartItem.getProductId());
                    if (product != null) {
                        subTotal += product.getPrice() * cartItem.getQuantity();
                    }
                }
                double total = subTotal + shippingCost;

                binding.checkoutSubtotal.setText(String.format(Locale.US, "LKR %,.2f", subTotal));
                binding.checkoutShipping.setText(String.format(Locale.US, "LKR %,.2f", shippingCost));
                binding.checkoutTotal.setText(String.format(Locale.US, "LKR %,.2f", total));

            });
        });

                String uid = firebaseAuth.getCurrentUser().getUid();

        binding.checkoutBtnProceed.setOnClickListener(v -> {
            db.collection("users").document(uid).collection("cart")
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot qds) {
                            if (!qds.isEmpty()) {
                                List<CartItem> cartItems = qds.toObjects(CartItem.class);

                                Order order = new Order();
                                order.setOrderId(String.valueOf(System.currentTimeMillis()));
                                order.setUserId(uid);

                                String shipping_name = binding.shippingDetailsName.getText().toString();
                                String shipping_email = binding.shippingDetailsEmail.getText().toString();
                                String shipping_contact = binding.shippingDetailsContact.getText().toString();
                                String shipping_address1 = binding.shippingDetailsAddress1.getText().toString();
                                String shipping_address2 = binding.shippingDetailsAddress2.getText().toString();
                                String shipping_city = binding.shippingDetailsCity.getText().toString();
                                String shipping_postalCode = binding.shippingDetailsPostcode.getText().toString();

                                Order.Address shippingAddress = Order.Address.builder()
                                        .name(shipping_name)
                                        .email(shipping_email)
                                        .contact(shipping_contact)
                                        .address1(shipping_address1)
                                        .address2(shipping_address2)
                                        .city(shipping_city)
                                        .postcode(shipping_postalCode).build();

                                order.setShippingAddress(shippingAddress);

                                if (!binding.shippingDetailsCheckBilling.isChecked()) {
                                    String billing_name = binding.billingDetailsName.getText().toString();
                                    String billing_email = binding.billingDetailsEmail.getText().toString();
                                    String billing_contact = binding.billingDetailsContact.getText().toString();
                                    String billing_address1 = binding.billingDetailsAddress1.getText().toString();
                                    String billing_address2 = binding.billingDetailsAddress2.getText().toString();
                                    String billing_city = binding.billingDetailsCity.getText().toString();
                                    String billing_postalCode = binding.billingDetailsPostcode.getText().toString();

                                    Order.Address billingAddress = Order.Address.builder()
                                            .name(billing_name)
                                            .email(billing_email)
                                            .contact(billing_contact)
                                            .address1(billing_address1)
                                            .address2(billing_address2)
                                            .city(billing_city)
                                            .postcode(billing_postalCode).build();

                                    order.setBillingAddress(billingAddress);
                                }

                                List<Order.OrderItem> orderItems = new ArrayList<>();

                                for (CartItem cartItem : cartItems) {
                                    List<Order.OrderItem.Attribute> attributes = new ArrayList<>();
                                    for (CartItem.Attribute at : cartItem.getAttributes()) {
                                        Order.OrderItem.Attribute attribute = Order.OrderItem.Attribute.builder()
                                                .name(at.getName()).value(at.getValue())
                                                .build();
                                        attributes.add(attribute);

                                    }
                                    Order.OrderItem orderItem = Order.OrderItem.builder()
                                            .productId(cartItem.getProductId())
                                            .unitPrice(0)
                                            .quantity(cartItem.getQuantity())
                                            .attributes(attributes).build();

                                    orderItems.add(orderItem);
                                }

                                //Add order items to Order object
                                order.setOrderItems(orderItems);
                                db.collection("orders").document()
                                        .set(order)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Order Saved", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }
                    });
        });

    }

    private void getCartItems(FirestoreCallback<List<CartItem>> callback) {

        List<CartItem> cartItems = new ArrayList<>();

        String uid = firebaseAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).collection("cart")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot qds) {
                        if (!qds.isEmpty()) {
                            List<CartItem> cartItems = qds.toObjects(CartItem.class);
                            callback.onCallBack(cartItems);
                        }
                    }
                });
    }


    private void getProductsById(List<String> productIds, FirestoreCallback<Map<String, Product>> callback) {

        Map<String, Product> products = new HashMap<>();

        if (productIds == null || productIds.isEmpty()) {
            callback.onCallBack(products);
            return;
        }
        db.collection("products")
                .whereIn("productId", productIds).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot qds) {

                        qds.getDocuments().forEach(ds -> {
                            Product product = ds.toObject(Product.class);
                            if (product != null) {
                                products.put(product.getProductId(), product);
                            }
                        });
                        callback.onCallBack(products);
                    }
                });
    }

//    private void getSubTotal() {
//
//        List<CartItem> cartItems = getCartItems();
//        List<String> productIds = new ArrayList<>();
//        cartItems.forEach(cartItem -> {
//            productIds.add(cartItem.getProductId());
//        });
//
//        Map<String, Product> products = getProductsById(productIds);
//
//        double subTotal = 0;
//
//        for (CartItem cartItem : cartItems) {
//            Product product = products.get(cartItem.getProductId());
//            if (product != null) {
//                subTotal += product.getPrice() * cartItem.getQuantity();
//            }
//        }
//        return subTotal;
//
//    }
}