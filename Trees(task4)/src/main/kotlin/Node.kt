import kotlinx.coroutines.sync.Mutex

class Node <T: Comparable<T>>(var value: T) {
    var key: T? = null
    var left: Node<T>? = null
    var right: Node<T>? = null
    var mutex = Mutex()
}