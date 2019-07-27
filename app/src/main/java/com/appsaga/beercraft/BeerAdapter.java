package com.appsaga.beercraft;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class BeerAdapter extends ArrayAdapter<Beer> {

    public BeerAdapter(Context context, ArrayList<Beer> beers) {
        super(context, 0,beers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View beerView = convertView;

        if(beerView==null)
        {
            beerView= LayoutInflater.from(getContext()).inflate(R.layout.beer_view, parent, false);
        }

        Beer beer = getItem(position);

        TextView name = beerView.findViewById(R.id.name);
        TextView style = beerView.findViewById(R.id.style);
        TextView content = beerView.findViewById(R.id.beer_content);
        final Button addButton = beerView.findViewById(R.id.add_button);
        TextView bitter = beerView.findViewById(R.id.bitter);
        TextView size = beerView.findViewById(R.id.size);

        final ImageButton add= beerView.findViewById(R.id.add_beer);
        ImageButton subtract = beerView.findViewById(R.id.subtract_beer);
        final TextView quantity = beerView.findViewById(R.id.quantity);
        final LinearLayout addLayout = beerView.findViewById(R.id.add_layout);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addButton.setVisibility(View.GONE);
                addLayout.setVisibility(View.VISIBLE);
                quantity.setText("1");
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String quant = quantity.getText().toString();

                if(!quant.equalsIgnoreCase("4"))
                {
                    quantity.setText((Integer.parseInt(quant)+1)+"");
                }
                else
                {
                    Toast.makeText(getContext(),"You cannot add more than 4 beers",Toast.LENGTH_SHORT).show();
                }
            }
        });

        subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String quant = quantity.getText().toString();

                if(quant.equalsIgnoreCase("1"))
                {
                    addButton.setVisibility(View.VISIBLE);
                    addLayout.setVisibility(View.GONE);
                }
                else
                {
                    quantity.setText((Integer.parseInt(quant)-1)+"");
                }
            }
        });

        name.setText(beer.getName());
        style.setText(beer.getStyle());

        if(!beer.getIbu().equalsIgnoreCase(""))
        {
            bitter.setText("Bitterness Level: "+ beer.getIbu());
        }
        else
        {
            bitter.setText("Bitterness Level: 0");
        }
        size.setText("size: "+beer.getOunces()+" Ounces");

        if(!beer.getAbv().equalsIgnoreCase(""))
        {
            content.setText("Alcohol Content: "+beer.getAbv());
        }
        else
        {
            content.setText("Alcohol Content: 0.00");
        }

        return beerView;
    }
}
