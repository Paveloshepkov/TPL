package Labs.MPA;

public class TransitionPDA implements Comparable<TransitionPDA> {
    public String currentState;
    public Character inputSymbol;
    public Character stackTopSymbol;

    public TransitionPDA(String currentState, Character inputSymbol, Character stackTopSymbol) {
        this.currentState = currentState;
        this.inputSymbol = inputSymbol;
        this.stackTopSymbol = stackTopSymbol;
    }

    @Override
    public int compareTo(TransitionPDA other) {

        int currentStateComparison = this.currentState.compareTo(other.currentState);

        if (currentStateComparison != 0) {
            return currentStateComparison;
        }

        int inputSymbolComparison = this.inputSymbol.compareTo(other.inputSymbol);

        if (inputSymbolComparison != 0) {
            return inputSymbolComparison;
        }

        return this.stackTopSymbol.compareTo(other.stackTopSymbol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransitionPDA that = (TransitionPDA) o;

        if (!inputSymbol.equals(that.inputSymbol)) return false;
        if (!stackTopSymbol.equals(that.stackTopSymbol)) return false;
        return currentState.equals(that.currentState);
    }

    @Override
    public int hashCode() {
        int result = currentState.hashCode();
        result = 31 * result + inputSymbol.hashCode();
        result = 31 * result + stackTopSymbol.hashCode();
        return result;
    }
}
