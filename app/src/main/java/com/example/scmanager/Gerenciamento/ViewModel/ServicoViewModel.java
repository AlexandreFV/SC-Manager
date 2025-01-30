package com.example.scmanager.Gerenciamento.ViewModel;

import android.app.Application;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.scmanager.BancoDeDados.ClienteRepository;
import com.example.scmanager.BancoDeDados.ServicoRepository;
import com.example.scmanager.Gerenciamento.Objetos.Cliente;
import com.example.scmanager.Gerenciamento.Objetos.Servico;

import java.util.List;

public class ServicoViewModel extends AndroidViewModel {
    private ServicoRepository servicoRepository;
    private MutableLiveData<List<Servico>> listaServico = new MutableLiveData<>();
    private ClienteRepository clienteRepository; // Adicionando o repositório de cliente
    public ServicoViewModel(@NonNull Application application) {
        super(application);
        servicoRepository = new ServicoRepository(application.getApplicationContext());
        clienteRepository = new ClienteRepository(application.getApplicationContext()); // Inicializa o repositório de cliente
        listaServico = new MutableLiveData<>();
        carregarServicos();
    }

    public LiveData<List<Servico>> getListaServico() {
        return listaServico;
    }

    public void adicionarServico(Integer idCliente, Integer idCategoria, Float valor, String dataAceiteServ, String estado, String dataPagOuEstipu) {
        servicoRepository.AdicionarServicoAsync(idCliente,idCategoria, valor, dataAceiteServ,estado, dataPagOuEstipu , new ServicoRepository.ServicoCallback() {
            @Override
            public void onResult(long resultado) {
                if (resultado != -1) {
                    // Atualiza a lista de clientes após a adição
                    carregarServicos();
                    showToast("Servico adicionado com sucesso!");

                } else {
                    Log.d("ServicoViewModel", "Erro ao adicionar servico.");
                    showToast("Erro ao adicionar servico.");
                }
            }
        });
    }

    public void excluirServico(int servicoId) {
        servicoRepository.ExcluirServicoAsync(servicoId, new ServicoRepository.ServicoCallback() {
            @Override
            public void onResult(long resultado) {
                if (resultado != -1) {

                    // Atualiza a lista de clientes no LiveData
                    carregarServicos();
                    showToast("Servico excluído com sucesso!");
                } else {
                    Log.d("ServicoViewModel", "Erro ao excluir Servico.");
                    showToast("Erro ao excluir Servico.");
                }
            }
        });
    }

    public void editarServico(Integer idServico,Integer idCliente, Integer idCategoria, Float valor, String dataAceiteServ, String estado, String dataPagOuEstipu) {
        servicoRepository.EditarServicoAsync(idServico, idCliente, idCategoria, valor, dataAceiteServ, estado, dataPagOuEstipu, new ServicoRepository.ServicoCallback() {
            @Override
            public void onResult(long resultado) {
                if (resultado == 1) { // Sucesso
                    carregarServicos(); // Atualiza a lista de clientes no LiveData
                    showToast("Servico editado com sucesso!");
                } else if (resultado == 0) { // Falha (duplicado ou cliente inexistente)
                    showToast("Erro ao editar Servico. Verifique se os dados já existem ou se o Servico é válido.");
                } else { // Qualquer outro erro
                    Log.d("ServicoViewModel", "Erro ao editar Servico.");
                    showToast("Erro ao editar Servico.");
                }
            }
        });
    }


    public void carregarServicos() {
        servicoRepository.CarregarServicosAsync(new ServicoRepository.ServicoListCallback() {
            @Override
            public void onServicosLoaded(List<Servico> servicos) {
                listaServico.setValue(servicos);
            }
        });
    }

    private void showToast(String mensagem) {
        new android.os.Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(getApplication().getBaseContext(), mensagem, Toast.LENGTH_SHORT).show()
        );
    }

}
