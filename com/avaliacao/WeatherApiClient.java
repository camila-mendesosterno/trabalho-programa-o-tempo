package com.avaliacao;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Cliente responsável pela comunicação com a API da Open-Meteo.
 * Monta a URL, realiza a requisição HTTP e faz o parsing da resposta JSON.
 */
public class WeatherApiClient {

    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";

    /**
     * Busca os dados climáticos para uma dada coordenada e período fazendo uma chamada HTTP real.
     * @return Uma string contendo o JSON da resposta da API.
     */
    public String getWeatherData(double latitude, double longitude, LocalDate startDate, LocalDate endDate) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        // Monta a URL com os parâmetros necessários
        String urlString = String.format("%s?latitude=%.4f&longitude=%.4f&start_date=%s&end_date=%s&hourly=temperature_2m",
                BASE_URL, latitude, longitude, startDate.format(formatter), endDate.format(formatter));

        // Garante que o formato de decimal na URL use ponto, não vírgula.
        urlString = urlString.replace(',', '.');

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        // Verifica se a requisição foi bem-sucedida
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode + " for URL " + urlString);
        } else {
            // Lê o corpo da resposta
            Scanner scanner = new Scanner(url.openStream());
            StringBuilder inline = new StringBuilder();
            while (scanner.hasNext()) {
                inline.append(scanner.nextLine());
            }
            scanner.close();
            return inline.toString();
        }
    }

    /**
     * Extrai a lista de temperaturas horárias do JSON recebido.
     * @param jsonData A string JSON completa da resposta da API.
     * @return Uma lista de temperaturas (Double).
     */
    public List<Double> parseTemperatures(String jsonData) {
        List<Double> temperatures = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonData);

        if (!jsonObject.has("hourly")) {
            System.err.println("Atenção: A resposta da API não contém dados 'hourly'. JSON: " + jsonData);
            return temperatures;
        }

        JSONObject hourly = jsonObject.getJSONObject("hourly");
        JSONArray tempArray = hourly.getJSONArray("temperature_2m");

        for (int i = 0; i < tempArray.length(); i++) {
            // Verifica se o valor na posição 'i' NÃO é nulo antes de adicioná-lo.
            if (!tempArray.isNull(i)) {
                temperatures.add(tempArray.getDouble(i));
            }
        }
        return temperatures;
    }
}