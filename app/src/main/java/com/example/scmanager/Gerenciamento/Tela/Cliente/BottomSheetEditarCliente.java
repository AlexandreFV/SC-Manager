package com.example.scmanager.Gerenciamento.Tela.Cliente;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.scmanager.BancoDeDados.ClienteRepository;
import com.example.scmanager.Gerenciamento.Objetos.Cliente;
import com.example.scmanager.Gerenciamento.ViewModel.ClienteViewModel;
import com.example.scmanager.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

public class BottomSheetEditarCliente extends BottomSheetDialogFragment implements View.OnClickListener {

    private ClienteViewModel clienteViewModel;
    private TextInputEditText nomeClienteInput;
    private TextInputEditText telefoneClienteInput;
    private Button buttonConfirmarEditar;
    private Button buttonCancelarEditar;
    private long idCliente;
    private String nomeCliente;
    private String telefoneCliente;

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

        View view = inflater.inflate(R.layout.bottom_sheet_editar_cliente, container, false);
        nomeClienteInput = view.findViewById(R.id.inputNomeCliente);
        telefoneClienteInput = view.findViewById(R.id.inputTelefone);
        buttonConfirmarEditar = view.findViewById(R.id.buttonEditarCliente);
        buttonConfirmarEditar.setOnClickListener(this);

        buttonCancelarEditar = view.findViewById(R.id.buttonCancelarEdicaoCliente);
        buttonCancelarEditar.setOnClickListener(this);

        idCliente = getArguments().getLong("clienteId", -1);
        nomeCliente = getArguments().getString("clienteNome", "");
        telefoneCliente = getArguments().getString("clienteTelefone", "");

        if (idCliente != -1) {
            nomeClienteInput.setText(nomeCliente);
            telefoneClienteInput.setText(telefoneCliente);
        }

        ClienteRepository clienteRepository = new ClienteRepository(requireContext());

        // Carrega os clientes do banco e exibe no log
        clienteRepository.carregarClientesDoBancoAsync(clientes -> {
            for (Cliente cliente : clientes) {
                Log.d("BottomSheetEditarCliente", "Cliente: ID=" + cliente.getId() + ", Nome=" + cliente.getNome() + ", Telefone=" + cliente.getTelefone());
            }
        });

        // Inicializa o controlador do banco (Singleton)
        return view;
    }

    public void setClienteViewModel(ClienteViewModel clienteViewModel) {
        this.clienteViewModel = clienteViewModel;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonEditarCliente) {
            //chamar funcao editar e fechar
            nomeCliente = nomeClienteInput.getText().toString();
            telefoneCliente = telefoneClienteInput.getText().toString();

            clienteViewModel.editarCliente((int) idCliente,nomeCliente, telefoneCliente);
            dismiss();
            // Notifique o listener para fechar o BottomSheetDetalhesCliente
            if (listener != null) {
                listener.onEdicaoConfirmada();
            }

        } else if (v.getId() == R.id.buttonCancelarEdicaoCliente){
            dismiss();
        }
    }


}
