package ru.shmvsky;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

//Напишите программу, которая создаст два потока, которые по очереди будут выводить числа.
//Первый поток чётные числа, второй потом нечётные.
public class MultithreadPrinter {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private Boolean turnEven = true;

    public void run() {

        var evenPrinter = new EvenPrinter();
        var oddPrinter = new OddPrinter();

        try {
            long startTime = System.currentTimeMillis();

            evenPrinter.start();
            oddPrinter.start();

            evenPrinter.join();
            oddPrinter.join();

            long executionTime = System.currentTimeMillis() - startTime;

            System.out.println("Execution time: " + executionTime);

        } catch (InterruptedException e) {
            System.out.println("Something went wrong");
        }

    }

    private class OddPrinter extends Thread {

        @Override
        public void run() {
            for (int i = 1; i < 100; i += 2) {
                lock.lock();
                try {
                    while (turnEven) {
                        condition.await();
                    }
                    System.out.println("ODD:  " + i);
                    turnEven = true;
                    condition.signalAll();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            }
        }

    }

    private class EvenPrinter extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < 100; i += 2) {
                lock.lock();
                try {
                    while (!turnEven) {
                        condition.await();
                    }
                    System.out.println("EVEN: " + i);
                    turnEven = false;
                    condition.signalAll();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            }
        }
    }

}