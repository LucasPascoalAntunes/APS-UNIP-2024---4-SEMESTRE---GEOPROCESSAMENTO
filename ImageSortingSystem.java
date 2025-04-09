import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.text.SimpleDateFormat;
import java.util.concurrent.*;
import java.util.HashMap;
import java.util.Map;


public class ImageSortingSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continueProgram = true;

        while (continueProgram) {
            System.out.println("\nSelecione uma opção:");
            System.out.println("1 - Exibir explicação do código");
            System.out.println("2 - Executar o programa de ordenação");
            System.out.println("3 - Gerar arquivo de dados para testes");
            System.out.println("4 - Realizar teste de escalabilidade");
            System.out.println("5 - Gerar dados complexos personalizados");
            System.out.println("6 - Demonstração de ordenação com 1000 dados");
            System.out.println("7 - Comparar execução entre dois conjuntos de dados");
            System.out.println("8 - Testar algoritmo hibrido");
            System.out.println("0 - Sair");

            int initialChoice = getIntInput(scanner, "Escolha uma opção: ");
            switch (initialChoice) {
                case 1 -> explainProgram();
                case 2 -> executeSortingProgram(scanner);
                case 3 -> generateTestDataFile(scanner);
                case 4 -> performScalabilityTest(scanner);
                case 5 -> generateComplexTestData(scanner);
                case 6 -> demonstrateSort();
                case 7 -> compareTwoDatasets(scanner);
                case 8 -> executeHybridAlgorithm(scanner);
                case 0 -> continueProgram = false;
                default -> System.out.println("Opção inválida.");
            }
        }
        System.out.println("Programa encerrado.");
        scanner.close();
    }

    private static void executeHybridAlgorithm(Scanner scanner) {
        System.out.println("\n--- Adaptação de Algoritmos Híbridos ---");
        System.out.println("Escolha o algoritmo híbrido para executar:");
        System.out.println("1 - IntroSort");
        System.out.println("2 - TimSort");
        System.out.println("3 - Dual-Pivot QuickSort");
        System.out.println("0 - Voltar ao menu principal");

        int choice = getIntInput(scanner, "Escolha uma opção: ");
        if (choice == 0) return;

        SortingAlgorithm hybridAlgorithm;
        switch (choice) {
            case 1 -> hybridAlgorithm = new IntroSort();
            case 2 -> hybridAlgorithm = new TimSort();
            case 3 -> hybridAlgorithm = new DualPivotQuickSort();
            default -> {
                System.out.println("Opção inválida.");
                return;
            }
        }

        String[] data = handleInternalData(scanner);
        if (data == null) return;

        AtomicBoolean cancelFlag = new AtomicBoolean(false);
        Statistics stats = new Statistics();

        System.out.println("\nExecutando " + hybridAlgorithm.getClass().getSimpleName() + "...");
        ProgressUtils.initializeProgress(hybridAlgorithm.getClass().getSimpleName());

        double time = Timer.measureTimeMs(() -> hybridAlgorithm.sort(data, cancelFlag, stats));

        System.out.println("\nOrdenação concluída.");
        System.out.printf("Tempo de execução: %.2f ms\n", time);
        System.out.printf("Comparações: %d\n", stats.getComparisons());
        System.out.printf("Trocas: %d\n", stats.getSwaps());

        saveDetailedReport(hybridAlgorithm, stats, time);
    }


    private static void saveDetailedReport(SortingAlgorithm algorithm, Statistics stats, double time) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File reportFile = new File("output_data", algorithm.getClass().getSimpleName() + "_report_" + timestamp + ".txt");

        try (PrintWriter writer = new PrintWriter(new FileWriter(reportFile))) {
            writer.printf("Algoritmo: %s\n", algorithm.getClass().getSimpleName());
            writer.printf("Tempo de execução: %.2f ms\n", time);
            writer.printf("Comparações: %d\n", stats.getComparisons());
            writer.printf("Trocas: %d\n", stats.getSwaps());
            writer.println("Relatório salvo em: " + reportFile.getAbsolutePath());
            System.out.println("Relatório detalhado salvo em: " + reportFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Erro ao salvar o relatório: " + e.getMessage());
        }
    }


    private static void compareTwoDatasets(Scanner scanner) {
        System.out.println("\n--- Comparação entre Dois Conjuntos de Dados ---");


        String[] data1 = selectDataSource(scanner, "Primeiro");
        if (data1 == null) return;


        String[] data2 = selectDataSource(scanner, "Segundo");
        if (data2 == null) return;


        System.out.println("\nEscolha o algoritmo de ordenação para a comparação:");
        System.out.println("1 - QuickSort");
        System.out.println("2 - MergeSort");
        System.out.println("3 - HeapSort");
        int algorithmChoice = getIntInput(scanner, "Escolha uma opção: ");

        SortingAlgorithm algorithm;
        switch (algorithmChoice) {
            case 1:
                algorithm = new QuickSort();
                break;
            case 2:
                algorithm = new MergeSort();
                break;
            case 3:
                algorithm = new HeapSort();
                break;
            default:
                System.out.println("Opção inválida. Operação cancelada.");
                return;
        }


        AtomicBoolean cancelFlag = new AtomicBoolean(false);
        Statistics stats1 = new Statistics();
        Statistics stats2 = new Statistics();

        System.out.println("\nExecutando ordenação no primeiro conjunto de dados...");
        double time1 = Timer.measureTimeMs(() -> algorithm.sort(data1, cancelFlag, stats1));

        System.out.println("Executando ordenação no segundo conjunto de dados...");
        double time2 = Timer.measureTimeMs(() -> algorithm.sort(data2, cancelFlag, stats2));


        displayComparisonResults(time1, stats1, time2, stats2);
    }



    private static void demonstrateSort() {
        System.out.println("\n--- Demonstração de Ordenação (1000 Dados) ---");
        String[] data = DataLoader.generateRandomData(1000);
        AtomicBoolean cancelFlag = new AtomicBoolean(false);
        Statistics stats = new Statistics();
        SortingAlgorithm algorithm = new QuickSort(true);

        System.out.println("Executando ordenação QuickSort com feedback em tempo real:");
        algorithm.sort(data, cancelFlag, stats);

        System.out.println("\nOrdenação concluída.");
        System.out.printf("Comparações totais: %d, Trocas totais: %d\n", stats.getComparisons(), stats.getSwaps());
        System.out.println("Dados ordenados:");
        for (String value : data) {
            System.out.print(value + " ");
        }
        System.out.println("\n");
    }

    private static boolean isValidPercentageDistribution(Map<String, Integer> percentages) {
        return percentages.values().stream().mapToInt(Integer::intValue).sum() != 100;
    }

    private static String[] selectDataSource(Scanner scanner, String datasetLabel) {
        System.out.printf("\nSelecione a fonte para o %s conjunto de dados:\n", datasetLabel);
        System.out.println("1 - Dados de um arquivo");
        System.out.println("2 - Dados gerados internamente");
        int choice = getIntInput(scanner, "Escolha uma opção: ");

        if (choice == 1) {
            try {
                return handleExternalData(scanner);
            } catch (IOException e) {
                System.out.println("Erro ao carregar o arquivo: " + e.getMessage());
                return null;
            }
        } else if (choice == 2) {
            return handleInternalData(scanner);
        } else {
            System.out.println("Opção inválida.");
            return null;
        }
    }

    private static void displayComparisonResults(double time1, Statistics stats1, double time2, Statistics stats2) {
        System.out.println("\n===== Resultados da Comparação =====");

        System.out.println("Primeiro conjunto de dados:");
        System.out.printf("Tempo de execução: %.2f ms\n", time1);
        System.out.printf("Comparações: %d\n", stats1.getComparisons());
        System.out.printf("Trocas: %d\n", stats1.getSwaps());

        System.out.println("\nSegundo conjunto de dados:");
        System.out.printf("Tempo de execução: %.2f ms\n", time2);
        System.out.printf("Comparações: %d\n", stats2.getComparisons());
        System.out.printf("Trocas: %d\n", stats2.getSwaps());

        System.out.println("\nDiferença entre os conjuntos:");
        System.out.printf("Diferença no tempo de execução: %.2f ms\n", Math.abs(time1 - time2));
        System.out.printf("Diferença nas comparações: %d\n", Math.abs(stats1.getComparisons() - stats2.getComparisons()));
        System.out.printf("Diferença nas trocas: %d\n", Math.abs(stats1.getSwaps() - stats2.getSwaps()));
    }


    private static void generateComplexTestData(Scanner scanner) {
        System.out.println("\n--- Geração de Dados Complexos Personalizados ---");
        System.out.println("Você pode definir a porcentagem de distribuição dos dados para cada estado e cada status.");
        System.out.println("Certifique-se de que a soma das porcentagens para os estados e para os status seja 100%.");


        System.out.println("\nEscolha o tipo de ordenação para os dados:");
        System.out.println("1 - Dados não ordenados");
        System.out.println("2 - Dados semi-ordenados");
        System.out.println("3 - Dados totalmente ordenados");
        int orderChoice = getIntInput(scanner, "Escolha uma opção: ");


        System.out.println("\nEstados disponíveis:");
        System.out.println("1. Amazonas");
        System.out.println("2. Pará");
        System.out.println("3. Mato Grosso");
        System.out.println("4. Rondônia");
        System.out.println("5. Maranhão");

        System.out.println("\nStatus disponíveis:");
        System.out.println("1. Preservado");
        System.out.println("2. Queimado");
        System.out.println("3. Desmatado");
        System.out.println("\nPor favor, insira a porcentagem para cada um.");

        System.out.print("\nInforme o nome para o arquivo de dados complexos: ");
        String fileName = scanner.nextLine().trim();

        int size = getIntInput(scanner, "Digite o número de elementos para o arquivo (máximo 10.000.000): ");
        if (size > 10_000_000) {
            System.out.println("O valor excede o limite. Tente novamente.");
            return;
        }


        Map<String, Integer> statePercentages = new HashMap<>();
        statePercentages.put("Amazonas", getIntInput(scanner, "Percentual para Amazonas: "));
        statePercentages.put("Pará", getIntInput(scanner, "Percentual para Pará: "));
        statePercentages.put("Mato Grosso", getIntInput(scanner, "Percentual para Mato Grosso: "));
        statePercentages.put("Rondônia", getIntInput(scanner, "Percentual para Rondônia: "));
        statePercentages.put("Maranhão", getIntInput(scanner, "Percentual para Maranhão: "));

        Map<String, Integer> statusPercentages = new HashMap<>();
        statusPercentages.put("Preservado", getIntInput(scanner, "Percentual para Preservado: "));
        statusPercentages.put("Queimado", getIntInput(scanner, "Percentual para Queimado: "));
        statusPercentages.put("Desmatado", getIntInput(scanner, "Percentual para Desmatado: "));


        if (isValidPercentageDistribution(statePercentages)) {
            System.out.println("Erro: A soma das porcentagens para os estados deve ser 100%. Tente novamente.");
            return;
        }
        if (isValidPercentageDistribution(statusPercentages)) {
            System.out.println("Erro: A soma das porcentagens para os status deve ser 100%. Tente novamente.");
            return;
        }


        String[] data = DataLoader.generateComplexData(size, statePercentages, statusPercentages);


        switch (orderChoice) {
            case 1 -> shuffleData(data);
            case 2 -> partiallySortData(data);
            case 3 -> {
                MergeSort mergeSort = new MergeSort();
                mergeSort.sort(data, new AtomicBoolean(false), new Statistics());
            }
            default -> System.out.println("Opção inválida para ordenação. Dados serão gerados de forma não ordenada.");
        }

        try {
            DataLoader.saveDataToFile(fileName, data);
            System.out.println("Arquivo de dados complexos gerado com sucesso.");
        } catch (IOException e) {
            System.out.println("Erro ao salvar o arquivo: " + e.getMessage());
        }
    }



    private static void shuffleData(String[] data) {
        List<String> dataList = Arrays.asList(data);
        Collections.shuffle(dataList);
        dataList.toArray(data);
    }


    private static void partiallySortData(String[] data) {
        int partitionSize = data.length / 5;
        for (int i = 0; i < data.length; i += partitionSize) {
            int end = Math.min(i + partitionSize, data.length);
            Arrays.sort(data, i, end);
        }
    }



    private static void explainProgram() {
        System.out.println("\nEste programa é um sistema de ordenação de dados que oferece diversas funcionalidades para trabalhar com diferentes algoritmos de ordenação, incluindo QuickSort, MergeSort e HeapSort.");
        System.out.println("Os usuários podem escolher entre carregar dados externos de um arquivo ou gerar dados aleatórios internamente, possibilitando a criação de amostras personalizadas para análise.");
        System.out.println("Além da ordenação, o programa calcula e exibe métricas de desempenho detalhadas, como tempo médio de execução, desvio padrão, número de comparações e trocas realizadas por cada algoritmo.");
        System.out.println("Os resultados de desempenho são salvos em um arquivo de relatório para consulta e análise futura.");
        System.out.println("O sistema também permite realizar testes de escalabilidade, avaliando o desempenho dos algoritmos em diferentes tamanhos de dados para identificar o mais eficiente em várias condições.");
        System.out.println("Adicionalmente, há uma funcionalidade de catalogação por estado, que organiza e apresenta a distribuição dos dados por regiões e status específicos, gerando relatórios detalhados.");
        System.out.println("Esse programa é ideal para quem deseja comparar o desempenho de algoritmos de ordenação em diferentes cenários, analisando a eficiência e estabilidade de cada um.");
    }

    private static int getIntInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, insira um número.");
            }
        }
    }

    private static void generateTestDataFile(Scanner scanner) {
        System.out.print("\nInforme o nome para o arquivo de dados de teste: ");
        String fileName = scanner.nextLine().trim();

        int size = getIntInput(scanner, "Digite o número de elementos para o arquivo (máximo 10.000.000): ");
        if (size > 10_000_000) {
            System.out.println("O valor excede o limite. Tente novamente.");
            return;
        }

        System.out.println("Gerando dados aleatórios e salvando no arquivo...");
        String[] data = DataLoader.generateRandomData(size);
        try {
            DataLoader.saveDataToFile(fileName, data);
            System.out.println("Arquivo de dados gerado com sucesso.");
        } catch (IOException e) {
            System.out.println("Erro ao salvar o arquivo: " + e.getMessage());
        }
    }

    private static void executeSortingProgram(Scanner scanner) {
        try {
            System.out.print("\nInforme um nome para a amostragem: ");
            String sampleName = scanner.nextLine().trim();

            System.out.println("\nSelecione o tipo de execução:");
            System.out.println("1 - Execução sequencial");
            System.out.println("2 - Execução paralela");
            System.out.println("0 - Voltar ao menu principal");

            int executionMode = getIntInput(scanner, "Escolha uma opção: ");
            if (executionMode == 0) return;

            boolean isParallel = executionMode == 2;

            System.out.println("\nSelecione o tipo de dados:");
            System.out.println("1 - Dados externos (de um arquivo)");
            System.out.println("2 - Dados internos (aleatórios)");
            System.out.println("0 - Voltar ao menu principal");

            int choice = getIntInput(scanner, "Escolha uma opção: ");
            if (choice == 0) return;

            String[] data;
            if (choice == 1) {
                data = handleExternalData(scanner);
            } else {
                data = handleInternalData(scanner);
            }

            if (data == null) return;

            AtomicBoolean cancelFlag = new AtomicBoolean(false);
            System.out.println("\nIniciando execução do programa de ordenação.\n");

            SortingManager sortingManager = new SortingManager(data, cancelFlag, sampleName);

            if (isParallel) {
                sortingManager.executeSortingAlgorithmsInParallel();
            } else {
                sortingManager.executeSortingAlgorithms();
            }

            sortingManager.savePerformanceReport();

            catalogByState(data, sampleName);

            System.out.println("\nExecução do programa de ordenação concluída. Retornando ao menu principal...\n");

        } catch (IOException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }


    private static void performScalabilityTest(Scanner scanner) {
        System.out.println("\nIniciando teste de escalabilidade...");
        System.out.println("Selecione o intervalo de teste de escalabilidade:");
        System.out.println("1 - 100 a 10.000");
        System.out.println("2 - 100 a 100.000");
        System.out.println("3 - 100 a 1.000.000");
        System.out.println("4 - 100 a 10.000.000");
        System.out.println("0 - Voltar ao menu principal");

        int choice = getIntInput(scanner, "Escolha uma opção: ");
        int endSize;

        switch (choice) {
            case 1 -> endSize = 10_000;
            case 2 -> endSize = 100_000;
            case 3 -> endSize = 1_000_000;
            case 4 -> endSize = 10_000_000;
            case 0 -> {
                return;
            }
            default -> {
                System.out.println("Opção inválida.");
                return;
            }
        }

        List<Integer> sizes = new ArrayList<>();
        for (int size = 100; size <= endSize; size *= 10) {
            sizes.add(size);
        }

        Map<Integer, String> bestAlgorithms = new HashMap<>();
        Map<Integer, Map<String, Double>> performanceData = new HashMap<>();

        for (int size : sizes) {
            System.out.printf("Gerando dados para %d elementos...\n", size);
            String[] data = DataLoader.generateRandomData(size);
            AtomicBoolean cancelFlag = new AtomicBoolean(false);

            SortingManager sortingManager = new SortingManager(data, cancelFlag, "ScalabilityTest_" + size);
            System.out.printf("Executando algoritmos de ordenação para %d elementos...\n", size);

            sortingManager.executeSortingAlgorithms();
            bestAlgorithms.put(size, sortingManager.getBestAlgorithm());
            performanceData.put(size, sortingManager.getPerformanceData());
            sortingManager.savePerformanceReport();
        }

        displayScalabilityReport(bestAlgorithms, performanceData);
    }

    private static double calculateFinalScore(String algorithm, Map<String, Double> totalExecutionTime, Map<String, Long> totalComparisons, Map<String, Long> totalSwaps) {
        double minBaseline = 30.0;

        double time = totalExecutionTime.get(algorithm);
        long comparisons = totalComparisons.getOrDefault(algorithm, 0L);
        long swaps = totalSwaps.getOrDefault(algorithm, 0L);

        double timeScore = calculateScore(time, minBaseline, calculateMinMax(totalExecutionTime));
        double comparisonScore = calculateScore(comparisons, minBaseline, calculateMinMax(totalComparisons));
        double swapScore = calculateScore(swaps, minBaseline, calculateMinMax(totalSwaps));

        return (timeScore * 0.9) + (comparisonScore * 0.05) + (swapScore * 0.05);
    }

    private static void displayScalabilityReport(Map<Integer, String> bestAlgorithms, Map<Integer, Map<String, Double>> performanceData) {
        File outputFolder = new File("output_data");
        if (!outputFolder.exists() && !outputFolder.mkdir()) {
            System.out.println("Falha ao criar a pasta output_data.");
            return;
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File reportFile = new File(outputFolder, "scalability_report_" + timestamp + ".txt");

        try (PrintWriter writer = new PrintWriter(new FileWriter(reportFile))) {
            String header = "===== Relatório Completo de Escalabilidade =====";
            String separator = "----------------------------------------------------";


            writer.println(header);
            writer.println("Data de Execução: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
            writer.println(separator);
            System.out.println(header);
            System.out.println("Data de Execução: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
            System.out.println(separator);


            Map<String, Double> totalExecutionTime = new HashMap<>();
            Map<String, Long> totalComparisons = new HashMap<>();
            Map<String, Long> totalSwaps = new HashMap<>();

            for (Map.Entry<Integer, Map<String, Double>> entry : performanceData.entrySet()) {
                Map<String, Double> results = entry.getValue();
                for (Map.Entry<String, Double> algoEntry : results.entrySet()) {
                    String algorithm = algoEntry.getKey();
                    double time = algoEntry.getValue();

                    totalExecutionTime.merge(algorithm, time, Double::sum);
                    totalComparisons.merge(algorithm, (long) (time * 100), Long::sum);
                    totalSwaps.merge(algorithm, (long) (time * 50), Long::sum);
                }
            }


            rankAlgorithms(totalExecutionTime, totalComparisons, totalSwaps, writer);
            analyzeScalability(bestAlgorithms, writer);
            analyzeCostOfComparisonsAndSwaps(totalExecutionTime, totalComparisons, totalSwaps, writer);
            provideAlgorithmUsageAdvice(writer);
            summarizeResultsWithCostBenefit(totalExecutionTime, totalComparisons, totalSwaps, writer);


            System.out.println("\n--- Tabela de Custo-Benefício ---");
            System.out.printf("%-15s %-15s %-15s %-15s %-15s\n", "Algoritmo", "Tempo (ms)", "Comparações", "Trocas", "Nota Geral");

            for (String algorithm : totalExecutionTime.keySet()) {
                double finalScore = calculateFinalScore(algorithm, totalExecutionTime, totalComparisons, totalSwaps);
                System.out.printf("%-15s %-15.2f %-15d %-15d %-15.2f\n", algorithm, totalExecutionTime.get(algorithm), totalComparisons.getOrDefault(algorithm, 0L), totalSwaps.getOrDefault(algorithm, 0L), finalScore);
            }


            writer.println(separator);
            writer.println("Relatório de escalabilidade salvo em: " + reportFile.getAbsolutePath());
            System.out.println(separator);
            System.out.println("Relatório de escalabilidade salvo em: " + reportFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Erro ao salvar o relatório: " + e.getMessage());
        }
    }


    private static void rankAlgorithms(Map<String, Double> totalExecutionTime, Map<String, Long> totalComparisons, Map<String, Long> totalSwaps, PrintWriter writer) {
        writer.println("\n--- Rankeamento Geral dos Algoritmos ---");
        System.out.println("\n--- Rankeamento Geral dos Algoritmos ---");

        writer.println("Rankeamento por Tempo de Execução:");
        System.out.println("Rankeamento por Tempo de Execução:");
        totalExecutionTime.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> {
                    writer.printf("%s: Tempo Total = %.2f ms\n", entry.getKey(), entry.getValue());
                    System.out.printf("%s: Tempo Total = %.2f ms\n", entry.getKey(), entry.getValue());
                });

        writer.println("Rankeamento por Comparações:");
        System.out.println("Rankeamento por Comparações:");
        totalComparisons.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> {
                    writer.printf("%s: Comparações = %d\n", entry.getKey(), entry.getValue());
                    System.out.printf("%s: Comparações = %d\n", entry.getKey(), entry.getValue());
                });

        writer.println("Rankeamento por Trocas:");
        System.out.println("Rankeamento por Trocas:");
        totalSwaps.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> {
                    writer.printf("%s: Trocas = %d\n", entry.getKey(), entry.getValue());
                    System.out.printf("%s: Trocas = %d\n", entry.getKey(), entry.getValue());
                });
    }


    private static void analyzeScalability(Map<Integer, String> bestAlgorithms, PrintWriter writer) {
        writer.println("\n--- Análise de Escalabilidade ---");
        System.out.println("\n--- Análise de Escalabilidade ---");
        for (Map.Entry<Integer, String> entry : bestAlgorithms.entrySet()) {
            int dataSize = entry.getKey();
            String bestAlgorithm = entry.getValue();
            writer.printf("Para %d elementos, o algoritmo mais eficiente foi: %s\n", dataSize, bestAlgorithm);
            System.out.printf("Para %d elementos, o algoritmo mais eficiente foi: %s\n", dataSize, bestAlgorithm);
        }
    }


    private static void analyzeCostOfComparisonsAndSwaps(Map<String, Double> totalExecutionTime, Map<String, Long> totalComparisons, Map<String, Long> totalSwaps, PrintWriter writer) {
        writer.println("\n--- Análise do Custo de Comparações e Trocas ---");
        System.out.println("\n--- Análise do Custo de Comparações e Trocas ---");
        for (String algorithm : totalExecutionTime.keySet()) {
            double time = totalExecutionTime.get(algorithm);
            long comparisons = totalComparisons.getOrDefault(algorithm, 0L);
            long swaps = totalSwaps.getOrDefault(algorithm, 0L);

            writer.printf("%s: Tempo = %.2f ms, Comparações = %d, Trocas = %d\n", algorithm, time, comparisons, swaps);
            writer.printf("Razão Comparações/Tempo: %.2f, Razão Trocas/Tempo: %.2f\n", comparisons / time, swaps / time);
            System.out.printf("%s: Tempo = %.2f ms, Comparações = %d, Trocas = %d\n", algorithm, time, comparisons, swaps);
            System.out.printf("Razão Comparações/Tempo: %.2f, Razão Trocas/Tempo: %.2f\n", comparisons / time, swaps / time);
        }
    }


    private static void provideAlgorithmUsageAdvice(PrintWriter writer) {
        writer.println("\n--- Conselhos de Utilização dos Algoritmos ---");
        writer.println("1. QuickSort é eficiente para dados quase ordenados e tamanhos de dados menores.");
        writer.println("2. MergeSort apresenta desempenho mais estável em grandes volumes de dados.");
        writer.println("3. HeapSort é preferível para dados aleatórios onde a estabilidade não é um requisito.");
        System.out.println("\n--- Conselhos de Utilização dos Algoritmos ---");
        System.out.println("1. QuickSort é eficiente para dados quase ordenados e tamanhos de dados menores.");
        System.out.println("2. MergeSort apresenta desempenho mais estável em grandes volumes de dados.");
        System.out.println("3. HeapSort é preferível para dados aleatórios onde a estabilidade não é um requisito.");
    }


    private static void summarizeResultsWithCostBenefit(Map<String, Double> totalExecutionTime, Map<String, Long> totalComparisons, Map<String, Long> totalSwaps, PrintWriter writer) {
        writer.println("\n--- Resumo de Custo-Benefício ---");
        writer.printf("%-15s %-15s %-15s %-15s %-15s\n", "Algoritmo", "Tempo (ms)", "Comparações", "Trocas", "Nota Geral");


        for (String algorithm : totalExecutionTime.keySet()) {
            double finalScore = calculateFinalScore(algorithm, totalExecutionTime, totalComparisons, totalSwaps);
            writer.printf("%-15s %-15.2f %-15d %-15d %-15.2f\n", algorithm, totalExecutionTime.get(algorithm), totalComparisons.getOrDefault(algorithm, 0L), totalSwaps.getOrDefault(algorithm, 0L), finalScore);
        }
    }



    private static double calculateScore(double value, double minBaseline, MinMax minMax) {
        return (minMax.max > minMax.min) ? Math.max((1 - (value - minMax.min) / (minMax.max - minMax.min)) * 100, minBaseline) : 100;
    }


    private static <T extends Number & Comparable<T>> MinMax calculateMinMax(Map<String, T> data) {
        T max = Collections.max(data.values());
        T min = Collections.min(data.values());
        return new MinMax(min.doubleValue(), max.doubleValue());
    }


    private static class MinMax {
        double min, max;
        MinMax(double min, double max) {
            this.min = min;
            this.max = max;
        }
    }


    private static String[] handleExternalData(Scanner scanner) throws IOException {
        System.out.print("Informe o nome do arquivo (ex.: dados.txt): ");
        String fileName = scanner.nextLine().trim();
        File file = new File(fileName);

        if (!file.exists()) {
            System.out.println("O arquivo não existe.");
            System.out.print("Deseja criá-lo e preencher com dados aleatórios? (s/n): ");
            String createFile = scanner.nextLine().trim();
            if (createFile.equalsIgnoreCase("s")) {
                int size = getIntInput(scanner, "Digite o número de elementos para o arquivo (máximo 10.000.000): ");
                if (size > 10_000_000) {
                    System.out.println("O valor excede o limite. Tente novamente.");
                    return handleExternalData(scanner);
                }
                System.out.println("Gerando dados aleatórios e salvando no arquivo...");
                String[] data = DataLoader.generateRandomData(size);
                DataLoader.saveDataToFile(fileName, data);
                System.out.println("Arquivo criado e preenchido com dados aleatórios.");
                return data;
            } else {
                System.out.println("Operação cancelada.");
                return null;
            }
        } else {
            System.out.println("Carregando dados do arquivo " + fileName + "...");
            return DataLoader.loadDataFromFile(fileName);
        }
    }

    private static String[] handleInternalData(Scanner scanner) {
        System.out.println("Escolha o tamanho dos dados:");
        System.out.println("1 - Pequeno (1.000 elementos)");
        System.out.println("2 - Médio (10.000 elementos)");
        System.out.println("3 - Grande (100.000 elementos)");
        System.out.println("4 - Personalizado (digite o número de elementos até 10.000.000)");
        System.out.println("0 - Voltar ao menu principal");

        int sizeChoice = getIntInput(scanner, "Escolha uma opção: ");
        if (sizeChoice == 0) return null;

        int dataSize;
        switch (sizeChoice) {
            case 1 -> dataSize = 1_000;
            case 2 -> dataSize = 10_000;
            case 3 -> dataSize = 100_000;
            case 4 -> dataSize = getCustomSize(scanner);
            default -> {
                System.out.println("Opção inválida.");
                return handleInternalData(scanner);
            }
        }

        return DataLoader.generateRandomData(dataSize);
    }

    private static int getCustomSize(Scanner scanner) {
        int size = getIntInput(scanner, "Digite o número de elementos para a amostragem personalizada (até 10.000.000): ");
        if (size > 10_000_000) {
            System.out.println("O valor excede o limite de 10.000.000. Tente novamente.");
            return getCustomSize(scanner);
        }
        return size;
    }

    private static void catalogByState(String[] sortedData, String sampleName) {
        Map<String, List<String>> stateCatalog = new LinkedHashMap<>();
        stateCatalog.put("Amazonas", new ArrayList<>());
        stateCatalog.put("Pará", new ArrayList<>());
        stateCatalog.put("Mato Grosso", new ArrayList<>());
        stateCatalog.put("Rondônia", new ArrayList<>());
        stateCatalog.put("Maranhão", new ArrayList<>());

        int preservedCount = 0;
        int burnedCount = 0;
        int deforestedCount = 0;

        for (String data : sortedData) {
            String[] parts = data.split(":");
            int number = Integer.parseInt(parts[0]);
            int status = Integer.parseInt(parts[1]);

            if (status == 1) preservedCount++;
            else if (status == 2) burnedCount++;
            else if (status == 3) deforestedCount++;

            if (number >= 1 && number <= 3_130_000) {
                stateCatalog.get("Amazonas").add(data);
            } else if (number >= 3_130_001 && number <= 5_640_000) {
                stateCatalog.get("Pará").add(data);
            } else if (number >= 5_640_001 && number <= 7_400_000) {
                stateCatalog.get("Mato Grosso").add(data);
            } else if (number >= 7_400_001 && number <= 8_060_000) {
                stateCatalog.get("Rondônia").add(data);
            } else if (number >= 8_060_001 && number <= 10_000_000) {
                stateCatalog.get("Maranhão").add(data);
            }
        }

        displayStateCatalogReport(stateCatalog, sortedData.length, preservedCount, burnedCount, deforestedCount);
        saveStateCatalog(stateCatalog, sampleName, preservedCount, burnedCount, deforestedCount);
    }

    private static void displayStateCatalogReport(Map<String, List<String>> stateCatalog, int totalNumbers, int preservedCount, int burnedCount, int deforestedCount) {
        System.out.println("\n===== Relatório de Catalogação por Estado =====");
        System.out.println("Data de Execução: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        System.out.println("----------------------------------------------------");

        for (Map.Entry<String, List<String>> entry : stateCatalog.entrySet()) {
            String state = entry.getKey();
            int count = entry.getValue().size();
            double percentage = (count / (double) totalNumbers) * 100;
            System.out.printf("Estado: %s - Total: %d (%.2f%%)\n", state, count, percentage);
        }
        System.out.println("===============================================");
        System.out.printf("Total Preservado: %d (%.2f%%)\n", preservedCount, (preservedCount / (double) totalNumbers) * 100);
        System.out.printf("Total Queimado: %d (%.2f%%)\n", burnedCount, (burnedCount / (double) totalNumbers) * 100);
        System.out.printf("Total Desmatado: %d (%.2f%%)\n", deforestedCount, (deforestedCount / (double) totalNumbers) * 100);
    }

    private static void saveStateCatalog(Map<String, List<String>> stateCatalog, String sampleName, int preservedCount, int burnedCount, int deforestedCount) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File outputFile = new File("output_data", sampleName + "_state_catalog_" + timestamp + ".txt");

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("===== Relatório de Catalogação por Estado =====");
            writer.println("Data de Execução: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
            writer.println("----------------------------------------------------");

            for (Map.Entry<String, List<String>> entry : stateCatalog.entrySet()) {
                String state = entry.getKey();
                List<String> numbersList = entry.getValue();

                numbersList.sort(Comparator.comparingInt(data -> Integer.parseInt(data.split(":")[0])));

                int statePreserved = 0;
                int stateBurned = 0;
                int stateDeforested = 0;

                for (String data : numbersList) {
                    int status = Integer.parseInt(data.split(":")[1]);
                    switch (status) {
                        case 1 -> statePreserved++;
                        case 2 -> stateBurned++;
                        case 3 -> stateDeforested++;
                    }
                }

                writer.println("Estado: " + state);
                writer.println("Números: " + numbersList);
                writer.printf("Total: %d (Preservado: %d, Queimado: %d, Desmatado: %d)\n", numbersList.size(), statePreserved, stateBurned, stateDeforested);
                writer.println("----------------------------------------------------");
            }

            writer.printf("Total Preservado: %d (%.2f%%)\n", preservedCount, (preservedCount / (double) (preservedCount + burnedCount + deforestedCount)) * 100);
            writer.printf("Total Queimado: %d (%.2f%%)\n", burnedCount, (burnedCount / (double) (preservedCount + burnedCount + deforestedCount)) * 100);
            writer.printf("Total Desmatado: %d (%.2f%%)\n", deforestedCount, (deforestedCount / (double) (preservedCount + burnedCount + deforestedCount)) * 100);
            writer.println("----------------------------------------------------");

            System.out.println("Catalogação por estado salva em: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Erro ao salvar a catalogação por estado: " + e.getMessage());
        }
    }
}

class DataLoader {
    public static String[] generateRandomData(int size) {
        Random random = new Random();
        String[] data = new String[size];

        for (int i = 0; i < size; i++) {
            int digitCategory = random.nextInt(8) + 1;
            int lowerBound = (int) Math.pow(10, digitCategory - 1);
            int upperBound = (int) Math.pow(10, digitCategory) - 1;
            int number = random.nextInt(upperBound - lowerBound + 1) + lowerBound;

            int status = (random.nextInt(100) < 90) ? 1 : (random.nextBoolean() ? 2 : 3);
            data[i] = number + ":" + status;
        }
        return data;
    }

    public static String[] generateComplexData(int size, Map<String, Integer> statePercentages, Map<String, Integer> statusPercentages) {
        Random random = new Random();
        String[] data = new String[size];


        Map<String, Integer> stateCounts = calculateCounts(statePercentages, size);
        Map<String, Integer> statusCounts = calculateCounts(statusPercentages, size);


        int[] ranges = {1, 10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000};
        int numRanges = ranges.length - 1;

        int index = 0;
        for (String state : stateCounts.keySet()) {
            int stateCount = stateCounts.getOrDefault(state, 0);
            for (int i = 0; i < stateCount; i++) {

                int rangeIndex = i % numRanges;
                int min = ranges[rangeIndex];
                int max = ranges[rangeIndex + 1] - 1;
                int number = random.nextInt(max - min + 1) + min;
                String status = getStatusBasedOnPercentage(statusCounts, random);
                data[index++] = number + ":" + status;
            }
        }

        return data;
    }




    private static String getStatusBasedOnPercentage(Map<String, Integer> statusCounts, Random random) {
        int total = statusCounts.values().stream().mapToInt(Integer::intValue).sum();
        int rand = random.nextInt(total);
        int cumulative = 0;


        Map<String, String> statusMap = Map.of(
                "Preservado", "1",
                "Queimado", "2",
                "Desmatado", "3"
        );

        for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
            cumulative += entry.getValue();
            if (rand < cumulative) {

                return statusMap.getOrDefault(entry.getKey(), "1");
            }
        }
        return "1";
    }



    private static Map<String, Integer> calculateCounts(Map<String, Integer> percentages, int total) {
        Map<String, Integer> counts = new HashMap<>();
        for (Map.Entry<String, Integer> entry : percentages.entrySet()) {
            counts.put(entry.getKey(), (int) Math.round(total * (entry.getValue() / 100.0)));
        }
        return counts;
    }





    public static void saveDataToFile(String fileName, String[] data) throws IOException {
        File file = new File(fileName);
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (String value : data) {
                writer.println(value);
            }
            System.out.println("Arquivo de dados salvo com sucesso no diretório: " + file.getAbsolutePath());
        }
    }


    public static String[] loadDataFromFile(String fileName) throws IOException {
        List<String> dataList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                dataList.add(line.trim());
            }
        }
        return dataList.toArray(new String[0]);
    }
}

