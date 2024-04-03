import java.util.concurrent.atomic.AtomicReference

enum class Operations {PUSH, POP}

class EliminationStack<T> : Stack<T> {
    private val eliminationArray = arrayOfNulls<AtomicReference<ThreadInfo<T>>?>(1000)
    private val top = AtomicReference<Node<T>>(null)
    private val collision = Array<AtomicReference<Int>>(6) {AtomicReference(null)}


    override suspend fun push(value: T) {

        StackOp(ThreadInfo(Operations.PUSH, Node(value)))

    }

    override suspend fun pop(): T? {

        val info = ThreadInfo<T>(Operations.POP, null)
        StackOp(info)
        return info.node?.value

    }

    override fun peek(): T? {

        return top.get()?.value

    }

    private fun TryCollision(p: ThreadInfo<T>, q: ThreadInfo<T>): Boolean {
        val him = p.mypid
        if (p.op == Operations.PUSH){
            return eliminationArray[him]?.compareAndSet(q, p) == true
        }
        if (p.op == Operations.POP){
            if (eliminationArray[him]?.compareAndSet(q, null) == true){
                p.node = q.node
                eliminationArray[p.mypid] = null
                return true
            }
        }
        return false
    }
    private fun FinishCollision(p: ThreadInfo<T>){
        if (p.op == Operations.POP){
            p.node = eliminationArray[p.mypid]?.get()?.node
            eliminationArray[p.mypid]?.set(null)
        }
    }

    private fun StackOp(p: ThreadInfo<T>){
        if (!TryPerformStackOp(p))
            LesOp(p)
        return
    }
    private fun LesOp (p: ThreadInfo<T>){
        while (true){
            eliminationArray[p.mypid] = AtomicReference(p)
            val pos = java.util.Random().nextInt(collision.size)
            var him = collision[pos].get()
            while (!collision[pos].compareAndSet(him, p.mypid))
                him = collision[pos].get()
            if (him != null){
                val q = eliminationArray[him]?.get()
                if (q != null && q.mypid == him && q.op != p.op){
                    if (eliminationArray[p.mypid]?.compareAndSet(p, null) == true){
                        if (TryCollision(p,q))
                            return
                        if (TryPerformStackOp(p)){
                            return
                        }
                        continue
                    }
                    else{
                        FinishCollision(p)
                        return
                    }
                }
            }
            if (eliminationArray[p.mypid]?.compareAndSet(p, null) == false){
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

        if (p.mypid >= 1000)
            throw IllegalArgumentException("Thread cannot be placed to array of size ")

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