class Node<T>(var value: T) {
    var next: Node<T>? = null
}

class ThreadInfo<T>( var op: Operations, var node: Node<T>?){
    var mypid :Long = Thread.currentThread().id
    val spin = 1000L
}

class SimpleStack<T>(ptop: Node<T>)