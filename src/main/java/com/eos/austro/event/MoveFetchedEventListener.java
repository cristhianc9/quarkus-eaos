package com.eos.austro.event;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;

/**
 * Listener asíncrono para eventos de movimientos obtenidos.
 * <p>
 * Procesa el evento y permite extender la lógica (guardar, analizar, etc.).
 */
@ApplicationScoped
public class MoveFetchedEventListener {
    public void onMoveFetched(@ObservesAsync MoveFetchedEvent event) {
        Log.info("[EVENT BUS] Evento recibido con movimientos: " + event.getMovesJson());
        // Aquí se puede agregar lógica adicional (guardar, procesar, etc.)
    }
}
