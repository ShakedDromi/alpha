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

        num = (EditText) findViewById(R.id.num);
        msg = (EditText) findViewById(R.id.msg);
        sendBtn = (Button) findViewById(R.id.sndbtn);

        sendBtn.setEnabled(false);
        if (checkPermission(Manifest.permission.SEND_SMS)) {
            sendBtn.setEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
        }
    }

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


    public boolean checkPermission(String permission){
        int check=ContextCompat.checkSelfPermission(this,permission);
        return(check==PackageManager.PERMISSION_GRANTED);
    }

    public boolean onCreateOptionsMenu (Menu menu){

        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    public boolean onOptionsItemSelected (MenuItem item){
        String st = item.getTitle().toString();
        if (st.equals("registration")) {
            Intent si = new Intent(this, MainActivity.class);
            startActivity(si);
        }
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
