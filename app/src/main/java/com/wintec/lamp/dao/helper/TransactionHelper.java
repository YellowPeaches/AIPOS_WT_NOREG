package com.wintec.lamp.dao.helper;

import com.wintec.lamp.base.MyApp;
import com.wintec.lamp.dao.entity.Transaction;

public class TransactionHelper {
    public static void insert(Transaction transaction) {
        MyApp.getDaoInstant().getTransactionDao().insert(transaction);
    }

}
