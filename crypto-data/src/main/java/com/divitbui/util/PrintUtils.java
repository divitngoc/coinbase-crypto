package com.divitbui.util;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import com.divitbui.model.OrderBook;

import dnl.utils.text.table.TextTable;

public final class PrintUtils {

    private static final String[] HEADERS = new String[] { "Ask Quantity", "Ask Price", "Level", "Bid price", "Bid Quantity" };

    private PrintUtils() {}

    /**
     * 
     * Print orderbook for a specific product id in a nice format up to the
     * maxPriceLevel, see below for example:
     * 
     * <pre>
     * printStatus(orderbook, 10);
    ======================== BTC-USD ========================
    _________________________________________________________
    | Ask Quantity| Ask Price| Level| Bid price| Bid Quantity|
    |========================================================|
    | 0.37229941  | 34474.10 | 1    | 34474.09 | 0.90000000  |
    | 0.01260000  | 34474.78 | 2    | 34470.20 | 0.04899046  |
    | 0.01260000  | 34474.79 | 3    | 34470.19 | 0.26000000  |
    | 0.04899046  | 34476.99 | 4    | 34469.88 | 0.47000000  |
    | 0.26000000  | 34477.00 | 5    | 34469.82 | 1.25802174  |
    | 0.09966679  | 34477.16 | 6    | 34467.50 | 0.07619389  |
    | 0.02259990  | 34479.12 | 7    | 34466.76 | 0.01428400  |
    | 0.01000000  | 34479.13 | 8    | 34464.92 | 0.10192530  |
    | 1.90000000  | 34482.00 | 9    | 34461.31 | 1.72331636  |
    | 0.64659440  | 34482.64 | 10   | 34461.30 | 0.66580424  |
     * </pre>
     * 
     */
    public static void printStatus(final OrderBook orderBook, final int maxPriceLevel) {
        Iterator<Entry<BigDecimal, BigDecimal>> buyIt = orderBook.getBuyOrders().entrySet().iterator();
        Iterator<Entry<BigDecimal, BigDecimal>> sellit = orderBook.getSellOrders().entrySet().iterator();

        final String[][] data = new String[maxPriceLevel][];
        int level = 0;
        while (level++ < maxPriceLevel) {
            List<String> row = new ArrayList<>();
            row.addAll(getDataFromOrders(sellit));
            row.add(String.valueOf(level));

            List<String> buyData = getDataFromOrders(buyIt);
            Collections.reverse(buyData);
            row.addAll(buyData);

            data[level - 1] = row.toArray(new String[0]);
        }

        PrintStream ps = System.out;
        ps.print("\n======================== " + orderBook.getProductId() + " ========================\n");
        new TextTable(HEADERS, data).printTable(ps, 0);
    }

    private static List<String> getDataFromOrders(final Iterator<Entry<BigDecimal, BigDecimal>> it) {
        List<String> data = new ArrayList<>();
        if (it.hasNext()) {
            Entry<BigDecimal, BigDecimal> entryLevel = it.next();
            data.add(entryLevel.getValue().toPlainString());
            data.add(entryLevel.getKey().toPlainString());
        } else {
            addEmptyDataOrder(data);
        }
        return data;
    }

    private static void addEmptyDataOrder(final List<String> row) {
        IntStream.range(0, HEADERS.length / 2).forEach(i -> row.add("-"));
    }
}
