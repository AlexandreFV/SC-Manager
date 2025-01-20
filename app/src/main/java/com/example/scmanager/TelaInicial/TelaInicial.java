package com.example.scmanager.TelaInicial;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.scmanager.BuildConfig;
import com.example.scmanager.TelaGerenciamentoCliente.TelaGerenciamentoCliente;
import com.example.scmanager.R;
import com.example.scmanager.TelaGerenciamentoServico.TelaGerenciamentoServico;

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

import org.checkerframework.common.returnsreceiver.qual.This;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executor;


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
        // Animação para preencher o gráfico em 2 segundos
        grafico.animateXY(1500, 1500); // Animação em ambos os eixos (X e Y) com duração de 1 segundo cada
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonGerenciamentoClientes) {
            Intent intent = new Intent(TelaInicial.this, TelaGerenciamentoCliente.class);
            startActivity(intent); // Inicia a nova Activity
        } else if (view.getId() == R.id.buttonGerenciamentoServico) {
            Intent intent = new Intent(TelaInicial.this, TelaGerenciamentoServico.class);
            startActivity(intent); // Inicia a nova Activity
        } else if (view.getId() == R.id.setaBaixo) {
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

    private void trocarParaMenuAnalise(){
        buttonTrocarParaViewDados.setEnabled(true);
        buttonTrocarParaViewAnalise.setEnabled(false);
        // Definindo animação para a transição da view atual para a esquerda (saída)
        Animation slideOutLeft = new TranslateAnimation(0, -1000, 0, 0); // Move para a esquerda
        slideOutLeft.setDuration(300);
        slideOutLeft.setFillAfter(true);

        // Definindo animação para a transição da direita para a esquerda (entrada da próxima view)
        Animation slideInRight = new TranslateAnimation(1000, 0, 0, 0); // Move da direita para o centro
        slideInRight.setDuration(300);
        slideInRight.setFillAfter(true);

        // Aplicando as animações no ViewSwitcher
        SwitcherDasInfosDoGrafico.setOutAnimation(slideOutLeft); // Animação de saída (move para a esquerda)
        SwitcherDasInfosDoGrafico.setInAnimation(slideInRight); // Animação de entrada (move da direita para o centro)

        // Alternando para a próxima view
        SwitcherDasInfosDoGrafico.showNext();
        GrupoDados.setVisibility(View.GONE);
    }

    private void trocarParaMenuDados(){
        buttonTrocarParaViewDados.setEnabled(false);
        buttonTrocarParaViewAnalise.setEnabled(true);
        // Definindo animação para a transição da view atual para a direita (saída)
        Animation slideOutRight = new TranslateAnimation(0, 1000, 0, 0); // Move para a direita
        slideOutRight.setDuration(300);
        slideOutRight.setFillAfter(true);

        // Definindo animação para a transição da esquerda para a direita (entrada da view anterior)
        Animation slideInLeft = new TranslateAnimation(-1000, 0, 0, 0); // Move da esquerda para o centro
        slideInLeft.setDuration(300);
        slideInLeft.setFillAfter(true);

        // Aplicando as animações no ViewSwitcher
        SwitcherDasInfosDoGrafico.setOutAnimation(slideOutRight); // Animação de saída (move para a direita)
        SwitcherDasInfosDoGrafico.setInAnimation(slideInLeft); // Animação de entrada (move da esquerda para o centro)

        // Alternando para a view anterior
        SwitcherDasInfosDoGrafico.showNext();
        GrupoAnalise.setVisibility(View.GONE);
    }

    private void animarQuadradoLista(boolean expandir) {
        int start = expandir ? -400 : 0;
        int end = expandir ? 0 : -400;

        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(400);
        animator.addUpdateListener(animation -> {
            int topMargin = (int) animation.getAnimatedValue();
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) SwitcherDasInfosDoGrafico.getLayoutParams();
            params.topMargin = topMargin;
            SwitcherDasInfosDoGrafico.setLayoutParams(params);

            int altura = 400 + topMargin; // Ajustar conforme necessário
            SwitcherDasInfosDoGrafico.getLayoutParams().height = altura;
            SwitcherDasInfosDoGrafico.requestLayout();
        });
        animator.start();

        if (!expandir) {
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    SwitcherDasInfosDoGrafico.setVisibility(View.GONE);
                }
            });
        } else {
            SwitcherDasInfosDoGrafico.setVisibility(View.VISIBLE);
        }
    }

    private void deixarInvisivelGrupoIcones(View vaiSerInvisivel)
    {
        vaiSerInvisivel.setVisibility(View.INVISIBLE);
    }

    private void deixarVisivelGrupoIcones(View vaiSerVisivel)
    {
        // Definindo a visibilidade como visível antes da animação começar
        vaiSerVisivel.setVisibility(View.VISIBLE);

        // Configurando a animação de fade-in (transparência)
        ObjectAnimator animator = ObjectAnimator.ofFloat(vaiSerVisivel, "alpha", 0f, 1f);
        animator.setDuration(400); // Duração de 1 segundo
        animator.start(); // Inicia a animação
    }

    private void AnimaçãoSubirIconesInicio() {
        // Animação para mover o GrupoIcones de 1000 pixels abaixo até a posição inicial
        ObjectAnimator animator = ObjectAnimator.ofFloat(GrupoIcones, "translationY", 1000f, 0f);
        animator.setDuration(500); // Duração de 2 segundos
        animator.start(); // Inicia a animação
        animarGrafico();
    }

    private void AnimacaoDiminuirGradiente() {
        // Armazena os tamanhos originais do FundoGradiente
        getOriginalWidthGradiente = FundoGradiente.getLayoutParams().width;
        getOriginalHeightGradiente = FundoGradiente.getLayoutParams().height;

        // Medir o tamanho do FundoGradiente com altura WRAP_CONTENT
        FundoGradiente.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        FundoGradiente.requestLayout();
        FundoGradiente.post(new Runnable() {
            @Override
            public void run() {
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
                widthAnimator.setDuration(400); // Duração de 1,5 segundos
                widthAnimator.addUpdateListener(animation -> {
                    int animatedValue = (int) animation.getAnimatedValue();
                    FundoGradiente.getLayoutParams().width = animatedValue;
                    FundoGradiente.requestLayout(); // Atualiza o layout
                });

                // Animação para ajustar a altura do FundoGradiente
                ValueAnimator heightAnimator = ValueAnimator.ofInt(
                        wrapContentHeight,
                        getOriginalHeightGradiente
                );
                heightAnimator.setDuration(400); // Duração de 1,5 segundos
                heightAnimator.addUpdateListener(animation -> {
                    int animatedValue = (int) animation.getAnimatedValue();
                    FundoGradiente.getLayoutParams().height = animatedValue;
                    FundoGradiente.requestLayout(); // Atualiza o layout
                });

                // Animação para reduzir a elevação do FundoGradiente (diminuir sobreposição)
                ObjectAnimator elevationAnimator = ObjectAnimator.ofFloat(
                        FundoGradiente,
                        "elevation",
                        100f, // Inicia com elevação alta
                        0f    // Termina com elevação zero
                );
                elevationAnimator.setDuration(400); // Duração de 1,5 segundos

                // Inicia todas as animações simultaneamente
                widthAnimator.start();
                heightAnimator.start();
                elevationAnimator.start();
                // Quando a animação terminar, iniciar a animação dos ícones
                elevationAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        deixarVisivelGrupoIcones(GrupoIcones);
                        deixarVisivelGrupoIcones(LogoApp);
                        deixarVisivelGrupoIcones(TelaInicial);
                        AnimaçãoSubirIconesInicio(); // Inicia a animação dos ícones
                    }
                });
            }
        });
    }

    private void analisarDadosIA()
    {
        // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
        GenerativeModel gm = new GenerativeModel(/* modelName */ "gemini-1.5-flash",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
                BuildConfig.GEMINI_API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText("Me conte uma curiosidade sobre IA")
                .build();


                ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                runOnUiThread(() -> animateTextTyping(resultText, textRespostaIA));
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, this.getMainExecutor());
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
