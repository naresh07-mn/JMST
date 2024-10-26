package com.travel_track.solution.views.components;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.travel_track.solution.R;

import java.util.Calendar;

public class CustomDialogWithInput extends Dialog {
    int buttonCount = 0;
    Button button1, button2, button3;
    TextView title, message;
    ImageView icon;
    EditText edittext_1;
    EditText edittext_2;
    LinearLayout text_input1;
    LinearLayout text_input_type_2;
    LinearLayout title_layout, layout_buttons;
    Context context;

    public CustomDialogWithInput(Context context, String... options) {
        super(context, R.style.AlertDialogCustom);
        this.context = context;
        initDialog(options);
    }

    private void initDialog(String... options) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (options != null) {
            for (String option : options) {
                if (option != null && option.length() > 0) buttonCount++;
            }
        }
        int layout = R.layout.layout_custom_dialog_with_input;
        this.setContentView(layout);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.88);
        lp.width = screenWidth;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);

        title_layout = findViewById(R.id.title_layout);
        title_layout.setVisibility(View.GONE);

        layout_buttons = findViewById(R.id.layout_buttons);

        icon = findViewById(R.id.icon);
        icon.setVisibility(View.GONE);
        title = findViewById(R.id.title);

        message = findViewById(R.id.message);
        message.setMovementMethod(new ScrollingMovementMethod());

        edittext_1 = findViewById(R.id.edittext_1);
        edittext_2 = findViewById(R.id.edittext_2);
        text_input1 = findViewById(R.id.dialog_layout);
        text_input_type_2 = findViewById(R.id.dialog_layout_2);

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
    }

    @Override
    public void setTitle(CharSequence title) {
        title_layout.setVisibility(View.VISIBLE);
        if (title != null && title.length() > 0) {
            this.title.setVisibility(View.VISIBLE);
            this.title.setText(title);
        } else {
            this.title.setVisibility(View.GONE);
        }

    }

    public void setIcon(int resId) {
        this.icon.setImageResource(resId);
    }

    public void setMessage(CharSequence message) {
        if(!TextUtils.isEmpty(message)) {
            this.message.setText(message);
        } else {
            this.message.setVisibility(View.GONE);
        }
    }

    public void setEditTextVisibility(String hintText, String value){
        this.edittext_1.setHint(hintText);
        this.edittext_1.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(value)){
            this.edittext_1.setEnabled(false);
            this.edittext_1.setText(value);
            try{
                Integer reading = Integer.parseInt(value);
                if (reading <=0){
                    this.edittext_1.setEnabled(true);
                    this.edittext_1.setSelection(0, this.edittext_1.getText().toString().length());
                }
            } catch (Exception e){
                this.edittext_1.setEnabled(true);
            }
        }
    }

    public void setEditTextVisibility2(String hintText, String value){
        this.edittext_2.setHint(hintText);
        this.edittext_2.setVisibility(View.VISIBLE);
        this.edittext_2.addTextChangedListener(mTimeEntryWatcher);
        if(!TextUtils.isEmpty(value)){
            //this.edittext_2.setEnabled(false);
            this.edittext_2.setText(value);
            try{
                Integer reading = Integer.parseInt(value);
                if (reading <=0){
                    this.edittext_2.setEnabled(true);
                }
            } catch (Exception e){
                this.edittext_2.setEnabled(true);
            }
        }
    }

    public void setError1(String error){
       // text_input1.setError(error);
        this.edittext_1.setVisibility(View.VISIBLE);
    }

    public String getInputText() {
        return edittext_1.getText().toString().trim();
    }
    public String getInputText2() {
        return edittext_2.getText().toString().trim();
    }

    public void setButton1(CharSequence text, View.OnClickListener listener) {
        button1.setVisibility(View.VISIBLE);
        button1.setText(text);
        button1.setOnClickListener(listener);
    }

    public void setButton2(CharSequence text, View.OnClickListener listener) {
        button2.setVisibility(View.VISIBLE);
        button2.setText(text);
        button2.setOnClickListener(listener);
    }

    public boolean isValid = true;
    private final TextWatcher mTimeEntryWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String working = s.toString();
            isValid = true;
            if (working.length()==2 && before ==0) {
                if (Integer.parseInt(working) < 1 || Integer.parseInt(working)>23) {
                    isValid = false;
                } else {
                    working+=":";
                    edittext_2.setText(working);
                    edittext_2.setSelection(working.length());
                }
            }
            else if (working.length()==5 && before ==0) {
                String enterMin = working.substring(3);
                if (Integer.parseInt(enterMin) >= 60) {
                    isValid = false;
                }
            } else if (working.length()!=5) {
                isValid = false;
            }
            if (!isValid) {
               // text_input_type_2.setError("Enter Valid Time");
            } else {
                //text_input_type_2.setError(null);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    };
}
