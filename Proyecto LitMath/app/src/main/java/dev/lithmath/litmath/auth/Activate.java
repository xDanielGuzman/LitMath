package dev.lithmath.litmath.auth;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import dev.lithmath.litmath.R;

public class Activate extends AppCompatActivity {

    TextView txtWaiting, txtBody, txtCount, txtEmailActivate;
    ProgressBar progressWaiting;
    Button btnResend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate);

        txtWaiting = findViewById(R.id.txt_waiting_activate);
        txtCount = findViewById(R.id.txt_resend_count);
        txtBody = findViewById(R.id.txt_activate_account_body);
        txtEmailActivate = findViewById(R.id.txt_email_activate);
        progressWaiting = findViewById(R.id.progress_active_account);
        btnResend = findViewById(R.id.btn_resend_email_verification);

        onConfigUI();

        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResendEmail();
            }
        });
    }

    private void onResendEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Email was send!", Toast.LENGTH_SHORT).show();
                    txtWaiting.setVisibility(View.VISIBLE);
                    progressWaiting.setVisibility(View.VISIBLE);
                    btnResend.setEnabled(false);
                    onCountActiveResend();
                } else {
                    Toast.makeText(getApplicationContext(), "Error, try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onCountActiveResend() {
        new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                txtCount.setVisibility(View.VISIBLE);
                txtCount.setText("Time remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                btnResend.setEnabled(true);
            }
        }.start();
    }

    private void onConfigUI() {
        Bundle extras = getIntent().getExtras();
        txtEmailActivate.setText(extras.getString("email"));

        txtWaiting.setVisibility(View.GONE);
        progressWaiting.setVisibility(View.GONE);
        txtCount.setVisibility(View.GONE);
    }
}