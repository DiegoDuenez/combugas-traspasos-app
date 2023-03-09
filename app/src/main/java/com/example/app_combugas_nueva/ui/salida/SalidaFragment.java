
package com.example.app_combugas_nueva.ui.salida;

import static android.content.Context.SENSOR_SERVICE;
import static android.view.Gravity.CENTER;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.app_combugas_nueva.MainActivity;
import com.example.app_combugas_nueva.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.seismic.ShakeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import api.CurrentFragment;
import api.URL;
import modelos.Alert;
import modelos.AlertProgress;
import modelos.OnSwipeTouchListener;
import modelos.VolleyS;

public class SalidaFragment extends Fragment implements ShakeDetector.Listener {
    private SalidaViewModel mViewModel;
    private VolleyS vs;
    private RequestQueue requestQueue;
    private RadioGroup radioGroup;
    private RadioButton radioButtonVehiculo, radioButtonPublico;
    private EditText editTextOperador, editTextVehiculo, editTextFolio, editTextTanques30Salida, editTextTanques45Salida;
    private TextView textViewVehiculo, textViewOperador, textViewAyudante, textViewCredito, textView30kg, textView45kg;
    private Button btnSalida, btnCredito, btnQR;
    private String id_estacion, id_empleado, id_operador, id_vehiculo, tipo_empleado, precio_30, precio_45, credito = "0";
    private Integer tanques_30_salida = 0, tanques_45_salida = 0;
    private int tanques_30_inventario, tanques_45_inventario;
    private boolean existVehiculo,existeOperador, ingresoFolio, folioValido, tanques30Validos, tanques45Validos;
    private LinearLayout linearLayoutCredito;
    public Alert alerta;
    public AlertProgress alertaProgress;
    public ImageView img;

