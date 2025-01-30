package com.example.scmanager.Gerenciamento.DiffCallback;

import androidx.recyclerview.widget.DiffUtil;

import com.example.scmanager.Gerenciamento.Objetos.Categoria;

import java.util.List;

public class CategoriaDiffCallback extends DiffUtil.Callback{

    private final List<Categoria> oldList;
    private final List<Categoria> newList;

    public CategoriaDiffCallback(List<Categoria> oldList, List<Categoria> newList) {
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
        Categoria oldCategoria = oldList.get(oldItemPosition);
        Categoria newCategoria = newList.get(newItemPosition);
        return oldCategoria.equals(newCategoria);  // Supondo que você tenha implementado equals() em Categoria
    }

    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Se você quiser otimizar ainda mais, pode retornar um objeto de "payload" para mudanças parciais
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
