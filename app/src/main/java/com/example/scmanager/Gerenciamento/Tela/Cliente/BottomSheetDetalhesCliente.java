package com.example.scmanager.Gerenciamento.Tela.Cliente;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.example.scmanager.Gerenciamento.ViewModel.ClienteViewModel;
import com.example.scmanager.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDetalhesCliente extends BottomSheetDialogFragment implements View.OnClickListener,BottomSheetDeletarCliente.OnExclusaoConfirmadaListener, BottomSheetEditarCliente.OnEdicaoConfirmadaListener {

    private ClienteViewModel clienteViewModel;
    private TextView nomeClienteText;
    private TextView telefoneClienteText;
    private Button buttonEditar;
    private Button buttonExcluir;
    private long idCliente;
    private String nomeCliente;
    private String telefoneCliente;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate o layout do Bottom Sheet

        View view = inflater.inflate(R.layout.bottom_sheet_detalhes_cliente, container, false);
        nomeClienteText = view.findViewById(R.id.textValorNomeCliente);
        telefoneClienteText = view.findViewById(R.id.textValorTelefoneCliente);
        buttonEditar = view.findViewById(R.id.buttonEditarCliente);
        buttonEditar.setOnClickListener(this);

        buttonExcluir = view.findViewById(R.id.buttonExcluirCliente);
        buttonExcluir.setOnClickListener(this);

         idCliente = getArguments().getLong("clienteId", -1);
         nomeCliente = getArguments().getString("clienteNome", "");
         telefoneCliente = getArguments().getString("clienteTelefone", "");

        if (idCliente != -1) {
            nomeClienteText.setText(nomeCliente);
            telefoneClienteText.setText(telefoneCliente);
        }
        clienteViewModel = new ViewModelProvider(requireActivity()).get(ClienteViewModel.class);

        // Inicializa o controlador do banco (Singleton)
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonExcluirCliente) {
            if (getParentFragmentManager().findFragmentByTag("BottomSheetDialogFragment") == null)
            {
            // Cria o Bundle com os dados do cliente
            Bundle bundle = new Bundle();
            bundle.putLong("clienteId", idCliente);
            bundle.putString("clienteNome", nomeCliente);
            bundle.putString("clienteTelefone", telefoneCliente);

            // Cria o BottomSheetDeletarCliente e passa os dados
            BottomSheetDeletarCliente fragment = new BottomSheetDeletarCliente();
            fragment.setArguments(bundle); // Passa os dados para o fragment
            fragment.setOnExclusaoConfirmadaListener(this); // Defina o listener
            // Exibe o BottomSheetDeletarCliente
            fragment.show(getParentFragmentManager(), "BottomSheetDialogFragment");
            }
        } else if (v.getId() == R.id.buttonEditarCliente) {
            if (getParentFragmentManager().findFragmentByTag("BottomSheetDialogFragment") == null)
            {
            // Cria o Bundle com os dados do cliente
            Bundle bundle = new Bundle();
            bundle.putLong("clienteId", idCliente);
            bundle.putString("clienteNome", nomeCliente);
            bundle.putString("clienteTelefone", telefoneCliente);

            // Cria o BottomSheetDeletarCliente e passa os dados
            BottomSheetEditarCliente fragment = new BottomSheetEditarCliente();
            fragment.setArguments(bundle); // Passa os dados para o fragment
            fragment.setOnEdicaoConfirmadaListener(this); // Defina o listener
            // Exibe o BottomSheetDeletarCliente
            fragment.show(getParentFragmentManager(), "BottomSheetDialogFragment");
            }
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
