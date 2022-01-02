package com.androidtetris

import com.androidtetris.game.CoordinatesChangedEventArgs
import com.androidtetris.game.Event
import com.androidtetris.game.EventDispatcher
import com.androidtetris.game.Point
import org.junit.Assert.*
import org.junit.Test

class EventDispatcherUnitTest {
    var functionCalled = false

    @Test
    fun testAddCallbackDispatchEvent() {
        val dispatcher = EventDispatcher()
        dispatcher.addCallback(Event.CoordinatesChanged, ::displayCoordinates)
        dispatcher.dispatch(
            Event.CoordinatesChanged, CoordinatesChangedEventArgs(arrayOf(Point(0, 0)), arrayOf(
                Point(1, 1)
            ))
        )
        // Assert that the function was called
        assertTrue(functionCalled)
        functionCalled = false
    }
    fun displayCoordinates(args: CoordinatesChangedEventArgs) {
        // Our callback function
        assertTrue(args.old[0].x == 0)
        assertTrue(args.old[0].y == 0)
        assertTrue(args.new[0].x == 1)
        assertTrue(args.new[0].y == 1)
        functionCalled = true
    }

    @Test
    fun testDeleteCallback() {
        val dispatcher = EventDispatcher()
        dispatcher.deleteCallback(Event.CoordinatesChanged, ::displayCoordinates)
        // Dispatch the event. The function should NOT be called, because it was removed.
        dispatcher.dispatch(
            Event.CoordinatesChanged, CoordinatesChangedEventArgs(arrayOf(Point(0, 0)), arrayOf(
                Point(1, 1)
            ))
        )
        // Assert that the function was NOT called
        assertFalse(functionCalled)
    }
}