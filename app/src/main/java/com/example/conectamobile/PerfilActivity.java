package com.example.conectamobile;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PerfilActivity extends AppCompatActivity {

    private ImageView imagenPerfil;
    private EditText nombreEditText, estadoEditText, cumpleEditText, urlFotoEditText;
    private Button guardarCambiosBoton, volverMenuBoton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Usuarios");

        imagenPerfil = findViewById(R.id.imagenPerfil);
        nombreEditText = findViewById(R.id.nombreEditText);
        estadoEditText = findViewById(R.id.estadoEditText);
        cumpleEditText = findViewById(R.id.cumpleEditText);
        urlFotoEditText = findViewById(R.id.urlFotoEditText);
        guardarCambiosBoton = findViewById(R.id.guardarCambiosBoton);
        volverMenuBoton = findViewById(R.id.volverMenuBoton);

        cargarDatosPerfil();

        guardarCambiosBoton.setOnClickListener(v -> guardarCambios());
        volverMenuBoton.setOnClickListener(v -> finish());
    }

    private void cargarDatosPerfil() {
        String userId = mAuth.getCurrentUser().getUid();

        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    if (usuario != null) {
                        nombreEditText.setText(usuario.getNombre());
                        estadoEditText.setText(usuario.getEstado());
                        cumpleEditText.setText(usuario.getFechaNacimiento());
                        urlFotoEditText.setText(usuario.getFotoUrl());

                        if (!TextUtils.isEmpty(usuario.getFotoUrl())) {
                            Glide.with(PerfilActivity.this).load(usuario.getFotoUrl()).into(imagenPerfil);
                        } else {
                            imagenPerfil.setImageResource(R.drawable.ic_placeholder);
                        }
                    }
                } else {
                    Toast.makeText(PerfilActivity.this, "No se encontraron datos del perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PerfilActivity.this, "Error al cargar datos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarCambios() {
        String nuevoNombre = nombreEditText.getText().toString().trim();
        String nuevoEstado = estadoEditText.getText().toString().trim();
        String nuevoCumple = cumpleEditText.getText().toString().trim();
        String nuevaFotoUrl = urlFotoEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(nuevaFotoUrl)) {
            Glide.with(this).load(nuevaFotoUrl).into(imagenPerfil);
        }

        actualizarPerfil(nuevoNombre, nuevoEstado, nuevoCumple, nuevaFotoUrl);
    }

    private void actualizarPerfil(String nuevoNombre, String nuevoEstado, String nuevoCumple, String fotoUrl) {
        String userId = mAuth.getCurrentUser().getUid();
        Usuario usuario = new Usuario(
                TextUtils.isEmpty(nuevoNombre) ? mAuth.getCurrentUser().getDisplayName() : nuevoNombre,
                mAuth.getCurrentUser().getEmail(),
                fotoUrl,
                TextUtils.isEmpty(nuevoEstado) ? "Sin estado" : nuevoEstado,
                "",
                TextUtils.isEmpty(nuevoCumple) ? "No especificado" : nuevoCumple
        );

        mDatabase.child(userId).setValue(usuario).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(PerfilActivity.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PerfilActivity.this, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
