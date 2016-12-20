/*
 * Copyright 2016 Codemate Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codemate.koffeemate.util.extensions

import android.animation.Animator
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.view.animation.DecelerateInterpolator
import android.animation.AnimatorListenerAdapter
import org.jetbrains.anko.dip

class BasicListItemAnimator() : DefaultItemAnimator() {
    override fun animateAdd(viewHolder: RecyclerView.ViewHolder): Boolean {
        runEnterAnimation(viewHolder)
        return false
    }

    private fun runEnterAnimation(viewHolder: RecyclerView.ViewHolder) {
        viewHolder.itemView.translationY = viewHolder.itemView.context.dip(500).toFloat()
        viewHolder.itemView.scaleX = 0.9f
        viewHolder.itemView.alpha = 0.5f
        viewHolder.itemView.animate()
                .setInterpolator(DecelerateInterpolator(3f))
                .setDuration(1000)
                .setStartDelay((250 + viewHolder.layoutPosition * 75).toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        this@BasicListItemAnimator.dispatchAddFinished(viewHolder)
                    }
                })
                .translationY(0f)
                .scaleX(1f)
                .alpha(1f)
                .start()
    }
}