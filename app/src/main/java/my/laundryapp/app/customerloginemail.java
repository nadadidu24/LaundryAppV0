package my.laundryapp.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;

public class customerloginemail extends AppCompatActivity {

    TextInputLayout email, pass;
    Button Signout;
    TextView Forgotpassword;
    TextView txt;
    FirebaseAuth FAuth;
    String em;
    String pwd;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerloginemail);

        try {
            email = (TextInputLayout) findViewById(R.id.logincustemail);
            pass = (TextInputLayout) findViewById(R.id.logincustpass);
            Signout = (Button) findViewById(R.id.buttonlogincust);
            txt = (TextView) findViewById(R.id.registerheretxt);
            Forgotpassword = (TextView) findViewById(R.id.clickhereinfo);



            FAuth = FirebaseAuth.getInstance();

            Signout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    em = email.getEditText().getText().toString().trim();
                    pwd = pass.getEditText().getText().toString().trim();
                    if (isValid()) {

                        final ProgressDialog mDialog = new ProgressDialog(customerloginemail.this);
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.setCancelable(false);
                        mDialog.setMessage("Logging in...");
                        mDialog.show();
                        FAuth.signInWithEmailAndPassword(em, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    mDialog.dismiss();
                                    if (FAuth.getCurrentUser().isEmailVerified()) {
                                        FAuth = FirebaseAuth.getInstance();

                                        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getUid() + "/Role");
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                String role = dataSnapshot.getValue(String.class);
                                                if (role.equals("Customer")) {

                                                    mDialog.dismiss();
                                                    Toast.makeText(customerloginemail.this, "You are logged in", Toast.LENGTH_SHORT).show();
                                                    Intent z = new Intent(customerloginemail.this, Main4Activity.class); //tukar this nanti
                                                    startActivity(z);
                                                    finish();
                                                }
                                                if (role.equals("LaundryProvider")) {
                                                    mDialog.dismiss();
                                                    Toast.makeText(customerloginemail.this, "You are logged in", Toast.LENGTH_SHORT).show();
                                                    Intent z = new Intent(customerloginemail.this, ProviderActivity.class); //tukar this nanti
                                                    startActivity(z);
                                                    finish();
                                                }
                                                if (role.equals("LaundryRunner")) {
                                                    mDialog.dismiss();
                                                    Toast.makeText(customerloginemail.this, "You are logged in", Toast.LENGTH_SHORT).show();
                                                    Intent z = new Intent(customerloginemail.this, ProviderActivity.class); //tukar this nanti
                                                    startActivity(z);
                                                    finish();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Toast.makeText(customerloginemail.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();

                                            }
                                        });


                                        //mDialog.dismiss();
                                        //Toast.makeText(customerloginemail.this, "You are logged in", Toast.LENGTH_SHORT).show();
                                        //Intent z = new Intent(customerloginemail.this, ProviderActivity.class); //tukar this nanti
                                        //startActivity(z);
                                        //finish();


                                    } else {
                                        ReusableCodeForAll.ShowAlert(customerloginemail.this, "", "Please Verify your Email");
                                    }

                                } else {

                                    mDialog.dismiss();
                                    ReusableCodeForAll.ShowAlert(customerloginemail.this, "Error", task.getException().getMessage());
                                }
                            }
                        });

                    }
                }
            });

            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent Register = new Intent(customerloginemail.this, usermenu.class);
                    startActivity(Register);
                    finish();

                }
            });

            Forgotpassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent a = new Intent(customerloginemail.this, CustForgotPassword.class);
                    startActivity(a);
                    finish();

                }
            });


        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    String emailpattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public boolean isValid() {
        email.setErrorEnabled(false);
        email.setError("");
        pass.setErrorEnabled(false);
        pass.setError("");

        boolean isvalidemail = false, isvalidpassword = false, isvalid = false;

        if (TextUtils.isEmpty(em)) {
            email.setErrorEnabled(true);
            email.setError("Email is required");
        } else {
            if (em.matches(emailpattern)) {
                isvalidemail = true;
            } else {
                email.setErrorEnabled(true);
                email.setError("Enter a valid Email Address");
            }

        }
        if (TextUtils.isEmpty(pwd)) {
            pass.setErrorEnabled(true);
            pass.setError("Password is required");
        } else {
            isvalidpassword = true;
        }
        isvalid = (isvalidemail && isvalidpassword) ? true : false;
        return isvalid;
    }

}