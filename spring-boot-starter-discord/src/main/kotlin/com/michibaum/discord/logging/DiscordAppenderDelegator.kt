package com.michibaum.discord.logging

import ch.qos.logback.core.Appender
import ch.qos.logback.core.UnsynchronizedAppenderBase
import java.util.*

/**
 * A delegator for an appender that initially buffers log events and later delegates them
 * to the specified appender upon being set.
 *
 * This class handles the buffering of log events when no delegate is set. When a delegate
 * appender is set using the setDelegateAndReplayBuffer method, all buffered events are
 * replayed to the delegate and future events are directly passed to the delegate appender.
 *
 * @param E The type of log event.
 */
class DiscordAppenderDelegator<E> : UnsynchronizedAppenderBase<E>() {
    private val logBuffer: MutableList<E> = Collections.synchronizedList(ArrayList())
    private var delegate: Appender<E>? = null

    /**
     * Appends a log event to the buffer or directly to the delegate appender if it is set.
     *
     * If no delegate appender is set, the log event is added to an internal buffer.
     * Once a delegate is set, all buffered events will be replayed to it.
     *
     * @param event The log event to be appended.
     */
    override fun append(event: E) {
        synchronized(logBuffer) {
            if (delegate != null) {
                delegate!!.doAppend(event)
            } else {
                logBuffer.add(event)
            }
        }
    }

    /**
     * Sets the delegate appender and replays all buffered log events to it.
     *
     * This function synchronizes access to the log buffer, sets the given delegate appender,
     * and then replays all the buffered log events to the newly set delegate.
     * After replaying the buffered events, it clears the buffer to free up memory.
     *
     * @param delegate The appender that should handle the log events going forward.
     */
    fun setDelegateAndReplayBuffer(delegate: Appender<E>) {
        synchronized(logBuffer) {
            this.delegate = delegate
            for (event in logBuffer) {
                delegate.doAppend(event)
            }
            logBuffer.clear()
        }
    }
}
