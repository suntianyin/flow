package com.apabi.flow.processing.util;

public class IsbnCheck {

    public static void main(String[] args) {
        String s="9787202126660";
        boolean b = IsbnCheck.CheckISBN(s);
        System.out.println(b);
    }

    public static boolean CheckISBN(String isbn) {
        try {
            int checkNum = 0;
            isbn=isbn.replaceAll("[-\\s]","");
            if (isbn.length() == 10) {
                int start = 10;
                int total = 0;
                for (int i = 0; i < isbn.length() - 1; i++) {
                    total += GetISBNAt(isbn, i, false) * start--;
                }
                checkNum = total % 11;
                if (checkNum > 0) {
                    checkNum = 11 - checkNum;
                }
            } else {
                int total = 0;
                for (int i = 0; i < isbn.length() - 1; i++) {
                    total += GetISBNAt(isbn, i, false) * (i % 2 == 0 ? 1 : 3);
                }
                checkNum = total % 10;
                if (checkNum > 0) {
                    checkNum = 10 - checkNum;
                }
            }

            return GetISBNAt(isbn, isbn.length() - 1, true) == checkNum;
        } catch (Exception e) {
            return false;
        }

    }
    private static int GetISBNAt(String isbn, int index, boolean xEnable)
    {

        char c = isbn.charAt(index);
        int n = c - '0';
        if (n < 0 || n > 9)
        {
            if (xEnable && (c == 'X' || c == 'x'))
            {
                n = 10;
            }
        }
        return n;
    }

}
