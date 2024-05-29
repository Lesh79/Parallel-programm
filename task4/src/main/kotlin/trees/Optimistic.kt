package trees

import Node
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class Optimistic<T : Comparable<T>> : BinaryTree<T>() {

    private val treeMutex = Mutex()

    data class NodeWithParent<T : Comparable<T>>(val node: Node<T>?, val parent: Node<T>?)

    override suspend fun insert(value: T) {
        while (true) {
            val (node, parent) = findNodeAndParent(value)
            val newNode = Node(value)
            parent?.let {
                val parentMutex = it.mutex
                parentMutex.withLock {
                    if (validate(parent, node, value)) {
                        if (value < parent.value)
                            parent.left = newNode
                        else
                            parent.right = newNode
                        return
                    }
                }
            } ?: run {
                treeMutex.withLock {
                    if (root == null) {
                        root = newNode
                        return
                    }
                }
            }
        }
    }

    override suspend fun delete(value: T) {
        while (true) {
            val (node, parent) = findNodeAndParent(value)
            node?.let {
                val nodeMutex = node.mutex
                nodeMutex.withLock {
                    if (validate(parent, node, value)) {
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
                                parent?.let{
                                    val parentMutex = it.mutex
                                    parentMutex.withLock {
                                        if (it.left == node)
                                            it.left = node.left ?: node.right
                                        else
                                            it.right = node.left ?: node.right
                                    }
                                } ?: run {
                                    root = node.left ?: node.right
                                }
                            }
                            else ->{
                                val (minNode, minParent) = findMinNode(node.right!!)
                                val minValue = minNode?.value
                                delete(minValue!!)
                                node.value = minValue
                            }
                        }
                        return
                    }
                }
            }
        }
    }

    override suspend fun contains(value: T): Boolean {
        val (node, _) = findNodeAndParent(value)
        return node != null
    }

    private fun findNodeAndParent(value: T): NodeWithParent<T> {
        var current = root
        var parent: Node<T>? = null

        while (current != null) {
            if (value < current.value) {
                parent = current
                current = current.left
            } else if (value > current.value) {
                parent = current
                current = current.right
            } else {
                return NodeWithParent(current, parent)
            }
        }
        return NodeWithParent(null, parent)
    }

    private fun validate(parent: Node<T>?, node: Node<T>?, value: T): Boolean {
        var curent = root
        var currentParent: Node<T>? = null

        while (curent != null) {
            if (value < curent.value) {
                currentParent = curent
                curent = curent.left
            } else if (value > curent.value) {
                currentParent = curent
                curent = curent.right
            } else break
        }
        return curent == node && currentParent == parent
    }

    private fun findMinNode(node: Node<T>): NodeWithParent<T>{
        var current: Node<T>? = node
        var parent: Node<T>? = null
        while (current?.left != null){
            parent = current
            current = current.left
        }
        return NodeWithParent(current, parent)
    }


}