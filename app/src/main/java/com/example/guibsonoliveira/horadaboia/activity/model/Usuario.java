package com.example.guibsonoliveira.horadaboia.activity.model;

import com.example.guibsonoliveira.horadaboia.activity.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Usuario
{
    private String idUsuario;
    private String nome;
    private String endereco;

    public Usuario()
    {

    }

    public void salvar()
    {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(getIdUsuario());
        /*Salva o dados*/
        usuarioRef.setValue(this);
    }

    public String getIdUsuario()
    {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario)
    {
        this.idUsuario = idUsuario;
    }

    public String getNome()
    {
        return nome;
    }

    public void setNome(String nome)
    {
        this.nome = nome;
    }

    public String getEndereco()
    {
        return endereco;
    }

    public void setEndereco(String endereco)
    {
        this.endereco = endereco;
    }
}
