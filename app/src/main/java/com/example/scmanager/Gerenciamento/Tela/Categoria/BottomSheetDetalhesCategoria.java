package com.example.scmanager.Gerenciamento.Tela.Categoria;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.scmanager.Gerenciamento.ViewModel.CategoriaViewModel;
import com.example.scmanager.Gerenciamento.ViewModel.ClienteViewModel;
import com.example.scmanager.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDetalhesCategoria extends BottomSheetDialogFragment implements View.OnClickListener, BottomSheetDeletarCategoria.OnExclusaoConfirmadaListener, BottomSheetEditarCategoria.OnEdicaoConfirmadaListener {

    private CategoriaViewModel categoriaViewModel;
    private TextView nomeClienteText;
    private Button buttonEditar;
    private Button buttonExcluir;
    private long idCategoria;
    private String nomeCategoria;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate o layout do Bottom Sheet

        View view = inflater.inflate(R.layout.bottom_sheet_detalhes_categoria, container, false);
        nomeClienteText = view.findViewById(R.id.textValorNomeCategoriaDetalhes);
        buttonEditar = view.findViewById(R.id.buttonEditarCategoria);
        buttonEditar.setOnClickListener(this);

        buttonExcluir = view.findViewById(R.id.buttonExcluirCategoria);
        buttonExcluir.setOnClickListener(this);

         idCategoria = getArguments().getLong("categoriaId", -1);
         nomeCategoria = getArguments().getString("categoriaNome", "");

        if (idCategoria != -1) {
            nomeClienteText.setText(nomeCategoria);
        }

        // Inicializa o controlador do banco (Singleton)
        return view;
    }

    public void setCategoriaViewModel(CategoriaViewModel categoriaViewModel) {
        this.categoriaViewModel = categoriaViewModel;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonExcluirCategoria) {
            // Cria o Bundle com os dados do cliente
            Bundle bundle = new Bundle();
            bundle.putLong("categoriaId", idCategoria);
            bundle.putString("categoriaNome", nomeCategoria);

            // Cria o BottomSheetDeletarCliente e passa os dados
            BottomSheetDeletarCategoria fragment = new BottomSheetDeletarCategoria();
            fragment.setArguments(bundle); // Passa os dados para o fragment
            fragment.setCategoriaViewModel(categoriaViewModel);
            fragment.setOnExclusaoConfirmadaListener(this); // Defina o listener
            // Exibe o BottomSheetDeletarCliente
            fragment.show(getParentFragmentManager(), fragment.getTag());
        } else if (v.getId() == R.id.buttonEditarCategoria){
            // Cria o Bundle com os dados do cliente
            Bundle bundle = new Bundle();
            bundle.putLong("categoriaId", idCategoria);
            bundle.putString("categoriaNome", nomeCategoria);

            // Cria o BottomSheetDeletarCliente e passa os dados
            BottomSheetEditarCategoria fragment = new BottomSheetEditarCategoria();
            fragment.setArguments(bundle); // Passa os dados para o fragment
            fragment.setCategoriaViewModel(categoriaViewModel);
            fragment.setOnEdicaoConfirmadaListener(this); // Defina o listener
            // Exibe o BottomSheetDeletarCliente
            fragment.show(getParentFragmentManager(), fragment.getTag());
        }
    }

    @Override
    public void onExclusaoConfirmada() {
        // Fecha o BottomSheetDetalhesCliente
        dismiss();
    }

    @Override
    public void onEdicaoConfirmada() {
        // Fecha o BottomSheetDetalhesCliente
        dismiss();
    }
}
