package common.int_computer;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Longs;
import common.Strings;

import java.util.*;
import java.util.function.LongSupplier;

public class IntComputer {

    private final Map<Long, Long> tape;

    private long pointer = 0L;
    private long relativeBase = 0;
    private final Deque<Long> outputs = new ArrayDeque<>();

    public IntComputer(String instructionString) {

        List<Long> instructions = Arrays.stream(Strings.firstRow(instructionString).split(",")).mapToLong(Long::parseLong).boxed().toList();
        int length = instructions.size();

        tape = new HashMap<>();
        for (long i = 0; i < length; i++) {
            tape.put(i, instructions.get((int) i));
        }

    }

    public Deque<Long> runProgram(long input) {
        return runProgram(new ArrayDeque<>(Collections.singleton(input)));
    }

    public Deque<Long> runProgram(long... inputs) {
        return runProgram(new ArrayDeque<>(Longs.asList(inputs)));
    }

    public Deque<Long> runProgram(Deque<Long> inputs) {
        return runProgram(inputs::remove);
    }

    public Deque<Long> runProgram(LongSupplier inputSupplier) {
        while (true) {

            long opCodeFull = readFromMemory(pointer);

            int opCode = (int) (opCodeFull % 100); // Last 2 digits

            if (opCode == 99) {
                break;
            }

            IntComputerOp op = IntComputerOp.fromOpCode(opCode);

            int numberOfParameters = op.getNumberOfParameters();

            // Parameters taken from the tape, without change
            long[] subsequentElements = getSubsequentElements(numberOfParameters);

            int[] parameterModes = getParameterModes(numberOfParameters, opCodeFull / 100); // division by 100 excludes the 2 right-most digits

            long[] memoryAddresses = new long[numberOfParameters];
            for (int i = 0; i < numberOfParameters; i++) {
                memoryAddresses[i] = switch (parameterModes[i]) {
                    case 0 -> subsequentElements[i];                    // position mode
                    case 1 ->
                            -1;                                       // immediate mode never refers to memory addresses
                    case 2 -> subsequentElements[i] + relativeBase;     // relative mode
                    default -> throw new IllegalArgumentException("Invalid parameter mode: " + parameterModes[i]);
                };
            }

            // Parameter values, taking into consideration the parameter modes
            long[] parameterValues = getParameterValues(numberOfParameters, subsequentElements, parameterModes, memoryAddresses);

            boolean jumpFlag = false;

            if (op == IntComputerOp.ADJUST_RELATIVE_BASE) {
                relativeBase += parameterValues[0];
            } else if (op.producesAnIntValue()) {

                // Compute result
                long result = computeResult(op, inputSupplier, parameterValues);

                // Handle result
                handleResult(op, outputs, result, memoryAddresses);

            } else if (op.jumps()) {
                jumpFlag = op.computeJumpFlag(parameterValues);
            }

            if (jumpFlag) {
                // Sets the pointer by jumping to a specific position
                pointer = (int) parameterValues[op.getJumpIndex()];
            } else {
                // Advances the pointer to be immediately after the parameters of the instruction just executed
                // This includes the case of JUMP-type instructions where their jump condition failed
                pointer += op.getNumberOfParameters() + 1;
            }

        }

        return outputs;
    }

    private long[] getSubsequentElements(int numberOfParameters) {
        long[] parameters = new long[numberOfParameters];

        for (int i = 0; i < numberOfParameters; i++) {
            parameters[i] = tape.getOrDefault(pointer + 1 + i, 0L);
        }

        return parameters;
    }

    private static int[] getParameterModes(int numberOfParameters, long n) {
        int[] parameterModes = new int[numberOfParameters];
        for (int i = 0; i < numberOfParameters; i++) {
            parameterModes[i] = (int) (n % 10);
            n /= 10;
            if (n == 0) {
                break;
            }
        }
        return parameterModes;
    }

    private long[] getParameterValues(int numberOfParameters, long[] subsequentElements, int[] parameterModes, long[] memoryAddresses) {
        long[] parameterValues = new long[numberOfParameters];
        for (int i = 0; i < numberOfParameters; i++) {
            long subsequentElement = subsequentElements[i];
            long memoryAddress = memoryAddresses[i];
            parameterValues[i] = switch (parameterModes[i]) {
                case 0 -> readFromMemory(memoryAddress);    // position mode
                case 1 -> subsequentElement;                // immediate mode
                case 2 -> readFromMemory(memoryAddress);    // relative mode
                default -> throw new IllegalArgumentException("Invalid parameter mode: " + parameterModes[i]);
            };
        }

        return parameterValues;
    }

    private long computeResult(IntComputerOp op, LongSupplier inputs, long[] parameterValues) {
        long result;
        if (op.readsFromInput()) {
            result = inputs.getAsLong();
        } else {
            result = op.computeLongValue(parameterValues);
        }
        return result;
    }

    private void handleResult(IntComputerOp op, Deque<Long> outputs, long result,
                              long[] memoryAddresses) {
        if (op.writesToOutput()) {
            outputs.add(result);
        } else if (op.hasDestination()) {

            long destinationMemoryAddress = memoryAddresses[op.getDestinationParameterIndex()];
            writeToMemory(destinationMemoryAddress, result);

        }
    }

    private long readFromMemory(long memoryAddress) {
        if (memoryAddress < 0) {
            throw new IllegalArgumentException("Cannot read from a negative memory address: " + memoryAddress);
        }
        return tape.getOrDefault(memoryAddress, 0L);
    }

    public void writeToMemory(long memoryAddress, long value) {
        if (memoryAddress < 0) {
            throw new IllegalStateException("Trying to save to a negative memory address: " + memoryAddress);
        }

        tape.put(memoryAddress, value);
    }

    public ImmutableList<Long> getAndClearOutputs() {

        ImmutableList<Long> copy = ImmutableList.copyOf(outputs);
        outputs.clear();
        return copy;

    }

}
