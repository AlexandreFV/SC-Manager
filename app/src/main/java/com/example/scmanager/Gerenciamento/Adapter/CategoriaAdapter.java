package com.example.scmanager.Gerenciamento.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scmanager.Gerenciamento.DiffCallback.CategoriaDiffCallback;
import com.example.scmanager.Gerenciamento.Objetos.Categoria;
import com.example.scmanager.Gerenciamento.Tela.TelaGerenciamento;
import com.example.scmanager.R;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder> {

    private List<Categoria> ListaCategorias;
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

    public CategoriaAdapter(Context context, List<Categoria> ListaCategorias) {
        this.context = context;
        this.ListaCategorias = ListaCategorias;
    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_lista_categoria, parent, false);
        return new CategoriaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder holder, int position) {
        Categoria categoria = ListaCategorias.get(position);
        holder.nomeTextView.setText(categoria.getNome());

        // Define a cor do backgroundTint para imagePersonCard
        int corIndex = position % coresBackground.length; // Garante que o índice esteja no intervalo do array
        holder.imagePersonCard.setBackgroundTintList(
                context.getResources().getColorStateList(coresBackground[corIndex])
        );

        holder.lupaButton.setOnClickListener(v -> {
            // Passa o cliente clicado para a atividade ou fragmento
            if (context instanceof TelaGerenciamento) {
                ((TelaGerenciamento) context).onCategoriaClicked(categoria); // Chama o metodo na classe que exibe o RecyclerView
            }
        });
    }

    @Override
    public int getItemCount() {
        return ListaCategorias != null ? ListaCategorias.size() : 0;
    }

    public List<Categoria> getCategoria() {
        return ListaCategorias;
    }

    public void setCategoria(List<Categoria> listaCategorias) {
        // Use o DiffUtil para comparar as listas e otimizar as atualizações
        CategoriaDiffCallback diffCallback = new CategoriaDiffCallback(this.ListaCategorias, listaCategorias);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        // Atualiza a lista de categorias
        this.ListaCategorias.clear();
        this.ListaCategorias.addAll(listaCategorias);

        // Aplica as mudanças ao RecyclerView
        diffResult.dispatchUpdatesTo(this);  // Notifica o RecyclerView sobre as mudanças
    }



    public static class CategoriaViewHolder extends RecyclerView.ViewHolder {
        TextView nomeTextView;
        ImageButton lupaButton;
        ImageView imagePersonCard;
        public CategoriaViewHolder(View itemView) {
            super(itemView);
            nomeTextView = itemView.findViewById(R.id.textNomeCategoriaTabela);
            lupaButton = itemView.findViewById(R.id.imageLupaDetalhes);
            imagePersonCard = itemView.findViewById(R.id.imagePersonCard);
        }

    }
}
