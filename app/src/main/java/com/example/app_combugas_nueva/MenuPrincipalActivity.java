package com.example.app_combugas_nueva;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.app_combugas_nueva.awa.salida.SalidaAwaFragment;
import com.example.app_combugas_nueva.ui.inventario.InventarioFragment;
import com.example.app_combugas_nueva.ui.salida.SalidaFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import api.URL;
import modelos.Alert;
import modelos.AlertProgress;
import modelos.Empleado;
import modelos.VolleyS;

import static androidx.core.location.LocationManagerCompat.isLocationEnabled;

public class MenuPrincipalActivity extends AppCompatActivity {

    private VolleyS vs;
    private RequestQueue requestQueue;
    private TextView txtViewEmpleado;
    private TextView txtViewEstacion;
    private TextView txtViewModo;
    private Button btnTanques, btnGarrafones, btnVouchers, btnYelow;
    private LocationManager locationManager;
    private Location location;
    private Criteria criteria;
    public String id_estacion_original;
    public Empleado empleadoLogeado;

    private int permisoUbicacion, permisoUbicacionFine, permisoWriteStorage, permisoReadStorage;
    private int permisoTelefono;
    private double latitud = 0;
    private double longitud = 0;
    public String id_estacion = "";
    private String id_empleado;
    private String nombre_empleado;
    private String tipo_empleado;
    private String uso_gps;
    private String precio_tanque_30 = "0", precio_tanque_45 = "0" , precio_awa, precio_alk, precio_six_awa, precio_six_alk;
    private AlertProgress alertaProgress;
    //private  Alert alerta;
    private String numeroCelular;

    private String IMEI = "";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        alertaProgress = new AlertProgress(MenuPrincipalActivity.this, R.style.AppCompatAlertDialogStyle);

        setContentView(R.layout.activity_menu_principal);

        this.txtViewEmpleado = findViewById(R.id.textViewEmpleado);
        this.txtViewEstacion = findViewById(R.id.textViewEstacion);
        this.btnTanques = findViewById(R.id.btnTanques);
        this.btnGarrafones = findViewById(R.id.btnAwa);
        this.btnYelow = findViewById(R.id.btnYelow);
        this.btnVouchers = findViewById(R.id.btnVouchers);
        this.txtViewModo = findViewById(R.id.textViewModo);

        if (URL.PORT_SERVER.equals("8032")) {
            this.txtViewModo.setVisibility(View.VISIBLE);
           // this.btnVouchers.setVisibility(View.VISIBLE);
        }

        new SoapCall().execute();

        this.criteria = new Criteria();

        empleadoLogeado = (Empleado) getIntent().getSerializableExtra("empleado_logeado");
        id_empleado = empleadoLogeado.getId_empleado();
        id_estacion_original = empleadoLogeado.getId_estacion();
        nombre_empleado = empleadoLogeado.getNombre();
        tipo_empleado = empleadoLogeado.getTipo_empleado();
        uso_gps = empleadoLogeado.getUso_gps();

        this.permisoUbicacion = ActivityCompat.checkSelfPermission(MenuPrincipalActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        this.permisoUbicacionFine = ActivityCompat.checkSelfPermission(MenuPrincipalActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        this.permisoTelefono = ActivityCompat.checkSelfPermission(MenuPrincipalActivity.this, Manifest.permission.READ_PHONE_STATE);

        if (this.permisoUbicacion != PackageManager.PERMISSION_GRANTED
                && this.permisoUbicacionFine != PackageManager.PERMISSION_GRANTED ) {
            Toast.makeText(getApplicationContext(), "La app no tiene los permisos para acceder a la ubicación.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(this.permisoTelefono != PackageManager.PERMISSION_GRANTED){
            finish();
            Toast.makeText(getApplicationContext(), "La app no tiene los permisos para acceder al celular.", Toast.LENGTH_SHORT).show();
            return;
        }

        alertaProgress.content("Cargando estación", "Por favor espere.").show(false);

        if(uso_gps.equals("0")){
            // SIN GPS
            txtViewEstacion.setText("Estación: " + empleadoLogeado.getNombre_estacion());
            txtViewEmpleado.setText("Empleado: " + empleadoLogeado.getNombre());
            if(!empleadoLogeado.getNombre_estacion().equals("")){
                alertaProgress.close();
            }
            id_estacion = id_estacion_original;

        }
        else{
            // CON GPS*/
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, new Listener());
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, new Listener());
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                txtViewEstacion.setText("Estación: " + empleadoLogeado.getNombre_estacion());
                txtViewEmpleado.setText("Empleado: " + empleadoLogeado.getNombre());
                alertaProgress.close();
            }
            if (location != null) {
                handleLatLng(location.getLatitude(), location.getLongitude());
            }

        }

        IMEI = getDeviceId(this);

        Log.d("IMEI", IMEI);

        compararImei(IMEI);

