package com.example.ecowise.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecowise.R;

public class AjustesActivity extends AppCompatActivity {
    private EditText etnombreUsuario;
    private CheckBox checkNotificaciones;
    private Spinner spinnerTema;
    private Button btnGuardar;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "AppPreferences";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ajustes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        etnombreUsuario = findViewById(R.id.etnombreUsuario);
        checkNotificaciones = findViewById(R.id.checkNotificaciones);
        spinnerTema = findViewById(R.id.spinnerTema);
        btnGuardar = findViewById(R.id.btnGuardar);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        cargarPreferencias();

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.temaApp_opciones, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTema.setAdapter(arrayAdapter);


        btnGuardar.setOnClickListener(v-> {
            guardarPreferencias();
            aplicarTema();

        });

    }



    private void guardarPreferencias() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", etnombreUsuario.getText().toString());
        editor.putBoolean("notifications", checkNotificaciones.isChecked());
        editor.putInt("theme", spinnerTema.getSelectedItemPosition());
        editor.apply();

        Toast.makeText(this, "Preferencias guardadas", Toast.LENGTH_SHORT).show();
    }


    private void cargarPreferencias() {
        String nombreUsuario = sharedPreferences.getString("username", "");
        boolean notificaciones = sharedPreferences.getBoolean("notifications", true);
        int themePosition = sharedPreferences.getInt("theme", 0);

        etnombreUsuario.setText(nombreUsuario);
        checkNotificaciones.setChecked(notificaciones);
        spinnerTema.setSelection(themePosition);

    }

    private void aplicarTema() {
        int themePosition = spinnerTema.getSelectedItemPosition();
        switch (themePosition) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
}