interface SortingAlgorithm {
    void sort(String[] array, AtomicBoolean cancelFlag, Statistics stats);
}

class Statistics {
    private long comparisons;
    private long swaps;

    public void incrementComparisons() {
        comparisons++;
    }

    public void incrementSwaps() {
        swaps++;
    }

    public long getComparisons() {
        return comparisons;
    }

    public long getSwaps() {
        return swaps;
    }

    public void reset() {
        comparisons = 0;
        swaps = 0;
    }
}

class SortingManager {
    private final String[] originalData;
    private final List<SortingAlgorithm> algorithms;
    private final Map<String, List<Double>> executionTimes;
    private final Map<String, Double> stdDeviations;
    private final AtomicBoolean cancelFlag;
    private final String sampleName;
    private final Map<String, Long> totalComparisons = new HashMap<>();
    private final Map<String, Long> totalSwaps = new HashMap<>();
    private final Map<String, String> savedFilePaths = new HashMap<>();


    public SortingManager(String[] data, AtomicBoolean cancelFlag, String sampleName) {
        this.originalData = data;
        this.algorithms = List.of(new QuickSort(), new MergeSort(), new HeapSort());
        this.executionTimes = new HashMap<>();
        this.stdDeviations = new HashMap<>();
        this.cancelFlag = cancelFlag;
        this.sampleName = sampleName;
    }

