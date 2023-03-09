package com.example.app_combugas_nueva;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import modelos.AlertProgress;
import modelos.Connectivity;
import modelos.Empleado;
import modelos.VolleyS;
import api.URL;
import modelos.Alert;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.Calendar;
import api.URL;

public class MainActivity extends AppCompatActivity {

    private VolleyS vs;
    private RequestQueue requestQueue;

    private EditText editTextNumeroEmpleado;
    private Button btnIniciarSesion;
    private TextView textViewAño, textViewVersion;
    private AlertProgress alertaProgress;
    private int permisoUbicacionFine, permisoUbicacion, permisoWriteStorage, permisoReadStorage, permisoTelefono, ACTIVITY_REQUEST_CODE = 123;
    private boolean isFastConnection, isConnectedMobile;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alertaProgress = new AlertProgress(MainActivity.this, R.style.AppCompatAlertDialogStyle);

        textViewAño = findViewById(R.id.textViewAño);
        textViewVersion = findViewById(R.id.textViewVersion);
        textViewVersion.setText("v " + BuildConfig.VERSION_NAME);

        if(URL.PORT_SERVER.equals("8032")){
            Toast.makeText(getApplicationContext(), "Modo de pruebas", Toast.LENGTH_LONG).show();
        }

        Calendar cal= Calendar.getInstance();
        int año  =  cal.get(Calendar.YEAR);
        textViewAño.setText("©"+año+" AW Software");

        this.permisoUbicacion = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        this.permisoUbicacionFine = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        //this.permisoReadStorage = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        //this.permisoWriteStorage = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (this.permisoUbicacion != PackageManager.PERMISSION_GRANTED && this.permisoUbicacionFine != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, this.ACTIVITY_REQUEST_CODE);
            Toast.makeText(getApplicationContext(),"La app necesita los permisos de ubicación.", Toast.LENGTH_SHORT).show();
            return;
        }

        this.editTextNumeroEmpleado = findViewById(R.id.editTextNumeroEmpleado);
        this.btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        editTextNumeroEmpleado.requestFocus();
        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextNumeroEmpleado.getText().toString().isEmpty()){
                    Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vib.vibrate(500);
                    Alert alerta = new Alert(MainActivity.this, "AVISO", "Debes ingresar un número de empleado.");
                    alerta.possitiveButton("Ok", "close")
                            .show(false)
                            .style("Possitive", R.color.red);
                }
                else{
                    iniciarSesion();
                }
            }
        });

        this.editTextNumeroEmpleado.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    if(editTextNumeroEmpleado.getText().toString().isEmpty()){
                        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vib.vibrate(500);
                        Alert alerta = new Alert(MainActivity.this, "AVISO", "Debes ingresar un número de empleado.");
                        alerta.possitiveButton("Ok", "close")
                                .show(false)
                                .style("Possitive", R.color.red);
                    }
                    else{
                        iniciarSesion();
                    }

                    return true;

                }
                return false;
            }
        });



        this.editTextNumeroEmpleado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(getApplicationContext(), "HOla", Toast.LENGTH_SHORT).show();
               /* ¿IntentIntegrator integrador = new IntentIntegrator(MainActivity.this);
                integrador.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrador.setPrompt("Escanea tu número de empleado");
                integrador.setCameraId(0);
                integrador.setBeepEnabled(true);
                integrador.setBarcodeImageEnabled(true);
                integrador.initiateScan();*/
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAffinity(MainActivity.this);
    }

    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null){
            if(result.getContents() == null){
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_LONG).show();
            }else{
                //Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                editTextNumeroEmpleado.setText(result.getContents());
                iniciarSesion();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    public static boolean isConnectedWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean isConnectedMobile(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACTIVITY_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public void iniciarSesion() {

        alertaProgress.content("Iniciando sesión", "Por favor espere.").show(false);

        String numeroEmpleado = this.editTextNumeroEmpleado.getText().toString();

        vs = VolleyS.getInstance(this.getApplicationContext());
        requestQueue = vs.getRequestQueue();

        String url = URL.URL_EMPLEADOS + "?funcion=iniciarSesion";

        JSONObject data = new JSONObject();
        try {
            data.put("numero_empleado", numeroEmpleado);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, data,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray mJsonArray = response.getJSONArray("empleado");

                            JSONObject mJsonObject = mJsonArray.getJSONObject(0);
                            String id_empleado = mJsonObject.getString("id_empleado");
                            String nombre = mJsonObject.getString("nombre");
                            String numero_empleado = mJsonObject.getString("numero_empleado");
                            String id_estacion = mJsonObject.getString("id_estacion");
                            String nombre_estacion = mJsonObject.getString("nombre_estacion");
                            String tipo_empleado = mJsonObject.getString("idtipo_empleado");
                            Empleado empleado = new Empleado(id_empleado, nombre, numero_empleado, id_estacion, nombre_estacion, tipo_empleado);
                            Intent intent = new Intent(MainActivity.this, MenuPrincipalActivity.class);
                            intent.putExtra("empleado_logeado", empleado);
                            alertaProgress.close();
                            startActivity(intent);
                            finish();

                        } catch (JSONException e) {
                           e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                alertaProgress.close();

                String codigoError = "ERROR ";
                String message="Hubo un problema, checa tu conectividad o contacta al área de soporte.";
                if (error instanceof NetworkError || error instanceof NoConnectionError || error instanceof AuthFailureError) {
                    message = "No se ha podido conectar a internet, por favor checa tu conexión e intentalo nuevamente.";
                }
                else if (error instanceof ServerError){
                    if(error.networkResponse.statusCode == 400){
                        codigoError = "AVISO";
                        message = "El número de empleado no existe.";
                    }
                    else if(error.networkResponse.statusCode == 500){
                        message = "Hubo un problema, contacta al área de soporte.";
                    }
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
                Alert alerta = new Alert(MainActivity.this, ""+codigoError, ""+ message);
                alerta.possitiveButton("Ok", "close")
                        .show(false)
                        .style("Possitive", R.color.red);
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);

    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }


}