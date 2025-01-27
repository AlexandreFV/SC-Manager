package com.example.scmanager.Inicial.Tela;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.splashscreen.SplashScreen;

import com.example.scmanager.BuildConfig;
import com.example.scmanager.Gerenciamento.Tela.TelaGerenciamento;
import com.example.scmanager.R;
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
import java.util.Random;
import java.util.concurrent.Executors;


public class TelaInicial extends AppCompatActivity implements View.OnClickListener{

    private ConstraintLayout GrupoIcones;

    private View FundoGradiente;

    private ImageView LogoApp;

    private TextView TelaInicial;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Handle the splash screen transition.
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_inicial);

        grafico = findViewById(R.id.grafico);
        setaBaixo = findViewById(R.id.setaBaixo);
        SwitcherDasInfosDoGrafico = findViewById(R.id.SwitcherDasInfosDoGrafico);
        buttonTrocarParaViewAnalise = findViewById(R.id.ButtonTrocarParaViewAnalise);
        buttonTrocarParaViewDados = findViewById(R.id.buttonTrocarParaViewDados);
        GrupoDados = findViewById(R.id.GrupoDados);
        GrupoAnalise = findViewById(R.id.GrupoAnalise);
        textRespostaIA = findViewById(R.id.textRespostaIA);

        if(primeiraExecucao)
        {
            FundoGradiente = findViewById(R.id.FundoGradiente);
            GrupoIcones = findViewById(R.id.GrupoIcones);
            LogoApp = findViewById(R.id.LogoApp);
            TelaInicial = findViewById(R.id.TelaInicial);

            deixarInvisivelGrupoIcones(GrupoIcones);
            deixarInvisivelGrupoIcones(LogoApp);
            deixarInvisivelGrupoIcones(TelaInicial);

            AnimacaoDiminuirGradiente();
            gerarDadosGrafico();

            primeiraExecucao = false;
        }


        buttonGerenciamentoClientes = findViewById(R.id.buttonGerenciamentoClientes);
        buttonGerenciamentoClientes.setOnClickListener(this);
        buttonGerenciamentoServico = findViewById(R.id.buttonGerenciamentoServico);
        buttonGerenciamentoServico.setOnClickListener(this);
        setaBaixo.setOnClickListener(this);
        buttonTrocarParaViewAnalise.setOnClickListener(this);
        buttonTrocarParaViewDados.setOnClickListener(this);
        buttonGerenciamentoCategoria = findViewById(R.id.buttonGerenciamentoCategoria);
        buttonGerenciamentoCategoria.setOnClickListener(this);
    }

    private void configurarGrafico(PieData data)
    {
        // Configurações do gráfico
        grafico.setData(data);
        grafico.getDescription().setEnabled(false);
        grafico.getLegend().setEnabled(true);
        grafico.getLegend().setDrawInside(false);
        grafico.setDrawEntryLabels(false);
        grafico.setCenterTextColor(Color.WHITE);
        grafico.setDrawHoleEnabled(true);
        grafico.setHoleRadius(50f);  // Tamanho do buraco central
        grafico.setHoleColor(Color.TRANSPARENT);

        Legend l = grafico.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER); // Ajuste a vertical
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT); // Alinhar à direita
        l.setOrientation(Legend.LegendOrientation.VERTICAL); // Manter as legendas verticais
        l.setTextColor(Color.BLACK);
        l.setTextSize(14f);

    }

    private void gerarDadosGrafico() {
        // Gerando dados aleatórios para o gráfico de pizza
        ArrayList<PieEntry> entries = new ArrayList<>();
        Random random = new Random();

        // Criando 3 setores com valores aleatórios
        for (int i = 0; i < 3; i++) {
            float valorAleatorio = random.nextInt(100) + 1; // Gera um valor entre 1 e 100
            entries.add(new PieEntry(valorAleatorio, "Categoria " + (i + 1)));  // Adiciona o valor e o label
        }

        // Criando um conjunto de dados para o gráfico de pizza
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS); // Define cores aleatórias para os setores
        dataSet.setValueTextSize(12f);
        PieData data = new PieData(dataSet);

        configurarGrafico(data);
    }

    private void animarGrafico()
    {
        // Animação suave para preencher o gráfico em 2 segundos
        grafico.animateXY(1000, 1000, Easing.Linear); // Animação em ambos os eixos (X e Y) com duração de 2 segundos e interpolação linear
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
//        else if (view.getId() == R.id.buttonGerenciamentoServico) {
//            Intent intent = new Intent(TelaInicial.this, TelaGerenciamentoServico.class);
//            startActivity(intent); // Inicia a nova Activity
//        }
        else if (view.getId() == R.id.setaBaixo) {
            if (SwitcherDasInfosDoGrafico.getVisibility() == View.VISIBLE)
            {
                animarQuadradoLista(false);
            }
            else if (SwitcherDasInfosDoGrafico.getVisibility() == View.GONE)
            {
                animarQuadradoLista(true);
            }
        } else if(view.getId() == R.id.ButtonTrocarParaViewAnalise){
            trocarParaMenuAnalise();
            analisarDadosIA();
        }
        else if(view.getId() == R.id.buttonTrocarParaViewDados){
            trocarParaMenuDados();
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
        int start = expandir ? -400 : 0;
        int end = expandir ? 0 : -400;

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
            int altura = 400 + topMargin; // Ajuste a altura conforme necessário
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

    private void deixarInvisivelGrupoIcones(View vaiSerInvisivel)
    {
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
                grafico.setVisibility(View.VISIBLE);
                animarGrafico();
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
                    deixarVisivelGrupoIcones(LogoApp);
                    deixarVisivelGrupoIcones(TelaInicial);
                    AnimaçãoSubirIconesInicio(); // Inicia a animação dos ícones
                }
            });
        });
    }

    private void analisarDadosIA() {
        // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
        GenerativeModel gm = new GenerativeModel(
                /* modelName */ "gemini-1.5-flash",
                // Access your API key as a Build Configuration variable
                BuildConfig.GEMINI_API_KEY
        );
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText("Me conte uma curiosidade sobre IA")
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                // Use Handler to run on the main thread for API < 28
                new Handler(Looper.getMainLooper()).post(() -> animateTextTyping(resultText, textRespostaIA));
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, Executors.newSingleThreadExecutor()); // Substitui `getMainExecutor` por um Executor customizado
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
                    handler.postDelayed(this, delay);
                }
            }
        }, delay);
    }

}
