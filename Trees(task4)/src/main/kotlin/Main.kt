
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import trees.Optimistic

fun main() = runBlocking {
    val tree = Optimistic<Int>()
    coroutineScope {
        launch {
            tree.insert(5)
            tree.insert(3)
            tree.insert(7)
        }
        launch {
            tree.insert(124)
            tree.insert(1)
            tree.insert(35)
        }
    }

    println(tree.contains(3)) // true
    println(tree.contains(124)) // true
    tree.delete(3)
    println(tree.contains(3)) // false
}