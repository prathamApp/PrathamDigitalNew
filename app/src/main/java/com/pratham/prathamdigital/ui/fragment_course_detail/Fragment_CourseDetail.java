package com.pratham.prathamdigital.ui.fragment_course_detail;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.pratham.prathamdigital.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

import butterknife.BindView;

@EFragment(R.layout.fragment_course_detail)
public class Fragment_CourseDetail extends Fragment {
    @BindView(R.id.rv_course)
    RecyclerView rv_course;
    @BindView(R.id.btn_startCourse)
    Button btn_startCourse;
    @BindView(R.id.txt_course_title)
    TextView txt_course_title;
    @BindView(R.id.img_course)
    SimpleDraweeView img_course;

    @AfterViews
    public void initialize() {

    }
}
