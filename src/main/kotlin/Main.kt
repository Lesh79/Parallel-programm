fun main() {
    val stack = TreiberStack<Int>()

    stack.push(1)
    stack.push(2)
    stack.push(3)
    println("Peek: ${stack.peek()}")
    println("Pop: ${stack.pop()}")
    println("Pop: ${stack.pop()}")
    println("Pop: ${stack.pop()}")
    println("Pop: ${stack.pop()}")
}