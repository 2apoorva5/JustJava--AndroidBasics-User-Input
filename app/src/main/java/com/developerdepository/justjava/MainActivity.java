package com.developerdepository.justjava;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText inputName;
    private MaterialCheckBox checkBoxCream, checkBoxChocolate;
    private MaterialButton btnRemove, btnAdd, btnOrder;
    private MaterialTextView quantityTV, orderSummaryTV;

    private int coffeeQuantity = 0;
    private int totalPrice = 0;

    private static final int UNIT_PRICE = 5;
    private static final int CREAM_PRICE = 1;
    private static final int CHOCOLATE_PRICE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        // handles checked state for checkBoxCream
        checkBoxCream.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (coffeeQuantity > 0) {
                updatePrice();
            }
        });

        // handles checked state for checkBoxChocolate
        checkBoxChocolate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (coffeeQuantity > 0) {
                updatePrice();
            }
        });

        // handles click event on btnRemove
        btnRemove.setOnClickListener(v -> decreaseQuantity());

        // handles click event on btnAdd
        btnAdd.setOnClickListener(v -> increaseQuantity());

        // handles click event on btnOrder
        btnOrder.setOnClickListener(v -> validate());
    }

    /**
     * Views' hook to the layout
     */
    private void initViews() {
        inputName = findViewById(R.id.input_name);
        checkBoxCream = findViewById(R.id.checkbox_cream);
        checkBoxChocolate = findViewById(R.id.checkbox_chocolate);
        btnRemove = findViewById(R.id.btn_remove);
        btnAdd = findViewById(R.id.btn_add);
        btnOrder = findViewById(R.id.btn_order);
        quantityTV = findViewById(R.id.quantity);
        orderSummaryTV = findViewById(R.id.order_summary);
    }

    /**
     * Handles click event on btnRemove - Decrease coffee quantity
     */
    private void decreaseQuantity() {
        if (coffeeQuantity > 1) {
            coffeeQuantity--;

            displayQuantity();
            updatePrice();
        } else if (coffeeQuantity == 1) {
            Toast.makeText(MainActivity.this, "Quantity should at least be 1!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "That's Invalid!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles click event on btnAdd - Increase coffee quantity
     */
    private void increaseQuantity() {
        if (coffeeQuantity < 20) {
            coffeeQuantity++;

            displayQuantity();
            updatePrice();
        } else {
            Toast.makeText(MainActivity.this, "Maximum 20 cups can be ordered at a time!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Display coffee quantity on quantityTV
     */
    private void displayQuantity() {
        quantityTV.setText(String.valueOf(coffeeQuantity));
    }

    /**
     * Update price based on quantity and other inputs
     */
    private void updatePrice() {
        totalPrice = coffeeQuantity * UNIT_PRICE;

        if (checkBoxCream.isChecked()) {
            totalPrice = totalPrice + (coffeeQuantity * CREAM_PRICE);
        }

        if (checkBoxChocolate.isChecked()) {
            totalPrice = totalPrice + (coffeeQuantity * CHOCOLATE_PRICE);
        }

        // display order summary
        displayOrderSummary(coffeeQuantity, totalPrice);
    }

    /**
     * Display order summary on orderSummaryTV
     */
    @SuppressLint("SetTextI18n")
    private void displayOrderSummary(int coffeeQuantity, int totalPrice) {
        orderSummaryTV.setVisibility(View.VISIBLE);

        if (coffeeQuantity > 1) {
            orderSummaryTV.setText(
                    "Quantity : " + coffeeQuantity + " cups" + "\n" + "Total Price : $ " + totalPrice
            );
        } else {
            orderSummaryTV.setText(
                    "Quantity : " + coffeeQuantity + " cup" + "\n" + "Total Price : $ " + totalPrice
            );
        }
    }

    /**
     * Validate for coffee quantity and nameField
     */
    private void validate() {
        String name = Objects.requireNonNull(inputName.getText()).toString().trim();

        if (coffeeQuantity > 0) {
            if (!name.isEmpty()) {
                // send an email with order summary
                sendOrderViaEmail(name, checkBoxCream.isChecked(), checkBoxChocolate.isChecked(), coffeeQuantity, totalPrice);
            } else {
                Toast.makeText(MainActivity.this, "Enter your name!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Quantity can't be 0!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Send an email with order summary using Implicit Intent
     */
    @SuppressLint({"QueryPermissionsNeeded"})
    private void sendOrderViaEmail(String name, boolean creamAdded, boolean chocolateAdded, int coffeeQuantity, int totalPrice) {
        String[] emailList = new String[]{"developerdepository@gmail.com"};

        String subject = "Just Java - Order Request for " + name;

        String messageBody = "Order Summary : " + "\n\n" +
                "Name : " + name + "\n" +
                "Whipped Cream Added? : " + creamAdded + "\n" +
                "Chocolate Added? : " + chocolateAdded + "\n" +
                "Quantity : " + coffeeQuantity + " cups" + "\n" +
                "Total Price : $ " + totalPrice + "\n\n" +
                "Thank You!";

        // open an email app on the device (Implicit Intent)
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, emailList);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, messageBody);
        intent.setType("message/rfc822");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}