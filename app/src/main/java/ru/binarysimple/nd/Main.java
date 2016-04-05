package ru.binarysimple.nd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Currency;
import java.util.Locale;

public class Main extends AppCompatActivity {

    private final String TAX13 = "13.00";
    private final String TAX9 = "9.00";
    private final String TAX30 = "30.00";
    private static final String LOG_TAG = "nd_log";
    private final static String SAVE_TV_GROSS = "SAVE_TV_GROSSN";
    private final static String SAVE_TV_NET = "SAVE_TV_NET";
    private final static String SAVE_TV_TAX = "SAVE_TV_TAX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //ADS
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // извлекаем данные
        TextView tvGross = (TextView) findViewById(R.id.tvGross);
        TextView tvTax = (TextView) findViewById(R.id.tvTax);
        TextView tvNet = (TextView) findViewById(R.id.tvNet);
        if (savedInstanceState != null) {
            tvGross.setText(savedInstanceState.getString(SAVE_TV_GROSS, getResources().getString(R.string.zero)));
            tvTax.setText(savedInstanceState.getString(SAVE_TV_TAX, getResources().getString(R.string.zero)));
            tvNet.setText(savedInstanceState.getString(SAVE_TV_NET, getResources().getString(R.string.zero)));
        }


        RadioGroup radiogroup = (RadioGroup) findViewById(R.id.rgTaxes);

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                EditText etTaxRate = (EditText) findViewById(R.id.etTaxrate);
                RadioButton rbOther = (RadioButton) findViewById(R.id.rbOther);
                etTaxRate.setEnabled(rbOther.isChecked());

                // TODO Auto-generated method stub
                switch (checkedId) {
                    case -1:
                        break;
                    case R.id.rbUsual13:
                        break;
                    case R.id.rbNonresident30:
                        break;
                    case R.id.rbDividend9:
                        break;
                    case R.id.rbOther:
                        break;
                    default:
                        break;
                }
            }
        });

        Button btnCalc = (Button) findViewById(R.id.button);

        btnCalc.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                calc();
            }
        });
    }

    private void calc() {
        RadioGroup rgBase = (RadioGroup) findViewById(R.id.rgBase);
        RadioGroup rgTaxes = (RadioGroup) findViewById(R.id.rgTaxes);
        TextView tvGross = (TextView) findViewById(R.id.tvGross);
        TextView tvTax = (TextView) findViewById(R.id.tvTax);
        TextView tvNet = (TextView) findViewById(R.id.tvNet);
        EditText etBase = (EditText) findViewById(R.id.etBase);
        EditText etTaxrate = (EditText) findViewById(R.id.etTaxrate);
        String taxRate = "0";

        switch (rgTaxes.getCheckedRadioButtonId()) {
            case -1:
                break;
            case R.id.rbUsual13:
                taxRate = TAX13;
                break;
            case R.id.rbNonresident30:
                taxRate = TAX30;
                break;
            case R.id.rbDividend9:
                taxRate = TAX9;
                break;
            case R.id.rbOther:
                taxRate = etTaxrate.getText().toString();
                break;
            default:
                break;
        }

        if ((CurrOps.zero(taxRate)) || CurrOps.zero(etBase.getText().toString())) {
            setToZero();
            return;
        }

        switch (rgBase.getCheckedRadioButtonId()) {
            case -1:
                break;
            case R.id.rbSumNet: //сумма к выплате
                String base = calcGrossN(taxRate); //получаю полную сумму оклада
                String taxSum = calcTaxNet(base); //получаю сумму налогов. вычитанием
                tvGross.setText(calcFullNet(convertC(etBase.getText().toString()),taxSum));
                tvTax.setText(taxSum);
                tvNet.setText(convertC(etBase.getText().toString()));

                break;
            case R.id.rbsumGross://введена общая сумма √
                String taxG = calcTax(etBase.getText().toString(), taxRate); //получаем сумму налога
                String give = calcGross(taxG); //получаю сумму на руки dsxbnfybtv.
                tvGross.setText(convertC(etBase.getText().toString()));
                tvTax.setText(taxG);
                tvNet.setText(give);
                break;
            default:
                break;
        }
    }

    private void setToZero() {
        TextView tvGross = (TextView) findViewById(R.id.tvGross);
        TextView tvTax = (TextView) findViewById(R.id.tvTax);
        TextView tvNet = (TextView) findViewById(R.id.tvNet);
        tvGross.setText(getResources().getText(R.string.zero));
        tvTax.setText(getResources().getText(R.string.zero));
        tvNet.setText(getResources().getText(R.string.zero));
    }

    private String calcTaxNet(String base) {
        EditText etBase = (EditText) findViewById(R.id.etBase);
        Currency curr = Currency.getInstance(Locale.getDefault());
        String taxSum = CurrOps.sub(curr, base, etBase.getText().toString());
        //return CurrOps.sub(curr, base, etBase.getText().toString());
        return CurrOps.currRound(taxSum,0);
    }

    private String calcFullNet(String base, String taxSum) {
        Currency curr = Currency.getInstance(Locale.getDefault());
        return CurrOps.add(curr,base,taxSum);
    }

    private String convertC(String base) {
        Currency curr = Currency.getInstance(Locale.getDefault());
        return CurrOps.convertToCurr(curr, base);
    }

    private String calcGross(String taxSum) {
        EditText etBase = (EditText) findViewById(R.id.etBase);
        Currency curr = Currency.getInstance(Locale.getDefault());
        //return CurrOps.mult(curr, etBase.getText().toString(), CurrOps.sub(curr, "100", tax));
        return CurrOps.sub(curr,etBase.getText().toString(),taxSum);
    }

    private String calcGrossN(String tax) {
        EditText etBase = (EditText) findViewById(R.id.etBase);
        Currency curr = Currency.getInstance(Locale.getDefault());
        tax = CurrOps.sub(curr, "100", tax);
        tax = CurrOps.div(curr, etBase.getText().toString(), tax);
        return tax;//CurrOps.currRound(tax,0);
    }

    private String calcTax(String base, String taxRate) {
        Currency curr = Currency.getInstance(Locale.getDefault());
        String taxSum = CurrOps.mult(curr, base, taxRate);
        return CurrOps.currRound(taxSum,0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState");
        TextView tvGross = (TextView) findViewById(R.id.tvGross);
        TextView tvTax = (TextView) findViewById(R.id.tvTax);
        TextView tvNet = (TextView) findViewById(R.id.tvNet);
        outState.putString(SAVE_TV_GROSS, tvGross.getText().toString());
        outState.putString(SAVE_TV_NET, tvNet.getText().toString());
        outState.putString(SAVE_TV_TAX, tvTax.getText().toString());
    }


}
