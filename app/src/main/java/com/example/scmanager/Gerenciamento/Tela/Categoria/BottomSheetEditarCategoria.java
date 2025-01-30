package com.example.scmanager.Gerenciamento.Tela.Categoria;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.lifecycle.ViewModelProvider;

import com.example.scmanager.BancoDeDados.CategoriaRepository;
import com.example.scmanager.BancoDeDados.ClienteRepository;
import com.example.scmanager.Gerenciamento.Objetos.Categoria;
import com.example.scmanager.Gerenciamento.Objetos.Cliente;
import com.example.scmanager.Gerenciamento.ViewModel.CategoriaViewModel;
import com.example.scmanager.Gerenciamento.ViewModel.ClienteViewModel;
import com.example.scmanager.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

public class BottomSheetEditarCategoria extends BottomSheetDialogFragment implements View.OnClickListener {

    private CategoriaViewModel categoriaViewModel;
    private TextInputEditText nomeCategoriaInput;
    private Button buttonConfirmarEditar;
    private Button buttonCancelarEditar;
    private long idCategoria;
    private String nomeCategoria;
    private OnEdicaoConfirmadaListener listener;

    // Defina a interface do listener
    public interface OnEdicaoConfirmadaListener {
        void onEdicaoConfirmada();
    }

    public void setOnEdicaoConfirmadaListener(OnEdicaoConfirmadaListener listener) {
        this.listener = listener;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate o layout do Bottom Sheet

        View view = inflater.inflate(R.layout.bottom_sheet_editar_categoria, container, false);
        nomeCategoriaInput = view.findViewById(R.id.InputCategoriaNome);
        buttonConfirmarEditar = view.findViewById(R.id.buttonConfirmarEditarCategoria);
        buttonConfirmarEditar.setOnClickListener(this);

        buttonCancelarEditar = view.findViewById(R.id.buttonCancelarEdicaoCategoria);
        buttonCancelarEditar.setOnClickListener(this);

        idCategoria = getArguments().getLong("categoriaId", -1);
        nomeCategoria = getArguments().getString("categoriaNome", "");

        categoriaViewModel = new ViewModelProvider(requireActivity()).get(CategoriaViewModel.class);

        if (idCategoria != -1) {
            nomeCategoriaInput.setText(nomeCategoria);
        }

        CategoriaRepository categoriaRepository = new CategoriaRepository(requireContext());

        // Carrega os clientes do banco e exibe no log
        categoriaRepository.CarregarCategoriasAsync(categorias -> {
            for (Categoria categoria : categorias) {
                Log.d("BottomSheetEditarCliente", "Cliente: ID=" + categoria.getId() + ", Nome=" + categoria.getNome());
            }
        });

        // Inicializa o controlador do banco (Singleton)
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonConfirmarEditarCategoria) {
            //chamar funcao editar e fechar
            nomeCategoria = nomeCategoriaInput.getText().toString();

            categoriaViewModel.editarCategoria((int) idCategoria,nomeCategoria);
            dismiss();
            // Notifique o listener para fechar o BottomSheetDetalhesCliente
            if (listener != null) {
                listener.onEdicaoConfirmada();
            }

        } else if (v.getId() == R.id.buttonCancelarEdicaoCategoria){
            dismiss();
        }
    }


}
