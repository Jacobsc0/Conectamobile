package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText correoEditText, contrasenaEditText;
    private Button iniciarSesionBoton, registrarBoton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        correoEditText = findViewById(R.id.correoEditText);
        contrasenaEditText = findViewById(R.id.contrasenaEditText);
        iniciarSesionBoton = findViewById(R.id.iniciarSesionBoton);
        registrarBoton = findViewById(R.id.registrarBoton);

        iniciarSesionBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion();
            }
        });

        registrarBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistrarActivity.class);
                startActivity(intent);
            }
        });
    }

    private void iniciarSesion() {
        String correo = correoEditText.getText().toString().trim();
        String contrasena = contrasenaEditText.getText().toString().trim();

        if (TextUtils.isEmpty(correo) || TextUtils.isEmpty(contrasena)) {
            Toast.makeText(MainActivity.this, "Por favor, ingrese sus credenciales", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser Usuario = mAuth.getCurrentUser();
                        Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, MenuPrincipalActivity.class));
                    } else {
                        Toast.makeText(MainActivity.this, "Error en inicio de sesión", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}