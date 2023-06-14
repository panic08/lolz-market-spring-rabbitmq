package ru.panic.template.dto.crypto;

import lombok.Getter;

import java.util.List;

@Getter
public class BitcoinResponseDto {
    public String hash160;
    public String address;
    public int n_tx;
    public int n_unredeemed;
    public int total_received;
    public int total_sent;
    public int final_balance;
    public List<TransactionDTO> txs;
    @Getter
    public static class TransactionDTO {
        public String hash;
        public int ver;
        public int vin_sz;
        public int vout_sz;
        public int size;
        public int weight;
        public int fee;
        public String relayed_by;
        public int lock_time;
        public long tx_index;
        public boolean double_spend;
        public long time;
        public int block_index;
        public int block_height;
        public List<InputDTO> inputs;
        public List<OutputDTO> out;
        public int result;
        public int balance;
    }
    @Getter
    public static class InputDTO {
        public long sequence;
        public String witness;
        public String script;
        public int index;
        public PrevOutDTO prev_out;
    }
    @Getter
    public static class PrevOutDTO {
        public String addr;
        public int n;
        public String script;
        public List<SpendingOutpointDTO> spending_outpoints;
        public boolean spent;
        public long tx_index;
        public int type;
        public int value;
    }
    @Getter
    public static class SpendingOutpointDTO {
        public int n;
        public long tx_index;
    }
    @Getter
    public static class OutputDTO {
        public int type;
        public boolean spent;
        public int value;
        public List<SpendingOutpointDTO> spending_outpoints;
        public int n;
        public long tx_index;
        public String script;
        public String addr;
    }
}
