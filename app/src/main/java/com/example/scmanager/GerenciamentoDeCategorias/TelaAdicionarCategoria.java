package com.example.scmanager.GerenciamentoDeCategorias;

import android.app.AlertDialog;
import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.DialogCompat;
import androidx.fragment.app.DialogFragment;

import com.example.scmanager.BancoDeDados.CategoriaRepository;
import com.example.scmanager.BancoDeDados.ControladorBancoDeDados;
import com.example.scmanager.R;

public class TelaAdicionarCategoria extends DialogFragment {


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Crie o layout do modal
        //View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_adicionar_item, null);

        // Inicializa o controlador do banco (Singleton)

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Adicionar Item")
                .setMessage("Preencha as informações para adicionar um item")
                //.setView(R.layout.dialog_adicionar_item)  // Layout do modal
                .setPositiveButton("Adicionar", (dialog, id) -> {
                    // Lógica para adicionar item
                })
                .setNegativeButton("Cancelar", (dialog, id) -> {
                    // Lógica para cancelar
                });
        return builder.create();
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
