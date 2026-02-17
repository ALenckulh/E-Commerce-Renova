package com.retificarenova.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utilitário de Dinheiro da Aplicação
 * Fornece métodos para formatação, validação e cálculos de valores monetários
 */
public class MoneyUtils {

    private static final int DECIMAL_PLACES = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final Locale LOCALE_BR = new Locale("pt", "BR");

    /**
     * Formata um valor monetário para o padrão brasileiro (R$ 1.234,56)
     */
    public static String formatMoney(BigDecimal value) {
        if (value == null) {
            return "R$ 0,00";
        }
        NumberFormat formatter = NumberFormat.getCurrencyInstance(LOCALE_BR);
        return formatter.format(value);
    }

    /**
     * Formata um valor monetário (double) para o padrão brasileiro
     */
    public static String formatMoney(Double value) {
        if (value == null) {
            return "R$ 0,00";
        }
        return formatMoney(BigDecimal.valueOf(value));
    }

    /**
     * Arredonda um valor para 2 casas decimais
     */
    public static BigDecimal roundMoney(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value.setScale(DECIMAL_PLACES, ROUNDING_MODE);
    }

    /**
     * Calcula o desconto em percentual
     * Exemplo: desconto de 10% sobre 100 = 10
     */
    public static BigDecimal calculatePercentageDiscount(BigDecimal originalValue, BigDecimal percentageDiscount) {
        if (originalValue == null || percentageDiscount == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal discount = originalValue.multiply(percentageDiscount)
                .divide(BigDecimal.valueOf(100), DECIMAL_PLACES, ROUNDING_MODE);
        return roundMoney(discount);
    }

    /**
     * Calcula o valor final após aplicar desconto percentual
     */
    public static BigDecimal applyPercentageDiscount(BigDecimal originalValue, BigDecimal percentageDiscount) {
        if (originalValue == null) {
            return BigDecimal.ZERO;
        }
        if (percentageDiscount == null || percentageDiscount.compareTo(BigDecimal.ZERO) <= 0) {
            return originalValue;
        }
        BigDecimal discount = calculatePercentageDiscount(originalValue, percentageDiscount);
        return roundMoney(originalValue.subtract(discount));
    }

    /**
     * Calcula o valor final após aplicar desconto fixo
     */
    public static BigDecimal applyFixedDiscount(BigDecimal originalValue, BigDecimal fixedDiscount) {
        if (originalValue == null) {
            return BigDecimal.ZERO;
        }
        if (fixedDiscount == null || fixedDiscount.compareTo(BigDecimal.ZERO) <= 0) {
            return originalValue;
        }
        BigDecimal result = originalValue.subtract(fixedDiscount);
        return roundMoney(result.max(BigDecimal.ZERO)); // Garante que não seja negativo
    }

    /**
     * Calcula taxa/imposto sobre um valor
     * Exemplo: taxa de 10% sobre 100 = 10
     */
    public static BigDecimal calculateTax(BigDecimal value, BigDecimal taxPercentage) {
        return calculatePercentageDiscount(value, taxPercentage);
    }

    /**
     * Calcula o valor final com taxa incluída
     */
    public static BigDecimal applyTax(BigDecimal value, BigDecimal taxPercentage) {
        if (value == null || taxPercentage == null || taxPercentage.compareTo(BigDecimal.ZERO) <= 0) {
            return value;
        }
        BigDecimal tax = calculateTax(value, taxPercentage);
        return roundMoney(value.add(tax));
    }

    /**
     * Valida se um valor monetário é válido (positivo e com no máximo 2 casas decimais)
     */
    public static boolean isValidMoney(BigDecimal value) {
        if (value == null) {
            return false;
        }
        return value.compareTo(BigDecimal.ZERO) >= 0 && 
               value.scale() <= DECIMAL_PLACES;
    }

    /**
     * Compara dois valores monetários com tolerância de arredondamento
     */
    public static boolean isMoneysEqual(BigDecimal value1, BigDecimal value2) {
        if (value1 == null && value2 == null) {
            return true;
        }
        if (value1 == null || value2 == null) {
            return false;
        }
        return roundMoney(value1).compareTo(roundMoney(value2)) == 0;
    }

    /**
     * Converte String para BigDecimal de forma segura
     */
    public static BigDecimal stringToBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
