package my.laundryapp.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class accountmenu extends AppCompatActivity {

    Button usermenu,emaillogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountmenu);

        usermenu = findViewById(R.id.registerbutton);
        emaillogin = findViewById(R.id.loginemail);

        usermenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(accountmenu.this, usermenu.class);
                startActivity(intent);
            }
        });

        emaillogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(accountmenu.this, customerloginemail.class);
                startActivity(intent);
            }
        });

    }
}