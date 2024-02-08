package com.fakebank.account.balances.interactors.strategies.samebank;

import com.fakebank.account.balances.entities.accounts.CustomerAccount;
import com.fakebank.account.balances.entities.accounts.StatusEnum;
import com.fakebank.account.balances.interactors.clients.bacen.models.NotificationRequestDataModel;
import com.fakebank.account.balances.interactors.exceptions.AccountInactiveException;
import com.fakebank.account.balances.interactors.exceptions.InsufficientFundsException;
import com.fakebank.account.balances.interactors.exceptions.LimitExceededException;
import com.fakebank.account.balances.interactors.services.BacenNotificationService;
import com.fakebank.account.balances.interactors.strategies.TransferStrategy;
import com.fakebank.account.balances.interactors.strategies.models.TransferSameBankRequest;
import com.fakebank.account.balances.repositories.CustomersAccountsRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import static com.fakebank.account.balances.interactors.clients.bacen.models.EnumTransactionTypesModel.TRANSFERENCIA_MESMA_INSTITUICAO;

@Service
public class TransferSameBankStrategy implements TransferStrategy<TransferSameBankRequest, CustomerAccount> {
    public static final String BACEN_NOTIFICADO_SUCESSO =
            "Transferencia de {} para {} no valor de R${} notificada com sucesso.";
    public static final String LIMITE_ATUALIZADO_SUCESSO = "Limite disponivel para conta {} atualizado com sucesso.";
    public static final String INICIANDO_TRANSFERENCIA = "Iniciando transferencia de {} para {} no valor de R${}";
    public static final String TRANSFERENCIA_CONCLUIDA = "Transferencia de {} para {} no valor de R${} concluida com sucesso.";
    Logger logger = LogManager.getLogger(TransferSameBankStrategy.class);
    private final BacenNotificationService bacenNotificationService;
    private final CustomersAccountsRepository customersAccountsRepository;

    public TransferSameBankStrategy(BacenNotificationService bacenNotificationService,
                                    CustomersAccountsRepository customersAccountsRepository) {
        this.bacenNotificationService = bacenNotificationService;
        this.customersAccountsRepository = customersAccountsRepository;
    }

    public CustomerAccount transfer(TransferSameBankRequest transferSameBankRequest) {
        logger.info(INICIANDO_TRANSFERENCIA,
                transferSameBankRequest.getSourceAccountFullName(),
                transferSameBankRequest.getTargetAccountFullName(),
                transferSameBankRequest.getAmount());

        CustomerAccount sourceCustomerAccount = this.customersAccountsRepository
                .findByName(transferSameBankRequest.getSourceAccountFullName());

        executeBusinessValidations(transferSameBankRequest, sourceCustomerAccount);

        updateSourceAccountAvailableLimit(transferSameBankRequest, sourceCustomerAccount);

        this.customersAccountsRepository
                .updateAvailableLimitByName(transferSameBankRequest.getTargetAccountFullName(),
                        transferSameBankRequest.getAmount());

        notifyBacen(transferSameBankRequest);

        logger.info(TRANSFERENCIA_CONCLUIDA,
                transferSameBankRequest.getSourceAccountFullName(),
                transferSameBankRequest.getTargetAccountFullName(),
                transferSameBankRequest.getAmount());

        return sourceCustomerAccount;
    }

    private void notifyBacen(TransferSameBankRequest transferSameBankRequest) {
        NotificationRequestDataModel notificationRequestDataModel
                = buildBacenNotificationRequest(transferSameBankRequest);

        this.bacenNotificationService.send(notificationRequestDataModel);

        logger.info(BACEN_NOTIFICADO_SUCESSO,
                transferSameBankRequest.getSourceAccountFullName(),
                transferSameBankRequest.getTargetAccountFullName(),
                transferSameBankRequest.getAmount());
    }

    private void updateSourceAccountAvailableLimit(TransferSameBankRequest transferSameBankRequest, CustomerAccount sourceCustomerAccount) {
        var updatedAmount = sourceCustomerAccount
                .getAvailableBalancesAmountLimit()
                .subtract(transferSameBankRequest.getAmount());

        sourceCustomerAccount
                .setAvailableBalancesAmountLimit(updatedAmount);

        logger.info(LIMITE_ATUALIZADO_SUCESSO,
                transferSameBankRequest.getSourceAccountFullName());
    }

    private static void executeBusinessValidations(TransferSameBankRequest transferSameBankRequest, CustomerAccount sourceCustomerAccount) {
        validateActive(sourceCustomerAccount);
        validateLimitExceeded(transferSameBankRequest, sourceCustomerAccount);
        validateInsufficientFunds(transferSameBankRequest, sourceCustomerAccount);
    }

    private static NotificationRequestDataModel buildBacenNotificationRequest(
            TransferSameBankRequest transferSameBankRequest) {
        NotificationRequestDataModel notificationRequestDataModel = new NotificationRequestDataModel();
        notificationRequestDataModel.setAmount(transferSameBankRequest.getAmount());
        notificationRequestDataModel.setTransactionType(TRANSFERENCIA_MESMA_INSTITUICAO);
        notificationRequestDataModel.setTargetAccountFullName(transferSameBankRequest.getTargetAccountFullName());
        return notificationRequestDataModel;
    }

    private static void validateInsufficientFunds(TransferSameBankRequest transferSameBankRequest,
                                                  CustomerAccount sourceCustomerAccount) {
        if (sourceCustomerAccount.getAvailableBalancesAmountLimit().compareTo(transferSameBankRequest.getAmount()) < 0)
            throw new InsufficientFundsException(transferSameBankRequest.getAmount(),
                    sourceCustomerAccount.getAvailableBalancesAmountLimit());
    }

    private static void validateLimitExceeded(TransferSameBankRequest transferSameBankRequest,
                                              CustomerAccount sourceCustomerAccount) {
        if (transferSameBankRequest.getAmount().compareTo(sourceCustomerAccount.getMaxSetBalancesAmountLimit()) > 0)
            throw new LimitExceededException(transferSameBankRequest.getAmount(),
                    sourceCustomerAccount.getMaxSetBalancesAmountLimit());
    }

    private static void validateActive(CustomerAccount sourceCustomerAccount) {
        if (!sourceCustomerAccount.getStatus().equals(StatusEnum.ACTIVE))
            throw new AccountInactiveException(sourceCustomerAccount.getName());
    }
}
