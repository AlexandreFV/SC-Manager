package com.example.scmanager.Gerenciamento.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import java.text.NumberFormat;
import java.util.Locale;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scmanager.Gerenciamento.DiffCallback.ClienteDiffCallback;
import com.example.scmanager.Gerenciamento.DiffCallback.ServicoDiffCallback;
import com.example.scmanager.Gerenciamento.Objetos.Categoria;
import com.example.scmanager.Gerenciamento.Objetos.Cliente;
import com.example.scmanager.Gerenciamento.Objetos.Servico;
import com.example.scmanager.Gerenciamento.Tela.TelaGerenciamento;
import com.example.scmanager.Gerenciamento.ViewModel.CategoriaViewModel;
import com.example.scmanager.Gerenciamento.ViewModel.ClienteViewModel;
import com.example.scmanager.Inicial.Tela.TelaInicial;
import com.example.scmanager.R;

import java.util.List;

public class ServicoAdapter extends RecyclerView.Adapter<ServicoAdapter.ServicoViewHolder> {

    private List<Servico> ListaServicos;
    private Context context;
    private CategoriaViewModel categoriaViewModel;
    private ClienteViewModel clienteViewModel;
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

    public ServicoAdapter(Context context, List<Servico> ListaServicos, CategoriaViewModel categoriaViewModel, ClienteViewModel clienteViewModel) {
        this.context = context;
        this.ListaServicos = ListaServicos;
        this.categoriaViewModel = categoriaViewModel;
        this.clienteViewModel = clienteViewModel;

        // Observa as mudanças nas categorias
        this.categoriaViewModel.getListaCategorias().observe((LifecycleOwner) context, categorias -> {
            // Atualize o adaptador sempre que as categorias mudarem
            notifyDataSetChanged();
        });

        this.clienteViewModel.getListaClientes().observe((LifecycleOwner) context, clientes -> {
            // Atualize o adaptador sempre que as categorias mudarem
            notifyDataSetChanged();
        });
    }

    @NonNull
    @Override
    public ServicoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_lista_servico, parent, false);
        return new ServicoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicoViewHolder holder, int position) {
        Servico servico = ListaServicos.get(position);

        String nomeCategoria = getNomeCategoriaById(servico.getTipoServico());
        holder.textTipoServico.setText(nomeCategoria);

        String nomeCliente = getNomeClienteiaById(servico.getIdCliente());
        holder.textNomeClienteServ.setText(nomeCliente);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
        holder.textValorClienteServ.setText(currencyFormat.format(servico.getValor()));

        holder.textEstadoServ.setText(String.valueOf(servico.getDataPagamento()));

        // Define a cor do backgroundTint para imagePersonCard
        int corIndex = position % coresBackground.length; // Garante que o índice esteja no intervalo do array
        holder.imageServicoCard.setBackgroundTintList(
                context.getResources().getColorStateList(coresBackground[corIndex])
        );

        int estado = servico.getEstado();
        if (estado == 1) {
            holder.textEstadoServ.setTextColor(Color.parseColor("#2B8A2F"));
            holder.textValorClienteServ.setTextColor(Color.parseColor("#2B8A2F"));
            holder.textEstadoServ.setCompoundDrawablesWithIntrinsicBounds(R.drawable.confirmar_icon, 0, 0, 0);
            holder.textEstadoServ.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#2B8A2F")));

        } else if (estado == 0) {
            holder.textEstadoServ.setTextColor(Color.parseColor("#A6212F"));
            holder.textValorClienteServ.setTextColor(Color.parseColor("#A6212F"));
            holder.textEstadoServ.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cancelar_icon,0, 0, 0);
            holder.textEstadoServ.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#A6212F")));
        }

        holder.imageLupaDetalhes2.setOnClickListener(v -> {
            // Passa o cliente clicado para a atividade ou fragmento
            if (context instanceof TelaGerenciamento) {
                ((TelaGerenciamento) context).onServicoClicked(servico); // Chama o metodo na classe que exibe o RecyclerView
            } else if (context instanceof TelaInicial) {
                ((TelaInicial) context).onServicoClicked(servico); // Chama o metodo na classe que exibe o RecyclerView
            }
        });
    }


    public String getNomeCategoriaById(int idCategoria) {
        if (categoriaViewModel.getListaCategorias().getValue() != null) {
            for (Categoria categoria : categoriaViewModel.getListaCategorias().getValue()) {
                if (categoria.getId() == idCategoria) {
                    return categoria.getNome(); // Supondo que 'getNome' retorne o nome da categoria
                }
            }
        }
        return "Categoria desconhecida"; // Caso não encontre a categoria
    }

    public String getNomeClienteiaById(int idCliente) {
        if (clienteViewModel.getListaClientes().getValue() != null) {
            for (Cliente cliente : clienteViewModel.getListaClientes().getValue()) {
                if (cliente.getId() == idCliente) {
                    return cliente.getNome(); // Supondo que 'getNome' retorne o nome da categoria
                }
            }
        }
        return "Categoria desconhecida"; // Caso não encontre a categoria
    }

    @Override
    public int getItemCount() {
        return ListaServicos != null ? ListaServicos.size() : 0;
    }

    // Metodo para atualizar a lista de clientes
    public void setServico(List<Servico> listaServicos) {
        // Use o DiffUtil para comparar as listas e otimizar as atualizações
        ServicoDiffCallback diffCallback = new ServicoDiffCallback(this.ListaServicos, listaServicos);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.ListaServicos.clear();
        this.ListaServicos.addAll(listaServicos);

        // Aplica as mudanças ao RecyclerView
        diffResult.dispatchUpdatesTo(this);  // Notifica o RecyclerView sobre as mudanças
    }

    public List<Servico> getServicos()
    {
        return ListaServicos;
    }
    public static class ServicoViewHolder extends RecyclerView.ViewHolder {
        TextView textTipoServico;
        TextView textNomeClienteServ;
        TextView textValorClienteServ;
        TextView textEstadoServ;
        ImageButton imageServicoCard;
        ImageButton imageLupaDetalhes2;
        public ServicoViewHolder(View itemView) {
            super(itemView);
            textTipoServico = itemView.findViewById(R.id.textTipoServico);
            textNomeClienteServ = itemView.findViewById(R.id.textNomeClienteServ);
            textValorClienteServ = itemView.findViewById(R.id.textValorClienteServ);
            textEstadoServ = itemView.findViewById(R.id.textEstadoServ);
            imageLupaDetalhes2 = itemView.findViewById(R.id.imageLupaDetalhes2);
            imageServicoCard = itemView.findViewById(R.id.imageServicoCard);
        }

    }
}
