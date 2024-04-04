import kotlinx.coroutines.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals
import kotlin.test.assertNull

class EliminationStackTest {


    @ParameterizedTest
    @ValueSource(strings = ["TreiberStack", "EliminationStack"])
    fun testPushAndPop(name: String) {

        val stack = when (name) {
            "TreiberStack" -> TreiberStack<Int>()
            "EliminationStack" -> EliminationStack<Int>()
            else -> throw IllegalArgumentException("Unknown stack type: $name")
        }

        runBlocking {
            stack.push(1)
            assertEquals(1, stack.peek())

            stack.push(2)
            assertEquals(2, stack.peek())

            assertEquals(2, stack.pop())
            assertEquals(1, stack.pop())
            assertEquals(null, stack.pop())
        }

    }



    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    @ParameterizedTest
    @ValueSource(strings = ["TreiberStack", "EliminationStack"])
    fun correctnessTest(name: String) {
        val threads = 10
        val iterations = 1_000
        val stack = when (name) {
            "TreiberStack" -> TreiberStack<Int>()
            "EliminationStack" -> EliminationStack<Int>()
            else -> throw IllegalArgumentException("Unknown stack type: $name")
        }

        val successfulPops = AtomicInteger(0)
        val valuesChecked = Array(iterations * threads) { false }
        val tasks = mutableListOf<Job>()
        val executionTime = measureTimeMillis {
            runBlocking {
                repeat(threads) { curThread ->
                    tasks.add(launch(newSingleThreadContext(curThread.toString())) {
                        repeat(iterations) {
                            stack.push(it * threads + curThread)
                        }
                    })
                    tasks.add(launch(newSingleThreadContext((curThread + threads).toString())) {
                        repeat(iterations) {
                            stack.pop()?.let { pos ->
                                assert(!valuesChecked[pos])
                                valuesChecked[pos] = true
                                successfulPops.getAndIncrement()
                            }
                        }
                    })
                }
                tasks.joinAll()
                while (successfulPops.get() < iterations * threads) {
                    stack.pop()?.let { pos ->
                        assert(!valuesChecked[pos])
                        valuesChecked[pos] = true
                        successfulPops.getAndIncrement()
                    }
                }
                assertEquals(null, stack.pop())
                for (valueChecked in valuesChecked) {
                    assert(valueChecked)
                }
            }
        }

        println("Execution time TEST-1 of $name: $executionTime milliseconds")
    }


    @ParameterizedTest
    @ValueSource(strings = ["TreiberStack", "EliminationStack"])
    fun testConcurrentPushAndPop(name: String) {
        val stack = when (name) {
            "TreiberStack" -> TreiberStack<Int>()
            "EliminationStack" -> EliminationStack<Int>()
            else -> throw IllegalArgumentException("Unknown stack type: $name")
        }

        runBlocking {
            val threads = 1000
            val iterations = 100_000

            val executionTime = measureTimeMillis {
                repeat(threads) {
                    val job1 = launch {
                        repeat(iterations) { stack.push(it) }
                    }
                    val job2 = launch {
                        repeat(iterations) { stack.pop() }
                    }
                    val job3 = launch {
                        repeat(iterations) { stack.peek() }
                    }

                    listOf(job1, job2, job3).joinAll()
                }
            }

            println("Execution time TEST-2 of $name: $executionTime milliseconds")
            assertNull(stack.pop())
            assertNull(stack.peek())
        }
    }
}