package com.androidtetris

import com.androidtetris.game.event.*
import com.androidtetris.game.*
import org.junit.Assert.*
import org.junit.Test


class EventDispatcherUnitTest {
    var functionCalled = false
    val dispatcher = EventDispatcher()

    @Test
    fun testAddCallbackDispatchEvent() {
        // Add a callback and dispatch its event
        dispatcher.addCallback(Event.CoordinatesChanged, ::displayCoordinates)
        dispatcher.dispatch(
            Event.CoordinatesChanged, CoordinatesChangedEventArgs(arrayOf(Point(0, 0)), arrayOf(
                Point(1, 1)
            ), TetrominoCode.I)
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
        dispatcher.deleteCallback(Event.CoordinatesChanged, ::displayCoordinates)
        // Dispatch the event. The function should NOT be called, because it was removed.
        dispatcher.dispatch(
            Event.CoordinatesChanged, CoordinatesChangedEventArgs(arrayOf(Point(0, 0)), arrayOf(
                Point(1, 1)
            ), TetrominoCode.I)
        )
        // Assert that the function was NOT called
        assertFalse(functionCalled)
    }
}