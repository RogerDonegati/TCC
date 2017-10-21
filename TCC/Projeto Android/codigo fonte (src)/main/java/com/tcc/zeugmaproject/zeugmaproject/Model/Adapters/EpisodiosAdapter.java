package com.tcc.zeugmaproject.zeugmaproject.Model.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tcc.zeugmaproject.zeugmaproject.Model.Season;
import com.tcc.zeugmaproject.zeugmaproject.R;

public class EpisodiosAdapter extends RecyclerView.Adapter<EpisodiosAdapter.MyViewHolder> {
    Season season;
    Context contexto;

    public EpisodiosAdapter(Season season, Context ctx) {
        this.contexto = ctx;
        this.season = season;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_episodio, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.imageView.setAdjustViewBounds(true);
        if (season.getEpisodes() != null) {
            if (season.getEpisodes().get(position).getStill_path() == null)
                holder.imageView.setImageBitmap(season.getBackdrop());
            else {
                holder.imageView.setImageBitmap(season.getEpisodes().get(position).getPoster());
            }
            holder.txt_titulo.setText(season.getEpisodes().get(position).getEpisode_number() + " - " + season.getEpisodes().get(position).getName());
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (season.getEpisodes() != null) {
//                    Intent intent = new Intent(contexto, MovieDetalhadoActivity.class);
//                    intent.putExtra("id", season.getEpisodes().get(position).getId());
//                    intent.putExtra("titulo", season.getEpisodes().get(position).getName());
//
//                    Bitmap bmp = season.getEpisodes().get(position).getPoster();
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    byte[] byteArray = stream.toByteArray();
//                    intent.putExtra("poster", byteArray);
//
//                    contexto.startActivity(intent);
//                }
            }

        });
    }

    @Override
    public int getItemCount() {
        return season.getEpisodes().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txt_titulo;
        public MyViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.img_episodios);
            txt_titulo = (TextView) view.findViewById(R.id.txt_titulo_episodio_lista);

        }
    }
}
