package com.androidtetris.game

import kotlin.reflect.*

enum class Event { CoordinatesChanged, Collision, LinesCompleted }

data class CoordinatesChangedEventArgs(val old: Array<Point>, val new: Array<Point>)
data class CollisionEventArgs(val coordinates: Array<Point>, val direction: Direction)
data class LinesCompletedEventArgs(val lines: List<Int>)

class EventDispatcher {
    private val callbacks: HashMap<Event, MutableList<KFunction<Unit>>> = hashMapOf()

    private fun callRef(callbackFunc: KFunction<Any>, args: Any) {
        // Calls the function itself
        callbackFunc.call(args)
    }

    fun dispatch(event: Event, args: Any) {
        val callbackFunctions = callbacks[event] ?: return
        for(cb in callbackFunctions) {
            callRef(cb, args)
        }
    }

    fun addCallback(event: Event, func: KFunction<Unit>) {
        if (callbacks[event] == null) {
            callbacks[event] = mutableListOf(func)
        }
        callbacks[event]?.add(func)
    }

    fun deleteCallback(event: Event, func: KFunction<Unit>) {
        callbacks[event]?.remove(func)
    }
}