package com.example.mono.core.common.scope

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val monoDispatcher: MonoDispatchers)

enum class MonoDispatchers {
    Default,
    IO
}