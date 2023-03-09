package modelos;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_combugas_nueva.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdaptadorHistorialAwaRecycler extends RecyclerView.Adapter<AdaptadorHistorialAwaRecycler.AdaptadorHistorialAwaRecyclerHolder>{

    private Context mContext;
    ArrayList<HistorialAwa> listaHistorial;

    public AdaptadorHistorialAwaRecycler (ArrayList<HistorialAwa> listaHistorial ,Context context) {
        this.listaHistorial = listaHistorial;
        this.mContext =context;
    }

    @NotNull
    @Override
    public AdaptadorHistorialAwaRecycler.AdaptadorHistorialAwaRecyclerHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        SharedPreferences prefe = mContext.getSharedPreferences("datos", Context.MODE_PRIVATE);
        String temaApp = prefe.getString("tema","awaTema");
        int layout = 0;
        if(temaApp.equals("awaTema")){
            layout = R.layout.item_historial_awa;
        }
        else if(temaApp.equals("alkTema")){
            layout = R.layout.item_historial_alk;
        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);

        return new AdaptadorHistorialAwaRecycler.AdaptadorHistorialAwaRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdaptadorHistorialAwaRecycler.AdaptadorHistorialAwaRecyclerHolder holder, int position) {
        holder.imprimir(position);
    }

    @Override
    public int getItemCount() {
        return listaHistorial.size();
    }

    public class AdaptadorHistorialAwaRecyclerHolder extends RecyclerView.ViewHolder{
        TextView vehiculo, garrAwa, garrAlk, fecha, vehiiculoPublicoText, sixAwa, sixAlk;

        public AdaptadorHistorialAwaRecyclerHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            vehiculo = itemView.findViewById(R.id.vehiculoAwaHistorial);
            garrAwa = itemView.findViewById(R.id.garAwaHistorial);
            garrAlk = itemView.findViewById(R.id.garAlkHistorial);
            /*sixAwa = itemView.findViewById(R.id.sixAwaHistorial);
            sixAlk = itemView.findViewById(R.id.sixAlkHistorial);*/
            fecha = itemView.findViewById(R.id.fechaAwa);
            vehiiculoPublicoText = itemView.findViewById(R.id.vehiiculoPublicoTextAwa);
        }

        public void imprimir(int position) {
            vehiculo.setText(listaHistorial.get(position).getVehiculo());
            garrAwa.setText(String.valueOf(listaHistorial.get(position).getGarrAwa()));
            garrAlk.setText(String.valueOf(listaHistorial.get(position).getGarrAlk()));
            /*sixAwa.setText(String.valueOf(listaHistorial.get(position).getSixAwa()));
            sixAlk.setText(String.valueOf(listaHistorial.get(position).getSixAlk()));*/

            fecha.setText(listaHistorial.get(position).getFecha());
            if(listaHistorial.get(position).getVehiculo().equals("") || listaHistorial.get(position).getVehiculo().isEmpty()){
                vehiiculoPublicoText.setText("Público");
                vehiculo.setWidth(60);
                vehiculo.setBackgroundResource(R.drawable.ic_publico);
                //listaSalidasPublicas.add(listaHistorial.get(position));
            }
            else{
                vehiiculoPublicoText.setText("Vehículo");
                vehiculo.setWidth(100);
                vehiculo.setBackgroundResource(0);
                //listaSalidasVeh.add(listaHistorial.get(position));
            }
        }
    }

}
