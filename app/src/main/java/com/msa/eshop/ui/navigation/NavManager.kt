package com.msa.eshop.ui.navigation

import android.os.Bundle
import androidx.navigation.NavOptions
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@Singleton
class NavManager @Inject constructor() {

    private val commandsChannel = Channel<NavInfo>(
        capacity = Channel.BUFFERED
    )

    val commands = commandsChannel.receiveAsFlow()

    fun navigate(navInfo: NavInfo) {
        commandsChannel.trySend(navInfo)
    }

    fun navigate(route: String, navOptions: NavOptions? = null) {
        navigate(
            NavInfo(
                id = route,
                navOption = navOptions
            )
        )
    }

    fun back() {
        navigate(Route.BACK.route)
    }
}

data class NavInfo(
    val id: String? = null,
    val args: Bundle? = null,
    val navOption: NavOptions? = null
)