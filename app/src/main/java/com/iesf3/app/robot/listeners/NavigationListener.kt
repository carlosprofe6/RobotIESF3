package com.iesf3.app.robot.listeners

interface NavigationListener {
    fun onRouteBlocked()
    fun onObstacleDisappeared()
    fun onNavigationStarted()
}