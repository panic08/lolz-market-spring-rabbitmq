package ru.panic.template.dto.crypto;

import lombok.Getter;

import java.util.List;

@Getter
public class TronResponseDto {
    public List<Data> data;
    @Getter
    public static class Data {
        public Ret[] ret;
        public String[] signature;
        public String txID;
        public int net_usage;
        public String raw_data_hex;
        public int net_fee;
        public int energy_usage;
        public int blockNumber;
        public long block_timestamp;
        public int energy_fee;
        public int energy_usage_total;
        public RawData raw_data;
        public Object[] internal_transactions;
    }
    @Getter
    public static class Ret {
        public String contractRet;
        public int fee;
    }
    @Getter
    public static class RawData {
        public List<Contract> contract;
        public String ref_block_bytes;
        public String ref_block_hash;
        public long expiration;
        public long timestamp;
    }
    @Getter
    public static class Contract {
        public Parameter parameter;
        public String type;
    }
    @Getter
    public static class Parameter {
        public Value value;
        public String type_url;
    }
    @Getter
    public static class Value {
        public long amount;
        public String owner_address;
        public String to_address;
        public String asset_name;
    }
}
