package modelos;

public class HistorialVoucher {

    private String carburista, estacion, operador, banco, numAut, importe, tarjeta, fecha;

    public HistorialVoucher(String carburista, String estacion, String operador, String banco, String numAut, String importe, String tarjeta, String fecha){
        this.carburista = carburista;
        this.estacion = estacion;
        this.operador = operador;
        this.banco = banco;
        this.numAut = numAut;
        this.importe = importe;
        this.tarjeta = tarjeta;
        this.fecha = fecha;
    }

    public String getCarburista() {
        return carburista;
    }

    public void setCarburista(String carburista) {
        this.carburista = carburista;
    }

    public String getEstacion() {
        return estacion;
    }

    public void setEstacion(String estacion) {
        this.estacion = estacion;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getNumAut() {
        return numAut;
    }

    public void setNumAut(String numAut) {
        this.numAut = numAut;
    }

    public String getImporte() {
        return importe;
    }

    public void setImporte(String importe) {
        this.importe = importe;
    }

    public String getTarjeta() {
        return tarjeta;
    }

    public void setTarjeta(String tarjeta) {
        this.tarjeta = tarjeta;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
