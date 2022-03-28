package dev.lithmath.litmath.auth;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import dev.lithmath.litmath.LocaleHelper;
import dev.lithmath.litmath.R;

public class Recovery extends AppCompatActivity {

    TextView txtTitle, txtSubtitle, txtInstructions;
    EditText edtRecoveryPassword;
    Button btnRecoveryPassword;
    ProgressBar progressBar;

    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        txtTitle = findViewById(R.id.txt_title_recovery);
        txtSubtitle = findViewById(R.id.txt_subtitle_recovery);
        txtInstructions = findViewById(R.id.txt_instructions_recovery);
        edtRecoveryPassword = findViewById(R.id.edt_email_recovery);
        btnRecoveryPassword = findViewById(R.id.btn_recovery);
        progressBar = findViewById(R.id.progress_recovery);

        // TODO: Actions
        setUILng();

        btnRecoveryPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtRecoveryPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_SHORT).show();
                } else {
                    onRecoverPassword(email);
                }
            }
        });
    }

    private void onRecoverPassword(String email) {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "The email was sent, check your email.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getApplicationContext(), "There is an error, please try again later", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setUILng(){
        String defaultLng = LocaleHelper.getLanguage(Recovery.this);
        context = LocaleHelper.setLocale(Recovery.this, defaultLng);
        resources = context.getResources();
        txtTitle.setText(resources.getString(R.string.title_recovery));
        txtInstructions.setText(resources.getString(R.string.instructions_recovery));
        txtSubtitle.setText(resources.getString(R.string.subtitle_recovery));
        btnRecoveryPassword.setText(resources.getString(R.string.button_recovery));
    }
}