
package com.bx.implatform.server ;

import java.io.IOException ;
import java.math.BigDecimal ;
import java.math.BigInteger ;
import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import org.springframework.beans.factory.annotation.Value ;
import org.springframework.cache.annotation.Cacheable ;
import org.springframework.stereotype.Service ;
import org.tron.common.utils.ByteArray ;
import org.web3j.crypto.Credentials ;
import org.web3j.crypto.ECKeyPair ;
import org.web3j.crypto.RawTransaction ;
import org.web3j.crypto.TransactionEncoder ;
import org.web3j.crypto.Wallet ;
import org.web3j.crypto.WalletFile ;
import org.web3j.crypto.WalletUtils ;
import org.web3j.protocol.ObjectMapperFactory ;
import org.web3j.protocol.Web3j ;
import org.web3j.protocol.core.DefaultBlockParameter ;
import org.web3j.protocol.core.methods.response.EthGasPrice ;
import org.web3j.protocol.core.methods.response.EthGetBalance ;
import org.web3j.protocol.core.methods.response.TransactionReceipt ;
import org.web3j.protocol.http.HttpService ;
import org.web3j.tx.RawTransactionManager ;
import org.web3j.tx.TransactionManager ;
import org.web3j.tx.Transfer ;
import org.web3j.tx.gas.ContractGasProvider ;
import org.web3j.tx.gas.DefaultGasProvider ;
import org.web3j.tx.gas.StaticGasProvider ;
import org.web3j.utils.Convert ;
import org.web3j.utils.Convert.Unit ;
import org.web3j.utils.Numeric ;

import com.alibaba.fastjson.JSONArray ;
import com.alibaba.fastjson.JSONObject ;
import com.beust.jcommander.internal.Maps ;
import com.bx.implatform.config.ApplicatCfg ;
import com.bx.implatform.dto.CreateWalletResultDTO ;
import com.bx.implatform.dto.ExeTransferDTO ;
import com.bx.implatform.dto.ImportWalletDTO ;
import com.bx.implatform.dto.TransactionRecordsResultDTO ;
import com.bx.implatform.util.CustomException ;
import com.bx.implatform.util.ERC20 ;
import com.bx.implatform.util.IDUtil ;
import com.bx.implatform.util.MathUtil ;
import com.bx.implatform.util.StringUtil ;
import com.fasterxml.jackson.databind.ObjectMapper ;

import cn.hutool.http.HttpRequest ;
import lombok.extern.slf4j.Slf4j ;

@Slf4j
@Service
public class ChainServer{
	
	@Value("${bsc.rpc_endpoint}")
	public String rpc ="https://bsc-dataseed1.binance.org/";
	@Value("${bsc.china_id}")
	public String china_id;
	@Value("${bsc.coin_contract_address}")
	public String coin_contract_address;
	@Value("${bsc.coin_gas}")
	public String coin_gas;
	@Value("${bsc.coin_decimal}")
	public Integer coin_decimal;

