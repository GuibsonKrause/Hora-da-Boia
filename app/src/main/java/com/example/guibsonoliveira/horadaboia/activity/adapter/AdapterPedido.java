package com.example.guibsonoliveira.horadaboia.activity.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guibsonoliveira.horadaboia.R;
import com.example.guibsonoliveira.horadaboia.activity.model.ItemPedido;
import com.example.guibsonoliveira.horadaboia.activity.model.Pedido;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AdapterPedido extends RecyclerView.Adapter<AdapterPedido.MyViewHolder>
{

    private List<Pedido> pedidos;

    public AdapterPedido(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
    {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_pedidos, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i)
    {
        Pedido pedido = pedidos.get(i);
        holder.nome.setText( pedido.getNome() );
        holder.endereco.setText( "Endereço: "+pedido.getEndereco() );
        holder.observacao.setText( "Obs: "+ pedido.getObservacao() );

        List<ItemPedido> itens = new ArrayList<ItemPedido>();
        itens = pedido.getItens();
        String descricaoItens = "";

        int numeroItem = 1;
        Double total = 0.0;

        for( ItemPedido itemPedido : itens )
        {
            int qtde = itemPedido.getQuantidade();
            Double preco = itemPedido.getPreco();
            total += (qtde * preco);

            String nome = itemPedido.getNomeProduto();
            descricaoItens += numeroItem + ") " + nome + " / (" + qtde + " x R$ " + preco + ") \n";
            numeroItem++;
        }
        DecimalFormat df = new DecimalFormat("0.00");
        descricaoItens += "Total: R$ " + df.format(total);
        holder.itens.setText(descricaoItens);

        int metodoPagamento = pedido.getMetodoPagamento();
        String pagamento = metodoPagamento == 0 ? "Dinheiro" : "Máquina cartão" ;
        holder.pgto.setText( "pgto: " + pagamento );

    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView nome;
        TextView endereco;
        TextView pgto;
        TextView observacao;
        TextView itens;

        public MyViewHolder(View itemView)
        {
            super(itemView);

            nome        = itemView.findViewById(R.id.textPedidoNome);
            endereco    = itemView.findViewById(R.id.textPedidoEndereco);
            pgto        = itemView.findViewById(R.id.textPedidoPgto);
            observacao  = itemView.findViewById(R.id.textPedidoObs);
            itens       = itemView.findViewById(R.id.textPedidoItens);
        }
    }

}
