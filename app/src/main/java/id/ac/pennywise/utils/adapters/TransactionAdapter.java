package id.ac.pennywise.utils.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import id.ac.pennywise.R;
import id.ac.pennywise.models.TransactionModel;

public class TransactionAdapter extends BaseAdapter {

    private Context context;
    private List<TransactionModel> transactionList;

    public TransactionAdapter(Context context, List<TransactionModel> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @Override
    public int getCount() {
        return transactionList.size();
    }

    @Override
    public Object getItem(int position) {
        return transactionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.transaction_list_item, parent, false);
        }

        TransactionModel current = transactionList.get(position);

        TextView categoryTxt = convertView.findViewById(R.id.categoryTxt);
        TextView dateTxt = convertView.findViewById(R.id.dateTxt);
        TextView amountTxt = convertView.findViewById(R.id.amountTxt);

        categoryTxt.setText(current.getCategory().getName());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd LLL", Locale.UK);
        dateTxt.setText(current.getDate().toString());

        String amountStr = "";
        if (current.getCategory().isIncome()) {
            amountTxt.setTextColor(ContextCompat.getColor(context, R.color.medium_slate_blue));
            amountStr = "+";
        }
        else {
            amountTxt.setTextColor(ContextCompat.getColor(context, R.color.red));
            amountStr = "-";
        }
        amountStr += String.valueOf(current.getAmount());
        amountTxt.setText(amountStr);

        return convertView;
    }
}
