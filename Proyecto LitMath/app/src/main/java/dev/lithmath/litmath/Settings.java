package dev.lithmath.litmath;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {

    int lang_selected;
    Context context;
    Resources resources;

    TextView txtChangeLng, txtLng, txtInfoApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        txtChangeLng = findViewById(R.id.txt_change_language);
        txtLng = findViewById(R.id.txt_lng_demo);
        txtInfoApp = findViewById(R.id.txt_info_app);

        setUILng();
        setUIConfig();

        txtChangeLng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangeLngDialog();
            }
        });
    }

    private void setUIConfig() {
        String versionCode = String.valueOf(BuildConfig.VERSION_CODE);
        String versionName = String.valueOf(BuildConfig.VERSION_NAME);
        txtInfoApp.setText("Version App: " + versionCode + '/' + versionName);
    }

    private void setUILng(){
        String defaultLng = LocaleHelper.getLanguage(Settings.this);
        context = LocaleHelper.setLocale(Settings.this, defaultLng);
        resources = context.getResources();
        txtLng.setText(resources.getString(R.string.spiner_language));
    }

    private void onChangeLngDialog() {
        final String[] Language = {"English", "Spanish", "Portuguese"};
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Settings.this);
        dialogBuilder.setTitle("Select a Language").setSingleChoiceItems(Language, lang_selected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                txtChangeLng.setText(Language[i]);
                if (Language[i].equals("English")) {
                    context = LocaleHelper.setLocale(Settings.this, "en");
                    resources = context.getResources();
                    lang_selected = 0;
                    txtLng.setText(resources.getString(R.string.spiner_language));
                }
                if (Language[i].equals("Spanish")) {
                    context = LocaleHelper.setLocale(Settings.this, "es");
                    resources = context.getResources();
                    lang_selected = 1;
                    txtLng.setText(resources.getString(R.string.spiner_language));
                }
                if (Language[i].equals("Portuguese")) {
                    context = LocaleHelper.setLocale(Settings.this, "pt");
                    resources = context.getResources();
                    lang_selected = 2;
                    txtLng.setText(resources.getString(R.string.spiner_language));
                }
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogBuilder.create().show();
    }
}