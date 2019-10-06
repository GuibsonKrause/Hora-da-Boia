package com.example.guibsonoliveira.horadaboia.activity.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UsuarioFirebase
{
    /*Retorna o id do usuário*/
    public static String getIdUsuario()
    {
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return autenticacao.getCurrentUser().getUid();
    }

    /*Retorna o usuário atual*/
    public static FirebaseUser getUsuarioAtual()
    {
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static boolean atualizaTipoUsuario(String tipo)
    {
        try
        {
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(tipo)
                    .build();
            user.updateProfile(profile);
            return true;
        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

}
