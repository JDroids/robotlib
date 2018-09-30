package com.jdroids.robotlib.command

/**
 * An element that is in a LinkedList.
 */
class LinkedListElement {
    private var next: LinkedListElement? = null
    private var previous: LinkedListElement? = null
    private var data: Command? = null

    fun setData(newData: Command) {
        data = newData
    }

    fun getData(): Command? {
        return data
    }

    fun getPrevious(): LinkedListElement? {
        return previous
    }

    fun getNext(): LinkedListElement? {
        return next
    }

    fun add(listElement: LinkedListElement) {
        if (next == null) {
            next = listElement
            next!!.previous = this
        }
        else {
            next!!.previous = listElement
            listElement.next = next
            listElement.previous = this
            next = listElement
        }
    }

    fun remove(): LinkedListElement? {
        if (previous == null && next == null) {
            //no-op
        }
        else if (next == null) {
            previous!!.next = null
        }
        else if (previous == null) {
            next!!.previous = null
        }
        else {
            next!!.previous = previous
            previous!!.next = next
        }

        val returnNext = next

        next = null
        previous = null
        return returnNext
    }
}