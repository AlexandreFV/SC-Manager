package com.example.scmanager.BancoDeDados;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

    public void verServico() {
        // Realizando a consulta
        String query = "SELECT * FROM Servicos";  // Consulta para pegar todos os registros da tabela Clientes
        Cursor cursor = database.rawQuery(query, null);  // Passa o SQL e null porque não tem argumentos de parâmetros

        // Verifica se existem resultados
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Aqui você pode acessar os dados da tabela, por exemplo:
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int tipoServico = cursor.getInt(cursor.getColumnIndex("tipoServico"));

                // Imprima ou faça algo com os dados, por exemplo:
                Log.d("Servico", "ID: " + id + " id categoria: " + tipoServico);
            } while (cursor.moveToNext());  // Move para o próximo registro
        } else {
            Log.d("Servico", "Nenhum registro encontrado.");
        }

        String query2 = "SELECT * FROM Categorias";  // Consulta para pegar todos os registros da tabela Clientes
        Cursor cursor2 = database.rawQuery(query2, null);  // Passa o SQL e null porque não tem argumentos de parâmetros

        if (cursor2 != null && cursor2.moveToFirst()) {
            do {
                // Aqui você pode acessar os dados da tabela, por exemplo:
                int id = cursor2.getInt(cursor2.getColumnIndex("id"));
                String tipoServico = cursor2.getString(cursor2.getColumnIndex("nome"));

                // Imprima ou faça algo com os dados, por exemplo:
                Log.d("Servico", "ID: " + id + " nome categoria: " + tipoServico);
            } while (cursor2.moveToNext());  // Move para o próximo registro
        } else {
            Log.d("Servico", "Nenhum registro encontrado.");
        }

        // Não se esqueça de fechar o cursor após o uso
        if (cursor2 != null) {
            cursor2.close();
        }
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

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Habilita as restrições de chave estrangeira
        db.execSQL("PRAGMA foreign_keys = ON;");
    }
}
