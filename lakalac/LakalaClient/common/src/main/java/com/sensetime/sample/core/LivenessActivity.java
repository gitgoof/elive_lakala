package com.sensetime.sample.core;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensetime.library.finance.common.camera.CameraError;
import com.sensetime.library.finance.common.camera.CameraPreviewView;
import com.sensetime.library.finance.common.camera.CameraUtil;
import com.sensetime.library.finance.common.camera.OnCameraListener;
import com.sensetime.library.finance.common.type.PixelFormat;
import com.sensetime.library.finance.common.type.Size;
import com.sensetime.library.finance.common.util.FileUtil;
import com.sensetime.library.finance.liveness.DetectInfo;
import com.sensetime.library.finance.liveness.LivenessCode;
import com.sensetime.library.finance.liveness.MotionLivenessApi;
import com.sensetime.library.finance.liveness.NativeComplexity;
import com.sensetime.library.finance.liveness.NativeMotion;
import com.sensetime.library.finance.liveness.type.BoundInfo;
import com.sensetime.sample.common.R;
import com.sensetime.sample.core.util.MediaController;
import com.sensetime.sample.core.view.CircleTimeView;
import com.sensetime.sample.core.view.FixedSpeedScroller;
import com.sensetime.sample.core.view.MotionPagerAdapter;
import com.sensetime.sample.core.view.TimeViewContoller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LivenessActivity extends Activity {

    public static final String MESSAGE_ERROR_TIMEOUT = "MESSAGE_ERROR_TIMEOUT";
    public static final String MESSAGE_ERROR_NO_PERMISSIONS = "MESSAGE_ERROR_NO_PERMISSIONS";
    public static final String MESSAGE_CANCELED = "MESSAGE_CANCELED";
    public static final String MESSAGE_ERROR_CAMERA = "MESSAGE_ERROR_CAMERA";
    public static final String MESSAGE_ACTION_OVER = "MESSAGE_ACTION_OVER";

    public static final String EXTRA_MESSAGE = "extra_message";
    public static final String EXTRA_DIFFICULTY = "extra_difficulty";
    public static final String EXTRA_VOICE = "extra_voice";
    public static final String EXTRA_SEQUENCES = "extra_sequences";

    private static final String FILES_PATH = Environment.getExternalStorageDirectory().getPath() + "/sensetime/";
    private static final String MODEL_FILE_NAME = "M_Finance_Composite_General_Liveness_1.0.model";
    private static final String LICENSE_FILE_NAME = "SenseID_Liveness.lic";

    private static final int DELAY_ALIGN_DURATION = 1000;

    private static final int[] STEP_PIC_NORMAL = {R.drawable.common_step_1_normal, R.drawable.common_step_2_normal, R.drawable.common_step_3_normal,
                                                  R.drawable.common_step_4_normal, R.drawable.common_step_5_normal, R.drawable.common_step_6_normal,
                                                  R.drawable.common_step_7_normal, R.drawable.common_step_8_normal, R.drawable.common_step_9_normal,
                                                  R.drawable.common_step_10_normal};
    private static final int[] STEP_PIC_SELECTED = {R.drawable.common_step_1_selected, R.drawable.common_step_2_selected, R.drawable.common_step_3_selected,
                                                    R.drawable.common_step_4_selected, R.drawable.common_step_5_selected, R.drawable.common_step_6_selected,
                                                    R.drawable.common_step_7_selected, R.drawable.common_step_8_selected, R.drawable.common_step_9_selected,
                                                    R.drawable.common_step_10_selected};

    private boolean mIsStopped = true;
    private boolean mIsVoiceOn = true;
    private boolean mMotionChanged = false;
    private boolean mIsImageDataChanged = false;
    private int mDifficulty = NativeComplexity.WRAPPER_COMPLEXITY_NORMAL;
    private int[] mSequences = new int[]{NativeMotion.CV_LIVENESS_BLINK, NativeMotion.CV_LIVENESS_MOUTH, NativeMotion.CV_LIVENESS_HEADNOD, NativeMotion.CV_LIVENESS_HEADYAW};
    private int mCurrentMotionIndex = -1;
    private long mAlignedStartTime = -1L;

    private final byte[] mImageData = new byte[CameraUtil.DEFAULT_PREVIEW_WIDTH * CameraUtil.DEFAULT_PREVIEW_HEIGHT * 3 / 2];
    private byte[] mDetectImageData = null;

    private ExecutorService mLivenessExecutor = null;

    private TextView mNoteTextView = null;
    private View mDetectLayout = null;
    private ViewPager mAnimationView = null;
    private ViewGroup mStepsView = null;

    private CameraPreviewView mCameraPreviewView = null;
    private LivenessState mState = new AlignmentState();
    private TimeViewContoller mTimerViewContoller = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        if (!checkPermission()) {
            exit(MESSAGE_ERROR_NO_PERMISSIONS);
        }

		setContentView(R.layout.common_activity_liveness);

        // init setting values.
        mIsVoiceOn = getIntent().getBooleanExtra(EXTRA_VOICE, true);
        mDifficulty = getIntent().getIntExtra(EXTRA_DIFFICULTY, NativeComplexity.WRAPPER_COMPLEXITY_NORMAL);
        int[] sequences = getIntent().getIntArrayExtra(EXTRA_SEQUENCES);
        if (sequences != null && sequences.length > 0) {
            mSequences = sequences;
        }

		findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				exit(MESSAGE_CANCELED);
			}
		});

        View voiceView = findViewById(R.id.btn_voice);
        voiceView.setBackgroundResource(mIsVoiceOn ? R.drawable.common_ic_voice : R.drawable.common_ic_mute);
        voiceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsVoiceOn = !mIsVoiceOn;
                v.setBackgroundResource(mIsVoiceOn ? R.drawable.common_ic_voice : R.drawable.common_ic_mute);
                if (mIsVoiceOn) {
                    MediaController.getInstance().playNotice(LivenessActivity.this, mSequences[mCurrentMotionIndex]);
                } else {
                    MediaController.getInstance().release();
                }
            }
        });

        mNoteTextView = (TextView)findViewById(R.id.txt_note);

        mCameraPreviewView = (CameraPreviewView)findViewById(R.id.camera_preview);
        CameraUtil.INSTANCE.setPreviewView(mCameraPreviewView);
        CameraUtil.INSTANCE.setOnCameraListener(new OnCameraListener() {
            @Override
            public void onCameraDataFetched(byte[] data) {
                synchronized(mImageData) {
                    if (data == null || data.length < 1) {
                        return;
                    }
                    Arrays.fill(mImageData, (byte)0);
                    System.arraycopy(data, 0, mImageData, 0, data.length);
                    mIsImageDataChanged = true;
                }
            }

            @Override
            public void onError(CameraError error) {
                exit(LivenessActivity.MESSAGE_ERROR_CAMERA);
            }
        });

        mDetectLayout = findViewById(R.id.layout_detect);
        initDetectLayout();
	}

    @Override
	protected void onResume() {
		super.onResume();

        File dir = new File(FILES_PATH);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        FileUtil.copyAssetsToFile(LivenessActivity.this, MODEL_FILE_NAME, FILES_PATH + MODEL_FILE_NAME);
        FileUtil.copyAssetsToFile(LivenessActivity.this, LICENSE_FILE_NAME, FILES_PATH + LICENSE_FILE_NAME);
        LivenessCode result = MotionLivenessApi.init(LivenessActivity.this, FILES_PATH + LICENSE_FILE_NAME, FILES_PATH + MODEL_FILE_NAME);
        if (result != LivenessCode.OK) {
            exit(result.name());
            return;
        }

        startDetectThread();
	}

    @Override
    protected void onPause() {
        MediaController.getInstance().release();

        MotionLivenessApi.getInstance().release();

        destroyExecutor();

        exit(MESSAGE_CANCELED);

        super.onPause();
    }

    private void exit(String message) {
        mIsStopped = true;

        mTimerViewContoller.stop();

        MotionLivenessApi.getInstance().stopDetect(true, false);
        byte[] result = MotionLivenessApi.getInstance().getLastDetectProtoBufData();
        if(result != null && result.length > 0) {
            saveLivenessResult(result);
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_MESSAGE, message);
        setResult(RESULT_FIRST_USER, intent);
        finish();
    }

    private void updateMessage(final DetectInfo.FaceState faceState, final DetectInfo.FaceDistance faceDistance) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (faceDistance == DetectInfo.FaceDistance.CLOSE) {
                    mNoteTextView.setText(R.string.common_face_too_close);
                } else if (faceDistance == DetectInfo.FaceDistance.FAR) {
                    mNoteTextView.setText(R.string.common_face_too_far);
                } else if (faceState == DetectInfo.FaceState.NORMAL) {
                    mNoteTextView.setText(R.string.common_detecting);
                } else {
                    mNoteTextView.setText(R.string.common_tracking_missed);
                }
            }
        });
    }

    @SuppressLint("NewApi") // Already check in first case(Build.VERSION.SDK_INT < 23);
    private boolean checkPermission() {
        return Build.VERSION.SDK_INT < 23 ||
               (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
               checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void initDetectLayout() {
        // init setp view.
        mStepsView = (ViewGroup)mDetectLayout.findViewById(R.id.layout_steps);
        for (int i = 0; i < mSequences.length; i++) {
            ImageView imageView = (ImageView)getLayoutInflater().inflate(R.layout.common_item_motion_step, mStepsView, false);
            imageView.setImageResource(STEP_PIC_NORMAL[i]);
            mStepsView.addView(imageView);
        }

        // init anim view.
        mAnimationView = (ViewPager)findViewById(R.id.pager_action);
        mAnimationView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });// return true prevent touch event
        mAnimationView.setAdapter(new MotionPagerAdapter(mSequences));
        try {
            // FixedSpeedScroller control change time
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller fScroller = new FixedSpeedScroller(mAnimationView.getContext());
            mScroller.set(mAnimationView, fScroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        // init timer view.
        CircleTimeView timerView = (CircleTimeView) findViewById(R.id.time_view);
        mTimerViewContoller = new TimeViewContoller(timerView);
        mTimerViewContoller.setCallBack(new TimeViewContoller.CallBack() {
            @Override
            public void onTimeEnd() {
                exit(MESSAGE_ERROR_TIMEOUT);
            }
        });
    }

    private void switchMotion(final int index) {
        mCurrentMotionIndex = index;
        mMotionChanged = true;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAnimationView.setCurrentItem(index, true);

                if (index > 0) {
                    ((ImageView)mStepsView.getChildAt(index - 1)).setImageResource(STEP_PIC_NORMAL[index - 1]);
                }
                ((ImageView)mStepsView.getChildAt(index)).setImageResource(STEP_PIC_SELECTED[index]);

                mTimerViewContoller.start(true);
            }
        });

        if (mIsVoiceOn) {
            MediaController.getInstance().playNotice(LivenessActivity.this, mSequences[mCurrentMotionIndex]);
        }
    }

    private void startDetectThread() {
        mLivenessExecutor = Executors.newSingleThreadExecutor();
        mLivenessExecutor.execute(new Runnable() {
            @Override
            public void run() {
                LivenessCode code = MotionLivenessApi.getInstance().prepare(mDifficulty);
                if(code != LivenessCode.OK) {
                    // Try restart liveness if prepare fail.
                    MotionLivenessApi.getInstance().stopDetect(false, false);

                    code = MotionLivenessApi.getInstance().prepare(mDifficulty);
                    if(code != LivenessCode.OK) {
                        exit(code.name());
                        return;
                    }
                }

                while(true) {
                    if(mIsStopped) {
                        break;
                    }
                    if(!mIsImageDataChanged) {
                        try {
                            Thread.sleep(10);
                        } catch(InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    mState.beforeDetect();
                    synchronized(mImageData) {
                        if (mDetectImageData == null) {
                            mDetectImageData = new byte[mImageData.length];
                        }
                        System.arraycopy(mImageData, 0, mDetectImageData, 0, mImageData.length);
                    }
                    final DetectInfo info = MotionLivenessApi.getInstance()
                                                       .detect(mDetectImageData,
                                                               PixelFormat.NV21,
                                                               new Size(CameraUtil.DEFAULT_PREVIEW_WIDTH, CameraUtil.DEFAULT_PREVIEW_HEIGHT),
                                                               new Size(mCameraPreviewView.getWidth(), mCameraPreviewView.getHeight()),
                                                               CameraUtil.INSTANCE.getCameraOrientation(),
                                                               new BoundInfo(((View)mCameraPreviewView.getParent()).getWidth() /
                                                                             2,
                                                                             ((View)mCameraPreviewView.getParent()).getHeight() /
                                                                             2,
                                                                             (((View)mCameraPreviewView.getParent()).getWidth() /
                                                                              3)));
                    mIsImageDataChanged = false;

                    if(mIsStopped) {
                        break;
                    }
                    mState.checkResult(info);
                }
            }
        });
        mIsStopped = false;
    }

    private void saveLivenessResult(byte[] livenessResult) {
        if (livenessResult == null || livenessResult.length < 1) {
            return;
        }
        String resultPath = FILES_PATH + "livenessResult";
        try {
            File file = new File(resultPath);
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            } else {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(resultPath);
            fos.write(livenessResult);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void destroyExecutor() {
        if (mLivenessExecutor == null) {
            return;
        }
        mLivenessExecutor.shutdown();
        try {
            mLivenessExecutor.awaitTermination(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mLivenessExecutor = null;
    }

    private void switchToDetectState() {
        mState = new DetectState();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mNoteTextView.setVisibility(View.GONE);
                mDetectLayout.setVisibility(View.VISIBLE);

                mAnimationView.setCurrentItem(0, false);
                if (mCurrentMotionIndex > -1) {
                    ((ImageView)mStepsView.getChildAt(mCurrentMotionIndex)).setImageResource(STEP_PIC_NORMAL[mCurrentMotionIndex]);
                }

                // restart detect when align passed.
                mIsStopped = true;
                destroyExecutor();
                startDetectThread();

                switchMotion(0);
            }
        });
    }

    private interface LivenessState {
        void checkResult(DetectInfo info);
        void beforeDetect();
    }

    private class AlignmentState implements LivenessState {

        private boolean mIsMotionSet = false;

        @Override
        public void checkResult(DetectInfo info) {
            if(info.getFaceState() == DetectInfo.FaceState.NORMAL && info.getFaceDistance() == DetectInfo.FaceDistance.NORMAL) {
                if (mAlignedStartTime < 0) {
                    mAlignedStartTime = SystemClock.uptimeMillis();
                } else {
                    if (SystemClock.uptimeMillis() - mAlignedStartTime > DELAY_ALIGN_DURATION) {
                        mAlignedStartTime = -1;
                        switchToDetectState();
                        return;
                    }
                }
            } else {
                mAlignedStartTime = -1L;
            }
            updateMessage(info.getFaceState(), info.getFaceDistance());
        }

        @Override
        public void beforeDetect() {
            if (!mIsMotionSet) {
                mIsMotionSet = MotionLivenessApi.getInstance().setMotion(NativeMotion.CV_LIVENESS_BLINK);
            }
        }
    }

    private class DetectState implements LivenessState {

        @Override
        public void checkResult(DetectInfo info) {
            if (info.getFaceState() == null || info.getFaceState() == DetectInfo.FaceState.UNKNOWN || info.getFaceState() == DetectInfo.FaceState.MISSED) {
                exit(MESSAGE_ACTION_OVER);
                return;
            }
            if(!info.isPass()) {
                return;
            }
            if (mCurrentMotionIndex == mSequences.length - 1) {
                MotionLivenessApi.getInstance().stopDetect(true, true);
                byte[] result = MotionLivenessApi.getInstance().getLastDetectProtoBufData();
                if(result != null && result.length > 0) {
                    saveLivenessResult(result);
                }

                mIsStopped = true;
                mTimerViewContoller.stop();

                setResult(RESULT_OK, getIntent());
                finish();
            } else {
                switchMotion(mCurrentMotionIndex + 1);
            }
        }

        @Override
        public void beforeDetect() {
            if (mMotionChanged && mCurrentMotionIndex > -1) {
                if (MotionLivenessApi.getInstance().setMotion(mSequences[mCurrentMotionIndex])) {
                    mMotionChanged = false;
                }
            }
        }
    }
}