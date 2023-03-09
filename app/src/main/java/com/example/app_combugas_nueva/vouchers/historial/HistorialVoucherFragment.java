package com.example.app_combugas_nueva.vouchers.historial;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

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
import modelos.AdaptadorHistorialVoucherRecycler;
import modelos.AdaptadorRecycler;
import modelos.Alert;
import modelos.AlertProgress;
import modelos.Historial;
import modelos.HistorialAwa;
import modelos.HistorialVoucher;
import modelos.VolleyS;

public class HistorialVoucherFragment extends Fragment {

    private HistorialVoucherViewModel mViewModel;
    private ArrayList<HistorialVoucher> listaHistorial;
    private AdaptadorHistorialVoucherRecycler adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AlertProgress alertaProgress;
    public Alert alerta;
    private VolleyS vs;
    private RequestQueue requestQueue;
    private String id_estacion, id_empleado, nombre_empleado;



    public static HistorialVoucherFragment newInstance() {
        return new HistorialVoucherFragment();
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

        getActionBar().setTitle("Historial Vouchers");

        View vieww = getActivity().getCurrentFocus();

        if(vieww != null){
            InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(Context.INPUT_METHOD_SERVICE));
            input.hideSoftInputFromWindow(vieww.getWindowToken(), 0);
        }

        View root = inflater.inflate(R.layout.historial_voucher_fragment, container, false);
        CurrentFragment.currentFragment = "NAV_HISTORIAL_VOUCHERS";

        recyclerView = root.findViewById(R.id.recyclerHistorialVouchers);
        listaHistorial = new ArrayList<>();
        //getHistorialEmpleadoEstacion(id_estacion, id_empleado);
        getHistorial(id_estacion);
        //HistorialVoucher historial = new HistorialVoucher("Diego", "CUAUTHEMOC", "Dueñez", "Bancomer", "98765450", "5.00", "9898");
        //HistorialVoucher historial2 = new HistorialVoucher("Diego", "CUAUTHEMOC", "Dueñez", "Bancomer", "98765450", "5.00", "9898");

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new AdaptadorHistorialVoucherRecycler(listaHistorial, getContext());
        recyclerView.setAdapter(adapter);

        adapter.notifyItemRangeInserted(listaHistorial.size(), 1);

        return root;
    }

    private ActionBar getActionBar() {
        return ((NavigationActivity) getActivity()).getSupportActionBar();
    }

    public void getHistorial(String id_estacion){

        alertaProgress.content("Buscando historial", "Por favor espere").show();

        vs = VolleyS.getInstance(getContext());
        requestQueue = vs.getRequestQueue();
        String url = URL.URL_VOUCHERS+ "?funcion=historialVoucher&id_estacion="+id_estacion;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            alertaProgress.close();
                            JSONArray mVouchers = response.getJSONArray("vouchers");
                            if(mVouchers.length() == 0){
                                Bundle registroBundle  = new Bundle();
                                registroBundle.putString("id_estacion", id_estacion);
                                registroBundle.putString("id_empleado", id_empleado);
                                alerta.content("AVISO", "No se encontro historial de vouchers aún.")
                                        .possitiveButton("Ok", "close", registroBundle, getActivity(), R.id.nav_vouchers_registro)
                                        .show(false)
                                        .style("Possitive", R.color.yellow);
                            }

                            for(int i=0; i < mVouchers.length(); i++){
                                JSONObject mJsonObject = mVouchers.getJSONObject(i);
                                String no_voucher = mJsonObject.getString("no_voucher");
                                String fecha = mJsonObject.getString("fecha_registro");
                                //String banco = mJsonObject.getString("banco");
                                String importe = mJsonObject.getString("monto");
                                String tarjeta = mJsonObject.getString("tarjeta");
                                String nombre_estacion = mJsonObject.getString("nombre_estacion");
                                String carburista = mJsonObject.getString("nombre");

                                HistorialVoucher historial = new HistorialVoucher(carburista, nombre_estacion, "", "", no_voucher, importe, tarjeta, fecha);
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
                        .possitiveButton("Ok", "close", getActivity(), R.id.nav_vouchers_registro)
                        .show(false)
                        .style("Possitive", R.color.yellow);

            }
        });
        requestQueue.add(request);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HistorialVoucherViewModel.class);
        // TODO: Use the ViewModel
    }

}