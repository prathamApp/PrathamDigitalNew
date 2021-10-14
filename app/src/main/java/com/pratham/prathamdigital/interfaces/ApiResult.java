package com.pratham.prathamdigital.interfaces;

import com.pratham.prathamdigital.models.Modal_ContentDetail;

import java.util.ArrayList;

public interface ApiResult {
    void recievedContent(String header, String response, ArrayList<Modal_ContentDetail> contentList);

    void recievedError(String header, ArrayList<Modal_ContentDetail> contentList);

    //interface to fetch language from server
    interface languageResult{
        void recievedLang(String header, String response);

        void recievedLangError(String header);
    }
}
