package com.eos.austro.controller;

import com.eos.austro.service.StringConcatService;
import com.eos.austro.event.StringConcatEvent;
import io.quarkus.logging.Log;
import io.smallrye.common.constraint.NotNull;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Endpoint para concatenar 5 strings recibidos como text/plain, validando
 * entradas y usando EventBus bloqueante.
 * <p>
 * <b>Seguridad:</b> Valida caracteres y patrones para evitar inyección SQL.<br>
 * <b>Arquitectura:</b> Reactivo, orientado a eventos, desacoplado por
 * EventBus.<br>
 * <b>Perfil:</b> Dev/Prod puerto 15050, Test puerto 15055.
 */
@Tag(name = "Concatenación", description = "Concatenación segura de strings con validación y EventBus")
@Path("/api/v1/test")
public class StringConcatController {

    @Inject
    StringConcatService stringConcatService;

    @Inject
    Event<StringConcatEvent> eventBus;

    /**
     * Recibe 5 parámetros tipo String como path params, valida y retorna la
     * concatenación.
     * <p>
     * <b>Ejemplo:</b> <code>/api/v1/test/uno/dos/tres/cuatro/cinco</code><br>
     * <b>Validación:</b> No permite caracteres ni patrones típicos de inyección
     * SQL.<br>
     * <b>Seguridad:</b> Responde 400 si algún parámetro es inválido.
     *
     * @param p1 Primer string
     * @param p2 Segundo string
     * @param p3 Tercer string
     * @param p4 Cuarto string
     * @param p5 Quinto string
     * @return Concatenación de los 5 strings o error 400
     */
    @Operation(summary = "Concatena 5 strings recibidos como path params", description = "Valida caracteres inseguros y usa EventBus para publicar el evento de concatenación.")
    @POST
    @Path("/{p1}/{p2}/{p3}/{p4}/{p5}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response concatStrings(
            @NotNull @PathParam("p1") String p1,
            @NotNull @PathParam("p2") String p2,
            @NotNull @PathParam("p3") String p3,
            @NotNull @PathParam("p4") String p4,
            @NotNull @PathParam("p5") String p5) {
        String[] params = { p1, p2, p3, p4, p5 };
        // Bloquea caracteres y patrones típicos de inyección SQL
        String sqlInjectionPattern = ".*[;\'\"%#=|&$<>\\(\\)\\{\\}*].*";
        for (String param : params) {
            if (StringUtils.isBlank(param)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Ningún parámetro puede ser nulo, vacío o solo espacios.").build();
            }
            // Rechazar patrones típicos de inyección SQL
            if (param.matches(sqlInjectionPattern)
                    || param.contains("--")
                    || param.contains("/*")
                    || param.contains("*/")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Parámetro inválido detectado por seguridad (caracteres o patrones no permitidos).")
                        .build();
            }
        }
        String result = stringConcatService.concat(params);
        // Publicar evento bloqueante
        eventBus.fire(new StringConcatEvent(params, result));
        return Response.ok(result).build();
    }
}
