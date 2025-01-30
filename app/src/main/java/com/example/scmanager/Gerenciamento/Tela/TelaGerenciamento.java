package com.example.scmanager.Gerenciamento.Tela;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scmanager.BancoDeDados.ControladorBancoDeDados;
import com.example.scmanager.Gerenciamento.Adapter.CategoriaAdapter;
import com.example.scmanager.Gerenciamento.Adapter.ClienteAdapter;
import com.example.scmanager.Gerenciamento.Adapter.ServicoAdapter;
import com.example.scmanager.Gerenciamento.Objetos.Categoria;
import com.example.scmanager.Gerenciamento.Objetos.Cliente;
import com.example.scmanager.Gerenciamento.Objetos.Servico;
import com.example.scmanager.Gerenciamento.Tela.Categoria.BottomSheetDetalhesCategoria;
import com.example.scmanager.Gerenciamento.Tela.Cliente.BottomSheetDetalhesCliente;
import com.example.scmanager.Gerenciamento.Tela.Servico.BottomSheetDetalhesServico;
import com.example.scmanager.Gerenciamento.ViewModel.CategoriaViewModel;
import com.example.scmanager.Gerenciamento.ViewModel.ClienteViewModel;
import com.example.scmanager.Gerenciamento.ViewModel.ServicoViewModel;
import com.example.scmanager.R;
import com.example.scmanager.TelaAdicionarCategoriaClienteServico;
import com.example.scmanager.TelaFiltrarRegistrosListas;
import com.google.android.material.tabs.TabLayout;

import android.animation.ObjectAnimator;

