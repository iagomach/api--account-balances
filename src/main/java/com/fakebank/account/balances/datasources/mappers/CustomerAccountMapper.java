package com.fakebank.account.balances.datasources.mappers;

import com.fakebank.account.balances.datasources.clients.cadastro.models.ResponseAccountIdentificationModel;
import com.fakebank.account.balances.entities.accounts.CustomerAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CustomerAccountMapper {

    CustomerAccountMapper INSTANCE = Mappers.getMapper(CustomerAccountMapper.class);

    @Mapping(source = "data.balancesTransferLimit.availableAmount", target = "availableBalancesAmountLimit")
    @Mapping(source = "data.balancesTransferLimit.maxSetAmount", target = "maxSetBalancesAmountLimit")
    @Mapping(source = "data.status", target = "status")
    CustomerAccount map(ResponseAccountIdentificationModel responseAccountIdentificationModel);

}
