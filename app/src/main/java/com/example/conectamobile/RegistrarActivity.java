package com.example.conectamobile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrarActivity extends AppCompatActivity {

    private EditText nombreEditText, correoEditText, contrasenaEditText, estadoEditText, telefonoEditText, fechaNacimientoEditText;
    private Button registrarBoton, VolverBoton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Usuarios");

        nombreEditText = findViewById(R.id.nombreEditText);
        correoEditText = findViewById(R.id.correoEditText);
        contrasenaEditText = findViewById(R.id.contrasenaEditText);
        estadoEditText = findViewById(R.id.estadoEditText);
        telefonoEditText = findViewById(R.id.telefonoEditText);
        fechaNacimientoEditText = findViewById(R.id.fechaNacimientoEditText);
        registrarBoton = findViewById(R.id.registrarBoton);
        VolverBoton = findViewById(R.id.VolverBoton);

        registrarBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });

        VolverBoton.setOnClickListener(v -> VolverLogin());
    }

    private void registrarUsuario() {
        String nombre = nombreEditText.getText().toString().trim();
        String correo = correoEditText.getText().toString().trim();
        String contrasena = contrasenaEditText.getText().toString().trim();
        String estado = estadoEditText.getText().toString().trim();
        String telefono = telefonoEditText.getText().toString().trim();
        String fechaNacimiento = fechaNacimientoEditText.getText().toString().trim();

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(correo) || TextUtils.isEmpty(contrasena)) {
            Toast.makeText(RegistrarActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        Usuario usuario = new Usuario(nombre, correo, null, estado, telefono, fechaNacimiento);
                        mDatabase.child(userId).setValue(usuario)
                                .addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful()) {
                                        Toast.makeText(RegistrarActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(RegistrarActivity.this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(RegistrarActivity.this, "Error en el registro", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void VolverLogin() {
        finish();
    }
}
