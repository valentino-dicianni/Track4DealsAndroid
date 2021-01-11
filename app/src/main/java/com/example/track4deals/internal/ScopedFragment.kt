package com.example.track4deals.internal

import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext


// this class let us launch a job from a fragment on the main ui thread
// LOCAL SCOPE
// the difference from this and using Global scope is that
// here we can call job.cancel() when the fragment is canceled

abstract class ScopedFragment : Fragment(), CoroutineScope {
    private lateinit var job: Job

    //run on main thread
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}