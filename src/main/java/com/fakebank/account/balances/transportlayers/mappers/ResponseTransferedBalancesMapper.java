package com.fakebank.account.balances.transportlayers.mappers;

import com.fakebank.account.balances.entities.accounts.CustomerAccount;
import com.fakebank.account.balances.transportlayers.dtos.MetaDto;
import com.fakebank.account.balances.transportlayers.models.ResponseTransferedBalancesModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ResponseTransferedBalancesMapper {

    ResponseTransferedBalancesMapper INSTANCE = Mappers.getMapper(ResponseTransferedBalancesMapper.class);

    @Mapping(source = "customerAccount.availableBalancesAmountLimit", target = "data.availableAmount")
    @Mapping(source = "metaDto", target = "meta")
    ResponseTransferedBalancesModel map(CustomerAccount customerAccount, MetaDto metaDto);

}
