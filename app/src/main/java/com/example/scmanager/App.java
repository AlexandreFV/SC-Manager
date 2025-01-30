package com.example.scmanager;

import android.app.Application;
import android.os.AsyncTask;

import com.example.scmanager.BancoDeDados.ControladorBancoDeDados;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Inicializa o controlador de banco de dados aqui, se necessário
        ControladorBancoDeDados.getInstance(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // Fecha o banco de dados quando a aplicação for encerrada
        ControladorBancoDeDados.onDestroy();
    }

}
