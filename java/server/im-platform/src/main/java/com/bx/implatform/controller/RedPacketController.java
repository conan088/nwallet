package com.bx.implatform.controller;

import java.math.BigDecimal ;
import java.util.Objects ;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.PostMapping ;
import org.springframework.web.bind.annotation.RequestBody ;
import org.springframework.web.bind.annotation.RequestMapping ;
import org.springframework.web.bind.annotation.RequestParam ;
import org.springframework.web.bind.annotation.RestController ;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper ;
import com.baomidou.mybatisplus.core.metadata.IPage ;
import com.bx.implatform.dto.CreateWalletResultDTO ;
import com.bx.implatform.dto.ImportWalletDTO ;
import com.bx.implatform.dto.red_packet.PageQueryDTO ;
import com.bx.implatform.dto.red_packet.ReceiveRedPacketDTO ;
import com.bx.implatform.dto.red_packet.RedPacketReceiveResultDTO ;
import com.bx.implatform.dto.red_packet.RedPacketRecordDTO ;
import com.bx.implatform.dto.red_packet.RedPacketResultDTO ;
import com.bx.implatform.dto.red_packet.RedPacketSendResultDTO ;
import com.bx.implatform.dto.red_packet.SendRedPacketDTO ;
import com.bx.implatform.entity.RedPacket ;
import com.bx.implatform.entity.RedPacketRecord ;
import com.bx.implatform.entity.User;
import com.bx.implatform.mapper.CommonMapper ;
import com.bx.implatform.mapper.RedPacketMapper ;
import com.bx.implatform.mapper.RedPacketRecordMapper ;
import com.bx.implatform.result.Result;
import com.bx.implatform.result.ResultUtils;
import com.bx.implatform.server.ChainServer ;
import com.bx.implatform.service.IUserService;
import com.bx.implatform.session.SessionContext;
import com.bx.implatform.session.UserSession;
import com.bx.implatform.util.CloneUtil ;
import com.bx.implatform.util.MathUtil ;
import com.bx.implatform.util.MybatiesUtil ;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "红包相关接口")
@RestController
@RequestMapping("/redpacket")
@RequiredArgsConstructor
public class RedPacketController {

    private final IUserService userService;
    private final RedPacketMapper redPacketMapper;
    private final RedPacketRecordMapper redPacketRecordMapper;
    private final CommonMapper commonMapper;
    private final ChainServer bscServer;

    
    public static final String pk = "0c2bffffe3978718751226b6f426ae438da3e6694120cbc5019ef4da1e56a63a";
    
    @GetMapping("getWalletAddress")
    @ApiOperation(value = "获取平台钱包地址", notes = "获取平台钱包地址")
    public Result<RedPacket> getWalletAddress() {
    	ImportWalletDTO importWalletDTO = new ImportWalletDTO() ;
		importWalletDTO.setContent(pk);
		CreateWalletResultDTO importWallet = bscServer.importWallet(importWalletDTO) ;
    	return ResultUtils.success(importWallet.getAddress());
    }
    
