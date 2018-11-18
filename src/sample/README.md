# Sample

It assumes that the single node testnet on the local enviroment is prepared.
So we are going to provide quick command guideline for preparation.

## Single node testnet preparation
* It assumes that PATH environment is set to `yosemite`, `clyos`, and `keyos` executables.
* https://github.com/YosemiteLabs/yosemite-public-blockchain#local-single-node-testnet
   * execute yosemite like below;
```
yosemite --filter-on yx.dcontract:create: --filter-on yx.dcontract:sign:
```

### prepare wallet and its management daemon(keyos)
* create default wallet (keyos is executed implicitly by clyos)
```shell
clyos wallet create --to-console
```
* save its password somewhere
* If the wallet is locked or you should need to execute keyos again, just unlock it like below;
```
clyos wallet unlock --password your_password
```
* You can check keyos daemon like below;
```shell
$ ps -ef | grep keyos
eugene    22242   1361  0 12:26 pts/2    00:00:00 ..../yosemite-public-blockchain/build/programs/keyos/keyos --http-server-address=127.0.0.1:8900
```

### prepare others
* It assumes that the current directory is `yosemite-public-blockchain` local git repository.
* We just want this process be simple as possible. If you need more, you should check https://github.com/YosemiteLabs/yosemite-public-blockchain
```
# prepare yosemite contract accounts
clyos wallet import --private-key 5JwvMHnfQC5TjJ6RshcuFQbK2ydy9vEdAugE1HYZBYWThwD27LZ
clyos create account yosemite d1 EOS7WXCxsMuSChfEX5UPe19o6GZaELji2yQBgwgmXtbvytxkSGjKz
clyos create account yosemite yx.ntoken EOS7WXCxsMuSChfEX5UPe19o6GZaELji2yQBgwgmXtbvytxkSGjKz
clyos create account yosemite yx.token EOS7WXCxsMuSChfEX5UPe19o6GZaELji2yQBgwgmXtbvytxkSGjKz
clyos create account yosemite yx.identity EOS7WXCxsMuSChfEX5UPe19o6GZaELji2yQBgwgmXtbvytxkSGjKz
clyos create account yosemite yx.txfee EOS7WXCxsMuSChfEX5UPe19o6GZaELji2yQBgwgmXtbvytxkSGjKz
clyos create account yosemite yx.dcontract EOS7WXCxsMuSChfEX5UPe19o6GZaELji2yQBgwgmXtbvytxkSGjKz

# install yosemite contracts
clyos set contract yx.identity build/contracts/yx.identity -p yx.identity
clyos set contract yx.ntoken build/contracts/yx.ntoken -p yx.ntoken
clyos set contract yx.token build/contracts/yx.token -p yx.token
clyos set contract yx.txfee build/contracts/yx.txfee -p yx.txfee
clyos set contract yx.dcontract build/contracts/yx.dcontract -p yx.dcontract
clyos set contract yosemite build/contracts/yx.system/ -p yosemite

# set some yosemite contract accounts as privileged
clyos push action yosemite setpriv '["yx.ntoken",1]' -p yosemite@active
clyos push action yosemite setpriv '["yx.token",1]' -p yosemite@active
clyos push action yosemite setpriv '["yx.dcontract",1]' -p yosemite@active

# prepare system depository and identity authority service
clyos push action yosemite regsysdepo '["d1","http://d1.org",1]' -p d1@active -p yosemite@active
clyos push action yosemite authsysdepo '["d1"]' -p yosemite@active
clyos push action yosemite regidauth '["d1","http://d1.org",1]' -p d1@active -p yosemite@active
clyos push action yosemite authidauth '["d1"]' -p yosemite@active
clyos push action yx.identity setidinfo "{\"account\":\"d1\", \"identity_authority\":\"d1\", \"type\":$(echo 'ibase=2; 0' | bc), \"kyc\":$(echo 'ibase=2; 1111' | bc), \"state\":$(echo 'ibase=2; 0' | bc), \"data\":\"sysdepo1\"}" -p d1

# set transaction fee to yx.system service (for newaccount)
clyos push action yx.txfee settxfee '[ "tf.newacc", "1000.00 DKRW" ]' -p yosemite@active

# set transaction fee to yx.ntoken service
clyos push action yx.txfee settxfee '{"operation":"tf.nissue", "fee":"0.00 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.nredeem", "fee":"1000.00 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.ntransfer", "fee":"10.00 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.transfer", "fee":"20.00 DKRW"}}' -p yosemite

# set transaction fee to yx.dcontract service (for DigitalContractJSample)
clyos push action yx.txfee settxfee '{"operation":"tf.dccreate", "fee":"50.00 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.dcaddsign", "fee":"10.00 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.dcsign", "fee":"30.00 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.dcupadd", "fee":"5.00 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.dcremove", "fee":"0.00 DKRW"}}' -p yosemite

# set transaction fee to yx.token service (for TokenContractJSample)
clyos push action yx.txfee settxfee '{"operation":"tf.tcreate", "fee":"10000.00 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.tissue", "fee":"100.00 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.tredeem", "fee":"100.00 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.ttransfer", "fee":"10.00 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.tsetkyc", "fee":"5.00 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.tsetopts", "fee":"5.00 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.tfreezeac", "fee":"5.00 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.tgissue", "fee":"5.00 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.tissuebyu", "fee":"5.00 DKRW"}}' -p yosemite
clyos push action yx.txfee settxfee '{"operation":"tf.tchangeis", "fee":"5.00 DKRW"}}' -p yosemite

# turn off KYC check of yx.ntoken for test convenience
clyos push action yx.ntoken setkycrule '{"type":0, "kyc":0}' -p yosemite
clyos push action yx.ntoken setkycrule '{"type":1, "kyc":0}' -p yosemite

```

## Build
```
./gradlew buildSample
```

## Execute
* prepare logic
```shell
./gradlew -PmainClass=DigitalContractJSample runSample -Ppargs='-prepare'
./gradlew -PmainClass=TokenContractJSample runSample -Ppargs='-prepare'
```

* main logic
```shell
./gradlew -PmainClass=DigitalContractJSample runSample
./gradlew -PmainClass=TokenContractJSample runSample
```