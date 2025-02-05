package com.example.scmanager.Inicial.Tela;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.lifecycle.ViewModelProvider;

import com.example.scmanager.Gerenciamento.Objetos.Categoria;
import com.example.scmanager.Gerenciamento.Objetos.Cliente;
import com.example.scmanager.Gerenciamento.ViewModel.CategoriaViewModel;
import com.example.scmanager.Gerenciamento.ViewModel.ClienteViewModel;
import com.example.scmanager.Gerenciamento.ViewModel.ServicoViewModel;
import com.example.scmanager.R;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

public class TelaFiltrarServicosInicial extends BottomSheetDialogFragment implements View.OnClickListener{

    private TextInputEditText inputDeData;
    private TextInputLayout textInputLayout;
    private TextInputEditText inputAteData;
    private FlexboxLayout flexboxLayoutCategorias;
    private RadioGroup radioGroupEstadoServico;
    private FlexboxLayout flexboxLayoutClientes;
    private RadioGroup radioGroupOrdemCategoriaOuCliente;
    private Button buttonSalvarServico2;
    private CategoriaViewModel categoriaViewModel;
    private ClienteViewModel clienteViewModel;
    private ServicoViewModel servicoViewModel;
    private static boolean isPrimeiraVez = true;
    private static final String PREFS_NAME = "PreferenciasApp";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.filtrar_servicos_inicio, container, false);
        categoriaViewModel = new ViewModelProvider(requireActivity()).get(CategoriaViewModel.class);
        clienteViewModel = new ViewModelProvider(requireActivity()).get(ClienteViewModel.class);
        servicoViewModel = new ViewModelProvider(requireActivity()).get(ServicoViewModel.class);
        categoriaViewModel.carregarCategorias();
        clienteViewModel.carregarClientes();
        servicoViewModel.carregarServicosInicio();
        atribuirInputsERadios(view);
        return view;
    }

    private void atribuirInputsERadios(View view)
    {

        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);

        inputDeData = view.findViewById(R.id.inputDeData);
        String ordemDeData = prefs.getString("ordemDeData", ""); // Padrão: CRESCENTE
        inputDeData.setText(ordemDeData);

        textInputLayout = view.findViewById(R.id.textInputLayout);

        inputAteData = view.findViewById(R.id.inputAteData);
        String ordemAteData = prefs.getString("ordemAteData", ""); // Padrão: CRESCENTE
        inputAteData.setText(ordemAteData);

        inputDeData.setOnClickListener(v -> abrirDatePicker(inputDeData));
        inputAteData.setOnClickListener(v -> abrirDatePicker(inputAteData));
        textInputLayout.setOnClickListener(v -> abrirDatePicker(inputDeData));


        flexboxLayoutCategorias = view.findViewById(R.id.flexboxLayoutCategorias);
        categoriaViewModel.getListaCategorias().observe(getViewLifecycleOwner(), categorias -> {
            flexboxLayoutCategorias.removeAllViews(); // Limpa o layout antes de adicionar novos itens
            for (Categoria categoria : categorias) {
                CheckBox checkBox = new CheckBox(requireContext());
                checkBox.setText(categoria.getNome());
                checkBox.setId(categoria.getId());

                // Verifica se a categoria foi previamente selecionada
                boolean isSelected = prefs.getBoolean("categoria_" + categoria.getId(), false); // Padrão: desmarcado
                checkBox.setChecked(isSelected);

                // Adiciona listener para salvar o estado do CheckBox
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("categoria_" + categoria.getId(), isChecked);
                    editor.apply(); // Salva o estado
                });

                flexboxLayoutCategorias.addView(checkBox);
            }
        });

        if (isPrimeiraVez)
        {
            for (int i = 0; i < flexboxLayoutCategorias.getChildCount(); i++)
            {
                CheckBox checkBox = (CheckBox) flexboxLayoutCategorias.getChildAt(i);
                checkBox.setChecked(true); // Marca o CheckBox como selecionado
            }
        } else {
            for (int i = 0; i < flexboxLayoutCategorias.getChildCount(); i++)
            {
                CheckBox checkBox = (CheckBox) flexboxLayoutCategorias.getChildAt(i);
                boolean isSelected = prefs.getBoolean("categoria_" + checkBox.getId(), false); // Padrão: desmarcado
                checkBox.setChecked(isSelected); // Restaura o estado
            }
        }

        radioGroupEstadoServico = view.findViewById(R.id.radioGroupEstadoServico);
        String ordemEstadoInicio = prefs.getString("ordemEstadoInicio", "Todos");
        if(ordemEstadoInicio.equals("Todos"))
        {
            radioGroupEstadoServico.check(R.id.RadioButtonEstadoServicoTodos);
        } else if (ordemEstadoInicio.equals("Pago")){
            radioGroupEstadoServico.check(R.id.RadioButtonEstadoServicoPago);
        } else if (ordemEstadoInicio.equals("NaoPago")){
            radioGroupEstadoServico.check(R.id.RadioButtonEstadoServicoNãoPago);
        }

        flexboxLayoutClientes = view.findViewById(R.id.flexboxLayoutClientes);
        clienteViewModel.getListaClientes().observe(getViewLifecycleOwner(), clientes -> {
            flexboxLayoutClientes.removeAllViews(); // Limpa o layout antes de adicionar novos itens
            for (Cliente cliente : clientes) {
                CheckBox checkBox = new CheckBox(requireContext());
                checkBox.setText(cliente.getNome());
                checkBox.setId((int) cliente.getId()); // Converte long para int

                // Verifica se o cliente foi previamente selecionado
                boolean isSelected = prefs.getBoolean("cliente_" + cliente.getId(), false); // Padrão: desmarcado
                checkBox.setChecked(isSelected);

                // Adiciona listener para salvar o estado do CheckBox
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("cliente_" + cliente.getId(), isChecked);
                    editor.apply(); // Salva o estado
                });

                flexboxLayoutClientes.addView(checkBox);
            }
        });

        if (isPrimeiraVez)
        {
            for (int i = 0; i < flexboxLayoutClientes.getChildCount(); i++)
            {
                CheckBox checkBox = (CheckBox) flexboxLayoutClientes.getChildAt(i);
                checkBox.setChecked(true); // Marca o CheckBox como selecionado
            }
            isPrimeiraVez = false;
        } else {
            for (int i = 0; i < flexboxLayoutClientes.getChildCount(); i++) {
                CheckBox checkBox = (CheckBox) flexboxLayoutClientes.getChildAt(i);
                boolean isSelected = prefs.getBoolean("cliente_" + checkBox.getId(), false); // Padrão: desmarcado
                checkBox.setChecked(isSelected); // Restaura o estado
            }

        }

        radioGroupOrdemCategoriaOuCliente = view.findViewById(R.id.radioGroupOrdemCategoriaOuCliente);
        String ordemCategoriaOuCliente = prefs.getString("ordemCategoriaOuCliente", "Categorias"); // Padrão: CRESCENTE
        if(ordemCategoriaOuCliente.equals("Categorias"))
        {
            radioGroupOrdemCategoriaOuCliente.check(R.id.RadioButtonCategorias);
        } else if (ordemCategoriaOuCliente.equals("Clientes"))
        {
            radioGroupOrdemCategoriaOuCliente.check(R.id.RadioButtonClientes);
        }

        buttonSalvarServico2 = view.findViewById(R.id.buttonSalvarServico2);
        buttonSalvarServico2.setOnClickListener(this);
    }

    private void abrirDatePicker(TextInputEditText input) {
        // Obtenha a data atual para exibir no DatePicker
        Calendar calendar = Calendar.getInstance();
        int ano = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        // Crie e exiba o DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    // Formate a data para o formato desejado (dd/MM/yyyy)
                    String dataSelecionada = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);

                    // Exiba a data selecionada no campo correspondente
                    input.setText(dataSelecionada);
                },
                ano, mes, dia
        );

        datePickerDialog.show();
    }

    private void filtrarServicoInicio()
    {
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        String ordemDeData = inputDeData.getText().toString().trim();
        String ordemAteData = inputAteData.getText().toString().trim();

        SharedPreferences.Editor editor = prefs.edit(); // Cria o editor para editar o SharedPreferences
        editor.putString("ordemDeData", ordemDeData); // Armazena o valor da ordemDeData
        editor.putString("ordemAteData", ordemAteData); // Armazena o valor da ordemDeData
        // Pegar os CheckBoxes do FlexboxLayout de Categorias
        for (int i = 0; i < flexboxLayoutCategorias.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) flexboxLayoutCategorias.getChildAt(i);
            boolean isSelected = checkBox.isChecked(); // Verifica se o CheckBox está selecionado

            // Armazena o estado de cada CheckBox (categorias) no SharedPreferences
            editor.putBoolean("categoria_" + checkBox.getId(), isSelected);
        }

        int selectedIdEstado = radioGroupEstadoServico.getCheckedRadioButtonId();
        if (selectedIdEstado != -1) {
            RadioButton radioButtonSelecionado = requireView().findViewById(selectedIdEstado);
            String textoSelecionado = radioButtonSelecionado.getText().toString();

            if (textoSelecionado.equals("Todos")) {
                editor.putString("ordemEstadoInicio", "Todos");
            } else if (textoSelecionado.equals("Pago")) {
                editor.putString("ordemEstadoInicio", "Pago");
            }else if (textoSelecionado.equals("Não Pago")) {
                editor.putString("ordemEstadoInicio", "NaoPago");
            }
        }

        // Pegar os CheckBoxes do FlexboxLayout de Clientes
        for (int i = 0; i < flexboxLayoutClientes.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) flexboxLayoutClientes.getChildAt(i);
            boolean isSelected = checkBox.isChecked(); // Verifica se o CheckBox está selecionado

            // Armazena o estado de cada CheckBox (clientes) no SharedPreferences
            editor.putBoolean("cliente_" + checkBox.getId(), isSelected);
        }

        int selectedIdExibirGraficoPor = radioGroupOrdemCategoriaOuCliente.getCheckedRadioButtonId();
        if (selectedIdExibirGraficoPor != -1) {
            RadioButton radioButtonSelecionado = requireView().findViewById(selectedIdExibirGraficoPor);
            String textoSelecionado = radioButtonSelecionado.getText().toString();

            if (textoSelecionado.equals("Categorias")) {
                editor.putString("ordemCategoriaOuCliente", "Categorias");
            } else if (textoSelecionado.equals("Clientes")) {
                editor.putString("ordemCategoriaOuCliente", "Clientes");
            }
        }

        editor.apply(); // Salva as mudanças (apply é assíncrono e mais recomendado)
        servicoViewModel.carregarServicosInicio();
        dismiss();
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonSalvarServico2)
        {
            filtrarServicoInicio();
        }
    }
}
