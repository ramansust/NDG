package com.nissan.alldriverguide;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.nissan.alldriverguide.augmentedreality.ARQashqai2017Rus;
import com.nissan.alldriverguide.augmentedreality.ARQashqaiRUS;
import com.nissan.alldriverguide.augmentedreality.ARXtrail;
import com.nissan.alldriverguide.augmentedreality.ARXtrail2017;
import com.nissan.alldriverguide.augmentedreality.ARXtrail2017Rus;
import com.nissan.alldriverguide.augmentedreality.ARXtrailRUS;
import com.nissan.alldriverguide.augmentedreality.cars.ARLeaf2019;
import com.nissan.alldriverguide.augmentedreality.cars.ARNewNissanJuke2019;
import com.nissan.alldriverguide.augmentedreality.cars.ARQashqai2017;
import com.nissan.alldriverguide.augmentedreality.cars.ARQashqai2021;
import com.nissan.alldriverguide.augmentedreality.cars.ARXtrail2020Eur;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.nissan.alldriverguide.utils.Values.DEFAULT_CLICK_TIMEOUT;

public class ImageTargetActivity extends AppCompatActivity implements SampleApplicationControl {
    private static final String LOG_TAG = "ImageTargets";
    public static boolean isFromShowInfo = false;
    public static boolean isDetected = false;
    //parentview
    @SuppressLint("StaticFieldLeak")
    public static View inflatedLayout = null;
    //childview
    @SuppressLint("StaticFieldLeak")
    public static View inflatedLayout_second;
    @SuppressLint("StaticFieldLeak")
    public static View inflatedLayout_third;
    public final LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(this);

    // Our renderer:
    //private ImageTargetRendererFerrari mRenderer;
    public RelativeLayout layoutCameraView;
    public ViewGroup layoutBackRefreshView;
    public ImageView iv;
    public boolean mIsActive = false;
    SampleApplicationSession vuforiaAppSession;
    private DataSet mCurrentDataset;
    private final ArrayList<String> mDatasetStrings = new ArrayList<>();
    // Our OpenGL view:
    private SampleApplicationGLView mGlView;
    private GestureDetector mGestureDetector;
    // The textures we will use for rendering:
    private Vector<Texture> mTextures;
    private boolean mContAutofocus = false;
    private RelativeLayout mUILayout;
    // Alert Dialog used to display SDK errors
    private AlertDialog mErrorDialog;
    private boolean mIsDroidDevice = false;

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
    private ARQashqai2017Rus mRendererQashqaiRus2017;
    private ARLeaf2019 mRendererLeaf2019;
    private ARNewNissanJuke2019 mRendererJuke2019;
    private ARXtrail2020Eur mRendererXtrail2020Eur;
    private ARQashqai2021 mRendererQashqai2021Eur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new PreferenceUtil(getApplicationContext()).setOpenCountForRateApp();

        vuforiaAppSession = new SampleApplicationSession(this);
        resources = new Resources(getAssets(), new DisplayMetrics(), NissanApp.getInstance().changeLocalLanguage(ImageTargetActivity.this, new PreferenceUtil(getApplicationContext()).getSelectedLang()));

        startLoadingAnimation();

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

            case 16:
                mDatasetStrings.add(NissanApp.getInstance().getCarPath(Values.carType) + Values.ASSETS + "qashqai2017rus.xml");
                break;

            case 17:
                mDatasetStrings.add(NissanApp.getInstance().getCarPath(Values.carType) + Values.ASSETS + "leaf2019.xml");
                break;

            case 18:
                mDatasetStrings.add(NissanApp.getInstance().getCarPath(Values.carType) + Values.ASSETS + "jukef16.xml");
                break;

            case 19:
                mDatasetStrings.add(NissanApp.getInstance().getCarPath(Values.carType) + Values.ASSETS + "XtrailEur2020.xml");
                break;

            case 20:
                mDatasetStrings.add(NissanApp.getInstance().getCarPath(Values.carType) + Values.ASSETS + "j12qashqai.xml");
                break;

