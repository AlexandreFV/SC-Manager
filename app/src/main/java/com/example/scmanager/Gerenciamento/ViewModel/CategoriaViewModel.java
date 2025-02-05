package com.example.scmanager.Gerenciamento.ViewModel;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.scmanager.BancoDeDados.CategoriaRepository;
import com.example.scmanager.Gerenciamento.Objetos.Categoria;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CategoriaViewModel extends AndroidViewModel {
    private CategoriaRepository categoriaRepository;
    private MutableLiveData<List<Categoria>> listaCategorias = new MutableLiveData<>();

    public CategoriaViewModel(@NonNull Application application) {
        super(application);
        categoriaRepository = new CategoriaRepository(application.getApplicationContext());
        listaCategorias = new MutableLiveData<>();
        carregarCategorias();
    }


    public LiveData<List<Categoria>> getListaCategorias() {
        return listaCategorias;
    }

    public void adicionarCategoria(String nomeCategoria) {
        categoriaRepository.AdicionarCategoriaAsync(nomeCategoria, new CategoriaRepository.CategoriaCallback() {
            @Override
            public void onResult(long resultado) {
                if (resultado != -1) {
                    carregarCategorias();
                    showToast("Categoria adicionada com sucesso!");
                } else {
                    Log.d("CategoriaViewModel", "Erro ao adicionar categoria.");
                    showToast("Erro ao adicionar categoria.");
                }
            }
        });
    }

    public void excluirCategoria(int categoriaId) {
        categoriaRepository.ExcluirCategoriaAsync(categoriaId, new CategoriaRepository.CategoriaCallback() {
            @Override
            public void onResult(long resultado) {
                if (resultado != -1) {
                    carregarCategorias();
                    showToast("Categoria excluída com sucesso!");
                } else {
                    Log.d("CategoriaViewModel", "Erro ao excluir categoria.");
                    showToast("Erro ao excluir categoria.");
                }
            }
        });
    }

    public void editarCategoria(int idCategoria, String nomeCategoria) {
        categoriaRepository.EditarCategoriaAsync(idCategoria, nomeCategoria, new CategoriaRepository.CategoriaCallback() {
            @Override
            public void onResult(long resultado) {
                if (resultado == 1) {
                    carregarCategorias();
                    showToast("Categoria editada com sucesso!");
                } else if (resultado == 0) {
                    showToast("Erro ao editar categoria. Verifique se os dados já existem ou se a categoria é válida.");
                } else {
                    Log.d("CategoriaViewModel", "Erro ao editar categoria.");
                    showToast("Erro ao editar categoria.");
                }
            }
        });
    }

    public void carregarCategorias() {
        SharedPreferences prefs = getApplication().getSharedPreferences("PreferenciasApp", getApplication().MODE_PRIVATE);
        String ordemSalva = prefs.getString("ordemCategoria", "CRESCENTE"); // Padrão: CRESCENTE

        categoriaRepository.CarregarCategoriasAsync(new CategoriaRepository.CategoriaListCallback() {
            @Override
            public void onCategoriasLoaded(List<Categoria> categorias) {
                if (ordemSalva.equals("CRESCENTE")) {
                    Collections.sort(categorias, Comparator.comparing(Categoria::getNome)); // Ordem alfabética crescente
                } else if (ordemSalva.equals("DECRESCENTE")) {
                    Collections.sort(categorias, (c1, c2) -> c2.getNome().compareTo(c1.getNome())); // Ordem decrescente
                }

                listaCategorias.postValue(categorias); // Atualiza a LiveData
            }
        });
    }




    private void showToast(String mensagem) {
        new android.os.Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(getApplication().getBaseContext(), mensagem, Toast.LENGTH_SHORT).show()
        );
    }
}

