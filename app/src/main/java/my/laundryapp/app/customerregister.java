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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.HashMap;

public class customerregister extends AppCompatActivity {

    TextInputLayout custname,custemail,custphone,custcapitals,pass,cpass;
    Button signup;
    CountryCodePicker ccp;
    FirebaseAuth FAuthCust;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    String customername,customeremail,customerphone,customercapital,customerpass,customercpass,role="Customer";

    AutoCompleteTextView custcapitaltext;

    ArrayList<String> arrayList_capitals;
    ArrayAdapter<String > arrayAdapter_capital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerregister);

        custname = (TextInputLayout) findViewById(R.id.custname);
        custemail = (TextInputLayout) findViewById(R.id.custemail);
        custphone = (TextInputLayout) findViewById(R.id.Mobileno);
        pass = (TextInputLayout) findViewById(R.id.custpass);
        cpass = (TextInputLayout) findViewById(R.id.custpass2);
        custcapitals = (TextInputLayout) findViewById(R.id.custcapital);
        custcapitaltext = (AutoCompleteTextView) findViewById(R.id.custcapitaltext);
        signup = (Button) findViewById(R.id.buttonsignupcust);
        ccp = (CountryCodePicker) findViewById(R.id.CountryCode);

        arrayList_capitals = new ArrayList<>();
        arrayList_capitals.add("Alor Gajah");
        arrayList_capitals.add("Malacca City");
        arrayList_capitals.add("Jasin");

        arrayAdapter_capital = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, arrayList_capitals);

        custcapitaltext.setAdapter(arrayAdapter_capital);

        custcapitaltext.setThreshold(1);

        databaseReference = firebaseDatabase.getInstance().getReference("Customer");
        FAuthCust = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                customername = custname.getEditText().getText().toString().trim();
                customeremail = custemail.getEditText().getText().toString().trim();
                customerphone = custphone.getEditText().getText().toString().trim();
                customerpass = pass.getEditText().getText().toString().trim();
                customercpass = cpass.getEditText().getText().toString().trim();
                customercapital = custcapitals.getEditText().getText().toString().trim();

                if (isValid()) {

                    final ProgressDialog mDialog = new ProgressDialog(customerregister.this);
                    mDialog.setCancelable(false);
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.setMessage("Registering please wait...");
                    mDialog.show();

                    FAuthCust.createUserWithEmailAndPassword(customeremail, customerpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                                        hashMappp.put("Capital", customercapital);
                                        hashMappp.put("ConfirmPassword", customercpass);
                                        hashMappp.put("Email", customeremail);
                                        hashMappp.put("Name", customername);
                                        hashMappp.put("PhoneNumber", customerphone);
                                        hashMappp.put("Password", customerpass);
                                        hashMappp.put("custUid",useridd);
                                        firebaseDatabase.getInstance().getReference("Customer")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(hashMappp).addOnCompleteListener(new OnCompleteListener<Void>() {

                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mDialog.dismiss();

                                                FAuthCust.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            //adik edit
                                                            AlertDialog.Builder buildero = new AlertDialog.Builder(customerregister.this);

                                                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(customerregister.this, R.style.myFullscreenAlertDialogStyle);



                                                            View view = LayoutInflater.from(customerregister.this).inflate(R.layout.layout_successfull_register,null);

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
                                                                        Intent login = new Intent(customerregister.this, accountmenu.class);
                                                                        startActivity(login);
                                                                    }
                                                                });
                                                                AlertDialog alert = buildero.create();
                                                                //TextView messageView = (TextView)alert.findViewById(android.R.id.message);
                                                                //messageView.setGravity(Gravity.CENTER);

                                                                alert.show();

                                                                TextView titleView = (TextView)alert.findViewById(customerregister.this.getResources().getIdentifier("alertTitle", "id", "android"));
                                                                if (titleView != null) {
                                                                    titleView.setGravity(Gravity.CENTER);
                                                                }
                                                                TextView messageView = (TextView)alert.findViewById(android.R.id.message);
                                                                messageView.setGravity(Gravity.CENTER);





                                                            }).setPositiveButton("Yes, thankyou", (dialog, which) -> {
                                                                //Toast.makeText(getContext(), "Coming soon features!", Toast.LENGTH_SHORT).show();

                                                                android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(customerregister.this, R.style.myFullscreenAlertDialogStyle);



                                                                View view1 = LayoutInflater.from(customerregister.this).inflate(R.layout.layout_tutorial,null);

                                                                builder1.setView(view1);

                                                                builder1.setPositiveButton("Close", (dialog1, which1) -> {
                                                                    dialog1.dismiss();


                                                                    buildero.setMessage("Please verify your email before login");
                                                                    buildero.setCancelable(false);
                                                                    buildero.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {

                                                                            dialog.dismiss();
                                                                            Intent login = new Intent(customerregister.this, accountmenu.class);
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
                                                                buttonOK.setTextColor(ContextCompat.getColor(customerregister.this, R.color.colorPrimary));

                                                                Button buttonNo = dialog1.getButton(DialogInterface.BUTTON_NEGATIVE);
                                                                buttonNo.setTextColor(ContextCompat.getColor(customerregister.this, R.color.grey));



                                                                //editjap
                                                                //Intent login = new Intent(customerregister.this, accountmenu.class);
                                                                //startActivity(login);


                                                            });

                                                            AlertDialog dialog = builder.create();
                                                            dialog.show();

                                                            // Buttons
                                                            Button buttonOK = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                                                            buttonOK.setTextColor(ContextCompat.getColor(customerregister.this, R.color.colorPrimary));

                                                            Button buttonNo = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                                                            buttonNo.setTextColor(ContextCompat.getColor(customerregister.this, R.color.grey));

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
                                                            ReusableCodeForAll.ShowAlert(customerregister.this, "Error", task.getException().getMessage());

                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });


                            } else {
                                mDialog.dismiss();
                                ReusableCodeForAll.ShowAlert(customerregister.this, "Error", task.getException().getMessage());
                            }

                        }
                    });
                }
            }

        });
    }
    String emailpattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public boolean isValid(){

        custemail.setErrorEnabled(false);
        custemail.setError("");
        custname.setErrorEnabled(false);
        custname.setError("");
        pass.setErrorEnabled(false);
        pass.setError("");
        custphone.setErrorEnabled(false);
        custphone.setError("");
        cpass.setErrorEnabled(false);
        cpass.setError("");
        custcapitals.setErrorEnabled(false);
        custcapitals.setError("");

        boolean isValidname = false, isValidemail = false, isvalidpassword = false, isvalidconfirmpassword = false, isvalid = false, isvalidmobileno = false,  isvalidcapitals = false;

        if (TextUtils.isEmpty(customername)) {
            custname.setErrorEnabled(true);
            custname.setError("Name is required");
        } else {
            isValidname = true;
        }
        if (TextUtils.isEmpty(customeremail)) {
            custemail.setErrorEnabled(true);
            custemail.setError("Email is required");
        } else {
            if (customeremail.matches(emailpattern)) {
                isValidemail = true;
            } else {
                custemail.setErrorEnabled(true);
                custemail.setError("Enter a valid Email Address");
            }

        }
        if (TextUtils.isEmpty(customerpass)) {
            pass.setErrorEnabled(true);
            pass.setError("Password is required");
        } else {
            if (customerpass.length() < 6) {
                pass.setErrorEnabled(true);
                pass.setError("Password must be 6 characters and above");
            } else {
                isvalidpassword = true;
            }
        }
        if (TextUtils.isEmpty(customercpass)) {
            cpass.setErrorEnabled(true);
            cpass.setError("Confirm Password is required");
        } else {
            if (!customerpass.equals(customercpass)) {
                cpass.setErrorEnabled(true);
                cpass.setError("Password doesn't match");
            } else {
                isvalidconfirmpassword = true;
            }
        }
        if (TextUtils.isEmpty(customerphone)) {
            custphone.setErrorEnabled(true);
            custphone.setError("Mobile number is required");
        } else {
            if (customerphone.length() < 9) {
                custphone.setErrorEnabled(true);
                custphone.setError("Invalid mobile number");
            } else {
                isvalidmobileno = true;
            }
        }
        if (TextUtils.isEmpty(customercapital)) {
            custcapitals.setErrorEnabled(true);
            custcapitals.setError("Field cannot be empty");
        } else{
            if (!"Malacca City".equals(customercapital) && !"Alor Gajah".equals(customercapital) && !"Jasin".equals(customercapital) ) {
                custcapitals.setErrorEnabled(true);
                custcapitals.setError("Location out of area");
            } else {
                isvalidcapitals = true;
            }
        }


        isvalid = (isValidname && isValidemail && isvalidconfirmpassword && isvalidpassword && isvalidmobileno  && isvalidcapitals) ? true : false;
        return isvalid;

    }

}