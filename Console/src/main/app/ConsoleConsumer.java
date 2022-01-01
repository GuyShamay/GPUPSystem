package main.app;

import gpup.dto.GPUPConsumerDTO;

import java.util.function.Consumer;

public class ConsoleConsumer implements Consumer<GPUPConsumerDTO> {
    @Override
    public void accept(GPUPConsumerDTO consumerDTO) {
        GPUPConsoleIO.printMsg(consumerDTO.toString());
    }
}
