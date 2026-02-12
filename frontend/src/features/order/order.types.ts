// Order Types
// Definir interfaces e tipos para pedidos
// TODO: Criar interfaces Order, OrderItem, OrderStatus, etc

export interface Order {
  // TODO: id, userId, items, total, status, createdAt, etc
}

export interface OrderItem {
  // TODO: productId, name, quantity, price
}

export enum OrderStatus {
  // TODO: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
}
