package dev.lithmath.litmath;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Calendar;

public class Profile extends AppCompatActivity {

    TextView txtTitle, txtInstructions, txtCheers, txtPlaceholderFullName, txtPlaceholderEmail, txtPlaceholderPhone;
    EditText edtFullName, edtEmail, edtPhone;
    Button btnUpdate, btnSignOut;
    ImageView imageProfile;

    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edtFullName = findViewById(R.id.edt_fullname_profile);
        edtEmail = findViewById(R.id.edt_email_profile);
        edtPhone = findViewById(R.id.edt_phone_profile);
        btnUpdate = findViewById(R.id.btn_update_profile);
        btnSignOut = findViewById(R.id.btn_logout_profile);
        imageProfile = findViewById(R.id.imageProfile);

        txtCheers = findViewById(R.id.txt_cheers);
        txtTitle = findViewById(R.id.txt_title_profile);
        txtInstructions = findViewById(R.id.txt_instructions_profile);
        txtPlaceholderFullName = findViewById(R.id.txt_holder_fullname_profile);
        txtPlaceholderEmail = findViewById(R.id.txt_holder_email_profile);
        txtPlaceholderPhone = findViewById(R.id.txt_holder_phone_profile);

        // TODO: Set UI Config
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.primary_dark));
        }

        // TODO: Actions
        onUpdateUI();
        setUILng();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpdateProfile();
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogout();
            }
        });
    }

    private void onUpdateProfile() {
        String newFullName = edtFullName.getText().toString().trim();
        if (newFullName.length() > 0) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(newFullName).build();

            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Email is not empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void onLogout(){
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    private void onUpdateUI() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            String phone = user.getPhoneNumber();
            Uri image = user.getPhotoUrl();

            edtFullName.setText(name);
            edtEmail.setText(email);
            edtPhone.setText(phone);
            Glide.with(getApplicationContext()).load(image).into(imageProfile);
        }
    }

    private void onSetCheers(){
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if(timeOfDay >= 0 && timeOfDay < 12){
            txtCheers.setText(resources.getString(R.string.cheers_morning).length() > 0 ? resources.getString(R.string.cheers_morning) : "");
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            txtCheers.setText(resources.getString(R.string.cheers_afternoon).length() > 0 ? resources.getString(R.string.cheers_afternoon) : "");
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            txtCheers.setText(resources.getString(R.string.cheers_evening).length() > 0 ? resources.getString(R.string.cheers_evening) : "");
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            txtCheers.setText(resources.getString(R.string.cheers_night).length() > 0 ? resources.getString(R.string.cheers_night) : "");
        }
    }

    private void setUILng(){
        String defaultLng = LocaleHelper.getLanguage(Profile.this);
        context = LocaleHelper.setLocale(Profile.this, defaultLng);
        resources = context.getResources();
        txtTitle.setText(resources.getString(R.string.title_profile));
        txtInstructions.setText(resources.getString(R.string.instructions_profile));
        btnUpdate.setText(resources.getString(R.string.btn_update_profile));
        btnSignOut.setText(resources.getString(R.string.btn_sign_out));
        txtPlaceholderFullName.setText(resources.getString(R.string.edt_full_name_signup));
        txtPlaceholderEmail.setText(resources.getString(R.string.edt_email_signup));
        txtPlaceholderPhone.setText(resources.getString(R.string.edt_phone_signup));
        onSetCheers();
    }
}