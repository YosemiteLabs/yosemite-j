package io.yosemiteblockchain.sample;

import io.yosemiteblockchain.Consts;
import io.yosemiteblockchain.data.remote.chain.PushedTransaction;
import io.yosemiteblockchain.data.remote.chain.TableRow;
import io.yosemiteblockchain.data.remote.chain.account.Account;
import io.yosemiteblockchain.services.TransactionParameters;
import io.yosemiteblockchain.services.YosemiteApiClientFactory;
import io.yosemiteblockchain.services.YosemiteApiRestClient;
import io.yosemiteblockchain.services.yxcontracts.KYCStatusType;
import io.yosemiteblockchain.services.yxcontracts.YosemiteDigitalContractJ;
import io.yosemiteblockchain.services.yxcontracts.YosemiteSystemJ;

import java.util.*;

public class DigitalContractJSample extends SampleCommon {
    private static final String IDENTITY_AUTHORITY_ACCOUNT = "idauth.a";
    private static final String SYSTEM_TOKEN_ACCOUNT = "systoken.a";
    private static final String SERVICE_PROVIDER_ACCOUNT = "servprovider"; // YPV_5JPknPMKNadXMaixB5zo6zAss7ZMtbJcyETxhVV19cP115RCKBi
    private static final String USER1_ACCOUNT = "servpuserxx1"; // YPV_5K31Vufu6KibpfUjrmSYQm3BHfG7XYPYWMvQG4CiSJUGNv5WJHq
    private static final String USER2_ACCOUNT = "servpuserxx2"; // YPV_5JdwhSpBSoZVysfhGtXPrVdNSbGBCr7hUoZRAaxWN6HfX7tKQbF

    public static void main(String[] args) {
        boolean wait_for_irreversibility = false;
        // Create Yosemite Client with servers of the same machine; transaction vote target for PoT is set to "d1"
        YosemiteApiRestClient apiClient = YosemiteApiClientFactory.createYosemiteApiClient(
            Consts.TESNET_SENTINEL_NODE_ADDRESS, Consts.DEFAULT_KEYOS_HTTP_URL);
        apiClient.setTransactionVoteTarget("producer.a");

        if (args.length > 0) {
            for (String arg : args) {
                if ("-prepare".equals(arg)) {
                    prepareServiceProvider(apiClient, IDENTITY_AUTHORITY_ACCOUNT, SYSTEM_TOKEN_ACCOUNT, SERVICE_PROVIDER_ACCOUNT);
                    return;
                } else if ("-wait-irr".equals(arg)) {
                    wait_for_irreversibility = true;
                }
            }
        }

        apiClient.setTransactionFeePayer(SERVICE_PROVIDER_ACCOUNT);

        // For this sample, we get the service provider's public key from the chain,
        // but in real case, you should get them from your storage.
        Account account = apiClient.getAccount(SERVICE_PROVIDER_ACCOUNT).execute();
        String serviceProviderPublicKey = account.getActivePublicKey();

        // create the user accounts or get the public key from the chain
        YosemiteSystemJ yxSystemJ = new YosemiteSystemJ(apiClient);

        String user1PublicKey;
        try {
            user1PublicKey = createKeyPairAndAccount(apiClient, yxSystemJ, SERVICE_PROVIDER_ACCOUNT, USER1_ACCOUNT);
            // KYC process done by Identity Authority Service
            processKYC(yxSystemJ, IDENTITY_AUTHORITY_ACCOUNT, USER1_ACCOUNT, EnumSet.of(KYCStatusType.KYC_STATUS_PHONE_AUTH));
        } catch (Exception e) {
            // log and ignore; usually the error is "already created"
            log(e.toString());
            user1PublicKey = apiClient.getAccount(USER1_ACCOUNT).execute().getActivePublicKey();
        }

        String user2PublicKey;
        try {
            user2PublicKey = createKeyPairAndAccount(apiClient, yxSystemJ, SERVICE_PROVIDER_ACCOUNT, USER2_ACCOUNT);
            processKYC(yxSystemJ, IDENTITY_AUTHORITY_ACCOUNT, USER2_ACCOUNT, EnumSet.of(KYCStatusType.KYC_STATUS_PHONE_AUTH));
        } catch (Exception e) {
            log(e.toString());
            user2PublicKey = apiClient.getAccount(USER2_ACCOUNT).execute().getActivePublicKey();
        }

        //----------------------------------------------
        // Let's start to use digital contract service!
        //----------------------------------------------
        YosemiteDigitalContractJ digitalContractJ = new YosemiteDigitalContractJ(apiClient);

        TransactionParameters txParametersForServiceProvider =
                TransactionParameters.Builder().addPublicKey(serviceProviderPublicKey).build();

        // 0. remove digital contract first
        PushedTransaction pushedTransaction;
        try {
            pushedTransaction = digitalContractJ.removeDigitalContract(
                    SERVICE_PROVIDER_ACCOUNT, 20, txParametersForServiceProvider).join();
            log("Pushed Remove Transaction:" + pushedTransaction.getTransactionId());
        } catch (Exception ignored) {
        }

        // 1. create digital contract
        List<String> signers = Arrays.asList(USER1_ACCOUNT, USER2_ACCOUNT);
        // prepare expiration time based on UTC time-zone
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.HOUR, 48);
        Date expirationTime = calendar.getTime();

