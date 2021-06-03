package my.laundryapp.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;
import my.laundryapp.app.providerCommon.providercommon;
import my.laundryapp.app.providerModel.providerLaundryProviderUserModel;
import my.laundryapp.app.providerModel.providerServerUserModel;


public class providerRegister extends AppCompatActivity {

    private static int APP_REQUEST_CODE = 7171;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private AlertDialog dialog1;
    private DatabaseReference serverRef;
    private List<AuthUI.IdpConfig> providers;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        if(listener !=null)
            firebaseAuth.removeAuthStateListener(listener);
        super.onStop();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_provider_register);
        init();
    }





    private void init() {
        providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());

        serverRef = FirebaseDatabase.getInstance().getReference(providercommon.SERVER_REF); //mcm kena tukar
        firebaseAuth = FirebaseAuth.getInstance();
        dialog1 = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        listener = firebaseAuthLocal ->{
            //check ada luser log in or not
            FirebaseUser user = firebaseAuthLocal.getCurrentUser();
            if(user != null)
            {
               //check user from firebase
                checkServerUserFromFirebase(user);

            }
            else
            {
                phoneLogin();
                
            }


        };


    }

    private void checkServerUserFromFirebase(FirebaseUser user) {
        dialog1.show();
        serverRef.child(user.getPhoneNumber())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            providerLaundryProviderUserModel userModel = snapshot.getValue(providerLaundryProviderUserModel.class);
                            //providerServerUserModel userModel = snapshot.getValue(providerServerUserModel.class);
                            //if(userModel.isActive())
                            //{
                              //  goToHomeActivity(userModel);
                            //}
                            //else
                            //{
                            if(userModel.getPhoneNumber().equals("phoneNumber"))
                            {
                                dialog1.dismiss();
                                Toast.makeText(providerRegister.this, "u have already registered", Toast.LENGTH_SHORT).show();
                            }

                            //}
                        }
                        else
                        {
                            //user not exist in database
                            dialog1.dismiss();
                            //showRegisterDialog(user);
                            registerProvidernow(user);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dialog1.dismiss();
                        Toast.makeText(providerRegister.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void registerProvidernow(FirebaseUser user) {


        String phonenumber = user.getPhoneNumber();
        Intent b = new Intent(providerRegister.this, providerregisternew.class);
        b.putExtra("phonenumber", phonenumber);
        startActivity(b);
    }

    private void showRegisterDialog(FirebaseUser user) {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Register");
        builder.setMessage("Please Fill infromation . \nAdmin will accept sooner");

        View itemView = LayoutInflater.from(this).inflate(R.layout.provider_layout_register,null);
        EditText edt_name = (EditText) itemView.findViewById(R.id.edt_name);
        EditText edt_phone = (EditText) itemView.findViewById(R.id.edt_phone);




        //set data
        edt_phone.setText(user.getPhoneNumber());
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
                .setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(TextUtils.isEmpty(edt_name.getText().toString()))
                        {
                            Toast.makeText(providerRegister.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        providerServerUserModel serverUserModel = new providerServerUserModel();
                        serverUserModel.setUid(user.getUid());
                        serverUserModel.setName(edt_name.getText().toString());
                        serverUserModel.setPhone(edt_phone.getText().toString());
                        serverUserModel.setActive(false); //default failed,manually change in firebase
                        dialog1.show();

                        serverRef.child(serverUserModel.getUid())
                                .setValue(serverUserModel)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog1.dismiss();
                                        Toast.makeText(providerRegister.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog1.dismiss();
                                Toast.makeText(providerRegister.this, "Congratulations! register success!", Toast.LENGTH_SHORT).show();
                                //goToHomeActivity(serverUserModel);
                            }
                        });

                    }
                });

        builder.setView(itemView);

        androidx.appcompat.app.AlertDialog resgisterDialog = builder.create();
        resgisterDialog.show();

    }

    private void goToHomeActivity(providerLaundryProviderUserModel providerLaundryProviderUserModel) {

        dialog1.dismiss();
        providercommon.currentLaundryProviderUser = providerLaundryProviderUserModel;
        //providercommon.currentServerUser = serverUserModel;
        startActivity(new Intent(this,ProviderActivity.class));
        finish();

    }

    private void phoneLogin() {
        startActivityForResult(AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .build(),APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == APP_REQUEST_CODE)
        {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK)
            {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            }
            else
            {
                Toast.makeText(this,"Failed to sign in",Toast.LENGTH_SHORT).show();
            }
        }
    }

}