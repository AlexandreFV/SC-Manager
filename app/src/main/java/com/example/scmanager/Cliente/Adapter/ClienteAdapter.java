package com.example.scmanager.Cliente.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scmanager.Cliente.Cliente;
import com.example.scmanager.Cliente.Tela.TelaGerenciamentoCliente;
import com.example.scmanager.R;

import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder> {

    private List<Cliente> listaClientes;
    private Context context;

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

        // Alterna a cor de fundo do item
        if (position % 2 == 0) {
            holder.fundoItemTabelaCliente.setBackgroundColor(context.getResources().getColor(R.color.color_FFEDCC)); // Cor 1 (exemplo: FFEDCC)
        } else {
            holder.fundoItemTabelaCliente.setBackgroundColor(context.getResources().getColor(R.color.color_DCEEFB)); // Cor 2 (exemplo: DCEEFB)
        }

        holder.lupaButton.setOnClickListener(v -> {
            // Passa o cliente clicado para a atividade ou fragmento
            if (context instanceof TelaGerenciamentoCliente) {
                ((TelaGerenciamentoCliente) context).onClienteClicked(cliente); // Chama o metodo na classe que exibe o RecyclerView
            }
        });
    }



    @Override
    public int getItemCount() {
        return listaClientes != null ? listaClientes.size() : 0;
    }

    // Metodo para atualizar a lista de clientes
    public void setClientes(List<Cliente> listaClientes) {
        this.listaClientes = listaClientes;
        notifyDataSetChanged(); // Notifica o RecyclerView sobre as mudanças
    }

    public static class ClienteViewHolder extends RecyclerView.ViewHolder {
        TextView nomeTextView;
        TextView telefoneTextView;
        ImageButton lupaButton;
        View fundoItemTabelaCliente; // Referência para o fundo do item

        public ClienteViewHolder(View itemView) {
            super(itemView);
            nomeTextView = itemView.findViewById(R.id.textNomeClienteTabela);
            telefoneTextView = itemView.findViewById(R.id.textTelefoneClienteTabela);
            lupaButton = itemView.findViewById(R.id.imageLupaDetalhes);
            fundoItemTabelaCliente = itemView.findViewById(R.id.fundoItemTabelaCliente); // Inicializa a referência

        }

    }

    // Metodo para atualizar os dados
    public void atualizarClientes(List<Cliente> novosClientes) {
        this.listaClientes = novosClientes;
        notifyDataSetChanged();
    }

    public void setData(List<Cliente> clientes) {
        this.listaClientes.clear();
        this.listaClientes.addAll(clientes);
        notifyDataSetChanged();
    }
}
