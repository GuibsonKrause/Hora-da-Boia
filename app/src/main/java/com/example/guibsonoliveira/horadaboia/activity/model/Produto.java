package com.example.guibsonoliveira.horadaboia.activity.model;

import com.example.guibsonoliveira.horadaboia.activity.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Produto
{
    private String idUsuario;
    private String nome;
    private String descricao;
    private String idProduto;
    private Double preco;

    public Produto()
    {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef
                .child("produtos");
        setIdProduto(produtoRef.push().getKey());
    }

    public String getIdProduto()
    {
        return idProduto;
    }

    public void setIdProduto(String idProduto)
    {
        this.idProduto = idProduto;
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

    public String getDescricao()
    {
        return descricao;
    }

    public void setDescricao(String descricao)
    {
        this.descricao = descricao;
    }

    public Double getPreco()
    {
        return preco;
    }

    public void setPreco(Double preco)
    {
        this.preco = preco;
    }

    public void salvar()
    {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef
                .child("produtos")
                .child(getIdUsuario())
                .child(getIdProduto());
        /*Salva o dados*/
        produtoRef.setValue(this);
    }

    public void remover()
    {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef
                .child("produtos")
                .child(getIdUsuario())
                .child(getIdProduto());
        /*Remove o produto*/
        produtoRef.removeValue();
    }
}