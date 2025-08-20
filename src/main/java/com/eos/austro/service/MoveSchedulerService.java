package com.eos.austro.service;

import com.eos.austro.client.PokeApiMoveClient;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.quarkus.scheduler.Scheduled;
import com.eos.austro.event.MoveFetchedEvent;

/**
 * Servicio programado que obtiene movimientos de la pokeapi periódicamente y
 * publica eventos en el bus.
 * <p>
 * Usa configuración externa (cron), es reactivo y desacoplado por eventos.
 */
@ApplicationScoped
public class MoveSchedulerService {

    @Inject
    @RestClient
    PokeApiMoveClient pokeApiMoveClient;

    @ConfigProperty(name = "app.scheduled.cron", defaultValue = "0 */5 * * * ?")
    String cron;

    @Inject
    Event<MoveFetchedEvent> moveFetchedEventBus;

    @Scheduled(cron = "{app.scheduled.cron}")
    void fetchMovesPeriodically() {
        pokeApiMoveClient.getMoves()
                .subscribe().with(
                        json -> {
                            Log.info("[SCHEDULER] Movimientos obtenidos: " + json);
                            moveFetchedEventBus.fireAsync(new MoveFetchedEvent(json));
                        },
                        failure -> Log.error("[SCHEDULER] Error al obtener movimientos: " + failure.getMessage()));
    }
}
