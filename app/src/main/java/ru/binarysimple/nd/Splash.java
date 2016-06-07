package ru.binarysimple.nd;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash extends Activity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        TextView textView = (TextView) findViewById(R.id.splashTextView);
        textView.setText("binarysimple");
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "Play-Bold.ttf");
        textView.setTypeface(typeFace);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(Splash.this, Main.class);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

        setDrawable();

    }

    private void setDrawable() {
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR,
                new int[]{0xA53F51B5, 0xFF3F51B5});
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        double diag = Math.sqrt((size.x) * (size.x) + (size.y) * (size.y));
        diag = diag / 2;
        Integer radius = (int) diag;
        drawable.setGradientRadius(radius);
        ImageView imageView = (ImageView) findViewById(R.id.ivSplash);
        imageView.setImageDrawable(drawable);
    }
}
