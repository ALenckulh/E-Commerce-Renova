// Checkout Validation Schema
// Definir validações com Zod para checkout completo
// TODO: Implementar schema de validação

import { z } from 'zod';

export const checkoutSchema = z.object({
  // TODO: address, shippingOption, paymentMethod
});

export type CheckoutFormData = z.infer<typeof checkoutSchema>;
