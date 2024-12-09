package com.example.conectamobile;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.HashSet;

public class ChatEnVivoActivity extends AppCompatActivity {

    private ListView listViewMensajes;
    private EditText editTextMensaje;
    private Button btnEnviarMensaje, btnVolver;
    private ArrayList<String> mensajesList;
    private ArrayAdapter<String> adapter;
    private HashSet<String> mensajesProcesados;

    private String topico;
    private String usuarioActual;
    private String usuarioDestinatario;
    private MqttAsyncClient mqttClient;
    private final String brokerUrl = "tcp://test.mosquitto.org:1883";

    private DatabaseReference chatReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_en_vivo);

        listViewMensajes = findViewById(R.id.listViewMensajes);
        editTextMensaje = findViewById(R.id.editTextMensaje);
        btnEnviarMensaje = findViewById(R.id.btnEnviarMensaje);
        btnVolver = findViewById(R.id.btnVolverChatEnVivo);

        mensajesList = new ArrayList<>();
        mensajesProcesados = new HashSet<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mensajesList);
        listViewMensajes.setAdapter(adapter);

        topico = getIntent().getStringExtra("topico");
        usuarioActual = getIntent().getStringExtra("usuarioEmail");
        usuarioDestinatario = getIntent().getStringExtra("usuarioDestinatario");

        if (topico == null || usuarioActual == null || usuarioDestinatario == null) {
            Toast.makeText(this, "Error: Datos del contacto no disponibles.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String nombreUsuarioActual = extraerNombreDeCorreo(usuarioActual);
        String nombreUsuarioDestinatario = extraerNombreDeCorreo(usuarioDestinatario);
        setTitle("Chat: " + nombreUsuarioActual + " y " + nombreUsuarioDestinatario);

        chatReference = FirebaseDatabase.getInstance()
                .getReference("Chats")
                .child(topico)
                .child("Mensajes");

        cargarMensajesGuardados();
        inicializarMQTT();

        btnEnviarMensaje.setOnClickListener(v -> enviarMensaje());
        btnVolver.setOnClickListener(v -> finish());
    }

    private void inicializarMQTT() {
        try {
            mqttClient = new MqttAsyncClient(brokerUrl, MqttAsyncClient.generateClientId(), null);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);

            mqttClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT", "Conexión exitosa");
                    suscribirTopico();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("MQTT", "Error al conectar: " + exception.getMessage());
                    Toast.makeText(ChatEnVivoActivity.this, "Error al conectar al servidor MQTT", Toast.LENGTH_SHORT).show();
                }
            });

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.e("MQTT", "Conexión perdida");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String contenido = new String(message.getPayload());

                    if (!mensajesProcesados.contains(contenido)) {
                        mensajesProcesados.add(contenido);

                        if (contenido.startsWith(extraerNombreDeCorreo(usuarioActual))) {
                            mensajesList.add("Yo: " + contenido.split(": ", 2)[1]);
                        } else {
                            mensajesList.add(contenido);
                        }

                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                        guardarMensajeEnFirebase(contenido);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d("MQTT", "Mensaje entregado");
                }
            });
        } catch (MqttException e) {
            Log.e("MQTT", "Error al inicializar MQTT: " + e.getMessage());
        }
    }

    private void suscribirTopico() {
        try {
            mqttClient.subscribe(topico, 1);
            Log.d("MQTT", "Suscripción al tópico exitosa");
        } catch (MqttException e) {
            Log.e("MQTT", "Error al suscribirse al tópico: " + e.getMessage());
        }
    }

    private void enviarMensaje() {
        String mensaje = editTextMensaje.getText().toString().trim();
        if (mensaje.isEmpty()) {
            Toast.makeText(this, "Escribe un mensaje", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombreRemitente = extraerNombreDeCorreo(usuarioActual);
        String mensajeFormateado = nombreRemitente + ": " + mensaje;

        try {
            mqttClient.publish(topico, new MqttMessage(mensajeFormateado.getBytes()));

            mensajesList.add("Yo: " + mensaje);
            mensajesProcesados.add(mensajeFormateado);
            adapter.notifyDataSetChanged();

            guardarMensajeEnFirebase(mensajeFormateado);
            editTextMensaje.setText("");
        } catch (MqttException e) {
            Log.e("MQTT", "Error al enviar mensaje: " + e.getMessage());
        }
    }

    private void guardarMensajeEnFirebase(String mensaje) {
        chatReference.push().setValue(mensaje).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firebase", "Mensaje guardado exitosamente");
            } else {
                Log.e("Firebase", "Error al guardar el mensaje");
            }
        });
    }

    private void cargarMensajesGuardados() {
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    String mensaje = data.getValue(String.class);
                    if (mensaje != null && !mensajesProcesados.contains(mensaje)) {
                        mensajesProcesados.add(mensaje);
                        mensajesList.add(mensaje);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error al cargar mensajes: " + error.getMessage());
            }
        });
    }


    private String extraerNombreDeCorreo(String correo) {
        int index = correo.indexOf("@");
        if (index != -1) {
            return correo.substring(0, index);
        }
        return correo;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
            }
        } catch (MqttException e) {
            Log.e("MQTT", "Error al desconectar MQTT: " + e.getMessage());
        }
    }
}
