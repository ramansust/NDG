package com.nissan.alldriverguide;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nissan.alldriverguide.utils.Values;

import java.io.File;
import java.util.Arrays;

public class TyreTransitionActivity extends Activity {
    private GestureDetector mGestureDetector;

    private ImageView imageView;
    private TextView txtView;

    private SimpleDraweeView draweeView;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_transition);

        draweeView = (SimpleDraweeView) findViewById(R.id.img_view_git);
        txtView = (TextView) findViewById (R.id.txt_title);

        imageView = (ImageView) findViewById (R.id.img_view_back);
        imageView.setOnClickListener (new OnClickListener () {

            @Override
            public void onClick (View v) {
                // TODO Auto-generated method stub
                finish ();
            }
        });

        if (Values.gif_index == 2) {
            Values.tyre_gif_folder = Values.car_path + "/tyre_gifs/";
        } else if (Values.gif_index == 6) {
            Values.tyre_gif_folder = Values.car_path + "/repair_kit/";
        } else {
            Values.tyre_gif_folder = Values.car_path + "/tyre_gifs/";
        }

        Values.gif_count = checkFiles (Values.tyre_gif_folder);

        String swipe = getIntent ().getStringExtra ("swipe");

        if (swipe != null) {
            int count;

            count = getIntent ().getIntExtra ("count", 1);

            Values.i = count;
            System.out.println ("swipe= " + swipe + " count= " + count);
            if (swipe.equals ("left")) {

                overridePendingTransition (R.anim.left_in, R.anim.left_out);
                try {
                    DraweeController controller =
                            Fresco.newDraweeControllerBuilder()
                                    .setUri("file:///" + Values.tyre_gif_folder + Values.gif_names.get (count))
                                    .setAutoPlayAnimations(true)
                                    .build();

                    draweeView.setController(controller);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace ();
                }

                txtView.setText (count + "/" + (Values.gif_names.size () - 1));

            } else {
                overridePendingTransition (R.anim.right_in, R.anim.right_out);
                try {
                    DraweeController controller =
                            Fresco.newDraweeControllerBuilder()
                                    .setUri("file:///" + Values.tyre_gif_folder + Values.gif_names.get (count))
                                    .setAutoPlayAnimations(true)
                                    .build();

                    draweeView.setController(controller);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace ();
                }

                txtView.setText (count + "/" + (Values.gif_names.size () - 1));
            }
        } else {

            Values.i = 0;

            overridePendingTransition (R.anim.right_in, R.anim.right_out);
            try {
                DraweeController controller =
                        Fresco.newDraweeControllerBuilder()
                                .setUri("file:///" + Values.tyre_gif_folder + Values.gif_names.get (Values.i))
                                .setAutoPlayAnimations(true)
                                .build();

                draweeView.setController(controller);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace ();
            }
            txtView.setText ("1/" + (Values.gif_names.size () - 1));
            txtView.setVisibility (View.INVISIBLE);
        }

        CustomGestureDetector customGestureDetector = new CustomGestureDetector ();
        mGestureDetector = new GestureDetector (this, customGestureDetector);

        draweeView.setOnTouchListener (new View.OnTouchListener () {

            @Override
            public boolean onTouch (View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent (event);
            }
        });
    }

    public int checkFiles (String s) {

        Values.i = 0;
        Values.gif_names.clear ();
        String[] file_names = null;
        File f = new File (s);
        File[] file = f.listFiles ();

        int size = 0;
        if (file != null && file.length > 0) {
            file_names = new String[file.length];

            for (int i = 0; i < file.length; i++) {
                if (file[i].getName ().contains (".gif")) {
                    file_names[i] = file[i].getName ();
                    size = size + 1;
                }
            }

            Arrays.sort (file_names);

            for (int j = 0; j < file_names.length; j++) {
                Values.gif_names.add(file_names[j]);
            }
            return size;
        } else {
            return size;
        }

    }

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling (MotionEvent e1, MotionEvent e2, float velocityX,
                                float velocityY) {

            // Swipe left (next)
            if (e1.getX () > e2.getX ()) {
                if (Values.i == Values.gif_names.size () - 1)
                    Toast.makeText (getApplicationContext (), "last",
                            Toast.LENGTH_SHORT);
                else if (Values.i < Values.gif_names.size () - 1) {
                    Values.i++;
                    Intent inttent = new Intent (TyreTransitionActivity.this,
                            TyreTransitionActivity.class);
                    inttent.putExtra ("swipe", "left");
                    inttent.putExtra ("count", Values.i);
                    inttent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity (inttent);
                    finish ();
                }
            }

            // Swipe right (previous)
            if (e1.getX () < e2.getX ()) {
                if (Values.i == 0)
                    Toast.makeText (getApplicationContext (), "It's first",
                            Toast.LENGTH_SHORT);
                else if (Values.i > 1) {
                    Values.i--;
                    Intent inttent = new Intent (TyreTransitionActivity.this,
                            TyreTransitionActivity.class);
                    inttent.putExtra ("swipe", "right");
                    inttent.putExtra ("count", Values.i);
                    inttent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity (inttent);
                    finish ();
                }
            }

            return super.onFling (e1, e2, velocityX, velocityY);
        }
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        mGestureDetector.onTouchEvent (event);

        return super.onTouchEvent (event);
    }

    @Override
    protected void onPause () {
        super.onPause ();
        overridePendingTransition (R.anim.right_in, R.anim.right_out);
    }

    @Override
    protected void onStop () {
        // TODO Auto-generated method stub
        super.onStop ();
        System.gc ();
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged (newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            requestWindowFeature (Window.FEATURE_NO_TITLE);
            getWindow ().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView (R.layout.activity_transition);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // no need to fullscreen
            setContentView (R.layout.activity_transition);
        }
    }

    @Override
    public void onBackPressed () {
        super.onBackPressed ();
    }

}
