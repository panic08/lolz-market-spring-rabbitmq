package ru.panic.template.dto;

import lombok.Getter;
@Getter
public class CoinProviderResponseDto {
    private CoinDto bitcoin;
    private CoinDto dogecoin;
    private CoinDto ethereum;
    private CoinDto litecoin;
    private CoinDto ripple;
    private CoinDto solana;
    private CoinDto tron;

    @Getter
    public static class CoinDto {
        private double usd;
        private double eur;
        private double rub;
        private double pln;
    }
}
