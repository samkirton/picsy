package com.memtrip.picsy.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.memtrip.picsy.sample.R;

import java.io.IOException;

public class DisplayActivity extends Activity implements View.OnClickListener {
    private ImageView uiPhotoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        uiPhotoImageView = (ImageView)findViewById(R.id.activity_display_capture);
        uiPhotoImageView.setOnClickListener(this);
    }

    private void capture_Click() {
        Intent intent = new Intent(this,PhotoCaptureActivity.class);
        startActivityForResult(intent,0);
    }

    private void setPhotoLayoutParams() {
        ViewGroup.LayoutParams params = uiPhotoImageView.getLayoutParams();
        params.width = getResources().getDimensionPixelOffset(R.dimen.activity_display_photo);
        params.height = getResources().getDimensionPixelOffset(R.dimen.activity_display_photo);
        uiPhotoImageView.setLayoutParams(params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PhotoCaptureActivity.RESULT_CODE) {
            String uri = data.getStringExtra(PhotoCaptureActivity.URI);
            Uri imageUri = Uri.parse(uri);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                uiPhotoImageView.setImageDrawable(new BitmapDrawable(getResources(),bitmap));
                setPhotoLayoutParams();
            } catch (IOException e) { }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == uiPhotoImageView) {
            capture_Click();
        }
    }
}
