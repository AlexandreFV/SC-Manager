package com.example.scmanager;

import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.ViewModelProvider;

import com.example.scmanager.Gerenciamento.Objetos.Categoria;
import com.example.scmanager.Gerenciamento.Objetos.Cliente;
import com.example.scmanager.Gerenciamento.ViewModel.CategoriaViewModel;
import com.example.scmanager.Gerenciamento.ViewModel.ClienteViewModel;
import com.example.scmanager.Gerenciamento.ViewModel.ServicoViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashSet;
import java.util.Set;

public class TelaFiltrarRegistrosListas extends BottomSheetDialogFragment implements View.OnClickListener{

    private ConstraintLayout FiltrarCategoria;
    private ConstraintLayout FiltrarCliente;
    private ConstraintLayout FiltrarServico;

    private Button SelecionarCategoria;
    private Button SelecionarCliente;
    private Button SelecionarServico;

    private Button FiltrarClienteButton;
    private Button FiltrarCategoriaButton;
    private Button FiltrarServicoButton;
    private String qualLayoutAddMostrar;

    //Categoria
    private RadioGroup radioGroupOrdemAlfabeticaNomesCategoria;
    private Button buttonFiltrarCategoria;
    private CategoriaViewModel categoriaViewModel;

    //Cliente
    private RadioGroup radioGroupOrdemAlfabeticaNomesClienteCliente;
    private RadioGroup radioGroupOrdemNumeriaTelefonesClienteCliente;
    private Button buttonFiltrarCliente;
    private ClienteViewModel clienteViewModel;


    //Servico
    private RadioGroup radioGroupOrdemAlfabeticaNomesTipoServico;

    private LinearLayout linearLayoutCheck;
    private CheckBox checkBoxModeloTipoServico;

    private RadioGroup radioGroupOrdemAlfabeticaNomesClienteServico;

    private RadioGroup radioGroupOrdemValorServico;

    private RadioGroup radioGroupOrdemDataAceiteServico;

