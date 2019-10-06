package com.example.guibsonoliveira.horadaboia.activity.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.guibsonoliveira.horadaboia.R;
import com.example.guibsonoliveira.horadaboia.activity.adapter.AdapterEmpresa;
import com.example.guibsonoliveira.horadaboia.activity.helper.ConfiguracaoFirebase;
import com.example.guibsonoliveira.horadaboia.activity.helper.UsuarioFirebase;
import com.example.guibsonoliveira.horadaboia.activity.model.Empresa;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class ConfiguracoesEmpresaActivity extends AppCompatActivity
{
    private EditText editEmpresaNome, editEmpresaCategoria
            , editEmpresaTempo, editEmpresaFrete;
    private CircleImageView imagePerfilEmpresa;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private String idUsuarioLogado;
    private String urlImagemSelecionada = "";
    private DatabaseReference firebaseRef;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_empresa);

        /*Inicializa componentes*/
        inicializarComponentes();

        /*Pega Referencia do Storege para fazer upload da logo da empresa*/
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();

        /*Pega referencia do faribase*/
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        /*Pega o id do usuario que está logado para fazer referencia a logo da empresa*/
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        /*Configuração ToolBar*/
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Escolher logo da Empresa*/
        imagePerfilEmpresa.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        );
                if (intent.resolveActivity(getPackageManager()) != null)
                {
                    startActivityForResult(intent, SELECAO_GALERIA);
                }
            }
        });

        /*Recuperar dados da empresa*/
        recuperarDadosEmpresa();
    }

    private void recuperarDadosEmpresa()
    {
        DatabaseReference empresaRef = firebaseRef
                .child("empresas")
                .child(idUsuarioLogado);
        empresaRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                /*https://github.com/d-max/spots-dialog*/
                dialog = new SpotsDialog.Builder()
                        .setContext(ConfiguracoesEmpresaActivity.this)
                        .setMessage("Carregando Dados")
                        .setCancelable(false)
                        .build();
                dialog.show();
                if (dataSnapshot.getValue() != null)
                {
                    Empresa empresa = dataSnapshot.getValue(Empresa.class);
                    editEmpresaNome.setText(empresa.getNome());
                    editEmpresaCategoria.setText(empresa.getCategoria());
                    editEmpresaFrete.setText(empresa.getFrete().toString());
                    editEmpresaTempo.setText(empresa.getTempo());

                    urlImagemSelecionada = empresa.getUrlImagem();

                    if (urlImagemSelecionada != "")
                    {
                        // Fazer Download
                        Picasso.with(ConfiguracoesEmpresaActivity.this)
                                .load(urlImagemSelecionada)
                                .into(imagePerfilEmpresa);
                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    public void validarDadosEmpresa(View view)
    {
        /*Valida se os campos foram preenchidos*/
        String nome = editEmpresaNome.getText().toString();
        String frete = editEmpresaFrete.getText().toString();
        String categoria = editEmpresaCategoria.getText().toString();
        String tempo = editEmpresaTempo.getText().toString();

        if (!nome.isEmpty())
        {
            if (!frete.isEmpty())
            {
                if (!categoria.isEmpty())
                {
                    if (!tempo.isEmpty())
                    {
                        Empresa empresa = new Empresa();
                        empresa.setIdUsuario(idUsuarioLogado);
                        empresa.setNome(nome);
                        empresa.setNomeFiltro(nome);
                        empresa.setFrete(Double.parseDouble(frete));
                        empresa.setCategoria(categoria);
                        empresa.setTempo(tempo);
                        empresa.setUrlImagem(urlImagemSelecionada);
                        empresa.salvar();
                        exibirMensagem("Empresa cadastrada com sucesso!");
                        finish();

                    }else
                    {
                        exibirMensagem("Digite o tempo de entrega");
                    }
                }else
                {
                    exibirMensagem("Digite a categoria da empresa");
                }
            }else
            {
                exibirMensagem("Digite o valor do frete");
            }
        }else
        {
            exibirMensagem("Digite o nome da empresa");
        }
    }

    /*Todo o trabalho desde pegar a logo na galeria até salvar a mesma no banco*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            Bitmap imagem = null;

            try
            {
                switch (requestCode)
                {
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images
                                .Media
                                .getBitmap(
                                        getContentResolver(),
                                        localImagem
                                );
                        break;
                }

                if (imagem != null)
                {
                    imagePerfilEmpresa.setImageBitmap(imagem);

                    /*Convertendo imagem*/
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    /*Salvar imagem no Firebase*/
                    StorageReference imageRef = storageReference
                            .child("imagens")
                            .child("empresas")
                            .child(idUsuarioLogado + "jpeg");

                    UploadTask uploadTask = imageRef.putBytes(dadosImagem);

                    uploadTask.addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(ConfiguracoesEmpresaActivity.this,
                                    "Erro ao fazer upload da imagem " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            urlImagemSelecionada = taskSnapshot.getDownloadUrl().toString();
                            Toast.makeText(ConfiguracoesEmpresaActivity.this,
                                    "Sucesso ao fazer o upload da sua logo",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
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
        editEmpresaNome = findViewById(R.id.editEmpresaNome);
        editEmpresaCategoria = findViewById(R.id.editEmpresaCategoria);
        editEmpresaFrete = findViewById(R.id.editEmpresaFrete);
        editEmpresaTempo = findViewById(R.id.editEmpresaTempo);
        imagePerfilEmpresa = findViewById(R.id.imagePerfilEmpresa);
    }
}
