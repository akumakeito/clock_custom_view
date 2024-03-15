package ru.akumakeito.clockcustomview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private const val START_ANGLE = -Math.PI / 2

private const val REFRESH_PERIOD = 180L
private const val CLOCK_STATE = "clockState"
private const val CLOCK_RADIUS = "clockRadius"
private const val CLOCK_FACE_BACKGROUND_COLOR = "clockFaceBackgroundColor"
private const val BORDER_COLOR = "borderColor"
private const val NUMBER_COLOR = "numberColor"
private const val DOT_COLOR = "dotColor"
private const val HOUR_HAND_COLOR = "hourHandColor"
private const val MINUTE_HAND_COLOR = "minuteHandColor"
private const val SECOND_HAND_COLOR = "secondHandColor"


class ClockView(context: Context, attrs: AttributeSet) :
    View(context, attrs) {

    private var clockRadius = 0f
    private var centerX = 0.0f
    private var centerY = 0.0f
    private var positionOnClock = PointF(0.0f, 0.0f)

    var clockFaceBackgroundColor = 0
    var borderColor = 0
    var numberColor = 0
    var dotColor = 0
    var hourHandColor = 0
    var minuteHandColor = 0
    var secondHandColor = 0


    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.ClockView, 0, 0).apply {

            try {
                clockFaceBackgroundColor = getColor(
                    R.styleable.ClockView_clockFaceBackgroundColor,
                    context.getColor(R.color.clockFaceBackgroundColor)
                )
                borderColor = getColor(
                    R.styleable.ClockView_borderColor,
                    context.getColor(R.color.borderColor)
                )
                numberColor = getColor(
                    R.styleable.ClockView_numberColor,
                    context.getColor(R.color.numberColor)
                )
                dotColor =
                    getColor(R.styleable.ClockView_dotColor, context.getColor(R.color.dotColor))
                hourHandColor = getColor(
                    R.styleable.ClockView_hourHandColor,
                    context.getColor(R.color.hourHandColor)
                )
                minuteHandColor = getColor(
                    R.styleable.ClockView_minuteHandColor,
                    context.getColor(R.color.minuteHandColor)
                )
                secondHandColor = getColor(
                    R.styleable.ClockView_secondHandColor,
                    context.getColor(R.color.secondHandColor)
                )


            } finally {
                recycle()
            }
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        clockRadius = min(w, h) / 2f
        centerX = w / 2f
        centerY = h / 2f
    }

    private fun PointF.calculateXYForDots(position: Int, radius: Float) {
        val angle = position * (Math.PI / 30)
        x = radius * cos(angle).toFloat() + centerX
        y = radius * sin(angle).toFloat() + centerY
    }

    private fun PointF.calculateXYForNumbers(hour: Int, radius: Float) {
        val angle = hour * (Math.PI / 6) + START_ANGLE
        x = radius * cos(angle).toFloat() + centerX

        val numbersBaselineToCenter = (paint.descent() + paint.ascent()) / 2
        y = radius * sin(angle).toFloat() + centerY - numbersBaselineToCenter

    }

    private fun drawClockFace(canvas: Canvas) {
        paint.apply {
            color = clockFaceBackgroundColor
            style = Paint.Style.FILL
        }
        canvas.drawCircle(centerX, centerY, clockRadius, paint)
    }

    private fun drawBorder(canvas: Canvas) {
        paint.apply {
            color = borderColor
            style = Paint.Style.STROKE
            strokeWidth = clockRadius / 10

        }
        val borderRadius = clockRadius - paint.strokeWidth / 2
        canvas.drawCircle(centerX, centerY, borderRadius, paint)
        paint.strokeWidth = 0f
    }

    private fun drawDots(canvas: Canvas) {
        paint.apply {
            color = dotColor
            style = Paint.Style.FILL
            val dotLineRadius = clockRadius * 5 / 6
            val dotRadius = clockRadius / 50
            for (i in 0 until 60) {
                positionOnClock.calculateXYForDots(i, dotLineRadius)
                canvas.drawCircle(positionOnClock.x, positionOnClock.y, dotRadius, paint)
            }
        }
    }

    private fun drawNumbers(canvas: Canvas) {
        paint.apply {
            textSize = clockRadius * 2 / 8
            color = numberColor
            strokeWidth = 0f
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            textScaleX = 0.9f
            letterSpacing = -0.15f
            typeface = Typeface.DEFAULT

        }

        val numberLineRadius = clockRadius * 11 / 16

        for (i in 1..12) {
            positionOnClock.calculateXYForNumbers(i, numberLineRadius)
            val number = i.toString()
            canvas.drawText(number, positionOnClock.x, positionOnClock.y, paint)
        }
    }

    private fun drawClockHands(canvas: Canvas) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