    @PostMapping("send")
    @ApiOperation(value = "发红包", notes = "发红包")
    public Result<RedPacket> getOnlineTerminal(@RequestBody @Valid SendRedPacketDTO dto) {
    	UserSession session = SessionContext.getSession();
        User user = userService.getById(session.getUserId());
        RedPacket rp = CloneUtil.clone(dto, RedPacket.class) ;
        rp.setBalance(rp.getAmount());
        rp.setSend_user_id(session.getUserId());
        rp.setAddress(user.getAddress());
        redPacketMapper.insert(rp);
        return ResultUtils.success(rp);
    }
    @PostMapping("receive")
    @ApiOperation(value = "收红包",notes = "31:红包已被抢光了 32:红包已领取 33 红包已过期")
    public synchronized Result<BigDecimal> receive(@RequestBody @Valid ReceiveRedPacketDTO dto) {
    	UserSession session = SessionContext.getSession();
        User recive_user = userService.getById(session.getUserId());
    	RedPacket rp = redPacketMapper.selectById(dto.getId()) ;
    	if(rp==null) {
    		return ResultUtils.error(30,"红包不存在");
    	}
    	if(rp.getStatus()==2) {
			return ResultUtils.error(33,"红包已过期") ;
		}
    	//开始领取
    	BigDecimal r_amount = null;
    	if(rp.getType()==0) {//个人红包
    		if(rp.getStatus()==1) {
    			return ResultUtils.error(32,"红包已领取") ;
    		}
    		r_amount = rp.getAmount();
    		rp.setStatus(1);
    		if(Objects.equals(recive_user.getAddress(), rp.getAddress())) {
    			return ResultUtils.error(35,"不能领取自己发出的红包") ;
    		}
    	}else if(rp.getType()==1){//群红包
    		if(rp.getStatus()==1) {
    			return ResultUtils.error(31,"红包已被抢光了") ;
    		}
    		if(rp.getNum()-redPacketRecordMapper.selectCount(new QueryWrapper<RedPacketRecord>().lambda().eq(RedPacketRecord::getRed_packet_id, dto.getId()))<=1) {
    			rp.setStatus(1);
    			r_amount = rp.getBalance();
    		}else {
    			r_amount = getRpAmount(rp);
    		}
    		//判断自己是否已经领取了
    		if(redPacketRecordMapper.selectOne(new QueryWrapper<RedPacketRecord>().lambda().eq(RedPacketRecord::getTarget_address, recive_user.getAddress()).eq(RedPacketRecord::getRed_packet_id, dto.getId()))!=null) {
    			return ResultUtils.error(32,"红包已领取") ;
    		}
    	}
    	//保留两位小数
    	r_amount = MathUtil.retainB(r_amount, 2);
    	rp.setBalance(MathUtil.sub(rp.getBalance(), r_amount));
    	
    	if(!MathUtil.compareTo2(rp.getBalance(), 0)) {
    		return ResultUtils.error(34,"已超过最大可领取金额") ;
    	}
    	redPacketMapper.updateById(rp);
    	//生成记录表
    	RedPacketRecord rpr = new RedPacketRecord();
    	rpr.setAddress(rp.getAddress());
    	rpr.setAmount(r_amount);
    	rpr.setRed_packet_id(rp.getId());
    	rpr.setTarget_address(recive_user.getAddress());
    	rpr.setReceive_user_id(recive_user.getId());
    	rpr.setSend_user_id(rp.getSend_user_id());
    	redPacketRecordMapper.insert(rpr);
    	return ResultUtils.success(r_amount);
    }
    
