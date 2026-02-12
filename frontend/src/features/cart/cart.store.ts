// Cart Store
// Estado global do carrinho usando Zustand
// TODO: Implementar addItem, removeItem, updateQuantity, clearCart, etc

import { create } from 'zustand';

interface CartState {
  // TODO: items, total, addItem, removeItem, updateQuantity, clearCart
}

export const useCartStore = create<CartState>((set) => ({
  // TODO: Implementar estado e ações do carrinho
}));
