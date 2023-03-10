package com.example.app_combugas_nueva.yelow.inventario;

import androidx.lifecycle.ViewModelProvider;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.app_combugas_nueva.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import api.CurrentFragment;
import api.URL;
import modelos.VolleyS;

public class InventarioYelowFragment extends Fragment {

    private InventarioYelowViewModel mViewModel;

    private SeekBar seekBarR1, seekBarR2, seekBarR3, seekBarR4, seekBarR5, seekBarR6;

    private String estacion, empleado;

    private VolleyS vs;
    private RequestQueue requestQueue;

    public static InventarioYelowFragment newInstance() {
        return new InventarioYelowFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventario_yelow, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments() != null){
            estacion = getArguments().getString("id_estacion");
            empleado = getArguments().getString("id_empleado");
            getEstacion(estacion);
        }

        CurrentFragment.currentFragment = "NAV_INVENTARIO_YELOW";

        seekBarR1 = view.findViewById(R.id.seekBar1);
        seekBarR1.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });

        seekBarR2 = view.findViewById(R.id.seekBar2);
        seekBarR2.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });

        seekBarR3 = view.findViewById(R.id.seekBar3);
        seekBarR3.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });

        seekBarR4 = view.findViewById(R.id.seekBar4);
        seekBarR4.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });

        seekBarR5 = view.findViewById(R.id.seekBar5);
        seekBarR5.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });

        seekBarR6 = view.findViewById(R.id.seekBar6);
        seekBarR6.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });

    }

    public void getEstacion(String id_estacion){

        Toast.makeText(getContext(), "ENTRO", Toast.LENGTH_SHORT).show();
        String url = URL.URL_ESTACIONES + "?id_estacion=" +id_estacion+"&funcion=";;
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


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "ENTRO ERROR", Toast.LENGTH_SHORT).show();



            }
        });
        requestQueue.add(request);
    }
}