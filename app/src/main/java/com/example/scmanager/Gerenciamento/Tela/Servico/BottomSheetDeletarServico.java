package com.example.scmanager.Gerenciamento.Tela.Servico;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.example.scmanager.Gerenciamento.Objetos.Categoria;
import com.example.scmanager.Gerenciamento.Objetos.Cliente;
import com.example.scmanager.Gerenciamento.Objetos.Servico;
import com.example.scmanager.Gerenciamento.ViewModel.CategoriaViewModel;
import com.example.scmanager.Gerenciamento.ViewModel.ClienteViewModel;
import com.example.scmanager.Gerenciamento.ViewModel.ServicoViewModel;
import com.example.scmanager.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.NumberFormat;
import java.util.Locale;

public class BottomSheetDeletarServico extends BottomSheetDialogFragment implements View.OnClickListener {

    private TextView textValorTipoServico;
    private TextView textValorNomeClienteServico;
    private TextView textValorServico;
    private TextView textValorDataAceiteServico;
    private TextView textValorEstadoServico;
    private TextView textValorDataPag;
    private Button ButtonConfirmarExclusao;
    private Button ButtonCancelarExclusao;

    private ServicoViewModel servicoViewModel;
    private long idServico;
    private String nomeCategoria;

    private Servico servicoPassado;

    // Listener para comunicar o fechamento
    private OnExclusaoConfirmadaListener listener;
    private ClienteViewModel clienteViewModel;
    private CategoriaViewModel categoriaViewModel;


    private long servicoId;
    private Integer tipoServico;
    private Integer idCliente;
    private Double valor;
    private String dataAceiteServico;
    private Integer estado;
    private String dataPagamento;

    // Defina a interface do listener
    public interface OnExclusaoConfirmadaListener {
        void onExclusaoConfirmada();
    }

    public void setOnExclusaoConfirmadaListener(OnExclusaoConfirmadaListener listener) {
        this.listener = listener;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate o layout do Bottom Sheet

        View view = inflater.inflate(R.layout.bottom_sheet_deletar_servico, container, false);

        clienteViewModel = new ViewModelProvider(requireActivity()).get(ClienteViewModel.class);
        categoriaViewModel = new ViewModelProvider(requireActivity()).get(CategoriaViewModel.class);
        servicoViewModel = new ViewModelProvider(requireActivity()).get(ServicoViewModel.class);

        textValorTipoServico = view.findViewById(R.id.textValorTipoServico);
        textValorNomeClienteServico = view.findViewById(R.id.textValorNomeClienteServico);
        textValorServico = view.findViewById(R.id.textValorServico);
        textValorDataAceiteServico = view.findViewById(R.id.textValorDataAceiteServico);
        textValorEstadoServico = view.findViewById(R.id.textValorEstadoServico);
        textValorDataPag = view.findViewById(R.id.textValorDataPag);
        TextView textLabelValorDetalhes = view.findViewById(R.id.textLabelNome5);
        TextView textLabelEstadoDetalhes = view.findViewById(R.id.textLabelNome7);
        TextView textLabelDataPagOuEstipDetalhesServ = view.findViewById(R.id.textLabelDataPagOuEstip);
        ButtonConfirmarExclusao = view.findViewById(R.id.buttonConfirmarExclusaoServico);
        ButtonConfirmarExclusao.setOnClickListener(this);

        ButtonCancelarExclusao = view.findViewById(R.id.buttonCancelarExclusaoServico);
        ButtonCancelarExclusao.setOnClickListener(this);


        servicoId = getArguments().getLong("servicoId", -1);
        tipoServico = getArguments().getInt("tipoServico", -1);
        idCliente = getArguments().getInt("idCliente", -1);
        valor = getArguments().getDouble("valor", -1);
        dataAceiteServico = getArguments().getString("dataAceiteServico", "");
        estado = getArguments().getInt("estado", -1);
        dataPagamento = getArguments().getString("dataPagamento", "");


        if(servicoId != -1)
        {
            // Busca o nome do cliente usando o idCliente
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
            textValorServico.setText(currencyFormat.format(valor));
            textValorDataAceiteServico.setText(dataAceiteServico);
            textValorEstadoServico.setText((estado == 1? "Pago" : "Não Pago"));
            if(estado == 1)
            {
                textValorEstadoServico.setTextColor(Color.parseColor("#2B8A2F"));
                textValorEstadoServico.setCompoundDrawablesWithIntrinsicBounds(R.drawable.confirmar_icon, 0, 0, 0);
                textValorEstadoServico.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#2B8A2F")));
                textLabelValorDetalhes.setTextColor(Color.parseColor("#2B8A2F"));
                textLabelEstadoDetalhes.setTextColor(Color.parseColor("#2B8A2F"));
                textValorServico.setTextColor(Color.parseColor("#2B8A2F"));
                textLabelDataPagOuEstipDetalhesServ.setText("Data de Recebimento do Pagamento*:");
            } else if (estado == 0) {
                textValorEstadoServico.setTextColor(Color.parseColor("#A6212F"));
                textValorEstadoServico.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cancelar_icon, 0, 0, 0);
                textValorEstadoServico.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#A6212F")));
                textLabelValorDetalhes.setTextColor(Color.parseColor("#A6212F"));
                textLabelEstadoDetalhes.setTextColor(Color.parseColor("#A6212F"));
                textValorServico.setTextColor(Color.parseColor("#A6212F"));
                textLabelDataPagOuEstipDetalhesServ.setText("Data Estipulada para o Pagamento*:");
            }
            textValorDataPag.setText(dataPagamento);


            String nomeCategoria = getNomeCategoriaById(tipoServico);
            textValorTipoServico.setText(nomeCategoria);

            String nomeCliente = getNomeClienteById(idCliente);
            textValorNomeClienteServico.setText(nomeCliente);
        }

        // Inicializa o controlador do banco (Singleton)
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonCancelarExclusaoServico) {
        dismiss();
        } else if (v.getId() == R.id.buttonConfirmarExclusaoServico) {
//            servicoViewModel.excluirServico((int) idCategoria);
            dismiss();
            int servicoIdInt = (int) servicoId;
            servicoViewModel.excluirServico(servicoIdInt);
            // Notifique o listener para fechar o BottomSheetDetalhesCliente
            if (listener != null) {
                listener.onExclusaoConfirmada();
            }
        }
    }

    private String getNomeCategoriaById(int idCategoria) {
        if (categoriaViewModel.getListaCategorias().getValue() != null) {
            for (Categoria categoria : categoriaViewModel.getListaCategorias().getValue()) {
                if (categoria.getId() == idCategoria) {
                    return categoria.getNome();
                }
            }
        }
        return "Categoria desconhecida"; // Caso não encontre a categoria
    }

    private String getNomeClienteById(int idCliente) {
        if (clienteViewModel.getListaClientes().getValue() != null) {
            for (Cliente cliente : clienteViewModel.getListaClientes().getValue()) {
                if (cliente.getId() == idCliente) {
                    return cliente.getNome();
                }
            }
        }
        return "Categoria desconhecida"; // Caso não encontre a categoria
    }
}
