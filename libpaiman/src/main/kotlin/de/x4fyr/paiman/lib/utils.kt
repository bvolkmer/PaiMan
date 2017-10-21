package de.x4fyr.paiman.lib

import java.util.concurrent.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * A future whose result is external settable
 */
class SettableFuture<T: Any>: Future<T> {

    private lateinit var value: T
    private var done = false
    private var canceled = false
    private var excepted = false
    private lateinit var exception: Throwable
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    /** Set the result of the Future */
    fun set(result: T) {
        lock.withLock {
            value = result
            done = true
            condition.signalAll()
        }
    }

    /**
     * Waits if necessary for at most the given time for the computation
     * to complete, and then retrieves its result, if available.
     *
     * @param timeout the maximum time to wait
     * @param unit the time unit of the timeout argument
     * @return the computed result
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException if the computation threw an
     * exception
     * @throws InterruptedException if the current thread was interrupted
     * while waiting
     * @throws TimeoutException if the wait timed out
     */
    override fun get(timeout: Long, unit: TimeUnit?): T {
        return lock.withLock {
            if (!done) {
                condition.await(timeout, unit)
                when {
                    isDone -> value
                    isCancelled -> throw CancellationException()
                    excepted -> throw ExecutionException(exception)
                    else -> throw TimeoutException()
                }
            } else value
        }
    }

    /**
     * Waits if necessary for the computation to complete, and then
     * retrieves its result.
     *
     * @return the computed result
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException if the computation threw an
     * exception
     * @throws InterruptedException if the current thread was interrupted
     * while waiting
     */
    override fun get(): T {
        lock.withLock {
            while (!done) {
                condition.await()
                if (isCancelled) throw CancellationException()
                if (excepted) throw ExecutionException(exception)
            }
            return value
        }
    }

    /**
     * Returns `true` if this task was cancelled before it completed
     * normally.
     *
     * @return `true` if this task was cancelled before it completed
     */
    override fun isCancelled(): Boolean = canceled

    /**
     * Returns `true` if this task completed.
     *
     * Completion may be due to normal termination, an exception, or
     * cancellation -- in all of these cases, this method will return
     * `true`.
     *
     * @return `true` if this task completed
     */
    override fun isDone(): Boolean = done

    /**
     * Attempts to cancel execution of this task.  This attempt will
     * fail if the task has already completed, has already been cancelled,
     * or could not be cancelled for some other reason. If successful,
     * and this task has not started when `cancel` is called,
     * this task should never run.  If the task has already started,
     * then the `mayInterruptIfRunning` parameter determines
     * whether the thread executing this task should be interrupted in
     * an attempt to stop the task.
     *
     *
     * After this method returns, subsequent calls to [.isDone] will
     * always return `true`.  Subsequent calls to [.isCancelled]
     * will always return `true` if this method returned `true`.
     *
     * @param mayInterruptIfRunning `true` if the thread executing this
     * task should be interrupted; otherwise, in-progress tasks are allowed
     * to complete
     * @return `false` if the task could not be cancelled,
     * typically because it has already completed normally;
     * `true` otherwise
     */
    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        lock.withLock {
            canceled = true
            condition.signalAll()
        }
        return true
    }

    /** Let the future end with an exception */
    fun except(cause: Throwable) {
        lock.withLock {
            excepted = true
            exception = cause
            condition.signalAll()
        }
    }

}
