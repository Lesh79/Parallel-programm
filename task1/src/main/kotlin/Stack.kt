interface Stack<T>{
    suspend fun push(value: T)

    suspend fun pop(): T?

    fun peek(): T?
}