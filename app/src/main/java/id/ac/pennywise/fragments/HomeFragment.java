package id.ac.pennywise.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import id.ac.pennywise.R;
import id.ac.pennywise.activities.TransactionDetailActivity;
import id.ac.pennywise.controllers.TransactionController;
import id.ac.pennywise.models.TransactionModel;
import id.ac.pennywise.utils.PreferenceManager;
import id.ac.pennywise.utils.adapters.TransactionAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    ListView transactionLv;
    private TextView balanceTxt;

    private List<TransactionModel> transactionList;
    private TransactionController transactionController;
    private TransactionAdapter transactionAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        transactionController = new TransactionController(requireContext());
        transactionLv = view.findViewById(R.id.transactionLv);

        balanceTxt = view.findViewById(R.id.balanceTxt);

        setBalance();

        transactionList = transactionController.getAllTransactions();
        transactionAdapter = new TransactionAdapter(requireContext(), transactionList);
        transactionLv.setAdapter(transactionAdapter);

        transactionLv.setOnItemClickListener((parent, view1, position, id) -> {
            TransactionModel clickedTransaction = transactionList.get(position);

            Intent intent = new Intent(getActivity(), TransactionDetailActivity.class);
            intent.putExtra("transaction_id", clickedTransaction.getId());
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        transactionList.clear();
        transactionList.addAll(transactionController.getAllTransactions());
        transactionAdapter.notifyDataSetChanged();

        setBalance();
    }

    private void setBalance() {
        String amountFormat = "Rp%,.2f";
        String balanceStr = String.format(Locale.UK, amountFormat, PreferenceManager.getUserBalance(requireContext()));

        balanceTxt.setText(balanceStr);
    }
}