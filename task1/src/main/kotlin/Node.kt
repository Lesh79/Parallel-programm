class Node<T>(var value: T) {
    var next: Node<T>? = null
}

class ThreadInfo<T>( var op: Operations, var node: Node<T>?){
    var mypid  = Thread.currentThread().id.toInt()
}
