package nl.tudelft.trustchain.common.eurotoken.blocks

import nl.tudelft.ipv8.attestation.trustchain.TrustChainBlock
import nl.tudelft.ipv8.attestation.trustchain.store.TrustChainStore
import nl.tudelft.trustchain.common.eurotoken.TransactionRepository

class EuroTokenDestructionValidator(transactionRepository: TransactionRepository) : EuroTokenBaseValidator(transactionRepository) {
    override fun validateEuroTokenProposal(
        block: TrustChainBlock,
        database: TrustChainStore
    ) {
        if (!block.transaction.containsKey(TransactionRepository.KEY_AMOUNT)) {
            throw MissingAmount("Missing amount")
        }
        if (!block.transaction.containsKey(TransactionRepository.KEY_PAYMENT_ID) &&
            !block.transaction.containsKey(TransactionRepository.KEY_IBAN)
        ) {
            throw MissingPaymentIDorIBAN("Missing Payment id or IBAN")
        }
        super.validateEuroTokenProposal(block, database)
        return // Valid
    }

    class MissingAmount(message: String) : Invalid(message) {
        override val type: String = "MissingAmount"
    }

    class MissingPaymentIDorIBAN(message: String) : Invalid(message) {
        override val type: String = "MissingPaymentIDorIBAN"
    }
}
