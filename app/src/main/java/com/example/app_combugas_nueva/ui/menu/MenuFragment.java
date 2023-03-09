package com.example.app_combugas_nueva.ui.menu;

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
import com.example.app_combugas_nueva.SplashActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import api.CurrentFragment;
import api.URL;
import modelos.Alert;
import modelos.AlertProgress;
import modelos.VolleyS;

public class MenuFragment extends Fragment {

    private MenuViewModel mViewModel;

    //private URL url = new URL();
    //private String apiUrl = url.getUrl() + "estaciones/index.php";
    private VolleyS vs;
    private RequestQueue requestQueue;
    private String cantidad_30_tanques, tipo_empleado, empleado, estacion, cantidad_45_tanques, precio_30, precio_45;
    private int tanques_30 = 0,tanques_45 = 0;
    private Button btnEntrada,btnSalida;
    private TextView textViewStock30, textViewStock45, textViewPrecio30, textViewPrecio45;
    public Alert alerta;
    public AlertProgress alertaProgress;

    public static MenuFragment newInstance() {
        return new MenuFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        alerta = new Alert(getActivity());
        alertaProgress = new AlertProgress(getActivity(), R.style.AppCompatAlertDialogStyle);

        if (getArguments() != null) {
            estacion = getArguments().getString("id_estacion");
            empleado = getArguments().getString("id_empleado");
            tipo_empleado = getArguments().getString("tipo_empleado");
            /*precio_30 = getArguments().getString("precio_30");
            precio_45 = getArguments().getString("precio_45");*/
            //Toast.makeText(getContext(), "FLAG " + estacion, Toast.LENGTH_SHORT).show();

            getEstacion(estacion);
        }

        SharedPreferences prefe = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
        precio_30 = prefe.getString("precio_30", "0");
        precio_45 = prefe.getString("precio_45", "0");

        CurrentFragment.currentFragment = "NAV_MENU";

        View root = inflater.inflate(R.layout.menu_fragment, container, false);

        btnEntrada = root.findViewById(R.id.btnEntrada);
        btnSalida = root.findViewById(R.id.btnSalida);
        textViewStock30 = root.findViewById(R.id.textViewStock30);
        textViewStock45 = root.findViewById(R.id.textViewStock45);
        textViewPrecio30 = root.findViewById(R.id.textViewPrecio30);
        textViewPrecio45 = root.findViewById(R.id.textViewPrecio45);

        if (precio_30.equals("0")) {
            textViewPrecio30.setText("(Sin precios)");
        }
        else{
            textViewPrecio30.setText("$ " + precio_30);
        }
        if(precio_45.equals("0")){
            textViewPrecio45.setText("(Sin precios)");
        }
        else{
            textViewPrecio45.setText("$ " + precio_45);
        }



        if(tipo_empleado == null){
        }
        else if(tipo_empleado.equals("1")){
            btnSalida.setVisibility(View.VISIBLE);
        }
        else if(tipo_empleado.equals("2")){
            btnSalida.setVisibility(View.GONE);
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
                        .navigate(R.id.nav_entrada, entradaBundle);
            }
        });

        btnSalida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle salidaBundle  = new Bundle();
                salidaBundle.putString("id_estacion", estacion);
                salidaBundle.putString("id_empleado", empleado);
                salidaBundle.putInt("tanques_30", tanques_30);
                salidaBundle.putInt("tanques_45", tanques_45);
                salidaBundle.putString("precio_30", precio_30);
                salidaBundle.putString("precio_45", precio_45);
                salidaBundle.putString("tipo_empleado", tipo_empleado);
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.nav_salida, salidaBundle);
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MenuViewModel.class);
        // TODO: Use the ViewModel
    }

    public void getEstacion(String id_estacion){

        alertaProgress.content("Cargando inventario", "Por favor espere.").show(false);


        String url = URL.URL_ESTACIONES + "?id_estacion=" +id_estacion;
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

                            cantidad_30_tanques = mJsonObject.getString("tank_30_quantity");
                            cantidad_45_tanques = mJsonObject.getString("tank_45_quantity");

                            if(cantidad_30_tanques != null){
                                tanques_30 = Integer.parseInt(cantidad_30_tanques);
                            }
                            if(cantidad_45_tanques != null) {
                                tanques_45 = Integer.parseInt(cantidad_45_tanques);
                            }
                            alertaProgress.close();

                            ValueAnimator animator30 = new  ValueAnimator();
                            animator30.setObjectValues(0, tanques_30); //double value
                            animator30.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    textViewStock30.setText(String.valueOf(animation.getAnimatedValue()));
                                }
                            });
                            animator30.setEvaluator(new TypeEvaluator<Integer>() {
                                @Override
                                public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                                    return  (startValue + (int)((endValue - startValue) * fraction));
                                } // problem here
                            });
                            animator30.setDuration(2000);
                            //animator30.cancel();
                            animator30.start();


                            ValueAnimator animator45 = new  ValueAnimator();
                            animator45.setObjectValues(0, tanques_45); //double value
                            animator45.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    textViewStock45.setText(String.valueOf(animation.getAnimatedValue()));
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

                            if(tanques_30 == 0 && tanques_45 == 0){
                                alerta.content("AVISO","Estación sin cilindros (30 kg Y 45 kg)")
                                        .possitiveButton("Ok", "close")
                                        .show(false)
                                        .style("Possitive", R.color.red);
                            }
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
                        .style("Possitive", R.color.red);

            }
        });
        requestQueue.add(request);
    }






}