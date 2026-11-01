final class Balance {
    private final long amt;
    private final Currency curr;

    public Balance() {
        this.amt = 0;
        this.curr = Currency.RUB;
    }

    public Balance(long amt) {
        if (amt < 0) {
            throw new IllegalArgumentException("Сумма не может быть отрицательной");
        }
        this.amt = amt;
        this.curr = Currency.RUB;
    }

    public Balance(long amt, Currency curr) {
        if (amt < 0) {
            throw new IllegalArgumentException("Сумма не может быть отрицательной");
        }
        if (curr == null) {
            throw new IllegalArgumentException("Валюта не может быть null");
        }
        this.amt = amt;
        this.curr = curr;
    }

    public long getAmt() {
        return amt;
    }

    public Currency getCurr() {
        return curr;
    }

    public Balance add(long incoming_amt) {
        if (incoming_amt < 0) {
            throw new IllegalArgumentException("Сумма не может быть отрицательной");
        }
        return new Balance(this.amt + incoming_amt, this.curr);
    }

    public Balance substract(long outgoing_amt) {
        if (outgoing_amt < 0) {
            throw new IllegalArgumentException("Сумма не может быть отрицательной");
        }
        if (amt - outgoing_amt < 0) {
            throw new IllegalArgumentException("Недостаточно средств для осуществления перевода");
        }
        return new Balance(this.amt - outgoing_amt, this.curr);
    }

    public Balance convert(Currency new_curr) {
        if (new_curr == null) {
            throw new IllegalArgumentException("Валюта не может быть null");
        }
        if (new_curr == this.curr)
            return this;
        else return new Balance(this.amt*this.curr.getPriceInRublesKopecks()/ new_curr.getPriceInRublesKopecks(), new_curr);
    }

    void show() {
        System.out.println(amt + curr.getSymbol());
    }

    @Override
    public String toString() {
        return String.format("Баланс: %s | Валюта: %s", this.getAmt() / this.getCurr().getPriceInRublesKopecks(), this.getCurr());
    }
}

final class BankAccount {
    private Balance balance;
    private final boolean is_blocked = false;
    private final long monthly_transaction_transfer_current_state;
    private final long single_transaction_transfer_limit = 10000000;
    private final long monthly_transaction_transfer_limit = 1000000000;

    public BankAccount() {
        this.balance = new Balance();
        this.monthly_transaction_transfer_current_state = 0;
    }

    public BankAccount(Balance balance) {
        this.balance = balance;
        this.monthly_transaction_transfer_current_state = 0;
    }

    public BankAccount(Balance balance, long monthly_transaction_transfer_current_state) {
        this.balance = balance;
        this.monthly_transaction_transfer_current_state = monthly_transaction_transfer_current_state;
    }

    public Balance getBalance() {
        return balance;
    }

    public boolean is_blocked() {
        return is_blocked;
    }

    public long getMonthly_transaction_transfer_current_state() {
        return monthly_transaction_transfer_current_state;
    }

    public long getSingle_transaction_transfer_limit() {
        return single_transaction_transfer_limit;
    }

    public long getMonthly_transaction_transfer_limit() {
        return monthly_transaction_transfer_limit;
    }

    public BankAccount add_monthly_transaction_transfer_current_state(long amt) {
        if (amt < 0) {
            throw new IllegalArgumentException("Сумма не может быть отрицательной");
        }
        return new BankAccount(this.balance, this.monthly_transaction_transfer_current_state + amt);
    }

    public void check_transaction_limits(long out_amt) {
        if (out_amt > getSingle_transaction_transfer_limit()) {
            throw new IllegalStateException("Перевод средств невозможен, превышен предел единоразового перевода");
        }
        else if (out_amt + getMonthly_transaction_transfer_current_state() > getMonthly_transaction_transfer_limit()) {
            throw new IllegalStateException("Перевод средств невозможен, превышен предел суммы переводов за месяц");
        }
    }

    public void transaction_transfer(BankAccount target, long transfer_amt) {
        if (this == target) {
            throw new IllegalStateException("Нельзя перевести средства на этот же счёт");
        }
        if (this.is_blocked()) {
            throw new IllegalStateException("Ваш аккаунт заблокирован. Транзакции не доступны." +
                    " Обратитесь в поддержку для перехода к процедуре восстановления");
        }
        if (target.is_blocked()) {
            throw new IllegalStateException("Целевой аккаунт заблокирован. Транзакции не доступны." +
                    " Обратитесь в поддержку для перехода к процедуре восстановления");
        }
        this.check_transaction_limits(transfer_amt);

        this.balance = this.balance.substract(transfer_amt);
        target.balance = target.balance.add(transfer_amt);
    }

    @Override
    public String toString() {
        return String.format("Баланс: %s" + this.balance.getCurr().getSymbol() + " | Статус: %s | Переведенная сумма за месяц: %s",
                             balance.getAmt() / this.balance.getCurr().getPriceInRublesKopecks(), is_blocked ? "Заблокирован" : "Активен", this.getMonthly_transaction_transfer_current_state());
    }
}

public class Main {
    public static void main(String[] args) {
        Balance b1 = new Balance();
        Balance b2 = new Balance(1000000);
        Balance b3 = new Balance(235346356, Currency.RUB);

        System.out.println(b1);
        System.out.println(b2);
        System.out.println(b3);
        System.out.println();

        b2 = b2.add(123);
        System.out.println(b2);
        b2 = b2.substract(100);
        System.out.println(b2);
        System.out.println();

        BankAccount ba1 = new BankAccount(b1);
        BankAccount ba2 = new BankAccount(b2);
        BankAccount ba3 = new BankAccount(b3);

        System.out.println(ba1);
        System.out.println(ba2);
        System.out.println(ba3);
        System.out.println();

        long transfer_amt = 1000;
        ba2.transaction_transfer(ba3, transfer_amt);
        ba2 = ba2.add_monthly_transaction_transfer_current_state(transfer_amt);
        System.out.println(ba2);
        System.out.println(ba3);
        System.out.println();

    }
}