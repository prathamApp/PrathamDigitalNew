package com.pratham.prathamdigital.services.youtube_extractor;

public interface JsCallback {
    public abstract void onResult(String value);

    public abstract void onError(String errorMessage);
}