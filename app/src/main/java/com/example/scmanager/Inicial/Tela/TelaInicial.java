package com.example.scmanager.Inicial.Tela;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scmanager.BuildConfig;
import com.example.scmanager.Gerenciamento.Adapter.CategoriaAdapter;
import com.example.scmanager.Gerenciamento.Adapter.ClienteAdapter;
import com.example.scmanager.Gerenciamento.Adapter.ServicoAdapter;
import com.example.scmanager.Gerenciamento.Objetos.Categoria;
import com.example.scmanager.Gerenciamento.Objetos.Cliente;
import com.example.scmanager.Gerenciamento.Objetos.Servico;
import com.example.scmanager.Gerenciamento.Tela.Servico.BottomSheetDetalhesServico;
import com.example.scmanager.Gerenciamento.Tela.TelaGerenciamento;
import com.example.scmanager.Gerenciamento.ViewModel.CategoriaViewModel;
import com.example.scmanager.Gerenciamento.ViewModel.ClienteViewModel;
import com.example.scmanager.Gerenciamento.ViewModel.ServicoViewModel;
import com.example.scmanager.R;
import com.example.scmanager.TelaFiltrarRegistrosListas;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;


public class TelaInicial extends AppCompatActivity implements View.OnClickListener {
    private String analiseTexto;
    private Float totalRecebido = 0f;
    private String clienteMaiorCompra = "";
    private String clienteMenorCompra = "";
    private Float maiorVenda = 0f;
    private Float menorVenda = Float.MAX_VALUE;
    private String dataMaisVendas = "";
    private String dataMenosVendas = "";
    private Float maiorVendaDia = 0f;
    private Float menorVendaDia = Float.MAX_VALUE;

    private ConstraintLayout GrupoIcones;

    private View FundoGradiente;

    private TextView TelaInicial;
    private TextView Recebimento;
    private TextView periodo;
    private int getOriginalWidthGradiente;
    private int getOriginalHeightGradiente;

    private static boolean primeiraExecucao = true;

    private Button buttonGerenciamentoClientes;

    private Button buttonGerenciamentoServico;
    private Button buttonGerenciamentoCategoria;

    private ImageButton setaBaixo;

    private ViewSwitcher SwitcherDasInfosDoGrafico;

    private ImageButton buttonTrocarParaViewAnalise;

    private ImageButton buttonTrocarParaViewDados;

    private PieChart grafico;

    private ConstraintLayout GrupoDados;
    private ConstraintLayout GrupoAnalise;

    private TextView textRespostaIA;
    private ServicoViewModel servicoViewModel;
    private ServicoAdapter servicoAdapter;
    private CategoriaAdapter categoriaAdapter;
    private ClienteAdapter clienteAdapter;
    private CategoriaViewModel categoriaViewModel;
    private ClienteViewModel clienteViewModel;
    private RecyclerView QuadradoListaDados;
    private TextView textNaoHaDadosGrafico;
    private boolean temDadosServico = false;
    private Button buttonAnalisarGrafico;
    private ImageButton buttonFiltrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        // Esconde a barra de status.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_inicial);



        periodo = findViewById(R.id.periodo);
        Recebimento = findViewById(R.id.Recebimento);
        grafico = findViewById(R.id.grafico);
        setaBaixo = findViewById(R.id.setaBaixo);
        SwitcherDasInfosDoGrafico = findViewById(R.id.SwitcherDasInfosDoGrafico);
        buttonTrocarParaViewAnalise = findViewById(R.id.ButtonTrocarParaViewAnalise);
        buttonTrocarParaViewDados = findViewById(R.id.buttonTrocarParaViewDados);
        GrupoDados = findViewById(R.id.GrupoDados);
        GrupoAnalise = findViewById(R.id.GrupoAnalise);
        textRespostaIA = findViewById(R.id.textRespostaIA);
        textNaoHaDadosGrafico = findViewById(R.id.textNaoHaDadosGrafico);
        buttonAnalisarGrafico = findViewById(R.id.buttonAnalisarGrafico);
        FundoGradiente = findViewById(R.id.FundoGradiente);
        GrupoIcones = findViewById(R.id.GrupoIcones);
        TelaInicial = findViewById(R.id.TelaInicial);

