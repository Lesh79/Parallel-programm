package trees

import Node
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class Thin<T : Comparable<T>> : BinaryTree<T>() {

    private val treeMutex = Mutex()

    data class NodeWithParent<T : Comparable<T>>(val node: Node<T>?, val parent: Node<T>?)

    override suspend fun insert(value: T) {
        val (node, parent) = findNodeAndParent(value)
        if (node == null) {
            val newNode = Node(value)
            if (parent == null) {
                treeMutex.withLock {
                    if (root == null) {
                        root = newNode
                    }
                }
            } else {
                if (value < parent.value) {
                    parent.left = newNode
                } else {
                    parent.right = newNode
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
