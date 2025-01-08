package com.example.travelshooting.local.service;

import com.example.travelshooting.local.controller.LocalController;

public class LocalServiceBuilder {
    private LocalController localController;

    public LocalServiceBuilder setLocalController(LocalController localController) {
        this.localController = localController;
        return this;
    }

//    public LocalService createLocalService() {
//        return new LocalService(localController);
//    }
}