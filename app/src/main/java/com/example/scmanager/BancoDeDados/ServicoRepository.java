package com.example.scmanager.BancoDeDados;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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

    public ServicoRepository(SQLiteDatabase db){
        banco = db;
    }

    private boolean existeRegistro(String tabela, String coluna, String valor) {
        String query = "SELECT EXISTS (SELECT 1 FROM " + tabela + " WHERE " + coluna + " = ?)";
        try (Cursor cursor = banco.rawQuery(query, new String[]{valor})) {
            return cursor != null && cursor.moveToFirst() && cursor.getLong(0) == 1;
        }
    }

    public boolean AdicionarCategoria(String nome) {
        banco.beginTransaction();
        try {
            if (existeRegistro("Categorias", "nome", nome)) {
                throw new IllegalArgumentException("Já existe uma categoria com o mesmo nome.");
            }

            ContentValues valores = new ContentValues();
            valores.put("nome", nome);

            banco.insert("Categorias", null, valores);
            banco.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e("DatabaseError", "Erro ao adicionar categoria", e);
            return false;
        } finally {
            banco.endTransaction();
        }
    }

    public boolean EditarCategoria(int idCategoria, String nome) {
        banco.beginTransaction();
        try {
            if (!existeRegistro("Categorias", "id", String.valueOf(idCategoria))) {
                throw new IllegalArgumentException("Não existe uma categoria com id: " + idCategoria);
            }

            if (existeRegistro("Categorias", "nome", nome)) {
                throw new IllegalArgumentException("Já existe uma categoria com o mesmo nome.");
            }

            ContentValues valores = new ContentValues();
            valores.put("nome", nome);

            banco.update("Categorias", valores, "id = ?", new String[]{String.valueOf(idCategoria)});
            banco.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e("DatabaseError", "Erro ao editar categoria", e);
            return false;
        } finally {
            banco.endTransaction();
        }
    }

    public boolean ExcluirCategoria(int id) {
        banco.beginTransaction();
        try {
            if (!existeRegistro("Categorias", "id", String.valueOf(id))) {
                throw new IllegalArgumentException("Não existe essa categoria.");
            }

            banco.delete("Categorias", "id = ?", new String[]{String.valueOf(id)});
            banco.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e("DatabaseError", "Erro ao excluir categoria", e);
            return false;
        } finally {
            banco.endTransaction();
        }
    }

    public boolean AdicionarServico(int tipoServico, int idCliente, double valorPagamento, String dataAceiteServico, int estadoPagamento, String dataPagamento) {
        banco.beginTransaction();
        try {
            if (!existeRegistro("Clientes", "id", String.valueOf(idCliente))) {
                throw new IllegalArgumentException("Cliente com o id " + idCliente + " não encontrado.");
            }

            if (!existeRegistro("Categorias", "id", String.valueOf(tipoServico))) {
                throw new IllegalArgumentException("Tipo de serviço com o id " + tipoServico + " não encontrado.");
            }

            ContentValues valores = new ContentValues();
            valores.put("tipoServico", tipoServico);
            valores.put("idCliente", idCliente);
            valores.put("valor", valorPagamento);
            valores.put("dataAceiteServico", dataAceiteServico);
            valores.put("estado", estadoPagamento);
            valores.put("dataPagamento", dataPagamento);

            banco.insert("Servicos", null, valores);
            banco.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e("DatabaseError", "Erro ao adicionar serviço", e);
            return false;
        } finally {
            banco.endTransaction();
        }
    }

    public boolean EditarServico(int idServico, int tipoServico, int idCliente, double valorPagamento, String dataAceiteServico, int estadoPagamento, String dataPagamento) {
        banco.beginTransaction();
        try {
            if (!existeRegistro("Servicos", "id", String.valueOf(idServico))) {
                throw new IllegalArgumentException("Não existe esse serviço.");
            }

            if (!existeRegistro("Clientes", "id", String.valueOf(idCliente))) {
                throw new IllegalArgumentException("Não existe esse cliente.");
            }

            if (!existeRegistro("Categorias", "id", String.valueOf(tipoServico))) {
                throw new IllegalArgumentException("Não existe esse tipo de serviço.");
            }

            ContentValues valores = new ContentValues();
            valores.put("tipoServico", tipoServico);
            valores.put("idCliente", idCliente);
            valores.put("valor", valorPagamento);
            valores.put("dataAceiteServico", dataAceiteServico);
            valores.put("estado", estadoPagamento);
            valores.put("dataPagamento", dataPagamento);

            banco.update("Servicos", valores, "id = ?", new String[]{String.valueOf(idServico)});
            banco.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e("DatabaseError", "Erro ao editar serviço", e);
            return false;
        } finally {
            banco.endTransaction();
        }
    }

    public boolean ExcluirServico(int id) {
        banco.beginTransaction();
        try {
            if (!existeRegistro("Servicos", "id", String.valueOf(id))) {
                throw new IllegalArgumentException("Não existe esse serviço.");
            }

            banco.delete("Servicos", "id = ?", new String[]{String.valueOf(id)});
            banco.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e("DatabaseError", "Erro ao excluir serviço", e);
            return false;
        } finally {
            banco.endTransaction();
        }
    }

}
