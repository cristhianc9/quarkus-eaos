package com.eos.austro.client;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/move")
@RegisterRestClient(configKey = "pokeapi-client")
public interface PokeApiMoveClient {
    /**
     * Obtiene todos los movimientos.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    Uni<String> getMoves();

    /**
     * Obtiene un movimiento específico por ID numérico.
     * 
     * @param id ID del movimiento
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<String> getMoveById(@jakarta.ws.rs.PathParam("id") int id);
}
