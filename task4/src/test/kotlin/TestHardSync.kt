import trees.BinaryTree
import trees.HardSync

class TestHardSync: AbstractTest<Int>(){
    override fun createBSTree(): BinaryTree<Int> {
        return HardSync()
    }
}