package modelos;

public class Vale {

    private String id_vale;
    private String folio;
    private String nombre;
    private String fecha_recarga;
    private String tank_30_quantity;
    private String tank_45_quantity;
    private String nombre_estacion;
    private String nombre_completo;
    private String descripcion;
    private String awa_quantity;
    private String alk_quantity;

    private boolean isSelected;

    public Vale(String id_vale, String folio, String nombre, String fecha_recarga, String tank_30_quantity, String tank_45_quantity, String nombre_estacion, String nombre_completo, String descripcion) {
        this.id_vale = id_vale;
        this.folio = folio;
        this.nombre = nombre;
        this.fecha_recarga = fecha_recarga;
        this.tank_30_quantity = tank_30_quantity;
        this.tank_45_quantity = tank_45_quantity;
        this.nombre_estacion = nombre_estacion;
        this.nombre_completo = nombre_completo;
        this.descripcion = descripcion;
    }

    public String getId_vale() {
        return id_vale;
    }

    public void setId_vale(String id_vale) {
        this.id_vale = id_vale;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha_recarga() {
        return fecha_recarga;
    }

    public void setFecha_recarga(String fecha_recarga) {
        this.fecha_recarga = fecha_recarga;
    }

    public String getTank_30_quantity() {
        return tank_30_quantity;
    }

    public void setTank_30_quantity(String tank_30_quantity) {
        this.tank_30_quantity = tank_30_quantity;
    }

    public String getTank_45_quantity() {
        return tank_45_quantity;
    }

    public void setTank_45_quantity(String tank_45_quantity) {
        this.tank_45_quantity = tank_45_quantity;
    }

    public String getNombre_estacion() {
        return nombre_estacion;
    }

    public void setNombre_estacion(String nombre_estacion) {
        this.nombre_estacion = nombre_estacion;
    }

    public String getNombre_completo() {
        return nombre_completo;
    }

    public void setNombre_completo(String nombre_completo) {
        this.nombre_completo = nombre_completo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isSelected(){
        return this.isSelected;
    }

    public void setSelected(boolean selected){
        this.isSelected = selected;
    }

    public Boolean getSelected() {return this.isSelected;}
}
