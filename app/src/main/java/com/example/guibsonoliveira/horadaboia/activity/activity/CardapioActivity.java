package com.example.guibsonoliveira.horadaboia.activity.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guibsonoliveira.horadaboia.R;
import com.example.guibsonoliveira.horadaboia.activity.adapter.AdapterProduto;
import com.example.guibsonoliveira.horadaboia.activity.helper.ConfiguracaoFirebase;
import com.example.guibsonoliveira.horadaboia.activity.helper.UsuarioFirebase;
import com.example.guibsonoliveira.horadaboia.activity.listener.RecyclerItemClickListener;
import com.example.guibsonoliveira.horadaboia.activity.model.Empresa;
import com.example.guibsonoliveira.horadaboia.activity.model.ItemPedido;
import com.example.guibsonoliveira.horadaboia.activity.model.Pedido;
import com.example.guibsonoliveira.horadaboia.activity.model.Produto;
import com.example.guibsonoliveira.horadaboia.activity.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class CardapioActivity extends AppCompatActivity
{
    private RecyclerView recyclerProdutosCardapio;
    private ImageView imageEmpresaCardapio;
    private TextView textNomeEmpresaCardapio;
    private TextView textCategoriaEmpresaCardapio;
    private TextView textTempoEmpresaCardapio;
    private TextView textFreteEmpresaCardapio;
    private Empresa empresaSelecionada;
    private TextView textCarrinhoQtd, textCarrinhoTotal;

    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idEmpresa;
    private String idUsuarioLogado;
    private AlertDialog alertDialog;
    private Usuario usuario;
    private Pedido pedidoRecuperado;
    private int qtdItensCarrinho;
    private Double totalCarrinho;
    private int metodoPagamento;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio);

        /*inicializarComponentes*/
        inicializarComponentes();

        /*Configurações iniciais*/
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        /*Recuperar Empresa Selecionada*/
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            empresaSelecionada = (Empresa) bundle.getSerializable("empresa");

            /*Preenchendo dados*/
            textNomeEmpresaCardapio.setText(empresaSelecionada.getNome());
            textCategoriaEmpresaCardapio.setText(empresaSelecionada.getCategoria() + " -");
            textTempoEmpresaCardapio.setText(empresaSelecionada.getTempo() + " Min");
            textFreteEmpresaCardapio.setText("Frete R$: " + empresaSelecionada.getFrete().toString());
            idEmpresa = empresaSelecionada.getIdUsuario();

            String url = empresaSelecionada.getUrlImagem();
            Picasso.with(imageEmpresaCardapio.getContext())
                    .load(url)
                    .into(imageEmpresaCardapio);

        }

        /*Configuração ToolBar*/
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cardápio");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Configurar RecyclerView*/
        recyclerProdutosCardapio.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutosCardapio.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutosCardapio.setAdapter(adapterProduto);

        /*Configurar Evento Clique*/
        recyclerProdutosCardapio.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerProdutosCardapio,
                        new RecyclerItemClickListener.OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(View view, int position)
                            {
                                confirmarQuantidade(position);
                            }

                            @Override
                            public void onLongItemClick(View view, int position)
                            {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                            {

                            }
                        }
                )
        );

        /*Recuperar produtos*/
        recuperaProdutos();

        /*Recuperar dados usuario*/
        recuperarDadosUsuario();
    }

    private void confirmarQuantidade(final int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quantidade");
        builder.setMessage("Digite a quantidade");

        final EditText editQuantidade = new EditText(this);
        editQuantidade.setInputType(InputType.TYPE_CLASS_NUMBER);
        editQuantidade.setText("1");

        builder.setView(editQuantidade);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String quantidade = editQuantidade.getText().toString();

                Produto produtoSelecionado = produtos.get(position);

                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setIdProduto(produtoSelecionado.getIdProduto());
                itemPedido.setDescricaoProduto(produtoSelecionado.getDescricao());
                itemPedido.setNomeProduto(produtoSelecionado.getNome());
                itemPedido.setPreco(produtoSelecionado.getPreco());
                itemPedido.setQuantidade(Integer.parseInt(quantidade));

                itensCarrinho.add(itemPedido);

                if (pedidoRecuperado == null)
                {
                    pedidoRecuperado = new Pedido(idUsuarioLogado, idEmpresa);
                }

                pedidoRecuperado.setNome(usuario.getNome());
                pedidoRecuperado.setEndereco(usuario.getEndereco());
                pedidoRecuperado.setItens(itensCarrinho);
                pedidoRecuperado.salvar();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void recuperarDadosUsuario()
    {
        /*https://github.com/d-max/spots-dialog*/
        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando Dados")
                .setCancelable(false)
                .build();
        alertDialog.show();

        DatabaseReference usuariosRef = firebaseRef
                .child("usuarios")
                .child(idUsuarioLogado);

        usuariosRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.getValue() != null)
                {
                    usuario = dataSnapshot.getValue(Usuario.class);

                }
                recuperarPedido();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private void recuperarPedido()
    {
        DatabaseReference pedidosRef = firebaseRef
                .child("pedidos_usuario")
                .child(idEmpresa)
                .child(idUsuarioLogado);

        pedidosRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                qtdItensCarrinho = 0;
                totalCarrinho = 0.0;
                itensCarrinho = new ArrayList<>();

                if (dataSnapshot.getValue() != null)
                {
                    pedidoRecuperado = dataSnapshot.getValue(Pedido.class);
                    itensCarrinho = pedidoRecuperado.getItens();

                    for (ItemPedido itemPedido: itensCarrinho)
                    {
                        int qtde = itemPedido.getQuantidade();
                        Double preco = itemPedido.getPreco();

                        totalCarrinho += (qtde * preco);
                        qtdItensCarrinho += qtde;
                    }
                }

                DecimalFormat df = new DecimalFormat("0.00");

                textCarrinhoQtd.setText("qtd: " + String.valueOf(qtdItensCarrinho));
                textCarrinhoTotal.setText("R$: " + df.format(totalCarrinho));

                alertDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private void recuperaProdutos()
    {
        DatabaseReference produtosRef = firebaseRef
                .child("produtos")
                .child(idEmpresa);

        produtosRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                produtos.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren())
                {
                    produtos.add(ds.getValue(Produto.class));
                }

                adapterProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        /*Infla o menu*/
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cardapio, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menuPedido :
                confirmarPedido();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmarPedido()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Método de pagamento");

        CharSequence[] itens = new CharSequence[]{
                "Dinheiro", "Cartão"
        };

        builder.setSingleChoiceItems(itens, 1, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                metodoPagamento = which;
            }
        });
        final EditText editObservacao = new EditText(this);
        editObservacao.setHint("Observação");
        builder.setView(editObservacao);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String observacao = editObservacao.getText().toString();
                pedidoRecuperado.setMetodoPagamento(metodoPagamento);
                pedidoRecuperado.setObservacao(observacao);
                pedidoRecuperado.setStatus("Confirmado");
                /*Cria nó pedidos*/
                pedidoRecuperado.confirmar();
                /*Remove nó pedidos_usuario*/
                pedidoRecuperado.remover();
                pedidoRecuperado = null;
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void inicializarComponentes()
    {
        recyclerProdutosCardapio = findViewById(R.id.recyclerProdutoCardapio);
        imageEmpresaCardapio = findViewById(R.id.imageEmpresaCardapio);
        textNomeEmpresaCardapio = findViewById(R.id.textNomeEmpresaCardapio);
        textCategoriaEmpresaCardapio = findViewById(R.id.textCategoriaEmpresaCardapio);
        textTempoEmpresaCardapio = findViewById(R.id.textTempoEmpresaCardapio);
        textFreteEmpresaCardapio = findViewById(R.id.textFreteEmpresaCardapio);
        textCarrinhoQtd = findViewById(R.id.textCarrinhoQtd);
        textCarrinhoTotal = findViewById(R.id.textCarrinhoTotal);
    }
}
