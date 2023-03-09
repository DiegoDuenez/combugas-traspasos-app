package modelos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_combugas_nueva.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdaptadorRecycler extends RecyclerView.Adapter<AdaptadorRecycler.AdaptadorRecyclerHolder> {

    private Context mContext;
    public ArrayList<Historial> listaHistorial;

    public AdaptadorRecycler (ArrayList<Historial> listaHistorial ,Context context) {
        this.listaHistorial = listaHistorial;
        this.mContext =context;
    }


    @NotNull
    @Override
    public AdaptadorRecyclerHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);
        return new AdaptadorRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdaptadorRecyclerHolder holder, int position) {
        holder.imprimir(position);
    }

    @Override
    public int getItemCount() {
        return listaHistorial.size();
    }

    public class AdaptadorRecyclerHolder extends RecyclerView.ViewHolder{

        TextView vehiculo, tanques30, tanques45, fecha, vehiiculoPublicoText;

        public AdaptadorRecyclerHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            vehiculo = itemView.findViewById(R.id.vehiculoHistorial);
            tanques30 = itemView.findViewById(R.id.tank30Historial);
            tanques45 = itemView.findViewById(R.id.tan45Historial);
            fecha = itemView.findViewById(R.id.fecha);
            vehiiculoPublicoText = itemView.findViewById(R.id.vehiiculoPublicoText);
        }

        public void imprimir(int position) {
            vehiculo.setText(listaHistorial.get(position).getVehiculo());
            tanques30.setText(String.valueOf(listaHistorial.get(position).getTanques30()));
            tanques45.setText(String.valueOf(listaHistorial.get(position).getTanques45()));
            fecha.setText(listaHistorial.get(position).getFecha());
            // Toast.makeText(mContext.getApplicationContext(), "" + listaHistorial.get(position).getVehiculo(), Toast.LENGTH_SHORT).show();
            if(listaHistorial.get(position).getVehiculo().equals("") || listaHistorial.get(position).getVehiculo().isEmpty()){
                vehiiculoPublicoText.setText("Público");
                vehiculo.setWidth(60);
                vehiculo.setBackgroundResource(R.drawable.ic_publico);
            }
            else{

                vehiiculoPublicoText.setText("Vehículo");
                vehiculo.setWidth(100);
                vehiculo.setBackgroundResource(0);

            }
        }
    }
}