        if (primeiraExecucao) {

            deixarInvisivelGrupoIcones(GrupoIcones);
            deixarInvisivelGrupoIcones(TelaInicial);

            AnimacaoDiminuirGradiente();

            primeiraExecucao = false;
        }

        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);
        clienteAdapter = new ClienteAdapter(TelaInicial.this, new ArrayList<>());

        clienteViewModel.getListaClientes().observe(this, new Observer<List<Cliente>>() {
            @Override
            public void onChanged(List<Cliente> clientes) {
                // Atualize o Adapter com a nova lista de serviços
                clienteAdapter.setCliente(clientes);
            }
        });

        categoriaViewModel = new ViewModelProvider(this).get(CategoriaViewModel.class);
        categoriaAdapter = new CategoriaAdapter(TelaInicial.this, new ArrayList<>());

        categoriaViewModel.getListaCategorias().observe(this, new Observer<List<Categoria>>() {
            @Override
            public void onChanged(List<Categoria> categorias) {
                if (categoriaAdapter == null) {
                    categoriaAdapter = new CategoriaAdapter(TelaInicial.this, categorias);
                } else {
                    categoriaAdapter.setCategoria(categorias);
                }
            }
        });

        servicoViewModel = new ViewModelProvider(this).get(ServicoViewModel.class);
        servicoAdapter = new ServicoAdapter(TelaInicial.this, new ArrayList<>(), categoriaViewModel, clienteViewModel);
        QuadradoListaDados = findViewById(R.id.QuadradoListaDados);
        QuadradoListaDados.setLayoutManager(new LinearLayoutManager(this));
        QuadradoListaDados.setAdapter(servicoAdapter);


        servicoViewModel.getListaServicoInicio().observe(this, new Observer<List<Servico>>() {
            @Override
            public void onChanged(List<Servico> servicos) {
                Log.d("TelaInicial", "Serviços atualizados: " + servicos);

                // Atualize o Adapter com a nova lista de serviços
                servicoAdapter.setServico(servicos);
                QuadradoListaDados.setAdapter(servicoAdapter);

                // Atualiza a variável com base na quantidade de serviços
                temDadosServico = servicos != null && !servicos.isEmpty();
                if(temDadosServico)
                {
                    grafico.setVisibility(View.VISIBLE);
                    textNaoHaDadosGrafico.setVisibility(View.INVISIBLE);
                    setaBaixo.setEnabled(true);
                    QuadradoListaDados.postDelayed(() -> gerarDadosGrafico(), 1);
                } else {
                    grafico.setVisibility(View.INVISIBLE);
                    textNaoHaDadosGrafico.setVisibility(View.VISIBLE);
                    setaBaixo.setEnabled(false);
                }
                alterarTextGrafico();
                QuadradoListaDados.requestLayout();

            }
        });

        buttonFiltrar = findViewById(R.id.buttonFiltrar);

        buttonGerenciamentoClientes = findViewById(R.id.buttonGerenciamentoClientes);
        buttonGerenciamentoClientes.setOnClickListener(this);
        buttonGerenciamentoServico = findViewById(R.id.buttonGerenciamentoServico);
        buttonGerenciamentoServico.setOnClickListener(this);
        setaBaixo.setOnClickListener(this);
        buttonFiltrar.setOnClickListener(this);
        buttonTrocarParaViewAnalise.setOnClickListener(this);
        buttonTrocarParaViewDados.setOnClickListener(this);
        buttonGerenciamentoCategoria = findViewById(R.id.buttonGerenciamentoCategoria);
        buttonGerenciamentoCategoria.setOnClickListener(this);
        buttonAnalisarGrafico.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Limpa observadores anteriores
        categoriaViewModel.getListaCategorias().removeObservers(this);
        servicoViewModel.getListaServicoInicio().removeObservers(this);

        // Re-define as observações
        categoriaViewModel.getListaCategorias().observe(this, new Observer<List<Categoria>>() {
            @Override
            public void onChanged(List<Categoria> categorias) {
                if (categorias !=   null && !categorias.equals(categoriaAdapter.getCategoria())) {
                    categoriaAdapter.setCategoria(categorias);
                    QuadradoListaDados.setAdapter(servicoAdapter);
                }
            }
        });
