package modelos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_combugas_nueva.R;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AdaptadorHistorialVoucherRecycler extends RecyclerView.Adapter<AdaptadorHistorialVoucherRecycler.AdaptadorHistorialVoucherRecyclerHolder>{

    private Context mContext;
    ArrayList<HistorialVoucher> listaHistorial;

    public AdaptadorHistorialVoucherRecycler (ArrayList<HistorialVoucher> listaHistorial ,Context context) {
        this.listaHistorial = listaHistorial;
        this.mContext =context;
    }

    @NonNull
    @Override
    public AdaptadorHistorialVoucherRecycler.AdaptadorHistorialVoucherRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voucher, parent, false);

        return new AdaptadorHistorialVoucherRecycler.AdaptadorHistorialVoucherRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorHistorialVoucherRecycler.AdaptadorHistorialVoucherRecyclerHolder holder, int position) {
        holder.imprimir(position);
    }

    @Override
    public int getItemCount() {
        return listaHistorial.size();
    }

    public class AdaptadorHistorialVoucherRecyclerHolder extends RecyclerView.ViewHolder{

        TextView carburista, operador, estacion, numAut, banco, importe, tarjeta,fecha, voucherTitulo;

        public AdaptadorHistorialVoucherRecyclerHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            carburista = itemView.findViewById(R.id.textViewCarburistaVoucher);
            //operador = itemView.findViewById(R.id.textViewOperadorVoucher);
            fecha = itemView.findViewById(R.id.textViewFechaVoucher);
            // estacion = itemView.findViewById(R.id.textViewEstacionVoucher);
            numAut = itemView.findViewById(R.id.textViewNumAutVoucher);
            //banco = itemView.findViewById(R.id.textViewBancoVoucher);
            importe = itemView.findViewById(R.id.textViewImporteVoucher);
            tarjeta = itemView.findViewById(R.id.textViewTarjetaVoucher);
            voucherTitulo = itemView.findViewById(R.id.textViewVoucherTitulo);

        }

        public void imprimir(int position) {

            DecimalFormat formateador = new DecimalFormat("###,###.00");

            carburista.setText("Carburista: " + listaHistorial.get(position).getCarburista());
//            operador.setText("Operador: " +listaHistorial.get(position).getOperador());
         //   estacion.setText("Estación: " +listaHistorial.get(position).getEstacion());
            numAut.setText("Num. Aut.: " +listaHistorial.get(position).getNumAut());
            //banco.setText("Banco: " +listaHistorial.get(position).getBanco());
            importe.setText("Importe: $" + formateador.format(Double.parseDouble(listaHistorial.get(position).getImporte())));
            tarjeta.setText("Tarjeta: ************" +listaHistorial.get(position).getTarjeta());
            fecha.setText("Fecha: " + listaHistorial.get(position).getFecha());
            voucherTitulo.setText("VOUCHER " + listaHistorial.get(position).getEstacion());


        }
    }
}
