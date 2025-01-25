package com.example.scmanager.BancoDeDados;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CategoriaRepository {

    public static final String SQL_CREATE_CATEGORIAS =
            "CREATE TABLE Categorias (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome TEXT UNIQUE)";

    private SQLiteDatabase banco;

    public CategoriaRepository(SQLiteDatabase db){
        banco = db;
    }

    // Metodo auxiliar para verificar a existência de uma categoria pelo nome
    private boolean existeCategoriaPorNome(String nome) {
        String query = "SELECT EXISTS (SELECT 1 FROM Categorias WHERE nome = ?)";
        try (Cursor cursor = banco.rawQuery(query, new String[]{nome})) {
            return cursor != null && cursor.moveToFirst() && cursor.getInt(0) == 1;
        }
    }

    // Metodo auxiliar para verificar a existência de uma categoria pelo ID
    private boolean existeCategoriaPorId(int id) {
        String query = "SELECT EXISTS (SELECT 1 FROM Categorias WHERE id = ?)";
        try (Cursor cursor = banco.rawQuery(query, new String[]{String.valueOf(id)})) {
            return cursor != null && cursor.moveToFirst() && cursor.getLong(0) == 1;
        }
    }

    // Adicionar uma nova categoria
    public boolean AdicionarCategoria(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da categoria não pode ser vazio.");
        }

        banco.beginTransaction();
        try {
            if (existeCategoriaPorNome(nome)) {
                throw new IllegalArgumentException("Já existe uma categoria com o mesmo nome.");
            }

            ContentValues valores = new ContentValues();
            valores.put("nome", nome);

            banco.insert("Categorias", null, valores);
            banco.setTransactionSuccessful();
            return true; // Categoria adicionada com sucesso
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Falha ao adicionar a categoria
        } finally {
            banco.endTransaction();
        }
    }

    // Editar uma categoria existente
    public boolean EditarCategoria(int idCategoria, String nome) {
        if (idCategoria == 0 || nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O ID e o nome da categoria são obrigatórios.");
        }

        banco.beginTransaction();
        try {
            if (!existeCategoriaPorId(idCategoria)) {
                throw new IllegalArgumentException("Não existe uma categoria com o ID: " + idCategoria);
            }

            if (existeCategoriaPorNome(nome)) {
                throw new IllegalArgumentException("Já existe uma categoria com o mesmo nome.");
            }

            ContentValues valores = new ContentValues();
            valores.put("nome", nome);

            banco.update("Categorias", valores, "id = ?", new String[]{String.valueOf(idCategoria)});
            banco.setTransactionSuccessful();
            return true; // Categoria editada com sucesso
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Falha ao editar a categoria
        } finally {
            banco.endTransaction();
        }
    }

    // Excluir uma categoria pelo ID
    public boolean ExcluirCategoria(int id) {
        if (id == 0) {
            throw new IllegalArgumentException("O ID da categoria é obrigatório.");
        }

        banco.beginTransaction();
        try {
            if (!existeCategoriaPorId(id)) {
                throw new IllegalArgumentException("Não existe uma categoria com o ID: " + id);
            }

            banco.delete("Categorias", "id = ?", new String[]{String.valueOf(id)});
            banco.setTransactionSuccessful();
            return true; // Categoria excluída com sucesso
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Falha ao excluir a categoria
        } finally {
            banco.endTransaction();
        }
    }

}
