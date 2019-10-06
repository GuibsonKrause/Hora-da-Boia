package com.example.guibsonoliveira.horadaboia.activity.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.guibsonoliveira.horadaboia.R;
import com.example.guibsonoliveira.horadaboia.activity.adapter.AdapterProduto;
import com.example.guibsonoliveira.horadaboia.activity.helper.ConfiguracaoFirebase;
import com.example.guibsonoliveira.horadaboia.activity.helper.UsuarioFirebase;
import com.example.guibsonoliveira.horadaboia.activity.listener.RecyclerItemClickListener;
import com.example.guibsonoliveira.horadaboia.activity.model.Produto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class EmpresaActivity extends AppCompatActivity
{

    private FirebaseAuth autenticacao;
    private RecyclerView recyclerProdutos;
    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    private AlertDialog alerta;
    private android.app.AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);

        /*Configurações iniciais*/
        inicializarComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        /*Configuração ToolBar*/
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Hora da Boia - Empresa");
        setSupportActionBar(toolbar);

        /*Configurar RecyclerView*/
        recyclerProdutos.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutos.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutos.setAdapter(adapterProduto);

        /*Recurar produtos*/
        recuperaProdutos();

        /*Adiciona evento de clique no RecyclerView*/
        recyclerProdutos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerProdutos,
                        new RecyclerItemClickListener.OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(View view, int position)
                            {

                            }

                            @Override
                            public void onLongItemClick(View view, final int position)
                            {
                                //Cria o gerador do AlertDialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(EmpresaActivity.this);
                                //define o titulo
                                builder.setTitle("Confirmação");
                                //define a mensagem
                                builder.setMessage("Excluir esse produto?");
                                //define um botão como positivo
                                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        Produto produtoSelecionado = produtos.get(position);
                                        produtoSelecionado.remover();
                                        exibirMensagem("Produto removido");
                                    }
                                });
                                //define um botão como negativo.
                                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {

                                    }
                                });
                                //cria o AlertDialog
                                alerta = builder.create();
                                //Exibe
                                alerta.show();
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                            {

                            }
                        }
                )
        );

    }

    private void recuperaProdutos()
    {

        /*https://github.com/d-max/spots-dialog*/
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando Dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference produtosRef = firebaseRef
                .child("produtos")
                .child(idUsuarioLogado);

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
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private void inicializarComponentes()
    {
        recyclerProdutos = findViewById(R.id.recyclerProdutos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        /*Infla o menu*/
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_empresa, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menuSair :
                deslogarUsuario();
                break;
            case R.id.menuConfiguracoes :
                abrirConfiguracoes();
                break;
            case R.id.menuNovoProduto :
                abrirNovoProduto();
                break;
            case R.id.menuPedidos :
                abrirPedidos();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void abrirNovoProduto()
    {
        startActivity(new Intent(EmpresaActivity.this,
                NovoProdutoEmpresaActivity.class));
    }

    private void abrirConfiguracoes()
    {
        startActivity(new Intent(EmpresaActivity.this,
                ConfiguracoesEmpresaActivity.class));
    }

    private void abrirPedidos()
    {
        startActivity(new Intent(EmpresaActivity.this,
                PedidosActivity.class));
    }

    private void deslogarUsuario()
    {
        try
        {
            autenticacao.signOut();
            finish();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void exibirMensagem(String texto)
    {
        Toast.makeText(this,
                texto,
                Toast.LENGTH_LONG).show();
    }

    private void confirmarExclusao(String titulo, String mensagem) {

    }
}
