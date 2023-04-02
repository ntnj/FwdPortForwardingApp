package com.elixsr.core.common.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.SwitchCompat

/**
 * Created by Niall on 27/07/2016.
 */
class ToggleSwitch : SwitchCompat {
    private var mOnBeforeListener: OnBeforeCheckedChangeListener? = null

    interface OnBeforeCheckedChangeListener {
        fun onBeforeCheckedChanged(toggleSwitch: ToggleSwitch?, checked: Boolean): Boolean
    }

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr)

    fun setOnBeforeCheckedChangeListener(listener: OnBeforeCheckedChangeListener?) {
        mOnBeforeListener = listener
    }

    override fun setChecked(checked: Boolean) {
        if (mOnBeforeListener != null
                && mOnBeforeListener!!.onBeforeCheckedChanged(this, checked)) {
            return
        }
        super.setChecked(checked)
    }

    fun setCheckedInternal(checked: Boolean) {
        super.setChecked(checked)
    }
}