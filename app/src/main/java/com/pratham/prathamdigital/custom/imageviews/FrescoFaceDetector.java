package com.pratham.prathamdigital.custom.imageviews;

import android.content.Context;

import com.google.android.gms.vision.face.FaceDetector;

public class FrescoFaceDetector {

    private static volatile FaceDetector faceDetector;
    private static Context mContext;

    public static Context getContext() {
        if (mContext == null) {
            throw new RuntimeException("Initialize FrescoFaceDetector by calling FrescoFaceDetector.initialize(context).");
        }
        return mContext;
    }

    public static void initialize(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null.");
        }
        mContext = context.getApplicationContext(); // To make it independent of activity lifecycle
    }

    private static void initDetector() {
        if (faceDetector == null) {
            synchronized ((FrescoFaceDetector.class)) {
                if (faceDetector == null) {
                    faceDetector = new
                            FaceDetector.Builder(getContext())
                            .setTrackingEnabled(false)
                            .build();
                }
            }
        }
    }

    public static FaceDetector getFaceDetector() {
        if (mContext == null) {
            throw new RuntimeException("Initialize FrescoFaceDetector by calling FrescoFaceDetector.initialize(context).");
        }
        initDetector();
        return faceDetector;
    }

    public static void releaseDetector() {
        if (faceDetector != null) {
            faceDetector.release();
            faceDetector = null;
        }
        mContext = null;
    }
}
