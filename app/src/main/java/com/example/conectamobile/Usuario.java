package com.example.conectamobile;


 public class Usuario {
    private String nombre;
    private String correo;
    private String fotoUrl;
    private String estado;
    private String telefono;
    private String fechaNacimiento;

    public Usuario() {

    }

     public Usuario(String nombre, String correo, String fotoUrl, String estado, String telefono, String fechaNacimiento) {
         this.nombre = nombre;
         this.correo = correo;
         this.fotoUrl = fotoUrl;
         this.estado = estado;
         this.telefono = telefono;
         this.fechaNacimiento = fechaNacimiento;
     }

     public Usuario(String nuevoNombre, String email, String fotoUrl, String nuevoEstado) {
     }


     public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}
