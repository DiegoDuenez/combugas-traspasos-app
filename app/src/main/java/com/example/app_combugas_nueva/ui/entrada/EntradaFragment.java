package com.example.app_combugas_nueva.ui.entrada;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Vibrator;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.app_combugas_nueva.MainActivity;
import com.example.app_combugas_nueva.MenuPrincipalActivity;
import com.example.app_combugas_nueva.R;
import com.example.app_combugas_nueva.ui.inventario.InventarioFragment;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import api.CurrentFragment;
import api.URL;
import modelos.AdaptadorEntradaRecycler;
import modelos.AdaptadorRecycler;
import modelos.Alert;
import modelos.AlertProgress;
import modelos.Empleado;
import modelos.Historial;
import modelos.Vale;
import modelos.VolleyS;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class EntradaFragment extends Fragment {

    private EntradaViewModel mViewModel;

    private VolleyS vs;
    private RequestQueue requestQueue;
    private String id_estacion, id_empleado, tipo_empleado, id_empleado_traspasos;
    private EditText editTextFolioLiquidacion;
    private Button btnEntrada;
    private ArrayList<Vale> listaVales;
    public AdaptadorEntradaRecycler adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private TextView cantValesSeleccionados;
    private AlertProgress alertaProgress;
    public Alert alerta;


    public static EntradaFragment newInstance() {
        return new EntradaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        alerta = new Alert(getActivity());
        alertaProgress = new AlertProgress(getActivity(), R.style.AppCompatAlertDialogStyle);

        if(getArguments() != null){
            id_estacion = getArguments().getString("id_estacion");
            id_empleado = getArguments().getString("id_empleado");
            tipo_empleado = getArguments().getString("tipo_empleado");
        }

        View root = inflater.inflate(R.layout.entrada_fragment, container, false);

        CurrentFragment.currentFragment = "NAV_ENTRADA";
        recyclerView = root.findViewById(R.id.recyclerVales);
        btnEntrada = root.findViewById(R.id.btnEntrada);
        cantValesSeleccionados = root.findViewById(R.id.textViewCantValesSeleccionados);

        listaVales= new ArrayList<>();

        getVales(id_estacion);

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new AdaptadorEntradaRecycler(listaVales, getContext());

        recyclerView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Entrada");
        builder.setMessage("Ingrese el n??mero de empleado de traspaso");

        editTextFolioLiquidacion = new EditText(getActivity());
        editTextFolioLiquidacion.setInputType(InputType.TYPE_CLASS_NUMBER);

        builder.setView(editTextFolioLiquidacion);


        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String num_empleado_traspasos = editTextFolioLiquidacion.getText().toString();

                if(num_empleado_traspasos.equals("")){
                    Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(500);
                    alerta.content("AVISO", "Debes ingresar un n??mero de empleado de traspaso")
                            .possitiveButton("Ok", "close")
                            .show(false)
                            .style("Possitive", R.color.red);
                    btnEntrada.setEnabled(true);

                }else{

                    getEmpleadoTraspasos(num_empleado_traspasos);
                }

            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btnEntrada.setEnabled(true); dialog.dismiss();
            }
        });

        AlertDialog ad = builder.create();
        ad.setCanceledOnTouchOutside(false);
        ad.setCancelable(false);



        btnEntrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEntrada.setEnabled(false);
                if(adapter.listaValesUsados.size() > 0){
                    ad.show();
                    editTextFolioLiquidacion.requestFocus();
                    ad.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
                    ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
                }
                else{
                    btnEntrada.setEnabled(true);
                    Vibrator vib = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vib.vibrate(500);

                    alerta.content("AVISO", "Debes seleccionar al menos un vale")
                            .possitiveButton("Ok", "close")
                            .show(false)
                            .style("Possitive", R.color.red);
                    btnEntrada.setEnabled(true);
                }

            }
        });

        return root;
    }


    public void getEmpleadoTraspasos(String numero_empleado){
        vs = VolleyS.getInstance(getContext());
        requestQueue = vs.getRequestQueue();
        String url = URL.URL_EMPLEADOS + "?num_traspasos="+numero_empleado;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray mJsonArray = null;
                        try {
                            mJsonArray = response.getJSONArray("empleado");
                            JSONObject mJsonObject = mJsonArray.getJSONObject(0);
                            id_empleado_traspasos  = mJsonObject.getString("id_empleado");

                            for(int i = 0; i < adapter.listaValesUsados.size(); i++){
                                String id_vale = adapter.listaValesUsados.get(i).getId_vale().toString();
                                String folio = adapter.listaValesUsados.get(i).getFolio().toString();
                                String tanques_30 = adapter.listaValesUsados.get(i).getTank_30_quantity();
                                String tanques_45 = adapter.listaValesUsados.get(i).getTank_45_quantity();
                                entradaInventarioTraspasos(id_vale, id_empleado_traspasos,id_estacion, tanques_30, tanques_45);
                            }

                        } catch (JSONException e) {
                            btnEntrada.setEnabled(true);
                            e.printStackTrace();
                            alerta.content("AVISO", "El empleado de traspasos ingresado no existe")
                                    .possitiveButton("Ok", "close")
                                    .show(false)
                                    .style("Possitive", R.color.red);

                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                btnEntrada.setEnabled(true);
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
                alerta.content("AVISO", "El empleado de traspasos ingresado no existe")
                        .possitiveButton("Ok", "close")
                        .show(false)
                        .style("Possitive", R.color.red);

            }
        });
        requestQueue.add(request);

    }

    public void getVales(String id_estacion){


        alertaProgress.content("Buscando vales", "Por favor espere.").show(false);


        String url = URL.URL_VALES + "?id_estacion=" + id_estacion + "&tipo_vale=CILINDRO";
        vs = VolleyS.getInstance(getContext());
        requestQueue = vs.getRequestQueue();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            alertaProgress.close();
                                JSONArray mJsonArray = response.getJSONArray("vales");
                                if(mJsonArray.length() == 0){

                                    Bundle inventarioBundle  = new Bundle();
                                    inventarioBundle.putString("id_estacion", id_estacion);
                                    inventarioBundle.putString("id_empleado", id_empleado);
                                    inventarioBundle.putString("tipo_empleado", tipo_empleado);
                                    alerta.content("AVISO", "No se encontrar??n vales de recarga a??n.")
                                            .possitiveButton("Ok", "close", inventarioBundle, getActivity(), R.id.nav_menu)
                                            .show(false)
                                            .style("Possitive", R.color.red);

                                }
                                for(int i = 0; i < mJsonArray.length(); i++){
                                    JSONObject mJsonObject = mJsonArray.getJSONObject(i);
                                    String id_vale = mJsonObject.getString("id_vale");
                                    String folio = mJsonObject.getString("folio");
                                    String nombre_estacion = mJsonObject.getString("nombre_estacion");
                                    String carburista = mJsonObject.getString("nombre");
                                    String cilindrero = mJsonObject.getString("nombre_completo");
                                    String tanques_30 = mJsonObject.getString("tank_30_quantity");
                                    String tanques_45 = mJsonObject.getString("tank_45_quantity");
                                    String unidad = mJsonObject.getString("descripcion");
                                    String fecha_recarga =  mJsonObject.getString("fecha_recarga");
                                    Vale vale = new Vale(id_vale, folio, carburista, fecha_recarga, tanques_30, tanques_45, nombre_estacion,  cilindrero, unidad);
                                    listaVales.add(vale);
                                    adapter.notifyItemRangeInserted(listaVales.size(), 1);
                                    cantValesSeleccionados.setText("0 de " + listaVales.size() + " vales seleccionados");
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

    public void entradaInventarioTraspasos(String id_vale, String id_empleado,String id_estacion, String tanques_30, String tanques_45){
        vs = VolleyS.getInstance(getContext());
        requestQueue = vs.getRequestQueue();
        String urlVale = URL.URL_VALES  + "?funcion=conciliarVale";

        JSONObject dataVale = new JSONObject();
        try {
            dataVale.put("id_vale", "" + id_vale);
            dataVale.put("id_empleado", "" + id_empleado);
            dataVale.put("id_estacion", "" +id_estacion);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, urlVale, dataVale,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        entradaInventario(id_estacion, tanques_30, tanques_45);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(request);
    }

    public void entradaInventarioVale(String id_vale, String id_empleado,String id_estacion, String tanques_30, String tanques_45){
        vs = VolleyS.getInstance(getContext());
        requestQueue = vs.getRequestQueue();

        //this.apiUrl = url.getUrl() + "vales/index.php";
        String urlVale = URL.URL_VALES + "?funcion=conciliarVale";

        JSONObject dataVale = new JSONObject();
        try {
            dataVale.put("id_vale", "" + id_vale);
            dataVale.put("id_empleado", "" + id_empleado);
            dataVale.put("id_estacion", "" +id_estacion);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, urlVale, dataVale,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        entradaInventario(id_estacion, tanques_30, tanques_45);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(request);

    }

    public void entradaInventario(String id_estacion, String tanques_30, String tanques_45){

        vs = VolleyS.getInstance(getContext());
        requestQueue = vs.getRequestQueue();

        String url = URL.URL_ESTACIONES + "?funcion=entradaInventario&id_estacion=" + id_estacion;
        JSONObject data = new JSONObject();

        try {
            data.put("tanques_30", "" + tanques_30);
            data.put("tanques_45", "" + tanques_45);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        alertaProgress.content("Guardando entrada", "Por favor espere, no cierre la aplicaci??n.").show(false);

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
                        Toast.makeText(getContext(), "Se ha recargado el inventario", Toast.LENGTH_SHORT).show();
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


}