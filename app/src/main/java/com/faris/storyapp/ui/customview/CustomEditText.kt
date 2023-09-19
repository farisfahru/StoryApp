package com.faris.storyapp.ui.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.faris.storyapp.R
import com.faris.storyapp.utils.*

class CustomEditText : AppCompatEditText, View.OnTouchListener {
    private lateinit var eyeButtonIcon: Drawable
    private var isVisibilityOff = true

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet? = null) {
        eyeButtonIcon =
            ContextCompat.getDrawable(context, R.drawable.ic_eye_on) as Drawable
        setBackground(EDIT_TEXT_BLACK)
        var flag = 0
        context.theme.obtainStyledAttributes(attrs, R.styleable.CustomEditText, 0, 0)
            .apply {
                try {
                    flag = getInteger(R.styleable.CustomEditText_flagType, 0)
                } finally {
                    recycle()
                }
            }
        when (flag) {
            EDIT_TEXT_NAME -> nameEditText()
            EDIT_TEXT_EMAIL -> emailEditText()
            EDIT_TEXT_PASSWORD -> {
                passwordEditText()
                setDrawables(endOfTheText = eyeButtonIcon)
                setOnTouchListener(this)
            }
        }
    }

    private fun setBackground(color: String) {
        background = when (color) {
            EDIT_TEXT_BLACK -> ContextCompat.getDrawable(context, R.drawable.bg_edt_normal)
            EDIT_TEXT_GREEN -> ContextCompat.getDrawable(context, R.drawable.bg_edt_true)
            EDIT_TEXT_RED -> ContextCompat.getDrawable(context, R.drawable.bg_edt_false)
            else -> null
        }
    }

    private fun setDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null,
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    private fun nameEditText() {
        val regex2 = "^\\p{L}+(?: \\p{L}+)*\$".toRegex()

        doAfterTextChanged {
            error = when {
                text.isNullOrBlank() -> {
                    setBackground(EDIT_TEXT_RED)
                    resources.getString(R.string.empty_name)
                }
                !text.toString().matches(regex2) -> {
                    setBackground(EDIT_TEXT_RED)
                    resources.getString(R.string.invalid_name)
                }
                else -> {
                    setBackground(EDIT_TEXT_GREEN)
                    null
                }
            }
        }
    }

    private fun emailEditText() {
        doAfterTextChanged {
            error = when {
                text.isNullOrBlank() -> {
                    setBackground(EDIT_TEXT_RED)
                    resources.getString(R.string.empty_email)
                }
                !Patterns.EMAIL_ADDRESS.matcher(text!!).matches() -> {
                    setBackground(EDIT_TEXT_RED)
                    resources.getString(R.string.invalid_email)
                }
                else -> {
                    setBackground(EDIT_TEXT_GREEN)
                    null
                }
            }
        }
    }

    private fun passwordEditText() {
        doAfterTextChanged {
            when {
                text.isNullOrBlank() -> {
                    setBackground(EDIT_TEXT_RED)
                    setError(resources.getString(R.string.empty_password), null)
                }
                text!!.length < 8 -> {
                    setBackground(EDIT_TEXT_RED)
                    setError(resources.getString(R.string.length_password), null)
                }
                else -> {
                    setBackground(EDIT_TEXT_GREEN)
                    setError(null, null)
                }
            }
        }
    }

    override fun onTouch(v: View, motionEvent: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val eyeButtonStart: Float
            val eyeButtonEnd: Float
            var isEyeButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                eyeButtonEnd = (eyeButtonIcon.intrinsicWidth + paddingStart).toFloat()
                when {
                    motionEvent.x < eyeButtonEnd -> isEyeButtonClicked = true
                }
            } else {
                eyeButtonStart = (width - paddingEnd - eyeButtonIcon.intrinsicWidth).toFloat()
                when {
                    motionEvent.x > eyeButtonStart -> isEyeButtonClicked = true
                }
            }
            if (isEyeButtonClicked) {
                return when (motionEvent.action) {
                    MotionEvent.ACTION_UP -> {
                        if (isVisibilityOff) {
                            eyeButtonIcon = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_eye_off
                            ) as Drawable
                            inputType =
                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            isVisibilityOff = false
                        } else {
                            eyeButtonIcon = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_eye_on
                            ) as Drawable
                            inputType =
                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                            isVisibilityOff = true
                        }
                        setDrawables(endOfTheText = eyeButtonIcon)
                        true
                    }
                    else -> false
                }
            } else return false
        }
        return false
    }
}