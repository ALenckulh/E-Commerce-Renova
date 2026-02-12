// Checkout Store
// Estado global do checkout (endereço, frete, pagamento)
// TODO: Implementar com Zustand

import { create } from 'zustand';

interface CheckoutState {
  // TODO: address, shipping, paymentMethod, etc
}

export const useCheckoutStore = create<CheckoutState>((set) => ({
  // TODO: Implementar estado do checkout
}));
