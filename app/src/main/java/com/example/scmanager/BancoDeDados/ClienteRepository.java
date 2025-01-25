package com.example.scmanager.BancoDeDados;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;

import com.example.scmanager.Cliente.Cliente;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClienteRepository {

    public static final String SQL_CREATE_CLIENTES =
            "CREATE TABLE Clientes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome TEXT NOT NULL, " +
                    "telefone TEXT)";

    private SQLiteDatabase banco;
    private final ExecutorService executor;

    public ClienteRepository(Context context){
        ControladorBancoDeDados db = ControladorBancoDeDados.getInstance(context);
        banco = db.getWritableDatabase();  // Certifique-se de que o banco está sendo obtido corretamente
        executor = Executors.newSingleThreadExecutor(); // Usando um executor simples
    }

    // Adiciona um cliente de forma assíncrona
    public void AdicionarClienteAsync(String nome, String telefone, ClienteCallback callback) {
        executor.submit(() -> {
            if (nome == null || nome.trim().isEmpty() || telefone == null || telefone.trim().isEmpty()) {
                throw new IllegalArgumentException("Nome e telefone são obrigatórios.");
            }

            banco.beginTransaction();
            Cursor cursor = null;
            try {
                String query = "SELECT EXISTS (SELECT 1 FROM Clientes WHERE nome = ? AND telefone = ?)";
                cursor = banco.rawQuery(query, new String[]{nome, telefone});

                if (cursor.moveToFirst() && cursor.getInt(0) == 1) {
                    callback.onResult(-1);  // Cliente já existe
                    return;
                }

                ContentValues valores = new ContentValues();
                valores.put("nome", nome);
                valores.put("telefone", telefone);

                long result = banco.insert("Clientes", null, valores);
                if (result == -1) {
                    throw new Exception("Erro ao inserir cliente.");
                }

                banco.setTransactionSuccessful();
                callback.onResult(result);  // Retorna o ID do cliente inserido
            } catch (Exception e) {
                e.printStackTrace();
                callback.onResult(-1);  // Falha na inserção
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                banco.endTransaction();
            }
        });
    }

    // Edita um cliente de forma assíncrona
    public void EditarClienteAsync(int idCliente, String nome, String telefone, ClienteCallback callback) {
        executor.submit(() -> {
            if (idCliente == 0 || nome == null || nome.trim().isEmpty() || telefone == null || telefone.trim().isEmpty()) {
                throw new IllegalArgumentException("ID, nome e telefone são obrigatórios.");
            }

            banco.beginTransaction(); // Inicia a transação
            Cursor cursorCliente = null;
            Cursor cursorDuplicado = null;

            try {
                // Verifica se o cliente com o ID informado existe
                String queryCliente = "SELECT EXISTS (SELECT 1 FROM Clientes WHERE id = ?)";
                cursorCliente = banco.rawQuery(queryCliente, new String[]{String.valueOf(idCliente)});
                if (cursorCliente.moveToFirst() && cursorCliente.getInt(0) == 0) {
                    callback.onResult(0);  // Cliente não encontrado
                    return;
                }

                // Verifica se já existe um cliente com o mesmo nome e telefone, exceto o próprio cliente
                String queryDuplicado = "SELECT EXISTS (SELECT 1 FROM Clientes WHERE nome = ? AND telefone = ? AND id != ?)";
                cursorDuplicado = banco.rawQuery(queryDuplicado, new String[]{nome, telefone, String.valueOf(idCliente)});
                if (cursorDuplicado.moveToFirst() && cursorDuplicado.getInt(0) == 1) {
                    callback.onResult(0);  // Cliente duplicado
                    return;
                }

                // Atualiza os dados do cliente
                ContentValues valores = new ContentValues();
                valores.put("nome", nome);
                valores.put("telefone", telefone);
                int linhasAfetadas = banco.update("Clientes", valores, "id = ?", new String[]{String.valueOf(idCliente)});

                if (linhasAfetadas > 0) {
                    banco.setTransactionSuccessful(); // Confirma a transação
                    callback.onResult(1);  // Sucesso
                } else {
                    callback.onResult(0);  // Falha na atualização
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onResult(0);  // Erro durante a execução
            } finally {
                // Fecha os cursores e finaliza a transação
                if (cursorCliente != null) cursorCliente.close();
                if (cursorDuplicado != null) cursorDuplicado.close();
                banco.endTransaction(); // Finaliza a transação (confirma ou descarta)
            }
        });
    }

    // Carrega clientes de forma assíncrona
    public void carregarClientesDoBancoAsync(ClienteListCallback callback) {
        executor.submit(() -> {
            List<Cliente> clientes = new ArrayList<>();
            String query = "SELECT id, nome, telefone FROM Clientes";  // Buscando todas as colunas

            try (Cursor cursor = banco.rawQuery(query, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                        String nome = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
                        String telefone = cursor.getString(cursor.getColumnIndexOrThrow("telefone"));

                        // Criando um objeto Cliente e adicionando à lista
                        clientes.add(new Cliente(id, nome, telefone));
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Chama o callback na thread principal para atualizar a UI
            new Handler(Looper.getMainLooper()).post(() -> callback.onClientesLoaded(clientes));
        });
    }

    // Exclui um cliente de forma assíncrona
    public void ExcluirClienteAsync(int id, ClienteCallback callback) {
        executor.submit(() -> {
            if (id == 0) {
                throw new IllegalArgumentException("O ID do cliente é obrigatório.");
            }

            banco.beginTransaction();
            Cursor cursor = null;

            try {
                // Verifica se o cliente com o ID fornecido existe
                String query = "SELECT EXISTS (SELECT 1 FROM Clientes WHERE id = ?)";
                cursor = banco.rawQuery(query, new String[]{String.valueOf(id)});

                if (cursor.moveToFirst() && cursor.getInt(0) == 0) {
                    callback.onResult(0);  // Cliente não encontrado
                    return;
                }

                // Exclui o cliente
                int linhasAfetadas = banco.delete("Clientes", "id = ?", new String[]{String.valueOf(id)});
                if (linhasAfetadas > 0) {
                    banco.setTransactionSuccessful();
                    callback.onResult(1);  // Exclusão bem-sucedida
                } else {
                    callback.onResult(0);  // Falha na exclusão
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onResult(0);  // Falha devido a uma exceção
            } finally {
                // Fecha o cursor, se aberto
                if (cursor != null) {
                    cursor.close();
                }
                banco.endTransaction();
            }
        });
    }


    // Callback para operações de cliente (sucesso ou falha)
    public interface ClienteCallback {
        void onResult(long result);  // 1 = sucesso, 0 = falha, -1 = já existe
    }

    // Callback para carregar lista de clientes
    public interface ClienteListCallback {
        void onClientesLoaded(List<Cliente> clientes);
    }
}
