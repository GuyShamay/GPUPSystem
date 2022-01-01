package gpup.dto;

public interface GPUPConsumerDTO {
    default void setTaskOutput(TaskOutputDTO taskOutput) {};

    default String getName() {
        return "Name";
    }
}
