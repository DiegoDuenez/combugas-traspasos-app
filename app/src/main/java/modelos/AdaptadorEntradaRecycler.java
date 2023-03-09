package modelos;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_combugas_nueva.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdaptadorEntradaRecycler extends RecyclerView.Adapter<AdaptadorEntradaRecycler.AdaptadorEntradaRecyclerHolder> {

    private Context mContext;
    public ArrayList<Vale> listaVales;
    public ArrayList<Vale> listaValesUsados = new ArrayList<>();
    TextView cantValesSeleccionados;

    public boolean isSelectMode = true;

    public AdaptadorEntradaRecycler (ArrayList<Vale> listaVales , Context context) {
        this.listaVales = listaVales;
        this.mContext =context;

        setHasStableIds(true);
    }

    public void seleccionarTodos(){
        this.listaValesUsados.clear();
        this.listaValesUsados.addAll(this.listaVales);
        cantValesSeleccionados.setText( listaValesUsados.size() + " de " + listaVales.size() + " vales seleccionados");

        notifyDataSetChanged();
    }

    public void limpiarTodos(){
        this.listaValesUsados.clear();
        notifyDataSetChanged();
    }

    @NotNull
    @Override
    public AdaptadorEntradaRecyclerHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vale, parent, false);

        return new AdaptadorEntradaRecyclerHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position){
        return Integer.parseInt(listaVales.get(position).getId_vale());
    }
    @Override
    public void onBindViewHolder(@NonNull @NotNull AdaptadorEntradaRecycler.AdaptadorEntradaRecyclerHolder holder, int position) {

        holder.imprimir(position);


    }


    @Override
    public int getItemCount() {
        return listaVales.size();
    }

    public class AdaptadorEntradaRecyclerHolder extends RecyclerView.ViewHolder {

        TextView folio, estacion, carburista, cilindrero, cant_30kg, cant_45kg, unidad;

        CheckBox checkBox;
        Button btnEntrada, btnSeleccionarVale;

        public AdaptadorEntradaRecyclerHolder(@NonNull View itemView) {
            super(itemView);

            folio = itemView.findViewById(R.id.textViewFolio);
            estacion = itemView.findViewById(R.id.textViewEstacion);
            carburista = itemView.findViewById(R.id.textViewCarburista);
            cilindrero = itemView.findViewById(R.id.textViewCilindrero);
            cant_30kg = itemView.findViewById(R.id.textViewCant30);
            cant_45kg = itemView.findViewById(R.id.textViewCant45);
            unidad = itemView.findViewById(R.id.textViewUnidad);
           //checkBox = itemView.findViewById(R.id.checkBoxVale);

            cantValesSeleccionados = ((Activity) mContext).findViewById(R.id.textViewCantValesSeleccionados);

            btnSeleccionarVale = itemView.findViewById(R.id.btnSeleccionarVale);
            btnEntrada = itemView.findViewById(R.id.btnEntrada);

            btnSeleccionarVale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isSelectMode){
                        if(listaValesUsados.contains(listaVales.get(getAdapterPosition()))){
                            btnSeleccionarVale.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ghost_button));
                            btnSeleccionarVale.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                            btnSeleccionarVale.setText("SELECCIONAR");
                            listaValesUsados.remove(listaVales.get(getAdapterPosition()));
                        }else{
                            btnSeleccionarVale.setBackground(ContextCompat.getDrawable(mContext, R.drawable.normal_red_button));
                            btnSeleccionarVale.setTextColor(Color.WHITE);
                            btnSeleccionarVale.setText("SELECCIONADO");
                            listaValesUsados.add(listaVales.get(getAdapterPosition()));
                        }
                        cantValesSeleccionados.setText(listaValesUsados.size() + " de " + listaVales.size() + " vales seleccionados");


                    }


                }
            });

        }

        public void imprimir(int position) {
            folio.setText("Folio: " + listaVales.get(position).getFolio());
            estacion.setText("Estación: "+listaVales.get(position).getNombre_estacion());
            carburista.setText("Carburista: "+listaVales.get(position).getNombre());
            cilindrero.setText("Cilindrero: " +listaVales.get(position).getNombre_completo());
            cant_30kg.setText("30 Kg: " + listaVales.get(position).getTank_30_quantity());
            cant_45kg.setText("45 Kg: " +listaVales.get(position).getTank_45_quantity());
            unidad.setText("Unidad: " + listaVales.get(position).getDescripcion());

            if(listaVales.get(position).getNombre_completo() == "" || listaVales.get(position).getNombre_completo() =="null"){
               // cilindrero.setVisibility(View.INVISIBLE);
                cilindrero.setText("Movimiento público");
            }
            else{
                cilindrero.setVisibility(View.VISIBLE);
            }

            if(listaVales.get(position).getDescripcion() == "" || listaVales.get(position).getDescripcion() == "null"){
                unidad.setVisibility(View.INVISIBLE);
            }
            else{
                unidad.setVisibility(View.VISIBLE);
            }

        }


    }
}
