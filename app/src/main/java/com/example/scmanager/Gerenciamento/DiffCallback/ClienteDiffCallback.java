package com.example.scmanager.Gerenciamento.DiffCallback;

import androidx.recyclerview.widget.DiffUtil;

import com.example.scmanager.Gerenciamento.Objetos.Categoria;
import com.example.scmanager.Gerenciamento.Objetos.Cliente;

import java.util.List;

public class ClienteDiffCallback extends DiffUtil.Callback{

    private final List<Cliente> oldList;
    private final List<Cliente> newList;

    public ClienteDiffCallback(List<Cliente> oldList, List<Cliente> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // Verifica se os itens são os mesmos (você pode usar o ID ou algum identificador único)
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // Verifica se o conteúdo dos itens é o mesmo
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }

    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}

