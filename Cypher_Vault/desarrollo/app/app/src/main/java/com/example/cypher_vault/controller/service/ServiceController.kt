package com.example.cypher_vault.controller.service

import com.example.cypher_vault.model.service.ServiceManager

class ServiceController(private val serviceManager: ServiceManager) {
    fun isInternetAvailable(): Boolean {
        return serviceManager.hasInternetConnection()
    }
}