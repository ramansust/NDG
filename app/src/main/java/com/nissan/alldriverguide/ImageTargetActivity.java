package com.nissan.alldriverguide;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.nissan.alldriverguide.augmentedreality.ARJuke;
import com.nissan.alldriverguide.augmentedreality.ARLeaf;
import com.nissan.alldriverguide.augmentedreality.ARLeaf2017;
import com.nissan.alldriverguide.augmentedreality.ARMicra;
import com.nissan.alldriverguide.augmentedreality.ARMicraNew;
import com.nissan.alldriverguide.augmentedreality.ARNavara;
import com.nissan.alldriverguide.augmentedreality.ARNote;
import com.nissan.alldriverguide.augmentedreality.ARPulsar;
import com.nissan.alldriverguide.augmentedreality.ARQashqai;
import com.nissan.alldriverguide.augmentedreality.ARQashqai2017;
import com.nissan.alldriverguide.augmentedreality.ARQashqaiRUS;
import com.nissan.alldriverguide.augmentedreality.ARXtrail;
import com.nissan.alldriverguide.augmentedreality.ARXtrail2017;
import com.nissan.alldriverguide.augmentedreality.ARXtrail2017Rus;
import com.nissan.alldriverguide.augmentedreality.ARXtrailRUS;
import com.nissan.alldriverguide.customviews.DialogController;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.utils.Analytics;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;
import com.nissan.alldriverguide.vuforia.LoadingDialogHandler;
import com.nissan.alldriverguide.vuforia.SampleApplicationControl;
import com.nissan.alldriverguide.vuforia.SampleApplicationException;
import com.nissan.alldriverguide.vuforia.SampleApplicationGLView;
import com.nissan.alldriverguide.vuforia.SampleApplicationSession;
import com.nissan.alldriverguide.vuforia.Texture;
import com.vuforia.CameraDevice;
import com.vuforia.DataSet;
import com.vuforia.ObjectTracker;
import com.vuforia.STORAGE_TYPE;
import com.vuforia.State;
import com.vuforia.Trackable;
import com.vuforia.Tracker;
import com.vuforia.TrackerManager;
import com.vuforia.Vuforia;

import java.util.ArrayList;
import java.util.Vector;

import static com.nissan.alldriverguide.utils.Values.DEFAULT_CLICK_TIMEOUT;

public class ImageTargetActivity extends AppCompatActivity implements SampleApplicationControl {
    private static final String LOGTAG = "ImageTargets";

    SampleApplicationSession vuforiaAppSession;

    private DataSet mCurrentDataset;
    private int mCurrentDatasetSelectionIndex = 0;
    private ArrayList<String> mDatasetStrings = new ArrayList<String>();

    // Our OpenGL view:
    private SampleApplicationGLView mGlView;

    // Our renderer:
    //private ImageTargetRendererFerrari mRenderer;

    private GestureDetector mGestureDetector;

    // The textures we will use for rendering:
    private Vector<Texture> mTextures;

    private boolean mSwitchDatasetAsap = false;
    private boolean mFlash = false;
    private boolean mContAutofocus = false;
    private boolean mExtendedTracking = false;

    private View mFlashOptionView;
    private RelativeLayout mUILayout;

    public LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(this);

    // Alert Dialog used to display SDK errors
    private AlertDialog mErrorDialog;

    private boolean mIsDroidDevice = false;
    public static boolean isFromShowInfo = false;
    // Called when the activity first starts or the user navigates back to an activity.

    public static boolean isDetected = false;
    public RelativeLayout layoutCameraView;
    public ViewGroup layoutBackRefreshView;
    public ImageView iv;

    //parentview
    public static View inflatedLayout = null;
    //childview
    public static View inflatedLayout_second;

    public boolean mIsActive = false;
    private com.google.android.gms.analytics.Tracker tracker;

    private DisplayMetrics metrics;
    private Resources resources;
    // variable to track event time
    private long mLastClickTime = 0;

    private ARQashqai mRendererQashqai;
    private ARQashqaiRUS mRendererQashqaiRus;

    private ARJuke mRendererJuke;

