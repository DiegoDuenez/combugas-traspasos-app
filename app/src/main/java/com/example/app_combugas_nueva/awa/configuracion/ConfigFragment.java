package com.example.app_combugas_nueva.awa.configuracion;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.app_combugas_nueva.R;

public class ConfigFragment extends Fragment {

    private ConfigViewModel mViewModel;

    private Button btnTema;
    private Button btnGuardarConfig;
    private String tema = "awa";
    private String id_estacion;
    private String id_empleado;


    public static ConfigFragment newInstance() {
        return new ConfigFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if(getArguments() != null){
            id_estacion = getArguments().getString("id_estacion");
            id_empleado = getArguments().getString("id_empleado");
        }

        View root = inflater.inflate(R.layout.config_fragment, container, false);
        btnTema = root.findViewById(R.id.btnTema);
        btnGuardarConfig = root.findViewById(R.id.btnGuardarConfigAwa);

        SharedPreferences prefe= getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
        String temaApp = prefe.getString("tema","awaTema");
        if(temaApp.equals("awaTema")){
            btnTema.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ghost_button_awa));
            btnTema.setTextColor(ContextCompat.getColor(getContext(), R.color.blueAwa));
            btnTema.setText("AWA");
            tema = "awa";
            btnGuardarConfig.setBackgroundColor(getResources().getColor(R.color.blueAwa));
        }
        else if(temaApp.equals("alkTema")){
            btnTema.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ghost_button_alk));
            btnTema.setTextColor(ContextCompat.getColor(getContext(), R.color.verdeAlk));
            btnTema.setText("ALK");
            tema = "alk";
            btnGuardarConfig.setBackgroundColor(getResources().getColor(R.color.verdeAlk));

        }

        btnTema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tema.equals("awa")){
                    btnTema.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ghost_button_alk));
                    btnTema.setTextColor(ContextCompat.getColor(getContext(), R.color.verdeAlk));
                    btnTema.setText("ALK");
                    tema = "alk";
                }
                else{
                    btnTema.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ghost_button_awa));
                    btnTema.setTextColor(ContextCompat.getColor(getContext(), R.color.blueAwa));
                    btnTema.setText("AWA");
                    tema = "awa";
                }
            }
        });

        btnGuardarConfig.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(tema.equals("awa")){
                    SharedPreferences preferencias= getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferencias.edit();
                    editor.putString("tema", "awaTema");
                    editor.commit();
                }
                else if(tema.equals("alk")){
                    SharedPreferences preferencias= getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferencias.edit();
                    editor.putString("tema", "alkTema");
                    editor.commit();
                }
                //getActivity().finish();
                /*Bundle configBundle  = new Bundle();
                configBundle.putString("id_estacion", id_estacion);
                configBundle.putString("id_empleado", id_empleado);
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.nav_menu_awa, configBundle);*/
                getActivity().finish();
                //getActivity().recreate();
                Toast.makeText(getActivity(), "Configuración guardada", Toast.LENGTH_LONG).show();

            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ConfigViewModel.class);
        // TODO: Use the ViewModel
    }

}