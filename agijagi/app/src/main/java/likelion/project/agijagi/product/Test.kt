package likelion.project.agijagi.product

import android.util.Log
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun test() = runBlocking {
//    launch {
    test2()
//    }
    Log.d("hye2","DONE")
}

suspend fun test2() = coroutineScope {
    launch {
        delay(2000)
        Log.d("hye1", "World1")
    }
    launch {
        delay(1000)
        Log.d("hye1", "World2")
    }
    Log.d("hye2", "Hello")
}