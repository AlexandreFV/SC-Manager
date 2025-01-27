package com.example.scmanager.Gerenciamento.Tela.Cliente;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.scmanager.Gerenciamento.ViewModel.ClienteViewModel;
import com.example.scmanager.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDeletarCliente extends BottomSheetDialogFragment implements View.OnClickListener {

    private TextView nomeClienteText;
    private TextView telefoneClienteText;

    private Button ButtonConfirmarExclusao;
    private Button ButtonCancelarExclusao;

    private ClienteViewModel clienteViewModel;
    private long idCliente;
    private String nomeCliente;
    private String telefoneCliente;

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

        View view = inflater.inflate(R.layout.bottom_sheet_deletar_cliente, container, false);
        nomeClienteText = view.findViewById(R.id.textValorNomeCliente);
        telefoneClienteText = view.findViewById(R.id.textValorTelefoneCliente);
        ButtonConfirmarExclusao = view.findViewById(R.id.buttonConfirmarExclusao);
        ButtonConfirmarExclusao.setOnClickListener(this);

        ButtonCancelarExclusao = view.findViewById(R.id.buttonCancelarExclusao);
        ButtonCancelarExclusao.setOnClickListener(this);
        idCliente = getArguments().getLong("clienteId", -1);
        nomeCliente = getArguments().getString("clienteNome", "");
        telefoneCliente = getArguments().getString("clienteTelefone", "");

        if (idCliente != -1) {
            nomeClienteText.setText(nomeCliente);
            telefoneClienteText.setText(telefoneCliente);
        }

        // Inicializa o controlador do banco (Singleton)
        return view;
    }

    public void setClienteViewModel(ClienteViewModel clienteViewModel) {
        this.clienteViewModel = clienteViewModel;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonCancelarExclusao) {
        dismiss();
        } else if (v.getId() == R.id.buttonConfirmarExclusao) {
            Log.d("CLicou", "Clicou " + (int) idCliente);
            clienteViewModel.excluirCliente((int) idCliente);
            dismiss();

            // Notifique o listener para fechar o BottomSheetDetalhesCliente
            if (listener != null) {
                listener.onExclusaoConfirmada();
            }
        }
    }
}
