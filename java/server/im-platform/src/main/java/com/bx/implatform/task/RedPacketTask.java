package com.bx.implatform.task;

import java.io.IOException ;
import java.math.BigDecimal ;
import java.math.BigInteger ;
import java.time.LocalDateTime ;
import java.util.List ;

import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.beans.factory.annotation.Value ;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.web3j.protocol.core.methods.response.EthTransaction ;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper ;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper ;
import com.baomidou.mybatisplus.core.toolkit.AES ;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page ;
import com.bx.implatform.config.BeanServer ;
import com.bx.implatform.controller.RedPacketController ;
import com.bx.implatform.dto.CreateWalletResultDTO ;
import com.bx.implatform.dto.ImportWalletDTO ;
import com.bx.implatform.dto.red_packet.PageQueryDTO ;
import com.bx.implatform.entity.RedPacket ;
import com.bx.implatform.entity.RedPacketRecord ;
import com.bx.implatform.entity.Transaction ;
import com.bx.implatform.enums.TransactionStatusEnum ;
import com.bx.implatform.mapper.CommonMapper ;
import com.bx.implatform.mapper.RedPacketMapper ;
import com.bx.implatform.mapper.RedPacketRecordMapper ;
import com.bx.implatform.mapper.TransactionMapper ;
import com.bx.implatform.server.ChainServer ;
import com.bx.implatform.util.MathUtil ;
import com.bx.implatform.util.MybatiesUtil ;

import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Configuration
//@ConditionalOnProperty(prefix = "scheduling", name = "enabled", havingValue = "true")
public class RedPacketTask  {
	@Autowired
	private  CommonMapper commonMapper;
	@Autowired
	private  RedPacketRecordMapper redPacketRecordMapper;
	@Autowired
	private  BeanServer beanServer;
	@Autowired
	private  TransactionMapper transactionMapper;
	@Autowired
	private  RedPacketMapper redPacketMapper;
	@Autowired
	private  ChainServer chainServer;
	
	@Value("${key}")
    private String key;

    @Scheduled(cron = "0 0/30 * * * ?")
    protected void delRedPackege() {
		log.info("更新未领取红包的状态: ", LocalDateTime.now()) ;
		
		//暂时不过期
//        commonMapper.update("update red_packet set status=2 where status=0 and  create_date <= (select now()-interval 1 day);");
        commonMapper.update("update red_packet_record set status=0 where  status=1");
    }
    /**
     * 根据hash验证是否收到钱了
     */
    @Scheduled(cron = "0 0/1 * * * ?")
   	public void sq() {
    	List<RedPacket> selectList = redPacketMapper.selectList(new LambdaQueryWrapper<RedPacket>().eq(RedPacket::getStatus, 3)) ;
    	selectList.forEach(x->{
    		try {
				EthTransaction ethGetTransactionReceipt = chainServer.getW3j().ethGetTransactionByHash(x.getHash()).send();
				BigInteger amount = ethGetTransactionReceipt.getResult().getValue() ;
				x.setAmount(new BigDecimal(amount));
				x.setStatus(0);
    		} catch (IOException e) {
				log.error("",e);
			}
    	});
    }

    @Scheduled(cron = "0 0/1 * * * ?")
	public void reciveRp() {
    	Page<RedPacketRecord> selectPage = redPacketRecordMapper.selectPage(MybatiesUtil.getPage(new PageQueryDTO()), new QueryWrapper<RedPacketRecord>().lambda().eq(RedPacketRecord::getStatus, 0)) ;
    	List<RedPacketRecord> records = selectPage.getRecords() ;
    	log.info("红包待分配数量："+records.size());
    	for (int i = 0; i < records.size(); i++) {
    		RedPacketRecord rpr = records.get(i);
    		try {
    			RedPacket rp = redPacketMapper.selectById(i) ;
        		String txid = startTransaction(rpr.getAmount(), "红包", rpr.getTarget_address(), chainServer.coin_contract_address, rpr.getRed_packet_id(),new BigInteger(chainServer.coin_gas),chainServer.coin_decimal);
        		rpr.setStatus(2);
        		rpr.setTxid(txid);
        		log.info("红包领取完成：{}"+rpr.getId(),txid);
			} catch (Exception e) {
				log.error("领取红包异常",e);
				rpr.setStatus(1);
			}
        	redPacketRecordMapper.updateById(rpr);
		}
    }
    
//    @Async
	public String startTransaction(BigDecimal amount,String remark,String target_address,String coin_contract_address,String order_id,BigInteger gas, Integer decimal) throws Exception {
		/*if ("dev".equals(active)){
			return null;
		}*/
		if(!MathUtil.compareTo(amount, 0)) {
			return null;
		}
		ImportWalletDTO importWalletDTO = new ImportWalletDTO() ;
		importWalletDTO.setContent(RedPacketController.pk);
		CreateWalletResultDTO importWallet = chainServer.importWallet(importWalletDTO) ;
		String address = importWallet.getAddress() ;
		//log.info(remark+" 数量："+amount+" 转出钱包："+wallet.getAddress()+" 接收钱包："+target_address +" 币id："+coin_id);
		String formatePrivateKey = formatePrivateKey(RedPacketController.pk) ;
		String txid = chainServer.transferOther(formatePrivateKey,amount.toPlainString(),target_address,coin_contract_address,gas,decimal);
			Transaction t = new Transaction() ;
			t.setAddress(address) ;
			t.setId(txid) ;
			t.setTxid(txid) ;
			t.setAmount(amount);
			t.setTarget_address(target_address) ;
			t.setOrder_id(order_id);
			t.setStatus(TransactionStatusEnum.SUCCESS.getId());
			t.setRemark(remark);
			transactionMapper.insert(t);
		return txid;
	}
	
	public String formatePrivateKey(String private_key) {
        if (private_key.contains("=") || private_key.contains("+")) {
            private_key = AES.decrypt(private_key, key);
        }
        return private_key;
    }
}
