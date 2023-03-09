package com.example.app_combugas_nueva.vouchers.registro;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.DialogInterface;
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
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.app_combugas_nueva.R;
import com.google.zxing.common.StringUtils;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import api.CurrentFragment;
import api.URL;
import modelos.Alert;
import modelos.AlertProgress;
import modelos.VolleyS;

public class VoucherRegistroFragment extends Fragment {

    private VoucherRegistroViewModel mViewModel;

    private EditText editTextTarjeta, editTextImporte, editTextNumAut;
    private VolleyS vs;
    private RequestQueue requestQueue;
    private String id_estacion, id_empleado, tipo_empleado;
    public AlertProgress alertaProgress;
    private Button btnGuardarVoucher;
    public Alert alerta;

    public static VoucherRegistroFragment newInstance() {
        return new VoucherRegistroFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        alertaProgress = new AlertProgress(getActivity(), R.style.AppCompatAlertDialogStyle);
        alerta = new Alert(getActivity());

        if(getArguments() != null){
            id_estacion = getArguments().getString("id_estacion");
            id_empleado = getArguments().getString("id_empleado");
            tipo_empleado = getArguments().getString("tipo_empleado");
        }

       View root = inflater.inflate(R.layout.voucher_registro_fragment, container, false);

        CurrentFragment.currentFragment = "NAV_REGISTRO_VOUCHERS";


        editTextTarjeta = root.findViewById(R.id.editTextTarjeta);
       editTextTarjeta.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
       editTextImporte = root.findViewById(R.id.editTextImporte);
       editTextNumAut = root.findViewById(R.id.editTextNumAut);
       editTextNumAut.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
       btnGuardarVoucher = root.findViewById(R.id.btnGuardarVoucher);

       editTextTarjeta.requestFocus();

        //ditTextImporte.keyListener = DigitsKeyListener.getInstance("0123456789");

       btnGuardarVoucher.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               btnGuardarVoucher.setEnabled(false);
               validarCampos();

           }
       });


        editTextImporte.addTextChangedListener(new TextWatcher() {
            boolean isNotBackspace = true;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isNotBackspace = count>before;
                if(editTextImporte.getText().toString().equals("$") ){
                    editTextImporte.setText("");
                }

            }
            @Override
            public void afterTextChanged(Editable s) {
                editTextImporte.removeTextChangedListener(this);
                if(isNotBackspace){
                    /*if(editTextImporte.getText().toString().length() <= 2){
                        editTextImporte.getText().insert(0, "$");
                    }
                    if (editTextImporte == null) return;*/
                    String inputString = s.toString();
                    editTextImporte.removeTextChangedListener(this);
                    String cleanString = inputString.toString().replaceAll("[$,.]", "");
                    BigDecimal bigDecimal = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                    String  converted = NumberFormat.getCurrencyInstance().format(bigDecimal);
                    editTextImporte.setText(converted);
                    editTextImporte.setSelection(converted.length());
                    editTextImporte.addTextChangedListener(this);
                }
                editTextImporte.addTextChangedListener(this);
            }
        });

        return root;
    }

    public void validarCampos(){

        String numAut = editTextNumAut.getText().toString();
        String importe = editTextImporte.getText().toString();
        String tarjeta = editTextTarjeta.getText().toString();

        if(tarjeta.isEmpty()){
            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
            alerta.content("AVISO", "Debes ingresar los ultimos 4 digítos de la tarjeta")
                    .possitiveButton("Ok", "close")
                    .show(false)
                    .style("Possitive", R.color.red);
            editTextTarjeta.requestFocus();
            btnGuardarVoucher.setEnabled(true);
        }
        else if(tarjeta.length() < 4){
            int faltantes =  4 - tarjeta.length();
            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
            alerta.content("AVISO", "Debes ingresar los ultimos 4 digítos de la tarjeta. (Faltan " + faltantes  + " dígitos)")
                    .possitiveButton("Ok", "close")
                    .show(false)
                    .style("Possitive", R.color.red);
            editTextTarjeta.requestFocus();
            btnGuardarVoucher.setEnabled(true);
        }


        if(importe.isEmpty()){
            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
            alerta.content("AVISO", "Debes ingresar el importe")
                    .possitiveButton("Ok", "close")
                    .show(false)
                    .style("Possitive", R.color.red);
            editTextImporte.requestFocus();
            btnGuardarVoucher.setEnabled(true);
        }

        if(numAut.isEmpty()){
            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
            alerta.content("AVISO", "Debes ingresar el número de autorización")
                    .possitiveButton("Ok", "close")
                    .show(false)
                    .style("Possitive", R.color.red);
            editTextNumAut.requestFocus();
            btnGuardarVoucher.setEnabled(true);
        }
        else if(numAut.length() < 6){
            int faltantes =  6 - numAut.length();
            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
            alerta.content("AVISO", "Debes ingresar un número de autorización de 6 digítos. (Faltan " + faltantes  + " dígitos)")
                    .possitiveButton("Ok", "close")
                    .show(false)
                    .style("Possitive", R.color.red);
            editTextNumAut.requestFocus();
            btnGuardarVoucher.setEnabled(true);
        }

        if(!tarjeta.isEmpty() && !importe.isEmpty() && !numAut.isEmpty()){
            AlertDialog.Builder alertaForm = new AlertDialog.Builder(getActivity());
            alertaForm.setMessage(Html.fromHtml("" +
                    "<br> Tarjeta: <b><span style='color:#de312e;'>"+ tarjeta +"</span></b> <br> <br>"+
                    "Importe: <b><span style='color:#de312e;'>" + importe +"</span></b> <br> <br>"+
                    "Núm Autorización: <b><span style='color:#de312e;'>" + numAut+"</span></b> <br> <br>"
            )).setTitle("¿LOS DATOS SON CORRECTOS?");

            alertaForm.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    registrarVoucher(numAut, importe, tarjeta);

                }
            });
            alertaForm.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Toast.makeText(getActivity(), "Cambia la información que necesites", Toast.LENGTH_LONG).show();
                    btnGuardarVoucher.setEnabled(true);
                }
            });
            if(!alerta.isOpen) {
                AlertDialog dialog = alertaForm.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                textView.setTextSize(25);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.red));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.verde));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0,0,20,0);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setLayoutParams(params);
            }

        }

    }

    public String removeLastChar(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, s.length()-1);
    }

    public boolean stringContainsLetters(String text){
        String regex = ".*[a-zA-Z].*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcherText = pattern.matcher(text);
        Boolean textMatches = matcherText.matches();
        return textMatches;
    }


    public void registrarVoucher(String numAut, String importe, String tarjeta){

        vs = VolleyS.getInstance(getContext());
        requestQueue = vs.getRequestQueue();
        JSONObject data = new JSONObject();

        try {
            data.put("numAut", "" + numAut);
            data.put("importe", "" + importe.substring(1).replace(",", ""));
            data.put("tarjeta", "" + tarjeta);
            data.put("id_estacion", "" + id_estacion);
            data.put("id_empleado", "" + id_empleado);
            data.put("funcion", "insertarVoucherCarburacion");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        String url = URL.URL_VOUCHERS;
        alertaProgress.content("Registrando voucher", "Por favor espere, no cierre la aplicación.").show(false);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Bundle historialVBundle  = new Bundle();
                        historialVBundle.putString("id_estacion", id_estacion);
                        historialVBundle.putString("id_empleado", id_empleado);
                        historialVBundle.putString("nombre_empleado", "");
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                                .navigate(R.id.nav_historial_vouchers, historialVBundle);
                        Toast.makeText(getContext(), "Se ha registrado el voucher.", Toast.LENGTH_SHORT).show();
                        editTextImporte.setText("");
                        editTextTarjeta.setText("");
                        editTextNumAut.setText("");
                        alertaProgress.close();
                        editTextTarjeta.requestFocus();
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



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(VoucherRegistroViewModel.class);
        // TODO: Use the ViewModel
    }

}