    private ARXtrail mRendererXtrail;
    private ARXtrailRUS mRendererXtrailRus;

    private ARPulsar mRendererPulsar;
    private ARMicra mRendererMicra;
    private ARNote mRendererNote;
    private ARLeaf mRendererLeaf;
    private ARNavara mRendererNavara;

    private ARMicraNew mRendererMicraNew;
    private ARQashqai2017 mRendererQashqai2017;
    private ARXtrail2017 mRendererXtrail2017;
    private ARLeaf2017 mRendererLeaf2017;
    private ARXtrail2017Rus mRendererXtrailRus2017;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new PreferenceUtil(getApplicationContext()).setOpenCountForRateApp();

        vuforiaAppSession = new SampleApplicationSession(this);

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        resources = new Resources(getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(ImageTargetActivity.this, new PreferenceUtil(getApplicationContext()).getSelectedLang()));

        startLoadingAnimation();

        Logger.error("Path ar",Values.carType + "     ------------" + NissanApp.getInstance().getCarPath(Values.carType));

        // Here added the vuforia xml in arrayList according to car type
        switch (Values.carType) {
            case 1:
                mDatasetStrings.add(NissanApp.getInstance().getCarPath(Values.carType) + Values.ASSETS + "qashqai.xml");
                break;

            case 2:
                mDatasetStrings.add(NissanApp.getInstance().getCarPath(Values.carType) + Values.ASSETS + "qashqairus.xml");
                break;

            case 3:
                mDatasetStrings.add(NissanApp.getInstance().getCarPath(Values.carType) + Values.ASSETS + "juke.xml");
                break;

            case 4:
                mDatasetStrings.add(NissanApp.getInstance().getCarPath(Values.carType) + Values.ASSETS + "xtrail.xml");
                break;

            case 5:
                mDatasetStrings.add(NissanApp.getInstance().getCarPath(Values.carType) + Values.ASSETS + "xtrailrus.xml");
                break;

            case 6:
                mDatasetStrings.add(NissanApp.getInstance().getCarPath(Values.carType) + Values.ASSETS + "pulsar.xml");
                break;

            case 7:
                mDatasetStrings.add(NissanApp.getInstance().getCarPath(Values.carType) + Values.ASSETS + "micra.xml");
                break;

            case 8:
                mDatasetStrings.add(NissanApp.getInstance().getCarPath(Values.carType) + Values.ASSETS + "note.xml");
                break;

            case 9:
                mDatasetStrings.add(NissanApp.getInstance().getCarPath(Values.carType) + Values.ASSETS + "leaf.xml");
                break;

            case 10:
                mDatasetStrings.add(NissanApp.getInstance().getCarPath(Values.carType) + Values.ASSETS + "navara.xml");
                break;

            case 11:
                mDatasetStrings.add(NissanApp.getInstance().getCarPath(Values.carType) + Values.ASSETS + "micrak14.xml");
                break;

            case 12:
                mDatasetStrings.add(NissanApp.getInstance().getCarPath(Values.carType) + Values.ASSETS + "qashqai2017.xml");
                break;

            case 13:
                mDatasetStrings.add(NissanApp.getInstance().getCarPath(Values.carType) + Values.ASSETS + "xtrail2017.xml");
                break;

            case 14:
                mDatasetStrings.add(NissanApp.getInstance().getCarPath(Values.carType) + Values.ASSETS + "leaf2017.xml");
                break;

            case 15:
                mDatasetStrings.add(NissanApp.getInstance().getCarPath(Values.carType) + Values.ASSETS + "xtrail2017rus.xml");
                break;

            default:
                break;
        }

        // set screen orientation for AR
        vuforiaAppSession.initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mGestureDetector = new GestureDetector(this, new GestureListener());

        // Load any sample specific textures:
        mTextures = new Vector<Texture>();
        loadTextures();

