package com.example.scmanager.Categorias.Tela;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.scmanager.BancoDeDados.CategoriaRepository;
import com.example.scmanager.BancoDeDados.ControladorBancoDeDados;
import com.example.scmanager.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class TelaAdicionarCategoria extends BottomSheetDialogFragment {


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate o layout do Bottom Sheet
        View view = inflater.inflate(R.layout.dialog_adicionar_cliente, container, false);

        // Inicializa o controlador do banco (Singleton)
        return view;
    }

    private void criarCategoriaDeServico()
    {
        // Pega o texto inserido no EditText
        //String nome = inputNomeCategoria.getText().toString().trim();

        // Valida se o nome foi preenchido
//        if (nome.isEmpty()) {
//            // Exibe um Toast ou Snackbar para o usuário saber que o campo está vazio
//            Toast.makeText(this, "Por favor, preencha o nome da categoria.", Toast.LENGTH_SHORT).show();
//            return; // Não prosseguir caso o nome esteja vazio
//        }

        // Aqui você coloca a lógica para criar a categoria de serviço
        ControladorBancoDeDados controlador = ControladorBancoDeDados.getInstance(getContext()); //pega a instancia definida na classe do banco
        SQLiteDatabase banco = controlador.getWritableDatabase(); //Verifica se a instancia ja foi criada, caso n, cria ela
        CategoriaRepository categoriaRepository = new CategoriaRepository(banco);
        // Exemplo: salvar em banco de dados ou em uma lista
        // Após criar, pode fornecer feedback ao usuário
//        try {
//            categoriaRepository.AdicionarCategoria(nomeCategoria);
//            Toast.makeText(getContext(), "Categoria '" + nomeCategoria + "' criada com sucesso!", Toast.LENGTH_SHORT).show();
//        } catch (IllegalArgumentException e) {
//            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//        }

        // Limpa o campo após a criação, se necessário
//        inputNomeCategoria.setText("");

    }

}
