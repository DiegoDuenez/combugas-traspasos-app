package modelos;

import java.io.Serializable;

public class Empleado implements Serializable {

    private String id_empleado;
    private String nombre;
    private String numero_empleado;
    private String id_estacion;
    private String fecha_registro;
    private String fecha_actulizacion;
    private String status;
    private String nombre_estacion;

    private String usa_gps = "0";

    private String tipo_empleado;

    public Empleado(String id_empleado, String nombre, String numero_empleado, String id_estacion, String nombre_estacion, String tipo_empleado) {
        this.id_empleado = id_empleado;
        this.nombre = nombre;
        this.numero_empleado = numero_empleado;
        this.id_estacion = id_estacion;
        this.nombre_estacion = nombre_estacion;
        this.tipo_empleado = tipo_empleado;
    }

    public String getUsa_gps() {
        return usa_gps;
    }

    public void setUsa_gps(String usa_gps) {
        this.usa_gps = usa_gps;
    }

    public String getId_empleado() {
        return id_empleado;
    }

    public void setId_empleado(String id_empleado) {
        this.id_empleado = id_empleado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumero_empleado() {
        return numero_empleado;
    }

    public void setNumero_empleado(String numero_empleado) {
        this.numero_empleado = numero_empleado;
    }

    public String getId_estacion() {
        return id_estacion;
    }

    public void setId_estacion(String id_estacion) {
        this.id_estacion = id_estacion;
    }

    public String getFecha_registro() {
        return fecha_registro;
    }

    public void setFecha_registro(String fecha_registro) {
        this.fecha_registro = fecha_registro;
    }

    public String getFecha_actulizacion() {
        return fecha_actulizacion;
    }

    public void setFecha_actulizacion(String fecha_actulizacion) {
        this.fecha_actulizacion = fecha_actulizacion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNombre_estacion() {
        return nombre_estacion;
    }

    public void setNombre_estacion(String nombre_estacion) {
        this.nombre_estacion = nombre_estacion;
    }

    public String getTipo_empleado() {
        return tipo_empleado;
    }

    public void setTipo_empleado(String tipo_empleado) {
        this.tipo_empleado = tipo_empleado;
    }
}
