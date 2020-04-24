package com.userdev.winnerstars.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.userdev.winnerstars.R;

public class SpinnerAdapter extends ArrayAdapter<String> {String[] spinnerTitles;
    Integer[] spinnerImages;
    String[] spinnerPopulation;
    Context mContext;

    public SpinnerAdapter(@NonNull Context context, String[] titles, Integer[] images, String[] population) {
        super(context, R.layout.linha_spinner_curso);
        this.spinnerTitles = titles;
        this.spinnerImages = images;
        this.spinnerPopulation = population;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return spinnerTitles.length;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.linha_spinner_curso, parent, false);
            mViewHolder.mFlag = (ImageView) convertView.findViewById(R.id.icon);
            mViewHolder.mName = (TextView) convertView.findViewById(R.id.curso);
            //mViewHolder.mPopulation = (TextView) convertView.findViewById(R.id.tvPopulation);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.mFlag.setImageResource(spinnerImages[position]);
        mViewHolder.mName.setText(spinnerTitles[position]);
        //mViewHolder.mPopulation.setText(spinnerPopulation[position]);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    private static class ViewHolder {
        ImageView mFlag;
        TextView mName;
        TextView mPopulation;
    }
}