    private void saveSortedArrayToFile(String[] sortedArray, String algorithmName) {
        File outputFolder = new File("sorted_data");
        if (!outputFolder.exists() && !outputFolder.mkdir()) {
            System.out.println("Falha ao criar a pasta sorted_data.");
            return;
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File outputFile = new File(outputFolder, sampleName + "_" + algorithmName + "_sorted_" + timestamp + ".txt");

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            for (String value : sortedArray) {
                writer.println(value);
            }
            synchronized (this) {
                savedFilePaths.put(algorithmName, outputFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar o array ordenado: " + e.getMessage());
        }
    }

    public void executeSortingAlgorithmsInParallel() {
        ProgressUtils.setupTerminal();


        try (ExecutorService executor = Executors.newFixedThreadPool(algorithms.size())) {
            List<Callable<Void>> tasks = new ArrayList<>();

            for (SortingAlgorithm algorithm : algorithms) {
                ProgressUtils.initializeProgress(algorithm.getClass().getSimpleName());
                tasks.add(() -> {
                    executeSingleAlgorithm(algorithm);
                    return null;
                });
            }


            executor.invokeAll(tasks);


            displayAllSavedPaths();


            ProgressUtils.finalizeProgress();
        } catch (InterruptedException e) {
            System.out.println("Execução paralela interrompida: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void displayAllSavedPaths() {
        System.out.println("\n===== Caminhos dos Arquivos Ordenados =====");
        savedFilePaths.forEach((algorithmName, filePath) ->
                System.out.printf("%s: %s%n", algorithmName, filePath)
        );
        System.out.println("===========================================\n");
    }



    private void executeSingleAlgorithm(SortingAlgorithm algorithm) {
        if (cancelFlag.get()) return;

        String[] dataCopy = Arrays.copyOf(originalData, originalData.length);
        String algorithmName = algorithm.getClass().getSimpleName();
        List<Double> times = new ArrayList<>();
        long comparisons = 0;
        long swaps = 0;

        Statistics stats = new Statistics();
        int totalIterations = 10;

        ProgressUtils.initializeProgress(algorithmName);

        for (int i = 0; i < totalIterations && !cancelFlag.get(); i++) {
            double timeMs = Timer.measureTimeMs(() -> algorithm.sort(dataCopy, cancelFlag, stats));
            times.add(timeMs);
            comparisons += stats.getComparisons();
            swaps += stats.getSwaps();
            stats.reset();


            ProgressUtils.displayProgress(algorithmName, i + 1, totalIterations);
        }

        if (!cancelFlag.get()) {
            synchronized (this) {
                executionTimes.put(algorithmName, times);
                stdDeviations.put(algorithmName, calculateStandardDeviation(times, calculateAverage(times)));
                totalComparisons.put(algorithmName, comparisons / totalIterations);
                totalSwaps.put(algorithmName, swaps / totalIterations);
                saveSortedArrayToFile(dataCopy, algorithmName);
            }
        }
    }

    public void executeSortingAlgorithms() {
        for (SortingAlgorithm algorithm : algorithms) {
            if (cancelFlag.get()) {
                System.out.println("Processo de ordenação interrompido.");
                return;
            }

            String[] dataCopy = Arrays.copyOf(originalData, originalData.length);
            String algorithmName = algorithm.getClass().getSimpleName();
            List<Double> times = new ArrayList<>();
            long comparisons = 0;
            long swaps = 0;

            Statistics stats = new Statistics();
            System.out.println("Iniciando " + algorithmName + "...");

            for (int i = 0; i < 10 && !cancelFlag.get(); i++) {
                displayProgressWithCancelOption(algorithmName, i);
                double timeMs = Timer.measureTimeMs(() -> algorithm.sort(dataCopy, cancelFlag, stats));
                times.add(timeMs);
                comparisons += stats.getComparisons();
                swaps += stats.getSwaps();
                stats.reset();
            }

            if (!cancelFlag.get()) {
                executionTimes.put(algorithmName, times);
                stdDeviations.put(algorithmName, calculateStandardDeviation(times, calculateAverage(times)));
                totalComparisons.put(algorithmName, comparisons / 10);
                totalSwaps.put(algorithmName, swaps / 10);
                System.out.println("\n" + algorithmName + " concluído.");


                saveSortedArrayToFile(dataCopy, algorithmName);
            } else {
                System.out.println("Processo de ordenação interrompido.");
                return;
            }
        }


        displayAllSavedPaths();
    }


    private double calculateAverage(List<Double> times) {
        return times.stream().mapToDouble(Double::doubleValue).sum() / times.size();
    }

    private double calculateStandardDeviation(List<Double> times, double average) {
        return (times.size() > 1) ? Math.sqrt(times.stream().mapToDouble(time -> Math.pow(time - average, 2)).sum() / (times.size() - 1)) : 0;
    }

    public String getBestAlgorithm() {
        return Collections.min(executionTimes.entrySet(), Comparator.comparingDouble(e -> calculateAverage(e.getValue()))).getKey();
    }

    public Map<String, Double> getPerformanceData() {
        Map<String, Double> performanceData = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : executionTimes.entrySet()) {
            String algorithmName = entry.getKey();
            double avgTime = calculateAverage(entry.getValue());
            performanceData.put(algorithmName, avgTime);
        }
        return performanceData;
    }

    private void displayProgressWithCancelOption(String algorithmName, int iteration) {
        int progress = (iteration + 1) * 10;
        System.out.printf("\r%s Progresso: [%s] %d%%", algorithmName, "=".repeat(progress / 10), progress);
    }



    public void savePerformanceReport() {
        if (cancelFlag.get()) return;

        File outputFolder = new File("output_data");
        if (!outputFolder.exists() && !outputFolder.mkdir()) {
            System.out.println("Falha ao criar a pasta output_data.");
            return;
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File reportFile = new File(outputFolder, sampleName + "_report_" + timestamp + ".txt");

        try (PrintWriter writer = new PrintWriter(new FileWriter(reportFile))) {

            PrintStream console = System.out;


            Map<String, Double> averageTimes = new HashMap<>();


            printBoth(writer, console, "Relatório de Desempenho:");
            printBoth(writer, console, "Quantidade de dados: " + originalData.length + " elementos");


            printBoth(writer, console, "\nTabela de Resumo (Tempo Médio, Comparações e Trocas):");
            printBoth(writer, console, "-------------------------------------------------------");
            printBoth(writer, console, String.format("%-15s %-20s %-15s %-15s", "Algoritmo", "Tempo Médio (ms)", "Comparações", "Trocas"));


            for (String algorithmName : executionTimes.keySet()) {
                List<Double> times = executionTimes.get(algorithmName);

                if (times.isEmpty()) {
                    printBoth(writer, console, algorithmName + " - Nenhum tempo registrado.");
                    continue;
                }

                double averageMs = calculateAverage(times);
                long avgComparisons = totalComparisons.getOrDefault(algorithmName, 0L);
                long avgSwaps = totalSwaps.getOrDefault(algorithmName, 0L);


                averageTimes.put(algorithmName, averageMs);


                printBoth(writer, console, String.format("%-15s %-20.2f %-15d %-15d", algorithmName, averageMs, avgComparisons, avgSwaps));
            }


            for (Map.Entry<String, List<Double>> entry : executionTimes.entrySet()) {
                String algorithmName = entry.getKey();
                List<Double> times = entry.getValue();

                if (times.isEmpty()) {
                    printBoth(writer, console, algorithmName + " - Nenhum tempo registrado.");
                    continue;
                }

                double averageMs = calculateAverage(times);
                double medianMs = calculateMedian(times);
                double stdDevMs = stdDeviations.get(algorithmName);
                printBoth(writer, console, "\n" + algorithmName + " - Tempo Médio: " + String.format("%.2f", averageMs) + " ms, Mediana: " + String.format("%.2f", medianMs) + " ms, Desvio Padrão: " + String.format("%.2f", stdDevMs) + " ms");
                printBoth(writer, console, algorithmName + " - Comparações Médias: " + totalComparisons.get(algorithmName) + ", Trocas Médias: " + totalSwaps.get(algorithmName));


                printBoth(writer, console, "\nDistribuição de Tempo de Execução por Iteração:");
                for (int i = 0; i < times.size(); i++) {
                    printBoth(writer, console, "Iteração " + (i + 1) + ": " + String.format("%.2f", times.get(i)) + " ms");
                }

                double highOutlierThreshold = averageMs + 2 * stdDevMs;
                double lowOutlierThreshold = averageMs - 2 * stdDevMs;
                printBoth(writer, console, "\nAnálise de Outliers:");
                for (double time : times) {
                    if (time > highOutlierThreshold) {
                        printBoth(writer, console, "Tempo de Execução Alto Anômalo: " + String.format("%.2f", time) + " ms (acima de " + String.format("%.2f", highOutlierThreshold) + " ms)");
                    } else if (time < lowOutlierThreshold) {
                        printBoth(writer, console, "Tempo de Execução Baixo Anômalo: " + String.format("%.2f", time) + " ms (abaixo de " + String.format("%.2f", lowOutlierThreshold) + " ms)");
                    }
                }
                printBoth(writer, console, "");
            }


            String bestAlgorithm = Collections.min(averageTimes.entrySet(), Map.Entry.comparingByValue()).getKey();
            double bestTime = averageTimes.get(bestAlgorithm);

            printBoth(writer, console, "\nAlgoritmo mais eficiente: " + bestAlgorithm);
            for (Map.Entry<String, Double> entry : averageTimes.entrySet()) {
                String algorithmName = entry.getKey();
                double time = entry.getValue();
                if (!algorithmName.equals(bestAlgorithm)) {
                    double efficiencyRatio = time / bestTime;
                    double timeDifference = time - bestTime;
                    printBoth(writer, console, algorithmName + " foi " + String.format("%.2f", efficiencyRatio) + " vezes mais lento que " + bestAlgorithm + ", diferença de " + String.format("%.2f", timeDifference) + " ms");
                }
            }


            String lowestStdDevAlgorithm = Collections.min(stdDeviations.entrySet(), Map.Entry.comparingByValue()).getKey();
            printBoth(writer, console, "\nAlgoritmo com menor desvio padrão: " + lowestStdDevAlgorithm + " (Desvio Padrão: " + String.format("%.2f", stdDeviations.get(lowestStdDevAlgorithm)) + " ms)");


            printBoth(writer, console, "\nComparação Gráfica de Tempo de Execução:");
            for (Map.Entry<String, Double> entry : averageTimes.entrySet()) {
                String algorithmName = entry.getKey();
                double relativeTime = entry.getValue() / bestTime;
                int barLength = (int) (relativeTime * 20);
                printBoth(writer, console, String.format("%-15s | %s (%.2f ms)", algorithmName, "=".repeat(barLength), entry.getValue()));
            }


            double minBaseline = 30.0;
            printBoth(writer, console, "\n------------------------------------------------------------");
            printBoth(writer, console, "Tabela de Avaliação (0-100)");
            printBoth(writer, console, "------------------------------------------------------------");
            printBoth(writer, console, String.format("%-15s %-15s %-15s %-15s", "Algoritmo", "Nota Tempo", "Nota Comp.", "Nota Final"));

            for (String algorithmName : averageTimes.keySet()) {
                double timeScore = calculateScore(averageTimes.get(algorithmName), minBaseline, calculateMinMax(averageTimes));
                double comparisonScore = calculateScore(totalComparisons.getOrDefault(algorithmName, 0L), minBaseline, calculateMinMax(totalComparisons));
                double swapScore = calculateScore(totalSwaps.getOrDefault(algorithmName, 0L), minBaseline, calculateMinMax(totalSwaps));
                double finalScore = (timeScore * 0.9) + (comparisonScore * 0.05) + (swapScore * 0.05);

                printBoth(writer, console, String.format("%-15s %-15.2f %-15.2f %-15.2f", algorithmName, timeScore, comparisonScore, finalScore));
            }


            printBoth(writer, console, "\n------------------------------------------------------------");
            printBoth(writer, console, "Resumo");
            printBoth(writer, console, "------------------------------------------------------------");
            printBoth(writer, console, "Algoritmo mais eficiente: " + bestAlgorithm + " (Tempo Médio: " + String.format("%.2f", bestTime) + " ms)");
            printBoth(writer, console, "Algoritmo com menor desvio padrão: " + lowestStdDevAlgorithm + " (Desvio Padrão: " + String.format("%.2f", stdDeviations.get(lowestStdDevAlgorithm)) + " ms)");
            printBoth(writer, console, "\nRecomendações para Cenários Específicos:");
            printBoth(writer, console, "------------------------------------------------------------");

            if (bestAlgorithm.equals("MergeSort")) {
                printBoth(writer, console, "MergeSort é recomendado para conjuntos de dados grandes devido à sua consistência e estabilidade.");
            }
            if (bestAlgorithm.equals("QuickSort")) {
                printBoth(writer, console, "QuickSort é ideal para conjuntos de dados parcialmente ordenados ou pequenos, pois tende a ser mais rápido nesses casos.");
            }
            if (bestAlgorithm.equals("HeapSort")) {
                printBoth(writer, console, "HeapSort é indicado para dados aleatórios onde a estabilidade não é um requisito.");
            }

            printBoth(writer, console, "\nPara dados com grande variação, é recomendável usar algoritmos com menor desvio padrão (ex.: " + lowestStdDevAlgorithm + ").");

            System.out.println("Relatório salvo em: " + reportFile.getAbsolutePath());
            System.out.println("\n");
        } catch (IOException e) {
            System.out.println("Erro ao salvar o relatório: " + e.getMessage());
        }
    }


    private void printBoth(PrintWriter writer, PrintStream console, String text) {
        writer.println(text);
        console.println(text);
    }


    private double calculateMedian(List<Double> values) {
        Collections.sort(values);
        int middle = values.size() / 2;
        if (values.size() % 2 == 0) {
            return (values.get(middle - 1) + values.get(middle)) / 2.0;
        } else {
            return values.get(middle);
        }
    }


    private <T extends Number & Comparable<T>> MinMax calculateMinMax(Map<String, T> data) {
        T max = Collections.max(data.values());
        T min = Collections.min(data.values());
        return new MinMax(min.doubleValue(), max.doubleValue());
    }



    private double calculateScore(double value, double minBaseline, MinMax minMax) {
        return (minMax.max > minMax.min) ? Math.max((1 - (value - minMax.min) / (minMax.max - minMax.min)) * 100, minBaseline) : 100;
    }


    private static class MinMax {
        double min, max;
        MinMax(double min, double max) {
            this.min = min;
            this.max = max;
        }
    }

}


class SortingUtils {
    public static void insertionSort(String[] array, int low, int high, Statistics stats) {
        for (int i = low + 1; i <= high; i++) {
            String key = array[i];
            int j = i - 1;
            while (j >= low && compare(array[j], key) > 0) {
                stats.incrementComparisons();
                array[j + 1] = array[j];
                j--;
                stats.incrementSwaps();
            }
            array[j + 1] = key;
        }
    }

    private static int compare(String a, String b) {
        return Integer.compare(extractNumericPart(a), extractNumericPart(b));
    }

    private static int extractNumericPart(String data) {
        return Integer.parseInt(data.split(":")[0]);
    }
}





class Timer {
    public static double measureTimeMs(Runnable algorithm) {
        long startTime = System.nanoTime();
        algorithm.run();
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000.0;
    }
}

class QuickSort implements SortingAlgorithm {
    private static final int INSERTION_SORT_THRESHOLD = 32;
    private final boolean showSteps;


    public QuickSort() {
        this.showSteps = false;
    }


    public QuickSort(boolean showSteps) {
        this.showSteps = showSteps;
    }

    @Override
    public void sort(String[] array, AtomicBoolean cancelFlag, Statistics stats) {
        quickSortIterative(array, cancelFlag, stats);
    }

    private void quickSortIterative(String[] array, AtomicBoolean cancelFlag, Statistics stats) {
        if (cancelFlag.get()) return;

        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{0, array.length - 1});

        while (!stack.isEmpty()) {
            if (cancelFlag.get()) return;

            int[] range = stack.pop();
            int low = range[0];
            int high = range[1];

            if (high - low <= INSERTION_SORT_THRESHOLD) {
                SortingUtils.insertionSort(array, low, high, stats);
                continue;
            }

            int pivotIndex = medianOfThreePartition(array, low, high, stats);

            if (pivotIndex - 1 - low > high - pivotIndex - 1) {
                stack.push(new int[]{low, pivotIndex - 1});
                stack.push(new int[]{pivotIndex + 1, high});
            } else {
                stack.push(new int[]{pivotIndex + 1, high});
                stack.push(new int[]{low, pivotIndex - 1});
            }
        }
    }

    private int medianOfThreePartition(String[] array, int low, int high, Statistics stats) {
        int mid = low + (high - low) / 2;

        if (compare(array[low], array[mid]) > 0) swap(array, low, mid, stats);
        if (compare(array[low], array[high]) > 0) swap(array, low, high, stats);
        if (compare(array[mid], array[high]) > 0) swap(array, mid, high, stats);

        String pivot = array[mid];
        swap(array, mid, high - 1, stats);
        int i = low;
        int j = high - 1;

        while (true) {
            while (compare(array[++i], pivot) < 0) if (i == high - 1) break;
            while (compare(array[--j], pivot) > 0) if (j == low) break;
            if (i >= j) break;
            swap(array, i, j, stats);
        }
        swap(array, i, high - 1, stats);
        return i;
    }

    private void swap(String[] array, int i, int j, Statistics stats) {
        String temp = array[i];
        array[i] = array[j];
        array[j] = temp;
        stats.incrementSwaps();
        if (showSteps) {
            System.out.printf("Troca realizada: %s <-> %s\n", array[i], array[j]);
        }
    }

    private int compare(String a, String b) {
        int result = Integer.compare(extractNumericPart(a), extractNumericPart(b));
        if (showSteps) {
            System.out.printf("Comparação realizada: %s e %s (resultado: %d)\n", a, b, result);
        }
        return result;
    }

    private int extractNumericPart(String data) {
        return Integer.parseInt(data.split(":")[0]);
    }
}

class MergeSort implements SortingAlgorithm {
    private static final int INSERTION_SORT_THRESHOLD = 32;

    @Override
    public void sort(String[] array, AtomicBoolean cancelFlag, Statistics stats) {
        String[] aux = new String[array.length];
        System.arraycopy(array, 0, aux, 0, array.length);
        mergeSort(array, aux, 0, array.length - 1, cancelFlag, stats);
    }

    private void mergeSort(String[] array, String[] aux, int left, int right, AtomicBoolean cancelFlag, Statistics stats) {
        if (left >= right || cancelFlag.get()) return;

        if (right - left <= INSERTION_SORT_THRESHOLD) {
            SortingUtils.insertionSort(array, left, right, stats);
            return;
        }

        int middle = left + (right - left) / 2;
        mergeSort(aux, array, left, middle, cancelFlag, stats);
        mergeSort(aux, array, middle + 1, right, cancelFlag, stats);

        if (compare(aux[middle], aux[middle + 1]) <= 0) {
            System.arraycopy(aux, left, array, left, right - left + 1);
            return;
        }
        merge(array, aux, left, middle, right, stats);
    }

    private void merge(String[] array, String[] aux, int left, int middle, int right, Statistics stats) {
        int i = left, j = middle + 1;
        for (int k = left; k <= right; k++) {
            stats.incrementComparisons();
            if (i > middle) array[k] = aux[j++];
            else if (j > right) array[k] = aux[i++];
            else if (compare(aux[i], aux[j]) <= 0) array[k] = aux[i++];
            else array[k] = aux[j++];
            stats.incrementSwaps();
        }
    }

    private int compare(String a, String b) {
        return Integer.compare(extractNumericPart(a), extractNumericPart(b));
    }

    private int extractNumericPart(String data) {
        return Integer.parseInt(data.split(":")[0]);
    }
}

class HeapSort implements SortingAlgorithm {
    @Override
    public void sort(String[] array, AtomicBoolean cancelFlag, Statistics stats) {
        int n = array.length;


        for (int i = n / 2 - 1; i >= 0 && !cancelFlag.get(); i--) {
            siftDown(array, i, n, stats);
        }

        for (int i = n - 1; i > 0 && !cancelFlag.get(); i--) {
            swap(array, 0, i, stats);
            siftDown(array, 0, i, stats);
        }
    }

    private void siftDown(String[] array, int i, int n, Statistics stats) {
        while (true) {
            int largest = i;
            int left = 2 * i + 1;
            int right = 2 * i + 2;

            if (left < n && compare(array[left], array[largest]) > 0) {
                stats.incrementComparisons();
                largest = left;
            } else if (left < n) {
                stats.incrementComparisons();
            }

            if (right < n && compare(array[right], array[largest]) > 0) {
                stats.incrementComparisons();
                largest = right;
            } else if (right < n) {
                stats.incrementComparisons();
            }

            if (largest == i) break;

            swap(array, i, largest, stats);
            i = largest;
        }
    }

    private void swap(String[] array, int i, int j, Statistics stats) {
        String temp = array[i];
        array[i] = array[j];
        array[j] = temp;
        stats.incrementSwaps();
    }

    private int compare(String a, String b) {
        return Integer.compare(extractNumericPart(a), extractNumericPart(b));
    }

    private int extractNumericPart(String data) {
        return Integer.parseInt(data.split(":")[0]);
    }
}



class IntroSort implements SortingAlgorithm {
    private static final int INSERTION_SORT_THRESHOLD = 32;

    @Override
    public void sort(String[] array, AtomicBoolean cancelFlag, Statistics stats) {
        int maxDepth = (int) (2 * Math.log(array.length) / Math.log(2));
        int totalSize = array.length;
        int[] progress = {0};

        introSort(array, 0, array.length - 1, maxDepth, cancelFlag, stats, progress, totalSize);
        ProgressUtils.displayProgress("IntroSort", totalSize, totalSize);
        System.out.println("\nIntroSort concluído.");

        FileUtils.saveArrayToFile(array, "IntroSort");
    }

    private void introSort(String[] array, int low, int high, int depthLimit, AtomicBoolean cancelFlag, Statistics stats, int[] progress, int totalSize) {
        while (high - low > INSERTION_SORT_THRESHOLD) {
            if (cancelFlag.get()) return;

            if (depthLimit == 0) {
                HeapSort heapSort = new HeapSort();
                heapSort.sort(array, cancelFlag, stats);
                return;
            }

            int pivotIndex = partition(array, low, high, stats);
            depthLimit--;

            if (pivotIndex - low < high - pivotIndex) {
                introSort(array, low, pivotIndex - 1, depthLimit, cancelFlag, stats, progress, totalSize);
                low = pivotIndex + 1;
            } else {
                introSort(array, pivotIndex + 1, high, depthLimit, cancelFlag, stats, progress, totalSize);
                high = pivotIndex - 1;
            }


            progress[0] += high - low + 1;
            if (progress[0] % (totalSize / 100) == 0 || progress[0] >= totalSize) {
                ProgressUtils.displayProgress("IntroSort", progress[0], totalSize);
            }
        }

        SortingUtils.insertionSort(array, low, high, stats);
    }



    private int partition(String[] array, int low, int high, Statistics stats) {
        String pivot = array[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            stats.incrementComparisons();
            if (array[j].compareTo(pivot) <= 0) {
                i++;
                swap(array, i, j, stats);
            }
        }
        swap(array, i + 1, high, stats);
        return i + 1;
    }

    private void swap(String[] array, int i, int j, Statistics stats) {
        String temp = array[i];
        array[i] = array[j];
        array[j] = temp;
        stats.incrementSwaps();
    }
}

class FileUtils {
    public static void saveArrayToFile(String[] array, String algorithmName) {
        File outputFolder = new File("sorted_data");
        if (!outputFolder.exists() && !outputFolder.mkdir()) {
            System.out.println("Falha ao criar o diretório sorted_data.");
            return;
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File outputFile = new File(outputFolder, algorithmName + "_sorted_" + timestamp + ".txt");

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            for (String value : array) {
                writer.println(value);
            }
            System.out.println("Resultado da ordenação salvo em: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Erro ao salvar o resultado da ordenação: " + e.getMessage());
        }
    }
}



class TimSort implements SortingAlgorithm {
    private static final int RUN = 32;

    @Override
    public void sort(String[] array, AtomicBoolean cancelFlag, Statistics stats) {
        int n = array.length;
        int processed = 0;

        ProgressUtils.initializeProgress("TimSort");

        for (int i = 0; i < n; i += RUN) {
            if (cancelFlag.get()) return;
            int end = Math.min((i + RUN - 1), (n - 1));
            SortingUtils.insertionSort(array, i, end, stats);

            processed += end - i + 1;
            ProgressUtils.displayProgress("TimSort", processed, n);
        }

        for (int size = RUN; size < n; size = 2 * size) {
            for (int left = 0; left < n; left += 2 * size) {
                if (cancelFlag.get()) return;
                int mid = Math.min(left + size - 1, n - 1);
                int right = Math.min(left + 2 * size - 1, n - 1);

                merge(array, left, mid, right, stats);

                processed += Math.min(2 * size, n - left);
                ProgressUtils.displayProgress("TimSort", processed, n);
            }
        }

        ProgressUtils.displayProgress("TimSort", n, n);
        System.out.println("\nTimSort concluído.");
        saveSortedArray(array);
    }

    private void merge(String[] array, int left, int mid, int right, Statistics stats) {
        int n1 = mid - left + 1;
        int n2 = right - mid;


        String[] leftArray = new String[n1];
        String[] rightArray = new String[n2];
        System.arraycopy(array, left, leftArray, 0, n1);
        System.arraycopy(array, mid + 1, rightArray, 0, n2);


        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            stats.incrementComparisons();
            if (leftArray[i].compareTo(rightArray[j]) <= 0) {
                array[k++] = leftArray[i++];
            } else {
                array[k++] = rightArray[j++];
            }
            stats.incrementSwaps();
        }


        while (i < n1) {
            array[k++] = leftArray[i++];
            stats.incrementSwaps();
        }


        while (j < n2) {
            array[k++] = rightArray[j++];
            stats.incrementSwaps();
        }
    }

    private void saveSortedArray(String[] array) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File outputFile = new File("output_data", "TimSort_sorted_" + timestamp + ".txt");

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            for (String value : array) {
                writer.println(value);
            }
            System.out.println("\nArray ordenado salvo em: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("\nErro ao salvar o array ordenado: " + e.getMessage());
        }
    }
}

class ProgressUtils {
    private static final Map<String, Integer> progressMap = new HashMap<>();
    private static final Object LOCK = new Object();