        mIsDroidDevice = Build.MODEL.toLowerCase().startsWith("droid");
    }

    // Process Single Tap event to trigger autofocus
    private class GestureListener extends
            GestureDetector.SimpleOnGestureListener {
        // Used to set autofocus one second after a manual focus is triggered
        private final Handler autofocusHandler = new Handler();

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // Generates a Handler to trigger autofocus
            // after 1 second
            autofocusHandler.postDelayed(new Runnable() {
                public void run() {
                    boolean result = CameraDevice.getInstance().setFocusMode(
                            CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO);

                    if (!result)
                        Logger.error("SingleTapUp", "Unable to trigger focus");
                }
            }, 1000L);

            return true;
        }
    }

    // We want to load specific textures from the APK, which we will later use
    // for rendering.

    private void loadTextures() {
        mTextures.add(Texture.loadTextureFromApk("TextureTeapotBrass.png",
                getAssets()));
        mTextures.add(Texture.loadTextureFromApk("TextureTeapotBlue.png",
                getAssets()));
        mTextures.add(Texture.loadTextureFromApk("TextureTeapotRed.png",
                getAssets()));
        mTextures.add(Texture.loadTextureFromApk("ImageTargets/Buildings.jpeg",
                getAssets()));
    }

    // Called when the activity will start interacting with the user.
    @Override
    protected void onResume() {
        Logger.debugging(LOGTAG, "onResume");
        super.onResume();

        // This is needed for some Droid devices to force portrait
        if (mIsDroidDevice) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // Resume the GL view:
        if (mGlView != null) {
            mGlView.setVisibility(View.VISIBLE);
            mGlView.onResume();
        }
        if (isFromShowInfo && !isDetected) {
            isFromShowInfo = false;
            vuforiaAppSession.onResume();
        }
    }

    // Callback for configuration changes the activity handles itself
    @Override
    public void onConfigurationChanged(Configuration config) {
        Logger.debugging(LOGTAG, "onConfigurationChanged");
        super.onConfigurationChanged(config);

        vuforiaAppSession.onConfigurationChanged();
    }

    // Called when the system is about to start resuming a previous activity.
    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        Logger.debugging(LOGTAG, "onPause");
        super.onPause();

        if (mGlView != null) {
//            mGlView.setVisibility(View.INVISIBLE); // This line for AR Black Screen
            mGlView.onPause();
        }

        // Turn off the flash
        if (mFlashOptionView != null && mFlash) {
            // OnCheckedChangeListener is called upon changing the checked state
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                ((Switch) mFlashOptionView).setChecked(false);
            } else {
                ((CheckBox) mFlashOptionView).setChecked(false);
            }
        }

