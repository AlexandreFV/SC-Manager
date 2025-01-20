package com.example.scmanager.TelaGerenciamentoCliente;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scmanager.R;

public class TelaGerenciamentoCliente extends AppCompatActivity implements View.OnClickListener {

    private ImageButton voltarTela;
    private ImageButton imageMaisOpcoes;
    private ImageButton imageAdicionarLista;
    private ImageButton imageFiltrarLista;
    private boolean primeiraSubidaMaisOp = true;

    private float posicaoOriginalFiltrar;
    private float posicaoOriginalAdicionar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_gerenciamento_cliente);

        voltarTela = findViewById(R.id.imageVoltarTela);
        voltarTela.setOnClickListener(this);
        imageMaisOpcoes = findViewById(R.id.imageMaisOpcoes);
        imageMaisOpcoes.setOnClickListener(this);
        imageAdicionarLista = findViewById(R.id.imageAdicionarLista);
        imageAdicionarLista.setOnClickListener(this);
        imageFiltrarLista = findViewById(R.id.imageFiltrarLista);
        imageFiltrarLista.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imageVoltarTela) {
            finish();  // Finaliza a Activity atual e volta para a anterior
        } else if (view.getId() == R.id.imageMaisOpcoes) {
            if (imageFiltrarLista.getVisibility() == View.VISIBLE && imageAdicionarLista.getVisibility() == View.VISIBLE) {
                animacaoAdicionarVoltandoParaAdicionar();
            } else {
                animacaoFiltrarSaindoDeMaisOpcoes();
            }
        } else if (view.getId() == R.id.imageAdicionarLista) {
            Toast.makeText(getBaseContext(), "apertou em adicionar", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.imageFiltrarLista) {
            Toast.makeText(getBaseContext(), "apertou em filtrar", Toast.LENGTH_SHORT).show();
        }
    }

    private void animacaoFiltrarSaindoDeMaisOpcoes() {
        imageMaisOpcoes.setEnabled(false);

        // Verifica se é a primeira vez que a animação está ocorrendo
        if (primeiraSubidaMaisOp) {
            // Inicializa a posição original da imagem como a altura da imageFiltrarLista
            posicaoOriginalFiltrar = imageFiltrarLista.getY();
        }

        // Define a posição inicial da imageFiltrarLista logo abaixo da imageMaisOpcoes
        imageFiltrarLista.setY(posicaoOriginalFiltrar);

        // A posição final será 180 pixels acima de imageMaisOpcoes
        float posicaoFiltrarAcimaMaisOp = imageMaisOpcoes.getY() - 180;

        // Garante que imageFiltrarLista esteja visível e não interativa durante a animação
        imageFiltrarLista.setVisibility(View.VISIBLE);
        imageFiltrarLista.setEnabled(false);

        // Cria o animador para mover imageFiltrarLista de sua posição inicial para a posição final
        ValueAnimator animator = ValueAnimator.ofFloat(imageFiltrarLista.getY(), posicaoFiltrarAcimaMaisOp);

        // Define a duração da animação
        animator.setDuration(300); // Pode ser ajustada conforme necessário
        animator.setInterpolator(new AccelerateDecelerateInterpolator()); // Interpolação suave

        // Atualiza a posição de imageFiltrarLista durante a animação
        animator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            imageFiltrarLista.setY(animatedValue);  // Atualiza a posição durante a animação
        });

        // Ouve o final da animação para garantir que a visibilidade e interatividade da imagem estejam corretas
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // Garante que a imageFiltrarLista tenha a posição final e esteja visível
                imageFiltrarLista.setVisibility(View.VISIBLE);
                animacaoSubirAdicionarDentroFiltrar();
            }
        });

        // Inicia a animação
        animator.start();
    }


    private void animacaoFiltrarVoltandoParaMaisOpcoes() {
        imageMaisOpcoes.setEnabled(false);
        // Garantir que a imageFiltrarLista está visível e configurada corretamente
        imageFiltrarLista.setVisibility(View.VISIBLE);
        imageFiltrarLista.setEnabled(false);

        // Criar o animador para mover imageFiltrarLista de volta para sua posição original
        ValueAnimator animator = ValueAnimator.ofFloat(imageFiltrarLista.getY(), posicaoOriginalFiltrar);

        // Definir a duração da animação (lentamente descendo)
        animator.setDuration(300);  // A duração pode ser ajustada conforme necessário
        animator.setInterpolator(new AccelerateDecelerateInterpolator()); // Interpolação suave

        // Atualizar a posição de imageFiltrarLista durante a animação
        animator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            imageFiltrarLista.setY(animatedValue);
        });

        // Adicionar um ouvinte para garantir que a animação finalize e a view esteja configurada corretamente
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // Garantir que imageFiltrarLista tenha a posição correta e esteja visível
                imageFiltrarLista.setVisibility(View.INVISIBLE);
                imageMaisOpcoes.setEnabled(true);
            }
        });

        // Iniciar a animação
        animator.start();
    }

    private void animacaoSubirAdicionarDentroFiltrar() {
        imageMaisOpcoes.setEnabled(false);

        if (primeiraSubidaMaisOp) {
            // Inicializa a posição original da imagem como a altura de imageAdicionarLista
            posicaoOriginalAdicionar = imageAdicionarLista.getY(); // Corrigido: posição de imageAdicionarLista
            primeiraSubidaMaisOp = false;  // Marca que a animação já foi iniciada
        }

        // Define a posição inicial da imageAdicionarLista como a posição atual dela
        imageAdicionarLista.setY(posicaoOriginalAdicionar);

        // A posição final será 180 pixels acima de imageFiltrarLista
        float posicaoFiltrarAcimaMaisOp = imageFiltrarLista.getY() - 180; // Correção: posicione-a acima de imageFiltrarLista

        // Garante que imageAdicionarLista esteja visível e não interativa durante a animação
        imageAdicionarLista.setVisibility(View.VISIBLE);
        imageAdicionarLista.setEnabled(false);

        // Cria o animador para mover imageAdicionarLista de sua posição atual até a nova posição
        ValueAnimator animator = ValueAnimator.ofFloat(imageAdicionarLista.getY(), posicaoFiltrarAcimaMaisOp);

        // Define a duração da animação
        animator.setDuration(300); // Pode ser ajustada conforme necessário
        animator.setInterpolator(new AccelerateDecelerateInterpolator()); // Interpolação suave

        // Atualiza a posição de imageAdicionarLista durante a animação
        animator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            imageAdicionarLista.setY(animatedValue);  // Atualiza a posição durante a animação
        });

        // Ouve o final da animação para garantir que a visibilidade e interatividade da imagem estejam corretas
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // Garante que imageAdicionarLista tenha a posição final e esteja visível
                imageAdicionarLista.setVisibility(View.VISIBLE);
                imageFiltrarLista.setEnabled(true);
                imageMaisOpcoes.setEnabled(true);
                imageAdicionarLista.setEnabled(true);
            }
        });

        // Inicia a animação
        animator.start();
    }

    private void animacaoAdicionarVoltandoParaAdicionar() {
        imageMaisOpcoes.setEnabled(false);
        // Garantir que a imageFiltrarLista está visível e configurada corretamente
        imageAdicionarLista.setVisibility(View.VISIBLE);
        imageAdicionarLista.setEnabled(false);

        // Criar o animador para mover imageFiltrarLista de volta para sua posição original
        ValueAnimator animator = ValueAnimator.ofFloat(imageAdicionarLista.getY(), posicaoOriginalAdicionar);

        // Definir a duração da animação (lentamente descendo)
        animator.setDuration(300);  // A duração pode ser ajustada conforme necessário
        animator.setInterpolator(new AccelerateDecelerateInterpolator()); // Interpolação suave

        // Atualizar a posição de imageFiltrarLista durante a animação
        animator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            imageAdicionarLista.setY(animatedValue);
        });

        // Adicionar um ouvinte para garantir que a animação finalize e a view esteja configurada corretamente
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // Garantir que imageFiltrarLista tenha a posição correta e esteja visível
                imageAdicionarLista.setVisibility(View.INVISIBLE);
                animacaoFiltrarVoltandoParaMaisOpcoes();
            }
        });

        // Iniciar a animação
        animator.start();
    }

}
