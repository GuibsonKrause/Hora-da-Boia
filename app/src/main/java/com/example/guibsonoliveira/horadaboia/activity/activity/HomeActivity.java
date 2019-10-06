package com.example.guibsonoliveira.horadaboia.activity.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.example.guibsonoliveira.horadaboia.R;
import com.example.guibsonoliveira.horadaboia.activity.adapter.AdapterEmpresa;
import com.example.guibsonoliveira.horadaboia.activity.adapter.AdapterProduto;
import com.example.guibsonoliveira.horadaboia.activity.helper.ConfiguracaoFirebase;
import com.example.guibsonoliveira.horadaboia.activity.listener.RecyclerItemClickListener;
import com.example.guibsonoliveira.horadaboia.activity.model.Empresa;
import com.example.guibsonoliveira.horadaboia.activity.model.Produto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class HomeActivity extends AppCompatActivity
{
    private FirebaseAuth autenticacao;
    /*Biblioteca MaterialSearchView
    * https://github.com/MiguelCatalan/MaterialSearchView*/
    private MaterialSearchView searchView;
    private RecyclerView recyclerEmpresa;
    private List<Empresa> empresas = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private AdapterEmpresa adapterEmpresa;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /*Inicializa componentes*/
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        /*Configuração ToolBar*/
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Hora da Boia");
        setSupportActionBar(toolbar);

        /*Configurar RecyclerView*/
        recyclerEmpresa.setLayoutManager(new LinearLayoutManager(this));
        recyclerEmpresa.setHasFixedSize(true);
        adapterEmpresa = new AdapterEmpresa(empresas);
        recyclerEmpresa.setAdapter(adapterEmpresa);

        /*Recurar empresas*/
        recuperaEmpresas();

        /*Configurar SearchView*/
        searchView.setHint("Pesquisar Restaurantes");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                pesquisarEmpresas(newText);
                return true;
            }
        });

        /*Configurar Envento de clique Empresa*/
        recyclerEmpresa.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerEmpresa,
                        new RecyclerItemClickListener.OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(View view, int position)
                            {
                                Empresa empresaSelecionada = empresas.get(position);
                                /*Abre o cardapio*/
                                Intent i = new Intent(HomeActivity.this, CardapioActivity.class);

                                /*Manda o objeto empresa*/
                                i.putExtra("empresa", empresaSelecionada);

                                startActivity(i);
                            }

                            @Override
                            public void onLongItemClick(View view, int position)
                            {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

    }

    private void pesquisarEmpresas(String pesquisa)
    {
        DatabaseReference empresaRef = firebaseRef
                .child("empresas");
        Query query = empresaRef.orderByChild("nomeFiltro")
                .startAt(pesquisa)
                .endAt(pesquisa + "\uf8ff");
        /* O caractere \uf8ff usado na consulta é um ponto de código
         * muito alto no intervalo Unicode (é um código de Área de Uso
         * Privado [PUA]). Como é depois da maioria dos caracteres regulares
         * no Unicode, a consulta corresponde a todos os valores que começam
         * com o queryText.*/
        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                empresas.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren())
                {
                    empresas.add(ds.getValue(Empresa.class));
                }

                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

    }

    private void recuperaEmpresas()
    {

        /*https://github.com/d-max/spots-dialog*/
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando Dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference empresaRef = firebaseRef.child("empresas");
        empresaRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                empresas.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren())
                {
                    empresas.add(ds.getValue(Empresa.class));
                }

                adapterEmpresa.notifyDataSetChanged();

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
        searchView = findViewById(R.id.materialSearchView);
        recyclerEmpresa = findViewById(R.id.recyclerEmpresa);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        /*Infla o menu*/
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_usuario, menu);

        /*Configurar botao pesquisa*/
        MenuItem item = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(item);

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
        }
        return super.onOptionsItemSelected(item);
    }

    private void abrirConfiguracoes()
    {
        startActivity(new Intent(HomeActivity.this,
                ConfiguracoesUsuarioActivity.class));
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
}
