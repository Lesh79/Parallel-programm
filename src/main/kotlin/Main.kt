fun main() {
    println("-=-=-==-===-=-=-=-=-=Default Treiber stack-=-=-=-=-=-=-=-=-=-=-=-=-")
    val stack2 = TreiberStack<Int>()
    val startTime2 = System.nanoTime()
    val threads2 = mutableListOf<Thread>()
    repeat(10){
        val thread = Thread{
            val value2 = (1..100).random()
            stack2.push(value2)
            println("Thread ${Thread.currentThread().id} pushed: $value2")
        }
        threads2.add(thread)
        thread.start()
    }
    threads2.forEach { it.join() }


}