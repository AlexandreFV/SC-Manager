    package com.example.scmanager;

    import android.animation.ValueAnimator;
    import android.graphics.drawable.Drawable;
    import android.graphics.drawable.LayerDrawable;
    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.Toast;

    import androidx.constraintlayout.widget.ConstraintLayout;

    import com.example.scmanager.Gerenciamento.ViewModel.ClienteViewModel;
    import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
    import com.google.android.material.textfield.TextInputEditText;

    public class TelaAdicionarCategoriaClienteServico extends BottomSheetDialogFragment implements View.OnClickListener {

        private ConstraintLayout AddCategoria;
        private ConstraintLayout AddCliente;
        private ConstraintLayout AddServico;
        public static int LayoutExibir = 0;
        private Button CategoriaAdd;
        private Button ClienteAdd;
        private Button ServicoAdd;
        private Button buttonSalvarCliente;
        private Button buttonSalvarCategoria;
        private ClienteViewModel clienteViewModel;


        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate o layout do Bottom Sheet
            View view = inflater.inflate(R.layout.dialog_adicionar_cliente, container, false);

            AddCategoria = view.findViewById(R.id.FundoLayoutAddCategoria);
            AddCliente = view.findViewById(R.id.FundoLayoutAddCliente);
            AddServico = view.findViewById(R.id.FundoLayoutAddServico);

            // Configura o onClickListener para os botões
            CategoriaAdd = view.findViewById(R.id.CategoriaButtonAdd);
            CategoriaAdd.setOnClickListener(this);
            ClienteAdd = view.findViewById(R.id.btnAddCliente);
            ClienteAdd.setOnClickListener(this);
            ServicoAdd = view.findViewById(R.id.btnAddServico);
            ServicoAdd.setOnClickListener(this);

            buttonSalvarCategoria = view.findViewById(R.id.buttonSalvarCategoria);
            buttonSalvarCategoria.setOnClickListener(this);
            buttonSalvarCliente = view.findViewById(R.id.buttonSalvarCliente);
            buttonSalvarCliente.setOnClickListener(this);

            exibirLayout(LayoutExibir);

            // Inicializa o controlador do banco (Singleton)
            return view;
        }

        public void setClienteViewModel(ClienteViewModel clienteViewModel) {
            this.clienteViewModel = clienteViewModel;
        }

        private void exibirLayout(int layoutAExibir)
        {
            if(layoutAExibir == 1)
            {
                AddCategoria.setVisibility(View.VISIBLE);
                AddCliente.setVisibility(View.GONE);
                AddServico.setVisibility(View.GONE);
            } else if(layoutAExibir == 2){
                AddCliente.setVisibility(View.VISIBLE);
                AddCategoria.setVisibility(View.GONE);
                AddServico.setVisibility(View.GONE);
            }else if(layoutAExibir == 3){
                AddServico.setVisibility(View.VISIBLE);
                AddCategoria.setVisibility(View.GONE);
                AddCliente.setVisibility(View.GONE);
            }
            buttonSelecionado(LayoutExibir);

        }

        private void buttonSelecionado(int layoutAExibir1) {
            if (layoutAExibir1 == 1) {
                alterarBackgroundGradualmente(CategoriaAdd); // Troca o background do botão de Categoria
                alterarBackgroundGradualmenteParaSemGradiente(ClienteAdd);
                alterarBackgroundGradualmenteParaSemGradiente(ServicoAdd);
            } else if (layoutAExibir1 == 2) {
                alterarBackgroundGradualmente(ClienteAdd); // Troca o background do botão de Cliente
                alterarBackgroundGradualmenteParaSemGradiente(CategoriaAdd);
                alterarBackgroundGradualmenteParaSemGradiente(ServicoAdd);
            } else if (layoutAExibir1 == 3) {
                alterarBackgroundGradualmente(ServicoAdd); // Troca o background do botão de Serviço
                alterarBackgroundGradualmenteParaSemGradiente(CategoriaAdd);
                alterarBackgroundGradualmenteParaSemGradiente(ClienteAdd);
            }
        }


        private void alterarBackgroundGradualmente(Button button) {
            // Obtém o novo Drawable
            final Drawable newDrawable = getResources().getDrawable(R.drawable.button_add_layout_gradiente, null);

            // Verifica se o Drawable é um LayerDrawable
            if (newDrawable instanceof LayerDrawable) {
                final LayerDrawable layerDrawable = (LayerDrawable) newDrawable;

                // Cria o ValueAnimator para animar a transição
                ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
                animator.setDuration(500); // Duração de 500ms para a animação (ajuste conforme necessário)
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float progress = (float) animation.getAnimatedValue();

                        // Anima a alteração de opacidade (se necessário) ou outras propriedades
                        layerDrawable.setAlpha((int) (progress * 255)); // Ajuste da opacidade conforme a animação

                        // Aplica o novo Drawable progressivamente
                        button.setBackground(layerDrawable);
                    }
                });

                animator.start(); // Inicia a animação
            } else {
                // Caso o Drawable não seja do tipo LayerDrawable, aplica diretamente
                button.setBackground(newDrawable);
            }
        }

        private void alterarBackgroundGradualmenteParaSemGradiente(Button button) {
            // Obtém o novo Drawable
            final Drawable newDrawable = getResources().getDrawable(R.drawable.button_add_layout_no_gradiente, null);

            // Verifica se o Drawable é um LayerDrawable
            if (newDrawable instanceof LayerDrawable) {
                final LayerDrawable layerDrawable = (LayerDrawable) newDrawable;

                // Cria o ValueAnimator para animar a transição
                ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
                animator.setDuration(500); // Duração de 500ms para a animação (ajuste conforme necessário)
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float progress = (float) animation.getAnimatedValue();

                        // Anima a alteração de opacidade (se necessário) ou outras propriedades
                        layerDrawable.setAlpha((int) (progress * 255)); // Ajuste da opacidade conforme a animação

                        // Aplica o novo Drawable progressivamente
                        button.setBackground(layerDrawable);
                    }
                });

                animator.start(); // Inicia a animação
            } else {
                // Caso o Drawable não seja do tipo LayerDrawable, aplica diretamente
                button.setBackground(newDrawable);
            }
        }

        private void criarCliente(View view) {
            TextInputEditText inputNomeCliente = view.findViewById(R.id.inputNomeCliente);
            TextInputEditText inputTelefone = view.findViewById(R.id.inputTelefone);

            if (inputNomeCliente == null || inputTelefone == null) {
                showToast("Erro ao encontrar os campos de entrada no layout.");
                return;
            }

            String valorNomeCliente = inputNomeCliente.getText().toString().trim();
            String valorTelefone = inputTelefone.getText().toString().trim();

            if (valorNomeCliente.isEmpty() || valorTelefone.isEmpty()) {
                showToast("Todos os campos são obrigatórios.");
                return;
            }

            // Chama o ViewModel para adicionar o cliente
            clienteViewModel.adicionarCliente(valorNomeCliente, valorTelefone);
            dismiss();
        }

        private void showToast(String mensagem) {
            Toast.makeText(getContext(), mensagem, Toast.LENGTH_SHORT).show();
        }

        private void criarCategoria(View view) {
            TextInputEditText inputNomeCategoria = view.findViewById(R.id.inputNomeCategoria);

            if (inputNomeCategoria == null) {
                showToast("Erro ao encontrar os campos de entrada no layout.");
                return;
            }

            String valorNomeCategoria = inputNomeCategoria.getText().toString().trim();

            if (valorNomeCategoria.isEmpty()) {
                showToast("Todos os campos são obrigatórios.");
                return;
            }

        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.CategoriaButtonAdd) {
                exibirLayout(LayoutExibir = 1);
            } else if (view.getId() == R.id.btnAddCliente) {
                exibirLayout(LayoutExibir = 2);
            } else if (view.getId() == R.id.btnAddServico) {
                exibirLayout(LayoutExibir = 3);
            } else if(view.getId() == R.id.buttonSalvarCategoria){
                criarCategoria(requireView()); // Passa a View principal do fragment
            } else if(view.getId() == R.id.buttonSalvarCliente){
                criarCliente(requireView()); // Passa a View principal do fragment
            }
        }
    }
