package com.example.scmanager.BancoDeDados;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ControladorBancoDeDados extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SCManager.db";
    private static ControladorBancoDeDados instance;
    private SQLiteDatabase database;

    private static final String SQL_DELETE_ALL_ENTRIES =
            "DROP TABLE IF EXISTS Servicos; " +
                    "DROP TABLE IF EXISTS Categorias; " +
                    "DROP TABLE IF EXISTS Clientes;";


    public ControladorBancoDeDados(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public static synchronized ControladorBancoDeDados getInstance(Context context) {
        if (instance == null) {
            instance = new ControladorBancoDeDados(context.getApplicationContext());
        }
        return instance;
    }

    // Metodo para obter a instância do banco de dados
    @Override
    public SQLiteDatabase getWritableDatabase() {
        if (database == null || !database.isOpen()) {
            database = super.getWritableDatabase();
        }
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ClienteRepository.SQL_CREATE_CLIENTES);
        db.execSQL(CategoriaRepository.SQL_CREATE_CATEGORIAS);
        db.execSQL(ServicoRepository.SQL_CREATE_SERVICOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ALL_ENTRIES);
        onCreate(db);
    }

    // Metodo para fechar o banco de dados
    public void closeDatabase() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    public static void onDestroy() {
        if (instance != null) {
            instance.closeDatabase();
            instance = null; // Limpa a instância do banco de dados
        }
    }
}
