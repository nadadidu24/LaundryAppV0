package my.laundryapp.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class customerregister extends AppCompatActivity {

    TextInputLayout custcapital;
    AutoCompleteTextView custcapitaltext;

    ArrayList<String> arrayList_capitals;
    ArrayAdapter<String > arrayAdapter_capital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerregister);

        custcapital=(TextInputLayout)findViewById(R.id.custcapital);
        custcapitaltext=(AutoCompleteTextView)findViewById(R.id.custcapitaltext);

        arrayList_capitals=new ArrayList<>();
        arrayList_capitals.add("Alor Gajah");
        arrayList_capitals.add("Malacca City");
        arrayList_capitals.add("Jasin");

        arrayAdapter_capital=new ArrayAdapter<>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,arrayList_capitals);

        custcapitaltext.setAdapter(arrayAdapter_capital);

        custcapitaltext.setThreshold(1);



    }
}