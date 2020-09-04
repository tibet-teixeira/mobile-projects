package tesky.vamosrachar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements TextWatcher {
    private double value = 0.0;
    private int people = 0;
    private double result = 0;
    private DecimalFormat df = new DecimalFormat("0.00");


    EditText totalValue;
    EditText numPeople;
    TextView valuePerPerson;

    FloatingActionButton shareButton;
    FloatingActionButton voiceButton;

    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();

        totalValue = findViewById(R.id.totalValue);
        numPeople = findViewById(R.id.numPeople);
        valuePerPerson = findViewById(R.id.valuePerPerson);

        shareButton = findViewById(R.id.shareButton);
        voiceButton = findViewById(R.id.voiceButton);


        valuePerPerson.setText("R$ 0.00");
        totalValue.addTextChangedListener(this);
        numPeople.addTextChangedListener(this);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Locale localeBR = new Locale("pt", "BR");
                    tts.setLanguage(localeBR);
                }
            }
        });

        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dollars = String.valueOf((int) Math.floor(result));
                String cents = String.valueOf((int) ((result - Math.floor(result)) * 100));
                String text;

                if (cents.equals("0")) {
                    text = dollars + " reais";
                } else if (dollars.equals("0.0")) {
                    text = cents + " centavos";
                } else {
                    text = dollars + " reais e " + cents + " centavos";

                }
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });


        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message) + df.format(result));
                startActivity(intent);
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        try {
            value = Double.parseDouble(totalValue.getText().toString());
            people = Integer.parseInt(numPeople.getText().toString());

            if ((value != 0) && (people != 0)) {
                BigDecimal bd = new BigDecimal(value / people).setScale(2, RoundingMode.HALF_UP);
                result = bd.doubleValue();

                valuePerPerson.setText("R$ " + df.format(result));
            }
        } catch (Exception e) {
            valuePerPerson.setText("R$ 0.00");
            result = 0;
        }
    }
}
