package com.example.smartcalci;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import  java.util.*;
public class MainActivity extends AppCompatActivity {
    private Button select, pred;
    private TextView ans;
    private ImageView imgveiw;
    private Bitmap img;
    private TextView res;
    public String s;
    public String expression;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgveiw = (ImageView) findViewById(R.id.img);
        select = (Button) findViewById(R.id.select);
        pred = (Button) findViewById(R.id.pred);
        ans = (TextView) findViewById(R.id.ans);
        res = (TextView) findViewById(R.id.result);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);

        }
//////////////////////////////////////////////////////////////////////////        /////////////////////////////////////////////////////////////////////////////////////////
        pred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            /*long finalres;
            finalres=calculateExpression(expression);
            String finalans =Long.toString(finalres);
            res.setText(finalans);*/
            String no=expression;
                long b=calculateExpression(no);
                String finalans =Long.toString(b);
                res.setText(finalans);

            }
        });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });

    }

///////////////////////////////////////////////////////////////////////////////////////////////////
public static long calculateExpression(String expression) {

    Stack<Long> operandStack = new Stack<>();
    Stack<Character> operatorStack = new Stack<>();

    if (!isValidExpression(expression)) {
        System.out.println("Not a valid expression to evaluate");
        return 0;
    }

    int i = 0;
    String currentInteger = null;
    while (i < expression.length()) {

        // System.out.println(expression.charAt(i));
        if (expression.charAt(i) >= '0' && expression.charAt(i) <= '9') {

            currentInteger = expression.charAt(i) + "";
            i++;
            while (i != expression.length() && (expression.charAt(i) >= '0' && expression.charAt(i) <= '9')) {
                currentInteger = currentInteger + expression.charAt(i);
                i++;
            }

            operandStack.push(Long.parseLong(currentInteger));
        } else {

            if (expression.charAt(i) == ')') {

                while (operatorStack.peek() != '(') {
                    performArithmeticOperation(operandStack, operatorStack);
                }
                operatorStack.pop();
            } else {

                Character currentOperator = expression.charAt(i);
                Character lastOperator = (operatorStack.isEmpty() ? null : operatorStack.peek());


                if (lastOperator != null && checkPrecedence(currentOperator, lastOperator)) {
                    performArithmeticOperation(operandStack, operatorStack);
                }
                operatorStack.push(expression.charAt(i));

            }
            i++;
        }

    }


    while (!operatorStack.isEmpty()) {
        performArithmeticOperation(operandStack, operatorStack);
    }

    //    System.out.println(Arrays.toString(operandStack.toArray()));
    //    System.out.println(Arrays.toString(operatorStack.toArray()));

    return operandStack.pop();

}

    public static void performArithmeticOperation(Stack<Long> operandStack, Stack<Character> operatorStack) {
        try {
            long value1 = operandStack.pop();
            long value2 = operandStack.pop();
            char operator = operatorStack.pop();

            long intermediateResult = arithmeticOperation(value1, value2, operator);
            operandStack.push(intermediateResult);
        } catch (EmptyStackException e) {
            System.out.println("Not a valid expression to evaluate");
            throw e;
        }
    }


    public static boolean checkPrecedence(Character operator1, Character operator2) {

        List<Character> precedenceList = new ArrayList<>();
        precedenceList.add('(');
        precedenceList.add(')');
        precedenceList.add('/');
        precedenceList.add('*');
        precedenceList.add('%');
        precedenceList.add('+');
        precedenceList.add('-');


        if(operator2 == '(' ){
            return false;
        }

        if (precedenceList.indexOf(operator1) > precedenceList.indexOf(operator2)) {
            return true;
        } else {
            return false;
        }

    }

    public static long arithmeticOperation(long value2, long value1, Character operator) {

        long result;

        switch (operator) {

            case '+':
                result = value1 + value2;
                break;

            case '-':
                result = value1 - value2;
                break;

            case '*':
                result = value1 * value2;
                break;

            case '/':
                result = value1 / value2;
                break;

            case '%':
                result = value1 % value2;
                break;

            default:
                result = value1 + value2;


        }
        return result;
    }


    public static boolean isValidExpression(String expression) {

        if ((!Character.isDigit(expression.charAt(0)) && !(expression.charAt(0) == '('))
                || (!Character.isDigit(expression.charAt(expression.length() - 1)) && !(expression.charAt(expression.length() - 1) == ')'))) {
            return false;
        }

        HashSet<Character> validCharactersSet = new HashSet<>();
        validCharactersSet.add('*');
        validCharactersSet.add('+');
        validCharactersSet.add('-');
        validCharactersSet.add('/');
        validCharactersSet.add('%');
        validCharactersSet.add('(');
        validCharactersSet.add(')');

        Stack<Character> validParenthesisCheck = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {

            if (!Character.isDigit(expression.charAt(i)) && !validCharactersSet.contains(expression.charAt(i))) {
                return false;
            }

            if (expression.charAt(i) == '(') {
                validParenthesisCheck.push(expression.charAt(i));
            }

            if (expression.charAt(i) == ')') {

                if (validParenthesisCheck.isEmpty()) {
                    return false;
                }
                validParenthesisCheck.pop();
            }
        }

        if (validParenthesisCheck.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }








////////////////////////////////////////////////////////////////////////////////////




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100) {
            img = (Bitmap) data.getExtras().get("data");
            imgveiw.setImageBitmap(img);
            FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(img);

            FirebaseVision firebaseVision = FirebaseVision.getInstance();

            FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

            Task<FirebaseVisionText> task = firebaseVisionTextRecognizer.processImage(firebaseVisionImage);

            task.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {

                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    String g = firebaseVisionText.getText();

                    char[] ch2 = new char[10];

                    int i;
                    char[] ch1 = g.toCharArray();

                    for (i = 0; i < ch1.length; i++) {
                        if (ch1[i] == 't') {
                            ch2[i] = '+';
                        } else {
                            ch2[i] = ch1[i];
                        }
                    }
                     expression = new String(ch2);
                    ans.setText(expression);


///////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////

                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }













}

