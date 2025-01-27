package com.example.scmanager.BancoDeDados;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;

import com.example.scmanager.Gerenciamento.Objetos.Categoria;
import com.example.scmanager.Gerenciamento.Objetos.Cliente;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoriaRepository {

    public static final String SQL_CREATE_CATEGORIAS =
            "CREATE TABLE Categorias (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome TEXT UNIQUE)";

    private SQLiteDatabase banco;
    private final ExecutorService executor;

    public CategoriaRepository(Context context) {
        ControladorBancoDeDados db = ControladorBancoDeDados.getInstance(context);
        banco = db.getWritableDatabase();
        executor = Executors.newSingleThreadExecutor(); // Executor simples para operações assíncronas
    }

    // Adiciona uma categoria de forma assíncrona
    public void AdicionarCategoriaAsync(String nome, CategoriaCallback callback) {
        executor.submit(() -> {
            if (nome == null || nome.trim().isEmpty()) {
                throw new IllegalArgumentException("O nome da categoria é obrigatório.");
            }

            banco.beginTransaction();
            Cursor cursor = null;

            try {
                // Verifica se já existe uma categoria com o mesmo nome
                String query = "SELECT EXISTS (SELECT 1 FROM Categorias WHERE nome = ?)";
                cursor = banco.rawQuery(query, new String[]{nome});

                if (cursor.moveToFirst() && cursor.getInt(0) == 1) {
                    callback.onResult(-1); // Categoria já existe
                    return;
                }

                ContentValues valores = new ContentValues();
                valores.put("nome", nome);
                long result = banco.insert("Categorias", null, valores);

                if (result == -1) {
                    throw new Exception("Erro ao inserir categoria.");
                }

                banco.setTransactionSuccessful();
                callback.onResult(result); // Retorna o ID da categoria inserida
            } catch (Exception e) {
                e.printStackTrace();
                callback.onResult(-1); // Falha na inserção
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                banco.endTransaction();
            }
        });
    }

    // Edita uma categoria de forma assíncrona
    public void EditarCategoriaAsync(int idCategoria, String nome, CategoriaCallback callback) {
        executor.submit(() -> {
            if (idCategoria == 0 || nome == null || nome.trim().isEmpty()) {
                throw new IllegalArgumentException("ID e nome são obrigatórios.");
            }

            banco.beginTransaction();
            Cursor cursorCategoria = null;
            Cursor cursorDuplicado = null;

            try {
                // Verifica se a categoria com o ID informado existe
                String queryCategoria = "SELECT EXISTS (SELECT 1 FROM Categorias WHERE id = ?)";
                cursorCategoria = banco.rawQuery(queryCategoria, new String[]{String.valueOf(idCategoria)});

                if (cursorCategoria.moveToFirst() && cursorCategoria.getInt(0) == 0) {
                    callback.onResult(0); // Categoria não encontrada
                    return;
                }

                // Verifica se já existe uma categoria com o mesmo nome, exceto a atual
                String queryDuplicado = "SELECT EXISTS (SELECT 1 FROM Categorias WHERE nome = ? AND id != ?)";
                cursorDuplicado = banco.rawQuery(queryDuplicado, new String[]{nome, String.valueOf(idCategoria)});

                if (cursorDuplicado.moveToFirst() && cursorDuplicado.getInt(0) == 1) {
                    callback.onResult(0); // Categoria duplicada
                    return;
                }

                // Atualiza os dados da categoria
                ContentValues valores = new ContentValues();
                valores.put("nome", nome);
                int linhasAfetadas = banco.update("Categorias", valores, "id = ?", new String[]{String.valueOf(idCategoria)});

                if (linhasAfetadas > 0) {
                    banco.setTransactionSuccessful();
                    callback.onResult(1); // Sucesso
                } else {
                    callback.onResult(0); // Falha na atualização
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onResult(0); // Falha
            } finally {
                if (cursorCategoria != null) cursorCategoria.close();
                if (cursorDuplicado != null) cursorDuplicado.close();
                banco.endTransaction();
            }
        });
    }

    // Carrega categorias de forma assíncrona
    public void CarregarCategoriasAsync(CategoriaListCallback callback) {
        executor.submit(() -> {
            List<Categoria> categorias = new ArrayList<>();
            String query = "SELECT id, nome FROM Categorias";

            try (Cursor cursor = banco.rawQuery(query, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                        String nome = cursor.getString(cursor.getColumnIndexOrThrow("nome"));

                        categorias.add(new Categoria(id, nome));
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Chama o callback na thread principal para atualizar a UI
            new Handler(Looper.getMainLooper()).post(() -> callback.onCategoriasLoaded(categorias));
        });
    }

    // Exclui uma categoria de forma assíncrona
    public void ExcluirCategoriaAsync(int idCategoria, CategoriaCallback callback) {
        executor.submit(() -> {
            if (idCategoria == 0) {
                throw new IllegalArgumentException("O ID da categoria é obrigatório.");
            }

            banco.beginTransaction();
            Cursor cursor = null;

            try {
                // Verifica se a categoria com o ID fornecido existe
                String query = "SELECT EXISTS (SELECT 1 FROM Categorias WHERE id = ?)";
                cursor = banco.rawQuery(query, new String[]{String.valueOf(idCategoria)});

                if (cursor.moveToFirst() && cursor.getInt(0) == 0) {
                    callback.onResult(0); // Categoria não encontrada
                    return;
                }

                // Exclui a categoria
                int linhasAfetadas = banco.delete("Categorias", "id = ?", new String[]{String.valueOf(idCategoria)});
                if (linhasAfetadas > 0) {
                    banco.setTransactionSuccessful();
                    callback.onResult(1); // Sucesso
                } else {
                    callback.onResult(0); // Falha
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onResult(0); // Falha devido a exceção
            } finally {
                if (cursor != null) cursor.close();
                banco.endTransaction();
            }
        });
    }

    // Callback para operações de categoria
    public interface CategoriaCallback {
        void onResult(long result); // 1 = sucesso, 0 = falha, -1 = já existe
    }

    // Callback para carregar lista de categorias
    public interface CategoriaListCallback {
        void onCategoriasLoaded(List<Categoria> categorias);
    }
}
