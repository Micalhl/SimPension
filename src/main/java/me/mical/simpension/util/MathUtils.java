/*
 *  Copyright (C) <2023>  <南外丶仓鼠>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.mical.simpension.util;

import java.math.RoundingMode;
import java.util.*;

public class MathUtils {
    private static final int[] operatePriority = new int[]{0, 3, 2, 1, -1, 1, 0, 2};
    private static final List<String> symbols = Arrays.asList(">=", "<=", ">", "<", "=", "!=");
    private static Stack<String> postfixStack = new Stack<>();
    private static Stack<Character> opStack = new Stack<>();

    /**
     * <p>字符串替换</p>
     * <p>参数格式：两个为一组，表示将前一个替换为后一个</p>
     * @see <a href="https://github.com/SummerIceBearStudio/NereusOpusBase/blob/main/src/main/java/hamsteryds/nereusopus/utils/api/StringUtils.java">...</a>
     *
     * @param origin 原始字符串
     * @param params 参数
     * @return {@link String} - 替换后的字符串
     */
    @SafeVarargs
    public static <E> String replace(String origin, E... params) {
        for (int i = 0; i < params.length; i += 2) {
            origin = origin.replace(String.valueOf(params[i]), String.valueOf(params[i + 1]));
        }
        return origin;
    }

    /**
     * 字符串替换
     * @see <a href="https://github.com/SummerIceBearStudio/NereusOpusBase/blob/main/src/main/java/hamsteryds/nereusopus/utils/api/StringUtils.java">...</a>
     *
     * @param origin 原始字符串
     * @param params 参数
     * @return {@link String} - 替换后的字符串
     */
    public static <E> String replace(String origin, Map<String, String> params) {
        for (String key : params.keySet()) {
            origin = origin.replace(key, params.get(key));
        }
        return origin;
    }

    public static boolean isTrue(String expression) {
        for (String symbol : symbols) {
            if (expression.contains(symbol)) {
                String[] splited = expression.split(symbol);
                switch (symbol) {
                    case ">=":
                        return calculate(splited[0]) >= calculate(splited[1]);
                    case "<=":
                        return calculate(splited[0]) <= calculate(splited[1]);
                    case ">":
                        return calculate(splited[0]) > calculate(splited[1]);
                    case "<":
                        return calculate(splited[0]) < calculate(splited[1]);
                    case "=":
                        return calculate(splited[0]) == calculate(splited[1]);
                    case "!=":
                        return calculate(splited[0]) != calculate(splited[1]);
                    default:
                        return true;
                }
            }
        }
        return true;
    }

    @SafeVarargs
    public static <E> double calculate(String expression, E... params) {
        try {
            expression = replace(expression, params);
            expression = expression.replace(" ", "");
            return Calculator.calculate(Calculator.transform(expression + "+0"));
        } catch (Exception exception) {
            postfixStack = new Stack<>();
            opStack = new Stack<>();
            throw exception;
        }
    }

    public static double calculate(String expression, Map<String, String> params) {
        try {
            expression = replace(expression, params);
            expression = expression.replace(" ", "");
            return Calculator.calculate(Calculator.transform(expression + "+0"));
        } catch (Exception exception) {
            postfixStack = new Stack<>();
            opStack = new Stack<>();
            throw exception;
        }
    }

    public static String numToRoman(int number, boolean ignoreI) {
        StringBuilder rNumber = new StringBuilder();
        int[] aArray = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] rArray = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X",
                "IX", "V", "IV", "I"};
        if (number < 1 || number > 3999) {
            rNumber = new StringBuilder("-1");
        } else {
            for (int i = 0; i < aArray.length; i++) {
                while (number >= aArray[i]) {
                    rNumber.append(rArray[i]);
                    number -= aArray[i];
                }
            }
        }
        return rNumber.toString().equalsIgnoreCase("I") && ignoreI ? "" : rNumber.toString();
    }

    private static class Calculator {

        public static String transform(String expression) {
            char[] arr = expression.toCharArray();
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == '-') {
                    if (i == 0) {
                        arr[i] = '~';
                    } else {
                        char c = arr[i - 1];
                        if (c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == 'E' || c == 'e') {
                            arr[i] = '~';
                        }
                    }
                }
            }
            if (arr[0] == '~' || arr[1] == '(') {
                arr[0] = '-';
                return "0" + new String(arr);
            } else {
                return new String(arr);
            }
        }

        public static double calculate(String expression) {
            Stack<String> resultStack = new Stack<>();
            prepare(expression);
            Collections.reverse(postfixStack);
            String firstValue, secondValue, currentValue;
            while (!postfixStack.isEmpty()) {
                currentValue = postfixStack.pop();
                if (!isOperator(currentValue.charAt(0))) {
                    currentValue = currentValue.replace("~", "-");
                    resultStack.push(currentValue);
                } else {
                    secondValue = resultStack.pop();
                    firstValue = resultStack.pop();

                    firstValue = firstValue.replace("~", "-");
                    secondValue = secondValue.replace("~", "-");

                    String tempResult = calculate(firstValue, secondValue, currentValue.charAt(0));
                    resultStack.push(tempResult);
                }
            }
            return Double.parseDouble(resultStack.pop());
        }

        public static void prepare(String expression) {
            opStack.push(',');
            char[] arr = expression.toCharArray();
            int currentIndex = 0;
            int count = 0;
            char currentOp, peekOp;
            for (int i = 0; i < arr.length; i++) {
                currentOp = arr[i];
                if (isOperator(currentOp)) {
                    if (count > 0) {
                        postfixStack.push(new String(arr, currentIndex, count));
                    }
                    peekOp = opStack.peek();
                    if (currentOp == ')') {
                        while (opStack.peek() != '(') {
                            postfixStack.push(String.valueOf(opStack.pop()));
                        }
                        opStack.pop();
                    } else {
                        while (currentOp != '(' && peekOp != ',' && compare(currentOp, peekOp)) {
                            postfixStack.push(String.valueOf(opStack.pop()));
                            peekOp = opStack.peek();
                        }
                        opStack.push(currentOp);
                    }
                    count = 0;
                    currentIndex = i + 1;
                } else {
                    count++;
                }
            }
            if (count > 1 || (count == 1 && !isOperator(arr[currentIndex]))) {
                postfixStack.push(new String(arr, currentIndex, count));
            }

            while (opStack.peek() != ',') {
                postfixStack.push(String.valueOf(opStack.pop()));
            }
        }

        public static boolean isOperator(char c) {
            return c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')';
        }

        public static boolean compare(char cur, char peek) {
            return operatePriority[(peek) - 40] >= operatePriority[(cur) - 40];
        }

        public static String calculate(String firstValue, String secondValue, char currentOp) {
            switch (currentOp) {
                case '+':
                    return String.valueOf(ArithHelper.add(firstValue, secondValue));
                case '-':
                    return String.valueOf(ArithHelper.sub(firstValue, secondValue));
                case '*':
                    return String.valueOf(ArithHelper.mul(firstValue, secondValue));
                case '/':
                    return String.valueOf(ArithHelper.div(firstValue, secondValue));
                default:
                    return "";
            }
        }

        public static class ArithHelper {
            public static final int DEF_DIV_SCALE = 16;

            public static double add(String v1, String v2) {
                java.math.BigDecimal b1 = new java.math.BigDecimal(v1);
                java.math.BigDecimal b2 = new java.math.BigDecimal(v2);
                return b1.add(b2).doubleValue();
            }

            public static double sub(String v1, String v2) {
                java.math.BigDecimal b1 = new java.math.BigDecimal(v1);
                java.math.BigDecimal b2 = new java.math.BigDecimal(v2);
                return b1.subtract(b2).doubleValue();
            }

            public static double mul(String v1, String v2) {
                java.math.BigDecimal b1 = new java.math.BigDecimal(v1);
                java.math.BigDecimal b2 = new java.math.BigDecimal(v2);
                return b1.multiply(b2).doubleValue();
            }

            public static double div(String v1, String v2) {
                java.math.BigDecimal b1 = new java.math.BigDecimal(v1);
                java.math.BigDecimal b2 = new java.math.BigDecimal(v2);
                return b1.divide(b2, DEF_DIV_SCALE, RoundingMode.HALF_UP).doubleValue();
            }
        }
    }
}
