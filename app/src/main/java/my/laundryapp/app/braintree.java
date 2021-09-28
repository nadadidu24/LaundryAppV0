package my.laundryapp.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.Authorization;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.PayPalRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.api.models.PostalAddress;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import my.laundryapp.app.Callback.ILoadTimeFromFirebaseListener;
import my.laundryapp.app.Common.Common;
import my.laundryapp.app.Database.CartDataSource;
import my.laundryapp.app.Model.Order;
import my.laundryapp.app.ui.cart.CartFragment;

public class braintree extends AppCompatActivity {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private CartDataSource cartDataSource;

    ILoadTimeFromFirebaseListener listener;
    double harga;
    double contoh;

    final int REQUEST_CODE = 1;
    final String get_token = "http://10.0.2.2/BraintreePayments/main.php";
    final String send_payment_details = "http://10.0.2.2/BraintreePayments/mycheckout.php";
    String token, amount;
    HashMap<String, String> paramHash;
    private BraintreeFragment mBraintreeFragment;
    Authorization mAuthorization;

    Button btnPay;
    EditText etAmount;
    LinearLayout llHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_braintree);

        Bundle c = getIntent().getExtras();
        double result = c.getDouble("contoh");
        String resultContoh =String.valueOf(result);




        try {
            mBraintreeFragment = BraintreeFragment.newInstance(this, String.valueOf(mAuthorization));
            mBraintreeFragment.addListener(new PaymentMethodNonceCreatedListener() {
                @Override
                public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
                    // Send nonce to server
                    String nonce = paymentMethodNonce.getNonce();
                    if (paymentMethodNonce instanceof PayPalAccountNonce) {
                        PayPalAccountNonce payPalAccountNonce = (PayPalAccountNonce)paymentMethodNonce;

                        // Access additional information
                        String email = payPalAccountNonce.getEmail();
                        String firstName = payPalAccountNonce.getFirstName();
                        String lastName = payPalAccountNonce.getLastName();
                        String phone = payPalAccountNonce.getPhone();

                        // See PostalAddress.java for details
                        PostalAddress billingAddress = payPalAccountNonce.getBillingAddress();
                        PostalAddress shippingAddress = payPalAccountNonce.getShippingAddress();
                    }

                }
            });
            // mBraintreeFragment is ready to use!
            setupBraintreeAndStartExpressCheckout();

        } catch (InvalidArgumentException e) {
            // There was an issue with your authorization string.
        }

        llHolder = (LinearLayout) findViewById(R.id.llHolder);
        etAmount = (EditText) findViewById(R.id.etPrice);
        btnPay = (Button) findViewById(R.id.btnPay);

        //etAmount.setText((int) harga);
        etAmount.setText(resultContoh);


        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBraintreeSubmit();
            }

        });

        HttpRequest1();
        //init();

    }

    private void init() {
        compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getCustUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> {
                    //when we have all cart items, we have total price
                    cartDataSource.sumPriceInCart(Common.currentUser.getCustUid())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<Double>() {
                                @Override
                                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                }

                                @Override
                                public void onSuccess(@io.reactivex.annotations.NonNull Double totalPrice) {
                                    double finalPrice = totalPrice; // will modify later for discount

                                    Order order = new Order();
                                    order.setCustUserId(Common.currentUser.getCustUid());
                                    order.setCustUserName(Common.currentUser.getName());
                                    order.setCustUserPhone(Common.currentUser.getPhoneNumber());
                                    //order.setShippingAddress(address);
                                    //order.setComment(comment);


                                    //ada if actually
                                    order.setLat(-0.1f);
                                    order.setLng(-0.1f);

                                    order.setCartItemList(cartItems);
                                    order.setTotalPayment(totalPrice);
                                    order.setDiscount(0); // modify later with discount
                                    order.setFinalPayment(finalPrice);
                                    order.setCod(false);
                                    order.setTransactionId("Card Payment");

                                    //submit this order object to firebase
                                    syncLocalTimeWithGlobalTime(order);




                                }

                                @Override
                                public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                    Toast.makeText(braintree.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();


                                }
                            });


                }, throwable -> {
                    Toast.makeText(braintree.this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();

                }));
    }



    public void setupBraintreeAndStartExpressCheckout() {
        PayPalRequest request = new PayPalRequest("1")
                .currencyCode("USD")
                .intent(PayPalRequest.INTENT_AUTHORIZE);

        PayPal.requestOneTimePayment(mBraintreeFragment, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();

                String stringNonce = nonce.getNonce();
                Log.d("mylog", "Result: " + stringNonce);
                // Send payment price with the nonce
                // use the result to update your UI and send the payment method nonce to your server
                if (!etAmount.getText().toString().isEmpty()) {
                    amount = etAmount.getText().toString();
                    paramHash = new HashMap<>();
                    paramHash.put("amount", amount);
                    paramHash.put("nonce", stringNonce);
                    sendPaymentDetails();
                } else
                    Toast.makeText(braintree.this, "Please enter a valid amount.", Toast.LENGTH_SHORT).show();
                //
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
                Log.d("mylog", "user canceled");
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.d("mylog", "Error : " + error.toString());
            }
        }
    }

    public void onBraintreeSubmit() {
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(token);
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
    }

    private void sendPaymentDetails() {
        RequestQueue queue = Volley.newRequestQueue(braintree.this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, send_payment_details,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.contains("Successful"))
                        {
                            Toast.makeText(braintree.this, "Transaction successful", Toast.LENGTH_LONG).show();
                            inita();

                        }
                        else Toast.makeText(braintree.this, "Transaction failed", Toast.LENGTH_LONG).show();
                        Log.d("mylog", "Final Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("mylog", "Volley error : " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                if (paramHash == null)
                    return null;
                Map<String, String> params = new HashMap<>();
                for (String key : paramHash.keySet()) {
                    params.put(key, paramHash.get(key));
                    Log.d("mylog", "Key : " + key + " Value : " + paramHash.get(key));
                }

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void inita() {
        Toast.makeText(braintree.this, "Inita", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(braintree.this, Main4Activity.class);
        startActivity(intent);
        
    }

    private void syncLocalTimeWithGlobalTime(Order order) {
        final DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long offset = snapshot.getValue(Long.class);
                long estimatedServerTimeMs = System.currentTimeMillis()+offset;
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                Date resultDate = new Date(estimatedServerTimeMs);
                Log.d("TEST_DATE",""+sdf.format(resultDate));

                listener.onLoadTimeSuccess(order,estimatedServerTimeMs);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onLoadTimeFailed(error.getMessage());

            }
        });
    }

    public void onLoadTimeSuccess(Order order, long estimateTimeTaken){

    }

    private void HttpRequest1() {

        new AsyncTasks() {
            ProgressDialog progress;
            @Override
            public void onPreExecute() {
                // before execution
                progress = new ProgressDialog(braintree.this, android.R.style.Theme_DeviceDefault_Dialog);
                progress.setCancelable(false);
                progress.setMessage("We are contacting our servers for token, Please wait");
                progress.setTitle("Getting token");
                progress.show();
            }

            @Override
            public void doInBackground() {
                // background task here
                HttpClient client = new HttpClient();
                client.get(get_token, new HttpResponseCallback() {
                    @Override
                    public void success(String responseBody) {
                        Log.d("mylog", responseBody);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(braintree.this, "Successfully got token", Toast.LENGTH_SHORT).show();
                                llHolder.setVisibility(View.VISIBLE);
                            }
                        });
                        token = responseBody;
                    }

                    @Override
                    public void failure(Exception exception) {
                        final Exception ex = exception;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(braintree.this, "Failed to get token: " + ex.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

            }

            @Override
            public void onPostExecute() {
                // Ui task here
                progress.dismiss();
            }
        }.execute();

    }



}