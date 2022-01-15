package com.radiant.settings.color;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.radiant.support.monet.SettingsColors;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorListener;
import com.android.settings.R;
import java.util.Objects;

public class WallpaperColorActivity extends AppCompatActivity {
    public static final int[] i = new int[1];
    ColorPickerView colorPickerView;
    ConstraintLayout cL;
    ImageView iV1, iV2, iV3, bck, apl;
    FrameLayout mkd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_wall);
        setupViews();
        setupLayout();
        setupListners();
    }


    void setupLayout() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        getWindow().setDecorFitsSystemWindows(false);
        WindowInsetsController controller = getWindow().getInsetsController();
        if (controller != null) {
            controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setDecorFitsSystemWindows(false);

        colorPickerView.setPaletteDrawable(WallpaperManager.getInstance(this).getDrawable());

        for (int index = 0; index < colorPickerView.getChildCount(); index++) {
            View nextChild = colorPickerView.getChildAt(index);
            if (nextChild instanceof ImageView) {
                ((ImageView) nextChild).setAdjustViewBounds(true);
                ((ImageView) nextChild).setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }
    }

    void setupViews() {
        colorPickerView = findViewById(R.id.colorPickerView);
        cL = findViewById(R.id.constl);
        iV1 = findViewById(R.id.prim);
        iV2= findViewById(R.id.sec);
        iV3 = findViewById(R.id.tert);
        bck = findViewById(R.id.back);
        apl = findViewById(R.id.apply);
        mkd = findViewById(R.id.makedotoolbar);
    }

    void setupListners() {
        colorPickerView.setColorListener((ColorListener) (color, fromUser) -> {
            if (i[0] != color) {
                i[0] = color;
                if(i[0] == Color.BLACK)
                    i[0] = Color.parseColor("#171717");
                SettingsColors sc = new SettingsColors(i[0]);
                cL.getBackground().setTint(sc.mainBG());
                iV1.getDrawable().setTint(sc.accentCol());
                iV2.getDrawable().setTint(sc.secAccentCol());
                iV3.getDrawable().setTint(sc.tertAccentCol());
                mkd.setBackgroundColor(adjustAlpha(sc.secBG(), 0.7f));
            }
        });

        apl.setOnClickListener(v -> {

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.apply)+"?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Settings.System.putInt(WallpaperColorActivity.this.getContentResolver(), Settings.System.MONET_CUSTOM_COLOR, i[0]);
                        WallpaperColorActivity.this.finish();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
        });

        bck.setOnClickListener(v -> {
            onBackPressed();
        });

    }

    @ColorInt
    public static int adjustAlpha(@ColorInt int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupViews();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupViews();
    }
    
}