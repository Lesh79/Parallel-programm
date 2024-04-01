import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicReference

enum class Operations {PUSH, POP}

class EliminationStack<T> : Stack<T> {
    private val eliminationArray = arrayOfNulls<AtomicReference<ThreadInfo<T>>>(32)
    private val top = AtomicReference<Node<T>>(null)
    private val collision = arrayOfNulls<AtomicReference<Int>>(6)


    override suspend fun push(value: T){
        StackOp(ThreadInfo(Operations.PUSH, Node(value)))
    }

    override suspend fun pop(): T? {
        val info = ThreadInfo<T>(Operations.POP, null)
        StackOp(info)
        return info.node?.value
    }
    override fun peek(): T?{
        return top.get().value
    }
    fun TryCollision(p: ThreadInfo<T>, q: ThreadInfo<T>): Boolean {
        if (p.op == Operations.PUSH){
            val him = p.mypid
            return eliminationArray[him.toInt()]?.compareAndSet(q, p) == true
        }
        if (p.op == Operations.POP){
            val him = p.mypid
            if (eliminationArray[him.toInt()]?.compareAndSet(q, null) == true){
                p.node = q.node
                eliminationArray[him.toInt()] = null
                return true
            }
        }
         return false
    }
    fun FinishCollision(p: ThreadInfo<T>){
        if (p.op == Operations.POP){
            p.node = eliminationArray[p.mypid.toInt()]?.get()?.node
            eliminationArray[p.mypid.toInt()] = null
        }
    }

    suspend fun StackOp(p: ThreadInfo<T>){
        if (TryPerformStackOp(p) == false)
            LesOp(p)
        return
    }
    suspend fun LesOp (p: ThreadInfo<T>){
        while (true){
            eliminationArray[p.mypid.toInt()] = AtomicReference(p)
            var pos = collision.random()?.get()!!
            var him = collision[pos]
            while (collision[pos]?.compareAndSet(him?.get(), p.mypid.toInt()) == false)
                him = collision[pos]
            if (him != null){
                var q = eliminationArray[him.get()]?.get()
                if (q != null && q.mypid.toInt() == him.get() && q.op != p.op){
                    if (eliminationArray[p.mypid.toInt()]?.compareAndSet(p, null) == true){
                        if (TryCollision(p,q) == true)
                            return
                        else {
                            if (TryPerformStackOp(p) == true){
                                return
                            }
                            continue
                        }
                    }
                    else{
                        FinishCollision(p)
                        return
                    }
                }
            }
            delay(p.spin)
            if (eliminationArray[p.mypid.toInt()]?.compareAndSet(p, null) == false){
                FinishCollision(p)
                return
            }
            if (TryPerformStackOp(p) == true)
                return
        }
    }

    fun TryPerformStackOp(p: ThreadInfo<T>): Boolean {
        val phead : Node<T>?
        val pnext : Node<T>?

        if (p.op == Operations.PUSH){
            phead = this.top.get()
            p.node?.next = phead
            return this.top.compareAndSet(phead, p.node)
        }
        if(p.op == Operations.POP){
            phead = this.top.get()
            if(phead == null){
                p.node = null
                return true
            }
            pnext = phead.next
            if (this.top.compareAndSet(phead, pnext)) {
                p.node = phead
                return true
            }
        }
        p.node = null
        return false
    }
}