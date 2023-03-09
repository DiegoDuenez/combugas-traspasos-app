package com.example.app_combugas_nueva.awa.salida;

import static android.content.Context.SENSOR_SERVICE;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.app_combugas_nueva.R;
import com.example.app_combugas_nueva.ui.salida.SalidaFragment;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jraska.falcon.Falcon;
import com.squareup.seismic.ShakeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import api.CurrentFragment;
import api.URL;
import modelos.Alert;
import modelos.AlertProgress;
import modelos.VolleyS;

public class SalidaAwaFragment extends Fragment implements ShakeDetector.Listener{

    private SalidaAwaViewModel mViewModel;
    private VolleyS vs;
    private RequestQueue requestQueue;
    private RadioGroup radioGroup;
    private RadioButton radioButtonVehiculo,radioButtonPublico;
    private EditText editTextOperador,editTextVehiculo, editTextFolio, editTextAwa19Salida, editTextAlk19Salida, editTextAlkSixSalida, editTextAwaSixSalida;
    private TextView textViewVehiculo, textViewOperador, textViewFolio, textViewCredito;
    private Button btnSalida, btnCredito, btnQR;
    private String id_estacion, id_empleado, id_operador, id_vehiculo, tipo_empleado, precio_awa, precio_alk, precio_six_awa, precio_six_alk,  credito = "0";
    private Integer garrafones_awa_salida = 0, garrafones_alk_salida = 0, six_awa_salida = 0, six_alk_salida = 0;
    private int garrafones_awa_inventario, garrafones_alk_inventario, six_alk_inventario, six_awa_inventario;
    private boolean existVehiculo,existeOperador,folioValido,garrafonesAwaValidos,garrafonesAlkValidos, sixAwaValidos, sixAlkValidos;
    private LinearLayout linearLayoutCredito;
    public Alert alerta;
    public AlertProgress alertaProgress;

