package com.example.scmanager.Gerenciamento.Tela.Servico;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
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

public class BottomSheetDetalhesServico extends BottomSheetDialogFragment implements View.OnClickListener, BottomSheetDeletarServico.OnExclusaoConfirmadaListener, BottomSheetEditarServico.OnEdicaoConfirmadaListener {

    private ServicoViewModel servicoViewModel;
    private ClienteViewModel clienteViewModel;
    private TextView textValorTipoServico;
    private TextView textValorNomeClienteServico;
    private TextView textValorServico;
    private TextView textValorDataAceiteServico;
    private TextView textValorEstadoServico;
    private TextView textValorDataPag;

    private Button buttonEditar;
    private Button buttonExcluir;

    private long servicoId;
    private Integer tipoServico;
    private Integer idCliente;
    private Double valor;
    private String dataAceiteServico;
    private Integer estado;
    private String dataPagamento;
    private CategoriaViewModel categoriaViewModel;
    private TextView textLabelValorDetalhes;
    private TextView textLabelEstadoDetalhes;
    private TextView textLabelDataPagOuEstipDetalhesServ;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate o layout do Bottom Sheet

        View view = inflater.inflate(R.layout.bottom_sheet_detalhes_servico, container, false);

        // Inicializando o categoriaViewModel
        categoriaViewModel = new ViewModelProvider(requireActivity()).get(CategoriaViewModel.class);
        clienteViewModel = new ViewModelProvider(requireActivity()).get(ClienteViewModel.class);
        servicoViewModel = new ViewModelProvider(requireActivity()).get(ServicoViewModel.class);

        textValorTipoServico = view.findViewById(R.id.textValorTipoServicoDetalhes);
        textValorNomeClienteServico = view.findViewById(R.id.textValorNomeClienteServDetalhes);
        textValorServico = view.findViewById(R.id.textValorServicoDetalhes);
        textValorDataAceiteServico = view.findViewById(R.id.textValorDataAceiteServDetalhes);
        textValorEstadoServico = view.findViewById(R.id.textValorEstadoServicoDetalhes);
        textValorDataPag = view.findViewById(R.id.textValorDataPagDetalhesServ);
        textLabelValorDetalhes = view.findViewById(R.id.textLabelValorDetalhes);
        textLabelEstadoDetalhes = view.findViewById(R.id.textLabelEstadoDetalhes);
        textLabelDataPagOuEstipDetalhesServ = view.findViewById(R.id.textLabelDataPagOuEstipDetalhesServ);
        buttonEditar = view.findViewById(R.id.buttonEditarServico);
        buttonEditar.setOnClickListener(this);

        buttonExcluir = view.findViewById(R.id.buttonExcluirServico);
        buttonExcluir.setOnClickListener(this);

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
        if (v.getId() == R.id.buttonExcluirServico) {
            if (getParentFragmentManager().findFragmentByTag("BottomSheetDialogFragment") == null)
            {
            // Cria o Bundle com os dados do cliente
            Bundle bundle = new Bundle();
            bundle.putLong("servicoId", servicoId);
            bundle.putInt("tipoServico", tipoServico);
            bundle.putInt("idCliente", idCliente);
            bundle.putDouble("valor", valor);
            bundle.putString("dataAceiteServico", dataAceiteServico);
            bundle.putInt("estado", estado);
            bundle.putString("dataPagamento", dataPagamento);
            // Cria o BottomSheetDeletarCliente e passa os dados
            BottomSheetDeletarServico fragment = new BottomSheetDeletarServico();
            fragment.setArguments(bundle); // Passa os dados para o fragment
            fragment.setOnExclusaoConfirmadaListener(this); // Defina o listener
            // Exibe o BottomSheetDeletarCliente
            fragment.show(getParentFragmentManager(), "BottomSheetDialogFragment");
            }
        } else if (v.getId() == R.id.buttonEditarServico) {
            if (getParentFragmentManager().findFragmentByTag("BottomSheetDialogFragment") == null)
            {
            // Cria o Bundle com os dados do cliente
            Bundle bundle = new Bundle();
            bundle.putLong("servicoId", servicoId);
            bundle.putInt("tipoServico", tipoServico);
            bundle.putInt("idCliente", idCliente);
            bundle.putDouble("valor", valor);
            bundle.putString("dataAceiteServico", dataAceiteServico);
            bundle.putInt("estado", estado);
            bundle.putString("dataPagamento", dataPagamento);

            // Cria o BottomSheetDeletarCliente e passa os dados
            BottomSheetEditarServico fragment = new BottomSheetEditarServico();
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
