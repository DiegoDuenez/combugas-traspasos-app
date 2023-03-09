package modelos;

public class ValeAwa {

    private String id_vale;
    private String folio;
    private String nombre;
    private String fecha_recarga;
    private String awa_quantity;
    private String alk_quantity;
    private String six_quantity;
    private String salk_quantity;
    private String nombre_estacion;
    private String nombre_completo;
    private String descripcion;

    private boolean isSelected;

    public ValeAwa(String id_vale, String folio, String nombre, String fecha_recarga, String awa_quantity, String alk_quantity, String nombre_estacion, String nombre_completo, String descripcion, String six_quantity, String salk_quantity) {
        this.id_vale = id_vale;
        this.folio = folio;
        this.nombre = nombre;
        this.fecha_recarga = fecha_recarga;
        this.awa_quantity = awa_quantity;
        this.alk_quantity = alk_quantity;
        this.six_quantity = six_quantity;
        this.salk_quantity = salk_quantity;
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

    public String getAwa_quantity() {
        return awa_quantity;
    }

    public void setAwa_quantity(String awa_quantity) {
        this.awa_quantity = awa_quantity;
    }

    public String getAlk_quantity() {
        return alk_quantity;
    }

    public void setAlk_quantity(String alk_quantity) {
        this.alk_quantity = alk_quantity;
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

    public String getSix_quantity() {
        return six_quantity;
    }

    public void setSix_quantity(String six_quantity) {
        this.six_quantity = six_quantity;
    }

    public String getSalk_quantity() {
        return salk_quantity;
    }

    public void setSalk_quantity(String salk_quantity) {
        this.salk_quantity = salk_quantity;
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
