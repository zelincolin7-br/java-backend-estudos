package com.estudos.orderplatform.domain;

public enum OrderStatus {

    PENDING,
    PAID,
    PREPARING,   // Em preparo na cozinha
    READY,       // Pronto para entrega / retirada
    DELIVERED,   // Entregue ao cliente / Concluído
    CANCELED
}