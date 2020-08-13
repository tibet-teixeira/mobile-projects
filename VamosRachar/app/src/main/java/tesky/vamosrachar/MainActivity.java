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

        totalValue = (EditText) findViewById(R.id.totalValue);
        numPeople = (EditText) findViewById(R.id.numPeople);
        valuePerPerson = (TextView) findViewById(R.id.valuePerPerson);

        shareButton = (FloatingActionButton) findViewById(R.id.shareButton);
        voiceButton = (FloatingActionButton) findViewById(R.id.voiceButton);


        valuePerPerson.setText("$ 0.00");
        totalValue.addTextChangedListener(this);
        numPeople.addTextChangedListener(this);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                }
            }
        });

        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dollars = String.valueOf(Math.floor(result));
                String cents = String.valueOf((int) ((result - Math.floor(result)) * 100));
                String text;

                if (cents.equals("0")) {
                    text = dollars + " dollars";
                } else if (dollars.equals("0.0")) {
                    text = cents + " cents";

                } else {
                    text = dollars + " dollars and " + cents + " cents";

                }
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });


        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message) + result);
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
                DecimalFormat df = new DecimalFormat("0.00");

                BigDecimal bd = new BigDecimal(value / people).setScale(2, RoundingMode.HALF_UP);
                result = bd.doubleValue();

                valuePerPerson.setText("$ " + df.format(result));
            }
        } catch (Exception e) {
            valuePerPerson.setText("$ 0.00");
            result = 0;
        }
    }
}
