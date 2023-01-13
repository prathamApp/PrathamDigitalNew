package com.pratham.prathamdigital.custom.search_view

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.FrameLayout
import android.widget.Toast
import com.pratham.prathamdigital.R
import kotlinx.android.synthetic.main.view_search.view.*

class SearchView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_search, this, true)

        open_search_button.setOnClickListener { openSearch() }
        close_search_button.setOnClickListener { closeSearch() }
        execute_search_button.setOnClickListener { executeSearch() }
    }

    private fun executeSearch() {
        Toast.makeText(context, "Execute", Toast.LENGTH_SHORT).show()
    }

    private fun openSearch() {
        et_search_text.setText("")
        search_open_view.visibility = View.VISIBLE
        val circularReveal = ViewAnimationUtils.createCircularReveal(
                search_open_view,
                (open_search_button.right + open_search_button.left) / 2,
                (open_search_button.top + open_search_button.bottom) / 2,
                0f, width.toFloat()
        )
        circularReveal.duration = 300
        circularReveal.start()
    }

    private fun closeSearch() {
        val circularConceal = ViewAnimationUtils.createCircularReveal(
                search_open_view,
                (open_search_button.right + open_search_button.left) / 2,
                (open_search_button.top + open_search_button.bottom) / 2,
                width.toFloat(), 0f
        )

        circularConceal.duration = 300
        circularConceal.start()
        circularConceal.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator) = Unit
            override fun onAnimationCancel(animation: Animator) = Unit
            override fun onAnimationStart(animation: Animator) = Unit
            override fun onAnimationEnd(animation: Animator) {
                search_open_view.visibility = View.INVISIBLE
                et_search_text.setText("")
                circularConceal.removeAllListeners()
            }
        })
    }

}