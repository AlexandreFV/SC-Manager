package com.example.scmanager.BancoDeDados;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;

import com.example.scmanager.Gerenciamento.Objetos.Servico;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServicoRepository {

    public static final String SQL_CREATE_SERVICOS =
            "CREATE TABLE Servicos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "tipoServico INTEGER NOT NULL, " +
                    "idCliente INTEGER, " +
                    "valor REAL, " +
                    "dataAceiteServico TEXT, " +
                    "estado INTEGER, " +
                    "dataPagamento TEXT, " +
                    "FOREIGN KEY (idCliente) REFERENCES Clientes (id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (tipoServico) REFERENCES Categorias (id) ON DELETE CASCADE)";

    private SQLiteDatabase banco;
    private final ExecutorService executor;

    public ServicoRepository(Context context){
        ControladorBancoDeDados db = ControladorBancoDeDados.getInstance(context);
        banco = db.getWritableDatabase();  // Certifique-se de que o banco está sendo obtido corretamente
        executor = Executors.newSingleThreadExecutor(); // Usando um executor simples
    }

    private boolean existeRegistro(String tabela, String coluna, String valor) {
        String query = "SELECT EXISTS (SELECT 1 FROM " + tabela + " WHERE " + coluna + " = ?)";
        try (Cursor cursor = banco.rawQuery(query, new String[]{valor})) {
            return cursor != null && cursor.moveToFirst() && cursor.getLong(0) == 1;
        }
    }

    public void AdicionarServicoAsync(Integer idCliente, Integer idCategoria, Float valor, String dataAceiteServ, String estado, String dataPagOuEstipu, ServicoCallback callback) {
        executor.submit(() -> {
            if (idCategoria == 0 || idCliente == 0 || valor == 0 || dataAceiteServ.isEmpty() || dataPagOuEstipu.isEmpty() || estado.isEmpty()) {
                throw new IllegalArgumentException("Preencha todos os campos.");
            }
            banco.beginTransaction();
            try {

                if (!existeRegistro("Clientes", "id", String.valueOf(idCliente))) {
                    throw new IllegalArgumentException("Cliente com o id " + idCliente + " não encontrado.");
                }

                if (!existeRegistro("Categorias", "id", String.valueOf(idCategoria))) {
                    throw new IllegalArgumentException("Tipo de categoria com o id " + idCategoria + " não encontrado.");
                }

                ContentValues valores = new ContentValues();
                valores.put("idCliente", idCliente);
                valores.put("tipoServico", idCategoria);
                valores.put("valor", valor);
                valores.put("dataAceiteServico", dataAceiteServ);
                if (estado.equals("Pago")) {
                    Integer pago = 1;
                    valores.put("estado", pago);
                } else if (estado.equals("Não Pago")) {
                    Integer pago = 0;
                    valores.put("estado", pago);
                }
                valores.put("dataPagamento", dataPagOuEstipu);

                long result = banco.insert("Servicos", null, valores);

                if (result == -1) {
                    throw new Exception("Erro ao inserir categoria.");
                }

                banco.setTransactionSuccessful();
                callback.onResult(result); // Retorna o ID da categoria inserida
            } catch (Exception e) {
                e.printStackTrace();
                callback.onResult(-1); // Falha na inserção
            } finally {
                banco.endTransaction();
            }
        });
    }

    public void EditarServicoAsync(Integer idServico,Integer idCliente, Integer idCategoria, Float valor, String dataAceiteServ, String estado, String dataPagOuEstipu, ServicoCallback callback) {
        executor.submit(() -> {

            if (idCategoria == 0 || idCliente == 0 || valor == 0 || dataAceiteServ.isEmpty() || dataPagOuEstipu.isEmpty() || estado.isEmpty()) {
                throw new IllegalArgumentException("Preencha todos os campos.");
            }

            banco.beginTransaction();
            try {
                if (!existeRegistro("Servicos", "id", String.valueOf(idServico))) {
                    throw new IllegalArgumentException("Não existe esse serviço.");
                }

                if (!existeRegistro("Clientes", "id", String.valueOf(idCliente))) {
                    throw new IllegalArgumentException("Não existe esse cliente.");
                }

                if (!existeRegistro("Categorias", "id", String.valueOf(idCategoria))) {
                    throw new IllegalArgumentException("Não existe esse tipo de categoria.");
                }

                ContentValues valores = new ContentValues();
                valores.put("idCliente", idCliente);
                valores.put("tipoServico", idCategoria);
                valores.put("valor", valor);
                valores.put("dataAceiteServico", dataAceiteServ);
                if (estado.equals("Pago")) {
                    Integer pago = 1;
                    valores.put("estado", pago);
                } else if (estado.equals("Não Pago")) {
                    Integer pago = 0;
                    valores.put("estado", pago);
                }
                valores.put("dataPagamento", dataPagOuEstipu);
                int linhasAfetadas = banco.update("Servicos", valores, "id = ?", new String[]{String.valueOf(idServico)});
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
                banco.endTransaction();
            }
        });
    }

    public void ExcluirServicoAsync(int id, ServicoCallback callback) {
        executor.submit(() -> {
            if (id == 0) {
                throw new IllegalArgumentException("O ID do serviço é obrigatório.");
            }

            banco.beginTransaction();
            Cursor cursor = null;

            try {
                if (!existeRegistro("Servicos", "id", String.valueOf(id))) {
                    throw new IllegalArgumentException("Não existe esse serviço.");
                }

                int linhasAfetadas = banco.delete("Servicos", "id = ?", new String[]{String.valueOf(id)});                if (linhasAfetadas > 0) {
                    banco.setTransactionSuccessful();
                    callback.onResult(1); // Sucesso
                } else {
                    callback.onResult(0); // Falha
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onResult(0); // Falha devido a exceção
            } finally {
                banco.endTransaction();
            }
        });
    }

    public void CarregarServicosAsync(ServicoListCallback callback) {
        executor.submit(() -> {
            List<Servico> servicos = new ArrayList<>();
            String query = "SELECT id, tipoServico, idCliente,valor,dataAceiteServico,estado,dataPagamento FROM Servicos";

            try (Cursor cursor = banco.rawQuery(query, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                        int tipoServico = cursor.getInt(cursor.getColumnIndexOrThrow("tipoServico"));
                        int idCliente = cursor.getInt(cursor.getColumnIndexOrThrow("idCliente"));
                        Double valor = cursor.getDouble(cursor.getColumnIndexOrThrow("valor"));
                        String dataAceiteServico = cursor.getString(cursor.getColumnIndexOrThrow("dataAceiteServico"));
                        int estado = cursor.getInt(cursor.getColumnIndexOrThrow("estado"));
                        String dataPagamento = cursor.getString(cursor.getColumnIndexOrThrow("dataPagamento"));

                        servicos.add(new Servico(id, tipoServico, idCliente,valor, dataAceiteServico, estado, dataPagamento));
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Chama o callback na thread principal para atualizar a UI
            new Handler(Looper.getMainLooper()).post(() -> callback.onServicosLoaded(servicos));
        });
    }

    // Callback para operações de categoria
    public interface ServicoCallback {
        void onResult(long result); // 1 = sucesso, 0 = falha, -1 = já existe
    }

    public String getNomeCategoriaById(int idCategoria) {
        String nomeCategoria = "Categoria desconhecida";
        String query = "SELECT nome FROM Categorias WHERE id = ?";
        try (Cursor cursor = banco.rawQuery(query, new String[]{String.valueOf(idCategoria)})) {
            if (cursor != null && cursor.moveToFirst()) {
                nomeCategoria = cursor.getString(cursor.getColumnIndex("nome"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nomeCategoria;
    }

    // Callback para carregar lista de categorias
    public interface ServicoListCallback {
        void onServicosLoaded(List<Servico> servicos);
    }

}
