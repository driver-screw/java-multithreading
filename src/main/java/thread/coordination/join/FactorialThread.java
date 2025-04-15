package thread.coordination.join;

import java.math.BigInteger;

public class FactorialThread extends Thread {
    private long inputNumber;
    private BigInteger result = BigInteger.ZERO;
    private boolean isFinished = false;

    public FactorialThread(long inputNumber) {
        this.inputNumber = inputNumber;
    }

    @Override
    public void run(){
        result = factorial(inputNumber);
        isFinished = true;

    }

    public BigInteger factorial(long n){
        BigInteger tempResult = BigInteger.ONE;
        for (long i = n; i >0; i--) {
            if(Thread.interrupted()){
                    System.out.println("Prematurely interrupted computation");
                    return BigInteger.ZERO;
                }
            tempResult = tempResult.multiply(new BigInteger(String.valueOf(i)));
        }
        return tempResult;
    }

    public BigInteger getResult() {
        return result;
    }

    public boolean isFinished() {
        return isFinished;
    }
}
