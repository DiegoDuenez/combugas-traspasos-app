package com.example.app_combugas_nueva.yelow.recarga;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.app_combugas_nueva.R;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBarWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import api.CurrentFragment;
import api.URL;
import modelos.VolleyS;

public class RecargaYelowFragment extends Fragment {

    private RecargaYelowViewModel mViewModel;

    private SeekBar seekBarR1, seekBarR2, seekBarR3, seekBarR4, seekBarR5, seekBarR6;

    private String estacion, empleado;

    private float cantidadRefris = 6, cantidadContenedoresRefris = 0;

    private VolleyS vs;
    private RequestQueue requestQueue;

    private LinearLayoutCompat contenedorRefris;


    public static RecargaYelowFragment newInstance() {
        return new RecargaYelowFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recarga_yelow, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments() != null){
            estacion = getArguments().getString("id_estacion");
            empleado = getArguments().getString("id_empleado");
            getEstacion(estacion);
        }

        CurrentFragment.currentFragment = "NAV_RECARGA_YELOW";

        seekBarR1 = view.findViewById(R.id.seekBar1);
        seekBarR2 = view.findViewById(R.id.seekBar2);
        seekBarR3 = view.findViewById(R.id.seekBar3);
        seekBarR4 = view.findViewById(R.id.seekBar4);
        seekBarR5 = view.findViewById(R.id.seekBar5);
        seekBarR6 = view.findViewById(R.id.seekBar6);

        contenedorRefris = view.findViewById(R.id.contenedorRefris);

        cantidadContenedoresRefris = Math.round( cantidadRefris / 2.0);

        for(int i = 0; i <  cantidadContenedoresRefris ; i++){

            LinearLayoutCompat ll1 = new LinearLayoutCompat(getActivity());
            ll1.setGravity(Gravity.CENTER);
            LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            );
            ll1.setLayoutParams(params);
            ll1.setId(i);
            ll1.setOrientation(LinearLayoutCompat.HORIZONTAL);

            contenedorRefris.addView(ll1);



            /*for(int j = 0; j <  cantidadRefris ; j++){

                LinearLayoutCompat ll2 = new LinearLayoutCompat(getActivity());
                ll2.setGravity(Gravity.CENTER);
                LinearLayoutCompat.LayoutParams params2 = new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                );
                params2.setMarginEnd(20);
                ll2.setLayoutParams(params2);
                ll2.setOrientation(LinearLayoutCompat.VERTICAL);

                VerticalSeekBarWrapper verticalSeekBarWrapper = new VerticalSeekBarWrapper(getActivity());
                LinearLayoutCompat.LayoutParams params3 = new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                        250
                );
                verticalSeekBarWrapper.setLayoutParams(params3);

                VerticalSeekBar verticalSeekBar = new VerticalSeekBar(getActivity());
                LinearLayoutCompat.LayoutParams params4 = new LinearLayoutCompat.LayoutParams(
                       0,
                        0
                );
                verticalSeekBar.setLayoutParams(params4);
                verticalSeekBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_seekbar, getActivity().getTheme()));
                verticalSeekBar.setSplitTrack(false);
                verticalSeekBar.setThumb(getResources().getDrawable(R.color.zxing_transparent, getActivity().getTheme()));
                verticalSeekBar.setRotationAngle(270);
                verticalSeekBar.setMax(120);
                verticalSeekBar.setProgress(120);
                verticalSeekBarWrapper.addView(verticalSeekBar);

                ll2.addView(verticalSeekBarWrapper);

                ll1.addView(ll2);

                if(j > 1){
                    break;
                }

            }*/



        }

        /*for(int i = 0; i <  cantidadRefris ; i++){

            LinearLayoutCompat contenedor = view.findViewById(getActivity().getResources().getIdentifier(i+"", "id", getActivity().getPackageName()));
            Log.d("IDLL2",  contenedor.getId()+"");

        }*/




    }

    public void getEstacion(String id_estacion){

        Toast.makeText(getContext(), "ENTRO2", Toast.LENGTH_SHORT).show();
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

            }
        });
        requestQueue.add(request);
    }
}