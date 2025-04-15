package thread.coordination;

import java.math.BigInteger;

public class ComplexCalculation {
    public BigInteger calculateResult(BigInteger base1, BigInteger power1, BigInteger base2, BigInteger power2) {
        BigInteger result;

        PowerCalculatingThread powerThread1 = new PowerCalculatingThread(base1, power1);
        PowerCalculatingThread powerThread2 = new PowerCalculatingThread(base2, power2);
        powerThread1.start();
        powerThread2.start();
        try {
            powerThread1.join();
            powerThread2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        result = powerThread1.getResult().add(powerThread2.getResult());
        return result;
    }

    private static class PowerCalculatingThread extends Thread {
        private BigInteger result = BigInteger.ONE;
        private BigInteger base;
        private BigInteger power;

        public PowerCalculatingThread(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
            result = base.pow(power.intValue());
        }

        public BigInteger getResult() {
            return result;
        }
    }
}
