package com.bx.implatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder ;
import lombok.Data;
import lombok.experimental.Accessors ;
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
public class CreateWalletResultDTO{
  private String address;
  private String private_key;
  private String public_key;
  private String password;
  private String keystore;
  private String mnemonics;
  
}
