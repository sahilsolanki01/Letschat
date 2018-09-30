package com.example.rahul.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity1 extends AppCompatActivity {

    private EditText result;
    private EditText newNumber;
    private TextView displayOperation;

    private String pendingOperation = "=";
    private Double operand1 = null;
    private Double operand2 = null;
    private static final String state ="pending";
    private static final String resvalue ="operand1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        result =(EditText) findViewById(R.id.result);
        newNumber =(EditText) findViewById(R.id.newnumber);
        displayOperation =(TextView) findViewById(R.id.operation);

        Button button0 =  findViewById(R.id.button0);
        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        Button button4 = (Button) findViewById(R.id.button4);
        Button button5 = (Button) findViewById(R.id.button5);
        Button button6 = (Button) findViewById(R.id.button6);
        Button button7 = (Button) findViewById(R.id.button7);
        Button button8 = (Button) findViewById(R.id.button8);
        Button button9 = (Button) findViewById(R.id.button9);
        Button buttonDot = (Button) findViewById(R.id.buttonDot);

        Button buttondivide = (Button) findViewById(R.id.buttondivide);
        Button buttonmultiply = (Button) findViewById(R.id.buttonmultiply);
        Button buttonminus = (Button) findViewById(R.id.buttonminus);
        Button buttonremainder = (Button) findViewById(R.id.buttonremainder);
        Button buttonplus = (Button) findViewById(R.id.buttonplus);
        Button buttonequal = (Button) findViewById(R.id.buttonequal);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b =(Button)view;
                newNumber.append(b.getText().toString());
            }
        };

        button0.setOnClickListener(listener);
        button1.setOnClickListener(listener);
        button2.setOnClickListener(listener);
        button3.setOnClickListener(listener);
        button4.setOnClickListener(listener);
        button5.setOnClickListener(listener);
        button6.setOnClickListener(listener);
        button7.setOnClickListener(listener);
        button8.setOnClickListener(listener);
        button9.setOnClickListener(listener);
        buttonDot.setOnClickListener(listener);

        View.OnClickListener oplistener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                String op = b.getText().toString();
                String value = newNumber.getText().toString();
                try {
                    Double doublevalue = Double.valueOf(value);
                    performoperation(op,doublevalue);

                }catch(NumberFormatException e){
                    newNumber.setText("");
                }
                pendingOperation = op;
                displayOperation.setText(pendingOperation);

            }
        };

        buttondivide.setOnClickListener(oplistener);
        buttonmultiply.setOnClickListener(oplistener);
        buttonminus.setOnClickListener(oplistener);
        buttonplus.setOnClickListener(oplistener);
        buttonremainder.setOnClickListener(oplistener);
        buttonequal.setOnClickListener(oplistener);

        Button buttoneg = (Button) findViewById(R.id.buttoneg);

        buttoneg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = newNumber.getText().toString();
                if(value.length()==0){
                    newNumber.setText("-");
                }
                else {
                    try{
                        Double doublevalue = Double.valueOf(value);
                        doublevalue*=-1;
                        newNumber.setText(doublevalue.toString());
                    }catch(NumberFormatException e){

                        newNumber.setText("");
                    }

                }
            }
        });

        }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(state,pendingOperation);
        if(operand1!=null){
            outState.putDouble(resvalue,operand1);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pendingOperation = savedInstanceState.getString(state);
        operand1 = savedInstanceState.getDouble(resvalue);
        displayOperation.setText(pendingOperation);
    }

    public void performoperation(String operation , Double value){
        if(operand1==null){
            operand1 =value;
        }
        else{
            operand2 = value;

            if(pendingOperation.equals("=")){
                pendingOperation= operation;
                }

                switch (pendingOperation){
                    case "=":
                        operand1=operand2;
                    break;
                    case "/":
                        if(operand2==0){
                            operand1 = 0.0;
                        }
                        operand1/=operand2;
                        break;
                    case"*":
                        operand1*=operand2;
                        break;
                    case"+":
                        operand1+=operand2;
                        break;
                    case"-":
                        operand1-=operand2;
                        break;
                    case"%":
                        operand1%=operand2;
                        break;

                        }



        }
        result.setText(operand1.toString());
        newNumber.setText("");

    }

}
