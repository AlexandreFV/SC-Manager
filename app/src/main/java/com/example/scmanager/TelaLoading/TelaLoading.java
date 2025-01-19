package com.example.scmanager.TelaLoading;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scmanager.R;
import com.example.scmanager.TelaInicial.TelaInicial;

public class TelaLoading extends AppCompatActivity {

    private ImageView LogoApp;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        // Inicializa os componentes
        LogoApp = findViewById(R.id.LogoApp);
        progressBar = findViewById(R.id.progressBar);

        // Configura a animação para o logo
        tornarLogoVisivel(LogoApp);

        // Exibe o ProgressBar por um tempo, ou até o carregamento ser concluído
        progressBar.setVisibility(View.VISIBLE);

        // Troca para a próxima tela após 2 segundos
        TrocaTela(2000);
    }

    private void tornarLogoVisivel(View view)
    {
        // Configura a animação de fade-in (transparência) para o logo
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        animator.setDuration(1000); // Duração da animação (2 segundos)
        animator.start(); // Inicia a animação
    }

    private void TrocaTela(int Tempo)
    {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Após o tempo configurado (Tempo), muda para a TelaInicial
                Intent intent = new Intent(TelaLoading.this, TelaInicial.class);
                startActivity(intent);
                // Remove animações de transição
                overridePendingTransition(0, 0);
                finish(); // Finaliza a tela de loading
            }
        }, Tempo); // Tempo de exibição da tela de loading
    }
}
