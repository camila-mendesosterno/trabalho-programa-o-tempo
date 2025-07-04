package com.avaliacao;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementação do experimento na versão sequencial (sem threads).
 * Processa as 27 capitais uma após a outra na thread principal.
 */
public class NoThreadExperiment extends ExperimentRunner {

    public NoThreadExperiment(List<CapitalsData.Capital> capitals, ResultStore resultStore, LocalDate startDate, LocalDate endDate) {
        super(capitals, resultStore, startDate, endDate);
    }

    /**
     * Executa o experimento iterando sobre a lista de capitais e processando cada uma sequencialmente.
     */
    @Override
    public void runExperiment() throws Exception {
        for (CapitalsData.Capital capital : capitals) {
            fetchDataAndProcess(capital);
        }
    }
}