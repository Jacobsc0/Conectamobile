package com.example.conectamobile;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ContactoAdapter extends RecyclerView.Adapter<ContactoAdapter.ContactoViewHolder> {

    private ArrayList<Contacto> listaContactos;
    private Context context;

    public ContactoAdapter(ArrayList<Contacto> listaContactos, Context context) {
        this.listaContactos = listaContactos;
        this.context = context;
    }

    @NonNull
    @Override
    public ContactoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contacto, parent, false);
        return new ContactoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactoViewHolder holder, int position) {
        Contacto contacto = listaContactos.get(position);

        holder.nombreTextView.setText(contacto.getNombre());
        holder.telefonoTextView.setText(contacto.getTelefono());
        holder.correoTextView.setText(contacto.getCorreo());
        holder.topicoTextView.setText(contacto.getTopico());

        holder.itemView.setOnLongClickListener(v -> {
            mostrarMenu(v, contacto, position);
            return true;
        });
    }

    private void mostrarMenu(View view, Contacto contacto, int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.menu_contacto);

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_editar) {
                editarContacto(contacto);
                return true;
            } else if (item.getItemId() == R.id.menu_eliminar) {
                eliminarContacto(contacto.getId(), position);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void editarContacto(Contacto contacto) {
        Intent intent = new Intent(context, FormularioContactoActivity.class);
        intent.putExtra("contactoId", contacto.getId());
        intent.putExtra("nombre", contacto.getNombre());
        intent.putExtra("telefono", contacto.getTelefono());
        intent.putExtra("correo", contacto.getCorreo());
        context.startActivity(intent);
    }

    private void eliminarContacto(String id, int position) {
        if (position >= 0 && position < listaContactos.size()) {
            DatabaseReference contactoRef = FirebaseDatabase.getInstance()
                    .getReference("Contactos")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(id);

            contactoRef.removeValue()
                    .addOnCompleteListener(task -> {
                        try {
                            if (task.isSuccessful()) {
                                listaContactos.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(context, "Contacto eliminado", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Error al eliminar el contacto", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        } else {
            Toast.makeText(context, "Índice no válido", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return listaContactos.size();
    }

    public static class ContactoViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView, telefonoTextView, correoTextView, topicoTextView;

        public ContactoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.nombreTextView);
            telefonoTextView = itemView.findViewById(R.id.telefonoTextView);
            correoTextView = itemView.findViewById(R.id.correoTextView);
            topicoTextView = itemView.findViewById(R.id.topicoTextView);
        }
    }
}
