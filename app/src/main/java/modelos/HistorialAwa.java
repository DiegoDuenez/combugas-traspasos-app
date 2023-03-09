package modelos;

public class HistorialAwa {
    private String vehiculo;
    private int garrAwa;
    private int garrAlk;
    private int sixAwa;
    private int sixAlk;
    private String fecha;

    public HistorialAwa(){

    }

    public HistorialAwa(String vehiculo, int garrAwa, int garrAlk, String fecha, int sixAwa, int sixAlk){
        this.vehiculo = vehiculo;
        this.garrAwa = garrAwa;
        this.garrAlk = garrAlk;
        this.sixAwa = sixAwa;
        this.sixAlk = sixAlk;
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

    public int getGarrAwa() {
        return garrAwa;
    }

    public void setGarrAwa(int garrAwa) {
        this.garrAwa = garrAwa;
    }

    public int getGarrAlk() {
        return garrAlk;
    }

    public void setGarrAlk(int garrAlk) {
        this.garrAlk = garrAlk;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getSixAwa() {
        return sixAwa;
    }

    public void setSixAwa(int sixAwa) {
        this.sixAwa = sixAwa;
    }

    public int getSixAlk() {
        return sixAlk;
    }

    public void setSixAlk(int sixAlk) {
        this.sixAlk = sixAlk;
    }
}