//
//
        servicoViewModel.getListaServicoInicio().observe(this, new Observer<List<Servico>>() {
            @Override
            public void onChanged(List<Servico> servicos) {
                if (servicos != null && !servicos.equals(servicoAdapter.getServicos())) {
                    Log.d("TelaInicial", "Serviços atualizados: " + servicos);
                    servicoAdapter.setServico(servicos);
                    QuadradoListaDados.setAdapter(servicoAdapter);
                    // Atualiza a variável com base na quantidade de serviços
                    temDadosServico = servicos != null && !servicos.isEmpty();
                    // Aguarda a interface atualizar antes de gerar o gráfico
                    if(temDadosServico)
                    {
                        grafico.setVisibility(View.VISIBLE);

                        textNaoHaDadosGrafico = findViewById(R.id.textNaoHaDadosGrafico);
                        textNaoHaDadosGrafico.setVisibility(View.INVISIBLE);

                        setaBaixo = findViewById(R.id.setaBaixo);
                        setaBaixo.setEnabled(true);

                        QuadradoListaDados.postDelayed(() -> gerarDadosGrafico(), 100);
                    } else {
                        grafico.setVisibility(View.INVISIBLE);

                        textNaoHaDadosGrafico = findViewById(R.id.textNaoHaDadosGrafico);
                        textNaoHaDadosGrafico.setVisibility(View.VISIBLE);

                        setaBaixo = findViewById(R.id.setaBaixo);
                        setaBaixo.setEnabled(false);
                        animarQuadradoLista(false);
                    }
                    alterarTextGrafico();
                }
            }
        });
