package com.example.scmanager.Gerenciamento.ViewModel;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.scmanager.BancoDeDados.ClienteRepository;
import com.example.scmanager.Gerenciamento.Objetos.Categoria;
import com.example.scmanager.Gerenciamento.Objetos.Cliente;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClienteViewModel extends AndroidViewModel {
    private ClienteRepository clienteRepository;
    private MutableLiveData<List<Cliente>> listaClientes = new MutableLiveData<>();

    public ClienteViewModel(@NonNull Application application) {
        super(application);
        clienteRepository = new ClienteRepository(application.getApplicationContext());
        listaClientes = new MutableLiveData<>();
        carregarClientes();
    }

    public LiveData<List<Cliente>> getListaClientes() {
        return listaClientes;
    }

    public void adicionarCliente(String nomeCliente, String telefoneCliente) {
        clienteRepository.AdicionarClienteAsync(nomeCliente, telefoneCliente, new ClienteRepository.ClienteCallback() {
            @Override
            public void onResult(long resultado) {
                if (resultado != -1) {
                    // Atualiza a lista de clientes após a adição
                    carregarClientes();
                    showToast("Cliente adicionado com sucesso!");

                } else {
                    Log.d("ClienteViewModel", "Erro ao adicionar cliente.");
                    showToast("Erro ao adicionar cliente.");
                }
            }
        });
    }

    public void excluirCliente(int clienteId) {
        clienteRepository.ExcluirClienteAsync(clienteId, new ClienteRepository.ClienteCallback() {
            @Override
            public void onResult(long resultado) {
                if (resultado != -1) {
                    // Atualiza a lista de clientes no LiveData
                    carregarClientes();
                    showToast("Cliente excluído com sucesso!");
                } else {
                    Log.d("ClienteViewModel", "Erro ao excluir cliente.");
                    showToast("Erro ao excluir cliente.");
                }
            }
        });
    }

    public void editarCliente(int idCliente, String nomeCliente, String telefoneCliente) {
        clienteRepository.EditarClienteAsync(idCliente, nomeCliente, telefoneCliente, new ClienteRepository.ClienteCallback() {
            @Override
            public void onResult(long resultado) {
                if (resultado == 1) { // Sucesso
                    carregarClientes(); // Atualiza a lista de clientes no LiveData
                    showToast("Cliente editado com sucesso!");
                } else if (resultado == 0) { // Falha (duplicado ou cliente inexistente)
                    showToast("Erro ao editar cliente. Verifique se os dados já existem ou se o cliente é válido.");
                } else { // Qualquer outro erro
                    Log.d("ClienteViewModel", "Erro ao editar cliente.");
                    showToast("Erro ao editar cliente.");
                }
            }
        });
    }


    public void carregarClientes() {
        SharedPreferences prefs = getApplication().getSharedPreferences("PreferenciasApp", getApplication().MODE_PRIVATE);
        String ordemSalvaNome = prefs.getString("ordemClienteNome", "CRESCENTE"); // Padrão: CRESCENTE
        String ordemSalvaTelefone = prefs.getString("ordemClienteTelefone", "CRESCENTE"); // Padrão: CRESCENTE

        clienteRepository.carregarClientesDoBancoAsync(new ClienteRepository.ClienteListCallback() {
            @Override
            public void onClientesLoaded(List<Cliente> clientes) {
                // Ordena primeiro pelo nome
                if (ordemSalvaNome.equals("CRESCENTE")) {
                    Collections.sort(clientes, (c1, c2) -> c1.getNome().compareToIgnoreCase(c2.getNome()));
                } else if (ordemSalvaNome.equals("DECRESCENTE")) {
                    Collections.sort(clientes, (c1, c2) -> c2.getNome().compareToIgnoreCase(c1.getNome()));
                }

                // Depois, ordena pelo telefone, mantendo a ordem do nome
                if (ordemSalvaTelefone.equals("CRESCENTE")) {
                    Collections.sort(clientes, Comparator.comparing(Cliente::getTelefone));
                } else if (ordemSalvaTelefone.equals("DECRESCENTE")) {
                    Collections.sort(clientes, (c1, c2) -> c2.getTelefone().compareTo(c1.getTelefone()));
                }

                listaClientes.postValue(clientes); // Atualiza a LiveData
            }
        });
    }


    private void showToast(String mensagem) {
        new android.os.Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(getApplication().getBaseContext(), mensagem, Toast.LENGTH_SHORT).show()
        );
    }
}


