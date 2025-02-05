package com.example.scmanager.Gerenciamento.Tela.Servico;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.scmanager.BancoDeDados.CategoriaRepository;
import com.example.scmanager.BancoDeDados.ServicoRepository;
import com.example.scmanager.Gerenciamento.Objetos.Categoria;
import com.example.scmanager.Gerenciamento.Objetos.Cliente;
import com.example.scmanager.Gerenciamento.Objetos.Servico;
import com.example.scmanager.Gerenciamento.ViewModel.CategoriaViewModel;
import com.example.scmanager.Gerenciamento.ViewModel.ClienteViewModel;
import com.example.scmanager.Gerenciamento.ViewModel.ServicoViewModel;
import com.example.scmanager.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class BottomSheetEditarServico extends BottomSheetDialogFragment implements View.OnClickListener {

    private AutoCompleteTextView InputTipoServEdit;
    private EditText InputTipoServEditId;
    private EditText InputClienteEditId;
    private AutoCompleteTextView InputServNomeClienteEdit;
    private TextInputEditText InputServValorEdit;
    private TextInputEditText InputDataAceiteServEdit;
    private AutoCompleteTextView InputServEstadoEdit;
    private TextInputEditText InputServDataPagOuEstip;

    private ServicoViewModel servicoViewModel;
    private Button buttonConfirmarEditar;
    private Button buttonCancelarEditar;
    private OnEdicaoConfirmadaListener listener;

    private long servicoId;
    private int tipoServicoId;
    private int idCliente;
    private Double valor;
    private String dataAceiteServico;
    private int estado;
    private String estadoText;
    private String dataPagamento;
    private List<Cliente> todosClientes;
    private List<Categoria> todasCategorias;
    private ClienteViewModel clienteViewModel;
    private CategoriaViewModel categoriaViewModel;

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

        InputTipoServEditId = view.findViewById(R.id.editTextIdCategoria2);
        InputClienteEditId = view.findViewById(R.id.editTextIdCliente2);


        InputServDataPagOuEstip.setOnClickListener(v -> abrirDatePicker(InputServDataPagOuEstip));
        InputDataAceiteServEdit.setOnClickListener(v -> abrirDatePicker(InputDataAceiteServEdit));


        categoriaViewModel = new ViewModelProvider(requireActivity()).get(CategoriaViewModel.class);
        clienteViewModel = new ViewModelProvider(requireActivity()).get(ClienteViewModel.class);
        servicoViewModel = new ViewModelProvider(requireActivity()).get(ServicoViewModel.class);

        buttonConfirmarEditar = view.findViewById(R.id.buttonConfirmarEditarServ);
        buttonConfirmarEditar.setOnClickListener(this);

        buttonCancelarEditar = view.findViewById(R.id.buttonCancelarEdicaoServ);
        buttonCancelarEditar.setOnClickListener(this);

        servicoId = getArguments().getLong("servicoId", -1);
        tipoServicoId = getArguments().getInt("tipoServico", -1);
        idCliente = getArguments().getInt("idCliente", -1);
        valor = getArguments().getDouble("valor", -1);
        dataAceiteServico = getArguments().getString("dataAceiteServico", "");
        estado = getArguments().getInt("estado", -1);
        dataPagamento = getArguments().getString("dataPagamento", "");

        if (servicoId != -1)
        {
            todasCategorias = categoriaViewModel.getListaCategorias().getValue();
            todosClientes = clienteViewModel.getListaClientes().getValue();
            InputServValorEdit.setText(String.valueOf(valor));
            InputDataAceiteServEdit.setText(dataAceiteServico);

            String servicoNome = getNomeCategoriaById(tipoServicoId);
            InputTipoServEdit.setText(servicoNome);
            InputTipoServEditId.setText(String.valueOf(tipoServicoId));

            String nomeCliente = getNomeClienteById(idCliente);
            InputServNomeClienteEdit.setText(nomeCliente);
            InputClienteEditId.setText(String.valueOf(idCliente));

            if(estado == 1){InputServEstadoEdit.setText("Pago");InputServDataPagOuEstip.setText(dataPagamento);InputServDataPagOuEstip.setHint("Data pagamento recebido:"); estadoText = "Pago";}
            if(estado == 0){InputServEstadoEdit.setText("Não Pago");InputServDataPagOuEstip.setText(dataPagamento);InputServDataPagOuEstip.setHint("Data pagamento estipulado:"); estadoText = "Não Pago";}
        }

        ServicoRepository servicoRepository = new ServicoRepository(requireContext());

        // Carrega os clientes do banco e exibe no log
        servicoRepository.CarregarServicosAsync(servicos -> {
            for (Servico servico : servicos) {
                Log.d("BottomSheetEditarCliente", "Cliente: ID=" + servico.getId());
            }
        });

        // Adiciona um observador no CategoriaViewModel para atualizar a lista de categorias
        categoriaViewModel.getListaCategorias().observe(getViewLifecycleOwner(), categorias -> {
            // Atualiza a lista de categorias sempre que o ViewModel for alterado
            todasCategorias = categorias;
            Log.d("Categorias", "Categorias atualizadas: " + todasCategorias.toString());
        });

        // Adiciona um observador no clienteViewModel para atualizar a lista de categorias
        clienteViewModel.getListaClientes().observe(getViewLifecycleOwner(), clientes -> {
            // Atualiza a lista de categorias sempre que o ViewModel for alterado
            todosClientes = clientes;
            Log.d("Clientes", "Clientes atualizadas: " + todosClientes.toString());
        });

        definirValoresSpinnersServico(view);

        // Inicializa o controlador do banco (Singleton)
        return view;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonConfirmarEditarServ) {
            //chamar funcao editar e fechar
            editarServico();
            // Notifique o listener para fechar o BottomSheetDetalhesCliente
            if (listener != null) {
                listener.onEdicaoConfirmada();
            }
            dismiss();
        } else if (v.getId() == R.id.buttonCancelarEdicaoServ){
            dismiss();
        }
    }

    private void definirValoresSpinnersServico(View view) {
        atribuirValorSpinnerEstado(view);
        atribuirValorSpinnerNomeCategoria(view);
        atribuirValorSpinnerNomeCliente(view);
    }

    private void atribuirValorSpinnerEstado(View view)
    {
        List<String> valoresEstadoSpinner = Arrays.asList("Pago", "Não Pago");

        // Criando o adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                valoresEstadoSpinner
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Obtendo o AutoCompleteTextView
        AutoCompleteTextView inputEstado = view.findViewById(R.id.InputServEstadoEdit);
        inputEstado.setAdapter(adapter);

        // Abrindo o dropdown quando o campo é tocado
        inputEstado.setOnTouchListener((v, event) -> {
            inputEstado.showDropDown();  // Abre o dropdown ao tocar no campo
            return false;  // Permite que o evento continue a ser processado
        });

        inputEstado.setOnItemClickListener((parent, view1, position, id) -> {
            String estadoSelecionado = (String) parent.getItemAtPosition(position);

            // Agora você pode realizar ações com base no valor selecionado
            if (estadoSelecionado.equals("Pago")) {
                // Ação para "Pago"
                TextInputLayout menuDataPagamentoRecebidoOuAReceberServ = view.findViewById(R.id.textInputLayout455);
                menuDataPagamentoRecebidoOuAReceberServ.setHint("Data de Recebimento do Pagamento*:");
                menuDataPagamentoRecebidoOuAReceberServ.setVisibility(View.VISIBLE);
                // Exemplo: Realize alguma ação aqui, como mudar a cor de um texto ou habilitar/desabilitar campos
            } else if (estadoSelecionado.equals("Não Pago")) {
                // Ação para "Não Pago"
                TextInputLayout menuDataPagamentoRecebidoOuAReceberServ = view.findViewById(R.id.textInputLayout455);
                menuDataPagamentoRecebidoOuAReceberServ.setHint("Data Estipulada para o Pagamento*:");
                menuDataPagamentoRecebidoOuAReceberServ.setVisibility(View.VISIBLE);
            } else{
                TextInputLayout menuDataPagamentoRecebidoOuAReceberServ = view.findViewById(R.id.textInputLayout455);
                menuDataPagamentoRecebidoOuAReceberServ.setHint("");
                menuDataPagamentoRecebidoOuAReceberServ.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void atribuirValorSpinnerNomeCategoria(View view)
    {
        if (todasCategorias == null || todasCategorias.isEmpty())
        {
            AutoCompleteTextView inputTipo = view.findViewById(R.id.InputTipoServEdit);
            inputTipo.setText("Não há categorias adicionadas!");
        }
        else
        {
            AutoCompleteTextView inputTipo = view.findViewById(R.id.InputTipoServEdit);

            ArrayAdapter<Categoria> adapterTipoServico = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    todasCategorias // Criando um array com uma única string
            );

            adapterTipoServico.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Obtendo o AutoCompleteTextView
            inputTipo.setAdapter(adapterTipoServico);

            // Abrindo o dropdown quando o campo é tocado
            inputTipo.setOnTouchListener((v, event) -> {
                inputTipo.showDropDown();  // Abre o dropdown ao tocar no campo
                return false;  // Permite que o evento continue a ser processado
            });

            // Captura do item selecionado
            inputTipo.setOnItemClickListener((parent, view1, position, id) -> {
                Categoria categoriaSelecionada = (Categoria) parent.getItemAtPosition(position); // Obtém o objeto completo
                Integer idCategoria = categoriaSelecionada.getId(); // Obtém o ID da categoria

                // Aqui você pode usar o ID da categoria conforme necessário
                Log.d("ID Categoria", "ID da categoria selecionada: " + idCategoria);

                // Caso queira passar o ID para um EditText, por exemplo:
                InputTipoServEditId = view.findViewById(R.id.editTextIdCategoria2);
                InputTipoServEditId.setText(String.valueOf(idCategoria)); // Define o ID no EditText
            });
        }
    }

    private void atribuirValorSpinnerNomeCliente(View view)
    {
        if (todosClientes == null || todosClientes.isEmpty())
        {
            AutoCompleteTextView inputNomeCliente = view.findViewById(R.id.InputServNomeClienteEdit);
            inputNomeCliente.setText("Não há clientes adicionados!");
        }
        else
        {
            AutoCompleteTextView inputNomeCliente = view.findViewById(R.id.InputServNomeClienteEdit);

            ArrayAdapter<Cliente> adapterTipoServico = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    todosClientes // Criando um array com uma única string
            );

            adapterTipoServico.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Obtendo o AutoCompleteTextView
            inputNomeCliente.setAdapter(adapterTipoServico);

            // Abrindo o dropdown quando o campo é tocado
            inputNomeCliente.setOnTouchListener((v, event) -> {
                inputNomeCliente.showDropDown();  // Abre o dropdown ao tocar no campo
                return false;  // Permite que o evento continue a ser processado
            });

            // Captura do item selecionado
            inputNomeCliente.setOnItemClickListener((parent, view1, position, id) -> {
                Cliente clienteSelecionado = (Cliente) parent.getItemAtPosition(position); // Obtém o objeto completo
                Long idCliente = clienteSelecionado.getId(); // Obtém o ID da categoria

                // Aqui você pode usar o ID da categoria conforme necessário
                Log.d("ID Categoria", "ID da categoria selecionada: " + idCliente);

                // Caso queira passar o ID para um EditText, por exemplo:
                EditText editTextIdCliente = view.findViewById(R.id.editTextIdCliente2);
                editTextIdCliente.setText(String.valueOf(idCliente)); // Define o ID no EditText
            });

        }
    }

    private void abrirDatePicker(EditText editText) {
        // Obtém a data atual
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Cria o DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // Formata a data selecionada
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year1, monthOfYear, dayOfMonth);
                    // Define a data no EditText
                    editText.setText(sdf.format(selectedDate.getTime()));
                }, year, month, day);

        // Exibe o DatePickerDialog
        datePickerDialog.show();
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

    private void editarServico()
    {
        String categoriaId = InputTipoServEditId.getText().toString();
        String clienteId = InputClienteEditId.getText().toString();
        String valor = InputServValorEdit.getText().toString();
        String dataAceitePag = InputDataAceiteServEdit.getText().toString();
        String estado = InputServEstadoEdit.getText().toString();
        String dataPagOuEstipulada = InputServDataPagOuEstip.getText().toString();

        Integer categoriaIdValue = Integer.parseInt(categoriaId);
        Integer clienteIdValue = Integer.parseInt(clienteId);
        Float valorValue = Float.parseFloat(valor);
        Integer servicoIdValue = Math.toIntExact(servicoId);

        if(servicoIdValue == 0 || categoriaIdValue == 0 || clienteIdValue == 0 || valorValue == 0 || clienteId.equals("") || dataAceitePag.equals("") || estado.equals("") || dataPagOuEstipulada.equals(""))
        {
            int duration = Toast.LENGTH_SHORT;
            Toast.makeText(getContext(), "Preencha todos os campos!", duration).show();
            return;
        }

        servicoViewModel.editarServico(servicoIdValue, clienteIdValue, categoriaIdValue, valorValue, dataAceitePag, estado, dataPagOuEstipulada);
    }
}