    private RadioGroup radioGroupEstadoServico;
    private Group GrupoOrdenarPagamentoEstipuladoVencidoNVencidoOuPaga;
    private TextView textOrdernarDataPagEstipulado;
    private RadioGroup radioGroupDataPagEstipuladoOuPaga; //Deve mudar no codigo os valores caso seja pago ou não pago
    private static final String PREFS_NAME = "PreferenciasApp";
    private static final String KEY_ORDEM_CATEGORIA = "ordemCategoria";
    private RadioButton RadioButtonDataPagEstipuladoTodos;
    private RadioButton RadioButtonDataPagEstipuladoVenciados;
    private RadioButton RadioButtonDataPagEstipuladoNaoVencidos;
    private ServicoViewModel servicoViewModel;
    private String valorExibir;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_filtrar_listas, container, false);

        qualLayoutAddMostrar = getArguments().getString("veioDe", "");
        categoriaViewModel = new ViewModelProvider(requireActivity()).get(CategoriaViewModel.class);
        clienteViewModel = new ViewModelProvider(requireActivity()).get(ClienteViewModel.class);
        servicoViewModel = new ViewModelProvider(requireActivity()).get(ServicoViewModel.class);

        FiltrarCategoria = view.findViewById(R.id.FundoLayoutAddCategoria);
        FiltrarCliente = view.findViewById(R.id.FundoLayoutAddCliente);
        FiltrarServico = view.findViewById(R.id.FundoLayoutAddServico);

        SelecionarCategoria = view.findViewById(R.id.CategoriaButtonFiltrar);
        SelecionarCategoria.setOnClickListener(this);
        SelecionarCliente = view.findViewById(R.id.btnFiltrarCliente);
        SelecionarCliente.setOnClickListener(this);
        SelecionarServico = view.findViewById(R.id.btnFiltrarServico);
        SelecionarServico.setOnClickListener(this);

        atribuirReferenciasCategoria(view);
        atribuirReferenciaClientes(view);
        atribuirReferenciasServicos(view);

        FiltrarCategoriaButton = view.findViewById(R.id.buttonSalvarCategoria);
        FiltrarCategoriaButton.setOnClickListener(this);
        FiltrarClienteButton = view.findViewById(R.id.buttonFiltrarCliente);
        FiltrarClienteButton.setOnClickListener(this);
        FiltrarServicoButton = view.findViewById(R.id.buttonSalvarServico);
        FiltrarServicoButton.setOnClickListener(this);

        exibirLayout(qualLayoutAddMostrar);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Agora você pode chamar métodos que dependem da view, como:
        atualizarInterfaceEstadoServico(valorExibir);
    }

    private void atribuirReferenciasCategoria(View view) {
        radioGroupOrdemAlfabeticaNomesCategoria = view.findViewById(R.id.radioGroupOrdemAlfabeticaNomesCategoria);
        buttonFiltrarCategoria = view.findViewById(R.id.buttonSalvarCategoria);

        // Recupera a última ordenação salva
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        String ordemSalva = prefs.getString(KEY_ORDEM_CATEGORIA, "CRESCENTE"); // Padrão: CRESCENTE

        if (ordemSalva.equals("CRESCENTE")) {
            radioGroupOrdemAlfabeticaNomesCategoria.check(R.id.NomesCategoriaCrescenteRadioButton);
        } else if (ordemSalva.equals("DECRESCENTE")) {
            radioGroupOrdemAlfabeticaNomesCategoria.check(R.id.NomesCategoriaDecrescenteRadioButton);
        }
    }

    private void atribuirReferenciaClientes(View view)
    {
        radioGroupOrdemAlfabeticaNomesClienteCliente = view.findViewById(R.id.radioGroupOrdemAlfabeticaNomesClienteCliente);
        radioGroupOrdemNumeriaTelefonesClienteCliente = view.findViewById(R.id.radioGroupOrdemNumeriaTelefonesClienteCliente);
        buttonFiltrarCliente = view.findViewById(R.id.buttonFiltrarCliente);

        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        String ordemSalvaNomeCliente = prefs.getString("ordemClienteNome", "PADRAO"); // Padrão: CRESCENTE

        if (ordemSalvaNomeCliente.equals("CRESCENTE")) {
            radioGroupOrdemAlfabeticaNomesClienteCliente.check(R.id.RadioButtonNomeClienteClienteCrescente);
        } else if (ordemSalvaNomeCliente.equals("DECRESCENTE")) {
            radioGroupOrdemAlfabeticaNomesClienteCliente.check(R.id.RadioButtonNomeClienteClienteDecrescente);
        }else if (ordemSalvaNomeCliente.equals("PADRAO")) {
            radioGroupOrdemAlfabeticaNomesClienteCliente.check(R.id.RadioButtonNomeClienteClientePadrao);
        }

        String ordemSalvaTelefoneCliente = prefs.getString("ordemClienteTelefone", "PADRAO"); // Padrão: CRESCENTE
        if (ordemSalvaTelefoneCliente.equals("CRESCENTE")) {
            radioGroupOrdemNumeriaTelefonesClienteCliente.check(R.id.RadioButtonTelefonesClienteClienteCrescente);
        } else if (ordemSalvaTelefoneCliente.equals("DECRESCENTE")) {
            radioGroupOrdemNumeriaTelefonesClienteCliente.check(R.id.RadioButtonTelefonesClienteClienteDecrescente);
        }else if (ordemSalvaTelefoneCliente.equals("PADRAO")) {
            radioGroupOrdemNumeriaTelefonesClienteCliente.check(R.id.RadioButtonTelefonesClienteClientePadrao);
        }


    }

    private void atribuirReferenciasServicos(View view)
    {
        radioGroupOrdemAlfabeticaNomesTipoServico = view.findViewById(R.id.radioGroupOrdemAlfabeticaNomesTipoServico);
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        String ordemSalvaNomeTipoServico = prefs.getString("ordemServicoNomeCategoria", "PADRAO"); // Padrão: CRESCENTE
        if (ordemSalvaNomeTipoServico.equals("CRESCENTE")) {
            radioGroupOrdemAlfabeticaNomesTipoServico.check(R.id.RadioButtonNomeTipoServicoCrescente);
        } else if (ordemSalvaNomeTipoServico.equals("DECRESCENTE")) {
            radioGroupOrdemAlfabeticaNomesTipoServico.check(R.id.RadioButtonNomeTipoServicoDecrescente);
        } else if (ordemSalvaNomeTipoServico.equals("PADRAO")) {
            radioGroupOrdemAlfabeticaNomesTipoServico.check(R.id.RadioButtonNomeTipoServicoPadrao);
        }


        radioGroupOrdemAlfabeticaNomesClienteServico = view.findViewById(R.id.radioGroupOrdemAlfabeticaNomesClienteServico);
        String ordemSalvaNomeClienteServico = prefs.getString("ordemServicoNomeCliente", "PADRAO"); // Padrão: CRESCENTE
        if (ordemSalvaNomeClienteServico.equals("CRESCENTE")) {
            radioGroupOrdemAlfabeticaNomesClienteServico.check(R.id.RadioButtonNomeClienteServicoCrescente);
        } else if (ordemSalvaNomeClienteServico.equals("DECRESCENTE")) {
            radioGroupOrdemAlfabeticaNomesClienteServico.check(R.id.RadioButtonNomeClienteServicoDecrescente);
        } else if (ordemSalvaNomeClienteServico.equals("PADRAO")) {
            radioGroupOrdemAlfabeticaNomesClienteServico.check(R.id.RadioButtonNomeClienteServicoPadrao);
        }


        //Terminar e ajeitar os checkbox, eles devem pular a linha caso n caiba mais

        radioGroupOrdemValorServico = view.findViewById(R.id.radioGroupOrdemValorServico);
        String ordemSalvaValorServico = prefs.getString("ordemServicoValor", "PADRAO"); // Padrão: CRESCENTE
        if (ordemSalvaValorServico.equals("CRESCENTE")) {
            radioGroupOrdemValorServico.check(R.id.RadioButtonValorCrescente);
        } else if (ordemSalvaValorServico.equals("DECRESCENTE")) {
            radioGroupOrdemValorServico.check(R.id.RadioButtonValorDecrescente);
        } else if (ordemSalvaValorServico.equals("PADRAO")) {
            radioGroupOrdemValorServico.check(R.id.RadioButtonValorPadrao);
        }

        radioGroupOrdemDataAceiteServico = view.findViewById(R.id.radioGroupOrdemDataAceiteServico);
        String ordemSalvaDataAceiteServico = prefs.getString("ordemServicoDataAceite", "PADRAO"); // Padrão: CRESCENTE
        if (ordemSalvaDataAceiteServico.equals("CRESCENTE")) {
            radioGroupOrdemDataAceiteServico.check(R.id.RadioButtonDataAceiteCrescente);
        } else if (ordemSalvaDataAceiteServico.equals("DECRESCENTE")) {
            radioGroupOrdemDataAceiteServico.check(R.id.RadioButtonDataAceiteDecrescente);
        } else if (ordemSalvaDataAceiteServico.equals("PADRAO")) {
            radioGroupOrdemDataAceiteServico.check(R.id.RadioButtonDataAceitePadrao);
        }

        //Caso o valor de estado seja diferente de todos, ele deve exibir o grupoOrdenar pagamento, alterar o texto do textView e alterar os valores de dentro do radio group
        radioGroupEstadoServico = view.findViewById(R.id.radioGroupEstadoServico);
        String ordemSalvaEstadoServico = prefs.getString("ordemServicoEstado", "TODOS"); // Padrão: CRESCENTE
        if (ordemSalvaEstadoServico.equals("PAGO")) {
            radioGroupEstadoServico.check(R.id.RadioButtonEstadoServicoPago);
        } else if (ordemSalvaEstadoServico.equals("NAOPAGO")) {
            radioGroupEstadoServico.check(R.id.RadioButtonEstadoServicoNãoPago);
        } else if (ordemSalvaEstadoServico.equals("TODOS")){
            radioGroupEstadoServico.check(R.id.RadioButtonEstadoServicoTodos);
        }

        GrupoOrdenarPagamentoEstipuladoVencidoNVencidoOuPaga = view.findViewById(R.id.GrupoOrdenarPagamentoEstipuladoVencidoNVencidoOuPaga);
        textOrdernarDataPagEstipulado = view.findViewById(R.id.textOrdernarDataPagEstipulado);

        // Adiciona um listener para capturar mudanças no RadioGroup
        radioGroupEstadoServico.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String novoEstado = "TODOS"; // Padrão

                if (checkedId == R.id.RadioButtonEstadoServicoPago) {
                    novoEstado = "PAGO";
                } else if (checkedId == R.id.RadioButtonEstadoServicoNãoPago) {
                    novoEstado = "NAOPAGO";
                } else if (checkedId == R.id.RadioButtonEstadoServicoTodos){
                    novoEstado = "TODOS";
                }

                // Atualiza a interface conforme a nova escolha
                atualizarInterfaceEstadoServico(novoEstado);
            }
        });

        radioGroupDataPagEstipuladoOuPaga = view.findViewById(R.id.radioGroupDataPagEstipuladoOuPaga);
        String ordemSalvaDataPagoOuEstipulado = prefs.getString("ordemServicoDataAceiteOuEstipulado", "TODOS");
        if (ordemSalvaEstadoServico.equals("PAGO")) {
            if(ordemSalvaDataPagoOuEstipulado.equals("CRESCENTE")){
                radioGroupDataPagEstipuladoOuPaga.check(R.id.RadioButtonDataPagEstipuladoTodos);
            } else if(ordemSalvaDataPagoOuEstipulado.equals("DECRESCENTE")) {
                radioGroupDataPagEstipuladoOuPaga.check(R.id.RadioButtonDataPagEstipuladoVenciados);
            }
            valorExibir="PAGO";
        }
        else if (ordemSalvaEstadoServico.equals("NAOPAGO")) {
            if(ordemSalvaDataPagoOuEstipulado.equals("TODOS")){
                radioGroupDataPagEstipuladoOuPaga.check(R.id.RadioButtonDataPagEstipuladoTodos);
            } else if(ordemSalvaDataPagoOuEstipulado.equals("PAGAMENTOSVENCIDOS")) {
                radioGroupDataPagEstipuladoOuPaga.check(R.id.RadioButtonDataPagEstipuladoVenciados);
            } else if(ordemSalvaDataPagoOuEstipulado.equals("PAGAMENTOSNAOVENCIDOS")) {
                radioGroupDataPagEstipuladoOuPaga.check(R.id.RadioButtonDataPagEstipuladoNaoVencidos);
            }
            valorExibir="NAOPAGO";
        } else{
            valorExibir="TODOS";
        }

    }


    private void atualizarInterfaceEstadoServico(String estado) {
        if (estado.equals("PAGO")) {
            GrupoOrdenarPagamentoEstipuladoVencidoNVencidoOuPaga.setVisibility(View.VISIBLE);
            textOrdernarDataPagEstipulado.setText("Ordenação por Data de Pagamento:");

            RadioButtonDataPagEstipuladoTodos = requireView().findViewById(R.id.RadioButtonDataPagEstipuladoTodos);
            RadioButtonDataPagEstipuladoTodos.setText("Crescente");
            RadioButtonDataPagEstipuladoVenciados = requireView().findViewById(R.id.RadioButtonDataPagEstipuladoVenciados);
            RadioButtonDataPagEstipuladoVenciados.setText("Decrescente");
            RadioButtonDataPagEstipuladoNaoVencidos = requireView().findViewById(R.id.RadioButtonDataPagEstipuladoNaoVencidos);
            RadioButtonDataPagEstipuladoNaoVencidos.setVisibility(View.GONE);

        } else if (estado.equals("NAOPAGO")) {
            GrupoOrdenarPagamentoEstipuladoVencidoNVencidoOuPaga.setVisibility(View.VISIBLE);
            textOrdernarDataPagEstipulado.setText("Ordenação por Data de Pagamento Estipulado:");

            RadioButtonDataPagEstipuladoTodos = requireView().findViewById(R.id.RadioButtonDataPagEstipuladoTodos);
            RadioButtonDataPagEstipuladoTodos.setText("Todos");
            RadioButtonDataPagEstipuladoVenciados = requireView().findViewById(R.id.RadioButtonDataPagEstipuladoVenciados);
            RadioButtonDataPagEstipuladoVenciados.setText("Pagamentos Vencidos");
            RadioButtonDataPagEstipuladoNaoVencidos = requireView().findViewById(R.id.RadioButtonDataPagEstipuladoNaoVencidos);
            RadioButtonDataPagEstipuladoNaoVencidos.setVisibility(View.VISIBLE);

        } else { // "TODOS"
            GrupoOrdenarPagamentoEstipuladoVencidoNVencidoOuPaga.setVisibility(View.GONE);
        }
    }


    private void exibirLayout(String layoutAExibir)
    {
        if(layoutAExibir.equals("categoria"))
        {
            FiltrarCategoria.setVisibility(View.VISIBLE);
            FiltrarCliente.setVisibility(View.GONE);
            FiltrarServico.setVisibility(View.GONE);
        } else if(layoutAExibir.equals("cliente")){
            FiltrarCliente.setVisibility(View.VISIBLE);
            FiltrarCategoria.setVisibility(View.GONE);
            FiltrarServico.setVisibility(View.GONE);
        }else if(layoutAExibir.equals("servico")){
            FiltrarServico.setVisibility(View.VISIBLE);
            FiltrarCategoria.setVisibility(View.GONE);
            FiltrarCliente.setVisibility(View.GONE);
        }
        buttonSelecionado(layoutAExibir);

    }

    private void buttonSelecionado(String layoutAExibir1) {
        if (layoutAExibir1.equals("categoria")) {
            alterarBackgroundGradualmente(SelecionarCategoria); // Troca o background do botão de Categoria
            alterarBackgroundGradualmenteParaSemGradiente(SelecionarCliente);
            alterarBackgroundGradualmenteParaSemGradiente(SelecionarServico);
        } else if (layoutAExibir1.equals("cliente")) {
            alterarBackgroundGradualmente(SelecionarCliente); // Troca o background do botão de Cliente
            alterarBackgroundGradualmenteParaSemGradiente(SelecionarCategoria);
            alterarBackgroundGradualmenteParaSemGradiente(SelecionarServico);
        } else if (layoutAExibir1.equals("servico")) {
            alterarBackgroundGradualmente(SelecionarServico); // Troca o background do botão de Serviço
            alterarBackgroundGradualmenteParaSemGradiente(SelecionarCategoria);
            alterarBackgroundGradualmenteParaSemGradiente(SelecionarCliente);
        }
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


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.CategoriaButtonFiltrar) {
            exibirLayout(qualLayoutAddMostrar = "categoria");
        } else if (view.getId() == R.id.btnFiltrarCliente) {
            exibirLayout(qualLayoutAddMostrar = "cliente");
        } else if (view.getId() == R.id.btnFiltrarServico) {
            exibirLayout(qualLayoutAddMostrar = "servico");
        } else if (view.getId() == R.id.buttonSalvarCategoria){
            filtrarCategoria(requireView());
        } else if (view.getId() == R.id.buttonFiltrarCliente) {
            filtrarCliente(requireView());
        } else if (view.getId() == R.id.buttonSalvarServico){
            filtrarServicos(requireView());
        }
    }

    private void filtrarCategoria(View view) {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Captura a ordenação selecionada pelo usuário
        int selectedId = radioGroupOrdemAlfabeticaNomesCategoria.getCheckedRadioButtonId();

        if (selectedId != -1) {
            RadioButton radioButtonSelecionado = view.findViewById(selectedId);
            String textoSelecionado = radioButtonSelecionado.getText().toString();

            if (textoSelecionado.equals("Crescente")) {
                editor.putString(KEY_ORDEM_CATEGORIA, "CRESCENTE");
            } else if (textoSelecionado.equals("Decrescente")) {
                editor.putString(KEY_ORDEM_CATEGORIA, "DECRESCENTE");
            }
        }

        // Salva as preferências do usuário
        editor.apply();

        // Atualiza a lista de categorias após aplicar os filtros
        categoriaViewModel.carregarCategorias();
        dismiss();
    }

    private void filtrarCliente(View view) {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Captura a ordenação por nome
        int selectedIdNome = radioGroupOrdemAlfabeticaNomesClienteCliente.getCheckedRadioButtonId();
        if (selectedIdNome != -1) {
            RadioButton radioButtonSelecionado = view.findViewById(selectedIdNome);
            String textoSelecionado = radioButtonSelecionado.getText().toString();

            if (textoSelecionado.equals("Crescente")) {
                editor.putString("ordemClienteNome", "CRESCENTE");
            } else if (textoSelecionado.equals("Decrescente")) {
                editor.putString("ordemClienteNome", "DECRESCENTE");
            } else if (textoSelecionado.equals("Padrão")) {
                editor.putString("ordemClienteNome", "PADRAO");
            }
        }

        // Captura a ordenação por telefone
        int selectedIdTelefone = radioGroupOrdemNumeriaTelefonesClienteCliente.getCheckedRadioButtonId();
        if (selectedIdTelefone != -1) {
            RadioButton radioButtonSelecionado = view.findViewById(selectedIdTelefone);
            String textoSelecionado = radioButtonSelecionado.getText().toString();

            if (textoSelecionado.equals("Crescente")) {
                editor.putString("ordemClienteTelefone", "CRESCENTE");
            } else if (textoSelecionado.equals("Decrescente")) {
                editor.putString("ordemClienteTelefone", "DECRESCENTE");
            }else if (textoSelecionado.equals("Padrão")) {
                editor.putString("ordemClienteTelefone", "PADRAO");
            }
        }

        // Salva as preferências do usuário
        editor.apply();

        // Atualiza a lista de clientes após aplicar os filtros
        clienteViewModel.carregarClientes();
        dismiss();

    }

    private void filtrarServicos(View view)
    {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        int selectedIdTipoServico = radioGroupOrdemAlfabeticaNomesTipoServico.getCheckedRadioButtonId();
        if (selectedIdTipoServico != -1) {
            RadioButton radioButtonSelecionado = view.findViewById(selectedIdTipoServico);
            String textoSelecionado = radioButtonSelecionado.getText().toString();

            if (textoSelecionado.equals("Crescente")) {
                editor.putString("ordemServicoNomeCategoria", "CRESCENTE");
            } else if (textoSelecionado.equals("Decrescente")) {
                editor.putString("ordemServicoNomeCategoria", "DECRESCENTE");
            }else if (textoSelecionado.equals("Padrão")) {
                editor.putString("ordemServicoNomeCategoria", "PADRAO");
            }
        }

        int selectedIdNomeCliente = radioGroupOrdemAlfabeticaNomesClienteServico.getCheckedRadioButtonId();
        if (selectedIdNomeCliente != -1) {
            RadioButton radioButtonSelecionado = view.findViewById(selectedIdNomeCliente);
            String textoSelecionado = radioButtonSelecionado.getText().toString();

            if (textoSelecionado.equals("Crescente")) {
                editor.putString("ordemServicoNomeCliente", "CRESCENTE");
            } else if (textoSelecionado.equals("Decrescente")) {
                editor.putString("ordemServicoNomeCliente", "DECRESCENTE");
            }else if (textoSelecionado.equals("Padrão")) {
                editor.putString("ordemServicoNomeCliente", "PADRAO");
            }
        }

        int selectedIdValor = radioGroupOrdemValorServico.getCheckedRadioButtonId();
        if (selectedIdValor != -1) {
            RadioButton radioButtonSelecionado = view.findViewById(selectedIdValor);
            String textoSelecionado = radioButtonSelecionado.getText().toString();

            if (textoSelecionado.equals("Crescente")) {
                editor.putString("ordemServicoValor", "CRESCENTE");
            } else if (textoSelecionado.equals("Decrescente")) {
                editor.putString("ordemServicoValor", "DECRESCENTE");
            }else if (textoSelecionado.equals("Padrão")) {
                editor.putString("ordemServicoValor", "PADRAO");
            }
        }

        int selectedIdDataAceite = radioGroupOrdemDataAceiteServico.getCheckedRadioButtonId();
        if (selectedIdDataAceite != -1) {
            RadioButton radioButtonSelecionado = view.findViewById(selectedIdDataAceite);
            String textoSelecionado = radioButtonSelecionado.getText().toString();

            if (textoSelecionado.equals("Crescente")) {
                editor.putString("ordemServicoDataAceite", "CRESCENTE");
            } else if (textoSelecionado.equals("Decrescente")) {
                editor.putString("ordemServicoDataAceite", "DECRESCENTE");
            }else if (textoSelecionado.equals("Padrão")) {
                editor.putString("ordemServicoDataAceite", "PADRAO");
            }
        }

        int selectedIdEstado = radioGroupEstadoServico.getCheckedRadioButtonId();
        if (selectedIdEstado != -1) {
            RadioButton radioButtonSelecionado = view.findViewById(selectedIdEstado);
            String textoSelecionado = radioButtonSelecionado.getText().toString();

            if (textoSelecionado.equals("Todos")) {
                editor.putString("ordemServicoEstado", "TODOS");
            } else if (textoSelecionado.equals("Pago")) {
                editor.putString("ordemServicoEstado", "PAGO");
            } else if (textoSelecionado.equals("Não Pago")) {
                editor.putString("ordemServicoEstado", "NAOPAGO");
            }
        }


        int selectedIdDataPagOuEstipulado = radioGroupDataPagEstipuladoOuPaga.getCheckedRadioButtonId();
        if (selectedIdDataPagOuEstipulado != -1) {
            RadioButton radioButtonSelecionado = view.findViewById(selectedIdDataPagOuEstipulado);
            String textoSelecionado = radioButtonSelecionado.getText().toString();

            if (textoSelecionado.equals("Todos")) {
                editor.putString("ordemServicoDataAceiteOuEstipulado", "TODOS");
            } else if (textoSelecionado.equals("Pagamentos Vencidos")) {
                editor.putString("ordemServicoDataAceiteOuEstipulado", "PAGAMENTOSVENCIDOS");
            } else if (textoSelecionado.equals("Pagamentos Não Vencidos")) {
                editor.putString("ordemServicoDataAceiteOuEstipulado", "PAGAMENTOSNAOVENCIDOS");
            }

            else if(textoSelecionado.equals("Crescente")){
                editor.putString("ordemServicoDataAceiteOuEstipulado", "CRESCENTE");
            } else if(textoSelecionado.equals("Decrescente")){
                editor.putString("ordemServicoDataAceiteOuEstipulado", "DECRESCENTE");
            }
        }

        // Salva as preferências do usuário
        editor.apply();

        // Atualiza a lista de clientes após aplicar os filtros
        servicoViewModel.carregarServicos();
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
