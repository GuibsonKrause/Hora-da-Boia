package com.example.guibsonoliveira.horadaboia.activity.model;

import com.example.guibsonoliveira.horadaboia.activity.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Empresa implements Serializable
{
    private String idUsuario;
    private String urlImagem;
    private String nome;
    /*nomeFiltro com letras minusculas para usar no SearchView*/
    private String nomeFiltro;
    private String tempo;
    private String categoria;
    private Double frete;

    public Empresa()
    {

    }

    public String getNomeFiltro() {
        return nomeFiltro;
    }

    public void setNomeFiltro(String nomeFiltro) {
        this.nomeFiltro = nomeFiltro.toLowerCase();
    }

    public String getIdUsuario()
    {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario)
    {
        this.idUsuario = idUsuario;
    }

    public String getUrlImagem()
    {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem)
    {
        this.urlImagem = urlImagem;
    }

    public String getNome()
    {
        return nome;
    }

    public void setNome(String nome)
    {
        this.nome = nome;
    }

    public String getTempo()
    {
        return tempo;
    }

    public void setTempo(String tempo)
    {
        this.tempo = tempo;
    }

    public String getCategoria()
    {
        return categoria;
    }

    public void setCategoria(String categoria)
    {
        this.categoria = categoria;
    }

    public Double getFrete()
    {
        return frete;
    }

    public void setFrete(Double frete)
    {
        this.frete = frete;
    }

    public void salvar()
    {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference empresaRef = firebaseRef
                .child("empresas")
                .child(getIdUsuario());
        /*Salva todos os dados*/
        empresaRef.setValue(this);
    }
}