    public static void initializeProgress(String algorithmName) {
        synchronized (LOCK) {
            progressMap.put(algorithmName, 0);
        }
    }

    public static void displayProgress(String algorithmName, int completed, int total) {
        synchronized (LOCK) {
            int progress = Math.min((completed * 100) / total, 100);
            if (progress == progressMap.getOrDefault(algorithmName, -1)) {
                return;
            }
            progressMap.put(algorithmName, progress);


            String bar = "=".repeat(progress / 10);
            String paddedBar = String.format("%-10s", bar);
            System.out.printf("\r%s Progresso: [%s] %3d%%%s", algorithmName, paddedBar, progress,
                    progress == 100 ? " - Concluído!" : "");
            if (progress == 100) {
                System.out.println();
            }
        }
    }

    public static void setupTerminal() {
        System.out.print("\033[H\033[J");
    }

    public static void finalizeProgress() {
        System.out.println();
    }
}

class DualPivotQuickSort implements SortingAlgorithm {
    @Override
    public void sort(String[] array, AtomicBoolean cancelFlag, Statistics stats) {
        int[] progress = {0};
        int totalSize = array.length;

        ProgressUtils.initializeProgress("DualPivotQuickSort");
        dualPivotQuickSort(array, 0, array.length - 1, cancelFlag, stats, progress, totalSize);

        ProgressUtils.displayProgress("DualPivotQuickSort", totalSize, totalSize);
        System.out.println("\nDualPivotQuickSort concluído.");
        saveSortedArray(array);
    }

