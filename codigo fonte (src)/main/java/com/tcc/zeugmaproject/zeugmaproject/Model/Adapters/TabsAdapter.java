package com.tcc.zeugmaproject.zeugmaproject.Model.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

import com.tcc.zeugmaproject.zeugmaproject.Control.fragments.MovieFragment;
import com.tcc.zeugmaproject.zeugmaproject.Control.fragments.SearchFragment;
import com.tcc.zeugmaproject.zeugmaproject.Control.fragments.SerieFragment;
import com.tcc.zeugmaproject.zeugmaproject.R;

public class TabsAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private String[] abas = new String[]{"Filmes", "Search ", "Series"};
    private int[] icons = new int[] {R.mipmap.movie_icon, R.mipmap.search_icon, R.mipmap.serie_icon};
    private int icon_size;
    private FragmentTransaction mFragmentTransaction;
    private FragmentManager fm;

    public TabsAdapter(FragmentManager fm, Context c) {
        super(fm);
        this.context = c;
        this.fm = fm;
        double scale = c.getResources().getDisplayMetrics().density;
        icon_size = (int) (24 * scale + 0.5f);

    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        mFragmentTransaction = fm.beginTransaction();
        switch (position){
            case 0 :
                fragment = new MovieFragment();
                break;
            case 1:
                fragment = new SearchFragment();
                break;
            case 2:
                fragment = new SerieFragment();
                break;
        }
        return  fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable drawable = ContextCompat.getDrawable(context, icons[position]);
        drawable.setBounds(0,0, icon_size, icon_size);
        ImageSpan imageSpan = new ImageSpan( drawable );

        SpannableString spannableString = new SpannableString(" ");
        spannableString.setSpan( imageSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );

        return ( spannableString );

    }


    @Override
    public int getCount() {
        return abas.length;
    }
}
