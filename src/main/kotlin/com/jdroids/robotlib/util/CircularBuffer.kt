package com.jdroids.robotlib.util

class CircularBuffer {
    private var dataOfBuffer: DoubleArray

    //Index of element at the front of the buffer
    private var front: Int = 0

    //Number of elements used in buffer
    private var length: Int = 0

    constructor(size: Int) {
        dataOfBuffer = DoubleArray(size)
        clear()
    }

    /**
     * Returns number of elements in the buffer.
     *
     * @return the number of elements in the buffer
     */
    fun size(): Int = length

    /**
     * Get value at the front of the buffer.
     *
     * @return the value at the front of the buffer
     */
    fun getFirst(): Double = dataOfBuffer[front]

    /**
     * Get the value at the end of the buffer.
     *
     * @return the value at the end of the buffer
     */
    fun getLast(): Double = when (length) {
        0 -> 0.0
        else -> dataOfBuffer[(front+length-1) % dataOfBuffer.size]
    }

    /**
     * Push new value to the front of the buffer. The value at the back is overwritten if the buffer
     * is full.
     */
    fun addFirst(value: Double) {
        //If the buffer is empty, do nothing
        if (dataOfBuffer.isEmpty()) {
            return
        }

        front = moduloInc(front)

        dataOfBuffer[front] = value

        if (length < dataOfBuffer.size) {
            ++length
        }
    }

    /**
     * Pop value at the front of the buffer.
     *
     * @return value at the front of the buffer
     */
    fun removeFirst(): Double {
        //If the buffer is empty, do nothing and return 0.0
        if (dataOfBuffer.isEmpty()) {
            return 0.0
        }
        val temp = dataOfBuffer[front]
        front = moduloInc(front)
        --length
        return temp
    }

    /**
     * Pop value at the back of the buffer.
     */
    fun removeLast(): Double {
        //If the buffer is empty, do nothing and return 0.0
        if (dataOfBuffer.isEmpty()) {
            return 0.0
        }

        --length
        return dataOfBuffer[(front + length) % dataOfBuffer.size]
    }

    /**
     * Resizes internal buffer to given size.
     *
     *
     * A new buffer is allocated because arrays are not resizable.
     */
    fun resize(size: Int) {
        val newBuffer = DoubleArray(size)
        length = Math.min(length, size)
        for (i in 0 until length) {
            newBuffer[i] = dataOfBuffer[(front + i) % dataOfBuffer.size]
        }
        dataOfBuffer = newBuffer
        front = 0
    }

    /**
     * Sets internal buffer contents to 0
     */
    fun clear() {
        dataOfBuffer = DoubleArray(dataOfBuffer.size) { i -> 0.0}
        front = 0
        length = 0
    }

    /**
     * Get the element at the provided index relative to the start of the buffer.
     *
     * @return element at index starting from front of buffer
     */
    operator fun get(index: Int): Double = dataOfBuffer[(front + index) % dataOfBuffer.size]


    /**
     * Increment an index modulo the length of the m_data buffer.
     */
    private fun moduloInc(index: Int): Int = (index + 1) % dataOfBuffer.size

    /**
     * Decrement an index modulo the length of the dataOfBuffer.
     */
    private fun moduloDec(index: Int): Int = if (index == 0) dataOfBuffer.size-1 else index-1
}