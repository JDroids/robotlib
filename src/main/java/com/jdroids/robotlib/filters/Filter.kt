package com.jdroids.robotlib.filters

import com.jdroids.robotlib.pid.PIDSource

/**
 * Superclass for filters
 */
abstract class Filter(private val source: PIDSource) : PIDSource{
    abstract override fun pidGet(): Double

    /**
     * Returns the current filter estimate without also inserting new data as pidGet() would do.
     *
     * @return The current filter estimate
     */
    abstract fun get(): Double

    /**
     * Reset the filter state.
     */
    abstract fun reset()

    /**
     * Calls PIDGet() of source.
     *
     * @return Current value of source
     */
    protected fun pidGetSource(): Double {
        return source.pidGet()
    }

    override fun setPIDSourceType(type: PIDSource.Type) {
        source.setPIDSourceType(type)
    }

    override fun getPIDSourceType(): PIDSource.Type = source.getPIDSourceType()
}