package modelos;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_combugas_nueva.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorEntradaAwaRecycler extends RecyclerView.Adapter<AdaptadorEntradaAwaRecycler.AdaptadorEntradaAwaRecyclerHolder> {

    private Context mContext;

    public ArrayList<ValeAwa> listaValesUsados = new ArrayList<>();
    public ArrayList<ValeAwa> listaVales;
    public ArrayList<ValeAwa> listaValesPublicos;
    public ArrayList<ValeAwa> listaValesVehiculares;

    TextView cantValesSeleccionados;
    boolean isSelectAll = false;
    Button btnSeleccionarTodos;

    public boolean isSelectMode = true;

    public AdaptadorEntradaAwaRecycler (ArrayList<ValeAwa> listaVales , Context context) {
        this.listaVales = listaVales;
        this.mContext =context;
        setHasStableIds(true);

    }



   public void selecteAll() {
       listaValesUsados.clear();
       if(isSelectAll == false){
           isSelectAll = true;
           try {
               if (listaVales != null) {
                   for (int index = 0; index < listaVales.size(); index++) {
                       listaValesUsados.add(listaVales.get(index));
                   }
               }
               notifyDataSetChanged();
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
       else{
           listaValesUsados.clear();
           isSelectAll = false;
       }
       cantValesSeleccionados.setText(listaValesUsados.size() + " de " + listaVales.size() + " vales seleccionados");

        notifyDataSetChanged();
    }

    public void selectAllOnly(){
        listaValesUsados.clear();
        if(isSelectAll == false){
            isSelectAll = true;
            try {
                if (listaVales != null) {
                    for (int index = 0; index < listaVales.size(); index++) {
                        listaValesUsados.add(listaVales.get(index));
                    }
                }
                notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cantValesSeleccionados.setText(listaValesUsados.size() + " de " + listaVales.size() + " vales seleccionados");

    }
    @NotNull
    @Override
    public AdaptadorEntradaAwaRecycler.AdaptadorEntradaAwaRecyclerHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        SharedPreferences prefe = mContext.getSharedPreferences("datos", Context.MODE_PRIVATE);
        String temaApp = prefe.getString("tema","awaTema");
        int layout = 0;
        if(temaApp.equals("awaTema")){
            layout = R.layout.item_vale_awa;
        }
        else if(temaApp.equals("alkTema")){
            layout = R.layout.item_vale_alk;
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);


        return new AdaptadorEntradaAwaRecycler.AdaptadorEntradaAwaRecyclerHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return Integer.parseInt(listaVales.get(position).getId_vale());
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdaptadorEntradaAwaRecycler.AdaptadorEntradaAwaRecyclerHolder holder, int position) {
        if(isSelectAll){
            holder.btnSeleccionarVale.setBackground(ContextCompat.getDrawable(mContext, R.drawable.normal_blue_button));
            holder.btnSeleccionarVale.setTextColor(Color.WHITE);
        }
        else{
            holder.btnSeleccionarVale.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ghost_button_awa));
            holder.btnSeleccionarVale.setTextColor(ContextCompat.getColor(mContext, R.color.blueAwa));
        }
        holder.imprimir(position);
    }

    @Override
    public int getItemCount() {
        return listaVales.size();
    }

    public class AdaptadorEntradaAwaRecyclerHolder extends RecyclerView.ViewHolder {
        TextView folio, estacion, carburista, cilindrero, cant_awa, cant_alk, unidad, cant_six_awa, cant_six_alk;

        CheckBox checkBox;
        Button btnEntrada, btnSeleccionarVale;
        public AdaptadorEntradaAwaRecyclerHolder(@NonNull View itemView) {
            super(itemView);
            SharedPreferences prefe = mContext.getSharedPreferences("datos", Context.MODE_PRIVATE);
            String temaApp = prefe.getString("tema","awaTema");

            folio = itemView.findViewById(R.id.textViewFolioAwa);
            estacion = itemView.findViewById(R.id.textViewEstacionAwa);
            carburista = itemView.findViewById(R.id.textViewCarburistaAwa);
            cilindrero = itemView.findViewById(R.id.textViewCilindreroAwa);
            cant_awa= itemView.findViewById(R.id.textViewCantAwa);
            cant_alk = itemView.findViewById(R.id.textViewCantAlk);
            /*cant_six_awa = itemView.findViewById(R.id.textViewCantSixAwa);
            cant_six_alk = itemView.findViewById(R.id.textViewCantSixAlk);*/
            unidad = itemView.findViewById(R.id.textViewUnidadAwa);

            cantValesSeleccionados = ((Activity) mContext).findViewById(R.id.textViewCantValesSeleccionadosAwa);

            btnSeleccionarVale = itemView.findViewById(R.id.btnSeleccionarValeAwa);
            btnEntrada = itemView.findViewById(R.id.btnEntradaAwa);

            btnSeleccionarVale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isSelectMode){

                        if(temaApp.equals("awaTema")){
                            if(listaValesUsados.contains(listaVales.get(getAdapterPosition()))){
                                btnSeleccionarVale.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ghost_button_awa));
                                btnSeleccionarVale.setTextColor(ContextCompat.getColor(mContext, R.color.blueAwa));
                                btnSeleccionarVale.setText("SELECCIONAR");
                                listaValesUsados.remove(listaVales.get(getAdapterPosition()));
                            }else{
                                btnSeleccionarVale.setBackground(ContextCompat.getDrawable(mContext, R.drawable.normal_blue_button));
                                btnSeleccionarVale.setTextColor(Color.WHITE);
                                btnSeleccionarVale.setText("SELECCIONADO");
                                listaValesUsados.add(listaVales.get(getAdapterPosition()));
                            }
                            cantValesSeleccionados.setText(listaValesUsados.size() + " de " + listaVales.size() + " vales seleccionados");
                        }
                        else if(temaApp.equals("alkTema")){
                            if(listaValesUsados.contains(listaVales.get(getAdapterPosition()))){
                                btnSeleccionarVale.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ghost_button_alk));
                                btnSeleccionarVale.setTextColor(ContextCompat.getColor(mContext, R.color.verdeAlk));
                                btnSeleccionarVale.setText("SELECCIONAR");
                                listaValesUsados.remove(listaVales.get(getAdapterPosition()));
                            }else{
                                btnSeleccionarVale.setBackground(ContextCompat.getDrawable(mContext, R.drawable.normal_green_button));
                                btnSeleccionarVale.setTextColor(Color.WHITE);
                                btnSeleccionarVale.setText("SELECCIONADO");
                                listaValesUsados.add(listaVales.get(getAdapterPosition()));
                            }
                            cantValesSeleccionados.setText(listaValesUsados.size() + " de " + listaVales.size() + " vales seleccionados");
                        }

                       ;
                    }
                }
            });
        }

        public void imprimir(int position) {
            folio.setText("Folio: " + listaVales.get(position).getFolio());
            estacion.setText("Estación: "+listaVales.get(position).getNombre_estacion());
            carburista.setText("Carburista: "+listaVales.get(position).getNombre());
            cilindrero.setText("Repartidor: " +listaVales.get(position).getNombre_completo());
            cant_awa.setText("Garr. Awa: " + listaVales.get(position).getAwa_quantity());
            cant_alk.setText("Garr. Alk.: " +listaVales.get(position).getAlk_quantity());
/*            cant_six_awa.setText("Six Awa: " + listaVales.get(position).getSix_quantity());
            cant_six_alk.setText("Six Alk: " + listaVales.get(position).getSalk_quantity());*/
            unidad.setText("Unidad: " + listaVales.get(position).getDescripcion());

            if(listaVales.get(position).getNombre_completo() == "" || listaVales.get(position).getNombre_completo() =="null"){
                // cilindrero.setVisibility(View.INVISIBLE);
                cilindrero.setText("Movimiento público");
            }
            else{
                cilindrero.setVisibility(View.VISIBLE);
            }

            if(listaVales.get(position).getDescripcion() == "" || listaVales.get(position).getDescripcion() == "null"){
               // listaValesPublicos.add(listaVales.get(position));
                unidad.setVisibility(View.INVISIBLE);
            }
            else{
                //listaValesVehiculares.add(listaVales.get(position));
                unidad.setVisibility(View.VISIBLE);
            }

        }
    }

}
