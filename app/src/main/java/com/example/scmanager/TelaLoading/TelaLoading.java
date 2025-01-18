package com.example.scmanager.TelaLoading;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scmanager.R;
import com.example.scmanager.TelaInicial.TelaInicial;

import java.lang.Object;

public class TelaLoading extends AppCompatActivity {

    private ImageView LogoApp;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        LogoApp = findViewById(R.id.LogoApp);
        progressBar = findViewById(R.id.progressBar);

        tornarLogoInvisivel(LogoApp);
        tornarLogoInvisivel(progressBar);

        TrocaTela(2000);
    }

    private void tornarLogoInvisivel(View view)
    {
        // Configurando a animação de fade-in (transparência)
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        animator.setDuration(2000); // Duração de 1 segundo
        animator.start(); // Inicia a animação
    }
    private void TrocaTela(int Tempo)
    {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                Intent intent = new Intent(TelaLoading.this, TelaInicial.class);
                startActivity(intent);
                // Remove animações de transição
                overridePendingTransition(0, 0);
                finish(); // Opcional: encerra a tela de loading
            }
        }, Tempo);
    }

}
