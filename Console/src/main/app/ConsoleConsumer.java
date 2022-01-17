package main.app;

import dto.GPUPConsumer;

import java.util.function.Consumer;

public class ConsoleConsumer implements Consumer<GPUPConsumer> {
    @Override
    public void accept(GPUPConsumer consumerDTO) {
        GPUPConsoleIO.printMsg(consumerDTO.toString());
    }
}