        btnTanques.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                Intent iTanques = new Intent(MenuPrincipalActivity.this, NavigationActivity.class);
                iTanques.putExtra("id_estacion", id_estacion);
                iTanques.putExtra("tipo_empleado", tipo_empleado);
                iTanques.putExtra("nombre_empleado", nombre_empleado);
                iTanques.putExtra("tipo", "tanques");
                iTanques.putExtra("id_empleado", id_empleado);
                startActivity(iTanques);
            }
        });

        btnGarrafones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iTanques = new Intent(MenuPrincipalActivity.this, NavigationActivity.class);
                iTanques.putExtra("id_estacion", id_estacion);
                iTanques.putExtra("tipo_empleado", tipo_empleado);
                iTanques.putExtra("nombre_empleado", nombre_empleado);
                iTanques.putExtra("tipo", "awa");
                iTanques.putExtra("id_empleado", id_empleado);
                startActivity(iTanques);
            }
        });

        btnYelow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iTanques = new Intent(MenuPrincipalActivity.this, NavigationActivity.class);
                iTanques.putExtra("id_estacion", id_estacion);
                iTanques.putExtra("tipo_empleado", tipo_empleado);
                iTanques.putExtra("nombre_empleado", nombre_empleado);
                iTanques.putExtra("tipo", "yelow");
                iTanques.putExtra("id_empleado", id_empleado);
                startActivity(iTanques);
            }
        });

        btnVouchers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iTanques = new Intent(MenuPrincipalActivity.this, NavigationActivity.class);
                iTanques.putExtra("id_estacion", id_estacion);
                iTanques.putExtra("tipo_empleado", tipo_empleado);
                iTanques.putExtra("nombre_empleado", nombre_empleado);
                iTanques.putExtra("tipo", "voucher");
                iTanques.putExtra("id_empleado", id_empleado);
                startActivity(iTanques);
            }
        });

    }

    public void compararImei(String imei){

        alertaProgress.content("Validando dispositivo", "Por favor espere.").show(false);

        String url = URL.URL_IMEI + "?imei=" + imei;

        vs = VolleyS.getInstance(this.getApplicationContext());
        requestQueue = vs.getRequestQueue();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray data = (JSONArray) response.get("imei");

                            if(data.length() <= 0){

                                Toast.makeText(MenuPrincipalActivity.this, "Este dispositivo no se encuentra registrado", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else{
                                alertaProgress.close();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(request);

    }

    public static String getDeviceId(Context context) {

        String deviceId;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return "";
                }
            }
            assert mTelephony != null;
            if (mTelephony.getDeviceId() != null)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    deviceId = mTelephony.getImei();
                }else {
                    deviceId = mTelephony.getDeviceId();
                }
            } else {
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        }
        Log.d("deviceId", deviceId);
        return deviceId;
    }

    public void obtenerEstacionActual(Double latitud, Double longitud){
        String url =  URL.URL_ESTACIONES + "?funcion=estacionCercana&lat=" + latitud +"&long="+longitud;
        Log.i("URL TAG", url);
        vs = VolleyS.getInstance(this.getApplicationContext());
        requestQueue = vs.getRequestQueue();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray mJsonArray = response.getJSONArray("estacion_cerca");
                        JSONObject mJsonObject = mJsonArray.getJSONObject(0);
                        id_estacion = mJsonObject.getString("id_estacion");
                        String nombre_estacion = mJsonObject.getString("nombre_estacion");
                        Log.v("TAG 3", "" + id_estacion + " " + nombre_estacion);

                        empleadoActualizarEstacion(id_estacion);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        if(id_estacion == null){
                            id_estacion = id_estacion_original;
                        }

                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(500);
                        Alert alerta = new Alert(MenuPrincipalActivity.this, "AVISO", "Ninguna estación cercana a tu ubicación fue encontrada.");
                        alerta.possitiveButton("Ok", "kill", MenuPrincipalActivity.this)
                                .show(false)
                                .style("Possitive", R.color.red);
                    }

                }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String codigoError = "ERROR ";
                String message="Hubo un problema, checa tu conectividad o contacta al área de soporte.";
                if (error instanceof NetworkError || error instanceof NoConnectionError || error instanceof AuthFailureError) {
                    message = "No se ha podido conectar a internet, por favor checa tu conexión.";
                } else if (error instanceof ServerError){
                    message = "Hubo un problema, contacta al área de soporte.";
                } else if (error instanceof ParseError) {
                    message = "Hubo un problema, contacta al área de soporte.";
                } else if (error instanceof TimeoutError) {
                    message = "Tiempo de espera agotado.";
                }
                else{
                    codigoError += getString(R.string.error_desconocido);
                    message = "Hubo un problema, contacta al área de soporte.";
                }

                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
                Alert alerta = new Alert(MenuPrincipalActivity.this, ""+codigoError, ""+message);
                alerta.possitiveButton("Ok", "kill", MenuPrincipalActivity.this)
                        .show(false)
                        .style("Possitive", R.color.red);

            }
        });
        requestQueue.add(request);
    }


    public void empleadoActualizarEstacion(String id_estacion){

        String url = URL.URL_EMPLEADOS + "?funcion=actEstacionEmpleado&id_empleado=" + this.empleadoLogeado.getId_empleado();

        vs = VolleyS.getInstance(this.getApplicationContext());
        requestQueue = vs.getRequestQueue();

        JSONObject data = new JSONObject();
        try {
            data.put("id_estacion", id_estacion);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        getEmpleado(empleadoLogeado.getId_empleado());

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(request);
    }


    public void getEmpleado(String id_empleado){
        String url = URL.URL_EMPLEADOS + "?id_empleado=" + id_empleado;
        vs = VolleyS.getInstance(this.getApplicationContext());
        requestQueue = vs.getRequestQueue();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray mJsonArray = response.getJSONArray("empleado");
                            JSONObject mJsonObject = mJsonArray.getJSONObject(0);
                            String nombre_estacion = mJsonObject.getString("nombre_estacion");
                            String nombre_empleado = mJsonObject.getString("nombre");
                            if(!nombre_empleado.equals("") && !nombre_estacion.equals("") && !precio_tanque_30.equals("0") && !precio_tanque_45.equals("0") && !precio_awa.equals("0") && !precio_alk.equals("0")){
                                txtViewEstacion.setText("Estación: " + nombre_estacion);
                                txtViewEmpleado.setText("Empleado: " + nombre_empleado);
                                SharedPreferences prefe = getSharedPreferences("datos", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefe.edit();
                                editor.putString("precio_30", precio_tanque_30);
                                editor.putString("precio_45", precio_tanque_45);
                                editor.putString("precio_awa", precio_awa);
                                editor.putString("precio_alk", precio_alk);
                                editor.putString("precio_six_alk", precio_six_alk);
                                editor.putString("precio_six_awa", precio_six_awa);
                                editor.commit();
                                alertaProgress.close();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w("Error getEmpleado", error.getMessage());
                Toast.makeText(MenuPrincipalActivity.this, "Ninguna estación cercana fue encontrada", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);

    }

    private void handleLatLng(double latitude, double longitude){
        this.latitud = latitude;
        this.longitud = longitude;
        Log.i("TAG", "(" + latitude + "," + longitude + ")");
        obtenerEstacionActual(latitude, longitude);
    }

    private class Listener implements LocationListener {
        public void onLocationChanged(Location location) {
            longitud = location.getLongitude();
            latitud = location.getLatitude();
            handleLatLng(latitud, longitud);
        }

        public void onProviderDisabled(String provider){}
        public void onProviderEnabled(String provider){}
        public void onStatusChanged(String provider, int status, Bundle extras){}
    }

    private class SoapCall extends AsyncTask<Integer, String, SoapObject>{
        private static final String NAMESPACE = "awserver.noip.me:8889/";;
        private static final String URLSERVER = "http://cgtng.sytes.net:8889/wscli/";
        public static final String METHOD_NAME =  "getPrecios";
        public static final String URLPEDIDOS = URLSERVER +"ws/pedidos.asmx";
        SoapObject response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected SoapObject doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE httpTransport = new HttpTransportSE(URLPEDIDOS);
            try {
                httpTransport.call(NAMESPACE + METHOD_NAME, envelope);
                response = (SoapObject)envelope.getResponse();
                Log.i("SOAPP", response.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return response;
        }
        @Override
        protected void onPostExecute(SoapObject soapObject) {
            super.onPostExecute(soapObject);
            if(soapObject != null){
                try {
                    JSONArray jsonArray = new JSONArray(soapObject.getProperty("Data").toString());
                    // ESTE FOR ES PARA CHECAR LAS POSICIONES DE LOS PRECIOS (NO BORRAR)
                    /*for(int i = 0; i <= jsonArray.length(); i++){
                        Log.i("SOAPP item pos " + i, String.valueOf(jsonArray.getJSONObject(i)));
                    }*/
                    JSONObject tanque30 = jsonArray.getJSONObject(0);
                    precio_tanque_30 = tanque30.getString("_precioProducto");
                    JSONObject tanque45 = jsonArray.getJSONObject(1);
                    precio_tanque_45 = tanque45.getString("_precioProducto");
                    JSONObject precioAwa = jsonArray.getJSONObject(2);
                    precio_awa = precioAwa.getString("_precioProducto"); //32
                    JSONObject precioAlk = jsonArray.getJSONObject(4);
                    precio_alk = precioAlk.getString("_precioProducto"); //38
                    JSONObject precioSixAwa = jsonArray.getJSONObject(5);
                    precio_six_awa = precioSixAwa.getString("_precioProducto");
                    JSONObject precioSixAlk = jsonArray.getJSONObject(8);
                    precio_six_alk = precioSixAlk.getString("_precioProducto");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
            }
        }
    }




}