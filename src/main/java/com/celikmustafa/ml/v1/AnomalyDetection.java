package com.celikmustafa.ml.v1;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AnomalyDetection {

    // String inputFilePath = "/Users/mustafacelik/Storages/20180528_mergedv2.csv";
    private static String[] inputFilePaths = new String[]{
            "/Users/mustafacelik/Storages/20180528_mergedv2_small.csv",
            "/Users/mustafacelik/Storages/20180528_mergedv2_small.csv"
    };
    private static String columnSplitChar = ",";

    private static Integer anomalyCheckHistory = 5; // kaç kayıt geriye bakılacağı bilgisi tutuluyor

    private static Double cpuAnomalyRate = 10000.0;
    private static Double ramAnomalyRate = 350.0;


    public static void main(String[] args) {

        System.out.println("com.celikmustafa.ml.v1.Anomaly Detection starts...");

        // List<List<String>> stringDataset = processInputFileForString(inputFilePath);
        List<ServerLog> serverLogDataset = new ArrayList<ServerLog>();
        List<Anomaly> serverLogAnomly = new ArrayList<Anomaly>();
        for (String inputFilePath: inputFilePaths) {
            serverLogDataset = processInputFileForServerLog(inputFilePath);
            serverLogAnomly.addAll(detectAnomaly(serverLogDataset));
        }

        Collections.sort(serverLogAnomly, Anomaly.serverLogRamComparator);

        System.out.println("com.celikmustafa.ml.v1.Anomaly Detection ends...");
    }

    private static List<Anomaly> detectAnomaly(List<ServerLog> serverLogDataset) {
        List<Anomaly> anomalyList = new ArrayList<>();
        Anomaly anomaly = null;
        for (int i = 0; i < serverLogDataset.size(); i++) {
            ServerLog currentServerLog = serverLogDataset.get(i);
            for (int j = 1; j < anomalyCheckHistory; j++) {
                if(i-j >= 0){ // boundary check of server log when comparing current log with previous ones
                    anomaly = new Anomaly(currentServerLog, serverLogDataset.get(i-j));
                    if(isAnomalyExists(anomaly.getServerLogDifference())){ // if there exists an anomaly, add it anomaly list
                        anomalyList.add(anomaly);
                    }
                }
            }

        }

        return anomalyList;
    }


    // checks if there exists an anomaly by using anomaly configuration value of each field.
    public static Boolean isAnomalyExists(ServerLog serverLogDifference){
        Boolean isAnomalyExists = false;
        if(serverLogDifference.getCpu() > cpuAnomalyRate){ // cpu anomaly is exists
            isAnomalyExists = true;
        }
        if(serverLogDifference.getRam() > ramAnomalyRate){ // cpu anomaly is exists
            isAnomalyExists = true;
        }
        return isAnomalyExists;
    }

    private static Function<String, ServerLog> mapToServerLog = (line) -> {
        String[] p = line.split(columnSplitChar);// a CSV has comma separated lines

        //System.out.println(p[0]);
        ServerLog serverLog = new ServerLog(p);

        return serverLog;
    };

    public static List<ServerLog> processInputFileForServerLog(String inputFilePath) {
        List<ServerLog> inputList = null;
        try{
            File inputF = new File(inputFilePath);
            InputStream inputFS = new FileInputStream(inputF);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
            // skip the header of the csv
            inputList = br.lines().skip(1).map(mapToServerLog).collect(Collectors.toList());
            br.close();
        } catch (IOException e) {
            System.out.println("file read exception.");
            System.out.println(e.toString());
        }
        return inputList ;
    }


    // ##### ##### ##### HELPER FUNCTIONS ##### ##### #####
    private static Function<String, List<String>> mapToStringList = (line) -> {
        List<String> p = Arrays.asList(line.split(columnSplitChar));// a CSV has comma separated lines

        return p;
    };
    public static List<List<String>> processInputFileForString(String inputFilePath) {
        List<List<String>> inputList = null;
        try{
            File inputF = new File(inputFilePath);
            InputStream inputFS = new FileInputStream(inputF);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
            // skip the header of the csv
            inputList = br.lines().skip(1).map(mapToStringList).collect(Collectors.toList());
            br.close();
        } catch (IOException e) {
            System.out.println("file read exception.");
            System.out.println(e.toString());
        }
        return inputList ;
    }
    // ##### ##### ##### HELPER FUNCTIONS ENDS ##### ##### #####

}
