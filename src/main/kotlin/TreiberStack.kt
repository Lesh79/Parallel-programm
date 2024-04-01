import java.util.concurrent.atomic.AtomicReference

class TreiberStack<T> : Stack<T>{
    private val head = AtomicReference<Node<T>?>(null)

    override suspend fun push(value: T) {
        val newHead = Node(value)
        var currentHead: Node<T>?
        do {
            currentHead = head.get()
            newHead.next = currentHead
        } while (!head.compareAndSet(currentHead, newHead))
    }

    override suspend fun pop(): T? {
        var currentHead: Node<T>?
        var newHead: Node<T>?
        do {
            currentHead = head.get()
            if (currentHead == null) return null
            newHead = currentHead.next
        } while (!head.compareAndSet(currentHead, newHead))
        return currentHead?.value
    }

    override fun peek(): T? {
        return head.get()?.value
    }

}