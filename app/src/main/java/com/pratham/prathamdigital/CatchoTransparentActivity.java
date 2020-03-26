package com.pratham.prathamdigital;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pratham.prathamdigital.custom.ProcessPhoenix;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_Log;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import net.alhazmy13.catcho.library.Catcho;
import net.alhazmy13.catcho.library.error.CatchoError;

import static com.pratham.prathamdigital.PrathamApplication.logDao;

public class CatchoTransparentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_report);
        CatchoError error = (CatchoError) getIntent().getSerializableExtra(Catcho.ERROR);
        Modal_Log log = new Modal_Log();
        log.setCurrentDateTime(PD_Utility.getCurrentDateTime());
        log.setErrorType("ERROR");
        log.setExceptionMessage(error.toString());
        log.setExceptionStackTrace(error.getError());
        log.setMethodName("NO_METHOD");
        log.setSessionId(FastSave.getInstance().getString(PD_Constant.SESSIONID, "no_session"));
        log.setDeviceId(PD_Utility.getDeviceSerialID());
        logDao.insertLog(log);
        findViewById(R.id.btn_report).setOnClickListener(v -> ProcessPhoenix.triggerRebirth(CatchoTransparentActivity.this));
    }
}
