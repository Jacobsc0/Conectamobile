package com.example.conectamobile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PerfilActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imagenPerfil;
    private EditText nombreEditText, estadoEditText, cumpleEditText;
    private Button cambiarFotoBoton, guardarCambiosBoton, volverMenuBoton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private Uri imagenUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Usuarios");
        mStorage = FirebaseStorage.getInstance().getReference("FotosPerfil");

        imagenPerfil = findViewById(R.id.imagenPerfil);
        nombreEditText = findViewById(R.id.nombreEditText);
        estadoEditText = findViewById(R.id.estadoEditText);
        cumpleEditText = findViewById(R.id.cumpleEditText);
        cambiarFotoBoton = findViewById(R.id.cambiarFotoBoton);
        guardarCambiosBoton = findViewById(R.id.guardarCambiosBoton);
        volverMenuBoton = findViewById(R.id.volverMenuBoton);

        cargarDatosPerfil();

        cambiarFotoBoton.setOnClickListener(v -> abrirGaleria());
        guardarCambiosBoton.setOnClickListener(v -> guardarCambios());
        volverMenuBoton.setOnClickListener(v -> finish());
    }

    private void cargarDatosPerfil() {
        String userId = mAuth.getCurrentUser().getUid();

        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Obtener datos del perfil
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    if (usuario != null) {
                        nombreEditText.setText(usuario.getNombre());
                        estadoEditText.setText(usuario.getEstado());
                        cumpleEditText.setText(usuario.getFechaNacimiento());

                        if (!TextUtils.isEmpty(usuario.getFotoUrl())) {
                            Glide.with(PerfilActivity.this).load(usuario.getFotoUrl()).into(imagenPerfil);
                        } else {
                            imagenPerfil.setImageResource(R.drawable.default_profile);
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

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imagenUri = data.getData();
            imagenPerfil.setImageURI(imagenUri);
        }
    }

    private void guardarCambios() {
        String nuevoNombre = nombreEditText.getText().toString().trim();
        String nuevoEstado = estadoEditText.getText().toString().trim();
        String nuevoCumple = cumpleEditText.getText().toString().trim();

        if (imagenUri != null) {
            StorageReference fileRef = mStorage.child(mAuth.getCurrentUser().getUid() + ".jpg");
            fileRef.putFile(imagenUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                actualizarPerfil(nuevoNombre, nuevoEstado, nuevoCumple, uri.toString());
            }));
        } else {
            actualizarPerfil(nuevoNombre, nuevoEstado, nuevoCumple, null);
        }
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
