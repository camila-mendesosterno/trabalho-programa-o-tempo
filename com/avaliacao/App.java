package com.avaliacao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe principal da aplicação.
 * Responsável por orquestrar e executar os quatro cenários de experimento
 * para análise de performance de threads.
 */
public class App {

    // Constante que define o número de repetições para cada cenário de teste
    private static final int NUM_REPETITIONS = 10;
    // Constantes para o período de busca dos dados climáticos (Janeiro de 2024)
    private static final LocalDate START_DATE = LocalDate.of(2024, 1, 1);
    private static final LocalDate END_DATE = LocalDate.of(2024, 1, 31);

    /**
     * Ponto de entrada do programa.
     * @param args Argumentos da linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        // Carrega a lista de capitais a serem processadas
        List<CapitalsData.Capital> capitals = CapitalsData.getCapitals();
        // Cria uma instância para armazenar os resultados
        ResultStore resultStore = new ResultStore();

        // --- Cenário 1: Versão Sequencial (Sem Threads) ---
        System.out.println("--- Iniciando Experimento: Versão Sem Threads ---");
        NoThreadExperiment noThreadExperiment = new NoThreadExperiment(capitals, resultStore, START_DATE, END_DATE);
        runExperimentSet(noThreadExperiment);

        // --- Cenários 2, 3 e 4: Versões Concorrentes ---
        int[] threadCounts = {3, 9, 27};
        for (int numThreads : threadCounts) {
            System.out.printf("--- Iniciando Experimento: Versão com %d Threads ---\n", numThreads);
            ThreadedExperiment threadedExperiment = new ThreadedExperiment(capitals, resultStore, START_DATE, END_DATE, numThreads);
            runExperimentSet(threadedExperiment);
        }
    }

    /**
     * Executa um conjunto de testes (10 rodadas) para um determinado tipo de experimento.
     * @param runner A implementação do experimento a ser executado (com ou sem threads).
     */
    private static void runExperimentSet(ExperimentRunner runner) {
        List<Long> executionTimes = new ArrayList<>();
        ResultStore resultStore = runner.getResultStore();

        // Loop para executar o experimento 10 vezes e coletar os tempos
        for (int i = 1; i <= NUM_REPETITIONS; i++) {
            System.out.printf("Rodada %d/%d...\n", i, NUM_REPETITIONS);
            resultStore.clear(); // Limpa os resultados da rodada anterior
            try {
                long time = runner.runAndMeasureTime();
                executionTimes.add(time);
                System.out.printf("Rodada %d concluída em %d ms.\n", i, time);
            } catch (Exception e) {
                System.err.println("Erro durante a execução do experimento: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Calcula a média dos tempos de execução
        double averageTime = executionTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        System.out.printf("\nTempo médio de execução (%d rodadas): %.2f ms\n\n", NUM_REPETITIONS, averageTime);
        
        // Exibe os resultados detalhados da última rodada
        runner.displayResults();
    }
}