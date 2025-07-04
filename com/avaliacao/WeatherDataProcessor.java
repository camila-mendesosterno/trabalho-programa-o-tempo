package com.avaliacao;

import java.time.LocalDate;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe responsável por processar os dados climáticos brutos,
 * calculando as estatísticas diárias.
 */
public class WeatherDataProcessor {

    /**
     * Classe interna para armazenar as estatísticas (mínima, máxima, média) de um dia.
     */
    public static class DailyStats {
        private final double min;
        private final double max;
        private final double mean;

        public DailyStats(double min, double max, double mean) {
            this.min = min;
            this.max = max;
            this.mean = mean;
        }

        public double getMin() { return min; }
        public double getMax() { return max; }
        public double getMean() { return mean; }
    }

    /**
     * Recebe uma lista de temperaturas horárias e agrupa em estatísticas diárias.
     * @param hourlyTemperatures Lista com todas as temperaturas do período.
     * @param startDate A data de início, para calcular as datas corretas.
     * @return Um mapa que associa cada data às suas estatísticas diárias.
     */
    public Map<LocalDate, DailyStats> processDailyTemperatures(List<Double> hourlyTemperatures, LocalDate startDate) {
        Map<LocalDate, DailyStats> dailyStatsMap = new LinkedHashMap<>();

        // *** VERIFICAÇÃO ADICIONADA AQUI ***
        // Se a lista de temperaturas estiver vazia (devido a uma falha na API, por exemplo),
        // retorna o mapa vazio imediatamente para evitar erros no processamento.
        if (hourlyTemperatures == null || hourlyTemperatures.isEmpty()) {
            System.err.println("Atenção: Lista de temperaturas vazia recebida para processamento. Pulando esta capital.");
            return dailyStatsMap;
        }
        
        int days = (int) startDate.lengthOfMonth();

        // Itera dia a dia sobre o mês de Janeiro
        for (int day = 0; day < days; day++) {
            // Separa as 24 leituras de temperatura para o dia atual
            int start = day * 24;
            int end = start + 24;
            
            // Garante que não vamos tentar acessar um índice fora da lista
            if (start >= hourlyTemperatures.size()) {
                break; // Se não há mais dados, para o loop
            }
            if (end > hourlyTemperatures.size()) {
                end = hourlyTemperatures.size();
            }

            List<Double> dayTemperatures = hourlyTemperatures.subList(start, end);
            
            if (dayTemperatures.isEmpty()) {
                continue; // Pula para o próximo dia se não houver dados para o dia atual
            }
            
            // Usa a classe DoubleSummaryStatistics para calcular min, max e média de forma eficiente
            DoubleSummaryStatistics stats = dayTemperatures.stream()
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();
            
            LocalDate currentDate = startDate.plusDays(day);
            dailyStatsMap.put(currentDate, new DailyStats(stats.getMin(), stats.getMax(), stats.getAverage()));
        }
        return dailyStatsMap;
    }
}