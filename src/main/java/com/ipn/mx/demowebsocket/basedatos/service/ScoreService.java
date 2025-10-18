package com.ipn.mx.demowebsocket.basedatos.service;

import org.springframework.transaction.annotation.Transactional;

public interface ScoreService {
    @Transactional
    void processImpact(Integer combateId, String petoGolpeado, double impact);

    void processImpact(Long combateId, String petoGolpeado, double impact);
}
