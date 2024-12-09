package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListaContactosActivity extends AppCompatActivity {

    private RecyclerView recyclerViewContactos;
    private Button botonAgregarContacto, volverMenuBoton;
    private ContactoAdapter adapter;
    private ArrayList<Contacto> listaContactos = new ArrayList<>();
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contactos);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Contactos")
                .child(mAuth.getCurrentUser().getUid());

        recyclerViewContactos = findViewById(R.id.recyclerViewContactos);
        botonAgregarContacto = findViewById(R.id.botonAgregarContacto);
        volverMenuBoton = findViewById(R.id.volverMenuBoton);

        recyclerViewContactos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactoAdapter(listaContactos, this);
        recyclerViewContactos.setAdapter(adapter);

        cargarContactos();

        botonAgregarContacto.setOnClickListener(v -> {
            Intent intent = new Intent(ListaContactosActivity.this, FormularioContactoActivity.class);
            startActivity(intent);
        });

        volverMenuBoton.setOnClickListener(v -> finish());
    }

    private void cargarContactos() {
        mDatabase.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaContactos.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Contacto contacto = data.getValue(Contacto.class);
                    if (contacto != null) {
                        listaContactos.add(contacto);
                        Log.d("Firebase", "Contacto descargado: " + contacto.getNombre());
                    } else {
                        Log.e("Firebase", "El contacto descargado es nulo.");
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error al cargar contactos: " + error.getMessage());
            }
        });
    }
}
