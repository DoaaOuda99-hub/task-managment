package com.example.timemanagment.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timemanagment.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

public class AccountSettingsActivity extends AppCompatActivity {

    ImageView img_profile;

    TextView tv_email, tv_username;
    final int IMAGE_REQUEST_CODE = 100;

    //fire base
    FirebaseAuth auth ;
    FirebaseUser user ;

    FirebaseFirestore db;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String userEmail;
    String user_id;
    //for saving settings
    SharedPreferences settingsPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingsPreferences = getSharedPreferences("settings", MODE_PRIVATE);;
        if(settingsPreferences.getString("lang", "en").equals("ar"))
            setLocale(settingsPreferences.getString("lang", "en"));

        setContentView(R.layout.activity_account_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title);
        title.setText(R.string.account);
        setTitle("");
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        user= auth.getCurrentUser();

        db=FirebaseFirestore.getInstance();

        preferences = getSharedPreferences("user info", Context.MODE_PRIVATE);
        editor =preferences.edit();

        userEmail = preferences.getString("email", null);
        user_id = preferences.getString("user_id", null);

        findViews();
        clickListeners();
    }

    public void findViews(){
        img_profile = findViewById(R.id.img_profile);
        tv_username = findViewById(R.id.txt_username);
        tv_email = findViewById(R.id.txt_email);

        tv_username.setText(preferences.getString("username", "Username"));
        tv_email.setText(preferences.getString("email", "Email"));

        if (preferences.getString("profileImg","").equals("default")||
                preferences.getString("profileImg","").equals("")||
                preferences.getString("profileImg","").equals(null))
            img_profile.setImageResource(R.drawable.img_profile);
        else
            img_profile.setImageBitmap(BitmapFactory.decodeFile(preferences.getString("profileImg","ProfileImg")));
    }

    //..........click listener methods.............//
    private void clickListeners(){
    }


    public void changeProfileImage(View view) {

        final Dialog dialog = new Dialog(AccountSettingsActivity.this);
        dialog.setContentView(R.layout.dialog_choose_image);
        dialog.show();


        RadioButton btn_fromCamera = dialog.findViewById(R.id.rb_from_camera);
        RadioButton btn_from_gallery = dialog.findViewById(R.id.rb_from_gallery);

        btn_from_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromGallery();
                dialog.dismiss();

            }
        });

        btn_fromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromCamera();
                dialog.dismiss();
            }
        });

    }

    Bitmap bitmap;
    String imagePath;

    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            // Let's read picked image data - its URI
            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pickedImage);
                db.collection("users").document(user_id).update("profileImg",imagePath).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(AccountSettingsActivity.this, imagePath+"", Toast.LENGTH_SHORT).show();
                            img_profile.setImageBitmap(bitmap);
                            editor.putString("profileImg", imagePath);
                            editor.apply();

                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }

            // Do something with the bitmap


            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close();
        }else if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            db.collection("users").document(user_id).update("profileImg", saveImageFromCamera(bitmap)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        // Toast.makeText(getContext(), imagePath+"", Toast.LENGTH_SHORT).show();
                        img_profile.setImageBitmap(bitmap);

                    }
                }
            });
        }
    }


    public  void fromGallery(){
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 2);

    }

    private void fromCamera(){
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }


    public String saveImageFromCamera(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + "/timeManagement");
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(AccountSettingsActivity.this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    public void changeEmail(View view){
        Dialog dialog = new Dialog(AccountSettingsActivity.this);
        dialog.setContentView(R.layout.dialog_update_email);
        dialog.show();

        EditText et_email = dialog.findViewById(R.id.et_field);
        Button btn_save = dialog.findViewById(R.id.btn_save);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = et_email.getText().toString();
                user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(AccountSettingsActivity.this, "successfully updated user", Toast.LENGTH_SHORT).show();
                            tv_email.setText(email);
                            editor.putString("email", et_email.getText().toString());
                            editor.apply();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AccountSettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void changeUsername(View view){
        Dialog dialog = new Dialog(AccountSettingsActivity.this);
        dialog.setContentView(R.layout.dialog_update_email);
        dialog.show();

        EditText et_username = dialog.findViewById(R.id.et_field);
        Button btn_save = dialog.findViewById(R.id.btn_save);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = et_username.getText().toString();
                db.collection("users").document(user_id).update("username", username)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(AccountSettingsActivity.this, "username updated", Toast.LENGTH_SHORT).show();
                            tv_username.setText(username);
                            editor.putString("username", username);
                            editor.apply();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AccountSettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    public void changePassword(View view){
        Dialog dialog = new Dialog(AccountSettingsActivity.this);
        dialog.setContentView(R.layout.dialog_change_pass);
        dialog.show();

        EditText et_oldPass = dialog.findViewById(R.id.et_old_pass);
        EditText et_newPass = dialog.findViewById(R.id.et_new_pass);
        EditText et_confirmPass = dialog.findViewById(R.id.et_confirm_pass);
        Button btn_save = dialog.findViewById(R.id.btn_save);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_oldPass.getText().toString().equals("")||et_newPass.getText().toString().equals("")||et_confirmPass.getText().toString().equals("")){
                    Toast.makeText(AccountSettingsActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }
                //  else if(et_newPass.getText().toString().equals(et_confirmPass.getText().toString()))
                //    Toast.makeText(AccountSettingsActivity.this,
                //          "new password and confirmed password not match", Toast.LENGTH_LONG).show();
                else{
                    //do saving pass operation
                    // Get auth credentials from the user for re-authentication. The example below shows
                    // email and password credentials but there are multiple possible providers,
                    // such as GoogleAuthProvider or FacebookAuthProvider.
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(userEmail, et_oldPass.getText().toString());

                    // Prompt the user to re-provide their sign-in credentials
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        user.updatePassword(et_newPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(AccountSettingsActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                                                    editor.putString("password", et_newPass.getText().toString());
                                                    editor.apply();
                                                } else {
                                                    Toast.makeText(AccountSettingsActivity.this, "Error password not updated", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(AccountSettingsActivity.this, "Error auth failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    dialog.dismiss();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void logout(View view){
        new AlertDialog.Builder(AccountSettingsActivity.this)
                .setMessage(R.string.logout_confirm)

                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with logout operation
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(AccountSettingsActivity.this,LoginActivity.class);
                        startActivity(intent);


                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .show();
    }


    /*
     * for profile image
     * */
    // method for bitmap to base64
    public static String encodeToBase64(Bitmap image) {
        // Bitmap image = image;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] b = byteArrayOutputStream.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    // method for base64 to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    //for language configuration
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}
