import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import trees.BinaryTree
import kotlin.test.assertFalse
import kotlin.test.assertTrue

abstract class AbstractTest<T : Comparable<T>> {
    protected abstract fun createBSTree(): BinaryTree<Int>

    @Test
    fun testParallelInsertAndContains() = runBlocking {
        val tree = createBSTree()
        val values = (1..1000).toList()
        val mid = values.size / 2

        coroutineScope {
            val job1 = launch {
                for (i in 0..<mid) {
                    tree.insert(values[i])
                }
            }
            val job2 = launch {
                for (i in mid..<values.size) {
                    tree.insert(values[i])
                }
            }
            joinAll(job1, job2)
        }

        values.forEach {
            assertTrue(tree.contains(it), "Tree should contain value $it")
        }
    }

    @Test
    fun testParallelInsertAndDelete() = runBlocking {
        val tree = createBSTree()
        val values = (1..1000).toList()
        val mid = values.size / 2

        coroutineScope {
            val insertJob1 = launch {
                for (i in 0..<mid) {
                    tree.insert(values[i])
                }
            }
            val insertJob2 = launch {
                for (i in mid..<values.size) {
                    tree.insert(values[i])
                }
            }
            joinAll(insertJob1, insertJob2)
        }

        coroutineScope {
            val deleteJob1 = launch {
                for (i in 0..<mid) {
                    tree.delete(values[i])
                }
            }
            val deleteJob2 = launch {
                for (i in mid..<values.size) {
                    tree.delete(values[i])
                }
            }
            joinAll(deleteJob1, deleteJob2)
        }

        values.forEach {
            assertFalse(tree.contains(it), "Tree should not contain value $it after parallel insert and delete")
        }
    }
}
