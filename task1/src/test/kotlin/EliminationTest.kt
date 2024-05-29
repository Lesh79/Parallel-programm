
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import kotlin.test.Test


class EliminationTest {
    private val stack = EliminationStack<Int>()

    @Operation
    suspend fun push(value: Int) = stack.push(value)

    @Operation
    suspend fun pop() =  stack.pop()

    @Operation
    fun peek() =  stack.peek()

    @Test
    fun modelCheckingTest() = ModelCheckingOptions().check(this::class)

    @Test
    fun stressTest() = StressOptions().check(this::class)



}