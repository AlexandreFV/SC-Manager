package com.example.scmanager.Servico;

public class Servico {
    private Integer id;
    private Integer tipoServico; // ID da categoria
    private Integer idCliente;
    private Double valor;
    private String dataAceiteServico;
    private Integer estado; // Pode representar o status do serviço (ex: 0 = pendente, 1 = concluído)
    private String dataPagamento;

    public Servico() {
    }

    public Servico(Integer id, Integer tipoServico, Integer idCliente, Double valor, String dataAceiteServico, Integer estado, String dataPagamento) {
        this.id = id;
        this.tipoServico = tipoServico;
        this.idCliente = idCliente;
        this.valor = valor;
        this.dataAceiteServico = dataAceiteServico;
        this.estado = estado;
        this.dataPagamento = dataPagamento;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(Integer tipoServico) {
        this.tipoServico = tipoServico;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getDataAceiteServico() {
        return dataAceiteServico;
    }

    public void setDataAceiteServico(String dataAceiteServico) {
        this.dataAceiteServico = dataAceiteServico;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(String dataPagamento) {
        this.dataPagamento = dataPagamento;
    }
}
