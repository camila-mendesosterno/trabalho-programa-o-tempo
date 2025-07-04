package com.avaliacao;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Implementação do experimento na versão concorrente.
 * Utiliza um pool de threads para processar as capitais em paralelo.
 */
public class ThreadedExperiment extends ExperimentRunner {

    private final int numThreads;

    public ThreadedExperiment(List<CapitalsData.Capital> capitals, ResultStore resultStore, LocalDate startDate, LocalDate endDate, int numThreads) {
        super(capitals, resultStore, startDate, endDate);
        this.numThreads = numThreads;
    }

    /**
     * Executa o experimento distribuindo as tarefas de processamento de capitais
     * entre um número definido de threads.
     */
    @Override
    public void runExperiment() throws InterruptedException {
        // Cria um pool de threads com um número fixo.
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Para cada capital, submete uma nova tarefa para o pool de threads.
        for (CapitalsData.Capital capital : capitals) {
            executor.submit(() -> {
                try {
                    fetchDataAndProcess(capital);
                } catch (Exception e) {
                    System.err.println("Erro ao processar capital: " + capital.getName() + ": " + e.getMessage());
                }
            });
        }

        // Inicia o processo de desligamento do executor. Nenhuma nova tarefa será aceita.
        executor.shutdown();
        // Aguarda a finalização de todas as tarefas submetidas por até 1 hora.
        // Isso é crucial para garantir que a medição do tempo só termine após todo o trabalho ser concluído.
        executor.awaitTermination(1, TimeUnit.HOURS);
    }
}