        pushedTransaction = digitalContractJ.createDigitalContract(SERVICE_PROVIDER_ACCOUNT, 20, "test1234", "",
                signers, expirationTime, 0, EnumSet.of(KYCStatusType.KYC_STATUS_PHONE_AUTH), (short) 0,
                txParametersForServiceProvider).join();
        log("Pushed Create Transaction: " + pushedTransaction.getTransactionId() + ", block number=" + pushedTransaction.getTransactionTrace().getBlockNumer());

        // 3. sign contract by signers
        TransactionParameters txParametersForUser2 = TransactionParameters.Builder().
                addPermission(USER2_ACCOUNT).addPermission(SERVICE_PROVIDER_ACCOUNT).
                addPublicKey(user2PublicKey).addPublicKey(serviceProviderPublicKey).
            setTransactionFeePayer(SERVICE_PROVIDER_ACCOUNT).
                build();
        pushedTransaction = digitalContractJ.signDigitalDocument(SERVICE_PROVIDER_ACCOUNT, 20, USER2_ACCOUNT, "",
                txParametersForUser2).join();
        log("Pushed Sign Transaction: " + pushedTransaction.getTransactionId());
        if (wait_for_irreversibility) {
            waitForIrreversibility(apiClient, pushedTransaction);
        }

        TransactionParameters txParametersForUser1 = TransactionParameters.Builder().
                addPermission(USER1_ACCOUNT).addPermission(SERVICE_PROVIDER_ACCOUNT).
                addPublicKey(user1PublicKey).addPublicKey(serviceProviderPublicKey).
            setTransactionFeePayer(SERVICE_PROVIDER_ACCOUNT).
                build();
        pushedTransaction = digitalContractJ.signDigitalDocument(SERVICE_PROVIDER_ACCOUNT, 20, USER1_ACCOUNT, "I am user1",
                txParametersForUser1).join();
        log("Pushed Sign Transaction: " + pushedTransaction.getTransactionId());
        if (wait_for_irreversibility) {
            waitForIrreversibility(apiClient, pushedTransaction);
        }

        // update additional info
        pushedTransaction = digitalContractJ.updateAdditionalDocumentHash(SERVICE_PROVIDER_ACCOUNT, 20, "added after signing",
                txParametersForServiceProvider).join();
        log("Pushed Transaction: " + pushedTransaction.getTransactionId());

        log("");
        log("[Digital Contract]");
        TableRow tableRow = digitalContractJ.getCreatedDigitalContract(SERVICE_PROVIDER_ACCOUNT, 20).join();
        for (Map<String, ?> row : tableRow.getRows()) {
            // There must be only one row.
            log(row.toString());
        }

        log("");
        log("[Digital Contract Signer Info : " + USER1_ACCOUNT + "]");
        TableRow signerInfoTable = digitalContractJ.getSignerInfo(USER1_ACCOUNT, SERVICE_PROVIDER_ACCOUNT, 20).join();
        for (Map<String, ?> row : signerInfoTable.getRows()) {
            // There must be only one row.
            log((String) row.get("signerinfo"));
        }
    }

}