/*
        try {
            vuforiaAppSession.pauseAR();
        } catch (SampleApplicationException e) {
            Logger.error(LOGTAG, e.getString());
        } finally {
            isFromShowInfo = true;
        }
*/

    }

    // The final call you receive before your activity is destroyed.
    @Override
    protected void onDestroy() {
        Logger.debugging(LOGTAG, "onDestroy");
        super.onDestroy();

        try {
            vuforiaAppSession.stopAR();
        } catch (SampleApplicationException e) {
            Logger.error(LOGTAG, e.getString());
        }

        // Unload texture:
        mTextures.clear();
        mTextures = null;

        System.gc();
    }

    // Initializes AR application components.
    private void initApplicationAR() {
        Logger.debugging(LOGTAG, "initApplicationAR()");

        // Create OpenGL ES view:
        int depthSize = 16;
        int stencilSize = 0;
        boolean translucent = Vuforia.requiresAlpha();

        mGlView = new SampleApplicationGLView(this);
        mGlView.init(translucent, depthSize, stencilSize);

        //mRenderer = new ImageTargetRendererFerrari(this, vuforiaAppSession);


        // create carAR instance compare with carType;
        switch (Values.carType) {

            case 1:
                mRendererQashqai = new ARQashqai(this, vuforiaAppSession);
                mGlView.setRenderer(mRendererQashqai);
                break;

            case 2:
                mRendererQashqaiRus = new ARQashqaiRUS(this, vuforiaAppSession);
                mGlView.setRenderer(mRendererQashqaiRus);
                break;

            case 3:
                mRendererJuke = new ARJuke(this, vuforiaAppSession);
                mGlView.setRenderer(mRendererJuke);
                break;

            case 4:
                mRendererXtrail = new ARXtrail(this, vuforiaAppSession);
                mGlView.setRenderer(mRendererXtrail);
                break;

            case 5:
                mRendererXtrailRus = new ARXtrailRUS(this, vuforiaAppSession);
                mGlView.setRenderer(mRendererXtrailRus);
                break;

            case 6:
                mRendererPulsar = new ARPulsar(this, vuforiaAppSession);
                mGlView.setRenderer(mRendererPulsar);
                break;

            case 7:
                mRendererMicra = new ARMicra(this, vuforiaAppSession);
                mGlView.setRenderer(mRendererMicra);
                break;

            case 8:
                mRendererNote = new ARNote(this, vuforiaAppSession);
                mGlView.setRenderer(mRendererNote);
                break;

            case 9:
                mRendererLeaf = new ARLeaf(this, vuforiaAppSession);
                mGlView.setRenderer(mRendererLeaf);
                break;

            case 10:
                mRendererNavara = new ARNavara(this, vuforiaAppSession);
                mGlView.setRenderer(mRendererNavara);
                break;

            case 11:
                mRendererMicraNew = new ARMicraNew(this, vuforiaAppSession);
                mGlView.setRenderer(mRendererMicraNew);
                break;

            case 12:
                mRendererQashqai2017 = new ARQashqai2017(this, vuforiaAppSession);
                mGlView.setRenderer(mRendererQashqai2017);
                break;

            case 13:
                mRendererXtrail2017 = new ARXtrail2017(this, vuforiaAppSession);
                mGlView.setRenderer(mRendererXtrail2017);
                break;

            case 14:
                mRendererLeaf2017 = new ARLeaf2017(this, vuforiaAppSession);
                mGlView.setRenderer(mRendererLeaf2017);
                break;

            case 15:
                mRendererXtrailRus2017 = new ARXtrail2017Rus(this, vuforiaAppSession);
                mGlView.setRenderer(mRendererXtrailRus2017);
                break;

            default:
                break;

        }

    }

    private void startLoadingAnimation() {
        Logger.error(LOGTAG, "startLoadingAnimation()");

        mUILayout = (RelativeLayout) View.inflate(this,
                R.layout.camera_overlay, null);

        mUILayout.setVisibility(View.VISIBLE);
        mUILayout.setBackgroundColor(Color.BLACK);

        // Gets a reference to the loading dialog
        loadingDialogHandler.mLoadingDialogContainer = mUILayout
                .findViewById(R.id.loading_indicator);

        // Shows the loading indicator at start
        loadingDialogHandler
                .sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);

        // Adds the inflated layout to the view
        addContentView(mUILayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    // Methods to load and destroy tracking data.
    @Override
    public boolean doLoadTrackersData() {
        Logger.debugging(LOGTAG, "SampleApplicationControl override method:    doLoadTrackersData()");
        TrackerManager tManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) tManager
                .getTracker(ObjectTracker.getClassType());
        if (objectTracker == null)
            return false;

        if (mCurrentDataset == null)
            mCurrentDataset = objectTracker.createDataSet();

        if (mCurrentDataset == null)
            return false;

//        if (!mCurrentDataset.load (mDatasetStrings.get (mCurrentDatasetSelectionIndex), STORAGE_TYPE.STORAGE_APPRESOURCE))
        if (mDatasetStrings != null && mDatasetStrings.size() > 0 && !mCurrentDataset.load(mDatasetStrings.get(mCurrentDatasetSelectionIndex), STORAGE_TYPE.STORAGE_ABSOLUTE))
            return false;

        if (!objectTracker.activateDataSet(mCurrentDataset))
            return false;

        int numTrackables = mCurrentDataset.getNumTrackables();
        for (int count = 0; count < numTrackables; count++) {
            Trackable trackable = mCurrentDataset.getTrackable(count);
            if (isExtendedTrackingActive()) {
                trackable.startExtendedTracking();
            }

            String name = "" + trackable.getName();
            trackable.setUserData(name);
            Logger.debugging(LOGTAG, "UserData:Set the following user data "
                    + (String) trackable.getUserData());
        }

        return true;
    }

    @Override
    public boolean doUnloadTrackersData() {
        Logger.debugging(LOGTAG, "SampleApplicationControl override method:    doUnloadTrackersData()");


        // Indicate if the trackers were unloaded correctly
        boolean result = true;

        TrackerManager tManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) tManager
                .getTracker(ObjectTracker.getClassType());
        if (objectTracker == null)
            return false;

        if (mCurrentDataset != null && mCurrentDataset.isActive()) {
            if (objectTracker.getActiveDataSet(0).equals(mCurrentDataset)
                    && !objectTracker.deactivateDataSet(mCurrentDataset)) {
                result = false;
            } else if (!objectTracker.destroyDataSet(mCurrentDataset)) {
                result = false;
            }

            mCurrentDataset = null;
        }

        return result;
    }

    @Override
    public void onInitARDone(SampleApplicationException exception) {
        Logger.debugging(LOGTAG, "SampleApplicationControl override method:    onInitARDone(SampleApplicationException exception)");
        if (exception == null) {
            initApplicationAR();

            mIsActive = true;

            // Now add the GL surface view. It is important
            // that the OpenGL ES surface view gets added
            // BEFORE the camera is started and video
            // background is configured.
            addContentView(mGlView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            // Sets the UILayout to be drawn in front of the camera
            mUILayout.bringToFront();

            // Sets the layout background to transparent
            mUILayout.setBackgroundColor(Color.TRANSPARENT);

            vuforiaAppSession.startAR(CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_DEFAULT);

            boolean result = CameraDevice.getInstance().setFocusMode(
                    CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);

            if (result)
                mContAutofocus = true;
            else
                Logger.error(LOGTAG, "Unable to enable continuous autofocus");

            // mSampleAppMenu = new SampleAppMenu(this, this, "Image Targets",
            // mGlView, mUILayout, null);
            // setSampleAppMenuSettings();

        } else {
            Logger.error(LOGTAG, exception.getString());
            showInitializationErrorMessage(exception.getString());
        }
    }

    @Override
    public void onVuforiaUpdate(State state) {

    }

    @Override
    public void onVuforiaResumed() {
        if (mGlView != null) {
            mGlView.setVisibility(View.VISIBLE);
            mGlView.onResume();
        }
    }

    @Override
    public void onVuforiaStarted() {
        switch (Values.carType) {
            case 1:
                mRendererQashqai.updateConfiguration();
                break;

            case 2:
                mRendererQashqaiRus.updateConfiguration();
                break;

            case 3:
                mRendererJuke.updateConfiguration();
                break;

            case 4:
                mRendererXtrail.updateConfiguration();
                break;

            case 5:
                mRendererXtrailRus.updateConfiguration();
                break;

            case 6:
                mRendererPulsar.updateConfiguration();
                break;

            case 7:
                mRendererMicra.updateConfiguration();
                break;

            case 8:
                mRendererNote.updateConfiguration();
                break;

            case 9:
                mRendererLeaf.updateConfiguration();
                break;

            case 10:
                mRendererNavara.updateConfiguration();
                break;

            case 11:
                mRendererMicraNew.updateConfiguration();
                break;

            case 12:
                mRendererQashqai2017.updateConfiguration();
                break;


            case 13:
                mRendererXtrail2017.updateConfiguration();
                break;

            case 14:
                mRendererLeaf2017.updateConfiguration();
                break;

            case 15:
                mRendererXtrailRus2017.updateConfiguration();
                break;


                default:
                    break;
        }

        if (mContAutofocus) {
            // Set camera focus mode
            if (!CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO)) {
                // If continuous autofocus mode fails, attempt to set to a different mode
                if (!CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO)) {
                    CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_NORMAL);
                }

            } else {

            }
        } else {

        }

        showProgressIndicator(false);
    }

    private void showProgressIndicator(boolean show) {
        if (loadingDialogHandler != null) {
            if (show) {
                loadingDialogHandler
                        .sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);
            } else {
                loadingDialogHandler
                        .sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
            }
        }
    }

    // Shows initialization error messages as System dialogs
    public void showInitializationErrorMessage(String message) {
        Logger.debugging(LOGTAG, "showInitializationErrorMessage(String message)");
        final String errorMessage = message;
        runOnUiThread(new Runnable() {
            public void run() {
                if (mErrorDialog != null) {
                    mErrorDialog.dismiss();
                }

                // Generates an Alert Dialog to show the error message
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        ImageTargetActivity.this);
                builder.setMessage(errorMessage)
                        .setTitle(getString(R.string.INIT_ERROR))
                        .setCancelable(false)
                        .setIcon(0)
                        .setPositiveButton(getString(R.string.button_OK),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        finish();
                                    }
                                });

                mErrorDialog = builder.create();
                mErrorDialog.show();
            }
        });
    }

    @Override
    public boolean doInitTrackers() {
        Logger.debugging(LOGTAG, "SampleApplicationControl override method:    doInitTrackers()");

        // Indicate if the trackers were initialized correctly
        boolean result = true;

        TrackerManager tManager = TrackerManager.getInstance();
        Tracker tracker;

        // Trying to initialize the image tracker
        tracker = tManager.initTracker(ObjectTracker.getClassType());
        if (tracker == null) {
            Logger.error(LOGTAG,
                    "Tracker not initialized. Tracker already initialized or the camera is already started");
            result = false;
        } else {
            Logger.error(LOGTAG, "Tracker successfully initialized");
        }
        return result;
    }

    @Override
    public boolean doStartTrackers() {
        Logger.debugging(LOGTAG, "SampleApplicationControl override method:    doStartTrackers()");

        // Indicate if the trackers were started correctly
        boolean result = true;

        Tracker objectTracker = TrackerManager.getInstance().getTracker(
                ObjectTracker.getClassType());
        if (objectTracker != null)
            objectTracker.start();

        return result;
    }

    @Override
    public boolean doStopTrackers() {
        Logger.debugging(LOGTAG, "SampleApplicationControl override method:    doStopTrackers()");

        // Indicate if the trackers were stopped correctly
        boolean result = true;

        Tracker objectTracker = TrackerManager.getInstance().getTracker(
                ObjectTracker.getClassType());
        if (objectTracker != null)
            objectTracker.stop();

        return result;
    }

    @Override
    public boolean doDeinitTrackers() {
        Logger.error(LOGTAG, "SampleApplicationControl override method:    doDeinitTrackers()");

        // Indicate if the trackers were deinitialized correctly
        boolean result = true;

        TrackerManager tManager = TrackerManager.getInstance();
        tManager.deinitTracker(ObjectTracker.getClassType());

        return result;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Logger.error(LOGTAG, "onTouchEvent(MotionEvent event)");
        return mGestureDetector.onTouchEvent(event);
    }

    boolean isExtendedTrackingActive() {
        return mExtendedTracking;
    }


    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Logger.error(LOGTAG, "onBackPressed()");
        if (isDetected) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (inflatedLayout_second != null && inflatedLayout_second.isAttachedToWindow()) {
                    if (layoutCameraView != null) {
                        layoutCameraView.removeView(inflatedLayout_second);
                        inflatedLayout_second = null;
                        layoutCameraView.addView(inflatedLayout);
                    }
                } else {
                    if (inflatedLayout != null && inflatedLayout.isAttachedToWindow()) {
                        if (layoutCameraView != null) {
                            layoutCameraView.removeAllViews();
                        }
                        vuforiaAppSession.onResume();
                        isDetected = false;
                    } else {
                        backButtonAlert();
                    }
                }
            }

        } else {
            backButtonAlert();
        }

    }

    /**
     * this method is used in xml onClick
     * here tag 1000 is used for .......
     * @param b need to get tag from button
     *          that set in xml (set tag).
     */
    public void buttonEvent(View b) {
        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < DEFAULT_CLICK_TIMEOUT) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch (Integer.parseInt(b.getTag().toString())) {
            case 1000:
                Values.ePubType = Values.COMBIMETER_TYPE;
                Intent intent = new Intent(ImageTargetActivity.this, CombimeterActivity.class);
                startActivity(intent);
                break;

            default:
                Values.ePubType = Values.BUTTON_TYPE;
                PreferenceUtil preferenceUtil = new PreferenceUtil(getApplicationContext());

                int ePubIndex = 0;
/*                if (Values.carType == 11 || Values.carType == 12 || Values.carType == 13 || Values.carType == 14) {
//                if (Values.carType == 14) {
                    ePubIndex = (Integer.parseInt(b.getTag().toString()) * 2) + 1;
                } else {
                    if(preferenceUtil.getSelectedLang().equalsIgnoreCase("pl") || preferenceUtil.getSelectedLang().equalsIgnoreCase("fi") || preferenceUtil.getSelectedLang().equalsIgnoreCase("pt")) {
                        ePubIndex = (Integer.parseInt(b.getTag().toString()) * 2) + 1;
                    } else {
                        ePubIndex = Integer.parseInt(b.getTag().toString());
                    }
                }
*/
                ePubIndex = (Integer.parseInt(b.getTag().toString()) * 2) + 1;

//                ePubIndex = (Integer.parseInt(b.getTag().toString()) * 2) + 1;

                // here specify the DetailsActivity for loading epub data
                Intent intentButton = new Intent(ImageTargetActivity.this, DetailsActivity.class);
                intentButton.putExtra("epub_index", ePubIndex);
                intentButton.putExtra("epub_title", "DETAILS");
                intentButton.putExtra("ar_name", Values.ar_value);
                startActivity(intentButton);
                break;
        }

    }

    public void backButtonAlert() {
        final Dialog dialog = new DialogController(ImageTargetActivity.this).langDialog();

        TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);
        String exitDialogueText = NissanApp.getInstance().getAlertMessage(this, new PreferenceUtil(this).getSelectedLang(), Values.CONFIRM_EXIT_MESSAGE);
        txtViewTitle.setText(exitDialogueText == null || exitDialogueText.isEmpty() ? resources.getString(R.string.exit_alert) : exitDialogueText);

        //TODO
        String okText = NissanApp.getInstance().getGlobalMessage(this).getOk();
        String cancelText = NissanApp.getInstance().getGlobalMessage(this).getCancel();

        Button btnNo = (Button) dialog.findViewById(R.id.btn_cancel);
