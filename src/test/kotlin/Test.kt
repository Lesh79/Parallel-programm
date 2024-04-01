import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.DefaultAsserter.assertEquals
import kotlin.test.assertEquals

class EliminationStackTest {

    @Test
    fun testPushAndPop() = runBlocking<Unit> {
        val stack = EliminationStack<Int>()

        stack.push(1)
        assertEquals(1, stack.peek())

        stack.push(2)
        assertEquals(3, stack.peek())

        assertEquals(2, stack.pop())
        assertEquals(1, stack.pop())
        assertEquals(null, stack.pop())
    }

}