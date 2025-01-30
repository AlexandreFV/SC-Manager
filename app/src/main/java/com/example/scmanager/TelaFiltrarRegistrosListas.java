package com.example.scmanager;

import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class TelaFiltrarRegistrosListas extends BottomSheetDialogFragment implements View.OnClickListener{

    private ConstraintLayout FiltrarCategoria;
    private ConstraintLayout FiltrarCliente;
    private ConstraintLayout FiltrarServico;

    private Button SelecionarCategoria;
    private Button SelecionarCliente;
    private Button SelecionarServico;

    private Button FiltrarClienteButton;
    private Button FiltrarCategoriaButton;
    private Button FiltrarServicoButton;
    private String qualLayoutAddMostrar;

    private Group GrupoFiltroPagamentoRealiado;
    private Group GrupoFiltroPagamentoEstipulado;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_filtrar_listas, container, false);

        qualLayoutAddMostrar = getArguments().getString("veioDe", "");

        FiltrarCategoria = view.findViewById(R.id.FundoLayoutAddCategoria);
        FiltrarCliente = view.findViewById(R.id.FundoLayoutAddCliente);
        FiltrarServico = view.findViewById(R.id.FundoLayoutAddServico);

        GrupoFiltroPagamentoRealiado = view.findViewById(R.id.GrupoFiltroDataPag);
        GrupoFiltroPagamentoEstipulado = view.findViewById(R.id.GrupoOrdenarPagamentoEstipuladoVencidoNVencido);

        SelecionarCategoria = view.findViewById(R.id.CategoriaButtonFiltrar);
        SelecionarCategoria.setOnClickListener(this);
        SelecionarCliente = view.findViewById(R.id.btnFiltrarCliente);
        SelecionarCliente.setOnClickListener(this);
        SelecionarServico = view.findViewById(R.id.btnFiltrarServico);
        SelecionarServico.setOnClickListener(this);

        FiltrarCategoriaButton = view.findViewById(R.id.buttonSalvarCategoria);
        FiltrarCategoriaButton.setOnClickListener(this);
        FiltrarClienteButton = view.findViewById(R.id.buttonFiltrarCliente);
        FiltrarClienteButton.setOnClickListener(this);
        FiltrarServicoButton = view.findViewById(R.id.buttonSalvarServico);
        FiltrarServicoButton.setOnClickListener(this);

        exibirLayout(qualLayoutAddMostrar);
        return view;
    }


    private void exibirLayout(String layoutAExibir)
    {
        if(layoutAExibir.equals("categoria"))
        {
            FiltrarCategoria.setVisibility(View.VISIBLE);
            FiltrarCliente.setVisibility(View.GONE);
            FiltrarServico.setVisibility(View.GONE);
        } else if(layoutAExibir.equals("cliente")){
            FiltrarCliente.setVisibility(View.VISIBLE);
            FiltrarCategoria.setVisibility(View.GONE);
            FiltrarServico.setVisibility(View.GONE);
        }else if(layoutAExibir.equals("servico")){
            FiltrarServico.setVisibility(View.VISIBLE);
            FiltrarCategoria.setVisibility(View.GONE);
            FiltrarCliente.setVisibility(View.GONE);
        }
        buttonSelecionado(layoutAExibir);

    }

    private void buttonSelecionado(String layoutAExibir1) {
        if (layoutAExibir1.equals("categoria")) {
            alterarBackgroundGradualmente(SelecionarCategoria); // Troca o background do botão de Categoria
            alterarBackgroundGradualmenteParaSemGradiente(SelecionarCliente);
            alterarBackgroundGradualmenteParaSemGradiente(SelecionarServico);
        } else if (layoutAExibir1.equals("cliente")) {
            alterarBackgroundGradualmente(SelecionarCliente); // Troca o background do botão de Cliente
            alterarBackgroundGradualmenteParaSemGradiente(SelecionarCategoria);
            alterarBackgroundGradualmenteParaSemGradiente(SelecionarServico);
        } else if (layoutAExibir1.equals("servico")) {
            alterarBackgroundGradualmente(SelecionarServico); // Troca o background do botão de Serviço
            alterarBackgroundGradualmenteParaSemGradiente(SelecionarCategoria);
            alterarBackgroundGradualmenteParaSemGradiente(SelecionarCliente);
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


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.CategoriaButtonFiltrar) {
            exibirLayout(qualLayoutAddMostrar = "categoria");
        } else if (view.getId() == R.id.btnFiltrarCliente) {
            exibirLayout(qualLayoutAddMostrar = "cliente");
        } else if (view.getId() == R.id.btnFiltrarServico) {
            exibirLayout(qualLayoutAddMostrar = "servico");
        }
    }
}