//        btnNo.setText(resources.getString(R.string.button_NO));
        btnNo.setText(cancelText == null || cancelText.isEmpty() ? resources.getString(R.string.button_NO) : cancelText);

        Button btnYes = (Button) dialog.findViewById(R.id.btn_ok);
//        btnYes.setText(resources.getString(R.string.button_YES));
        btnYes.setText(okText == null || okText.isEmpty() ? resources.getString(R.string.button_YES) : okText);


        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        btnYes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isDetected = false;
                dialog.dismiss();

                Values.ePubType = 0;
                finish();
            }
        });

        dialog.show();
    }

    public void showInfo() {
        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < DEFAULT_CLICK_TIMEOUT) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        Values.ePubType = Values.INFO_TYPE;
        Intent intent = new Intent(ImageTargetActivity.this, DetailsActivity.class);
        intent.putExtra("epub_index", 0);
        intent.putExtra("epub_title", "INFO");
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    public void sendMsgToGoogleAnalytics(String msgName) {
        // Get a Tracker (should auto-report)
        ((MyApplication) getApplication())
                .getTracker(MyApplication.TrackerName.APP_TRACKER);

        // Get tracker.
        tracker = ((MyApplication) getApplication())
                .getTracker(MyApplication.TrackerName.APP_TRACKER);

        // Set screen name.
        tracker.setScreenName(msgName.toLowerCase());
        tracker.enableAdvertisingIdCollection(true);

        // Send a screen view.
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    public String getGoogleAnalyticeName(String values) {
        return NissanApp.getInstance().getCarName(Values.carType) + Analytics.DOT + Values.tabExplore + Analytics.AUGMENTED_REALITY + Analytics.DOT + values + Analytics.DOT + NissanApp.getInstance().getLanguageName(new PreferenceUtil(getApplicationContext()).getSelectedLang()) + Analytics.DOT + Analytics.PLATFORM + "";
    }
}