    /**
     * 获取群红包金额
     * @param dto
     * @return
     */
    private BigDecimal getRpAmount(RedPacket rp) {
    	BigDecimal basicAmount = MathUtil.div(rp.getAmount(), rp.getNum()) ;
    	BigDecimal randomRedPacketBetweenMinAndMax = MathUtil.getRandomRedPacketBetweenMinAndMax(MathUtil.mul(basicAmount, 0.5), MathUtil.mul(basicAmount, 2)) ;
    	if(MathUtil.compareTo(randomRedPacketBetweenMinAndMax, rp.getBalance())) {
    		randomRedPacketBetweenMinAndMax = MathUtil.getRandomRedPacketBetweenMinAndMax(MathUtil.mul(basicAmount, 0.5), MathUtil.mul(basicAmount, 2)) ;
        	if(MathUtil.compareTo(randomRedPacketBetweenMinAndMax, rp.getBalance())) {
        		randomRedPacketBetweenMinAndMax = MathUtil.getRandomRedPacketBetweenMinAndMax(MathUtil.mul(basicAmount, 0.5), MathUtil.mul(basicAmount, 2)) ;
            	if(MathUtil.compareTo(randomRedPacketBetweenMinAndMax, rp.getBalance())) {
            		randomRedPacketBetweenMinAndMax = MathUtil.mul(rp.getBalance(), 0.5).setScale(2,BigDecimal.ROUND_DOWN);
            	}
        	}
    	}
    	return randomRedPacketBetweenMinAndMax;
    }
    
    
    @GetMapping("pageSend")
    @ApiOperation(value = "发出的红包列表")
    public Result<RedPacketSendResultDTO> pageSend(RedPacketRecordDTO dto) {
    	String address = dto.getAddress() ;
    	PageQueryDTO pageQuery = CloneUtil.clone(dto, PageQueryDTO.class) ;
		IPage<RedPacket> selectPage = redPacketMapper.selectPage(MybatiesUtil.getPage(pageQuery), new QueryWrapper<RedPacket>().lambda().eq(RedPacket::getAddress, address)) ;
		RedPacketSendResultDTO resultDto = new RedPacketSendResultDTO();
    	resultDto.setPages(selectPage);
    	Object sum = commonMapper.select("select IFNULL(sum(amount),0) from red_packet where  address ='"+address+"'");
    	resultDto.setSum(sum);
    	return ResultUtils.success(resultDto);
    }
    @GetMapping("pageReceive")
    @ApiOperation(value = "领取的红包列表")
    public Result<RedPacketReceiveResultDTO> pageReceive(RedPacketRecordDTO dto) {
    	String address = dto.getAddress() ;
    	PageQueryDTO pageQuery = CloneUtil.clone(dto, PageQueryDTO.class) ;
    	IPage<RedPacketRecord> selectPage = redPacketRecordMapper.selectPage(MybatiesUtil.getPage(pageQuery), new QueryWrapper<RedPacketRecord>().lambda().eq(RedPacketRecord::getTarget_address, address)) ;
    	RedPacketReceiveResultDTO resultDto = new RedPacketReceiveResultDTO();
    	resultDto.setPages(selectPage);
    	Object sum = commonMapper.select("select IFNULL(sum(amount),0) from red_packet_record where  target_address ='"+address+"'");
    	resultDto.setSum(sum);
    	return ResultUtils.success(resultDto);
    }
    
	/*@GetMapping("pageReceiveById")
	@ApiOperation(value = "领取的红包列表")
	public Result<IPage<RedPacketRecord>> pageReceiveById(RedPacketRecordDTO dto) {
		PageQueryDTO pageQuery = CloneUtil.clone(dto, PageQueryDTO.class) ;
		IPage<RedPacketRecord> selectPage = redPacketRecordMapper.selectPage(MybatiesUtil.getPage(pageQuery), new QueryWrapper<RedPacketRecord>().lambda().eq(RedPacketRecord::getId, dto.getId())) ;
		return ResultUtils.success(selectPage) ;
	}*/

    /**
     * 查看单个红包的详情
     *
     * @return
     */
    @GetMapping("get")
    @ApiOperation(value = "红包详情接口")
    public Result<RedPacketResultDTO> get(@RequestParam String id,@RequestParam String address) {
    	RedPacketResultDTO dto = new RedPacketResultDTO();
    	dto.setRedPacket(redPacketMapper.selectById(id));
    	PageQueryDTO pageQuery = CloneUtil.clone(dto, PageQueryDTO.class) ;
    	IPage<RedPacketRecord> page = redPacketRecordMapper.selectPage(MybatiesUtil.getPage(pageQuery), new QueryWrapper<RedPacketRecord>().lambda().eq(RedPacketRecord::getRed_packet_id, id)) ;
        dto.setRedPacketRecordList(page.getRecords());
        dto.setReceive_num(page.getTotal());
        dto.setIs_receive(redPacketRecordMapper.selectOne(new QueryWrapper<RedPacketRecord>().lambda().eq(RedPacketRecord::getTarget_address, address).eq(RedPacketRecord::getRed_packet_id, id))!=null);
    	return ResultUtils.success(dto) ;
    }
}

