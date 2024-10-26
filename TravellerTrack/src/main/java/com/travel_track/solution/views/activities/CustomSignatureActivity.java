package com.travel_track.solution.views.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.travel_track.solution.R;
import com.travel_track.solution.apihandler.ApiHandler;
import com.travel_track.solution.apihandler.ImageProcessingHandler;
import com.travel_track.solution.apihandler.RestClient;
import com.travel_track.solution.data.PreferenceManager;
import com.travel_track.solution.model.BookingModel;
import com.travel_track.solution.model.FeedbackModel;
import com.travel_track.solution.model.LoginModel;
import com.travel_track.solution.model.UploadResponce;
import com.travel_track.solution.model.UploadSignatureResponce;
import com.travel_track.solution.utils.ImageResizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CustomSignatureActivity extends AppCompatActivity implements View.OnClickListener {


    TextView clear;
    Button save, cancel;
    LinearLayout mContent;
    Bitmap bitmap;
    View view;
    File file;

    @BindView(R.id.booking_id_value) @Nullable
    EditText bookingId;
     signature mSignature;
    ProgressDialog pDialog;
    BookingModel bookingDetails;
    PreferenceManager preferenceManager;

    ApiHandler.UpdateFeedbackAPIService updateFeedbackAPIService;

    // Creating Separate Directory for saving Generated Images
    String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/MySignature/";
    String pic_name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    String StoredPath = DIRECTORY + pic_name + ".png";
    private LoginModel userDetails;
    String orgId;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_signature);
        ButterKnife.bind(this);

        if(getIntent().getExtras()!=null) {
            Bundle extra = getIntent().getExtras();
            bookingDetails = (BookingModel) extra.getSerializable("booking_details");
        }

        mContent = findViewById(R.id.mySignature);
        mSignature = new signature(getApplicationContext(), null);
        //mSignature.setBackgroundColor(Color.WHITE);
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        clear = findViewById(R.id.clear);
        clear.setOnClickListener(this);

        save = findViewById(R.id.save);
        save.setOnClickListener(this);

        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        view = mContent;

        // Method to create Directory, if the Directory doesn't exists
        file = new File(DIRECTORY);
        if (!file.exists()) {
            file.mkdir();
        }
        preferenceManager = PreferenceManager.getInstance(this);
        userDetails = preferenceManager.getUserInfo();
        orgId = preferenceManager.getStingData("c_company_id");
        initDialog();
    }

    @Override
    public void onClick(View v) {
        if(v == clear){
            if(mSignature !=null){
                mSignature.clear();
            }
        } else if (save == v){
            view.setDrawingCacheEnabled(true);
            mSignature.save(view, StoredPath);

        } else if (cancel == v){

            Intent mIntent = new Intent(getApplicationContext(), HomeActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mIntent);
            finish();
            //Toast.makeText(this, "feedback not submitted", Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadFile(Map<String, RequestBody> map) throws IOException {
        ApiHandler.UploadImageAPIServise getResponse = ImageProcessingHandler.getClient().create(ApiHandler.UploadImageAPIServise.class);
        Call<UploadResponce> call = getResponse.upload(map);
        Log.i("custom_map", "Calling function" + map + userDetails.getCompanyId().toString() + bookingId);
        call.enqueue(new Callback<UploadResponce>() {
            @Override
            public void onResponse(Call<UploadResponce> call, Response<UploadResponce> response) {
                Log.i("custom_map", "responsse code = " + response.code());

                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getMessage() != null) {
                        hidepDialog();
                        sendBookingId(bookingId.getText().toString());
                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    hidepDialog();
                    Toast.makeText(getApplicationContext(), "problem uploading signature", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UploadResponce> call, Throwable t) {
                hidepDialog();
                Toast.makeText(CustomSignatureActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.v("Response gotten is", t.getMessage());
            }
        });
    }


    private void sendBookingId(String bookingId) {
        // Map is used to multipart the file using okhttp3.RequestBody
        ApiHandler.UploadSignaturAPIServise getResponse = RestClient.getClient().create(ApiHandler.UploadSignaturAPIServise.class);
        Call<UploadSignatureResponce> call = getResponse.upload(orgId,bookingId);
        call.enqueue(new Callback<UploadSignatureResponce>() {
            @Override
            public void onResponse(Call<UploadSignatureResponce> call, Response<UploadSignatureResponce> response) {
                if (response.isSuccessful()) {
                    hidepDialog();
                    if (response.body() != null) {
                        mSignature.clear();

                        Toast.makeText(getApplicationContext(),response.body().getMessage().get(0).getMessage(), Toast.LENGTH_SHORT).show();

                    }
                } else {
                    hidepDialog();
                    Toast.makeText(getApplicationContext(), "problem uploading Signature", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UploadSignatureResponce> call, Throwable t) {
                hidepDialog();
                Toast.makeText(CustomSignatureActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.v("Response gotten is", t.getMessage());
            }
        });
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            view.setDrawingCacheEnabled(true);
            mSignature.save(view, StoredPath);
            Toast.makeText(getApplicationContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();
            // Calling the same class
            recreate();
        }
        else {
            Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
        }
    }

    public class signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private final Paint paint = new Paint();
        private final Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        @SuppressLint("WrongThread")
        public void save(View v, String StoredPath) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }

            Canvas canvas = new Canvas(bitmap);
            try {
                // Output the file
                //FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);
                // Convert the output file to Image such as .png
                //bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);///created by naresh bhadana
                byte[] byteArray = byteArrayOutputStream .toByteArray();
           //     String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                Map<String, RequestBody> map = new HashMap<>();
//                    File file = new File(postPath);
                File mfile = new File(getApplicationContext().getCacheDir(), "signatureImage.JPEG");
                try {
                    mfile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(mfile);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    fos.write(byteArray);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Parsing any Media type file
                RequestBody requestBody = RequestBody.create(MediaType.parse("*signatureImage/*"), mfile);
                // Parsing any Media type file
                map.put("file\"; filename=\"" + mfile.getName() + "\"", requestBody);
                try {
                    if (bookingId.getText().toString().isEmpty()){
                        Toast.makeText(CustomSignatureActivity.this, "Please enter booking NO.", Toast.LENGTH_SHORT).show();
                    }else {
                        uploadFile(map);
                        showpDialog();
                    }

                }  catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(String.valueOf(CustomSignatureActivity.this),"encoded");

            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }

        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event);
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY)  {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
    protected void initDialog() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.msg_loading));
        pDialog.setCancelable(true);
    }
    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }
}
