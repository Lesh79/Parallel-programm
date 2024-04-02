import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertNull

class EliminationStackTest {

    @Test
    fun testPushAndPop() = runBlocking<Unit> {
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
    fun testConcurrentPushAndPop1() = runBlocking {
        val stack = EliminationStack<Int>()
        val iterations = 1000
        val counter = AtomicInteger(0)
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
        runBlocking {
            deferred.forEach { it.await() }
        }

        assert(counter.get() == 1000)
    }

    @Test
    fun testConcurrentPushAndPop2() = runBlocking {
        val stack = EliminationStack<Int>()

        val job1 = launch { repeat(100) { stack.push(it) } }
        val job2 = launch { repeat(100) { stack.pop() } }
        val job3 = launch { repeat(100) { stack.peek() } }

        listOf(job1, job2, job3).joinAll()

        assertNull(stack.pop())
        assertNull(stack.peek())
    }

}