package com.example.scmanager.Gerenciamento.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scmanager.Gerenciamento.DiffCallback.CategoriaDiffCallback;
import com.example.scmanager.Gerenciamento.DiffCallback.ClienteDiffCallback;
import com.example.scmanager.Gerenciamento.Objetos.Categoria;
import com.example.scmanager.Gerenciamento.Objetos.Cliente;
import com.example.scmanager.Gerenciamento.Tela.TelaGerenciamento;
import com.example.scmanager.R;

import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder> {

    private List<Cliente> listaClientes;
    private Context context;

    private int[] coresBackground = {
            R.color.color1,
            R.color.color2,
            R.color.color3,
            R.color.color4,
            R.color.color5,
            R.color.color6,
            R.color.color7,
            R.color.color8
    };

    public ClienteAdapter(Context context, List<Cliente> listaClientes) {
        this.context = context;
        this.listaClientes = listaClientes;
    }

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_lista_cliente, parent, false);
        return new ClienteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteViewHolder holder, int position) {
        Cliente cliente = listaClientes.get(position);
        holder.nomeTextView.setText(cliente.getNome());
        holder.telefoneTextView.setText(cliente.getTelefone());

        // Define a cor do backgroundTint para imagePersonCard
        int corIndex = position % coresBackground.length; // Garante que o índice esteja no intervalo do array
        holder.imagePersonCard.setBackgroundTintList(
                context.getResources().getColorStateList(coresBackground[corIndex])
        );

        holder.lupaButton.setOnClickListener(v -> {
            // Passa o cliente clicado para a atividade ou fragmento
            if (context instanceof TelaGerenciamento) {
                ((TelaGerenciamento) context).onClienteClicked(cliente); // Chama o metodo na classe que exibe o RecyclerView
            }
        });
    }



    @Override
    public int getItemCount() {
        return listaClientes != null ? listaClientes.size() : 0;
    }

    // Metodo para atualizar a lista de clientes
    public void setCliente(List<Cliente> listaClientes) {
        // Use o DiffUtil para comparar as listas e otimizar as atualizações
        ClienteDiffCallback diffCallback = new ClienteDiffCallback(this.listaClientes, listaClientes);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.listaClientes.clear();
        this.listaClientes.addAll(listaClientes);

        // Aplica as mudanças ao RecyclerView
        diffResult.dispatchUpdatesTo(this);  // Notifica o RecyclerView sobre as mudanças
    }


    public static class ClienteViewHolder extends RecyclerView.ViewHolder {
        TextView nomeTextView;
        TextView telefoneTextView;
        ImageButton lupaButton;
        ImageView imagePersonCard;

        ConstraintLayout layoutCard;
        public ClienteViewHolder(View itemView) {
            super(itemView);
            nomeTextView = itemView.findViewById(R.id.textNomeCategoriaTabela);
            telefoneTextView = itemView.findViewById(R.id.textTelefoneClienteTabela);
            lupaButton = itemView.findViewById(R.id.imageLupaDetalhes);
            imagePersonCard = itemView.findViewById(R.id.imagePersonCard);
            layoutCard = itemView.findViewById(R.id.layoutCard);
        }

    }
}
