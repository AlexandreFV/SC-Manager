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
import androidx.lifecycle.ViewModelProvider;

import com.example.scmanager.BancoDeDados.CategoriaRepository;
import com.example.scmanager.BancoDeDados.ClienteRepository;
import com.example.scmanager.BancoDeDados.ServicoRepository;
import com.example.scmanager.Gerenciamento.Objetos.Categoria;
import com.example.scmanager.Gerenciamento.Objetos.Cliente;
import com.example.scmanager.Gerenciamento.Objetos.Servico;

import java.security.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ServicoViewModel extends AndroidViewModel {
    private ServicoRepository servicoRepository;
    private MutableLiveData<List<Servico>> listaServico = new MutableLiveData<>();
    private ClienteRepository clienteRepository; // Adicionando o repositório de cliente
    private CategoriaRepository categoriaRepository; // Adicionando o repositório de cliente
    private Map<Integer, String> categoriaMap = new HashMap<>();
    private Map<Long, String> clienteMap = new HashMap<>();
    private Map<Integer, Categoria> categoriaMap2 = new HashMap<>();
    private Map<Long, Cliente> clienteMap2 = new HashMap<>();


    public ServicoViewModel(@NonNull Application application) {
        super(application);
        servicoRepository = new ServicoRepository(application.getApplicationContext());
        clienteRepository = new ClienteRepository(application.getApplicationContext()); // Inicializa o repositório de cliente
        categoriaRepository = new CategoriaRepository(application.getApplicationContext()); // Inicializa o repositório de cliente
        listaServico = new MutableLiveData<>();
        carregarServicos();
        carregarServicosInicio();
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
                    carregarServicosInicio();
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
                    carregarServicosInicio();
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
                    carregarServicosInicio();
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
        SharedPreferences prefs = getApplication().getSharedPreferences("PreferenciasApp", getApplication().MODE_PRIVATE);

        String ordemNomeCategoria = prefs.getString("ordemServicoNomeCategoria", "PADRAO");
        String ordemNomeCliente = prefs.getString("ordemServicoNomeCliente", "PADRAO");
        String ordemValor = prefs.getString("ordemServicoValor", "PADRAO");
        String ordemDataAceite = prefs.getString("ordemServicoDataAceite", "PADRAO");
        String ordemEstado = prefs.getString("ordemServicoEstado", "TODOS");
        String ordenDataPagOuEstip = prefs.getString("ordemServicoDataAceiteOuEstipulado", "TODOS");

        categoriaRepository.CarregarCategoriasAsync(categorias1 -> {
            for (Categoria categoria : categorias1) {
                    categoriaMap.put(categoria.getId(), categoria.getNome());
            }
        });


        clienteRepository.carregarClientesDoBancoAsync(clientes -> {
            for (Cliente cliente : clientes) {
                clienteMap.put(cliente.getId(), cliente.getNome());
            }
        });

        servicoRepository.CarregarServicosAsync(new ServicoRepository.ServicoListCallback() {
            @Override
            public void onServicosLoaded(List<Servico> servicos) {
                Date hoje = new Date(); // Data atual

                // Filtrar por estado de pagamento, se necessário
                if (!ordemEstado.equals("TODOS")) {
                    if (ordemEstado.equals("PAGO")) {
                        // Filtra para manter apenas os serviços pagos
                        servicos.removeIf(servico -> servico.getEstado() != 1);

                        // Ordenação por data de pagamento
                        if (ordenDataPagOuEstip.equals("CRESCENTE")) {
                            Collections.sort(servicos, Comparator.comparing(Servico::getDataPagamento));
                        } else if (ordenDataPagOuEstip.equals("DECRESCENTE")) {
                            Collections.sort(servicos, (s1, s2) -> s2.getDataPagamento().compareTo(s1.getDataPagamento()));
                        }
                    } else if (ordemEstado.equals("NAOPAGO")) {
                        // Filtra para manter apenas os serviços não pagos
                        servicos.removeIf(servico -> servico.getEstado() != 0);

                        // Filtragem adicional de pagamentos vencidos ou não vencidos
                        if (ordenDataPagOuEstip.equals("PAGAMENTOSVENCIDOS")) {
                            servicos.removeIf(servico -> {
                                Date dataPagamento = converterParaDate(servico.getDataPagamento());
                                if (dataPagamento == null) return true; // Remove se a data for inválida
                                return !dataPagamento.before(hoje); // Remove se NÃO estiver vencida
                            });
                        } else if (ordenDataPagOuEstip.equals("PAGAMENTOSNAOVENCIDOS")) {
                            servicos.removeIf(servico -> {
                                Date dataPagamento = converterParaDate(servico.getDataPagamento());
                                if (dataPagamento == null) return true; // Remove se a data for inválida
                                return dataPagamento.before(hoje); // Remove se estiver vencida
                            });
                        } else if (ordenDataPagOuEstip.equals("TODOS")) {
                            Collections.sort(servicos, (s1, s2) -> s2.getDataPagamento().compareTo(s1.getDataPagamento()));
                        }
                    }
                }

                // Ordenação por nome da categoria
                if (ordemNomeCategoria.equals("CRESCENTE")) {
                    Collections.sort(servicos, Comparator.comparing(servico -> {
                        String categoria = categoriaMap.get(servico.getTipoServico());
                        return categoria != null ? categoria : "";  // Se for null, retorna uma string vazia
                    }, String::compareToIgnoreCase));
                } else if (ordemNomeCategoria.equals("DECRESCENTE")) {
                    Collections.sort(servicos, (s1, s2) -> {
                        String categoria1 = categoriaMap.get(s1.getTipoServico());
                        String categoria2 = categoriaMap.get(s2.getTipoServico());
                        // Caso seja null, substitui por string vazia
                        return (categoria2 != null ? categoria2 : "").compareToIgnoreCase(categoria1 != null ? categoria1 : "");
                    });
                }


                if (ordemNomeCliente.equals("CRESCENTE")) {
                    Collections.sort(servicos, Comparator.comparing(servico -> {
                        String cliente = clienteMap.get(servico.getIdCliente().longValue());
                        return cliente != null ? cliente : "";  // Se for null, retorna uma string vazia
                    }, String::compareToIgnoreCase));
                } else if (ordemNomeCliente.equals("DECRESCENTE")) {
                    Collections.sort(servicos, (s1, s2) -> {
                        String cliente1 = clienteMap.get(s1.getIdCliente().longValue());
                        String cliente2 = clienteMap.get(s2.getIdCliente().longValue());
                        // Caso seja null, substitui por string vazia
                        return (cliente2 != null ? cliente2 : "").compareToIgnoreCase(cliente1 != null ? cliente1 : "");
                    });
                }


                // Ordenação por valor
                if (ordemValor.equals("CRESCENTE")) {
                    Collections.sort(servicos, Comparator.comparing(Servico::getValor));
                } else if (ordemValor.equals("DECRESCENTE")) {
                    Collections.sort(servicos, (s1, s2) -> Double.compare(s2.getValor(), s1.getValor()));
                }

                // Ordenação por data de aceite
                if (ordemDataAceite.equals("CRESCENTE")) {
                    Collections.sort(servicos, Comparator.comparing(Servico::getDataAceiteServico));
                } else if (ordemDataAceite.equals("DECRESCENTE")) {
                    Collections.sort(servicos, (s1, s2) -> s2.getDataAceiteServico().compareTo(s1.getDataAceiteServico()));
                }

                listaServico.postValue(servicos); // Atualiza a LiveData com os serviços filtrados e ordenados
            }
        });
    }


    public void carregarServicosInicio() {
        SharedPreferences prefs = getApplication().getSharedPreferences("PreferenciasApp", getApplication().MODE_PRIVATE);

        // Carregar categorias e clientes
        categoriaRepository.CarregarCategoriasAsync(categorias1 -> {
            for (Categoria categoria : categorias1) {
                categoriaMap2.put(categoria.getId(), categoria);
            }
            carregarClientesEServicos(prefs);
        });
    }

    private void carregarClientesEServicos(SharedPreferences prefs) {
        clienteRepository.carregarClientesDoBancoAsync(clientes -> {
            for (Cliente cliente : clientes) {
                clienteMap2.put(cliente.getId(), cliente);
            }

            servicoRepository.CarregarServicosAsync(new ServicoRepository.ServicoListCallback() {
                @Override
                public void onServicosLoaded(List<Servico> servicos) {
                    List<Integer> categoriasSelecionadas = getCategoriasSelecionadas(prefs);
                    List<Long> clientesSelecionados = getClientesSelecionados(prefs);

                    // Se nenhuma categoria ou cliente for selecionado, não exibe nenhum serviço
                    if (categoriasSelecionadas.isEmpty() || clientesSelecionados.isEmpty()) {
                        listaServico.postValue(new ArrayList<>()); // Exibe uma lista vazia
                        return;
                    }

                    // Filtrando serviços com base nas seleções
                    List<Servico> servicosFiltrados = filtrarServicos(servicos, categoriasSelecionadas, clientesSelecionados);

                    // Ordenando os serviços filtrados
                    ordenarServicos(servicosFiltrados);

                    if (servicosFiltrados.isEmpty()) {
                        showToast("Nenhum serviço encontrado com os filtros aplicados.");
                    }
                }
            });
        });
    }

    private List<Integer> getCategoriasSelecionadas(SharedPreferences prefs) {
        List<Integer> categoriasSelecionadas = new ArrayList<>();
        for (Categoria categoria : categoriaMap2.values()) {
            boolean isSelected = prefs.getBoolean("categoria_" + categoria.getId(), false);
            if (isSelected) {
                categoriasSelecionadas.add(categoria.getId());
            }
        }
        return categoriasSelecionadas;
    }

    private List<Long> getClientesSelecionados(SharedPreferences prefs) {
        List<Long> clientesSelecionados = new ArrayList<>();
        for (Cliente cliente : clienteMap2.values()) {
            boolean isSelected = prefs.getBoolean("cliente_" + cliente.getId(), false);
            if (isSelected) {
                clientesSelecionados.add(cliente.getId());
            }
        }
        return clientesSelecionados;
    }

    private List<Servico> filtrarServicos(List<Servico> servicos, List<Integer> categoriasSelecionadas, List<Long> clientesSelecionados) {
        List<Servico> servicosFiltrados = new ArrayList<>();
        for (Servico servico : servicos) {
            boolean incluirServico = true;

            // Filtrando por categoria
            if (!categoriasSelecionadas.isEmpty() && !categoriasSelecionadas.contains(servico.getTipoServico())) {
                incluirServico = false;
            }

            // Filtrando por cliente
            if (!clientesSelecionados.isEmpty() && !clientesSelecionados.contains(servico.getIdCliente().longValue())) {
                incluirServico = false;
            }

            // Adicionando serviço à lista filtrada se passar nos filtros
            if (incluirServico) {
                servicosFiltrados.add(servico);
            }
        }
        return servicosFiltrados;
    }


    private void showToast(String mensagem) {
        new android.os.Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(getApplication().getBaseContext(), mensagem, Toast.LENGTH_SHORT).show()
        );
    }

    private Date converterParaDate(String dataPagamento) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date data = sdf.parse(dataPagamento);
            Log.d("ConverterParaDate", "Data convertida: " + dataPagamento + " -> " + data);
            return data;
        } catch (Exception e) {
            Log.e("ConverterParaDate", "Erro ao converter data: " + dataPagamento, e);
            return null;
        }
    }

    private void ordenarServicos(List<Servico> servicos) {
        SharedPreferences prefs = getApplication().getSharedPreferences("PreferenciasApp", getApplication().MODE_PRIVATE);

        String ordemDeData = prefs.getString("ordemDeData", "");
        String ordemAteData = prefs.getString("ordemAteData", "");
        String ordemEstadoInicio = prefs.getString("ordemEstadoInicio", "Todos");
        String ordemCategoriaOuCliente = prefs.getString("ordemCategoriaOuCliente", "Categorias");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Filtrar por data de início e fim
        if (!ordemDeData.isEmpty() && !ordemAteData.isEmpty()) {
            try {
                Date dataInicio = dateFormat.parse(ordemDeData);
                Date dataFim = dateFormat.parse(ordemAteData);

                if (dataInicio != null && dataFim != null) {
                    servicos = servicos.stream()
                            .filter(servico -> {
                                try {
                                    // Convertendo a data do serviço
                                    Date dataServico = dateFormat.parse(servico.getDataAceiteServico());

                                    // Verificar se a data do serviço foi corretamente convertida
                                    if (dataServico == null) {
                                        Log.e("OrdenarServicos", "Erro ao converter data de serviço: " + servico.getDataAceiteServico());
                                        return false; // Ignorar esse serviço caso a data seja inválida
                                    }

                                    // Comparar se a data do serviço está dentro do intervalo
                                    return !dataServico.before(dataInicio) && !dataServico.after(dataFim);
                                } catch (ParseException e) {
                                    Log.e("OrdenarServicos", "Erro ao comparar data de serviço", e);
                                    return false;
                                }
                            })
                            .collect(Collectors.toList());
                }
            } catch (ParseException e) {
                Log.e("OrdenarServicos", "Erro ao converter datas de filtro", e);
            }
        }

        // Filtrar por estado (Ex: Pago / Não pago)
        if (!ordemEstadoInicio.equals("Todos")) {
            if (ordemEstadoInicio.equals("Pago")) {
                // Filtra os serviços com estado igual a 1 (Pago)
                servicos = servicos.stream()
                        .filter(servico -> servico.getEstado() == 1) // Mantém apenas os serviços pagos
                        .collect(Collectors.toList());
            } else if (ordemEstadoInicio.equals("NaoPago")) {
                // Filtra os serviços com estado igual a 0 (Não Pago)
                servicos = servicos.stream()
                        .filter(servico -> servico.getEstado() == 0) // Mantém apenas os serviços não pagos
                        .collect(Collectors.toList());
            }
        }

        // Agrupar por Categoria ou Cliente
        Map<String, List<Servico>> groupedServicos = new HashMap<>();
        if (ordemCategoriaOuCliente.equals("Categorias")) {
            // Agrupar por ID de Categoria (supondo que você tenha o idCategoria no seu objeto Servico)
            groupedServicos = servicos.stream()
                    .collect(Collectors.groupingBy(servico -> String.valueOf(servico.getTipoServico())));
        } else if (ordemCategoriaOuCliente.equals("Clientes")) {
            // Agrupar por ID de Cliente (supondo que você tenha o idCliente no seu objeto Servico)
            groupedServicos = servicos.stream()
                    .collect(Collectors.groupingBy(servico -> String.valueOf(servico.getIdCliente())));
        }

        listaServico.postValue(servicos); // Atualiza a LiveData com os serviços filtrados
    }



}
