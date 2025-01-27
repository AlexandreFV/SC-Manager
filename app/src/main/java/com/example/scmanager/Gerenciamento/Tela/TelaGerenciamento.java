package com.example.scmanager.Gerenciamento.Tela;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scmanager.BancoDeDados.ClienteRepository;
import com.example.scmanager.Gerenciamento.Adapter.ClienteAdapter;
import com.example.scmanager.Gerenciamento.Objetos.Cliente;
import com.example.scmanager.Gerenciamento.Tela.Cliente.BottomSheetDetalhesCliente;
import com.example.scmanager.Gerenciamento.ViewModel.ClienteViewModel;
import com.example.scmanager.R;
import com.example.scmanager.TelaAdicionarCategoriaClienteServico;
import android.animation.ObjectAnimator;

import java.util.List;

public class TelaGerenciamento extends AppCompatActivity implements View.OnClickListener  {

    private ImageButton voltarTela;
    private ImageButton imageMaisOpcoes;
    private ImageButton imageAdicionarLista;
    private ImageButton imageFiltrarLista;
    private boolean primeiraSubidaMaisOp = true;

    private float posicaoOriginalFiltrar;
    private float posicaoOriginalAdicionar;

    //Layout de respectivo gerenciamento, será usado para criar animacoes de surgimento e desaparecimento
    private ConstraintLayout LayoutClientes;
    private ConstraintLayout LayoutCategorias;



    //Objetos que exibirão os dados de respectivo gerenciamento
    private RecyclerView recyclerViewClientes;
    private RecyclerView recyclerViewCategorias;
    private RecyclerView recyclerViewServicos;


    private ClienteAdapter clienteAdapter;
    private ClienteViewModel clienteViewModel;


    //Button para trocar a exibicao do Layout
    private Button buttonCategoria;
    private Button buttonCliente;
    private Button buttonServico;


