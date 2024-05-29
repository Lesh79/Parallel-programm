package trees

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class HardSync<T: Comparable<T>>: BinaryTree<T>() {
    private val mutex = Mutex()

    override suspend fun insert(value: T) {
        mutex.withLock {
            root = insertNode(root, value)
        }
    }

    override suspend fun delete(value: T) {
        mutex.withLock {
            root = deleteNode(root, value)
        }
    }

    override suspend fun contains(value: T): Boolean {
        return mutex.withLock {
            containsNode(root, value)
        }
    }
}
