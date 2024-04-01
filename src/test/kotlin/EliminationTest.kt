import kotlin.test.*
import org.jetbrains.kotlinx.lincheck.annotations.*
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions


class EliminationTest {
    var stack = EliminationStack<Int>()

    @Operation
    suspend fun push(value: Int) = stack.push(value)

    @Operation
    suspend fun pop() = stack.pop()

    @Operation
    suspend fun peek() = stack.peek()

    @Test // JUnit
    fun stressTest() = StressOptions().check(this::class) // The magic button
}