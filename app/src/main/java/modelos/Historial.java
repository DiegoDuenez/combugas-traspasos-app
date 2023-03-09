package modelos;

public class Historial {

    private String vehiculo;
    private int tanques30;
    private int tanques45;
    private String fecha;

    public Historial(){

    }

    public Historial(String vehiculo, int tanques30, int tanques45, String fecha){
        this.vehiculo = vehiculo;
        this.tanques30 = tanques30;
        this.tanques45 = tanques45;
        this.fecha = fecha;
        if(vehiculo == null || vehiculo.equals("") || vehiculo.equals("null")){
            this.vehiculo = "";
        }
    }

    public String getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(String vehiculo) {
        this.vehiculo = vehiculo;
    }

    public int getTanques30() {
        return tanques30;
    }

    public void setTanques30(int tanques30) {
        this.tanques30 = tanques30;
    }

    public int getTanques45() {
        return tanques45;
    }

    public void setTanques45(int tanques45) {
        this.tanques45 = tanques45;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
