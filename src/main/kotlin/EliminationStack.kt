import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicReference

enum class Operations {PUSH, POP}

class EliminationStack<T> : Stack<T> {
    private val eliminationArray = arrayOfNulls<AtomicReference<ThreadInfo<T>>>(1000)
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
    private fun TryCollision(p: ThreadInfo<T>, q: ThreadInfo<T>): Boolean {
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
    private fun FinishCollision(p: ThreadInfo<T>){
        if (p.op == Operations.POP){
            p.node = eliminationArray[p.mypid.toInt()]?.get()?.node
            eliminationArray[p.mypid.toInt()] = null
        }
    }

    private suspend fun StackOp(p: ThreadInfo<T>){
        if (!TryPerformStackOp(p))
            LesOp(p)
        return
    }
    private suspend fun LesOp (p: ThreadInfo<T>){
        while (true){
            eliminationArray[p.mypid.toInt()] = AtomicReference(p)
            val pos = collision.random()?.get()!!
            var him = collision[pos]
            while (collision[pos]?.compareAndSet(him?.get(), p.mypid.toInt()) == false)
                him = collision[pos]
            if (him != null){
                val q = eliminationArray[him.get()]?.get()
                if (q != null && q.mypid.toInt() == him.get() && q.op != p.op){
                    if (eliminationArray[p.mypid.toInt()]?.compareAndSet(p, null) == true){
                        if (TryCollision(p,q))
                            return
                        else {
                            if (TryPerformStackOp(p)){
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
            if (TryPerformStackOp(p))
                return
        }
    }

    private fun TryPerformStackOp(p: ThreadInfo<T>): Boolean {
        val pHead : Node<T>?
        val pNext : Node<T>?

        if (p.op == Operations.PUSH){
            pHead = this.top.get()
            p.node?.next = pHead
            return this.top.compareAndSet(pHead, p.node)
        }
        if(p.op == Operations.POP){
            pHead = this.top.get()
            if(pHead == null){
                p.node = null
                return true
            }
            pNext = pHead.next
            if (this.top.compareAndSet(pHead, pNext)) {
                p.node = pHead
                return true
            }
        }
        p.node = null
        return false
    }
}