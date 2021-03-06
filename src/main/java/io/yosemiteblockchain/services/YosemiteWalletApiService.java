package io.yosemiteblockchain.services;

import io.yosemiteblockchain.data.remote.chain.SignedTransaction;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.Collection;
import java.util.List;

public interface YosemiteWalletApiService {

    @POST("/v1/wallet/create_key")
    Call<String> createKey(@Body Collection request);

    @GET("/v1/wallet/get_public_keys")
    Call<List<String>> getPublicKeys();

    @POST("/v1/wallet/sign_transaction")
    Call<SignedTransaction> signTransaction(@Body Collection transactionData);

    @POST("/v1/wallet/sign_digest")
    Call<String> signDigest(@Body Collection dataToSign);
}
