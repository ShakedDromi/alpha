package com.example.alpha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

/**
 * this activity sends sms to the written number (which the user chose), a message (that the user wrote).
 */
public class message extends AppCompatActivity {
    final int SEND_SMS_PERMISSION_REQUEST_CODE = 55555;
    Button sendBtn;
    EditText num, msg;
    String phoneNo = "";
    String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        /*
        give each UI variable a value
         */
        num = (EditText) findViewById(R.id.num);
        msg = (EditText) findViewById(R.id.msg);
        sendBtn = (Button) findViewById(R.id.sndbtn);

        /**
         * this method checks for permission to send a message
         */
        sendBtn.setEnabled(false);
        if (checkPermission(Manifest.permission.SEND_SMS)) {
            sendBtn.setEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * when button is clicked, it checks if the data the user inserted is correct and if nothing is missing
     * the method brings up toast accordingly
     * @param v
     */
    public void onSend(View v) {
        phoneNo = num.getText().toString();
        message = msg.getText().toString();

        if ((phoneNo.length() != 10) || (!phoneNo.substring(0, 2).equals("05")) || (Pattern.matches("[a-aZ-Z]+", phoneNo)) || (message.equals(""))) {
            //num.setError("invalid phone number");
          Toast.makeText(this, "invalid phone number or text", Toast.LENGTH_SHORT).show();
            return;}

        if(checkPermission(Manifest.permission.SEND_SMS)){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo, null, message, null, null);

        Toast.makeText(this, "SMS sent", Toast.LENGTH_SHORT).show();
    } else{
        Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
    }}


    /**
     * this method checks for permission to send a message
     */
    public boolean checkPermission(String permission){
        int check=ContextCompat.checkSelfPermission(this,permission);
        return(check==PackageManager.PERMISSION_GRANTED);
    }


    /**
     * this function creates the menu options
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu (Menu menu){

        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    /**
     * this function gets the user's choice from the menu and sends him to the appropriate activity
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected (MenuItem item){
        String st = item.getTitle().toString();
        if (st.equals("location")) {
            Intent si = new Intent(this, location.class);
            startActivity(si);
        }
        if (st.equals("message")){
            Intent si = new Intent(this, message.class);
            startActivity(si);
        }
        if (st.equals("image")){
            Intent si = new Intent(this, pic.class);
            startActivity(si);
        }
        return true;
    }
}