	public  boolean validAddress(String address) {
        try {
            return WalletUtils.isValidAddress(address) ;
        } catch (Exception e) {
            return false ;
        }
    }
	/**
	 * 查询TRC20交易记录
	 * 
	 */
	public List<TransactionRecordsResultDTO> getTransactionRecords(String address, String contract_address, Integer limit,Integer decimals) {
		List<TransactionRecordsResultDTO> list = new ArrayList<>() ;
		if (StringUtil.isNotEmpty(contract_address)) {
			String url = "https://api.bscscan.com/api?module=account&action=tokentx&contractaddress=" + contract_address + "&address=" + address + "&page=1&offset=" + limit + "&startblock=0&endblock=999999999&sort=desc&apikey=1IQGUE4IBASYX1124J61FUBZNXYY6MZQRA";
			String body = HttpRequest.get(url).timeout(5000).execute().body() ;
			JSONObject jsonBody = JSONObject.parseObject(body) ;
			JSONArray jsonArray = jsonBody.getJSONArray("result") ;
			if (jsonArray == null)
				return null ;
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject d = jsonArray.getJSONObject(i) ;
//				System.out.println(d) ;
				String block_timestamp = d.getString("timeStamp") ;
				BigDecimal value = new BigDecimal(d.getString("value")).divide(BigDecimal.TEN.pow(decimals)) ;
				list.add(new TransactionRecordsResultDTO(d.getString("hash"), block_timestamp, d.getString("from"), d.getString("to"), value/*,d.getJSONObject("token_info").getString("symbol")*/,d.getString("blockNumber"))) ;
			}
		} else {
			Map map = new HashMap<>();
			String url = "https://api.bscscan.com/api?module=account&action=txlist&address=" + address + "&startblock=0&endblock=999999999&page=1&offset=" + limit + "&sort=desc&apikey=1IQGUE4IBASYX1124J61FUBZNXYY6MZQRA";
			String url2 = "https://api.bscscan.com/api?module=account&action=txlistinternal&address=" + address + "&startblock=0&endblock=999999999&page=1&offset=" + limit + "&sort=desc&apikey=1IQGUE4IBASYX1124J61FUBZNXYY6MZQRA";
			name(url, decimals, map);
			name(url2, decimals, map);
			sortByValue(map, true, list);
		}
		return list ;
	}
	public CreateWalletResultDTO importWallet(ImportWalletDTO dto) {
		String content = dto.getContent() ;
		try {
			String password = IDUtil.get8Hash();
			ECKeyPair keyPair = null;
			String mnemonics = null;
			Credentials credentials = null;
			if(!content.contains(" ")) {
//				Assert.isFalse(WalletUtils.isValidPrivateKey(content), "私钥输入错误");
//				keyPair =ECKeyPair.create(new BigInteger(content,16));
				if (content.substring(0, 2).equalsIgnoreCase("0x")) {
					content = content.replaceFirst("0x", "") ;
				}
				keyPair = ECKeyPair.create(ByteArray.fromHexString(content)) ;
			}else {
				credentials = WalletUtils.loadBip39Credentials(password, content);
				keyPair = credentials.getEcKeyPair();
				mnemonics = content;
			}
			WalletFile walletFile=Wallet.createStandard(password, keyPair);
			ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
			String keystore = objectMapper.writeValueAsString(walletFile);
			return CreateWalletResultDTO.builder().address("0x"+walletFile.getAddress())
					.private_key(ByteArray.toHexString(keyPair.getPrivateKey().toByteArray()))
					.public_key(ByteArray.toHexString(keyPair.getPublicKey().toByteArray()))
					.keystore(keystore)
					.mnemonics(mnemonics)
					.password(password).build();
		} catch (Exception e) {
			throw new CustomException("导入钱包异常："+e.getMessage()+"  "+content);
		}
	}
	private void name(String url,Integer decimals,Map map) {
		String body = HttpRequest.get(url).timeout(5000).execute().body() ;
		JSONObject jsonBody = JSONObject.parseObject(body) ;
		JSONArray jsonArray = jsonBody.getJSONArray("result") ;
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject d = jsonArray.getJSONObject(i) ;
			String block_timestamp = d.getString("timeStamp") ;
			if(MathUtil.compareTo(d.getString("value"), 0)) {
				BigDecimal value = new BigDecimal(d.getString("value")).divide(BigDecimal.TEN.pow(decimals)) ;
				Integer blockNumber = d.getInteger("blockNumber") ;
				map.put(blockNumber,new TransactionRecordsResultDTO(d.getString("hash"), block_timestamp, d.getString("from"), d.getString("to"), value/*,d.getJSONObject("token_info").getString("symbol")*/)) ;
			}
		}
	}
	/**
	 * 根据map的value排序
	 *
	 * @param map 待排序的map
	 * @param isDesc 是否降序，true：降序，false：升序
	 * @return 排序好的map
	 * @author zero 2019/04/08
	 */
	public static <K extends Comparable<? super K>, V> void sortByValue(Map<K, V> map, boolean isDesc,List list) {
	    Map<K, V> result = Maps.newLinkedHashMap();
	    if (isDesc) {
//	        map.entrySet().stream().sorted(Map.Entry.<K, V>comparingByValue().reversed())
//	                .forEach(e -> result.put(e.getKey(), e.getValue()));
	        map.entrySet().stream().sorted(Map.Entry.<K, V>comparingByKey().reversed())
	        .forEach(e -> list.add(e.getValue()));
	    } else {
	        map.entrySet().stream().sorted(Map.Entry.<K, V>comparingByKey())
	                .forEachOrdered(e -> list.add(e.getValue()));
	    }
	}
	
	public String transfer(ExeTransferDTO dto) throws Exception {
		String target_address = dto.getTarget_address() ;
		String private_key = dto.getPrivate_key() ;
		String contract_address = dto.getContract_address() ;
		String amount = dto.getAmount() ;
        if(contract_address == null){
        	return transferMain(private_key, amount, target_address);
        }else {
        	return transferOther(private_key, amount, target_address,contract_address, dto.getGas(),dto.getDecimal());
        }
    }
	public String transferMain(String private_key, String amount, String target_address) throws Exception {
    	try {
    		Credentials credentials = Credentials.create(private_key);
    		TransactionManager transactionManager = new RawTransactionManager(getW3j(), credentials);
    		BigInteger gas_price = getGasPrice();
    		TransactionReceipt send = new Transfer(getW3j(), transactionManager).sendFunds(target_address, Convert.toWei(amount, Unit.GWEI), Unit.GWEI, gas_price, BigInteger.valueOf(21000)).send();
    		return send.getTransactionHash();
		} catch (Exception e) {
			log.error("---->转账失败",e);
            throw new CustomException("转账失败：" + e.getMessage());
		}
    }
    public  String transferOther(String private_key, String amount, String target_address,String constract_address,BigInteger gas, Integer decimal) throws Exception{
		try {
			Credentials credentials = Credentials.create(private_key);
			BigInteger gas_price = getGasPrice();
			ContractGasProvider contractGasProvider = new StaticGasProvider(gas_price, gas);
//          ContractGasProvider contractGasProvider = new DefaultGasProvider();
			ERC20 contract = ERC20.load(constract_address, getW3j(), credentials, contractGasProvider);
			BigDecimal weiValue = Convert.toWei(amount, getWei(decimal));
			TransactionReceipt send = contract.transfer(target_address, weiValue.toBigInteger()).sendAsync().get();
			return send.getTransactionHash();
		}catch (Exception e){
			log.error("---->转账失败",e);
			throw new CustomException("转账失败：" + e.getMessage());
		}
    }
    
    private static Unit getWei(Integer wei) {
        Map<Integer, Unit> map = new HashMap<>();
        map.put(18, Unit.ETHER);
        map.put(6, Unit.MWEI);
        return map.get(wei);
    }
    private Web3j wab3j;
	public Web3j getW3j() {
		if(wab3j==null) {
			wab3j = Web3j.build(new HttpService(rpc)) ;
		}
		return wab3j;
	}
	@Cacheable(value = "getGasPrice")
    public BigInteger getGasPrice() {
		try {
    		EthGasPrice send = getW3j().ethGasPrice().send() ;
    		return send.getGasPrice() ;
    	} catch (Exception e) {
    		throw new CustomException("手续费查询失败：" + e.getMessage()) ;
    	}
	}
    public BigDecimal getBalance(String address, String contractAddress, Integer decimal) {
		try {
			if (contractAddress == null) {
				return getBalanceMain(address) ;
			} else {
				return getBalanceOther(address, contractAddress,decimal);
			}
		} catch (Exception e) {
			log.error("", e) ;
			throw new CustomException("余额查询失败：", e) ;
		}
	}
	public BigDecimal getBalanceMain(String address) throws IOException {
		EthGetBalance ethGetBalance = getW3j().ethGetBalance(address, DefaultBlockParameter.valueOf("latest")).send();
  		return Convert.fromWei(new BigDecimal(ethGetBalance.getBalance()), Convert.Unit.ETHER);
	}
	private BigDecimal getBalanceOther(String address, String contractAddress, Integer decimal) throws Exception {
		StaticGasProvider staticGasProvider = new DefaultGasProvider();
  		 Credentials credentials = Credentials.create("bf07c8dc33eb1d9bea0c86617ebb316c3214b43b735bda672f62e38df3066683");
  		 ERC20 contract = ERC20.load(contractAddress, getW3j(), credentials, staticGasProvider);
  		 return new BigDecimal( contract.balanceOf(address).send()).divide(BigDecimal.TEN.pow(decimal));
	}
	public BigInteger getDecimals(String contractAddress) throws Exception {
		StaticGasProvider staticGasProvider = new DefaultGasProvider();
		Credentials credentials = Credentials.create("bf07c8dc33eb1d9bea0c86617ebb316c3214b43b735bda672f62e38df3066683");
		ERC20 contract = ERC20.load(contractAddress, getW3j(), credentials, staticGasProvider);
		return contract.decimals().send();
	}
	public TransactionManager getRawTransactionManager(String PLANT_WALLET_PRIVATEKEY,String CHAIN_CHAINID){
		return new RawTransactionManager(getW3j(), getCredentials(PLANT_WALLET_PRIVATEKEY), Integer.valueOf(CHAIN_CHAINID)) ;
	}
	public TransactionManager getRawTransactionManager(String PLANT_WALLET_PRIVATEKEY) throws IOException {
		return new RawTransactionManager(getW3j(), getCredentials(PLANT_WALLET_PRIVATEKEY)) ;
	}
	public static Credentials getCredentials(String PLANT_WALLET_PRIVATEKEY) {
		//这个Web3Util 是自己定义的工具，能根据userId和siteId生成Credentials 对象的工具，大家可以自己构造，我这个有其他依赖，不方便提供源码
		//	        return Web3Util.getUserCredentials(userId,siteId);
		Credentials credentials = Credentials.create(PLANT_WALLET_PRIVATEKEY) ;
		return credentials ;
	}
	public  ContractGasProvider getContractGasProvider(String gaslimit) {
		BigInteger gasPrice = ApplicatCfg.getBean(ChainServer.class).getGasPrice() ;
		return new StaticGasProvider(gasPrice, BigInteger.valueOf(Long.valueOf(gaslimit))) ;
	}
	public  ContractGasProvider getContractGasProvider() {
		StaticGasProvider staticGasProvider=new DefaultGasProvider();
		return staticGasProvider;
	}
	public String sign(String privateKey,String to,String data){
    	try {
    		//查询地址交易编号
    		WalletFile walletFile = org.web3j.crypto.Wallet.createStandard("", ECKeyPair.create(new BigInteger(privateKey.replace("0x", ""), 16))) ;
    		BigInteger nonce = new BigInteger("3");
    		System.out.println(walletFile.getAddress()) ;
    				//getW3j().ethGetTransactionCount(walletFile.getAddress(), DefaultBlockParameterName.PENDING).send().getTransactionCount();
    		//支付的矿工费
    		BigInteger gasPrice = getW3j().ethGasPrice().send().getGasPrice();
    		
//    		BigInteger amountWei = Convert.toWei(dto.getSubunitAmount(), Convert.Unit.ETHER).toBigInteger();
    		BigInteger amountWei = new BigInteger("0");
    		BigInteger gasLimit = new BigInteger("170000");
    		//签名交易
    		RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, amountWei, data);
    		Credentials credentials = Credentials.create(privateKey);
//    		log.info("私钥：{}", dto.getPrivateKey());
    		byte[] signMessage = TransactionEncoder.signMessage(rawTransaction,56L, credentials);
    		log.info("签名结果：{}", Numeric.toHexString(signMessage));
			/*EthSendTransaction send = getW3j().ethSendRawTransaction(Numeric.toHexString(signMessage)).sendAsync().get() ;
			if(send.getError()!=null) {
				Assert.isTrue(send.getError().getCode()<0, send.getError().getMessage());
			}
			String hash = send.getTransactionHash();*/
			String hash = null;
    		log.info("交易Hash：{}", hash);
    		return hash;
		} catch (Exception e) {
			log.error("签名异常 ",e) ;
			throw new CustomException("签名异常 :"+e.getMessage());
		}
    }
	
}
