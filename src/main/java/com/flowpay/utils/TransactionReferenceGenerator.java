package com.flowpay.utils;

import java.util.UUID;

public final class TransactionReferenceGenerator {
    private static final String PREFIX  = "TXN-";

    private TransactionReferenceGenerator(){
    }

    public static String generator() {
        String randomPart = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0,12)
                .toUpperCase();
        return PREFIX + randomPart;
    }
}
