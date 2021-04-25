package my.laundryapp.app.ui.servicesdetail;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import my.laundryapp.app.Common.Common;
import my.laundryapp.app.Model.CommentModel;
import my.laundryapp.app.Model.CustomerModel;
import my.laundryapp.app.Model.LaundryServicesModel;
import my.laundryapp.app.R;
import my.laundryapp.app.ui.comments.CommentFragment;

public class serviceDetailFragment extends Fragment {

    //change
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userCustID;

    //change

    private ServiceDetailViewModel serviceDetailViewModel;

    private Unbinder unbinder;
    private android.app.AlertDialog waitingDialog;

    @BindView(R.id.img_services)
    ImageView img_services;
    @BindView(R.id.btnCart)
    CounterFab btnCart;
    @BindView(R.id.btn_rating)
    FloatingActionButton btn_rating;
    @BindView (R.id.services_name)
    TextView services_name;
    @BindView(R.id.services_description)
    TextView services_description;
    @BindView(R.id.service_price)
    TextView service_price;
    @BindView(R.id.number_button)
    ElegantNumberButton numberButton;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.btnShowComment)
    Button btnShowComment;

    @OnClick(R.id.btn_rating)
    void OnRatingButtonClick()
    {
        showDialogRating();
    }

    @OnClick(R.id.btnShowComment)
    void onShowCommentButtonClick(){
        CommentFragment commentFragment = CommentFragment.getInstance();
        commentFragment.show(getActivity().getSupportFragmentManager(),"CommentFragment");
    }


    private void showDialogRating() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Rating Services");
        builder.setMessage("Please rate the services");

        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_rating,null);

        RatingBar ratingBar= (RatingBar) itemView.findViewById(R.id.rating_bar);
        EditText edit_comment= (EditText) itemView.findViewById(R.id.edit_comment);

        builder.setView(itemView);

        builder.setNegativeButton("CANCEL", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.setPositiveButton("Okay", (dialog, which) -> {
            //changes try
            user = FirebaseAuth.getInstance().getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("Customer");
            userCustID = user.getUid();
            //

            //changes 3
            reference.child(userCustID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    CustomerModel userProfile = snapshot.getValue(CustomerModel.class);
                    if(userProfile != null){
                        CommentModel commentModel = new CommentModel();
                        commentModel.setName(userProfile.getName());
                        commentModel.setUid(userProfile.getEmail());
                        commentModel.setComment(edit_comment.getText().toString());
                        commentModel.setRatingValue(ratingBar.getRating());
                        Map<String,Object> serverTimeStamp = new HashMap<>();
                        serverTimeStamp.put("timeStamp", ServerValue.TIMESTAMP);
                        commentModel.setCommentTimeStamp(serverTimeStamp);

                        serviceDetailViewModel.setCommentModel(commentModel);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(),"Thank you!",Toast.LENGTH_SHORT).show();

                }
            });


            /*
            CommentModel commentModel = new CommentModel();
            commentModel.setName("custName");
            commentModel.setUid("Email");
            commentModel.setComment(edit_comment.getText().toString());
            commentModel.setRatingValue(ratingBar.getRating());
            Map<String,Object> serverTimeStamp = new HashMap<>();
            serverTimeStamp.put("timeStamp", ServerValue.TIMESTAMP);
            commentModel.setCommentTimeStamp(serverTimeStamp);

            serviceDetailViewModel.setCommentModel(commentModel);

             */


        });

        AlertDialog dialog =builder.create();
        dialog.show();

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        serviceDetailViewModel =
                new ViewModelProvider(this).get(ServiceDetailViewModel.class);
        View root = inflater.inflate(R.layout.fragment_service_detail, container, false);
        unbinder = ButterKnife.bind(this,root);
        initViews();
        serviceDetailViewModel.getMutableLiveDataFood().observe(getViewLifecycleOwner(), laundryServicesModel -> {
            displayInfo(laundryServicesModel);
        });
        serviceDetailViewModel.getMutableLiveDataComment().observe(getViewLifecycleOwner(),commentModel -> {
           submitRatingToFirebase(commentModel);
        });
        return root;

    }

    private void initViews() {
        waitingDialog = new SpotsDialog.Builder().setCancelable(false).setContext(getContext()).build();
    }

    private void submitRatingToFirebase(CommentModel commentModel) {
        waitingDialog.show();
        //first,submit to comment ref
        FirebaseDatabase.getInstance()
                .getReference("Comments")
                .child(Common.selectedService.getId())
                .push()
                .setValue(commentModel)
                .addOnCompleteListener(task -> {
                      if(task.isSuccessful())
                      {
                          //after submit to commentref,update value rating average in services
                          addRatingToServices(commentModel.getRatingValue());
                      }
                      waitingDialog.dismiss();
                });

    }

    private void addRatingToServices(float ratingValue) {
        FirebaseDatabase.getInstance()
                .getReference("Category")
                .child(Common.categorySelected.getCatalog_id()) //select category
        .child("services") //select array list
        .child(Common.selectedService.getKey())
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    LaundryServicesModel laundryServicesModel = snapshot.getValue(LaundryServicesModel.class);
                    laundryServicesModel.setKey(Common.selectedService.getKey());

                    //apply rating
                    if(laundryServicesModel.getRatingValue() == null)
                        laundryServicesModel.setRatingValue(0d);
                    if(laundryServicesModel.getRatingCount()==null)
                        laundryServicesModel.setRatingCount(0l);
                    double sumRating = laundryServicesModel.getRatingValue()+ratingValue;
                    long ratingCount = laundryServicesModel.getRatingCount()+1;
                    double result = sumRating/ratingCount;

                    Map<String,Object> updateData = new HashMap<>();
                    updateData.put("ratingValue",result);
                    updateData.put("ratingCount",ratingCount);

                    //update data in variable
                    laundryServicesModel.setRatingValue(result);
                    laundryServicesModel.setRatingCount(ratingCount);

                    snapshot.getRef()
                            .updateChildren(updateData)
                            .addOnCompleteListener(task -> {
                                waitingDialog.dismiss();
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(getContext(),"Thank you!",Toast.LENGTH_SHORT).show();
                                    Common.selectedService = laundryServicesModel;
                                    serviceDetailViewModel.setServiceModel(laundryServicesModel);
                                }
                            });
                }
                else
                    waitingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                waitingDialog.dismiss();
                Toast.makeText(getContext(),""+error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });//Because services items is array list so key is index of arraylist

    }

    private void displayInfo(LaundryServicesModel laundryServicesModel) {
        Glide.with(getContext()).load(laundryServicesModel.getImage()).into(img_services);
        services_name.setText(new StringBuilder(laundryServicesModel.getName()));
        services_description.setText(new StringBuilder(laundryServicesModel.getDescription()));
        service_price.setText(new StringBuilder(laundryServicesModel.getPrice().toString()));

        if(laundryServicesModel.getRatingValue() != null)
        ratingBar.setRating(laundryServicesModel.getRatingValue().floatValue());

        ((AppCompatActivity)getActivity())
                .getSupportActionBar()
                .setTitle(Common.selectedService.getName());

    }
}