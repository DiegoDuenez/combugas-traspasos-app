package com.example.app_combugas_nueva;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.app_combugas_nueva.ui.entrada.EntradaFragment;
import com.example.app_combugas_nueva.ui.inventario.InventarioFragment;
import com.example.app_combugas_nueva.ui.salida.SalidaFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import api.CurrentFragment;
import api.URL;
import modelos.AlertProgress;
import modelos.VolleyS;

import com.example.app_combugas_nueva.ui.menu.MenuFragment;

public class NavigationActivity extends AppCompatActivity {

    //private URL url = new URL();
    //private String apiUrl = url.getUrl() + "estaciones/index.php";

    private VolleyS vs;
    private RequestQueue requestQueue;

    private AppBarConfiguration mAppBarConfiguration;
    private Fragment frag;
    private String id_estacion;
    private String id_empleado;
    private String cantidad_30_tanques;
    private String cantidad_45_tanques;
    private String precio_awa;
    private String precio_alk;
    private String precio_30;
    private String precio_45;
    private String tipo_empleado;
    private String tipo;
    private String nombre_empleado;
    private Bundle bundleInit;
    private  NavController navController;
    private ImageView imageView;
    private TextView textViewMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(),R.color.dark));
        setSupportActionBar(toolbar);

        id_estacion = getIntent().getExtras().getString("id_estacion");
        id_empleado = getIntent().getExtras().getString("id_empleado");
        tipo_empleado = getIntent().getExtras().getString("tipo_empleado");
        nombre_empleado = getIntent().getExtras().getString("nombre_empleado");
        tipo = getIntent().getExtras().getString("tipo");

        bundleInit = new Bundle();
        bundleInit.putString("id_estacion", id_estacion);
        bundleInit.putString("id_empleado", id_empleado);
        bundleInit.putString("tipo_empleado", tipo_empleado);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        SharedPreferences prefe = getSharedPreferences("datos", Context.MODE_PRIVATE);
        String temaApp = prefe.getString("tema","awaTema");

        if(tipo.equals("awa")){
            mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_historial_awa,R.id.nav_entrada_awa, R.id.nav_salida_awa,R.id.nav_menu_awa, R.id.nav_config_awa)
                    .setDrawerLayout(drawer)
                    .build();
            navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
            imageView = navigationView.getHeaderView(0).findViewById(R.id.imageView);
            textViewMenu = navigationView.getHeaderView(0).findViewById(R.id.textViewHeader);

            if(temaApp.equals("awaTema")){
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.awa, getApplicationContext().getTheme()));
                navigationView.getHeaderView(0).setBackgroundColor(getResources().getColor(R.color.blueAwa));
                toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.blueAwa));
                navigationView.setItemBackground(getResources().getDrawable(R.drawable.awa_navigation_view_item));
                navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.blueAwa)));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.blueAwa));
                }

            }
            else if(temaApp.equals("alkTema")){
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.awa, getApplicationContext().getTheme()));
                navigationView.getHeaderView(0).setBackgroundColor(getResources().getColor(R.color.verdeAlk));
                toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.verdeAlk));
                navigationView.setItemBackground(getResources().getDrawable(R.drawable.alk_navigation_view_item));
                navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.verdeAlk)));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.verdeAlk));
                }
            }

            toolbar.setTitleTextColor(Color.WHITE);

            textViewMenu.setText("Garrafones");
            textViewMenu.setTextColor(Color.WHITE);
            toolbar.getNavigationIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar();

            navController.navigate(R.id.nav_menu_awa, bundleInit);
            navigationView.getMenu().getItem(0).setVisible(false);
            navigationView.getMenu().getItem(1).setVisible(false);
            navigationView.getMenu().getItem(2).setVisible(false);
            navigationView.getMenu().getItem(3).setVisible(false);
            //navigationView.getMenu().getItem(7).setVisible(false); //MODULO CONFIGURACIÓN
            navigationView.getMenu().getItem(4).setChecked(true);
            navigationView.getMenu().getItem(8).setVisible(false);
            navigationView.getMenu().getItem(9).setVisible(false);
            navigationView.getMenu().getItem(10).setVisible(false);
            navigationView.getMenu().getItem(11).setVisible(false);
            navigationView.getMenu().getItem(12).setVisible(false);
            navigationView.getMenu().getItem(13).setVisible(false);
            navigationView.getMenu().getItem(14).setVisible(false);

            navigationView.setNavigationItemSelectedListener(item ->{
                switch (item.getItemId()){
                    case R.id.nav_menu_awa:
                        navController.navigate(R.id.nav_menu_awa, bundleInit);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_historial_awa:
                        Bundle bundleHistorial = new Bundle();
                        bundleHistorial.putString("id_estacion", id_estacion);
                        bundleHistorial.putString("id_empleado", id_empleado);
                        bundleHistorial.putString("nombre_empleado", nombre_empleado);
                        navController.navigate(R.id.nav_historial_awa, bundleHistorial);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_menu_principal_awa:
                        finish();
                        break;
                    case R.id.nav_config_awa:
                        Bundle bundleConfig = new Bundle();
                        bundleConfig.putString("id_estacion", id_estacion);
                        bundleConfig.putString("id_empleado", id_empleado);
                        navController.navigate(R.id.nav_config_awa, bundleConfig);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_cerrar_sesion_awa:
                        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_LONG).show();
                        Intent iCerrarSesion = new Intent(NavigationActivity.this, MainActivity.class);
                        startActivity(iCerrarSesion);
                        finish();
                        break;
                }
                return true;
            });

        }
        else  if(tipo.equals("tanques")){

            mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_menu,
                    R.id.nav_inventario, R.id.nav_entrada, R.id.nav_salida, R.id.nav_historial)
                    .setDrawerLayout(drawer)
                    .build();
            navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);

            imageView = navigationView.getHeaderView(0).findViewById(R.id.imageView);
            textViewMenu = navigationView.getHeaderView(0).findViewById(R.id.textViewHeader);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.combu_icon, getApplicationContext().getTheme()));
            navigationView.getHeaderView(0).setBackgroundColor(getResources().getColor(R.color.yellow));
            textViewMenu.setText("Tanques");
            toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow));
            navigationView.setItemBackground(getResources().getDrawable(R.drawable.tanques_navigation_view_item));
            navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.black)));
            navController.navigate(R.id.nav_menu, bundleInit);

            navigationView.getMenu().getItem(4).setVisible(false);
            navigationView.getMenu().getItem(5).setVisible(false);
            navigationView.getMenu().getItem(6).setVisible(false);
            navigationView.getMenu().getItem(7).setVisible(false);
            //navigationView.getMenu().getItem(8).setVisible(false);
            navigationView.getMenu().getItem(0).setChecked(true);
            navigationView.getMenu().getItem(8).setVisible(false);
            navigationView.getMenu().getItem(9).setVisible(false);
            navigationView.getMenu().getItem(10).setVisible(false);
            navigationView.getMenu().getItem(11).setVisible(false);
            navigationView.getMenu().getItem(12).setVisible(false);
            navigationView.getMenu().getItem(13).setVisible(false);
            navigationView.getMenu().getItem(14).setVisible(false);

            navigationView.setNavigationItemSelectedListener(item ->{
                switch (item.getItemId()){
                    case R.id.nav_menu:
                        navController.navigate(R.id.nav_menu, bundleInit);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_historial:
                        Bundle bundleHistorial = new Bundle();
                        bundleHistorial.putString("id_estacion", id_estacion);
                        bundleHistorial.putString("id_empleado", id_empleado);
                        bundleHistorial.putString("nombre_empleado", nombre_empleado);
                        navController.navigate(R.id.nav_historial, bundleHistorial);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_menu_principal:
                        finish();
                        break;
                    case R.id.nav_cerrar_sesion:
                        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_LONG).show();
                        Intent iCerrarSesion = new Intent(NavigationActivity.this, MainActivity.class);
                        startActivity(iCerrarSesion);
                        finish();
                        break;
                }
                return true;
            });
        }
        else  if(tipo.equals("yelow")){

            mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_menu_yelow, R.id.nav_menu_principal_yelow, R.id.nav_inventario_yelow)
                    .setDrawerLayout(drawer)
                    .build();
            navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);

            imageView = navigationView.getHeaderView(0).findViewById(R.id.imageView);
            textViewMenu = navigationView.getHeaderView(0).findViewById(R.id.textViewHeader);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.combu_icon, getApplicationContext().getTheme()));
            navigationView.getHeaderView(0).setBackgroundColor(getResources().getColor(R.color.blueLight2Yel));
            textViewMenu.setText("Yelow");
            toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.blueLight2Yel));
            navigationView.setItemBackground(getResources().getDrawable(R.drawable.yelow_navigation_view_item));
            navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.black)));
            navController.navigate(R.id.nav_menu_yelow, bundleInit);

            navigationView.getMenu().getItem(0).setVisible(false);
            navigationView.getMenu().getItem(1).setVisible(false);
            navigationView.getMenu().getItem(2).setVisible(false);
            navigationView.getMenu().getItem(3).setVisible(false);
            navigationView.getMenu().getItem(4).setVisible(false);
            navigationView.getMenu().getItem(5).setVisible(false);
            navigationView.getMenu().getItem(6).setVisible(false);
            navigationView.getMenu().getItem(7).setVisible(false);
            //navigationView.getMenu().getItem(8).setVisible(false);
            navigationView.getMenu().getItem(0).setChecked(true);
            navigationView.getMenu().getItem(8).setVisible(false);
            navigationView.getMenu().getItem(9).setVisible(false);
            navigationView.getMenu().getItem(10).setVisible(false);
            navigationView.getMenu().getItem(11).setVisible(false);
            navigationView.getMenu().getItem(12).setVisible(true);
            navigationView.getMenu().getItem(13).setVisible(true);
            navigationView.getMenu().getItem(14).setVisible(true);

            navigationView.setNavigationItemSelectedListener(item ->{
                switch (item.getItemId()){
                    case R.id.nav_menu_yelow:
                        navController.navigate(R.id.nav_menu_yelow, bundleInit);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_menu_principal_yelow:
                        finish();
                        break;
                    case R.id.nav_cerrar_sesion_yelow:
                        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_LONG).show();
                        Intent iCerrarSesion = new Intent(NavigationActivity.this, MainActivity.class);
                        startActivity(iCerrarSesion);
                        finish();
                        break;
                }
                return true;
            });
        }
        else  if(tipo.equals("voucher")){

            mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_menu,
                    R.id.nav_vouchers_registro, R.id.nav_historial_vouchers)
                    .setDrawerLayout(drawer)
                    .build();
            navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);

            imageView = navigationView.getHeaderView(0).findViewById(R.id.imageView);
            textViewMenu = navigationView.getHeaderView(0).findViewById(R.id.textViewHeader);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.combu_icon, getApplicationContext().getTheme()));
            navigationView.getHeaderView(0).setBackgroundColor(getResources().getColor(R.color.yellow));
            textViewMenu.setText("Vouchers");
            toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow));
            navigationView.setItemBackground(getResources().getDrawable(R.drawable.tanques_navigation_view_item));
            navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.black)));
            navController.navigate(R.id.nav_vouchers_registro, bundleInit);


            navigationView.getMenu().getItem(0).setVisible(false);
            navigationView.getMenu().getItem(1).setVisible(false);
            navigationView.getMenu().getItem(2).setVisible(false);
            navigationView.getMenu().getItem(3).setVisible(false);
            navigationView.getMenu().getItem(4).setVisible(false);
            navigationView.getMenu().getItem(5).setVisible(false);
            navigationView.getMenu().getItem(6).setVisible(false);
            navigationView.getMenu().getItem(7).setVisible(false);
            navigationView.getMenu().getItem(8).setChecked(true);

            navigationView.setNavigationItemSelectedListener(item ->{
                switch (item.getItemId()){
                    case R.id.nav_vouchers_registro:
                        navController.navigate(R.id.nav_vouchers_registro, bundleInit);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_historial_vouchers:
                        Bundle bundleHistorial = new Bundle();
                        bundleHistorial.putString("id_estacion", id_estacion);
                        bundleHistorial.putString("id_empleado", id_empleado);
                        bundleHistorial.putString("nombre_empleado", nombre_empleado);
                        navController.navigate(R.id.nav_historial_vouchers, bundleHistorial);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_menu_principal_vouchers:
                        finish();
                        break;
                    case R.id.nav_cerrar_sesion_vouchers:
                        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_LONG).show();
                        Intent iCerrarSesion = new Intent(NavigationActivity.this, MainActivity.class);
                        startActivity(iCerrarSesion);
                        finish();
                        break;
                }
                return true;
            });

        }








    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            if(tipo.equals("tanques")){
                if(CurrentFragment.currentFragment.equals("NAV_MENU")){
                    finish();
                }
                else{
                    navController.navigate(R.id.nav_menu, bundleInit);
                }
            }
            else if(tipo.equals("awa")){
                if(CurrentFragment.currentFragment.equals("NAV_MENU_AWA")){
                    finish();
                }
                else{
                    navController.navigate(R.id.nav_menu_awa, bundleInit);
                }
            }
            else if(tipo.equals("voucher")){
                if(CurrentFragment.currentFragment.equals("NAV_REGISTRO_VOUCHERS")){
                    finish();
                }
                else{
                    navController.navigate(R.id.nav_vouchers_registro, bundleInit);
                }
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    public void getEstacion(String id_estacion){

        String url = URL.URL_ESTACIONES + "?id_estacion=" +id_estacion;
        vs = VolleyS.getInstance(this.getApplicationContext());
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

                            bundleInit.putString("cantidad_30_tanques", "" + cantidad_30_tanques);
                            bundleInit.putString("cantidad_45_tanques", "" + cantidad_45_tanques);

                            navController.navigate(R.id.nav_inventario, bundleInit);

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

