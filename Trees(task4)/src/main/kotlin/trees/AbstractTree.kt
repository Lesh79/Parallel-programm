package trees

import Node

abstract class BinaryTree<T : Comparable<T>> {

    protected var root: Node<T>? = null



    abstract suspend fun insert(value: T)
    abstract suspend fun delete(value: T)
    abstract suspend fun contains(value: T): Boolean

    protected fun insertNode(node: Node<T>?, value: T): Node<T> {
        node ?: return Node(value)
        if (value < node.value) {
            node.left = insertNode(node.left, value)
        } else if (value > node.value) {
            node.right = insertNode(node.right, value)
        }
        return node
    }

    fun deleteNode(node: Node<T>?, value: T): Node<T>? {
        node ?: return null

        when {
            value < node.value -> node.left = deleteNode(node.left, value)
            value > node.value -> node.right = deleteNode(node.right, value)
            else -> {
                if (node.left == null) return node.right
                if (node.right == null) return node.left

                node.value = findMin(node.right!!)
                node.right = deleteNode(node.right, node.value)
            }
        }
        return node
    }

    protected fun containsNode(node: Node<T>?, value: T): Boolean {
        node ?: return false
        return when {
            value < node.value -> containsNode(node.left, value)
            value > node.value -> containsNode(node.right, value)
            else -> true
        }
    }

    private fun findMin(node: Node<T>): T {
        var current = node
        while (current.left != null) {
            current = current.left!!
        }
        return current.value
    }
}
