package com.tcc.zeugmaproject.zeugmaproject.Model.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tcc.zeugmaproject.zeugmaproject.Model.Season;
import com.tcc.zeugmaproject.zeugmaproject.Model.Serie;
import com.tcc.zeugmaproject.zeugmaproject.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class SeasonAdapter extends BaseAdapter {
    private Context contexto;
    private Serie serie;
    private ImageView img;

    public SeasonAdapter(Context contexto, Serie serie) {
        this.contexto = contexto;
        this.serie = serie;
    }

    @Override
    public int getCount() {
        return serie.getSeasons().size();
    }

    @Override
    public Object getItem(int position) {
        return serie.getSeasons().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Season season =  this.serie.getSeasons().get(position);

        convertView = LayoutInflater.from(this.contexto).inflate(R.layout.adapter_temporada, null);
        img = (ImageView) convertView.findViewById(R.id.img_temporada);
        TextView txt1 = (TextView) convertView.findViewById(R.id.txt_numero_temporada);
        TextView txt2 = (TextView) convertView.findViewById(R.id.txt_info_temporada);

        if (season != null) {
            if (season.getPoster_path() != null) {
                img.setImageBitmap(season.getPoster());
            } else{
                img.setImageBitmap(serie.getBitmap());
            }
            img.setAdjustViewBounds(true);
            img.setPadding(5,5,5,5);
            if (season.getSeason_number() > 0)
                txt1.setText(season.getSeason_number() + " Temporada");
            else if (season.getSeason_number() == 0) {
                txt1.setText("Especiais");
            }

            if (season.getAir_date() != null) {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String data = dateFormat.format(season.getAir_date());
                txt2.setText("Lançamento: " + data + "\n");
            }

            if (season.getEpisode_count() != 0)
                txt2.setText(txt2.getText() + "Episodios: " + season.getEpisode_count());
        }
        return convertView;
    }
}