            default:
                break;
        }

        // set screen orientation for AR
        vuforiaAppSession.initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mGestureDetector = new GestureDetector(this, new GestureListener());

        // Load any sample specific textures:
        mTextures = new Vector<>();
        loadTextures();

        mIsDroidDevice = Build.MODEL.toLowerCase().startsWith("droid");
    }

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

    // We want to load specific textures from the APK, which we will later use
    // for rendering.

    // Called when the activity will start interacting with the user.
    @Override
    protected void onResume() {
        Logger.debugging(LOG_TAG, "onResume");
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
//            layoutCameraView.removeAllViews();
            vuforiaAppSession.onResume();
        }
    }

    // Callback for configuration changes the activity handles itself
    @Override
    public void onConfigurationChanged(@NonNull Configuration config) {
        Logger.debugging(LOG_TAG, "onConfigurationChanged");
        super.onConfigurationChanged(config);

        vuforiaAppSession.onConfigurationChanged();
    }

    // Called when the system is about to start resuming a previous activity.
    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        Logger.debugging(LOG_TAG, "onPause");
        super.onPause();

        if (mGlView != null) {
//            mGlView.setVisibility(View.INVISIBLE); // This line for AR Black Screen
            mGlView.onPause();
        }

        // Turn off the flash
        // Called when the activity first starts or the user navigates back to an activity.

        isFromShowInfo = true;
    }

    // The final call you receive before your activity is destroyed.
    @Override
    protected void onDestroy() {
        Logger.debugging(LOG_TAG, "onDestroy");
        super.onDestroy();

        try {
            vuforiaAppSession.stopAR();
        } catch (SampleApplicationException e) {
            Logger.error(LOG_TAG, e.getString());
        }

        // Unload texture:
        mTextures.clear();
        mTextures = null;

        System.gc();
    }

    // Initializes AR application components.
    private void initApplicationAR() {
        Logger.debugging(LOG_TAG, "initApplicationAR()");

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
                /*mRendererQashqai2017 = new ARQashqai2017(this, vuforiaAppSession);
                mGlView.setRenderer(mRendererQashqai2017);*/
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

            case 16:
                mRendererQashqaiRus2017 = new ARQashqai2017Rus(this, vuforiaAppSession);
                mGlView.setRenderer(mRendererQashqaiRus2017);
                break;

            case 17:
                Logger.error("carPath", "_____" + Values.car_path);
//                mRendererLeaf2019 = new ARLeaf2019(this, vuforiaAppSession);
//                mGlView.setRenderer(mRendererLeaf2019);
                mRendererLeaf2019 = new ARLeaf2019(this, vuforiaAppSession);
                mGlView.setRenderer(mRendererLeaf2019);
                break;

            case 18:
                mRendererJuke2019 = new ARNewNissanJuke2019(this, vuforiaAppSession);
                mGlView.setRenderer(mRendererJuke2019);
                break;

            case 19:
                mRendererXtrail2020Eur = new ARXtrail2020Eur(this, vuforiaAppSession);
                mGlView.setRenderer(mRendererXtrail2020Eur);
                break;

            case 20:
                mRendererQashqai2021Eur = new ARQashqai2021(this, vuforiaAppSession);
                mGlView.setRenderer(mRendererQashqai2021Eur);
                break;

            default:
                break;
        }

    }

    private void startLoadingAnimation() {
        Logger.error(LOG_TAG, "startLoadingAnimation()");

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
        Logger.debugging(LOG_TAG, "SampleApplicationControl override method:    doLoadTrackersData()");
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
        int mCurrentDatasetSelectionIndex = 0;
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
            Logger.debugging(LOG_TAG, "UserData:Set the following user data "
                    + trackable.getUserData());
        }

        return true;
    }

    @Override
    public boolean doUnloadTrackersData() {
        Logger.debugging(LOG_TAG, "SampleApplicationControl override method:    doUnloadTrackersData()");


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
        Logger.debugging(LOG_TAG, "SampleApplicationControl override method:    onInitARDone(SampleApplicationException exception)");
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
            else {
                Logger.error(LOG_TAG, "Unable to enable continuous autofocus");
            }
            // mSampleAppMenu = new SampleAppMenu(this, this, "Image Targets",
            // mGlView, mUILayout, null);
            // setSampleAppMenuSettings();

        } else {
            Logger.error(LOG_TAG, exception.getString());
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
//                mRendererQashqai2017.updateConfiguration();
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

            case 16:
                mRendererQashqaiRus2017.updateConfiguration();
                break;

            case 17:
//                mRendererLeaf2019.updateConfiguration();
                mRendererLeaf2019.updateConfiguration();
                break;
            case 18:
                mRendererJuke2019.updateConfiguration();
                break;
            case 19:
                mRendererXtrail2020Eur.updateConfiguration();
                break;
            case 20:
                mRendererQashqai2021Eur.updateConfiguration();
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

            }
        }

        showProgressIndicator();
    }

    private void showProgressIndicator() {
        if (loadingDialogHandler != null) {
            loadingDialogHandler
                    .sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
        }
    }

    // Shows initialization error messages as System dialogs
    public void showInitializationErrorMessage(String message) {
        Logger.debugging(LOG_TAG, "showInitializationErrorMessage(String message)");
        final String errorMessage = message;
        runOnUiThread(() -> {
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
                            (dialog, id) -> finish());

            mErrorDialog = builder.create();
            mErrorDialog.show();
        });
    }

    @Override
    public boolean doInitTrackers() {
        Logger.debugging(LOG_TAG, "SampleApplicationControl override method:    doInitTrackers()");

        // Indicate if the trackers were initialized correctly
        boolean result = true;

        TrackerManager tManager = TrackerManager.getInstance();
        Tracker tracker;

        // Trying to initialize the image tracker
        tracker = tManager.initTracker(ObjectTracker.getClassType());
        if (tracker == null) {
            Logger.error(LOG_TAG,
                    "Tracker not initialized. Tracker already initialized or the camera is already started");
            result = false;
        } else {
            Logger.error(LOG_TAG, "Tracker successfully initialized");
        }
        return result;
    }

    @Override
    public boolean doStartTrackers() {
        Logger.debugging(LOG_TAG, "SampleApplicationControl override method:    doStartTrackers()");

        // Indicate if the trackers were started correctly

        Tracker objectTracker = TrackerManager.getInstance().getTracker(
                ObjectTracker.getClassType());
        if (objectTracker != null)
            objectTracker.start();

        return true;
    }

    @Override
    public boolean doStopTrackers() {
        Logger.debugging(LOG_TAG, "SampleApplicationControl override method:    doStopTrackers()");

        // Indicate if the trackers were stopped correctly

        Tracker objectTracker = TrackerManager.getInstance().getTracker(
                ObjectTracker.getClassType());
        if (objectTracker != null)
            objectTracker.stop();

        return true;
    }

    @Override
    public boolean doDeinitTrackers() {
        Logger.error(LOG_TAG, "SampleApplicationControl override method:    doDeinitTrackers()");

        // Indicate if the trackers were deinitialized correctly

        TrackerManager tManager = TrackerManager.getInstance();
        tManager.deinitTracker(ObjectTracker.getClassType());

        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Logger.error(LOG_TAG, "onTouchEvent(MotionEvent event)");
        return mGestureDetector.onTouchEvent(event);
    }

    boolean isExtendedTrackingActive() {
        return false;
    }

    @Override
    public void onBackPressed() {
        Logger.error(LOG_TAG, "onBackPressed()");
        if (isDetected) {

            if (inflatedLayout_third != null && inflatedLayout_third.isAttachedToWindow()) {
                if (layoutCameraView != null) {
                    layoutCameraView.removeView(inflatedLayout_third);
                    inflatedLayout_third = null;
                    layoutCameraView.addView(inflatedLayout_second);
                }
            } else if (inflatedLayout_second != null && inflatedLayout_second.isAttachedToWindow()) {
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

        } else {
            backButtonAlert();
        }

    }

    /**
     * this method is used in xml onClick
     * here tag 1000 is used for .......
     *
     * @param b need to get tag from button
     *          that set in xml (set tag).
     */
    public void buttonEvent(View b) {
        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < DEFAULT_CLICK_TIMEOUT * 3) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        if (Integer.parseInt(b.getTag().toString()) == 1000) {
            Values.ePubType = Values.COMBIMETER_TYPE;
            Intent intent = new Intent(ImageTargetActivity.this, CombimeterActivity.class);
            startActivity(intent);
        } else {
            Values.ePubType = Values.BUTTON_TYPE;

            int ePubIndex;
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

            Intent intentButton = new Intent(ImageTargetActivity.this, DetailsActivity.class);
            intentButton.putExtra("epub_index", ePubIndex);
            intentButton.putExtra("epub_title", "DETAILS");
            intentButton.putExtra("ar_name", Values.ar_value);
            startActivity(intentButton);
        }

    }

    public void backButtonAlert() {
        final Dialog dialog = new DialogController(ImageTargetActivity.this).langDialog();

        TextView txtViewTitle = dialog.findViewById(R.id.txt_title);
        TextView txtViewHeader = dialog.findViewById(R.id.txt_header);
        txtViewHeader.setTypeface(null, Typeface.BOLD);
        txtViewTitle.setTypeface(null, Typeface.BOLD);
        String exitDialogueText = NissanApp.getInstance().getAlertMessage(this, new PreferenceUtil(this).getSelectedLang(), Values.CONFIRM_EXIT_MESSAGE);
        txtViewTitle.setText(exitDialogueText == null || exitDialogueText.isEmpty() ? resources.getString(R.string.exit_alert) : exitDialogueText);

        //TODO
        String okText = NissanApp.getInstance().getGlobalMessage(this).getOk();
        String cancelText = NissanApp.getInstance().getGlobalMessage(this).getCancel();

        Button btnNo = dialog.findViewById(R.id.btn_cancel);
//        btnNo.setText(resources.getString(R.string.button_NO));
        btnNo.setText(cancelText == null || cancelText.isEmpty() ? resources.getString(R.string.button_NO) : cancelText);
        Button btnYes = dialog.findViewById(R.id.btn_ok);
//        btnYes.setText(resources.getString(R.string.button_YES));
        btnYes.setText(okText == null || okText.isEmpty() ? resources.getString(R.string.button_YES) : okText);
        btnNo.setOnClickListener(v -> dialog.dismiss());

        btnYes.setOnClickListener(v -> {
            isDetected = false;
            dialog.dismiss();

            Values.ePubType = 0;
            finish();
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
        com.google.android.gms.analytics.Tracker tracker = ((MyApplication) getApplication())
                .getTracker(MyApplication.TrackerName.APP_TRACKER);

        // Set screen name.
        tracker.setScreenName(msgName.toLowerCase());
        tracker.enableAdvertisingIdCollection(true);

        // Send a screen view.
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public String getGoogleAnalyticeName(String values) {
        Values.ar_value = values;
        return NissanApp.getInstance().getCarName(Values.carType) + Analytics.DOT + Values.tabExplore + Analytics.AUGMENTED_REALITY + Analytics.DOT + values + Analytics.DOT + NissanApp.getInstance().getLanguageName(new PreferenceUtil(getApplicationContext()).getSelectedLang()) + Analytics.DOT + Analytics.PLATFORM + "";
    }

    // Process Single Tap event to trigger autofocus
    private static class GestureListener extends
            GestureDetector.SimpleOnGestureListener {
        // Used to set autofocus one second after a manual focus is triggered
        private final Handler autofocusHandler = new Handler(Looper.getMainLooper());

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // Generates a Handler to trigger autofocus
            // after 1 second
            autofocusHandler.postDelayed(() -> {
                boolean result = CameraDevice.getInstance().setFocusMode(
                        CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO);

                if (!result)
                    Logger.error("SingleTapUp", "Unable to trigger focus");
            }, 1000L);

            return true;
        }
    }
}
