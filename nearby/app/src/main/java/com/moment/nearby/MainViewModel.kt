package com.moment.nearby

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.google.android.gms.nearby.Nearby
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel(), DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
     }

    override fun onStop(owner: LifecycleOwner) {
        // fragment is now stopped, cancel running tasks
    }
}