import org.checkerframework.checker.units.qual.C;

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
    private ConstraintLayout LayoutServico;


    //Objetos que exibirão os dados de respectivo gerenciamento
    private RecyclerView recyclerViewClientes;
    private RecyclerView recyclerViewCategorias;
    private RecyclerView recyclerViewServicos;


    private ClienteAdapter clienteAdapter;
    private ClienteViewModel clienteViewModel;
    private ServicoViewModel servicoViewModel;
    private CategoriaViewModel categoriaViewModel;
    private CategoriaAdapter categoriaAdapter;
    private ServicoAdapter servicoAdapter;

    //Indicativo de que opcao escolheu e está
    private String vaiPara = "";
    //Valor para saber qual layout exibir
    private String valor;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_gerenciamento_cliente);

        tabLayout = findViewById(R.id.tabLayout);

        recyclerViewClientes = findViewById(R.id.QuadradoLista);
        recyclerViewCategorias = findViewById(R.id.QuadradoListaCategoria);
        recyclerViewServicos = findViewById(R.id.QuadradoListaServ);
        recyclerViewClientes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCategorias.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewServicos.setLayoutManager(new LinearLayoutManager(this));

        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);
        servicoViewModel = new ViewModelProvider(this).get(ServicoViewModel.class);
        categoriaViewModel = new ViewModelProvider(this).get(CategoriaViewModel.class);

        LayoutClientes = findViewById(R.id.LayoutClientes);
        LayoutCategorias = findViewById(R.id.GrupoIconesCategoria);
        LayoutServico = findViewById(R.id.GrupoLayoutServico);
        voltarTela = findViewById(R.id.imageVoltarTela);
        imageMaisOpcoes = findViewById(R.id.imageMaisOpcoes);
        imageAdicionarLista = findViewById(R.id.imageAdicionarLista);
        imageFiltrarLista = findViewById(R.id.imageFiltrarLista);

        voltarTela.setOnClickListener(this);
        imageMaisOpcoes.setOnClickListener(this);
        imageAdicionarLista.setOnClickListener(this);
        imageFiltrarLista.setOnClickListener(this);

        referenciarLayout();

        clienteViewModel.getListaClientes().observe(this, new Observer<List<Cliente>>() {
            @Override
            public void onChanged(List<Cliente> clientes) {
                // Atualize o Adapter com a nova lista de clientes
                if (clienteAdapter == null) {
                    clienteAdapter = new ClienteAdapter(TelaGerenciamento.this, clientes);
                    recyclerViewClientes.setAdapter(clienteAdapter);
                } else {
                    clienteAdapter.setCliente(clientes);
                    clienteAdapter.notifyDataSetChanged();
                }

                // Atualiza a altura do RecyclerView com base na quantidade de itens
                int itemCount = clientes.size();
                if (itemCount == 0) {
                    recyclerViewClientes.getLayoutParams().height = 0;
                }
                recyclerViewClientes.requestLayout();
                servicoViewModel.carregarServicos();
            }
        });

        ControladorBancoDeDados db = ControladorBancoDeDados.getInstance(getApplicationContext());
        db.verServico();

        categoriaViewModel.getListaCategorias().observe(this, new Observer<List<Categoria>>() {
            @Override
            public void onChanged(List<Categoria> categorias) {
                if (categoriaAdapter == null) {
                    categoriaAdapter = new CategoriaAdapter(TelaGerenciamento.this, categorias);
                    recyclerViewCategorias.setAdapter(categoriaAdapter);
                } else {
                    categoriaAdapter.setCategoria(categorias);
                    categoriaAdapter.notifyDataSetChanged();
                }

                int itemCount = categorias.size();
                if (itemCount == 0) {
                    recyclerViewCategorias.getLayoutParams().height = 0;
                }
                recyclerViewCategorias.requestLayout();
                servicoViewModel.carregarServicos();
            }
        });


        servicoViewModel.getListaServico().observe(this, new Observer<List<Servico>>() {
            @Override
            public void onChanged(List<Servico> servicos) {
                // Atualize o Adapter com a nova lista de serviços
                if (servicoAdapter == null) {
                    servicoAdapter = new ServicoAdapter(TelaGerenciamento.this, servicos, categoriaViewModel, clienteViewModel);
                    recyclerViewServicos.setAdapter(servicoAdapter);
                } else {
                    servicoAdapter.setServico(servicos);
                    servicoAdapter.notifyDataSetChanged();
                }

                // Atualiza a altura do RecyclerView com base na quantidade de itens
                int itemCount = servicos.size();
                if (itemCount == 0) {
                    recyclerViewServicos.getLayoutParams().height = 0;
                }
                recyclerViewServicos.requestLayout();
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String direcao = "";
                String veioDe = vaiPara;
                switch (tab.getPosition()) {
                    case 0:
                        if(veioDe == "servico") direcao = "esquerda";animarTransicaoLayouts(LayoutServico,LayoutCategorias,direcao);
                        if(veioDe == "cliente") direcao = "esquerda";animarTransicaoLayouts(LayoutClientes,LayoutCategorias,direcao);
                        if(veioDe == "categoria")LayoutCategorias.setVisibility(View.VISIBLE);
                        vaiPara = "categoria";
                        break;
                    case 1:
                        if(veioDe == "servico") direcao = "esquerda";animarTransicaoLayouts(LayoutServico,LayoutClientes,direcao);
                        if(veioDe == "categoria") direcao = "direita";animarTransicaoLayouts(LayoutCategorias,LayoutClientes,direcao);
                        if(veioDe == "cliente")LayoutClientes.setVisibility(View.VISIBLE);
                        vaiPara = "cliente";
                        break;
                    case 2:
                        if(veioDe == "cliente") direcao = "direita";animarTransicaoLayouts(LayoutClientes,LayoutServico,direcao);
                        if(veioDe == "categoria") direcao = "direita";animarTransicaoLayouts(LayoutCategorias,LayoutServico,direcao);
                        if(veioDe == "servico")LayoutServico.setVisibility(View.VISIBLE);
                        vaiPara = "servico";
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Implementar caso precise realizar algo ao desmarcar a aba
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        LayoutCategorias.setVisibility(View.VISIBLE);
                        vaiPara = "categoria";
                        break;
                    case 1:
                        LayoutClientes.setVisibility(View.VISIBLE);
                        vaiPara = "cliente";
                        break;
                    case 2:
                        LayoutServico.setVisibility(View.VISIBLE);
                        vaiPara = "servico";
                        break;
                }
            }
        });
    }

    private void referenciarLayout()
    {
        Intent intent = getIntent();
        // Recupera o valor passado no Intent
        valor = intent.getStringExtra("botao_apertado");

        if(valor.equals("categoria"))
        {
            LayoutCategorias.setVisibility(View.VISIBLE);
            LayoutClientes.setVisibility(View.INVISIBLE);
            LayoutServico.setVisibility(View.INVISIBLE);
            tabLayout.selectTab(tabLayout.getTabAt(0)); // 0 é a posição da aba "Categoria"
            vaiPara = "categoria";
        }
        else if (valor.equals("cliente"))
        {
            LayoutClientes.setVisibility(View.VISIBLE);
            LayoutCategorias.setVisibility(View.INVISIBLE);
            LayoutServico.setVisibility(View.INVISIBLE);
            tabLayout.selectTab(tabLayout.getTabAt(1)); // 1 é a posição da aba "Cliente"
            // Quando clicado no "serviço" novamente
            vaiPara = "cliente";
        }
        else if (valor.equals("servico"))
        {
            LayoutServico.setVisibility(View.VISIBLE);
            LayoutCategorias.setVisibility(View.INVISIBLE);
            LayoutClientes.setVisibility(View.INVISIBLE);
            tabLayout.selectTab(tabLayout.getTabAt(2)); // 2 é a posição da aba "Servico"
            vaiPara = "servico";
        }

    }

    private void animarTransicaoLayouts(View layoutAnterior, View layoutNovo, String lado) {
        // Torna o layout anterior visível antes de iniciar a animação

        // Verifica para qual direção a animação será feita
        float inicioXLayoutAnterior = 0f;
        float fimXLayoutAnterior = 0f;
        float inicioXLayoutNovo = 0f;
        float fimXLayoutNovo = 0f;

        // Caso o lado seja "esquerda", os layouts vão da direita para a esquerda
        if (lado.equals("esquerda")) {
            // Layout anterior vai para a direita (fora da tela)
            inicioXLayoutAnterior = 0f;
            fimXLayoutAnterior = layoutAnterior.getWidth(); // Move para a direita
            // Novo layout vem da esquerda (fora da tela) para a posição original
            inicioXLayoutNovo = -layoutNovo.getWidth(); // Começa fora da tela à esquerda
            fimXLayoutNovo = 0f; // Vai para a posição original
        }
        // Caso o lado seja "direita", os layouts vão da esquerda para a direita
        else if (lado.equals("direita")) {
            // Layout anterior vai para a esquerda (fora da tela)
            inicioXLayoutAnterior = 0f;
            fimXLayoutAnterior = -layoutAnterior.getWidth(); // Move para a esquerda
            // Novo layout vem da direita (fora da tela) para a posição original
            inicioXLayoutNovo = layoutNovo.getWidth(); // Começa fora da tela à direita
            fimXLayoutNovo = 0f; // Vai para a posição original
        }

        // Primeiro, esconda o layout anterior com a animação de "arraste para fora"
        ObjectAnimator animaLayoutAnterior = ObjectAnimator.ofFloat(layoutAnterior, "translationX", inicioXLayoutAnterior, fimXLayoutAnterior);
        animaLayoutAnterior.setDuration(300); // Duração da animação (em milissegundos)

        // Depois, traga o layout novo de fora para dentro com a animação de "arraste para dentro"
        layoutNovo.setTranslationX(inicioXLayoutNovo); // Começa fora da tela
        ObjectAnimator animaLayoutNovo = ObjectAnimator.ofFloat(layoutNovo, "translationX", fimXLayoutNovo);
        animaLayoutNovo.setDuration(300); // Duração da animação (em milissegundos)

        // Crie um AnimatorSet para rodar as animações ao mesmo tempo
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animaLayoutAnterior, animaLayoutNovo);

        // Ao terminar a animação, o layout anterior ficará invisível
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                layoutNovo.setVisibility(View.VISIBLE); // Torna o novo layout visível
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // Torna o layout anterior invisível após a animação
                layoutNovo.setVisibility(View.VISIBLE); // Torna o novo layout visível
                layoutAnterior.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                layoutNovo.setVisibility(View.VISIBLE); // Torna o novo layout visível
                layoutAnterior.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // Nada a fazer se a animação for repetida
            }
        });

        // Inicia a animação
        animatorSet.start();
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
            Bundle bundle = new Bundle();
            bundle.putString("veioDe",vaiPara);
            fragment.setClienteViewModel(clienteViewModel);
            fragment.setCategoriaViewModel(categoriaViewModel);
            fragment.setServicoViewModel(servicoViewModel);
            fragment.setArguments(bundle);
            fragment.show(getSupportFragmentManager(), fragment.getTag());

        } else if (view.getId() == R.id.imageFiltrarLista) {
            Toast.makeText(getBaseContext(), "apertou em filtrar", Toast.LENGTH_SHORT).show();
            TelaFiltrarRegistrosListas fragment = new TelaFiltrarRegistrosListas();
            Bundle bundle = new Bundle();
            bundle.putString("veioDe",vaiPara);
            fragment.setArguments(bundle);
            fragment.show(getSupportFragmentManager(), fragment.getTag());

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
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    public void onCategoriaClicked(Categoria categoria) {
        long categoriaId = categoria.getId();
        String categoriaNome = categoria.getNome();

        Bundle bundle = new Bundle();
        bundle.putLong("categoriaId", categoriaId);
        bundle.putString("categoriaNome", categoriaNome);

        // Exemplo: Você pode iniciar uma nova Activity com os detalhes do cliente
        BottomSheetDetalhesCategoria fragment = new BottomSheetDetalhesCategoria();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    public void onServicoClicked(Servico servico) {
        long servicoId = servico.getId();
        Integer tipoServico = servico.getTipoServico();
        Integer idCliente = servico.getIdCliente();
        Double valor = servico.getValor();
        String dataAceiteServico = servico.getDataAceiteServico();
        Integer estado = servico.getEstado();
        String dataPagamento = servico.getDataPagamento();

        Bundle bundle = new Bundle();
        bundle.putLong("servicoId", servicoId);
        bundle.putInt("tipoServico",tipoServico);
        bundle.putInt("idCliente",idCliente);
        bundle.putDouble("valor",valor);
        bundle.putString("dataAceiteServico",dataAceiteServico);
        bundle.putInt("estado",estado);
        bundle.putString("dataPagamento",dataPagamento);

        // Exemplo: Você pode iniciar uma nova Activity com os detalhes do cliente
        BottomSheetDetalhesServico fragment = new BottomSheetDetalhesServico();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }
}
