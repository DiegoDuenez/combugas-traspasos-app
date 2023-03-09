package api;

public class URL {

    /* PRUEBAS */
    /*public static String PORT_SERVER = "8032";
    public static String URL_SERVER = "http://cgtng.sytes.net:"+PORT_SERVER+"/api_combugas/api/";*/

    /* PRODUCCION */
    public static String PORT_SERVER = "8031";
    public static String URL_SERVER = "http://cgtng.sytes.net:"+PORT_SERVER+"/api_cawa/api/";
    public static String URL_EMPLEADOS = URL_SERVER + "empleados/index.php";
    public static String URL_ESTACIONES = URL_SERVER + "estaciones/index.php";
    public static String URL_VEHICULOS = URL_SERVER + "vehiculos/index.php";
    public static String URL_MOVIMIENTOS = URL_SERVER + "movimientos/index.php";
    public static String URL_VALES = URL_SERVER + "vales/index.php";
    public static String URL_VOUCHERS = URL_SERVER + "vouchers/index.php";


}


