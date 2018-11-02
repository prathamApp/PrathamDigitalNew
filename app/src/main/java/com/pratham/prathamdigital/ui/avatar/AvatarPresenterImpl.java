package com.pratham.prathamdigital.ui.avatar;

import android.content.Context;
import android.os.AsyncTask;

import com.pratham.prathamdigital.dbclasses.StudentDao;
import com.pratham.prathamdigital.models.Modal_Student;

import java.util.ArrayList;

public class AvatarPresenterImpl implements AvatarContract.avatarPresenter {
    Context context;
    AvatarContract.avatarView avatarView;
    Modal_Student modal_student;
    StudentDao studentDao;

    public AvatarPresenterImpl(Context context, AvatarContract.avatarView avatarView) {
        this.context = context;
        this.avatarView = avatarView;
    }

    public void addStudent(Modal_Student modal_student, StudentDao studentDao) {
        this.modal_student = modal_student;
        this.studentDao = studentDao;
        new StudentAsync().execute();
    }

    private class StudentAsync extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            studentDao.insertStudent(modal_student);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            avatarView.openDashboard();
        }
    }
}
