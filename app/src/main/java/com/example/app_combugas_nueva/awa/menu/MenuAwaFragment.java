package com.example.app_combugas_nueva.awa.menu;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import com.example.app_combugas_nueva.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import api.CurrentFragment;
import api.URL;
import modelos.Alert;
import modelos.AlertProgress;
import modelos.VolleyS;

public class MenuAwaFragment extends Fragment {

    private MenuAwaViewModel mViewModel;
    private VolleyS vs;
    private RequestQueue requestQueue;
    private String estacion, empleado, tipo_empleado,cantidad_19_awa,cantidad_19_alk, cantidad_six_awa, cantidad_six_alk, precio_awa, precio_alk, precio_six_awa, precio_six_alk;
    private int garrafones_19_awa = 0, garrafones_19_alk = 0, six_alk = 0, six_awa = 0;
    private Button btnEntrada, btnSalida;
    private TextView textViewStockAwa19,textViewStockAlk19, textViewPrecioAwa, textViewPrecioAlk, textViewPrecioAwaSix, textViewPrecioAlkSix, textViewStockAwaSix, textViewStockAlkSix;
    public Alert alerta;
    public AlertProgress alertaProgress;

    public static MenuAwaFragment newInstance() {
        return new MenuAwaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        alerta = new Alert(getActivity());
        alertaProgress = new AlertProgress(getActivity(), R.style.AppCompatAlertDialogStyleAwa);

        if(getArguments() != null){
            estacion = getArguments().getString("id_estacion");
            empleado = getArguments().getString("id_empleado");
            tipo_empleado = getArguments().getString("tipo_empleado");
            precio_awa = getArguments().getString("precio_awa");
            precio_alk = getArguments().getString("precio_alk");
            precio_six_awa = getArguments().getString("precio_six_awa");
            precio_six_alk = getArguments().getString("precio_six_alk");
            getEstacion(estacion);
        }

        SharedPreferences prefe = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
        precio_awa = prefe.getString("precio_awa","0");
        precio_alk = prefe.getString("precio_alk","0");
        precio_six_alk = prefe.getString("precio_six_alk","0");
        precio_six_awa = prefe.getString("precio_six_awa","0");


        View root = inflater.inflate(R.layout.menu_awa_fragment, container, false);
        CurrentFragment.currentFragment = "NAV_MENU_AWA";

        btnEntrada = root.findViewById(R.id.btnEntradaAwa);
        btnSalida = root.findViewById(R.id.btnSalidaAwa);
        textViewStockAlk19 = root.findViewById(R.id.textViewStockAlk19);
        textViewStockAwa19 = root.findViewById(R.id.textViewStockAwa19);
        textViewPrecioAwa = root.findViewById(R.id.textViewPrecioAwa);
        textViewPrecioAlk = root.findViewById(R.id.textViewPrecioAlk);
        /*textViewPrecioAwaSix = root.findViewById(R.id.textViewPrecioAwaSix);
        textViewPrecioAlkSix = root.findViewById(R.id.textViewPrecioAlkSix);
        textViewStockAwaSix = root.findViewById(R.id.textViewStockAwaSix19);
        textViewStockAlkSix = root.findViewById(R.id.textViewStockAlkSix19);*/

        if (precio_awa.equals("0")) {
            textViewPrecioAwa.setText("(Sin precios)");
        }
        else{
            textViewPrecioAwa.setText("$ " + precio_awa);
        }
        if(precio_alk.equals("0")){
            textViewPrecioAlk.setText("(Sin precios)");
        }
        else{
            textViewPrecioAlk.setText("$ " + precio_alk);
        }

        /*if (precio_six_awa.equals("0")) {
            textViewPrecioAwaSix.setText("(Sin precios)");
        }
        else{
            textViewPrecioAwaSix.setText("$ " + precio_six_awa);
        }
        if(precio_six_alk.equals("0")){
            textViewPrecioAlkSix.setText("(Sin precios)");
        }
        else{
            textViewPrecioAlkSix.setText("$ " + precio_six_alk);
        }*/


        String temaApp = prefe.getString("tema","awaTema");

        if(temaApp.equals("awaTema")){
            btnEntrada.setBackgroundColor(getResources().getColor(R.color.blueAwa));
            btnSalida.setBackgroundColor(getResources().getColor(R.color.blueAwa));

        }
        else if(temaApp.equals("alkTema")){
            btnEntrada.setBackgroundColor(getResources().getColor(R.color.verdeAlk));
            btnSalida.setBackgroundColor(getResources().getColor(R.color.verdeAlk));
        }

        View vieww = getActivity().getCurrentFocus();
        if(vieww != null){
            InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(Context.INPUT_METHOD_SERVICE));
            input.hideSoftInputFromWindow(vieww.getWindowToken(), 0);
        }


        btnEntrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle entradaBundle  = new Bundle();
                entradaBundle.putString("id_estacion", estacion);
                entradaBundle.putString("id_empleado", empleado);
                entradaBundle.putString("tipo_empleado", tipo_empleado);
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.nav_entrada_awa, entradaBundle);
            }
        });

        btnSalida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle salidaBundle  = new Bundle();
                salidaBundle.putString("id_estacion", estacion);
                salidaBundle.putString("id_empleado", empleado);
                salidaBundle.putInt("garrafones_awa", garrafones_19_awa);
                salidaBundle.putInt("garrafones_alk", garrafones_19_alk);
                salidaBundle.putInt("six_awa", six_awa);
                salidaBundle.putInt("six_alk", six_alk);
                salidaBundle.putString("tipo_empleado", tipo_empleado);
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.nav_salida_awa, salidaBundle);

            }
        });

        return root;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MenuAwaViewModel.class);
        // TODO: Use the ViewModel
    }



    public void getEstacion(String id_estacion){

        alertaProgress.content("Cargando inventario", "Por favor espere.").show(false);

        String url = URL.URL_ESTACIONES + "?id_estacion=" +id_estacion+"&funcion=";
        vs = VolleyS.getInstance(getContext());
        requestQueue = vs.getRequestQueue();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray mJsonArray = response.getJSONArray("estacion");
                            JSONObject mJsonObject = mJsonArray.getJSONObject(0);
                            String nombre_estacion = mJsonObject.getString("nombre_estacion");

                            cantidad_19_awa = mJsonObject.getString("awa_quantity");
                            cantidad_19_alk = mJsonObject.getString("alk_quantity");
                            cantidad_six_awa =mJsonObject.getString("six_quantity");
                            cantidad_six_alk =mJsonObject.getString("salk_quantity");


                            if(cantidad_19_alk != null){
                                garrafones_19_alk = Integer.parseInt(cantidad_19_alk);
                            }
                            if(cantidad_19_awa != null) {
                                garrafones_19_awa = Integer.parseInt(cantidad_19_awa);
                            }
                            if(cantidad_six_awa != null){
                                six_awa = Integer.parseInt(cantidad_six_awa);
                            }
                            if(cantidad_six_alk != null){
                                six_alk = Integer.parseInt(cantidad_six_alk);
                            }

                            alertaProgress.close();

                            /* ANIMATOR GARRAFON AWA */
                            ValueAnimator animator30 = new  ValueAnimator();
                            animator30.setObjectValues(0, Integer.parseInt(cantidad_19_awa));
                            animator30.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    textViewStockAwa19.setText(String.valueOf(animation.getAnimatedValue()));
                                }
                            });
                            animator30.setEvaluator(new TypeEvaluator<Integer>() {
                                @Override
                                public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                                    return  (startValue + (int)((endValue - startValue) * fraction));
                                } // problem here

                            });
                            animator30.setDuration(2000);
                            animator30.start();

                            /* ANIMATOR GARRAFON ALK */
                            ValueAnimator animator45 = new  ValueAnimator();
                            animator45.setObjectValues(0, Integer.parseInt(cantidad_19_alk));
                            animator45.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    textViewStockAlk19.setText(String.valueOf(animation.getAnimatedValue()));
                                }
                            });
                            animator45.setEvaluator(new TypeEvaluator<Integer>() {
                                @Override
                                public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                                    return  (startValue + (int)((endValue - startValue) * fraction));
                                }
                            });
                            animator45.setDuration(2000);
                            animator45.start();

                            /* ANIMATOR SIX AWA
                            ValueAnimator animatorSixAwa = new  ValueAnimator();
                            animatorSixAwa.setObjectValues(0, Integer.parseInt(cantidad_six_awa));
                            animatorSixAwa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    textViewStockAwaSix.setText(String.valueOf(animation.getAnimatedValue()));
                                }
                            });
                            animatorSixAwa.setEvaluator(new TypeEvaluator<Integer>() {
                                @Override
                                public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                                    return  (startValue + (int)((endValue - startValue) * fraction));
                                }
                            });
                            animatorSixAwa.setDuration(2000);
                            animatorSixAwa.start();*/

                            /* ANIMATOR SIX ALK
                            ValueAnimator animatorSixAlk = new  ValueAnimator();
                            animatorSixAlk.setObjectValues(0, Integer.parseInt(cantidad_six_alk));
                            animatorSixAlk.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    textViewStockAlkSix.setText(String.valueOf(animation.getAnimatedValue()));
                                }
                            });
                            animatorSixAlk.setEvaluator(new TypeEvaluator<Integer>() {
                                @Override
                                public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                                    return  (startValue + (int)((endValue - startValue) * fraction));
                                }
                            });
                            animatorSixAlk.setDuration(2000);
                            animatorSixAlk.start();*/

                            /*if(cantidad_19_awa.equals("0") && cantidad_19_alk.equals("0")){
                                alerta.content("AVISO","Estación sin garrafones de Awa y Alkalina")
                                        .possitiveButton("Ok", "close")
                                        .show(false)
                                        .style("Possitive", R.color.blueAwa);
                            }
                            if(cantidad_six_awa.equals("0") && cantidad_six_alk.equals("0")){
                                alerta.content("AVISO","Estación sin six de Awa y Alkalina")
                                        .possitiveButton("Ok", "close")
                                        .show(false)
                                        .style("Possitive", R.color.blueAwa);
                            }*/


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                alertaProgress.close();

                String codigoError = "ERROR ";
                String message;
                if (error instanceof NetworkError || error instanceof NoConnectionError || error instanceof AuthFailureError) {
                    message = "No se ha podido conectar a internet, por favor checa tu conexión.";
                } else if (error instanceof ServerError){
                    message = "Hubo un problema al traer el inventario, contacta al área de soporte.";
                } else if (error instanceof ParseError) {
                    message = "Hubo un problema, contacta al área de soporte.";
                } else if (error instanceof TimeoutError) {
                    message = "Tiempo de espera agotado.";
                }
                else{
                    codigoError += getString(R.string.error_desconocido);
                    message = "Hubo un problema, contacta al área de soporte.";
                }

                alerta.content("" + codigoError, "" + message)
                        .possitiveButton("Ok", "kill", getActivity())
                        .show(false)
                        .style("Possitive", R.color.blueAwa);

            }
        });
        requestQueue.add(request);
    }


}