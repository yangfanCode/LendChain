package com.lend.lendchain.ui.activity.cut;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.lend.lendchain.R;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.utils.BitmapUtil;
import com.lend.lendchain.utils.LogUtils;
import com.lend.lendchain.versioncontrol.utils.FilePathUtils;
import com.yangfan.utils.CommonUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 裁剪图片的Activity
 */
public class ClipImageActivity extends BaseActivity implements OnClickListener {
    public static final String RESULT_PATH = "image";
    private static final String KEY = "path";
    private ClipImageLayout mClipImageLayout = null;

    public static void startActivity(Activity activity, Uri path, int code) {
        Intent intent = new Intent(activity, ClipImageActivity.class);
        intent.putExtra(KEY, path);
        activity.startActivityForResult(intent, code);
    }

    @Override
    public void initView() { }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_image);
        mClipImageLayout = (ClipImageLayout) findViewById(R.id.clipImageLayout);
        Uri path = getIntent().getParcelableExtra(KEY);
        mClipImageLayout.setImageBitmap(BitmapUtil.getSmallBitmap2(CommonUtils.getImagePathFromUri(path, this), CommonUtils.getScreenWidth(this), CommonUtils.getScreenWidth(this), 200));
//        mClipImageLayout.setImageBitmap(path);
        findViewById(R.id.tv_ok).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.tv_ok) {
            Bitmap bitmap = mClipImageLayout.clip();

            String filePath = FilePathUtils.getFilePath(this, null);
            String fileName = System.currentTimeMillis() + ".jpg";
            File file = new File(filePath, fileName);

            LogUtils.LogE(FilePathUtils.class, "filePath====>" + file.getAbsolutePath());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                bitmap.compress(CompressFormat.JPEG, 95, fos);
                fos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null)
                        fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Intent intent = new Intent();
            intent.putExtra(RESULT_PATH, file.getAbsolutePath());
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }

}
