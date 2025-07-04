package com.avaliacao;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Armazena os resultados de todos os experimentos.
 * Utiliza um ConcurrentHashMap para garantir que o acesso seja thread-safe,
 * permitindo que múltiplas threads adicionem dados sem conflitos.
 */
public class ResultStore {
    
    private final Map<String, Map<LocalDate, WeatherDataProcessor.DailyStats>> allCapitalsData;

    public ResultStore() {
        // Usa ConcurrentHashMap para segurança em ambientes concorrentes
        this.allCapitalsData = new ConcurrentHashMap<>();
    }

    /**
     * Adiciona os dados processados de uma capital ao mapa de resultados.
     * @param capitalName O nome da capital.
     * @param data O mapa contendo os dados diários (stats) da capital.
     */
    public void addCapitalData(String capitalName, Map<LocalDate, WeatherDataProcessor.DailyStats> data) {
        this.allCapitalsData.put(capitalName, data);
    }

    /**
     * Retorna uma visão não modificável de todos os dados armazenados.
     * @return Um mapa com todos os dados das capitais.
     */
    public Map<String, Map<LocalDate, WeatherDataProcessor.DailyStats>> getAllCapitalsData() {
        return Collections.unmodifiableMap(this.allCapitalsData);
    }
    
    /**
     * Limpa todos os dados armazenados, útil para iniciar uma nova rodada de testes.
     */
    public void clear() {
        this.allCapitalsData.clear();
    }
}