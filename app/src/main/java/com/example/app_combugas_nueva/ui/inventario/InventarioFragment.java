package com.example.app_combugas_nueva.ui.inventario;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.app_combugas_nueva.R;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import api.CurrentFragment;
import api.URL;
import modelos.VolleyS;

public class InventarioFragment extends Fragment {

   // private URL url = new URL();
   // private String apiUrl = url.getUrl() + "estaciones/index.php";
    private VolleyS vs;
    private RequestQueue requestQueue;

    private InventarioViewModel inventarioViewModel;
    private String estacion, empleado, cantidad_30_tanques, cantidad_45_tanques;
    private int tanques_30 = 0, tanques_45 = 0;
    private TextView textViewTanques30, textViewTanques45;
    public static InventarioFragment newInstance() {
        return new InventarioFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            estacion = getArguments().getString("id_estacion");
            empleado = getArguments().getString("id_empleado");
            getEstacion(estacion);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        inventarioViewModel =
                new ViewModelProvider(InventarioFragment.this).get(InventarioViewModel.class);
        View root = inflater.inflate(R.layout.inventario_fragment, container, false);

        CurrentFragment.currentFragment = "NAV_INVENTARIO";

        textViewTanques30 = root.findViewById(R.id.textViewInvTanques30);
        textViewTanques45 = root.findViewById(R.id.textViewInvTanques45);

        if(estacion != null) Toast.makeText(getContext(), "estacion " + estacion, Toast.LENGTH_SHORT).show();
        if(empleado != null)  Toast.makeText(getContext(), "Empleado " + empleado, Toast.LENGTH_SHORT).show();


        return root;

    }

    public void getEstacion(String id_estacion){

        //apiUrl = url.getUrl() + "estaciones/index.php";
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

                            cantidad_30_tanques = mJsonObject.getString("tank_30_quantity");
                            cantidad_45_tanques = mJsonObject.getString("tank_45_quantity");


                            if(cantidad_30_tanques != null){
                                tanques_30 = Integer.parseInt(cantidad_30_tanques);
                            }
                            if(cantidad_45_tanques != null) {
                                tanques_45 = Integer.parseInt(cantidad_45_tanques);
                            }

                            ValueAnimator animator30 = new  ValueAnimator();
                            animator30.setObjectValues(0, tanques_30); //double value
                            animator30.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    textViewTanques30.setText(String.valueOf(animation.getAnimatedValue()));
                                }
                            });
                            animator30.setEvaluator(new TypeEvaluator<Integer>() {
                                @Override
                                public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                                    return  (startValue + (int)((endValue - startValue) * fraction));
                                }

                            });
                            animator30.setDuration(2000);
                            animator30.start();


                            ValueAnimator animator45 = new  ValueAnimator();
                            animator45.setObjectValues(0, tanques_45); //double value
                            animator45.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    textViewTanques45.setText(String.valueOf(animation.getAnimatedValue()));
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



}