    public static SalidaFragment newInstance() {
        return new SalidaFragment();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        alerta = new Alert(getActivity());
        alertaProgress = new AlertProgress(getActivity(), R.style.AppCompatAlertDialogStyle);

        if (getArguments() != null) {
            id_estacion = getArguments().getString("id_estacion");
            id_empleado = getArguments().getString("id_empleado");
            tanques_30_inventario = getArguments().getInt("tanques_30");
            tanques_45_inventario = getArguments().getInt("tanques_45");
            tipo_empleado = getArguments().getString("tipo_empleado");
        }

        SharedPreferences prefe = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
        precio_30 = prefe.getString("precio_30","0");
        precio_45 = prefe.getString("precio_45","0");

        View root = inflater.inflate(R.layout.salida_fragment, container, false);
        CurrentFragment.currentFragment = "NAV_SALIDA";
        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        int sensorDelay = SensorManager.SENSOR_DELAY_GAME;
        sd.start(sensorManager, sensorDelay);

        editTextVehiculo = root.findViewById(R.id.editTextVehiculo);
        editTextOperador = root.findViewById(R.id.editTextOperador);
        editTextFolio = root.findViewById(R.id.editTextFolio);
        editTextFolio.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        editTextTanques30Salida = root.findViewById(R.id.editTextTanques30Salida);
        editTextTanques45Salida = root.findViewById(R.id.editTextTanques45Salida);
        linearLayoutCredito = root.findViewById(R.id.linearLayoutCredito);
        textViewVehiculo = root.findViewById(R.id.textViewVehiculo);
        textViewAyudante = root.findViewById(R.id.textViewAyudante);
        textViewOperador = root.findViewById(R.id.textViewOperador);
        textViewCredito = root.findViewById(R.id.textViewCredito);
        textView30kg = root.findViewById(R.id.textView30kg);
        textView45kg = root.findViewById(R.id.textView45kg);
        //img = root.findViewById(R.id.imgQR);
        btnSalida = root.findViewById(R.id.btnSalida);
        btnCredito = root.findViewById(R.id.btnCredito);
        btnQR = root.findViewById(R.id.btnQR);
        radioGroup = root.findViewById(R.id.radioGroup2);
        radioButtonVehiculo = root.findViewById(R.id.radio_vehiculo);
        radioButtonPublico = root.findViewById(R.id.radio_publico);
        editTextVehiculo.requestFocus();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = radioGroup.findViewById(checkedId);
                int index = radioGroup.indexOfChild(radioButton);
                switch (index) {
                    case 0:
                        textViewVehiculo.setVisibility(View.VISIBLE);
                        editTextVehiculo.setVisibility(View.VISIBLE);
                        textViewOperador.setVisibility(View.VISIBLE);
                        editTextOperador.setVisibility(View.VISIBLE);
                        textViewAyudante.setVisibility(View.VISIBLE);
                        editTextFolio.setVisibility(View.VISIBLE);
                        linearLayoutCredito.setVisibility(View.GONE);
                        credito = "0";
                        editTextVehiculo.requestFocus();
                        if (editTextFolio.getText().toString().length() > 5) {
                            editTextFolio.setText("");
                        }
                        editTextFolio.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
                        //ultimoValeVehiculo(id_estacion);
                        break;
                    case 1:
                        textViewVehiculo.setVisibility(View.GONE);
                        editTextVehiculo.setVisibility(View.GONE);
                        textViewOperador.setVisibility(View.GONE);
                        editTextOperador.setVisibility(View.GONE);
                        textViewAyudante.setVisibility(View.VISIBLE);
                        editTextFolio.setVisibility(View.VISIBLE);
                        linearLayoutCredito.setVisibility(View.VISIBLE);
                        editTextFolio.requestFocus();
                        editTextFolio.setFilters(new InputFilter[]{});
                        //ultimoValePublico(id_estacion);
                        break;
                }
            }
        });

        editTextTanques30Salida.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (editTextTanques30Salida.getText().toString().startsWith("0")){
                    editTextTanques30Salida.setText(editTextTanques30Salida.getText().toString().replace("0", ""));
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editTextTanques45Salida.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (editTextTanques45Salida.getText().toString().startsWith("0")){
                    editTextTanques45Salida.setText(editTextTanques45Salida.getText().toString().replace("0", ""));
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrador = new IntentIntegrator(getActivity());
                integrador.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrador.setPrompt("Escanea el QR del vale.");
                integrador.setCameraId(0);
                integrador.setBeepEnabled(true);
                integrador.setBarcodeImageEnabled(true);
                integrador.forSupportFragment(SalidaFragment.this).initiateScan();
            }
        });

        btnSalida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSalida.setEnabled(false);
                if (radioButtonVehiculo.isChecked()) {
                    validarCamposVehiculo();
                }
                if (radioButtonPublico.isChecked()) {
                    validarCamposPublico();
                }
            }
        });

        btnCredito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (credito.equals("0")) {
                    btnCredito.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ghost_button_3));
                    btnCredito.setTextColor(ContextCompat.getColor(getContext(), R.color.verde));
                    btnCredito.setText("Sí");
                    credito = "1";
                } else {
                    btnCredito.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ghost_button_2));
                    btnCredito.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                    btnCredito.setText("NO");
                    credito = "0";
                }
            }
        });
        ConstraintLayout cl = root.findViewById(R.id.contenedorSalida);
        ScrollView scv = root.findViewById(R.id.layoutScrollCil);

       editTextVehiculo.addTextChangedListener(new TextWatcher() {
           boolean isNotBackspace = true;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isNotBackspace = count>before;
            }
            @Override
            public void afterTextChanged(Editable s) {
                editTextVehiculo.removeTextChangedListener(this);
                if (s.length() == 0) {
                    isNotBackspace = true;
                }else if(isNotBackspace){
                    if(s.toString().equals("h") || s.toString().equals("vh")){
                        editTextVehiculo.append("-");
                    }
                }
                editTextVehiculo.addTextChangedListener(this);
            }
        });

        //new SoapCall().execute();
        return root;
    }


    @Override public void hearShake() {
        /*if(editTextFolio.isFocused() && !editTextFolio.getText().toString().isEmpty()){
            editTextFolio.setText("");
            Toast.makeText(getActivity(), "Folio borrado", Toast.LENGTH_SHORT).show();
        }
        if(editTextOperador.isFocused() && !editTextOperador.getText().toString().isEmpty()){
            editTextOperador.setText("");
            Toast.makeText(getActivity(), "Operador borrado", Toast.LENGTH_SHORT).show();
        }
        if(editTextVehiculo.isFocused() && !editTextVehiculo.getText().toString().isEmpty()){
            editTextVehiculo.setText("");
            Toast.makeText(getActivity(), "Vehículo borrado", Toast.LENGTH_SHORT).show();
        }
        if(editTextTanques30Salida.isFocused() && !editTextTanques30Salida.getText().toString().isEmpty()){
            editTextTanques30Salida.setText("");
            Toast.makeText(getActivity(), "Cantidad 30 kg borrada", Toast.LENGTH_SHORT).show();
        }
        if(editTextTanques45Salida.isFocused() && !editTextTanques45Salida.getText().toString().isEmpty()){
            editTextTanques45Salida.setText("");
            Toast.makeText(getActivity(), "Cantidad 45 kg borrada", Toast.LENGTH_SHORT).show();
        }*/
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null){
            if(result.getContents() == null){
                Toast.makeText(getActivity(), "Escaneo cancelado", Toast.LENGTH_LONG).show();
            }else{
                editTextFolio.setText(result.getContents());
                editTextFolio.requestFocus();
                editTextFolio.setSelection(editTextFolio.getText().length());
                //generateQRCode(result.getContents());
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }

    }


    public void validarCamposPublico(){
        if(editTextTanques30Salida.getText().toString().equals("") && editTextTanques45Salida.getText().toString().equals("") || editTextTanques30Salida.getText().toString().equals("0") && editTextTanques45Salida.getText().toString().equals("0")
                || editTextTanques30Salida.getText().toString().equals("") && editTextTanques45Salida.getText().toString().equals("0") || editTextTanques30Salida.getText().toString().equals("0") && editTextTanques45Salida.getText().toString().equals("")){
            tanques30Validos = false;
            tanques45Validos = false;

            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);

            alerta.content("AVISO", "Debes ingresar una cantidad de tanques")
                    .possitiveButton("Ok", "close")
                    .show(false)
                    .style("Possitive", R.color.red);
            btnSalida.setEnabled(true);

        }
        else {

            if(editTextTanques30Salida.getText().toString().length() > 0){
                tanques_30_salida = Integer.parseInt(editTextTanques30Salida.getText().toString());
            }
            else{
                tanques_30_salida = 0;
            }
            if(editTextTanques45Salida.getText().toString().length() > 0){
                tanques_45_salida = Integer.parseInt(editTextTanques45Salida.getText().toString());
            }else{
                tanques_45_salida = 0;
            }
            if(tanques_30_salida > tanques_30_inventario){
                tanques30Validos = false;
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);


                alerta.content("AVISO", "La cantidad de salida es superior a la del inventario (30 kg)")
                        .possitiveButton("Ok", "close")
                        .show(false)
                        .style("Possitive", R.color.red);
                editTextTanques30Salida.requestFocus();
                btnSalida.setEnabled(true);
            }
            else{
                tanques30Validos = true;
            }
            if(tanques_45_salida > tanques_45_inventario){
                tanques45Validos = false;
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);


                alerta.content("AVISO", "La cantidad de salida es superior a la del inventario (45 kg)")
                        .possitiveButton("Ok", "close")
                        .show(false)
                        .style("Possitive", R.color.red);
                editTextTanques45Salida.requestFocus();
                btnSalida.setEnabled(true);

            }
            else{
                tanques45Validos = true;

            }
        }
        if(editTextFolio.getText().toString().equals("")){
            folioValido = false;
            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);

            alerta.content("AVISO", "Debes ingresar un número de folio")
                    .possitiveButton("Ok", "close")
                    .show(false)
                    .style("Possitive", R.color.red);
            editTextFolio.requestFocus();
            btnSalida.setEnabled(true);



        }
        else{
            getValeExistencia(editTextFolio.getText().toString());
        }
    }

    public void validarCamposVehiculo() {
        if (editTextVehiculo.getText().toString().equals("")) {
            existVehiculo = false;
            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);

            alerta.content("AVISO", "Debes ingresar un vehículo")
                    .possitiveButton("Ok", "close")
                    .show(false)
                    .style("Possitive", R.color.red);
            editTextVehiculo.requestFocus();
            btnSalida.setEnabled(true);


        } else {
            getVehiculo(editTextVehiculo.getText().toString());
        }
        if (editTextOperador.getText().toString().equals("")) {
            existeOperador = false;

            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);

            alerta.content("AVISO", "Debes ingresar un número de operador")
                    .possitiveButton("Ok", "close")
                    .show(false)
                    .style("Possitive", R.color.red);
            editTextOperador.requestFocus();
            btnSalida.setEnabled(true);


        } else {
            getOperador(editTextOperador.getText().toString());
        }
        if (editTextTanques30Salida.getText().toString().equals("") && editTextTanques45Salida.getText().toString().equals("") || editTextTanques30Salida.getText().toString().equals("0") && editTextTanques45Salida.getText().toString().equals("0")
                || editTextTanques30Salida.getText().toString().equals("") && editTextTanques45Salida.getText().toString().equals("0") || editTextTanques30Salida.getText().toString().equals("0") && editTextTanques45Salida.getText().toString().equals("")) {
            tanques30Validos = false;
            tanques45Validos = false;

            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);

            alerta.content("AVISO", "Debes ingresar una cantidad de tanques")
                    .possitiveButton("Ok", "close")
                    .show(false)
                    .style("Possitive", R.color.red);
            btnSalida.setEnabled(true);


        } else {

            if (editTextTanques30Salida.getText().toString().length() > 0) {
                tanques_30_salida = Integer.parseInt(editTextTanques30Salida.getText().toString());
            }
            else{
                tanques_30_salida = 0;
            }
            if (editTextTanques45Salida.getText().toString().length() > 0) {
                tanques_45_salida = Integer.parseInt(editTextTanques45Salida.getText().toString());
            }
            else{
                tanques_45_salida = 0;
            }
            if (tanques_30_salida > tanques_30_inventario) {
                tanques30Validos = false;

                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);

                alerta.content("AVISO", "La cantidad de salida es superior a la del inventario (30 kg)")
                        .possitiveButton("Ok", "close")
                        .show(false)
                        .style("Possitive", R.color.red);
                editTextTanques30Salida.requestFocus();
                btnSalida.setEnabled(true);

            }
            else{
                tanques30Validos = true;
            }
            if (tanques_45_salida > tanques_45_inventario) {
                tanques45Validos = false;

                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);

                alerta.content("AVISO", "La cantidad de salida es superior a la del inventario (45 kg)")
                        .possitiveButton("Ok", "close")
                        .show(false)
                        .style("Possitive", R.color.red);
                editTextTanques45Salida.requestFocus();
                btnSalida.setEnabled(true);

            }
            else{
                tanques45Validos = true;
            }
        }
    }

    public void getValeExistencia(String folio){
        String url = URL.URL_VALES + "?folio=" + folio + "&id_estacion="+id_estacion + "&tipo_vale=CILINDRO";
        vs = VolleyS.getInstance(getContext());
        requestQueue = vs.getRequestQueue();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray mJsonArray = response.getJSONArray("vale");
                            if(mJsonArray.length() > 0){
                                JSONObject mJsonObject = mJsonArray.getJSONObject(0);
                                String folio = mJsonObject.getString("folio");

                                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                                v.vibrate(500);

                                alerta.content("AVISO", "El folio " + folio + " ya fue registrado en otro vale")
                                        .possitiveButton("Ok", "close")
                                        .show(false)
                                        .style("Possitive", R.color.red);
                                folioValido = false;
                                editTextFolio.requestFocus();
                                btnSalida.setEnabled(true);

                            }
                            else{

                                folioValido = true;
                                AlertDialog.Builder alertaForm = new AlertDialog.Builder(getActivity());

                                String cantidad30 = "";
                                String cantidad45 = "";
                                String creditoSeleccionado = "";
                                if(editTextTanques30Salida.getText().toString().isEmpty()){
                                    cantidad30= "0";
                                }
                                else{
                                    cantidad30 = editTextTanques30Salida.getText().toString();
                                }
                                if(editTextTanques45Salida.getText().toString().isEmpty()){
                                    cantidad45 = "0";
                                }
                                else{
                                    cantidad45 = editTextTanques45Salida.getText().toString();
                                }

                                if(radioButtonVehiculo.isChecked()){
                                    alertaForm.setMessage(Html.fromHtml("" +
                                            "<br> Folio: <b><span style='color:#de312e;'>"+ editTextFolio.getText().toString()+"</span></b> <br> <span style='color:black;'> ("+ editTextFolio.getText().toString().length() +" caracteres) </span> <br> <br>" +
                                            "Vehículo: <b><span style='color:#de312e;'>" + editTextVehiculo.getText().toString()+"</span></b> <br> <br>"+
                                            "Operador: <b><span style='color:#de312e;'>" + editTextOperador.getText().toString()+"</span></b> <br> <br>" +
                                            "30 kg: <b><span style='color:#de312e;'>" + cantidad30 +"</span></b> <br> <br>" +
                                            "45 kg: <b><span style='color:#de312e;'>" + cantidad45 +"</span></b>"
                                            ))
                                            .setTitle("¿LOS DATOS SON CORRECTOS?");
                                }
                                else if(radioButtonPublico.isChecked()){
                                    if(credito == "0") {
                                        creditoSeleccionado = "No";
                                    }
                                    else if(credito == "1"){
                                        creditoSeleccionado = "Sí";
                                    }
                                    alertaForm.setMessage(Html.fromHtml("" +
                                            "<br> Folio: <b><span style='color:#de312e;'>"+ editTextFolio.getText().toString()+"</span></b> <br> <span style='color:black;'> ("+ editTextFolio.getText().toString().length() +" caracteres) </span> <br> <br>" +
                                            "30 kg: <b><span style='color:#de312e;'>" + cantidad30 +"</span></b> <br> <br>" +
                                            "45 kg: <b><span style='color:#de312e;'>" + cantidad45 +"</span></b> <br> <br>" +
                                            "Crédito: <b><span style='color:#de312e;'>" + creditoSeleccionado +"</span></b>"
                                    )).setTitle("¿LOS DATOS SON CORRECTOS?");
                                }

                                alertaForm.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        if(!existeOperador && !existVehiculo && folioValido && tanques30Validos && tanques45Validos){
                                            salidaInventario(id_estacion, tanques_30_salida, tanques_45_salida);
                                        }
                                        else if(existeOperador && existVehiculo && folioValido && tanques30Validos && tanques45Validos ) {
                                            salidaInventario(id_estacion, tanques_30_salida, tanques_45_salida);
                                        }
                                    }
                                });
                                alertaForm.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        editTextVehiculo.requestFocus();
                                        Toast.makeText(getActivity(), "Cambia la información que necesites", Toast.LENGTH_LONG).show();
                                        btnSalida.setEnabled(true);
                                    }
                                });

                                if(!alerta.isOpen){
                                    AlertDialog dialog = alertaForm.create();
                                    dialog.setCanceledOnTouchOutside(false);
                                    dialog.setCancelable(false);
                                    dialog.show();
                                    TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                                    textView.setTextSize(25);
                                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
                                }

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

    public void getVehiculo(String descripcion){
        vs = VolleyS.getInstance(getContext());
        requestQueue = vs.getRequestQueue();
        String url = URL.URL_VEHICULOS + "?descripcion="+descripcion;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray mJsonArray = null;
                        try {
                            mJsonArray = response.getJSONArray("vehiculo");
                            JSONObject mJsonObject = mJsonArray.getJSONObject(0);
                            id_vehiculo  = mJsonObject.getString("id_vehiculo");
                            if(!id_vehiculo.equals("")){
                                existVehiculo = true;
                            }
                            else{
                                alerta.content("AVISO", "El vehículo ingresado no existe")
                                        .possitiveButton("Ok", "close")
                                        .show(false)
                                        .style("Possitive", R.color.red);
                                existVehiculo = false;
                                editTextVehiculo.requestFocus();
                                btnSalida.setEnabled(true);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            alerta.content("AVISO", "El vehículo ingresado no existe")
                                    .possitiveButton("Ok", "close")
                                    .show(false)
                                    .style("Possitive", R.color.red);
                            existVehiculo = false;
                            editTextVehiculo.requestFocus();
                            btnSalida.setEnabled(true);

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
                alerta.content("AVISO", "El vehículo ingresado no existe")
                        .possitiveButton("Ok", "close")
                        .show(false)
                        .style("Possitive", R.color.red);
                editTextVehiculo.requestFocus();
                existVehiculo = false;
                btnSalida.setEnabled(true);

            }
        });
        requestQueue.add(request);
    }

    public void getOperador(String num_empleado){
        vs = VolleyS.getInstance(getContext());
        requestQueue = vs.getRequestQueue();
        String url = URL.URL_EMPLEADOS + "?funcion=''&num_operador="+num_empleado;
        Log.i("URLSALIDA", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray mJsonArray = null;
                        try {
                            mJsonArray = response.getJSONArray("operador");
                            JSONObject mJsonObject = mJsonArray.getJSONObject(0);
                            id_operador = mJsonObject.getString("id_operador");
                            if(!id_operador.equals("")){
                                existeOperador = true;
                                if(editTextFolio.getText().toString().equals("")){
                                    folioValido = false;
                                    Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                                    v.vibrate(500);

                                    alerta.content("AVISO", "Debes ingresar un número de folio")
                                            .possitiveButton("Ok", "close")
                                            .show(false)
                                            .style("Possitive", R.color.red);
                                    editTextFolio.requestFocus();
                                    btnSalida.setEnabled(true);

                                }
                                else{
                                    getValeExistencia(editTextFolio.getText().toString());
                                }
                            }
                            else{
                                existeOperador = false;
                                btnSalida.setEnabled(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
                alerta.content("AVISO", "El operador ingresado no existe")
                        .possitiveButton("Ok", "close")
                        .show(false)
                        .style("Possitive", R.color.red);
                existeOperador = false;
                editTextOperador.requestFocus();
                btnSalida.setEnabled(true);

            }
        });
        requestQueue.add(request);
    }

    public void salidaInventario(String id_estacion, Integer t30, Integer t45){
        vs = VolleyS.getInstance(getContext());
        requestQueue = vs.getRequestQueue();
        JSONObject data = new JSONObject();
        if(radioButtonVehiculo.isChecked()){
            try {
                data.put("tanques_30", "" + t30);
                data.put("tanques_45", "" + t45);
                data.put("id_estacion", "" + id_estacion);
                data.put("id_empleado", "" + id_empleado);
                data.put("id_vehiculo", "" + editTextVehiculo.getText().toString());
                data.put("id_operador", "" + editTextOperador.getText().toString());
                data.put("no_vale", "" + editTextFolio.getText().toString());
                data.put("precio_30", "" + precio_30);
                data.put("precio_45", "" + precio_45);
                data.put("tipo_vale", "CILINDRO");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(radioButtonPublico.isChecked()){
            try {
                data.put("tanques_30", "" + editTextTanques30Salida.getText().toString());
                data.put("tanques_45", "" + editTextTanques45Salida.getText().toString());
                data.put("id_estacion", "" + id_estacion);
                data.put("id_empleado", "" + id_empleado);
                data.put("precio_30", "" + precio_30);
                data.put("precio_45", "" + precio_45);
                data.put("no_vale", "" + editTextFolio.getText().toString());
                data.put("credito", "" + credito);
                data.put("tipo_vale", "CILINDRO");
            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }
        String url = URL.URL_MOVIMIENTOS + "?funcion=registrarMovimiento";
        alertaProgress.content("Guardando salida", "Por favor espere, no cierre la aplicación.").show(false);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Bundle inventarioBundle  = new Bundle();
                        inventarioBundle.putString("id_estacion", id_estacion);
                        inventarioBundle.putString("id_empleado", id_empleado);
                        inventarioBundle.putString("tipo_empleado", tipo_empleado);

                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                                .navigate(R.id.nav_menu, inventarioBundle);
                        Toast.makeText(getContext(), "Se ha guardado la salida", Toast.LENGTH_SHORT).show();
                        alertaProgress.close();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
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
                    JSONObject tanque30 = jsonArray.getJSONObject(0);
                    precio_30 = tanque30.getString("_precioProducto");
                    JSONObject tanque45 = jsonArray.getJSONObject(1);
                    precio_45 = tanque45.getString("_precioProducto");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
            }
        }
    }
}