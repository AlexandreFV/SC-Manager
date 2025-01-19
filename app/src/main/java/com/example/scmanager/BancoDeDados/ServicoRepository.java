package com.example.scmanager.BancoDeDados;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    public void AdicionarServico(Integer tipoServico, Integer idCliente, double valorPagamento, String dataAceiteServico, Integer estadoPagamento, String dataPagamento) {
        banco.beginTransaction();

        Cursor cursorCliente = null;
        Cursor cursorServico = null;

        try {
            String queryCliente = "SELECT EXISTS (SELECT 1 FROM Clientes WHERE id = ?)";
            cursorCliente = banco.rawQuery(queryCliente, new String[]{String.valueOf(idCliente)});

            if (cursorCliente == null || !cursorCliente.moveToFirst() || cursorCliente.getInt(0) == 0) {
                throw new IllegalArgumentException("Cliente com o id " + idCliente + " não encontrado.");
            }

            String queryServico = "SELECT EXISTS (SELECT 1 FROM Categorias WHERE id = ?)";
            cursorServico = banco.rawQuery(queryServico, new String[]{String.valueOf(tipoServico)});

            if (cursorServico == null || !cursorServico.moveToFirst() || cursorServico.getInt(0) == 0) {
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursorCliente != null) {
                cursorCliente.close();
            }
            if (cursorServico != null) {
                cursorServico.close();
            }
            banco.endTransaction();
        }
    }

    public void EditarServico(Integer idServico, Integer tipoServico, Integer idCliente, double valorPagamento, String dataAceiteServico, Integer estadoPagamento, String dataPagamento) {
        banco.beginTransaction();

        Cursor cursorServico = null;
        Cursor cursorCliente = null;

        try {
            String queryServico = "SELECT EXISTS (SELECT 1 FROM Servicos WHERE id = ?)";
            cursorServico = banco.rawQuery(queryServico, new String[]{String.valueOf(idServico)});

            if (cursorServico == null || !cursorServico.moveToFirst() || cursorServico.getInt(0) == 0) {
                cursorServico.close();
                throw new IllegalArgumentException("Não existe esse serviço.");
            }

            String queryCliente = "SELECT EXISTS (SELECT 1 FROM Clientes WHERE id = ?)";
            cursorCliente = banco.rawQuery(queryCliente, new String[]{String.valueOf(idCliente)});

            if (cursorCliente == null || !cursorCliente.moveToFirst() || cursorCliente.getInt(0) == 0) {
                cursorCliente.close();
                throw new IllegalArgumentException("Não existe esse cliente.");
            }

            String queryTipoServico = "SELECT EXISTS (SELECT 1 FROM Categorias WHERE id = ?)";
            cursorCliente = banco.rawQuery(queryTipoServico, new String[]{String.valueOf(tipoServico)});

            if (cursorCliente == null || !cursorCliente.moveToFirst() || cursorCliente.getInt(0) == 0) {
                cursorCliente.close();
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            banco.endTransaction();
            if (cursorCliente != null) {
                cursorCliente.close();
            }
            if (cursorServico != null) {
                cursorServico.close();
            }
        }
    }

    public void ExcluirServico(Integer id) {
        banco.beginTransaction();

        Cursor cursor = null;
        try {
            String query = "SELECT EXISTS (SELECT 1 FROM Servicos WHERE id = ?)";
            cursor = banco.rawQuery(query, new String[]{String.valueOf(id)});

            if (cursor == null || !cursor.moveToFirst() || cursor.getInt(0) == 0) {
                throw new IllegalArgumentException("Não existe esse serviço.");
            }

            banco.delete("Servicos", "id = ?", new String[]{String.valueOf(id)});
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
