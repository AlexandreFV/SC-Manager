    package com.example.scmanager;

    import android.animation.ValueAnimator;
    import android.app.DatePickerDialog;
    import android.graphics.drawable.Drawable;
    import android.graphics.drawable.LayerDrawable;
    import android.os.Bundle;
    import android.text.Editable;
    import android.text.TextWatcher;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ArrayAdapter;
    import android.widget.AutoCompleteTextView;
    import android.widget.Button;
    import android.widget.DatePicker;
    import android.widget.EditText;
    import android.widget.Toast;

    import androidx.constraintlayout.widget.ConstraintLayout;
    import androidx.lifecycle.ViewModelProvider;

    import com.example.scmanager.Gerenciamento.Objetos.Categoria;
    import com.example.scmanager.Gerenciamento.Objetos.Cliente;
    import com.example.scmanager.Gerenciamento.ViewModel.CategoriaViewModel;
    import com.example.scmanager.Gerenciamento.ViewModel.ClienteViewModel;
    import com.example.scmanager.Gerenciamento.ViewModel.ServicoViewModel;
    import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
    import com.google.android.material.textfield.MaterialAutoCompleteTextView;
    import com.google.android.material.textfield.TextInputEditText;
    import com.google.android.material.textfield.TextInputLayout;


    import java.text.SimpleDateFormat;
    import java.util.Arrays;
    import java.util.Calendar;
    import java.util.Date;
    import java.util.List;

    public class TelaAdicionarCategoriaClienteServico extends BottomSheetDialogFragment implements View.OnClickListener {

        private ConstraintLayout AddCategoria;
        private ConstraintLayout AddCliente;
        private ConstraintLayout AddServico;
        private Button CategoriaAdd;
        private Button ClienteAdd;
        private Button ServicoAdd;
        private Button buttonSalvarCliente;
        private Button buttonSalvarCategoria;
        private Button buttonSalvarServico;
        private ClienteViewModel clienteViewModel;
        private List<Cliente> todosClientes;
        private CategoriaViewModel categoriaViewModel;
        private List<Categoria> todasCategorias;
        private ServicoViewModel servicoViewModel;

        private String qualLayoutAddMostrar;

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate o layout do Bottom Sheet
            View view = inflater.inflate(R.layout.dialog_adicionar_registros, container, false);

            qualLayoutAddMostrar = getArguments().getString("veioDe", "");

            AddCategoria = view.findViewById(R.id.FundoLayoutAddCategoria);
            AddCliente = view.findViewById(R.id.FundoLayoutAddCliente);
            AddServico = view.findViewById(R.id.FundoLayoutAddServico);

            // Configura o onClickListener para os botões
            CategoriaAdd = view.findViewById(R.id.CategoriaButtonAdd);
            CategoriaAdd.setOnClickListener(this);
            ClienteAdd = view.findViewById(R.id.btnAddCliente);
            ClienteAdd.setOnClickListener(this);
            ServicoAdd = view.findViewById(R.id.btnAddServico);
            ServicoAdd.setOnClickListener(this);

            buttonSalvarCategoria = view.findViewById(R.id.buttonSalvarCategoria);
            buttonSalvarCategoria.setOnClickListener(this);
            buttonSalvarCliente = view.findViewById(R.id.buttonSalvarCliente);
            buttonSalvarCliente.setOnClickListener(this);
            buttonSalvarServico = view.findViewById(R.id.buttonSalvarServico);
            buttonSalvarServico.setOnClickListener(this);

            clienteViewModel = new ViewModelProvider(requireActivity()).get(ClienteViewModel.class);
            categoriaViewModel = new ViewModelProvider(requireActivity()).get(CategoriaViewModel.class);
            servicoViewModel = new ViewModelProvider(requireActivity()).get(ServicoViewModel.class);

            exibirLayout(qualLayoutAddMostrar);
            if(AddServico.getVisibility() == View.VISIBLE)
            {
                todasCategorias = categoriaViewModel.getListaCategorias().getValue();
                todosClientes = clienteViewModel.getListaClientes().getValue();
                definirValoresSpinnersServico(view);
            }

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

                // Inicializa o controlador do banco (Singleton)
            return view;
        }

        private void exibirLayout(String layoutAExibir)
        {
            if(layoutAExibir.equals("categoria"))
            {
                AddCategoria.setVisibility(View.VISIBLE);
                AddCliente.setVisibility(View.GONE);
                AddServico.setVisibility(View.GONE);
            } else if(layoutAExibir.equals("cliente")){
                AddCliente.setVisibility(View.VISIBLE);
                AddCategoria.setVisibility(View.GONE);
                AddServico.setVisibility(View.GONE);
            }else if(layoutAExibir.equals("servico")){
                AddServico.setVisibility(View.VISIBLE);
                AddCategoria.setVisibility(View.GONE);
                AddCliente.setVisibility(View.GONE);
            }
            buttonSelecionado(layoutAExibir);

        }

        private void buttonSelecionado(String layoutAExibir1) {
            if (layoutAExibir1.equals("categoria")) {
                alterarBackgroundGradualmente(CategoriaAdd); // Troca o background do botão de Categoria
                alterarBackgroundGradualmenteParaSemGradiente(ClienteAdd);
                alterarBackgroundGradualmenteParaSemGradiente(ServicoAdd);
            } else if (layoutAExibir1.equals("cliente")) {
                alterarBackgroundGradualmente(ClienteAdd); // Troca o background do botão de Cliente
                alterarBackgroundGradualmenteParaSemGradiente(CategoriaAdd);
                alterarBackgroundGradualmenteParaSemGradiente(ServicoAdd);
            } else if (layoutAExibir1.equals("servico")) {
                alterarBackgroundGradualmente(ServicoAdd); // Troca o background do botão de Serviço
                alterarBackgroundGradualmenteParaSemGradiente(CategoriaAdd);
                alterarBackgroundGradualmenteParaSemGradiente(ClienteAdd);
            }
        }

        private void definirValoresSpinnersServico(View view) {

            atribuirValorSpinnerEstado(view);
            atribuirValorSpinnerNomeCategoria(view);
            atribuirValorSpinnerNomeCliente(view);
            atribuirDataAtual(view);
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
            AutoCompleteTextView inputEstado = view.findViewById(R.id.InputEstadoServ);
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
                    TextInputLayout menuDataPagamentoRecebidoOuAReceberServ = view.findViewById(R.id.menuDataPagamentoRecebidoOuAReceberServ);
                    menuDataPagamentoRecebidoOuAReceberServ.setHint("Data de Recebimento do Pagamento*:");
                    menuDataPagamentoRecebidoOuAReceberServ.setVisibility(View.VISIBLE);
                // Exemplo: Realize alguma ação aqui, como mudar a cor de um texto ou habilitar/desabilitar campos
                } else if (estadoSelecionado.equals("Não Pago")) {
                    // Ação para "Não Pago"
                    TextInputLayout menuDataPagamentoRecebidoOuAReceberServ = view.findViewById(R.id.menuDataPagamentoRecebidoOuAReceberServ);
                    menuDataPagamentoRecebidoOuAReceberServ.setHint("Data Estipulada para o Pagamento*:");
                    menuDataPagamentoRecebidoOuAReceberServ.setVisibility(View.VISIBLE);
                } else{
                    TextInputLayout menuDataPagamentoRecebidoOuAReceberServ = view.findViewById(R.id.menuDataPagamentoRecebidoOuAReceberServ);
                    menuDataPagamentoRecebidoOuAReceberServ.setHint("");
                    menuDataPagamentoRecebidoOuAReceberServ.setVisibility(View.INVISIBLE);
                }
            });

        }

        private void atribuirValorSpinnerNomeCategoria(View view)
        {
            if (todasCategorias == null || todasCategorias.isEmpty())
            {
                AutoCompleteTextView inputTipo = view.findViewById(R.id.InputTipoServico);
                inputTipo.setText("Não há categorias adicionadas!");
            }
            else
            {
                AutoCompleteTextView inputTipo = view.findViewById(R.id.InputTipoServico);
                inputTipo.setText("Selecione uma categoria!");

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
                    EditText editTextIdCategoria = view.findViewById(R.id.editTextIdCategoria);
                    editTextIdCategoria.setText(String.valueOf(idCategoria)); // Define o ID no EditText
                });
            }
        }

        private void atribuirValorSpinnerNomeCliente(View view)
        {
            if (todosClientes == null || todosClientes.isEmpty())
            {
                AutoCompleteTextView inputNomeCliente = view.findViewById(R.id.InputNomeClienteServ);
                inputNomeCliente.setText("Não há clientes adicionados!");
            }
            else
            {
                AutoCompleteTextView inputNomeCliente = view.findViewById(R.id.InputNomeClienteServ);
                inputNomeCliente.setText("Selecione um cliente!");

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
                    EditText editTextIdCliente = view.findViewById(R.id.editTextIdCliente);
                    editTextIdCliente.setText(String.valueOf(idCliente)); // Define o ID no EditText
                });

            }
        }

        private void atribuirDataAtual(View view) {
            EditText inputDataAceite = view.findViewById(R.id.InputDataAceiteServ);
            EditText InputPagamentoServ = view.findViewById(R.id.InputPagamentoServ);

            // Atribui a data atual
            setDataAtual(inputDataAceite);
            setDataAtual(InputPagamentoServ);

            // Cria listeners para os campos de data
            inputDataAceite.setOnClickListener(v -> abrirDatePicker(inputDataAceite));
            InputPagamentoServ.setOnClickListener(v -> abrirDatePicker(InputPagamentoServ));
        }

        private void setDataAtual(EditText editText) {
            Date dataAtual = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dataFormatada = sdf.format(dataAtual);
            editText.setText(dataFormatada);
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

        private void alterarBackgroundGradualmente(Button button) {
            // Obtém o novo Drawable
            final Drawable newDrawable = getResources().getDrawable(R.drawable.button_add_layout_gradiente, null);

            // Verifica se o Drawable é um LayerDrawable
            if (newDrawable instanceof LayerDrawable) {
                final LayerDrawable layerDrawable = (LayerDrawable) newDrawable;

                // Cria o ValueAnimator para animar a transição
                ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
                animator.setDuration(500); // Duração de 500ms para a animação (ajuste conforme necessário)
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float progress = (float) animation.getAnimatedValue();

                        // Anima a alteração de opacidade (se necessário) ou outras propriedades
                        layerDrawable.setAlpha((int) (progress * 255)); // Ajuste da opacidade conforme a animação

                        // Aplica o novo Drawable progressivamente
                        button.setBackground(layerDrawable);
                    }
                });

                animator.start(); // Inicia a animação
            } else {
                // Caso o Drawable não seja do tipo LayerDrawable, aplica diretamente
                button.setBackground(newDrawable);
            }
        }

        private void alterarBackgroundGradualmenteParaSemGradiente(Button button) {
            // Obtém o novo Drawable
            final Drawable newDrawable = getResources().getDrawable(R.drawable.button_add_layout_no_gradiente, null);

            // Verifica se o Drawable é um LayerDrawable
            if (newDrawable instanceof LayerDrawable) {
                final LayerDrawable layerDrawable = (LayerDrawable) newDrawable;

                // Cria o ValueAnimator para animar a transição
                ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
                animator.setDuration(500); // Duração de 500ms para a animação (ajuste conforme necessário)
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float progress = (float) animation.getAnimatedValue();

                        // Anima a alteração de opacidade (se necessário) ou outras propriedades
                        layerDrawable.setAlpha((int) (progress * 255)); // Ajuste da opacidade conforme a animação

                        // Aplica o novo Drawable progressivamente
                        button.setBackground(layerDrawable);
                    }
                });

                animator.start(); // Inicia a animação
            } else {
                // Caso o Drawable não seja do tipo LayerDrawable, aplica diretamente
                button.setBackground(newDrawable);
            }
        }

        private void criarCliente(View view) {
            TextInputEditText inputNomeCliente = view.findViewById(R.id.inputNomeCliente);
            TextInputEditText inputTelefone = view.findViewById(R.id.inputTelefone);

            if (inputNomeCliente == null || inputTelefone == null) {
                showToast("Erro ao encontrar os campos de entrada no layout.");
                return;
            }

            String valorNomeCliente = inputNomeCliente.getText().toString().trim();
            String valorTelefone = inputTelefone.getText().toString().trim();

            if (valorNomeCliente.isEmpty() || valorTelefone.isEmpty()) {
                showToast("Todos os campos são obrigatórios.");
                return;
            }

            // Chama o ViewModel para adicionar o cliente
            clienteViewModel.adicionarCliente(valorNomeCliente, valorTelefone);
            dismiss();
        }

        private void showToast(String mensagem) {
            Toast.makeText(getContext(), mensagem, Toast.LENGTH_SHORT).show();
        }

        private void criarCategoria(View view) {
            TextInputEditText inputNomeCategoria = view.findViewById(R.id.inputNomeCategoria);

            if (inputNomeCategoria == null) {
                showToast("Erro ao encontrar os campos de entrada no layout.");
                return;
            }

            String valorNomeCategoria = inputNomeCategoria.getText().toString().trim();

            if (valorNomeCategoria.isEmpty()) {
                showToast("Todos os campos são obrigatórios.");
                return;
            }

            // Chama o ViewModel para adicionar o cliente
            categoriaViewModel.adicionarCategoria(valorNomeCategoria);
            dismiss();

        }

        private void criarServico(View view) {
            EditText editTextIdCategoria = view.findViewById(R.id.editTextIdCategoria);
            EditText editTextIdCliente = view.findViewById(R.id.editTextIdCliente);
            TextInputEditText InputValorServ = view.findViewById(R.id.InputValorServ);
            TextInputEditText InputDataAceiteServ = view.findViewById(R.id.InputDataAceiteServ);
            MaterialAutoCompleteTextView InputEstadoServ = view.findViewById(R.id.InputEstadoServ);
            TextInputEditText InputPagamentoServ = view.findViewById(R.id.InputPagamentoServ);

            if (editTextIdCategoria == null || editTextIdCliente == null || InputValorServ == null || InputDataAceiteServ == null || InputEstadoServ == null || InputPagamentoServ == null) {
                showToast("Erro ao encontrar os campos de entrada no layout.");
                return;
            }

            // Verifica se algum campo está vazio
            String valorIdCategoria = editTextIdCategoria.getText().toString().trim();
            String valorIdCliente = editTextIdCliente.getText().toString().trim();
            String valorValorPagamento = InputValorServ.getText().toString().trim();
            String valorDataAceiteServico = InputDataAceiteServ.getText().toString().trim();
            String valorEstadoServ = InputEstadoServ.getText().toString().trim();
            String valorDataPagamento = InputPagamentoServ.getText().toString().trim();

            if (valorIdCategoria.isEmpty() || valorIdCliente.isEmpty() || valorValorPagamento.isEmpty() ||
                    valorDataAceiteServico.isEmpty() || valorEstadoServ.isEmpty() || valorDataPagamento.isEmpty()) {
                showToast("Todos os campos são obrigatórios.");
                return;
            }

            // Tente converter os valores para os tipos apropriados
            Integer valorIdCategoriaInteger = null;
            Integer valorIdClienteInteger = null;
            Float valorPagamentoFloat = null;

            try {
                valorIdCategoriaInteger = Integer.parseInt(valorIdCategoria);
                valorIdClienteInteger = Integer.parseInt(valorIdCliente);
                valorPagamentoFloat = Float.parseFloat(valorValorPagamento);
            } catch (NumberFormatException e) {
                showToast("Erro na conversão dos valores. Verifique se os campos numéricos estão corretos.");
                return;
            }

            // Chama o ViewModel para adicionar o serviço
            servicoViewModel.adicionarServico(valorIdClienteInteger, valorIdCategoriaInteger, valorPagamentoFloat, valorDataAceiteServico, valorEstadoServ, valorDataPagamento);

            dismiss();
        }



        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.CategoriaButtonAdd) {
                exibirLayout(qualLayoutAddMostrar = "categoria");
            } else if (view.getId() == R.id.btnAddCliente) {
                exibirLayout(qualLayoutAddMostrar = "cliente");
            } else if (view.getId() == R.id.btnAddServico) {
                exibirLayout(qualLayoutAddMostrar = "servico");
                definirValoresSpinnersServico(requireView());
            } else if(view.getId() == R.id.buttonSalvarCategoria){
                criarCategoria(requireView()); // Passa a View principal do fragment
            } else if(view.getId() == R.id.buttonSalvarCliente){
                criarCliente(requireView()); // Passa a View principal do fragment
            } else if(view.getId() == R.id.buttonSalvarServico){
                criarServico(requireView()); // Passa a View principal do fragment
            }
        }
    }
