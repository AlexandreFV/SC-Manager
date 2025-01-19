package com.example.scmanager.GerenciamentoDeCategorias;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scmanager.BancoDeDados.CategoriaRepository;
import com.example.scmanager.BancoDeDados.ControladorBancoDeDados;
import com.example.scmanager.R;

import java.util.List;

public class TelaGerenciamentoDeCategorias extends AppCompatActivity {

    private RecyclerView recyclerViewCategorias;
//    private CategoriaAdapter adapter;
    private CategoriaRepository categoriaRepository;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        recyclerViewCategorias = findViewById(R.id.recyclerViewCategorias);
        recyclerViewCategorias.setLayoutManager(new LinearLayoutManager(this));

        ControladorBancoDeDados controlador = ControladorBancoDeDados.getInstance(TelaGerenciamentoDeCategorias.this);
        SQLiteDatabase banco = controlador.getWritableDatabase();
        CategoriaRepository categoriaRepository = new CategoriaRepository(banco);

        // Obtenha as categorias do banco de dados
        List<String> categorias = categoriaRepository.BuscarCategorias();

        // Agora, inicialize o adaptador e passe as categorias para ele
//        adapter = new CategoriaAdapter(categorias);
//        recyclerViewCategorias.setAdapter(adapter);

    }

    private void editarCategoriaDeServico()
    {

    }

    private void excluirCategoriaDeServico()
    {

    }
}
