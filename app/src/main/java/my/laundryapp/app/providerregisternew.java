package my.laundryapp.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.HashMap;

public class providerregisternew extends AppCompatActivity {

    TextInputLayout providername,provideremail,providerphone,providercapitals,providerpass,providercpass;
    TextInputEditText mobileprovider;
    Button providersignup;
    CountryCodePicker providerccp;
    FirebaseAuth FAuthprovider;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    String providerSname,providerSemail,providerSphone,providerScapital,providerSpass,providerScpass,role="LaundryProvider";

    String phonenumber;

    AutoCompleteTextView providercapitaltext;

    ArrayList<String> arrayList_capitals;
    ArrayAdapter<String > arrayAdapter_capital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_providerregisternew);

        //cuba
        phonenumber = getIntent().getStringExtra("phonenumber").trim();
        mobileprovider = (TextInputEditText) findViewById(R.id.numberhere);



        providername = (TextInputLayout) findViewById(R.id.providername);
        provideremail = (TextInputLayout) findViewById(R.id.provideremail);
        providerphone = (TextInputLayout) findViewById(R.id.providerMobileno);
        providerpass = (TextInputLayout) findViewById(R.id.providerpass);
        providercpass = (TextInputLayout) findViewById(R.id.providerpass2);
        providercapitals = (TextInputLayout) findViewById(R.id.providercapital);
        providercapitaltext = (AutoCompleteTextView) findViewById(R.id.providercapitaltext);
        providersignup = (Button) findViewById(R.id.buttonsignupprovider);
        providerccp = (CountryCodePicker) findViewById(R.id.providerCountryCode);

        mobileprovider.setText(phonenumber);

        arrayList_capitals = new ArrayList<>();
        arrayList_capitals.add("Alor Gajah");
        arrayList_capitals.add("Malacca City");
        arrayList_capitals.add("Jasin");

        arrayAdapter_capital = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, arrayList_capitals);

        providercapitaltext.setAdapter(arrayAdapter_capital);

        providercapitaltext.setThreshold(1);

        databaseReference = firebaseDatabase.getInstance().getReference("LaundryProvider");
        FAuthprovider = FirebaseAuth.getInstance();

        providersignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                providerSname = providername.getEditText().getText().toString().trim();
                providerSemail = provideremail.getEditText().getText().toString().trim();
                providerSphone = providerphone.getEditText().getText().toString().trim();
                providerSpass = providerpass.getEditText().getText().toString().trim();
                providerScpass = providercpass.getEditText().getText().toString().trim();
                providerScapital = providercapitals.getEditText().getText().toString().trim();

                if (isValid()) {

                    final ProgressDialog mDialog = new ProgressDialog(providerregisternew.this);
                    mDialog.setCancelable(false);
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.setMessage("Registering please wait...");
                    mDialog.show();

                    FAuthprovider.createUserWithEmailAndPassword(providerSemail, providerSpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                String useridd = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                databaseReference = FirebaseDatabase.getInstance().getReference("User").child(useridd);
                                final HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("Role", role);
                                databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {

                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        HashMap<String, String> hashMappp = new HashMap<>();
                                        hashMappp.put("Capital", providerScapital);
                                        hashMappp.put("ConfirmPassword", providerScpass);
                                        hashMappp.put("Email", providerSemail);
                                        hashMappp.put("Name", providerSname);
                                        hashMappp.put("PhoneNumber", providerSphone);
                                        hashMappp.put("Password", providerSpass);
                                        hashMappp.put("laundryProviderUid",useridd);
                                        firebaseDatabase.getInstance().getReference("LaundryProvider")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(hashMappp).addOnCompleteListener(new OnCompleteListener<Void>() {

                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mDialog.dismiss();

                                                FAuthprovider.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            //adik edit
                                                            AlertDialog.Builder buildero = new AlertDialog.Builder(providerregisternew.this);

                                                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(providerregisternew.this, R.style.myFullscreenAlertDialogStyle);



                                                            View view = LayoutInflater.from(providerregisternew.this).inflate(R.layout.layout_successfull_register,null);

                                                            builder.setView(view);

                                                            builder.setNegativeButton("No,thank you", (dialog, which) -> {
                                                                dialog.dismiss();

                                                                buildero.setTitle("Alright");
                                                                buildero.setMessage("Don't forget to verify your email before login");
                                                                buildero.setCancelable(false);
                                                                buildero.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {

                                                                        dialog.dismiss();
                                                                        Intent login = new Intent(providerregisternew.this, accountmenu.class);
                                                                        startActivity(login);
                                                                    }
                                                                });
                                                                AlertDialog alert = buildero.create();
                                                                //TextView messageView = (TextView)alert.findViewById(android.R.id.message);
                                                                //messageView.setGravity(Gravity.CENTER);

                                                                alert.show();

                                                                TextView titleView = (TextView)alert.findViewById(providerregisternew.this.getResources().getIdentifier("alertTitle", "id", "android"));
                                                                if (titleView != null) {
                                                                    titleView.setGravity(Gravity.CENTER);
                                                                }
                                                                TextView messageView = (TextView)alert.findViewById(android.R.id.message);
                                                                messageView.setGravity(Gravity.CENTER);





                                                            }).setPositiveButton("Yes, thankyou", (dialog, which) -> {
                                                                //Toast.makeText(getContext(), "Coming soon features!", Toast.LENGTH_SHORT).show();

                                                                android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(providerregisternew.this, R.style.myFullscreenAlertDialogStyle);



                                                                View view1 = LayoutInflater.from(providerregisternew.this).inflate(R.layout.layout_tutorial,null);

                                                                builder1.setView(view1);

                                                                builder1.setPositiveButton("Close", (dialog1, which1) -> {
                                                                    dialog1.dismiss();


                                                                    buildero.setMessage("Please verify your email before login");
                                                                    buildero.setCancelable(false);
                                                                    buildero.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {

                                                                            dialog.dismiss();
                                                                            Intent login = new Intent(providerregisternew.this, accountmenu.class);
                                                                            startActivity(login);
                                                                        }
                                                                    });
                                                                    AlertDialog alert = buildero.create();
                                                                    //TextView messageView = (TextView)alert.findViewById(android.R.id.message);
                                                                    //messageView.setGravity(Gravity.CENTER);

                                                                    alert.show();

                                                                    TextView messageView = (TextView)alert.findViewById(android.R.id.message);
                                                                    messageView.setGravity(Gravity.CENTER);
                                                                });
                                                                AlertDialog dialog1 = builder1.create();
                                                                dialog1.show();

                                                                // Buttons
                                                                Button buttonOK = dialog1.getButton(DialogInterface.BUTTON_POSITIVE);
                                                                buttonOK.setTextColor(ContextCompat.getColor(providerregisternew.this, R.color.colorPrimary));

                                                                Button buttonNo = dialog1.getButton(DialogInterface.BUTTON_NEGATIVE);
                                                                buttonNo.setTextColor(ContextCompat.getColor(providerregisternew.this, R.color.grey3));



                                                                //editjap
                                                                //Intent login = new Intent(customerregister.this, accountmenu.class);
                                                                //startActivity(login);


                                                            });

                                                            AlertDialog dialog = builder.create();
                                                            dialog.show();

                                                            // Buttons
                                                            Button buttonOK = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                                                            buttonOK.setTextColor(ContextCompat.getColor(providerregisternew.this, R.color.colorPrimary));

                                                            Button buttonNo = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                                                            buttonNo.setTextColor(ContextCompat.getColor(providerregisternew.this, R.color.grey3));

                                                            /*
                                                            builder.setMessage("Registered successfully!\nPlease verify your email");
                                                            builder.setCancelable(false);
                                                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                    dialog.dismiss();
                                                                    Intent login = new Intent(customerregister.this, accountmenu.class);
                                                                    startActivity(login);
                                                                }
                                                            });
                                                            AlertDialog alert = builder.create();
                                                            alert.show();

                                                             */

                                                        } else {
                                                            mDialog.dismiss();
                                                            ReusableCodeForAll.ShowAlert(providerregisternew.this, "Error", task.getException().getMessage());

                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });


                            } else {
                                mDialog.dismiss();
                                ReusableCodeForAll.ShowAlert(providerregisternew.this, "Error", task.getException().getMessage());
                            }

                        }
                    });
                }
            }

        });

    }

    String emailpattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public boolean isValid(){

        provideremail.setErrorEnabled(false);
        provideremail.setError("");
        providername.setErrorEnabled(false);
        providername.setError("");
        providerpass.setErrorEnabled(false);
        providerpass.setError("");
        providerphone.setErrorEnabled(false);
        providerphone.setError("");
        providercpass.setErrorEnabled(false);
        providercpass.setError("");
        providercapitals.setErrorEnabled(false);
        providercapitals.setError("");

        boolean isValidname = false, isValidemail = false, isvalidpassword = false, isvalidconfirmpassword = false, isvalid = false, isvalidmobileno = false,  isvalidcapitals = false;

        if (TextUtils.isEmpty(providerSname)) {
            providername.setErrorEnabled(true);
            providername.setError("Name is required");
        } else {
            isValidname = true;
        }
        if (TextUtils.isEmpty(providerSemail)) {
            provideremail.setErrorEnabled(true);
            provideremail.setError("Email is required");
        } else {
            if (providerSemail.matches(emailpattern)) {
                isValidemail = true;
            } else {
                provideremail.setErrorEnabled(true);
                provideremail.setError("Enter a valid Email Address");
            }

        }
        if (TextUtils.isEmpty(providerSpass)) {
            providerpass.setErrorEnabled(true);
            providerpass.setError("Password is required");
        } else {
            if (providerSpass.length() < 6) {
                providerpass.setErrorEnabled(true);
                providerpass.setError("Password must be 6 characters and above");
            } else {
                isvalidpassword = true;
            }
        }
        if (TextUtils.isEmpty(providerScpass)) {
            providercpass.setErrorEnabled(true);
            providercpass.setError("Confirm Password is required");
        } else {
            if (!providerSpass.equals(providerScpass)) {
                providercpass.setErrorEnabled(true);
                providercpass.setError("Password doesn't match");
            } else {
                isvalidconfirmpassword = true;
            }
        }
        if (TextUtils.isEmpty(providerSphone)) {
            providerphone.setErrorEnabled(true);
            providerphone.setError("Mobile number is required");
        } else {
            if (providerSphone.length() < 9) {
                providerphone.setErrorEnabled(true);
                providerphone.setError("Invalid mobile number");
            } else {
                isvalidmobileno = true;
            }
        }
        if (TextUtils.isEmpty(providerScapital)) {
            providercapitals.setErrorEnabled(true);
            providercapitals.setError("Field cannot be empty");
        } else{
            if (!"Malacca City".equals(providerScapital) && !"Alor Gajah".equals(providerScapital) && !"Jasin".equals(providerScapital) ) {
                providercapitals.setErrorEnabled(true);
                providercapitals.setError("Location out of area");
            } else {
                isvalidcapitals = true;
            }
        }


        isvalid = (isValidname && isValidemail && isvalidconfirmpassword && isvalidpassword && isvalidmobileno  && isvalidcapitals) ? true : false;
        return isvalid;

    }
}