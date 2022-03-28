package dev.lithmath.litmath;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import dev.lithmath.litmath.auth.Activate;
import dev.lithmath.litmath.auth.Recovery;
import dev.lithmath.litmath.auth.SignUp;

public class MainActivity extends AppCompatActivity {

    TextView txtTitle, txtSubtitle, txtInstructions, txtHelp, txtRegister, txtRegisterAction;
    EditText edtEmail, edtPassword;
    Button btnSignin;
    ImageButton btnSignInLng;
    LinearLayoutCompat layout;
    ProgressBar progress;
    Context context;
    Resources resources;
    int lang_selected;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: Reference UI
        txtTitle = findViewById(R.id.txt_title_signin);
        txtSubtitle = findViewById(R.id.txt_subtitle_signin);
        txtInstructions = findViewById(R.id.txt_instructions_signin);
        txtHelp = findViewById(R.id.txt_help_signin);
        txtRegister = findViewById(R.id.txt_register_signin);
        txtRegisterAction = findViewById(R.id.txt_register_signin_action);
        edtEmail = findViewById(R.id.edt_email_signin);
        edtPassword = findViewById(R.id.edt_password_signin);
        btnSignin = findViewById(R.id.btn_signin);
        btnSignInLng = findViewById(R.id.btn_signin_lng);
        layout = findViewById(R.id.layout_signin);
        progress = findViewById(R.id.progress_signin);

        // TODO: Initialize context app
        mAuth = FirebaseAuth.getInstance();

        // TODO: Debug Config
        if (BuildConfig.DEBUG) {
            // Warning: This user was registered by me, please do not use it because I will find you and cut off your fingers.
            //edtEmail.setText("xidoyi2507@chinamkm.com");
            //edtPassword.setText("123456");
        }

        // TODO: Actions
        txtHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHelp();
            }
        });

        txtRegisterAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegister();
            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignIn();
            }
        });

        btnSignInLng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangeLngDialog();
            }
        });

        setUILng();
    }

    private void onHelp() {
        Intent i = new Intent(getApplicationContext(), Recovery.class);
        startActivity(i);
    }

    private void onRegister() {
        Intent i = new Intent(getApplicationContext(), SignUp.class);
        startActivity(i);
    }

    private void onSignIn() {
        String email, password;
        email = edtEmail.getText().toString().trim();
        password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            onShowSnack("Please enter email...");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            onShowSnack("Please enter password...");
            return;
        }

        progress.setVisibility(View.VISIBLE);
        btnSignin.setVisibility(View.GONE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null && user.isEmailVerified()) {
                        onUserRedirectToHome();
                    } else {
                        onUserVerifyEmail(email);
                    }
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        onShowSnack("Passowrd invalid length");
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        onShowSnack("Password or User incorrect...");
                    } catch (FirebaseAuthUserCollisionException e) {
                        onShowSnack("Error with user");
                    } catch (Exception e) {
                        onShowSnack("Exists un error, try again later...");
                    }
                }
                progress.setVisibility(View.GONE);
                btnSignin.setVisibility(View.VISIBLE);
                onClearInputs();
            }
        });
    }

    private void onUserRedirectToHome() {
        Intent intent = new Intent(getApplicationContext(), Home.class);
        startActivity(intent);
    }

    private void onUserVerifyEmail(String email) {
        Intent i = new Intent(getApplicationContext(), Activate.class);
        i.putExtra("email", email);
        startActivity(i);
    }

    private void onClearInputs() {
        edtEmail.setText("");
        edtPassword.setText("");
    }

    private void onShowSnack(String text) {
        Snackbar snackbar = Snackbar.make(layout, text, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void onChangeLngDialog() {
        final String[] Language = {resources.getString(R.string.dialog_option_en), resources.getString(R.string.dialog_option_es), resources.getString(R.string.dialog_option_pt)};
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        dialogBuilder.setTitle(resources.getString(R.string.dialog_lng_title)).setSingleChoiceItems(Language, lang_selected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Language[i].equals(resources.getString(R.string.dialog_option_en))) {
                    context = LocaleHelper.setLocale(MainActivity.this, "en");
                    resources = context.getResources();
                    lang_selected = 0;

                    txtTitle.setText(resources.getString(R.string.title_signin));
                    txtSubtitle.setText(resources.getString(R.string.subtitle_signin));
                    txtInstructions.setText(resources.getString(R.string.instructions_signin));
                    txtHelp.setText(resources.getString(R.string.help_signin));
                    btnSignin.setText(resources.getString(R.string.button_sigin));
                    txtRegister.setText(resources.getString(R.string.register_signin));
                    txtRegisterAction.setText(resources.getString(R.string.register_link_sigin));

                    dialogInterface.dismiss();
                }
                if (Language[i].equals(resources.getString(R.string.dialog_option_es))) {
                    context = LocaleHelper.setLocale(MainActivity.this, "es");
                    resources = context.getResources();
                    lang_selected = 1;

                    txtTitle.setText(resources.getString(R.string.title_signin));
                    txtSubtitle.setText(resources.getString(R.string.subtitle_signin));
                    txtInstructions.setText(resources.getString(R.string.instructions_signin));
                    txtHelp.setText(resources.getString(R.string.help_signin));
                    btnSignin.setText(resources.getString(R.string.button_sigin));
                    txtRegister.setText(resources.getString(R.string.register_signin));
                    txtRegisterAction.setText(resources.getString(R.string.register_link_sigin));

                    dialogInterface.dismiss();
                }
                if (Language[i].equals(resources.getString(R.string.dialog_option_pt))) {
                    context = LocaleHelper.setLocale(MainActivity.this, "pt");
                    resources = context.getResources();
                    lang_selected = 2;

                    txtTitle.setText(resources.getString(R.string.title_signin));
                    txtSubtitle.setText(resources.getString(R.string.subtitle_signin));
                    txtInstructions.setText(resources.getString(R.string.instructions_signin));
                    txtHelp.setText(resources.getString(R.string.help_signin));
                    btnSignin.setText(resources.getString(R.string.button_sigin));
                    txtRegister.setText(resources.getString(R.string.register_signin));
                    txtRegisterAction.setText(resources.getString(R.string.register_link_sigin));

                    dialogInterface.dismiss();
                }
            }
        }).setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogBuilder.create().show();
    }

    private void setUILng(){
        String defaultLng = LocaleHelper.getLanguage(MainActivity.this);
        context = LocaleHelper.setLocale(MainActivity.this, defaultLng);
        resources = context.getResources();

        txtTitle.setText(resources.getString(R.string.title_signin));
        txtSubtitle.setText(resources.getString(R.string.subtitle_signin));
        txtInstructions.setText(resources.getString(R.string.instructions_signin));
        txtHelp.setText(resources.getString(R.string.help_signin));
        btnSignin.setText(resources.getString(R.string.button_sigin));
        txtRegister.setText(resources.getString(R.string.register_signin));
        txtRegisterAction.setText(resources.getString(R.string.register_link_sigin));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            Intent i = new Intent(getApplicationContext(), Home.class);
            startActivity(i);
        }
    }
}