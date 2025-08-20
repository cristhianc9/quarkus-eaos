package com.eos.austro.event;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Evento para la concatenación de strings.
 * <p>
 * Contiene los parámetros originales y el resultado de la operación.
 */
@Data
@AllArgsConstructor
public class StringConcatEvent {
    private String[] params;
    private String result;
}
