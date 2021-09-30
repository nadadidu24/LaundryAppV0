package my.laundryapp.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hbb20.CountryCodePicker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.reactivex.disposables.CompositeDisposable;
import my.laundryapp.app.Common.Common;
import my.laundryapp.app.Model.CustomerModel;
import my.laundryapp.app.providerCommon.providercommon;
import my.laundryapp.app.providerModel.providerLaundryProviderUserModel;
import my.laundryapp.app.providerModel.providerServerUserModel;


public class providerRegister extends AppCompatActivity {

    private static int APP_REQUEST_CODE = 7171;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private AlertDialog dialog1;
    //add on
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private DatabaseReference userRef;
    private List<AuthUI.IdpConfig> providers;

    private Place placeSelected;
    private AutocompleteSupportFragment place_fragment;
    private PlacesClient placesClient;
    private List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG);

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        if (listener != null)
            firebaseAuth.removeAuthStateListener(listener);
        compositeDisposable.clear();
        super.onStop();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_provider_register);
        init();
    }


    private void init() {

        Places.initialize(this,getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);

        //add on
        dialog1 = new SpotsDialog.Builder().setCancelable(false).setContext(this).build();

        providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder()
                .setDefaultCountryIso("MY").build());

        userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCES); //mcm kena tukar
        firebaseAuth = FirebaseAuth.getInstance();
        dialog1 = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        listener = firebaseAuthLocal -> {
            //add location permission
            Dexter.withActivity(this)
                    .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            //check ada luser log in or not
                            FirebaseUser user = firebaseAuthLocal.getCurrentUser();
                            if (user != null) {
                                //Already login
                                Toast.makeText(providerRegister.this, "Already login", Toast.LENGTH_SHORT).show();
                                checkServerUserFromFirebase(user);

                            } else {
                                //not login

                                phoneLogin();

                            }

                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toast.makeText(providerRegister.this, "Enable the permission", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        }
                    }).check();


        };


    }

    private void checkServerUserFromFirebase(FirebaseUser user) {
        dialog1.show();
        userRef.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(providerRegister.this, "u have already register", Toast.LENGTH_SHORT).show();
                            CustomerModel customerModel = snapshot.getValue(CustomerModel.class);
                            goToHomeActivity(customerModel);
                            //providerServerUserModel userModel = snapshot.getValue(providerServerUserModel.class);
                            //if(userModel.isActive())
                            //{
                            //  goToHomeActivity(userModel);
                            //}F
                            //else
                            //{
                            //if(userModel.getPhoneNumber().equals("phoneNumber"))
                            //{
                            //  dialog1.dismiss();
                            //  Toast.makeText(providerRegister.this, "u have already registered", Toast.LENGTH_SHORT).show();
                            // }

                            //}
                        } else {
                            //user not exist in database
                            //dialog1.dismiss();
                            showRegisterDialog(user);
                            //registerProvidernow(user);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dialog1.dismiss();
                        Toast.makeText(providerRegister.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.myFullscreenAlertDialogStyle);
        //builder.setTitle("Register");
        //builder.setMessage("Please Fill infromation . \nAdmin will accept sooner");

        View itemView = LayoutInflater.from(this).inflate(R.layout.provider_layout_register, null);
        EditText edt_name = (EditText) itemView.findViewById(R.id.edt_name);
        EditText edt_phone = (EditText) itemView.findViewById(R.id.edt_phone);
        //EditText edt_address = (EditText) itemView.findViewById(R.id.edt_address);
        TextView txt_address_detail = (TextView) itemView.findViewById(R.id.txt_address_detail);

        place_fragment = (AutocompleteSupportFragment)getSupportFragmentManager()
                .findFragmentById(R.id.places_autocomplete_fragment);
        place_fragment.setPlaceFields(placeFields);
        place_fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                placeSelected = place;
                txt_address_detail.setText(place.getAddress());

            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(providerRegister.this, ""+status.getStatusMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        //set data
        edt_phone.setText(user.getPhoneNumber());
        builder.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog1.dismiss();
                startActivity(new Intent(providerRegister.this, second.class));
                finish();
            }
        })
                .setPositiveButton("REGISTER", (dialog, which) -> {
                    if (placeSelected != null) {
                        if (TextUtils.isEmpty(edt_name.getText().toString())) {
                            Toast.makeText(providerRegister.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        CustomerModel customerModel = new CustomerModel();
                        customerModel.setCustUid(user.getUid());
                        customerModel.setName(edt_name.getText().toString());
                        customerModel.setPhoneNumber(edt_phone.getText().toString());
                        //customerModel.setAddress(edt_address.getText().toString());
                        customerModel.setAddress(txt_address_detail.getText().toString());
                        customerModel.setLat(placeSelected.getLatLng().latitude);
                        customerModel.setLng(placeSelected.getLatLng().longitude);

                        dialog1.show();

                        userRef.child(user.getUid())
                                .setValue(customerModel)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog1.dismiss();
                                        Toast.makeText(providerRegister.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog1.dismiss();
                                Toast.makeText(providerRegister.this, "Congratulations! register success!", Toast.LENGTH_SHORT).show();
                                goToHomeActivity(customerModel);
                            }
                        });
                    }
                    else{
                        Toast.makeText(this, "Please select address", Toast.LENGTH_SHORT).show();
                    }


                });


        builder.setView(itemView);

        androidx.appcompat.app.AlertDialog resgisterDialog = builder.create();
        resgisterDialog.setOnDismissListener(dialog -> {
            FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(place_fragment);
            fragmentTransaction.commit();

        });
        resgisterDialog.show();
        // Buttons
        Button buttonOK = resgisterDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonOK.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

        Button buttonNo = resgisterDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonNo.setTextColor(ContextCompat.getColor(this, R.color.grey3));

    }

    private void goToHomeActivity(CustomerModel customerModel) {

        FirebaseMessaging.getInstance().getToken()
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    dialog1.dismiss();
                    Common.currentUser = customerModel;
                    //providercommon.currentServerUser = serverUserModel;
                    startActivity(new Intent(this, Main4Activity.class));
                    finish();

                }).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                String token = task.getResult();
                dialog1.dismiss();
                Common.currentUser = customerModel;
                Common.updateToken(providerRegister.this, token);
                //providercommon.currentServerUser = serverUserModel;
                startActivity(new Intent(providerRegister.this, Main4Activity.class));
                finish();

            }
        });


    }

    private void phoneLogin() {


        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                //.setLogo(R.mipmap.ic_launcher_foreground)
                .setAvailableProviders(providers)
                .setTheme(R.style.LoginTheme)
                .build(), APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            } else {
                if (response == null) {
                    startActivity(new Intent(providerRegister.this, second.class));
                    finish();
                } else {
                    Toast.makeText(this, "Failed to sign in", Toast.LENGTH_SHORT).show();
                }


                //
            }
        }
    }


}