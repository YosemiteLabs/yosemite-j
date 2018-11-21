package io.yosemite.services.yxcontracts;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.yosemite.data.remote.chain.PushedTransaction;
import io.yosemite.data.remote.chain.TableRow;
import io.yosemite.data.remote.history.action.GetTableOptions;
import io.yosemite.data.types.TypeName;
import io.yosemite.services.TransactionParameters;
import io.yosemite.services.YosemiteApiRestClient;
import io.yosemite.services.YosemiteJ;
import io.yosemite.util.StringUtils;
import io.yosemite.util.Utils;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.yosemite.Consts.YOSEMITE_DIGITAL_CONTRACT_CONTRACT;

/**
 * Provides the methods for Digital Contract Signing Service.
 * For your information,
 * please read <a href="https://github.com/YosemiteLabs/yosemite-public-blockchain/blob/yosemite-master/contracts/yx.dcontract/README.md">yx.dcontract README</a>.
 */
public class YosemiteDigitalContractJ extends YosemiteJ {
    private final static int MAX_INPUT_STRING_LENGTH = 256;

    public YosemiteDigitalContractJ(YosemiteApiRestClient yosemiteApiRestClient) {
        super(yosemiteApiRestClient);
    }

    /**
     * Creates a digital contract.
     * Transaction fee is charged to the creator.
     * @param creator the account who creates the digital contract
     * @param sequence 64-bit unsigned integer generated by the caller of the action; it must be unique
     * @param digitalContractHash string representation of the digital contract hash
     * @param additionalDocumentHash string representation of the additional document hash
     * @param signers the list of account name who will sign the digital contract
     * @param expiration expiration time of the digital contract in UTC+0
     * @param accountType the type of the signer account; only the signer who is the specified type can sign the digital contract
     * @param kycVectors the KYC authentication flags of the signer account; only the signer who passes the required KYC authentication can sign the digital contract
     * @param options reserved; must be 0
     * @param params transaction parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> createDigitalContract(
            String creator, long sequence, String digitalContractHash, String additionalDocumentHash,
            List<String> signers, Date expiration,
            int accountType, EnumSet<KYCStatusType> kycVectors,
            short options, @Nullable TransactionParameters params) {
        if (StringUtils.isEmpty(creator)) throw new IllegalArgumentException("empty creator");
        if (sequence < 0) throw new IllegalArgumentException("negative sequence");
        if (StringUtils.isEmpty(digitalContractHash)) throw new IllegalArgumentException("empty digitalContractHash");
        if (digitalContractHash.length() > MAX_INPUT_STRING_LENGTH) throw new IllegalArgumentException("too long digitalContractHash");
        if (additionalDocumentHash != null && additionalDocumentHash.length() > MAX_INPUT_STRING_LENGTH) {
            throw new IllegalArgumentException("too long additionalDocumentHash");
        }
        if (signers == null || signers.isEmpty()) throw new IllegalArgumentException("empty signers");
        if (signers.size() > 32) throw new IllegalArgumentException("too many signers");
        if (expiration == null) throw new IllegalArgumentException("wrong expiration");
        if (accountType < 0) throw new IllegalArgumentException("negative accountType");
        if (options < 0) throw new IllegalArgumentException("negative option");

        JsonArray arrayObj = new JsonArray();
        JsonObject digitalContractIdObj = new JsonObject();
        digitalContractIdObj.addProperty("creator", creator);
        digitalContractIdObj.addProperty("sequence", Long.toString(sequence));
        arrayObj.add(digitalContractIdObj);
        arrayObj.add(digitalContractHash);
        arrayObj.add(additionalDocumentHash == null ? "" : additionalDocumentHash);
        JsonArray signersObj = new JsonArray();
        for (String signer : signers) {
            signersObj.add(signer);
        }
        arrayObj.add(signersObj);
        arrayObj.add(Utils.SIMPLE_DATE_FORMAT_FOR_EOS.get().format(expiration));
        arrayObj.add(accountType);
        arrayObj.add(KYCStatusType.getAsBitFlags(kycVectors));
        arrayObj.add(options);

        return pushAction(YOSEMITE_DIGITAL_CONTRACT_CONTRACT, "create", new Gson().toJson(arrayObj),
                buildCommonParametersWithDefaults(params, creator));
    }

    /**
     * Adds additional signers to the digital contract.
     * Transaction fee is charged to the creator.
     * @param creator the account who creates the digital contract
     * @param sequence 64-bit unsigned integer which indicates the digital contract
     * @param signers the list of account name who will sign the digital contract
     * @param params transaction parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> addSigners(
            String creator, long sequence, List<String> signers,
            @Nullable TransactionParameters params) {
        if (StringUtils.isEmpty(creator)) throw new IllegalArgumentException("empty creator");
        if (sequence < 0) throw new IllegalArgumentException("negative sequence");
        if (signers == null || signers.isEmpty()) throw new IllegalArgumentException("empty signers");

        JsonArray arrayObj = new JsonArray();
        JsonObject digitalContractIdObj = new JsonObject();
        digitalContractIdObj.addProperty("creator", creator);
        digitalContractIdObj.addProperty("sequence", Long.toString(sequence));
        arrayObj.add(digitalContractIdObj);
        JsonArray signersObj = new JsonArray();
        for (String signer : signers) {
            signersObj.add(signer);
        }
        arrayObj.add(signersObj);

        return pushAction(YOSEMITE_DIGITAL_CONTRACT_CONTRACT, "addsigners", new Gson().toJson(arrayObj),
                buildCommonParametersWithDefaults(params, creator));
    }

    /**
     * Signs the digital contract by the signer.
     * Transaction fee is charged to the signer.
     * @param creator the account who creates the digital contract
     * @param sequence 64-bit unsigned integer which indicates the digital contract
     * @param signer the account name who will sign the digital contract
     * @param signerInfo the information of the signer
     * @param params transaction parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> signDigitalDocument(
            String creator, long sequence, String signer, String signerInfo,
            @Nullable TransactionParameters params) {
        if (StringUtils.isEmpty(creator)) throw new IllegalArgumentException("empty creator");
        if (sequence < 0) throw new IllegalArgumentException("negative sequence");
        if (StringUtils.isEmpty(signer)) throw new IllegalArgumentException("empty signer");
        if (signerInfo != null && signerInfo.length() > MAX_INPUT_STRING_LENGTH) throw new IllegalArgumentException("too long signerInfo");

        JsonArray arrayObj = new JsonArray();
        JsonObject digitalContractIdObj = new JsonObject();
        digitalContractIdObj.addProperty("creator", creator);
        digitalContractIdObj.addProperty("sequence", Long.toString(sequence));
        arrayObj.add(digitalContractIdObj);
        arrayObj.add(signer);
        arrayObj.add(signerInfo == null ? "" : signerInfo);

        return pushAction(YOSEMITE_DIGITAL_CONTRACT_CONTRACT, "sign", new Gson().toJson(arrayObj),
                buildCommonParametersWithDefaults(params, signer));
    }

    /**
     * Updates the additional document hash string.
     * Transaction fee is charged to the creator.
     * @param creator the account who creates the digital contract
     * @param sequence 64-bit unsigned integer which indicates the digital contract
     * @param additionalDocumentHash string representation of the additional document hash
     * @param params transaction parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> updateAdditionalDocumentHash(
            String creator, long sequence, String additionalDocumentHash,
            @Nullable TransactionParameters params) {
        if (StringUtils.isEmpty(creator)) throw new IllegalArgumentException("empty creator");
        if (sequence < 0) throw new IllegalArgumentException("negative sequence");
        if (additionalDocumentHash != null && additionalDocumentHash.length() > MAX_INPUT_STRING_LENGTH) {
            throw new IllegalArgumentException("too long additionalDocumentHash");
        }

        JsonArray arrayObj = new JsonArray();
        JsonObject digitalContractIdObj = new JsonObject();
        digitalContractIdObj.addProperty("creator", creator);
        digitalContractIdObj.addProperty("sequence", Long.toString(sequence));
        arrayObj.add(digitalContractIdObj);
        arrayObj.add(additionalDocumentHash == null ? "" : additionalDocumentHash);

        return pushAction(YOSEMITE_DIGITAL_CONTRACT_CONTRACT, "upadddochash", new Gson().toJson(arrayObj),
                buildCommonParametersWithDefaults(params, creator));
    }

    /**
     * Removes the digital contract from Yosemite RAM Database.
     * Transaction fee is charged to the creator.
     * Note that it doesn't remove the digital contract information in the block and no one can remove as the irreversible attribute of Blockchain.
     * @param creator the account who creates the digital contract
     * @param sequence 64-bit unsigned integer which indicates the digital contract
     * @param params transaction parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> removeDigitalContract(
            String creator, long sequence,
            @Nullable TransactionParameters params) {
        if (StringUtils.isEmpty(creator)) throw new IllegalArgumentException("empty creator");
        if (sequence < 0) throw new IllegalArgumentException("negative sequence");

        JsonArray arrayObj = new JsonArray();
        JsonObject digitalContractIdObj = new JsonObject();
        digitalContractIdObj.addProperty("creator", creator);
        digitalContractIdObj.addProperty("sequence", Long.toString(sequence));
        arrayObj.add(digitalContractIdObj);

        return pushAction(YOSEMITE_DIGITAL_CONTRACT_CONTRACT, "remove", new Gson().toJson(arrayObj),
                buildCommonParametersWithDefaults(params, creator));
    }

    public CompletableFuture<TableRow> getCreatedDigitalContract(String creator, long sequence) {
        if (StringUtils.isEmpty(creator)) throw new IllegalArgumentException("empty creator");
        if (sequence < 0) throw new IllegalArgumentException("negative sequence");

        GetTableOptions options = new GetTableOptions();
        options.setLowerBound(String.valueOf(sequence));
        options.setLimit(1);

        return getTableRows(YOSEMITE_DIGITAL_CONTRACT_CONTRACT, creator, "dcontracts", options);
    }

    public CompletableFuture<TableRow> getSignerInfo(String signer, String creator, long sequence) {
        if (StringUtils.isEmpty(signer)) throw new IllegalArgumentException("empty signer");
        if (StringUtils.isEmpty(creator)) throw new IllegalArgumentException("empty creator");

        long creatorAsInteger = TypeName.stringToName(creator);
        String dcIdSerializedHex = Utils.makeWebAssembly128BitIntegerAsHexString(creatorAsInteger, sequence);

        // cleos get table yx.dcontract user3 signers --index 2 --key-type i128 -L 0x0b000000000000007055729bdebaafc2 -l 1
        GetTableOptions options = new GetTableOptions();
        options.setIndexPosition("2"); // indicates secondary index 'dcids' of dcontract_signer_index
                                       // defined by contracts/yx.dcontract/yx.dcontract.hpp of YosemiteChain
        options.setKeyType("i128");
        options.setLowerBound(dcIdSerializedHex);
        options.setLimit(1);

        return getTableRows(YOSEMITE_DIGITAL_CONTRACT_CONTRACT, signer, "signers", options);
    }
}
