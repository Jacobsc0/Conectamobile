package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MenuPrincipalActivity extends AppCompatActivity {

    private Button btnCerrarSesion, perfilButton, contactosButton, chatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        perfilButton = findViewById(R.id.PerfilButton);
        contactosButton = findViewById(R.id.contactosButton);
        chatButton = findViewById(R.id.chatButton);

        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
        perfilButton.setOnClickListener(v -> abrirPerfil());
        contactosButton.setOnClickListener(v -> abrirContactos());
        chatButton.setOnClickListener(v -> abrirChat());
    }

    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut(); // Cierra la sesión en Firebase
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MenuPrincipalActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void abrirPerfil() {
        Intent intent = new Intent(MenuPrincipalActivity.this, PerfilActivity.class);
        startActivity(intent);
    }

    private void abrirContactos() {
        Intent intent = new Intent(MenuPrincipalActivity.this, ListaContactosActivity.class);
        startActivity(intent);
    }

    private void abrirChat() {
        Intent intent = new Intent(MenuPrincipalActivity.this, ListaContactosChatActivity.class);
        startActivity(intent);
    }
}
