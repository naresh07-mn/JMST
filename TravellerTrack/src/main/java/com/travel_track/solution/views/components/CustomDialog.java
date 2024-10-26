package com.travel_track.solution.views.components;

import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.travel_track.solution.R;

public class CustomDialog extends Dialog {
    int buttonCount = 0;
    Button button1, button2, button3;
    TextView title, message;
    ImageView icon;
    LinearLayout title_layout, layout_buttons;

    public CustomDialog(Context context, String... options) {
        super(context, R.style.AlertDialogCustom);
        initDialog(context, options);
    }

    private void initDialog(Context context, String... options) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (options != null) {
            for (String option : options) {
                if (option != null && option.length() > 0) buttonCount++;
            }
        }
        int layout = R.layout.layout_custom_dialog;
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
        this.message.setText(message);
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

    public void setButton3(CharSequence text, View.OnClickListener listener) {
        button3.setVisibility(View.VISIBLE);
        button3.setText(text);
        button3.setOnClickListener(listener);
    }
}
