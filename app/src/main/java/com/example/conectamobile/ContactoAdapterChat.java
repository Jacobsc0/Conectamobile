package com.example.conectamobile;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ContactoAdapterChat extends RecyclerView.Adapter<ContactoAdapterChat.ContactoViewHolder> {

    private ArrayList<Contacto> listaContactos;
    private Context context;

    public ContactoAdapterChat(ArrayList<Contacto> listaContactos, Context context) {
        this.listaContactos = listaContactos;
        this.context = context;
    }

    @NonNull
    @Override
    public ContactoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contacto_chat, parent, false);
        return new ContactoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactoViewHolder holder, int position) {
        Contacto contacto = listaContactos.get(position);

        holder.nombreTextView.setText(contacto.getNombre());
        holder.topicoTextView.setText(contacto.getTopico());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("usuarioEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
            intent.putExtra("usuarioDestinatario", contacto.getCorreo());
            intent.putExtra("topico", contacto.getTopico());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return listaContactos.size();
    }

    public static class ContactoViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView, topicoTextView;

        public ContactoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.nombreTextView);
            topicoTextView = itemView.findViewById(R.id.topicoTextView);
        }
    }
}
