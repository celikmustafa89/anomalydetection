package com.celikmustafa.ml.v1;

public class ServerLog {

    Double cpu;
    Double ram;

    public ServerLog() {
    }

    public ServerLog(String[] fields) {
        this.cpu = Double.valueOf(fields[0]);
        this.ram = Double.valueOf(fields[23]);
    }

    public ServerLog(Double ram, Double cpu) {
        this.cpu = cpu;
        this.ram = ram;
    }

    public ServerLog getDifference(ServerLog otherServerLog){
        ServerLog serverLog = new ServerLog();

        serverLog.cpu = this.cpu - otherServerLog.cpu;
        serverLog.ram = this.ram - otherServerLog.ram;

        return serverLog;
    }

    public Double getRam() {
        return ram;
    }

    public void setRam(Double ram) {
        this.ram = ram;
    }

    public Double getCpu() {
        return cpu;
    }

    public void setCpu(Double cpu) {
        this.cpu = cpu;
    }
}
