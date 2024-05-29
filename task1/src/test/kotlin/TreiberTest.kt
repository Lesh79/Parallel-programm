import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.Test

class TreiberTest {
    var stack = TreiberStack<Int>()

    @Operation
    suspend fun push(value: Int) = stack.push(value)

    @Operation
    suspend fun pop() = stack.pop()

    @Operation
    suspend fun peek() = stack.peek()

    @Test // JUnit
    fun stressTest() = StressOptions().check(this::class) // The magic button
}