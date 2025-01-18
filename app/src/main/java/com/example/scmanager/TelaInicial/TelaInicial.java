package com.example.scmanager.TelaInicial;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.scmanager.TelaGerenciamentoCliente.TelaGerenciamentoCliente;
import com.example.scmanager.R;
import com.example.scmanager.TelaGerenciamentoServico.TelaGerenciamentoServico;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Random;

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

    private View QuadradoLista;

    private PieChart grafico;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_inicial);

        grafico = findViewById(R.id.grafico);
        setaBaixo = findViewById(R.id.setaBaixo);
        QuadradoLista = findViewById(R.id.QuadradoLista);

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
            finish(); //Encerra essa tela
        } else if (view.getId() == R.id.buttonGerenciamentoServico) {
            Intent intent = new Intent(TelaInicial.this, TelaGerenciamentoServico.class);
            startActivity(intent); // Inicia a nova Activity
            finish(); //Encerra essa tela
        } else if (view.getId() == R.id.setaBaixo) {
            if (QuadradoLista.getVisibility() == View.GONE) {
                QuadradoLista.setVisibility(View.VISIBLE);

                // Animação para aumentar a margem superior e altura (expandir)
                ValueAnimator animator = ValueAnimator.ofInt(-250, 0); // Vai de -100 para 0 para topMargin
                animator.setDuration(400); // Duração de 600ms
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        // Atualiza a topMargin conforme a animação
                        int topMargin = (int) animation.getAnimatedValue();
                        ViewGroup.LayoutParams params = QuadradoLista.getLayoutParams();
                        ((ViewGroup.MarginLayoutParams) params).topMargin = topMargin;
                        QuadradoLista.setLayoutParams(params); // Aplica a alteração

                        // Anima a altura de forma gradual (exemplo de aumento de altura)
                        int altura = 250 + topMargin; // Ajuste o valor conforme necessário (para expandir)
                        QuadradoLista.getLayoutParams().height = altura;
                        QuadradoLista.requestLayout(); // Força a atualização do layout
                    }
                });
                animator.start(); // Inicia a animação

            } else if (QuadradoLista.getVisibility() == View.VISIBLE) {
                // Animação para diminuir a margem superior e altura (contrair)
                ValueAnimator animator = ValueAnimator.ofInt(0, -250); // Vai de 0 para -100 para topMargin
                animator.setDuration(400); // Duração de 600ms
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        // Atualiza a topMargin conforme a animação
                        int topMargin = (int) animation.getAnimatedValue();
                        ViewGroup.LayoutParams params = QuadradoLista.getLayoutParams();
                        ((ViewGroup.MarginLayoutParams) params).topMargin = topMargin;
                        QuadradoLista.setLayoutParams(params); // Aplica a alteração

                        // Diminui a altura durante a animação (para contrair)
                        int altura = 250 + topMargin; // Ajuste o valor conforme necessário (para contrair)
                        QuadradoLista.getLayoutParams().height = altura;
                        QuadradoLista.requestLayout(); // Força a atualização do layout
                    }
                });
                animator.start(); // Inicia a animação

                // Torna a View invisível após a animação
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        QuadradoLista.setVisibility(View.GONE); // Torna invisível após a animação
                    }
                });
            }
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
        animator.setDuration(500); // Duração de 1 segundo
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
                widthAnimator.setDuration(700); // Duração de 1,5 segundos
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
                heightAnimator.setDuration(700); // Duração de 1,5 segundos
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
                elevationAnimator.setDuration(700); // Duração de 1,5 segundos

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

}