//
        categoriaViewModel.carregarCategorias();
        clienteViewModel.carregarClientes();
        servicoViewModel.carregarServicosInicio();

    }

    private void gerarDadosGrafico() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        Map<String, Float> tipoServicoMap = new HashMap<>();
        List<Servico> listaServicos = servicoAdapter.getServicos();

        totalRecebido = 0f;
        clienteMaiorCompra = "";
        clienteMenorCompra = "";
        maiorVenda = 0f;
        menorVenda = Float.MAX_VALUE;
        dataMaisVendas = "";
        dataMenosVendas = "";
        maiorVendaDia = 0f;
        menorVendaDia = Float.MAX_VALUE;

        SharedPreferences prefs = getApplication().getSharedPreferences("PreferenciasApp", getApplication().MODE_PRIVATE);

        String ordemDeData = prefs.getString("ordemDeData", "");
        String ordemAteData = prefs.getString("ordemAteData", "");
        String ordemEstadoInicio = prefs.getString("ordemEstadoInicio", "Todos");
        String ordemCategoriaOuCliente = prefs.getString("ordemCategoriaOuCliente", "Categorias");

        if (listaServicos != null && !listaServicos.isEmpty()) {
            Map<String, Float> clienteMap = new HashMap<>();
            Map<String, Float> diaMap = new HashMap<>();

            for (Servico servico : listaServicos) {
                Float valor = servico.getValor().floatValue();
                String estadoPagamento = servico.getEstado() == 1 ? " (Pago)" : " (Não Pago)";
                String cliente = getNomeClienteById(servico.getIdCliente());
                String dia = "";
                if(ordemEstadoInicio.equals("Todos"))
                {
                    dia = servico.getDataAceiteServico();
                } else if (ordemEstadoInicio.equals("Pago")){
                    dia = servico.getDataPagamento();
                } else if (ordemEstadoInicio.equals("NaoPago")){
                    dia = servico.getDataPagamento();
                }

                totalRecebido += valor;
                clienteMap.put(cliente, clienteMap.getOrDefault(cliente, 0f) + valor);
                diaMap.put(dia, diaMap.getOrDefault(dia, 0f) + valor);

                String chaveAgrupamento;
                if ("Clientes".equals(ordemCategoriaOuCliente)) {
                    chaveAgrupamento = cliente + estadoPagamento;
                } else {
                    int tipoServicoId = servico.getTipoServico();
                    chaveAgrupamento = getNomeCategoriaById(tipoServicoId) + estadoPagamento;
                }

                tipoServicoMap.put(chaveAgrupamento, tipoServicoMap.getOrDefault(chaveAgrupamento, 0f) + valor);
            }

            for (Map.Entry<String, Float> entry : clienteMap.entrySet()) {
                if (entry.getValue() > maiorVenda) {
                    maiorVenda = entry.getValue();
                    clienteMaiorCompra = entry.getKey();
                }
                if (entry.getValue() < menorVenda) {
                    menorVenda = entry.getValue();
                    clienteMenorCompra = entry.getKey();
                }
            }

            for (Map.Entry<String, Float> entry : diaMap.entrySet()) {
                if (entry.getValue() > maiorVendaDia) {
                    maiorVendaDia = entry.getValue();
                    dataMaisVendas = entry.getKey();
                }
                if (entry.getValue() < menorVendaDia) {
                    menorVendaDia = entry.getValue();
                    dataMenosVendas = entry.getKey();
                }
            }

            for (Map.Entry<String, Float> entry : tipoServicoMap.entrySet()) {
                entries.add(new PieEntry(entry.getValue(), entry.getKey()));
            }

            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            dataSet.setValueTextSize(12f);
            PieData data = new PieData(dataSet);

            configurarGrafico(data);
            grafico.invalidate();
        } else {
            Log.d("tag", "Lista de serviços está vazia!");
        }


        if (ordemEstadoInicio.equals("Todos")) {
            Recebimento.setText("Total à Receber e Recebido");
        } else if (ordemEstadoInicio.equals("NaoPago")) {
            Recebimento.setText("Total á Receber");
        } else if (ordemEstadoInicio.equals("Pago")) {
            Recebimento.setText("Total Recebido");
        }

        if (!ordemDeData.isEmpty() || !ordemAteData.isEmpty()) {
            periodo.setText("Período: " + ordemDeData + " até " + ordemAteData);
        } else if(ordemDeData.isEmpty() && ordemAteData.isEmpty()){
            periodo.setText("Período Total");
            ordemDeData = "Todo o Periodo";
            ordemAteData = "Todo o Periodo";
        }

        analiseTexto = "Aqui estão os resultados da análise do gráfico:\n";
        analiseTexto += "Filtros utilizados:\n";
        analiseTexto += "Período: " + ordemDeData + " até " + ordemAteData + "\n";
        analiseTexto += "Estado: " + ordemEstadoInicio + "\n";
        analiseTexto += "Agrupamento: " + (ordemCategoriaOuCliente.equals("Clientes") ? "Por Cliente" : "Por Categoria") + "\n";
        analiseTexto += "Total a receber: " + totalRecebido + "\n";
        analiseTexto += "Cliente com maior compra: " + clienteMaiorCompra + " - " + maiorVenda + "\n";
        analiseTexto += "Cliente com menor compra: " + clienteMenorCompra + " - " + menorVenda + "\n";
        analiseTexto += "Data com maior somatório de vendas: " + dataMaisVendas + " - " + maiorVendaDia + "\n";
        analiseTexto += "Data com menor somatório de vendas: " + dataMenosVendas + " - " + menorVendaDia + "\n";
    }


    private void configurarGrafico(PieData data) {

        Legend l = grafico.getLegend();
        l.setEnabled(true);
        l.setOrientation(Legend.LegendOrientation.VERTICAL); // Deixa as legendas em formato vertical
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER); // Centraliza verticalmente
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT); // Move para o lado direito
        l.setDrawInside(false); // Evita que a legenda fique sobre o gráfico
        l.setTextColor(Color.BLACK);
        l.setTextSize(14f);

        // Configurações do gráfico
        grafico.setData(data);
        grafico.getDescription().setEnabled(false);
        grafico.setDrawEntryLabels(false);
        grafico.setCenterTextColor(Color.WHITE);
        grafico.setDrawHoleEnabled(true);
        grafico.setHoleRadius(50f); // Tamanho do buraco central
        grafico.setHoleColor(Color.TRANSPARENT);

    }


    private void animarGrafico() {
        // Animação suave para preencher o gráfico em 2 segundos
        grafico.animateXY(1000, 1000, Easing.Linear); // Animação em ambos os eixos (X e Y) com duração de 2 segundos e interpolação linear
    }

    private void alterarTextGrafico()
    {
        SharedPreferences prefs = getApplication().getSharedPreferences("PreferenciasApp", getApplication().MODE_PRIVATE);
        String ordemDeData = prefs.getString("ordemDeData", "");
        String ordemAteData = prefs.getString("ordemAteData", "");
        String ordemEstadoInicio = prefs.getString("ordemEstadoInicio", "Todos");
        String ordemCategoriaOuCliente = prefs.getString("ordemCategoriaOuCliente", "Categorias");
        if(ordemEstadoInicio.equals("Todos"))
        {
            Recebimento.setText("Total à Receber e Recebido");
        } else if (ordemEstadoInicio.equals("NaoPago")){
            Recebimento.setText("Total á Receber");
        } else if (ordemEstadoInicio.equals("Pago")){
            Recebimento.setText("Total Recebido");
        }

        if(!ordemDeData.equals("")|| !ordemAteData.equals(""))
        {
            periodo.setText("Periodo: " + ordemDeData + " até " + ordemAteData);
        }

        if(ordemDeData.equals("") && ordemAteData.equals(""))
        {
            periodo.setText("Periodo Total");
        }

    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonGerenciamentoClientes) {
            Intent intent = new Intent(TelaInicial.this, TelaGerenciamento.class);
            intent.putExtra("botao_apertado", "cliente");
            startActivity(intent); // Inicia a nova Activity
        }
        if (view.getId() == R.id.buttonGerenciamentoCategoria) {
            Intent intent = new Intent(TelaInicial.this, TelaGerenciamento.class);
            intent.putExtra("botao_apertado", "categoria");
            startActivity(intent); // Inicia a nova Activity
        }
        else if (view.getId() == R.id.buttonGerenciamentoServico) {
            Intent intent = new Intent(TelaInicial.this, TelaGerenciamento.class);
            intent.putExtra("botao_apertado", "servico");
            startActivity(intent); // Inicia a nova Activity
        }
        else if (view.getId() == R.id.setaBaixo) {
            if (SwitcherDasInfosDoGrafico.getVisibility() == View.VISIBLE) {
                animarQuadradoLista(false);
                verificarPosicaoDoBotao();
            } else if (SwitcherDasInfosDoGrafico.getVisibility() == View.GONE) {
                animarQuadradoLista(true);
                verificarPosicaoDoBotao();
            }
        } else if (view.getId() == R.id.ButtonTrocarParaViewAnalise) {
            trocarParaMenuAnalise();
        } else if (view.getId() == R.id.buttonTrocarParaViewDados) {
            trocarParaMenuDados();
        } else if (view.getId() == R.id.buttonAnalisarGrafico){
            analisarDadosIA();
        } else if (view.getId() == R.id.buttonFiltrar){
            TelaFiltrarServicosInicial fragment = new TelaFiltrarServicosInicial();
            Bundle bundle = new Bundle();
            bundle.putString("veioDe-", "servico");
            fragment.setArguments(bundle);
            fragment.show(getSupportFragmentManager(), "BottomSheetDialog");
        }

    }

    private void trocarParaMenuAnalise() {
        buttonTrocarParaViewDados.setEnabled(true);
        buttonTrocarParaViewAnalise.setEnabled(false);

        // Animação de saída (da view atual para a esquerda)
        Animation slideOutLeft = new TranslateAnimation(0, -1000, 0, 0); // Move para a esquerda
        slideOutLeft.setDuration(400); // Duração aumentada para 400ms para suavidade
        slideOutLeft.setInterpolator(new AccelerateDecelerateInterpolator()); // Suavização do movimento
        slideOutLeft.setFillAfter(true);

        // Animação de entrada (da direita para a posição central)
        Animation slideInRight = new TranslateAnimation(1000, 0, 0, 0); // Move da direita para o centro
        slideInRight.setDuration(400); // Mesma duração para consistência
        slideInRight.setInterpolator(new AccelerateDecelerateInterpolator()); // Suavização do movimento
        slideInRight.setFillAfter(true);

        // Aplicando as animações no ViewSwitcher
        SwitcherDasInfosDoGrafico.setOutAnimation(slideOutLeft);
        SwitcherDasInfosDoGrafico.setInAnimation(slideInRight);

        // Alternando para a próxima view
        SwitcherDasInfosDoGrafico.showNext();
        GrupoDados.setVisibility(View.GONE); // Esconde o grupo de dados após a transição
    }

    private void trocarParaMenuDados() {
        buttonTrocarParaViewDados.setEnabled(false);
        buttonTrocarParaViewAnalise.setEnabled(true);

        // Animação de saída (da view atual para a direita)
        Animation slideOutRight = new TranslateAnimation(0, 1000, 0, 0); // Move para a direita
        slideOutRight.setDuration(400); // Duração aumentada para 400ms
        slideOutRight.setInterpolator(new AccelerateDecelerateInterpolator()); // Suavização
        slideOutRight.setFillAfter(true);

        // Animação de entrada (da esquerda para a posição central)
        Animation slideInLeft = new TranslateAnimation(-1000, 0, 0, 0); // Move da esquerda para o centro
        slideInLeft.setDuration(400);
        slideInLeft.setInterpolator(new AccelerateDecelerateInterpolator());
        slideInLeft.setFillAfter(true);

        // Aplicando as animações no ViewSwitcher
        SwitcherDasInfosDoGrafico.setOutAnimation(slideOutRight);
        SwitcherDasInfosDoGrafico.setInAnimation(slideInLeft);

        // Alternando para a view anterior
        SwitcherDasInfosDoGrafico.showNext();
        GrupoAnalise.setVisibility(View.GONE); // Esconde o grupo de análise após a transição
    }

    private void animarQuadradoLista(boolean expandir) {
        int start = expandir ? -500 : 0;
        int end = expandir ? 0 : -500;

        // Configura o animador
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(400); // Aumentei levemente a duração para mais suavidade (500ms)
        animator.setInterpolator(new AccelerateDecelerateInterpolator()); // Suavização no movimento

        animator.addUpdateListener(animation -> {
            int topMargin = (int) animation.getAnimatedValue();

            // Atualiza as margens do layout
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) SwitcherDasInfosDoGrafico.getLayoutParams();
            params.topMargin = topMargin;
            SwitcherDasInfosDoGrafico.setLayoutParams(params);

            // Ajusta a altura conforme o valor da animação
            int altura = 500 + topMargin; // Ajuste a altura conforme necessário
            SwitcherDasInfosDoGrafico.getLayoutParams().height = Math.max(altura, 0); // Garante altura mínima de 0
            SwitcherDasInfosDoGrafico.requestLayout();
        });

        // Listener para lidar com a visibilidade ao final da animação
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!expandir) {
                    SwitcherDasInfosDoGrafico.setVisibility(View.GONE); // Esconde o componente após a animação de retração
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                if (expandir) {
                    SwitcherDasInfosDoGrafico.setVisibility(View.VISIBLE); // Mostra o componente antes de expandir
                }
            }
        });

        // Inicia a animação
        animator.start();
    }

    private void deixarInvisivelGrupoIcones(View vaiSerInvisivel) {
        vaiSerInvisivel.setVisibility(View.INVISIBLE);
    }

    private void deixarVisivelGrupoIcones(View vaiSerVisivel) {
        grafico.setVisibility(View.INVISIBLE);
        // Definindo a visibilidade como visível antes da animação começar
        vaiSerVisivel.setVisibility(View.VISIBLE);

        // Configurando a animação de fade-in (transparência)
        ObjectAnimator animator = ObjectAnimator.ofFloat(vaiSerVisivel, "alpha", 0f, 1f);
        animator.setDuration(500); // Duração de 400ms
        animator.setInterpolator(new AccelerateDecelerateInterpolator()); // Suavização
        animator.start(); // Inicia a animação
    }

    private void AnimaçãoSubirIconesInicio() {
        // Animação para mover o GrupoIcones de 1000 pixels abaixo até a posição inicial
        ObjectAnimator animator = ObjectAnimator.ofFloat(GrupoIcones, "translationY", 1000f, 0f);
        animator.setDuration(600); // Duração de 500ms
        animator.setInterpolator(new AccelerateDecelerateInterpolator()); // Adiciona um efeito elástico ao final
        animator.start(); // Inicia a animação

        // Chamar animação do gráfico após um pequeno atraso para sincronia
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(temDadosServico)
                {
                    grafico.setVisibility(View.VISIBLE);
                    textNaoHaDadosGrafico.setVisibility(View.INVISIBLE);
                    setaBaixo.setEnabled(true);
                    animarGrafico();
                } else {
                    textNaoHaDadosGrafico.setVisibility(View.VISIBLE);
                    setaBaixo.setEnabled(false);
                }
            }
        });
    }


    private void AnimacaoDiminuirGradiente() {
        // Armazena os tamanhos originais do FundoGradiente
        getOriginalWidthGradiente = FundoGradiente.getLayoutParams().width;
        getOriginalHeightGradiente = FundoGradiente.getLayoutParams().height;

        // Medir o tamanho do FundoGradiente com altura WRAP_CONTENT
        FundoGradiente.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        FundoGradiente.requestLayout();
        FundoGradiente.post(() -> {
            // Obtém a altura real de WRAP_CONTENT após a medida
            int wrapContentHeight = FundoGradiente.getHeight();

            // Define o layout inicial com largura total e altura wrap_content
            FundoGradiente.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_PARENT;
            FundoGradiente.getLayoutParams().height = wrapContentHeight;
            FundoGradiente.setElevation(100); // Inicia com uma elevação alta
            FundoGradiente.requestLayout();

            // Animação para ajustar a largura do FundoGradiente
            ValueAnimator widthAnimator = ValueAnimator.ofInt(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    getOriginalWidthGradiente
            );
            widthAnimator.setInterpolator(new AccelerateDecelerateInterpolator()); // Suaviza
            widthAnimator.setDuration(700); // Duração de 400ms
            widthAnimator.addUpdateListener(animation -> {
                int animatedValue = (int) animation.getAnimatedValue();
                FundoGradiente.getLayoutParams().width = animatedValue;
                FundoGradiente.requestLayout();
            });

            // Animação para ajustar a altura do FundoGradiente
            ValueAnimator heightAnimator = ValueAnimator.ofInt(
                    wrapContentHeight,
                    getOriginalHeightGradiente
            );
            heightAnimator.setInterpolator(new AccelerateDecelerateInterpolator()); // Suaviza
            heightAnimator.setDuration(700); // Duração de 400ms
            heightAnimator.addUpdateListener(animation -> {
                int animatedValue = (int) animation.getAnimatedValue();
                FundoGradiente.getLayoutParams().height = animatedValue;
                FundoGradiente.requestLayout();
            });

            // Animação para reduzir a elevação do FundoGradiente
            ObjectAnimator elevationAnimator = ObjectAnimator.ofFloat(
                    FundoGradiente,
                    "elevation",
                    100f, // Inicia com elevação alta
                    0f    // Termina com elevação zero
            );
            elevationAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            elevationAnimator.setDuration(700); // Duração de 400ms

            // Combina todas as animações em um AnimatorSet para sincronização
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(widthAnimator, heightAnimator, elevationAnimator);
            animatorSet.start();

            // Após as animações, iniciar a animação dos ícones
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    deixarVisivelGrupoIcones(GrupoIcones);
                    deixarVisivelGrupoIcones(TelaInicial);
                    AnimaçãoSubirIconesInicio(); // Inicia a animação dos ícones
                }
            });
        });
    }

    private void analisarDadosIA() {
        // Create GenerativeModel instance
        GenerativeModel gm = new GenerativeModel(
                "gemini-2.0-flash-exp",
                BuildConfig.GEMINI_API_KEY
        );
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText(analiseTexto)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        // Retry logic with a maximum of 3 attempts
        retryRequest(response, 3);
    }

    private void retryRequest(ListenableFuture<GenerateContentResponse> response, int maxRetries) {
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            private int attempts = 0;

            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                new Handler(Looper.getMainLooper()).post(() -> animateTextTyping(resultText, textRespostaIA));
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                if (attempts < maxRetries) {
                    attempts++;
                    Log.w("IA Error", "Retrying attempt " + attempts + "...");
                    // Retry the request after a delay (e.g., 2 seconds)
                    Executors.newSingleThreadExecutor().submit(() -> {
                        try {
                            Thread.sleep(2000); // Delay before retry
                            retryRequest(response, maxRetries);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    Log.e("IA Error", "Failed after " + maxRetries + " attempts.");
                    // Optionally, show a user-friendly error message or handle accordingly
                }
            }
        }, Executors.newSingleThreadExecutor());
    }


    private void animateTextTyping(String text, TextView textView) {
        final Handler handler = new Handler();
        final int delay = 40; // Milissegundos entre cada letra
        final StringBuilder displayedText = new StringBuilder();

        final char[] textArray = text.toCharArray();
        final int[] index = {0};

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index[0] < textArray.length) {
                    displayedText.append(textArray[index[0]]);
                    textView.setText(displayedText.toString());
                    index[0]++;

                    // Forçar o ScrollView a rolar até o final
                    ScrollView scrollView = findViewById(R.id.scrollViewGemini);
                    scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));

                    handler.postDelayed(this, delay);
                }
            }
        }, delay);
    }


    private void verificarPosicaoDoBotao() {
        // Localize os elementos
        Button buttonCategoria = findViewById(R.id.buttonGerenciamentoCategoria);
        ScrollView scrollView = findViewById(R.id.scrollView);

        // Obter posição do botão na tela
        int[] location = new int[2];
        buttonCategoria.getLocationOnScreen(location);

        // Posição Y do botão
        int botaoY = location[1];

        // Obter altura da tela
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int alturaTela = metrics.heightPixels;

        // Verificar se o botão está fora da tela
        if (botaoY + buttonCategoria.getHeight() > alturaTela) {
            // Fazer scroll para o botão ficar visível
            scrollView.post(() -> scrollView.smoothScrollTo(0, buttonCategoria.getBottom()));
        }
    }

    public void onServicoClicked(Servico servico) {
        if (getSupportFragmentManager().findFragmentByTag("BottomSheetDialog") == null)
        {
            BottomSheetDetalhesServico fragment = new BottomSheetDetalhesServico();

            long servicoId = servico.getId();
            Integer tipoServico = servico.getTipoServico();
            Integer idCliente = servico.getIdCliente();
            Double valor = servico.getValor();
            String dataAceiteServico = servico.getDataAceiteServico();
            Integer estado = servico.getEstado();
            String dataPagamento = servico.getDataPagamento();

            Bundle bundle = new Bundle();
            bundle.putLong("servicoId", servicoId);
            bundle.putInt("tipoServico", tipoServico);
            bundle.putInt("idCliente", idCliente);
            bundle.putDouble("valor", valor);
            bundle.putString("dataAceiteServico", dataAceiteServico);
            bundle.putInt("estado", estado);
            bundle.putString("dataPagamento", dataPagamento);

            // Exemplo: Você pode iniciar uma nova Activity com os detalhes do cliente
            fragment.setArguments(bundle);
            fragment.show(getSupportFragmentManager(), "BottomSheetDialog");
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
        return "Cliente desconhecido"; // Caso não encontre a categoria
    }
}
