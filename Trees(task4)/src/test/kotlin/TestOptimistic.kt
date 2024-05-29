import trees.BinaryTree
import trees.Optimistic

class TestOptimistic: AbstractTest<Int>() {
    override fun createBSTree(): BinaryTree<Int> {
        return Optimistic()
    }
}