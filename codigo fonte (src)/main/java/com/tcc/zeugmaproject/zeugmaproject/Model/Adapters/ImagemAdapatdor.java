package com.tcc.zeugmaproject.zeugmaproject.Model.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.tcc.zeugmaproject.zeugmaproject.Model.Conteudo;

import java.util.List;

public class ImagemAdapatdor extends BaseAdapter {
    private Context contexto;
    private List<Conteudo> conteudoList;

    public ImagemAdapatdor(List<Conteudo> conteudoList, Context contexto) {
        this.contexto = contexto;
        this.conteudoList = conteudoList;
    }

    @Override
    public int getCount() {
        return conteudoList.size();
    }

    @Override
    public Object getItem(int position) {
        return conteudoList.get(position).getBitmap();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView img = new ImageView(contexto);
        img.setImageBitmap(conteudoList.get(position).getBitmap());
        img.setAdjustViewBounds(true);
        img.setPadding(5,5,5,5);
        return img;
    }
}