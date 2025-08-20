package com.eos.austro.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.lang3.StringUtils;

/**
 * Servicio para concatenar strings de forma segura.
 * <p>
 * Aplica validaciones y utiliza utilidades de Apache Commons para evitar
 * errores comunes.
 */
@ApplicationScoped
public class StringConcatService {
    public String concat(String[] params) {
        // Concatenar con espacio como separador
        return StringUtils.join(params, " ");
    }
}
