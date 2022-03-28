package dev.lithmath.litmath.auth;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dev.lithmath.litmath.BuildConfig;
import dev.lithmath.litmath.Crypto;
import dev.lithmath.litmath.LocaleHelper;
import dev.lithmath.litmath.MainActivity;
import dev.lithmath.litmath.R;
import dev.lithmath.litmath.Utils;

public class SignUp extends AppCompatActivity {

    String uriProfile = "https://firebasestorage.googleapis.com/v0/b/mobileapps-leal.appspot.com/o/user.png?alt=media&token=d768a211-5907-4807-bada-0a039105b804";
    TextView txtTitle, txtSubtitle, txtInstructions, txtUserType, txtBack;
    TextView txtHoldelReferral, txtHolderFullName, txtHolderEmail, txtHolderPassword, txtHolderRepeatPassword, txtHolderPhone, txtHolderTypeClient;
    EditText edtFullName, edtPassword, edtRepeatPassword, edtEmail, edtPhone, edtReferral;
    CheckBox checkTycos;
    Button btnSignUp;
    LinearLayoutCompat layout;
    ProgressBar progress;
    Context context;
    Resources resources;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // TODO: Set UI Config
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.primary_dark));
        }

        // TODO: Reference UI
        txtTitle = findViewById(R.id.txt_title_signup);
        txtSubtitle = findViewById(R.id.txt_subtitle_signup);
        txtInstructions = findViewById(R.id.txt_instructions_signup);
        txtUserType = findViewById(R.id.txt_type_client_signup);
        txtBack = findViewById(R.id.txt_back_signup);
        txtHolderFullName = findViewById(R.id.txt_holder_fullname_signup);
        txtHolderEmail = findViewById(R.id.txt_holder_email_signup);
        txtHolderPassword = findViewById(R.id.txt_holder_password_signup);
        txtHolderRepeatPassword = findViewById(R.id.txt_holder_repeat_password_signup);
        txtHolderPhone = findViewById(R.id.txt_holder_phone_signup);
        txtHolderTypeClient = findViewById(R.id.txt_holder_user_type_signup);
        txtHoldelReferral = findViewById(R.id.txt_holder_referral_code_signup);
        edtFullName = findViewById(R.id.edt_fullname_signup);
        edtPassword = findViewById(R.id.edt_password_signup);
        edtRepeatPassword = findViewById(R.id.edt_repeat_password_signup);
        edtEmail = findViewById(R.id.edt_email_signup);
        edtPhone = findViewById(R.id.edt_phone_signup);
        edtReferral = findViewById(R.id.edt_referral_code_signup);
        checkTycos = findViewById(R.id.check_tycos);
        btnSignUp = findViewById(R.id.btn_signup);
        layout = findViewById(R.id.layout_signup);
        progress = findViewById(R.id.progress_signup);

        // TODO: Initialize context app
        mAuth = FirebaseAuth.getInstance();

        // TODO: Debug Config
        if (BuildConfig.DEBUG) {
            //edtFullName.setText("Eduardo Leal");
            //edtPassword.setText("123456");
            //edtRepeatPassword.setText("123456");
            //edtPhone.setText("1234567890");
        }

        // TODO: Actions
        onUpdateUI(1);
        setUILng();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkTycos.isChecked()) {
                    onRegisterUser();
                } else {
                    onShowSnack("On continue please check terms");
                }
            }
        });

        txtUserType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangeTypeClient();
            }
        });

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }

    private void onRegisterUser() {
        String fullName, email, password, repeatPassword, phone;

        fullName = edtFullName.getText().toString().trim();
        email = edtEmail.getText().toString().trim();
        password = edtPassword.getText().toString().trim();
        repeatPassword = edtRepeatPassword.getText().toString().trim();
        phone = edtPhone.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) && fullName.length() < 5) {
            onShowSnack("Enter a full name");
            return;
        }

        if (TextUtils.isEmpty(email) && !Utils.isEmailValid(email)) {
            onShowSnack("Enter a valid email");
            return;
        }

        if (TextUtils.isEmpty(password) && password.length() < 6) {
            onShowSnack("Enter a valid password, minimum 6 characters.");
            return;
        }

        if (!password.equals(repeatPassword)) {
            onShowSnack("Passwords do not match");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            onShowSnack("Enter a valid phone");
            return;
        }

        progress.setVisibility(View.VISIBLE);
        btnSignUp.setVisibility(View.GONE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    user.sendEmailVerification();
                    onUserDataRegister(user);
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException){
                        onShowSnack("Email already in use");
                    }
                    else{
                        onShowSnack("Error during sign up, try again later.");
                    }
                    progress.setVisibility(View.GONE);
                    btnSignUp.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void onUserDataRegister(FirebaseUser user) {
        String id, fullName, email, password, phone, clientType, referralCode;

        id = user.getUid();
        fullName = edtFullName.getText().toString().trim();
        email = edtEmail.getText().toString().trim();
        phone = edtPhone.getText().toString().trim();

        // TODO: Decode or Encode
        // Crypto.encryptAndEncode("String_to_encrypt"); or Crypto.decodeAndDecrypt("String_encrypted");
        password = Crypto.encryptAndEncode(edtPassword.getText().toString().trim());

        referralCode = edtReferral.getText().toString().trim();
        clientType = edtReferral.getText().toString().trim().length() > 0 ? "admin" : "client";
        Date currentTime = Calendar.getInstance().getTime();

        Map<String, Object> dataUser = new HashMap<>();
        dataUser.put("id", id);
        dataUser.put("name", fullName);
        dataUser.put("email", email);
        dataUser.put("password", password);
        dataUser.put("phone", phone);
        dataUser.put("clientType", clientType);
        dataUser.put("referralCode", referralCode);
        dataUser.put("createAt", currentTime);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(id).set(dataUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                onUserProfileUpdate(fullName);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onShowSnack("There is an error saving user information.");
                progress.setVisibility(View.GONE);
                btnSignUp.setVisibility(View.VISIBLE);
            }
        });
    }

    private void onUserProfileUpdate(String name) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(Uri.parse(uriProfile)).build();

        assert user != null;
        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    onSuccessRegisterUser();
                    progress.setVisibility(View.GONE);
                    btnSignUp.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void onSuccessRegisterUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(resources.getString(R.string.dialog_create_user_title));
        builder.setMessage(resources.getString(R.string.dialog_create_user_message));
        builder.setPositiveButton(resources.getString(R.string.dialog_create_user_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void onChangeTypeClient(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(resources.getString(R.string.dialog_client_title_signup));
        builder.setMessage(resources.getString(R.string.dialog_client_instructions_signup));
        builder.setPositiveButton(resources.getString(R.string.dialog_client_client_signup), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onUpdateUI(1);
            }
        });
        builder.setNegativeButton(resources.getString(R.string.dialog_client_admin_signup), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onUpdateUI(0);
            }
        });
        builder.setNeutralButton(resources.getString(R.string.dialog_client_neutral_signup), null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void onUpdateUI(int type){
        txtUserType.setText(type == 0 ? "Admin" : "Client");
        txtHoldelReferral.setVisibility(type == 0 ? View.VISIBLE: View.GONE);
        edtReferral.setVisibility(type == 0 ? View.VISIBLE : View.GONE);
        edtReferral.setText( type == 0 ? UUID.randomUUID().toString().substring(3): "");
    }

    private void onShowSnack(String text) {
        Snackbar snackbar = Snackbar.make(layout, text, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void setUILng(){
        String defaultLng = LocaleHelper.getLanguage(SignUp.this);
        context = LocaleHelper.setLocale(SignUp.this, defaultLng);
        resources = context.getResources();
        txtTitle.setText(resources.getString(R.string.title_signup));
        txtSubtitle.setText(resources.getString(R.string.subtitle_signup));
        txtInstructions.setText(resources.getString(R.string.instructions_signup));
        txtHolderFullName.setText(resources.getString(R.string.edt_full_name_signup));
        txtHolderEmail.setText(resources.getString(R.string.edt_email_signup));
        txtHolderPassword.setText(resources.getString(R.string.edt_password_signup));
        txtHolderRepeatPassword.setText(resources.getString(R.string.edt_repeat_password_signup));
        txtHolderPhone.setText(resources.getString(R.string.edt_phone_signup));
        txtHolderTypeClient.setText(resources.getString(R.string.dialog_client_signup));
        checkTycos.setText(resources.getString(R.string.btn_tycos_signup));
        btnSignUp.setText(resources.getString(R.string.btn_signup));
        btnSignUp.setText(resources.getString(R.string.btn_signup));
        txtBack.setText(resources.getString(R.string.btn_signin_signup));
    }
}