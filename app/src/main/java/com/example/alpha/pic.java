package com.example.alpha;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import static com.example.alpha.MainActivity.refUsers;

/**
 * this activity uploads an image that the user chose from his gallery
 * (after getting permission to access his gallery)
 */
public class pic extends AppCompatActivity {

    public static FirebaseStorage FBST = FirebaseStorage.getInstance();
    public static StorageReference refStor=FBST.getReference();
    public static StorageReference refImages=refStor.child("images");

    int Gallery=1;
    AlertDialog ad;
    ImageView img;
    String uiduser="",imguser="empty";
    User user,userimage;

    public static FirebaseAuth refAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic);
        img=(ImageView)findViewById(R.id.img);

        FirebaseUser fbuser = refAuth.getCurrentUser();
        uiduser = fbuser.getUid();
        Query query = refUsers.orderByChild("uid").equalTo(uiduser);
        query.addListenerForSingleValueEvent(VELUser);
    }

    /**
     * this method reads the necessary information about the user to this activity
     */
    com.google.firebase.database.ValueEventListener VELUser = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dS) {
            if (dS.exists()) {
                for (DataSnapshot data : dS.getChildren()) {
                    user = data.getValue(User.class);
                    uiduser=user.getUid();
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    /**
     * this method gets the image's current status
     */
    com.google.firebase.database.ValueEventListener VELimage = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dSim) {
            if (dSim.exists()) {
                for(DataSnapshot dataim : dSim.getChildren()) {
                    userimage = dataim.getValue(User.class);
                    if (userimage.getImage().equals("empty")){
                        imguser="empty";
                    }
                    else {
                        imguser="checked";
                    }
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    /**
     * this function checks the user's image status from the Firebase Database
     * if there is no picture- it opens the gallery
     * if there is- it gives the user the option to replace it, delete it, or do nothing.
     * @param view
     */
    public void strg(View view) {
        Query q = refUsers.orderByChild("uid").equalTo(uiduser);
        q.addListenerForSingleValueEvent(VELimage);
        if (imguser.equals("empty")){
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, Gallery);
        }
        else {
            AlertDialog.Builder alertDialogB = new AlertDialog.Builder(this);
            alertDialogB.setTitle("what would you like to do with this image?");
            alertDialogB.setPositiveButton("delete", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int whichButton) {
                    img.setImageResource(R.drawable.alphapic);
                    imguser="empty";
                    refUsers.child(uiduser).child("image").removeValue();
                    refUsers.child(uiduser).child("image").setValue(imguser);
                    dialogInterface.dismiss();
                }
            });
            alertDialogB.setNegativeButton("replace", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int whichButton) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, Gallery);
                    dialogInterface.cancel();
                }
            });
            alertDialogB.setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int whichButton) {
                    dialogInterface.cancel();
                }
            });
            ad = alertDialogB.create();
            if(!pic.this.isFinishing())
            {
                ad.show();
            }
        }
    }

    /**
     * after the user chose an image from te gallery
     * this function updates the image status, and uploads the selected image file to the Firebase storage
     * @param requestCode the call sign of the intent that requested the results
     * @param resultCode a code that symnols the status of the result of this activity
     * @param data the data returned
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Gallery) {
                Uri file = data.getData();
                if (file != null) {
                        final ProgressDialog pd=ProgressDialog.show(this,"Upload image","Uploading...",true);
                        StorageReference refImg = refImages.child(uiduser+".jpg");
                        refImg.putFile(file)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        pd.dismiss();
                                        Toast.makeText(pic.this, "image uploaded", Toast.LENGTH_LONG).show();

                                        refUsers.child(uiduser).child("image").removeValue();
                                        refUsers.child(uiduser).child("image").setValue("checked");
                                        imguser="checked";


                                        try {
                                            download();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        pd.dismiss();
                                        Toast.makeText(pic.this, "upload failed", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                     else {
                    Toast.makeText(this, "please choose an image", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * this method downloads the chosen image from Firebase storage to a local file, and presents the image
     * @throws IOException
     */
    public void download() throws IOException{
        StorageReference refImg = refImages.child(uiduser+".jpg");

        final File localFile = File.createTempFile(uiduser,"jpg");
        refImg.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                String filePath = localFile.getPath();
                Bitmap bitmapImage = BitmapFactory.decodeFile(filePath);
                img.setImageBitmap(bitmapImage);
                img.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(pic.this, "image download failed", Toast.LENGTH_LONG).show();
            }
        });
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
