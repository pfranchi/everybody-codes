package common.support.params;

public record GenericExecutionParameter<T>(T value) implements ExecutionParameters {
}
