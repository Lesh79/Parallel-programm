import trees.BinaryTree
import trees.Thin

class TestThin : AbstractTest<Int>() {
    override fun createBSTree(): BinaryTree<Int> {
        return Thin()
    }
}