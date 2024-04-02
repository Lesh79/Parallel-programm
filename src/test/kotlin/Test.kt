import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals
import kotlin.test.assertNull

class EliminationStackTest {

    @Test
    fun testPushAndPop() = runBlocking {
        val stack = EliminationStack<Int>()

        stack.push(1)
        assertEquals(1, stack.peek())

        stack.push(2)
        assertEquals(2, stack.peek())

        assertEquals(2, stack.pop())
        assertEquals(1, stack.pop())
        assertEquals(null, stack.pop())
    }

    @Test
    fun testCorrectPPP() = runBlocking {
        val stack = EliminationStack<Int>()
        val iterations = 1000
        val counter = AtomicInteger(0)

        val executionTime = measureTimeMillis {
            val deferred = (1..iterations).map {
                async {
                    stack.push(it)
                    delay(100)
                    val value = stack.pop()
                    if (value != null) {
                        counter.incrementAndGet()
                    }
                }
            }
            deferred.forEach { it.await() }
        }

        assert(counter.get() == 1000)
        println("Execution time of test Correct PPP: $executionTime milliseconds")
    }

    @Test
    fun testConcurrentPushAndPop() = runBlocking {
        val stack = EliminationStack<Int>()
        val numRepeats = 100
        val numOperations = 1_000_000

        val executionTime = measureTimeMillis {
            repeat(numRepeats) {
                val job1 = launch {
                    repeat(numOperations) { stack.push(it) }
                }
                val job2 = launch {
                    repeat(numOperations) { stack.pop() }
                }
                val job3 = launch {
                    repeat(numOperations) { stack.peek() }
                }

                listOf(job1, job2, job3).joinAll()
            }
        }

        println("Execution time of test Concurrent Push And Pop: $executionTime milliseconds")
        assertNull(stack.pop())
        assertNull(stack.peek())
    }
    @Test
    fun testConcurrentPushAndPopTreiber() = runBlocking {
        val stack = TreiberStack<Int>()
        val numRepeats = 100
        val numOperations = 1_000_000

        val executionTime = measureTimeMillis {
            repeat(numRepeats) {
                val job1 = launch {
                    repeat(numOperations) { stack.push(it) }
                }
                val job2 = launch {
                    repeat(numOperations) { stack.pop() }
                }
                val job3 = launch {
                    repeat(numOperations) { stack.peek() }
                }

                listOf(job1, job2, job3).joinAll()
            }
        }

        println("Execution time of Treiber: $executionTime milliseconds")
        assertNull(stack.pop())
        assertNull(stack.peek())
    }
}