//        Log.d("clock","hour: $hour minute: $minute second: $second")
        val hourWithMinutes = hour * 60 + minute

        drawHourHand(canvas, hourWithMinutes.toFloat())
        drawMinuteHand(canvas, minute.toFloat())
        drawSecondHand(canvas, second.toFloat())

    }

    private fun drawHourHand(canvas: Canvas, hourWithMinutes: Float) {
        paint.apply {
            strokeWidth = clockRadius / 20
            color = hourHandColor
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND

        }
        val angle = hourWithMinutes / 60 * (Math.PI / 6) + START_ANGLE

        canvas.drawLine(
            (centerX + cos(angle) * clockRadius / 3).toFloat(),
            (centerY + sin(angle) * clockRadius / 3).toFloat(),
            centerX,
            centerY,
            paint
        )

    }

    private fun drawMinuteHand(canvas: Canvas, minute: Float) {
        paint.apply {
            strokeWidth = clockRadius / 30
            color = minuteHandColor
            style = Paint.Style.STROKE
        }
        val angle = minute * (Math.PI / 30) + START_ANGLE
        canvas.drawLine(
            (centerX + cos(angle) * clockRadius / 2).toFloat(),
            (centerY + sin(angle) * clockRadius / 2).toFloat(),
            centerX,
            centerY,
            paint
        )
    }

    private fun drawSecondHand(canvas: Canvas, second: Float) {
        paint.apply {
            strokeWidth = clockRadius / 40
            color = secondHandColor
            style = Paint.Style.STROKE
        }
        val angle = second * (Math.PI / 30) + START_ANGLE
        canvas.drawLine(
            (centerX + cos(angle) * clockRadius * 5 / 8).toFloat(),
            (centerY + sin(angle) * clockRadius * 5 / 8).toFloat(),
            centerX,
            centerY,
            paint
        )
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val defaultWidth = context.resources.getDimension(R.dimen.clock_size).toInt()
        val defaultHeight = context.resources.getDimension(R.dimen.clock_size).toInt()

        val width = resolveSize(defaultWidth, widthMeasureSpec)
        val height = resolveSize(defaultHeight, heightMeasureSpec)

        setMeasuredDimension(width, height)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawClockFace(canvas)
        drawBorder(canvas)
        drawNumbers(canvas)
        drawDots(canvas)

        drawClockHands(canvas)

        postInvalidateDelayed(REFRESH_PERIOD)
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(CLOCK_STATE, super.onSaveInstanceState())
        bundle.putFloat(CLOCK_RADIUS, clockRadius)
        bundle.putInt(CLOCK_FACE_BACKGROUND_COLOR, clockFaceBackgroundColor)
        bundle.putInt(BORDER_COLOR, borderColor)
        bundle.putInt(NUMBER_COLOR, numberColor)
        bundle.putInt(DOT_COLOR, dotColor)
        bundle.putInt(HOUR_HAND_COLOR, hourHandColor)
        bundle.putInt(MINUTE_HAND_COLOR, minuteHandColor)
        bundle.putInt(SECOND_HAND_COLOR, secondHandColor)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var clockState: Parcelable? = null
        if (state is Bundle) {
            clockRadius = state.getFloat(CLOCK_RADIUS)
            clockFaceBackgroundColor = state.getInt(CLOCK_FACE_BACKGROUND_COLOR)
            borderColor = state.getInt(BORDER_COLOR)
            numberColor = state.getInt(NUMBER_COLOR)
            dotColor = state.getInt(DOT_COLOR)
            hourHandColor = state.getInt(HOUR_HAND_COLOR)
            minuteHandColor = state.getInt(MINUTE_HAND_COLOR)
            secondHandColor = state.getInt(SECOND_HAND_COLOR)

            clockState = if (Build.VERSION.SDK_INT >= 33) {
                state.getParcelable(CLOCK_STATE, Parcelable::class.java)
            } else {
                @Suppress("DEPRECATION") state.getParcelable(CLOCK_STATE)
            }
        }

        super.onRestoreInstanceState(clockState)
    }

}


