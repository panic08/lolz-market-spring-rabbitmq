package ru.panic.template.dto.crypto;

import lombok.Getter;

import java.util.List;
@Getter
public class EthereumResponseDto {
    public String status;
    public String message;
    public List<TransactionDto> result;
    @Getter
    public static class TransactionDto {
        public String blockNumber;
        public String timeStamp;
        public String hash;
        public String nonce;
        public String blockHash;
        public String transactionIndex;
        public String from;
        public String to;
        public String value;
        public String gas;
        public String gasPrice;
        public String isError;
        public String txreceipt_status;
        public String input;
        public String contractAddress;
        public String cumulativeGasUsed;
        public String gasUsed;
        public String confirmations;
        public String methodId;
        public String functionName;
}
}
