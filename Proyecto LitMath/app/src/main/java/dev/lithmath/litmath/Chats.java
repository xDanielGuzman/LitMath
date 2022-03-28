package dev.lithmath.litmath;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Chats extends AppCompatActivity {
    EditText et_mensaje_txt;
    ImageButton btn_enviar_msj;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        String usuario = getIntent().getExtras().getString("name");
        final String id_user = getIntent().getExtras().getString("id_user");
        et_mensaje_txt = findViewById(R.id.et_txt_mensaje);
        btn_enviar_msj = findViewById(R.id.btn_enviar_msj);
        btn_enviar_msj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msj = et_mensaje_txt.getText().toString();
                Chat_f chatmsj = new Chat_f(user.getUid(),id_user,msj,"no");

            }
        });
    }
}