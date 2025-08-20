package com.eos.austro.event;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Evento publicado cuando se obtienen movimientos desde la pokeapi.
 * <p>
 * Contiene el JSON crudo de la respuesta para ser procesado por listeners.
 */
@Data
@AllArgsConstructor
public class MoveFetchedEvent {
    private String movesJson;
}
