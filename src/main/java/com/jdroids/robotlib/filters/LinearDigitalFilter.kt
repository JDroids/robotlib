package com.jdroids.robotlib.filters

import com.jdroids.robotlib.util.CircularBuffer
import com.jdroids.robotlib.pid.PIDSource
import java.util.*

/**
 * This class implements a linear, digital filter. All types of FIR and IIR filters are supported.
 * Static factory methods are provided to create commonly used types of filters.
 *
 * Filters are of the form: y[n] = (b0*x[n] + b1*x[n-1] + ... + bP*x[n-P]) - (a0*y[n-1] +
 * a2*y[n-2] + ... + aQ*y[n-Q])
 *
 * Where: y[n] is the output at time "n" x[n] is the input at time "n" y[n-1] is the output from
 * the LAST time step ("n-1") x[n-1] is the input from the LAST time step ("n-1") b0...bP are the
 * "feedforward" (FIR) gains a0...aQ are the "feedback" (IIR) gains IMPORTANT! Note the "-" sign in
 * front of the feedback term! This is a common convention in signal processing.
 *
 * What can linear filters do? Basically, they can filter, or diminish, the effects of
 * undesirable input frequencies. High frequencies, or rapid changes, can be indicative of sensor
 * noise or be otherwise undesirable. A "low pass" filter smooths out the signal, reducing the
 * impact of these high frequency components.  Likewise, a "high pass" filter gets rid of
 * slow-moving signal components, letting you detect large changes more easily.
 *
 * For more on filters, I highly recommend the following articles: http://en.wikipedia
 * .org/wiki/Linear_filter http://en.wikipedia.org/wiki/Iir_filter http://en.wikipedia
 * .org/wiki/Fir_filter
 *
 * Note 1: PIDGet() should be called by the user on a known, regular period. You can set up a
 * Notifier to do this (look at the WPILib PIDController class), or do it "inline" with code in a
 * periodic function.
 *
 * Note 2: For ALL filters, gains are necessarily a function of frequency. If you make a filter
 * that works well for you at, say, 100Hz, you will most definitely need to adjust the gains if you
 * then want to run it at 200Hz! Combining this with Note 1 - the impetus is on YOU as a developer
 * to make sure PIDGet() gets called at the desired, constant frequency!
 */

/**
 * Create a linear FIR or IIR filter.
 *
 * @param source  The PIDSource object that is used to get values
 * @param ffGains The "feed forward" or FIR gains
 * @param fbGains The "feed back" or IIR gains
 */
class LinearDigitalFilter(source: PIDSource, ffGains: DoubleArray, fbGains: DoubleArray) :
        Filter(source) {
    private val inputs: CircularBuffer
    private val outputs: CircularBuffer
    private val inputGains: DoubleArray
    private val outputGains: DoubleArray

    init {
        inputs = CircularBuffer(ffGains.size)
        outputs = CircularBuffer(fbGains.size)
        inputGains = Arrays.copyOf(ffGains, ffGains.size)
        outputGains = Arrays.copyOf(fbGains, fbGains.size)
    }

    companion object {
        /**
         * Creates a one-pole IIR low-pass filter of the form: y[n] = (1-gain)*x[n] + gain*y[n-1]
         * where gain = e^(-dt / T), T is the time constant in seconds.
         *
         * This filter is stable for time constants greater than zero.
         *
         * @param source       The PIDSource object that is used to get values
         * @param timeConstant The discrete-time time constant in seconds
         * @param period       The period in seconds between samples taken by the user
         */
        fun singlePoleIIR(source: PIDSource, timeConstant: Double, period: Double):
                LinearDigitalFilter {
            val gain = Math.exp(-period/timeConstant)
            val ffGains = doubleArrayOf(1.0 - gain)
            val fbGains = doubleArrayOf(-gain)

            return LinearDigitalFilter(source, ffGains, fbGains)
        }

        /**
         * Creates a first-order high-pass filter of the form: y[n] = gain*x[n] + (-gain)*x[n-1] +
         * gain*y[n-1] where gain = e^(-dt / T), T is the time constant in seconds.
         *
         * This filter is stable for time constants greater than zero.
         *
         * @param source       The PIDSource object that is used to get values
         * @param timeConstant The discrete-time time constant in seconds
         * @param period       The period in seconds between samples taken by the user
         */
        fun highPass(source: PIDSource, timeConstant: Double, period: Double): LinearDigitalFilter {
            val gain = Math.exp(-period / timeConstant)
            val ffGains = doubleArrayOf(gain, -gain)
            val fbGains = doubleArrayOf(-gain)
            return LinearDigitalFilter(source, ffGains, fbGains)
        }

        /**
         * Creates a K-tap FIR moving average filter of the form: y[n] = 1/k * (x[k] + x[k-1] + ...
         * + x[0]).
         *
         * This filter is always stable.
         *
         * @param source The [PIDSource] object that is used to get values
         * @param taps The numbers of samples to average over. Higher = smoother but also slower.
         * @throws IllegalArgumentException if the number of taps is less than 1
         */
        fun movingAverage(source: PIDSource, taps: Int): LinearDigitalFilter {
            if (taps <= 0) {
                throw IllegalArgumentException("Number of taps was not at least 1")
            }

            val ffGains = DoubleArray(taps) {i -> 1.0/taps}

            val fbGains = DoubleArray(0)

            return LinearDigitalFilter(source, ffGains, fbGains)
        }
    }

    override fun get(): Double {
        var retVal = 0.0

        //Calculate the new values
        for (i in 0 until inputGains.size) {
            retVal += inputs[i] * inputGains[i]
        }
        for (i in 0 until outputGains.size) {
            retVal -= outputs[i] * outputGains[i]
        }

        return retVal
    }

    override fun reset() {
        inputs.clear()
        outputs.clear()
    }

    override fun pidGet(): Double {
        var retVal = 0.0

        //Rotate the inputs
        inputs.addFirst(pidGetSource())

        //Calculate the new values
        for (i in 0 until inputGains.size) {
            retVal += inputs[i] * inputGains[i]
        }
        for (i in 0 until outputGains.size) {
            retVal -= outputs[i] * outputGains[i]
        }

        //Rotate the outputs
        outputs.addFirst(retVal)

        return retVal
    }
}