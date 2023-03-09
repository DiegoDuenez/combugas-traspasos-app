package com.example.app_combugas_nueva.ui.historial;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.app_combugas_nueva.NavigationActivity;
import com.example.app_combugas_nueva.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import api.CurrentFragment;
import api.URL;
import modelos.AdaptadorRecycler;
import modelos.Alert;
import modelos.AlertProgress;
import modelos.Historial;
import modelos.VolleyS;

public class HistorialFragment extends Fragment {

    private HistorialViewModel mViewModel;
    private VolleyS vs;
    private RequestQueue requestQueue;
    private String id_estacion, id_empleado, nombre_empleado;
    private ArrayList<Historial> listaHistorial;
    private AdaptadorRecycler adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AlertProgress alertaProgress;
    public Alert alerta;

    public static HistorialFragment newInstance() {
        return new HistorialFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        alerta = new Alert(getActivity());
        alertaProgress = new AlertProgress(getActivity(), R.style.AppCompatAlertDialogStyle);

        if(getArguments() != null){
            id_estacion = getArguments().getString("id_estacion");
            id_empleado = getArguments().getString("id_empleado");
            nombre_empleado = getArguments().getString("nombre_empleado");
        }
        getActionBar().setTitle("Historial ("+nombre_empleado+")");

        View root = inflater.inflate(R.layout.historial_fragment, container, false);
        CurrentFragment.currentFragment = "NAV_HISTORIAL";
        recyclerView = root.findViewById(R.id.recyclerHistorial);
        listaHistorial = new ArrayList<>();
        getHistorialEmpleadoEstacion(id_estacion, id_empleado);

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new AdaptadorRecycler(listaHistorial, getContext());
        recyclerView.setAdapter(adapter);



        return root;
    }
    private ActionBar getActionBar() {
        return ((NavigationActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HistorialViewModel.class);
        // TODO: Use the ViewModel
    }

    public void getHistorialEmpleadoEstacion(String id_estacion, String id_empleado){


        alertaProgress.content("Buscando historial", "Por favor espere").show();

        vs = VolleyS.getInstance(getContext());
        requestQueue = vs.getRequestQueue();
        String url = URL.URL_MOVIMIENTOS + "?funcion=historial";

        JSONObject data = new JSONObject();

        try {
            data.put("id_estacion", "" + id_estacion);
            data.put("id_empleado", "" + id_empleado);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            alertaProgress.close();
                            JSONArray mInventario = response.getJSONArray("movimientos");
                            if(mInventario.length() == 0){
                                Bundle historialBundle  = new Bundle();
                                historialBundle.putString("id_estacion", id_estacion);
                                historialBundle.putString("id_empleado", id_empleado);
                                alerta.content("AVISO", "No se encontro historial aún.")
                                        .possitiveButton("Ok", "close", historialBundle, getActivity(), R.id.nav_menu)
                                        .show(false)
                                        .style("Possitive", R.color.red);

                            }
                            for(int i=0; i < mInventario.length(); i++){
                                JSONObject mJsonObject = mInventario.getJSONObject(i);
                                String vehiculo = mJsonObject.getString("descripcion");
                                int tank_30_q = mJsonObject.getInt("tank_30_quantity");
                                int tank_45_q = mJsonObject.getInt("tank_45_quantity");
                                String fecha = mJsonObject.getString("fecha_registro");
                                //Toast.makeText(MenuLateralActivity.this, " " + tank_30_q + " " + tank_45_q, Toast.LENGTH_SHORT).show();
                                Historial historial = new Historial(vehiculo, tank_30_q, tank_45_q, fecha);
                                listaHistorial.add(historial);
                                adapter.notifyItemRangeInserted(listaHistorial.size(), 1);
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
                    message = "Hubo un problema al traer el historial, contacta al área de soporte.";
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
                        .possitiveButton("Ok", "close", getActivity(), R.id.nav_menu)
                        .show(false)
                        .style("Possitive", R.color.red);

            }
        });
        requestQueue.add(request);

    }

}