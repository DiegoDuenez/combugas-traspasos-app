package com.example.app_combugas_nueva.yelow.menu;

import androidx.lifecycle.ViewModelProvider;

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

import api.CurrentFragment;

public class MenuYelowFragment extends Fragment {

    private MenuYelowViewModel mViewModel;

    private Button btnInventario, btnRecarga;
    private String estacion, empleado, tipo_empleado;

    public static MenuYelowFragment newInstance() {
        return new MenuYelowFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu_yelow_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            estacion = getArguments().getString("id_estacion");
            empleado = getArguments().getString("id_empleado");
            tipo_empleado = getArguments().getString("tipo_empleado");
        }


        CurrentFragment.currentFragment = "NAV_MENU_YELOW";

        btnInventario = view.findViewById(R.id.btnInventarioYelow);
        btnRecarga = view.findViewById(R.id.btnRecargaYelow);

        btnInventario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle entradaBundle  = new Bundle();
                entradaBundle.putString("id_estacion", estacion);
                entradaBundle.putString("id_empleado", empleado);
                entradaBundle.putString("tipo_empleado", tipo_empleado);
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.nav_inventario_yelow, entradaBundle);

            }
        });

        btnRecarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle entradaBundle  = new Bundle();
                entradaBundle.putString("id_estacion", estacion);
                entradaBundle.putString("id_empleado", empleado);
                entradaBundle.putString("tipo_empleado", tipo_empleado);
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.nav_recarga_yelow, entradaBundle);

            }
        });
    }

}