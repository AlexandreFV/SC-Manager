package com.example.scmanager.Gerenciamento.Tela.Categoria;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.scmanager.Gerenciamento.ViewModel.CategoriaViewModel;
import com.example.scmanager.Gerenciamento.ViewModel.ClienteViewModel;
import com.example.scmanager.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDeletarCategoria extends BottomSheetDialogFragment implements View.OnClickListener {

    private TextView nomeCategoriaText;
    private Button ButtonConfirmarExclusao;
    private Button ButtonCancelarExclusao;

    private CategoriaViewModel categoriaViewModel;
    private long idCategoria;
    private String nomeCategoria;

    // Listener para comunicar o fechamento
    private OnExclusaoConfirmadaListener listener;

    // Defina a interface do listener
    public interface OnExclusaoConfirmadaListener {
        void onExclusaoConfirmada();
    }

    public void setOnExclusaoConfirmadaListener(OnExclusaoConfirmadaListener listener) {
        this.listener = listener;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate o layout do Bottom Sheet

        View view = inflater.inflate(R.layout.bottom_sheet_deletar_categoria, container, false);
        nomeCategoriaText = view.findViewById(R.id.textValorNomeCategoria);
        ButtonConfirmarExclusao = view.findViewById(R.id.buttonConfirmarExclusaoCategoria);
        ButtonConfirmarExclusao.setOnClickListener(this);

        ButtonCancelarExclusao = view.findViewById(R.id.buttonCancelarExclusaoCategoria);
        ButtonCancelarExclusao.setOnClickListener(this);
        idCategoria = getArguments().getLong("categoriaId", -1);
        nomeCategoria = getArguments().getString("categoriaNome", "");

        if (idCategoria != -1) {
            nomeCategoriaText.setText(nomeCategoria);
        }

        // Inicializa o controlador do banco (Singleton)
        return view;
    }

    public void setCategoriaViewModel(CategoriaViewModel categoriaViewModel) {
        this.categoriaViewModel = categoriaViewModel;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonCancelarExclusaoCategoria) {
        dismiss();
        } else if (v.getId() == R.id.buttonConfirmarExclusaoCategoria) {
            Log.d("CLicou", "Clicou " + (int) idCategoria);
            categoriaViewModel.excluirCategoria((int) idCategoria);
            dismiss();

            // Notifique o listener para fechar o BottomSheetDetalhesCliente
            if (listener != null) {
                listener.onExclusaoConfirmada();
            }
        }
    }
}
