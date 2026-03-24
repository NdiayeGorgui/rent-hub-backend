package com.smartiadev.item_service.entity;

public enum ItemStatus {
    DRAFT,              // créé mais pas encore publié
    PENDING_AUCTION_FEE,// enchère créée mais paiement non fait
    ACTIVE,             // visible
    INACTIVE,           // désactivé par le propriétaire
    BLOCKED,             // bloqué par un admin
    CANCELLED_AUCTION      // enchère annulée (avant refund)

}
