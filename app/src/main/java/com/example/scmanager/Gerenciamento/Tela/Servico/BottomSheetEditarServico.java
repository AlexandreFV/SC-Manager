package com.example.scmanager.Gerenciamento.Tela.Servico;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.lifecycle.ViewModelProvider;

import com.example.scmanager.BancoDeDados.CategoriaRepository;
import com.example.scmanager.BancoDeDados.ServicoRepository;
import com.example.scmanager.Gerenciamento.Objetos.Categoria;
import com.example.scmanager.Gerenciamento.Objetos.Servico;
import com.example.scmanager.Gerenciamento.ViewModel.CategoriaViewModel;
import com.example.scmanager.Gerenciamento.ViewModel.ServicoViewModel;
import com.example.scmanager.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

public class BottomSheetEditarServico extends BottomSheetDialogFragment implements View.OnClickListener {

    private AutoCompleteTextView InputTipoServEdit;
    private AutoCompleteTextView InputServNomeClienteEdit;
    private TextInputEditText InputServValorEdit;
    private TextInputEditText InputDataAceiteServEdit;
    private AutoCompleteTextView InputServEstadoEdit;
    private TextInputEditText InputServDataPagOuEstip;

    private ServicoViewModel servicoViewModel;
    private Button buttonConfirmarEditar;
    private Button buttonCancelarEditar;
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

        View view = inflater.inflate(R.layout.bottom_sheet_editar_servico, container, false);
        InputTipoServEdit = view.findViewById(R.id.InputTipoServEdit);
        InputServNomeClienteEdit = view.findViewById(R.id.InputServNomeClienteEdit);
        InputServValorEdit = view.findViewById(R.id.InputServValorEdit);
        InputDataAceiteServEdit = view.findViewById(R.id.InputDataAceiteServEdit);
        InputServEstadoEdit = view.findViewById(R.id.InputServEstadoEdit);
        InputServDataPagOuEstip = view.findViewById(R.id.InputServDataPagOuEstip);

        buttonConfirmarEditar = view.findViewById(R.id.buttonConfirmarEditarServ);
        buttonConfirmarEditar.setOnClickListener(this);

        buttonCancelarEditar = view.findViewById(R.id.buttonCancelarEdicaoServ);
        buttonCancelarEditar.setOnClickListener(this);
        servicoViewModel = new ViewModelProvider(requireActivity()).get(ServicoViewModel.class);

        ServicoRepository servicoRepository = new ServicoRepository(requireContext());
        servicoViewModel = new ViewModelProvider(requireActivity()).get(ServicoViewModel.class);

        // Carrega os clientes do banco e exibe no log
        servicoRepository.CarregarServicosAsync(servicos -> {
            for (Servico servico : servicos) {
                Log.d("BottomSheetEditarCliente", "Cliente: ID=" + servico.getId());
            }
        });

        // Inicializa o controlador do banco (Singleton)
        return view;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonConfirmarEditarServ) {
            //chamar funcao editar e fechar

            dismiss();
            // Notifique o listener para fechar o BottomSheetDetalhesCliente
            if (listener != null) {
                listener.onEdicaoConfirmada();
            }

        } else if (v.getId() == R.id.buttonCancelarEdicaoServ){
            dismiss();
        }
    }


}
