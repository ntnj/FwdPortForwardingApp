package com.elixsr.core.common.widgets

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.TextAppearanceSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.elixsr.portforwarder.R

class SwitchBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    fun interface OnSwitchChangeListener {
        /**
         * Called when the checked state of the Switch has changed.
         *
         * @param switchView The Switch view whose state has changed.
         * @param isChecked  The new checked state of switchView.
         */
        fun onSwitchChanged(switchView: SwitchCompat?, isChecked: Boolean)
    }

    private val mSummarySpan: TextAppearanceSpan
    val switch: ToggleSwitch
    private val mTextView: TextView
    private var mLabel: String
    private var mSummary: String? = null
    private val mSwitchChangeListeners = ArrayList<OnSwitchChangeListener>()

    init {
        LayoutInflater.from(context).inflate(R.layout.switch_bar, this)
        val a = context.obtainStyledAttributes(attrs, MARGIN_ATTRIBUTES)
        //        int switchBarMarginStart = (int) a.getDimension(0, 0);
//        int switchBarMarginEnd = (int) a.getDimension(1, 0);
        a.recycle()
        mTextView = findViewById(R.id.switch_text)
        mLabel = resources.getString(R.string.switch_off_text)
        mSummarySpan = TextAppearanceSpan(context, R.style.TextAppearance_Switch)
        updateText()
        var lp = mTextView.layoutParams as MarginLayoutParams
        //        lp.setMarginStart(switchBarMarginStart);
        switch = findViewById(R.id.switch_widget)
        // Prevent onSaveInstanceState() to be called as we are managing the state of the Switch
        // on our own
        switch.isSaveEnabled = false
        lp = switch.layoutParams as MarginLayoutParams
        //        lp.setMarginEnd(switchBarMarginEnd);
        addOnSwitchChangeListener { _: SwitchCompat?, isChecked: Boolean -> setTextViewLabel(isChecked) }
        setOnClickListener(this)

        // Default is hide
        visibility = GONE
    }

    fun setTextViewLabel(isChecked: Boolean) {
        mLabel = resources
                .getString(if (isChecked) R.string.switch_on_text else R.string.switch_off_text)
        updateText()
    }

    fun setSummary(summary: String?) {
        mSummary = summary
        updateText()
    }

    private fun updateText() {
        if (TextUtils.isEmpty(mSummary)) {
            mTextView.text = mLabel
            return
        }
        val ssb = SpannableStringBuilder(mLabel).append('\n')
        val start = ssb.length
        ssb.append(mSummary)
        ssb.setSpan(mSummarySpan, start, ssb.length, 0)
        mTextView.text = ssb
    }

    fun setCheckedInternal(checked: Boolean) {
        setTextViewLabel(checked)
        switch.setCheckedInternal(checked)
    }

    var isChecked: Boolean
        get() = switch.isChecked
        set(checked) {
            setTextViewLabel(checked)
            switch.isChecked = checked
        }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        mTextView.isEnabled = enabled
        switch.isEnabled = enabled
    }

    fun show() {
        if (!isShowing) {
            visibility = VISIBLE
            switch.setOnCheckedChangeListener(this)
        }
    }

    fun hide() {
        if (isShowing) {
            visibility = GONE
            switch.setOnCheckedChangeListener(null)
        }
    }

    val isShowing: Boolean
        get() = visibility == VISIBLE

    override fun onClick(v: View) {
        val isChecked = !switch.isChecked
        this.isChecked = isChecked
    }

    fun propagateChecked(isChecked: Boolean) {
        val count = mSwitchChangeListeners.size
        for (n in 0 until count) {
            mSwitchChangeListeners[n].onSwitchChanged(switch, isChecked)
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        propagateChecked(isChecked)
    }

    fun addOnSwitchChangeListener(listener: OnSwitchChangeListener) {
        check(!mSwitchChangeListeners.contains(listener)) { "Cannot add twice the same OnSwitchChangeListener" }
        mSwitchChangeListeners.add(listener)
    }

    fun removeOnSwitchChangeListener(listener: OnSwitchChangeListener) {
        check(mSwitchChangeListeners.contains(listener)) { "Cannot remove OnSwitchChangeListener" }
        mSwitchChangeListeners.remove(listener)
    }

    internal class SavedState : BaseSavedState {
        var checked = false
        var visible = false

        constructor(superState: Parcelable?) : super(superState)

        /**
         * Constructor called from [.CREATOR]
         */
        private constructor(`in`: Parcel) : super(`in`) {
            checked = (`in`.readValue(null) as Boolean?)!!
            visible = (`in`.readValue(null) as Boolean?)!!
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeValue(checked)
            out.writeValue(visible)
        }

        override fun toString(): String {
            return ("SwitchBar.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " checked=" + checked
                    + " visible=" + visible + "}")
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState?> = object : Parcelable.Creator<SavedState?> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    public override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.checked = switch.isChecked
        ss.visible = isShowing
        return ss
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState
        super.onRestoreInstanceState(ss.superState)
        switch.setCheckedInternal(ss.checked)
        setTextViewLabel(ss.checked)
        visibility = if (ss.visible) VISIBLE else GONE
        switch.setOnCheckedChangeListener(if (ss.visible) this else null)
        requestLayout()
    }

    companion object {
        private val MARGIN_ATTRIBUTES = intArrayOf(
                R.attr.switchBarMarginStart, R.attr.switchBarMarginEnd)
    }
}