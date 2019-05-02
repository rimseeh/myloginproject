package com.example.designandloginproject;

//import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.ImageFormat;
//import android.net.Uri;
//import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import com.ablanco.zoomy.Zoomy;
import com.android.volley.toolbox.NetworkImageView;
import com.example.designandloginproject.application.MyApplication;
import com.example.designandloginproject.network.ImageRequester;
import com.google.firebase.auth.FirebaseAuth;
//import android.util.Log;
//
//import com.example.designandloginproject.models.Accessory;
//import com.example.designandloginproject.models.ImageRequester;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//import com.squareup.picasso.Picasso;
//import com.squareup.picasso.RequestCreator;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationHost {

    private static final String TAG = "MainActivity";

//    private StorageReference mStorageReference;
//    private DatabaseReference mDatabaseReference;
//    ArrayList<Accessory> accessories;

    //    private ArrayList<String> pathArray;
//    private ArrayList<String> keysArray=new ArrayList<>();
//    private ArrayList<Uri> urlArray=new ArrayList<>();
//    private int array_position;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApplication myApplication = MyApplication.getInstance();
//        accessories = (ArrayList<Accessory>) Accessory.initAccessoryEntryList(getResources());
//        mStorageReference = FirebaseStorage.getInstance().getReference();
//        mDatabaseReference = FirebaseDatabase.getInstance().getReference("images");
//        pathArray = new ArrayList<>();
//        /checkFilePermissions();
//        addFilePaths();
//        for (array_position = 0; array_position < pathArray.size(); array_position++) {
//            String key = mDatabaseReference.push().getKey();
//            keysArray.add(key);
//            Uri uri = Uri.fromFile(new File(pathArray.get(array_position)));
//            StorageReference storageReference = mStorageReference.child("accessories/" + key + ".jpg");
//            storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
//                urlArray.add(uri1);
//                Log.d(TAG, "onCreate: " + uri1);
//            }));
//        }
//        int i=0;
//        for (Accessory accessory :accessories){
//            mDatabaseReference.child(keysArray.get(i)).setValue(accessory).addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess: "+accessory));
//            i++;
//        }
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new AccessoryGridFragment())
                    .commit();
        } else if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }


    }

//    private void addFilePaths() {
//        Log.d(TAG, "addFilePaths: Adding file paths.");
//        String path = System.getenv("EXTERNAL_STORAGE");
//        pathArray.add(path + "/Pictures/images/0-0.jpg");
//        pathArray.add(path + "/Pictures/images/1-0.jpg");
//        pathArray.add(path + "/Pictures/images/2-0.jpg");
//        pathArray.add(path + "/Pictures/images/3-0.jpg");
//        pathArray.add(path + "/Pictures/images/4-0.jpg");
//        pathArray.add(path + "/Pictures/images/5-0.jpg");
//        pathArray.add(path + "/Pictures/images/6-0.jpg");
//        loadImageFromStorage();
//    }
//
//    private void loadImageFromStorage() {
//        try {
//            String path = pathArray.get(array_position);
//            File f = new File(path, "");
//            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
//        } catch (FileNotFoundException e) {
//            Log.e(TAG, "loadImageFromStorage: FileNotFoundException: " + e.getMessage());
//        }
//
//    }
//
//    private void checkFilePermissions() {
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            int permissionCheck = MainActivity.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
//            permissionCheck += MainActivity.this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
//            if (permissionCheck != 0) {
//                this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1001); //Any number
//            }
//        } else {
//            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
//        }
//    }


    @Override
    public void navigateTo(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}
