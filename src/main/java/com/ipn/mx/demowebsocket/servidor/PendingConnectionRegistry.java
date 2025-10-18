package com.ipn.mx.demowebsocket.servidor;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class PendingConnectionRegistry {

    private final AtomicReference<Long> pendingRojoId = new AtomicReference<>();
    private final AtomicReference<Long> pendingAzulId = new AtomicReference<>();

    public void prepareForRojo(Long combateId) {
        this.pendingRojoId.set(combateId);
        System.out.println("SALA DE ESPERA: Preparado para recibir conexión ROJA para combate " + combateId);
    }

    public void prepareForAzul(Long combateId) {
        this.pendingAzulId.set(combateId);
        System.out.println("SALA DE ESPERA: Preparado para recibir conexión AZUL para combate " + combateId);
    }

    public Long claimRojoConnection() {
        // getAndSet es atómico: obtiene el valor actual y lo establece en null en una sola operación
        Long claimedId = this.pendingRojoId.getAndSet(null);
        if (claimedId != null) {
            System.out.println("SALA DE ESPERA: Conexión ROJA reclamada para combate " + claimedId);
        }
        return claimedId;
    }


    public Long claimAzulConnection() {
        Long claimedId = this.pendingAzulId.getAndSet(null);
        if (claimedId != null) {
            System.out.println("SALA DE ESPERA: Conexión AZUL reclamada para combate " + claimedId);
        }
        return claimedId;
    }
}