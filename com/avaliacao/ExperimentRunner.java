package com.avaliacao;

import java.time.LocalDate;
import java.util.LinkedHashMap; // <-- LINHA ADICIONADA
import java.util.List;
import java.util.Map;

/**
 * Classe base abstrata que define a estrutura comum para todos os tipos de experimentos.
 * Centraliza a lógica de medição de tempo e processamento de dados.
 */
public abstract class ExperimentRunner {

    protected final List<CapitalsData.Capital> capitals;
    protected final ResultStore resultStore;
    protected final LocalDate startDate;
    protected final LocalDate endDate;

    // Instâncias dos clientes de API e processadores para serem usados pelas subclasses
    private final WeatherApiClient weatherApiClient = new WeatherApiClient();
    private final WeatherDataProcessor dataProcessor = new WeatherDataProcessor();

    public ExperimentRunner(List<CapitalsData.Capital> capitals, ResultStore resultStore, LocalDate startDate, LocalDate endDate) {
        this.capitals = capitals;
        this.resultStore = resultStore;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    /**
     * Método abstrato que deve ser implementado por cada tipo de experimento (sequencial ou concorrente).
     */
    public abstract void runExperiment() throws Exception;

    /**
     * Mede o tempo de execução do método runExperiment.
     * @return O tempo total de execução em milissegundos.
     */
    public long runAndMeasureTime() throws Exception {
        long startTime = System.currentTimeMillis();
        runExperiment();
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    /**
     * Orquestra o fluxo de dados para uma única capital: busca, processa e armazena os dados.
     * @param capital A capital a ser processada.
     */
    protected void fetchDataAndProcess(CapitalsData.Capital capital) throws Exception {
        // 1. Busca os dados climáticos da API
        String jsonData = weatherApiClient.getWeatherData(capital.getLatitude(), capital.getLongitude(), startDate, endDate);
        // 2. Extrai la lista de temperaturas do JSON
        List<Double> temperatures = weatherApiClient.parseTemperatures(jsonData);
        // 3. Calcula as estatísticas diárias (min, max, média)
        Map<LocalDate, WeatherDataProcessor.DailyStats> dailyStats = dataProcessor.processDailyTemperatures(temperatures, startDate);
        // 4. Armazena o resultado
        resultStore.addCapitalData(capital.getName(), dailyStats);
    }
    
    public ResultStore getResultStore() {
        return this.resultStore;
    }
    
    /**
     * Exibe os resultados finais armazenados no ResultStore de forma formatada no console.
     */
    public void displayResults() {
        System.out.println("\n--- Resultados do Experimento ---");
        Map<String, Map<LocalDate, WeatherDataProcessor.DailyStats>> allData = resultStore.getAllCapitalsData();

        for (Map.Entry<String, Map<LocalDate, WeatherDataProcessor.DailyStats>> capitalEntry : allData.entrySet()) {
            System.out.println("Capital: " + capitalEntry.getKey());
            // A linha abaixo usa LinkedHashMap para manter a ordem, por isso o import é necessário.
            Map<LocalDate, WeatherDataProcessor.DailyStats> sortedDailyData = new LinkedHashMap<>(capitalEntry.getValue());
            
            for (Map.Entry<LocalDate, WeatherDataProcessor.DailyStats> dailyEntry : sortedDailyData.entrySet()) {
                WeatherDataProcessor.DailyStats stats = dailyEntry.getValue();
                System.out.printf("  Data: %s, Média: %.2f, Mínima: %.2f, Máxima: %.2f\n",
                        dailyEntry.getKey(), stats.getMean(), stats.getMin(), stats.getMax());
            }
        }
        System.out.println("----------------------------------");
    }
}