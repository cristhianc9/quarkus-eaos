package com.eos.austro.controller;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.eos.austro.client.PokeApiMoveClient;

/**
 * Endpoint para obtener movimientos desde pokeapi.co/api/v2/move de forma
 * reactiva.
 * <p>
 * <b>Arquitectura:</b> Reactivo, tolerancia a fallos, orientado a eventos.<br>
 * <b>Seguridad:</b> Propaga cabecera Authorization.<br>
 * <b>Perfil:</b> Dev/Prod puerto 15050, Test puerto 15055.
 */
@Tag(name = "Movimientos", description = "Obtención de movimientos desde pokeapi de forma reactiva y tolerante a fallos")
@Path("/api/v2/move")
@RequestScoped
public class MoveController {

    @Inject
    @RestClient
    PokeApiMoveClient pokeApiMoveClient;

    @Inject
    ManagedExecutor executor;

    /**
     * Obtiene movimientos desde pokeapi.co/api/v2/move de forma reactiva y
     * tolerante a fallos.
     * <p>
     * <b>Cabeceras:</b> Propaga Authorization si está presente.<br>
     * <b>Resiliencia:</b> Retry y CircuitBreaker configurados.<br>
     * <b>Respuesta:</b> JSON con movimientos o error 503 si el servicio externo
     * falla.
     *
     * @param authHeader Cabecera Authorization (opcional)
     * @return Lista de movimientos en formato JSON o error 503
     */
    @Operation(summary = "Obtiene movimientos desde pokeapi de forma reactiva", description = "Propaga cabecera Authorization, usa tolerancia a fallos y responde en JSON.")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 3, delay = 500)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 2000)
    public Uni<Response> getMoves(@HeaderParam("Authorization") String authHeader) {
        // Propagación de cabeceras y consumo no bloqueante
        return pokeApiMoveClient.getMoves()
                .onItem().transform(json -> {
                    Log.info("Respuesta de pokeapi.co/api/v2/move: " + json);
                    return Response.ok(json).build();
                })
                .onFailure().recoverWithItem(th -> {
                    Log.error("Error al consumir pokeapi: " + th.getMessage());
                    String errorJson = "{\"error\":\"Error externo\"}";
                    return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                            .type(MediaType.APPLICATION_JSON)
                            .entity(errorJson).build();
                });
    }

    /**
     * Obtiene un movimiento específico por ID numérico desde
     * pokeapi.co/api/v2/move/{id}.
     * <p>
     * <b>Resiliencia:</b> Retry y CircuitBreaker configurados.<br>
     * <b>Respuesta:</b> JSON con el movimiento o error 503 si el servicio externo
     * falla.
     *
     * @param id ID numérico del movimiento
     * @return Movimiento en formato JSON o error 503
     */
    @APIResponse(responseCode = "200", description = "Movimiento encontrado", content = @org.eclipse.microprofile.openapi.annotations.media.Content(mediaType = MediaType.APPLICATION_JSON, examples = @org.eclipse.microprofile.openapi.annotations.media.ExampleObject(name = "move", value = "{\"id\":1,\"name\":\"pound\",...}")))
    @APIResponse(responseCode = "503", description = "Error externo o movimiento no encontrado", content = @org.eclipse.microprofile.openapi.annotations.media.Content(mediaType = MediaType.APPLICATION_JSON, examples = @org.eclipse.microprofile.openapi.annotations.media.ExampleObject(name = "error", value = "{\"error\":\"Error externo\"}")))
    @Operation(summary = "Obtiene un movimiento específico por ID desde pokeapi", description = "Consulta reactiva y tolerante a fallos de un movimiento por ID.")
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 3, delay = 500)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 2000)
    public Uni<Response> getMoveById(@PathParam("id") int id) {
        return pokeApiMoveClient.getMoveById(id)
                .onItem().transform(json -> {
                    Log.info("Respuesta de pokeapi.co/api/v2/move/" + id + ": " + json);
                    return Response.ok(json).build();
                })
                .onFailure().recoverWithItem(th -> {
                    Log.error("Error al consumir pokeapi para id " + id + ": " + th.getMessage());
                    String errorJson = "{\"error\":\"Error externo\"}";
                    return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                            .type(MediaType.APPLICATION_JSON)
                            .entity(errorJson).build();
                });

    }
}