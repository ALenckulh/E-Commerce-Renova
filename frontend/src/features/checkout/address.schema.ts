// Address Validation Schema
// Definir validações com Zod para formulário de endereço
// TODO: Implementar schema de validação

import { z } from 'zod';

export const addressSchema = z.object({
  // TODO: street, number, complement, neighborhood, city, state, zipCode
});

export type AddressFormData = z.infer<typeof addressSchema>;
