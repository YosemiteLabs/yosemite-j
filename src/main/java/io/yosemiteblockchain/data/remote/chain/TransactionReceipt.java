package io.yosemiteblockchain.data.remote.chain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionReceipt {

//    enum status_enum {
//        executed  = 0, ///< succeed, no error handler executed
//        soft_fail = 1, ///< objectively failed (not executed), error handler executed
//        hard_fail = 2, ///< objectively failed and error handler objectively failed thus no state change
//        delayed   = 3  ///< transaction delayed
//    };

    @Expose
    private String status;

    @Expose
    @SerializedName("cpu_usage_us")
    private long cpuUsageUs;

    @Expose
    @SerializedName("net_usage_words")
    private long netUsageWords;

    public String getStatus() {
        return status;
    }

    public long getCpuUsageUs() {
        return cpuUsageUs;
    }

    public long getNetUsageWords() {
        return netUsageWords;
    }
}
