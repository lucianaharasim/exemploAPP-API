package com.example.novoaplicativo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;
import java.util.Locale;

public class MenuActivity extends AppCompatActivity {
private LinearLayout container;
private ApiClient apiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        container = findViewById(R.id.containerProdutos);
        Button btnNovoProduto = findViewById(R.id.btnNovoProduto);
        apiClient = new ApiClient(this);

        btnNovoProduto.setOnClickListener(v->abrirFormulario(null));
        carregarProdutos();
    }
    private void  carregarProduto(){
        mostrarMensagem("Carregando produtos...");
        apiClient.listarProdutos(this::criarLista, this::mostrarErro);
    }

    private void criarLista(List<Produto> produto){
        container.removeAllViews();
        if(produto.isEmpty()){
            mostrarMensagem("Nenhum produto cadastrado");
            return;
        }
        for(Produto produto : produtos){
            View item = getLayoutInflater().inflate(R.layout.item_produto,
                    container, false);
            ImageView img = item.findViewById(R.id.imgProduto);
            TextView txtNome = item.findViewById(R.id.txtNomeProduto);
            TextView txtPreco = item.findViewById(R.id.txtPrecoProduto);

            img.setImageResource((R.drawable.hamburguer));
            txtNome.setText((produto.getNome()));
            txtPreco.setText(String.format(Locale.getDefault(),"R$" +
                            "%.2f",produto.getPreco()));

            item.setOnClickListener(v->apiClient.buscarProduto(produto.
                    getId(),this::abrirFormulario,this:: mostrarErro));

            item.setOnClickListener((v -> {
                confirmarExclusao(produto);
                return true;
            }));
                    container.addView(item);
        }
    }
}