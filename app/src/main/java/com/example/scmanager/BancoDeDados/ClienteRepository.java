package com.example.scmanager.BancoDeDados;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ClienteRepository {

    public static final String SQL_CREATE_CLIENTES =
            "CREATE TABLE Clientes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome TEXT NOT NULL, " +
                    "telefone TEXT)";

    private SQLiteDatabase banco;

    public ClienteRepository(SQLiteDatabase db){
        banco = db;
    }

    public void AdicionarCliente(String nome, String telefone) {
        banco.beginTransaction();
        try {
            String query = "SELECT EXISTS (SELECT 1 FROM Clientes WHERE nome = ? AND telefone = ?)";
            Cursor cursor = banco.rawQuery(query, new String[]{nome, telefone});

            if (cursor != null && cursor.moveToFirst() && cursor.getInt(0) == 1) {
                cursor.close();
                throw new IllegalArgumentException("Já existe um cliente com o mesmo nome e telefone.");
            }

            if (cursor != null) {
                cursor.close();
            }

            ContentValues valores = new ContentValues();
            valores.put("nome", nome);
            valores.put("telefone", telefone);

            banco.insert("Clientes", null, valores);
            banco.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            banco.endTransaction();
        }
    }

    public void EditarCliente(Integer idCliente, String nome, String telefone) {
        banco.beginTransaction();

        try {
            String queryCliente = "SELECT EXISTS (SELECT 1 FROM Clientes WHERE id = ?)";
            Cursor cursorCliente = banco.rawQuery(queryCliente, new String[]{String.valueOf(idCliente)});

            if (cursorCliente == null || !cursorCliente.moveToFirst() || cursorCliente.getInt(0) == 0) {
                cursorCliente.close();
                throw new IllegalArgumentException("Não existe um cliente com id: " + idCliente);
            }

            String query = "SELECT EXISTS (SELECT 1 FROM Clientes WHERE nome = ? AND telefone = ?)";
            Cursor cursor = banco.rawQuery(query, new String[]{nome, telefone});

            if (cursor != null && cursor.moveToFirst() && cursor.getInt(0) == 1) {
                cursor.close();
                throw new IllegalArgumentException("Já existe um cliente com o mesmo nome e telefone.");
            }

            if (cursor != null) {
                cursor.close();
            }

            ContentValues valores = new ContentValues();
            valores.put("nome", nome);
            valores.put("telefone", telefone);

            banco.update("Clientes", valores, "id = ?", new String[]{String.valueOf(idCliente)});
            banco.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            banco.endTransaction();
        }
    }

    public void ExcluirCliente(Integer id) {
        banco.beginTransaction();

        Cursor cursor = null;
        try {
            String query = "SELECT EXISTS (SELECT 1 FROM Clientes WHERE id = ?)";
            cursor = banco.rawQuery(query, new String[]{String.valueOf(id)});

            if (cursor == null || !cursor.moveToFirst() || cursor.getInt(0) == 0) {
                throw new IllegalArgumentException("Não existe esse cliente.");
            }

            banco.delete("Clientes", "id = ?", new String[]{String.valueOf(id)});
            banco.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            banco.endTransaction();
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