    public static SalidaAwaFragment newInstance() {
        return new SalidaAwaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        alerta = new Alert(getActivity());
        alertaProgress = new AlertProgress(getActivity(), R.style.AppCompatAlertDialogStyleAwa);

        if(getArguments() != null){
            id_estacion = getArguments().getString("id_estacion");
            id_empleado = getArguments().getString("id_empleado");
            garrafones_awa_inventario = getArguments().getInt("garrafones_awa");
            garrafones_alk_inventario = getArguments().getInt("garrafones_alk");
            six_awa_inventario = getArguments().getInt("six_awa");
            six_alk_inventario = getArguments().getInt("six_alk");
            tipo_empleado = getArguments().getString("tipo_empleado");
        }

        SharedPreferences prefe = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
        precio_awa = prefe.getString("precio_awa","0");
        precio_alk = prefe.getString("precio_alk","0");
        precio_six_alk = prefe.getString("precio_six_alk", "0");
        precio_six_awa = prefe.getString("precio_six_awa", "0");


        View root = inflater.inflate(R.layout.salida_awa_fragment, container, false);
        CurrentFragment.currentFragment = "NAV_SALIDA_AWA";

        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        int sensorDelay = SensorManager.SENSOR_DELAY_GAME;
        sd.start(sensorManager, sensorDelay);

        editTextVehiculo = root.findViewById(R.id.editTextVehiculoAwa);
        editTextOperador = root.findViewById(R.id.editTextOperadorAwa);
        editTextFolio = root.findViewById(R.id.editTextFolioAwa);
        editTextAwa19Salida = root.findViewById(R.id.editTextGarrafonesAwaSalida);
        editTextAlk19Salida = root.findViewById(R.id.editTextGarrafonesAlkSalida);
        editTextAwaSixSalida = root.findViewById(R.id.editTextSixAwaSalida);
        editTextAlkSixSalida = root.findViewById(R.id.editTextSixAlkSalida);
        linearLayoutCredito = root.findViewById(R.id.linearLayoutCreditoAWA);
        textViewVehiculo = root.findViewById(R.id.textViewVehiculoAwa);
        textViewFolio = root.findViewById(R.id.textViewFolioAwa);
        textViewOperador = root.findViewById(R.id.textViewOperadorAwa);
        textViewCredito = root.findViewById(R.id.textViewCreditoAwa);
        btnSalida = root.findViewById(R.id.btnSalidaAwa);
        btnCredito = root.findViewById(R.id.btnCreditoAwa);
        btnQR = root.findViewById(R.id.btnQR);
        radioGroup = root.findViewById(R.id.radioGroupAwa);
        radioButtonVehiculo = root.findViewById(R.id.radio_vehiculo_awa);
        radioButtonPublico= root.findViewById(R.id.radio_publico_awa);
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
                        textViewFolio.setVisibility(View.VISIBLE);
                        editTextFolio.setVisibility(View.VISIBLE);
                        linearLayoutCredito.setVisibility(View.GONE);
                        credito = "0";
                        editTextVehiculo.requestFocus();
                        //ultimoValeVehiculo(id_estacion);
                        break;
                    case 1:
                        textViewVehiculo.setVisibility(View.GONE);
                        editTextVehiculo.setVisibility(View.GONE);
                        textViewOperador.setVisibility(View.GONE);
                        editTextOperador.setVisibility(View.GONE);
                        textViewFolio.setVisibility(View.VISIBLE);
                        editTextFolio.setVisibility(View.VISIBLE);
                        linearLayoutCredito.setVisibility(View.VISIBLE);
                        editTextFolio.requestFocus();
                        // ultimoValePublico(id_estacion);
                        break;
                }
            }
        });


        editTextAwa19Salida.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (editTextAwa19Salida.getText().toString().startsWith("0")){
                    editTextAwa19Salida.setText(editTextAwa19Salida.getText().toString().replace("0", ""));
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editTextAlk19Salida.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (editTextAlk19Salida.getText().toString().startsWith("0")){
                    editTextAlk19Salida.setText(editTextAlk19Salida.getText().toString().replace("0", ""));
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editTextAwaSixSalida.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (editTextAwaSixSalida.getText().toString().startsWith("0")){
                    editTextAwaSixSalida.setText(editTextAwaSixSalida.getText().toString().replace("0", ""));
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editTextAlkSixSalida.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (editTextAlkSixSalida.getText().toString().startsWith("0")){
                    editTextAlkSixSalida.setText(editTextAlkSixSalida.getText().toString().replace("0", ""));
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        Handler handler = new Handler();
        int TIEMPO = 500;
        btnSalida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSalida.setEnabled(false);
                if(radioButtonVehiculo.isChecked()){
                    validarCamposVehiculo();
                }
                if(radioButtonPublico.isChecked()){
                    validarCamposPublico();
                }
            }
        });
        btnCredito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(credito.equals("0")){
                    btnCredito.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ghost_button_3));
                    btnCredito.setTextColor(ContextCompat.getColor(getContext(), R.color.verde));
                    btnCredito.setText("Sí");
                    credito = "1";
                }
                else{
                    btnCredito.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ghost_button_2));
                    btnCredito.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                    btnCredito.setText("NO");
                    credito = "0";
                }
            }
        });
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
                    if(s.toString().equals("aw") || s.toString().equals("w") || s.toString().equals("h")){
                        editTextVehiculo.append("-");
                    }
                }
                editTextVehiculo.addTextChangedListener(this);
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
                integrador.forSupportFragment(SalidaAwaFragment.this).initiateScan();
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
        if(editTextAwa19Salida.isFocused() && !editTextAwa19Salida.getText().toString().isEmpty()){
            editTextAwa19Salida.setText("");
            Toast.makeText(getActivity(), "Cantidad awa borrada", Toast.LENGTH_SHORT).show();
        }
        if(editTextAlk19Salida.isFocused() && !editTextAlk19Salida.getText().toString().isEmpty()){
            editTextAlk19Salida.setText("");
            Toast.makeText(getActivity(), "Cantidad alkalina borrada", Toast.LENGTH_SHORT).show();
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

            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
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
                    .style("Possitive", R.color.blueAwa);
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
                    .style("Possitive", R.color.blueAwa);
            editTextOperador.requestFocus();
            btnSalida.setEnabled(true);

        } else {
            getOperador(editTextOperador.getText().toString());
        }

        if(editTextAwa19Salida.getText().toString().equals("")){

        }

        if (editTextAwa19Salida.getText().toString().equals("") && editTextAlk19Salida.getText().toString().equals("") && editTextAwaSixSalida.getText().toString().equals("") && editTextAlkSixSalida.getText().toString().equals("")
                || editTextAwa19Salida.getText().toString().equals("0") && editTextAlk19Salida.getText().toString().equals("0") && editTextAwaSixSalida.getText().toString().equals("0") && editTextAlkSixSalida.getText().toString().equals("0")
                || editTextAwa19Salida.getText().toString().equals("") && editTextAlk19Salida.getText().toString().equals("0") && editTextAwaSixSalida.getText().toString().equals("") && editTextAlkSixSalida.getText().toString().equals("0")
                || editTextAwa19Salida.getText().toString().equals("0") && editTextAlk19Salida.getText().toString().equals("") && editTextAwaSixSalida.getText().toString().equals("0") && editTextAlkSixSalida.getText().toString().equals("")
                || editTextAwa19Salida.getText().toString().equals("") && editTextAlk19Salida.getText().toString().equals("") && editTextAwaSixSalida.getText().toString().equals("0") && editTextAlkSixSalida.getText().toString().equals("0")
                || editTextAwa19Salida.getText().toString().equals("0") && editTextAlk19Salida.getText().toString().equals("0") && editTextAwaSixSalida.getText().toString().equals("") && editTextAlkSixSalida.getText().toString().equals("")
        ) {

            garrafonesAwaValidos = false;
            garrafonesAlkValidos= false;
            sixAlkValidos = false;
            sixAlkValidos = false;

            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);

            alerta.content("AVISO", "Debes ingresar una cantidad de garrafones")
                    .possitiveButton("Ok", "close")
                    .show(false)
                    .style("Possitive", R.color.blueAwa);
            btnSalida.setEnabled(true);

        } else {
            if (editTextAwa19Salida.getText().toString().length() > 0) {
                garrafones_awa_salida = Integer.parseInt(editTextAwa19Salida.getText().toString());
            }
            else {
                garrafones_awa_salida = 0;
            }
            if (editTextAlk19Salida.getText().toString().length() > 0) {
                garrafones_alk_salida = Integer.parseInt(editTextAlk19Salida.getText().toString());
            }
            else {
                garrafones_alk_salida = 0;
            }
            if(editTextAwaSixSalida.getText().toString().length() > 0){
                six_awa_salida = Integer.parseInt(editTextAwaSixSalida.getText().toString());
            }
            else{
                six_awa_salida = 0;
            }
            if(editTextAlkSixSalida.getText().toString().length() > 0){
                six_alk_salida = Integer.parseInt(editTextAlkSixSalida.getText().toString());
            }
            else{
                six_alk_salida = 0;
            }

            if (garrafones_awa_salida > garrafones_awa_inventario) {
                garrafonesAwaValidos = false;
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);

                alerta.content("AVISO", "La cantidad de salida es superior a la del inventario (AWA Normal)")
                        .possitiveButton("Ok", "close")
                        .show(false)
                        .style("Possitive", R.color.blueAwa);
                editTextAwa19Salida.requestFocus();
                btnSalida.setEnabled(true);
            }
            else{
                garrafonesAwaValidos = true;
            }
            if (garrafones_alk_salida > garrafones_alk_inventario) {
                Toast.makeText(getActivity(), "salida alk: " + garrafones_alk_salida, Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "salida inventario: " + garrafones_alk_inventario, Toast.LENGTH_SHORT).show();

                garrafonesAlkValidos = false;
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);

                alerta.content("AVISO", "La cantidad de salida superior a la del inventario (AWA Alkalina)")
                        .possitiveButton("Ok", "close")
                        .show(false)
                        .style("Possitive", R.color.blueAwa);
                editTextAlk19Salida.requestFocus();
                btnSalida.setEnabled(true);

            }
            else{
               garrafonesAlkValidos = true;
            }

            if (six_awa_salida > six_awa_inventario) {
                sixAwaValidos = false;
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);

                alerta.content("AVISO", "La cantidad de salida superior a la del inventario (Six AWA)")
                        .possitiveButton("Ok", "close")
                        .show(false)
                        .style("Possitive", R.color.blueAwa);
                editTextAwaSixSalida.requestFocus();
                btnSalida.setEnabled(true);

            }
            else{
                sixAwaValidos = true;
            }

            if (six_alk_salida > six_alk_inventario) {
                sixAlkValidos = false;
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);

                alerta.content("AVISO", "La cantidad de salida superior a la del inventario (Six AWA Alkalina)")
                        .possitiveButton("Ok", "close")
                        .show(false)
                        .style("Possitive", R.color.blueAwa);
                editTextAlkSixSalida.requestFocus();
                btnSalida.setEnabled(true);

            }
            else{
                sixAlkValidos = true;
            }
        }
    }

    public void validarCamposPublico(){
        if(editTextAwa19Salida.getText().toString().equals("") && editTextAlk19Salida.getText().toString().equals("") && editTextAwaSixSalida.getText().toString().equals("") && editTextAlkSixSalida.getText().toString().equals("")
                || editTextAwa19Salida.getText().toString().equals("0") && editTextAlk19Salida.getText().toString().equals("0") && editTextAwaSixSalida.getText().toString().equals("0") && editTextAlkSixSalida.getText().toString().equals("0")
                || editTextAwa19Salida.getText().toString().equals("") && editTextAlk19Salida.getText().toString().equals("0") && editTextAwaSixSalida.getText().toString().equals("") && editTextAlkSixSalida.getText().toString().equals("0")
                || editTextAwa19Salida.getText().toString().equals("0") && editTextAlk19Salida.getText().toString().equals("") && editTextAwaSixSalida.getText().toString().equals("0") && editTextAlkSixSalida.getText().toString().equals("")
                || editTextAwa19Salida.getText().toString().equals("") && editTextAlk19Salida.getText().toString().equals("") && editTextAwaSixSalida.getText().toString().equals("0") && editTextAlkSixSalida.getText().toString().equals("0")
                || editTextAwa19Salida.getText().toString().equals("0") && editTextAlk19Salida.getText().toString().equals("0") && editTextAwaSixSalida.getText().toString().equals("") && editTextAlkSixSalida.getText().toString().equals("")
        ){
            garrafonesAwaValidos = false;
            garrafonesAlkValidos = false;
            sixAlkValidos = false;
            sixAlkValidos = false;
            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);

            alerta.content("AVISO", "Debes ingresar una cantidad de garrafones")
                    .possitiveButton("Ok", "close")
                    .show(false)
                    .style("Possitive", R.color.blueAwa);
            btnSalida.setEnabled(true);

        }
        else {
            if (editTextAwa19Salida.getText().toString().length() > 0) {
                garrafones_awa_salida = Integer.parseInt(editTextAwa19Salida.getText().toString());
            }
            else {
                garrafones_awa_salida = 0;
            }
            if (editTextAlk19Salida.getText().toString().length() > 0) {
                garrafones_alk_salida = Integer.parseInt(editTextAlk19Salida.getText().toString());
            }
            else {
                garrafones_alk_salida = 0;
            }
            if(editTextAwaSixSalida.getText().toString().length() > 0){
                six_awa_salida = Integer.parseInt(editTextAwaSixSalida.getText().toString());
            }
            else{
                six_awa_salida = 0;
            }
            if(editTextAlkSixSalida.getText().toString().length() > 0){
                six_alk_salida = Integer.parseInt(editTextAlkSixSalida.getText().toString());
            }
            else{
                six_alk_salida = 0;
            }
            if(garrafones_awa_salida > garrafones_awa_inventario){
                garrafonesAwaValidos = false;
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);

                alerta.content("AVISO", "La cantidad de salida es superior a la del inventario (AWA Normal)")
                        .possitiveButton("Ok", "close")
                        .show(false)
                        .style("Possitive", R.color.blueAwa);
                editTextAwa19Salida.requestFocus();
                btnSalida.setEnabled(true);

            }
            else{
                garrafonesAwaValidos= true;
            }
            if(garrafones_alk_salida > garrafones_alk_inventario){
                garrafonesAlkValidos= false;
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);

                alerta.content("AVISO", "La cantidad de salida es superior a la del inventario (AWA Alkalina)")
                        .possitiveButton("Ok", "close")
                        .show(false)
                        .style("Possitive", R.color.blueAwa);
                editTextAlk19Salida.requestFocus();
                btnSalida.setEnabled(true);

            }
            else{
                garrafonesAlkValidos = true;
            }
            if (six_awa_salida > six_awa_inventario) {
                sixAwaValidos = false;
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);

                alerta.content("AVISO", "La cantidad de salida superior a la del inventario (Six AWA)")
                        .possitiveButton("Ok", "close")
                        .show(false)
                        .style("Possitive", R.color.blueAwa);
                editTextAwaSixSalida.requestFocus();
                btnSalida.setEnabled(true);

            }
            else{
                sixAwaValidos = true;
            }

            if (six_alk_salida > six_alk_inventario) {
                sixAlkValidos = false;
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);

                alerta.content("AVISO", "La cantidad de salida superior a la del inventario (Six AWA Alkalina)")
                        .possitiveButton("Ok", "close")
                        .show(false)
                        .style("Possitive", R.color.blueAwa);
                editTextAlkSixSalida.requestFocus();
                btnSalida.setEnabled(true);

            }
            else{
                sixAlkValidos = true;
            }
        }
        if(editTextFolio.getText().toString().equals("")){
            folioValido = false;
            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);

            alerta.content("AVISO", "Debes ingresar un número de folio")
                    .possitiveButton("Ok", "close")
                    .show(false)
                    .style("Possitive", R.color.blueAwa);
            editTextFolio.requestFocus();
            btnSalida.setEnabled(true);

        }
        else{
            getValeExistencia(editTextFolio.getText().toString());
        }
    }

    public void getValeExistencia(String folio){
        String url = URL.URL_VALES + "?folio=" + folio + "&id_estacion="+id_estacion + "&tipo_vale=AWA";
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
                                        .style("Possitive", R.color.blueAwa);
                                folioValido = false;
                                editTextFolio.requestFocus();
                                btnSalida.setEnabled(true);
                            }
                            else{
                                folioValido = true;
                                AlertDialog.Builder alertaForm = new AlertDialog.Builder(getActivity());
                                String cantidadAlk = "", cantidadAwa = "", cantidadAwaSix = "", cantidadAlkSix = "";
                                String creditoSeleccionado = "";
                                if(editTextAlk19Salida.getText().toString().isEmpty()){
                                    cantidadAlk = "0";
                                }
                                else{
                                    cantidadAlk = editTextAlk19Salida.getText().toString();
                                }
                                if(editTextAwa19Salida.getText().toString().isEmpty()){
                                    cantidadAwa = "0";
                                }
                                else{
                                    cantidadAwa = editTextAwa19Salida.getText().toString();
                                }
                                if(editTextAwaSixSalida.getText().toString().isEmpty()){
                                    cantidadAwaSix = "0";
                                }
                                else{
                                    cantidadAwaSix = editTextAwaSixSalida.getText().toString();
                                }
                                if(editTextAlkSixSalida.getText().toString().isEmpty()){
                                    cantidadAlkSix = "0";
                                }
                                else{
                                    cantidadAlkSix = editTextAlkSixSalida.getText().toString();
                                }

                                if(radioButtonVehiculo.isChecked()){
                                    alertaForm.setMessage(Html.fromHtml("" +
                                            " <br> Folio: <b><span style='color:#1a8dc2;'>"+ editTextFolio.getText().toString()+"</span></b> <br> <span style='color:black;'> ("+ editTextFolio.getText().toString().length() +" caracteres) </span> <br> <br>" +
                                            "Vehículo: <b><span style='color:#1a8dc2;'>" + editTextVehiculo.getText().toString()+"</span></b> <br> <br>"+
                                            "Operador: <b><span style='color:#1a8dc2;'>" + editTextOperador.getText().toString()+"</span></b> <br> <br>" +
                                            "Garr. Awa: <b><span style='color:#1a8dc2;'>" + cantidadAwa +"</span></b> <br> <br>" +
                                            "Garr. Alk.: <b><span style='color:#339632;'>" + cantidadAlk +"</span></b> <br> <br> "
                                            /*"Six Awa: <b><span style='color:#1a8dc2;'>" + cantidadAwaSix + "</span></b> <br> <br>" +
                                            "Six Alk.: <b><span style='color:#339632;'>" + cantidadAlkSix + "</span></b>"*/
                                    )).setTitle("¿LOS DATOS SON CORRECTOS?");
                                }
                                else if(radioButtonPublico.isChecked()){
                                    if(credito == "0") {
                                        creditoSeleccionado = "No";
                                    }
                                    else if(credito == "1"){
                                        creditoSeleccionado = "Sí";
                                    }
                                    alertaForm.setMessage(Html.fromHtml("" +
                                            " <br> Folio: <b><span style='color:#1a8dc2;'>"+ editTextFolio.getText().toString()+"</span></b> <br> <span style='color:black;'> ("+ editTextFolio.getText().toString().length() +" caracteres) </span> <br> <br>" +
                                            "Garr. Awa: <b><span style='color:#1a8dc2;'>" + cantidadAwa +"</span></b> <br> <br>" +
                                            "Garr. Alk.: <b><span style='color:#339632;'>" + cantidadAlk +"</span></b> <br> <br>"+
                                            /*"Six Awa: <b><span style='color:#1a8dc2;'>" + cantidadAwaSix + "</span></b> <br> <br>" +
                                            "Six Alk.: <b><span style='color:#339632;'>" + cantidadAlkSix + "</span></b> <br> <br>" +*/
                                            "Crédito: <b><span style='color:#1a8dc2;'>" + creditoSeleccionado +"</span></b>"
                                    )).setTitle("¿LOS DATOS SON CORRECTOS?");
                                }

                                alertaForm.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        if(!existeOperador && !existVehiculo && folioValido && garrafonesAwaValidos && garrafonesAlkValidos && sixAwaValidos && sixAlkValidos){
                                            salidaInventario(id_estacion, garrafones_awa_salida, garrafones_alk_salida, six_awa_salida, six_alk_salida);
                                        }
                                        else if(existeOperador && existVehiculo && folioValido && garrafonesAwaValidos && garrafonesAlkValidos && sixAwaValidos && sixAlkValidos) {
                                            salidaInventario(id_estacion, garrafones_awa_salida, garrafones_alk_salida, six_awa_salida, six_alk_salida);
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
                                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.blueAwa));
                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blueAwa));

                                    //saveTextAsFile("log-inventarios", "Hola");
                                    //generateNoteOnSD(getActivity(), "log-inventarios", "Hola precione guardar");

                                    //takeScreenshot(dialog, "prueba2");

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

    private void saveTextAsFile(String filename, String content){

        String fileName = filename + ".txt";
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileName);

        if(!file.exists()){
            file.mkdir();
        }
        try{
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.close();
            Toast.makeText(getContext(), "Se guardo el archivo", Toast.LENGTH_SHORT).show();
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
            Toast.makeText(getContext(), "No se encontro el archivo", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al guardar", Toast.LENGTH_SHORT).show();

        }

    }

    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                                        .style("Possitive", R.color.blueAwa);
                                existVehiculo = false;
                                editTextVehiculo.requestFocus();
                                btnSalida.setEnabled(true);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            alerta.content("AVISO", "El vehículo ingresado no existe")
                                    .possitiveButton("Ok", "close")
                                    .show(false)
                                    .style("Possitive", R.color.blueAwa);
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
                        .style("Possitive", R.color.blueAwa);
                existVehiculo = false;
                editTextVehiculo.requestFocus();
                btnSalida.setEnabled(true);
            }
        });
        requestQueue.add(request);
    }

    public void getOperador(String num_empleado){
        vs = VolleyS.getInstance(getContext());
        requestQueue = vs.getRequestQueue();
        String url = URL.URL_EMPLEADOS + "?funcion=''&num_operador="+num_empleado;

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
                                            .style("Possitive", R.color.blueAwa);
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
                        .style("Possitive", R.color.blueAwa);
                existeOperador = false;
                editTextOperador.requestFocus();
                btnSalida.setEnabled(true);
            }
        });
        requestQueue.add(request);
    }

    public void salidaInventario(String id_estacion, Integer gAwa, Integer gAlk, Integer sAwa, Integer sAlk){
        vs = VolleyS.getInstance(getContext());
        requestQueue = vs.getRequestQueue();
        JSONObject data = new JSONObject();
        if(radioButtonVehiculo.isChecked()){
            try {
                data.put("garrafones_19_awa", "" + gAwa);
                data.put("garrafones_19_alk", "" + gAlk);
                data.put("six_awa", "" + sAwa);
                data.put("six_alk", "" + sAlk);
                data.put("id_estacion", "" + id_estacion);
                data.put("id_empleado", "" + id_empleado);
                data.put("id_vehiculo", "" + editTextVehiculo.getText().toString());
                data.put("id_operador", "" + editTextOperador.getText().toString());
                data.put("no_vale", "" + editTextFolio.getText().toString());
                data.put("precio_awa", "" + precio_awa);
                data.put("precio_alk", "" + precio_alk);
                data.put("precio_six_alk", "" + precio_six_alk);
                data.put("precio_six_awa", "" + precio_six_awa);
                data.put("tipo_vale", "AWA");
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            Log.i("JSONDATA", data.toString());

        }
        if(radioButtonPublico.isChecked()){
            try {
                data.put("garrafones_19_awa", "" + gAwa);
                data.put("garrafones_19_alk", "" + gAlk);
                data.put("six_awa", "" + sAwa);
                data.put("six_alk", "" + sAlk);
                data.put("id_estacion", "" + id_estacion);
                data.put("id_empleado", "" + id_empleado);
                data.put("precio_awa", "" + precio_awa);
                data.put("precio_alk", "" + precio_alk);
                data.put("precio_six_alk", "" + precio_six_alk);
                data.put("precio_six_awa", "" + precio_six_awa);
                data.put("no_vale", "" + editTextFolio.getText().toString());
                data.put("credito", "" + credito);
                data.put("tipo_vale", "AWA");
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
                                .navigate(R.id.nav_menu_awa, inventarioBundle);
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


    private class SoapCall extends AsyncTask<Integer, String, SoapObject> {
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
                Log.i("SOAP AWA", response.toString());
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
                    JSONObject precioAwa = jsonArray.getJSONObject(2);
                    precio_awa = precioAwa.getString("_precioProducto"); //32
                    JSONObject precioAlk = jsonArray.getJSONObject(4);
                    precio_alk = precioAlk.getString("_precioProducto"); //38
                    Log.i("SOAP AWA ", precio_awa);
                    Log.i("SOAP ALK ", precio_alk);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
            }
        }
    }



    private static File takeScreenshot(AlertDialog dialog, String filename) {
        Date now = new Date();
        CharSequence format = android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        //Toast.makeText(view.getContext(), "entro 1", Toast.LENGTH_SHORT).show();
        try {

            View view = AlertDialog.class.cast(dialog).getWindow().getDecorView().getRootView();

            Toast.makeText(view.getContext(), "entro EN 2", Toast.LENGTH_SHORT).show();

            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now;
            File fileDir = new File(mPath);
            if(!fileDir.exists()){
                boolean mkdir = fileDir.mkdir();
            }

            String path = mPath + "/" + filename + format + ".jpeg";

            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            File imageFile = new File(path);
            FileOutputStream fos = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);

            //Toast.makeText(view.getContext(), path, Toast.LENGTH_SHORT).show();
            Log.v("PATH IMG", path);


            fos.flush();
            fos.close();

            return imageFile;


            // create bitmap screen capture
           /* View v1 = getActivity().getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);


            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();*/


        } catch (Throwable e) {
           // Toast.makeText(getActivity(), "entro fallo", Toast.LENGTH_SHORT).show();
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }

        return null;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SalidaAwaViewModel.class);
        // TODO: Use the ViewModel
    }

}