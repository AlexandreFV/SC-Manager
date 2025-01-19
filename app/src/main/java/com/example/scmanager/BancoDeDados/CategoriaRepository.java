package com.example.scmanager.BancoDeDados;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class CategoriaRepository {

    public static final String SQL_CREATE_CATEGORIAS =
            "CREATE TABLE Categorias (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome TEXT UNIQUE)";

    private SQLiteDatabase banco;

    public CategoriaRepository(SQLiteDatabase db){
        banco = db;
    }

    public void AdicionarCategoria(String nome) {
        banco.beginTransaction();
        try {
            String query = "SELECT EXISTS (SELECT 1 FROM Categorias WHERE nome = ?)";
            Cursor cursor = banco.rawQuery(query, new String[]{nome});

            if (cursor != null && cursor.moveToFirst() && cursor.getInt(0) == 1) {
                cursor.close();
                throw new IllegalArgumentException("Já existe uma categoria com o mesmo nome.");
            }

            if (cursor != null) {
                cursor.close();
            }

            ContentValues valores = new ContentValues();
            valores.put("nome", nome);

            banco.insert("Categorias", null, valores);
            banco.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            banco.endTransaction();

        }
    }

    public void EditarCategoria(Integer idCategoria, String nome) {
        banco.beginTransaction();
        try {
            String queryCategoria = "SELECT EXISTS (SELECT 1 FROM Categorias WHERE id = ?)";
            Cursor cursorCategoria = banco.rawQuery(queryCategoria, new String[]{String.valueOf(idCategoria)});

            if (cursorCategoria == null || !cursorCategoria.moveToFirst() || cursorCategoria.getInt(0) == 0) {
                cursorCategoria.close();
                throw new IllegalArgumentException("Não existe uma categoria com id: " + idCategoria);
            }

            String query = "SELECT EXISTS (SELECT 1 FROM Categorias WHERE nome = ?)";
            Cursor cursor = banco.rawQuery(query, new String[]{nome});

            if (cursor != null && cursor.moveToFirst() && cursor.getInt(0) == 1) {
                cursor.close();
                throw new IllegalArgumentException("Já existe uma categoria com o mesmo nome.");
            }

            if (cursor != null) {
                cursor.close();
            }

            ContentValues valores = new ContentValues();
            valores.put("nome", nome);

            banco.update("Categorias", valores, "id = ?", new String[]{String.valueOf(idCategoria)});
            banco.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            banco.endTransaction();

        }
    }

    public void ExcluirCategoria(Integer id) {
        banco.beginTransaction();

        Cursor cursor = null;
        try {
            String query = "SELECT EXISTS (SELECT 1 FROM Categorias WHERE id = ?)";
            cursor = banco.rawQuery(query, new String[]{String.valueOf(id)});

            if (cursor == null || !cursor.moveToFirst() || cursor.getInt(0) == 0) {
                throw new IllegalArgumentException("Não existe essa Categoria.");
            }

            banco.delete("Categorias", "id = ?", new String[]{String.valueOf(id)});
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

    public List<String> BuscarCategorias() {
        List<String> categorias = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT nome FROM Categorias";
            cursor = banco.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String nome = cursor.getString(cursor.getColumnIndex("nome"));
                    categorias.add(nome);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return categorias;
    }

}
