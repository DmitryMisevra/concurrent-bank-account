import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount {
    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    private final int id;
    private final Lock lock;

    public int getId() {
        return id;
    }

    public Lock getLock() {
        return lock;
    }

    private AtomicInteger balance;

    public BankAccount(int balance) {
        this.balance = new AtomicInteger(balance);
        this.id = idGenerator.incrementAndGet();
        this.lock = new ReentrantLock();
    }

    public int deposit(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        lock.lock();
        try {
            return balance.addAndGet(amount);
        } finally {
            lock.unlock();
        }
    }

    public int withdraw(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        lock.lock();
        try {
            if (balance.get() < amount) {
                throw new IllegalArgumentException("Insufficient balance");
            }
            return balance.addAndGet(-amount);
        } finally {
            lock.unlock();
        }
    }

    public int getBalance() {
        lock.lock();
        try {
            return balance.get();
        } finally {
            lock.unlock();
        }
    }
}
