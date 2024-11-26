package common.int_computer;

public enum IntComputerOp {

    ADD(3, true, false, true,
            2, -1, IntComputerAdvanceType.AFTER_PARAMETERS) {
        @Override
        public int computeIntValue(int[] parameterValues) {
            return parameterValues[0] + parameterValues[1];
        }

        @Override
        public long computeLongValue(long[] parameterValues) {
            return parameterValues[0] + parameterValues[1];
        }
    },
    MULTIPLY(3, true, false, true,
            2, -1, IntComputerAdvanceType.AFTER_PARAMETERS) {
        @Override
        public int computeIntValue(int[] parameterValues) {
            return parameterValues[0] * parameterValues[1];
        }

        @Override
        public long computeLongValue(long[] parameterValues) {
            return parameterValues[0] * parameterValues[1];
        }
    },
    READ_IN(1, true, false, true,
            0, -1, IntComputerAdvanceType.AFTER_PARAMETERS) {
        @Override
        public boolean readsFromInput() {
            return true;
        }
    },
    WRITE_OUT(1, true, false, false,
            -1, -1, IntComputerAdvanceType.AFTER_PARAMETERS) {
        @Override
        public boolean writesToOutput() {
            return true;
        }

        @Override
        public int computeIntValue(int[] parameterValues) {
            return parameterValues[0];
        }

        @Override
        public long computeLongValue(long[] parameterValues) {
            return parameterValues[0];
        }
    },
    JUMP_IF_TRUE(2, false, true, true,
            2, 1, IntComputerAdvanceType.JUMP) {
        @Override
        public boolean computeJumpFlag(int[] parameterValues) {
            return parameterValues[0] != 0;
        }

        @Override
        public boolean computeJumpFlag(long[] parameterValues) {
            return parameterValues[0] != 0;
        }
    },

    JUMP_IF_FALSE(2, false, true, true,
            2, 1, IntComputerAdvanceType.JUMP) {
        @Override
        public boolean computeJumpFlag(int[] parameterValues) {
            return parameterValues[0] == 0;
        }

        @Override
        public boolean computeJumpFlag(long[] parameterValues) {
            return parameterValues[0] == 0;
        }
    },
    LESS_THAN(3, true, false, true,
            2, -1, IntComputerAdvanceType.AFTER_PARAMETERS) {
        @Override
        public int computeIntValue(int[] parameterValues) {
            return parameterValues[0] < parameterValues[1] ? 1 : 0;
        }

        @Override
        public long computeLongValue(long[] parameterValues) {
            return parameterValues[0] < parameterValues[1] ? 1 : 0;
        }
    },
    EQUALS(3, true, false, true,
            2, -1, IntComputerAdvanceType.AFTER_PARAMETERS) {
        @Override
        public int computeIntValue(int[] parameterValues) {
            return parameterValues[0] == parameterValues[1] ? 1 : 0;
        }

        @Override
        public long computeLongValue(long[] parameterValues) {
            return parameterValues[0] == parameterValues[1] ? 1 : 0;
        }
    },

    ADJUST_RELATIVE_BASE(1, false, false, false,
            -1, -1, IntComputerAdvanceType.AFTER_PARAMETERS);

    public static IntComputerOp fromOpCode(int opCode) {
        return switch (opCode) {
            case 1 -> ADD;
            case 2 -> MULTIPLY;
            case 3 -> READ_IN;
            case 4 -> WRITE_OUT;
            case 5 -> JUMP_IF_TRUE;
            case 6 -> JUMP_IF_FALSE;
            case 7 -> LESS_THAN;
            case 8 -> EQUALS;
            case 9 -> ADJUST_RELATIVE_BASE;
            default -> throw new IllegalStateException("Invalid opCode: " + opCode);
        };
    }

    IntComputerOp(int numberOfParameters, boolean producesAnIntValue, boolean jumps, boolean hasDestination,
                  int destinationParameterIndex, int jumpIndex, IntComputerAdvanceType advanceType) {
        this.numberOfParameters = numberOfParameters;
        this.producesAnIntValue = producesAnIntValue;
        this.jumps = jumps;
        this.hasDestination = hasDestination;
        this.destinationParameterIndex = destinationParameterIndex;
        this.jumpIndex = jumpIndex;
        this.advanceType = advanceType;
    }

    private final int numberOfParameters;

    private final boolean producesAnIntValue;

    private final boolean jumps;
    private final IntComputerAdvanceType advanceType;

    private final boolean hasDestination;

    private final int destinationParameterIndex;

    private final int jumpIndex;

    public int getNumberOfParameters() {
        return numberOfParameters;
    }

    public boolean producesAnIntValue() {
        return producesAnIntValue;
    }

    public boolean jumps() {
        return jumps;
    }

    IntComputerAdvanceType getAdvanceType() {
        return advanceType;
    }

    public boolean hasDestination() {
        return hasDestination;
    }

    public int getDestinationParameterIndex() {
        return destinationParameterIndex;
    }

    public int getJumpIndex() {
        return jumpIndex;
    }

    public boolean readsFromInput() {
        return false;
    }

    public boolean writesToOutput() {
        return false;
    }

    public int computeIntValue(int[] parameterValues) {
        throw new UnsupportedOperationException("Not an instruction that computes a value: " + this);
    }

    public long computeLongValue(long[] parameterValues) {
        throw new UnsupportedOperationException("Not an instruction that computes a value: " + this);
    }

    public boolean computeJumpFlag(int[] parameterValues) {
        throw new UnsupportedOperationException("Not a jump instruction: " + this);
    }

    public boolean computeJumpFlag(long[] parameterValues) {
        throw new UnsupportedOperationException("Not a jump instruction: " + this);
    }


}
