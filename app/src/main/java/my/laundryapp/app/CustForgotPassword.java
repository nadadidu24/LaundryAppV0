package my.laundryapp.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class CustForgotPassword extends AppCompatActivity {

    TextInputLayout forgetpassword;
    String fp;
    Button Reset;
    FirebaseAuth FAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_forgot_password);

        try {

        forgetpassword = (TextInputLayout) findViewById(R.id.resetcustemail);
        Reset = (Button) findViewById(R.id.buttonresetcust);

        FAuth = FirebaseAuth.getInstance();
        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fp = forgetpassword.getEditText().getText().toString().trim();
                if (isValid()) {



                    final ProgressDialog mDialog = new ProgressDialog(CustForgotPassword.this);
                    mDialog.setCancelable(false);
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.setMessage("Logging in...");
                    mDialog.show();

                    FAuth.sendPasswordResetEmail(forgetpassword.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mDialog.dismiss();
                                ReusableCodeForAll.ShowAlert(CustForgotPassword.this, "", "Password has been sent to your Email");
                            } else {
                                mDialog.dismiss();
                                ReusableCodeForAll.ShowAlert(CustForgotPassword.this, "Error", task.getException().getMessage());
                            }
                        }
                    });
                }
            }
        });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    String emailpattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public boolean isValid() {
        forgetpassword.setErrorEnabled(false);
        forgetpassword.setError("");


        boolean isvalidemail = false, isvalid = false;

        if (TextUtils.isEmpty(fp)) {
            forgetpassword.setErrorEnabled(true);
            forgetpassword.setError("Email is required");
        } else {
            if (fp.matches(emailpattern)) {
                isvalidemail = true;
            } else {
                forgetpassword.setErrorEnabled(true);
                forgetpassword.setError("Enter a valid Email Address");
            }

        }

        isvalid = isvalidemail ? true : true;
        return isvalid;
    }


}