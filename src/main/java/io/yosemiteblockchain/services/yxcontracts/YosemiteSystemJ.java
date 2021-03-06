package io.yosemiteblockchain.services.yxcontracts;

import io.yosemiteblockchain.Consts;
import io.yosemiteblockchain.crypto.ec.EosPublicKey;
import io.yosemiteblockchain.data.remote.chain.PushedTransaction;
import io.yosemiteblockchain.data.remote.contract.ActionLinkAuth;
import io.yosemiteblockchain.data.remote.contract.ActionNewAccount;
import io.yosemiteblockchain.data.remote.contract.ActionUnlinkAuth;
import io.yosemiteblockchain.data.types.TypeAuthority;
import io.yosemiteblockchain.data.types.TypePublicKey;
import io.yosemiteblockchain.services.TransactionParameters;
import io.yosemiteblockchain.services.YosemiteApiRestClient;
import io.yosemiteblockchain.services.YosemiteJ;
import io.yosemiteblockchain.util.StringUtils;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

/**
 * Provides the methods for the yx.system Yosemite contract.
 */
public class YosemiteSystemJ extends YosemiteJ implements YosemiteSystemConsts, StandardTokenConsts {

    public YosemiteSystemJ(YosemiteApiRestClient yosemiteApiRestClient) {
        super(yosemiteApiRestClient);
    }

    /**
     * Creates the new account with its public key and the creator account.
     * The convenion of the account <code>name</code> follows
     * <a href="https://developers.eos.io/eosio-cpp/docs/naming-conventions#section-standard-account-names">Naming Convention of YOSEMITE Standard Account Names</a>
     * Transaction fee is charged to the creator.
     * @param creator the name of the creator account
     * @param name the new account
     * @param ownerKey the public key
     * @param activeKey the public key
     * @param params transaction parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> createAccount(String creator, String name, String ownerKey,
                                                              String activeKey,
                                                              @Nullable TransactionParameters params) {

        if (StringUtils.isEmpty(creator)) throw new IllegalArgumentException("empty creator account name");
        if (StringUtils.isEmpty(name)) throw new IllegalArgumentException("empty target account name");
        if (StringUtils.isEmpty(ownerKey)) throw new IllegalArgumentException("empty owner public key");
        if (StringUtils.isEmpty(activeKey)) throw new IllegalArgumentException("empty active public key");

        ActionNewAccount actionNewAccount = new ActionNewAccount(creator, name,
                TypePublicKey.from(new EosPublicKey(ownerKey)), TypePublicKey.from(new EosPublicKey(activeKey)));

        return pushAction(Consts.YOSEMITE_SYSTEM_CONTRACT, ACTION_NEW_ACCOUNT,
                gson.toJson(actionNewAccount),
                buildCommonParametersWithDefaults(params, creator));
    }

    /**
     * Creates the new account with its public key and the creator account.
     * The convenion of the account <code>name</code> follows
     * <a href="https://developers.eos.io/eosio-cpp/docs/naming-conventions#section-standard-account-names">Naming Convention of YOSEMITE Standard Account Names</a>
     * Transaction fee is charged to the creator.
     * @param creator the name of the creator account
     * @param name the new account
     * @param ownerAuthority the list of public keys or the account names with the threshold and each weight settings
     * @param activeAuthority the list of public keys or the account names with the threshold and each weight settings
     * @param params transaction parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> createAccount(String creator, String name,
                                                              TypeAuthority ownerAuthority,
                                                              TypeAuthority activeAuthority,
                                                              @Nullable TransactionParameters params) {

        if (StringUtils.isEmpty(creator)) throw new IllegalArgumentException("empty creator account name");
        if (StringUtils.isEmpty(name)) throw new IllegalArgumentException("empty target account name");
        if (ownerAuthority == null) throw new IllegalArgumentException("empty owner public authority");
        if (activeAuthority == null) throw new IllegalArgumentException("empty active public authority");

        ActionNewAccount actionNewAccount = new ActionNewAccount(creator, name, ownerAuthority, activeAuthority);

        return pushAction(Consts.YOSEMITE_SYSTEM_CONTRACT, ACTION_NEW_ACCOUNT,
                gson.toJson(actionNewAccount),
                buildCommonParametersWithDefaults(params, creator));
    }

    /**
     * Sets or updates the account's permission for the given permission name. 
     * @param accountName account name
     * @param permissionName the permission name to set
     * @param parentPermissionName the parent permission name of target permission
     * @param authority the list of public keys or the account names with the threshold and each weight settings
     * @param params transaction parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> setAccountPermission(String accountName,
                                                                     String permissionName,
                                                                     String parentPermissionName,
                                                                     TypeAuthority authority,
                                                                     @Nullable TransactionParameters params) {
        return pushAction(Consts.YOSEMITE_SYSTEM_CONTRACT, ACTION_UPDATE_AUTH,
                ActionDataJsonCreator.updateAuth(accountName, permissionName, parentPermissionName, authority),
                buildCommonParametersWithDefaults(params, accountName));
    }

    /**
     * Sets or updates the account's permission for the active permission.
     * @param accountName account name
     * @param activeAuthority the list of public keys or the account names with the threshold and each weight settings
     * @param params transaction parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> setAccountPermission(String accountName,
                                                                     TypeAuthority activeAuthority,
                                                                     @Nullable TransactionParameters params) {
        return setAccountPermission(accountName, Consts.ACTIVE_PERMISSION_NAME, Consts.OWNER_PERMISSION_NAME, activeAuthority, params);
    }

    /**
     * Links the account permission to the given action of the contract. 
     * @param accountName the account to link a permission for
     * @param code the contract name to link with
     * @param action the action of the contract to link with
     * @param requirement the permission name to link with
     * @param params transaction parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> linkPermission(String accountName,
                                                               String code,
                                                               String action,
                                                               String requirement,
                                                               @Nullable TransactionParameters params) {
        if (StringUtils.isEmpty(accountName)) throw new IllegalArgumentException("empty target account name");
        if (StringUtils.isEmpty(code)) throw new IllegalArgumentException("empty code name");
        if (StringUtils.isEmpty(action)) throw new IllegalArgumentException("empty action name");
        if (StringUtils.isEmpty(requirement)) throw new IllegalArgumentException("empty requirement");

        ActionLinkAuth linkAuth = new ActionLinkAuth(accountName, code, action, requirement);
        return pushAction(Consts.YOSEMITE_SYSTEM_CONTRACT, ACTION_LINK_AUTH,
            gson.toJson(linkAuth), buildCommonParametersWithDefaults(params, accountName));
    }

    /**
     * Unlinks the account permission from the given action of the contract. 
     * @param accountName the account to unlink a permission for
     * @param code the contract name to unlink with
     * @param action the action of the contract to unlink with
     * @param params transaction parameters
     * @return CompletableFuture instance to get PushedTransaction instance
     */
    public CompletableFuture<PushedTransaction> unlinkPermission(String accountName,
                                                                 String code,
                                                                 String action,
                                                                 @Nullable TransactionParameters params) {
        if (StringUtils.isEmpty(accountName)) throw new IllegalArgumentException("empty target account name");
        if (StringUtils.isEmpty(code)) throw new IllegalArgumentException("empty code name");
        if (StringUtils.isEmpty(action)) throw new IllegalArgumentException("empty action name");

        ActionUnlinkAuth unlinkAuth = new ActionUnlinkAuth(accountName, code, action);
        return pushAction(Consts.YOSEMITE_SYSTEM_CONTRACT, ACTION_UNLINK_AUTH,
            gson.toJson(unlinkAuth), buildCommonParametersWithDefaults(params, accountName));
    }
}
