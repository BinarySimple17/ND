package ru.binarysimple.nd;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Currency;
import java.util.Locale;

import static ru.binarysimple.nd.MUtils.getRound;
import static ru.binarysimple.nd.MUtils.getThemeId;

public class Main extends AppCompatActivity {

    private final String TAX13 = "13.00";
    private final String TAX9 = "9.00";
    private final String TAX30 = "30.00";
    private static final String LOG_TAG = "nd_log";
    private final static String SAVE_TV_GROSS = "SAVE_TV_GROSSN";
    private final static String SAVE_TV_NET = "SAVE_TV_NET";
    private final static String SAVE_TV_TAX = "SAVE_TV_TAX";
    private int mCurrentTheme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mCurrentTheme = getThemeId(this);
        setTheme(mCurrentTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        // извлекаем данные
        TextView tvGross = (TextView) findViewById(R.id.tvGross);
        TextView tvTax = (TextView) findViewById(R.id.tvTax);
        TextView tvNet = (TextView) findViewById(R.id.tvNet);
        if (savedInstanceState != null) {
            assert tvGross != null;
            tvGross.setText(savedInstanceState.getString(SAVE_TV_GROSS, getResources().getString(R.string.zero)));
            assert tvTax != null;
            tvTax.setText(savedInstanceState.getString(SAVE_TV_TAX, getResources().getString(R.string.zero)));
            assert tvNet != null;
            tvNet.setText(savedInstanceState.getString(SAVE_TV_NET, getResources().getString(R.string.zero)));
        }


        RadioGroup radiogroup = (RadioGroup) findViewById(R.id.rgTaxes);

        assert radiogroup != null;
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                EditText etTaxRate = (EditText) findViewById(R.id.etTaxrate);
                RadioButton rbOther = (RadioButton) findViewById(R.id.rbOther);
                assert etTaxRate != null;
                assert rbOther != null;
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

        assert btnCalc != null;
        btnCalc.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                calc();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent settingsActivity = new Intent(getBaseContext(),
                        Settings.class);
                startActivityForResult(settingsActivity,-1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        int newTheme = getThemeId(this);
        if(this.mCurrentTheme != newTheme) {
            this.finish();
            this.startActivity(new Intent(this, this.getClass()));
        }

        //ADS
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        assert mAdView != null;
        mAdView.loadAd(adRequest);
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

        assert rgTaxes != null;
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
                assert etTaxrate != null;
                taxRate = etTaxrate.getText().toString();
                break;
            default:
                break;
        }

        assert etBase != null;
        if ((CurrOps.zero(taxRate)) || CurrOps.zero(etBase.getText().toString())) {
            setToZero();
            return;
        }

        assert rgBase != null;
        switch (rgBase.getCheckedRadioButtonId()) {
            case -1:
                break;
            case R.id.rbSumNet: //сумма к выплате
                String taxSumG = calcTax(etBase.getText().toString(), taxRate); //сумма налога √
                if (getRound(this)) taxSumG = CurrOps.currRound(taxSumG,0);
                else taxSumG = CurrOps.currRound(taxSumG,2);
                String give = calcGross(etBase.getText().toString(),taxSumG); //общая сумма с налогом
                tvGross.setText(convertC(give)); //общая сумма с налогом
                tvTax.setText(convertC(taxSumG)); //сумма налога
                tvNet.setText(convertC(etBase.getText().toString())); //сумма (база)
                break;
            case R.id.rbsumGross://введена общая сумма √
                String taxSumN = calcGrossN(taxRate); //return tax (BASE*100)/100+18
                if (getRound(this)) taxSumN = CurrOps.currRound(taxSumN,0);
                else taxSumN = CurrOps.currRound(taxSumN,2);
                //String tax = calcTaxNet(base); //return full sum, then fill taxes.
                tvGross.setText(convertC(etBase.getText().toString()));//общая сумма с налогом
                tvTax.setText(convertC(taxSumN));//сумма налога
                tvNet.setText(convertC(calcBaseNet(taxSumN)));//сумма (база)
                break;
            default:
                break;
        }
    }

    private void setToZero() {
        TextView tvGross = (TextView) findViewById(R.id.tvGross);
        TextView tvTax = (TextView) findViewById(R.id.tvTax);
        TextView tvNet = (TextView) findViewById(R.id.tvNet);
        assert tvGross != null;
        tvGross.setText(getResources().getText(R.string.zero));
        assert tvTax != null;
        tvTax.setText(getResources().getText(R.string.zero));
        assert tvNet != null;
        tvNet.setText(getResources().getText(R.string.zero));
    }

    private String calcBaseNet(String taxSum) {
        EditText etBase = (EditText) findViewById(R.id.etBase);
        Currency curr = Currency.getInstance(Locale.getDefault());
        return CurrOps.sub(curr,etBase.getText().toString(),taxSum);
    }

    private String calcTaxNet(String base) {
        EditText etBase = (EditText) findViewById(R.id.etBase);
        Currency curr = Currency.getInstance(Locale.getDefault());
        assert etBase != null;
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

    private String calcGross(String base, String taxSum) {
        Currency curr = Currency.getInstance(Locale.getDefault());
        return CurrOps.add(curr, base, taxSum);
    }

    private String calcGrossN(String taxRate) {
        EditText etBase = (EditText) findViewById(R.id.etBase);
        Currency curr = Currency.getInstance(Locale.getDefault());
        String top = etBase.getText().toString();
        String tax100 = CurrOps.add(curr, "100", taxRate);
        String bottom = CurrOps.div(curr, tax100, "100");
        String base = CurrOps.div(curr, top, bottom);
        return CurrOps.sub(curr, etBase.getText().toString(), base);
    }

    private String calcTax(String base, String tax) {
        Currency curr = Currency.getInstance(Locale.getDefault());
        tax = CurrOps.div(curr,tax,"100");
        return CurrOps.mult(curr, base, tax);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState");
        TextView tvGross = (TextView) findViewById(R.id.tvGross);
        TextView tvTax = (TextView) findViewById(R.id.tvTax);
        TextView tvNet = (TextView) findViewById(R.id.tvNet);
        assert tvGross != null;
        outState.putString(SAVE_TV_GROSS, tvGross.getText().toString());
        assert tvNet != null;
        outState.putString(SAVE_TV_NET, tvNet.getText().toString());
        assert tvTax != null;
        outState.putString(SAVE_TV_TAX, tvTax.getText().toString());
    }


}
