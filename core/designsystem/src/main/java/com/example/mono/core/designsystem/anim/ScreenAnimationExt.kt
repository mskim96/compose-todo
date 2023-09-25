package com.example.mono.core.designsystem.anim

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut

fun AnimatedContentTransitionScope<*>.slideIntoUp(): EnterTransition =
    slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Up,
        animationSpec = tween(300),
        initialOffset = { it / 2 }
    )

fun AnimatedContentTransitionScope<*>.slideOutOfDown(): ExitTransition =
    fadeOut(
        animationSpec = tween(300)
    ) + slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Down,
        animationSpec = tween(500)
    )

