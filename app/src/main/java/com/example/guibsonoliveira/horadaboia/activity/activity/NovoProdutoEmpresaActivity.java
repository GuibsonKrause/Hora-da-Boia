package com.example.guibsonoliveira.horadaboia.activity.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.guibsonoliveira.horadaboia.R;
import com.example.guibsonoliveira.horadaboia.activity.helper.UsuarioFirebase;
import com.example.guibsonoliveira.horadaboia.activity.model.Produto;
import com.google.firebase.auth.FirebaseAuth;

public class NovoProdutoEmpresaActivity extends AppCompatActivity
{

    private EditText editProdutoNome,
            editProdutoDescricao,
            editProdutoPreco;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        /*Configurações Iniciais*/
        inicializarComponentes();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        /*Configuração ToolBar*/
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void validarDadosProduto(View view)
    {
        /*Valida se os campos foram preenchidos*/
        String nome = editProdutoNome.getText().toString();
        String descricao = editProdutoDescricao.getText().toString();
        String preco = editProdutoPreco.getText().toString();

        if (!nome.isEmpty())
        {
            if (!descricao.isEmpty())
            {
                if (!preco.isEmpty())
                {
                    Produto produto = new Produto();

                    produto.setIdUsuario(idUsuarioLogado);
                    produto.setNome(nome);
                    produto.setDescricao(descricao);
                    produto.setPreco(Double.parseDouble(preco));
                    produto.salvar();
                    finish();

                    exibirMensagem("Produto salvo com sucesso");
                }else
                {
                    exibirMensagem("Digite o preço do produto");
                }
            }else
            {
                exibirMensagem("Digite a descrição do produto");
            }
        }else
        {
            exibirMensagem("Digite o nome do produto");
        }
    }

    private void exibirMensagem(String texto)
    {
        Toast.makeText(this,
                texto,
                Toast.LENGTH_LONG).show();
    }

    private void inicializarComponentes()
    {
        editProdutoNome = findViewById(R.id.editProdutoNome);
        editProdutoDescricao = findViewById(R.id.editProdutoDescricao);
        editProdutoPreco = findViewById(R.id.editProdutoPreco);
    }
}
