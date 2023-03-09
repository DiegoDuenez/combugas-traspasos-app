package com.example.app_combugas_nueva.awa.entrada;

import static android.content.Context.SENSOR_SERVICE;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.SensorManager;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.app_combugas_nueva.MenuPrincipalActivity;
import com.example.app_combugas_nueva.R;
import com.squareup.seismic.ShakeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import api.CurrentFragment;
import api.URL;
import modelos.AdaptadorEntradaAwaRecycler;
import modelos.Alert;
import modelos.AlertProgress;
import modelos.Vale;
import modelos.ValeAwa;
import modelos.VolleyS;

public class EntradaAwaFragment extends Fragment{

    private EntradaAwaViewModel mViewModel;

    private VolleyS vs;
    private RequestQueue requestQueue;
    private String id_estacion, id_empleado, tipo_empleado, id_empleado_traspasos;
    private boolean traspasos = false, isSelectAll = true;
    private EditText editTextFolioLiquidacion;
    private Button btnEntrada, btnSeleccionarTodos;
    private ArrayList<ValeAwa> listaVales;
    public AdaptadorEntradaAwaRecycler adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private TextView cantValesSeleccionados;
    private AlertProgress alertaProgress;
    public Alert alerta;
    //public ArrayList<String> folios;

    public static EntradaAwaFragment newInstance() {
        return new EntradaAwaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        alerta = new Alert(getActivity());
        alertaProgress = new AlertProgress(getActivity(), R.style.AppCompatAlertDialogStyleAwa);

        if (getArguments() != null) {
            id_estacion = getArguments().getString("id_estacion");
            id_empleado = getArguments().getString("id_empleado");
            tipo_empleado = getArguments().getString("tipo_empleado");
        }

        View root = inflater.inflate(R.layout.entrada_awa_fragment, container, false);
        CurrentFragment.currentFragment = "NAV_ENTRADA_AWA";

        recyclerView = root.findViewById(R.id.recyclerValesAwa);
        btnEntrada = root.findViewById(R.id.btnRecargarAwa);
        btnSeleccionarTodos = root.findViewById(R.id.btnSeleccionarTodosAWA);
        cantValesSeleccionados = root.findViewById(R.id.textViewCantValesSeleccionadosAwa);

        SharedPreferences prefe = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
        String temaApp = prefe.getString("tema", "awaTema");

        if (temaApp.equals("awaTema")) {
            btnEntrada.setBackgroundColor(getResources().getColor(R.color.blueAwa));
        } else if (temaApp.equals("alkTema")) {
            btnEntrada.setBackgroundColor(getResources().getColor(R.color.verdeAlk));
        }

        listaVales = new ArrayList<>();

        getVales(id_estacion);

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new AdaptadorEntradaAwaRecycler(listaVales, getContext());

        recyclerView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Entrada");
        builder.setMessage("Ingrese el número de empleado de traspaso");

        editTextFolioLiquidacion = new EditText(getActivity());
        editTextFolioLiquidacion.setInputType(InputType.TYPE_CLASS_NUMBER);

        builder.setView(editTextFolioLiquidacion);

        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String num_empleado_traspasos = editTextFolioLiquidacion.getText().toString();

                if (num_empleado_traspasos.equals("")) {
                    Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(500);
                    alerta.content("AVISO", "Debes ingresar un número de empleado de traspaso")
                            .possitiveButton("Ok", "close")
                            .show(false)
                            .style("Possitive", R.color.blueAwa);
                    btnEntrada.setEnabled(true);

                } else {

                    getEmpleadoTraspasos(num_empleado_traspasos);
                }

            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btnEntrada.setEnabled(true);
                dialog.dismiss();

            }
        });
        AlertDialog ad = builder.create();
        ad.setCanceledOnTouchOutside(false);
        ad.setCancelable(false);

        btnSeleccionarTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.selecteAll();
                if (isSelectAll) {
                    btnSeleccionarTodos.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.normal_blue_button));
                    btnSeleccionarTodos.setTextColor(Color.WHITE);
                    btnSeleccionarTodos.setText("SELECCIONADOS");
                    isSelectAll = false;
                    Toast.makeText(getActivity(), "TODOS LOS VALES SELECCIONADOS", Toast.LENGTH_LONG).show();

                } else {
                    btnSeleccionarTodos.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ghost_button_awa));
                    btnSeleccionarTodos.setTextColor(ContextCompat.getColor(getActivity(), R.color.blueAwa));
                    btnSeleccionarTodos.setText("SELECCIONAR TODOS");
                    isSelectAll = true;
                    Toast.makeText(getActivity(), "TODOS LOS VALES DESELECCIONADOS", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnEntrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEntrada.setEnabled(false);
                if (adapter.listaValesUsados.size() > 0) {
                    ad.show();
                    editTextFolioLiquidacion.requestFocus();
                    ad.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.blueAwa));
                    ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blueAwa));
                } else {
                    btnEntrada.setEnabled(true);
                    Vibrator vib = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vib.vibrate(500);
                    alerta.content("AVISO", "Debes seleccionar al menos un vale")
                            .possitiveButton("Ok", "close")
                            .show(false)
                            .style("Possitive", R.color.blueAwa);
                    btnEntrada.setEnabled(true);

                }
            }
        });
        return root;
    }

    /*@Override public void hearShake() {
        adapter.selectAllOnly();
        if (isSelectAll) {
            Toast.makeText(getActivity(), "Todos los vales seleccionados", Toast.LENGTH_LONG).show();
        }
        else {
            isSelectAll = true;
            Toast.makeText(getActivity(), "Todos los vales deseleccionados", Toast.LENGTH_LONG).show();
        }
    }*/

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
                                String garrafones_awa = adapter.listaValesUsados.get(i).getAwa_quantity();
                                String garrafones_alk = adapter.listaValesUsados.get(i).getAlk_quantity();
                                String six_awa = adapter.listaValesUsados.get(i).getSix_quantity();
                                String six_alk = adapter.listaValesUsados.get(i).getSalk_quantity();

                                entradaInventarioTraspasos(id_vale, id_empleado_traspasos,id_estacion, garrafones_awa, garrafones_alk, six_awa, six_alk);

                            }

                        } catch (JSONException e) {
                            btnEntrada.setEnabled(true);
                            e.printStackTrace();
                            alerta.content("AVISO", "El empleado de traspasos ingresado no existe")
                                    .possitiveButton("Ok", "close")
                                    .show(false)
                                    .style("Possitive", R.color.blueAwa);

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
                        .style("Possitive", R.color.blueAwa);

            }
        });
        requestQueue.add(request);

    }

    public void getVales(String id_estacion){


        alertaProgress.content("Buscando vales", "Por favor espere.").show(false);

        String url = URL.URL_VALES + "?id_estacion=" + id_estacion + "&tipo_vale=AWA";
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
                                alerta.content("AVISO", "No se encontrarón vales de recarga aún.").
                                        possitiveButton("Ok", "close", inventarioBundle, getActivity(), R.id.nav_menu_awa)
                                        .show(false)
                                        .style("Possitive", R.color.blueAwa);
                            }

                            for(int i = 0; i < mJsonArray.length(); i++){
                                JSONObject mJsonObject = mJsonArray.getJSONObject(i);
                                String id_vale = mJsonObject.getString("id_vale");
                                String folio = mJsonObject.getString("folio");
                                String nombre_estacion = mJsonObject.getString("nombre_estacion");
                                String carburista = mJsonObject.getString("nombre");
                                String cilindrero = mJsonObject.getString("nombre_completo");
                                String awa_19 = mJsonObject.getString("awa_quantity");
                                String alk_19 = mJsonObject.getString("alk_quantity");
                                String six_awa = mJsonObject.getString("six_quantity");
                                String six_alk = mJsonObject.getString("salk_quantity");
                                String unidad = mJsonObject.getString("descripcion");
                                String fecha_recarga =  mJsonObject.getString("fecha_recarga");
                                ValeAwa vale = new ValeAwa(id_vale, folio, carburista, fecha_recarga, awa_19, alk_19, nombre_estacion,  cilindrero, unidad, six_awa, six_alk);
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

    public void entradaInventarioTraspasos(String id_vale, String id_empleado,String id_estacion, String garrafones_awa, String garrafones_alk, String six_awa, String six_alk){
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
                        entradaInventario(id_estacion, garrafones_awa, garrafones_alk, six_awa, six_alk);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(request);
    }

    public void entradaInventario(String id_estacion, String garrafones_awa, String garrafones_alk, String six_awa, String six_alk){

        vs = VolleyS.getInstance(getContext());
        requestQueue = vs.getRequestQueue();

        String url = URL.URL_ESTACIONES + "?funcion=entradaInventarioAWA&id_estacion=" + id_estacion;
        JSONObject data = new JSONObject();

        try {
            data.put("garrafon_19_awa", "" + garrafones_awa);
            data.put("garrafon_19_alk", "" + garrafones_alk);
            data.put("six_awa", "" + six_awa);
            data.put("six_alk", "" + six_alk);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        alertaProgress.content("Guardando entrada", "Por favor espere, no cierre la aplicación.").show(false);

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EntradaAwaViewModel.class);
        // TODO: Use the ViewModel
    }

}