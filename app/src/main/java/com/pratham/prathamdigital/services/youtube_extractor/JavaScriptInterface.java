package com.pratham.prathamdigital.services.youtube_extractor;

import android.webkit.JavascriptInterface;

/**
 * Passed in addJavascriptInterface of WebView to allow web views's JS execute
 * Java code
 */
public class JavaScriptInterface {
    private final CallJavaResultInterface mCallJavaResultInterface;

    public JavaScriptInterface(CallJavaResultInterface callJavaResult) {
        mCallJavaResultInterface = callJavaResult;
    }

    @JavascriptInterface
    public void returnResultToJava(String value) {
        mCallJavaResultInterface.jsCallFinished(value);
    }
}