    private void dualPivotQuickSort(String[] array, int low, int high, AtomicBoolean cancelFlag, Statistics stats, int[] progress, int totalSize) {
        if (low < high && !cancelFlag.get()) {
            int[] pivots = partition(array, low, high, stats);

            dualPivotQuickSort(array, low, pivots[0] - 1, cancelFlag, stats, progress, totalSize);
            dualPivotQuickSort(array, pivots[0] + 1, pivots[1] - 1, cancelFlag, stats, progress, totalSize);
            dualPivotQuickSort(array, pivots[1] + 1, high, cancelFlag, stats, progress, totalSize);

            progress[0] += high - low + 1;
            ProgressUtils.displayProgress("DualPivotQuickSort", progress[0], totalSize);
        }
    }



    private int[] partition(String[] array, int low, int high, Statistics stats) {
        if (compare(array[low], array[high]) > 0) {
            swap(array, low, high, stats);
        }

        String pivot1 = array[low];
        String pivot2 = array[high];
        int i = low + 1, lt = low + 1, gt = high - 1;

        while (i <= gt) {
            stats.incrementComparisons();
            if (compare(array[i], pivot1) < 0) {
                swap(array, i++, lt++, stats);
            } else if (compare(array[i], pivot2) > 0) {
                swap(array, i, gt--, stats);
            } else {
                i++;
            }
        }

        swap(array, low, --lt, stats);
        swap(array, high, ++gt, stats);

        return new int[]{lt, gt};
    }

    private void swap(String[] array, int i, int j, Statistics stats) {
        String temp = array[i];
        array[i] = array[j];
        array[j] = temp;
        stats.incrementSwaps();
    }

    private int compare(String a, String b) {
        return Integer.compare(extractNumericPart(a), extractNumericPart(b));
    }

    private int extractNumericPart(String data) {
        return Integer.parseInt(data.split(":")[0]);
    }

    private void saveSortedArray(String[] array) {
        FileUtils.saveArrayToFile(array, "DualPivotQuickSort");
    }
}

