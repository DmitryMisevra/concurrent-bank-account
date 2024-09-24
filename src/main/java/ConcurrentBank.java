import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentBank {
    private final Map<Integer, BankAccount> accounts;

    public ConcurrentBank() {
        this.accounts = new ConcurrentHashMap<>();
    }

    public BankAccount createAccount(int balance) {
        BankAccount account = new BankAccount(balance);
        accounts.put(account.getId(), account);
        return account;
    }

    public void transfer(BankAccount from, BankAccount to, int amount) {
        BankAccount firstLock, secondLock;
        if (from.getId() < to.getId()) {
            firstLock = from;
            secondLock = to;
        } else {
            firstLock = to;
            secondLock = from;
        }

        firstLock.getLock().lock();
        secondLock.getLock().lock();

        try {
            from.withdraw(amount);
            to.deposit(amount);
        } finally {
            secondLock.getLock().unlock();
            firstLock.getLock().unlock();
        }
    }

    public int getTotalBalance() {
        List<BankAccount> sortedAccounts = accounts.values().stream()
                .sorted(Comparator.comparingInt(BankAccount::getId))
                .toList();

        for (BankAccount account : sortedAccounts) {
            account.getLock().lock();
        }

        try {
            int total = 0;
            for (BankAccount account : sortedAccounts) {
                total += account.getBalance();
            }
            return total;
        } finally {
            for (int i = sortedAccounts.size() - 1; i >= 0; i--) {
                sortedAccounts.get(i).getLock().unlock();
            }
        }
    }
}
