package dev.lithmath.litmath;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {

    TextView txtSubtitle, txtMeet, txtChat;
    LinearLayout btnMeet;
    LinearLayout btnChat;
    ImageButton btnProfile;

    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // TODO: Reference UI
        txtSubtitle = findViewById(R.id.txt_subtitle_home);
        txtMeet = findViewById(R.id.txt_button_meet_home);
        btnMeet = findViewById(R.id.btn_meet);
        txtChat = findViewById(R.id.txt_button_chat_home);
        btnChat = findViewById(R.id.btn_chat);
        btnProfile = findViewById(R.id.btn_profile_home);

        // TODO: Actions
        setUILng();

        btnMeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowMeet();
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowChat();
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowProfile();
            }
        });
    }

    private void onShowMeet() {
        Intent i = new Intent(getApplicationContext(), Meet.class);
        startActivity(i);
    }
    private void onShowChat() {
        Intent i = new Intent(getApplicationContext(), Chats.class);
        startActivity(i);
    }
    private void onShowProfile() {
        Intent i = new Intent(getApplicationContext(), Profile.class);
        startActivity(i);
    }

    private void setUILng(){
        String defaultLng = LocaleHelper.getLanguage(Home.this);
        context = LocaleHelper.setLocale(Home.this, defaultLng);
        resources = context.getResources();
        txtSubtitle.setText(resources.getString(R.string.subtitle_home));
        txtMeet.setText(resources.getString(R.string.button_meet_home));
    }
}