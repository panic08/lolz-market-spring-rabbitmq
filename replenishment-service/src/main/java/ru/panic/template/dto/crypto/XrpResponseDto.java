package ru.panic.template.dto.crypto;

import lombok.Getter;

import java.util.List;
@Getter
public class XrpResponseDto {
    public Result result;
    public List<Warning> warnings;
    @Getter
    public static class Result {
        public AccountResult account;
        public int ledger_index_min;
        public int ledger_index_max;
        public List<Transaction> transactions;
        public boolean validated;
        public Marker marker;
        public int limit;
        public String status;
    }
    @Getter
    public static class AccountResult {
        public String account;
        public int ledger_index_min;
        public int ledger_index_max;
        public List<Transaction> transactions;
        public boolean validated;
        public Marker marker;
        public int limit;
        public String status;
    }
    @Getter
    public static class Transaction {
        public Meta meta;
        public Tx tx;
        public boolean validated;
    }
    @Getter
    public static class Meta {
        public List<AffectedNode> AffectedNodes;
        public int TransactionIndex;
        public String TransactionResult;
        public String delivered_amount;
    }
    @Getter
    public static class AffectedNode {
        public ModifiedNode ModifiedNode;
    }
    @Getter
    public static class ModifiedNode {
        public FinalFields FinalFields;
        public String LedgerEntryType;
        public String LedgerIndex;
        public PreviousFields PreviousFields;
        public String PreviousTxnID;
        public int PreviousTxnLgrSeq;
    }
    @Getter
    public static class FinalFields {
        public String Account;
        public String Balance;
        public int Flags;
        public int OwnerCount;
        public int Sequence;
    }
    @Getter

    public static class PreviousFields {
        public String Balance;
        public int Sequence;
    }
    @Getter
    public static class Tx {
        public String Account;
        public String Amount;
        public String Destination;
        public String Fee;
        public int Flags;
        public int LastLedgerSequence;
        public int Sequence;
        public String SigningPubKey;
        public String TransactionType;
        public String TxnSignature;
        public String hash;
        public int ledger_index;
        public long date;
    }

    public static class Marker {
        public int ledger;
        public int seq;
    }

    public static class Warning {
        public int id;
        public String message;
    }
}
