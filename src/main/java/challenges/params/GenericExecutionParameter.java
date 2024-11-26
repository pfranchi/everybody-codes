package challenges.params;

public record GenericExecutionParameter<T>(T value) implements ExecutionParameters {
}
