package com.example.conectamobile;

public class Contacto {
    private String id;
    private String nombre;
    private String telefono;
    private String correo;
    private String topico;

    public Contacto() {
    }

    public Contacto(String id, String nombre, String telefono, String correo, String topico) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.topico = topico;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTopico() {return topico; }

    public void setTopico(String topico) {this.topico = topico; }
}
