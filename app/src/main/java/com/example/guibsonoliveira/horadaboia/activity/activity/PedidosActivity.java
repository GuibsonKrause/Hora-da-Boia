package com.example.guibsonoliveira.horadaboia.activity.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.example.guibsonoliveira.horadaboia.R;
import com.example.guibsonoliveira.horadaboia.activity.adapter.AdapterPedido;
import com.example.guibsonoliveira.horadaboia.activity.adapter.AdapterProduto;
import com.example.guibsonoliveira.horadaboia.activity.helper.ConfiguracaoFirebase;
import com.example.guibsonoliveira.horadaboia.activity.helper.UsuarioFirebase;
import com.example.guibsonoliveira.horadaboia.activity.listener.RecyclerItemClickListener;
import com.example.guibsonoliveira.horadaboia.activity.model.Pedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PedidosActivity extends AppCompatActivity
{
    private RecyclerView recyclerPedidos;
    private AdapterPedido adapterPedido;
    private List<Pedido> pedidos = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference firebaseRef;
    private String idEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        /*Configurações iniciais*/
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idEmpresa = UsuarioFirebase.getIdUsuario();

        /*Configuração ToolBar*/
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pedidos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Configurar RecyclerView*/
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidos.setHasFixedSize(true);
        adapterPedido = new AdapterPedido(pedidos);
        recyclerPedidos.setAdapter(adapterPedido);

        recuperarPedidos();

        /*Adicionar Evendo de clique no recyclerView*/
        recyclerPedidos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerPedidos,
                        new RecyclerItemClickListener.OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(View view, int position)
                            {

                            }

                            @Override
                            public void onLongItemClick(View view, final int position)
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosActivity.this);
                                builder.setTitle("Confirmação");
                                builder.setMessage("Deseja confirmar esse pedido?");

                                builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        Pedido pedido = pedidos.get(position);
                                        pedido.setStatus("finalizado");
                                        pedido.atualizarStatus();
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

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                            {

                            }
                        }));
    }

    private void recuperarPedidos()
    {
        /*https://github.com/d-max/spots-dialog*/
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando Dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference pedidosRef = firebaseRef
                .child("pedidos")
                .child(idEmpresa);
        Query pedidosPesquisa = pedidosRef.orderByChild("status")
                .equalTo("Confirmado");
        pedidosPesquisa.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                pedidos.clear();
                if (dataSnapshot.getValue() != null)
                {
                    for (DataSnapshot ds: dataSnapshot.getChildren())
                    {
                        Pedido pedido = ds.getValue(Pedido.class);
                        pedidos.add(pedido);
                    }
                    adapterPedido.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private void inicializarComponentes()
    {
        recyclerPedidos = findViewById(R.id.recyclerPedidos);
    }
}