    //Indicativo de que opcao escolheu e está
    private View barraOpEscolhida;
    private String vaiPara = "";
    //Valor para saber qual layout exibir
    private String valor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_gerenciamento_cliente);
        referenciasButtonFlutuante();
        referenciasRecyclerView();
        referenciarLayout();

        // Obter instância do banco de dados e do repositório
        ClienteRepository clienteRepository = new ClienteRepository(this);
        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);

        // Adicionando o Observer para o LiveData
        clienteViewModel.getListaClientes().observe(this, new Observer<List<Cliente>>() {
            @Override
            public void onChanged(List<Cliente> clientes) {
                // Atualize o Adapter com a nova lista de clientes
                if (clienteAdapter == null) {
                    clienteAdapter = new ClienteAdapter(TelaGerenciamento.this, clientes);
                    recyclerViewClientes.setAdapter(clienteAdapter);
                } else {
                    clienteAdapter.setClientes(clientes); // Supondo que o Adapter tenha um metodo para atualizar a lista
                }

                // Atualiza a altura do RecyclerView com base na quantidade de itens
                int itemCount = clientes.size();
                int itemHeight = dpToPx(100); // Convertendo 100dp para pixels

                if (itemCount == 0) {
                    // Se não houver itens, defina a altura do RecyclerView como 0
                    recyclerViewClientes.getLayoutParams().height = 0;
                } else {
                    // Se houver itens, defina a altura do RecyclerView com base no número de itens
                    recyclerViewClientes.getLayoutParams().height = Math.min(itemCount * itemHeight, dpToPx(500)); // Limite de 500dp
                }
                recyclerViewClientes.requestLayout();
            }
        });

    }

    private void referenciarLayout()
    {
        LayoutClientes = findViewById(R.id.LayoutClientes);
        LayoutCategorias = findViewById(R.id.GrupoIconesCategoria);
        barraOpEscolhida = findViewById(R.id.barraOpcaoSelecionado);

        Intent intent = getIntent();
        // Recupera o valor passado no Intent
        valor = intent.getStringExtra("botao_apertado");
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) barraOpEscolhida.getLayoutParams();

        if(valor.equals("categoria"))
        {
            LayoutClientes.setVisibility(View.INVISIBLE);
//            LayoutServico.setVisibility(View.GONE);
            atualizarBotoes(false, true, true);
            params.horizontalBias = 0.0f;  // Alinha à esquerda
            params.startToStart = buttonCategoria.getId();  // Define o final à direita do botão "Serviço"
            params.leftMargin = buttonCategoria.getPaddingLeft();
            vaiPara = "categoria";
        }
        else if (valor.equals("cliente"))
        {
            LayoutCategorias.setVisibility(View.INVISIBLE);
//            LayoutServico.setVisibility(View.GONE);
            atualizarBotoes(true, true, false);
            // Quando clicado no "serviço" novamente
            params.horizontalBias = 0.5f;  // Centraliza
            params.leftMargin = 0;  // Sem padding lateral
            params.rightMargin = 0; // Sem padding lateral//
            vaiPara = "cliente";
        }
        else if (valor.equals("servico"))
        {
            LayoutCategorias.setVisibility(View.INVISIBLE);
            LayoutClientes.setVisibility(View.INVISIBLE);
            atualizarBotoes(true, false, true);
            params.horizontalBias = 1.0f;  // Alinha à direita
            params.endToEnd = buttonServico.getId();  // Define o final à direita do botão "Serviço"
            // Ajusta o padding lateral para a largura do botão "Serviço"
            params.rightMargin = buttonServico.getPaddingRight();

            vaiPara = "servico";
        }
        barraOpEscolhida.setLayoutParams(params);


    }
    private void alternarLayout(String valor1) {
        // Hides currently visible layout
        if (LayoutClientes.getVisibility() == View.VISIBLE) {
            animarSaidaPorBaixo(LayoutClientes, 500);
        } else if (LayoutCategorias.getVisibility() == View.VISIBLE) {
            animarSaidaPorBaixo(LayoutCategorias, 500);
        }

        // Set new value
        valor = valor1;

        // Update button states and show respective layout
        if (valor.equals("cliente")) {
            atualizarBotoes(true, true, false);
            vaiPara = "cliente";
            animarEntradaPorBaixo(LayoutClientes, 1000);
        } else if (valor.equals("categoria")) {
            atualizarBotoes(false, true, true);
            vaiPara = "categoria";
            animarEntradaPorBaixo(LayoutCategorias, 1000);
        } else if (valor.equals("servico")) {
            atualizarBotoes(true, false, true);
            vaiPara = "servico";
            animarEntradaPorBaixo(LayoutCategorias, 1000); // Se for necessário mais tarde
        }
    }

    private void atualizarBotoes(boolean categoria, boolean servico, boolean cliente) {
        buttonCategoria.setEnabled(categoria);
        buttonServico.setEnabled(servico);
        buttonCliente.setEnabled(cliente);
    }

    public void animarEntradaPorBaixo(View layout, long duracao) {
        // Inicializa a visibilidade como INVISIBLE para que o layout não apareça antes de animar
        layout.setVisibility(View.INVISIBLE);
        animarBarraOpEscolhida(1000);
        // Configura a posição inicial abaixo da tela
        layout.setTranslationY(layout.getHeight()); // Move o layout para fora da tela (abaixo)

        // Cria a animação para mover o layout até sua posição original (0f) e também alterar a opacidade (alpha)
        ObjectAnimator translateAnimator = ObjectAnimator.ofFloat(layout, "translationY", 0f);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(layout, "alpha", 0f, 1f);

        // Agrupa as duas animações
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translateAnimator, alphaAnimator);  // Reproduz ambas ao mesmo tempo

        // Define a duração da animação
        animatorSet.setDuration(duracao);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());

        // Inicia a animação
        animatorSet.start();
        layout.setVisibility(View.VISIBLE);


    }

    public void animarSaidaPorBaixo(View layout, long duracao) {
        // Calcula a posição final abaixo da tela
        float alturaTela = layout.getHeight();

        // Cria a animação para mover o layout para fora da tela (para baixo)
        ObjectAnimator animator = ObjectAnimator.ofFloat(layout, "translationY", alturaTela);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(layout, "alpha", 1f, 0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator, alphaAnimator);  // Reproduz ambas ao mesmo tempo

        animator.setDuration(duracao); // Define a duração da animação em milissegundos
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());

        // Ouve o final da animação
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // Após a animação, torna o layout invisível
                layout.setVisibility(View.INVISIBLE);
            }
        });

        // Inicia a animação
        animator.start();
    }

    public void animarBarraOpEscolhida(long duracao) {
        // Define o LayoutParams para aplicar mudanças de posição
        final ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) barraOpEscolhida.getLayoutParams();

        // Variáveis para controle de animação
        final float initialBias = params.horizontalBias;
        final float finalBias;
        final int initialLeftMargin = params.leftMargin;
        final int initialRightMargin = params.rightMargin;

        // Variáveis de padding
        final int targetLeftMargin;
        final int targetRightMargin;

        final int startToStart;
        final int endToEnd;

        // Determina qual é o destino da animação (Categoria ou Serviço)
        if ("categoria".equals(vaiPara)) {
            finalBias = 0.0f;  // Alinha à esquerda

            // Ajusta o padding lateral para a largura do botão "Categoria"
            targetLeftMargin = buttonCategoria.getPaddingLeft();
            startToStart = buttonCategoria.getId();  // Define o início do botão "Categoria"
            endToEnd = 0;  // Não há necessidade de alinhar com outro botão
            targetRightMargin = 0;  // Não precisa de padding direito
        } else if ("servico".equals(vaiPara)) {
            finalBias = 1.0f;  // Alinha à direita

            // Ajusta o padding lateral para a largura do botão "Serviço"
            targetRightMargin = buttonServico.getPaddingRight();
            startToStart = 0;  // Não há necessidade de alinhar com outro botão
            endToEnd = buttonServico.getId();  // Define o final do botão "Serviço"
            targetLeftMargin = 0;  // Não precisa de padding esquerdo
        } else if ("cliente".equals(vaiPara)) {
            finalBias = 0.5f;  // Centraliza
            targetLeftMargin = 0;  // Sem padding lateral
            targetRightMargin = 0;  // Sem padding lateral
            startToStart = 0;
            endToEnd = 0;
        } else {
            finalBias = initialBias;
            targetLeftMargin = initialLeftMargin;
            targetRightMargin = initialRightMargin;
            startToStart = 0;
            endToEnd = 0;
        }

        // Criação da animação para alterar o horizontalBias, leftMargin e rightMargin
        ValueAnimator biasAnimator = ValueAnimator.ofFloat(initialBias, finalBias);
        ValueAnimator leftMarginAnimator = ValueAnimator.ofInt(initialLeftMargin, targetLeftMargin);
        ValueAnimator rightMarginAnimator = ValueAnimator.ofInt(initialRightMargin, targetRightMargin);

        // Define a duração para as animações
        biasAnimator.setDuration(duracao);
        leftMarginAnimator.setDuration(duracao);
        rightMarginAnimator.setDuration(duracao);

        // Define a interpolação para as animações (usando um interpolador de desaceleração)
        biasAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        leftMarginAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        rightMarginAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        // Atualiza os parâmetros do Layout conforme a animação avança
        ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Atualiza o horizontalBias no LayoutParams
                params.horizontalBias = (float) biasAnimator.getAnimatedValue();

                // Atualiza os margens esquerdo e direito (levando em conta os paddings)
                // Só aplica os paddings se o destino for "categoria" ou "serviço"
                if ("categoria".equals(vaiPara)) {
                    params.leftMargin = (int) leftMarginAnimator.getAnimatedValue() + buttonCategoria.getPaddingLeft();
                    params.rightMargin = 0;  // Sem padding direito
                } else if ("servico".equals(vaiPara)) {
                    params.rightMargin = (int) rightMarginAnimator.getAnimatedValue() + buttonServico.getPaddingRight();
                    params.leftMargin = 0;  // Sem padding esquerdo
                } else {
                    params.leftMargin = (int) leftMarginAnimator.getAnimatedValue();
                    params.rightMargin = (int) rightMarginAnimator.getAnimatedValue();
                }


                // Aplica as alterações no Layout
                barraOpEscolhida.setLayoutParams(params);
            }
        };

        // Adiciona o update listener em todas as animações
        biasAnimator.addUpdateListener(updateListener);
        leftMarginAnimator.addUpdateListener(updateListener);
        rightMarginAnimator.addUpdateListener(updateListener);

        // Inicia as animações
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(biasAnimator, leftMarginAnimator, rightMarginAnimator);
        animatorSet.start();
    }


    private void referenciasRecyclerView()
    {
        recyclerViewClientes = findViewById(R.id.QuadradoLista);
        recyclerViewClientes.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewCategorias = findViewById(R.id.QuadradoListaCategoria);
        recyclerViewCategorias.setLayoutManager(new LinearLayoutManager(this));

//        recyclerViewServicos = findViewById(R.id.QuadradoLista);
//        recyclerViewServicos.setLayoutManager(new LinearLayoutManager(this));
    }

    private void referenciasButtonFlutuante()
    {
        voltarTela = findViewById(R.id.imageVoltarTela);
        voltarTela.setOnClickListener(this);
        imageMaisOpcoes = findViewById(R.id.imageMaisOpcoes);
        imageMaisOpcoes.setOnClickListener(this);
        imageAdicionarLista = findViewById(R.id.imageAdicionarLista);
        imageAdicionarLista.setOnClickListener(this);
        imageFiltrarLista = findViewById(R.id.imageFiltrarLista);
        imageFiltrarLista.setOnClickListener(this);
        buttonCategoria = findViewById(R.id.buttonCategoria);
        buttonCategoria.setOnClickListener(this);
        buttonCliente = findViewById(R.id.buttonClientes);
        buttonCliente.setOnClickListener(this);
        buttonServico = findViewById(R.id.buttonServicos);
        buttonServico.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imageVoltarTela) {
            finish();  // Finaliza a Activity atual e volta para a anterior
        } else if (view.getId() == R.id.imageMaisOpcoes) {
            if (imageFiltrarLista.getVisibility() == View.VISIBLE && imageAdicionarLista.getVisibility() == View.VISIBLE) {
                animacaoAdicionarVoltandoParaAdicionar();
            } else {
                animacaoFiltrarSaindoDeMaisOpcoes();
            }
        } else if (view.getId() == R.id.imageAdicionarLista) {
            Toast.makeText(getBaseContext(), "apertou em adicionar", Toast.LENGTH_SHORT).show();
            TelaAdicionarCategoriaClienteServico fragment = new TelaAdicionarCategoriaClienteServico();
            TelaAdicionarCategoriaClienteServico.LayoutExibir = 2;
            fragment.setClienteViewModel(clienteViewModel);
            fragment.show(getSupportFragmentManager(), fragment.getTag());

        } else if (view.getId() == R.id.imageFiltrarLista) {
            Toast.makeText(getBaseContext(), "apertou em filtrar", Toast.LENGTH_SHORT).show();
        } else if(view.getId() == R.id.buttonCategoria){
            valor = "categoria";
            alternarLayout(valor);
        } else if (view.getId() == R.id.buttonClientes){
            valor = "cliente";
            alternarLayout(valor);
        } else if (view.getId() == R.id.buttonServicos){
            valor = "servico";
            alternarLayout(valor);
        }
    }

    private void animacaoFiltrarSaindoDeMaisOpcoes() {
        imageMaisOpcoes.setEnabled(false);

        // Verifica se é a primeira vez que a animação está ocorrendo
        if (primeiraSubidaMaisOp) {
            // Inicializa a posição original da imagem como a altura da imageFiltrarLista
            posicaoOriginalFiltrar = imageFiltrarLista.getY();
        }

        // Define a posição inicial da imageFiltrarLista logo abaixo da imageMaisOpcoes
        imageFiltrarLista.setY(posicaoOriginalFiltrar);

        // A posição final será 180 pixels acima de imageMaisOpcoes
        float posicaoFiltrarAcimaMaisOp = imageMaisOpcoes.getY() - 180;

        // Garante que imageFiltrarLista esteja visível e não interativa durante a animação
        imageFiltrarLista.setVisibility(View.VISIBLE);
        imageFiltrarLista.setEnabled(false);

        // Cria o animador para mover imageFiltrarLista de sua posição inicial para a posição final
        ValueAnimator animator = ValueAnimator.ofFloat(imageFiltrarLista.getY(), posicaoFiltrarAcimaMaisOp);

        // Define a duração da animação
        animator.setDuration(300); // Pode ser ajustada conforme necessário
        animator.setInterpolator(new AccelerateDecelerateInterpolator()); // Interpolação suave

        // Atualiza a posição de imageFiltrarLista durante a animação
        animator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            imageFiltrarLista.setY(animatedValue);  // Atualiza a posição durante a animação
        });

        // Ouve o final da animação para garantir que a visibilidade e interatividade da imagem estejam corretas
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // Garante que a imageFiltrarLista tenha a posição final e esteja visível
                imageFiltrarLista.setVisibility(View.VISIBLE);
                animacaoSubirAdicionarDentroFiltrar();
            }
        });

        // Inicia a animação
        animator.start();
    }


    private void animacaoFiltrarVoltandoParaMaisOpcoes() {
        imageMaisOpcoes.setEnabled(false);
        // Garantir que a imageFiltrarLista está visível e configurada corretamente
        imageFiltrarLista.setVisibility(View.VISIBLE);
        imageFiltrarLista.setEnabled(false);

        // Criar o animador para mover imageFiltrarLista de volta para sua posição original
        ValueAnimator animator = ValueAnimator.ofFloat(imageFiltrarLista.getY(), posicaoOriginalFiltrar);

        // Definir a duração da animação (lentamente descendo)
        animator.setDuration(300);  // A duração pode ser ajustada conforme necessário
        animator.setInterpolator(new AccelerateDecelerateInterpolator()); // Interpolação suave

        // Atualizar a posição de imageFiltrarLista durante a animação
        animator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            imageFiltrarLista.setY(animatedValue);
        });

        // Adicionar um ouvinte para garantir que a animação finalize e a view esteja configurada corretamente
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // Garantir que imageFiltrarLista tenha a posição correta e esteja visível
                imageFiltrarLista.setVisibility(View.INVISIBLE);
                imageMaisOpcoes.setEnabled(true);
            }
        });

        // Iniciar a animação
        animator.start();
    }

    private void animacaoSubirAdicionarDentroFiltrar() {
        imageMaisOpcoes.setEnabled(false);

        if (primeiraSubidaMaisOp) {
            // Inicializa a posição original da imagem como a altura de imageAdicionarLista
            posicaoOriginalAdicionar = imageAdicionarLista.getY(); // Corrigido: posição de imageAdicionarLista
            primeiraSubidaMaisOp = false;  // Marca que a animação já foi iniciada
        }

        // Define a posição inicial da imageAdicionarLista como a posição atual dela
        imageAdicionarLista.setY(posicaoOriginalAdicionar);

        // A posição final será 180 pixels acima de imageFiltrarLista
        float posicaoFiltrarAcimaMaisOp = imageFiltrarLista.getY() - 180; // Correção: posicione-a acima de imageFiltrarLista

        // Garante que imageAdicionarLista esteja visível e não interativa durante a animação
        imageAdicionarLista.setVisibility(View.VISIBLE);
        imageAdicionarLista.setEnabled(false);

        // Cria o animador para mover imageAdicionarLista de sua posição atual até a nova posição
        ValueAnimator animator = ValueAnimator.ofFloat(imageAdicionarLista.getY(), posicaoFiltrarAcimaMaisOp);

        // Define a duração da animação
        animator.setDuration(300); // Pode ser ajustada conforme necessário
        animator.setInterpolator(new AccelerateDecelerateInterpolator()); // Interpolação suave

        // Atualiza a posição de imageAdicionarLista durante a animação
        animator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            imageAdicionarLista.setY(animatedValue);  // Atualiza a posição durante a animação
        });

        // Ouve o final da animação para garantir que a visibilidade e interatividade da imagem estejam corretas
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // Garante que imageAdicionarLista tenha a posição final e esteja visível
                imageAdicionarLista.setVisibility(View.VISIBLE);
                imageFiltrarLista.setEnabled(true);
                imageMaisOpcoes.setEnabled(true);
                imageAdicionarLista.setEnabled(true);
            }
        });

        // Inicia a animação
        animator.start();
    }

    private void animacaoAdicionarVoltandoParaAdicionar() {
        imageMaisOpcoes.setEnabled(false);
        // Garantir que a imageFiltrarLista está visível e configurada corretamente
        imageAdicionarLista.setVisibility(View.VISIBLE);
        imageAdicionarLista.setEnabled(false);

        // Criar o animador para mover imageFiltrarLista de volta para sua posição original
        ValueAnimator animator = ValueAnimator.ofFloat(imageAdicionarLista.getY(), posicaoOriginalAdicionar);

        // Definir a duração da animação (lentamente descendo)
        animator.setDuration(300);  // A duração pode ser ajustada conforme necessário
        animator.setInterpolator(new AccelerateDecelerateInterpolator()); // Interpolação suave

        // Atualizar a posição de imageFiltrarLista durante a animação
        animator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            imageAdicionarLista.setY(animatedValue);
        });

        // Adicionar um ouvinte para garantir que a animação finalize e a view esteja configurada corretamente
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // Garantir que imageFiltrarLista tenha a posição correta e esteja visível
                imageAdicionarLista.setVisibility(View.INVISIBLE);
                animacaoFiltrarVoltandoParaMaisOpcoes();
            }
        });

        // Iniciar a animação
        animator.start();
    }

    public int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    //Funcao chamada ao clicar na lupa do recyclerView (tela detalhes cliente)
    public void onClienteClicked(Cliente cliente) {
        // Aqui você pode fazer algo com o cliente clicado, como pegar o ID
        long clienteId = cliente.getId();  // Ou qualquer outra propriedade do cliente
        String ClienteNome = cliente.getNome();
        String ClieneTelefone = cliente.getTelefone();

        // Passa o ID do cliente para o BottomSheet usando um Bundle
        Bundle bundle = new Bundle();
        bundle.putLong("clienteId", clienteId);
        bundle.putString("clienteNome", ClienteNome);
        bundle.putString("clienteTelefone", ClieneTelefone);

        // Exemplo: Você pode iniciar uma nova Activity com os detalhes do cliente
        BottomSheetDetalhesCliente fragment = new BottomSheetDetalhesCliente();
        fragment.setArguments(bundle);
        fragment.setClienteViewModel(clienteViewModel);
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    //Será usada dentro da class de abrir o bottom sheet de detalhes
//    private void Excluir(Cliente cliente)
//    {
//        // Aqui você pode fazer algo com o cliente clicado, como pegar o ID
//        long clienteId = cliente.getId();  // Ou qualquer outra propriedade do cliente
//        String ClienteNome = cliente.getNome();
//        String ClieneTelefone = cliente.getTelefone();
//
//        // Passa o ID do cliente para o BottomSheet usando um Bundle
//        Bundle bundle = new Bundle();
//        bundle.putLong("clienteId", clienteId);
//        bundle.putString("clienteNome", ClienteNome);
//        bundle.putString("clienteTelefone", ClieneTelefone);
//
//        // Exemplo: Você pode iniciar uma nova Activity com os detalhes do cliente
//        BottomSheetDeletarCliente fragment = new BottomSheetDeletarCliente();
//        fragment.setArguments(bundle);
//        fragment.setClienteViewModel(clienteViewModel);
//        fragment.show(getSupportFragmentManager(), fragment.getTag());
//    }

}
