package trees

import Node
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class Thin<T : Comparable<T>> : BinaryTree<T>() {

    private val treeMutex = Mutex()

    data class NodeWithParent<T : Comparable<T>>(val node: Node<T>?, val parent: Node<T>?)

    override suspend fun insert(value: T) {
        treeMutex.withLock {
            if (root == null) {
                root = Node(value)
            } else {
                insertRecursive(root, value)
            }
        }
    }

    private suspend fun insertRecursive(node: Node<T>?, value: T) {
        if (node == null) {
            return
        }
        val nodeMutex = node.mutex
        nodeMutex.withLock {
            if (value < node.value) {
                if (node.left == null) {
                    node.left = Node(value)
                } else {
                    insertRecursive(node.left, value)
                }
            } else {
                if (node.right == null) {
                    node.right = Node(value)
                } else {
                    insertRecursive(node.right, value)
                }
            }
        }
    }

    override suspend fun delete(value: T) {
        treeMutex.withLock {
            val (node, parent) = findNodeAndParent(value)
            node?.let {
                val nodeMutex = node.mutex
                nodeMutex.withLock {
                    when {
                        node.left == null && node.right == null -> {
                            parent?.let {
                                val parentMutex = it.mutex
                                parentMutex.withLock {
                                    if (it.left == node) it.left = null
                                    else it.right = null
                                }
                            } ?: run {
                                root = null
                            }
                        }

                        node.left == null || node.right == null -> {
                            val child = node.left ?: node.right
                            parent?.let {
                                val parentMutex = it.mutex
                                parentMutex.withLock {
                                    if (it.left == node) it.left = child
                                    else it.right = child
                                }
                            } ?: run {
                                root = child
                            }
                        }

                        else -> {
                            val successor = findMinNode(node.right!!)
                            val successorValue = successor.value
                            delete(successorValue)
                            node.value = successorValue
                        }
                    }
                }
            }
        }
    }

    override suspend fun contains(value: T): Boolean {
        treeMutex.withLock {
            val (node, _) = findNodeAndParent(value)
            return node != null
        }
    }

    private suspend fun findMinNode(node: Node<T>): Node<T> {
        var current = node
        var currentMutex = current.mutex

        while (current.left != null) {
            currentMutex.withLock {
                current = current.left!!
                currentMutex = current.mutex
            }
        }
        return current
    }

    private suspend fun findNodeAndParent(value: T): NodeWithParent<T> {
        var current = root
        var parent: Node<T>? = null

        while (current != null) {
            val currentMutex = current.mutex
            currentMutex.withLock {
                when {
                    value < current!!.value -> {
                        parent = current
                        current = current!!.left
                    }

                    value > current!!.value -> {
                        parent = current
                        current = current!!.right
                    }

                    else -> {
                        return NodeWithParent(current, parent)
                    }
                }
            }
        }
        return NodeWithParent(null, parent)
    }
}
