package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class ListaContactosChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewContactos;
    private ContactoAdapterChat adapter;
    private ArrayList<Contacto> listaContactos = new ArrayList<>();
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Button btnVolverAlMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contactos_chat);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance()
                .getReference("Contactos")
                .child(mAuth.getCurrentUser().getUid());

        recyclerViewContactos = findViewById(R.id.recyclerViewContactosChat);
        recyclerViewContactos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactoAdapterChat(listaContactos, this);
        recyclerViewContactos.setAdapter(adapter);

        btnVolverAlMenu = findViewById(R.id.btnVolverAlMenu);

        btnVolverAlMenu.setOnClickListener(v -> volverAlMenu());

        cargarContactos();
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
                    } else {
                        Toast.makeText(ListaContactosChatActivity.this, "Contacto no válido", Toast.LENGTH_SHORT).show();
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListaContactosChatActivity.this, "Error al cargar contactos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void volverAlMenu() {
        finish();
    }
}
