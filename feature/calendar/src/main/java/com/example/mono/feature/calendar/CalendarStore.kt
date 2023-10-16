package com.example.mono.feature.calendar

class CalendarStore<V>(private val onCreate: (offset: Int) -> V) : HashMap<Int, V>() {
    override fun get(key: Int): V {
        val value = super.get(key)
        return if (value != null) {
            value
        } else {
            val newData = onCreate(key)
            put(key, newData)
            newData
        }
    }
}