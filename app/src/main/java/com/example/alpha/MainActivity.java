package com.example.alpha;

/**
 * @author Shaked Dromi
 * @version alpha
 * @since 30/11/2020
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * this activity creats a new user in the Firebase (with mail, password and uid)
 */
public class MainActivity extends AppCompatActivity {
    String userMail="", userPass="", uid="";
    EditText mail,pass;
    private FirebaseAuth mAuth;
    Boolean stayConnect, firstRun=true,isUID=false, registered=false;
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    public static DatabaseReference refUsers= FBDB.getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        give each UI variable a value
         */
        mail=(EditText)findViewById(R.id.mail);
        pass=(EditText)findViewById(R.id.pass);
        mAuth = FirebaseAuth.getInstance();

        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        firstRun=settings.getBoolean("firstRun",true);
        stayConnect = false;

        /**
         * this method checks if this is the first run on the user's device
         * if so, it sends the user strait to the registration activity(main activity)
         * if not, it sends him to the location activity
         */
        if (firstRun) {
            isUID=false;
            onVerificationStateChanged();
            regOption();
        }
        else {
            isUID=true;
            registered = true;
            onVerificationStateChanged();
            Intent si = new Intent(MainActivity.this, location.class);
            startActivity(si);        }
    }

    private void regOption() {
        isUID=false;
        registered=false;
    }

    private void onVerificationStateChanged() {
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser){
    }

    /**
     * when register button is pressed, check if both fields are correct.
     * make a toast accordingly.
     * @param view
     */
    public void register(View view) {
            userMail=mail.getText().toString();
            userPass=pass.getText().toString();
            if (userMail.isEmpty()||userPass.isEmpty()){
                Toast.makeText(this, "please fill all the necessary details", Toast.LENGTH_SHORT).show();
            }
            else {
                mAuth.createUserWithEmailAndPassword(userMail, userPass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "registration completed",
                                    Toast.LENGTH_SHORT).show();

                            // Sign in success, update UI with the signed-in user's information
                            //   Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            uid = user.getUid();
                            updateUI(user);
                            String image="empty";
                          //  userdb = new User
                            if (!isUID) {
                                User userdb=new User(userMail, userPass, uid, image);
                                refUsers.child(uid).setValue(userdb);
                            }
                            Intent si = new Intent(MainActivity.this, location.class);
                            startActivity(si);
                        } else {
                            // If sign in fails, display a message to the user.
                            //    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
            }
        }
}
