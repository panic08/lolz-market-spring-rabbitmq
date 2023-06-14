package ru.panic.template.dto.crypto;

import lombok.Getter;

import java.util.List;
@Getter
public class MaticResponseDto {
    private List<ResponseDto> responseDtos;
    @Getter
    public static class ResponseDto {
        public String blockHash;
        public int blockNumber;
        public String from;
        public int gas;
        public long gasPrice;
        public String maxFeePerGas;
        public String maxPriorityFeePerGas;
        public String input;
        public int nonce;
        public String to;
        public int transactionIndex;
        public String value;
        public String type;
        public List<Object> accessList;
        public String chainId;
        public Object contractAddress;
        public String cumulativeGasUsed;
        public String effectiveGasPrice;
        public String gasUsed;
        public List<Log> logs;
        public String logsBloom;
        public boolean status;
        public String transactionHash;
        public String hash;
        public long timestamp;
        @Getter
        public static class Log {
            public String address;
            public List<String> topics;
            public String data;
            public int blockNumber;
            public String transactionHash;
            public int transactionIndex;
            public String blockHash;
            public int logIndex;
            public boolean removed;
        }
    }
}
