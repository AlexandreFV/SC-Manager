package com.example.scmanager.TelaGerenciamentoCliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scmanager.R;
import com.example.scmanager.TelaInicial.TelaInicial;

public class TelaGerenciamentoCliente extends AppCompatActivity implements View.OnClickListener {

    private ImageButton voltarTela;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_gerenciamento_cliente);

        voltarTela = findViewById(R.id.imageVoltarTela);
        voltarTela.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imageVoltarTela)
        {
            finish();  // Finaliza a Activity atual e volta para a anterior
        }

        }
}
