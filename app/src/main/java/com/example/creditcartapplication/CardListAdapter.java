package com.example.creditcartapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CardListAdapter extends ArrayAdapter<Card> {

    private static final String TAG = "CardListAdapter";
    private Context mContext;
    int mResource;

    public CardListAdapter(@NonNull Context context, int resource,
                           @NonNull ArrayList<Card> objects, Boolean sensitiveDataHidden) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String bankName = getItem(position).getBankName();
        String firstName = getItem(position).getFirstName();
        String lastName = getItem(position).getLastName();
        String cardNumber = getItem(position).getCardNumber();
        String validThru = getItem(position).getValidThru();
        int CVC = getItem(position).getCVC();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvBankName = convertView.findViewById(R.id.bankName);
        TextView tvCardFirstName = convertView.findViewById(R.id.cardFirstName);
        TextView tvCardLastName = convertView.findViewById(R.id.cardLastName);
        TextView tvCardNumber = convertView.findViewById(R.id.cardNumber);
        TextView tvCardValidThru = convertView.findViewById(R.id.cardValidThru);
        TextView tvCardCVC = convertView.findViewById(R.id.cardCVC);

        tvBankName.setText(bankName);
        tvCardFirstName.setText(firstName);
        tvCardLastName.setText(lastName);
        tvCardNumber.setText(cardNumber);
        tvCardValidThru.setText(validThru);
        tvCardCVC.setText(Integer.toString(CVC));

        tvCardFirstName.setVisibility(View.GONE);
        tvCardLastName.setVisibility(View.GONE);
        tvCardValidThru.setVisibility(View.GONE);
        tvCardCVC.setVisibility(View.GONE);

        return convertView;
    }
}
