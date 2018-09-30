package com.jdroids.robotlib.util

import java.util.*

/**
 * A set
 */
internal class Set<T> {
    private val set = Vector<T>()

    constructor()

    fun add(element: T) {
        if (set.contains(element)) {
            return
        }
        set.addElement(element)
    }

    fun add(s: Set<T>) {
        val elements = s.getElements()
        while (elements.hasMoreElements()) {
            add(elements.nextElement())
        }
    }

    fun clear() {
        set.clear()
    }

    fun contains(element: T): Boolean = set.contains(element)

    fun getElements(): Enumeration<T> = set.elements()
}