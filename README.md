# Análise de Performance: Threads em Java

### Projeto da Disciplina de Programação Concorrente e Distribuída

## Sobre o Projeto

Bem-vindo! Este repositório contém o projeto final da disciplina de **Programação Concorrente e Distribuída**. O desafio foi investigar na prática como a concorrência afeta a performance de uma aplicação Java.

Para isso, foi desenvolvido um sistema que busca dados climáticos das 27 capitais brasileiras para janeiro de 2024. O objetivo principal foi comparar o tempo de execução do sistema em quatro cenários diferentes para entender o impacto do paralelismo em tarefas limitadas por I/O (requisições de rede).

Os cenários testados foram:
* **Sequencial:** Utilizando apenas a thread principal (`main`).
* **Concorrente:** Utilizando pools de 3, 9 e 27 threads.

A análise completa, incluindo a fundamentação teórica e o gráfico comparativo, está disponível no arquivo `Relatorio.pdf`.

## Tecnologias Utilizadas

* **Linguagem:** Java 17
* **Dependências:** Biblioteca `org.json` para o processamento de dados em formato JSON.

## Como Executar

Para rodar este experimento em sua máquina, você vai precisar de:
* Java 17 (ou superior) instalado e configurado nas variáveis de ambiente.

Depois, siga os passos:

1.  Clone ou faça o download deste repositório.
2.  Abra seu terminal na pasta raiz do projeto.
3.  Certifique-se de que a pasta `com` e o arquivo `json-20240303.jar` estão presentes.
4.  Execute o comando correspondente ao seu sistema operacional:

* **Windows (Prompt de Comando/PowerShell):**
    ```cmd
    java -cp ".;json-20240303.jar" com.avaliacao.App
    ```

* **Linux ou macOS:**
    ```bash
    java -cp ".:json-20240303.jar" com.avaliacao.App
    ```

## Arquitetura do Projeto

O código foi modularizado para separar as responsabilidades e facilitar a manutenção:

* **`App.class`**: O ponto de entrada da aplicação. É o "maestro" que rege a execução dos quatro cenários de teste, controlando as 10 rodadas e exibindo os resultados consolidados.
* **`ThreadedExperiment.class` / `NoThreadExperiment.class`**: O coração da lógica concorrente e sequencial. A versão `Threaded` gerencia um `ExecutorService` para distribuir as 27 tarefas entre as threads disponíveis, enquanto a `NoThread` executa uma tarefa após a outra.
* **`WeatherApiClient.class`**: A ponte de comunicação com o mundo exterior. Esta classe é responsável por montar e executar as requisições HTTP para a API da Open-Meteo.
* **`WeatherDataProcessor.class`**: O "cérebro" analítico. Recebe os dados brutos em JSON e os transforma nas informações necessárias: as temperaturas média, mínima e máxima de cada dia.
* **`CapitalsData.class`**: Nosso banco de dados embutido. Fornece a lista de capitais com suas latitudes e longitudes para as requisições.
* **`ResultStore.class`**: Um cofre de dados thread-safe que armazena os resultados finais de cada capital, permitindo que múltiplas threads escrevam nele sem conflitos.

## Resultados e Análise

A execução do experimento demonstrou um ganho de performance massivo com o uso de concorrência. A abordagem sequencial, limitada a esperar uma requisição terminar para começar a próxima, foi de longe a mais lenta.

A tabela abaixo resume o **"Speedup"** (ganho de performance) obtido em cada cenário, comparado à versão de referência:

| Versão do Experimento | Nº de Threads | Tempo Médio (ms) | Ganho de Performance (Speedup) |
| :--- | :--- | :--- | :--- |
| **Sequencial (Referência)**| 1 | 2.728,80 | 1,00x |
| **Concorrente** | 3 | 975,00 | **2,80x** |
| **Concorrente** | 9 | 376,40 | **7,25x** |
| **Concorrente** | 27 | 152,90 | **17,85x** |

A versão com 27 threads se mostrou **quase 18 vezes mais rápida** que a abordagem sequencial, comprovando a eficácia do paralelismo em tarefas de I/O.

## Autor

* **Camila Mendes Osterno** - Matrícula UC23421910
