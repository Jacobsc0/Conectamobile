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

public class FormularioContactoActivity extends AppCompatActivity {

    private EditText nombreEditText, telefonoEditText, correoEditText, topicoEditText;
    private Button botonGuardarContacto;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_contacto);

        mDatabase = FirebaseDatabase.getInstance().getReference("Contactos")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        nombreEditText = findViewById(R.id.nombreEditText);
        telefonoEditText = findViewById(R.id.telefonoEditText);
        correoEditText = findViewById(R.id.correoEditText);
        topicoEditText = findViewById(R.id.topicoEditText);  // Campo para 'topico'
        botonGuardarContacto = findViewById(R.id.botonGuardarContacto);

        botonGuardarContacto.setOnClickListener(v -> guardarContacto());
    }

    private void guardarContacto() {
        String nombre = nombreEditText.getText().toString().trim();
        String telefono = telefonoEditText.getText().toString().trim();
        String correo = correoEditText.getText().toString().trim();
        String topico = topicoEditText.getText().toString().trim();  // Obtener el valor de 'topico'

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(telefono) || TextUtils.isEmpty(correo) || TextUtils.isEmpty(topico)) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String contactoId = mDatabase.push().getKey();  // Genera un ID único para el nuevo contacto

        if (contactoId == null) {
            Toast.makeText(this, "Error al generar ID del contacto", Toast.LENGTH_SHORT).show();
            return;
        }

        Contacto contacto = new Contacto(contactoId, nombre, telefono, correo, topico);

        mDatabase.child(contactoId).setValue(contacto)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Contacto agregado", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Error al agregar el contacto", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
