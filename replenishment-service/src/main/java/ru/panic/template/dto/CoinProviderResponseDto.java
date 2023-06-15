package ru.panic.template.dto;

import lombok.Getter;
@Getter
public class CoinProviderResponseDto {
    private CoinDto bitcoin;
    private CoinDto ripple;
    private CoinDto ethereum;
    private CoinDto litecoin;
    private CoinDto maticNetwork;
    private CoinDto theOpenNetwork;
    private CoinDto tether;
    private CoinDto tron;

    @Getter
    public static class CoinDto {
        private double usd;
        private double eur;
        private double rub;
        private double pln;
    }
}
