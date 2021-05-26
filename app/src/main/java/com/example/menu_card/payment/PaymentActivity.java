package com.example.menu_card.payment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.menu_card.Common.common_methods;
import com.example.menu_card.R;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.example.menu_card.registration.MainActivity.BASE_URL;

public class PaymentActivity extends AppCompatActivity {
    private static final int PAYTM_REQUEST_CODE = 0;
    private static final int PAYMENT_SUCCESSFUL = 0;

    RequestQueue requestQueue;

    String authToken, txnID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_payment2);

        String orderID = getIntent().getStringExtra("orderID");
        assert (orderID != null);

        try {
            authToken = authToken();
            checkout(orderID, authToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkout(String orderID, String authToken) {
        requestQueue.add(new JsonObjectRequest(
                Request.Method.POST,
                /// I do not need to url encode the order id as it is
                /// a uuid4 which is compatible with urls as is
                BASE_URL + "/checkout?order_id=" + orderID,
                null,
                response -> {
                    try {
                        if (response.has("error")) {
                            String error = response.getString("error");
                            System.out.println(error);
                            showError(error);
                            return;
                        }

                        System.out.println(response);

                        String amount = response.getString("amount");
                        String callbackUrl = response.getString("callback_url");
                        String merchantID = response.getString("m_id");
                        String token = response.getString("token");
                        txnID = response.getString("txn_id");

                        startPayment(amount, callbackUrl, merchantID, token, txnID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                volleyError -> {
                    String error = volleyError.toString();
                    System.out.println(error);

                    showError(error);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-Auth-Token", authToken);
                return headers;
            }
        });
    }

    private void startPayment(String amount, String callbackUrl, String merchantID, String token, String txnID) {
        PaytmOrder paytmOrder = new PaytmOrder(txnID, merchantID, token, amount, callbackUrl);

        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {

            @Override
            public void onTransactionResponse(@Nullable Bundle bundle) {
                assert bundle != null;
                updatePaymentStatus();
            }

            @Override
            public void networkNotAvailable() {
                System.out.println("Network not available");
                // makes no sense to try to update status when network is not available
                setResult(RESULT_CANCELED);
                finish();
            }

            @Override
            public void onErrorProceed(String s) {
                updatePaymentStatus();
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                updatePaymentStatus();
            }

            @Override
            public void someUIErrorOccurred(String s) {
                updatePaymentStatus();
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                updatePaymentStatus();
            }

            @Override
            public void onBackPressedCancelTransaction() {
                updatePaymentStatus();
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                updatePaymentStatus();
            }
        });

        transactionManager.setShowPaymentUrl("https://securegw-stage.paytm.in/theia/api/v1/showPaymentPage");
        transactionManager.startTransaction(this, PAYTM_REQUEST_CODE);
    }

    private void showError(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setCancelable(true)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.cancel())
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYTM_REQUEST_CODE && data != null) {
            updatePaymentStatus();
        }
    }

    private void updatePaymentStatus() {
        try {
            requestQueue.add(new JsonObjectRequest(
                    Request.Method.POST,
                    BASE_URL + "/update_payment_status?txn_id=" + URLEncoder.encode(txnID, StandardCharsets.UTF_8.toString()),
                    null,
                    response -> {
                        try {
                            if (response.has("error")) {
                                String error = response.getString("error");
                                showError(error);
                                return;
                            }

                            int paymentStatus = response.getInt("payment_status");

                            if (paymentStatus == PAYMENT_SUCCESSFUL) {
                                setResult(RESULT_OK);
                            } else {
                                setResult(RESULT_CANCELED);
                            }
                        } catch (Exception e) {
                            System.out.println(response);
                            e.printStackTrace();
                        } finally {
                            finish();
                        }
                    },
                    volleyError -> {
                        String error = volleyError.toString();
                        System.out.println(error);

                        showError(error);
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("X-Auth-Token", authToken);
                    return headers;
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String authToken() throws IOException {
        return common_methods.getKey(PaymentActivity.this, "jwt_token");
    }
}