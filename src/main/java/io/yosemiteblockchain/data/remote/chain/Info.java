/*
 * Copyright (c) 2017-2018 PLACTAL.
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.yosemiteblockchain.data.remote.chain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.yosemiteblockchain.data.types.TypeSymbol;
import io.yosemiteblockchain.util.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

public class Info {

    @Expose
    @SerializedName("server_version")
    private String serverVersion;

    @Expose
    @SerializedName("chain_id")
    private String chainId;

    @Expose
    @SerializedName("head_block_num")
    private Integer headBlockNum;

    @Expose
    @SerializedName("last_irreversible_block_num")
    private Integer lastIrreversibleBlockNum;

    @Expose
    @SerializedName("last_irrerversible_block_id")
    private String lastIrreversibleBlockId;

    @Expose
    @SerializedName("head_block_id")
    private String headBlockId;

    @Expose
    @SerializedName("head_block_time")
    private String headBlockTime;

    @Expose
    @SerializedName("head_block_producer")
    private String headBlockProducer;

    @Expose
    @SerializedName("virtual_block_cpu_limit")
    private long virtualBlockCpuLimit;

    @Expose
    @SerializedName("virtual_block_net_limit")
    private long virtualBlockNetLimit;

    @Expose
    @SerializedName("block_cpu_limit")
    private long blockCpuLimit;

    @Expose
    @SerializedName("block_net_limit")
    private long blockNetLimit;

    @Expose
    @SerializedName("server_version_string")
    private Optional<String> serverVersionString;

    @Expose
    @SerializedName("native_token_symbol")
    private TypeSymbol nativeTokenSymbol;

    public String getServerVersion() {
        return serverVersion;
    }

    public String getChainId() {
        return chainId;
    }

    public Integer getHeadBlockNum() {
        return headBlockNum;
    }

    public Integer getLastIrreversibleBlockNum() {
        return lastIrreversibleBlockNum;
    }

    public String getLastIrreversibleBlockId() {
        return lastIrreversibleBlockId;
    }

    public String getHeadBlockId() {
        return headBlockId;
    }

    public String getHeadBlockTime() {
        return headBlockTime;
    }

    public String getHeadBlockProducer() {
        return headBlockProducer;
    }

    public long getVirtualBlockCpuLimit() {
        return virtualBlockCpuLimit;
    }

    public long getVirtualBlockNetLimit() {
        return virtualBlockNetLimit;
    }

    public long getBlockCpuLimit() {
        return blockCpuLimit;
    }

    public long getBlockNetLimit() {
        return blockNetLimit;
    }

    public String getTimeAfterHeadBlockTime(int diffInMilSec) {
        DateFormat sdf = Utils.SIMPLE_DATE_FORMAT_FOR_EOS.get();
        try {
            Date date = sdf.parse(this.headBlockTime);

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.MILLISECOND, diffInMilSec);
            date = c.getTime();

            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace(); //TODO
            return this.headBlockTime;
        }
    }

    public String toString() {
        return Utils.toJson(this, false);
    }

    public Optional<String> getServerVersionString() {
        return serverVersionString;
    }

    public void setServerVersionString(Optional<String> serverVersionString) {
        this.serverVersionString = serverVersionString;
    }

    public TypeSymbol getNativeTokenSymbol() {
        return nativeTokenSymbol;
    }

    public void setNativeTokenSymbol(TypeSymbol nativeTokenSymbol) {
        this.nativeTokenSymbol = nativeTokenSymbol;
    }
}
