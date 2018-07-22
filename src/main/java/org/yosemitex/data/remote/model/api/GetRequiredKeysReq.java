package org.yosemitex.data.remote.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.yosemitex.data.remote.model.chain.SignedTransaction;

import java.util.ArrayList;
import java.util.List;

public class GetRequiredKeysReq {

    @Expose
    private SignedTransaction transaction;

    @Expose
    @SerializedName("available_keys")
    private List<String> availableKeys;

    public GetRequiredKeysReq(SignedTransaction transaction, List<String> keys) {
        this.transaction = transaction;

        if (null != keys) {
            availableKeys = new ArrayList<>(keys);
        } else {
            availableKeys = new ArrayList<>();
        }
    }
}
